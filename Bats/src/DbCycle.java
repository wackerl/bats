import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * @author lukas
 *
 */
public class DbCycle
{
	static Logger log = Logger.getLogger(DbCycle.class);
	
	private boolean ora;
	private Document doc;
	private Connection conn;
	
	private String cycle_t;
//	private String proto;
	
	DbCycle(Connection conn)  throws IOException, JDOMException {
		this.conn=conn;
		ora = Setting.getElement("/bat/isotope/db/ora").getAttribute("active").getBooleanValue();
		if (ora) {
			cycle_t = "calcana";
//			proto = "calcproto";
		} else {
			cycle_t = Setting.getString("/bat/isotope/db/"+Setting.db+"/cycle_t");
//			proto = "+Setting.getString("/bat/isotope/db/"+Setting.db+"/proto");
		}
		SAXBuilder builder = new SAXBuilder();
//		log.debug(Setting.isotope);
		doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/cycle.xml"));
	}
	
	/**
	 * @param run 
	 * @return List of Runs
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Cycle> getCycleList(String run) {
		List<Element> list;
		
		list = doc.getRootElement().getChild("cycle").getChildren();
		log.debug("read cycles");
		
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> nameSetList = new ArrayList<String>();
		for (int i=0; i<list.size(); i++){
			nameList.add(list.get(i).getText());
			nameSetList.add(list.get(i).getAttributeValue("name"));
		}
		ResultSet resultSet = this.queryCycles(run, nameList);
		
		ArrayList<Cycle> runCyc = new ArrayList<Cycle>();
		int i=0;
		try{
			while (resultSet.next()) {
				Cycle tempData = new Cycle(run);
//				log.debug(resultSet.getString("a"));
				for (i=0; i<nameList.size(); i++){
					tempData.setValue(resultSet.getString(nameList.get(i)), nameSetList.get(i));
				}
				if (ora) {
					tempData.setValue(resultSet.getString("timedat"), "timedatora");
				} else {
					tempData.setValue(resultSet.getString("timedat"), "timestamp");
				}
				runCyc.add(tempData);
			}
			log.debug("Size of run list: "+runCyc.size());
		} catch (SQLException e){
			log.error("sql exception in CycleRead! Faild to get tempData.");
		} catch (NullPointerException e) {
			log.error("Null pointer exception in reading results!");
			log.debug(nameSetList.get(i));
		}

        return runCyc;
	}
	
	/**
	 * @param run 
	 * @param nameList 
	 * @return results of query
	 */
	public ResultSet queryCycles(String run, ArrayList<String> nameList){
		ResultSet result;
		String query=null;
		try{
			Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    stmt.setQueryTimeout(10);
			
			query = "select ";
			for (int i=0; i<nameList.size(); i++) {
				query+=nameList.get(i)+",";
			}	
			if (ora) {
				query+="to_char(timedat, 'DD.MM.YYYY HH24:MI:SS') as timedat, timedat ";
				query+="from "+cycle_t+" where run='"+run+"' order by cycle asc";
			} else {
				query+="timedat ";
				query+="from "+cycle_t+" where run='"+run+"' order by cycle asc";
			}
			
//			query=JOptionPane.showInputDialog("Query: ", query);
			log.debug("query start");
			result = stmt.executeQuery (query);
			log.debug("sql querry to get cycles was successful: "+query);
		}
		catch (SQLException e) {
			String message = String.format( "Query of cycles failed!");
		    JOptionPane.showMessageDialog( null, message );
			result = null;
			log.error("sql exception: Query failed! "+query+"");
			log.debug(e);
			e.printStackTrace();
		}
		return result;
	}
	
    /**
     * @param run 
     * @param cycle 
     * @param active 
     */
    public void setActive(String run, Integer cycle, boolean active) {
    	if (ora) {
    		String query="";
    		if (active) {
    			query = "update "+cycle_t+" set CYCLTRUE=NULL where run='"+run+"' and cycle='"+cycle+"'";
    		} else {
    			query = "update "+cycle_t+" set CYCLTRUE='x' where run='"+run+"' and cycle='"+cycle+"'";
    		}
			try {
				Statement stmt = conn.createStatement ();
			    stmt.setQueryTimeout(10);
				stmt.executeUpdate(query);
	    	}
			catch (SQLException e) {
				String message = String.format( "<html>Update failed!<br>('"+query+"')</html>");
			    JOptionPane.showMessageDialog( null, message );
				log.debug(e);
				e.printStackTrace();
			}
    	} else {
    		try {
	    		String query = "call "+Setting.getString("/bat/isotope/db/"+Setting.db+"/cycle_enable")+
	    		"("+active+",'"+run+"','"+cycle+"')";
	    		log.debug(query);
				Statement stmt = conn.createStatement ();
			    stmt.setQueryTimeout(10);
				stmt.execute(query);
	    	}
			catch (SQLException e) {
				String message = String.format( "Update failed!");
			    JOptionPane.showMessageDialog( null, message );
				log.debug(e);
				e.printStackTrace();
			}
    	}
    }
    
}
	
class Cycle implements DataSet {
	final static Logger log = Logger.getLogger(DbCycle.class);
	protected String run;
	/**
	 * 
	 */
	public Integer cycle;
	protected String label;
	protected String commentinfo;
	protected String userlabel;

	protected Double r;
	protected Double a;
	protected Double iso;
	protected Double ana;
	protected Double anb;
	protected Double b;
	protected Double g1;
	protected Double g2;
	protected Double runtime;
	
	protected Double ra;
	protected Double ra_sig;
	protected Double ra_err;
	
	protected Double rb;
	protected Double rb_sig;
	protected Double rb_err;
	
	protected Double ba;
	protected Double ba_sig;
	protected Double ba_err;
	
	protected Double stripper;
	
	protected Date timedat;
	
	protected Double tra;
	protected Double tra_sig;
	
	/**
	 * 
	 */
	public Boolean active;
	
	Cycle(String run) {
		this.run = run;
		active = true;
	}
	
	/**
	 * @param name
	 * @return value
	 */
	public Object get(String name) {
		Object returnVal = null;
		if (name.equals("ra")) { returnVal = ra; }
		else if (name.equals("ra_err")) { returnVal = ra_err; }
		else if (name.equals("ra_sig")) { returnVal = ra_sig; }
		else if (name.equals("ra_errR")) { returnVal = ra_err/ra; }
		else if (name.equals("ra_sigR")) { returnVal = ra_sig/ra; }
		else if (name.equals("a")) { returnVal = a; }
		else if (name.equals("iso")) { returnVal = iso; }
		else if (name.equals("ana")) { returnVal = ana; }
		else if (name.equals("anb")) { returnVal = anb; }
		else if (name.equals("b")) { returnVal = b; }
		else if (name.equals("r")) { returnVal = r; }
		else if (name.equals("ba")) { returnVal = ba; }
		else if (name.equals("ba_err")) { returnVal = ba_err; }
		else if (name.equals("ba_sig")) { returnVal = ba_sig; }
		else if (name.equals("ba_errR")) { returnVal = ba_err/ba; }
		else if (name.equals("ba_sigR")) { returnVal = ba_sig/ba; }
		else if (name.equals("g1")) { returnVal = g1; }
		else if (name.equals("g2")) { returnVal = g2; }
		else if (name.equals("tra")) { returnVal = tra; }
		else if (name.equals("tra_sig")) { returnVal = tra_sig; }
		else if (name.equals("iso")) { returnVal = iso; }
		else if (name.equals("stripper")) { returnVal = stripper; }
		else if (name.equals("run")) { returnVal = run; }
		else if (name.equals("runtime")) { returnVal = runtime; }
		else if (name.equals("label")) { returnVal = label; }
		else if (name.equals("active")) {returnVal = active;}
		else if (name.equals("cycles")) {returnVal = cycle;}
		else if (name.equals("cycle")) {returnVal = cycle;}
		else {
			String message = String.format("");
	//		JOptionPane.showMessageDialog( null, message );
			returnVal = message;
//				log.info("object "+name+" not available!"); // this is not necessary, because not all fields are available
		}
		return returnVal;
	}

	
	/**
	 * @param value
	 * @param name
	 */
	public void set( Object value, String name ) {
		try {
			if (value.equals("null")){value=null;}
			else if (name.equals("r")) {r = (Double) value;}
			else if (name.equals("a")) {a = (Double) value;}
			else if (name.equals("iso")) {iso = (Double) value;}
			else if (name.equals("ana")) {ana = (Double) value;}
			else if (name.equals("anb")) {anb = (Double) value;}
			else if (name.equals("b")) {b = (Double) value;}
			else if (name.equals("g1")) {g1 = (Double) value;}
			else if (name.equals("g2")) {g2 = (Double) value;}
			else if (name.equals("runtime")) {runtime = (Double) value;}
			else if (name.equals("ra")) {ra = (Double) value;}
			else if (name.equals("ra_err")) {ra_err = (Double) value;}
			else if (name.equals("ra_sig")) {ra_err = (Double) value;}
			else if (name.equals("ra_errR")) {ra_err = (Double) value * ra;}
			else if (name.equals("ra_sigR")) {ra_sig = (Double) value * ra;}
			else if (name.equals("rb")) {rb = (Double) value;}
			else if (name.equals("rb_err")) {rb_err = (Double) value;}
			else if (name.equals("rb_sig")) {rb_err = (Double) value;}
			else if (name.equals("rb_errR")) {rb_err = (Double) value * rb;}
			else if (name.equals("rb_sigR")) {rb_sig = (Double) value * rb;}
			else if (name.equals("ba")) {ba = (Double) value;}
			else if (name.equals("ba_pc")) {ba = (Double) value;}
			else if (name.equals("ba_err")) {ba_err = (Double) value/100;}
			else if (name.equals("ba_sig")) {ba_sig = (Double) value;}
			else if (name.equals("ba_errR")) {ba_err = (Double) value * ba;}
			else if (name.equals("ba_sigR")) {ba_sig = (Double) value * ba;}
			else if (name.equals("label")) {label = (String)value;}
			else if (name.equals("cycle")) {cycle = (Integer)value;}
			else if (name.equals("commentinfo")) {commentinfo = (String)value;}
			else if (name.equals("userlabel")) {userlabel = (String)value;}
			else if (name.equals("tra")) {tra = (Double) value;}
			else if (name.equals("tra_sig")) {tra_sig = (Double) value;}
			else if (name.equals("stripper")) {stripper = (Double) value;}
			else if (name.equals("active")) {active = (Boolean) value;}
			else {
				String message = String.format("FormatT error in set function in correction! " + name + " - " + value);
				log.debug(message);
			}
		}
		catch (NumberFormatException e) {log.error("FormatT error in set function in correction!");}
		catch (ClassCastException e) {log.error(value+" is not the right object type!");}
	}
	
	/**
	 * @param value
	 * @param name
	 */
	public void setValue( String value, String name ) {
		try {
			if (value==null) /*log.debug("Set value of "+name+" is null!")*/;
			else if (value.equals("null")){value=null;}
			else if (name.equals("r")) {r = Double.valueOf(value);}
			else if (name.equals("a")) {a = Double.valueOf(value);}
			else if (name.equals("iso")) {iso = Double.valueOf(value);}
			else if (name.equals("ana")) {ana = Double.valueOf(value);}
			else if (name.equals("anb")) {anb = Double.valueOf(value);}
			else if (name.equals("b")) {b = Double.valueOf(value);}
			else if (name.equals("g1")) {g1 = Double.valueOf(value);}
			else if (name.equals("g2")) {g2 = Double.valueOf(value);}
			else if (name.equals("runtime")) {runtime = Double.valueOf(value);}
			else if (name.equals("ra")) {ra = Double.valueOf(value)*1000000000000.0;}
			else if (name.equals("ra_err")) {ra_err = Double.valueOf(value);}
			else if (name.equals("ra_sig")) {ra_err = Double.valueOf(value);}
			else if (name.equals("ra_errR")) {ra_err = Double.valueOf(value) * ra;}
			else if (name.equals("ra_sigR")) {ra_sig = Double.valueOf(value) * ra;}
			else if (name.equals("rb")) {rb = Double.valueOf(value);}
			else if (name.equals("rb_err")) {rb_err = Double.valueOf(value);}
			else if (name.equals("rb_sig")) {rb_err = Double.valueOf(value);}
			else if (name.equals("rb_errR")) {rb_err = Double.valueOf(value) * rb;}
			else if (name.equals("rb_sigR")) {rb_sig = Double.valueOf(value) * rb;}
			else if (name.equals("ba")) {ba = Double.valueOf(value);}
			else if (name.equals("ba_pc")) {ba = Double.valueOf(value)/100;}
			else if (name.equals("ba_err")) {ba_err = Double.valueOf(value)/100;}
			else if (name.equals("ba_sig")) {ba_sig = Double.valueOf(value);}
			else if (name.equals("ba_errR")) {ba_err = Double.valueOf(value) * ba;}
			else if (name.equals("ba_sigR")) {ba_sig = Double.valueOf(value) * ba;}
			else if (name.equals("label")) {label = (String)value;}
			else if (name.equals("cycle")) {cycle = Integer.valueOf(value);}
			else if (name.equals("commentinfo")) {commentinfo = (String)value;}
			else if (name.equals("userlabel")) {userlabel = (String)value;}
			else if (name.equals("stripper")) {stripper = Double.valueOf(value);}
			else if (name.equals("timedatora")) {
				SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy kk:mm:ss" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) {
					log.debug("Wrong date format: "+value);
				}}
			else if (name.equals("timedat")) {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
//				log.debug(df.format(new Date()));
				try {
					timedat=df.parse(value);
				} catch (ParseException e) {
					log.debug("Wrong date format: "+value);
				}}
			else if (name.equals("date")) {
				SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) {
					log.debug("Wrong date format: "+value);
				}}
			else if (name.equals("time")) {
				timedat.setTime(timedat.getTime()+Integer.valueOf(value.substring(0,2))*3600000
						+Integer.valueOf(value.substring(3,5))*60000);
				}
			else if (name.equals("timestamp")) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					timedat=df.parse(value);
				} catch (ParseException e) {
					log.debug("Wrong date format: "+value);
				}}
			else if (name.equals("tra")) {tra = Double.valueOf(value);}
			else if (name.equals("tra_sig")) {tra_sig = Double.valueOf(value);}
			else if (name.equals("active")) {
				if (value.equals("")) { active = true; } else { active = false; }
			}
			else if (name.equals("run")) {run = value;}
			else {
				String message = String.format("FormatT error in set function in correction! " + name + " - " + value);
				log.debug(message);
			}
		}
		catch (NumberFormatException e) {log.error("FormatT error in set function in correction!");}
		catch (ClassCastException e) {log.error(value+" is not the right object type!");}
	}
	
	/**
	 * @param name
	 * @param sTag 
	 * @param eTag 
	 * @return String with values of a run
	 */
	@SuppressWarnings("unchecked")
	public String getSet( ArrayList<String> name, String sTag, String eTag ) {
		Object returnVal;
		String val = "";
		for (int i=0;i<name.size();i++) {
			returnVal = this.get(name.get(i));
//			if (returnVal==null) {
//				returnVal="null";
//			}
			val += sTag+returnVal+eTag;
		}
		return val;
	}

	public String getSetEx(FormatT format, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer number() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double weight() {
		// TODO Auto-generated method stub
		return runtime*a;
	}
	public boolean active() {
		return active;
	}
	
	
	public double runtime() {
		return runtime;
	}
	
}
