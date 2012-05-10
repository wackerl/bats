import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

class DbMySqlConnect implements DbConnect {
	final static Logger log = Logger.getLogger(DbMySqlConnect.class);
    Integer year;
    private Bats main;
    private Connection conn;
	
    /**
     * @param main
     */
    public DbMySqlConnect(Bats main) {
		Setting.db="sql-import";
		this.main = main;
	}
    
//    /**
//     * @return List of Runs
//     */
//     public void getData(){
//     	ArrayList<Run> dataList = null;
// 		try {
//			if (login()) {
//				magazine=getMagazine();
//				getDataMag(magazine);
//			} else {
//				String message = String.format( "<html>Didn't login for data import!</html>");
//				JOptionPane.showMessageDialog( null, message );
//				log.debug("Didn't login for data import");
//			}
//    	} catch (HeadlessException e) {
//			String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
//		    JOptionPane.showMessageDialog( null, message );
//			log.error("Headless Exception in MySQL connection");
//			e.printStackTrace();
//			conn=null;
//		} catch (SQLException e) {
//			String message = String.format( "<html>MySQL exception</html>");
//		    JOptionPane.showMessageDialog( null, message );
//			log.error("MySQL exception");
//			e.printStackTrace();
//			conn = null;
//		}
//     }
         
     /**
      * @return List of Runs
      */
      public boolean downloadMag(String magazine){
  		try {
			if (login()) {
				if (magazine!=null/* || magazine.equalsIgnoreCase("no magazine")*/) {
					main.tba.update("db read", true);
					DbMySqlRead dbRead = new DbMySqlRead(conn);
					try {
						dbRead.getRunList(magazine, main.data);
						return true;
					} catch (JDOMException e) {
			  			String message = String.format( "<html>Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml</html>");
			  		    JOptionPane.showMessageDialog( null, message );
						log.error("Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml");
						return false;
					} catch (IOException e) {
			  			String message = String.format( "<html>Could not open db setup file!</html>");
			  		    JOptionPane.showMessageDialog( null, message );
						log.error("Could not open db setup file!");
						return false;
					} catch (NullPointerException e) {
			  			String message = String.format( "<html>No return value from mysql!</html>");
			  		    JOptionPane.showMessageDialog( null, message );
						log.debug("No return value from MySQL!");
						return false;
					} 
				} else {
			  		String message = String.format( "<html>No magazine was set!</html>");
			  		JOptionPane.showMessageDialog( null, message );
					log.debug("No magazine was set.");
					return false;
				}
			}
			else {
				String message = String.format( "<html>Didn't login for data import!</html>");
				JOptionPane.showMessageDialog( null, message );
				log.debug("Didn't login for data import");
				return false;
			}
    	} catch (HeadlessException e) {
			String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("Headless Exception in MySQL connection");
			e.printStackTrace();
			conn=null;
			return false;
		} catch (SQLException e) {
			String message = String.format( "<html>MySQL exception</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("MySQL exception");
			e.printStackTrace();
			conn = null;
			return false;
		}
    }
          
  	public void addRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
	}

	public String latestMag() {
		String magazine = "";
		Setting.no_data = true;
		String query = "";
		try {
			 if (login()) {
		    	Statement stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql-import/timeout"));
				query = "SELECT magazine, MAX(timedat) AS time FROM "+Setting.getString("/bat/isotope/db/sql-import/run_t")+" GROUP BY magazine ORDER BY time DESC LIMIT 1";
			    ResultSet result = stmt.executeQuery (query);
				result = stmt.executeQuery (query);
//				log.debug("sql querry to get run was successful: "+query);
				if (result.next()) {
					magazine = result.getString("magazine");
				} else {
					String message = String.format( "<html>No magazine to load!</html>");
				    JOptionPane.showMessageDialog( null, message );
				    log.debug("No magazine to load!");
				}
			 } else {
				String message = String.format( "<html>Could not open connection to get data!</html>");
			    JOptionPane.showMessageDialog( null, message );
			    log.error("<html>Could not open connection to get data!</html>");
			    logout();
			}
		 } catch (SQLException e) {
			String message = String.format( "<html>Could not get data.<br>Maybe there was a connection timeout <br> or the password is wrong!</html>");
		    JOptionPane.showMessageDialog( null, message );
		    log.debug("Could get data!");
		    e.printStackTrace();
		    log.error("Query: "+query);
		    log.error(e);
		    logout();
		 }
		 return magazine;
	}

	public boolean downloadRuns() {
		String message = String.format( "<html>Not jet implemented!</html>");
	    JOptionPane.showMessageDialog( null, message );
	    return false;
	}

     /**
      * @return Run
      */
      public Run updateRun(Run run){
    	try {
	  		if (login()) {
	    		main.tba.update("db read", true);
				DbMySqlRead dbRead = new DbMySqlRead(conn);
				try {
					run = dbRead.getRun(run);
					log.debug("Runs read from database!");
				} catch (JDOMException e) {
		  			String message = String.format( "<html>Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml</html>");
		  		    JOptionPane.showMessageDialog( null, message );
					log.error("Could not open db setup file: "+Setting.batDir+"/"+Setting.isotope+"/db_io/import.xml");
				} catch (IOException e) {
		  			String message = String.format( "<html>Could not open db setup file!</html>");
		  		    JOptionPane.showMessageDialog( null, message );
					log.error("Could not open db setup file!");
				} catch (NullPointerException e) {
		  			String message = String.format( "<html>No return value from mysql!</html>");
		  		    JOptionPane.showMessageDialog( null, message );
					log.debug("No return value from oracle!");
				} 
	  			main.tba.update("", false);
	  		}
	  		else {
		  		String message = String.format( "<html>Didn't login for data import!</html>");
		  		JOptionPane.showMessageDialog( null, message );
	  			log.debug("Didn't login for data import");
	      	}
	  		return run;
    	} catch (HeadlessException e) {
			String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("Headless Exception in MySQL connection");
			e.printStackTrace();
			conn=null;
			return null;
		} catch (SQLException e) {
			String message = String.format( "<html>MySQL exception</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("MySQL exception");
			e.printStackTrace();
			conn = null;
			return null;
		}

     }
          
	/**
	 * @return DbCycle
	 * 
	 */
	public DbCycle getConn(){
		try {
			if (login()) {
				return new DbCycle(conn);
			} else {
		   		log.error("Could not login!");
				String message = String.format( "<html>Could not login to database!</html>");
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
	   	} catch (HeadlessException e) {
			String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("Headless Exception in MySQL connection");
			e.printStackTrace();
			conn=null;
			return null;
		} catch (SQLException e) {
			String message = String.format( "<html>MySQL exception</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("MySQL exception");
			e.printStackTrace();
			conn = null;
			return null;
		}
	}

	public String selectMagazine() {
		String magazine="";
		Setting.no_data = true;
		String query="";
		Statement stmt = null;
 		try {
			if (login()) {
				ArrayList<Magazine> magList = new ArrayList<Magazine>();
	    		stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    	    stmt.setQueryTimeout(Setting.getInt("/bat/isotope/db/sql-import/timeout"));
	    	    
	    		query = "SELECT MIN(run) AS run1, MAX(run) AS run2, magazine, timedat"//
	    			+" FROM "+Setting.getString("/bat/isotope/db/sql-import/run_t")+" GROUP BY magazine ORDER BY MAX(timedat) DESC";//";WHERE TO_CHAR(MAX(timedat), 'YYYY')="+year+"
	    		ResultSet result = stmt.executeQuery (query);
	    		log.debug("Obtained results");
	    		while (result.next()) {
		    		Magazine mag = new Magazine(result.getString("run1"),result.getString("run2"),result.getString("magazine"),result.getDate("timedat"));
		    		magList.add(mag);
	    		}
	    		
				String[] magInfo = new String[magList.size()];
				int last=0;
				int k=0;
				for ( int i=0; i<magList.size(); i++) {
					if (magList.get(i).magazine!=null) {
						try {
							if (magList.get(i).magazine.equals(Setting.getString("/bat/isotope/db/sql-import/last_mag"))) {
								last=k;
							}
						} catch(NullPointerException e)
						{
							log.error("Magazine does not exist!");
						}
						SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
						magInfo[k++] = (magList.get(i).magazine+"      ").substring(0,8)
									+ " | "
									+ (magList.get(i).runStart
									+ "-"
									+ magList.get(i).runEnd+"            ").substring(0,18)
									+ " | "
									+ df.format(magList.get(i).timeDat);			
						}
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
		   		Setting.getElement("/bat/isotope/db/sql-import/last_mag").setText(magazine);
			} else {
				String message = String.format( "<html>Didn't login for data import!</html>");
				JOptionPane.showMessageDialog( null, message );
				log.debug("Didn't login for data import");
			}
    	} catch (HeadlessException e) {
			String message = String.format( "<html>Headless Exception in MySQL connection.</html>");
		    JOptionPane.showMessageDialog( null, message );
			log.error("Headless Exception in MySQL connection");
			e.printStackTrace();
			conn=null;
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
		return magazine;
	}
        
     @SuppressWarnings("static-access")
	private Boolean login() throws HeadlessException, SQLException {
    	if (conn==null || !conn.isValid(5)) {
    		String pw;
	        JPanel      connectionPanel;
	        String[] ConnectOptionNames = { "Login", "Cancel" };
	 		 	// Create the labels and text fields.
			JLabel     userNameLabel = new JLabel("User ID:   ", JLabel.RIGHT);
		 	JTextField userNameField = new JTextField("");
		 	userNameField.setText(Setting.getString("/bat/isotope/db/sql-import/user"));
			JLabel     passwordLabel = new JLabel("Password:   ", JLabel.RIGHT);
			JTextField passwordField = new JPasswordField(Setting.getString("/bat/isotope/db/sql-import/pw"));
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
		        return false;
			}
			else {
				String url = Setting.getString("/bat/isotope/db/sql-import/url");				
		        String id = userNameField.getText();
		   		Setting.getElement("/bat/isotope/db/sql-import/user").setText(id);
		        pw = passwordField.getText();
		    	try {
					Class.forName(Setting.getString("/bat/isotope/db/sql-import/driver")).newInstance();
					try {
						DriverManager.setLoginTimeout(6);
						conn = DriverManager.getConnection("jdbc:"+url+"?user="+id+"&password="+pw);
					} catch (SQLException e) {
						String message = String.format( "<html>Could not login to "+url+"<br>A connection timeout may have occured<br>or maybe the password is wrong!<br>(User: "+id+")</html>");
					    JOptionPane.showMessageDialog( null, message );
						log.debug("Could not connect: "+conn);
					    log.debug("SQLException: " + e.getMessage());
					    conn=null;
						return false;
					}
			        log.debug("Login to "+url+" with id '"+id+"'");
			        return true;
				} catch (Exception e) {
					String message = String.format( "<html>Could not load DB driver!<br>Maybe no driver is set>!</html>");
				    JOptionPane.showMessageDialog( null, message );
					log.error("DB driver for import could not be loaded!");
					log.error(e);
					conn=null;
					return false;
				}
	        }
    	} else {
    		return true;
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
			log.info("Could not close connection!");
        } catch (NullPointerException e) { ; }
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
    	
    	Magazine( String run1, String run2, String magazine, Date timedat)
    	{
    		runStart=run1;
    		runEnd=run2;
    		this.magazine=magazine;
    		this.timeDat=timedat;
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
