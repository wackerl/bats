import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class CorrPu extends Corr {
	final static Logger log = Logger.getLogger(CorrPu.class);
	@SuppressWarnings("unused")
	
	CorrPu(StdNom stdNom) {
		super(stdNom);
//		blank.ra_bg = -1.0;		
		std.std_ra = -1.0;
		std.std_ba = -1.0;
		std.std_rb = -1.0;
		std.std_ra_err = -1.0;
		std.std_ba_err = -1.0;
		std.std_rb_err = -1.0;
		std.std_ra_sig = -1.0;
		std.std_ba_sig = -1.0;
		std.std_rb_sig = -1.0;
	}
	
	/**
	 * 
	 */
	public Corr copy() {
		Corr corr = new Corr14C(this.stdNom);
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
		Double s1;
		Double s2;
		Double s3;
		Double s4;
		Double s5;
		Double s6;
		
		
		StdData stdData;
		Run run;
		Sample sample;
		Double p;
		int k = 0;
		
		// 
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		s6 = 0.0;
		k = 0;

		Double on_corr;
		Double s_sig = -1.0;
		
		for (int i=0; i<sampleList.size(); i++) {
			stdData = stdNom.getStd(sampleList.get(i).type);
			sample = sampleList.get(i);
			
			try {
				on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.ra);
				sample.std_ra = (sample.ra_bg - blank.ra_bg)*on_corr;
				sample.std_ra_err = Math.sqrt(Math.pow(sample.ra_bg_err,2)+Math.pow(blank.ra_bg_err,2) + Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*(sample.ra_bg - blank.ra_bg),2)) *on_corr;
				sample.std_ra_sig = Math.sqrt(Math.pow(sample.ra_bg_sig,2)+Math.pow(blank.ra_bg_sig,2)) *on_corr;
			} catch (NullPointerException e) {
				log.debug("Sample standard does not exist.");
//				log.debug(nom);
//				log.debug(blank.ra_bg);
//				log.debug(stdData.ra);
//				log.debug(sample.ra_bg);
			}
			
			if (sample.active==true && sample.std_ra!=null){
				p = sample.a / sample.runtime_a * sample.runtime;	// weight
				
				s1 += p;
				s2 += p * sample.std_ra;
				s3 += p * Math.pow(sample.std_ra, 2);
				s4 += Math.pow(p*sample.std_ra_err,2);
				s5 += sample.r_cor;
				s6 += sample.runtime;
				s_sig = sample.std_ra_sig;
				k++;
			}
		}
		stdS.r_cor=s5;
		stdS.runtime=s6;
		stdS.std_ra = s2 / s1;
		if(k>1) {
			stdS.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			stdSc.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		} else if (k==1) {
			stdS.std_ra_sig = s_sig;
			stdSc.std_ra_sig = s_sig;
		}
		stdS.std_ra_err = Math.sqrt(s4) / s1 ;			
		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		k = 0;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			stdData = stdNom.getStd(runList.get(i).sample.type);
			
			
			on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.ra);
			run.std_ra = (run.ra_bg - blank.ra_bg) * on_corr;
			run.std_ra_err = Math.sqrt(Math.pow(run.ra_bg_err,2)) * on_corr;
			try {
			run.std_ra_sig = Math.sqrt(Math.pow(run.ra_bg_sig,2)) * on_corr;
			} catch (NullPointerException e) {
				;
			}
			
			if (run.active == true){
				p = run.a / run.runtime_a * run.runtime;			
				s1 += p;
				s2 += p * run.std_ra;
				s3 += p * Math.pow(run.std_ra, 2);
				s4 += Math.pow(p*run.std_ra_err,2);
				s5 += run.r_cor;
				s_sig = run.std_ba_sig;
				k++;
			}
		}
		stdR.r_cor = s5;
		stdR.std_ra = s2 / s1;
		if(k>1) {
			stdR.std_ra_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );	
			stdSc.std_ra_err = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );	
		} else if (k==1) {
			stdR.std_ra_sig = s_sig;
			stdSc.std_ra_err = s_sig;
		}
		stdR.std_ra_err = Math.sqrt(s4) / s1 ;	
		
		if (std.std_ra.equals(-1.0)) {
			std.std_ra = stdS.std_ra;
//			Double a = Math.max(stdR.std_ra_sig, stdR.std_ra_err);
//			Double b = Math.max(stdS.std_ra_sig, stdS.std_ra_err);
			std.std_ra_err = stdS.std_ra_err;//Math.max(a, b);
			log.info("std automatically set: "+std.std_ra+"�"+std.std_ra_err+"/"+std.std_ra_sig);
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std")) {
			std.std_ra = stdR.std_ra;
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std_err")) {
//			Double a = Math.max(stdR.std_ra_sig, stdR.std_ra_err);
//			Double b = Math.max(stdS.std_ra_sig, stdS.std_ra_err);
			std.std_ra_err = stdS.std_ra_err;//Math.max(a, b);
		}
		std.std_ra_sig = std.std_ra_err;	
	
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		s6 = 0.0;
		k = 0;
		
		for (int i=0; i<sampleList.size(); i++) {
			stdData = stdNom.getStd(sampleList.get(i).type);
			sample = sampleList.get(i);
			
			try {
				on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.ba);
				sample.std_ba = (sample.ba - blank.ba_cur)*on_corr;
				sample.std_ba_err = Math.sqrt(Math.pow(sample.ba_err,2)+Math.pow(blank.ba_cur_err,2)+Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*(sample.ba - blank.ba_cur),2)) *on_corr;
				sample.std_ba_sig = Math.sqrt(Math.pow(sample.ba_sig,2)+Math.pow(blank.ba_cur_sig,2)) *on_corr;
			} catch (NullPointerException e) {
				log.debug("Sample standard does not exist.");
				log.debug(Setting.getDouble("bat/isotope/calc/nominal_ra"));
				log.debug(blank.ba_cur);
				log.debug(stdData.ba);
				log.debug(sample.ba);
			}
			
			if (sample.active==true && sample.std_ba!=null){
				p = sample.a / sample.runtime_a * sample.runtime_b;	// weight
				
				s1 += p;
				s2 += p * sample.std_ba;
				s3 += p * Math.pow(sample.std_ba, 2);
				s4 += Math.pow(p*sample.std_ba_err,2);
				s5 += sample.b_cur;
				s6 += sample.runtime_b;
				s_sig = sample.std_ba_sig;
				k++;
			}
		}
		stdS.b_cur = s5;
		stdS.runtime_b = s6;
		stdS.std_ba = s2 / s1;
		if(k>1) {
			stdS.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			stdSc.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		} else if (k==1) {
			stdS.std_ba_sig = s_sig;
			stdSc.std_ba_sig = s_sig;
		}
		stdS.std_ba_err = Math.sqrt(s4) / s1 ;			
		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		k = 0;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			stdData = stdNom.getStd(runList.get(i).sample.type);
						
			on_corr = Setting.getDouble("bat/isotope/calc/nominal_ba")/(stdData.ba);
			run.std_ba = (run.ba - blank.ba_cur) * on_corr;
			run.std_ba_err = Math.sqrt(Math.pow(run.ba_cur_err,2)) * on_corr;
			try {
			run.std_ba_sig = Math.sqrt(Math.pow(run.ba_cur_sig,2)) * on_corr;
			} catch (NullPointerException e) {
				;
			}
			
			if (run.active == true){
				p = run.a / run.runtime_a * run.runtime_b;			
				s1 += p;
				s2 += p * run.std_ba;
				s3 += p * Math.pow(run.std_ba, 2);
				s4 += Math.pow(p*run.std_ba_err,2);
				s5 += run.b_cur;
				s_sig = run.std_ba_sig;
				k++;
			}
		}
		stdR.b_cur = s5;
		stdR.std_ba = s2 / s1;
		if(k>1) {
			stdR.std_ba_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );	
			stdSc.std_ba_err = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );	
		} else if (k==1) {
			stdR.std_ba_sig = s_sig;
			stdSc.std_ba_err = s_sig;
		}
		stdR.std_ba_err = Math.sqrt(s4) / s1 ;	
		
		if (std.std_ba.equals(-1.0)) {
			std.std_ba = stdS.std_ba;
//			double a = Math.max(stdR.std_ba_sig, stdR.std_ba_err);
//			double b = Math.max(stdS.std_ba_sig, stdS.std_ba_err);
			std.std_ba_err = stdS.std_ba_err;//Math.max(a, b);
			log.info("std automatically set: "+std.std_ba+"±"+std.std_ba_err+"/"+std.std_ba_sig);
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std")) {
			std.std_ba = stdR.std_ba;
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std_err")) {
//			double a = Math.max(stdR.std_ba_sig, stdR.std_ba_err);
//			double b = Math.max(stdS.std_ba_sig, stdS.std_ba_err);
			std.std_ba_err = stdS.std_ba_err;//Math.max(a, b);
		}
		std.std_ba_sig = std.std_ba_err;

	
		// 
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		s6 = 0.0;
		k = 0;

		for (int i=0; i<sampleList.size(); i++) {
			stdData = stdNom.getStd(sampleList.get(i).type);
			sample = sampleList.get(i);
			
			try {
				on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/Setting.getDouble("bat/isotope/calc/nominal_ba")/(stdData.ra/stdData.ba);
				sample.std_rb = sample.std_ra/sample.std_ba;
				sample.std_rb_err = Math.sqrt(Math.pow(sample.rb_bg_err*on_corr,2) + Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*sample.std_rb,2)) ;
				sample.std_rb_sig = sample.rb_bg_sig*on_corr;
			} catch (NullPointerException e) {
				log.debug("Sample standard does not exist (Pu).");
//				log.debug(nom);
//				log.debug(blank.rb_bg);
//				log.debug(stdData.rb);
//				log.debug(sample.rb_bg);
			}
			
			if (sample.active==true && sample.std_rb!=null){
				p = sample.a / sample.runtime_a * sample.runtime_b;	// weight
				
				s1 += p;
				s2 += p * sample.std_rb;
				s3 += p * Math.pow(sample.std_rb, 2);
				s4 += Math.pow(p*sample.std_rb_err,2);
				s5 += sample.a_cur;
				s6 += sample.runtime_a;
				s_sig = sample.std_rb_sig;
				k++;
			}
		}
		stdS.a_cur = s5;
		stdS.runtime_a = s6;
		stdS.std_rb = s2 / s1;
		if(k>1) {
			stdS.std_rb_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );
			stdSc.std_rb_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );
		} else if (k==1) {
			stdS.std_rb_sig = s_sig;
			stdSc.std_rb_sig = s_sig;
		}
		stdS.std_rb_err = Math.sqrt(s4) / s1 ;			
		
		s1 = 0.0;
		s2 = 0.0;
		s3 = 0.0;
		s4 = 0.0;
		s5 = 0.0;
		k = 0;
		
		for (int i=0; i<runList.size(); i++) {
			run = runList.get(i);
			stdData = stdNom.getStd(runList.get(i).sample.type);
						
			on_corr = Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdData.ra/stdData.ba);
			run.std_rb = run.rb_bg * on_corr;
			run.std_rb_err = Math.sqrt(Math.pow(run.rb_bg_err,2)) * on_corr;
			try {
			run.std_rb_sig = Math.sqrt(Math.pow(run.rb_bg_sig,2)) * on_corr;
			} catch (NullPointerException e) {
				;
			}
			
			if (run.active == true){
				p = run.a / run.runtime_a * run.runtime_b;			
				s1 += p;
				s2 += p * run.std_rb;
				s3 += p * Math.pow(run.std_rb, 2);
				s4 += Math.pow(p*run.std_rb_err,2);
				s5 += run.a_cur;
				s_sig = run.std_ba_sig;
				k++;
			}
		}
		stdR.a_cur = s5;
		stdR.std_rb = s2 / s1;
		if(k>1) {
			stdR.std_rb_sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(k-1)) );	
			stdSc.std_rb_err = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1) );	
		} else if (k==1) {
			stdR.std_rb_sig = s_sig;
			stdSc.std_rb_err = s_sig;
		}
		stdR.std_rb_err = Math.sqrt(s4) / s1 ;	
		
		if (std.std_rb.equals(-1.0)) {
			std.std_rb = stdS.std_rb;
//			double a;
//			try {
//				a = Math.max(stdR.std_rb_sig, stdR.std_rb_err);
//			} catch (NullPointerException e) {
//				a = stdR.std_rb_err;
//			}
			log.debug(stdS.std_rb_sig);
			log.debug(stdS.std_rb_err);
//			double b;
//			try {
//				b = Math.max(stdS.std_rb_sig, stdS.std_rb_err);
//			} catch (NullPointerException e) {
//				b = stdS.std_rb_err;
//			}
			std.std_rb_err = stdS.std_rb_err;//Math.max(a, b);
			log.info("std automatically set: "+std.std_rb+"±"+std.std_rb_err+"/"+std.std_rb_sig);
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std")) {
			std.std_rb = stdR.std_rb;
		}
		if (Setting.getBoolean("/bat/isotope/calc/autocalc/std_err")) {
//			double a = Math.max(stdR.std_rb_sig, stdR.std_rb_err);
//			double b = Math.max(stdS.std_rb_sig, stdS.std_rb_err);
			std.std_rb_err = stdS.std_rb_err;//Math.max(a, b);
		}
		std.std_rb_sig = std.std_rb_err;
		
		stdSc.std_rb = stdS.r_cor/stdS.runtime / (stdS.b_cur/stdS.runtime_b) * Setting.getDouble("bat/isotope/calc/nominal_ra")/Setting.getDouble("bat/isotope/calc/nominal_ba")/(stdNom.getStd(runList.get(0).sample.type).ra / stdNom.getStd(runList.get(0).sample.type).ba);
		stdSc.std_ba = stdS.b_cur/stdS.runtime_b / (stdS.a_cur/stdS.runtime_a) * Setting.getDouble("bat/isotope/calc/nominal_ba")/(stdNom.getStd(runList.get(0).sample.type).ba);
		stdSc.std_ra = stdS.r_cor/stdS.runtime / (stdS.a_cur/stdS.runtime_a) * Setting.getDouble("bat/isotope/calc/nominal_ra")/(stdNom.getStd(runList.get(0).sample.type).ra);
		
		stdSc.rb_bg = stdS.r_cor/stdS.runtime / (stdS.b_cur/stdS.runtime_b);
		stdSc.ba_cur = stdS.b_cur/stdS.runtime_b / (stdS.a_cur/stdS.runtime_a);
		stdSc.ra_bg = stdS.r_cor/stdS.runtime / (stdS.a_cur/stdS.runtime_a);

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
