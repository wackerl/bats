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
public class DbOraPuRead
{
	static Logger log = Logger.getLogger(DbOraPuRead.class);
	
	private Boolean cycle;
	private Document doc;
	private Connection conn;
	
	private static String ANA = "calcana";
	private static String PROTO = "calcproto";
	private static String FINAL = "calcfinal";
	
	DbOraPuRead(Connection conn)  throws IOException, JDOMException {
		this.conn = conn;
        try {
			cycle = Setting.getElement("/bat/isotope/db/ora/cycle").getAttribute("name").getBooleanValue();
		} catch (DataConversionException e) { log.error("Cycle setting not found!"); }
		SAXBuilder builder = new SAXBuilder();
		
		doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml"));
	}
	
	private ArrayList<SampleInfo> querySampleInfo(String magazine, ArrayList<String> nameFinalList,	ArrayList<String> nameFinalSetList) {
		ResultSet resultMag;
		
		String query="";
		try{
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(10);
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
	
	private ArrayList<Run> reduceCycle(ArrayList<Run> cycleData) {
		ArrayList<Run> redData = new ArrayList<Run>();
		ArrayList<Run> tempData = null;		
		String tempRun="";
		int l=0;
		for (int i=0;i<cycleData.size();i++) {
			if (cycleData.get(i).run.equals(tempRun)) {
				tempData.add(cycleData.get(i));					
//					log.debug("same label: "+tempLabel+"-"+i+"("+k+")");
			} else if (tempRun=="") {
				tempData = new ArrayList<Run>();
				tempData.add(cycleData.get(i));
				tempRun = cycleData.get(i).run;
//				log.debug("first label: "+tempLabel);
			} else {
				redData.add(this.meanCycle(tempData, ++l));
				tempData = new ArrayList<Run>();
				tempData.add(cycleData.get(i));
				tempRun = cycleData.get(i).run;
				l=0;
//				log.debug("new label, new run: "+tempLabel);
			}
		}
		redData.add(this.meanCycle(tempData, ++l));
//		log.debug(redData.size());
		return redData;
	}
	
	private Run meanCycle(ArrayList<Run> cycleData, int runNum) {
		Run run = new Run(cycleData.get(0).sample);
		
		run.cycles=cycleData.size();
//		NumberFormatter nf = new NumberFormatter(new DecimalFormat("00"));
//		run.run = cycleData.get(0).run+cycleData.get(0).cycles;
		run.run = cycleData.get(0).run;
//		try {run.run = cycleData.get(0).run+nf.valueToString(runNum);} 
//		catch (ParseException e) {run.run = cycleData.get(0).run+cycleData.get(0).cycles;}

		try {run.timedat = cycleData.get(0).timedat;}
		catch (NullPointerException e) {run.timedat = null;}
		
		try {run.runtime = Func.sumNotNull(cycleData, "runtime");}
		catch (NullPointerException e) {run.runtime = null;}
		try {run.runtime_a = Func.sumNotNull(cycleData, "runtime_a");}
		catch (NullPointerException e) {run.runtime_a = null;}
		try {run.runtime_b = Func.sumNotNull(cycleData, "runtime_b");}
		catch (NullPointerException e) {run.runtime_b = null;}
		try {run.runtime_g1 = Func.sumNotNull(cycleData, "runtime_g1");}
		catch (NullPointerException e) {run.runtime_g1 = null;}
		try {run.runtime_g2 = Func.sumNotNull(cycleData, "runtime_g2");}
		catch (NullPointerException e) {run.runtime_g2 = null;}

		try {run.a = Func.sumNotNull(cycleData, "a");}
		catch (NullPointerException e) {run.a = null;}
		try {run.b = Func.sumNotNull(cycleData, "b");}
		catch (NullPointerException e) {run.b = null;}
		try {run.r = Func.sumNotNull(cycleData, "r");}
		catch (NullPointerException e) {run.r = null;}		
		try {run.g1 = Func.sumNotNull(cycleData, "g1");}
		catch (NullPointerException e) {run.g1 = null;}		
		try {run.g2 = Func.sumNotNull(cycleData, "g2");}
		catch (NullPointerException e) {run.g2 = null;}		
		
		// calculate sigma
		try { run.ba_sig=Func.sigmaNotNull(cycleData, "ba"); } 
		catch (NullPointerException e) { log.debug("Creating ba_sig from cyles error!");}
		try { run.ra_sig=Func.sigmaNotNull(cycleData, "ra"); } 
		catch (NullPointerException e) { log.debug("Creating ra_sig from cyles error!"); }
		try { run.rb_sig=Func.sigmaNotNull(cycleData, "rb"); } 
		catch (NullPointerException e) { log.debug("Creating rb_sig from cyles error!"); }

		return run;
	}

	/**
	 * @param mag
	 * @return results of query
	 */
	public ResultSet queryResults(String mag) {
		ResultSet result;
		String query=null;
		try {
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(10);
			query = "select "+ANA+".r, "+ANA+".a, "+ANA+".b, "+ANA+".runtime, "+ANA+".cycle, "+ANA+".iso, "+ANA+".strippr, "+ANA+".run," +
					" "+PROTO+".run, magazine, posit, label, to_char("+PROTO+".timedat, 'DD.MM.YYYY HH24:MI:SS') as timedate, "+
					PROTO+".timedat from "+ANA+", "+PROTO+" " +
					"where "+PROTO+".run="+ANA+".run and magazine='" + mag + "' order by "+ANA+".run asc, "+ANA+".cycle asc";
			result = stmt.executeQuery (query);		
			log.debug("SQL-query was successful: "+query);
		}
		catch (SQLException e) {
			log.error("sql exception in OracleRead! Query of sample information failed: "+query+e);
			log.error(query);
			log.error(e);
			String message = String.format( "Query of sample information failed: "+query);
		    JOptionPane.showMessageDialog( null, message );
			result = null;
		}
		return result;
	}
	
	/**
	 * @param magazine
	 * @param data 
	 */
	@SuppressWarnings("unchecked")
	public void getRunList( String magazine, Calc data) {
		ResultSet resultSet = this.queryResults(magazine);
		ArrayList<Run> runList = new ArrayList<Run>();
		Double r=null, a=null, b=null, g1=null, g2=null;
		Double tR=null, tA=null, tB=null, tG1=null, tG2=null;
		Double dtR=null, dtA=null, dtB=null, dtG1=null, dtG2=null, dt=null;
		Integer isoA=-1;
		Integer isoB=-1;
		Integer isoR=-1;
		Integer isoG1=-1;
		Integer isoG2=-1;
		try {
			isoA=Setting.getInt("/bat/isotope/db/ora/a");
			isoB=Setting.getInt("/bat/isotope/db/ora/b");
			isoR=Setting.getInt("/bat/isotope/db/ora/r");
			isoG1=Setting.getInt("/bat/isotope/db/ora/g1");
			isoG2=Setting.getInt("/bat/isotope/db/ora/g2");
			log.debug(isoR+"/"+isoA+"/"+isoB+"/"+isoG1+"/"+isoG2);
		} catch (NullPointerException e) {
			String message = String.format("Could not get isotope import information!");
		    JOptionPane.showMessageDialog( null, message );
		    log.debug("Could not get isotope import information!");
		}
		Integer iso=null;
		Integer firstIso=null;
		String lastRun=null;
		try{
			Run tempData = null;
			while (resultSet.next()) {
				iso=resultSet.getInt("iso");
//				log.debug("isotope: "+iso+" run: "+resultSet.getObject("run")+" cycle: "+resultSet.getInt("cycle"));
				
				if (lastRun==null) {
					tR=0.0;tA=0.0;tB=0.0;tG1=0.0;tG2=0.0;
					dtR=0.0;dtA=0.0;dtB=0.0;dtG1=0.0;dtG2=0.0;
					lastRun=(String)resultSet.getObject("run");
					firstIso=resultSet.getInt("iso");					
//					log.debug("First run was: "+lastRun);
				} else if ( !lastRun.equals((String)resultSet.getObject("run")) || iso.equals(firstIso) ) {
					try { tempData.set(r,"r"); } catch (NullPointerException e){;}
					try { tempData.set(a,"a"); } catch (NullPointerException e){;}
					try { tempData.set(b,"b"); } catch (NullPointerException e){;}
					try { tempData.set(g1,"g1"); } catch (NullPointerException e){;}
					try { tempData.set(g2,"g2"); } catch (NullPointerException e){;}
					try { tempData.set(tR,"runtime"); } catch (NullPointerException e){;}
					try { tempData.set(tA,"runtime_a"); } catch (NullPointerException e){;}
					try { tempData.set(tB,"runtime_b"); } catch (NullPointerException e){;}
					try { tempData.set(tG1,"runtime_g1"); } catch (NullPointerException e){;}
					try { tempData.set(tG2,"runtime_g2"); } catch (NullPointerException e){;}
					try { tempData.set(dtR,"dt_r"); } catch (NullPointerException e){;}
					try { tempData.set(dtA,"dt_a"); } catch (NullPointerException e){;}
					try { tempData.set(dtB,"dt_b"); } catch (NullPointerException e){;}
					try { tempData.set(dtG1,"dt_g1"); } catch (NullPointerException e){;}
					try { tempData.set(dtG2,"dt_g2"); } catch (NullPointerException e){;}
					try { tempData.ra=(r/tR)/(a/tA); } catch (NullPointerException e){;}
					try { tempData.ba=(b/tB)/(a/tA); } catch (NullPointerException e){;}
					try { tempData.rb=(r/tR)/(b/tB); } catch (NullPointerException e){;}
					runList.add(tempData);
//					log.debug("r/a/b/g1/g2: "+r+"/"+a+"/"+b+"/"+g1+"/"+g2);
//					log.debug("tr/ta/tb/tg1/tg2: "+tR+"/"+tA+"/"+tB+"/"+tG1+"/"+tG2);					
					r=0.0;a=0.0;b=0.0;g1=0.0;g2=0.0;
					tR=0.0;tA=0.0;tB=0.0;tG1=0.0;tG2=0.0;
					dtR=0.0;dtA=0.0;dtB=0.0;dtG1=0.0;dtG2=0.0;

					lastRun=resultSet.getObject("run").toString();
					firstIso=resultSet.getInt("iso");
				}
				
				if (iso.equals(firstIso)) {
					dt=0.0;
					tempData = new Run(data.setSample(resultSet.getString("label")));
					tempData.setValue(resultSet.getString("run"),"run");
					tempData.set((Double)resultSet.getDouble("strippr"),"stripper");
					tempData.setValue(resultSet.getString("run"),"run");
					tempData.setValue(resultSet.getString("magazine"),"magazine");
					tempData.set((Integer)resultSet.getInt("posit"),"posit");
					tempData.setValue(resultSet.getString("label"),"label");
					tempData.setValue(resultSet.getString("timedate"),"timedatora");
					Integer cycle = resultSet.getInt("cycle");
					tempData.set(cycle,"cycles");
				} 
				if (iso.equals(isoA)) {
					a=resultSet.getDouble("r");
					tA=resultSet.getDouble("runtime");
					dtA=dt+(tA/2);
					dt+=tA;
				} else if (iso.equals(isoR)) {
					r=resultSet.getDouble("r");
					tR=resultSet.getDouble("runtime");
					dtR=dt+(tR/2);
					dt+=tR;
				}
				else if (iso.equals(isoB)) {
					b=resultSet.getDouble("r");
					tB=resultSet.getDouble("runtime");
					dtB=dt+(tB/2);
					dt+=tB;
//					log.debug(iso+": "+resultSet.getDouble("r")+" time: "+resultSet.getDouble("runtime")+" - "+resultSet.getDouble("iso"));
				}
				else if (iso.equals(isoG1)) {
					g1=resultSet.getDouble("r");
					tG1=resultSet.getDouble("runtime");
					dtG1=dt+(tG1/2);
					dt+=tG1;
//					log.debug(iso+": "+resultSet.getDouble("r")+" time: "+resultSet.getDouble("runtime")+" - "+resultSet.getDouble("iso"));
				}
				else if (iso.equals(isoG2)) {
					g2=resultSet.getDouble("r");
					tG2=resultSet.getDouble("runtime");
					dtG2=dt+(tG2/2);
					dt+=tG2;
				} else {
					log.debug("Isotope not read in: "+lastRun+"/"+iso);
				}
			}
			try { tempData.set(r,"r"); } catch (NullPointerException e){;}
			try { tempData.set(a,"a"); } catch (NullPointerException e){;}
			try { tempData.set(b,"b"); } catch (NullPointerException e){;}
			try { tempData.set(g1,"g1"); } catch (NullPointerException e){;}
			try { tempData.set(g2,"g2"); } catch (NullPointerException e){;}
			try { tempData.set(tR,"runtime"); } catch (NullPointerException e){;}
			try { tempData.set(tA,"runtime_a"); } catch (NullPointerException e){;}
			try { tempData.set(tB,"runtime_b"); } catch (NullPointerException e){;}
			try { tempData.set(tG1,"runtime_g1"); } catch (NullPointerException e){;}
			try { tempData.set(tG2,"runtime_g2"); } catch (NullPointerException e){;}
			try { tempData.set(dtR,"dt_r"); } catch (NullPointerException e){;}
			try { tempData.set(dtA,"dt_a"); } catch (NullPointerException e){;}
			try { tempData.set(dtB,"dt_b"); } catch (NullPointerException e){;}
			try { tempData.set(dtG1,"dt_g1"); } catch (NullPointerException e){;}
			try { tempData.set(dtG2,"dt_g2"); } catch (NullPointerException e){;}
			try { tempData.ra=(r/tR)/(a/tA); } catch (NullPointerException e){;}
			try { tempData.ba=(b/tB)/(a/tA); } catch (NullPointerException e){;}
			try { tempData.rb=(r/tR)/(b/tB); } catch (NullPointerException e){;}
			runList.add(tempData);
//			log.debug("r/a/b/g1/g2: "+r+"/"+a+"/"+b+"/"+g1+"/"+g2);
//			log.debug("tr/ta/tb/tg1/tg2: "+tR+"/"+tA+"/"+tB+"/"+tG1+"/"+tG2);					
		} catch (SQLException e) {
			log.debug("sql exception error!");
			log.debug(e);
			e.printStackTrace(System.out);
		}
		
		if (cycle==false) {
			log.debug("Reduce cycles");
			data.runListL=reduceCycle(runList);
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
		for (int k=0; k<runList.size(); k++){
			Run tempData = runList.get(k);
			int index=0;
			while (!sampleInfo.get(index++).label.equals(tempData.sample.label)&&index<1000) {;}
			index=index-1;
			for (int i=0; i<nameFinalList.size();i++) {
				try {
					tempData.setValue(sampleInfo.get(index).get(nameFinalSetList.get(i)), nameFinalSetList.get(i));
				} catch(NullPointerException e){
					log.debug("Info set error: "+nameFinalSetList.get(i)+" for label "+tempData.sample.label);
				}
			}
//			tempData.setValue(sampleInfo.get(index).get("posit"), "posit");
//			log.debug("Position: "+sampleInfo.get(index).get("posit"));
		}
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

