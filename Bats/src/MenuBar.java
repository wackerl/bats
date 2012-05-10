import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;
import org.jdom.DataConversionException;

/**
 * @author lukas
 *
 */
public class MenuBar extends JMenuBar implements ActionListener{
	final static Logger log = Logger.getLogger(MenuBar.class);
	Action action;
	// Ask AWT which menu modifier we should be using.
	final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
//	// Check that we are on Mac OS X.  This is crucial to loading and using the OSXAdapter class.
	private static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

	JMenu menu, menu1, menu2, menu3, menu4, menu5, menu6;
	
	/**
	 * @param action
	 */
	public MenuBar(Action action) {
		
	    this.action = action;
	    JMenuItem menuItem;
	    
	    //Build the first menu.
	    menu = new JMenu("File");
	    menu.setMnemonic(KeyEvent.VK_F);
	    menu.getAccessibleContext().setAccessibleDescription("File");
	    this.add(menu);
	    
	    //Build the second menu.
	    Boolean active;
        try {
			active = Setting.getElement("/bat/isotope/db").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {       
		    menu1 = new JMenu("DB");
		    menu1.setMnemonic(KeyEvent.VK_F);
		    menu1.getAccessibleContext().setAccessibleDescription("File");
		    this.add(menu1);
		}
	    
	    //Build the second menu.
	    menu2 = new JMenu("Corrections");
//	    menu2.setMnemonic(KeyEvent.VK_C);
	    menu2.getAccessibleContext().setAccessibleDescription("GraphSeries");
	    this.add(menu2);
			
	    //Build the 4th menu.
	    menu3 = new JMenu("Calculations");
	    menu3.setMnemonic(KeyEvent.VK_T);
	    menu3.getAccessibleContext().setAccessibleDescription("Functions for the calculations of the data");
	    this.add(menu3);
	    
	    menu_update();

	    //Build the 4th menu.
	    menu4 = new JMenu("Table settings");
	    menu4.setMnemonic(KeyEvent.VK_T);
	    menu4.getAccessibleContext().setAccessibleDescription("GraphSeries");
	    this.add(menu4);

	    menuItem = new JMenuItem("Passes and mean",KeyEvent.VK_1);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Show format settings of passes and mean");
	    menuItem.setName("Setting of passes and mean");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);
	
	    menuItem = new JMenuItem("Passes",KeyEvent.VK_2);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Show format settings of passes");
	    menuItem.setName("Setting of passes");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);
	    
	    menuItem = new JMenuItem("Blanks",KeyEvent.VK_3);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Show format settings of blanks");
	    menuItem.setName("Setting of blanks");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);
	    
	    menuItem = new JMenuItem("Standards",KeyEvent.VK_4);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Show format settings of standard");
	    menuItem.setName("Setting of standards");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);
	    
	    if (Setting.isotope.contains("C14")) {
		    menuItem = new JMenuItem("Standards (δ¹³C)",KeyEvent.VK_5);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Show format δ¹³C-settings of standard");
		    menuItem.setName("Setting of standards (dC13)");
		    menuItem.addActionListener(this);
		    menu4.add(menuItem);
	    }
	    if (Setting.isotope.contains("P")) {
		    menuItem = new JMenuItem("Standards 240/242 panel",KeyEvent.VK_5);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Show format 240/242-settings of standard");
		    menuItem.setName("Setting of standards (240/242)");
		    menuItem.addActionListener(this);
		    menu4.add(menuItem);
		    
		    menuItem = new JMenuItem("Standards 239/240 panel",KeyEvent.VK_8);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Show format 239/240-settings of standard");
		    menuItem.setName("Setting of standards (239/240)");
		    menuItem.addActionListener(this);
		    menu4.add(menuItem);
	    }
	    
	    menuItem = new JMenuItem("Samples",KeyEvent.VK_6);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Show format settings of samples");
	    menuItem.setName("Setting of samples");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);
	    
		menu4.addSeparator();
		
	    menuItem = new JMenuItem("output",KeyEvent.VK_7);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("");
	    menuItem.setName("XHTML settings");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);

		menu4.addSeparator();
	
	    menuItem = new JMenuItem("Cycle table",KeyEvent.VK_8);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("");
	    menuItem.setName("Cycle settings");
	    menuItem.addActionListener(this);
	    menu4.add(menuItem);

	    // building the 5th menue
	    menu5 = new JMenu("Window");
	    menu5.setMnemonic(KeyEvent.VK_H);
	    menu5.getAccessibleContext().setAccessibleDescription("Help");
	    this.add(menu5);
	
	    menuItem = new JMenuItem("Comment",KeyEvent.VK_C);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Comment window");
	    menuItem.setName("Comment");
	    menuItem.addActionListener(this);
	    menu5.add(menuItem);

	    if (MAC_OS_X) {
		    menuItem = new JMenuItem("Log",KeyEvent.VK_L);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Log file");
		    menuItem.setName("Log");
		    menuItem.addActionListener(this);
		    menu5.add(menuItem);
		}

	    menu5.addSeparator();
	    
	    menuItem = new JMenuItem("Increase font size in output",KeyEvent.VK_C);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Increase font size in output");
	    menuItem.setName("Increase");
	    menuItem.addActionListener(this);
	    menu5.add(menuItem);

	    menuItem = new JMenuItem("Decrease font size in output",KeyEvent.VK_C);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("DEcrease font size in output");
	    menuItem.setName("Decrease");
	    menuItem.addActionListener(this);
	    menu5.add(menuItem);

	    // building the 6th menue
	    menu6 = new JMenu("Help");
	    menu6.setMnemonic(KeyEvent.VK_H);
	    menu6.getAccessibleContext().setAccessibleDescription("Help");
	    this.add(menu6);
	
	    menuItem = new JMenuItem("Bats Help",KeyEvent.VK_H);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Help");
	    menuItem.setName("Help");
	    menuItem.addActionListener(this);
	    menu6.add(menuItem);

	    menuItem = new JMenuItem("About");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("About");
	    menuItem.setName("About");
	    menuItem.addActionListener(this);
	    menu6.add(menuItem);

	    menuItem = new JMenuItem("Reset");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Reset (PW protected)");
	    menuItem.setName("reset");
	    menuItem.addActionListener(this);
	    menu6.add(menuItem);

	}

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());       
        action.exec(source.getName());
    }
    
    /**
     * 
     */
    public void menu_update() {
	    JMenuItem menuItem;
	
	    menu.removeAll();
	    
	    //group of JMenuItems
	    menuItem = new JMenuItem("Open data file...");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Opens data file");
	    menuItem.setName("Open data file...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);
	    
//	    if (System.getProperty("user.name").equalsIgnoreCase("lukas")||System.getProperty("user.name").equalsIgnoreCase("wacker")) {
		    //group of JMenuItems
		    menuItem = new JMenuItem("Open hv files...");
	//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Opens data files from directory");
		    menuItem.setName("Open HV dir...");
		    menuItem.addActionListener(this);
		    menu.add(menuItem);
		    
		    //group of JMenuItems
		    menuItem = new JMenuItem("Open nec files...");
	//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Opens data files from directory");
		    menuItem.setName("Open NEC dir...");
		    menuItem.addActionListener(this);
		    menu.add(menuItem);
	   
	    
	    menuItem = new JMenuItem("Save data file...");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save data file");
	    menuItem.setName("Save data file...");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu.add(menuItem);
	    
		menu.addSeparator();

//		Not supported anymore by flying saucer
//		menuItem = new JMenuItem("Print");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,  MENU_MASK));
//	    menuItem.getAccessibleContext().setAccessibleDescription("Print pdf");
//	    menuItem.setName("Print");
//	    menuItem.addActionListener(this);
//	    menuItem.setEnabled(true);
//	    menu.add(menuItem);
	    
		menuItem = new JMenuItem("Print with pdf preview");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Print pdf with preview");
	    menuItem.setName("Print pdf");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu.add(menuItem);
	    
		menuItem = new JMenuItem("Save pdf");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save xhtml output to a pdf file");
	    menuItem.setName("Save pdf");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu.add(menuItem);
	    
		menuItem = new JMenuItem("Save excel (xml)");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save xhtml output to a xml-excel file");
	    menuItem.setName("Save excel");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu.add(menuItem);
	    
		menuItem = new JMenuItem("Output to browser");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Open default output file in browser");
	    menuItem.setName("Browser");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu.add(menuItem);
	    
		menu.addSeparator();
		
		menuItem = new JMenuItem("Preferences");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, MENU_MASK));
	    menuItem.setName("Preferences");
		menuItem.addActionListener(this);
	    menu.add(menuItem);
	    

		menuItem = new JMenuItem("Save preferences");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save settings");
	    menuItem.setName("Save preferences");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

//		if (!MAC_OS_X) {
			menu.addSeparator();
			menuItem = new JMenuItem("Quit");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
		    menuItem.setName("Quit");
			menuItem.addActionListener(this);
		    menu.add(menuItem);
//		}
		    
		menu.addSeparator();
		
		ArrayList<String> lastFiles = Setting.getLastFile();
		for (int i=0; i<lastFiles.size(); i++) {
			menuItem = new JMenuItem(i+" "+lastFiles.get(i));
	//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, MENU_MASK));
		    menuItem.setName(i+" "+lastFiles.get(i));
			menuItem.addActionListener(this);
		    menu.add(menuItem);
		}

	    Boolean active;
        try {
			active = Setting.getElement("/bat/isotope/db").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {       

		    menu1.removeAll();

			menuItem = new JMenuItem("New ...");
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Insert into database");
		    menuItem.setName("New db");
		    menuItem.addActionListener(this);
		    menu1.add(menuItem);
	
			menuItem = new JMenuItem("New latest");
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Load latest data from database");
		    menuItem.setName("Latest db");
		    menuItem.addActionListener(this);
		    menu1.add(menuItem);
		    
			menuItem = new JMenuItem("Reload data");
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Reload data from database");
		    menuItem.setName("Reload db");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu1.add(menuItem);
		    
	        try {
				active = Setting.getElement("/bat/isotope/db/sql").getAttribute("active").getBooleanValue();
			} catch (DataConversionException e) { active = false; log.debug(""); }
			
			if (active == true) {       
				menuItem = new JMenuItem("Open ...");
			    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,  MENU_MASK));
			    menuItem.getAccessibleContext().setAccessibleDescription("Insert into database");
			    menuItem.setName("Open db");
			    menuItem.addActionListener(this);
			    menu1.add(menuItem);
		
				menuItem = new JMenuItem("Save");
			    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,  MENU_MASK));
			    menuItem.getAccessibleContext().setAccessibleDescription("Insert into database");
			    menuItem.setName("Save db");
			    menuItem.addActionListener(this);
			    menuItem.setEnabled(!Setting.no_data);
			    menu1.add(menuItem);
		
				menuItem = new JMenuItem("Save as ...");
			    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,  MENU_MASK));
			    menuItem.getAccessibleContext().setAccessibleDescription("Insert into database");
			    menuItem.setName("Save as db");
			    menuItem.addActionListener(this);
			    menuItem.setEnabled(!Setting.no_data);
			    menu1.add(menuItem);	
			    
				menu1.addSeparator();

				ArrayList<String> lastMag = Setting.getLastMag();
				for (int i=0; i<lastMag.size(); i++) {
					menuItem = new JMenuItem(i+".M "+lastMag.get(i));
			//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, MENU_MASK));
				    menuItem.setName(i+"M "+lastMag.get(i));
					menuItem.addActionListener(this);
				    menu1.add(menuItem);
				}

	    }
			menu1.addSeparator();
			
			menuItem = new JMenuItem("Logout from db");
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Logout from DB");
		    menuItem.setName("Logout DB");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(action.main.db.isConn());
		    menu1.add(menuItem);
		}
	
//			menuItem = new JMenuItem("New runs (import)");
////		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,  MENU_MASK));
//		    menuItem.getAccessibleContext().setAccessibleDescription("Load runs from database");
//		    menuItem.setName("Run import");
//		    menuItem.addActionListener(this);
//		    menu1.add(menuItem);
//		    
//			menuItem = new JMenuItem("Add runs (import)");
////		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,  MENU_MASK));
//		    menuItem.getAccessibleContext().setAccessibleDescription("Add runs from database");
//		    menuItem.setName("Add runs");
//		    menuItem.addActionListener(this);
//		    menu1.add(menuItem);

		    menu2.removeAll();

		    menuItem = new JMenuItem("Table of standards",KeyEvent.VK_T);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Standards for normalisation");
		    menuItem.setName("Table of standards");
		    menuItem.addActionListener(this);
		    menu2.add(menuItem);
		    
			menu2.addSeparator();
		
		    menuItem = new JMenuItem("Split correction",KeyEvent.VK_Y);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Split correction");
		    menuItem.setName("Split");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu2.add(menuItem);
		
		    menuItem = new JMenuItem("Remove correction splits",KeyEvent.VK_X);
//		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Remove any correction splits");
		    menuItem.setName("Remove split");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu2.add(menuItem);
		
			menu2.addSeparator();
		    //group of JMenuItems
			if (Setting.isotope.contains("C14")) {
			    menuItem = new JMenuItem(Setting.getString("/bat/isotope/graph/ba/title"),KeyEvent.VK_D);
			    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,  MENU_MASK));
			    menuItem.getAccessibleContext().setAccessibleDescription("Plot ¹³C/¹²C to ¹²C");
			    menuItem.setName("ba correction");
			    menuItem.addActionListener(this);
			    menuItem.setEnabled(!Setting.no_data);
			    menu2.add(menuItem);
			}
		
		    menuItem = new JMenuItem(Setting.getString("/bat/isotope/graph/ra/title"),KeyEvent.VK_E);
//		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Graph for current correction");
		    menuItem.setName("ra correction");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu2.add(menuItem);
		
		    menuItem = new JMenuItem(Setting.getString("/bat/isotope/graph/bg/title"),KeyEvent.VK_I);
		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Graph for isobar correction");
		    menuItem.setName("isobar correction");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu2.add(menuItem);
		
			if (Setting.isotope.contains("C14")) {
			    menuItem = new JMenuItem("Constant contamination",KeyEvent.VK_C);
//			    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,  MENU_MASK));
			    menuItem.getAccessibleContext().setAccessibleDescription("Constant contamination and constant background correction");
			    menuItem.setName("const");
			    menuItem.addActionListener(this);
			    menuItem.setEnabled(!Setting.no_data);
			    menu2.add(menuItem);
			}
		
		    menuItem = new JMenuItem(Setting.getString("/bat/isotope/graph/time/title"),KeyEvent.VK_G);
//		    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,  MENU_MASK));
		    menuItem.getAccessibleContext().setAccessibleDescription("Graph for time correction");
		    menuItem.setName("time correction");
		    menuItem.addActionListener(this);
		    menuItem.setEnabled(!Setting.no_data);
		    menu2.add(menuItem);		
		

	    menu3.removeAll();

	    menuItem = new JMenuItem("Recalculate",KeyEvent.VK_R);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Force recalculate samples");
	    menuItem.setName("Force recalculate");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu3.add(menuItem);

		menu3.addSeparator();

	    menuItem = new JMenuItem("Deactivate runs",KeyEvent.VK_D);
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Deactivate runs from run1 to run2");
	    menuItem.setName("Deactivate runs");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu3.add(menuItem);

	    menuItem = new JMenuItem("Activate runs",KeyEvent.VK_A);
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Activate runs from run1 to run2");
	    menuItem.setName("Activate runs");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu3.add(menuItem);

		menu3.addSeparator();

	    menuItem = new JMenuItem("Re-initialise");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Recalculate samples");
	    menuItem.setName("Reinitialise");
	    menuItem.addActionListener(this);
	    menuItem.setEnabled(!Setting.no_data);
	    menu3.add(menuItem);
	
		menu3.addSeparator();

	    menuItem = new JMenuItem("Change Isotope");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Change calculations and settings to another isotope");
	    menuItem.setName("Change isotope");
	    menuItem.addActionListener(this);
	    menu3.add(menuItem);
	    
	    if (Setting.isotope.equalsIgnoreCase("c14")) {

				menu3.addSeparator();
			
		 	    menuItem = new JMenuItem("Calibrated Ranges");
			    menuItem.getAccessibleContext().setAccessibleDescription("Calculate calibrated ranges for all samples");
			    menuItem.setName("CalibRanges");
			    menuItem.addActionListener(this);
			    menuItem.setEnabled(!Setting.no_data);
			    menu3.add(menuItem);
			
				menu3.addSeparator();
		 	    menuItem = new JMenuItem("Manual calibration...");
			    menuItem.getAccessibleContext().setAccessibleDescription("Open input for calibration");
			    menuItem.setName("calibMan");
			    menuItem.addActionListener(this);
			    menu3.add(menuItem);
		
			    if (System.getProperty("user.name").equalsIgnoreCase("lukas")||System.getProperty("user.name").equalsIgnoreCase("wacker")) {				
				 	    menuItem = new JMenuItem("Calibration statistics");
					    menuItem.getAccessibleContext().setAccessibleDescription("");
					    menuItem.setName("calibStat");
					    menuItem.addActionListener(this);
					    menu3.add(menuItem);		
			    }
	    }
	    
    }
}
