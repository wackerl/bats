import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class Calc36Cl extends Calc{
	
	final static Logger log = Logger.getLogger(Calc36Cl.class);
	
	
	Calc36Cl() {
		super();
		isotope = Setting.isotope;
		this.stdNom = new StdNom(isotope);
		isobar = Setting.getString("/bat/isotope/calc/bg/isobar");
		log.info("Isobar is set to "+isobar);
	}
	
	protected Corr newCorrection(){
		log.debug("new correction created");
		return new CorrDefault(stdNom);
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
		
		try {sample.a = Func.meanTime(runs, "a");}
		catch (NullPointerException e) {sample.a = null;}
		try {sample.a_cur = Func.meanTime(runs, "a_cur");}
		catch (NullPointerException e) {sample.a_cur = null;}
		
		try {sample.ana = Func.meanTime(runs, "ana");}
		catch (NullPointerException e) {sample.ana = null;}
		try {sample.anb = Func.meanTime(runs, "anb");}
		catch (NullPointerException e) {sample.anb = null;}
		try {sample.anbana = sample.anb/sample.ana;}
		catch (NullPointerException e) {sample.anbana = null;}
		
//cv
		try {sample.b = Func.meanTime(runs, "b");}
		catch (NullPointerException e) {sample.b = null;}
		try {sample.b_cur = Func.meanTime(runs, "b_cur");}
		catch (NullPointerException e) {sample.b_cur = null;}
	
		// calculate ba
		sample.setVES(Func.getMean(runs, "ba"), "ba");
		// calculate ba_cur	
		sample.setVES(Func.getMean(runs, "ba_cur"), "ba_cur");
//cv	
		sample.runs = runs.size();
		
		try {sample.r = Func.sum(runs, "r");}
		catch (NullPointerException e) {sample.r = null;}
		
		try {sample.g1 = Func.sum(runs, "g1");}
		catch (NullPointerException e) {sample.g1 = null;}
		
		try {sample.g2 = Func.sum(runs, "g2");}
		catch (NullPointerException e) {sample.g2 = null;}
		
		try {sample.iso = Func.sum(runs, "iso");}
		catch (NullPointerException e) {sample.iso = null;}
		
		try {sample.r_cor = Func.sum(runs, "r_cor");}		
		catch (NullPointerException e) {sample.r = null;}
		
		try {sample.r_err = Func.poissonErr(sample.r);}
		catch (NullPointerException e) {sample.r_err = null;}

		try {sample.iso_err = Func.poissonErr(sample.iso);}
		catch (NullPointerException e) {sample.iso_err = null;}

		try {sample.g1_err = Func.poissonErr(sample.g1);}
		catch (NullPointerException e) {sample.g1_err = null;}

		try {sample.g2_err = Func.poissonErr(sample.g2);}
		catch (NullPointerException e) {sample.g2_err = null;}
					
		// calculate ra	
		try {sample.setVES(Func.getMean(runs, "ra"), "ra");}
		catch (NullPointerException e) { ; }
		// calculate  ra_cur		
		try {sample.setVES(Func.getMean(runs, "ra_cur"), "ra_cur");}
		catch (NullPointerException e) { ; }
		// calculate ra_bg
		try {sample.setVES(Func.getMean(runs, "ra_bg"), "ra_bg");}
		catch (NullPointerException e) { ; }

		try {sample.rb = sample.r / sample.b;}
		catch (NullPointerException e) {sample.rb = null;}
		
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
		
//cv
		// calculate ba_fin		
		sample.setVES(Func.getMean_ba_fin(runs, corrList), "ba_fin");
//		log.debug("sample.ba_fin: "+sample.ba_fin);
//		log.debug("sample.ba_cur_err: "+sample.ba_cur_err);
//		log.debug("sample.ba_cur_sig: "+sample.ba_cur_sig);

//cv
		// calculate ra_bl
		try {
			if (Setting.blankLabel.contains(sample.type.toLowerCase()) 
						|| sample.type.equalsIgnoreCase("nb")) {
				sample.ra_bl = sample.ra_bg;
				sample.ra_bl_err = sample.ra_bg_err;
				sample.ra_bl_sig = sample.ra_bg_sig;
			} else {
				sample.setVES(Func.getMean_ra_bl(runs, corrList), "ra_bl");
			}
		} catch (NullPointerException e) { ; }

		// calculate ra_fin	
		try {
			if (Setting.blankLabel.contains(sample.type.toLowerCase()) 
					|| sample.type.equalsIgnoreCase("nb")){
				sample.setVES(Func.getMeanBl_ra_fin(runs, corrList), "ra_fin");
			} else {
				sample.setVES(Func.getMean_ra_fin(runs, corrList), "ra_fin");
			}
		} catch (NullPointerException e) { ; }

//cv
		// calculate rCl	
		log.debug("rCl: "+sample.ra_fin+" "+sample.ba_fin);
		try {sample.rCl = sample.ra_fin/(1+sample.ba_fin);}
		catch (NullPointerException e) {sample.ra_fin = null;}
		try {sample.rCl_err = Math.sqrt(Math.pow(sample.ra_fin_err/sample.ra_fin,2) 
				+ 2*Math.pow(sample.ba_fin_sig/sample.ba_fin,2))*sample.rCl;}
		catch (NullPointerException e) {sample.ra_fin_err = null;}
		try {sample.rCl_sig = Func.sigmaTA(runs, "rCl");}
		catch (NullPointerException e) {		
			try {sample.rCl_sig = sample.ra_fin_sig;}
			catch (NullPointerException e2) {sample.rCl_sig = null;}
		}
//cv
		
		return sample;
	}
	
	protected void runPreCalc(ArrayList<Run> runs) {
		Run run;

		for (int i=0; i<runs.size(); i++)
		{
			run = runs.get(i);
			Corr corr = corrList.get(run.corr_index);
			// calculate background
			try {run.iso_err = (Double)run.get(isobar) * iso_errR+iso_err;}
			catch (NullPointerException e) {run.iso_err = null;}
			if (run.runtime==null) {log.debug("There is no runtime for "+run.run+"!");run.runtime=-1.0;}
			try {
//				log.debug(corr.isoFact);
//				log.debug(run.get(isobar));
//				log.debug(corr.constBG);
//				log.debug(run.runtime);
//				log.debug(corr.isoErr);
//				log.debug(corr.isoFact);
//				log.debug((Double)run.get(isobar+"_err"));
//				log.debug((Double)run.get(isobar));
//				log.debug(corr.constBGErr);
//				log.debug(corr.constBG);
//cv original				run.bg = corr.isoFact*(Double)run.get(isobar)+corr.constBG*run.runtime;
				run.bg = corr.isoFact*(Double)run.get(isobar)+corr.constBG;
//		cv		
//				log.debug("run.bg: "+run.bg);

				run.bg_err = Math.sqrt(Math.pow(corr.isoErr/corr.isoFact,2)
						+ Math.pow((Double)run.get(isobar+"_err")/(Double)run.get(isobar),2) + Math.pow(corr.constBGErr/corr.constBG,2)) * run.bg;
			} catch (ClassCastException e) { 
				run.bg = 0.0; 
				run.bg_err = 0.0;
				log.info("Background set 0!");
			} catch (NullPointerException e) {
				log.info("Isobar "+isobar+" does not exist!");
				log.debug(e.toString());
				run.bg = 0.0; 
				run.bg_err = 0.0;
			}
			
			try {run.a_err = run.a * a_errR+a_err;}
			catch (NullPointerException e) {run.a_err = null;log.error(e);}

			try {run.a_cur = (run.a-a_off) * (1 + (run.a-corr.a_slope_off) * corr.a_slope);}
			catch (NullPointerException e) {run.a_cur = null;log.error(e);}
			try {run.a_cur_err = run.a_cur * a_errR+a_err;}
			catch (NullPointerException e) {run.a_cur_err = null;}
			
//cv
			try {run.b_err = run.b * b_errR+b_err;}
			catch (NullPointerException e) {run.b_cur_err = null;log.error(e);}

			try {run.b_cur = (run.b-b_off)*(1+ (run.b-corr.b_slope_off) * corr.b_slope);}
			catch (NullPointerException e) {run.b_cur = null;log.error(e);}
			try {run.b_cur_err = run.b_cur * b_errR+b_err;}
			catch (NullPointerException e) {run.b_cur_err = null;}
	
			try {run.ba = run.b / run.a;}
			catch (NullPointerException e) {run.ba = null;}
			try {run.ba_err = Math.sqrt(Math.pow(run.a_cur_err/run.a_cur,2)+Math.pow(run.b_cur_err/run.b_cur,2))*run.ba;}
			catch (NullPointerException e) {run.ba_err = null;}

			try {run.ba_cur = run.b_cur / run.a_cur;}
			catch (NullPointerException e) {run.ba_cur = null;log.error(e);}
			try {run.ba_cur_err = Math.sqrt(Math.pow(run.a_cur_err/run.a_cur,2)+Math.pow(run.b_cur_err/run.b_cur,2))*run.ba_cur;}
			catch (NullPointerException e) {run.ba_cur_err = null;}
			try {run.ba_cur_sig = run.ba_sig;}
			catch (NullPointerException e) {run.ba_cur_sig = null;}
//cv
			
			try {run.r_err = Func.poissonErr(run.r);}
			catch (NullPointerException e) {run.r_err = null;}

			try {run.g1_err = Func.poissonErr(run.g1);}
			catch (NullPointerException e) {run.g1_err = null;}

			try {run.g2_err = Func.poissonErr(run.g2);}
			catch (NullPointerException e) {run.g2_err = null;}
						
			try {run.time = 1.0*(run.timedat.getTime()-runListR.get(0).timedat.getTime())/3600000;}
			catch (NullPointerException e) {run.time = 0.0;}
						
//cv orig.			try {run.r_cor = (run.r-run.bg)*(1-corr.timeCorr*run.time);}
//cv orig.		catch (NullPointerException e) {run.r_cor = null;log.debug(e);}

			try {
				if ((run.r-run.bg)*(1-corr.timeCorr*run.time)<=0) {
					run.r_cor = 0.0;
				} else {
					run.r_cor = (run.r-run.bg)*(1-corr.timeCorr*run.time);
				}
			}
			catch (NullPointerException e) {run.r_cor = null;log.debug(e);}
//cv
			
//cv orig.			try {run.r_cor_err = Math.sqrt(Math.pow(corr.isoErr*(Double)run.get(isobar),2)
//cv orig.					+ run.r)*(1-corr.timeCorr*run.time);}
//cv orig.			catch (NullPointerException e) {run.r_cor = null;log.debug(e);}

			try {
				if ((run.r-run.bg)*(1-corr.timeCorr*run.time)<=0) {
					run.r_cor_err = Math.sqrt(Math.pow(corr.isoErr*(Double)run.get(isobar),2)
							+ run.r)*(1-corr.timeCorr*run.time);
				} else {
					run.r_cor_err = Math.sqrt(run.r)*(1-corr.timeCorr*run.time);
				}
			}
			catch (NullPointerException e) {run.r_cor_err = null;log.debug(e);}
//cv

			try {run.ra = run.r / (run.a * run.runtime * MICRO_A / charge) * 1E12;}
			catch (NullPointerException e) {run.ra = null;}	
			
			try {
				if (run.r<=0) {
					run.ra_err = 0.0;
//cv
					run.ra_sig = 0.0;
//cv
				} else {
					run.ra_err = Math.sqrt(Math.pow(run.r_err/run.r,2)+Math.pow(run.a_err/run.a,2))*run.ra;
				}
			} catch (NullPointerException e) { ; }


			try {run.ra_cur = run.r / (run.a_cur * run.runtime * MICRO_A / charge) * 1E12;}
//cv
//			catch (NullPointerException e) {run.ba_cur = null;}
			catch (NullPointerException e) {run.ra_cur = null;}
//cv		
			try {
				if (run.ra_cur<=0) {
					run.ra_cur_err = 0.0;
				} else {
					run.ra_cur_err = Math.sqrt(Math.pow(run.r_err/run.r,2)+Math.pow(run.a_cur_err/run.a_cur,2))*run.ra_cur;
				}
			} catch (NullPointerException e) { ; }

			try {
				if (run.ra_cur<=0) {
					run.ra_cur_sig = 0.0;
				} else {
					run.ra_cur_sig = run.ra_sig*run.ra_cur/run.ra;
				}
			} catch (NullPointerException e) { ; }		
//cv
			
			try {run.ra_bg = (run.r_cor) / (run.a_cur * run.runtime * MICRO_A / charge) * 1E12;}
			catch (NullPointerException e) {run.ra_bg = null;log.debug(e);}

			try {
				if (run.r_cor<=0) {
					run.ra_bg_err = 0.0;
				} else {
					run.ra_bg_err = Math.sqrt(Math.pow(run.r_cor_err/run.r_cor,2)+Math.pow(run.a_cur_err/run.a_cur,2))*run.ra_bg;
				}
			} catch (NullPointerException e) { ; }

//cv
			try {
				if (run.r_cor<=0) {
					run.ra_bg_sig = 0.0;
				} else {
					run.ra_bg_sig = run.ra_cur_sig;
				}
			} catch (NullPointerException e) { ; }
//cv
			
//			try {run.ra_bg_sig = run.ra_cur_sig;}  // is not correct, but is not so important
//			catch (NullPointerException e) {run.ra_bg_sig = null;}		
		}
	}
	
	protected void runCalc(ArrayList<Run> runs, ArrayList<Corr> corrList) {
		Run run;

		for (int i=0; i<runs.size(); i++)
		{
			run = runs.get(i);
			Corr corr = corrList.get(run.corr_index);

//cv
			try {run.ba_fin = (Setting.getDouble("bat/isotope/calc/nominal_ba")*run.ba_cur/corr.std.std_ba);}
			catch (NullPointerException e) {run.ba_fin = null;}
			try {run.ba_fin_err = Math.sqrt(Math.pow(corr.std.std_ba_err/corr.std.std_ba,2) + Math.pow(run.ba_cur_err/run.ba_cur,2))*run.ba_fin;}
			catch (NullPointerException e) {run.ba_fin_err = null;}
			try {run.ba_fin_sig = run.ba_cur_sig/run.ba_cur*run.ba_fin;}
			catch (NullPointerException e) {run.ba_fin_sig = null;}
//			log.debug("bat/isotope/calc/nominal_ba: "+Setting.getDouble("bat/isotope/calc/nominal_ba"));
//			log.debug("run.ba_cur: "+run.ba_cur);
//			log.debug("run.ba_fin: "+run.ba_fin);
//			log.debug("corr.std.std_ba: "+corr.std.std_ba);

//cv
			
			// blank is not substracted, if it is a blank or is set to nb
			if (Setting.blankLabel.contains(run.sample.type.toLowerCase()) 
					|| run.sample.type.equalsIgnoreCase("nb")){
				try {
					run.ra_bl = run.ra_bg;
					run.ra_bl_err = run.ra_bg_err;
					run.ra_bl_sig = run.ra_bg_sig;
				} catch (NullPointerException e) { log.debug("could not calculate ra_bl (nb)"); }
			} else {
				try {
					run.ra_bl = (run.ra_bg - corr.blank.ra_bg);		
					run.ra_bl_err = Math.sqrt(Math.pow(run.ra_bg_err,2) 
						+ Math.pow(corr.blank.ra_bg_err,2)
						+ Math.pow(Setting.getDouble("/bat/isotope/calc/scatter"),2));}
				catch (NullPointerException e) {
					run.ra_bl = null;
					run.ra_bl_err = null;
					log.debug("could not calculate ra_bl");
				}
				try {run.ra_bl_sig = run.ra_bg_sig;}
				catch (NullPointerException e) {
					run.ra_bl_sig = null;
				}
			}

			try {run.ra_fin = run.ra_bl / corr.std.std_ra * Setting.getDouble("bat/isotope/calc/nominal_ra");;}
			catch (NullPointerException e) {run.ra_fin = null;log.debug("could not calculate ra_fin");}

//cv
			try {
				if (run.ra_bl<=0) {
					run.ra_fin_err = 0.0;
				} else {
					run.ra_fin_err = Math.sqrt(Math.pow(run.ra_bl_err/Math.abs(run.ra_bl),2) 
							+ Math.pow(corr.std.std_ra_err/corr.std.std_ra,2))*run.ra_fin;
				}
			} catch (NullPointerException e) { ; }
			
//			try {run.ra_fin_err = Math.sqrt(Math.pow(run.ra_bl_err/Math.abs(run.ra_bl),2) 
//					+ Math.pow(corr.std.std_ra_err/corr.std.std_ra,2))*run.ra_fin;} 
//			catch (NullPointerException e) {run.ra_fin_err = null;}
//			try {run.ra_fin_sig = Math.sqrt(Math.pow(run.ra_bl_sig/run.ra_bl,2) 
//					+ Math.pow(corr.std.std_ra_sig/corr.std.std_ra,2))*run.ra_fin;} 
//			catch (NullPointerException e) {run.ra_fin_sig = null;}


			try {
				if (run.ra_bl<=0) {
					run.ra_fin_sig = 0.0;
				} else {
					run.ra_fin_sig = run.ra_bl_sig/run.ra_bl*run.ra_fin;
				}
			} catch (NullPointerException e) { ; }

//			try {run.ra_fin_sig = run.ra_bl_sig/run.ra_bl*run.ra_fin;} 
//			catch (NullPointerException e) {run.ra_fin_sig = null;}
//cv		
			
	//			log.debug("ra_err: "+run.ra_err);
	//			log.debug("ra_sig: "+run.ra_sig);
	//			log.debug("ra_cur_sig: "+run.ra_cur_sig);
	//			log.debug("ra_bg_sig: "+run.ra_bg_sig);
	//			log.debug("ra_bl_sig: "+run.ra_bl_sig);
	//			log.debug("ra_bl: "+run.ra_bl);
	//			log.debug("ra_fin: "+run.ra_fin);
	//			log.debug("ra_fin_sig: "+run.ra_fin_sig);
		}
	}
	
	
	
	protected void initCalc(ArrayList<Run> runs) {
		Run run;
		
		for (int i=0; i<runs.size(); i++) {
			run = runs.get(i);
			
			try { run.g1_err = Math.sqrt(run.g1); }
			catch (NullPointerException e) { run.g1_err=null; }
			try { run.g2_err = Math.sqrt(run.g2); }
			catch (NullPointerException e) { run.g2_err=null; }

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
		log.debug("recalc run currents only.");
		for (int i=0;i<runListL.size();i++){
			Run run=runListL.get(i);
			
			try {run.a_cur = run.a + run.a*(run.a-corrList.get(run.corr_index).a_slope_off) * corrList.get(run.corr_index).a_slope;}
			catch (NullPointerException e) {run.a_cur = null;}
			
//cv			
			try {run.b_cur = run.b + run.b*(run.b-corrList.get(run.corr_index).b_slope_off) * corrList.get(run.corr_index).b_slope;}
			catch (NullPointerException e) {run.b_cur = null;}
			
			try {run.ba = run.b / run.a;}
			catch (NullPointerException e) {run.ba = null;}

			try {run.ba_cur = run.b_cur / run.a_cur;}
			catch (NullPointerException e) {run.ba_cur = null;}
//cv
			
			try {run.ra = run.r / (run.a * run.runtime * MICRO_A / charge) * 1E12;}
			catch (NullPointerException e) {run.ra = null;}

			try {run.ra_cur = run.r / (run.a_cur * run.runtime * MICRO_A / charge) * 1E12;}
			catch (NullPointerException e) {run.ra_cur = null;}
		}
	}
}
