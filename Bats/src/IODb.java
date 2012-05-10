import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
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
public class IODb implements DbConnect {
	final static Logger log = Logger.getLogger(IODb.class);
	
	Bats main;
	Calc data;
	
	String url;
    String user;
    String pw;
    String lm;
    String ly;
    String target_t;
    String corr_t;
    String calcset_t;
    String calc_sample_t;
    Boolean cycle_imp;
    Integer cycle_nr;
    String run_t;
    String cycle_t;
    String calcset_null="";
    Integer timeout;
    Integer sampleMin;
    Calendar date, date2;
    
    /**
     * 
     */
    public Connection conn;
    String sql;

    ArrayList<String> nameJS = new ArrayList<String>();
	ArrayList<String> nameDbS = new ArrayList<String>();
	ArrayList<String> nameJR = new ArrayList<String>();
	ArrayList<String> nameDbR = new ArrayList<String>();
	ArrayList<String> openDbR = new ArrayList<String>();
	ArrayList<String> openDbS = new ArrayList<String>();
	ArrayList<String> openJR = new ArrayList<String>();
	ArrayList<String> openJS = new ArrayList<String>();

//	private String	calcSet;

	/**
	 * @param main 
	 */
	public IODb(Bats main) {
		this.main = main;
		this.data = main.data;
		Setting.db="sql";
	}
	
	/**
	 * 
	 */
	public void getSettings() {
//		calcset_null=" AND calcset IS NULL";
		url = Setting.getString("/bat/isotope/db/sql/url");
	    user = Setting.getString("/bat/isotope/db/sql/user");
	    pw = Setting.getString("/bat/isotope/db/sql/pw");
	    timeout = Setting.getInt("/bat/isotope/db/sql/timeout");
	    sampleMin = Setting.getInt("/bat/isotope/db/sql/sample_min");
	    lm = Setting.getString("/bat/isotope/db/sql/last_mag");
	    int timespan = Setting.getInt("/bat/isotope/db/sql/timespan");
	    try {
			if (Setting.getElement("/bat/isotope/db/sql/last_year").getAttribute("auto").getBooleanValue()) {
				date2 = Calendar.getInstance();
				date2.add(Calendar.DAY_OF_MONTH,1);
//				log.debug(date2.getTime().getTime());
				date = (Calendar) date2.clone();
				date.add(Calendar.MONTH,-(timespan-2));
//				log.debug(date2.getTime().getTime());
//				log.debug(date.getTimeInMillis());
			} else {					
				date.set(Setting.getInt("/bat/isotope/db/sql/last_year"),1,1);
				date2 = (Calendar) date.clone();
				date2.add(Calendar.YEAR,1);
			}
		} catch (DataConversionException e1) {
			date2 = Calendar.getInstance();
			date = (Calendar) date2.clone();
			date.add(Calendar.YEAR,-1);
			log.warn("Could not load year from settings -> set to last year");
		}
	    target_t = Setting.getString("/bat/isotope/db/sql/target_t");   // sample table
	    corr_t = Setting.getString("/bat/isotope/db/sql/calc_corr_t");   // correction table
	    calcset_t = Setting.getString("/bat/isotope/db/sql/calcset_t");   // calc set table
	    calc_sample_t = Setting.getString("/bat/isotope/db/sql/calc_sample_t");   // run table
	    run_t = Setting.getString("/bat/isotope/db/sql/run_t");   // run table
	    cycle_t = Setting.getString("/bat/isotope/db/sql/cycle_t");   // run table
		try {
			cycle_imp = Setting.getElement("/bat/isotope/db/sql/cycle").getAttribute("name").getBooleanValue();
			cycle_nr = Setting.getInt("/bat/isotope/db/sql/cycle");
			log.debug("Cycle import set "+cycle_imp+" ("+cycle_nr+")");
		} catch (DataConversionException e) {
			cycle_imp=false;
			log.debug("Cycle setting not found!");
		}
	    
	 	String driver = Setting.getString("/bat/isotope/db/sql/driver");
		try {
			Class.forName(driver).newInstance();
			log.debug("DB driver loaded: "+driver);
		} catch (Exception e) {
			log.error("DB driver could not be loaded! ("+driver+")");
			log.error(e);
		}
	}
	
	/**
	 * 
	 */
	public void logout() {
		try {
			conn.close();
			conn=null;
		} catch (SQLException e) {
			log.debug("DB connection already closed!");
		} catch (NullPointerException e) {
			log.debug("DB connection already closed!");
		}
		pw="";
	}
	
	@SuppressWarnings("unchecked")
	private void getSelectSQL() {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new File(Setting.batDir+"/"+Setting.isotope+"/db_io/eth_nt.xml"));
			List<Element> list;			
			list = doc.getRootElement().getChild("sample_save").getChildren();
			nameJS.clear();
			nameDbS.clear();
			for (int i=0; i<list.size(); i++){
				nameJS.add(list.get(i).getText());
				nameDbS.add(list.get(i).getAttributeValue("field"));
			}
			openJR.clear();
			openDbR.clear();
			if (cycle_imp) {
				list = doc.getRootElement().getChild("cycle_open").getChildren();
				for (int i=0; i<list.size(); i++){
					openJR.add(list.get(i).getText());
					openDbR.add(list.get(i).getAttributeValue("field"));
				}
			} else {
				list = doc.getRootElement().getChild("run_open").getChildren();
				for (int i=0; i<list.size(); i++){
					openJR.add(list.get(i).getText());
					openDbR.add(list.get(i).getAttributeValue("field"));
				}
			}			
			list = doc.getRootElement().getChild("sample_open").getChildren();
			openJS.clear();
			openDbS.clear();
			for (int i=0; i<list.size(); i++){
				openJS.add(list.get(i).getText());
				openDbS.add(list.get(i).getAttributeValue("field"));
			}
			doc.removeContent();
			log.debug("read db settings");
		} catch (JDOMException e) {
			log.error("JDOMExeption error reading eth_nt.xml");
		} catch (IOException e) {
			log.error("IOExeption error reading eth_nt.xml");
		}	
	}
	
	/**
	 * 
	 */
	public void saveAs() {
		try {
			data.calcSet = (String) (JOptionPane.showInputDialog("Select name to save data: ", data.calcSet));
			if (data.calcSet.length()>20) {
				try {
					data.calcSet=data.calcSet.substring(0,20);
				} catch (StringIndexOutOfBoundsException e) {;}
			}
			save();
		} catch (NullPointerException e) { 
			log.debug("Aborded save!"); 
		}		
	}
	
	/**
	 * 
	 */
	public void save() {
	    String calcSet = data.calcSet.replaceAll(" ","_");
	    log.debug(calcSet);
	    if (calcSet!=null) {
			if (data.runListR.size()>0) {
				main.tba.update("Save data to DB",true);
				Statement stmt = null;
		    	if (conn==null) {
		    		log.debug("Start login");
		    		conn = login();
		    	}
		    	if (conn!=null) {
				    try {	
						log.debug("Connection opend to "+url);
					    stmt = conn.createStatement();
					    stmt.setQueryTimeout(timeout);
					    // Check if calcSet exists
					    ResultSet res = stmt.executeQuery("SELECT calcSet, edit FROM "+calcset_t+" WHERE calcSet='"+calcSet+"'");
					    if( res.next()) {
					    	if (res.getBoolean("edit")) {
								int option = JOptionPane.showConfirmDialog(null, "Do your want to overwrite '"+calcSet+"'?", "DB save", JOptionPane.YES_NO_OPTION);
								if (option == JOptionPane.YES_OPTION) {
									stmt.execute("UPDATE "+target_t+" SET calcset=null WHERE calcset='"+calcSet+"'");
									data.calcSet=calcSet;
									upload(data, conn);
									main.tba.update("Overwritten in db!",false);
								} else {
						    		log.info("Didn't save to db!");
									main.tba.update("Didn't save to db",false);
								}
					    	} else {
								String message = String.format( "<html>Your are not allowed to overwrite calc-set: "+calcSet+"</html>");
							    JOptionPane.showMessageDialog( null, message );
							    log.info("Your are not allowed to overwrite calc-set: "+calcSet);
								main.tba.update("Didn't save to db",false);
					    	}
					    } else {
					    	log.debug("Start upload");
							data.calcSet=calcSet;
							upload(data, conn);
							main.tba.update("Saved to db!",false);
					    }
					} catch (SQLException e) {
						log.error("Could not execute insert");
					    log.debug("SQLException: " + e.getMessage());
					    logout();
						main.tba.update("Try again!",true);
					} finally {
					    if (stmt != null) {
					        try {
					            stmt.close();
					        } catch (SQLException e) {
								log.error("Could not insert data ");
							    log.info("SQLException: " + e.getMessage());
							    log.info("SQLState: " + e.getSQLState());
							    log.info("VendorError: " + e.getErrorCode());
					        }
					        stmt = null;
					    }
					}
		    	} else {
		    		log.info("Could not login!");
					main.tba.update("Couldn't save!",true);
		    	}
			}
	    }
	}
	
	/**
	 * @param data 
	 * @param conn 
	 * 
	 */
	private void upload(Calc data, Connection conn) {
		main.act.exec("CalibRanges");
		if (data.runListR.size()>0) {
			main.tba.update("Save data to DB",true);
			Statement stmt = null;
		    String nameQ = "";
		    try {	
			    stmt = conn.createStatement();
			    stmt.setQueryTimeout(timeout);
			    
		    	nameQ = "";
			    for (int j=0;j<nameDbS.size();j++) {
			    	if (!nameDbS.get(j).equalsIgnoreCase("calcset")) {
			    		nameQ+=nameDbS.get(j)+"=null,";
			    	}
			    }
			    nameQ+="calcset=null";
			    sql = "UPDATE "+target_t+" SET "+nameQ+" WHERE calcset='"+data.calcSet+"'";
			    stmt.execute(sql);
			    
				uploadCorr(data, conn);

			    for (int i=0;i<data.sampleList.size();i++) {
			    	nameQ = "";
				    for (int j=0;j<nameDbS.size();j++) {
				    	if (!nameDbS.get(j).equalsIgnoreCase("calcset")) {
				    		nameQ+=nameDbS.get(j)+"='"+data.sampleList.get(i).get(nameJS.get(j))+"',";
				    	}
				    }
				    nameQ+="calcset='"+data.calcSet+"'";
			    	
			    	nameQ = nameQ.replaceAll("'true'","true").replaceAll("'false'","false")
			    	.replaceAll("'null'","null").replaceAll("'NaN'","null").replace("'Infinity'","null");
			    	
			    	sql="UPDATE "+target_t+" SET "+nameQ+
			    	" WHERE sample_nr="+data.sampleList.get(i).sample_nr+
			    	" AND prep_nr="+data.sampleList.get(i).prep_nr+
			    	" AND target_nr="+data.sampleList.get(i).target_nr;
				    stmt.execute(sql);
				    
				    sql="INSERT INTO "+calc_sample_t+" (sample_nr, prep_nr, target_nr, calcset, type, prep_bl) VALUES ("
				    +data.sampleList.get(i).sample_nr+", "+data.sampleList.get(i).prep_nr+", "
				    +data.sampleList.get(i).target_nr+", '"+data.calcSet+"', '"+data.sampleList.get(i).type+"', '"+data.sampleList.get(i).prep_bl+"')";
				    stmt.execute(sql);
				    
//			    log.debug(sql);
			    }
			    log.debug("Inserted into "+target_t);
			    log.debug("Inserted into "+run_t);
				main.tba.update("Saved to db!",false);
			} catch (SQLException e) {
				log.warn("Could not execute insert");
			    log.info("SQLException: " + e.getMessage());
			    log.info("SQLState: " + e.getSQLState());
			    log.info("Query: " + sql);
			    log.info("VendorError: " + e.getErrorCode());
				String message = String.format( "<html>Could not insert data<br>SQLException: "+e.getMessage()
						+"</html>");
			    JOptionPane.showMessageDialog( null, message );
			} finally {
			    if (stmt != null) {
			        try {
			            stmt.close();
			        } catch (SQLException e) {
						log.error("Could not insert data ");
					    log.info("SQLException: " + e.getMessage());
					    log.info("SQLState: " + e.getSQLState());
					    log.info("VendorError: " + e.getErrorCode());
			        }
			        stmt = null;
			    }
			}
		}
	}
		
	/**
	 * @param data
	 * @param conn
	 * @throws SQLException 
	 */
	private void uploadCorr(Calc data, Connection conn) throws SQLException {
		Statement stmt=null;
	    stmt = conn.createStatement();
	    stmt.setQueryTimeout(timeout);
	    log.debug("deleted all samples for "+data.calcSet);
	    
	    sql = "DELETE FROM "+corr_t+" WHERE calcset='"+data.calcSet+"'";
	    stmt.execute(sql);

		sql = "DELETE FROM "+calc_sample_t+" WHERE calcset='"+data.calcSet+"'";
	    stmt.execute(sql);
	    
	    sql = "DELETE FROM "+calcset_t+" WHERE calcset='"+data.calcSet+"'";
	    stmt.execute(sql);
	    log.debug("deleted "+data.calcSet+" in "+corr_t+" and "+calcset_t);
	    
	    String nameQ = "calcset, date_calc, magazine, isobar, "+
						"a_err_abs, a_err_rel, a_off, "+
						"b_err_abs, b_err_rel, b_off, "+
						"charge, first_run, last_run, fract, "+
						"iso_err_abs, iso_err_rel, iso_off, user_calc, comment, "+
						"deadtime, scatter, ra_nom, "+
						"weighting, poisson, cycles";
	    if (Setting.isotope.equalsIgnoreCase("C14")) {
	    	nameQ += ", ba_nom";
	    }
	    int cycles=0;
        try {
			if (Setting.getElement("/bat/isotope/db/sql/cycle").getAttribute("name").getBooleanValue()) {
				cycles=Setting.getInt("/bat/isotope/db/sql/cycle");
			} 
		} catch (DataConversionException e) {  log.debug("Cycle setting not found!"); }
	    String dataQ = "'"+data.calcSet+"','"+data.runListR.get(data.runListR.size()-1).get("timestamp")+"','"+data.magazine+"','"+data.isobar+"','"+
	    				data.a_err+"','"+data.a_errR+"','"+data.a_off+"','"+
	    				data.b_err+"','"+data.b_errR+"','"+data.b_off+"','"+
	    				data.charge+"','"+data.firstRun+"','"+data.lastRun+"','"+Setting.getBoolean("/bat/isotope/calc/fract")+"','"+
	    				data.iso_err+"','"+data.iso_errR+"','"+data.iso_off+"','"+System.getProperty("user.name")+"','"+data.comment.getText()+"','"+
	    				Setting.getDouble("/bat/isotope/calc/dead_time")+"','"+Setting.getDouble("/bat/isotope/calc/scatter")+"','"+Setting.getFloat("/bat/isotope/calc/nominal_ra")+
	    				"','"+Setting.getInt("/bat/isotope/calc/mean")+"','"+Setting.getBoolean("/bat/isotope/calc/poisson")+"','"+cycles+"'";
	    if (Setting.isotope.equalsIgnoreCase("C14")) {
	    	dataQ += ",'"+Setting.getDouble("bat/isotope/calc/nominal_ba")+"'";
	    }
    	dataQ = dataQ.replaceAll("'true'","true").replaceAll("'false'","false")
    	.replaceAll("'null'","null").replaceAll("'NaN'","null").replace("'Infinity'","null");
    	sql = "INSERT INTO "+calcset_t+" ("+nameQ+") VALUES ("+dataQ+")";
	    stmt.executeUpdate(sql);
	    log.debug("Inserted into "+calcset_t);
	    nameQ = "calcset, isobar_fact, isobar_err, "+
	    		"std_ra, std_ra_err, bl_ra, bl_ra_err, "+
	    		"a_slope, a_slope_off, b_slope, b_slope_off, "+
	    		"time_corr, first_run, last_run, corr_index, "+
	    		"bg_const, bg_const_err, bl_const_mass, bl_const, bl_const_err";
	    if (Setting.isotope.equalsIgnoreCase("C14")) {
	    	nameQ += ", std_ba, std_ba_err";
	    }
	    for (int i=0; i<data.corrList.size(); i++) {
	    	Corr corr = data.corrList.get(i);
		    String dataQ2 = "'"+data.calcSet+"','" + corr.isoFact+"','"+corr.isoErr+"','"+
			    corr.std.std_ra+"','"+corr.std.std_ra_err+"','"+corr.blank.ra_bg+"','"+corr.blank.ra_bg_err+"','"+
			    corr.a_slope+"','"+corr.a_slope_off+"','"+corr.b_slope+"','"+corr.b_slope_off+"','"+
			    corr.timeCorr+"','"+corr.firstRun+"','"+corr.lastRun+"','"+i+"','"+
			    corr.constBG+"','"+corr.constBGErr+"','"+corr.constBlWeight+"','"+corr.constBlRatio+"','"+corr.constBlErr+"'";
		    if (Setting.isotope.equalsIgnoreCase("C14")) {
		    	dataQ2 += ",'"+corr.std.std_ba+"','"+corr.std.std_ba_err+"'";
		    }
	    	dataQ2 = dataQ2.replaceAll("'true'","true").replaceAll("'false'","false")
	    	.replaceAll("'null'","null").replaceAll("'NaN'","null").replace("'Infinity'","null");
		    sql="INSERT INTO "+corr_t+" ("+nameQ+") VALUES ("+dataQ2+")";
		    stmt.executeUpdate(sql);
		    log.debug("Inserted into "+corr_t);
	    }
	}
	
	/**
	 * @param conn
	 * @param calcSet 
	 * @param data 
	 * @return corections
	 */
	public ArrayList<Corr> downloadCorr(Connection conn, String calcSet, Calc data) {
		ArrayList<Corr> corrL= new ArrayList<Corr>();
		Statement stmt=null;
		try {
		    stmt = conn.createStatement();
		    stmt.setQueryTimeout(timeout);
		    
		    String nameQ = "calcset, date_calc, magazine, isobar, "+
			"a_err_abs, a_err_rel, a_off, ra_nom, scatter, "+
			"b_err_abs, b_err_rel, b_off, charge, fract, deadtime, "+
			"iso_err_abs, iso_err_rel, iso_off, first_run, last_run, comment, "+
			"cycles, weighting, poisson";
		    if (Setting.isotope.equalsIgnoreCase("C14")) {
		    	nameQ += ", ba_nom";
		    }
			sql="SELECT "+nameQ+" FROM "+calcset_t+" WHERE calcset='"+calcSet+"'";
//	    	log.debug(sql);
			ResultSet resultD = stmt.executeQuery(sql);
			resultD.next();
			data.calcSet = resultD.getString("calcset");
			data.magazine = resultD.getString("magazine");
			data.isobar = resultD.getString("isobar");
			data.firstRun = resultD.getString("first_run");
			data.lastRun = resultD.getString("last_run");
			data.comment.setText(resultD.getString("comment"));
//			log.debug(resultD.getString("comment"));
			Double value = data.a_err = resultD.getDouble("a_err_abs");
			((Element)Setting.getElement("/bat/isotope/calc/current/a/error_abs")).setText(value.toString());
			value = data.a_errR = resultD.getDouble("a_err_rel");
			((Element)Setting.getElement("/bat/isotope/calc/current/a/error_rel")).setText(value.toString());
			value = data.a_off = resultD.getDouble("a_off");
			((Element)Setting.getElement("/bat/isotope/calc/current/a/offset")).setText(value.toString());
			value = data.b_err = resultD.getDouble("b_err_abs");
			((Element)Setting.getElement("/bat/isotope/calc/current/b/error_abs")).setText(value.toString());
			value = data.b_errR = resultD.getDouble("b_err_rel");
			((Element)Setting.getElement("/bat/isotope/calc/current/b/error_rel")).setText(value.toString());
			value = data.a_off = resultD.getDouble("b_off");
			((Element)Setting.getElement("/bat/isotope/calc/current/b/offset")).setText(value.toString());
			value = data.iso_err = resultD.getDouble("iso_err_abs");
			((Element)Setting.getElement("/bat/isotope/calc/current/iso/error_abs")).setText(value.toString());
			value = data.iso_errR = resultD.getDouble("iso_err_rel");
			((Element)Setting.getElement("/bat/isotope/calc/current/iso/error_rel")).setText(value.toString());
			value = data.iso_off = resultD.getDouble("iso_off");
			((Element)Setting.getElement("/bat/isotope/calc/current/iso/offset")).setText(value.toString());
			value = data.deadtime = resultD.getDouble("deadtime");
			((Element)Setting.getElement("/bat/isotope/calc/dead_time")).setText(value.toString());
			Integer value2 = data.charge = resultD.getInt("charge");
			((Element)Setting.getElement("/bat/isotope/calc/current/charge")).setText(value2.toString());
			Boolean value3 = resultD.getBoolean("fract");
			((Element)Setting.getElement("/bat/isotope/calc/fract")).setText(value3.toString());
			value2 = resultD.getInt("weighting");
			((Element)Setting.getElement("/bat/isotope/calc/mean")).setText(value2.toString());
			value3 = resultD.getBoolean("poisson");
			((Element)Setting.getElement("/bat/isotope/calc/poisson")).setText(value3.toString());
			value = resultD.getDouble("scatter");
	    	((Element)Setting.getElement("bat/isotope/calc/scatter")).setText(String.valueOf(value));
			value = resultD.getDouble("ra_nom");
	    	((Element)Setting.getElement("bat/isotope/calc/nominal_ra")).setText(String.valueOf(value));
			if (resultD.getInt("cycles")>0) {
				value2 = resultD.getInt("cycles");
				((Element)Setting.getElement("/bat/isotope/db/sql/cycle")).setText(value2.toString());
			} 
		    if (Setting.isotope.equalsIgnoreCase("C14")) {
		    	value = resultD.getDouble("ba_nom");
		    	((Element)Setting.getElement("bat/isotope/calc/nominal_ba")).setText(value.toString());
		    }
		    log.debug("Calcset loaded.");
		} catch (SQLException e) {
			log.error("Could not get data from "+calcset_t);
		    log.info("SQLException: " + e.getMessage());
		    log.info("SQLState: " + e.getSQLState());
		    log.info("Query: " + sql);
		    log.info("VendorError: " + e.getErrorCode());
		    String message = String.format( "<html>Could not get data from "+calcset_t+"<br>("+e.getMessage()+")</html>");
		    JOptionPane.showMessageDialog( null, message );
		}
		try {			
		    String nameQ = "calcset, isobar_fact, isobar_err, "+
		    		"std_ra, std_ra_err, bl_ra, bl_ra_err, "+
		    		"a_slope, a_slope_off, b_slope, b_slope_off, "+
		    		"time_corr, first_run, last_run, bg_const, bg_const_err, "+
    				"bl_const_mass, bl_const, bl_const_err";
		    if (Setting.isotope.equalsIgnoreCase("C14")) {
		    	nameQ += ", std_ba, std_ba_err";
		    }
		    sql="SELECT "+nameQ+" FROM "+corr_t+" WHERE calcset='"+calcSet+"'";
		    ResultSet resultC = stmt.executeQuery(sql);
		    while (resultC.next()) {
		    	Corr corr = data.newCorrection();
		    	corr.isoFact = resultC.getDouble("isobar_fact");
				((Element)Setting.getElement("/bat/isotope/calc/bg/factor")).setText(corr.isoFact.toString());
		    	corr.isoErr = resultC.getDouble("isobar_err");
				((Element)Setting.getElement("/bat/isotope/calc/bg/error")).setText(corr.isoErr.toString());
		    	corr.constBG = resultC.getDouble("bg_const");
		    	corr.constBGErr = resultC.getDouble("bg_const_err");
		    	corr.constBlWeight = resultC.getDouble("bl_const_mass");
		    	corr.constBlRatio = resultC.getDouble("bl_const");
		    	corr.constBlErr = resultC.getDouble("bl_const_err");
		    	corr.std.std_ra = resultC.getDouble("std_ra");
		    	corr.std.std_ra_err = resultC.getDouble("std_ra_err");
			    if (Setting.isotope.equalsIgnoreCase("C14")) {
			    	corr.std.std_ba = resultC.getDouble("std_ba");
			    	corr.std.std_ba_err = resultC.getDouble("std_ba_err");
			    }
		    	corr.blank.ra_bg = resultC.getDouble("bl_ra");
		    	corr.blank.ra_bg_err = resultC.getDouble("bl_ra_err");
		    	corr.a_slope_off = resultC.getDouble("a_slope_off");
		    	corr.a_slope = resultC.getDouble("a_slope");
		    	corr.a_slope_off = resultC.getDouble("a_slope_off");
		    	corr.b_slope = resultC.getDouble("b_slope");
		    	corr.b_slope_off = resultC.getDouble("b_slope_off");
		    	corr.timeCorr = resultC.getDouble("time_corr");
		    	corr.firstRun = resultC.getString("first_run");
		    	corr.lastRun = resultC.getString("last_run");
		    	corrL.add(corr);
		    }
		    log.debug(corrL.size()+" corrections loaded:");
		} catch (SQLException e) {
			log.error("Could not get data from "+corr_t);
		    log.info("SQLException: " + e.getMessage());
		    log.info("SQLState: " + e.getSQLState());
		    log.info("Query: " + sql);
		    log.info("VendorError: " + e.getErrorCode());
		    String message = String.format( "<html>Could not get data from "+corr_t+"<br>("+e.getMessage()+")</html>");
		    JOptionPane.showMessageDialog( null, message );
	    } finally {
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException e) {
					log.error("Could not execute querry ");
				    log.info("SQLException: " + e.getMessage());
				    log.info("SQLState: " + e.getSQLState());
				    log.info("VendorError: " + e.getErrorCode());
		        }
		        stmt = null;
//		        conn = null;
		    }	
	    }
	    return corrL;
	}
	
	/**
	 * @param magazine 
	 */
	public boolean downloadMag(String magazine) {
		main.tba.update("Get data from DB",true);
		if (magazine!=null) {
		    if (conn==null) {
		    	log.debug("Start login");
		    	conn = login();
		    }
			if (conn!=null) {
			    Statement stmt = null;
				try {
				    if (magazine==null) {
				    	magazine = selectMagazine();
				    }
				    if (magazine!=null) {
					    String selectR = "";
					    String selectS = "";
					    for (int i=0;i<openDbS.size();i++) {
					    	if(!openDbS.get(i).equals("sample_id")&&!openDbS.get(i).equals("magazine")) {
					    		selectS+=openDbS.get(i)+",";
					    	}
					    }
					    selectS += "magazine,sample_id";
					    log.debug("Magazine opened: "+magazine);
						stmt = conn.createStatement();
					    stmt.setQueryTimeout(timeout);
						sql="SELECT "+selectS+" FROM "+target_t+
		    				" WHERE magazine='"+magazine+"' AND sample_nr>"+sampleMin+calcset_null;
				    	log.debug(sql);
						ResultSet result = stmt.executeQuery(sql);
	//					String samples="'";
				    	while (result.next()) {
	//			    		samples+=result.getString("sample_id")+"','";
							Sample samp = data.setSample(result.getString("sample_id"));
							for (int i=0; i<openDbS.size(); i++){
						    	if(!openDbS.get(i).equals("sample_id")&&!openDbS.get(i).equals("magazine")) {
						    		samp.setValue(result.getString(openDbS.get(i)), openJS.get(i));
						    	}
							}
							samp.setValue(result.getString("magazine"), "magazine");
				    	}	    	
				    	log.debug(data.sampleList.size()+" samples loaded.");
				    	if (data.sampleList.size()>0) {
							
							if (cycle_imp) {
							    for (int i=0;i<openDbR.size();i++) {
							    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("magazine")) {
							    		selectR+=cycle_t+"."+openDbR.get(i)+",";
							    	}
							    }
								sql= "SELECT "+selectR+run_t+".run, "+run_t+".sample_id FROM "+cycle_t+", "+run_t+
										" WHERE ("+cycle_t+".run="+run_t+".run) AND "+run_t+".magazine='"+magazine+"' AND cycltrue is null ORDER BY "+cycle_t+".run DESC, "+cycle_t+".cycle ASC";
							} else {
							    for (int i=0;i<openDbR.size();i++) {
							    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("magazine")) {
							    		selectR+=openDbR.get(i)+",";
							    	}
							    }
								sql="SELECT "+selectR+"sample_id,magazine FROM "+run_t+" WHERE magazine='"+magazine+"' AND sample_nr>"+sampleMin;
							}
						
					    	log.debug(sql);
							result = stmt.executeQuery(sql);
					    	while (result.next()) {
					    		Sample samp = data.getSample(result.getString("sample_id"));
					    		if (samp!=null) {
									Run run = new Run( samp );
									for (int i=0; i<openDbR.size(); i++) {
								    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("magazine")) {
								    		run.setValue(result.getString(openDbR.get(i)), openJR.get(i));
								    	}
									}
									data.runListL.add(run);
								}
					    	}	
							if (cycle_imp) {
	//							log.debug("Reduce cycles: "+data.runListL+" divide by "+cycle_nr);
								data.runListL=Func.reduceCycle(data.runListL, cycle_nr);
							}
					    	log.debug(data.runListL.size()+" runs loaded.");
							return true;
				    	} else {
				    		return false;
				    	}
				    }
				    else {
				    	log.debug("Nothing loaded!");
						return false;
				    }
				} catch (SQLException e) {
					log.error("Could not execute download");
				    log.info("SQLException: " + e.getMessage());
				    log.info("Query: " + sql);
				    logout();
				    String message = String.format( "<html>Could not execute download<br>Did logout!<br>Try again!</html>");
				    JOptionPane.showMessageDialog( null, message );
					return false;
			    } finally {
				    if (stmt != null) {
				        try {
				            stmt.close();
				        } catch (SQLException e) {
							log.error("Could not execute querry ");
						    log.info("SQLException: " + e.getMessage());
						    log.info("SQLState: " + e.getSQLState());
						    log.info("VendorError: " + e.getErrorCode());
				        }
				        stmt = null;
	//			        conn = null;
				    }	
			    }
			} else {
				log.info("Didn't login!");
				return false;
			}
		} else return false;
 	}

	/**
	 * 
	 */
	public void openCalc() {
		String calcset = this.selectCalcSet();
		main.tba.update("Get data from DB",true);
//		getSettings();
//	    Calc data = Setting.initCalcIso(Setting.isotope);
//		xmlSelect();
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
		    Statement stmt = null;
			try {
//			    if (calcset==null) {
//			    	calcset = selectCalcSet();
//			    }
			    if (calcset!=null) {
				    String selectR = "";
				    String selectS = "";
				    for (int i=0;i<openDbS.size();i++) {
				    	if(!openDbS.get(i).equals("sample_id")&&!openDbS.get(i).equals("calcset")) {
				    		selectS+=openDbS.get(i)+", ";
				    	}
				    }
				    selectS += "calcset,sample_id";
				    
					stmt = conn.createStatement();
				    stmt.setQueryTimeout(timeout);				    
				    sql="SELECT  sample_nr, prep_nr, target_nr, type, calcset, prep_bl from "+calc_sample_t+" WHERE calcset='"+calcset+"' AND sample_nr>"+sampleMin;
				    ResultSet result = stmt.executeQuery(sql);
				    ResultSet result2;
				    
					Statement stmt2 = conn.createStatement();
				    stmt2.setQueryTimeout(timeout);		
//				    this.data = main.data;
				    data.removeData();

				    sql="SELECT cycles from "+calcset_t+" where calcset='"+calcset+"'";
				     ResultSet cycleResult = stmt2.executeQuery(sql);
				     cycleResult.next();
				     Integer cycle =cycleResult.getInt("cycles");
				     
				    for (int i=0;i<openDbR.size();i++) {
				    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("calcset")) {
				    		selectR+=openDbR.get(i)+", ";
				    	}
				    }
				    selectR += "sample_id";

				    while (result.next()) {
						sql="SELECT "+selectS+" FROM "+target_t+
    						" WHERE sample_nr='"+result.getString("sample_nr")+"' AND prep_nr='"+result.getString("prep_nr")+"'  AND target_nr='"+result.getString("target_nr")+"'";
//				    	log.debug(sql);
						result2 = stmt2.executeQuery(sql);
				    	result2.next();
						Sample samp = data.setSample(result2.getString("sample_id"));
				    	samp.setValue(result.getString("type"), "type");
				    	for (int i=0; i<openDbS.size(); i++){
					    	if(!openDbS.get(i).equals("sample_id")&&!openDbS.get(i).equals("calcset")&&!openDbS.get(i).equals("type")) {
					    		samp.setValue(result2.getString(openDbS.get(i)), openJS.get(i));
					    	}
						}
				    	
						if (cycle>0) {
							sql="SELECT "+selectR+" FROM "+cycle_t+
	    					" WHERE sample_nr='"+result.getString("sample_nr")+"' AND prep_nr='"+result.getString("prep_nr")+"'  AND target_nr='"+result.getString("target_nr")+
	    					"'  AND cycltrue is null ORDER BY "+cycle_t+".run DESC, "+cycle_t+".cycle ASC";
						} else {
							sql="SELECT "+selectR+" FROM "+run_t+
		    					" WHERE sample_nr='"+result.getString("sample_nr")+"' AND prep_nr='"+result.getString("prep_nr")+"'  AND target_nr='"+result.getString("target_nr")+"'";
//						    for (int i=0;i<openDbR.size();i++) {
//						    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("magazine")) {
//						    		selectR+=openDbR.get(i)+",";
//						    	}
//						    }
//							sql="SELECT "+selectR+"sample_id,magazine FROM "+run_t+" WHERE magazine='"+magazine+"'";
						}

				    	
//				    	log.debug(sql);
						result2 = stmt2.executeQuery(sql);
				    	while (result2.next()) {
							Run run = new Run( data.setSample(result2.getString("sample_id")) );
							for (int i=0; i<openDbR.size(); i++){
						    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("calcset")) {
						    		run.setValue(result2.getString(openDbR.get(i)), openJR.get(i));
						    	}
							}
							data.runListL.add(run);
				    	}		 
			    	}
					if (cycle>0) {
						//							log.debug("Reduce cycles: "+data.runListL+" divide by "+cycle_nr);
							data.runListL=Func.reduceCycle(data.runListL, cycle);
					}
			    	log.debug(data.sampleList.size()+" samples loaded.");
			    	log.debug(data.runListL.size()+" runs loaded.");
					data.initData(downloadCorr(conn,calcset,data));
			    	data.calcSet=calcset;
					main.tba.update("Calc-set loaded from DB",false);
			    }
			    else {
					main.tba.update("No data from DB",false);
			    	log.debug("Nothing loaded!");
			    }
			} catch (SQLException e) {
				log.error("Could not execute download");
			    log.info("SQLException: " + e.getMessage());
			    log.info("Query: " + sql);
			    logout();
			    String message = String.format( "<html>Could not execute download<br>Did logout!<br>Try again!<br>"+e.getMessage()+"</html>");
			    JOptionPane.showMessageDialog( null, message );
		    } finally {
			    if (stmt != null) {
			        try {
			            stmt.close();
			        } catch (SQLException e) {
						log.error("Could not execute querry ");
					    log.info("SQLException: " + e.getMessage());
					    log.info("SQLState: " + e.getSQLState());
					    log.info("VendorError: " + e.getErrorCode());
			        }
			        stmt = null;
//			        conn = null;
			    }	
		    }
		} else {
			log.info("Didn't login!");
		}
 	}

//	/**
//	 * @param CalcSet 
//	 * @return data
//	 */
//	public Calc openCalcOld(String CalcSet) {
//		main.tba.update("Get data from DB",true);
////		getSettings();
////	    Calc data = Setting.initCalcIso(Setting.isotope);
////		xmlSelect();
//	    String magazine = CalcSet;
//	    if (conn==null) {
//	    	log.debug("Start login");
//	    	conn = login();
//	    }
//		if (conn!=null) {
//		    Statement stmt = null;
//			try {
//			    if (magazine==null) {
//			    	magazine = selectMagazine();
//			    }
//			    if (magazine!=null) {
//				    String selectR = "";
//				    String selectS = "";
//				    for (int i=0;i<openDbR.size();i++) {
//				    	selectR+=openDbR.get(i)+",";
//				    }
//				    selectR += run_t+"."+"calcset";
//				    for (int i=0;i<openDbS.size();i++) {
//				    	selectS+=openDbS.get(i)+",";
//				    }
//				    selectS += "calcset";
//				    log.debug("Magazine opened: "+magazine);
//					ArrayList<Corr> corrL = downloadCorr(conn, magazine, data);			
//					stmt = conn.createStatement();
//				    stmt.setQueryTimeout(timeout);
//					sql="SELECT "+selectR+", "+selectS+" FROM "+run_t+", "+target_t+
//	    				" WHERE ("+run_t+".label="+target_t+".label) " +
//	    				"AND ("+run_t+".calcset="+target_t+".calcset) AND "+run_t+".calcset='"+magazine+"'";
////			    	log.debug(sql);
//					ResultSet result = stmt.executeQuery(sql);
//			    	while (result.next()) {
//						Run run = new Run( data.setSample(result.getString("label")) );
//						for (int i=0; i<openDbR.size(); i++){
//							run.setValue(result.getString(openDbR.get(i)), openJR.get(i));
//						}
//						for (int i=0; i<openDbS.size(); i++){
//							run.setValue(result.getString(openDbS.get(i)), openJS.get(i));
//						}
//						data.runListL.add(run);
//			    	}	    	
//			    	log.debug("Correction size: "+ corrL.size());
//			    	log.debug("Data size: "+data.runListL.size());
//					data.initData(corrL);
//					main.tba.update("Data loaded from DB",false);
//			    }
//			    else {
//					main.tba.update("No data from DB",false);
//			    	log.debug("Nothing loaded!");
//			    }
//			} catch (SQLException e) {
//				log.error("Could not execute download");
//			    log.info("SQLException: " + e.getMessage());
//			    log.info("SQLState: " + e.getSQLState());
//			    log.info("Query: " + sql);
//			    log.info("VendorError: " + e.getErrorCode());
//			    logout();
//			    String message = String.format( "<html>Could not execute download<br>Did logout!<br>Try again!</html>");
//			    JOptionPane.showMessageDialog( null, message );
//		    } finally {
//			    if (stmt != null) {
//			        try {
//			            stmt.close();
//			        } catch (SQLException e) {
//						log.error("Could not execute querry ");
//					    log.info("SQLException: " + e.getMessage());
//					    log.info("SQLState: " + e.getSQLState());
//					    log.info("VendorError: " + e.getErrorCode());
//			        }
//			        stmt = null;
////			        conn = null;
//			    }	
//		    }
//		} else {
//			log.info("Didn't login!");
//		}
//		return data;
// 	}

	/**
	 * @return calc-set
	 */
	public String selectCalcSet() {
		String calSM=null;
		Setting.no_data=true;
		data.magazine="";
		
		ArrayList<Mag> magList = new ArrayList<Mag>();
		String query="";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
			try {
		    		Statement stmt = conn.createStatement();
		    	    stmt.setQueryTimeout(timeout);
		    		query = "SELECT calcset, date_calc, magazine FROM "+calcset_t
		    			+" WHERE date_calc BETWEEN '"+df.format(date.getTime())+"' AND '"+df.format(date2.getTime())
		    			+"' ORDER BY date_calc DESC";
				ResultSet result = stmt.executeQuery (query);
//			    log.debug("Query: "+query);
				String tempMag;
				String tempDat;
				String tempSet;
				while (result.next()){
	    			tempMag=result.getString("magazine");
	    			tempSet=result.getString("calcset");
	    			tempDat=result.getString("date_calc");
		    		Mag mag = new Mag(tempDat, tempMag, tempSet);
		    		magList.add(mag);
				}
			}
			catch (SQLException e){
				String message = String.format( "<html>Could not get calc-set list!<br>(SQLException)</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.error("Could not get calc-set list! (SQLException)");
			    log.error("Query: "+query);
			    log.error(e);
			    this.logout();
			}
	
			String[] calcSetOnly = new String[magList.size()];
			String[] magInfo = new String[magList.size()];
			int last=0;
			for ( int i=0; i<magList.size(); i++)
			{
				calcSetOnly[i] = magList.get(i).calcSet;
				if (calcSetOnly[i].equals(Setting.getString("/bat/isotope/db/sql/last_mag"))) {
					last=i;
				}
				magInfo[i] = ((magList.get(i).calcSet+"                    ").substring(0,20)
							+ " | "
							+ (magList.get(i).magazine+"                    ").substring(0,20)
							+ " | "
							+ magList.get(i).date);			
			}
			try {
				calSM = (String) JOptionPane.showInputDialog(null,
			            "Select calc-set",
			            "DB connect",
			            JOptionPane.QUESTION_MESSAGE,
			            null, magInfo,
			            magInfo[last]).toString();
			
				calSM = calSM.split(" | ")[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				log.error("No calc-sets!");
			} catch (NullPointerException e) {
				log.error("No calc-set selected!");
			}
		}
   		Setting.getElement("/bat/isotope/db/sql/last_mag").setText(calSM);
   		log.debug("Selected calcset: "+calSM);
		return calSM;
	}


	/**
	 * @return magazine name
	 */
	public String selectMagazine() {
		String magazine=null;
		Setting.no_data=true;
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
			ArrayList<Magazine> magList = new ArrayList<Magazine>();
			String query="";
			Statement stmt = null;
			try {
	    		stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql/timeout"));
	    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    		
	    		query = "SELECT MIN(run) AS run1, MAX(run) AS run2, "+target_t+".magazine, MAX(timedat) AS timedat"
	    		+" from ("+run_t+" join "+target_t+")"
	    		+" WHERE (("+run_t+".sample_nr = "+target_t+".sample_nr)"
	    		+" AND ("+run_t+".`prep_nr` = "+target_t+".prep_nr)"
	    		+" AND ("+run_t+".`target_nr` = "+target_t+".`target_nr`))" 
	    		 + "AND "+target_t+".magazine IS NOT NULL"
	    		 +" AND timedat BETWEEN '"+df.format(date.getTime())+"' AND '"+df.format(date2.getTime())+"'"+calcset_null
	    		+" AND "+target_t+".sample_nr>"+sampleMin
	    		+" GROUP BY "+target_t+".magazine ORDER BY timedat DESC";
	    		ResultSet result = stmt.executeQuery (query);
	    		log.debug(query);
	    		while (result.next()) {
		    		Magazine mag = new Magazine(result.getString("run1"),result.getString("run2"),result.getString("magazine"),result.getDate("timedat"));
		    		magList.add(mag);
		    		log.debug(magList.size());
		    		log.debug(result.getString("run1")+"/"+result.getString("run2")+"/"+result.getString("magazine"));
	    		}
	    		if (magList.isEmpty()) {
	    			magazine=null;
	        		log.debug(query);
	    		} else {	    		
					String[] magOnly = new String[magList.size()];
					String[] magInfo = new String[magList.size()];
					int last=0;
					if (magList.size()>0) {
						for ( int i=0; i<magList.size(); i++) {
							magOnly[i] = magList.get(i).magazine;
							if (magOnly[i].equals(Setting.getString("/bat/isotope/db/sql/last_mag"))) {
								last=i;
							}
							df = new SimpleDateFormat("dd.MM.yyyy");
							magInfo[i] = (magList.get(i).magazine+"                    ").substring(0,20)
										+ (magList.get(i).runStart
										+ "-"
										+ magList.get(i).runEnd+"                     ").substring(0,22)
										+ df.format(magList.get(i).timeDat)+ "  ";			
						}
						magazine= (String) JOptionPane.showInputDialog(main,
					            "Select magazine",
					            "DB connect",
					            JOptionPane.QUESTION_MESSAGE,
					            null, magInfo,
					            magInfo[last]);
						try {
							magazine = magazine.split(" ")[0];
						} catch (NullPointerException e) {
							log.info("No Magazine selected!");
						}
				   		Setting.getElement("/bat/isotope/db/sql-import/last_mag").setText(magazine);
					} else {
						String message = String.format( "<html>No Magazine available!<br>(between "+date.getTime()+" and "+date2.getTime()+")</html>");
					    JOptionPane.showMessageDialog( null, message );
					    log.info("No Magazine available!");
					    logout();
					}
	    		}
			}
			catch (SQLException e){
				String message = String.format( "<html>Could not get Magazine List!<br>(SQLException)</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.error("Could not get Magazine List! (SQLException)");
			    log.error("Query: "+query);
			    log.error(e);
			    logout();
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
		}
		return magazine;	
	}

	
	/**
	 * @return latest magazine
	 */
	public String latestMag() {
		String magazine=null;
		Setting.no_data=true;
		conn=null;
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    } 
	    if (conn!=null) {
			String query="";
			Statement stmt = null;
			try {
	    		stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql/timeout"));
	    		
				query = "SELECT sample_nr, target_nr, prep_nr, timedat AS time FROM "+run_t+" WHERE sample_nr>"+sampleMin+" ORDER BY time DESC LIMIT 1";
	    		ResultSet result = stmt.executeQuery (query);
	    		result.next();
				query = "SELECT magazine FROM "+run_t+" WHERE sample_nr="+result.getString("sample_nr")+
				" AND prep_nr="+result.getString("prep_nr")+
				" AND target_nr="+result.getString("target_nr")+" LIMIT 1";
	    		result = stmt.executeQuery (query);
	    		log.debug("Obtained results");
	    		log.debug(query);
	    		result.next();
	    		magazine=result.getString("magazine");
			}
			catch (SQLException e){
				String message = String.format( "<html>Could not get Magazine!<br>(SQLException)</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.info("Could not get Magazine List! (SQLException or empty result set)");
			    log.info("Query: "+query);
			    log.info(e);
			    logout();
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
		}
		return magazine;		
	}

	
    /**
 	 * @param run
 	 * @return Run
 	 */
 	public Run updateRun(Run run) {
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
			Statement stmt = null;
			try {
	    		stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql/timeout"));

			    String selectR = "";
			    for (int i=0;i<openDbR.size();i++) {
			    	if(!openDbR.get(i).equals("sample_id")) {
			    		selectR+=openDbR.get(i)+",";
			    	}
			    }
			    selectR += "sample_id";
		//	    samples=samples.substring(0,samples.length()-2);
				sql="SELECT "+selectR+" FROM "+run_t+
				" WHERE run='"+run.run+"'";
		    	log.debug(sql);
				ResultSet result = stmt.executeQuery(sql);
		    	result.next();
				for (int i=0; i<openDbR.size(); i++){
			    	if(!openDbR.get(i).equals("sample_id")&&!openDbR.get(i).equals("magazine")) {
			    		run.setValue(result.getString(openDbR.get(i)), openJR.get(i));
			    	}
				}
				log.debug("Run updated.");
			} catch (SQLException e) {
				log.error("Could not execute download");
			    log.info("SQLException: " + e.getMessage());
			    log.info("Query: " + sql);
			    logout();
			    String message = String.format( "<html>Could not execute download<br>Did logout!<br>Try again!</html>");
			    JOptionPane.showMessageDialog( null, message );
		    } finally {
			    if (stmt != null) {
			        try {
			            stmt.close();
			        } catch (SQLException e) {
						log.error("Could not execute querry for run.");
					    log.info("SQLException: " + e.getMessage());
					    log.info("SQLState: " + e.getSQLState());
					    log.info("VendorError: " + e.getErrorCode());
			        }
			        stmt = null;
//			        conn = null;
			    }	
		    }
		} else {
			log.info("Didn't login!");
		}
		return run;
	}
    	
	
	private Connection login() {
		this.getSettings();
		this.getSelectSQL();
		Connection con=null;
        JPanel      connectionPanel;
        String[] ConnectOptionNames = { "Login", "Cancel" };
 		// Create the labels and text fields.
		JLabel     userNameLabel = new JLabel("User ID:   ", JLabel.RIGHT);
	 	JTextField userNameField = new JTextField("");
	 	userNameField.setText(user);
		JLabel     passwordLabel = new JLabel("Password:   ", JLabel.RIGHT);
		JTextField passwordField = new JPasswordField(pw);
		passwordField.setPreferredSize(new Dimension(100,10));
		connectionPanel = new JPanel(false);
		connectionPanel.setLayout(new BoxLayout(connectionPanel,BoxLayout.X_AXIS));
		JPanel namePanel = new JPanel(false);
		namePanel.setLayout(new GridLayout(0, 1));
		namePanel.add(userNameLabel);
		namePanel.add(passwordLabel);
		JPanel fieldPanel = new JPanel(false);
		fieldPanel.setLayout(new GridLayout(0, 1));
		fieldPanel.add(userNameField);
		fieldPanel.add(passwordField);
		connectionPanel.add(namePanel);
		connectionPanel.add(fieldPanel);
		if(JOptionPane.showOptionDialog(
				main, connectionPanel, 
				"DB connect",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE,
                null, ConnectOptionNames, 
                ConnectOptionNames[0]) 
                != 0) {
			pw = null;
		} else {
	        user = userNameField.getText();
	   		Setting.getElement("/bat/isotope/db/sql/user").setText(user);
	        pw = passwordField.getText();
	    	try {
				Class.forName(Setting.getString("/bat/isotope/db/sql/driver")).newInstance();
				try {
					DriverManager.setLoginTimeout(timeout);
					con = DriverManager.getConnection("jdbc:"+url+"?user="+user+"&password="+pw);
				} catch (SQLException e) {
					String message = String.format( "<html>Could not login to "+url+"<br>with user "+user+".</html>");
				    JOptionPane.showMessageDialog( null, message );
					log.debug("Could not connect: "+con);
				    log.debug("SQLException: " + e.getMessage());
				}
		        log.debug("Login to "+url+" with id '"+user+"'");
			} catch (Exception e) {
				String message = String.format( "<html>Could not DB driver!</html>");
			    JOptionPane.showMessageDialog( null, message );
				log.error("DB driver for import could not be loaded!");
				log.error(e);
			}
        }
		return con;
	}
	
	private class Mag
	{
		/**
		 * 
		 */
		private String calcSet;
		
		/**
		 * 
		 */
		public String magazine;
		
		/**
		 * 
		 */
		public String date;
		
		Mag( String date, String magazine, String calcSet)
		{
			this.date = date;
			this.magazine = magazine;
			this.calcSet = calcSet;
		}
	}

    private class Magazine
    {
    	/**
    	 * 
    	 */
    	private String runStart;
    	
    	/**
    	 * 
    	 */
    	private String runEnd;
    	
    	/**
    	 * 
    	 */
    	private String magazine;
    	
    	/**
    	 * 
    	 */
    	private Date timeDat;
    	
//    	Magazine( String run1, String run2, String magazine, String timedat)
//    	{
//    		runStart=run1;
//    		runEnd=run2;
//    		this.magazine=magazine;
//			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//			try {
//				timeDat=df.parse(timedat);
//			} catch (ParseException e) {
//				log.debug("Wrong date format: "+timedat);
//			}
//    	}
//    	
    	Magazine( String run1, String run2, String magazine, Date timedat)
    	{
    		runStart=run1;
    		runEnd=run2;
    		this.magazine=magazine;
    		this.timeDat=timedat;
    	}	
    }

	/**
	 * 
	 */
	public boolean downloadRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
	    return false;
	}

	
	public void addRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
	}

	public DbCycle getConn() {
		DbCycle dbCycle=null;
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
			try {
				dbCycle = new DbCycle(conn);
			} catch (JDOMException e) {
		   		log.error("Could not open db setup file!");
				String message = String.format( "<html>Could not open connection for update!</html>");
			    JOptionPane.showMessageDialog( null, message );
			} catch (IOException e) {
				String message = String.format( "<html>Could not open connection for update!</html>");
			    JOptionPane.showMessageDialog( null, message );
				log.error("Could not open db setup file!");
		   	} catch (HeadlessException e) {
				String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
			    JOptionPane.showMessageDialog( null, message );
				log.error("Headless Exception in MySQL connection");
				e.printStackTrace();
				conn=null;
			} 
		}
		return dbCycle;
	}

	public Boolean isConn() {
		return (conn!=null);
	}

	public Boolean runTrue(Run run, Boolean active) {
	    if (conn==null) {
	    	log.debug("Start login");
	    	conn = login();
	    }
		if (conn!=null) {
    		try {
	    		String query = "call "+Setting.getString("/bat/isotope/db/"+Setting.db+"/run_enable")+
	    		"("+active+",'"+run.run+"')";
				Statement stmt = conn.createStatement ();
			    stmt.setQueryTimeout(10);
				stmt.execute(query);
				ResultSet result = stmt.executeQuery("SELECT ratrue FROM "+run_t+" WHERE run='"+run.run+"'");
				result.next();
				run.active = result.getBoolean("ratrue");
				log.debug("Run "+run.run+" set "+result.getBoolean("ratrue"));
				return true;
	    	}
			catch (SQLException e) {
				String message = String.format( "Update failed!");
			    JOptionPane.showMessageDialog( null, message );
				log.debug(e);
				e.printStackTrace();
				return false;
			}			
		} else {
			return false;
		}
	}

}

//JTextComponent textComponent = new JTextField();
//AbstractDocument doc = (AbstractDocument)textComponent.getDocument();
//doc.setDocumentFilter(new FixedSizeFilter(10));
//
//class FixedSizeFilter extends DocumentFilter {
//    int maxSize;
//
//    // limit is the maximum number of characters allowed.
//    public FixedSizeFilter(int limit) {
//        maxSize = limit;
//    }
//
//    // This method is called when characters are inserted into the document
//    public void insertString(DocumentFilter.FilterBypass fb, int offset, String str,
//            AttributeSet attr) throws BadLocationException {
//        replace(fb, offset, 0, str, attr);
//    }
//
//    // This method is called when characters in the document are replace with other characters
//    public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
//            String str, AttributeSet attrs) throws BadLocationException {
//        int newLength = fb.getDocument().getLength()-length+str.length();
//        if (newLength <= maxSize) {
//            fb.replace(offset, length, str, attrs);
//        } else {
//            throw new BadLocationException("New characters exceeds max size of document", offset);
//        }
//    }
//}
