import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;


/**
 * @author lukas
 *
 */
public class Action 
{
	final static Logger log = Logger.getLogger(Action.class);
	Bats main;
	/**
	 * @param main
	 */
	public Action(Bats main) {
		this.main = main;
	}
	
	/**
	 * @param text
	 */
	public void exec(String text){
        if (text == "calibMan") {
    		log.debug("execute: "+text);
    		main.calib.manInput();
        }  		
        
        else if (text == "CalibRanges") {
    		log.debug("execute: "+text);
    		this.calibRanges();
        }  		 
 
        else if (text == "calibStat") {
    		log.debug("execute: "+text);
    		main.calib.statistic(32.0,1,2);
    		main.calib.statistic(40.0,1,2);
    		main.calib.statistic(48.0,1,2);
    		main.calib.statistic(64.0,1,2);
        }   
 
        else if (text == "const") {
        	main.data.autocalcStdBl();
        	Double[] bg = this.bgFrame(main.data.corrList.get(0));
        	for (int i=0; i<main.data.corrList.size(); i++) {
        		main.data.corrList.get(i).constBG=bg[0];
        		main.data.corrList.get(i).constBGErr=bg[1];
        		main.data.corrList.get(i).constBlWeight=bg[2];
        		main.data.corrList.get(i).constBlRatio=bg[3];
        		main.data.corrList.get(i).constBlErr=bg[4];
        	}
         }
	       
        else if (text == "Comment") {
    		log.debug("execute: "+text);
    		main.data.comment.show();
        }   
        
        else if (text == "Save pdf") {
        	calibRanges();
        	log.debug("execute: "+text);
	        main.xFile = Setting.magDir+"/"+main.data.calcSet+".xhtml";
			main.xPanel.update(main.data, main.xFile);	
			Pdf.save(main.xFile, Setting.magDir+"/"+main.data.calcSet+".pdf");
        }   
        
        else if (text == "Save excel") {
        	calibRanges();
        	log.debug("execute: "+text);
			main.tba.update("save", true);
			ArrayList<DataSet> dat;
		    if (Setting.getBoolean("/bat/general/file/output/sample-only")) {
				dat = main.data.get("sample");
		    } else {
				dat = main.data.get("runSample");
		    }
			IOFile.saveExcel(Setting.magDir+"/"+main.data.calcSet+".xls", dat, main.xPanel.format, main.data);
			log.debug("Saved file: "+Setting.magDir+"/"+main.data.calcSet+".xls");
			main.tba.update("saved .xls", false);
        }

		
        else if (text == "Print pdf") {
        	calibRanges();
        	log.debug("execute: "+text);
	        main.xFile = Setting.magDir+"/"+main.data.calcSet+".xhtml";
			main.xPanel.update(main.data, main.xFile);	
			Pdf.preview(main.xFile, Setting.magDir+"/"+main.data.calcSet+".pdf");
        }   
        
        else if (text == "Logout DB") {
    		log.debug("execute: "+text);
    		main.db.logout();
        }   
        
		else if (text == "Save db") {
			calibRanges();
			log.debug("execute: "+text);
    		main.db.save();
        }   
        
        else if (text == "Save as db") {
			calibRanges();
    		log.debug("execute: "+text);
    		main.db.saveAs();
        }   
 
        else if (text == "New db") {
    		log.debug("execute: "+text);
    		Setting.autocalc=true;
    		main.data.removeData();
			if (main.db.downloadMag(main.db.selectMagazine())) {
				main.data.initData(new ArrayList<Corr>());
				log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
	    		main.dataRecalc2();
    		} else {
				main.tba.update("no data loaded", false);
    		}
        }   
        
        else if (text == "Open db") {
    		log.debug("execute: "+text);
    		Setting.autocalc=false;
    		main.data.removeData();
			main.db.openCalc();
			log.debug(main.data.magazine);
    		main.dataRecalc2();
        }  
        
        else if (text == "Add db") {
    		log.debug("execute: "+text);
    		Setting.autocalc=true;
			if (main.db.downloadMag(main.db.selectMagazine())) {
				main.data.initData(new ArrayList<Corr>());
				log.debug("Runs added from database!");
				main.dataRecalc2();
    		} else {
				main.tba.update("no data loaded", false);
    		}
        }  
        
        else if (text == "Latest db") {
    		log.debug("execute: "+text);
    		Setting.autocalc=true;
    		main.data.removeData();
			if (main.db.downloadMag(main.db.latestMag())) {
				main.data.initData(new ArrayList<Corr>());
				log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
	    		main.dataRecalc2();
			}
        }   
        
        else if (text == "Reload db") {
    		log.debug("execute: "+text);
//        		main.tbarStdBl.autocalc(true);
    		String magazine = main.data.magazine;
    		main.data.removeData();
    		if (main.db.downloadMag(magazine)) {
				main.data.initData(new ArrayList<Corr>());
				log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
    		} else {
				main.tba.update("no data loaded", false);
    		}
        }       
        
        else if (text == "Run import") {
        	main.db.downloadRuns();
			main.data.initData(new ArrayList<Corr>());
			log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
        }
        else if (text == "Add runs") {
        	main.db.addRuns();
        }

        else if (text == "Open data file...") {
    		log.debug("execute: "+text);
    		this.open();
        }   
        
        else if (text == "Open HV dir...") {
    		log.debug("execute: "+text);
    		Setting.autocalc=true;
    		main.data.removeData();
			if (this.openHV()) {
				main.data.initData(new ArrayList<Corr>());
				log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
	    		main.dataRecalc2();
    		} else {
				main.tba.update("no data loaded", false);
    		}
        }   
        
        else if (text == "Open NEC dir...") {
    		log.debug("execute: "+text);
    		Setting.autocalc=true;
    		main.data.removeData();
			if (this.openNEC()) {
				main.data.initData(new ArrayList<Corr>());
				log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
	    		main.dataRecalc2();
    		} else {
				main.tba.update("no data loaded", false);
    		}
        }   
        
        else if (text == "Save data file..."){
			calibRanges();
    		log.debug("execute: "+text);
    		this.saveAs();
        }  
        
        else if (text == "Table of standards") {
    		log.debug("execute: "+text);
        	main.data.stdNom.showStd();
        }   
        
        else if (text == "ba correction") {
    		log.debug("execute: "+text);
        	main.data.autocalcStdBl();
    		JComponent[] graph = new JComponent[main.data.corrList.size()];
        	for (int i=0; i<main.data.corrList.size(); i++) {
        		graph[i] = new GraphCurrent(main, "ba", i);
        	}
    		new TabbedFrame("Current correction", graph);
         }   
        
        else if (text == "isobar correction") {
        	main.data.autocalcStdBl();
        	if (Setting.isotope.contains("C14")){
				log.debug("execute: "+text);
		   		JComponent[] graph = new JComponent[main.data.corrList.size()];
	        	for (int i=0; i<main.data.corrList.size(); i++) {
	        		graph[i] = new GraphIso(main, "bg", i);
	        	}
	    		new TabbedFrame("Isobar correction", graph);
           	} else if (Setting.isotope.equalsIgnoreCase("Be10Tandem")){
				log.debug("execute: "+text);
				GraphIso2 graphIso2= new GraphIso2(main, "bg");
				//CV
        	} else if (Setting.isotope.equalsIgnoreCase("Cl36")){
//        		log.debug("execute: "+text);
//		   		JComponent[] graph = new JComponent[main.data.corrList.size()];
//	        	for (int i=0; i<main.data.corrList.size(); i++) {
//	        		graph[i] = new GraphIso(main, "bg", i);
//	        	}
        		log.debug("execute: "+text);
		   		GraphIso2 graphIso2= new GraphIso2(main, "bg");
//CV
           	} else if (Setting.isotope.contains("P")){
				log.debug("execute: "+text);
				GraphIso2 graphIso2= new GraphIso2(main, "bg");
       	} else if (Setting.isotope.equalsIgnoreCase("Be10")){
        		log.debug("execute: "+text);
		   		GraphIso2 graphIso2= new GraphIso2(main, "bg");
        	} else {
	    		log.debug("execute: "+text);
		   		@SuppressWarnings("unused") 
		   		GraphIso2 graphIso2= new GraphIso2(main, "bg");
        	}
        }   
        
        else if (text == "ra correction") {
    		log.debug("execute: "+text);
        	main.data.autocalcStdBl();
    		JComponent[] graph = new JComponent[main.data.corrList.size()];
        	for (int i=0; i<main.data.corrList.size(); i++) {
        		graph[i] = new GraphCurrent(main, "ra", i);
        	}
    		new TabbedFrame("Current correction", graph);
        }   
        
        else if (text == "time correction") {
    		log.debug("execute: "+text);
        	main.data.autocalcStdBl();
    		JComponent[] graph = new JComponent[main.data.corrList.size()];
        	for (int i=0; i<main.data.corrList.size(); i++) {
        		graph[i] = new GraphTime(main, "time", i);
        	}
    		new TabbedFrame("Time correction", graph);
        }   
        
        else if (text == "Change isotope") {
        	log.debug("execute: "+text);
        	Setting.save();
        	log.debug("execute: "+text);
    		main.startup();
        } 
        else if (text == "reset") {
    		log.debug("execute: "+text);
    		if (((String)JOptionPane.showInputDialog("For admins only!")).equals("vip")) {
    			Setting.reset();
    		}
        } 
        else if (text == "Remove split") {
    		log.debug("execute: "+text);
    		main.data.removeSplit();
    		main.dataRecalc2();
        } 
        else if (text == "Split") {
    		log.debug("execute: "+text);
	 		String run = (String) JOptionPane.showInputDialog(main,
		            "Correction",
		            "Split after run",
		            JOptionPane.QUESTION_MESSAGE,
		            null, main.data.getRunObjects("run"),
		            null);
	 		log.info("New correction after run: "+run);
	 		if (run!=null) {
	 			main.data.splitCorrection(run);
	        	main.data.autocalcStdBl();
	 		}
	 		main.dataRecalc2();
        }
        else if (text == "Deactivate runs") {
    		log.debug("execute: "+text);
    		String run2=null;
	 		String run1 = (String) JOptionPane.showInputDialog(main,
		            "Deactivate",
		            "First run",
		            JOptionPane.QUESTION_MESSAGE,
		            null, main.data.getRuns(null, null),
		            null);
	 		if (run1!=null) {
		 		run2 = (String) JOptionPane.showInputDialog(main,
			            "Deactivate",
			            "Last run",
			            JOptionPane.QUESTION_MESSAGE,
			            null, main.data.getRuns(run1, null),
			            null);
		 		log.info("Deactivat from run: "+run1 + " to "+run2);
		 		if (run2!=null) {
		 			for (int k=0; k<main.data.runListR.size(); k++) {
		 				if (main.data.runListR.get(k).run.compareToIgnoreCase(run1)>=0
		 						&& main.data.runListR.get(k).run.compareToIgnoreCase(run2)<=0) {
        					if (main.db.runTrue(main.data.runListR.get(k), false)) {
        						main.data.runListR.get(k).active=false;			
        					}
		 				}
		 			}	
			 		main.dataRecalc();
		 		}
	 		}
        }
        else if (text == "Activate runs") {
    		log.debug("execute: "+text);
    		String run2=null;
	 		String run1 = (String) JOptionPane.showInputDialog(main,
		            "Activate",
		            "First run",
		            JOptionPane.QUESTION_MESSAGE,
		            null, main.data.getRuns(null, null),
		            null);
	 		if (run1!=null) {
		 		run2 = (String) JOptionPane.showInputDialog(main,
			            "Activate",
			            "Last run",
			            JOptionPane.QUESTION_MESSAGE,
			            null, main.data.getRuns(run1, null),
			            null);
		 		log.info("Activat from run: "+run1 + " to "+run2);
		 		if (run2!=null) {
		 			for (int k=0; k<main.data.runListR.size(); k++) {
		 				if (main.data.runListR.get(k).run.compareToIgnoreCase(run1)>=0
		 						&& main.data.runListR.get(k).run.compareToIgnoreCase(run2)<=0) {
        					if (main.db.runTrue(main.data.runListR.get(k), true)) {
        						main.data.runListR.get(k).active=true;			
        					}
		 				}
		 			}	
			 		main.dataRecalc();
		 		}
	 		}
        }
        else if (text == "Setting of passes and mean") {
    		log.debug("execute: "+text);
    		main.runMeanPanel.format.showSettings();
        }
        else if (text == "Help") {
    		log.debug("execute: "+text);
    		try {  
	    		BrowserLauncher launcher = new BrowserLauncher(null);
	    		File helpFile=new File(Setting.batDir+"/doc/index.html");  
	    		String urlString;  
	    		try {  
		    		urlString = helpFile.toURI().toURL().toString();  
		    		launcher.openURLinBrowser(urlString); 
		    		log.debug("Opened file in browser: "+urlString);
	    		} catch (MalformedURLException e) {  
	    			log.debug(e.toString());  
	    		}  
    		}  
    		catch (BrowserLaunchingInitializingException ex) {  
    			log.debug(ex.getMessage());  
    		}  
    		catch (UnsupportedOperatingSystemException ex) {  
    			log.debug(ex.getMessage());  
    		}  
        }
        else if (text == "Browser") {
    		log.debug("execute: "+text);
    		if (!Setting.no_data) {
	    		try {  
		    		BrowserLauncher launcher = new BrowserLauncher(null);
		    		File helpFile=new File(Setting.magDir+"/"+main.data.magazine+".xhtml");  
		    		String urlString;  
		    		try {  
			    		URL url = helpFile.toURI().toURL();
						urlString = url.toString();  
			    		launcher.openURLinBrowser(urlString);  
			    		log.debug("Opened file in browser: "+urlString);
		    		} catch (MalformedURLException e) {  
		    			log.debug(e.toString());  
		    		}  
	    		}  
	    		catch (BrowserLaunchingInitializingException ex) {  
	    			log.debug(ex.getMessage());  
	    		}  
	    		catch (UnsupportedOperatingSystemException ex) {  
	    			log.debug(ex.getMessage());  
	    		}  
    		}
       	}
        else if (text == "About") {
    		log.debug("execute: "+text);
    		new About(Setting.batDir+"/doc/about.xhtml");
        }
        else if (text == "Log") {
        	JFrame help = new LogPanel(Setting.batDir+"/log/bats.log");
    		help.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if (text == "Setting of passes") {
    		log.debug("execute: "+text);
    		main.runPanel.format.showSettings();
        }       
        else if (text == "Setting of standards") {
    		log.debug("execute: "+text);
    		main.stdPanel.format.showSettings();
        }
        else if (text == "Setting of standards (dC13)") {
    		log.debug("execute: "+text);
    		main.std13Panel.format.showSettings();
        }
        else if (text == "Setting of standards (240/242)") {
    		log.debug("execute: "+text);
    		main.std13Panel.format.showSettings();
        }
        else if (text == "Setting of standards (239/240)") {
    		log.debug("execute: "+text);
    		main.stdPuPanel.format.showSettings();
        }
        else if (text == "Setting of blanks") {
    		log.debug("execute: "+text);
    		main.blankPanel.format.showSettings();
        }
        else if (text == "Setting of samples") {
    		log.debug("execute: "+text);
    		main.samplePanel.format.showSettings();
        }
        else if (text == "XHTML settings") {
    		log.debug("execute: "+text);
    		main.xPanel.format.showSettings();
        }
        else if (text == "Cycle settings") {
    		log.debug("execute: "+text);
    		main.cycDat.format.showSettings();
        }
        else if (text == "Print") {
    		log.debug("execute: "+text);
    		main.xPanel.print();
        }
        else if (text == "Recalculate") {
    		log.debug("execute: "+text);
        	main.dataRecalc();
        }      
        else if (text == "Increase") {
			main.xPanel.increase(true);	
			main.tableUpdate();
        }      
        else if (text == "Decrease") {
			main.xPanel.increase(false);	
        }      
        else if (text == "Force recalculate") {
    		log.debug("execute: "+text);
        	main.dataRecalc2();
        }      
        else if (text == "Reinitialise") {
    		log.debug("execute: "+text);
    		main.data.reinitData();
        	main.dataRecalc2();
        }      
        else if (text == "Save preferences") {
    		log.debug("execute: "+text);
        	Setting.save();
        }      
        else if (text == "Quit") {
    		int option = JOptionPane.showConfirmDialog(main, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
    		if (option == JOptionPane.YES_OPTION) {
    			Setting.save();
    			System.exit(0);
    		}
        }      
        else if (text == "Preferences") {
        	Preferences frame = new Preferences(main, 0);
        	frame.setModal(true);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else if ( Character.isDigit(text.charAt(0)) ) {
        	String fileName = text.substring(text.split(" ")[0].length()+1);
        	if ( text.charAt(1) == "M".charAt(0) ) {
//        		main.tbarStdBl.autocalc(true);
        		if (main.db.downloadMag(fileName)) {
	    			main.data.initData(new ArrayList<Corr>());
	    			log.debug(main.data.runListL.size()+" runs read from "+main.data.magazine+" in database!");
	        		main.dataRecalc2();
	            	log.debug("The following magazine was opend '"+fileName+"'");
        		} else {
					main.tba.update("no data loaded", false);
        		}
        	} else if (fileName.toLowerCase().endsWith(".txt")) {
        		IOFile.openDBXL(new File(fileName),main.data);
        		main.dataUpdate();
    			log.debug("DBXL file opened: "+fileName);
        	} else if (fileName.toLowerCase().endsWith(".hv")) {
//        		main.tbarStdBl.autocalc(true);
        		main.data.removeData();
        		IOFile.openHV(new File(fileName),main.data);
        		main.dataUpdate();
    			log.debug("HV file opened: "+fileName);
        	} else if (fileName.toLowerCase().endsWith(".bats")) {
				try {
//	        		main.tbarStdBl.autocalc(false);
	        		main.data.removeData();
	        		IOFile.openXML(new File(fileName),main);
	            	main.dataRecalc2();
		       		log.debug("Bats file opened: "+fileName);
    			}
    			catch(IOException e) {
    				String message = String.format( "File "+fileName+" does not exist or could not be opend!");
    			    	JOptionPane.showMessageDialog( null, message );
    				log.debug(message);
    			}
    			catch(JDOMException e) {
    				String message = String.format( "File "+fileName+" exists, but could not be read (JDOM exeption)!");
    			    	JOptionPane.showMessageDialog( null, message );
    				log.error(message);
    			}
        	} else {
				String message = String.format( "File type cannot be opend: "+text+"--"+fileName);
			    	JOptionPane.showMessageDialog( null, message );
				log.debug(message);
        	}
        }      
        else{
    		log.error("Could not execute: "+text);
         }      
	}
	
	private void saveAs() {
    	calibRanges();
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(Setting.homeDir+"/"+Setting.magazine+"/"+main.data.calcSet));
		fc.setDialogTitle("Save data to file");
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".csv");
			}
			public String getDescription() {
				return "comma delimited file";
			}
		});					
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".txt");
			}
			public String getDescription() {
				return "tab delimited file";
			}
		});	
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".xls");
			}
			public String getDescription() {
				return "excel file";
			}
		});	
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".pdf");
			}
			public String getDescription() {
				return "portable document file";
			}
		});	
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".bats");
			}
			public String getDescription() {
				return "bats file";
			}
		});					
		int returnVal = fc.showSaveDialog(main);
 		if (returnVal == JFileChooser.APPROVE_OPTION){
    		File file = fc.getSelectedFile();
    		String fileName = file.toString();
    		if (fc.getFileFilter().getDescription()=="bats file") {
	    		if (fileName.toLowerCase().endsWith(".bats") == false){
	    			main.data.calcSet=file.getName();
					fileName = fileName + ".bats";
				} else {
					main.data.calcSet=file.getName().substring(0,file.getName().length()-5);
				}
	    		main.tba.update("save", true);
				IOFile.saveXML(fileName, main.data);
				log.info("Saved file: "+fileName);
				main.infoPanel.update(main.data);
				main.tba.update("saved .bats", false);
   		}
    		else if (fc.getFileFilter().getDescription()=="comma delimited file") {
	    		if (fileName.toLowerCase().endsWith(".csv") == false){
					fileName = fileName + ".csv";
				}
	    		main.tba.update("save", true);
	    		ArrayList<DataSet> dat;
			    if (Setting.getBoolean("/bat/general/file/output/sample-only")) {
					dat = main.data.get("sample");
			    } else {
					dat = main.data.get("runSample");
			    }
				IOFile.save(fileName, dat, main.xPanel.format,"",",","","");
				log.debug("Saved file: "+fileName);
				main.tba.update("saved .csv", false);
//    			main.menuBar.menu_update();
//    			main.setJMenuBar(main.menuBar);
    		}
    		else if (fc.getFileFilter().getDescription()=="tab delimited file") {
	    		if (fileName.toLowerCase().endsWith(".txt") == false){
					fileName = fileName + ".txt";
				}
	    		main.tba.update("save", true);
	    		ArrayList<DataSet> dat;
			    if (Setting.getBoolean("/bat/general/file/output/sample-only")) {
					dat = main.data.get("sample");
			    } else {
					dat = main.data.get("runSample");
			    }
				IOFile.save(fileName, dat, main.xPanel.format, "","\t","","");
				log.debug("Saved file: "+fileName);
				main.tba.update("saved .txt", false);
//    			main.menuBar.menu_update();
//    			main.setJMenuBar(main.menuBar);
    		}
    		else if (fc.getFileFilter().getDescription()=="excel file") {
	    		if (fileName.toLowerCase().endsWith(".xls") == false){
					fileName = fileName + ".xls";
				}
	    		main.tba.update("save", true);
	    		ArrayList<DataSet> dat;
			    if (Setting.getBoolean("/bat/general/file/output/sample-only")) {
					dat = main.data.get("sample");
			    } else {
					dat = main.data.get("runSample");
			    }
				IOFile.saveExcel(fileName, dat, main.xPanel.format, main.data);
				log.debug("Saved file: "+fileName);
				main.tba.update("saved .xls", false);
//    			main.menuBar.menu_update();
//    			main.setJMenuBar(main.menuBar);
    		}
    		else if (fc.getFileFilter().getDescription()=="portable document file") {
	    		if (fileName.toLowerCase().endsWith(".pdf") == false){
					fileName = fileName + ".pdf";
				}
     			Pdf.save(main.xFile, fileName);
				log.debug("Saved file: "+fileName);
     		} else {
    			log.error("File filter error: "+fc.getFileFilter().getDescription());
    		}
		} else {
			log.debug("colSaveBat canceled by user.");
		}
	}
	
	private void open() {
		log.debug("start open");
		JFileChooser fc = new JFileChooser();
		log.debug("new file chooser");
		fc.setCurrentDirectory(new File(Setting.homeDir));
		log.debug("set home-dir");
		fc.setDialogTitle("Open file");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".dat");
			}
			public String getDescription() {
				return "DBXL file";
			}
		});
//		fc.addChoosableFileFilter(new FileFilter() {
//			public boolean accept( File f ) {
//				return f.isDirectory();
//			}
//			public String getDescription() {
//				return "HV directory";
//			}
//		});
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".txt");
			}
			public String getDescription() {
				return "TXT file";
			}
		});
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".bats");
			}
			public String getDescription() {
				return "bats file";
			}
		});
		log.debug("added filters");
		//Create the file chooser
		int returnVal = fc.showOpenDialog(fc);
		log.debug("file to open: "+returnVal);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getFileFilter().getDescription()=="DBXL file"){
				main.tba.update("open", true);
	    		Setting.autocalc=true;
				IOFile.openDBXL(fc.getSelectedFile(),main.data);
        		main.dataUpdate();
    			log.debug("DBXL-file opened: "+fc.getSelectedFile().toString());
//			} else if (fc.getFileFilter().getDescription()=="HV file"){
//				main.tba.update("open", true);
//        		main.tbarStdBl.autocalc(true);
//				main.data = IOFile.openHV(fc.getSelectedFile());
//        		main.dataRecalc2();
//     			log.debug("HV-dir opened: "+fc.getSelectedFile().toString());
			} else if (fc.getFileFilter().getDescription()=="bats file") {
				try {
					main.tba.update("open", true);
	        		main.data.removeData();
	        		Setting.autocalc=false;
					IOFile.openXML(fc.getSelectedFile(), main);
            		main.dataRecalc2();
        			// Add file to last-file list
        			Setting.setLastFile(fc.getSelectedFile().toString());
            		log.info("New bat file opened: "+fc.getSelectedFile().toString());
    			}
    			catch(IOException e) {
    				String message = String.format( "File "+fc.getSelectedFile()+" does not exist or could not be opend!");
    			    JOptionPane.showMessageDialog( null, message );
    				log.error(message);
    			}
    			catch(JDOMException e) {
    				String message = String.format( "File "+fc.getSelectedFile()+" exists, but could not be read!");
    			    JOptionPane.showMessageDialog( null, message );
    				log.error(message);
    			}
			}else{
				String message = String.format( "Fatal error: File filter does not exist!");
			    JOptionPane.showMessageDialog( null, message );
				log.fatal(message);
 			}
		}else{
		    log.debug("Open command cancelled by user.");
		}
	}
	
	private boolean openHV() {
		log.debug("start open");
		JFileChooser fc = new JFileChooser();
		log.debug("new file chooser");
		fc.setCurrentDirectory(new File(Setting.homeDir));
		log.debug("set home-dir");
		fc.setDialogTitle("Open file");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//Create the file chooser
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			main.tba.update("open", true);
 			log.debug("HV-dir opened: "+fc.getSelectedFile().toString());
 			return IOFile.openHV(fc.getSelectedFile(), main.data);
		}else{
		    log.debug("Open command cancelled by user.");
		    return false;
		}
	}
	
	private boolean openNEC() {
		log.debug("start open nec");
		JFileChooser fc = new JFileChooser();
		log.debug("new file chooser");
		fc.setCurrentDirectory(new File(Setting.homeDir));
		log.debug("set home-dir");
		fc.setDialogTitle("Open file");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//Create the file chooser
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			main.tba.update("open", true);
 			log.debug("NEC-dir opened: "+fc.getSelectedFile().toString());
 			return IOFile.openNEC(fc.getSelectedFile(), main.data);
		}else{
		    log.debug("Open command cancelled by user.");
		    return false;
		}
	}
	
	class TabbedFrame extends JDialog implements WindowListener
	{
		/**
		 * @param title
		 * @param graph 
		 */
		public TabbedFrame(String title, JComponent[] graph) {
			this.setModal(true);
			this.setDefaultLookAndFeelDecorated(true);
       		this.setTitle(title);       		
        	JTabbedPane tabbedPane = new JTabbedPane();
        	tabbedPane.setBorder(BorderFactory.createEmptyBorder());
            tabbedPane.setAutoscrolls(true);
        	for (int i=0; i<main.data.corrList.size(); i++) {
            	String name = "Correction "+i;
                tabbedPane.addTab(name, null, graph[i], name);
                tabbedPane.setMnemonicAt(i, KeyEvent.VK_1);
       		}
        	this.setContentPane(tabbedPane);
            this.addWindowListener(this);    
            this.pack();
            this.setVisible(true);			
		}

		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub		
		}

		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub			
		}

		public void windowClosing(WindowEvent arg0) {
			main.dataRecalc();
			log.debug("Closing window and recalculated data");
		}

		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub			
		}

		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub	
		}

		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub		
		}

		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub	
		}
	}
	
	private Double[] bgFrame(Corr corr) {
		Double[] bg = new Double[5];
        JPanel panel;
        String[] ConnectOptionNames = { "OK", "Cancel" };
	 	
	    NumberFormatter nf = new NumberFormatter();
	    nf.setMinimum(0.000001);
	    final JFormattedTextField textField = new JFormattedTextField(nf);
        textField.setValue(main.data.corrList.get(0).constBG);
        textField.setColumns(8); //get some space

        textField.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField.selectAll();
                }else try{                    //The text is valid,
                    textField.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField2 = new JFormattedTextField(nf);
        textField2.setValue(main.data.corrList.get(0).constBGErr);
        textField2.setColumns(8); //get some space

        textField2.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField2.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField2.selectAll();
                }else try{                    //The text is valid,
                    textField2.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField3 = new JFormattedTextField(nf);
        textField3.setValue(main.data.corrList.get(0).constBlWeight*1000);
        textField3.setColumns(8); //get some space

        textField3.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField3.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField3.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField3.selectAll();
                }else try{                    //The text is valid,
                    textField3.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField4 = new JFormattedTextField(nf);
        textField4.setValue(main.data.corrList.get(0).constBlRatio);
        textField4.setColumns(8); //get some space

        textField4.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField4.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField4.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField4.selectAll();
                }else try{                    //The text is valid,
                    textField4.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField5 = new JFormattedTextField(nf);
        textField5.setValue(main.data.corrList.get(0).constBlErr);
        textField5.setColumns(8); //get some space

        textField5.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField5.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField5.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField5.selectAll();
                }else try{                    //The text is valid,
                    textField5.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
		panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		JPanel namePanel = new JPanel(false);
		namePanel.setLayout(new GridLayout(0, 1));
 		// Create the labels
		JLabel labelBG = new JLabel("Counts per second:   ", JLabel.RIGHT);
		JLabel  labelErr = new JLabel("  error (abs):   ", JLabel.RIGHT);
		JLabel  labelWeight = new JLabel("Contamination weight (µg):   ", JLabel.RIGHT);
		JLabel  labelBl = new JLabel("<html>Contamination ratio (10<sup>12</sup>): </html>", JLabel.RIGHT);
		JLabel  labelBlErr = new JLabel("<html>  error (10<sup>12</sup>): </html>", JLabel.RIGHT);
		namePanel.add(labelBG);
		namePanel.add(labelErr);
		namePanel.add(labelWeight);
		namePanel.add(labelBl);
		namePanel.add(labelBlErr);
		JPanel fieldPanel = new JPanel(false);
		fieldPanel.setLayout(new GridLayout(0, 1));
		fieldPanel.add(textField);
		fieldPanel.add(textField2);
		fieldPanel.add(textField3);
		fieldPanel.add(textField4);
		fieldPanel.add(textField5);
		panel.add(namePanel);
		panel.add(fieldPanel);
		if(JOptionPane.showOptionDialog(
				main, panel, 
				"Constant background / contamination",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE,
                null, ConnectOptionNames, 
                ConnectOptionNames[0]) 
                != 0
                ) 
		{
			bg[0] = main.data.corrList.get(0).constBG;
			bg[1] = main.data.corrList.get(0).constBGErr;
			bg[2] = main.data.corrList.get(0).constBlWeight;
			bg[3] = main.data.corrList.get(0).constBlRatio;
			bg[4] = main.data.corrList.get(0).constBlErr;
		} else {
			bg[0] = Double.valueOf(textField.getText());
			bg[1] = Double.valueOf(textField2.getText());
			bg[2] = Double.valueOf(textField3.getText())/1000;
			bg[3] = Double.valueOf(textField4.getText());
			bg[4] = Double.valueOf(textField5.getText());
		}
		return bg;
	}
	
	/**
	 * 
	 */
	public void calibRanges() {
    	if (Setting.isotope.contains("C14")){
	        log.debug("start calc ranges");
	        for (int i=0; i<main.data.sampleList.size(); i++) {
	        	try {
	            	Sample sample = main.data.sampleList.get(i);
	            	sample.set(main.calib.sigmaRanges(main.calib.getProbability(sample.age, sample.age_err)),"ranges");
	        	} catch (NullPointerException e) {
	        		;
	        	}
	        }		
			log.debug("end calc ranges");		
		}
	}
}
