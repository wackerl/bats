import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
abstract class Corr {
	final static Logger log = Logger.getLogger(Corr.class);

	protected static Double microA = 6.24150948E12;

	/**
	 * 
	 */
	public StdBl blankR;
	
	/**
	 * 
	 */
	public StdBl blankS;
	/**
	 * 
	 */
	public StdBl blank;	
	/**
	 * 
	 */
	public StdBl std;
	
	/**
	 * 
	 */
	public StdBl stdR;
	
	/**
	 * 
	 */
	public StdBl stdS;
	
	/**
	 * 
	 */
	public StdBl stdSc;
	
	/**
	 * 
	 */
//	public Double nom;
	
	/**
	 * 
	 */
	public Integer select_mean;
	
	/**
	 * slope of current a (for current correction)
	 */
	public Double a_slope;
	/**
	 * offset for current correction
	 */
	public Double a_slope_off;
	/**
	 * slope of current b (for current correction)
	 */
	public Double b_slope;
	
	/**
	 * offset for current correction
	 */
	public Double b_slope_off;

	/**
	 * 
	 */
	public Double timeCorr;

	/**
	 * 
	 */
	public Double isoFact;
	
	/**
	 * 
	 */
	public Double isoErr;
	
	/**
	 * 
	 */
	public Double constBG;
	
	/**
	 * 
	 */
	public Double constBGErr;
	
	/**
	 * 
	 */
	public Double constBlWeight;
	
	/**
	 * 
	 */
	public Double constBlRatio;
	
	/**
	 * 
	 */
	public Double constBlErr;
	
	/**
	 * 
	 */
	protected StdNom stdNom;
	
	/**
	 * 
	 */
	public String firstRun = null;
	
	/**
	 * 
	 */
	public String lastRun = null;

	Corr(StdNom stdNom) {
		this.stdNom = stdNom;
		blank = new StdBl("Blank", "used", true, Setting.autocalc);
		blankR = new StdBl("Blank", "(mean)",false, false);
		blankS = new StdBl("Sample", "(scatter)", false, false);
		std = new StdBl("Std", "used",  true, Setting.autocalc);
		stdR = new StdBl("Std", "(run)", false, false);
		stdS = new StdBl("Std", "(sample)", false, false);
		stdSc = new StdBl("", "(scatter)", false, false);
		timeCorr = 0.0;
		constBG=0.0;
		constBGErr=0.0;
		constBlWeight=0.0;
		constBlRatio=0.0;
		constBlErr=0.0;
		loadSetting();
	}

	/**
	 * 
	 */
	public void loadSetting() {
		a_slope = Setting.getDouble("bat/isotope/calc/current/correction/a_slope");
		a_slope_off = Setting.getDouble("bat/isotope/calc/current/correction/a_slope_off");
		b_slope = Setting.getDouble("bat/isotope/calc/current/correction/b_slope");
		b_slope_off = Setting.getDouble("bat/isotope/calc/current/correction/b_slope_off");
		isoFact = Setting.getDouble("/bat/isotope/calc/bg/factor");
		isoErr = Setting.getDouble("/bat/isotope/calc/bg/error");
		select_mean = Setting.getInt("bat/isotope/calc/mean");
//		nom = Setting.getDouble("bat/isotope/calc/nominal_ra");
//	    if (Setting.isotope.contains("C14")) {
//	    	nom_ba = Setting.getDouble("bat/isotope/calc/nominal_ba");
//	    }
//		scatter = Setting.getDouble("/bat/isotope/calc/scatter");
//		log.debug(nom+"/"+nom_ba+"/"+scatter);
//		log.debug("Isobar multiplication factor is set to "+isoFact+"+-"+isoErr);
	}
	
	/**
	 * @return "copy" of Correction
	 */
	abstract public Corr copy();
	
	/**
	 * @param sampleList
	 * @param runList
	 */
	abstract public void setBlank(ArrayList<Sample> sampleList, ArrayList<Run> runList);
	
	/**
	 * @param sampleList
	 * @param runList
	 */
	abstract public void setStandard(ArrayList<Sample> sampleList, ArrayList<Run> runList);
}

class StdBl implements DataSet {
	final static Logger log = Logger.getLogger(StdBl.class);
	protected String run;
	protected Double ra_bg;
	protected Double ra_bg_sig;
	protected Double ra_bg_err;
	
	protected Double rb_bg;
	protected Double rb_bg_sig;
	protected Double rb_bg_err;
	
	protected Double std_ra;
	protected Double std_ra_sig;
	protected Double std_ra_err;
	
	protected Double ba_cur;
	protected Double ba_cur_sig;
	protected Double ba_cur_err;
	
	protected Double std_ba;
	protected Double std_ba_sig;
	protected Double std_ba_err;
	
	protected Double std_rb;
	protected Double std_rb_sig;
	protected Double std_rb_err;
	
	protected Double r_cor;
	protected Double a_cur;
	protected Double b_cur;
	
	protected Boolean editable;
	protected Boolean active;
	protected String label;
	protected Integer number;
	
	protected Double runtime;
	protected Double runtime_a;
	protected Double runtime_b;
	
	public Integer number() {
		return number;
	}

	StdBl(String run, String label, Boolean editable, Boolean active) {
		this.run = run;
		this.label = label;
		this.editable = editable;
		this.active=active;
		number=0;
		std_ra =-1.0;
		std_ra_err =-1.0;
		std_rb =-1.0;
		std_rb_err =-1.0;
		std_ba =-1.0;
		std_ba_err =-1.0;
	}
	
	public Object get(String name) {
		Object returnVal = null;
		if (name.equals("std_ra")) { returnVal = std_ra; }
		else if (name.equals("std_ra_err")) { returnVal = std_ra_err; }
		else if (name.equals("std_ra_sig")) { returnVal = std_ra_sig; }
		else if (name.equals("std_ra_errR")) { returnVal = std_ra_err/std_ra; }
		else if (name.equals("std_ra_sigR")) { returnVal = std_ra_sig/std_ra; }
		else if (name.equals("std_ra_chi")) { try {return Math.pow(std_ra_sig/std_ra,2)/Math.pow(std_ra_err/std_ra,2); } catch (NullPointerException e){return null;} } 	
		else if (name.equals("a_cur")) { returnVal = a_cur; }
		else if (name.equals("b_cur")) { returnVal = b_cur; }
		else if (name.equals("r_cor")) { returnVal = r_cor; }
		else if (name.equals("ra_bg")) { returnVal = ra_bg; }
		else if (name.equals("ra_bg_err")) { returnVal = ra_bg_err; }
		else if (name.equals("ra_bg_sig")) { returnVal = ra_bg_sig; }
		else if (name.equals("ra_bg_errR")) { returnVal = ra_bg_err/ra_bg; }
		else if (name.equals("ra_bg_sigR")) { returnVal = ra_bg_sig/ra_bg; }
		else if (name.equals("ra_bg_chi")) { try { return Math.pow(ra_bg_sig/ra_bg,2)/Math.pow(ra_bg_err/ra_bg,2); } catch (NullPointerException e){return null;} } 	
		else if (name.equals("rb_bg")) { returnVal = rb_bg; }
		else if (name.equals("rb_bg_err")) { returnVal = rb_bg_err; }
		else if (name.equals("rb_bg_sig")) { returnVal = rb_bg_sig; }
		else if (name.equals("rb_bg_errR")) { returnVal = rb_bg_err/rb_bg; }
		else if (name.equals("rb_bg_sigR")) { returnVal = rb_bg_sig/rb_bg; }
		else if (name.equals("rb_bg_chi")) { try { return Math.pow(rb_bg_sig/rb_bg,2)/Math.pow(rb_bg_err/rb_bg,2); } catch (NullPointerException e){return null;} } 	
		else if (name.equals("ba_cur")) { returnVal = ba_cur; }
		else if (name.equals("ba_cur_err")) { returnVal = ba_cur_err; }
		else if (name.equals("ba_cur_sig")) { returnVal = ba_cur_sig; }
		else if (name.equals("ba_cur_errR")) { returnVal = ba_cur_err/ba_cur; }
		else if (name.equals("ba_cur_sigR")) { returnVal = ba_cur_sig/ba_cur; }
		else if (name.equals("std_ba")) { returnVal = std_ba; }
		else if (name.equals("std_ba_err")) { returnVal = std_ba_err; }
		else if (name.equals("std_ba_sig")) { returnVal = std_ba_sig; }
		else if (name.equals("std_ba_errR")) { returnVal = std_ba_err/std_ba; }
		else if (name.equals("std_ba_sigR")) { returnVal = std_ba_sig/std_ba; }
		else if (name.equals("std_ba_chi")) { try { return Math.pow(std_ba_sig/std_ba,2)/Math.pow(std_ba_err/std_ba,2); }  catch (NullPointerException e){return null;} }	
		else if (name.equals("std_rb")) { returnVal = std_rb; }
		else if (name.equals("std_rb_err")) { returnVal = std_rb_err; }
		else if (name.equals("std_rb_sig")) { returnVal = std_rb_sig; }
		else if (name.equals("std_rb_errR")) { returnVal = std_rb_err/std_rb; }
		else if (name.equals("std_rb_sigR")) { returnVal = std_rb_sig/std_rb; }
		else if (name.equals("std_rb_chi")) { try { return Math.pow(std_rb_sig/std_rb,2)/Math.pow(std_rb_err/std_rb,2); }  catch (NullPointerException e){return null;} }	
		else if (name.equals("run")) { returnVal = run; }
		else if (name.equals("runtime")) { returnVal = runtime; }
		else if (name.equals("runtime_a")) { returnVal = runtime_a; }
		else if (name.equals("runtime_b")) { returnVal = runtime_b; }
		else if (name.equals("label")) { returnVal = label; }
		else if (name.equals("editable")) {returnVal = editable;}
		else if (name.equals("active")) {returnVal = active;}
		else {
			String message = String.format("");
	//		JOptionPane.showMessageDialog( null, message );
			returnVal = message;
//				log.info("object "+name+" not available!"); // this is not necessary, because not all fields are available
		}
		return returnVal;
	}

	
	public void set( Object value, String name ) {
		try {
			if (value.equals("null")){value=null;}
			else if (name.equals("std_ra")) {std_ra = (Double) value;}
			else if (name.equals("std_ra_err")) {std_ra_err = (Double) value;}
			else if (name.equals("std_ra_sig")) {std_ra_sig = (Double) value;}
			else if (name.equals("std_ra_errR")) {std_ra_err = (Double) value * std_ra;}
			else if (name.equals("std_ra_sigR")) {std_ra_sig = (Double) value * std_ra;}
			else if (name.equals("ra_bg")) {ra_bg = (Double) value;}
			else if (name.equals("ra_bg_err")) {ra_bg_err = (Double) value;}
			else if (name.equals("ra_bg_sig")) {ra_bg_sig = (Double) value;}
			else if (name.equals("ra_bg_errR")) {ra_bg_err = (Double) value * ra_bg;}
			else if (name.equals("ra_bg_sigR")) {ra_bg_sig = (Double) value * ra_bg;}
			else if (name.equals("ba_cur")) {ba_cur = (Double) value;}
			else if (name.equals("ba_cur_err")) {ba_cur_err = (Double) value;}
			else if (name.equals("ba_cur_sig")) {ba_cur_sig = (Double) value;}
			else if (name.equals("ba_cur_errR")) {ba_cur_err = (Double) value * ba_cur;}
			else if (name.equals("ba_cur_sigR")) {ba_cur_sig = (Double) value * ba_cur;}
			else if (name.equals("std_ba")) {std_ba = (Double) value;}
			else if (name.equals("std_ba_err")) {std_ba_err = (Double) value;}
			else if (name.equals("std_ba_sig")) {std_ba_sig = (Double) value;}
			else if (name.equals("std_ba_errR")) {std_ba_err = (Double) value * std_ba;}
			else if (name.equals("std_ba_sigR")) {std_ba_sig = (Double) value * std_ba;}
			else if (name.equals("std_rb")) {std_rb = (Double) value;}
			else if (name.equals("std_rb_err")) {std_rb_err = (Double) value;}
			else if (name.equals("std_rb_sig")) {std_rb_sig = (Double) value;}
			else if (name.equals("std_rb_errR")) {std_rb_err = (Double) value * std_rb;}
			else if (name.equals("std_rb_sigR")) {std_rb_sig = (Double) value * std_rb;}
			else if (name.equals("editable")) {editable = (Boolean) value;}
			else if (name.equals("active")) {active = (Boolean) value;}
			else {
				String message = String.format("FormatT error in set function in correction! " + name + " - " + value);
				log.debug(message);
			}
		}
		catch (NumberFormatException e) {log.error("FormatT error in set function in correction!");}
		catch (ClassCastException e) {log.error(value+" is not the right object type!");log.error(e.getMessage());}
	}
	
	/**
	 * @param name
	 * @param sTag 
	 * @param eTag 
	 * @return String with values of a run
	 */
	public String getSet( ArrayList<String> name, String sTag, String eTag ) {
		Object returnVal;
		String val = "";
		for (int i=0;i<name.size();i++) {
			returnVal = this.get(name.get(i));
//			if (returnVal==null) {
//				returnVal="null";
//			}
			val += sTag+returnVal+eTag;
		}
		return val;
	}
	
	/**
	 * @param format
	 * @return Set of data by name for excel-xml
	 */
	public String getSetEx(FormatT format, String type) {
		Object returnVal;
		String val="";
		for (int i=0;i<format.colValue.size();i++) {
			val += "    <Cell  ss:StyleID=\""+type+i+"\"><Data ss:Type=\"";
			returnVal = this.get(format.colValue.get(i));
			if (returnVal instanceof Double) {
				if (returnVal==null || returnVal.toString().equals("NaN")) {
					val+="String\">";
				} else {
					val+="Number\">";
					returnVal = format.colMulti.get(i)*((Double)returnVal);
				}
			} else if (returnVal instanceof Integer) {
				if (returnVal==null || returnVal.toString().equals("NaN")) {
					val+="String\">";
				} else {
					val+="Number\">";
				}
			} else if (returnVal instanceof Date) {
				val+="DateTime\">";
			} else {
				val+="String\">";
			}
			val += returnVal+"</Data></Cell>\n";
		}
		return val;
	}
	
	public double weight() {
		return 1.0;

	}
	public double runtime() {
		return runtime;
	}
	
	public boolean active() {
		return active;
	}
	

}
