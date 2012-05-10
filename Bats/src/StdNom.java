import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author lukas
 *
 */
public class StdNom {
	final static Logger log = Logger.getLogger(StdNom.class);
		
	String isotope;
	File file;
	
	/**
	 * 
	 */
	public ArrayList<StdData> stdList = new ArrayList<StdData>();
	StdNomTable stdPanel;
	
	StdNom(String isotope){		
		file = new File(Setting.batDir+"/"+isotope+"/standards.xml");
		this.isotope = isotope;
		if (file.exists()){
			try{
				open(file);
			} catch (IOException e) {
				String message = String.format( "<html>File could not be opend: <br>"+file+"<br>(IO exception)</html>");
				JOptionPane.showMessageDialog( null, message );
				log.error("File could not be opend: "+file+" (IO exception)");
				addStd();				
			}
		}
		else {
			String message = String.format( "<html>File could not be opend: <br>"+file+"</html>");
			JOptionPane.showMessageDialog( null, message );
			log.error("File could not be opend: "+file+" (IO exception)");
			addStd();
		}		
		stdPanel = new StdNomTable(this);
	}
	
	/**
	 * 
	 */
	public void showStd() {
		//Create and set up the window.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Standards");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.addWindowListener((WindowListener) this);
	    JMenuBar menuBar = new StdNomTableMenu(this);
	    frame.setJMenuBar(menuBar);
		StdNomTableTBar toolBar = new StdNomTableTBar(this);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH); 		
		frame.getContentPane().add(stdPanel,BorderLayout.SOUTH);
		
		//Display the window.
	    frame.pack();
	    frame.setVisible(true);
	}
	
	/**
	 * @param stdName
	 * @return if it is an active standard
	 */
	public Boolean isStd(String stdName) {
		Boolean a = false;
		if (!stdName.equals(null)) {
			for (int i=0;i<stdList.size();i++) {
				if (stdList.get(i).active && stdList.get(i).name.equalsIgnoreCase(stdName)) {
					a = true;
				}
			}
		}
		return a;
	}
	
	/**
	 * @param stdName
	 * @return if it is an active standard
	 * @throws NullPointerException 
	 */
	public StdData getStd(String stdName) {
		StdData std = null;
		if (!stdName.equals(null)) {
			for (int i=0;i<stdList.size();i++) {
				if (stdList.get(i).name.equalsIgnoreCase(stdName)) {
					std = stdList.get(i);
				}
			}
		}
		return std;
	}
	
	/**
	 * 
	 */
	public void addStd() {
		StdData std = new StdData();
		std.name = "new standard";
		std.delta = -19.4;
		std.delta_sig = 0.0;
		std.delta_nom = -19.0;
		std.pmC = 1.0;
		std.pmC_sig = 0.0;
		std.ra = 1.21;
		std.ra_sig = 0.0;
		std.active = true;
		stdList.add(std);
		log.debug("add Standard -> size:"+stdList.size());		
	}

	/**
	 * @param file
	 * Save settings to file
	 */
	public void save(File file) {
		Element root = new Element("standard");
		root.setAttribute("date",new Date().toString());
	    for (int i=0; i<stdList.size();i++) {
	    	Element col = new Element("std");
	    	col.setAttribute("name", stdList.get(i).getXML("name"));
	    	root.addContent(col);
	    	Element element = new Element("ra");
	    	element.addContent(stdList.get(i).getXML("ra"));
	    	col.addContent(element);
	    	element = new Element("ra_sig");
	    	element.addContent(stdList.get(i).getXML("ra_sig"));
	    	col.addContent(element);
	    	element = new Element("ba");
	    	element.addContent(stdList.get(i).getXML("ba"));
	    	col.addContent(element);
	    	element = new Element("ba_nom");
	    	element.addContent(stdList.get(i).getXML("ba_nom"));
	    	col.addContent(element);
	    	element = new Element("ba_sig");
	    	element.addContent(stdList.get(i).getXML("ba_sig"));
	    	col.addContent(element);
	    	element = new Element("delta");
	    	element.addContent(stdList.get(i).getXML("delta"));
	    	col.addContent(element);
	    	element = new Element("delta_nom");
	    	element.addContent(stdList.get(i).getXML("delta_nom"));
	    	col.addContent(element);
	    	element = new Element("delta_sig");
	    	element.addContent(stdList.get(i).getXML("delta_sig"));
	    	col.addContent(element);
	    	element = new Element("pmC");
	    	element.addContent(stdList.get(i).getXML("pmC"));
	    	col.addContent(element);
	    	element = new Element("pmC_sig");
	    	element.addContent(stdList.get(i).getXML("pmC_sig"));
	    	col.addContent(element);
	    	element = new Element("active");
	    	element.addContent(stdList.get(i).getXML("active"));
	    	col.addContent(element);
	    }
	    Document doc = new Document();
	    doc.setRootElement(root);
	    Format format= Format.getPrettyFormat();
	    format.setEncoding("ISO-8859-1");
		XMLOutputter out = new XMLOutputter(format);
		try {
			out.output(doc,new FileWriter(file));
			log.debug("File saved: "+file);
		} catch (IOException e) {
			String message = String.format( "<html>File could not be written: <br>"+file+"</html>");
			JOptionPane.showMessageDialog( null, message );
			log.error("XML-output could not be written to file: "+file);
		}
	}

	/**
	 * @param fileName 
	 * Opens file with XML settings
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void open(File fileName) throws IOException, FileNotFoundException { 
	    Document doc;
		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(fileName);
			log.debug("File ("+fileName+") for standards opened!");
			Element root = doc.getRootElement();
			List<Element> list = root.getChildren();
			stdList = new ArrayList<StdData>();
			StdData std;
			for (int i=0; i<list.size(); i++) {
				try {
					if (list.get(i).getAttribute("name").getValue().equals("")) {
						log.debug("Standard ignored because name=\"\"");
					} else {
						std = new StdData();
			    		try{std.name=list.get(i).getAttribute("name").getValue();}
						catch(NumberFormatException e){std.ra=null;}
			    		try{std.ra=Double.valueOf(list.get(i).getChild("ra").getText());}
						catch(NumberFormatException e){std.ra=null;}
			    		try{std.ra_sig=Double.valueOf(list.get(i).getChild("ra_sig").getText());}
						catch(NumberFormatException e){std.ra_sig=null;}
			    		try{std.ba=Double.valueOf(list.get(i).getChild("ba").getText());}
						catch(NumberFormatException e){std.ba=null;}
			    		try{std.ba_nom=Double.valueOf(list.get(i).getChild("ba_sig").getText());}
						catch(NumberFormatException e){std.ba_nom=null;}
			    		try{std.ba_sig=Double.valueOf(list.get(i).getChild("ba_nom").getText());}
						catch(NumberFormatException e){std.ba_sig=null;}
			    		try{std.delta=Double.valueOf(list.get(i).getChild("delta").getText());}
						catch(NumberFormatException e){std.delta=null;}
			    		try{std.delta_nom=Double.valueOf(list.get(i).getChild("delta_nom").getText());}
						catch(NumberFormatException e){std.delta_nom=null;}
			    		try{std.delta_sig=Double.valueOf(list.get(i).getChild("delta_sig").getText());}
						catch(NumberFormatException e){std.delta_sig=null;}
			    		try{std.pmC=Double.valueOf(list.get(i).getChild("pmC").getText());}
						catch(NumberFormatException e){std.pmC=null;}
			    		try{std.pmC_sig=Double.valueOf(list.get(i).getChild("pmC_sig").getText());}
						catch(NumberFormatException e){std.pmC_sig=null;}
			    		try{std.active=Boolean.valueOf(list.get(i).getChild("active").getText());}
						catch(NumberFormatException e){std.active=true;}
						stdList.add(std);
					}
				} catch (NumberFormatException e) {
					log.error("File improperly formed: edit in "+file+"");
				} catch (NullPointerException e) {
					log.error("NulPointerException in XML read-in (col "+(i)+").");
				}
			}
		}
		catch(JDOMException e) {
			log.error("Standard file ("+fileName+") exists, but could not be read!");
		}
    }
	
	/**
	 * @param text 
	 * 
	 */
	public void action(String text) {
        if (text == "Save...") {
    	    JFileChooser fc = new JFileChooser();
    	    fc.setSelectedFile(this.file);
    	    fc.setFileFilter( new FileFilter() {
    	    	      @Override public boolean accept( File f ) {
    	    	        return f.isDirectory() ||
    	    	          f.getName().toLowerCase().endsWith( ".xml" );
    	    	      }
    	    	      @Override public String getDescription() {
    	    	        return "AMS standard file";
    	    	      }
    	    	    } );	    		
    		int returnVal = fc.showSaveDialog(null);	    		
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
    			if (fc.getSelectedFile().isFile()) {
    				int overwrite = JOptionPane.showConfirmDialog(null, "<html>Do your want to overwrite this file?<br>"+fc.getSelectedFile()+"</html>");
    				if(overwrite==0) {
	            		this.file = fc.getSelectedFile();
	            		this.save(this.file);
    				} else {
    					log.debug("Save canceled by user: "+fc.getSelectedFile());
   				}
				} else {
            		this.file = fc.getSelectedFile();
            		this.save(this.file);
    			}
    		} else {
				String message = String.format( "Did not colSaveBat!");
				JOptionPane.showMessageDialog( null, message );
				log.debug("Selection was not approved: "+fc.getSelectedFile());
	    	}
        }        
        else if (text == "Save") {
			this.save(this.file);
        }        
        else if (text == "Add new standard") {
			this.addStd();
			this.stdPanel.updateTable();
			log.debug("add Standard");
        } 
        else if (text == "Open settings...") {
    		//Create a file chooser
    	    JFileChooser fc = new JFileChooser();
    	    fc.setSelectedFile(this.file);
     	    fc.setFileFilter( new FileFilter() {
    	    	      @Override public boolean accept( File f ) {
    	    	        return f.isDirectory() ||
    	    	          f.getName().toLowerCase().endsWith( ".xml" );
    	    	      }
    	    	      @Override public String getDescription() {
    	    	        return "AMS standard file";
    	    	      }
    	    	    } );
    		int returnVal = fc.showOpenDialog(fc);
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
        		this.file = fc.getSelectedFile();
        		try {
        			this.open(this.file);
        		}
        		catch (IOException e1) {
					String message = String.format( "File could not be opened ("+this.file+")");
					JOptionPane.showMessageDialog( null, message );
				}
    		} 
    		else 
    		{
    		    log.debug("Open command cancelled by user.");
    		}
       }  
   }
}

/**
 * @author lukas
 *
 */
class StdNomTableMenu extends JMenuBar implements ActionListener {
	/**
	 * 
	 */
	// Ask AWT which menu modifier we should be using.
	final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	StdNom stdNom;
	Bats main;
	
	/**
	 * @param stdNom
	 */
	public StdNomTableMenu(StdNom stdNom) {
		this.stdNom = stdNom;
		
	    JMenu menu;
	    JMenuItem menuItem;
	
	    //Build the first menu.
	    menu = new JMenu("File");
	    menu.setMnemonic(KeyEvent.VK_F);
	    menu.getAccessibleContext().setAccessibleDescription("File");
	    this.add(menu);

	    menuItem = new JMenuItem("Open settings...");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Opens...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);		
	
	    menuItem = new JMenuItem("Save...");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save stdNom...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

	    menuItem = new JMenuItem("Save");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save stdNom");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

	    menuItem = new JMenuItem("Add new standard");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Add new standard");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);		
	}

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        stdNom.action(source.getText());
    } 
}

/**
 * @author lukas
 *
 */
class StdNomTableTBar extends JToolBar implements ActionListener
{
	StdNom stdNom;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @param stdNom 
	 * 
	 */
	public StdNomTableTBar(StdNom stdNom) { 
		this.stdNom = stdNom;
		Insets margins = new Insets(2, 2, 2, 2);

		ToolBarButton button = new ToolBarButton(Setting.batDir+"/icon/open20.png");
		button.setToolTipText("Open...");
		button.setMargin(margins);
		button.addActionListener(this);
		add(button);
		addSeparator();
		
		ToolBarButton button2 = new ToolBarButton(Setting.batDir+"/icon/save20.png");
		button2.setToolTipText("Save");
		button2.setMargin(margins);
		button2.addActionListener(this);
		add(button2);
		addSeparator();
		
		ToolBarButton button3 = new ToolBarButton(Setting.batDir+"/icon/save-as20.png");
		button3.setToolTipText("Save...");
		button3.setMargin(margins);
		button3.addActionListener(this);
		add(button3);
		addSeparator();
		
		ToolBarButton button4 = new ToolBarButton(Setting.batDir+"/icon/list-add.png");
		button4.setToolTipText("Add new standard");
		button4.setMargin(margins);
		button4.addActionListener(this);
		add(button4);
		addSeparator();
	}
	
	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
        ToolBarButton source = (ToolBarButton)(e.getSource());
        stdNom.action(source.getToolTipText());
	}
}
       
/**
 * @author lukas
 *
 */
class StdData {
	final static Logger log = Logger.getLogger(StdData.class);
	
	/**
	 * Name of standard
	 */
	public String name = new String();
	/**
	 * delta 13C
	 */
	public Double delta; 		
	/**
	 * delta 13C sigma
	 */
	public Double delta_sig;
	/**
	 * delta 13C nominal
	 */
	public Double delta_nom;
	/**
	 * 
	 */
	public Double ba; 		
	/**
	 * 
	 */
	public Double ba_sig;
	/**
	 * 
	 */
	public Double ba_nom;
	/**
	 * 
	 */
	public Double pmC;
	/**
	 * 
	 */
	public Double pmC_sig;
	/**
	 * 
	 */
	public Double ra;		
	/**
	 * 
	 */
	public Double ra_sig;		
	/**
	 * 
	 */
	public Boolean active;
	
	StdData(){
		name = "";
		active = true;
	}
	
	/**
	 * @param field 
	 * @return value of Object
	 */
	public Object get(String field){
		Object returnVal = null;
		if (field.equals("name")) { returnVal = name; }
		else if (field.equals("ba")) { returnVal = ba; }
		else if (field.equals("ba_sig")) { returnVal = ba_sig; }
		else if (field.equals("ba_nom")) { returnVal = ba_nom; }
		else if (field.equals("delta")) { returnVal = delta; }
		else if (field.equals("delta_sig")) { returnVal = delta_sig; }
		else if (field.equals("delta_nom")) { returnVal = delta_nom; }
		else if (field.equals("pmC")) { returnVal = pmC; }
		else if (field.equals("pmC_sig")) { returnVal = pmC_sig; }
		else if (field.equals("ra")) { returnVal = ra; }
		else if (field.equals("ra_sig")) { returnVal = ra_sig; }
		else if (field.equals("active")) { returnVal = active; }
		return returnVal;
	}
	
	/**
	 * @param field
	 * @return value
	 */
	public Object getValue(String field){
		Object returnVal;
		returnVal = this.get(field);
		if (returnVal==""){returnVal="-";} 
		else if (returnVal==null){returnVal="null";}
		return returnVal;
	}
	
	/**
	 * @param field
	 * @return value
	 */
	public String getXML(String field){
		Object returnVal;
		returnVal = this.get(field);
		if (returnVal==null) {
			returnVal="";
		}
		if (returnVal.getClass()==String.class) {
			return (String)returnVal;			
		} else if (returnVal.getClass()==Double.class) {
			return Double.toString((Double)returnVal);			
		} else if (returnVal.getClass()==Boolean.class) {
			return Boolean.toString((Boolean)returnVal);			
		} else { 
			return "";
		}		
	}
	
	/**
	 * @param value
	 * @param field
	 */
	public void setValues( String[] value,  String[] field ){
		int i;
		for (i=0;i<value.length;i++){
			this.setValue(value[i],field[i]);
		}
	}
	
	/**
	 * @param value
	 * @param field 
	 */
	public void setValue( String value, String field ) {
		try {
			if (field.equals("name")) {name = String.valueOf(value);}
			else if (field.equals("ba")) {ba = Double.valueOf(value);}
			else if (field.equals("ba_sig")) {ba_sig = Double.valueOf(value);}
			else if (field.equals("ba_nom")) {ba_nom = Double.valueOf(value);}
			else if (field.equals("delta")) {delta = Double.valueOf(value);}
			else if (field.equals("delta_sig")) {delta_sig = Double.valueOf(value);}
			else if (field.equals("delta_nom")) {delta_nom = Double.valueOf(value);}
			else if (field.equals("pmC")) {pmC = Double.valueOf(value);}
			else if (field.equals("pmC_sig")) {pmC_sig = Double.valueOf(value);}
			else if (field.equals("ra")) {ra = Double.valueOf(value);}
			else if (field.equals("ra_sig")) {ra_sig = Double.valueOf(value);}
			else if (field.equals("active")) {active = Boolean.valueOf(value);}
			else {
				String message = String.format("Standard readin error: Wrong field -> " + field + " - " + value);
				log.error(message);
			}
		}
		catch (NumberFormatException e) {log.error("NumberFormatException in SetValue! (field "+field+")");}
	}

	/**
	 * @param value
	 * @param field
	 */
	public void set( Object value, String field ) {
		try {
			if (field.equals("name")) {name = (String)value;}
			else if (field.equals("ba")) {ba = (Double)value;}
			else if (field.equals("ba_sig")) {ba_sig = (Double)value;}
			else if (field.equals("ba_nom")) {ba_nom = (Double)value;}
			else if (field.equals("delta")) {delta = (Double)value;}
			else if (field.equals("delta_sig")) {delta_sig = (Double)value;}
			else if (field.equals("delta_nom")) {delta_nom = (Double)value;}
			else if (field.equals("pmC")) {pmC = (Double)value;}
			else if (field.equals("pmC_sig")) {pmC_sig = (Double)value;}
			else if (field.equals("ra")) {ra = (Double)value;}
			else if (field.equals("ra_sig")) {ra_sig = (Double)value;}
			else if (field.equals("active")) {active = (Boolean)value;}
			else {
				String message = String.format("Standard readin error: Wrong field -> " + field + " - " + value);
	//			JOptionPane.showMessageDialog( null, message );
				log.error(message);
			}
		}
		catch (NumberFormatException e) {log.error("NumberFormatException in Set! (field "+field+")");}
	}
/**
	 * @param value
	 * @param field
	 */
	public void setValues( ArrayList<String> value, String[] field){
		int i;
		for (i=0;i<value.size();i++){
			this.setValue(value.get(i),field[i]);
		}
	}
}
