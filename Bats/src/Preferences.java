import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;
import org.apache.log4j.Logger;
import org.jdom.DataConversionException;

/**
 * @author lukas
 *
 */
public class Preferences extends JDialog implements FocusListener, ActionListener, WindowListener {
	final static Logger log = Logger.getLogger(Preferences.class);
	
	ArrayList<JComponent> comp;
	Bats main;
	PanelDb panel3;

    /**
     * @param main 
     * @param selected tab
     * 
     */
    public Preferences(Bats main, int selected) {
    	    	
    	this.main = main;
    	
        this.addWindowListener(this);
		this.setModal(true);

    	this.setPreferredSize(new Dimension(400,900));
    	this.setTitle("Preferences");
    	JTabbedPane tabbedPane = new JTabbedPane();
    	tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setAutoscrolls(true);
        int n=0;

        PanelCalc panel1 = new PanelCalc(this);
    	String name = "calc";
        tabbedPane.addTab(name, null, panel1, name);
        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_1);

        PanelFile panel2 = new PanelFile(this);
    	name = "file";
        tabbedPane.addTab(name, null, panel2, name);
        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_2);

	    Boolean active;
        try {
			active = Setting.getElement("/bat/isotope/db").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {       
	        panel3 = new PanelDb(this);
	    	name = "DB";
	        tabbedPane.addTab(name, null, panel3, name);
	        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_3);
		}

        PanelUI panel5 = new PanelUI(this);
    	name = "UI";
        tabbedPane.addTab(name, null, panel5, name);
        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_5);

	    if (Setting.isotope.contains("C14")) {
	    	PanelCalib panel6 = new PanelCalib(this);
	    	name = "calib";
	        tabbedPane.addTab(name, null, panel6, name);
	        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_6);
	    }

        PanelColor panel7 = new PanelColor(this);
    	name = "colors";
        tabbedPane.addTab(name, null, panel7, name);
        tabbedPane.setMnemonicAt(n++, KeyEvent.VK_7);

        tabbedPane.setSelectedIndex(selected);
        this.setDefaultLookAndFeelDecorated(true);	
    	this.main= main;
        this.setContentPane(tabbedPane);
        this.pack();
        this.setVisible(true);
    }

	public void actionPerformed(ActionEvent e) {
		String actCom = e.getActionCommand();
		String value = null;
		log.debug("Action performed "+actCom);
		if (e.getSource().getClass().equals(JFormattedTextField.class)) {
			this.setTextValue((JFormattedTextField) e.getSource());
		} else if (e.getSource().getClass().equals(JPasswordField.class)) {
			JPasswordField tf = (JPasswordField) e.getSource();
			tf.transferFocus();
			value = String.valueOf(((JPasswordField)e.getSource()).getPassword());
	        Setting.getElement(actCom).setText(value);		
		} else if (e.getSource().getClass().equals(JComboBox.class)) {
			value = (String) Setting.selectIso.get(((JComboBox)e.getSource()).getSelectedIndex());
	        Setting.getElement(actCom).setText(value);		
		} else if (e.getSource().getClass().equals(JButton.class)) {
		    Color newColor = JColorChooser.showDialog(
			    this, "color of "+((JButton)e.getSource()).getName(),((JButton)e.getSource()).getBackground());
		    try {
				value = Integer.toString(newColor.getRGB());
				Setting.getElement(actCom).setText(value);
				((JButton)e.getSource()).setBackground(newColor);
		    } catch(NullPointerException ex) { log.debug("no new color set!");}
		} else if (e.getSource().getClass().equals(JCheckBox.class)) {
			value = Boolean.toString(((JCheckBox)e.getSource()).isSelected());
			if (actCom.equals("/bat/isotope/db/ora/cycle")||actCom.equals("/bat/isotope/db/sql-import/cycle")||actCom.equals("/bat/isotope/db/sql/cycle")) {
	        	Setting.getElement(actCom).getAttribute("name").setValue(value);
			} else {
	        	Setting.getElement(actCom).setText(value);
			}			
		} else if (e.getSource().getClass().equals(JRadioButton.class)) {
			Boolean bool = ((JRadioButton)e.getSource()).isSelected();
			value=Boolean.toString(bool);
			if (actCom.equals("MySQL NT")) {				
			    Setting.getElement("/bat/isotope/db/sql").getAttribute("active").setValue(Boolean.toString(bool));
			    Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").setValue(Boolean.toString(!bool));
			    Setting.getElement("/bat/isotope/db/ora").getAttribute("active").setValue(Boolean.toString(!bool));
			    main.db = new IODb(main);
			    this.panel3.dbPanel();
			} else if (actCom.equals("MySQL Import")) {
			    Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").setValue(Boolean.toString(bool));
			    Setting.getElement("/bat/isotope/db/ora").getAttribute("active").setValue(Boolean.toString(!bool));
			    Setting.getElement("/bat/isotope/db/sql").getAttribute("active").setValue(Boolean.toString(!bool));
			    main.db = new DbMySqlConnect(main);
			    this.panel3.dbPanel();
			} else if (actCom.equals("Oracle")) {
			    Setting.getElement("/bat/isotope/db/ora").getAttribute("active").setValue(Boolean.toString(bool));
			    Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").setValue(Boolean.toString(!bool));
			    Setting.getElement("/bat/isotope/db/sql").getAttribute("active").setValue(Boolean.toString(!bool));
			    main.db = new DbOraConnect(main);
			    this.panel3.dbPanel();
			    this.panel3.repaint();
			    this.panel3.setOpaque(true);
			} 

		}
    	log.debug("Set "+actCom+" to "+value);
	}
	
	public void focusLost(FocusEvent e) {
		if (e.getSource().getClass().equals(JFormattedTextField.class)) {
			this.setTextValue((JFormattedTextField) e.getSource());
		}		
	}
	
	private void setTextValue(JFormattedTextField tf) {
		String value = null;
		String actCom = tf.getToolTipText();
		try {
			tf.commitEdit(); 
			tf.transferFocus();
			value = tf.getText();
	        Setting.getElement(actCom).setText(value);		
			log.debug("Focus action performed "+actCom+" / value: "+value);
		} catch (ParseException e1) {
			log.debug("parse exception for JFormatedTextField: "+tf);
		}
	}

	/**
     * Called when one of the fields gets the focus so that
     * we can selectCol the focused field.
     * @param e 
     */
    public void focusGained(FocusEvent e) {
        Component c = e.getComponent();
        if (c instanceof JFormattedTextField) {
            selectItLater(c);
        } else if (c instanceof JTextField) {
            ((JTextField)c).selectAll();
        }
    }

    //Workaround for formatted text field focus side effects.
    protected void selectItLater(Component c) {
        if (c instanceof JFormattedTextField) {
            final JFormattedTextField ftf = (JFormattedTextField)c;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ftf.selectAll();
                }
            });
        }
    }
    
    
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		try {
	    	if (Setting.getElement("/bat/isotope/db").getAttribute("active").getBooleanValue()) {
		    		main.db.logout();
	    	}
		} catch (DataConversionException ee) { Setting.db=null; log.warn("Could not disconnect from DB!"); }
		main.dataRecalc();
		log.debug("Closed Preferences and made recalculation");
	}
	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}



class PanelCalc extends JPanel {
	final static Logger log = Logger.getLogger(Preferences.class);

	ArrayList<JComponent> comp;
	Bats main;

    /**
     * @param pref 
     * 
     */
    public PanelCalc(Preferences pref) {
    	JPanel cp;
     	comp= new ArrayList<JComponent>();
        ArrayList<String> des = new ArrayList<String>();
     	String path;
     	JLabel label;
     	JCheckBox check;
     	JFormattedTextField ftf;
	    NumberFormatter nf;
	    
		des.add("Isobar");
	    cp = new JPanel(new GridLayout(0,2));
	    label = new JLabel("Isobar: ");
	    cp.add(label);
		JComboBox comboBox = new JComboBox();
		path="/bat/isotope/calc/bg/isobar";
		for (int i=0; i<Setting.selectIsoN.size(); i++) {
			comboBox.addItem(Setting.selectIsoN.get(i));
		}
		comboBox.setToolTipText("Set isobar used for isobar correction (eg. iso, g1 or g2)");
		comboBox.setSelectedItem(Setting.getString(path));
		comboBox.setActionCommand(path);
		comboBox.addActionListener(pref);
        cp.add(comboBox);
		
	    label = new JLabel("Default isobar factor");
	    label.setToolTipText("Isobar correction factor (default value)");
	    cp.add(label);
	    path="/bat/isotope/calc/bg/factor";
	    nf = new NumberFormatter(new DecimalFormat("0.000E00"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

	    label = new JLabel("Default isobar unc.");
	    label.setToolTipText("Uncertainty of the isobar");
	    cp.add(label);
	    path="/bat/isotope/calc/bg/error";
	    ftf = new JFormattedTextField(
	    		new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		comp.add(cp);

	    des.add("Sample scatter");
	    cp = new JPanel(new GridLayout(0,2));
	    label = new JLabel("Relative error");
	    label.setToolTipText("This error is added to all samples");
	    cp.add(label);
	    path="/bat/isotope/calc/scatter";
	    nf = new NumberFormatter(new DecimalFormat("0.0000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
	    comp.add(cp);
		
	    des.add("Dead time");
	    cp = new JPanel(new GridLayout(0,2));
	    label = new JLabel("Dead time (s)");
	    label.setToolTipText("Dead time for 14C");
	    cp.add(label);
	    path="/bat/isotope/calc/dead_time";
	    nf = new NumberFormatter(new DecimalFormat("0.0000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
	    comp.add(cp);
		
	    cp = new JPanel(new GridLayout(0,2));
	    if (Setting.isotope.contains("C14")) {
		    des.add("Nominal pmC");
		    label = new JLabel("Nominal pmC for std");
	    } else {
	    	des.add("Nominal ratio");
	    }
	    label = new JLabel("Nominal ratio for std");
	    label.setToolTipText("Nominal ratio/pmC of standards (every std is normalised to this value)." +
		" Only used for display of standards.)");
	    cp.add(label);
	    path="/bat/isotope/calc/nominal_ra";
	    nf = new NumberFormatter(new DecimalFormat("0.00000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
	    comp.add(cp);
		
	    des.add("Current");
	    cp = new JPanel(new GridLayout(0,2));
	    label = new JLabel("Offset a (μA)");
		label.setToolTipText("Offset correction of the current measured in cup a.");
	    cp.add(label);
	    path="/bat/isotope/calc/current/a/offset";
	    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
	    label = new JLabel("rel. unc. a");
	    cp.add(label);
	    path="/bat/isotope/calc/current/a/error_rel";
	    nf = new NumberFormatter(new DecimalFormat("0.000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
	    label = new JLabel("abs. unc. a (μA)");
	    cp.add(label);
	    path="/bat/isotope/calc/current/a/error_abs";
	    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

	    label = new JLabel("Offset b (μA)");
		label.setToolTipText("Offset correction of the current measured in cup b.");
	    cp.add(label);
	    path="/bat/isotope/calc/current/b/offset";
	    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
	    label = new JLabel("rel. unc. b");
	    cp.add(label);
	    path="/bat/isotope/calc/current/b/error_rel";
	    nf = new NumberFormatter(new DecimalFormat("0.000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
	    label = new JLabel("abs. unc. b (μA)");
	    cp.add(label);
	    path="/bat/isotope/calc/current/b/error_abs";
	    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

	    if (Setting.isotope.contains("C14")) {
		    label = new JLabel("Offset iso (μA)");
		    cp.add(label);
		    path="/bat/isotope/calc/current/iso/offset";
		    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getDouble(path)));
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		    label = new JLabel("rel. unc. iso");
		    cp.add(label);
		    path="/bat/isotope/calc/current/iso/error_rel";
		    nf = new NumberFormatter(new DecimalFormat("0.000000"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getDouble(path)));
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("abs. unc. iso (μA)");
		    cp.add(label);
		    path="/bat/isotope/calc/current/iso/error_abs";
		    nf = new NumberFormatter(new DecimalFormat("0.000000000"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getDouble(path)));
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	    }
	   
        label = new JLabel("charge state");
		label.setToolTipText("Charge state of the ions measured in the cup (to correct ratios)");
	    cp.add(label);
	    path="/bat/isotope/calc/current/charge";
	    nf = new NumberFormatter(new DecimalFormat("#0"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
        label = new JLabel("weighting");
		label.setToolTipText("Select how standards are weighted for mean (if 2, then weighted by uncertainy of mean incl. sample scatter, else only counting statistics");
	    cp.add(label);
	    path="/bat/isotope/calc/mean";
	    nf = new NumberFormatter(new DecimalFormat("0"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		comp.add(cp);
		
	    cp = new JPanel(new GridLayout(0,2));
	    des.add("Autorecalc");
	    check = new JCheckBox("always recalculate");
        check.setToolTipText("If true, the values are always recalculated immediately after any data changes");
	    path="/bat/general/calc/autocalc";
	    check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
	    check.addActionListener(pref);
	    cp.add(check);
	    comp.add(cp);

	    cp = new JPanel(new GridLayout(0,1));
	    des.add("Uncertainties");
        check = new JCheckBox("Poisson distribution");
        check.setToolTipText("If true, the poisson distribution uncertainties is used (instead of normal distribution)");
        path="/bat/isotope/calc/poisson";
        check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
	    check.addActionListener(pref);
        cp.add(check);
        comp.add(cp);

	    if (Setting.isotope.contains("C14")) {
		    cp = new JPanel(new GridLayout(0,1));
		    des.add("Fractionation");
	        check = new JCheckBox("¹³C/¹²C correction on ¹⁴C/¹²C");
	        check.setToolTipText("If true, the standard ¹⁴C/¹²C are corrected first with ¹³C/¹²C ratios");
	        path="/bat/isotope/calc/fract";
	        check.setSelected(Setting.getBoolean(path));
		    check.setActionCommand(path);
		    check.addActionListener(pref);
	        cp.add(check);
	        comp.add(cp);
        }

	    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }
}


class PanelFile extends JPanel {
	final static Logger log = Logger.getLogger(Preferences.class);

	ArrayList<JComponent> comp;
	Bats main;

    /**
     * @param pref 
     * 
     */
    public PanelFile(Preferences pref) {
    	JPanel cp;
     	comp= new ArrayList<JComponent>();
        ArrayList<String> des = new ArrayList<String>();
     	String path;
     	JLabel label;
     	JCheckBox check;
     	JFormattedTextField ftf;
	    NumberFormatter nf;       
		
	    cp = new JPanel(new GridLayout(0,2));
	    des.add("Save");
	    check = new JCheckBox("autosave std/bl plot");
        check.setToolTipText("set automatic save of blank and standard plots to .png file");
	    path="/bat/general/file/autosave/plot";
	    check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("If selected, the standard and blank plots are saved to a png-file");
	    check.addActionListener(pref);
	    cp.add(check);
	    
	    label = new JLabel("   ");
	    cp.add(label);
	    
        check = new JCheckBox("autosave output file");
        path="/bat/general/file/autosave/result";
        check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("If selected, the output file with the results is saved automatically (.xhtml-file)");
	    check.addActionListener(pref);
        cp.add(check);

        check = new JCheckBox("output without runs");
        check.setToolTipText("Save samples without runs to output file.");
	    path="/bat/general/file/output/sample-only";
	    check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("If selected, the output file is saved without runs");
	    check.addActionListener(pref);
	    cp.add(check);
	    
	    label = new JLabel("page break after run");
		label.setToolTipText("A new header with a page break is inserted after the given number of data lines (after runs)");
	    cp.add(label);
	    path="/bat/general/file/output/page_break/run";
	    nf = new NumberFormatter(new DecimalFormat("##0"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("page break after sample");
		label.setToolTipText("A new header with a page break is inserted after the given number of data lines (after samplea)");
	    cp.add(label);
	    path="/bat/general/file/output/page_break/sample";
	    nf = new NumberFormatter(new DecimalFormat("##0"));
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        check = new JCheckBox("add corr to output");
        path="/bat/general/file/output/corr";
        check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("Adds corrections to output file (at the end)");
	    check.addActionListener(pref);
        cp.add(check);
        
        label = new JLabel(" ");
        cp.add(label);

        check = new JCheckBox("autosave bats file");
        path="/bat/general/file/autosave/bats";
        check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("If selected, the data is regularly saved to a bats file (.bats)");
	    check.addActionListener(pref);
        cp.add(check);
        
        label = new JLabel(" ");
        cp.add(label);

        label = new JLabel("Data path");
		label.setToolTipText("Local path to directory for save data");
	    cp.add(label);
	    path="/bat/isotope/path";
	    ftf = new JFormattedTextField();
	    ftf.setValue(Setting.getString(path));
		((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);


		comp.add(cp);

//	    cp = new JPanel(new GridLayout(0,2));
//	    des.add("Std / blank order");
//	    check = new JCheckBox("order by run");
//        check.setToolTipText("If true, the blanks are ordered by run instead by");
//	    path="/bat/general/table/run_order";
//	    check.setSelected(Setting.getBoolean(path));
//	    check.setActionCommand(path);
//	    check.addActionListener(pref);
//	    cp.add(check);
//	    comp.add(cp);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
//			borderPanel.setPreferredSize(new Dimension(400,50));
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }    
}

class PanelDb extends JPanel {
	final static Logger log = Logger.getLogger(Preferences.class);

	Bats main;
	JPanel cp;
	Preferences pref;
    ArrayList<String> des = new ArrayList<String>();
	ArrayList<JComponent> comp= new ArrayList<JComponent>();

	   /**
     * @param pref 
     * 
     */
    public PanelDb(Preferences pref) {
    	this.pref=pref;
		dbPanel();
    } 
    
    public void dbPanel() {
     	String path;
	    Boolean active=null;
	    JPasswordField pwf;
     	JLabel label;
     	JCheckBox check;
     	JFormattedTextField ftf;
	    NumberFormatter nf;
	    des.clear();
	    comp.clear();
	    this.removeAll();
	    
	    cp = new JPanel(new GridLayout(0,3));
		des.add("Select DB");
	    	    
	    JRadioButton thirdButton = new JRadioButton("MySQL NT");
	    thirdButton.setActionCommand("MySQL NT");
	    path="/bat/isotope/db/sql";
	    try {
	    	thirdButton.setSelected((Setting.getElement(path).getAttribute("active")).getBooleanValue());
		} catch (DataConversionException e) { thirdButton.setSelected(true); log.debug("MySQL setting not found!"); }
	    thirdButton.addActionListener(pref);
	    cp.add(thirdButton);

		JRadioButton secondButton = new JRadioButton("Oracle");
	    secondButton.setActionCommand("Oracle");	    
	    path="/bat/isotope/db/ora";
	    try {
	    	secondButton.setSelected(Setting.getElement("/bat/isotope/db/ora").getAttribute("active").getBooleanValue());
		} catch (DataConversionException e) { secondButton.setSelected(false); log.debug("Oracle settings not found!"); }
	    secondButton.addActionListener(pref);
	    cp.add(secondButton);

	    JRadioButton firstButton = new JRadioButton("MySQL Import");
	    firstButton.setActionCommand("MySQL Import");
	    path="/bat/isotope/db/sql-import";
	    try {
	    	firstButton.setSelected(Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").getBooleanValue());
		} catch (DataConversionException e) { firstButton.setSelected(false); log.debug("MySQL import settings not found!"); }
	    firstButton.addActionListener(pref);
	    cp.add(firstButton);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(firstButton);
	    group.add(secondButton);
	    group.add(thirdButton);

	    check = new JCheckBox("cycle edit");
        path="/bat/isotope/db/cycle_edit";
	    check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
        check.setToolTipText("Enable editing of cycles for a runs. Should only be active, when no cycle import!");
	    check.addActionListener(pref);
        cp.add(check);

	    label = new JLabel("");
	    cp.add(label);

		comp.add(cp);
		
	    try {
			active = Setting.getElement("/bat/isotope/db/ora").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {       
		    cp = new JPanel(new GridLayout(0,2));
		    
		    des.add("Oracle import");
		    check = new JCheckBox("virtual");
		    path="/bat/isotope/db/ora/virtual";
		    check.setSelected(Setting.getBoolean(path));
		    check.setActionCommand(path);
			check.setToolTipText("Import of virtually calculated data at the time of the import (works not jet for all isotopes)");
		    check.addActionListener(pref);
		    cp.add(check);
		    
	        check = new JCheckBox("cycle import");
	        check.setToolTipText("Enable import of cycles instead of runs.");
	        path="/bat/isotope/db/ora/cycle";
	        try {
				check.setSelected(Setting.getElement(path).getAttribute("name").getBooleanValue());
			} catch (DataConversionException e) { check.setSelected(false); log.debug("Cycle setting not found!"); }
		    check.setActionCommand(path);
			check.setToolTipText("Instead of runs cycles can be imported (->cycles taken for one run)");
		    check.addActionListener(pref);
	        cp.add(check);
	
		    label = new JLabel("cycles taken for one run");
			label.setToolTipText("If cycle import is on, then the given number of cycles is taken for one run");
		    cp.add(label);
		    path="/bat/isotope/db/ora/cycle";
		    nf = new NumberFormatter(new DecimalFormat("##0"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getInt(path)));
			ftf.setActionCommand(path);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
	        label = new JLabel("URL");
			label.setToolTipText("URL of database, eg. jdbc:oracle:thin:@sever.ethz.ch:1521:DB");
		    cp.add(label);
		    path="/bat/isotope/db/ora/url";
		    ftf = new JFormattedTextField();

		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Login name");
			label.setToolTipText("User name for DB login");
		    cp.add(label);
		    path="/bat/isotope/db/ora/user";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Password");
			label.setToolTipText("Password for DB login (optional). Set pw with Enter.");
		    cp.add(label);
		    path="/bat/isotope/db/ora/pw";
		    ftf = new JFormattedTextField();
		    pwf = new JPasswordField(Setting.getString(path));
			pwf.setActionCommand(path);
			pwf.addActionListener(pref);
			cp.add(pwf);
	
	        label = new JLabel("Driver");
			label.setToolTipText("Only change if you know what you do! (oracle.jdbc.driver.OracleDriver)");
		    cp.add(label);
		    path="/bat/isotope/db/ora/driver";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			comp.add(cp);
	    }
		
        try {
			active = Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {
		
			cp = new JPanel(new GridLayout(0,2));
		    des.add("MySQL import");
		    label = new JLabel("");
//		    check = new JCheckBox("virtual");
//		    path="/bat/isotope/db/sql-import/virtual";
//		    check.setSelected(Setting.getBoolean(path));
//		    check.setActionCommand(path);
//			check.setToolTipText("Import of virtually calculated data at the time of the import (works not jet for all isotopes)");
//		    check.addActionListener(pref);
		    cp.add(label);
		    
	        check = new JCheckBox("cycle import");
	        check.setToolTipText("Enable import of cycles instead of runs.");
	        path="/bat/isotope/db/sql-import/cycle";
	        try {
				check.setSelected(Setting.getElement(path).getAttribute("name").getBooleanValue());
			} catch (DataConversionException e) { check.setSelected(false); log.debug("Cycle setting not found!"); }
		    check.setActionCommand(path);
			check.setToolTipText("Instead of runs cycles can be imported (->cycles taken for one run)");
		    check.addActionListener(pref);
	        cp.add(check);
	
	        label = new JLabel("cycles taken for one run");
			label.setToolTipText("If cycle import is on, then the given number of cycles is taken for one run");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/cycle";
		    nf = new NumberFormatter(new DecimalFormat("##0"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getInt(path)));
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
	        label = new JLabel("URL");
			label.setToolTipText("URL of database, eg. mysql://host/database");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/url";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Login name");
			label.setToolTipText("User name for DB login");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/user";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Password");
			label.setToolTipText("Password for DB login (otional). Set pw with Enter!");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/pw";
		    pwf = new JPasswordField(Setting.getString(path));
			pwf.setActionCommand(path);
			pwf.addActionListener(pref);
			cp.add(pwf);

	        label = new JLabel("Driver");
			label.setToolTipText("Only change if you know what you do! (com.mysql.jdbc.Driver)");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/driver";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Cycle table");
			label.setToolTipText("Table for cycles");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/cycle_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Run table");
			label.setToolTipText("Table for runs");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/run_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
	        label = new JLabel("Final table");
			label.setToolTipText("Table for corrections");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/final_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		    label = new JLabel("Procedure to enable a cycle");
			label.setToolTipText("Stored procedure for enable/disable of a single cycle");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/cycle_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable cycles");
			label.setToolTipText("Stored procedure for enable/disable of multiple cycles");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/cycles_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable a run");
			label.setToolTipText("Stored procedure for enable/disable of a single run");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/run_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable runs");
			label.setToolTipText("Stored procedure for enable/disable of a single run");
		    cp.add(label);
		    path="/bat/isotope/db/sql-import/runs_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			comp.add(cp);
		}
	 		
	    try {
			active = Setting.getElement("/bat/isotope/db/sql").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) { active = false; log.debug(""); }
		
		if (active == true) {		
		    cp = new JPanel(new GridLayout(0,2));
		    des.add("MySQL NT");
		    
	        label = new JLabel("");
	        check = new JCheckBox("cycle import");
	        check.setToolTipText("Enable import of cycles instead of runs.");
	        path="/bat/isotope/db/sql/cycle";
	        try {
				check.setSelected(Setting.getElement(path).getAttribute("name").getBooleanValue());
			} catch (DataConversionException e) { check.setSelected(false); log.debug("Cycle setting not found!"); }
		    check.setActionCommand(path);
			check.setToolTipText("Instead of runs cycles can be imported (->cycles taken for one run)");
		    check.addActionListener(pref);
	        cp.add(check);
	
		    label = new JLabel("");
		    cp.add(label);
		    
	        label = new JLabel("cycles taken for one run");
			label.setToolTipText("If cycle import is on, then the given number of cycles is taken for one run");
		    cp.add(label);
		    path="/bat/isotope/db/sql/cycle";
		    nf = new NumberFormatter(new DecimalFormat("##0"));
		    nf.setOverwriteMode(false);
		    nf.setAllowsInvalid(true);
		    ftf = new JFormattedTextField(nf);
		    ftf.setValue(new Float(Setting.getInt(path)));
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		    
		    label = new JLabel("URL");
			label.setToolTipText("URL of database, eg. mysql://host/database");
		    cp.add(label);
		    path="/bat/isotope/db/sql/url";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		
		    label = new JLabel("Login name");
			label.setToolTipText("User name for DB login");
		    cp.add(label);
		    path="/bat/isotope/db/sql/user";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		
		    label = new JLabel("Password");
			label.setToolTipText("Password for DB login (otional). Set with Enter!");
		    cp.add(label);
		    path="/bat/isotope/db/sql/pw";
		    pwf = new JPasswordField(Setting.getString(path));
			pwf.setActionCommand(path);
			pwf.addActionListener(pref);
			cp.add(pwf);

		    label = new JLabel("Driver");
			label.setToolTipText("Only change if you know what you do! (com.mysql.jdbc.Driver)");
		    cp.add(label);
		    path="/bat/isotope/db/sql/driver";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		
	        label = new JLabel("Cycle table");
			label.setToolTipText("Table for cycles");
		    cp.add(label);
		    path="/bat/isotope/db/sql/cycle_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
	
		    label = new JLabel("Run table");
			label.setToolTipText("Table for runs");
		    cp.add(label);
		    path="/bat/isotope/db/sql/run_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		
		    label = new JLabel("Sample table");
			label.setToolTipText("Table for samples respectively targets");
		    cp.add(label);
		    path="/bat/isotope/db/sql/target_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
		
		    label = new JLabel("Calc-set table");
			label.setToolTipText("Table for calc-sets");
		    cp.add(label);
		    path="/bat/isotope/db/sql/calcset_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);

			label = new JLabel("Calc-set corr. table");
			label.setToolTipText("Table for corrections of calc-set");
		    cp.add(label);
		    path="/bat/isotope/db/sql/calc_corr_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Calc-set sample table");
			label.setToolTipText("Table for samples of a calc-sets");
		    cp.add(label);
		    path="/bat/isotope/db/sql/calc_sample_t";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable a cycle");
			label.setToolTipText("Stored procedure for enable/disable of a single cycle");
		    cp.add(label);
		    path="/bat/isotope/db/sql/cycle_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable cycles");
			label.setToolTipText("Stored procedure for enable/disable of multiple cycles");
		    cp.add(label);
		    path="/bat/isotope/db/sql/cycles_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable a run");
			label.setToolTipText("Stored procedure for enable/disable of a single run");
		    cp.add(label);
		    path="/bat/isotope/db/sql/run_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			
		    label = new JLabel("Procedure to enable runs");
			label.setToolTipText("Stored procedure for enable/disable of a single run");
		    cp.add(label);
		    path="/bat/isotope/db/sql/runs_enable";
		    ftf = new JFormattedTextField();
		    ftf.setValue(Setting.getString(path));
		    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
//			ftf.setActionCommand(path);
//			ftf.addActionListener(pref);
			ftf.setToolTipText(path);
			ftf.addFocusListener(pref);
			cp.add(ftf);
			comp.add(cp);
    	}

				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
//			borderPanel.setPreferredSize(new Dimension(400,50));
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }
}


class PanelUI extends JPanel implements ActionListener {
	final static Logger log = Logger.getLogger(Preferences.class);

	ArrayList<JComponent> comp;
	Bats main;

    /**
     * @param pref 
     * 
     */
    public PanelUI(Preferences pref) {
    	main = pref.main;
    	JPanel cp;
     	comp= new ArrayList<JComponent>();
        ArrayList<String> des = new ArrayList<String>();
     	String path;
     	JLabel label;
     	JFormattedTextField ftf;
	    NumberFormatter nf;
       
	    cp = new JPanel(new GridLayout(0,2));
	    des.add("Main window");
        label = new JLabel("Window width");
		label.setToolTipText("Window width (pixels)");
	    cp.add(label);
	    path="/bat/general/frame/main/width";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(100);
	    nf.setMaximum(2000);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		
        label = new JLabel("Window height");
	    cp.add(label);
	    path="/bat/general/frame/main/height";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(100);
	    nf.setMaximum(2000);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
	    comp.add(cp);
		
	    cp = new JPanel(new GridBagLayout());
	    cp.setAlignmentX(cp.LEFT_ALIGNMENT);
	    GridBagConstraints c = new GridBagConstraints();
	    des.add("Info panel");
	    
        label = new JLabel("Panel width");
		label.setToolTipText("Info panel width");
	    c.weightx = 1;
	    c.weighty = 1;
	    c.gridwidth = 1;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.anchor = c.LAST_LINE_START;
	    cp.add(label,c);
	    path="/bat/general/frame/split/width";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(100);
	    nf.setMaximum(400);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
	    c.weightx = 1;
	    c.weighty = 1;
	    c.gridwidth = 1;
	    c.gridx = 1;
	    c.gridy = 0;
	    c.anchor = c.LAST_LINE_START;
		cp.add(ftf,c);
		
		JCheckBox check = new JCheckBox("Set info panel to the right");
        path="/bat/general/frame/split/right";
        check.setSelected(Setting.getBoolean(path));
	    check.setActionCommand(path);
		check.setToolTipText("If selected, the info panel is set to the right side, the next time you start the program");
	    check.addActionListener(this);
	    
	    c.weightx = 0.0;
	    c.weighty = 1;
	    c.gridwidth = 2;
	    c.gridx = 0;
	    c.gridy = 1;
	    c.anchor = c.LAST_LINE_START;
	    cp.add(check,c);

		comp.add(cp);	    
			
	    cp = new JPanel(new GridLayout(0,2));
	    des.add("Font");
        label = new JLabel("Font type");
        label.setToolTipText("Font type (eg. Lycida)");
	    cp.add(label);
	    path="/bat/general/font/type";
	    ftf = new JFormattedTextField();
	    ftf.setValue(Setting.getString(path));
	    ((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);	    
	    
        label = new JLabel("Font size p");
		label.setToolTipText("Font size for normal text");
	    cp.add(label);
	    path="/bat/general/font/p";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Font size H1");
		label.setToolTipText("Font size for H1");
	    cp.add(label);
	    path="/bat/general/font/h1";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Font size H2");
		label.setToolTipText("Font size for H2");
	    cp.add(label);
	    path="/bat/general/font/h2";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Font size H3");
		label.setToolTipText("Font size for H3");
	    cp.add(label);
	    path="/bat/general/font/h3";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("XHTML font scaling");
		label.setToolTipText("Fontscaling for xhtml-panel");
	    cp.add(label);
	    path="/bat/general/file/output/fontscaling";
	    nf = new NumberFormatter(new DecimalFormat("0.00"));
	    nf.setMinimum(0.5);
	    nf.setMaximum(3.0);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getDouble(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		comp.add(cp);	    

	    cp = new JPanel(new GridLayout(0,2));
	    des.add("Table");
        label = new JLabel("Header height");
		label.setToolTipText("Header height");
	    cp.add(label);
	    path="/bat/general/table/top";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(60);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Line height");
		label.setToolTipText("Line hight");
	    cp.add(label);
	    path="/bat/general/table/c_hight";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(8);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Horizontal gap");
		label.setToolTipText("Gap between cells");
	    cp.add(label);
	    path="/bat/general/table/gap_x";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(0);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Vertical gap");
		label.setToolTipText("Gap between cells");
	    cp.add(label);
	    path="/bat/general/table/gap_y";
	    nf = new NumberFormatter(new DecimalFormat("###0"));
	    nf.setMinimum(0);
	    nf.setMaximum(40);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

        label = new JLabel("Sort");
		label.setToolTipText("Sort of samples");
	    cp.add(label);
	    path="/bat/general/table/sort";
	    nf = new NumberFormatter(new DecimalFormat("0"));
	    nf.setMinimum(0);
	    nf.setMaximum(4);
	    nf.setOverwriteMode(false);
	    nf.setAllowsInvalid(true);
	    ftf = new JFormattedTextField(nf);
	    ftf.setValue(new Float(Setting.getInt(path)));
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);
		comp.add(cp);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
//			borderPanel.setPreferredSize(new Dimension(400,50));
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }    
	public void actionPerformed(ActionEvent e) {
		String actCom = e.getActionCommand();
		String value = null;
		log.debug("Action performed "+actCom);
		if (e.getSource().getClass().equals(JFormattedTextField.class)) {
			JFormattedTextField tf = (JFormattedTextField) e.getSource();
			try {
				tf.commitEdit(); 
				tf.transferFocus();
				value = ((JFormattedTextField)e.getSource()).getText();
		        Setting.getElement(actCom).setText(value);		
			} catch (ParseException e1) {
				log.debug("parse exception for JFormatedTextField: "+tf);
			}
		} else if (e.getSource().getClass().equals(JComboBox.class)) {
			value = (String) Setting.selectIso.get(((JComboBox)e.getSource()).getSelectedIndex());
	        Setting.getElement(actCom).setText(value);		
		} else if (e.getSource().getClass().equals(JButton.class)) {
		    Color newColor = JColorChooser.showDialog(
			    this, "color of "+((JButton)e.getSource()).getName(),((JButton)e.getSource()).getBackground());
		    try {
				value = Integer.toString(newColor.getRGB());
				Setting.getElement(actCom).setText(value);
				((JButton)e.getSource()).setBackground(newColor);
		    } catch(NullPointerException ex) { log.debug("no new color set!");}
		} else if (e.getSource().getClass().equals(JCheckBox.class)) {
			value = Boolean.toString(((JCheckBox)e.getSource()).isSelected());
	        Setting.getElement(actCom).setText(value);		
		} else if (e.getSource().getClass().equals(JRadioButton.class)) {
			Boolean bool = ((JRadioButton)e.getSource()).isSelected();
			value=Boolean.toString(bool);
	        Setting.getElement(actCom).setText(value);		
		}
    	log.debug("Set "+actCom+" to "+value);
    	log.debug("Re-initialise program");
    	Setting.save();
		main.reInit();
	}
}



class PanelColor extends JPanel {
	final static Logger log = Logger.getLogger(Preferences.class);

	ArrayList<JComponent> comp;

    /**
     * @param pref 
     * 
     */
    public PanelColor(Preferences pref) {
    	JPanel cp;
     	comp= new ArrayList<JComponent>();
        ArrayList<String> des = new ArrayList<String>();
     	String path;
     	JLabel label;
     	JButton button;

	    des.add("Table color");
	    cp = new JPanel(new GridLayout(0,2));
	    label = new JLabel("row odd bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_odd";
	    button = new JButton();
	    button.setName("bg odd");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("row odd fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_odd";
	    button = new JButton();
	    button.setName("fg odd");
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("row even bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_even";
	    button = new JButton();
	    button.setName("bg even");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("row even fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_even";
	    button = new JButton();
	    button.setName("fg even");
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("sample row bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_sample";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("sample row fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_sample";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("std mean row bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_std1";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("std row fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_std1";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("std final row bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_std2";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("std final row fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_std2";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("selected row bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_select";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("selected row fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_select";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("table header bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_head";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("table header fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_head";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("inactive row bg");
	    cp.add(label);
	    path="/bat/general/table/color/bg_false";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("inactive row fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_false";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("limit highlight fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_limit";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("sample line color");
	    cp.add(label);
	    path="/bat/general/table/color/line_sample";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("plot bg");
	    cp.add(label);
	    path="/bat/general/plot/stdBl/bg_color";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("plot fg");
	    cp.add(label);
	    path="/bat/general/plot/stdBl/fg_color";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("plot sigma-bar");
	    cp.add(label);
	    path="/bat/general/plot/stdBl/sig_color";
	    button = new JButton();
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    label = new JLabel("plot false fg");
	    cp.add(label);
	    path="/bat/general/table/color/fg_false";
	    button = new JButton();
	    button.setBackground(Setting.getColor(path));
	    button.setText(label.getText()); 	    button.setForeground(Setting.getColor(path)); 	    	    button.setBorderPainted(true); ; 
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
	    comp.add(cp);
		
	    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
//			borderPanel.setPreferredSize(new Dimension(400,50));
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }
}

class PanelCalib extends JPanel {
	final static Logger log = Logger.getLogger(Preferences.class);

	ArrayList<JComponent> comp;

    /**
     * @param pref 
     * 
     */
    public PanelCalib(Preferences pref) {
    	JPanel cp;
     	JCheckBox check;
     	JFormattedTextField ftf;

	    comp= new ArrayList<JComponent>();
        ArrayList<String> des = new ArrayList<String>();
     	String path;
     	JLabel label;
     	JButton button;

	    des.add("Colors");
	    cp = new JPanel(new GridLayout(0,2));
	    
	    label = new JLabel("calibration curve (1sig)");
	    cp.add(label);
	    path="/bat/calib/graph/curve/sig1/color";
	    button = new JButton();
	    button.setName("cal 1sig");
	    button.setText("cal 1sig");
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);

	    label = new JLabel("calibration curve (2sig)");
	    cp.add(label);
	    path="/bat/calib/graph/curve/sig2/color";
	    button = new JButton();
	    button.setName("cal 2sig");
	    button.setText("cal 2sig");
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);

	    label = new JLabel("radiocarbon age");
	    cp.add(label);
	    path="/bat/calib/graph/age/color";
	    button = new JButton();
	    button.setName("rad age");
	    button.setText("rad age");
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);

	    label = new JLabel("probability all");
	    cp.add(label);
	    path="/bat/calib/graph/proba/all/color";
	    button = new JButton();
	    button.setName("probability all");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("probability 1-sig");
	    cp.add(label);
	    path="/bat/calib/graph/proba/sig1/color";
	    button = new JButton();
	    button.setName("probability 1-sig");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("probability 2-sig");
	    cp.add(label);
	    path="/bat/calib/graph/proba/sig2/color";
	    button = new JButton();
	    button.setName("probability 2-sig");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("font H1");
	    cp.add(label);
	    path="/bat/calib/graph/font/h1/color";
	    button = new JButton();
	    button.setName("H1");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("font P");
	    cp.add(label);
	    path="/bat/calib/graph/font/p/color";
	    button = new JButton();
	    button.setName("P");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    label = new JLabel("probablity ranges");
	    cp.add(label);
	    path="/bat/calib/graph/proba/ranges/color";
	    button = new JButton();
	    button.setName("ranges");
	    button.setText(label.getText());
	    button.setForeground(Setting.getColor(path));
	    button.setBorderPainted(true);
	    button.setBackground(Setting.getColor(path));
	    button.addActionListener(pref);
	    button.setActionCommand(path);
		cp.add(button);
		
	    comp.add(cp);
	    
	    des.add("Oxcal");
	    cp = new JPanel(new GridLayout(0,2));

	    check = new JCheckBox("Oxcal output enable");
        path="/bat/calib/oxcal";
	    try {
			check.setSelected(Setting.getElement(path).getAttribute("active").getBooleanValue());
		} catch (DataConversionException e) {
			log.warn("Could not get boolean from attribute active in "+path);
		}
	    check.setActionCommand(path);
        check.setToolTipText("Enables output to Oxcal in a browser window!");
	    check.addActionListener(pref);
        cp.add(check);
   
        label = new JLabel(" ");
        cp.add(label);

        label = new JLabel("Oxcal path");
		label.setToolTipText("Local path of Oxcal");
	    cp.add(label);
	    path="/bat/calib/oxcal";
	    ftf = new JFormattedTextField();
	    ftf.setValue(Setting.getString(path));
		((DefaultFormatter) ftf.getFormatter()).setOverwriteMode(false);
		ftf.setActionCommand(path);
		ftf.addActionListener(pref);
		ftf.setToolTipText(path);
		ftf.addFocusListener(pref);
		cp.add(ftf);

	    comp.add(cp);
		
	    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int j=0; j < comp.size(); j+=1) {
			JPanel borderPanel = new JPanel(new BorderLayout());
			borderPanel.setBorder(new TitledBorder(des.get(j)));
			borderPanel.add(comp.get(j), BorderLayout.CENTER);
//			borderPanel.setPreferredSize(new Dimension(400,50));
			this.add(borderPanel);
	    }
		JPanel borderPanel = new JPanel();
		borderPanel.setPreferredSize(new Dimension(400,700));
		this.add(borderPanel, BorderLayout.CENTER);
    }
}
