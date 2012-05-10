import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * @author lukas
 *
 */
public class Run extends Object implements DataSet, Cloneable

{
	final static Logger log = Logger.getLogger(Run.class);
	/**
	 * stable isotope current a
	 */
	public Double a;
	/**
	 * stable isotope current error
	 */
	public Double a_err;
	/**
	 * corrected stable isotope current
	 */
	public Double a_cur; 	
	/**
	 * corrected stable isotope current error
	 */
	public Double a_cur_err;
	/**
	 * final age
	 */
	public Double age;
	/**
	 * final age error
	 */
	public Double age_err;
	/**
	 * low energy current of stable isotope a
	 */
	public Double ana;
	/**
	 * low energy current of stable isotope b (2. stable isotope; if available)
	 */
	public Double anb;
	/**
	 * never used???
	 */
	public Double anbana;
	/**
	 * never used???
	 */
	public Double anbana_err;
	/**
	 * never used???
	 */
	public Double anban_sig;
	/**
	 * never used???
	 */
	public Double asym;
	/**
	 * stable isotope current b (2. stable isotope; if available)
	 */
	public Double b;
	/**
	 * error of stable isotope current  b (2. stable isotope; if available)
	 */
	public Double b_err;
	/**
	 * corrected stable isotope current  b (2. stable isotope; if available)
	 */
	public Double b_cur;
	/**
	 * error of corrected stable isotope current  b (2. stable isotope; if available)
	 */
	public Double b_cur_err;
	/**
	 * ratio of stable isotopes
	 */
	public Double ba;
	/**
	 * error of stable isotopes
	 */
	public Double ba_err;
	/**
	 * sigma of stable isotopes
	 */
	public Double ba_sig;
	/**
	 * current corrected ratio of stable isotopes
	 */
	public Double ba_cur; 	
	/**
	 * sigma of current corrected ratio of stable isotopes
	 */
	public Double ba_cur_sig;	
	/**
	 * error of current corrected ratio of stable isotopes
	 */
	public Double ba_cur_err; 	
	/**
	 * standard corrected ratio of stable isotopes
	 */
	public Double ba_fin; 	
	/**
	 * error of standard corrected ratio of stable isotopes
	 */
	public Double ba_fin_err; 	
	/**
	 * sigma of standard corrected ratio of stable isotopes
	 */
	public Double ba_fin_sig;	
	/**
	 * stable isotope ratio for the nominal ratio nom_ba (only used for standards!)
	 */
	public Double std_ba;	
	/**
	 * error of std_ba
	 */
	public Double std_ba_err;	
	/**
	 * sigma of std_ba
	 */
	public Double std_ba_sig;	
	/**
	 * background counts on r
	 */
	public Double bg;
	/**
	 * error of bg
	 */
	public Double bg_err;	
	/**
	 * number of cycles
	 */
	public Integer cycles;
	/**
	 * isotope in gate g1 (second gate)
	 */
	public Double g1;
	/**
	 * used for Pu
	 */
	public Double g1a;
	/**
	 * used for Pu
	 */
	public Double g1a_err;
	/**
	 * not used???
	 */
	public Double g1b;
	/**
	 * used for Pu
	 */
	public Double g2a;
	/**
	 * used for Pu
	 */
	public Double g2a_err;
	/**
	 * error of g1
	 */
	public Double g1_err;
	/**
	 * isotope in gate g2 (third gate)
	 */
	public Double g2;
	/**
	 * not used???
	 */
	public Double g2b;
	/**
	 * error of g2
	 */
	public Double g2_err;
	/**
	 * isobar current (for radiocarbon: 13C from 13CH)
	 */
	public Double iso;
	/**
	 * error of isobar
	 */
	public Double iso_err;
	/**
	 * radioisotope counts (isotope in gate 1)
	 */
	public Double r;
	/**
	 * error of radioisotope
	 */
	public Double r_err;
	/**
	 * background corrected radioisotope counts
	 */
	public Double r_cor;
	/**
	 * error of background corrected radioisotope counts
	 */
	public Double r_cor_err;
	/**
	 * ratio of r/a (raw ratio)
	 */
	public Double ra;
	/**
	 * error of ra
	 */
	public Double ra_err;
	/**
	 * sigma of ra
	 */
	public Double ra_sig;
	/**
	 * current corrected ratio (r/a_cur)
	 */
	public Double ra_cur;	
	/**
	 * error of ra_cur
	 */
	public Double ra_cur_err;	
	/**
	 * sigma of ra_cur
	 */
	public Double ra_cur_sig;	
	/**
	 * background corrected ratio (r_cor/a_cur)
	 */
	public Double ra_bg;	
	/**
	 * error of ra_bg
	 */
	public Double ra_bg_err;	
	/**
	 * sigma of ra_bg
	 */
	public Double ra_bg_sig;	
	/**
	 * blank corrected ratio (ra_bg - blank)
	 */
	public Double ra_bl;	
	/**
	 * error of ra_bl
	 */
	public Double ra_bl_err;	
	/**
	 * sigma of ra_bl
	 */
	public Double ra_bl_sig;	
	/**
	 * standard corrected ratio (ra_bl/<std_ra>*nom_ra). 
	 * Exception: for radiocarbon this is already an uncorrected pmC value! (without d13 correction)
	 */
	public Double ra_fin;	
	/**
	 * error of ra_fin
	 */
	public Double ra_fin_err;	
	/**
	 * sigma of ra_fin
	 */
	public Double ra_fin_sig;	
	/**
	 * d13 corrected pmC value (for radiocarbon only)
	 */
	public Double pmC;	
	/**
	 * error of pmC
	 */
	public Double pmC_err;	
	/**
	 * sigma of pmC
	 */
	public Double pmC_sig;	
	/**
	 * if true, run/sample is set active (only activated runs are taken for calculations!)
	 */
//cv
	public Double rCl;	
	/**
	 * error of pmC
	 */
	public Double rCl_err;	
	/**
	 * sigma of pmC
	 */
	public Double rCl_sig;	
	/**
	 * if true, run/sample is set active (only activated runs are taken for calculations!)
	 */
//cv
	public Boolean active;
	/**
	 * radioisotope ratio for the nominal ratio nom_ba (only used for standards!)
	 */
	public Double std_ra;	
	/**
	 * error of std_ra
	 */
	public Double std_ra_err;	
	/**
	 * sigma or std_ra
	 */
	public Double std_ra_sig;	
	/**
	 * 
	 */
	public Double rb;
	/**
	 * 
	 */
	public Double rb_err;
	/**
	 * 
	 */
	public Double rb_sig;
	/**
	 * 
	 */
	public Double rb_cur;
	/**
	 * 
	 */
	public Double rb_cur_err;
	/**
	 * 
	 */
	public Double rb_cur_sig;
	/**
	 * 
	 */
	public Double rb_bg;
	/**
	 * 
	 */
	public Double rb_bg_err;
	/**
	 * 
	 */
	public Double rb_bg_sig;
	/**
	 * 
	 */
	public Double rb_fin;
	/**
	 * 
	 */
	public Double rb_fin_err;
	/**
	 * 
	 */
	public Double rb_fin_sig;
	/**
	 * 
	 */
//cv
	public Double rs_fin;
	/**
	 * 
	 */
	public Double rs_fin_err;
	/**
	 * 
	 */
	public Double rs_fin_sig;
	/**
	 * 
	 */
//cv
	/**
	 * 
	 */
	public Double std_rb;
	/**
	 * 
	 */
	public Double std_rb_err;
	/**
	 * 
	 */
	public Double std_rb_sig;
	/**
	 * recno never used
	 */
	public Integer recno;
	/**
	 * Run number, used for sequence-sorting
	 */
	public String run;
	/**
	 * total measurement time for radioisotope of the run
	 */
	public Double runtime;
	/**
	 * total measurement time for 2. radioisotope of the run
	 */
	public Double runtime_a; 	
	/**
	 * total measurement time for 3. radioisotope of the run
	 */
	public Double runtime_b; 	
	/**
	 * total measurement time for 4. radioisotope of the run
	 */
	public Double runtime_g1; 	
	/**
	 * total measurement time for 5. radioisotope of the run
	 */
	public Double runtime_g2; 	
	/**
	 * delta time for radioisotope of the run
	 */
	public Double dt_a;
	/**
	 * delta time for 2. radioisotope of the run
	 */
	public Double dt_b; 	
	/**
	 * delta time for 3. radioisotope of the run
	 */
	public Double dt_r; 	
	/**
	 * delta time for 4. radioisotope of the run
	 */
	public Double dt_g1; 	
	/**
	 * delta time for 4. radioisotope of the run
	 */
	public Double dt_g2; 	
	/**
	 * regression for time correction of run (for Pu)
	 */
	public Double t_reg; 	
	/**
	 * stripper pressure
	 */
	public Double stripper;
	/**
	 * time and date of the measurement
	 */
	public Date timedat;
	/**
	 * time and date of the measurement
	 */
	public Double time;
	/**
	 * transmission of stable isotope a
	 */
	public Double tra;
	/**
	 * sigma of tra
	 */
	public Double tra_sig;

	/**
	 * if already reduced
	 */
	public Boolean reduced;
	/**
	 * used internally for blank and standard table
	 */
	public Boolean editable;
	/**
	 * defines the correction used for this run
	 */
	public Integer corr_index = 0;	
	/**
	 * 
	 */
	public Sample sample;

	SimpleDateFormat date_format;
	DateFormat date_year;
	
	/**
	 * Initialises run
	 * @param sample 
	 */
	public Run( Sample sample ) {
		this.sample = sample;
//		 magazine = "no mag";
//		 posit = new Integer(-1);
//		 weight = new Double(0);
//		 run = "?";
		 date_format = new SimpleDateFormat( "dd.MM.yyyy" );
		 date_year = DateFormat.getDateInstance(DateFormat.YEAR_FIELD);
		 iso=0.0;
		 active = true;
		 editable = true;
		 reduced=false;
	}

	/**
	 * @param value
	 * @param name
	 */
	public void setValues( String[] value, ArrayList<String> name ) {
		int i, cols;
		cols = Math.min(value.length,name.size());
		for (i=0;i<cols;i++) {
//			log.debug(value[i]);
//			log.debug(name.get(i));
			this.setValue(value[i],name.get(i));
		}
	}
	
	/**
	 * @param value
	 * @param name
	 */
	public void setValues( String[] value, String[] name ) {
		int i, cols;
		cols = Math.min(value.length,name.length);
		for (i=0;i<cols;i++) {
			this.setValue(value[i],name[i]);
		}
	}
	
	/**
	 * @param format
	 * @param type 
	 * @return Set of data by name for excel-xml
	 */
	public String getSetEx(FormatT format, String type) {
		Object returnVal;
		String val="";
		for (int i=0;i<format.colValue.size();i++) {
			val += "    <Cell  ss:StyleID=\""+type+i+"\"><Data ss:Type=\"";
			returnVal = this.get(format.colValue.get(i));
			if (returnVal instanceof Double) {
				if (returnVal==null || returnVal.toString().equals("NaN") || returnVal.toString().contains("Infinity")) {
					val+="String\">";
				} else {
					val+="Number\">";
					returnVal = format.colMulti.get(i)*((Double)returnVal);
				}
			} else if (returnVal instanceof Integer) {
				if (returnVal==null || returnVal.toString().equals("NaN") || returnVal.toString().contains("Infinity")) {
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
	
	/**
	 * @param value
	 * @param name
	 */
	public void setValues( ArrayList<String> value, ArrayList<String> name) {
		int i, cols;
		cols = Math.min(value.size(),name.size());
//		} else {
//			cols = name.size();
//			log.error("The number of columns in the file ("+value.size()+") is not equal the number of names ("
//					+name.size()+") asked.");
//		}
		for (i=0;i<cols;i++) {
			this.setValue(value.get(i),name.get(i));
		}
	}
	
	/**
	 * @param value
	 * @param name
	 */
	public void setRunSet( List<Element> value, ArrayList<String> name) {
		int i, cols;
		cols = Math.min(value.size(),name.size());
		for (i=0;i<cols;i++) {
			this.setValue(value.get(i).getText(),name.get(i));
		}
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
//			if (returnVal instanceof Date) {
//				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
//					returnVal = df.format( returnVal);
//			}
//			if (returnVal==null) {
//				returnVal="null";
//			}
			val += sTag+returnVal+eTag;
		}
		return val;
	}
	
	/**
	 * @param name
	 * @return value
	 */
	public Object getValue(String name){
		Object returnVal;
		returnVal = this.get(name);
		if (returnVal=="") {
			returnVal="";
		}
		if (returnVal==null) {
			returnVal="null";
		}
		return returnVal;
	}
	
	/**
	 * @param value Value to set
	 * @param name Field name for value
	 */
	public void setValue( String value, String name ) {
		try {
			if (value==null||name==null) /*log.debug("Set value of "+name+" is null!")*/;
			else if (value.equals("null")){value=null;}
			else if (name.equals("no")){;}
			else if (name.equals("a")) {a = Double.valueOf(value);}
			else if (name.equals("a_c")) {a = Double.valueOf(value)/runtime_b*1000000;}
			else if (name.equals("a_amp")) {a = Double.valueOf(value)*1000000;}
			else if (name.equals("a_err")) {a_err = Double.valueOf(value);}
			else if (name.equals("a_nano")) {a = Double.valueOf(value)/1000;}
			else if (name.equals("a_cur")) {a_cur = Double.valueOf(value);}
			else if (name.equals("a_cur_err")) {a_cur_err = Double.valueOf(value);}
			else if (name.equals("age")) {age = Double.valueOf(value);}
			else if (name.equals("age_err")) {age_err = Double.valueOf(value);}
			else if (name.equals("ana")) {ana = Double.valueOf(value);}
			else if (name.equals("anb")) {anb = Double.valueOf(value);}
			else if (name.equals("ana_amp")) {ana = Double.valueOf(value)*1000000;}
			else if (name.equals("anb_amp")) {anb = Double.valueOf(value)*1000000;}
			else if (name.equals("anbana")) {anbana = Double.valueOf(value);}
			else if (name.equals("anbana_err")) {anbana_err = Double.valueOf(value);}
			else if (name.equals("anban_sig")) {anban_sig = Double.valueOf(value);}
			else if (name.equals("anbansig")) {anban_sig = Double.valueOf(value);}
			else if (name.equals("asym")) {asym = Double.valueOf(value);}
			else if (name.equals("b")) {b = Double.valueOf(value);}
			else if (name.equals("b_c")) {b = Double.valueOf(value)/runtime_b*1000000;}
			else if (name.equals("b_amp")) {b = Double.valueOf(value)*1000000;}
			else if (name.equals("b_err")) {b_err = Double.valueOf(value);}
			else if (name.equals("b_nano")) {b = Double.valueOf(value)/1000;}
			else if (name.equals("b_cur")) {b_cur = Double.valueOf(value);}
			else if (name.equals("b_err")) {b_cur_err = Double.valueOf(value);}
			else if (name.equals("ba")) {ba = Double.valueOf(value);}
			else if (name.equals("ba_pc")) {ba = Double.valueOf(value)/100;}
			else if (name.equals("ba_sig_p")) {ba_sig = Double.valueOf(value)/100*ba;}
			else if (name.equals("ba_sig")) {ba_sig = Double.valueOf(value);}
			else if (name.equals("basig")) {ba_sig = Double.valueOf(value)/100*ba;}
			else if (name.equals("ba_err")) {ba_err = Double.valueOf(value);}
			else if (name.equals("ba_cur")) {ba_cur = Double.valueOf(value);}
			else if (name.equals("ba_cur_err")) {ba_cur_err = Double.valueOf(value);}
			else if (name.equals("ba_cur_sig")) {ba_cur_sig = Double.valueOf(value);}
			else if (name.equals("ba_fin")) {ba_fin = Double.valueOf(value);}
			else if (name.equals("ba_fin_err")) {ba_fin_err = Double.valueOf(value);}
			else if (name.equals("ba_fin_sig")) {ba_fin_sig = Double.valueOf(value);}
			else if (name.equals("std_ba")) {std_ba = Double.valueOf(value);}
			else if (name.equals("std_ba_err")) {std_ba_err = Double.valueOf(value);}
			else if (name.equals("std_ba_sig")) {std_ba_sig = Double.valueOf(value);}
			else if (name.equals("carrier")) {sample.carrier = Double.valueOf(value);} 	
			else if (name.equals("commentinfo")) {sample.desc1 = String.valueOf(value);}  	
			else if (name.equals("comment")) {;}  	
			else if (name.equals("corrIndex")) {corr_index = Integer.valueOf(value);}  	
			else if (name.equals("corr_index")) {corr_index = Integer.valueOf(value);}  	
			else if (name.equals("cycles")) {cycles = Integer.valueOf(value);}
			else if (name.equals("cycle")) {cycles = Integer.valueOf(value);}
			else if (name.equals("g1")) {g1 = Double.valueOf(value);}
			else if (name.equals("g1a")) {g1a = Double.valueOf(value);}
			else if (name.equals("g1b")) {g1b = Double.valueOf(value);}
			else if (name.equals("g1_err")) {g1_err = Double.valueOf(value);}
			else if (name.equals("g1del")) {g1_err = Double.valueOf(value);}
			else if (name.equals("g2")) {g2 = Double.valueOf(value);}
			else if (name.equals("g2a")) {g2a = Double.valueOf(value);}
			else if (name.equals("g2b")) {g2b = Double.valueOf(value);}
			else if (name.equals("g2_err")) {g2_err = Double.valueOf(value);}
			else if (name.equals("g2del")) {g2_err = Double.valueOf(value);}
			else if (name.equals("iso")) {iso = Double.valueOf(value);}
			else if (name.equals("iso")) {iso = Double.valueOf(value);}
			else if (name.equals("iso_err")) {iso_err = Double.valueOf(value);}
//			else if (name.equals("iso1")) {iso1 = Double.valueOf(value);}
//			else if (name.equals("iso2")) {iso2 = Double.valueOf(value);}
//			else if (name.equals("iso3")) {iso3 = Double.valueOf(value);}
			else if (name.equals("label")) {sample.label = String.valueOf(value);}
			else if (name.equals("sample_nr")) {sample.sample_nr = Integer.valueOf(value);}
			else if (name.equals("prep_nr")) {sample.prep_nr = Integer.valueOf(value);}
			else if (name.equals("target_nr")) {sample.target_nr = Integer.valueOf(value);}
			else if (name.equals("magazine")) {sample.magazine = String.valueOf(value);}
			else if (name.equals("posit")) {sample.posit = Integer.valueOf(value);}
			else if (name.equals("position")) {sample.posit = Integer.valueOf(value);}
			else if (name.equals("r")) {r = Double.valueOf(value);}
			else if (name.equals("rdel")) {r_err = Double.valueOf(value);}
			else if (name.equals("r_cor")) {r_cor = Double.valueOf(value);}
			else if (name.equals("ra")) {ra = Double.valueOf(value);}
			else if (name.equals("ra_err")) {ra_err = Double.valueOf(value);}
			else if (name.equals("ra_sig_p")) {ra_sig = Double.valueOf(value)/100*ra;}
			else if (name.equals("ra_sig")) {ra_sig = Double.valueOf(value);}
			else if (name.equals("rasig")) {ra_sig = Double.valueOf(value)*1E10*ra;}
			else if (name.equals("ra_cur")) {ra_cur = Double.valueOf(value);}
			else if (name.equals("ra_cur_err")) {ra_cur_err = Double.valueOf(value);}
			else if (name.equals("ra_cur_sig")) {ra_cur_sig = Double.valueOf(value);}
			else if (name.equals("ra_bg")) {ra_bg = Double.valueOf(value);}
			else if (name.equals("ra_bg_sig")) {ra_bg_sig = Double.valueOf(value);}
			else if (name.equals("ra_bg_err")) {ra_bg_err = Double.valueOf(value);}
			else if (name.equals("ra_fin")) {ra_fin = Double.valueOf(value);}
			else if (name.equals("ra_fin_err")) {ra_fin_err = Double.valueOf(value);}
			else if (name.equals("ra_fin_sig")) {ra_fin_sig = Double.valueOf(value);}
			else if (name.equals("ra_fin2")) {;}
			else if (name.equals("ra_fin2_err")) {;}
			else if (name.equals("ra_fin2_sig")) {;}
			else if (name.equals("ra_fin2_errR")) {;}
			else if (name.equals("ra_fin2_sigR")) {;}
			else if (name.equals("ra_bl")) {ra_bl = Double.valueOf(value);}
			else if (name.equals("ra_bl_err")) {ra_bl_err = Double.valueOf(value);}
			else if (name.equals("ra_bl_sig")) {ra_bl_sig = Double.valueOf(value);}
			else if (name.equals("std_ra")) {std_ra = Double.valueOf(value);}
			else if (name.equals("std_ra_err")) {std_ra_err = Double.valueOf(value);}
			else if (name.equals("std_ra_sig")) {std_ra_sig = Double.valueOf(value);}
			else if (name.equals("active")||name.equals("ratrue")) {
				if(value.equals("+")||value.equals("*")||value.equals("0")||value.equals("false")) {
					active=false;
				} else {
					active=true;
				}
			}
			else if (name.equals("rb")) {rb = Double.valueOf(value);}
			else if (name.equals("rb_err")) {rb_err = Double.valueOf(value);}
			else if (name.equals("rb_sig_p")) {rb_sig = Double.valueOf(value)/100*rb;}
			else if (name.equals("rb_sig")) {rb_sig = Double.valueOf(value);}
			else if (name.equals("rbsig")) {rb_sig = Double.valueOf(value)*1E10*rb;}
			else if (name.equals("rb_cur")) {rb_cur = Double.valueOf(value);}
			else if (name.equals("rb_cur_err")) {rb_cur_err = Double.valueOf(value);}
			else if (name.equals("rb_cur_sig")) {rb_cur_sig = Double.valueOf(value);}
			else if (name.equals("rb_bg")) {rb_bg = Double.valueOf(value);}
			else if (name.equals("rb_bg_err")) {rb_bg_err = Double.valueOf(value);}
			else if (name.equals("rb_bg_sig")) {rb_bg_sig = Double.valueOf(value);}
			else if (name.equals("r_err")) {r_err = Double.valueOf(value);}
//cv
			else if (name.equals("rs_fin")) {rs_fin = Double.valueOf(value);}
			else if (name.equals("rs_fin_err")) {rs_fin_err = Double.valueOf(value);}
			else if (name.equals("rs_fin_sig")) {rs_fin_sig = Double.valueOf(value);}
//cv
			else if (name.equals("recno")) {recno = Integer.valueOf(value);}
			else if (name.equals("run")) {run = String.valueOf(value);}
			else if (name.equals("runtime")) {runtime = Double.valueOf(value);}
			else if (name.equals("runtime_nec")) {runtime = Double.valueOf(value)/10;}
			else if (name.equals("runtime_a")) {runtime_a = Double.valueOf(value);} 	
			else if (name.equals("runtime_b")) {runtime_b = Double.valueOf(value);} 	
			else if (name.equals("runtime_hv")) {runtime = Double.valueOf(value)*runtime_g1;}
			else if (name.equals("runtime_a_hv")) {runtime_a = Double.valueOf(value)*runtime_g1;} 	
			else if (name.equals("runtime_b_hv")) {runtime_b = Double.valueOf(value)*runtime_g1;} 	
			else if (name.equals("runtime_g1")) {runtime_g1 = Double.valueOf(value);} 	
			else if (name.equals("runtime_g2")) {runtime_g2 = Double.valueOf(value);} 	
			else if (name.equals("dt_r")) {dt_r = Double.valueOf(value);}
			else if (name.equals("dt_a")) {dt_a = Double.valueOf(value);} 	
			else if (name.equals("dt_b")) {dt_b = Double.valueOf(value);} 	
			else if (name.equals("dt_g1")) {dt_g1 = Double.valueOf(value);} 	
			else if (name.equals("dt_g2")) {dt_g2 = Double.valueOf(value);} 	
			else if (name.equals("strippr")) {stripper = Double.valueOf(value);}
			else if (name.equals("stripper")) {stripper = Double.valueOf(value);}
			else if (name.equals("sample_year")) {sample.sample_date.set(Integer.valueOf(value),0,1);}
			else if (name.equals("sampletype")) {sample.type = String.valueOf(value);}
			else if (name.equals("timedatora")) {
				SimpleDateFormat df = new SimpleDateFormat( "MM/dd/yyyy kk:mm:ss" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("timedat")) {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
//				log.debug(df.format(new Date()));
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("date")) {
				SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("date_hv")) {
				SimpleDateFormat df = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss aaa" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("time_nec")) {
				SimpleDateFormat df = new SimpleDateFormat( "EEE MMM d HH:mm:ss yyyy" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("time_eu")) {
				SimpleDateFormat df = new SimpleDateFormat( "HH:mm" );
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			} else if (name.equals("time")) {
				timedat.setTime(timedat.getTime()+Integer.valueOf(value.substring(0,2))*3600000
						+Integer.valueOf(value.substring(3,5))*60000);
			} else if (name.equals("timestamp")) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					timedat=df.parse(value);
				} catch (ParseException e) { log.debug("Wrong date format: "+value); }
			}
			else if (name.equals("tra")) {tra = Double.valueOf(value);}
			else if (name.equals("tra_sig")) { tra_sig = Double.valueOf(value);}
			else if (name.equals("trasig")) { tra_sig = Double.valueOf(value);}
			else if (name.equals("username")) {sample.username = String.valueOf(value);}
			else if (name.equals("userlabel")) {sample.userlabel = String.valueOf(value);}
			else if (name.equals("userlabel_nr")) {sample.userlabel_nr = String.valueOf(value);}
			else if (name.equals("desc1")) {sample.desc1 = String.valueOf(value);}
			else if (name.equals("desc2")) {sample.desc2 = String.valueOf(value);}
			else if (name.equals("target_comm")) {sample.target_comm = String.valueOf(value);}
			else if (name.equals("prep_bl")) {sample.prep_bl= Double.valueOf(value);}
			else if (name.equals("project")) {sample.project = String.valueOf(value);}
			else if (name.equals("priority")) {sample.priority = Integer.valueOf(value);}
			else if (name.equals("material")) {sample.material = String.valueOf(value);}
			else if (name.equals("fraction")) {sample.fraction = String.valueOf(value);}
			else if (name.equals("weight")) {sample.weight = Double.valueOf(value);}	
			else if (name.equals("editable")) {editable = Boolean.valueOf(value);}
			else if (name.equals("reduced")) {reduced = Boolean.valueOf(value);}
			else {
				String message = String.format("CalcData setValue error in run: Wrong name -> " + name + " - " + value);
	//			JOptionPane.showMessageDialog( null, message );
				log.debug(message);
			}
		}
		catch (NumberFormatException e) {;}
	}
	
	
	/**
	 * @param value Value to set
	 * @param name Field name for value
	 */
	public void set( Object value, String name ) {
		try {
			if (value==null) log.debug("Set value of "+name+" is null! (Object)");
			if (value.equals("null")){value=null;}
			else if (name.equals("a")) {a = (Double) value;}
			else if (name.equals("a_err")) {a_err = (Double) value;}
			else if (name.equals("a_cur")) {a_cur = (Double)value;}
			else if (name.equals("a_err")) {a_cur_err = (Double) value;}
			else if (name.equals("age")) {age = (Double)value;}
			else if (name.equals("age_err")) {age_err = (Double)value;}
			else if (name.equals("ana")) {ana = (Double)value;}
			else if (name.equals("anb")) {anb = (Double)value;}
			else if (name.equals("anbana")) {anbana = (Double)value;}
			else if (name.equals("anbana_err")) {anbana_err = (Double)value;}
			else if (name.equals("anban_sig")) {anban_sig = (Double)value;}
			else if (name.equals("asym")) {asym = (Double)value;}
			else if (name.equals("b")) {b = (Double)value;}
			else if (name.equals("b_err")) {b_err = (Double)value;}
			else if (name.equals("b_cur")) {b_cur = (Double)value;}
			else if (name.equals("b_err")) {b_cur_err = (Double)value;}
			else if (name.equals("ba")) {ba = (Double)value;}
			else if (name.equals("ba_err")) {ba_err = (Double)value;}
			else if (name.equals("ba_sig")) {ba_sig = (Double)value;}
			else if (name.equals("ba_sig_p")) {ba_sig = (Double)value/100;}
			else if (name.equals("ba_cur")) {ba_cur = (Double)value;}
			else if (name.equals("ba_cur_err")) {ba_cur_err = (Double)value;}
			else if (name.equals("ba_cur_sig")) {ba_cur_sig = (Double)value;}
			else if (name.equals("ba_fin")) {ba_fin = (Double)value;}
			else if (name.equals("ba_fin_err")) {ba_fin_err = (Double)value;}
			else if (name.equals("ba_fin_sig")) {ba_fin_sig = (Double)value;}
			else if (name.equals("std_ra")) {std_ra = (Double)value;}
			else if (name.equals("std_ra_err")) {std_ra_err = (Double)value;}
			else if (name.equals("std_ra_sig")) {std_ra_sig = (Double)value;}
			else if (name.equals("carrier")) {sample.carrier = (Double)value;} 	
			else if (name.equals("commentinfo")) {sample.desc1 = (String)value;}  	
			else if (name.equals("comment")) {;}  	
			else if (name.equals("corrIndex")) {corr_index = (Integer)value;}  	
			else if (name.equals("corr_index")) {corr_index = (Integer)value;}  	
			else if (name.equals("cycles")) {cycles = (Integer)value;}
			else if (name.equals("cycle")) {cycles = (Integer)value;}
			else if (name.equals("g1")) {g1 = (Double)value;}
			else if (name.equals("g1a")) {g1a = (Double)value;}
			else if (name.equals("g1b")) {g1b = (Double)value;}
			else if (name.equals("g1_err")) {g1_err = (Double)value;}
			else if (name.equals("g2")) {g2 = (Double)value;}
			else if (name.equals("g2a")) {g2a = (Double)value;}
			else if (name.equals("g2b")) {g2b = (Double)value;}
			else if (name.equals("g2_err")) {g2_err = (Double)value;}
			else if (name.equals("iso")) {iso = (Double)value;}
			else if (name.equals("iso_err")) {iso_err = (Double)value;}
//			else if (name.equals("iso1")) {iso1 = (Double)value;}
//			else if (name.equals("iso2")) {iso2 = (Double)value;}
//			else if (name.equals("iso3")) {iso3 = (Double)value;}
			else if (name.equals("label")) {sample.label = (String)value;}
			else if (name.equals("sample_nr")) {sample.sample_nr = (Integer)value;}
			else if (name.equals("prep_nr")) {sample.prep_nr = (Integer)value;}
			else if (name.equals("target_nr")) {sample.target_nr = (Integer)value;}
			else if (name.equals("magazine")) {sample.magazine = (String)value;}
			else if (name.equals("posit")) {sample.posit = (Integer)value;}
			else if (name.equals("r")) {r = (Double)value;}
			else if (name.equals("r_cor")) {r_cor = (Double)value;}
			else if (name.equals("ra")) {ra = (Double)value;}
			else if (name.equals("ra_err")) {ra_err = (Double)value;}
			else if (name.equals("ra_sig")) {ra_sig = (Double)value;}
			else if (name.equals("ra_sig_p")) {ra_sig = (Double)value/100*ra;}
			else if (name.equals("ra_cur")) {ra_cur = (Double)value;}
			else if (name.equals("ra_cur_err")) {ra_cur_err = (Double)value;}
			else if (name.equals("ra_cur_sig")) {ra_cur_sig = (Double)value;}
			else if (name.equals("ra_bg")) {ra_bg = (Double)value;}
			else if (name.equals("ra_bg_err")) {ra_bg_err = (Double)value;}
			else if (name.equals("ra_bg_sig")) {ra_bg_sig = (Double)value;}
			else if (name.equals("ra_fin")) {ra_fin = (Double)value;}
			else if (name.equals("ra_fin_err")) {ra_fin_err = (Double)value;}
			else if (name.equals("ra_fin_sig")) {ra_fin_sig = (Double)value;}
			else if (name.equals("ra_bl")) {ra_bl = (Double)value;}
			else if (name.equals("ra_bl_err")) {ra_bl_err = (Double)value;}
			else if (name.equals("ra_bl_sig")) {ra_bl_sig = (Double)value;}
			else if (name.equals("std_ra")) {std_ra = (Double)value;}
			else if (name.equals("std_ra_err")) {std_ra_err = (Double)value;}
			else if (name.equals("std_ra_sig")) {std_ra_sig = (Double)value;}
			else if (name.equals("active")) {active = (Boolean)value;}
			else if (name.equals("rb")) {rb = (Double)value;}
			else if (name.equals("rb_err")) {rb_err = (Double)value;}
			else if (name.equals("rb_sig")) {rb_sig = (Double)value;}
			else if (name.equals("rb_sig_p")) {rb_sig = (Double)value/100*rb;}
			else if (name.equals("rb_cur")) {rb_cur = (Double)value;}
			else if (name.equals("rb_cur_err")) {rb_cur_err = (Double)value;}
			else if (name.equals("rb_cur_sig")) {rb_cur_sig = (Double)value;}
			else if (name.equals("rb_bg")) {rb_bg = (Double)value;}
			else if (name.equals("rb_bg_err")) {rb_bg_err = (Double)value;}
			else if (name.equals("rb_bg_sig")) {rb_bg_sig = (Double)value;}
			else if (name.equals("rb_fin")) {rb_fin = (Double)value;}
			else if (name.equals("rb_fin_err")) {rb_fin_err = (Double)value;}
			else if (name.equals("rb_fin_sig")) {rb_fin_sig = (Double)value;}
//cv
			else if (name.equals("rs_fin")) {rs_fin = (Double)value;}
			else if (name.equals("rs_fin_err")) {rs_fin_err = (Double)value;}
			else if (name.equals("rs_fin_sig")) {rs_fin_sig = (Double)value;}
//cv
			else if (name.equals("r_err")) {r_err = (Double)value;}
			else if (name.equals("recno")) {recno = (Integer)value;}
			else if (name.equals("run")) {run = (String)value;}
			else if (name.equals("runtime")) {runtime = (Double)value;}
			else if (name.equals("runtime_a")) {runtime_a = (Double)value;} 	
			else if (name.equals("runtime_b")) {runtime_b = (Double)value;} 	
			else if (name.equals("runtime_g1")) {runtime_g1 = (Double)value;} 	
			else if (name.equals("runtime_g2")) {runtime_g2 = (Double)value;} 	
			else if (name.equals("dt_r")) {dt_r = (Double)value;}
			else if (name.equals("dt_a")) {dt_a = (Double)value;} 	
			else if (name.equals("dt_b")) {dt_b = (Double)value;} 	
			else if (name.equals("dt_g1")) {dt_g1 = (Double)value;} 	
			else if (name.equals("dt_g2")) {dt_g2 = (Double)value;} 	
			else if (name.equals("strippr")) {stripper = (Double)value;}
			else if (name.equals("stripper")) {stripper = (Double)value;}
			else if (name.equals("sample_year")) {sample.sample_date.set((Integer)value,0,1);}
			else if (name.equals("sampletype")) {sample.type = (String)value;}
//			else if (name.equals("timedat")) {timedat = (String)value;}
			else if (name.equals("time")) {time = (Double)value;} 	
//			else if (name.equals("date")) {timedat = (String)value;} 	
			else if (name.equals("tra")) {tra = (Double)value;}
			else if (name.equals("tra_sig")) { tra_sig = (Double)value;}
			else if (name.equals("username")) {sample.username = (String)value;}
			else if (name.equals("userlabel")) {sample.userlabel = (String)value;}
			else if (name.equals("userlabel_nr")) {sample.userlabel_nr = (String)value;}
			else if (name.equals("desc2")) {sample.desc2 = (String)value;}
			else if (name.equals("target_comm")) {sample.target_comm = (String)value;}
			else if (name.equals("priority")) {sample.priority = (Integer)value;}
			else if (name.equals("prep_bl")) {sample.prep_bl= (Double)value;}
			else if (name.equals("project")) {sample.project = (String)value;}
			else if (name.equals("material")) {sample.material = (String)value;}
			else if (name.equals("fraction")) {sample.fraction = (String)value;}
			else if (name.equals("weight")) {sample.weight = (Double)value;}	
			else if (name.equals("editable")) {editable = (Boolean) value;}
			else {
				String message = String.format("CalcData set error in run: Wrong name -> " + name + " - " + value+" ("+name.getClass()+")");
	//			JOptionPane.showMessageDialog( null, message );
				log.debug(message);
			}
		}
		catch (NumberFormatException e) { log.debug("NumberFormatException in set run.");}
		catch (ClassCastException e) { log.debug("ClassCastException in set run.");e.printStackTrace(System.out);}
	}
	
	
	/**
	 * @param name
	 * @return value of Object
	 */
	public Object get(String name){
		try {
			if (name.equals("a")) { return a; }
			else if (name.equals("a_err")) { return a_err; }
			else if (name.equals("a_cur")) { return a_cur; }
			else if (name.equals("a_cur_err")) { return a_cur_err; }
			else if (name.equals("a_errR")) { return a_cur_err/a; }
			else if (name.equals("age")) { return age; }
			else if (name.equals("age_err")) { return age_err; }
			else if (name.equals("age_errR")) { return age_err/age; }
			else if (name.equals("ana")) { return ana; }
			else if (name.equals("anb")) { return anb; }
			else if (name.equals("anbana")) { return anbana; }
			else if (name.equals("anbana_err")) { return anbana_err; }
			else if (name.equals("anban_sig")) { return anban_sig; }
			else if (name.equals("asym")) { return asym; }
			else if (name.equals("b")) { return b; }
			else if (name.equals("b_cur")) { return b_cur; }
			else if (name.equals("b_err")) { return b_err; }
			else if (name.equals("b_cur_err")) { return b_cur_err; }
			else if (name.equals("ba")) { return ba; }
			else if (name.equals("ba_err")) { return ba_err; }
			else if (name.equals("ba_sig")) { return ba_sig; }
			else if (name.equals("ba_errR")) { return ba_err/ba; }
			else if (name.equals("ba_sigR")) { return ba_sig/ba; }
			else if (name.equals("ba_cur")) { return ba_cur; }
			else if (name.equals("ba_cur_err")) { return ba_cur_err; }
			else if (name.equals("ba_cur_sig")) { return ba_cur_sig; }
			else if (name.equals("ba_cur_errR")) { return ba_cur_err/ba_cur; }
			else if (name.equals("ba_cur_sigR")) { return ba_cur_sig/ba_cur; }
			else if (name.equals("ba_fin")) { return ba_fin; }
			else if (name.equals("ba_fin_err")) { return ba_fin_err; }
			else if (name.equals("ba_fin_sig")) { return ba_fin_sig; }
			else if (name.equals("ba_fin_errR")) { return ba_fin_err/ba_fin; }
			else if (name.equals("ba_fin_sigR")) { return ba_fin_sig/ba_fin; }
			else if (name.equals("ba_fin_chi")) { return Math.pow(ba_fin_sig/ba_fin,2)/Math.pow(ba_err/ba,2); } 	
			else if (name.equals("std_ba")) { return std_ba; }
			else if (name.equals("std_ba_err")) { return std_ba_err; }
			else if (name.equals("std_ba_sig")) { return std_ba_sig; }
			else if (name.equals("std_ba_errR")) { return std_ba_err/std_ba; }
			else if (name.equals("std_ba_sigR")) { return std_ba_sig/std_ba; }
			else if (name.equals("std_ba_errR")) { return std_ba_err/std_ba; }
			else if (name.equals("std_ba_sigR")) { return std_ba_sig/std_ba; }
			else if (name.equals("std_ba_chi")) { return Math.pow(std_ba_sig/std_ba,2)/Math.pow(std_ba_err/std_ba,2); } 	
			else if (name.equals("bg")) { return bg; }
			else if (name.equals("bg_err")) { return bg_err; }
			else if (name.equals("bg_errR")) { return bg_err/bg; }
			else if (name.equals("carrier")) { return sample.carrier; } 	
			else if (name.equals("commentinfo")) { return sample.desc1; }  
			else if (name.equals("comment")) { return ""; }  
			else if (name.equals("corrIndex")) { return corr_index; }
			else if (name.equals("corr_index")) { return corr_index; }
			else if (name.equals("cycles")) { return cycles; }
			else if (name.equals("date")) { return timedat; }
			else if (name.equals("d13")) { return (ba_fin-1); }
			else if (name.equals("d13_err")) { return ba_fin_err; }
			else if (name.equals("d13_sig")) { return ba_fin_sig; }
			else if (name.equals("g1")) { return g1; }
			else if (name.equals("g1_t")) { return g1/runtime_g1; }
			else if (name.equals("g1_err")) { return g1_err; }
			else if (name.equals("g1_t_err")) { return g1_err/runtime_g1; }
			else if (name.equals("g1a")) { return g1a; }
			else if (name.equals("g1b")) { return g1b; }
			else if (name.equals("g1_err")) { return g1_err; }
			else if (name.equals("g2")) { return g2; }
			else if (name.equals("g2_t")) { return g2/runtime_g2; }
			else if (name.equals("g2_err")) { return g2_err; }
			else if (name.equals("g2_t_err")) { return g2_err/runtime_g2; }
			else if (name.equals("g2a")) { return g2a; }
			else if (name.equals("g2b")) { return g2b; }
			else if (name.equals("g2_err")) { return g2_err; }
			else if (name.equals("iso")) { return iso; }
			else if (name.equals("iso_t")) { return iso/runtime; }
			else if (name.equals("iso_t_err")) { return iso_err/runtime; }
			else if (name.equals("iso/a")) { return iso/a; }
			else if (name.equals("iso_err")) { return iso_err; }
//			else if (name.equals("iso1")) { return iso1; }
//			else if (name.equals("iso2")) { return iso2; }
//			else if (name.equals("iso3")) { return iso3; }
			else if (name.equals("label")) { return sample.label; }
			else if (name.equals("sample_nr")) { return sample.sample_nr; }
			else if (name.equals("prep_nr")) { return sample.prep_nr; }
			else if (name.equals("target_nr")) { return sample.target_nr; }
			else if (name.equals("magazine")) { return sample.magazine; }
			else if (name.equals("posit")) { return sample.posit; }
			else if (name.equals("position")) { return sample.posit; }
//cv
			else if (name.equals("rCl")) { return rCl; }
			else if (name.equals("rCl_err")) { return rCl_err; }
			else if (name.equals("rCl_sig")) { return rCl_sig; }
			else if (name.equals("rCl_errR")) { return rCl_err/rCl; }
			else if (name.equals("rCl_sigR")) { return rCl_sig/rCl; }
//cv
			else if (name.equals("pmC")) { return pmC; }
			else if (name.equals("pmC_err")) { return pmC_err; }
			else if (name.equals("pmC_sig")) { return pmC_sig; }
			else if (name.equals("pmC_errR")) { return pmC_err/pmC; }
			else if (name.equals("pmC_sigR")) { return pmC_sig/pmC; }
			else if (name.equals("pmC_chi")) { return Math.pow(pmC_sig/pmC,2)/Math.pow(ra_cur_err/ra_bl,2); } 	
			else if (name.equals("pmC_a")) { return pmC/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_err")) { return pmC_err/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_sig")) { return pmC_sig/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_errR")) { return pmC_err/pmC; }
			else if (name.equals("pmC_a_sigR")) { return pmC_sig/pmC; }
//			else if (name.equals("pmC_today")) { 
//				Calendar cal;
//				cal.set(1950,1,1,0,0);
//				return pmC*Math.exp((timedat-cal.getTime())/8267); }
//			else if (name.equals("pmC_today_err")) { return pmC_err; }
//			else if (name.equals("pmC_today_sig")) { return pmC_sig; }
//			else if (name.equals("pmC_today_errR")) { return pmC_err/pmC; }
//			else if (name.equals("pmC_today_sigR")) { return pmC_sig/pmC; }
			else if (name.equals("r")) { return r; }
			else if (name.equals("r_err")) { return Math.sqrt(r); }
			else if (name.equals("r_errR")) { return 1/Math.sqrt(r); }
			else if (name.equals("r_cor")) { return r_cor; }
			else if (name.equals("r_cor_t")) { return r_cor/runtime; }
			else if (name.equals("r_cor_err")) { return r_cor_err; }
			else if (name.equals("r_cor_errR")) { return r_cor_err/r_cor; }
			else if (name.equals("ra")) { return ra; }
			else if (name.equals("ra_err")) { return ra_err; }
			else if (name.equals("ra_sig")) { return ra_sig; }
			else if (name.equals("ra_errR")) { return ra_err/ra; }
			else if (name.equals("ra_sigR")) { return ra_sig/ra; }
			else if (name.equals("ra_chi")) { return Math.pow(ra_sig,2)/Math.pow(ra_err,2); } 	
			else if (name.equals("ra_cur")) { return ra_cur; }
			else if (name.equals("ra_cur_err")) { return ra_cur_err; }
			else if (name.equals("ra_cur_sig")) { return ra_cur_sig; }
			else if (name.equals("ra_cur_errR")) { return ra_cur_err/ra_cur; }
			else if (name.equals("ra_cur_sigR")) { return ra_cur_sig/ra_cur; }
			else if (name.equals("ra_cur_chi")) { return Math.pow(ra_cur_sig/ra_cur,2)/Math.pow(ra_cur_err/ra_cur,2); } 	
			else if (name.equals("ra_bg")) { return ra_bg; }
			else if (name.equals("ra_bg_err")) { return ra_bg_err; }
			else if (name.equals("ra_bg_sig")) { return ra_bg_sig; }
			else if (name.equals("ra_bg_errR")) { return ra_bg_err/ra_bg; }
			else if (name.equals("ra_bg_sigR")) { return ra_bg_sig/ra_bg; }
			else if (name.equals("ra_bg_chi")) { return Math.pow(ra_bg_sig/ra_bg,2)/Math.pow(ra_cur_err/ra_bg,2); } 	
			else if (name.equals("ra_bl")) { return ra_bl; }
			else if (name.equals("ra_bl_err")) { return ra_bl_err; }
			else if (name.equals("ra_bl_sig")) { return ra_bl_sig; }
			else if (name.equals("ra_bl_errR")) { return ra_bl_err/ra_bl; }
			else if (name.equals("ra_bl_sigR")) { return ra_bl_sig/ra_bl; }
			else if (name.equals("ra_bl_chi")) { return Math.pow(ra_bl_sig/ra_bl,2)/Math.pow(ra_cur_err/ra_bl,2); }
			else if (name.equals("ra_fin")) { return ra_fin; }
			else if (name.equals("ra_fin_err")) { return ra_fin_err; }
			else if (name.equals("ra_fin_sig")) { return ra_fin_sig; }
			else if (name.equals("ra_fin_errR")) { return ra_fin_err/ra_fin; }
			else if (name.equals("ra_fin_sigR")) { return ra_fin_sig/ra_fin; }
			else if (name.equals("ra_fin_chi")) { return Math.pow(ra_fin_sig/ra_fin,2)/Math.pow(ra_cur_err/ra_bl,2); } 	
			else if (name.equals("ra_fin2")) { return pmC/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223)*1.1822E-14; }
			else if (name.equals("ra_fin2_err")) { return Math.sqrt(Math.pow(1.1822E-14*pmC_err/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223),2)+Math.pow(0.010E-14,2)); }
			else if (name.equals("ra_fin2_sig")) { return Math.sqrt(Math.pow(1.1822E-14*pmC_sig/Math.exp((sample.sample_date.get(sample.sample_date.YEAR)-1950.0)/8223),2)+Math.pow(0.010E-14,2)); }
			else if (name.equals("ra_fin2_errR")) { return Math.sqrt(Math.pow(pmC_err/pmC,2)+Math.pow(0.011822,2)); }
			else if (name.equals("ra_fin2_sigR")) { return Math.sqrt(Math.pow(pmC_sig/pmC,2)+Math.pow(0.011822,2)); }
			else if (name.equals("std_ra")) { return std_ra; }
			else if (name.equals("std_ra_err")) { return std_ra_err; }
			else if (name.equals("std_ra_sig")) { return std_ra_sig; }
			else if (name.equals("std_ra_errR")) { return std_ra_err/std_ra; }
			else if (name.equals("std_ra_sigR")) { return std_ra_sig/std_ra; }
			else if (name.equals("std_ra_chi")) { return Math.pow(std_ra_sig/std_ra,2)/Math.pow(ra_cur_err/ra_bl,2); } 	
			else if (name.equals("active")) { return active; }
			else if (name.equals("br")) { return 1/rb; }
			else if (name.equals("rb")) { return rb; }
			else if (name.equals("rb_err")) { return rb_err; }
			else if (name.equals("rb_sig")) { return rb_sig; }
			else if (name.equals("br_cur")) { return 1/rb_cur; }
			else if (name.equals("rb_cur")) { return rb_cur; }
			else if (name.equals("rb_cur_err")) { return rb_cur_err; }
			else if (name.equals("rb_cur_sig")) { return rb_cur_sig; }
			else if (name.equals("rb_cur_errR")) { return rb_cur_err/rb_cur; }
			else if (name.equals("rb_cur_sigR")) { return rb_cur_sig/rb_cur; }
			else if (name.equals("br_bg")) { return 1/rb_bg; }
			else if (name.equals("rb_bg")) { return rb_bg; }
			else if (name.equals("rb_bg_err")) { return rb_bg_err; }
			else if (name.equals("rb_bg_sig")) { return rb_bg_sig; }
			else if (name.equals("rb_bg_errR")) { return rb_bg_err/rb_bg; }
			else if (name.equals("rb_bg_sigR")) { return rb_bg_sig/rb_bg; }
			else if (name.equals("br_fin")) { return 1/rb_fin; }
			else if (name.equals("rb_fin")) { return rb_fin; }
			else if (name.equals("rb_fin_err")) { return rb_fin_err; }
			else if (name.equals("rb_fin_sig")) { return rb_fin_sig; }
			else if (name.equals("rb_fin_errR")) { return rb_fin_err/rb_fin; }
			else if (name.equals("rb_fin_sigR")) { return rb_fin_sig/rb_fin; }
			else if (name.equals("std_rb")) { return std_rb; }
			else if (name.equals("std_rb_err")) { return std_rb_err; }
			else if (name.equals("std_rb_errR")) { return std_rb_err/std_rb; }
			else if (name.equals("std_rb_sig")) { return std_rb_sig; }
			else if (name.equals("std_rb_sigR")) { return std_rb_sig/std_rb; }
			else if (name.equals("std_rb_chi")) { return Math.pow(std_rb_sig/std_rb,2)/Math.pow(std_rb_err/std_rb,2); } 	
//cv
			else if (name.equals("rs_fin_err")) { return rs_fin_err; }
			else if (name.equals("rs_fin_sig")) { return rs_fin_sig; }
			else if (name.equals("rs_fin_err")) { return rs_fin_err; }
			else if (name.equals("rs_fin_errR")) { return rs_fin_err/rs_fin; }
			else if (name.equals("rs_fin_sigR")) { return rs_fin_sig/rs_fin; }
//cv
			else if (name.equals("r_err")) { return r_err; }
			else if (name.equals("recno")) { return recno; }
			else if (name.equals("run")) { return run; }
			else if (name.equals("runtime")) { return runtime; }
			else if (name.equals("runtime_r")) { return runtime; } 	
			else if (name.equals("runtime_a")) { return runtime_a; } 	
			else if (name.equals("runtime_b")) { return runtime_b; } 	
			else if (name.equals("runtime_g1")) { return runtime_g1; } 	
			else if (name.equals("runtime_g2")) { return runtime_g2; } 	
			else if (name.equals("strippr")) { return stripper; }
			else if (name.equals("stripper")) { return stripper; }
			else if (name.equals("sample_year")) { return sample.sample_date.get(sample.sample_date.YEAR); }
			else if (name.equals("sampletype")) { return sample.type; }
			else if (name.equals("timedat")) { 
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
				return df.format(timedat); }
			else if (name.equals("time")) { return time; }
			else if (name.equals("timestamp")) { try {return new Timestamp(timedat.getTime());} catch (NullPointerException e) {return new Timestamp(new Date().getTime());} }
			else if (name.equals("tra")) { return tra; }
			else if (name.equals("tra_sig")) { return  tra_sig; }
			else if (name.equals("username")) { return sample.username; }	
			else if (name.equals("userlabel")) { return sample.userlabel; }	
			else if (name.equals("userlabel_nr")) { return sample.userlabel_nr; }	
			else if (name.equals("desc1")) { return sample.desc1; }	
			else if (name.equals("desc2")) { return sample.desc2; }	
			else if (name.equals("target_comm")) { return sample.target_comm; }	
			else if (name.equals("priority")) { return sample.priority; }	
			else if (name.equals("material")) { return sample.material; }	
			else if (name.equals("fraction")) { return sample.fraction; }	
			else if (name.equals("prep")) { return sample.prep_bl; }	
			else if (name.equals("project")) { return sample.project; }	
			else if (name.equals("editable")) {return editable;}
			else if (name.equals("reduced")) {return reduced;}
			else if (name.equals("weight")) {return sample.weight;}
			else 
			{
				String message = String.format("CalcData get error in Run: Wrong name -> '" + name + "'");
				log.debug(message);
	//			JOptionPane.showMessageDialog( null, message );
				return message;
			}
		} catch (NullPointerException e){
//			log.debug("Could not get value for: "+name);
			return null;
		}
	}
	
	/**
	 * @param runs 
	 * @param name 
	 * @throws NullPointerException 
	 */
	public void mean(ArrayList<Run> runs, String name) throws NullPointerException {
		double s1 = 0.0;
		double s2 = 0.0;
		double s3 = 0.0;
		double p=0;
		double val;
		int i;
		for (i=0; i<runs.size(); i++) {
				val = (Double)runs.get(i).get(name);
				p = runs.get(i).a * runs.get(i).runtime;
				s1 += p;
				s2 += p * val;
				s3 += p * Math.pow(val, 2);
		}
		this.set(s2 / s1, name);
		if (i>1) {
			this.set(Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(i-1)) ), name+"_sig");
		} else { 
			this.set(0, name+"_sig");
		}
	}	
		
	public double weight() {
		return runtime*a;
	}
	
	public double runtime() {
		return runtime;
	}
	
	public boolean active() {
		return active;
	}
	
	/**
	 * @return index
	 */
	public int corr_index() {
		return corr_index;
	}
	
	/**
	 * @param name
	 * @return weight
	 */
	public double weight(String name) {
		return a/runtime_a*(Double)get("runtime_"+name);
	}
	
	public Integer number() {
		return cycles;
	}
	
	public Run clone() {
	    try {
	      return (Run) super.clone();
	    } catch ( CloneNotSupportedException e ) {
	      throw new InternalError();
	    }
	  }

}
