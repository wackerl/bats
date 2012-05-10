import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * @author lukas
 *
 */
public class DbMySqlRead
{
	static Logger log = Logger.getLogger(DbMySqlRead.class);
	
	private Boolean cycle;
	private Connection conn;
	private Statement stmt;
	
	private String cycle_t = Setting.getString("/bat/isotope/db/sql-import/cycle_t");
	private String run_t = Setting.getString("/bat/isotope/db/sql-import/run_t");
	private String final_t = Setting.getString("/bat/isotope/db/sql-import/final_t");
	
	DbMySqlRead(Connection conn){
		this.conn=conn;
        try {
			cycle = Setting.getElement("/bat/isotope/db/sql-import/cycle").getAttribute("name").getBooleanValue();
		} catch (DataConversionException e) { cycle = false; log.error("Cycle setting not found!"); }
;
	}
	
	/**
	 * @param magazine
	 * @param data 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@SuppressWarnings("unchecked")
	public void getRunList(String magazine, Calc data) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml"));
		List<Element> list;
		
		if (cycle==true){
			list = doc.getRootElement().getChild("ana").getChildren();
			log.debug("read cycles");
		} else{
			list = doc.getRootElement().getChild("proto").getChildren();
			log.debug("no virtual");
		}
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> nameSetList = new ArrayList<String>();
		for (int i=0; i<list.size(); i++){
			nameList.add(list.get(i).getText());
			nameSetList.add(list.get(i).getAttributeValue("name"));
		}
		
		ResultSet resultSet = this.queryResults(magazine, nameList);
		
		ResultSet resultMag;
		
		List<Element> listFinal;
		listFinal = doc.getRootElement().getChild("final").getChildren();
		ArrayList<String> nameFinalList = new ArrayList<String>();
		ArrayList<String> nameFinalSetList = new ArrayList<String>();
		for (int i=0; i<listFinal.size(); i++){
			nameFinalList.add(listFinal.get(i).getText());
			nameFinalSetList.add(listFinal.get(i).getAttributeValue("name"));
		}
		
		String query="";
		try{
			stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql-import/timeout"));
			query = "select ";
			for (int i=0; i<nameFinalList.size(); i++){
				query+=nameFinalList.get(i)+", ";
			}
			query = query.substring(0,query.length()-2);
			query += " from "+final_t+" where magazine='" + magazine + "' order by posit desc";
			resultMag = stmt.executeQuery (query);
			log.debug("SQL-query to get finals was successful: "+query);
		}
		catch (SQLException e){
			log.error("sql exception in DBRead! Query of sample information failed: "+query+e);
			String message = String.format( "Query of sample information failed: "+query);
		    JOptionPane.showMessageDialog( null, message );
			resultMag = null;
		}

		// get sample info
		try{
			while (resultMag.next()){
				Sample sample = new Sample(resultMag.getString("label"));
				for (int i=0; i<nameFinalList.size(); i++) {
					try{
						sample.set(resultMag.getObject(nameFinalList.get(i)),nameFinalSetList.get(i));
//						log.debug(resultMag.getObject(nameFinalList.get(i))+"///"+nameFinalSetList.get(i));
					} catch(NullPointerException e){
						log.error("Info set error: "+nameFinalList.get(i)+"/"+nameFinalSetList.get(i));
					}
				}
				data.sampleList.add(sample);
			}
		} 
		catch (SQLException e){
			log.debug("sql exception in DBRead! Couldn't get sampleInfo!");
		}
		try{
			while (resultSet.next()) {
				Run run = new Run( data.setSample(resultSet.getString("label")) );
				for (int i=0; i<nameList.size(); i++){
					run.setValue(resultSet.getString(nameList.get(i)), nameSetList.get(i));
				}
				data.runListL.add(run);
			}
		} 
		catch (SQLException e){
			log.debug("sql exception in DBRead! Faild to get tempData.");
		} finally {
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException e) {
					log.error("Could not close statement!");
		        }
		        stmt = null;
		    }
		}
		if (cycle==true) {
			log.debug("Reduce cycles: "+data.runListL+" divide by "+Setting.getInt("/bat/isotope/db/sql-import/cycle"));
			data.runListL=Func.reduceCycle(data.runListL, Setting.getInt("/bat/isotope/db/sql-import/cycle"));
		}
	}
	
	/**
	 * @param mag
	 * @param nameList 
	 * @return results of query
	 */
	private ResultSet queryResults(String mag, ArrayList<String> nameList){
		ResultSet result;
		String query=null;
		try{
			stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql-import/timeout"));

		    
			if (cycle==true){
				query = "select ";
				for (int i=0; i<nameList.size(); i++) {
					query+=cycle_t+"."+nameList.get(i)+", ";
				}	
				query = query.substring(0,query.length());
				//cv
				log.debug(query+"- here");
				log.debug("cycle_t: "+cycle_t);
								
				query+= run_t+".run, "+run_t+".label from "+cycle_t+", "+run_t+" where ("+cycle_t+".run="+run_t+".run) and magazine='"+mag+"' and cycltrue is null order by "+cycle_t+".run desc, cycle asc";
			} else {
				query = "select ";
				for (int i=0; i<nameList.size(); i++) {
					query+=nameList.get(i)+", ";
				}	
				query = query.substring(0,query.length()-2);
				log.debug(query+"-");
				query+=" from "+run_t+" where magazine='"+mag+"' order by run desc";
			}
//			query=JOptionPane.showInputDialog("Query: ", query);
			result = stmt.executeQuery (query);
			log.debug("SQL-query to get runs/cycles was successful: "+query);
		} catch (SQLException e) {
			String message = String.format( "Query of runs failed!");
		    JOptionPane.showMessageDialog( null, message );
			result = null;
			log.error("sql exception: Query failed! "+query+"");
			log.error("Query with cycle="+cycle);
			log.debug(e);
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * @param run
	 * @return Run
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@SuppressWarnings("unchecked")
	public Run getRun(Run run) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml"));
		List<Element> list;
		

		list = doc.getRootElement().getChild("proto").getChildren();
		log.debug("no virtual");
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> nameSetList = new ArrayList<String>();
		for (int i=0; i<list.size(); i++){
			nameList.add(list.get(i).getText());
			nameSetList.add(list.get(i).getAttributeValue("name"));
		}
		
		ResultSet resultSet = this.queryResult(run.run, nameList);
		
		try{
			resultSet.next();
			for (int i=0; i<nameList.size(); i++){
				run.setValue(resultSet.getString(nameList.get(i)), nameSetList.get(i));
			}
		} 
		catch (SQLException e){
			log.debug("sql exception in DBRead! Faild to get run data.");
		} finally {
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException e) {
					log.error("Could not close statement!");
		        }
		        stmt = null;
		    }
		}
        return run;
	}
	
	/**
	 * @param run
	 * @param nameList 
	 * @return results of query
	 */
	private ResultSet queryResult(String run, ArrayList<String> nameList){
		ResultSet result;
		String query=null;
		try{
			stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql-import/timeout"));
			
			query = "select ";
			for (int i=0; i<nameList.size(); i++) {
				query+=nameList.get(i)+",";
			}	
			query = query.substring(0,query.length()-1);
//			query+="to_char(timedat, 'DD.MM.YYYY HH24:MI:SS') as timedat, timedat ";
			
			query+=" from "+run_t+" where run='"+run+"'";

			result = stmt.executeQuery (query);
			log.debug("sql querry to get run was successful: "+query);
		} catch (SQLException e) {
			String message = String.format( "Query of run failed!");
		    JOptionPane.showMessageDialog( null, message );
			result = null;
			log.error("sql exception: Query failed! "+query+"");
			log.debug(e);
			e.printStackTrace();
		}
		return result;
	}
	
	
	class SampleInfo
	{
		/**
		 * Sample Info: magazine
		 */
		public String magazine;
		/**
		 * Sample Info: label
		 */
		public String label;
		/**
		 * Sample Info: commentinfo
		 */
		public String commentinfo;
		/**
		 * Sample Info: posit
		 */
		public String posit;
		/**
		 * Sample Info: sampletype
		 */
		public String sampletype;
		/**
		 * Sample Info: carrier
		 */
		public String carrier;
		/**
		 * Sample Info: weight
		 */
		public String weight;
		/**
		 * Sample Info: username
		 */
		public String username;
		/**
		 * Sample Info: userproj
		 */
		public String userproj;
		private String userparam1;
		private String userparam2;
		/**
		 * Sample Info: userlabel
		 */
		public String userlabel;
		
		/**
		 * @param value
		 * @param field
		 */
		public void set(String value, String field){
			if (field.equals("commentinfo")) {commentinfo=value;}
			else if (field.equals("magazine")) {magazine=value;}
			else if (field.equals("label")) {label=value;}
			else if (field.equals("posit")) {posit=value;}
			else if (field.equals("sampletype")) {sampletype=value;}
			else if (field.equals("carrier")) {carrier=value;}
			else if (field.equals("weight")) {weight=value;}
			else if (field.equals("username")) {username=value;}
			else if (field.equals("userproj")) {userproj=value;}
			else if (field.equals("userparam1")) {userparam1=value;}
			else if (field.equals("userparam2")) {userparam2=value;}
			else if (field.equals("userlabel")) {userlabel=value;}
		}
		
		/**
		 * @param field
		 * @return String of field
		 */
		public String get(String field){
			if (field.equals("commentinfo")) {return commentinfo;}
			else if (field.equals("posit")) {return posit;}
			else if (field.equals("magazine")) {return magazine;}
			else if (field.equals("label")) {return label;}
			else if (field.equals("sampletype")) {return sampletype;}
			else if (field.equals("carrier")) {return carrier;}
			else if (field.equals("weight")) {return weight;}
			else if (field.equals("username")) {return username;}
			else if (field.equals("userproj")) {return userproj;}
			else if (field.equals("userparam1")) {return userparam1;}
			else if (field.equals("userparam2")) {return userparam2;}
			else if (field.equals("userlabel")) {return userlabel;}
			else return "";
		}
	}
}

