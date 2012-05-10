import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import org.apache.log4j.Logger;


/**
 * @author lukas
 *
 */
public class Sample implements DataSet, Cloneable {
	final static Logger log = Logger.getLogger(Sample.class);
	/**  	 *   	 */  	
	public Double a;
	/**  	 *   	 */  	
	public Double a_cur; 	
	/**  	 *   	 */  	
	public Double a_err;
	/**  	 *   	 */  	
	public Double a_cur_err; 	
	/**  	 *   	 */  	
	public Boolean active;
	/**  	 *   	 */  	
	public Double age;
	/**  	 *   	 */  	
	public Double age_err;
	/**  	 *   	 */  	
	public Double ana;
	/**  	 *   	 */  	
	public Double anb;
	/**  	 *   	 */  	
	public Double anbana;
	/**  	 *   	 */  	
	public Double anbandel;
	/**  	 *   	 */  	
	public Double anbansig;
	/**  	 *   	 */  	
	public Double asym;
	/**  	 *   	 */  	
	public Double b;
	/**  	 *   	 */  	
	public Double b_cur; 	
	/**  	 *   	 */  	
	public Double b_err;
	/**  	 *   	 */  	
	public Double b_cur_err; 	
	/**  	 *   	 */  	
	public Double ba;
	/**  	 *   	 */  	
	public Double ba_err;
	/**  	 *   	 */  	
	public Double ba_sig;
	/**  	 *   	 */  	
	public Double std_ba;		// only used for standard
	/**  	 *   	 */  	
	public Double std_ba_err;		// only used for standard
	/**  	 *   	 */  	
	public Double std_ba_sig;		// only used for standard
	/**  	 *   	 */  	
	public Double ba_cur; 	
	/**  	 *   	 */  	
	public Double ba_cur_err; 	
	/**  	 *   	 */  	
	public Double ba_cur_sig;	
	/**  	 *   	 */  	
	public Double ba_fin; 	
	/**  	 *   	 */  	
	public Double ba_fin_err; 	
	/**  	 *   	 */  	
	public Double ba_fin_sig;	
	/**  	 *   	 */  	
	public Double bg;
	/**  	 *   	 */  	
	public Double bg_err;
	/**  	 *   	 */  	
	public Double carrier;
	/**  	 *   	 */  	
	public String comment;
	/**  	 *   	 */  	
	public Integer runs;
	/**  	 *   	 */  	
	public Double g1;
	/**  	 *   	 */  	
	public Double g1a;
	/**  	 *   	 */  	
	public Double g1b;
	/**  	 *   	 */  	
	public Double g1_err;
	/**  	 *   	 */  	
	public Double g2;
	/**  	 *   	 */  	
	public Double g2a;
	/**  	 *   	 */  	
	public Double g2b;
	/**  	 *   	 */  	
	public Double g2_err;
	/**  	 *   	 */  	
	public Double iso;
	/**  	 *   	 */  	
	public Double iso_err;
//	public Double iso1;	
//	public Double iso2;	
//	public Double iso3;	
	/**  	 *   	 */  	
	public String label;
	/**  	 *   	 */  	
	public Integer sample_nr;
	/**  	 *   	 */  	
	public Integer prep_nr;
	/**  	 *   	 */  	
	public Integer target_nr;
	/**  	 *   	 */  	
	public Calendar sample_date = Calendar.getInstance();
	/**  	 *   	 */  	
	public String magazine;
	/**  	 *   	 */  	
	public Integer posit;
	/**  	 *   	 */  	
	public Double prep_bl;
	/**  	 *   	 */  	
	public Double r;
	/**  	 *   	 */  	
	public Double r_err;
	/**  	 *   	 */  	
	public Double r_cor;
	/**  	 *   	 */  	
	public Double r_cor_err;
	/**  	 *   	 */  	
	public Double ra;
	/**  	 *   	 */  	
	public Double ra_err;
	/**  	 *   	 */  	
	public Double ra_sig;
	/**  	 *   	 */  	
	public Double ra_cur;	
	/**  	 *   	 */  	
	public Double ra_cur_err;	
	/**  	 *   	 */  	
	public Double ra_cur_sig;	
	/**  	 *   	 */  	
	public Double ra_bg;	
	/**  	 *   	 */  	
	public Double ra_bg_err;	
	/**  	 *   	 */  	
	public Double ra_bg_sig;	
	/**  	 *   	 */  	
	public Double ra_bl;	
	/**  	 *   	 */  	
	public Double ra_bl_err;	
	/**  	 *   	 */  	
	public Double ra_bl_sig;	
	/**  	 *   	 */  	
	public Double ra_fin;	
	/**  	 *   	 */  	
	public Double ra_fin_err;	
	/**  	 *   	 */  	
	public Double ra_fin_sig;	
	/**  	 *   	 */  	
	public Double std_ra;	
	/**  	 *   	 */  	
	public Double std_ra_err;	
	/**  	 *   	 */  	
	public Double std_ra_sig;	
	/**  	 *   	 */  	
	public Double pmC;	
	/**  	 *   	 */  	
	public Double pmC_err;	
	/**  	 *   	 */  	
	public Double pmC_sig;	
	/**  	 *   	 */  	
//cv
	public Double rCl;	
	/**  	 *   	 */  	
	public Double rCl_err;	
	/**  	 *   	 */  	
	public Double rCl_sig;	
	/**  	 *   	 */  	
//cv
	public Double rb;
	/**  	 *   	 */  	
	public Double rb_err;
	/**  	 *   	 */  	
	public Double rb_sig;
	/**  	 *   	 */  	
	public Double rb_cur;
	/**  	 *   	 */  	
	public Double rb_cur_err;
	/**  	 *   	 */  	
	public Double rb_cur_sig;
	/**  	 *   	 */  	
	public Double rb_bg;
	/**  	 *   	 */  	
	public Double rb_bg_err;
	/**  	 *   	 */  	
	public Double rb_bg_sig;
	/**  	 *   	 */  	
	public Double rb_fin;
	/**  	 *   	 */  	
	public Double rb_fin_err;
	/**  	 *   	 */  	
	public Double rb_fin_sig;
	/**  	 *   	 */  	
	public Double std_rb;
	/**  	 *   	 */  	
	public Double std_rb_err;
	/**  	 *   	 */  	
	public Double std_rb_sig;
	/**  	 *   	 */  	
	public Integer recno;
	/**  	 *   	 */  	
	public String run;
	/**  	 *   	 */  	
	public Double runtime;
	/**  	 *   	 */  	
	public Double runtime_a; 	
	/**  	 *   	 */  	
	public Double runtime_b; 	
	/**  	 *   	 */  	
	public Double runtime_g1; 	
	/**  	 *   	 */  	
	public Double runtime_g2; 	
	/**  	 *   	 */  	
	public String type; 
	/**  	 *   	 */  	
	public Double stripper;
	/**  	 *   	 */  	
	public Date timedat;
	/**  	 *   	 */  	
	public Double tra;
	/**  	 *   	 */  	
	public Double tra_sig;
	/**  	 *   	 */  	
	public String userlabel;
	/**  	 *   	 */  	
	public String userlabel_nr;
	/**  	 *   	 */  	
	public String desc1;
	/**  	 *   	 */  	
	public String desc2;
	/**  	 *   	 */  	
	public String target_comm;
	/**  	 *   	 */  	
	public String meas_comm;
	/**  	 *   	 */  	
	public String material;
	/**  	 *   	 */  	
	public String fraction;
	/**  	 *   	 */  	
	public String project;
	/**  	 *   	 */  	
	public Integer priority;
	/**  	 *   	 */  	
	public Boolean readonly;
	/**  	 *   	 */  	
	public String username;
	/**  	 *   	 */  	
	public Double weight;	
	/**  	 *   	 */  	
	public Boolean editable;
	/**  	 *   	 */  	
	public int cal1Low;
	/**  	 *   	 */  	
	public int cal1High;
	/**  	 *   	 */  	
	public int cal2Low;
	/**  	 *   	 */  	
	public int cal2High;
	/**  	 *   	 */  	
	public int cal3Low;
	/**  	 *   	 */  	
	public int cal3High;

	SimpleDateFormat date_format;
	DateFormat date_year;
	
	Sample(String label) {
		 type = "";
		 active = new Boolean(true);
		 date_format = new SimpleDateFormat( "dd.MM.yyyy" );
		 date_year = DateFormat.getDateInstance(DateFormat.YEAR_FIELD);
		 sample_date.set(1950,0,1,0,0,0);
		 editable = true;
		 prep_bl = 0.0;
		 run = "mean";
		 this.label=label;
	}
	
	/**
	 * @param value 
	 * @param name
	 */
	public void setValue( String value, String name ) {
		if (value==null) /*log.debug("Set value of "+name+" is null!")*/;
		else if (value.equals("null")){value=null;}
		else if (name.equals("a")) {a = Double.valueOf(value);}
		else if (name.equals("a_cur")) {a_cur = Double.valueOf(value);}
		else if (name.equals("a_err")) {a_err = Double.valueOf(value);}
		else if (name.equals("a_err")) {a_cur_err = Double.valueOf(value);}
		else if (name.equals("age")) {age = Double.valueOf(value);}
		else if (name.equals("age_err")) {age_err = Double.valueOf(value);}
		else if (name.equals("ana")) {ana = Double.valueOf(value);}
		else if (name.equals("anb")) {anb = Double.valueOf(value);}
		else if (name.equals("anbana")) {anbana = Double.valueOf(value);}
		else if (name.equals("anbana_err")) {anbandel = Double.valueOf(value);}
		else if (name.equals("anban_sig")) {anbansig = Double.valueOf(value);}
		else if (name.equals("asym")) {asym = Double.valueOf(value);}
		else if (name.equals("b")) {b = Double.valueOf(value);}
		else if (name.equals("b_cur")) {b_cur_err = Double.valueOf(value);}
		else if (name.equals("b_err")) {b = Double.valueOf(value);}
		else if (name.equals("b_err")) {b_cur_err = Double.valueOf(value);}
		else if (name.equals("ba")) {ba = Double.valueOf(value);}
		else if (name.equals("ba_err")) {ba_err = Double.valueOf(value);}
		else if (name.equals("ba_sig")) {ba_sig = Double.valueOf(value);}
		else if (name.equals("ba_cur")) {ba_cur = Double.valueOf(value);}
		else if (name.equals("ba_cur_err")) {ba_cur_err = Double.valueOf(value);}
		else if (name.equals("ba_cur_sig")) {ba_cur_sig = Double.valueOf(value);}
		else if (name.equals("ba_fin")) {ba_fin = Double.valueOf(value);}
		else if (name.equals("ba_fin_sig")) {ba_fin_sig = Double.valueOf(value);}
		else if (name.equals("ba_fin_err")) {ba_fin_err = Double.valueOf(value);}
		else if (name.equals("std_ba")) {std_ba = Double.valueOf(value);}
		else if (name.equals("std_ba_sig")) {std_ba_sig = Double.valueOf(value);}
		else if (name.equals("std_ba_err")) {std_ba_err = Double.valueOf(value);}
		else if (name.equals("carrier")) {carrier = Double.valueOf(value);}
		else if (name.equals("commentinfo")) {
			desc1 = String.valueOf(value);
//			run = String.valueOf(value);
			}
		else if (name.equals("comment")) { comment = value; }  	
		else if (name.equals("runs")) {runs = Integer.valueOf(value);}
		else if (name.equals("cycles")) {runs = Integer.valueOf(value);}
		else if (name.equals("g1")) {g1 = Double.valueOf(value);}
		else if (name.equals("g1a")) {g1a = Double.valueOf(value);}
		else if (name.equals("g1b")) {g1b = Double.valueOf(value);}
		else if (name.equals("g1_err")) {g1_err = Double.valueOf(value);}
		else if (name.equals("g2")) {g2 = Double.valueOf(value);}
		else if (name.equals("g2a")) {g2a = Double.valueOf(value);}
		else if (name.equals("g2b")) {g2b = Double.valueOf(value);}
		else if (name.equals("g2_err")) {g2_err = Double.valueOf(value);}
		else if (name.equals("iso")) {iso = Double.valueOf(value);}
		else if (name.equals("iso_err")) {iso_err = Double.valueOf(value);}
//		else if (name.equals("iso1")) {iso1 = Double.valueOf(value);}
//		else if (name.equals("iso2")) {iso2 = Double.valueOf(value);}
//		else if (name.equals("iso3")) {iso3 = Double.valueOf(value);}
		else if (name.equals("label")) {label = String.valueOf(value);}
		else if (name.equals("sample_nr")) {sample_nr = Integer.valueOf(value);}
		else if (name.equals("prep_nr")) {prep_nr = Integer.valueOf(value);}
		else if (name.equals("target_nr")) {target_nr = Integer.valueOf(value);}
		else if (name.equals("magazine")) {magazine = String.valueOf(value);}
		else if (name.equals("posit")) {posit = Integer.valueOf(value);}
		else if (name.equals("r")) {r = Double.valueOf(value);}
		else if (name.equals("r_err")) {r_err = Double.valueOf(value);}
		else if (name.equals("r_cor")) {r_cor = Double.valueOf(value);}
		else if (name.equals("r_cor_err")) {r_cor_err = Double.valueOf(value);}
		else if (name.equals("ra")) {ra = Double.valueOf(value);}
		else if (name.equals("ra_err")) {ra_err = Double.valueOf(value);}
		else if (name.equals("ra_sig")) {ra_sig = Double.valueOf(value);}
		else if (name.equals("ra_cur")) {ra_cur = Double.valueOf(value);}
		else if (name.equals("ra_cur_err")) {ra_cur_err = Double.valueOf(value);}
		else if (name.equals("ra_cur_sig")) {ra_cur_sig = Double.valueOf(value);}
		else if (name.equals("ra_bg")) {ra_bg = Double.valueOf(value);}
		else if (name.equals("ra_bg_err")) {ra_bg_err = Double.valueOf(value);}
		else if (name.equals("ra_bg_sig")) {ra_bg_sig = Double.valueOf(value);}
		else if (name.equals("ra_bl")) {ra_bl = Double.valueOf(value);}
		else if (name.equals("ra_bl_err")) {ra_bl_err = Double.valueOf(value);}
		else if (name.equals("ra_bl_sig")) {ra_bl_sig = Double.valueOf(value);}
		else if (name.equals("ra_fin")) {ra_fin = Double.valueOf(value);}
		else if (name.equals("ra_fin_err")) {ra_fin_err = Double.valueOf(value);}
		else if (name.equals("ra_fin_sig")) {ra_fin_sig = Double.valueOf(value);}
		else if (name.equals("std_ra")) {std_ra = Double.valueOf(value);}
		else if (name.equals("std_ra_err")) {std_ra_err = Double.valueOf(value);}
		else if (name.equals("std_ra_sig")) {std_ra_sig = Double.valueOf(value);}
		else if (name.equals("active")||name.equals("ratrue")) {
			if(value.equals("+")||value.equals("*'")) {
				active=false;
			}
			active = Boolean.valueOf(value);
		}
		else if (name.equals("rb")) {rb = Double.valueOf(value);}
		else if (name.equals("rb_err")) {rb_err = Double.valueOf(value);}
		else if (name.equals("rb_sig")) {rb_sig = Double.valueOf(value);}
		else if (name.equals("rb_cur")) {rb_cur = Double.valueOf(value);}
		else if (name.equals("rb_cur_err")) {rb_cur_err = Double.valueOf(value);}
		else if (name.equals("rb_cur_sig")) {rb_cur_sig = Double.valueOf(value);}
		else if (name.equals("rb_bg")) {rb_bg = Double.valueOf(value);}
		else if (name.equals("rb_bg_err")) {rb_bg_err = Double.valueOf(value);}
		else if (name.equals("rb_bg_sig")) {rb_bg_sig = Double.valueOf(value);}
		else if (name.equals("recno")) {recno = Integer.valueOf(value);}
//		else if (name.equals("run")) {run = String.valueOf(value);}
		else if (name.equals("runtime")) {runtime = Double.valueOf(value);}
		else if (name.equals("runtime_a")) {runtime_a = Double.valueOf(value);} 	
		else if (name.equals("runtime_b")) {runtime_b = Double.valueOf(value);} 	
		else if (name.equals("runtime_g1")) {runtime_g1 = Double.valueOf(value);} 	
		else if (name.equals("runtime_g2")) {runtime_g2 = Double.valueOf(value);} 	
		else if (name.equals("stripper")) {stripper = Double.valueOf(value);}
		else if (name.equals("sampling_date")) { 
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	//		log.debug(df.format(new Date()));
			try {
				sample_date.setTime(df.parse(value));
//				log.debug(sample_date.toString());
//				log.debug(df.parse(value));
//				log.debug(value);
			} catch (ParseException e) {
				log.debug("Wrong date format: "+value);
		}}
		else if (name.equals("sample_year")) { sample_date.set(Integer.valueOf(value),0,1); }
		else if (name.equals("sampletype")) {type = String.valueOf(value);}
		else if (name.equals("type")) {type = String.valueOf(value);}
		else if (name.equals("timedat")) {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
//			log.debug(df.format(new Date()));
			try {
				timedat=df.parse(value);
			} catch (ParseException e) {
				log.debug("Wrong date format: "+value);
			}}
		else if (name.equals("tra")) {tra = Double.valueOf(value);}
		else if (name.equals("tra_sig")) {tra_sig = Double.valueOf(value);}
		else if (name.equals("username")) {username = String.valueOf(value);}
		else if (name.equals("userlabel")) {userlabel = String.valueOf(value);}
		else if (name.equals("userlabel_nr")) {userlabel_nr = String.valueOf(value);}
		else if (name.equals("desc1")) {desc1 = String.valueOf(value);}
		else if (name.equals("desc2")) {desc2 = String.valueOf(value);}
		else if (name.equals("target_comm")) {target_comm = String.valueOf(value);}
		else if (name.equals("meas_comm")) {meas_comm = String.valueOf(value);}
		else if (name.equals("prep_bl")) {prep_bl = Double.valueOf(value);}
		else if (name.equals("project")) {project = String.valueOf(value);}
		else if (name.equals("priority")) {priority = Integer.valueOf(value);}
		else if (name.equals("material")) {material = String.valueOf(value);}
		else if (name.equals("fraction")) {fraction = String.valueOf(value);}
		else if (name.equals("weight")) {weight = Double.valueOf(value);}	
		else if (name.equals("editable")) {editable = Boolean.valueOf(value);}
		else {
			String message = String.format("CalcData set error in sample: Wrong name -> " + name + " - " + value);
			JOptionPane.showMessageDialog( null, message );
//			System.exit( 1 );
			log.warn(message);
		}
	}
		
	/**
	 * @param name
	 */
	public void set( Object value, String name ) {
		try {
			if (value!=null){ if (value.equals("null")){value=null;} }
			if (name.equals("a")) {a = (Double)value;}
			else if (name.equals("a_cur")) {a_cur = (Double)value;}
			else if (name.equals("a_err")) {a_err = (Double)value;}
			else if (name.equals("a_err")) {a_cur_err = (Double)value;}
			else if (name.equals("age")) {age = (Double)value;}
			else if (name.equals("age_err")) {age_err = (Double)value;}
			else if (name.equals("ana")) {ana = (Double)value;}
			else if (name.equals("anb")) {anb = (Double)value;}
			else if (name.equals("anbana")) {anbana = (Double)value;}
			else if (name.equals("anbana_err")) {anbandel = (Double)value;}
			else if (name.equals("anban_sig")) {anbansig = (Double)value;}
			else if (name.equals("asym")) {asym = (Double)value;}
			else if (name.equals("b")) {b = (Double)value;}
			else if (name.equals("b_cur")) {b_cur = (Double)value;}
			else if (name.equals("b_err")) {b_err = (Double)value;}
			else if (name.equals("b_err")) {b_cur_err = (Double)value;}
			else if (name.equals("ba")) {ba = (Double)value;}
			else if (name.equals("ba_err")) {ba_err = (Double)value;}
			else if (name.equals("ba_sig")) {ba_sig = (Double)value;}
			else if (name.equals("ba_fin")) {ba_fin = (Double)value;}
			else if (name.equals("ba_fin_err")) {ba_fin_err = (Double)value;}
			else if (name.equals("ba_fin_sig")) {ba_fin_sig = (Double)value;}
			else if (name.equals("ba_cur")) {ba_cur = (Double)value;}
			else if (name.equals("ba_cur_sig")) {ba_cur_sig = (Double)value;}
			else if (name.equals("ba_cur_err")) {ba_cur_err = (Double)value;}
			else if (name.equals("std_ba")) {std_ba = (Double)value;}
			else if (name.equals("std_ba_sig")) {std_ba_sig = (Double)value;}
			else if (name.equals("std_ba_err")) {std_ba_err = (Double)value;}
			else if (name.equals("carrier")) {carrier = (Double)value;} 	
			else if (name.equals("commentinfo")||name.equals("desc1")) {desc1 = (String)value;}
			else if (name.equals("comment")) { comment = (String)value; }  	
			else if (name.equals("cycles")) {runs = (Integer)value;}
			else if (name.equals("runs")) {runs = (Integer)value;}
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
			else if (name.equals("label")) {label = (String)value;}
			else if (name.equals("sample_nr")) {sample_nr = (Integer)value;}
			else if (name.equals("prep_nr")) {prep_nr = (Integer)value;}
			else if (name.equals("target_nr")) {target_nr = (Integer)value;}
			else if (name.equals("magazine")) {magazine = (String)value;}
			else if (name.equals("posit")) {posit = (Integer)value;}
			else if (name.equals("r")) {r = (Double)value;}
			else if (name.equals("r_err")) {r_err = (Double)value;}
			else if (name.equals("r_cor")) {r_cor = (Double)value;}
			else if (name.equals("r_cor_err")) {r_cor_err = (Double)value;}
			else if (name.equals("ra_bg")) {ra_bg = (Double)value;}
			else if (name.equals("ra_bg_err")) {ra_bg_err = (Double)value;}
			else if (name.equals("ra_bg_sig")) {ra_bg_sig = (Double)value;}
			else if (name.equals("ra")) {ra = (Double)value;}
			else if (name.equals("ra_err")) {ra_err = (Double)value;}
			else if (name.equals("ra_sig")) {ra_sig = (Double)value;}
			else if (name.equals("ra_cur")) {ra_cur = (Double)value;}
			else if (name.equals("ra_cur_err")) {ra_cur_err = (Double)value;}
			else if (name.equals("ra_cur_sig")) {ra_cur_sig = (Double)value;}
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
			else if (name.equals("rb_cur")) {rb_cur = (Double)value;}
			else if (name.equals("rb_cur_err")) {rb_cur_err = (Double)value;}
			else if (name.equals("rb_cur_sig")) {rb_cur_sig = (Double)value;}
			else if (name.equals("rb_bg")) {rb_bg = (Double)value;}
			else if (name.equals("rb_bg_err")) {rb_bg_err = (Double)value;}
			else if (name.equals("rb_bg_sig")) {rb_bg_sig = (Double)value;}
			else if (name.equals("rb_fin")) {rb_fin = (Double)value;}
			else if (name.equals("rb_fin_err")) {rb_fin_err = (Double)value;}
			else if (name.equals("rb_fin_sig")) {rb_fin_sig = (Double)value;}
 			else if (name.equals("recno")) {recno = (Integer)value;}
//			else if (name.equals("run")) {run = (String)value;}
			else if (name.equals("runtime")) {runtime = (Double)value;}
			else if (name.equals("runtime_a")) {runtime_a = (Double)value;} 	
			else if (name.equals("runtime_b")) {runtime_b = (Double)value;} 	
			else if (name.equals("runtime_g1")) {runtime_g1 = (Double)value;} 	
			else if (name.equals("runtime_g2")) {runtime_g2 = (Double)value;} 	
			else if (name.equals("stripper")) {stripper = (Double)value;}
			else if (name.equals("sampletype")) {type = (String)value;}
			else if (name.equals("type")) {type = (String)value;}
			else if (name.equals("sample_year")) {sample_date.set((Integer)value,0,1);}
			else if (name.equals("timedat")) {timedat = (Date)value;}
			else if (name.equals("tra")) {tra = (Double)value;}
			else if (name.equals("tra_sig")) {tra_sig = (Double)value;}
			else if (name.equals("username")) {username = (String)value;}
			else if (name.equals("userlabel")) {userlabel = (String)value;}
			else if (name.equals("userlabel_nr")) {userlabel_nr = (String)value;}
			else if (name.equals("desc2")) {desc2 = (String)value;}
			else if (name.equals("target_comm")) {target_comm = (String)value;}
			else if (name.equals("meas_comm")) {meas_comm = (String)value;}
			else if (name.equals("priority")) {priority = (Integer)value;}
			else if (name.equals("prep_bl")) {prep_bl= (Double)value;}
			else if (name.equals("project")) {project = (String)value;}
			else if (name.equals("material")) {material = (String)value;}
			else if (name.equals("fraction")) {fraction = (String)value;}
			else if (name.equals("weight")) {weight = (Double)value;}	
			else if (name.equals("editable")) {editable = (Boolean) value;}
			else if (name.equals("readonly")) {readonly = (Boolean) value;}
			else if (name.equals("ranges")) {
				try {
					cal1Low = ((ArrayList<Range>) value).get(0).ll;
					cal1High = ((ArrayList<Range>) value).get(0).hl;
					cal2Low = ((ArrayList<Range>) value).get(1).ll;
					cal2High = ((ArrayList<Range>) value).get(1).hl;
					cal3Low = ((ArrayList<Range>) value).get(2).ll;
					cal3High = ((ArrayList<Range>) value).get(2).hl;
				} catch (NullPointerException e) {;}	
			}
			else {
				String message = String.format("CalcData set error in sample: Wrong name -> " + name + " - " + value);
				log.debug(message);
			}
		}
		catch (ClassCastException e) {
			String message = String.format("CalcData set error in sample: ClassCastException -> "+name+" - "+value+" ("+name.getClass()+")");
			log.error(message);				
		}
	}
		
	/**
	 * @param name
	 * @return value of Object
	 */
	public Object get(String name){
		try {
			if (name.equals("a")) { return a; }
			else if (name.equals("a_cur")) { return a_cur; }
			else if (name.equals("a_err")) { return a_err; }
			else if (name.equals("a_cur_err")) { return a_cur_err; }
			else if (name.equals("age")) { return age; }
			else if (name.equals("age_err")) { return age_err; }
			else if (name.equals("age_errR")) { return age_err/age; }
			else if (name.equals("ana")) { return ana; }
			else if (name.equals("anb")) { return anb; }
			else if (name.equals("anbana")) { return anbana; }
			else if (name.equals("anbana_err")) { return anbandel; }
			else if (name.equals("anban_sig")) { return anbansig; }
			else if (name.equals("asym")) { return asym; }
			else if (name.equals("b")) { return b; }
			else if (name.equals("b_cur")) { return b_cur; }
			else if (name.equals("b_err")) { return b_err; }
			else if (name.equals("b_cur_err")) { return b_cur_err; }
			else if (name.equals("b_errR")) { return b_err/b; }
			else if (name.equals("b_cur_errR")) { return b_cur_err/b_cur; }
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
			else if (name.equals("std_ba_chi")) { return Math.pow(std_ba_sig/std_ba,2)/Math.pow(std_ba_err/std_ba,2); } 	
			else if (name.equals("bg")) { return bg; }
			else if (name.equals("bg_err")) { return bg_err; }
			else if (name.equals("bg_errR")) { return bg_err/bg; }
			else if (name.equals("cal1Low")) { return cal1Low; } 	
			else if (name.equals("cal1High")) { return cal1High; } 	
			else if (name.equals("cal2Low")) { return cal2Low; } 	
			else if (name.equals("cal2High")) { return cal2High; } 	
			else if (name.equals("cal3Low")) { return cal3Low; } 	
			else if (name.equals("cal3High")) { return cal3High; } 	
			else if (name.equals("carrier")) { return carrier; } 	
			else if (name.equals("commentinfo")) { return desc1; }  
			else if (name.equals("comment")) { return comment; }  
			else if (name.equals("corrIndex")) { return null; }  
			else if (name.equals("corr_index")) { return null; }  
			else if (name.equals("cycles")) { return runs; }
			else if (name.equals("runs")) { return runs; }
			else if (name.equals("date")) { return timedat; }
			else if (name.equals("d13")) { return ba_fin-1; }
			else if (name.equals("d13_err")) { return ba_fin_err; }
			else if (name.equals("d13_sig")) { return ba_fin_sig; }
			else if (name.equals("g1")) { return g1; }
			else if (name.equals("g1_t")) { return g1/runtime_g1; }
			else if (name.equals("g1_err")) { return g1_err; }
			else if (name.equals("g1_errR")) { return g1_err/g1; }
			else if (name.equals("g1_t_err")) { return g1_err/runtime_g1; }
			else if (name.equals("g1a")) { return g1a; }
			else if (name.equals("g1b")) { return g1b; }
			else if (name.equals("g2")) { return g2; }
			else if (name.equals("g2_t")) { return g2/runtime_g2; }
			else if (name.equals("g2_err")) { return g2_err; }
			else if (name.equals("g2_errR")) { return g2_err/g2; }
			else if (name.equals("g2_t_err")) { return g2_err/runtime_g2; }
			else if (name.equals("g2a")) { return g2a; }
			else if (name.equals("g2b")) { return g2b; }
			else if (name.equals("iso")) { return iso; }
			else if (name.equals("iso_t")) { return iso/runtime; }
			else if (name.equals("iso_t_err")) { return iso_err/runtime; }
			else if (name.equals("iso_err")) { return iso_err; }
			else if (name.equals("iso_errR")) { return iso_err/iso; }
//			else if (name.equals("iso1")) { return iso1; }
//			else if (name.equals("iso2")) { return iso2; }
//			else if (name.equals("iso3")) { return iso3; }
			else if (name.equals("label")) { return label; }
			else if (name.equals("sample_nr")) { return sample_nr; }
			else if (name.equals("prep_nr")) { return prep_nr; }
			else if (name.equals("target_nr")) { return target_nr; }
			else if (name.equals("magazine")) { return magazine; }
			else if (name.equals("posit")) { return posit; }
			else if (name.equals("position")) { return posit; }
			else if (name.equals("fM")) { return pmC/100; }
			else if (name.equals("fM_err")) { return pmC_err/100; }
			else if (name.equals("fM_errR")) { return pmC_err/pmC; }
			else if (name.equals("pmC")) { return pmC; }
			else if (name.equals("pmC_err")) { return pmC_err; }
			else if (name.equals("pmC_sig")) { return pmC_sig; }
			else if (name.equals("pmC_errR")) { return pmC_err/pmC; }
			else if (name.equals("pmC_sigR")) { return pmC_sig/pmC; }
			else if (name.equals("pmC_chi")) { return Math.pow(pmC_sig/pmC,2)/Math.pow(ra_cur_err/ra_bl,2); } 	
			else if (name.equals("pmC_a")) { return pmC/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_err")) { return pmC_err/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_sig")) { return pmC_sig/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223); }
			else if (name.equals("pmC_a_errR")) { return pmC_err/pmC; }
			else if (name.equals("pmC_a_sigR")) { return pmC_sig/pmC; }
//cv
			else if (name.equals("rCl")) { return rCl; }
			else if (name.equals("rCl_err")) { return rCl_err; }
			else if (name.equals("rCl_sig")) { return rCl_sig; }
			else if (name.equals("rCl_errR")) { return rCl_err/rCl; }
			else if (name.equals("rCl_sigR")) { return rCl_sig/rCl; }
//cv
			else if (name.equals("r")) { return r; }
			else if (name.equals("r_err")) { return Math.sqrt(r); }
			else if (name.equals("r_errR")) { return (1/Math.sqrt(r)); }
			else if (name.equals("r_cor")) { return r_cor; }
			else if (name.equals("r_cor_err")) { return r_cor_err; }
			else if (name.equals("r_cor_errR")) { return r_cor_err/r_cor; }
			else if (name.equals("ra")) { return ra; }
			else if (name.equals("ra_err")) { return ra_err; }
			else if (name.equals("ra_errR")) { return ra_err/ra; }
			else if (name.equals("ra_sig")) { return ra_sig; }
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
			else if (name.equals("ra_fin2")) { return ra_fin/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223)*1.1822E-2; }
			else if (name.equals("ra_fin2_err")) { return Math.sqrt(Math.pow(1.1822E-2*ra_fin_err/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223),2)+Math.pow(0.010E-2,2)); }
			else if (name.equals("ra_fin2_sig")) { return Math.sqrt(Math.pow(1.1822E-2*ra_fin_sig/Math.exp((sample_date.get(sample_date.YEAR)-1950.0)/8223),2)+Math.pow(0.010E-2,2)); }
			else if (name.equals("ra_fin2_errR")) { return Math.sqrt(Math.pow(ra_fin_err/ra_fin,2)+Math.pow(0.011822,2)); }
			else if (name.equals("ra_fin2_sigR")) { return Math.sqrt(Math.pow(ra_fin_sig/ra_fin,2)+Math.pow(0.011822,2)); }
			else if (name.equals("std_ra")) { return std_ra; }
			else if (name.equals("std_ra_err")) { return std_ra_err; }
			else if (name.equals("std_ra_errR")) { return std_ra_err/std_ra; }
			else if (name.equals("std_ra_sig")) { return std_ra_sig; }
			else if (name.equals("std_ra_sigR")) { return std_ra_sig/std_ra; }
			else if (name.equals("std_ra_chi")) { return Math.pow(std_ra_sig/std_ra,2)/Math.pow(ra_cur_err/ra_bl,2); } 	
			else if (name.equals("active")) { return active; }
			else if (name.equals("br")) { return 1/rb; }
			else if (name.equals("rb")) { return rb; }
			else if (name.equals("rb_err")) { return rb_err; }
			else if (name.equals("rb_sig")) { return rb_sig; }
			else if (name.equals("rb_errR")) { return rb_err/rb; }
			else if (name.equals("rb_sigR")) { return rb_sig/rb; }
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
			else if (name.equals("r_err")) { return r_err; }
			else if (name.equals("recno")) { return recno; }
			else if (name.equals("run")) { return run; }
			else if (name.equals("runtime")) { return runtime; }
			else if (name.equals("runtime_r")) { return runtime; } 	
			else if (name.equals("runtime_a")) { return runtime_a; } 	
			else if (name.equals("runtime_b")) { return runtime_b; } 	
			else if (name.equals("runtime_g1")) { return runtime_g1; } 	
			else if (name.equals("runtime_g2")) { return runtime_g2; } 	
			else if (name.equals("stripper")) { return stripper; }
			else if (name.equals("strippr")) { return stripper; }
			else if (name.equals("sample_year")) { return sample_date.get(sample_date.YEAR); }
			else if (name.equals("sampletype")) { return type; }
			else if (name.equals("timedat")) { 
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
				return df.format(timedat); }
			else if (name.equals("timestamp")) { try {return new Timestamp(timedat.getTime());} catch (NullPointerException e) {return new Timestamp(new Date().getTime());} }
			else if (name.equals("time")) { return null; }
			else if (name.equals("tra")) { return tra; }
			else if (name.equals("tra_sig")) { return tra_sig; }
			else if (name.equals("username")) { return username; }	
			else if (name.equals("userlabel")) { return userlabel; }	
			else if (name.equals("userlabel_nr")) { return userlabel_nr; }	
			else if (name.equals("desc1")) { return desc1; }	
			else if (name.equals("desc2")) { return desc2; }	
			else if (name.equals("target_comm")) { return target_comm; }	
			else if (name.equals("meas_comm")) { return meas_comm; }	
			else if (name.equals("priority")) { return priority; }	
			else if (name.equals("material")) { return material; }	
			else if (name.equals("fraction")) { return fraction; }	
			else if (name.equals("prep_bl")) { return prep_bl; }	
			else if (name.equals("project")) { return project; }	
			else if (name.equals("weight")) { return weight; }	
			else if (name.equals("editable")) {return editable;}
			else if (name.equals("readonly")) {return readonly;}
			else {
				String message = String.format("CalcData get error in Sample: Wrong name -> '" + name + "'");
	//				JOptionPane.showMessageDialog( null, message );
				log.debug(message);
				return message;
			}
		} catch (NullPointerException e){
//			log.debug("Could not get value for: "+name);
			return null;
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
			} else if (returnVal instanceof String) {
				returnVal = ((String) returnVal).replaceAll("&","+").replaceAll("<","-").replaceAll(">","-");
				val+="String\">";
			} else {
				val+="String\">";
			}
			val += returnVal+"</Data></Cell>\n";
		}
		return val;
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
	 * @param name
	 * @return weight
	 */
	public double weight(String name) {
		return a/runtime_a*(Double)get("runtime_"+name);
	}
	
	public Integer number() {
		return runs;
	}

	/**
	 * set value and error and sigma of "name"
	 * @param val
	 * @param name 
	 */
	public void setVES(double[] val, String name) {
		try {this.set(val[0], name);}
		catch (NullPointerException e) {log.debug("Could not set "+name+"!");}
		try {this.set(val[1], name+"_err");}
		catch (NullPointerException e) {log.debug("Could not set "+name+"_err!");}
		try {this.set(val[2], name+"_sig");}
		catch (NullPointerException e) {log.debug("Could not set "+name+"_sig!");}
	}
}
