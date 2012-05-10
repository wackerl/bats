import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import org.jdom.CDATA;
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
public class FormatT {
	final static Logger log = Logger.getLogger(FormatT.class);

	String isotope;
	File file;
	Bats main;
	
	/**
	 * 
	 */
	public ArrayList<String> colName = new ArrayList<String>();	
	/**
	 * 
	 */
	public ArrayList<Integer> colWidth = new ArrayList<Integer>();	
	/**
	 * 
	 */
	public ArrayList<Double> colMulti = new ArrayList<Double>();
	/**
	 * 
	 */
	public ArrayList<String> colFormat = new ArrayList<String>();	
	/**
	 * 
	 */
	public ArrayList<String> colValName = new ArrayList<String>();
	/**
	 * 
	 */
	public ArrayList<String> colValue = new ArrayList<String>();
	/**
	 * 
	 */
	public ArrayList<Boolean> colEdit = new ArrayList<Boolean>();
	/**
	 * 
	 */
	public ArrayList<Boolean> colChi = new ArrayList<Boolean>();
	/**
	 * 
	 */
	public ArrayList<Double> colLimitH = new ArrayList<Double>();
	/**
	 * 
	 */
	public ArrayList<Double> colLimitL = new ArrayList<Double>();
	
	private FormatTable formatPanel;
	String name;
	
	FormatT(String name, Bats main) {
		this.main = main;
		this.name = name;

		this.initList();

		try {
			this.file = new File(Setting.getString("/bat/isotope/file/format/"+name));
			openXML(file);
			log.debug("Initialised format file: "+file.toString());
		} catch (Exception e){			
			if (name!=null){
				String fileName = Setting.batDir+"/"+Setting.isotope+"/table/"+name+".xml";
				this.file = new File(fileName);
				try{
					openXML(file);
					Setting.getElement("/bat/isotope/file/format/"+name).setText(file.toString());
					log.debug("Default file opend: "+file+" ("+colName.size()+" col)");
				} catch (Exception ee) {
					try{
						log.debug("Could not open file "+file);
						fileName = Setting.batDir+"/"+Setting.isotope+"/table/all.xml";
						this.file = new File(fileName);
						openXML(file);
						log.debug(Setting.getElement("/bat/isotope/file/format/"+name));						
						log.debug(file.toString());						
//						Setting.getElement("/bat/isotope/file/format/"+name).setText(file.toString());
//						log.debug("Default file opend: "+file);
					} catch (IOException eee) {
						String message = String.format( "<html>File could not be opend: <br>"+file+"</html>");
						JOptionPane.showMessageDialog( null, message );
						log.error("File could not be opend: "+file);
						log.error(ee.toString());
					}
					catch (JDOMException eee) {
						log.error("Setting file but could not be read!");
					}
				}
			}
		}
	}
	
	/**
	 * @param file
	 * Save settings to file
	 */
	public void saveXML(File file) {
		Element root = new Element(name);
		root.setAttribute("date",new Date().toString());
	    for (int i=0; i<colName.size();i++) {
	    	Element col = new Element("column");
	    	col.setAttribute("Nr", String.valueOf(i));
	    	root.addContent(col);
	    	Element element = new Element("name");
	    	element.addContent(new CDATA(colName.get(i)));
	    	col.addContent(element);
	    	element = new Element("value");
	    	element.addContent(colValue.get(i));
	    	col.addContent(element);
	    	element = new Element("val-name");
	    	element.addContent(colValName.get(i));
	    	col.addContent(element);
	    	element = new Element("width");
	    	element.addContent(colWidth.get(i).toString());
	    	col.addContent(element);
	    	element = new Element("format");
	    	element.addContent(colFormat.get(i));
	    	col.addContent(element);
	    	element = new Element("multi");
	    	element.addContent(colMulti.get(i).toString());
	    	col.addContent(element);
	    	element = new Element("edit");
	    	element.addContent(colEdit.get(i).toString());
	    	col.addContent(element);
	    	element = new Element("chi");
	    	element.addContent(colChi.get(i).toString());
	    	col.addContent(element);
	    	element = new Element("high-limit");
	    	element.addContent(colLimitH.get(i).toString());
	    	col.addContent(element);
	    	element = new Element("low-limit");
	    	element.addContent(colLimitL.get(i).toString());
	    	col.addContent(element);
	    }
	    Document doc = new Document();
	    doc.setRootElement(root);
	    Format format= Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
		XMLOutputter out = new XMLOutputter(format);
		try {
			FileOutputStream fs = new FileOutputStream(file); 
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			out.output(doc,writer);
			fs.close();
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
	 * @throws JDOMException 
	 */
	@SuppressWarnings("unchecked")
	public void openXML(File fileName) throws IOException, FileNotFoundException, JDOMException { 
	    Document doc;
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(fileName);

		Element root = doc.getRootElement();
		List<Element> colList = root.getChildren();
    	colName=new ArrayList<String>();
	    colValue=new ArrayList<String>();
	    colValName=new ArrayList<String>();
	    colWidth=new ArrayList<Integer>();
	    colFormat=new ArrayList<String>();
	    colMulti=new ArrayList<Double>();
	    colEdit=new ArrayList<Boolean>();
	    colLimitL=new ArrayList<Double>();
	    colLimitH=new ArrayList<Double>();
	    colChi=new ArrayList<Boolean>();
		for (int i=0; i<colList.size(); i++) {
			try {
			    colName.add(colList.get(i).getChild("name").getText());
			    colValue.add(colList.get(i).getChild("value").getText());
			    colWidth.add(Integer.valueOf(colList.get(i).getChild("width").getText()));
			    colFormat.add(colList.get(i).getChild("format").getText());
			    colMulti.add(Double.valueOf(colList.get(i).getChild("multi").getText()));
			    colEdit.add(Boolean.valueOf(colList.get(i).getChild("edit").getText()));
			} catch (NumberFormatException e) {
				log.error("File improperly formed: edit in "+file+"");
			} catch (NullPointerException e) {
				log.error("NulPointerException in XML read-in (col "+(i)+").");
			}
		}
		try {
			for (int i=0; i<colList.size(); i++) {
			    colValName.add(colList.get(i).getChild("val-name").getText());
			}
		} catch (NullPointerException e) {
				log.info("old format file: "+file);
				colValName.addAll(colValue);
		}
		try {
			for (int i=0; i<colList.size(); i++) {
			    colChi.add(Boolean.valueOf(colList.get(i).getChild("chi").getText()));
			}
		} catch (NullPointerException e) {
			log.info("old format file: "+file);
			for (int i=0; i<colList.size(); i++) {
				colChi.add(false);
			}
		}
		try {
			for (int i=0; i<colList.size(); i++) {
				try {
					colLimitH.add(Double.valueOf(colList.get(i).getChild("high-limit").getText()));
				} catch (NumberFormatException e) {
					colLimitH.add(0.0);
				}
			} 
		} catch (NullPointerException e) {
			log.info("old format file: "+file);
			colLimitH=new ArrayList<Double>() ;
			for (int i=0; i<colList.size(); i++) {
				colLimitH.add(0.0);
			}
		} 
		try {
			for (int i=0; i<colList.size(); i++) {
				try {
					colLimitL.add(Double.valueOf(colList.get(i).getChild("low-limit").getText()));
				} catch (NumberFormatException e) {
					colLimitL.add(0.0);
				}
			} 
		} catch (NullPointerException e) {
			log.info("old format file: "+file);
			colLimitL=new ArrayList<Double>();
			for (int i=0; i<colList.size(); i++) {
				colLimitL.add(0.0);
			}
		} 
    }
	
	/**
	 * show table settings
	 */
	public void showSettings(){	
	    //Create and set up the window.
	    JFrame.setDefaultLookAndFeelDecorated(true);
	    JFrame frame = new JFrame("Setting of table ("+name+")");
	    JMenuBar menuBar = new FormatMenuBar(this);
	    frame.setJMenuBar(menuBar);
	    formatPanel = new FormatTable(this);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.addWindowListener(formatPanel);
	    frame.getContentPane().add(formatPanel,BorderLayout.CENTER);
		FormatTableTBar toolBar = new FormatTableTBar(this);
		frame.getContentPane().add(toolBar,BorderLayout.NORTH);

	    //Display the window.
	    frame.pack();
	    frame.setVisible(true);
	}	
	
	/**
	 * @param posit
	 */
	public void insert(int posit){
		log.debug("Dublicate pos number: "+(posit+1));
		colName.add(posit,colName.get(posit));
		colValue.add(posit,colValue.get(posit));
		colValName.add(posit,colValName.get(posit));
		colWidth.add(posit,colWidth.get(posit));
		colFormat.add(posit,colFormat.get(posit));
		colMulti.add(posit,colMulti.get(posit));
		colEdit.add(posit,colEdit.get(posit));
		colChi.add(posit,colChi.get(posit));
		colLimitH.add(posit,colLimitH.get(posit));
		colLimitL.add(posit,colLimitL.get(posit));
	}
	
	/**
	 * @param posit
	 */
	public void remove(int posit) {
		log.debug("Remove pos number: "+(posit+1));
		colName.remove(posit);
		colValue.remove(posit);
		colValName.remove(posit);
		colWidth.remove(posit);
		colFormat.remove(posit);
		colMulti.remove(posit);
		colEdit.remove(posit);
		colChi.remove(posit);
		colLimitH.remove(posit);
		colLimitL.remove(posit);
	}
	
	/**
	 * @param posit
	 * @param pos 
	 */
	public void shift(int posit, int pos) {
		log.debug("Shift "+posit+" "+pos+" positions: ");
		if ((posit+pos)<0) { pos=-posit; }
		if (pos>0) { pos+=1; }
		if ((posit+pos)>colName.size()) { pos=colName.size()-posit; }
		colName.add(posit+pos,colName.get(posit));
		colValue.add(posit+pos,colValue.get(posit));
		colValName.add(posit+pos,colValName.get(posit));
		colWidth.add(posit+pos,colWidth.get(posit));
		colFormat.add(posit+pos,colFormat.get(posit));
		colMulti.add(posit+pos,colMulti.get(posit));
		colEdit.add(posit+pos,colEdit.get(posit));
		colChi.add(posit+pos,colChi.get(posit));
		colLimitH.add(posit+pos,colLimitH.get(posit));
		colLimitL.add(posit+pos,colLimitL.get(posit));
		if (pos<0) { posit+=1; }
		remove(posit);
	}
	
	private void initList() {
		colName.add("run");		
		colWidth.add(60);		
		colFormat.add("");		
		colValue.add("run");		
		colValName.add("Run");		
		colMulti.add(1.0);		
		colEdit.add(false);
		colChi.add(false);
		colLimitH.add(null);
		colLimitL.add(0.1);
	}
		
	/**
	 * @param text 
	 * 
	 */
	public void action(String text) {
       if (text == "Save...") {
    	    JFileChooser fc = new JFileChooser();
    	    fc.setSelectedFile( this.file );
    	    fc.setFileFilter( new FileFilter() {
    	    	      @Override public boolean accept( File f ) {
    	    	        return f.isDirectory() ||
    	    	          f.getName().toLowerCase().endsWith( ".xml" );
    	    	      }
    	    	      @Override public String getDescription() {
    	    	        return "Bats format file";
    	    	      }
    	    	    } );
    		
    		int returnVal = fc.showSaveDialog(null);	    		
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
    			if (fc.getSelectedFile().isFile()) {
    				if(fc.getSelectedFile().exists()) {
	    				int overwrite = JOptionPane.showConfirmDialog(null, "<html>Do your want to overwrite this file?<br>"+fc.getSelectedFile()+"</html>", "Overwrite?", JOptionPane.YES_NO_OPTION);
	    				if(overwrite==0) {
		            		this.file = fc.getSelectedFile();
		            		this.saveXML(this.file);
							Setting.getElement("/bat/isotope/file/format/"+name).setText(file.toString());
		            		log.debug("Overwritten file "+file);
	    				} else {
	       					String message = String.format( "Did not colSaveBat!");
	    					JOptionPane.showMessageDialog( null, message );
	    					log.debug("Save (overwrite) canceled by user.");
	    				}
    				}
				}
    			else {
            		this.file = fc.getSelectedFile();
            		this.saveXML(this.file);
					Setting.getElement("/bat/isotope/file/format/"+name).setText(file.toString());
    				log.debug("File "+file+" selected to colSaveBat format settings");
    			}
    		} else {
				String message = String.format( "Did not colSaveBat! (not approved!)");
				JOptionPane.showMessageDialog( null, message );
				log.debug("Did not colSaveBat! (not approved!)");
    	    }
       } else if (text == "Save") {
			log.debug("Save format settings to file "+file);
			String old = file.toString();
			if (new File(old).renameTo(new File(old+".old"))) { 
				log.debug("old file renamed");
			}
			else {
				log.debug("Old file could not be renamed and will be overwritten!");
			}
			this.saveXML(file);
       } else if (text == "Open...") {
    		//Create a file chooser
    	    JFileChooser fc = new JFileChooser();
    	    fc.setSelectedFile(file);
    	    fc.setFileFilter( new FileFilter() {
    	    	      @Override public boolean accept( File f ) {
    	    	    	  return f.isDirectory() ||
    	    	          f.getName().toLowerCase().endsWith( ".xml" );
    	    	      }
    	    	      @Override public String getDescription() {
    	    	    	  return "AMS file";
    	    	      }
    	    		} );
    		int returnVal = fc.showOpenDialog(fc);
    		if (returnVal == JFileChooser.APPROVE_OPTION) {
        		this.file = fc.getSelectedFile();
        		try {
        			this.openXML(this.file);
					Setting.getElement("/bat/isotope/file/format/"+name).setText(file.toString());
        			log.debug("Opened file: "+file);
        			formatPanel.updateTable(this);
        		} catch (IOException e1) {
					String message = String.format( "File could not be opened ("+this.file+")");
					JOptionPane.showMessageDialog( null, message );
        			log.debug("File could not be opened ("+this.file+")");
        		} catch(JDOMException e) {
        			log.error("Setting file but could not be read!");
        		}
    		} else {
    		    log.debug("Open command cancelled by user.");
    		}
        }        
	    else if (text == "Add column"){
	    	int i = (colName.size()-1);
//			i = JOptionPane.showInputDialog(i);
	    	this.insert(i);
			formatPanel.updateTable(this);
	    } else if (text == "update") {
	    	main.tableUpdate();
	    } else {
	    	log.debug("Didn't do anything! "+text);
	    }        
	}
}

/**
 * @author lukas
 *
 */
class ToolBarButton extends JButton {
	private static final Insets margins =new Insets(2, 2, 2, 2);
	
	  /**
	 * @param icon
	 */
	public ToolBarButton(Icon icon) {
	    super(icon);
	    setMargin(margins);
	    setRolloverEnabled(false);
	    setContentAreaFilled(false);
	    setBorderPainted(false);
	    setFocusPainted(true);
	    setVerticalTextPosition(BOTTOM);
	    setHorizontalTextPosition(CENTER);
	    setBorder(BorderFactory.createRaisedBevelBorder());
	}
	
	/**
	* @param imageFile
	*/
	public ToolBarButton(String imageFile) {
	    this(new ImageIcon(imageFile));
	}
	
	 /**
	* @param imageFile
	* @param text
	*/
	public ToolBarButton(String imageFile, String text) {
	    this(new ImageIcon(imageFile));
	    setText(text);
	    setActionCommand(text);
	}
}

/**
 * @author lukas
 *
 */
class FormatTableTBar extends JToolBar implements ActionListener {
	FormatT format;

	/**
	 * @param format 
	 * 
	 */
	public FormatTableTBar(FormatT format) { 
		this.format = format;
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
		
		ToolBarButton button3 = new ToolBarButton(Setting.batDir+"/icon/save_as20.png");
		button3.setToolTipText("Save...");
		button3.setMargin(margins);
		button3.addActionListener(this);
		add(button3);
		addSeparator();
		
		ToolBarButton button4 = new ToolBarButton(Setting.batDir+"/icon/list-add.png");
		button4.setToolTipText("Add column");
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
        format.action(source.getToolTipText());
	}
}


/**
 * @author lukas
 *
 */
class FormatMenuBar extends JMenuBar implements ActionListener {
	FormatT format;
	// Ask AWT which menu modifier we should be using.
	final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	/**
	 * @param format
	 */
	public FormatMenuBar(FormatT format) {
		this.format = format;
		
	    JMenu menu;
	    JMenuItem menuItem;
	
	    //Build the first menu.
	    menu = new JMenu("File");
	    menu.setMnemonic(KeyEvent.VK_F);
	    menu.getAccessibleContext().setAccessibleDescription("File");
	    this.add(menu);

	    menuItem = new JMenuItem("Open...");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Opens settings...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);		
	
	    menuItem = new JMenuItem("Save...");
	    menuItem.setMnemonic(KeyEvent.VK_X);
	    menuItem.getAccessibleContext().setAccessibleDescription("Save format...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

	    menuItem = new JMenuItem("Save");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save format");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

	    menuItem = new JMenuItem("Add column");
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Add column");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);		
	}

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        format.action(source.getText());
    } 
}

