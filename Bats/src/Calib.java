import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;


/**
 * @author lukas
 *
 */
public class Calib
{ 	
	static int MIN = 0;
	static int MAX = 1;
	static int X = 0;
	static int Y1 = 1;
	static int Y1E = 3;
	static int Y2 = 2;
	static double[] SIG = { 0.50, 0.6827, 0.9545, 0.9973 };
	private String labelName;
	private int adBc;
	boolean linear;
	/**
	 * 
	 */
	public boolean bp;
	
	String curve = "";
	
	
	final static Logger log = Logger.getLogger(Calib.class);	
	
	/**
	 * 
	 */
	public ArrayList<CaliP> calArr;
	
	Calib( String fileName )
	{        
        labelName = Setting.getString("/bat/calib/label");
        linear = Setting.getBoolean("/bat/calib/linear");
        if (linear) {
        	log.debug("Linear interpolation used for calibration.");
        } else {
           	log.debug("Non-linear interpolation used for calibration.");
        }
        bp=Setting.getBoolean("/bat/calib/bp");
        if (bp) {
        	adBc=0;
        } else {
        	adBc= -1950;
        }
		calArr = getRecords(openFile(fileName));
		log.debug("Opened file for calibration: "+fileName);
	}
		
	/**
	 * @param filename 
	 * @return Scanner of calibration file
	 * 
	 */
	public Scanner openFile(String filename)
	{
		Scanner input = null;
		try 
		{
			input = new Scanner( new File( filename ) );
		}
		catch ( FileNotFoundException fileNotFoundException)
		{
			String message = String.format( "Could not open file %s!", filename );
			JOptionPane.showMessageDialog( null, message );
		}
		return input;
	}
	
	/**
	 * @param input 
	 * @return Array with calib data
	 * @throws NoSuchElementException 
	 */
	private ArrayList<CaliP> getRecords(Scanner input) throws NoSuchElementException {
		CaliP point;
		ArrayList<CaliP> calArr = new ArrayList<CaliP>();
		int i = 0;
		String line;
		log.debug("Set BP "+bp+" ("+adBc+")");
		int ageT;
		try {
			while ( input.hasNextLine() ) {
				i++;
				line = input.nextLine();
				if (line.startsWith("#")) {
//					log.debug("Skip line "+i+": "+line);
					if (line.startsWith("##")) {
						curve=line.substring(2);
					}
				}
				else {
					line = (line.split("#"))[0];
					String[] words = line.split(",");
					if (words.length>2) {
						ageT=Integer.valueOf(words[0].trim());
						if (!bp) {
							ageT-=1950;
							if (ageT>-1) {
								ageT+=1;
							}						
						}
						point = new CaliP(ageT,Double.valueOf(words[1].trim()),Double.valueOf(words[2].trim()));
						calArr.add(point);
					} else {
						log.debug("Skip line "+i+": "+line);
					}
				}
			}
			calArr = interpolateRec(calArr);
		}
		catch ( NumberFormatException e ) {
			String message = String.format( "File improperly formated (at line "+i+")." );
			JOptionPane.showMessageDialog( null, message );
			log.debug("NumberFormatException: "+message);
		}
		catch ( NullPointerException e ) {
			String message = String.format( "File improperly formated (at line "+i+")." );
			JOptionPane.showMessageDialog( null, message );
			log.debug("NullPointerException: "+message);
		}
		return calArr;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<CaliP> interpolateRec(ArrayList<CaliP> calArr) {
		Collections.sort(calArr, new SortAge()); 
		calArr.add(calArr.get(calArr.size()-1)); 	// dublicate the first one for calculation
		CaliP n1 = calArr.get(0);					// 
		ArrayList<CaliP> calArrAll = new ArrayList<CaliP>();
		
		for (int i=0; i<(calArr.size()-2); i++) {
			CaliP p0 = calArr.get(i);
			CaliP p1 = calArr.get(i+1);
			CaliP p2 = calArr.get(i+2);
			calArrAll.add(calArr.get(i));
			
			int dt = p1.calAge - p0.calAge;
			double df = p1.radAge - p0.radAge;
			double d0 = (p1.radAge-n1.radAge)/(p1.calAge-n1.calAge);
			double d1 = (p2.radAge-p0.radAge)/(p2.calAge-p0.calAge);
			double a = (3*df - dt*(2*d0+d1))/Math.pow(dt,2);
			double b = (d1-d0-2*a*dt)/(3*Math.pow(dt,2));
			for (int j=1; j<dt; j++) {
				double radAge;
				if (linear) {
					radAge = p0.radAge+df/dt*j;
				} else {
					radAge = p0.radAge+d0*j+a*Math.pow(j,2)+b*Math.pow(j,3);
				}
				double radAgeErr = 1.0*j/(dt)*p1.radAgeSig+1.0*(dt-j)/dt*p0.radAgeSig;
				calArrAll.add(new CaliP(p0.calAge+j,radAge,radAgeErr));
//				log.debug(radAge+"/"+radAgeErr);
			}			
			n1 = p0;
		}
		calArrAll.add(calArr.get(calArr.size()-1));  // ... and add the last one!
		return calArrAll;
	}
	
	
	/**
	 * @param age
	 * @param sig
	 * @return a
	 */
	public ArrayList<ProbaP> getProbability( double age, double sig)
	{
		ArrayList<ProbaP> probArr= new ArrayList<ProbaP>();
		double probaMax = 0;
		for (int i=0; i<calArr.size(); i++) {
			CaliP caliP = this.calArr.get(i);
			double rAge = caliP.radAge;
			double rSig = caliP.radAgeSig;
			if ( (rAge-3*rSig)<(age+3*sig) && (rAge+3*rSig)>(age-3*sig) ) {
				double y = Math.pow((age-rAge),2)/(Math.pow(sig,2)+Math.pow(rSig,2));
				double proba = Math.exp(-y/2)/Math.sqrt((Math.pow(sig,2)+Math.pow(rSig,2)));
				probArr.add(new ProbaP(caliP,proba));
				probaMax = Math.max(proba, probaMax);
			}
		}
		for (int i=0; i<probArr.size(); i++) {
			probArr.get(i).probaN = probArr.get(i).proba/probaMax;
		}		
		return probArr;
	}
	
	
	/**
	 * @param probArr
	 * @param sig
	 * @return probability
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ProbaP> sigmaProba(ArrayList<ProbaP> probArr, int sig) {
		Collections.sort(probArr); 
		ArrayList<ProbaP> redList = new ArrayList<ProbaP>();
		double sum=0;
		for (int i=0; i<probArr.size(); i++) {
			sum+=probArr.get(i).probaN;
		}
		for (int i=0; i<probArr.size(); i++) {
			probArr.get(i).probaFract=probArr.get(i).probaN/sum;
//			log.debug(probArr.get(i).probaFract);
		}
		double dSum=0;
		int j=probArr.size();
		while (dSum<SIG[sig]) {
			dSum+=(probArr.get(--j).probaFract);
			redList.add(probArr.get(j));
		}	
		Collections.sort(redList, new SortAge()); 
		return redList;
	}
	
	/**
	 * @param probArr
	 * @return probability
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Range> sigmaRanges(ArrayList<ProbaP> probArr) {
		ArrayList<Range> ranges = new ArrayList<Range>(3);
		if (probArr.size()>0) {
			Collections.sort(probArr); 
			ArrayList<ProbaP> redList = new ArrayList<ProbaP>();
			double sum=0;
			for (int i=0; i<probArr.size(); i++) {
				sum+=probArr.get(i).probaN;
			}
			for (int i=0; i<probArr.size(); i++) {
				probArr.get(i).probaFract=probArr.get(i).probaN/sum;
	//			log.debug(probArr.get(i).probaFract);
			}
			double dSum=0;
			int j=probArr.size();
			while (dSum<SIG[1]) {
				dSum+=(probArr.get(--j).probaFract);
				redList.add(probArr.get(j));
			}	
			Collections.sort(redList, new SortAge()); 
			Range range = new Range();
			range.hl = redList.get(0).calAge;
			range.ll = redList.get(redList.size()-1).calAge;
			range.proba = dSum;
			ranges.add(range);
			while (dSum<SIG[2]) {
				dSum+=(probArr.get(--j).probaFract);
				redList.add(probArr.get(j));
			}	
			Collections.sort(redList, new SortAge());
			Range range2 = new Range();
			range2.hl = redList.get(0).calAge;
			range2.ll = redList.get(redList.size()-1).calAge;
			range2.proba = dSum;
			ranges.add(range2);
			while (dSum<SIG[3]) {
				dSum+=(probArr.get(--j).probaFract);
				redList.add(probArr.get(j));
			}	
			Collections.sort(redList, new SortAge()); 
			Range range3 = new Range();
			range3.hl = redList.get(0).calAge;
			range3.ll = redList.get(redList.size()-1).calAge;
			range3.proba = dSum;
			ranges.add(range3);
			return ranges;
		} else {
			return null;
		}
	}
	
	
	/**
	 * @param probArr
	 * @return ranges
	 */
	@SuppressWarnings("unchecked")
	public Range getRange(ArrayList<ProbaP> probArr) {
		Range range = new Range();
		range.ll = probArr.get(probArr.size()-1).calAge;
		range.hl = probArr.get(0).calAge;
		double dSum=0;
		for (int i=(probArr.size()-1); i>=0; i--) {
			dSum+=probArr.get(i).probaFract;
		}
		range.proba = dSum;
		return range;
	}
	
	/**
	 * @param probArr
	 * @return ranges
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Range> getRanges(ArrayList<ProbaP> probArr) {
		int first = probArr.get(probArr.size()-1).calAge;
		int last = first;
		ArrayList<Range> list = new ArrayList<Range>();
		double dSum=0;
		for (int i=(probArr.size()-1); i>=0; i--) {
			if ( probArr.get(i).calAge<(last-1) ) {
				Range range = new Range();
				range.ll = first;
				range.hl = last;
				range.proba = dSum;
				list.add(range);
//				log.debug(first);
//				log.debug(last);
//				log.debug(dSum/sum);
				dSum=probArr.get(i).probaFract;
				last=probArr.get(i).calAge;
				first = last;
			} else {
				dSum+=probArr.get(i).probaFract;
				last=probArr.get(i).calAge;
			}
		}
//		if (first!=last) {
			Range range = new Range();
			range.ll = first;
			range.hl = last;
			range.proba = dSum;
			list.add(range);
//			log.debug(first);
//			log.debug(last);
//			log.debug(dSum);
//		}
		return list;
	}
	

	/**
	 * @param label 
	 * @param age 
	 * @param ageSig 
	 * 
	 */
	public void plotAge(String label, double age, double ageSig) {
//		log.debug("Calibration of the following age will be started: "+age+" +- "+ageSig+" ("+label+")");
		CalibGraph graph = new CalibGraph(label,age, ageSig, this);
		graph.displayAuto();
	}
	
	
	/**
	 * @param sample 
	 * 
	 */
	public void plotSample(Sample sample) {
		try {
			CalibGraph graph = new CalibGraph((String)(sample.get(labelName)), sample.age, sample.age_err, this);
			graph.displayAuto();
		} catch (NullPointerException e) {
			String message = "No calibration found for the age range!";
	  		JOptionPane.showMessageDialog( null, message );
 			log.debug(message);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	class SortAge implements Comparator
	{
		public int compare(Object aa, Object bb )
		{
			int result = 0;
			CaliP a = (CaliP)aa;
			CaliP b = (CaliP)bb;
			result = a.calAge.compareTo(b.calAge);
			return result;
		}
	}
	
	/**
	 * @param unc
	 * @param inc
	 */
	public void statistic(double unc, int inc, int sig) {
		ArrayList<Range> ranges = new ArrayList<Range>();
		
		String file = Setting.homeDir+"/statistics-"+unc+".txt";
		if (new File(file).renameTo(new File(new File(file)+".old"))) { 
			log.debug("Old file renamed!");
		}
		try {
			
		    PrintWriter pw;
			pw = new PrintWriter(new FileWriter(file));
					    	
			int[] dist = new int[1000];
			for (int i=0; i<1000; i++) {
				dist[i]=0;
			}
			
			for (int i=0; i<=10000; i=i+inc) {
				ranges = getRanges(sigmaProba(getProbability(i, unc), sig));
//				log.debug(i);
				for (int k=0; k<ranges.size(); k++) {
//					log.debug(ranges.get(k).ll);
//					log.debug(ranges.get(k).hl);
				}
				if (ranges.size()>0) {
					int ll = ranges.get(0).ll;
					int hl = ranges.get(ranges.size()-1).hl;
//				    pw.print(i+"\t");
//				    pw.print(ll+"\t");
//				    pw.print(hl+"\t");
//				    pw.print((ll-hl)+"\t");
//				    for (int j=0; j<ranges.size(); j++) {
//					    pw.print(ranges.get(j).ll+"\t");
//					    pw.print(ranges.get(j).hl+"\t");
//					    pw.print((ranges.get(j).ll-ranges.get(j).hl)+"\t");
//				    }
//				    pw.println();	
				    if (ll-hl<1000) {
				    	dist[ll-hl]++;
				    } else {
				    	log.warn(ll-hl);
				    	dist[999]++;
				    }
				} else {
					log.info("no ranges for "+i);
				}
			}
		    pw.println();				
			for (int i=0; i<1000; i++) {
			    pw.print(i+"\t");
			    pw.print(dist[i]+"\t");
			    pw.println();
			}
		    pw.close();
		    log.info("Saved data to file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData!");
		}
	}

	/**
	 * @return if ok
	 */
	public boolean manInput() {
        JPanel panel;
        String[] ConnectOptionNames = { "OK", "Cancel" };
	 	
	    final JFormattedTextField textField = new JFormattedTextField();
        textField.setValue("");
        textField.setColumns(20); //get some space

        textField.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    textField.selectAll();
                }else try{                    //The text is valid,
                    textField.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { String message = "Text field 1 could not be paresed!"; log.warn(message);JOptionPane.showMessageDialog( null, message ); }
            }
        });
	 	
	    final JFormattedTextField textField2 = new JFormattedTextField(new DecimalFormat("##0.0"));
        textField2.setValue(0);
        textField2.setColumns(6); //get some space

        textField2.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField2.isEditValid()) { //The text is invalid.
                    textField2.selectAll();
                }else try{                    //The text is valid,
                    textField2.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { String message = "Text field 2 could not be paresed!"; log.warn(message);JOptionPane.showMessageDialog( null, message ); }
            }
        });
	 	
	    final JFormattedTextField textField3 = new JFormattedTextField(new DecimalFormat("##0.0"));
        textField3.setValue(30);
        textField3.setColumns(6); //get some space

        textField3.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField3.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField3.isEditValid()) { //The text is invalid.
                    textField3.selectAll();
                }else try{                    //The text is valid,
                	if (Double.valueOf(textField3.getText())<0.1) {
                		textField3.setText("0.1");
                	}
                    textField3.commitEdit(); 
                } catch (java.text.ParseException exc) {String message = "Text field 3 could not be paresed!"; log.warn(message);JOptionPane.showMessageDialog( null, message );}
            }
        });
	 	
		panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		JPanel namePanel = new JPanel(false);
		namePanel.setLayout(new GridLayout(0, 1));
 		// Create the labels
		JLabel name = new JLabel("Sample name:     ", JLabel.RIGHT);
		JLabel  age = new JLabel("Radiocarbon age: ", JLabel.RIGHT);
		JLabel  sig = new JLabel("Uncertainty:     ", JLabel.RIGHT);
		namePanel.add(name);
		namePanel.add(age);
		namePanel.add(sig);
		JPanel fieldPanel = new JPanel(false);
		fieldPanel.setLayout(new GridLayout(0, 1));
		fieldPanel.add(textField);
		fieldPanel.add(textField2);
		fieldPanel.add(textField3);
		panel.add(namePanel);
		panel.add(fieldPanel);
		if(JOptionPane.showOptionDialog(
				null, panel, 
				"Calibration input",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE,
                null, ConnectOptionNames, 
                ConnectOptionNames[0]) 
                != 0
                ) 
		{
			log.debug("Calibration aborted.");
			return false;
		} else {
			this.plotAge(textField.getText(),Double.valueOf(textField2.getText()),Double.valueOf(textField3.getText()));
			return true;
		}
	}
}	
	

/**
 * @author lukas
 *
 */
class CaliP 
{
	/**
	 * Calendar age
	 */
	public Integer calAge;
	
	/**
	 * Radiocarbon age
	 */
	public double radAge;
	
	/**
	 * Sigma of radiocarbon age
	 */
	public double radAgeSig;
	
	/**
	 * @param calAge
	 * @param radAge
	 * @param radAgeSig
	 */
	public CaliP(int calAge, double radAge, double radAgeSig) {
		this.calAge = calAge;
		this.radAge = radAge;
		this.radAgeSig = radAgeSig;
	}

	/**
	 * @param o
	 * @return compare
	 */
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}

@SuppressWarnings("unchecked")
class ProbaP extends CaliP implements Comparable
{
	/**
	 * Probability
	 */
	public Double proba;
	
	/**
	 * Normalized probability
	 */
	public Double probaN;
		
	/**
	 * Normalized probability
	 */
	public Double probaFract;
		
	/**
	 * @param calP 
	 * @param proba 
	 */
	public ProbaP(CaliP calP, double proba) {
		super(calP.calAge, calP.radAge, calP.radAgeSig);
		this.proba = proba;
	}

	@Override
	public int compareTo(Object o) {
		return this.probaN.compareTo(((ProbaP)o).probaN);
	}
}

class Range {
	/**
	 * 
	 */
	public int ll;
	/**
	 * 
	 */
	public int hl;
	/**
	 * 
	 */
	public double proba;
	/**
	 * 
	 */
	public Range() {
	}
}
