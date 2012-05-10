import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class Corr14C extends Corr {
	final static Logger log = Logger.getLogger(Corr14C.class);
	@SuppressWarnings("unused")
	private double d13_nom;	
	
	Corr14C(StdNom stdNom) {
		super(stdNom);
		std.std_ra = -1.0;
		std.std_ba = -1.0;
		std.std_ra_err = -1.0;
		std.std_ba_err = -1.0;
	}
	
	/**
	 * 
	 */
	public Corr copy() {
		Corr corr = new Corr14C(this.stdNom);
		corr.a_slope = this.a_slope;
		corr.a_slope_off = this.a_slope_off;
		corr.b_slope = this.b_slope;
		corr.b_slope_off = this.b_slope_off;
		corr.timeCorr = this.timeCorr;
		corr.isoFact = this.isoFact;
		corr.isoErr = this.isoErr;
		return corr;
	}
	
	
	/**
	 * @param sampleList
	 * @param runList 
	 */
	public void setStandard(ArrayList<Sample> sampleList, ArrayList<Run> runList) {
		Double s1 = 0.0;
		Double s2 = 0.0;
		Double s3 = 0.0;
		Double s4 = 0.0;
		
		
		StdData stdData;
		Sample sample;
		Double p = 0.0;
		Double sd_sig = -1.0;
		int k = 0;
		
		for (int i=0; i<sampleList.size(); i++) {
			stdData = stdNom.getStd(sampleList.get(i).type);
			sample = sampleList.get(i);
			
			try {	
				sample.std_ba = sample.ba_cur * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000);
				sample.std_ba_err = sample.ba_cur_err * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000);
				sample.std_ba_sig = sample.ba_cur_sig * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000); 
			} catch (NullPointerException e) {
				log.debug("Sample standard does not exist.");
				log.debug("Number of std: "+sampleList.size());
				log.debug("Type of std: "+sampleList.get(i).type);
			}
			
			if (sample.active==true && sample.std_ba!=null){
//				p = sample.a_cur * sample.runtime;	// weight
				p = 1.0;
				s1 += p;
				s2 += p * sample.std_ba;
				s3 += p * Math.pow(sample.std_ba, 2);
				s4 += Math.pow(p*sample.std_ba_err,2);
				sd_sig = sample.std_ba_sig;
				k++;
			}
		}		
		stdS.number=k;
		stdS.std_ba = s2 / s1;
		stdSc.std_ba = s2 / s1;
		if(k>1) {
			stdS.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			stdSc.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		} else if (k==1) {
			stdS.std_ba_sig = sd_sig;
			stdSc.std_ba_sig = sd_sig;
		} else {
			stdS.std_ba_sig = 0.0;
			stdSc.std_ba_sig = 0.0;
		}
		stdS.std_ba_err = Math.sqrt(s4) / s1 ;		
		stdSc.std_ba_err = stdS.std_ba_err * Math.sqrt(k);		
		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		Double s_rt = 0.0;
		Double s_r = 0.0;
		k=0;		
		Run run;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			if (run.ba_cur!=null){
				stdData = stdNom.getStd(runList.get(i).sample.type);
				
				run.std_ba = run.ba_cur * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000);
				run.std_ba_err = run.ba_cur_err * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000);
				try {
					run.std_ba_sig = run.ba_cur_sig * Setting.getDouble("bat/isotope/calc/nominal_ba") / (1+stdData.delta/1000);
				} catch (NullPointerException e){
					run.std_ba_sig = 0.0;
				}	
			}
			if (run.active == true && run.std_ba!=null){
				p = run.a_cur * run.runtime;
				s1 += p;
				s2 += p * run.std_ba;
				s3 += p * Math.pow(run.std_ba, 2);
				s4 += Math.pow(p*runList.get(i).std_ba_err,2);
				sd_sig = run.std_ba_sig;
				k++;
			}
		}
		stdR.number=k;
		stdR.std_ba = s2 / s1;
		if(k>1) {
			stdR.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
		} else if (k==1) {
			stdR.std_ba_sig = sd_sig;
		}
		stdR.std_ba_err = Math.sqrt(s4) / s1 ;
		
		if (std.std_ra.equals(-1.0) || std.active) {
			try {
//				Double a = Math.max(stdR.std_ba_sig, stdR.std_ba_err);
//				Double b = Math.max(stdS.std_ba_sig, stdS.std_ba_err);
				std.std_ba = stdR.std_ba;
				std.std_ba_err = stdS.std_ba_err;//Math.max(a, b);
				log.debug("std 13C set: "+std.std_ba+"±"+std.std_ba_err);
			} catch (NullPointerException e) {log.warn("No std set!");}
		}
		std.std_ba_sig = null;

		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		k = 0;
		
		Double fract_corr;
		Double on_corr;
		Double s_sig = -1.0;
		d13_nom = (Setting.getDouble("bat/isotope/calc/nominal_ba")-1)*1000;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			stdData = stdNom.getStd(runList.get(i).sample.type);
			if (run.ra_bg!=null){				
				if (Setting.getBoolean("/bat/isotope/calc/fract")==true) {
					fract_corr =  Math.pow(std.std_ba/run.std_ba*(1+stdData.delta_nom/1000)/(1+stdData.delta/1000),2);
				} else {
					fract_corr =  Math.pow(1+stdData.delta_nom/1000,2) / Math.pow(1+stdData.delta/1000,2);
				}
				on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.pmC);
				run.std_ra = (run.ra_bg - blank.ra_bg) * fract_corr * on_corr;
				run.std_ra_err = run.ra_bg_err *fract_corr*on_corr;
				try {
					run.std_ra_sig = run.ra_bg_sig *fract_corr*on_corr;
				} catch (NullPointerException e) {
					;
				}
			}
			
			if (run.active == true && run.std_ra!=null){
				p = run.a_cur * run.runtime * stdData.pmC;			
				s1 += p;
				s2 += p * run.std_ra;
				s3 += p * Math.pow(run.std_ra, 2);
				s4 += Math.pow(p*run.std_ra_err,2);
				s_sig = run.std_ra_sig;
				k++;
				s_rt += run.runtime;
				s_r += run.r_cor;
			}
		}
		stdR.std_ra = s2 / s1;
		if(k>1) {
			stdR.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );	
		} else if (k==1) {
			stdR.std_ra_sig = s_sig;
		}
		if (k!=0) {
			std.a_cur = p/s_rt;
			std.r_cor = s_r;
		}
		stdR.std_ra_err = Math.sqrt(s4) / s1 ;	
		

		// 
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		Double p2;
		Double s1s = 0.0;
		Double s2s = 0.0;
		
		k = 0;

		for (int i=0; i<sampleList.size(); i++) {
			stdData = stdNom.getStd(sampleList.get(i).type);
			sample = sampleList.get(i);
			
			try {
				if (Setting.getBoolean("/bat/isotope/calc/fract")==true) {
					fract_corr =  Math.pow(std.std_ba/sample.std_ba*(1+stdData.delta_nom/1000)/(1+stdData.delta/1000),2);
				} else {
					fract_corr =  Math.pow(1+stdData.delta_nom/1000,2) / Math.pow(1+stdData.delta/1000,2);
				}
				on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.pmC);
				sample.std_ra = (sample.ra_bg - blank.ra_bg)*fract_corr*on_corr;
				sample.std_ra_err = Math.sqrt(Math.pow(sample.ra_bg_err*fract_corr*on_corr,2)
					+Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*sample.std_ra,2));
				ArrayList<Run> runLS = new ArrayList<Run>();
//				log.debug(runList.size());
				for (int j=0;j<runList.size(); j++) {
//					log.debug(runList.get(i).sample+"  "+sample+"  "+runList.get(i).sample.label+"  "+runList.get(i).sample.active);
					if (runList.get(j).sample.label.equals(sample.label)) {
						if (runList.get(j).active==true && runList.get(j).std_ra!=null){
							runLS.add(runList.get(j));
						}
					}
				}
				if (runLS.size()>1) {
					sample.std_ra_sig = Func.sigmaOfMean(runLS,"std_ra");//sample.ra_bg_sig*fract_corr*on_corr;
				} else if (runLS.size()==1){
					sample.std_ra_sig = runLS.get(0).std_ra_sig;
				} else {
					log.warn("Failed calculating std_ra_sig!");
				}
//				sample.std_ra_err = Math.sqrt(Math.pow(sample.ra_bg_err,2)+Math.pow(blank.ra_bg_err,2)) *fract_corr*on_corr;
//				sample.std_ra_sig = Math.sqrt(Math.pow(sample.ra_bg_sig,2)+Math.pow(blank.ra_bg_sig,2)) *fract_corr*on_corr;
			} catch (NullPointerException e) {
				log.debug("Sample standard does not exist.");
			}
			
			if (sample.active==true && sample.std_ra!=null){
				p = sample.a_cur * sample.runtime*stdData.pmC;	// weight
				p2 = 1/Math.pow(sample.std_ra_err,2);
				s1s +=p2;
				s2s += p2 * sample.std_ra;
				s1 += p;
				s2 += p * sample.std_ra;
				s3 += p * Math.pow(sample.std_ra, 2);
				s4 += Math.pow(p*sample.std_ra_err,2);
				s_sig = sample.std_ra_sig;
				k++;
			}
		}
		stdS.std_ra = s2s / s1s;
		stdSc.std_ra = s2 / s1;
		if(k>1) {
			stdS.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			stdSc.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		} else if (k==1) {
			stdS.std_ra_sig = s_sig;
			stdSc.std_ra_sig = s_sig;
		}
		stdS.std_ra_err = 1/Math.sqrt(s1s);     //Math.sqrt(s4) / s1 ;
		stdSc.std_ra_err = stdS.std_ra_err * Math.sqrt(k);		
					
		if (std.std_ra.equals(-1.0) || std.active) {
			try {
				if (select_mean==2) {
					std.std_ra = stdS.std_ra;
				} else {
					std.std_ra = stdR.std_ra;
				}
//				Double a = Math.max(stdR.std_ra_sig, stdR.std_ra_err);
//				Double b = Math.max(stdS.std_ra_sig, stdS.std_ra_err);
				std.std_ra_err = stdS.std_ra_err; //Math.max(a, b);
				log.info("std automatically set: "+std.std_ra+"±"+std.std_ra_err);
			} catch (NullPointerException e) {log.warn("No std set!");}
		}
		std.std_ra_sig = null;
	}	


	/**
	 * @param sampleList
	 * @param runList
	 */
	public void setBlank(ArrayList<Sample> sampleList, ArrayList<Run> runList) {
	Double s1 = 0.0;
	Double s2 = 0.0;
	Double s3 = 0.0;
	Double s4 = 0.0;		
	Double p=1.0;
	Double s_sig = -1.0;
	Double s_a = 0.0;
	Double s_r = 0.0;
	int k=0;
	
	for (int i=0; i<sampleList.size(); i++) {
		if (sampleList.get(i).active==true && sampleList.get(i).ra_bg!=null) {
			k++;
//			p = 1.0;
			s1 += p;
			s2 += p * (sampleList.get(i).ra_bg);
			s3 += p * Math.pow(sampleList.get(i).ra_bg, 2);
			s4 += Math.pow(p*(sampleList.get(i).ra_bg_err),2);
			s_sig = sampleList.get(i).ra_bg_sig;
		}
	}
	blankS.ra_bg = s2 / s1;
	blankS.number=k;
	if(k>1) {
		blankS.ra_bg_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		blankS.ra_bg_err = Math.sqrt(s4) / s1 * Math.sqrt(k);		
	} else if (k==1) {
		blankS.ra_bg_sig = s_sig;
		blankS.ra_bg_err = Math.sqrt(s4) / s1 * Math.sqrt(k);		
	} else if (k==0) {
		blankS.ra_bg = 0.0;
		blankS.ra_bg_err = 0.0;
		blankS.ra_bg_sig = 0.0;
		log.warn("No blank is set -> blank is set 0!");
	}
	
	s1 = 0.0;
	s2 = 0.0;
	s3 = 0.0;
	s4 = 0.0;
	k=0;
	Run run;
	
	for (int i=0; i<runList.size(); i++) {
		run = runList.get(i);
		if (runList.get(i).active==true) {
			p = run.a_cur * runList.get(i).runtime;
			s1 += p;
			s2 += p * (run.ra_bg);
			s3 += p * Math.pow(run.ra_bg, 2);
			s4 += Math.pow(p*(run.ra_bg_err),2);
			s_sig = run.ra_bg_sig;
			k++;
			s_a += run.a_cur;
			s_r += run.r_cor;
		}
	}
	blankR.number=k;
	blankR.ra_bg = s2 / s1;
	if(k>1) {
		blankR.ra_bg_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
		blankR.ra_bg_err = Math.sqrt(s4) / s1;
	} else if (k==0) {
		blankR.ra_bg = 0.0;
		blankR.ra_bg_err = 0.0;
		blankR.ra_bg_sig = 0.0;
	} else {
		blankR.ra_bg_sig = s_sig;
		blankR.ra_bg_err = Math.sqrt(s4) / s1;
	}
	if (k!=0) {
		blank.a_cur = s_a/k;
		blank.r_cor = s_r;
	}
	
	log.debug("blank over runs: "+blankR.ra_bg+"±"+blankR.ra_bg_err+"/"+blankR.ra_bg_sig);
	log.debug("blank over samples: "+blankS.ra_bg+"±"+blankS.ra_bg_err+"/"+blankS.ra_bg_sig);
	if (blank.ra_bg==null||blank.ra_bg.equals(-1.0)){
		blank.ra_bg = blankR.ra_bg;
		Double a = Math.max(blankR.ra_bg_sig, blankR.ra_bg_err);
		Double b = Math.max(blankS.ra_bg_sig, blankS.ra_bg_err);
		blank.ra_bg_err = Math.max(a, b);
		log.info("blank automatically set: "+blank.ra_bg+"±"+blank.ra_bg_err+"/"+blank.ra_bg_sig);
	}
	if (blank.active) {
		blank.ra_bg = blankR.ra_bg;
	}
	if (blank.active) {
		Double a = Math.max(blankR.ra_bg_sig, blankR.ra_bg_err);
		Double b = Math.max(blankS.ra_bg_sig, blankS.ra_bg_err);
		blank.ra_bg_err = Math.max(a, b);
	}
	blank.ra_bg_sig = null;

	// second blank for Pu/Pa only!!!
	if (Setting.isotope.contains("Pu")) {
		Double s_b = 0.0;
		for (int i=0; i<sampleList.size(); i++) {
			if (sampleList.get(i).active==true && sampleList.get(i).ba!=null) {
				k++;
//				p = 1.0;
				s1 += p;
				s2 += p * (sampleList.get(i).ba);
				s3 += p * Math.pow(sampleList.get(i).ba, 2);
				s4 += Math.pow(p*(sampleList.get(i).ba_err),2);
				s_sig = sampleList.get(i).ba_sig;
			}
		}
		blankS.ba_cur = s2 / s1;
		if(k>1) {
			blankS.ba_cur_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1-1) );
			blankS.ba_cur_err = Math.sqrt(s4 / s1);
		} else if (k==1) {
			blankS.ba_cur_sig = s_sig;
			blankS.ba_cur_err = Math.sqrt(s4 / s1);
		} else if (k==0) {
			blankS.ba_cur = 0.0;
			blankS.ba_cur_err = 0.0;
			blankS.ba_cur_sig = 0.0;
			log.warn("No blank is set -> blank is set 0!");
		}
		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		k=0;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			if (runList.get(i).active==true) {
				p = run.a_cur * runList.get(i).runtime;
				s1 += p;
				s2 += p * (run.ba);
				s3 += p * Math.pow(run.ba, 2);
				s4 += Math.pow(p*(run.ba_err),2);
				s_sig = run.ba_sig;
				k++;
				s_a += run.a_cur;
				s_b += run.b_cur;
			}
		}
		blankR.ba_cur = s2 / s1;
		if(k>1) {
			blankR.ba_cur_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			blankR.ba_cur_err = Math.sqrt(s4) / s1;
		} else if (k==0) {
			blankR.ba_cur = 0.0;
			blankR.ba_cur_err = 0.0;
			blankR.ba_cur_sig = 0.0;
		} else {
			blankR.ba_cur_sig = s_sig;
			blankR.ba_cur_err = Math.sqrt(s4) / s1;
		}
		if (k!=0) {
			blank.a_cur = s_a/k;
			blank.b_cur = s_b;
		}
		
		log.debug("blank over runs: "+blankR.ba_cur+"±"+blankR.ba_cur_err+"/"+blankR.ba_cur_sig);
		log.debug("blank over samples: "+blankS.ba_cur+"±"+blankS.ba_cur_err+"/"+blankS.ba_cur_sig);
		if (blank.ba_cur==null||blank.ba_cur.equals(-1.0)){
			blank.ba_cur = blankR.ba_cur;
			Double a = Math.max(blankR.ba_cur_sig, blankR.ba_cur_err);
			Double b = Math.max(blankS.ba_cur_sig, blankS.ba_cur_err);
			blank.ba_cur_err = Math.max(a, b);
			log.info("blank automatically set: "+blank.ba_cur+"±"+blank.ba_cur_err+"/"+blank.ba_cur_sig);
		}
		if (blank.active) {
			blank.ba_cur = blankR.ba_cur;
		}
		if (blank.active) {
			Double a = Math.max(blankR.ba_cur_sig, blankR.ba_cur_err);
			Double b = Math.max(blankS.ba_cur_sig, blankS.ba_cur_err);
			blank.ba_cur_err = Math.max(a, b);
		}
		blank.ba_cur_sig = blank.ba_cur_err;
		
		// as this is not jet properly implemented
		blank.ba_cur=0.0;
		blank.ba_cur_err=0.0;
		blank.ba_cur_sig=0.0;			
	}
}

}
