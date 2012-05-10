import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdom.DataConversionException;

/**
 * @author lukas
 *
 */
public class InfoPanel extends JPanel {
	/**
	 * 
	 */
	final static Logger log = Logger.getLogger(InfoPanel.class);

    private static int h1;
    private static int p;
    private static String ft;
    private static Dimension dim;
    private static Color cIso;
	private static Font fIso;
	private static Color cSub;
	private static Font fSub;
	private static Color cText;
	private static Color cText2;
	private static Font fText;

    private Calc data;
	private CPanel cPanel;
	private GPanel gPanel;
	private Bats main;
	
    /**
     * @param main
     * 
     */
    public InfoPanel(Bats main) {
    	this.main = main;
    	
        h1 = Setting.getInt("/bat/general/font/h1");
        p = Setting.getInt("/bat/general/font/p");
        ft = Setting.getString("/bat/general/font/type");
        dim = new Dimension(Setting.getInt("/bat/general/frame/split/width")-14,p+1);;
        cIso = new Color(160,0,0);
    	fIso = new Font(ft, Font.BOLD, h1);
    	cSub = new Color(0,0,120);
    	fSub = new Font(ft, Font.PLAIN, p);
    	cText = Color.black;
    	cText2 = Color.gray;
    	fText = new Font(ft, Font.PLAIN, p);

    	this.data = main.data;
    	this.setLayout(new BorderLayout());
    	this.setBorder(BorderFactory.createEmptyBorder(5,5,20,5));
    	
    	gPanel= new GPanel(main);
        this.add(gPanel, BorderLayout.SOUTH, 0);		            	
    	
    	cPanel= new CPanel(main);
        this.add(cPanel, BorderLayout.NORTH, 0);		            	
    	
        this.setVisible( true );
		this.setOpaque(true);
    }
    
    /**
     * @param data 
     * 
     */
    public void update(Calc data) {
    	this.data = data;
    	this.removeAll();
    	this.cPanel=new CPanel(main);
    	this.gPanel=new GPanel(main);
        this.add(cPanel, BorderLayout.NORTH);		            	
        this.add(gPanel, BorderLayout.SOUTH);		            	
	    this.updateUI();
	    log.debug("Info Panel updated.");
    }
    
    /**
     * @author lukas
     * Correction Panel
     */
    class CPanel extends JPanel implements MouseListener {
    	Bats main;
    	/**
    	 * @param main 
    	 * 
    	 */
    	public CPanel(Bats main) {
    		this.main = main;
    		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	    DecimalFormat df = new DecimalFormat("0.0000");

    	    JLabel label = new JLabel(data.isotope);
    		label.setForeground(cIso);
    		label.setFont(fIso);
    		this.add(label);
    		
    		if (!Setting.no_data) {
	    		label = new JLabel("Magazine: "+data.magazine);
	    		label.setForeground(cText);
	    		label.setFont(fText);
	    		this.add(label);
	    		
	    		label = new JLabel("Calc-Set: "+data.calcSet);
	    		label.setForeground(cText);
	    		label.setFont(fText);
	    		this.add(label);
	    		
	    		label = new JLabel("("+data.firstRun+"-"+data.lastRun+")");
	    		label.setForeground(cText);
	    		label.setFont(fText);
	    		this.add(label);
    		}
    		
    	    int max = Math.min(4,data.corrList.size());
    	    for (int i=0;i<max;i++) {
	    	    label = new JLabel(" ");
	      		this.add(label);
	    	    label = new JLabel((i+1)+". Correction sequence");
        		label.setForeground(cSub);
        		label.setFont(fSub);
	      		this.add(label);
	    	    label = new JLabel(data.corrList.get(i).firstRun+" - "+data.corrList.get(i).lastRun);
	      		this.add(label);
    		
	      		if (data.isotope.contains("C14")) {
			      	if (data.corrList.get(i).blank.ra_bg>=0) {
					    	try {
				    		label = new JLabel("<html>Blank: "+df.format(data.corrList.get(i).blank.ra_bg)+"±<"
						    		+df.format(data.corrList.get(i).blank.ra_bg_err)
						    		+" 10⁻¹²</html>");
				    	}
				    	catch (IllegalArgumentException e) {
				    		label = new JLabel("Blank: "+data.corrList.get(i).blank.ra_bg);
				    	}
			    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
			    		this.add(label);
			    	}
			      	if (data.corrList.get(i).std.std_ra>=0) {
				    	try {
				    		label = new JLabel("<html>Std: "+df.format(data.corrList.get(i).std.std_ra)+"±"
						    		+df.format(data.corrList.get(i).std.std_ra_err)
						    		+" 10⁻¹²</html>");
				    	}
				    	catch (IllegalArgumentException e) {
				    		label = new JLabel("Standard: "+data.corrList.get(i).std.std_ra);
				    	}
			    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
			    		this.add(label);
			      	}
			      	if (data.corrList.get(i).std.std_ba>=0) {
				    	try
				    	{
				    		label = new JLabel("<html>Std13/12: "+df.format(data.corrList.get(i).std.std_ba*100)+"±"
						    		+df.format(data.corrList.get(i).std.std_ba_err*100)
						    		+" %</html>");
				    	}
				    	catch (IllegalArgumentException e) {
				    		label = new JLabel("Std (13/12): "+data.corrList.get(i).std.std_ba);	    		
				    	}
			    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
			    		this.add(label);
			      	}
		        } else if (data.isotope.contains("P")) {
			    	if (data.corrList.get(i).blank.ra_bg==0.0) {
			    		try {
			    			label = new JLabel("<html>Blank: "+df.format(data.corrList.get(i).blank.ra_bg)+"±"
			    					+df.format(data.corrList.get(i).blank.ra_bg_err)
			    					+"</html>");
			    		} catch (IllegalArgumentException e) {
				    		label = new JLabel("Blank: "+data.corrList.get(i).blank.ra_bg);
				    	}
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
			    	try {
			    		label = new JLabel("<html>Std1: "+df.format(data.corrList.get(i).std.std_ra)+"±"
					    		+df.format(data.corrList.get(i).std.std_ra_err)
					    		+"</html>");
			    	}
			    	catch (IllegalArgumentException e) {
			    		label = new JLabel("Standard: "+data.corrList.get(i).std.std_ra);
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
			    	try
			    	{
			    		label = new JLabel("<html>Std2: "+df.format(data.corrList.get(i).std.std_ba)+"±"
					    		+df.format(data.corrList.get(i).std.std_ba_err)
					    		+"</html>");
			    	}
			    	catch (IllegalArgumentException e) {
			    		label = new JLabel("Std2: "+data.corrList.get(i).std.std_ba);	    		
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
			    	try
			    	{
			    		label = new JLabel("<html>Std3: "+df.format(data.corrList.get(i).std.std_rb)+"±"
					    		+df.format(data.corrList.get(i).std.std_rb_err)
					    		+"</html>");
			    	}
			    	catch (IllegalArgumentException e) {
			    		label = new JLabel("Std3: "+data.corrList.get(i).std.std_rb);	    		
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
		        } else {
			    	try {
			    		label = new JLabel("<html>Blank: "+df.format(data.corrList.get(i).blank.ra_bg)+"±"
					    		+df.format(data.corrList.get(i).blank.ra_bg_err)
					    		+" E-12</html>");
			    	}
			    	catch (IllegalArgumentException e) {
			    		label = new JLabel("Blank: "+data.corrList.get(i).blank.ra_bg);
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
				    try {
			    		label = new JLabel("<html>Std: "+df.format(data.corrList.get(i).std.std_ra)+"±"
					    		+df.format(data.corrList.get(i).std.std_ra_err)
					    		+" E-12</html>");
			    	}
			    	catch (IllegalArgumentException e) {
			    		label = new JLabel("Std: "+data.corrList.get(i).std.std_ra);
			    	}
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
		        }
		        if (data.corrList.get(i).a_slope!=0.0) {
		        	label = new JLabel("¹²C  corr: "+df.format(data.corrList.get(i).a_slope*100)+" %/μA");
			        if (data.isotope.contains("C14")) {
			        	label.setName("ba");
			        } else {
			        	label.setName("ra");
			        }
		     		label.addMouseListener(this);
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
//			       	label = new JLabel("(offset:"+data.corrList.get(i).a_slope_off+" μA)");
//		     		label.addMouseListener(this);
//		    		label.setForeground(cText); label.setFont(fText); label.setPreferredSize(dim);
//		    		this.add(label);
		        }
		        if (data.corrList.get(i).timeCorr!=0.0) {
			       	label = new JLabel("Time corr: "+data.corrList.get(i).timeCorr+" μA/h");
			   		label.setName("time");
		     		label.addMouseListener(this);
		    		label.setForeground(cText); label.setFont(fText); //label.setPreferredSize(dim);
		    		this.add(label);
		        }

		        if (data.corrList.get(i).isoFact!=0.0) {
			   	    df = new DecimalFormat("0.0E0");
		     		label = new JLabel("Isobar: "+data.corrList.get(i).isoFact
		     				+"±"+data.corrList.get(i).isoErr+" *"
		     				+Setting.getString("/bat/isotope/calc/bg/isobar")+"*t");
		     		label.setName("iso");
		     		label.addMouseListener(this);
		     		label.setToolTipText("Slope of isobar correction on radioisotope");
		    		label.setForeground(cText);
		    		label.setFont(fText);
		    		this.add(label);
		        }
	    		}
    	}

    	public void mouseClicked(MouseEvent e) {
    		String label = ((JLabel)(e.getComponent())).getName();
    		if (label.equals("time")) {
    			main.act.exec("time correction");
       		} else 	if (label.equals("iso")) {
    			main.act.exec("isobar correction");
    		} else 	if (label.equals("ba")) {
    			main.act.exec("ba correction");
    		} else 	if (label.equals("ra")) {
    			main.act.exec("ra correction");
    		} else if (label.equals("general")) {
    			Preferences frame = new Preferences(main, 1);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		} else {
    			Preferences frame = new Preferences(main, 0);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		}
        }

    	public void mouseEntered(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mouseExited(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mousePressed(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mouseReleased(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}
    }
    
    /**
     * @author lukas
     * Panel with general corrections
     */
    class GPanel extends JPanel implements MouseListener {
    	Bats main;
    	/**
    	 * @param main 
    	 * 
    	 */
    	public GPanel(Bats main) {
    		log.debug(Setting.c);
    		this.main = main;
    		this.setLayout(new GridLayout(0, 1));
    		JLabel label = new JLabel("General settings:");
    		label.setForeground(cSub);
    		label.setFont(fSub);
    		this.add(label);
    
    		label = new JLabel(Setting.getInt("/bat/isotope/calc/current/charge")+"+ charge state");
    		label.setName("calc");
     		label.addMouseListener(this);
    		label.setForeground(cText);
    		label.setFont(fText);
     		this.add(label);
    		
			DecimalFormat df = new DecimalFormat("0.00");
//     		if (Setting.getDouble("/bat/isotope/calc/scatter")!=0.0) {
	     		label = new JLabel("Sample scatter: "+df.format(Setting.getDouble("/bat/isotope/calc/scatter")*100)+" %");
	    		label.setName("calc");
	     		label.addMouseListener(this);
	    		label.setForeground(cText);
	    		label.setFont(fText);
	    		this.add(label);
//     		}
    		
	     	if (Setting.getDouble("/bat/isotope/calc/dead_time")!=0.0) {
	     	    df = new DecimalFormat("#0.0");
	     		label = new JLabel("Dead time: "+df.format(Setting.getDouble("/bat/isotope/calc/dead_time")*1000000)+" µs");
	     		label.setName("dead");
	     		label.addMouseListener(this);
	     		label.setToolTipText("Dead time of radioiisotope detection system");
	    		label.setForeground(cText);
	    		label.setFont(fText);
	    		this.add(label);
	     	}
    		
    	    if (Setting.isotope.contains("C14")) {
    	    	try {
    	    		if (data.corrList.get(0).constBG!=0.0 || Setting.getBoolean("/bat/general/display/all_corr")) {
		    		
			     		label = new JLabel("Const. bg: "+data.corrList.get(0).constBG+"±"+data.corrList.get(0).constBGErr+" *t");
			    		label.setName("const");
			     		label.addMouseListener(this);
			    		label.setForeground(cText);
			    		label.setFont(fText);
			     		this.add(label);
    	    		}
	    		} catch (IndexOutOfBoundsException e) {;}
	    		try {
	    			if (data.corrList.get(0).constBlWeight!=0.0 || Setting.getBoolean("/bat/general/display/all_corr")) {
			     		label = new JLabel("Const. bl: "+data.corrList.get(0).constBlWeight+"μg "
			     				+data.corrList.get(0).constBlRatio+"±"+data.corrList.get(0).constBlErr+" 10⁻¹²");
			    		label.setName("const");
			     		label.addMouseListener(this);
			    		label.setForeground(cText);
			    		label.setFont(fText);
			     		this.add(label);
	    			}
	    		} catch (IndexOutOfBoundsException e) {;}	
    	    }
    		
	    	if (Setting.getBoolean("/bat/isotope/calc/poisson")==true) {
    	 		label = new JLabel("Poisson");			
    		} else {
    	 		label = new JLabel("No poisson");			
    		}
    		label.setName("calc");
     		label.addMouseListener(this);
    		label.setForeground(cText);
    		label.setFont(fText);
    		this.add(label);
    	    if (Setting.isotope.contains("C14")) {
    			if (Setting.getBoolean("/bat/isotope/calc/fract")==true) {
    		 		label = new JLabel("¹³C/¹²C corr on");			
    			} else {
    		 		label = new JLabel("¹³C/¹²C corr off");			
    			}
    			label.setName("calc");
    	 		label.addMouseListener(this);
        		label.setForeground(cText);
        		label.setFont(fText);
    			this.add(label);
    	    }
    	    
    	    if (Setting.isotope.contains("C14")) {
    		    df = new DecimalFormat("0.0");
    	 		label = new JLabel("Nom. F¹⁴C: "+df.format(Setting.getDouble("/bat/isotope/calc/nominal_ra")/100)+" ("+df.format(Setting.getDouble("/bat/isotope/calc/nominal_ba")*1000-1000)+"‰)");
    	 		label.setToolTipText("Nominal pmC value of standard. Standards are normalised to this value.");
    	    } else if (Setting.isotope.contains("P")) {
    		    df = new DecimalFormat("0.0");
    	 		label = new JLabel("Nom. std ratio: "+df.format(Setting.getDouble("/bat/isotope/calc/nominal_ra")));
    	 		label.setToolTipText("Nominal ratio of standard. Standards are normalised to this value.");
    	    } else {
    		    df = new DecimalFormat("0.0");
    	 		label = new JLabel("Nom. std ratio: "+df.format(Setting.getDouble("/bat/isotope/calc/nominal_ra"))+"E-12");
    	 		label.setToolTipText("Nominal ratio of standard. Standards are normalised to this value.");
    	    }
    		label.setName("calc");
     		label.addMouseListener(this);
    		label.setForeground(cText);
    		label.setFont(fText);
    		this.add(label);    	
    		
    	    Boolean active, active2;
            try {
    			active = Setting.getElement("/bat/isotope/db/sql").getAttribute("active").getBooleanValue();
    		} catch (DataConversionException e) { active = false; log.warn("Attribut is missing for sql active."); }
    		
    		if (active == true) {  
	    		Boolean cycle;
	    		String z;
	    		label = new JLabel("");
	    		try {
	    			cycle = Setting.getElement("/bat/isotope/db/sql/cycle").getAttribute("name").getBooleanValue();
	    		} catch (DataConversionException e) { cycle=false; log.debug("Cycle setting not found!"); }
	    		if (cycle==true) {
	    	 		z=" (C)";
	    		} else {
	    	 		z=" (R)";
	    		}
			    label.setText(Setting.getString("/bat/isotope/db/sql/url")+z);
	    		label.setName("DB save");
	     		label.addMouseListener(this);
	     		if (main.db.isConn()) {
	     			label.setForeground(cText);
	     		} else {
	     			label.setForeground(cText2);
	     		}
	    		label.setFont(fText);
	    		this.add(label);
    		} else {
	           try {
	    			active2 = Setting.getElement("/bat/isotope/db/sql-import").getAttribute("active").getBooleanValue();
	    		} catch (DataConversionException e) { active2 = true; log.warn("Attribut is missing for sql-import active."); }
	    		
	    		if (active2 == false) {       
		    		Boolean cycle;
		    		label = new JLabel("");
		    		try {
		    			cycle = Setting.getElement("/bat/isotope/db/ora/cycle").getAttribute("name").getBooleanValue();
		    		} catch (DataConversionException e) { cycle=false; log.debug("Cycle setting not found!"); }
		    		if (cycle==true) {
		    	 		label.setText(label.getText()+"cycle ("+Setting.getInt("/bat/isotope/db/ora/cycle")+") import (ORA)");
		    		} else {
			    		if (Setting.getBoolean("/bat/isotope/db/ora/virtual")==true) {
			    	 		label = new JLabel("virtual ");			
			    		} else {
			    	 		;			
			    		}
		    	 		label.setText(label.getText()+"run import (ORACLE)");			
		    		}
		    		label.setName("DB");
		     		label.addMouseListener(this);
		    		label.setForeground(cText);
		    		label.setFont(fText);
		    		this.add(label);
	    		} else {       
		    		Boolean cycle;
		    		label = new JLabel("");
		    		try {
		    			cycle = Setting.getElement("/bat/isotope/db/sql-import/cycle").getAttribute("name").getBooleanValue();
		    		} catch (DataConversionException e) { cycle=false; log.debug("Cycle setting not found!"); }
		    		if (cycle==true) {
		    	 		label.setText(label.getText()+"cycle ("+Setting.getInt("/bat/isotope/db/sql-import/cycle")+") import (MySQL)");
		    		} else {
		    	 		label.setText(label.getText()+"run import (MySQL)");			
		    		}
		    		label.setName("DB");
		     		label.addMouseListener(this);
		    		label.setForeground(cText);
		    		label.setFont(fText);
		    		this.add(label);
	    		}
    		}		
    	}

    	public void mouseClicked(MouseEvent e) {
    		String label = ((JLabel)(e.getComponent())).getName();
    		if (label.equals("time")) {
    			main.act.exec("time correction");
    		} else 	if (label.equals("corr")) {
    			main.act.exec("ra correction");
    		} else 	if (label.equals("const")) {
    			main.act.exec("const");
    		} else if (label.equals("file")) {
    			Preferences frame = new Preferences(main, 1);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		} else if (label.equals("DB")) {
    			Preferences frame = new Preferences(main, 2);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		} else if (label.equals("DB save")) {
    			Preferences frame = new Preferences(main, 3);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		} else {
    			Preferences frame = new Preferences(main, 0);
    	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		}
        }

    	public void mouseEntered(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mouseExited(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mousePressed(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}

    	public void mouseReleased(MouseEvent e) {
    		// TODO Auto-generated method stub
    		
    	}
    }    
}


