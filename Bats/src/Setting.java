import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.xpath.XPath;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * @author lukas
 *
 */
class Setting {
	static String lastIsotope;
	final static Logger log = Logger.getLogger(Setting.class);	
	static Document doc;
	static Document logDoc;
	
	/**
	 * Version of Bats
	 */
	public static String version="3.3 (9.5.2012)";
	
	/**
	 * Isotope for the current document
	 */
	public static String isotope;
	/**
	 * 
	 */
	public static String magazine;
	/**
	 * 
	 */
	public static String user = System.getProperty("user.name");
	/**
	 * 
	 */
	public static String homeDir = System.getProperty("user.home")+"/bats";
	/**
	 * 
	 */
	public static String magDir = homeDir + "/" + magazine;
	/**
	 * 
	 */
	public static String batDir = System.getProperty("user.dir")+"/settings";
	/**
	 * 
	 */
	public static ArrayList<String> selectCol;
	/**
	 * 
	 */
	public static ArrayList<String> selectIso;
	/**
	 * 
	 */
	public static ArrayList<String> colSaveRun;
	/**
	 * 
	 */
	public static ArrayList<String> colSaveSample;	
	/**
	 * 
	 */
	public static ArrayList<String> selectColN;
	/**
	 * 
	 */
	public static ArrayList<String> selectIsoN;
	/**
	 * 
	 */
	public static ArrayList<String> blankLabel;
	/**
	 * 
	 */
	public static Double[] chiLimitL;
	/**
	 * 
	 */
	public static Double[] chiLimitH;
	/**
	 * 
	 */
	public static Boolean no_data;
	/**
	 * 
	 */
	public static Boolean autocalc;
	/**
	 * 
	 */
	public static String db;
	
	public static int c=0;
	
	@SuppressWarnings("unchecked") 
	Setting() {
		String file = batDir+"/general.xml";
		try {
			magazine = "";
			no_data=true;
			doc = new Document(new Element("bat"));
			SAXBuilder builder = new SAXBuilder();
			Document doc1 = builder.build(new File(file));
			doc.getRootElement().addContent(doc1.cloneContent());
			setIsotope();
			file = batDir+"/"+isotope+"/isotope.xml";
			Document doc2 = builder.build(new File(file));
			doc.getRootElement().addContent(doc2.cloneContent());
			if(isotope.contains("C14")) {
				file = batDir+"/"+isotope+"/calib.xml";
				Document doc3 = builder.build(new File(file));
				doc.getRootElement().addContent(doc3.cloneContent());
				log.info("File ("+file+") for calib opened!");
			}
			try {
				homeDir = ((Element)XPath.selectSingleNode(doc, "/bat/isotope/path")).getText();
			} catch (JDOMException e) {
			} catch (NullPointerException e) {
				homeDir = System.getProperty("user.home")+"/bats";
				Element path = new Element("path");
		    	path.addContent(homeDir);
				Setting.getElement("/bat/isotope").addContent(0,path);
			}
			magDir = homeDir + "/" + magazine;
			List<Element> list = Setting.getElement("/bat/isotope/calc/blank").getChildren();
			blankLabel = new ArrayList<String>();
			for (int i=0;i<list.size();i++) {
				blankLabel.add((list.get(i)).getText());
			}
		}
		catch(IOException e) {
			String message = String.format("File ("+file+") could not be opened or does not exist!"+e);
			JOptionPane.showMessageDialog( null, message );
			log.fatal(message);
			System.exit(0);
		}
		catch(JDOMException e) {
			log.fatal("Setting file ("+file+") exists, but could not be read!");
			System.exit(0);
		}
		// load chisquare
		int chiSel = Setting.getInt("/bat/general/calc/chi");
		boolean lowLimit;
		lowLimit= Boolean.valueOf(Setting.getElement("/bat/general/calc/chi").getAttributeValue("ll"));
		log.debug("Chi-select: "+chiSel);
		load_chi(chiSel, lowLimit);
	}
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void reInit() {
		String file = batDir+"/general.xml";
		try {
			magazine ="";
			no_data=true;
			doc = new Document(new Element("bat"));
			SAXBuilder builder = new SAXBuilder();
			Document doc1 = builder.build(new File(file));
			doc.getRootElement().addContent(doc1.cloneContent());
			file = batDir+"/"+isotope+"/isotope.xml";
			Document doc2 = builder.build(new File(file));
			doc.getRootElement().addContent(doc2.cloneContent());
			log.info("File ("+file+") for settings opened!");
			List<Element> list = Setting.getElement("/bat/isotope/calc/blank").getChildren();
			blankLabel = new ArrayList<String>();
			for (int i=0;i<list.size();i++) {
				blankLabel.add((list.get(i)).getText());
			}
		}
		catch(IOException e) {
			String message = String.format("File ("+file+") could not be opened or does not exist!"+e);
			JOptionPane.showMessageDialog( null, message );
			log.fatal(message);
			System.exit(0);
		}
		catch(JDOMException e) {
			log.fatal("Setting file ("+file+") exists, but could not be read!");
			System.exit(0);
		}
	}
	
	
	
	/**
	 * @param chiSel
	 * @param lowLimit
	 */
	public static void load_chi(int chiSel, boolean lowLimit) {
		try {
			Scanner input = new Scanner( new File(batDir+"/chi.txt") );
			try {
				int i=0;
				chiLimitL = new Double[100];
				chiLimitH = new Double[100];
				while ( input.hasNextLine() && i<100){
					if (input.hasNext( "#" )){
						input.nextLine();
					}
					else{
						String[] temp = input.nextLine().split("\t");
						if (lowLimit) {
							chiLimitL[i]=Double.valueOf(temp[2*chiSel+1]);
						} else { chiLimitL[i]=0.0; }
						chiLimitH[i++]=Double.valueOf(temp[2*chiSel]);
					}
//					log.debug(chiLimitL[i-1]+" / "+chiLimitH[i-1]);
				}
				log.debug("dbxl-file successfully read!");
			}
			catch ( NoSuchElementException elementException){
				String message = String.format( "<html>Could not open file:<br>File improperly formed!</html>");
				log.error("Could not open file: File improperly formed!");
				JOptionPane.showMessageDialog( null, message );
			}
		}
		catch ( FileNotFoundException e) {
			String message = String.format( "Could not finde chi-file!");
			JOptionPane.showMessageDialog( null, message );
			log.debug(message);
		}		
	}
	
	@SuppressWarnings("unchecked")
	private static void selectLists() {
	    Document doc_sel;
		String fileNam=""; 
		try {
			fileNam = batDir+"/"+isotope+"/select.xml";
			SAXBuilder builder = new SAXBuilder();
			doc_sel = builder.build(new File(fileNam));
			List<Element> colList = (List<Element>)XPath.selectNodes(doc_sel, "/select/column/name");
			log.debug("File ("+fileNam+") for selectCol settings opened!");
			selectCol = new ArrayList<String>();
			selectColN = new ArrayList<String>();
			List<Element> isoList = (List<Element>)XPath.selectNodes(doc_sel, "/select/isobar/name");
			selectIso = new ArrayList<String>();
			selectIsoN = new ArrayList<String>();
			for (int i=0; i<colList.size(); i++) {
				try {
					selectColN.add(colList.get(i).getText());
					selectCol.add(colList.get(i).getAttributeValue("field"));
				} catch (NumberFormatException e) {
					log.error("File "+fileNam+" improperly formed in select-col "+(i));
				} catch (NullPointerException e) {
					log.error("NulPointerException in file: select-col "+(i));
				}
			}
			for (int i=0; i<isoList.size(); i++) {
				try {
					selectIso.add(isoList.get(i).getText());
					selectIsoN.add(isoList.get(i).getAttributeValue("field"));
				} catch (NumberFormatException e) {
					log.error("File "+fileNam+" improperly formed in select-iso "+(i));
				} catch (NullPointerException e) {
					log.error("NulPointerException in file: select-iso "+(i));
				}
			}
			fileNam = batDir+"/"+isotope+"/file_io/bats_io.xml";
			builder = new SAXBuilder();
			doc_sel = builder.build(new File(fileNam));
			List<Element> saveList = (List<Element>)XPath.selectNodes(doc_sel, "/select/save_run/name");
			List<Element> saveAllList = (List<Element>)XPath.selectNodes(doc_sel, "/select/save_sample/name");
			colSaveRun = new ArrayList<String>();
			colSaveSample = new ArrayList<String>();
			for (int i=0; i<saveList.size(); i++) {
				try {
					colSaveRun.add(saveList.get(i).getText());
				} catch (NumberFormatException e) {
					log.error("File "+fileNam+" improperly formed in save line "+(i));
				} catch (NullPointerException e) {
					log.error("NulPointerException in file: save line "+(i));
				}
			}
			for (int i=0; i<saveAllList.size(); i++) {
				try {
					colSaveSample.add(saveAllList.get(i).getText());
				} catch (NumberFormatException e) {
					log.error("File "+fileNam+" improperly formed in save-all line "+(i));
				} catch (NullPointerException e) {
					log.error("NulPointerException in file: save-all line "+(i));
				}
			}
		}
		catch(JDOMException e) {
			log.error("Select file ("+fileNam+") exists, but could not be read!");
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error("IOException for file: "+fileNam+"");
		}		
	}
		
	/**
	 * 
	 */
	static public void save(){
	    Format format= Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
		XMLOutputter out = new XMLOutputter(format);
		Document doc1 = new Document((Element) doc.getRootElement().getChild("general").clone());
		Document doc2 = new Document((Element) doc.getRootElement().getChild("isotope").clone());
		doc1.getRootElement().getAttribute("date").setValue(new Date().toString());
		doc2.getRootElement().getAttribute("date").setValue(new Date().toString());
		doc1.getRootElement().getAttribute("version").setValue(version);
		doc2.getRootElement().getAttribute("version").setValue(version);
		// write main settings file
		if (new File(batDir+"/general.xml").renameTo(new File(batDir+"/general.xml.old"))) { 
			log.debug("Old file renamed: "+batDir+"/general.xml");
			}
		else {
			log.debug("Old file ("+batDir+"/general.xml) could not be renamed and will be overwritten!");
		}
		try {
			FileOutputStream fs = new FileOutputStream(new File(batDir+"/general.xml")); 
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			out.output(doc1,writer);
			fs.close();
		} catch (IOException e) {
			String message = String.format( "<html>File could not be written: <br>"+batDir+"/general.xml</html>");
			JOptionPane.showMessageDialog( null, message );
			log.error("XML-output could not be written to file: "+batDir+"/general.xml");
		}
		// write isotope settings file
		if (new File(batDir+"/"+isotope+"/isotope.xml").renameTo(new File(batDir+"/"+isotope+"/isotope.xml.old"))) { 
			log.debug("Old file renamed: "+batDir+"/"+isotope+"/isotope.xml");
			}
		else {
			log.debug("Old file ("+batDir+"/"+isotope+"/isotope.xml) could not be renamed and will be overwritten!");
		}
		try {
			FileOutputStream fs = new FileOutputStream(new File(batDir+"/"+isotope+"/isotope.xml")); 
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			out.output(doc2,writer);
			fs.close();
		} catch (IOException e) {
			String message = String.format( "<html>File could not be written: <br>"+batDir+"/"+isotope+"/isotope.xml</html>");
			JOptionPane.showMessageDialog( null, message );
			log.error("XML-output could not be written to file: "+batDir+"/"+isotope+"/isotope.xml");
		}
		// write calib settings file
		if (new File(batDir+"/"+isotope+"/calib.xml").renameTo(new File(batDir+"/"+isotope+"/calib.xml.old"))) { 
			log.debug("Old file renamed: "+batDir+"/"+isotope+"/calib.xml");
			}
		else {
			log.debug("Old file ("+batDir+"/"+isotope+"/calib.xml) could not be renamed and will be overwritten!");
		}
	    if (Setting.isotope.contains("C14")) {
	    	Document doc3 = new Document((Element) doc.getRootElement().getChild("calib").clone());
			doc3.getRootElement().getAttribute("date").setValue(new Date().toString());
			doc3.getRootElement().getAttribute("version").setValue(version);
	    	try {
				FileOutputStream fs = new FileOutputStream(new File(batDir+"/"+isotope+"/calib.xml")); 
				OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
				out.output(doc3,writer);
				fs.close();
			} catch (IOException e) {
				String message = String.format( "<html>File could not be written: <br>"+batDir+"/"+isotope+"/calib.xml</html>");
				JOptionPane.showMessageDialog( null, message );
				log.error("XML-output could not be written to file: "+batDir+"/"+isotope+"/calib.xml");
			}
	    }
	}
	
	/**
	 * @param xPath
	 * @param attr attribute
	 * @return float attribute from xml-file
	 */
	static public float getFloatAttr(String xPath, String attr) {
		float value;
		try {
			value = Setting.getElement(xPath).getAttribute(attr).getFloatValue();
		} catch (JDOMException e) {
			value = 0;
			log.error("Coudn't get path: "+xPath);
		} catch (NumberFormatException e) {
			value = 0;
			log.error("Attribute "+attr+" with the following path is not an float: "+xPath);
		} catch (NullPointerException e) {
			log.error("NullPointerException: coudn't get path "+xPath+" in file for settings!");
			value = 0;
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return integer from xml-file
	 */
	static public int getInt(String xPath) {
		int value;
		try {
			try {
				value = Integer.decode(((Element)XPath.selectSingleNode(doc, xPath)).getText());
			} catch (NumberFormatException e) {
				value = 0;
				log.error("Element with the following path is not an integer: "+((Element)XPath.selectSingleNode(doc, xPath)).getText()+" ("+xPath+")");
			}
		} catch (JDOMException e) {
			value = 0;
			log.error("Coudn't get path: "+xPath);
		} catch (NullPointerException e) {
			log.error("NullPointerException: coudn't get path "+xPath+" in file for settings!");
			value = 0;
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return element from xml-file
	 * @throws JDOMException 
	 */
	@SuppressWarnings("unchecked")
	static public List<Element> getElements(String xPath) throws JDOMException {
		List<Element> values;
		List<Element> selectNodes = (List<Element>)XPath.selectNodes(doc, xPath);
		values =selectNodes;
		return values;
	}
	
	/**
	 * @param xPath
	 * @return element from xml-file
	 */
	static public Element getElement(String xPath) {
		Element value;
		try{
			value =(Element)XPath.selectSingleNode(doc, xPath);
		} catch (JDOMException e) {
			value = null;
			log.error("Coudn't get path: "+xPath);
		} 
		return value;
	}
	
	/**
	 * @param xPath
	 * @return double from xml-file
	 * @throws NullPointerException 
	 */
	static public Double getDouble(String xPath) throws NullPointerException{
		Double value=null;
		try{
			value = Double.valueOf(((Element)XPath.selectSingleNode(doc, xPath)).getText());
		} catch (JDOMException e) {
			log.error("Coudn't get path: "+xPath);
		} catch (NumberFormatException e) {
			log.error("Element with the following path is not an double: "+xPath);
		} catch (NullPointerException e) {
			log.error("Coudn't get path (NullPointerException: "+xPath);
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return float from xml-file
	 * @throws NullPointerException 
	 */
	static public Float getFloat(String xPath) throws NullPointerException{
		Float value=null;
		try{
			value = Float.valueOf(((Element)XPath.selectSingleNode(doc, xPath)).getText());
		} catch (JDOMException e) {
			log.error("Coudn't get path: "+xPath);
		} catch (NumberFormatException e) {
			log.error("Element with the following path is not an double: "+xPath);
		} catch (NullPointerException e) {
			log.error("Coudn't get path (NullPointerException: "+xPath);
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return color from xml-file
	 * @throws NullPointerException 
	 */
	static public Color getColor(String xPath) throws NullPointerException{
		Color value=null;
		try{
			value = new Color(Integer.decode(((Element)XPath.selectSingleNode(doc, xPath)).getText()));
		} catch (JDOMException e) {
			log.error("Coudn't get path: "+xPath);
		} catch (NumberFormatException e) {
			log.error("Element with the following path is not an integer: "+xPath);
			value=Color.white;
		} catch (NullPointerException e) {
			log.error("Coudn't get path (NullPointerException: "+xPath);
			value=Color.white;
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return string from xml-file
	 */
	static public Boolean getBoolean(String xPath) {
		Boolean value;
		try {
			value = Boolean.valueOf(((Element)XPath.selectSingleNode(doc, xPath)).getText());
		} catch (NullPointerException e) {
			value = false;
			log.error("Coudn't get path (NullPointerException): "+xPath+" (return false!)");
		} catch (JDOMException e) {
			value = false;
			log.error("Coudn't get path: "+xPath+" (return false!)");
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return string from xml-file
	 */
	static public Boolean getActive(String xPath) {
		Boolean value;
		try {
			value = ((Element)XPath.selectSingleNode(doc, xPath)).getAttribute("active").getBooleanValue();
		} catch (NullPointerException e) {
			value = false;
			log.error("Coudn't get path (NullPointerException): "+xPath+" (return false!)");
		} catch (JDOMException e) {
			value = false;
			log.error("Coudn't get path: "+xPath+" (return false!)");
		}
		return value;
	}
	
	/**
	 * @param xPath
	 * @return string from xml-file
	 */
	static public String getString(String xPath) {
		String value;
		try{
			value = ((Element)XPath.selectSingleNode(doc, xPath)).getText();
		} catch (JDOMException e) {
			value = "error";
			log.error("Coudn't get path: "+xPath);
		} catch (NullPointerException e) {
			value = "error";
			log.error("Coudn't get path (NullPointerException: "+xPath+")");
		}
		return value;
	}
	
    /**
     * Asks for an selectable isotope and initialises settings for this isotope
     * Sets settings for 
     * @return isotope
     */
    @SuppressWarnings("unchecked")
	static public String setIsotope(){
    	try {
    		List<Element> elements = getElement("/bat/general/isotope").getChildren();
	 		String[] isoList = new String[elements.size()];
			int k=0;
			for (Iterator i = elements.iterator(); i.hasNext();) {
				isoList[k++]=((Element) i.next()).getText();
			}
			isotope = getElement("/bat/general/isotope").getAttributeValue("last");
			log.debug("Last isotope was "+isotope);
			isotope = (String) JOptionPane.showInputDialog(null,
		        "Start-up",
		        "Select isotope", 
		        JOptionPane.QUESTION_MESSAGE,
		        new ImageIcon(Setting.batDir+"/icon/bat_icon.png"), isoList,
		        isotope);
			if(isotope==null){
				log.debug("Document with settings has no isotops!");
				System.exit(1);
			} else {
				getElement("/bat/general/isotope").getAttribute("last").setValue(isotope);
			}
		} catch(NullPointerException e) {
			log.debug("Document with settings has no isotopes!");
			System.exit(1);
		}
		selectLists();
   		return isotope;
    }
    
    /**
     * @param path 
     * @return Array of strings
     */
    @SuppressWarnings("unchecked")
	static public ArrayList<String> getStrings(String path){
		List<Element> elements;
		ArrayList<String> columns = new ArrayList<String>();
		try {
			elements = getElement(path).getChildren();
			for (int i=0; i<elements.size(); i++) {
				columns.add(elements.get(i).getText());
			}
			log.debug(columns.size()+" column will be imported.");
		} catch(NullPointerException e) {
			columns = null;
			log.error("No children found at "+path);
		}
    	return columns;
    }
    
    /**
     * @return Array with last files
     */
    @SuppressWarnings("unchecked")
	static public ArrayList<String> getLastFile() {
    	ArrayList<String> fileNames = new ArrayList<String>(10);
    	List<Element> last;
		try {
			last = getElement("/bat/isotope/file/last").getChildren();
		   	for (int i=0;i<last.size();i++) {
		   		if (!last.get(i).getText().equalsIgnoreCase("")) {
		   			fileNames.add(last.get(i).getText()); 
		   		}
		   	}
 		} catch (NullPointerException e) {
 			log.error("Could not get the last files.");
 		}
    	return fileNames;
    }
    
    /**
     * @return Array with last files
     */
    @SuppressWarnings("unchecked")
	static public ArrayList<String> getLastMag() {
    	ArrayList<String> magNames = new ArrayList<String>(10);
    	List<Element> last;
		try {
			last = getElement("/bat/isotope/db/last_mag").getChildren();
		   	for (int i=0;i<last.size();i++) {
		   		magNames.add(last.get(i).getText()); 
		   	}
 		} catch (NullPointerException e) {
 			log.warn("Could not get the last magazines.");
 		}
    	return magNames;
    }
    
    /**
     * @param fileName
     */
    static public void setLastFile(String fileName) {
    	try {
        	Element last;
			last = getElement("/bat/isotope/file/last");
	    	for (int i=0; i< last.getChildren().size(); i++) {
	    		if (fileName.equals(((Element)last.getChildren().get(i)).getText())) {
	    			last.getChildren().remove(i);
    				log.debug("Removed same file!");
	    		} else if (i>8) {
    				last.getChildren().remove(i);
    				log.debug("Removed last file: "+i);
	    		}
	    	}
	    	Element file = new Element("filename");
	    	file.addContent(fileName);
	    	last.addContent(0, file);
	    	log.debug("Last file set to "+fileName);
		} catch (NullPointerException e) {
			log.error("Could not get Element with filenames!");
		}
    }
    
    /**
     * @param magName
     */
    static public void setLastMag(String magName) {
    	try {
    		if (!Setting.no_data) {
	        	Element last;
				last = getElement("/bat/isotope/db/last_mag");
		    	for (int i=0; i< last.getChildren().size(); i++) {
		    		if (magName.equals(((Element)last.getChildren().get(i)).getText())) {
		    			last.getChildren().remove(i);
	    				log.debug("Removed same magazine!");
		    		} else if (i>8) {
	    				last.getChildren().remove(i);
	    				log.debug("Removed last magazine: "+i);
		    		}
		    	}
		    	Element file = new Element("magname");
		    	file.addContent(magName);
		    	last.addContent(0, file);
		    	log.debug("Last magazine set to "+magName);
    		}
		} catch (NullPointerException e) {
			log.error("Could not get Element with magnames!");
		}
    }
    
    /**
     * 
     */
    static public void reset() {
    	try {
        	Element element;
        	int j = 0;
			element = getElement("/bat/isotope/db/last_mag"); log.debug(j++);
	    	for (int i=0; i< element.getChildren().size(); i++) {
	    		element.getChildren().remove(i);
	    	} 
			element = getElement("/bat/isotope/file/last"); log.debug(j++);
	    	for (int i=0; i< element.getChildren().size(); i++) {
	    		element.getChildren().remove(i);
 	    	}
			getElement("/bat/isotope/calc/bg/factor").setText("0.0"); log.debug(j++);
			getElement("/bat/isotope/calc/bg/error").setText("0.0"); log.debug(j++);
			getElement("/bat/isotope/calc/scatter").setText("0.0"); log.debug(j++);
			getElement("/bat/isotope/calc/nominal_ra").setText("1.0"); log.debug(j++);
			getElement("/bat/isotope/calc/current/a/offset").setText("0.0"); log.debug(j++);
			getElement("/bat/isotope/calc/current/b/offset").setText("0.0"); log.debug(j++);
			getElement("/bat/isotope/calc/current/iso/offset").setText("0.0"); log.debug(j++);
			getElement("/bat/general/calc/autocalc").setText("true"); log.debug(j++);
			getElement("/bat/isotope/calc/poisson").setText("true"); log.debug(j++);
			if (Setting.isotope.contains("C14")) {
				getElement("/bat/isotope/calc/fract").setText("true"); log.debug(j++);
				getElement("/bat/isotope/calc/nominal_ra").setText("100"); log.debug(j++);
				getElement("/bat/isotope/calc/nominal_ba").setText("0.975"); log.debug(j++);
				getElement("/bat/isotope/calc/autocalc/std13").setText("true"); log.debug(j++);
				getElement("/bat/isotope/calc/autocalc/std13_err").setText("true"); log.debug(j++);
			}
			getElement("/bat/isotope/calc/autocalc/std").setText("true"); log.debug(j++);
			getElement("/bat/isotope/calc/autocalc/std_err").setText("true"); log.debug(j++);
			getElement("/bat/isotope/calc/autocalc/blank").setText("true"); log.debug(j++);
			getElement("/bat/isotope/calc/autocalc/blank_err").setText("true"); log.debug(j++);
			getElement("/bat/general/file/autosave/plot").setText("false"); log.debug(j++);
			getElement("/bat/general/file/autosave/result").setText("true"); log.debug(j++);
			getElement("/bat/general/file/output/sample-only").setText("true"); log.debug(j++);
			getElement("/bat/general/file/output/corr").setText("true"); log.debug(j++);
			getElement("/bat/general/file/autosave/bats").setText("true"); log.debug(j++);
			getElement("/bat/isotope/db/ora/cycle").getAttribute("name").setValue("false"); log.debug(j++);
			getElement("/bat/isotope/db/ora/virtual").setText("true"); log.debug(j++);
			getElement("/bat/general/db/ora/user").setText("Nobody"); log.debug(j++);
			getElement("/bat/general/db/ora/pw").setText(""); log.debug(j++);
			getElement("/bat/isotope/db/sql-import/cycle").getAttribute("name").setValue("false"); log.debug(j++);
			getElement("/bat/isotope/db/sql-import/virtual").setText("true"); log.debug(j++);
//			getElement("/bat/general/db/sql-import/user").setText("Nobody");
			getElement("/bat/general/db/sql-import/pw").setText(""); log.debug(j++);
//			getElement("/bat/general/db/sql/user").setText("Nobody");
			getElement("/bat/general/db/sql/pw").setText(""); log.debug(j++);
			getElement("/bat/general/table/run_order").setText("false"); log.debug(j++);
//			getElement("/bat/isotope/db/ora/virtual").setText("false");
//			Setting.save();
			log.debug("Made reset!");
    	} catch (NullPointerException e) {
    		log.error("Failed to make proper reset!");
		}
    }
    
    /**
     * @param mag
     */
    public static void setMagazine(String mag) {
    	magazine = mag;
    	no_data = false;
    	magDir = homeDir+"/"+magazine;
    	log.debug("Magazine set to "+magazine+" and magDir set to "+magDir);
    	File dir = new File(magDir);
    	if (!dir.exists()) {
    		dir.mkdirs();
    		log.debug("Directory "+dir+" created.");
    	}
    }
     
    /**
     * @param dir
     */
    static public void setHomeDir(String dir) {
    	Setting.homeDir = dir;  	
		log.debug("Home directory set to: "+homeDir);
    }
    
    /**
     * Defines the calcualtion for an isotope (eg. for radiocarbon "Calc14C" and for Be10 "CalcDefault")
     * @param iso
     * @return data
     */
    static public Calc initCalcIso(String iso) {
    	if (!iso.equalsIgnoreCase(Setting.isotope)) {
    		Setting.save();
    	   	isotope = iso;  	
    		log.debug("Isotope set to: "+isotope);
    	}
    	Calc data;
    	Setting.magazine="";
    	Setting.no_data=true;
    	if (isotope.equals("C14")){
			data = new Calc14C();
		}
		else if (isotope.equals("C14Tandem")){
			data = new Calc14C();
		}
		else if (isotope.equals("Ca41")){
			data = new Calc41Ca();
		}
		else if (isotope.equals("Cl36")){
			data = new Calc36Cl();
		}
		else if (isotope.equals("Be10")){
			data = new CalcDefault();
		}
		else if (isotope.equals("Be10Tandem")){
			data = new CalcDefault();
		}
		else if (isotope.equals("I129")){
			data = new CalcDefault();
		}
		else if (isotope.equals("Pu")){
			data = new CalcPu();
		}
		else {
			log.fatal("Isotope does not exist!!!");
			JOptionPane.showMessageDialog( null, "Isotope does not exist!!!");
			data = null;
			new Setting();
     		data = initCalcIso(iso);
		}
    	return data;
    }
}
