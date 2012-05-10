import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class CalcPu extends Calc{
	
	final static Logger log = Logger.getLogger(CalcPu.class);
	
	
	CalcPu() {
		super();
		isotope = Setting.isotope;
		this.stdNom = new StdNom(isotope);
		isobar = Setting.getString("/bat/isotope/calc/bg/isobar");
		log.info("Isobar is set to "+isobar);
	}
	
	protected Corr newCorrection(){
		log.debug("new correction created");
		return new CorrPu(stdNom);
	}	
	
	protected Sample samplePreCalc(Sample sample, ArrayList<Run> runLabelList) {
		ArrayList<Run> runs = new ArrayList<Run>();
		for (int i=0; i<runLabelList.size(); i++) {
			if (runLabelList.get(i).active) {
				runs.add(runLabelList.get(i));
			}
		}
		
		if (runs.size()==0) { sample.active=false; }
		else { sample.active=true; }
		
		try {sample.runtime = Func.sum(runs, "runtime");}
		catch (NullPointerException e) {sample.runtime = null;}
		
		try {sample.runtime = Func.sum(runs, "runtime");}
		catch (NullPointerException e) {sample.runtime = null;}		
		try {sample.runtime_a = Func.sum(runs, "runtime_a");}
		catch (NullPointerException e) {sample.runtime_a = null;}		
		try {sample.runtime_b = Func.sum(runs, "runtime_b");}
		catch (NullPointerException e) {sample.runtime_b = null;}
		try {sample.runtime_g1 = Func.sum(runs, "runtime_g1");}
		catch (NullPointerException e) {sample.runtime_g1 = null;}
		try {sample.runtime_g2 = Func.sum(runs, "runtime_g2");}
		catch (NullPointerException e) {sample.runtime_g2 = null;}		
				
		try {sample.ana = Func.meanTime(runs, "ana");}
		catch (NullPointerException e) {sample.ana = null;}
		try {sample.anb = Func.meanTime(runs, "anb");}
		catch (NullPointerException e) {sample.anb = null;}
		
		sample.runs = runs.size();
		
		try {sample.r = Func.sum(runs, "r");}
		catch (NullPointerException e) {sample.r = null;}
		
		try {sample.r_err = Func.poissonErr(sample.r);}
		catch (NullPointerException e) {sample.r_err = null;}

		try { sample.r_cor = Func.sum(runs, "r_cor");}		
		catch (NullPointerException e) {sample.r_cor = null;}
		
		try { sample.r_cor_err = Func.getErr(runs, "r_cor_err");}		
		catch (NullPointerException e) {sample.r_cor_err = null;}
		
		try {sample.a = Func.sum(runs, "a");}
		catch (NullPointerException e) {sample.a = null;}
		
		try {sample.a_err = Func.poissonErr(sample.a);}
		catch (NullPointerException e) {sample.a_err = null;}

		try {sample.b = Func.sum(runs, "b");}
		catch (NullPointerException e) {sample.b = null;}
		
		try {sample.b_err = Func.poissonErr(sample.b);}
		catch (NullPointerException e) {sample.b_err = null;}

		try {sample.a_cur = Func.sum(runs, "a_cur");}
		catch (NullPointerException e) {sample.a_cur = null;}
		
		try {sample.a_cur_err = Func.poissonErr(sample.a_cur);}
		catch (NullPointerException e) {sample.a_cur_err = null;}

		try {sample.b_cur = Func.sum(runs, "b_cur");}
		catch (NullPointerException e) {sample.b_cur = null;}
		
		try {sample.b_cur_err = Func.poissonErr(sample.b);}
		catch (NullPointerException e) {sample.b_cur_err = null;}

		try {sample.g1 = Func.sum(runs, "g1");}
		catch (NullPointerException e) {sample.g1 = null;}
		
		try {sample.g1_err = Func.poissonErr(sample.g1);}
		catch (NullPointerException e) {sample.g1_err = null;}

		try {sample.g2 = Func.sum(runs, "g2");}
		catch (NullPointerException e) {sample.g2 = null;}
		
		try {sample.g2_err = Func.poissonErr(sample.g2);}
		catch (NullPointerException e) {sample.g2_err = null;}
							
		try {sample.iso = Func.sum(runs, "iso");}
		catch (NullPointerException e) {sample.iso = null;}
		
		try {sample.iso_err = Func.poissonErr(sample.iso);}
		catch (NullPointerException e) {sample.iso_err = null;}

		// calculate ra	
		try{ sample.setVES(Func.getMeanP(runs, "ra", "r"), "ra"); }
		catch (NullPointerException e) {;}
		// calculate ra_bg
		try{sample.setVES(Func.getMeanP(runs, "ra_bg", "r"), "ra_bg");}
		catch (NullPointerException e) {;}
		// calculate ba
		try{sample.setVES(Func.getMeanP(runs, "ba", "b"), "ba");}
		catch (NullPointerException e) {;}
		// calculate rb
//		try{sample.setVES(Func.getMeanP(runs, "rb", "r"), "rb");}
//		catch (NullPointerException e) {;}
		try { sample.rb = sample.ra / sample.ba; }
		catch (NullPointerException e) {;}
		try { sample.rb_err = Math.sqrt(Math.pow(sample.r_err,2) + Math.pow(sample.b_err,2)); }
		catch (NullPointerException e) {;}
		// calculate ba_cur
		try{sample.setVES(Func.getMeanP(runs, "ba_cur", "b"), "ba_cur");}
		catch (NullPointerException e) {;}
// 		calculate rb_bg
//		try{sample.setVES(Func.getMeanP(runs, "rb_bg", "r"), "rb_bg");}
//		catch (NullPointerException e) {;}
		try { sample.rb_bg = sample.ra_bg /sample.ba_cur; }
		catch (NullPointerException e) {;}
		try { sample.rb_bg_err = Math.sqrt(Math.pow(sample.r_cor_err/sample.r,2) + Math.pow(sample.b_err/sample.b,2))*sample.rb; }
		catch (NullPointerException e) {log.error(sample.r_cor_err+"-"+sample.b_err);}
		try { sample.rb_bg_sig = Func.sigmaOfMean(runs, "rb_bg"); }
		catch (NullPointerException e) {;}

		try {sample.tra = Func.meanTime(runs, "tra");}
		catch (NullPointerException e) {sample.tra = null;}
		
		return sample;
	}
	
	protected Sample sampleCalc(Sample sample, ArrayList<Run> runL, ArrayList<Corr> corrList) {
		ArrayList<Run> runs = new ArrayList<Run>();
		for (int i=0; i<runL.size(); i++) {
			if (runL.get(i).active) {
				runs.add(runL.get(i));
			}
		}
		
//		// calculate ba_fin		
//		try {sample.setVES(Func.getMean_ba_fin(runs, corrList), "ba_fin");}
//		catch (NullPointerException e) {;}
//		// calculate rb_fin		
//		try {sample.setVES(Func.getMean_rb_fin(runs, corrList), "rb_fin");}
//		catch (NullPointerException e) {;}
		try { sample.rb_fin = sample.rb_bg * (Setting.getDouble("bat/isotope/calc/nominal_ra")/Setting.getDouble("bat/isotope/calc/nominal_ba")/corrList.get(0).std.std_rb); }
		catch (NullPointerException e) {;}
		try { sample.rb_fin_err = Math.sqrt(Math.pow(sample.rb_bg_err/sample.rb_bg,2) + Math.pow(corrList.get(0).std.std_rb_err/corrList.get(0).std.std_rb,2) + Math.pow(Setting.getDouble("/bat/isotope/calc/scatter"),2))*sample.rb_fin; }
		catch (NullPointerException e) {;}
		try { sample.rb_fin_sig = sample.rb_bg_sig; }
		catch (NullPointerException e) {;}

		try { sample.ba_fin = sample.ba_cur * (Setting.getDouble("bat/isotope/calc/nominal_ba")/corrList.get(0).std.std_ba); }
		catch (NullPointerException e) {;}
		try { sample.ba_fin_err = Math.sqrt(Math.pow(sample.ba_cur_err/sample.ba_cur,2) + Math.pow(corrList.get(0).std.std_ba_err/corrList.get(0).std.std_ba,2) + Math.pow(Setting.getDouble("/bat/isotope/calc/scatter"),2))*sample.ba_fin; }
		catch (NullPointerException e) {;}
		try { sample.ba_fin_sig = sample.ba_cur_sig; }
		catch (NullPointerException e) {;}

		try { sample.ra_fin = sample.ra_bg * (Setting.getDouble("bat/isotope/calc/nominal_ra")/corrList.get(0).std.std_ra); }
		catch (NullPointerException e) {;}
		try { sample.ra_fin_err = Math.sqrt(Math.pow(sample.ra_bg_err/sample.ra_bg,2) + Math.pow(corrList.get(0).std.std_ra_err/corrList.get(0).std.std_ra,2) + Math.pow(Setting.getDouble("/bat/isotope/calc/scatter"),2))*sample.ra_fin; }
		catch (NullPointerException e) {;}
		try { sample.ra_fin_sig = sample.ra_bg_sig; }
		catch (NullPointerException e) {;}

		// calculate ra_bl
//		if (Setting.blankLabel.contains(sample.sampletype.toLowerCase()) 
//					|| sample.sampletype.equalsIgnoreCase("nb")) {
//			sample.ra_bl = sample.ra_bg;
//			sample.ra_bl_err = sample.ra_bg_err;
//			sample.ra_bl_sig = sample.ra_bg_sig;
//		} else {
//			sample.setVES(Func.getMean_ra_bl(runs, corrList), "ra_bl");
//		}
//		// calculate ra_fin	
//		if (Setting.blankLabel.contains(sample.sampletype.toLowerCase()) 
//				|| sample.sampletype.equalsIgnoreCase("nb")){
//			sample.setVES(Func.getMeanBl_ra_fin(runs, corrList), "ra_fin");
//		} else {
//			sample.setVES(Func.getMean_ra_fin(runs, corrList), "ra_fin");
//		}
		
		return sample;
	}
	
	protected void runPreCalc(ArrayList<Run> runs) {
		Run run;

		for (int i=0; i<runs.size(); i++)
		{
			run = runs.get(i);
			Corr corr = corrList.get(run.corr_index);
			// calculate background
			try {run.iso_err = Func.poissonErr(run.iso);}
			catch (NullPointerException e) {run.iso_err = null;}
			
			try {
				run.bg = corr.isoFact*(Double)run.get(isobar);
				run.bg_err = Math.sqrt(Math.pow(corr.isoErr/corr.isoFact,2)
						+ (Double)run.get(isobar+"_err")) * run.bg;
			} catch (ClassCastException e) { 
				run.bg = corr.isoFact; 
				run.bg_err = corr.isoErr*run.bg;
//				log.info("Background set constant!");
			} catch (NullPointerException e) {
//				log.info("Isobar "+isobar+" does not exist!");
				run.bg = corr.isoFact; 
				run.bg_err = corr.isoErr*run.bg;
			}
			
			try {run.a_err = Func.poissonErr(run.a);}
			catch (NullPointerException e) {run.a_err = null;e.printStackTrace();}

			try {run.b_err = Func.poissonErr(run.b);}
			catch (NullPointerException e) {run.b_err = null;e.printStackTrace();}

			try {run.r_err = Func.poissonErr(run.r);}
			catch (NullPointerException e) {run.r_err = null;e.printStackTrace();}

			try {run.g1_err = Func.poissonErr(run.g1);}
			catch (NullPointerException e) {run.g1_err = null;}

			try {run.g2_err = Func.poissonErr(run.g2);}
			catch (NullPointerException e) {run.g2_err = null;}
						
			try {run.time = 1.0*(run.timedat.getTime()-runListR.get(0).timedat.getTime())/3600000;}
			catch (NullPointerException e) {run.time = 0.0;}
						
			try {run.ba = (run.b/run.runtime_b) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.ba = null;}
			if (run.b<=0) {
				run.ba_err = 0.0;
			} else {
				try {run.ba_err = Math.sqrt(Math.pow(run.a_err/run.a,2)+Math.pow(run.b_err/run.b,2))*run.ba;}
				catch (NullPointerException e) {run.ba_err = null;}
			}
			
			try {run.ba_cur = (run.b/run.runtime_b) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.ba = null;}
			if (run.b<=0) {
				run.ba_cur_err = 0.0;
			} else {
				try {run.ba_cur_err = Math.sqrt(Math.pow(run.a_err/run.a,2)+Math.pow(run.b_err/run.b,2))*run.ba;}
				catch (NullPointerException e) {run.ba_cur_err = null;}
			}
			try {run.ba_cur_sig = run.ba_sig/run.ba*run.ba_cur;}
			catch (NullPointerException e) {run.ba_cur_sig = null;}
			
			try {run.ra = (run.r/run.runtime) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.ra = null;log.debug("Could not calculate ra!");log.debug("a/rta/r/rtr: "+run.a+"/"+run.runtime_a+"/"+run.r+"/"+run.runtime);}
			try {run.ra_err = Math.sqrt(Math.pow(run.r_err/run.r,2)+Math.pow(run.b_err/run.b,2))*run.ba;}
			catch (NullPointerException e) {run.ra_err = null;}
			
			try {run.a_cur = run.a;}
			catch (NullPointerException e) {run.a_cur = null;log.debug(e);}

			try {run.a_cur_err = run.a_err;}
			catch (NullPointerException e) {run.a_cur_err = null;log.debug(e);}

			try {run.b_cur = run.b;}
			catch (NullPointerException e) {run.b_cur = null;log.debug(e);}

			try {run.b_cur_err = run.b_err;}
			catch (NullPointerException e) {run.b_cur_err = null;log.debug(e);}

			try {run.r_cor = (run.r-run.bg*run.runtime)*(1-corr.timeCorr*run.time);}
			catch (NullPointerException e) {run.r_cor = null;log.debug(e);}
			
			try {run.r_cor_err = Math.sqrt(Math.pow(corr.isoErr*(Double)run.get(isobar),2)
					+ run.r)*(1-corr.timeCorr*run.time);}
			catch (NullPointerException e) {run.r_cor = null;log.debug(e);}

			try {run.ra_bg = (run.r_cor/run.runtime) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.ra = null;}
			if (run.r<=0) {
				run.ra_bg_err = 0.0;
			} else {
				try {run.ra_bg_err = Math.sqrt(Math.pow(run.r_cor_err/run.r_cor,2)+Math.pow(run.a_err/run.a,2))*run.ra_bg;}
				catch (NullPointerException e) {run.ra_bg_err = null;}	
			}
			try {run.ra_bg_sig = run.ra_sig*run.ra_bg/run.ra;}
			catch (NullPointerException e) {run.ra_cur_sig = null;}		
			
			try {run.rb = (run.r/run.runtime) / (run.b/run.runtime_b);}
			catch (NullPointerException e) {run.rb = null;e.printStackTrace();}	
			if (run.r<=0) {
				run.rb_err = 0.0;
			} else {
				try {run.rb_err = Math.sqrt(Math.pow(run.r_err/run.r,2)+Math.pow(run.b_err/run.b,2))*run.rb;}
				catch (NullPointerException e) {run.rb_err = null;e.printStackTrace();}	
			}
			
			try {run.rb_bg = (run.r_cor/run.runtime) / (run.b/run.runtime_b);}
			catch (NullPointerException e) {run.ra = null;}
			if (run.r<=0) {
				run.rb_bg_err = 0.0;
			} else {
				try {run.rb_bg_err = Math.sqrt(Math.pow(run.r_cor_err/run.r_cor,2)+Math.pow(run.b_err/run.b,2))*run.rb_bg;}
				catch (NullPointerException e) {run.rb_bg_err = null;}	
			}
			try {run.rb_bg_sig = run.rb_sig*run.rb_bg/run.rb;}
			catch (NullPointerException e) {run.rb_cur_sig = null;}		

			try {run.g1a = (run.g1/run.runtime_g1) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.g1a = null;}
			try {run.g1a_err = Math.sqrt(Math.pow(run.a_err/run.a,2)+Math.pow(run.g1_err/run.g1,2))*run.g1a;}
			catch (NullPointerException e) {run.g1a_err = null;}
			
			try {run.g2a = (run.g2/run.runtime_g2) / (run.a/run.runtime_a);}
			catch (NullPointerException e) {run.g2a = null;}
			try {run.g2a_err = Math.sqrt(Math.pow(run.a_err/run.a,2)+Math.pow(run.g2_err/run.g2,2))*run.g2a;}
			catch (NullPointerException e) {run.g2a_err = null;}			
		}
	}
	
	protected void runCalc(ArrayList<Run> runs, ArrayList<Corr> corrList) {
		Run run;

		for (int i=0; i<runs.size(); i++)
		{
			run = runs.get(i);
			Corr corr = corrList.get(run.corr_index);

			
			// blank is not substracted, if it is a blank or is set to nb
			if (Setting.blankLabel.contains(run.sample.type.toLowerCase()) 
					|| run.sample.type.equalsIgnoreCase("nb")){
				run.ra_bl = run.ra_bg;
				run.ra_bl_err = run.ra_bg_err;
				run.ra_bl_sig = run.ra_bg_sig;
			} else {
				run.ra_bl = (run.ra_bg - corr.blank.ra_bg);		
				try {run.ra_bl_err = Math.sqrt(Math.pow(run.ra_bg_err,2) 
						+ Math.pow(corr.blank.ra_bg_err,2)
						+ Math.pow(Setting.getDouble("/bat/isotope/calc/scatter"),2));}
				catch (NullPointerException e) {
					run.ra_bl_err = null;
				}
				try {run.ra_bl_sig = Math.sqrt(Math.pow(run.ra_bg_sig,2) 
						+ Math.pow(corr.blank.ra_bg_sig,2));}
				catch (NullPointerException e) {
					run.ra_bl_sig = null;
				}
			}

			try {run.ra_fin = run.ra_bl / corr.std.std_ra * Setting.getDouble("bat/isotope/calc/nominal_ra");}
			catch (NullPointerException e) {run.ra_fin = null;}
			try {run.ra_fin_err = Math.sqrt(Math.pow(run.ra_bl_err/Math.abs(run.ra_bl),2) 
					+ Math.pow(corr.std.std_ra_err/corr.std.std_ra,2))*run.ra_fin;} 
			catch (NullPointerException e) {run.ra_fin_err = null;}
			try {run.ra_fin_sig = Math.sqrt(Math.pow(run.ra_bl_sig/run.ra_bl,2) 
					+ Math.pow(corr.std.std_ra_sig/corr.std.std_ra,2))*run.ra_fin;} 
			catch (NullPointerException e) {run.ra_fin_sig = null;}
			
			try {run.ba_fin = run.ba_cur / corr.std.std_ba * Setting.getDouble("bat/isotope/calc/nominal_ba");}
			catch (NullPointerException e) {run.ra_fin = null;}
			try {run.ba_fin_err = Math.sqrt(Math.pow(run.ba_cur_err/Math.abs(run.ba_cur),2) 
					+ Math.pow(corr.std.std_ba_err/corr.std.std_ba,2))*run.ba_fin;} 
			catch (NullPointerException e) {run.ba_fin_err = null;}
			try {run.ba_fin_sig = Math.sqrt(Math.pow(run.ba_cur_sig/run.ba_cur,2) 
					+ Math.pow(corr.std.std_ba_sig/corr.std.std_ba,2))*run.ba_fin;} 
			catch (NullPointerException e) {run.ba_fin_sig = null;}
			
			try {run.rb_fin = run.rb_bg / (corr.std.std_rb) * (Setting.getDouble("bat/isotope/calc/nominal_ra")/Setting.getDouble("bat/isotope/calc/nominal_ba"));}
			catch (NullPointerException e) {run.rb_fin = null;}
			try {run.rb_fin_err = Math.sqrt(Math.pow(run.rb_bg_err/Math.abs(run.rb_bg),2) 
					+ Math.pow(corr.std.std_rb_err/corr.std.std_rb,2))*run.rb_fin;} 
			catch (NullPointerException e) {run.rb_fin_err = null;}
			try {run.rb_fin_sig = Math.sqrt(Math.pow(run.rb_bg_sig/run.rb_bg,2) 
					+ Math.pow(corr.std.std_rb_sig/corr.std.std_rb,2))*run.rb_fin;} 
			catch (NullPointerException e) {run.rb_fin_sig = null;}
			
		}
	}
	
	
	protected void initCalc(ArrayList<Run> runs) {
		Run run;
		
		for (int i=0; i<runs.size(); i++) {
			run = runs.get(i);
			run.sample.label = run.sample.label.replace("&","+");
			run.sample.label = run.sample.label.replace("<","-");
			run.sample.label = run.sample.label.replace(">","-");
			run.sample.desc1 = run.sample.desc1.replace("&","+");
			run.sample.desc1 = run.sample.desc1.replace("<","-");
			run.sample.desc1 = run.sample.desc1.replace(">","-");

			String number = "";
			for (int j=0; j<run.run.length(); j++) {
				if ( Character.isDigit(run.run.charAt(j)) ) {
					number+=run.run.charAt(j);
				}
			}
			try {run.recno=Integer.valueOf(number);}
			catch (NumberFormatException e) {log.error("Recno of "+run+" was not an integer: "+number);}
		}
	}
	
	protected void calcCurrent() {
		log.debug("currents are not recalculated for Pu");
	}
}
