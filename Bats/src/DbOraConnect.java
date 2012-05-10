import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

class DbOraConnect implements DbConnect {
	final static Logger log = Logger.getLogger(DbOraConnect.class);

	String driver;
	String url;
    Integer year;
    private Bats main;
    private String oraSyn;
    private Connection conn;
    private String pw;
 
    /**
     * @param main
     */
    public DbOraConnect(Bats main) {
		this.main = main;
		conn=null;
		driver = Setting.getString("/bat/isotope/db/ora/driver");
	    year = null;
		Setting.db="ora";
	}
    
    /**
     * 
     */
    public void logout() {
    	oraSyn=null;
    	pw=null;
    	year=null;
    	try {
			conn.close();
		} catch (SQLException e) {
			log.debug("Could not close any connection.");
		} catch (NullPointerException e) {
			log.debug("There was no open connection!");
		}
		log.debug("Closed connection");
    }
    
//	/**
//	 * 
//	 */
//	 public void getData(){
//		String magazine=null;
//		if (setSyn()) {
//			magazine = this.getMag();
//			log.debug("Magazine "+magazine+" selected for DB read.");		
//			getDataMag(magazine);
//		} else {
//			String message = String.format( "<html>Could not open connection to get magazine!</html>");
//		    JOptionPane.showMessageDialog( null, message );
//		    log.error("<html>Could not open connection to get magazine!</html>");
//		}
//     }
          
		/**
		 * @param magazine 
		 * 
		 */
		 public boolean downloadMag(String magazine) {
			if (setSyn()) {
				main.tba.update("read "+magazine, true);
				log.debug("Magazine "+magazine+" selected for DB read.");			
				if (magazine!=null) {
					if (!magazine.equalsIgnoreCase("no magazine")) {
						try {
							main.tba.update("db read", true);
							if (oraSyn.equals("TPU")){
								DbOraPuRead oraRead = new DbOraPuRead(conn);
								oraRead.getRunList(magazine, main.data);
								return true;
							}
							else {
								DbOraRead oraRead = new DbOraRead(conn);
								oraRead.getRunList(magazine, main.data);
								main.data.initData(new ArrayList<Corr>());
//								main.data.calcSet = main.data.magazine;
								log.debug(main.data.runListL.size()+" runs read from "+magazine+" in database!");
								return true;
							}
						} catch (JDOMException e) {
				  			String message = String.format( "<html>Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml</html>");
				  		    JOptionPane.showMessageDialog( null, message );
							log.error("Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml");
							return false;
						} catch (IOException e) {
				  			String message = String.format( "<html>Could not open db setup file!</html>");
				  		    JOptionPane.showMessageDialog( null, message );
							log.error("Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml");
							return false;
						} catch (NullPointerException e) {
				  			String message = String.format( "<html>No return value from oracle!</html>");
				  		    JOptionPane.showMessageDialog( null, message );
							log.debug("No return value from oracle!");
							return false;
						} 
					} else {
				  		String message = String.format( "<html>No Magazine to import!</html>");
				  		JOptionPane.showMessageDialog( null, message );
						log.debug("No Magazine to import!");
						return false;
					}

				} else {
			  		String message = String.format( "<html>No Magazine to import!</html>");
			  		JOptionPane.showMessageDialog( null, message );
					log.debug("No Magazine to import!");
					return false;
				}
			} else {
				String message = String.format( "<html>Could not open connection to get data!</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.error("<html>Could not open connection to get data!</html>");
				return false;
			}
	     }
	          
	/**
	 * @param run 
	 * @return updated run
	 */
	 public Run updateRun(Run run){
		if (setSyn()) {
			 try {
				main.tba.update("db read", true);
				DbOraRead oraRead = new DbOraRead(conn);
				run = oraRead.getRun(run);
				log.debug("Run read from database!");
			} catch (JDOMException e) {
	  			String message = String.format( "<html>Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml</html>");
	  		    JOptionPane.showMessageDialog( null, message );
				log.error("Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import_ora.xml");
			} catch (IOException e) {
	  			String message = String.format( "<html>Could not open db setup file!</html>");
	  		    JOptionPane.showMessageDialog( null, message );
				log.error("Could not open db setup file!");
			} catch (NullPointerException e) {
	  			String message = String.format( "<html>No return value from oracle!</html>");
	  		    JOptionPane.showMessageDialog( null, message );
				log.debug("No return value from oracle!");
			} 
		} else {
			String message = String.format( "<html>Could not open connection for update!</html>");
	    	JOptionPane.showMessageDialog( null, message );
		    log.error("<html>Could not open connection to get data for update!</html>");
			main.tba.update("", false);
		}
		return run;
     }
	          
	public void addRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
	}

	public String latestMag() {
		String magazine="";
		Setting.no_data=true;
		if (setSyn()) {
			String query="";
			try {
		    	Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    	stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
				query = "SELECT magazine, timedat FROM calcproto WHERE timedat=(SELECT MAX(timedat) FROM calcproto)";
			    ResultSet result = stmt.executeQuery (query);
			    result.next();
			    magazine =result.getString("magazine");
			 } catch (SQLException e) {
				String message = String.format( "<html>Could not connect to "+url+"<br>Maybe there was a connection timeout <br> or the password is wrong!<br>"+query+"</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.debug("Could not connect to "+url+". Maybe password is wrong!");
			    e.printStackTrace();
			 }
		} else {
			String message = String.format( "<html>Could not open connection to get data!</html>");
		    JOptionPane.showMessageDialog( null, message );
		    log.error("<html>Could not open connection to get data!</html>");
		}
		return magazine;
	}

	public boolean downloadRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
		return false;
	}

	/**
  	 * @return DbCycle
  	 * 
  	 */
  	public DbCycle getConn(){
  		log.debug("start get cycle");
		if (setSyn()) {
	  		try {
	  			if (oraSyn!=null) {
	  				return new DbCycle(conn);
	  			} else {
	  		   		log.error("No synonym is set!");
	  				String message = String.format( "<html>Unkown error.<br>No synonym is set!</html>");
	  			    JOptionPane.showMessageDialog( null, message );
	  				return null;
	  			}
	  		} catch (JDOMException e) {
	  	   		log.error("Could not open db setup file!");
	  			String message = String.format( "<html>Could not open connection for update!</html>");
	  		    JOptionPane.showMessageDialog( null, message );
	  			return null;
	  		} catch (IOException e) {
	  			String message = String.format( "<html>Could not open connection for update!</html>");
	  		    JOptionPane.showMessageDialog( null, message );
	  			log.error("Could not open db setup file!");
	  	   		return null;
	  	   	}
		} else {
			return null;
		}
  	}

	public String selectMagazine() {
		String magazine="";
		Setting.no_data=true;
		if (setSyn()) {
			main.tba.update("get magazines", true);
	//		if (year==null) {
	//			int today = Calendar.getInstance().get(Calendar.YEAR);
	//			try {
	//				year = Integer.parseInt(JOptionPane.showInputDialog("Select year: ", Setting.getInt("/bat/isotope/db/ora/last_year")));
	//			} catch(NumberFormatException e) {
	//				year = today;
	//			}
	//			if (year<1968||year>today) { 
	//				year=today;
	//			}
	//			log.debug("Year set to: "+year);
	//			Setting.getElement("/bat/isotope/db/ora/last_year").setText(Integer.toString(year));
	//		}
	//
			ArrayList<Magazine> magList = new ArrayList<Magazine>();
			String query="";
			try {
				log.debug("Start get magazine list for "+year);
	    		Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
	
	    	    query = "SELECT MIN(run) AS run1, MAX(run) AS run2, magazine, TO_CHAR(MAX(timedat), 'DD.MM.YYYY HH24:MI:SS') AS timedate"//, timedat"
				+" FROM calcproto GROUP BY magazine ORDER BY MAX(timedat) DESC";//";WHERE TO_CHAR(MAX(timedat), 'YYYY')="+year+"
	    		ResultSet result = stmt.executeQuery (query);
	    		log.debug("Obtained results");
	    		while (result.next()) {
		    		Magazine mag = new Magazine(result.getString("run1"),result.getString("run2"),result.getString("magazine"),result.getString("timedate"));
		    		magList.add(mag);
	    		}
	    	    
	    	    
				String[] magOnly = new String[magList.size()];
				String[] magInfo = new String[magList.size()];
				int last=0;
				for ( int i=0; i<magList.size(); i++)
				{
					magOnly[i] = magList.get(i).magazine;
					if (magOnly[i].equals(Setting.getString("/bat/isotope/db/ora/last_mag"))) {
						last=i;
					}
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
					magInfo[i] = (magList.get(i).magazine+"      ").substring(0,8)
								+ " | "
								+ (magList.get(i).runStart
								+ "-"
								+ magList.get(i).runEnd+"            ").substring(0,18)
								+ " | "
								+ df.format(magList.get(i).timeDat);			
				}
				magazine= (String) JOptionPane.showInputDialog(main,
			            "Select magazine",
			            "DB connect",
			            JOptionPane.QUESTION_MESSAGE,
			            null, magInfo,
			            magInfo[last]);
				try {
					magazine = magazine.split(" | ")[0];
				} catch (NullPointerException e) {
					log.error("No Magazine selected!");
				}
		   		Setting.getElement("/bat/isotope/db/ora/last_mag").setText(magazine);
			}
			catch (Exception e){
				String message = String.format( "<html>Could not get Magazine List!<br>Login again...<br>(Connection was probably interrupted)</html>");
			    JOptionPane.showMessageDialog( null, message );
			    e.printStackTrace();
			    log.debug(e.getMessage());
			    log.debug(query);
			    log.info("Could not get Magazine List! (SQLException)");
			    oraSyn=null;
			    pw=null;
			    conn=null;
			}
		} else {
			String message = String.format( "<html>Could not open connection to get magazine!</html>");
		    JOptionPane.showMessageDialog( null, message );
		    log.error("<html>Could not open connection to get magazine!</html>");
		}
		return magazine;
	}
        
    private boolean setSyn() {
    	try {
	//		try {
	//			Class.forName(driver).newInstance();
	//		} catch (Exception e) {
	//			log.error("DB driver could not be loaded!");
	//			log.error(e);
	//		}
 	    	if (conn==null || conn.isClosed()) {
	    		DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());  // would be faster...
				log.debug("Start create connection");
				if (pw==null) {
					pw = getIDandPassword();
				}
			    if (pw!=null) {
					main.tba.update("set syn", true);
		    		ArrayList<String> synonym = new ArrayList<String>();
			  		url = Setting.getString("/bat/isotope/db/ora/url");
//					DriverManager.setLoginTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			    	conn = DriverManager.getConnection (url, Setting.getString("/bat/isotope/db/ora/user"), pw);
			    	log.debug("Estabished connection - get synonyms");
				    Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
			    	if (oraSyn==null) {
					    ResultSet result = stmt.executeQuery ("select granted_role from user_role_privs order by granted_role");
						log.debug("results given");
					    while (result.next()) { 	
					    	synonym.add(result.getString(1));
					    }
				
						log.debug("results asked");
					    log.info("Login on "+url+" was ok!");
					    
						int k=0;
						String[] synArr = new String[synonym.size()-1];
						for (int i=0; i<synonym.size();i++){
							if (!synonym.get(i).equals("CONNECT")){
								synArr[k++]=synonym.get(i).replace("WRITER","");
							}
						}
				   		oraSyn = (String) JOptionPane.showInputDialog(main,
					            "Select user", 
					            "DB connect",
					            JOptionPane.QUESTION_MESSAGE,
					            null, synArr,
					            Setting.getString("/bat/isotope/db/ora/last_iso"));
				   		Setting.getElement("/bat/isotope/db/ora/last_iso").setText(oraSyn);
			    	}
					log.debug("create synonym: "+oraSyn);
				    stmt.execute("begin crea_calc_syn('"+oraSyn+"'); end;");
				    log.debug("synonym created.");
					main.tba.update("set synonym "+oraSyn, false);
				    return true;
				} else {
					main.tba.update("did nothing", false);
					return false;
				}
			} else {
			    Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/ora/timeout"));
				log.debug("create synonym: "+oraSyn);
			    stmt.execute("begin crea_calc_syn('"+oraSyn+"'); end;");
				log.debug("Connection already exists");
				return true;
			}
		} catch (SQLException e) {
			String message = String.format( "<html>Could not connect to "+url+"<br>Maybe there was a connection timeout <br> or the password is wrong!</html>");
		    JOptionPane.showMessageDialog( null, message );
		    log.debug(message);
			main.tba.update("SQL exception!", false);
		    e.printStackTrace();
		    return false;
		}
   }   
            
    @SuppressWarnings("static-access")
	private String getIDandPassword() 
    {
        JPanel      connectionPanel;
        String[] ConnectOptionNames = { "Login", "Cancel" };
 	
	 	// Create the labels and text fields.
		JLabel     userNameLabel = new JLabel("User ID:   ", JLabel.RIGHT);
	 	JTextField userNameField = new JTextField("");
	 	userNameField.setText(Setting.getString("/bat/isotope/db/ora/user"));
		JLabel     passwordLabel = new JLabel("Password:   ", JLabel.RIGHT);
		JTextField passwordField = new JPasswordField("");
		passwordField.setPreferredSize(new Dimension(100,10));
		passwordField.setText(Setting.getString("/bat/isotope/db/ora/pw"));
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
//		connectionPanel.setPreferredSize(new Dimension(400,100));
	
	    // Connect or quit
		if(JOptionPane.showOptionDialog(
				main, connectionPanel, 
				"DB connect",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE,
                null, ConnectOptionNames, 
                ConnectOptionNames[0]) 
                != 0
                ) {
	        return null;
		}
		else{
	   		Setting.getElement("/bat/isotope/db/ora/user").setText(userNameField.getText());
	        return passwordField.getText();
		}
    }
    
    private class Magazine
    {
    	/**
    	 * 
    	 */
    	public String runStart;
    	
    	/**
    	 * 
    	 */
    	public String runEnd;
    	
    	/**
    	 * 
    	 */
    	public String magazine;
    	
    	/**
    	 * 
    	 */
    	public Date timeDat;
    	
    	Magazine( String run1, String run2, String magazine, String timeDatOra)
    	{
    		runStart=run1;
    		runEnd=run2;
    		this.magazine=magazine;
    		SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy kk:mm:ss" );
    		try {
    			timeDat=df.parse(timeDatOra);
    		} catch (ParseException e) {
    			log.error("Could not parse timeDat in magazine: "+timeDatOra);
    		}
    	}	
    }

	public void openCalc() {
		// TODO Auto-generated method stub
		
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}

	public void saveAs() {
		// TODO Auto-generated method stub
		
	}
	
	public Boolean isConn() {
		return (conn!=null);
	}
	
	public Boolean runTrue(Run run, Boolean active) {
		return true;
	}

}
