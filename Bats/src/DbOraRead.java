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
public class DbOraRead
{
	static Logger log = Logger.getLogger(DbOraRead.class);
	
	private Boolean virtual;
	private Boolean cycle;
	private Document doc;
	private Connection conn;
	
	private static String ANA = "calcana";
	private static String PROTO = "calcproto";
	private static String FINAL = "calcfinal";
	
	DbOraRead(Connection conn)  throws IOException, JDOMException {
		this.conn=conn;
		virtual = Setting.getBoolean("/bat/isotope/db/ora/virtual");;
        try {
			cycle = Setting.getElement("/bat/isotope/db/ora/cycle").getAttribute("name").getBooleanValue();
		} catch (DataConversionException e) { cycle = false; log.error("Cycle setting not found!"); }
		SAXBuilder builder = new SAXBuilder();
		log.debug(Setting.isotope);
		doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml"));
	}
	
	/**
	 * @param magazine
	 * @param data 
	 * 
	 */
	public void getRunList(String magazine, Calc data) {
		String query=null;
		ArrayList<String> runNameList=null;
		try{
			ResultSet resultSet;
			runNameList = new ArrayList<String>();
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			
			query="select run from "+PROTO+" where magazine='"+magazine+"' order by run desc";
			resultSet = stmt.executeQuery (query);
			while (resultSet.next()) {
				runNameList.add(resultSet.getString("run"));
			}
		} catch (SQLException e) {
			String message = String.format( "Query of run numbers failed!");
		    JOptionPane.showMessageDialog( null, message );
			log.error("sql exception: Query failed! "+query+"");
			log.error("Query with cycle="+cycle+" and virtual="+virtual);
			log.debug(e);
//			e.printStackTrace();
		}
		getRunList(runNameList, magazine, data);
	}
	
	/**
	 * @param runNameList 
	 * @param magazine 
	 * @param data 
	 */
	@SuppressWarnings("unchecked")
	public void getRunList( ArrayList<String> runNameList, String magazine, Calc data) {
		List<Element> list;
		
		if (cycle==true){
			list = doc.getRootElement().getChild("ana").getChildren();
			log.debug("read cycles");
		} else if (virtual==true){
			list = doc.getRootElement().getChild("proto_virtual").getChildren();
			log.debug("read virtual");
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
		ResultSet resultSet = this.queryResults(runNameList, nameList);
		try {
			log.debug(resultSet.getFetchSize());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			while (resultSet.next()) {
				Run tempData = new Run( data.setSample(resultSet.getString("label")) );
				for (int i=0; i<nameList.size(); i++){
//					log.debug(nameList.get(i));
//					log.debug(nameSetList.get(i));
					tempData.setValue(resultSet.getString(nameList.get(i)), nameSetList.get(i));
				}
				data.runListL.add(tempData);
			}
//			conn.close();
			log.debug("Size of run list: "+data.runListL.size());
		} catch (SQLException e){
		    JOptionPane.showMessageDialog( null, "<html>sql exception in OracleRead! Faild to get tempData.</html>" );
			log.error("sql exception in OracleRead! Faild to get tempData.");
		} catch (NullPointerException e) {
			log.error("Null pointer exception in reading results!");
			e.printStackTrace();
		}
		if (cycle==true) {
			log.debug("Reduce cycles: "+data.runListL.size()+" divide by "+Setting.getInt("/bat/isotope/db/ora/cycle"));
			data.runListL=Func.reduceCycle(data.runListL, Setting.getInt("/bat/isotope/db/ora/cycle"));
		}

		List<Element> listFinal;
		listFinal = doc.getRootElement().getChild("final").getChildren();
		ArrayList<String> nameFinalList = new ArrayList<String>();
		ArrayList<String> nameFinalSetList = new ArrayList<String>();
		for (int i=0; i<listFinal.size(); i++){
			nameFinalList.add(listFinal.get(i).getText());
			nameFinalSetList.add(listFinal.get(i).getAttributeValue("name"));
		}
		ArrayList<SampleInfo> sampleInfo = this.querySampleInfo(magazine, nameFinalList, nameFinalSetList);
		for (int k=0; k<data.runListL.size(); k++){
			Run tempData = data.runListL.get(k);
			int index=0;
			try {
				while (!sampleInfo.get(index++).label.equals(tempData.sample.label)&&index<1000) {;}
				index=index-1;
				for (int i=0; i<nameFinalList.size();i++) {
					try {
						tempData.setValue(sampleInfo.get(index).get(nameFinalSetList.get(i)), nameFinalSetList.get(i));
					} catch(NullPointerException e){
						log.debug("Info set error: "+nameFinalSetList.get(i)+" for label "+tempData.sample.label);
					}
				}
			} catch (IndexOutOfBoundsException e) {
				log.error("Could not find sample information for label: "+tempData.sample.label);
				String message = String.format("Could not find sample information for label: "+tempData.sample.label);
			    JOptionPane.showMessageDialog( null, message );
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.debug("Tried to logout, but failed!");
		}
	}
	
	/**
	 * @param run
	 * @return Runs
	 */
	@SuppressWarnings("unchecked")
	public Run getRun(Run run) {
		List<Element> list;
		
		if (virtual==true){
			list = doc.getRootElement().getChild("proto_virtual").getChildren();
			log.debug("read virtual");
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
		ResultSet resultSet = this.queryResult(run.run, nameList);
		
		try{
			resultSet.next();
			for (int i=0; i<nameList.size(); i++){
				run.setValue(resultSet.getString(nameList.get(i)), nameSetList.get(i));
			}
			log.debug("Run updated successfully requested "+run.run);
//			conn.close();
		} catch (SQLException e){
			log.error("sql exception in OracleRead! Faild to get tempData.");
		} catch (NullPointerException e) {
			log.error("Null pointer exception in reading results!");
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.debug("Tried to logout, but failed!");
		}
        return run;
	}
	
	private ArrayList<SampleInfo> querySampleInfo(String magazine, ArrayList<String> nameFinalList,	ArrayList<String> nameFinalSetList) {
		ResultSet resultMag;
		
		String query="";
		try{
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			query = "select ";
			for (int i=0; i<nameFinalList.size(); i++){
				query+=nameFinalList.get(i)+", ";
			}
			query = query.substring(0,query.length()-2);
			query += " from "+FINAL+" where magazine='" + magazine + "' order by posit desc";
			resultMag = stmt.executeQuery (query);
			log.debug("SQL-query to get finals was successful: "+query);
		}
		catch (SQLException e){
			log.error("sql exception in OracleRead! Query of sample information failed: "+query+e);
			String message = String.format( "Query of sample information failed: "+query);
		    JOptionPane.showMessageDialog( null, message );
			resultMag = null;
		}
		// get sample info
		ArrayList<SampleInfo> sampleInfo = new ArrayList<SampleInfo>();
		try{
			while (resultMag.next()){
				SampleInfo info = new SampleInfo();
				for (int i=0; i<nameFinalList.size(); i++) {
					try{
						info.set(resultMag.getString(nameFinalList.get(i)),nameFinalSetList.get(i));
					} catch(NullPointerException e){
						log.error("Info set error: "+nameFinalList.get(i)+"/"+nameFinalSetList.get(i));
					}
				}
				sampleInfo.add(info);
			}
		} 
		catch (SQLException e){
			log.debug("sql exception in OracleRead! Couldn't get sampleInfo!");
		}
		return sampleInfo;
	}
	
		
	/**
	 * @param runNameList 
	 * @param nameList 
	 * @return results of query
	 */
	private ResultSet queryResults(ArrayList<String> runNameList, ArrayList<String> nameList){
		ResultSet result;
		String query=null;
		try{
			String passes="'";
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			
			for (int i=0; i<runNameList.size(); i++){
				passes+=runNameList.get(i)+"','";
			}
			passes=passes.substring(0,passes.length()-2);
			query = "select ";
			for (int i=0; i<nameList.size(); i++) {
				query+=nameList.get(i)+",";
			}			
			query = query.substring(0,query.length()-1);
			
			if (cycle==true){
				query+=" from "+ANA+", "+ANA+"v1 where (recnov1=recno) and run in ("+passes+") and cycltrue is null order by label desc, timedat asc";
			}else if (virtual==true) {
				query+=" from calcvproto where run in ("+passes+") order by run desc";
			} else {
				query+=" from "+PROTO+" where run in ("+passes+") order by run desc";
			}
			query=JOptionPane.showInputDialog("Query: ", query);
			log.debug("query start");
			result = stmt.executeQuery (query);
			log.debug("sql querry to get runs/cycles was successful: "+query);
		}
		catch (SQLException e) {
			String message = String.format( "Query of runs failed!");
		    JOptionPane.showMessageDialog( null, message );
			result = null;
			log.error("sql exception: Query failed! "+query+"");
			log.error("Query with cycle="+cycle+" and virtual="+virtual);
			log.debug(e);
//			e.printStackTrace();
		}
		return result;
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
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			
			query = "select ";
			for (int i=0; i<nameList.size(); i++) {
				query+=nameList.get(i)+",";
			}			
			query = query.substring(0,query.length()-1);
			
			if (virtual==true) {
				query+=" from calcvproto where run='"+run+"'";
			} else {
				query+=" from "+PROTO+" where run='"+run+"'";
			}
//			query=JOptionPane.showInputDialog("Query: ", query);
			log.debug("query start");
			result = stmt.executeQuery (query);
			log.debug("sql querry to get runs/cycles was successful: "+query);
		}
		catch (SQLException e) {
			String message = String.format( "Query of runs failed!");
		    JOptionPane.showMessageDialog( null, message );
			result = null;
			log.error("sql exception: Query failed! "+query+"");
			log.error("Query with cycle="+cycle+" and virtual="+virtual);
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

