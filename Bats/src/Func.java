import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.text.NumberFormatter;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * @author lukas
 *
 */
public class Func{
	final static Logger log = Logger.getLogger(Func.class);
	
	

	/**
	 * @param runs 
	 * @param name 
	 * @return max value
	 */
	public static double getMax(ArrayList<Run> runs, String name) {
		double max = -10000000000000000000000000000000000000000.0;
		for (int i=0; i< runs.size(); i++) {
			max = Math.max(max, (Double)runs.get(i).get(name));
		}
		return max;
	}	
	
	/**
	 * @param runs 
	 * @param name1 
	 * @param name2 
	 * @return mean with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMeanC(ArrayList<Run> runs, String name1, String name2) throws NullPointerException {
		double[] result = new double[3];
		double s1 = 0.0;
		double s2 = 0.0;
		double s3 = 0.0;
		double s4 = 0.0;
		double s5 = 0.0;
		double s6 = 0.0;
		Double sig_temp = -1.0;
		double p=0;
		double val1;
		double err1;
		double val2;
		double err2;
		int i;
		for (i=0; i<runs.size(); i++) {
//			log.debug(runs.get(i).run);
//			log.debug(name);
			val1 = (Double)runs.get(i).get(name1);
			err1 = (Double)runs.get(i).get(name1+"_err");
			val2 = (Double)runs.get(i).get(name2);
			err2 = (Double)runs.get(i).get(name2+"_err");
			p = runs.get(i).weight();
			s4 = Math.pow(err1,2)+Math.pow(err2,2);
			s1 += p;
			try {
				s2 += p * val1/val2;
				s3 += p * Math.pow(val1/val2, 2);
			} catch (Exception e) 
			{
				s2+=0;
				s3+=0;
				log.debug(e.getCause());
			}
			s5 += val1;
			s6 += val2;
			sig_temp = (Double) runs.get(i).get(name1+name2+"_sig");
		}
		result[0] = s2 / s1;
		if (i>1) {
			result[2] = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(i-1)) );
		} else if (i==0){ 
			log.debug(name1+name2+"_sig set null!");result[2]=0;
		} else { 
			try {
				result[2] = sig_temp;
			} catch (NullPointerException e) { e.printStackTrace(); }
		}
		result[1] = Math.sqrt(s4);
		return result;
	}	
	
	
	/**
	 * @param runs 
	 * @param name 
	 * @return mean with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMean(ArrayList<Run> runs, String name) throws NullPointerException {
		double[] result = new double[3];
		double s1 = 0.0;
		double s2 = 0.0;
		double s3 = 0.0;
		double s4 = 0.0;
		Double sig_temp = -1.0;
		double p=0;
		double val;
		double err;
		int i;
		for (i=0; i<runs.size(); i++) {
//			log.debug(runs.get(i).run);
//			log.debug(name);
				val = (Double)runs.get(i).get(name);
				err = (Double)runs.get(i).get(name+"_err");
				p = runs.get(i).weight();
				s1 += p;
				s2 += p * val;
				s3 += p * Math.pow(val, 2);
				s4 += Math.pow(p*err,2);
				sig_temp = (Double) runs.get(i).get(name+"_sig");
		}
		result[0] = s2 / s1;
		if (i>1) {
			result[2] = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(i-1)) );
		} else if (i==0){ 
			log.debug(name+"_sig set null!");result[2]=0;
		} else { 
			try {
				result[2] = sig_temp;
			} catch (NullPointerException e) {;}
		}
		result[1] = Math.sqrt(s4)/s1;
		return result;
	}	
	
	
	/**
	 * @param runs 
	 * @param name 
	 * @param wt 
	 * @return mean with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMeanP(ArrayList<Run> runs, String name, String wt) throws NullPointerException {
		double[] result = new double[3];
		double s1 = 0.0;
		double s2 = 0.0;
		double s3 = 0.0;
		double s4 = 0.0;
		Double sig_temp = -1.0;
		double p=0;
		double val;
		double err;
		int i;
		for (i=0; i<runs.size(); i++) {
//			log.debug(runs.get(i).run);
//			log.debug(name);
				val = (Double)runs.get(i).get(name);
				err = (Double)runs.get(i).get(name+"_err");
				p = runs.get(i).weight(wt);
				s1 += p;
				s2 += p * val;
				s3 += p * Math.pow(val, 2);
				s4 += Math.pow(p*err,2);
				sig_temp = (Double) runs.get(i).get(name+"_sig");
		}
		result[0] = s2 / s1;
		if (i>1) {
			result[2] = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(i-1)) );
		} else if (i==0){ 
			log.debug(name+"_sig set null!");result[2]=0;
		} else { 
			try {
				result[2] = sig_temp;
			} catch (NullPointerException e) { e.printStackTrace(); }
		}
		result[1] = Math.sqrt(s4)/s1;
		return result;
	}	
	
	
	/**
	 * @param runs 
	 * @param corrList 
	 * @return calculated ba_fin with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMean_ba_fin(ArrayList<Run> runs, ArrayList<Corr> corrList) throws NullPointerException {
		double[] result = new double[3];
		Double dev = 0.0;
		Double weight = 0.0;
		Double sig_temp = -1.0;
		Corr corr;
		double s1 = 0.0;
		double s2 = 0.0;
		double s4 = 0.0;
		double s6 = 0.0;
		double s7 = 0.0;
		double s8 = 0.0;
		double p=0;
		int k=0;
		for (int j=0; j<corrList.size(); j++) {
			corr = corrList.get(j);
			s1 = 0.0;
			s2 = 0.0;
			s4 = 0.0;
			p=0;
			for (int i=0; i<runs.size(); i++) {
				if (runs.get(i).active && runs.get(i).corr_index==j) {
					if (Setting.isotope.contains("P")) {
						p = runs.get(i).a / runs.get(i).runtime_a * runs.get(i).runtime_b;
					} else {
						p = runs.get(i).a * runs.get(i).runtime;
					}
					s1 += p;
					s2 += p * runs.get(i).ba_cur;
					s4 += Math.pow(p*runs.get(i).ba_cur_err,2);
					sig_temp = runs.get(i).ba_fin_sig;
				}
			}
			weight += s1;
			s6 += s4 * Math.pow(Setting.getDouble("bat/isotope/calc/nominal_ba")/corr.std.std_ba,2);
			s7 += s2 * (Setting.getDouble("bat/isotope/calc/nominal_ba")/corr.std.std_ba) ;
			s8 += Math.pow(s7*corr.std.std_ba_err/corr.std.std_ba, 2);
		}
		result[0] = s7 / weight;		
		result[1] = Math.sqrt(s6+s8)/weight;
		for (int i=0; i<runs.size(); i++) {
			if (runs.get(i).active) {
				dev += runs.get(i).a * runs.get(i).runtime * Math.pow(runs.get(i).ba_fin-result[0],2);
				k++;
			}
		}
		if (k>1) {
			result[2] = Math.sqrt(dev/(weight*(k-1)));
		} else if (k==0) {
			result[2] = 0.0;
		} else {
			try{result[2] = sig_temp;}catch(NullPointerException e){;}
		}
		return result;
	}	
		
	/**
	 * @param runs 
	 * @param corrList 
	 * @return sample with calculated ra_fin with error and sigma, for blanks only!
	 * @throws NullPointerException 
	 */
	public static double[] getMeanBl_ra_fin(ArrayList<Run> runs, ArrayList<Corr> corrList) throws NullPointerException {
		double[] result = new double[3];
		Double dev = 0.0;
		Double weight = 0.0;
		Double sig_temp = -1.0;
		Corr corr;
		double s1 = 0.0;
		double s2 = 0.0;
		double s4 = 0.0;
		double s6 = 0.0;
		double s7 = 0.0;
		double s8 = 0.0;
		double p=0;
		int k=0;
		for (int j=0; j<corrList.size(); j++) {
			corr = corrList.get(j);
			s1 = 0.0;
			s2 = 0.0;
			s4 = 0.0;
			p=0;
			for (int i=0; i<runs.size(); i++) {
				if (runs.get(i).active && runs.get(i).corr_index==j) {
					if (Setting.isotope.contains("P")) {
						p = runs.get(i).a / runs.get(i).runtime_a * runs.get(i).runtime_b;
					} else {
						p = runs.get(i).a * runs.get(i).runtime;
					}
					s1 += p;
					s2 += p * runs.get(i).ra_bg;
					s4 += Math.pow(p*runs.get(i).ra_bg_err,2);
					sig_temp = runs.get(i).ra_fin_sig;
				}
			}
			weight += s1;
			s6 += s4 * Math.pow(Setting.getDouble("bat/isotope/calc/nominal_ra")/corr.std.std_ra,2);
			s7 += s2 * (Setting.getDouble("bat/isotope/calc/nominal_ra")/corr.std.std_ra) ;
			s8 += Math.pow(s7*corr.std.std_ra_err/corr.std.std_ra, 2);
		}
		result[0] = s7 / weight;		
		result[1] = Math.sqrt(s6+s8)/weight;
		for (int i=0; i<runs.size(); i++) {
			if (runs.get(i).active) {
				dev += runs.get(i).a * runs.get(i).runtime * Math.pow(runs.get(i).ra_fin-result[0],2);
				k++;
			}
		}
		if (k>1) {
			result[2] = Math.sqrt(dev/(weight*(k-1)));
		} else if (k==0) {
			result[2] = 0.0;
		} else {
			result[2] = sig_temp;
		}
		return result;
	}	
	
	
	/**
	 * @param runs 
	 * @param corrList 
	 * @return calculated ra_fin with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMean_ra_fin(ArrayList<Run> runs, ArrayList<Corr> corrList) throws NullPointerException {
		double[] result = new double[3];
		Double dev = 0.0;
		Double weight = 0.0;
		Double sig_temp = -1.0;
		Corr corr = corrList.get(0);
		double s1 = 0.0;
		double s2 = 0.0;
		double s4 = 0.0;
		double s6 = 0.0;
		double s7 = 0.0;
		double s8 = 0.0;
		double p=0;
		int k=0;
		for (int j=0; j<corrList.size(); j++) {
			corr = corrList.get(j);
			s1 = 0.0;
			s2 = 0.0;
			s4 = 0.0;
			p=0;
			for (int i=0; i<runs.size(); i++) {
				if (runs.get(i).active && runs.get(i).corr_index==j) {
					if (Setting.isotope.contains("P")) {
						p = runs.get(i).a / runs.get(i).runtime_a * runs.get(i).runtime;
					} else {
						p = runs.get(i).a * runs.get(i).runtime;
					}
					s1 += p;
					s2 += p * (runs.get(i).ra_bg-corr.blank.ra_bg);
					s4 += Math.pow(p*runs.get(i).ra_bg_err,2);
					sig_temp = runs.get(i).ra_fin_sig;
				}
			}
			weight += s1;
			s6 += (s4 + Math.pow(corr.blank.ra_bg_err*s1,2) )* Math.pow(Setting.getDouble("bat/isotope/calc/nominal_ra")/corr.std.std_ra,2);
			s7 += s2 * (Setting.getDouble("bat/isotope/calc/nominal_ra")/corr.std.std_ra) ;
			s8 += Math.pow(s7*corr.std.std_ra_err/corr.std.std_ra, 2);
		}
		result[0] = s7 / weight;		
		result[1] = Math.sqrt((s6+s8)/Math.pow(weight,2)+Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*result[0],2));
		for (int i=0; i<runs.size(); i++) {
			if (runs.get(i).active) {
				dev += runs.get(i).a * runs.get(i).runtime * Math.pow(runs.get(i).ra_fin-result[0],2);
				k++;
			}
		}
		if (k>1) {
			result[2] = Math.sqrt(dev/(weight*(k-1)));
		} else if (k==0) {
			result[2] = 0.0;
		} else {
			result[2] = sig_temp;
		}
		return result;
	}
	
	/**
	 * @param runs 
	 * @param corrList 
	 * @return calculated ra_fin with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMean_rb_fin(ArrayList<Run> runs, ArrayList<Corr> corrList) throws NullPointerException {
		double[] result = new double[3];
		Double dev = 0.0;
		Double weight = 0.0;
		Double sig_temp = -1.0;
		Corr corr = corrList.get(0);
		double s1 = 0.0;
		double s2 = 0.0;
		double s4 = 0.0;
		double s6 = 0.0;
		double s7 = 0.0;
		double s8 = 0.0;
		double p=0;
		int k=0;
		for (int j=0; j<corrList.size(); j++) {
			corr = corrList.get(j);
			s1 = 0.0;
			s2 = 0.0;
			s4 = 0.0;
			p=0;
			for (int i=0; i<runs.size(); i++) {
				if (runs.get(i).active && runs.get(i).corr_index==j) {
					if (Setting.isotope.contains("P")) {
						p = runs.get(i).a / runs.get(i).runtime_a * runs.get(i).runtime_b;
					} else {
						p = runs.get(i).a * runs.get(i).runtime;
					}
					s1 += p;
					s2 += p * (runs.get(i).rb_bg);
					s4 += Math.pow(p*runs.get(i).rb_bg_err,2);
					sig_temp = runs.get(i).rb_fin_sig;
				}
			}
			weight += s1;
			s6 += s4;
			s7 += s2 * (Setting.getDouble("bat/isotope/calc/nominal_ra")/Setting.getDouble("bat/isotope/calc/nominal_ba")/corr.std.std_rb) ;
			s8 += Math.pow(s7*corr.std.std_rb_err/corr.std.std_rb, 2);
		}
		result[0] = s7 / weight;		
		result[1] = Math.sqrt((s6+s8)/Math.pow(weight,2)+Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*result[0],2));
		for (int i=0; i<runs.size(); i++) {
			if (runs.get(i).active) {
				dev += runs.get(i).a * runs.get(i).runtime * Math.pow(runs.get(i).rb_fin-result[0],2);
				k++;
			}
		}
		if (k>1) {
			result[2] = Math.sqrt(dev/(weight*(k-1)));
		} else if (k==0) {
			result[2] = 0.0;
		} else {
			result[2] = sig_temp;
		}
		return result;
	}
	
	/**
	 * @param runs 
	 * @param corrList 
	 * @return calculated ra_bl with error and sigma
	 * @throws NullPointerException 
	 */
	public static double[] getMean_ra_bl(ArrayList<Run> runs, ArrayList<Corr> corrList) throws NullPointerException {
		double[] result = new double[3];
		Double dev = 0.0;
		Double weight = 0.0;
		Double sig_temp = -1.0;
		Corr corr = corrList.get(0);
		double s1 = 0.0;
		double s2 = 0.0;
		double s4 = 0.0;
		double s6 = 0.0;
		double p=0;
		int k=0;
		for (int j=0; j<corrList.size(); j++) {
			corr = corrList.get(j);
			s1 = 0.0;
			s4 = 0.0;
			p=0;
			for (int i=0; i<runs.size(); i++) {
				if (runs.get(i).active() && runs.get(i).corr_index==j) {
					if (Setting.isotope.contains("P")) {
						p = runs.get(i).a / runs.get(i).runtime_a * runs.get(i).runtime;
					} else {
						p = runs.get(i).weight();
					}
					s1 += p;
					s2 += p * (runs.get(i).ra_bg-corr.blank.ra_bg);
					s4 += Math.pow(p*runs.get(i).ra_bg_err,2);
					sig_temp = runs.get(i).ra_bl_sig;
				}
			}
			weight += s1;
			s6 += (s4 + Math.pow(corr.blank.ra_bg_err*s1,2) );
		}
		result[0] = s2 / weight;
		result[1] = Math.sqrt(s6/Math.pow(weight,2)+Math.pow(Setting.getDouble("/bat/isotope/calc/scatter")*result[0],2));
//		log.debug(result[0]+" - "+result[1]);
		for (int i=0; i<runs.size(); i++) {
			if (runs.get(i).active) {
				dev += runs.get(i).a * runs.get(i).runtime * Math.pow(runs.get(i).ra_bl-result[0],2);
				k++;
			}
		}
		if (k>1) {
			result[2] = Math.sqrt(dev/(weight*(k-1)));
		} else if (k==0) {
			result[2] = 0.0;
		} else {
			result[2] = sig_temp;
		}
		return result;
	}	

	/**
	 * @param runList
	 * @param name
	 * @return sum
	 * @throws NullPointerException
	 */
	public static Double sum(ArrayList<Run> runList, String name) throws NullPointerException{
		Double sum = new Double(0);
		for (int i=0; i<runList.size(); i++){
			if (runList.get(i).active()) {
				sum+=((Double)(runList.get(i)).get(name));
			}
		}
		return sum;
	}

	/**
	 * @param runList
	 * @param name
	 * @return sum
	 * @throws NullPointerException
	 */
	public static Double sumNotNull(ArrayList<Run> runList, String name) {
		Double sum = new Double(0);
		for (int i=0; i<runList.size(); i++){
			if (runList.get(i).active()) {
				try {
					sum+=((Double)(runList.get(i)).get(name));
				} catch (NullPointerException e) {;}
			}
		}
		return sum;
	}

	/**
	 * @param runList
	 * @param name
	 * @return mean
	 * @throws NullPointerException
	 */
	public static Double meanTime(ArrayList<Run> runList, String name) throws NullPointerException{
		Double mean = new Double(0.0);
		Double time = new Double(0.0);
		int i;
		for (i=0; i<runList.size(); i++) {
			if (runList.get(i).active()) {
				mean+=((Double)(runList.get(i)).get(name))*runList.get(i).runtime();
				time+= runList.get(i).runtime();
//				log.debug(((Double)(runList.get(i)).get(name))+" - "+runList.get(i).runtime());
			}
		}
		mean/=time;
//		log.debug(mean);
		return mean;
	}
	
	/**
	 * @param runList
	 * @param name
	 * @return sigma weight
	 * @throws NullPointerException
	 */
	public static Double sigmaTime(ArrayList<Run> runList, String name) throws NullPointerException{
		double mean = 0;
		int i;
		double weightSum = 0;
		for (i=0; i<runList.size(); i++){
			mean += runList.get(i).runtime() * (Double)runList.get(i).get(name);
			weightSum += runList.get(i).runtime();			
		}
		mean = mean / weightSum;
		double devSum = 0;
		for (i=0; i<runList.size(); i++){
			devSum += ( runList.get(i).runtime() ) * Math.pow( mean - (Double)runList.get(i).get(name) , 2);
		}
		if (runList.size()==1) {
			return (Double)runList.get(0).get(name+"_sig");
		} else {
			return Math.sqrt(devSum/weightSum/(i-1));
		}
	}
		
	/**
	 * Calculates mean weighted with runtime and current a
	 * @param runList
	 * @param name
	 * @return sigma weight
	 * @throws NullPointerException
	 */
	public static Double sigmaTA(ArrayList<Run> runList, String name) throws NullPointerException{
		double mean = 0;
		int i;
		double weightSum = 0;
		for (i=0; i<runList.size(); i++) {
			mean += runList.get(i).weight() * (Double)runList.get(i).get(name);
			weightSum += runList.get(i).weight() ;			
		}
		mean = mean / weightSum;
		double devSum = 0;
		for (i=0; i<runList.size(); i++){
			devSum += ( runList.get(i).weight() ) * Math.pow( mean - (Double)runList.get(i).get(name) , 2);
		}
		if (runList.size()==1) {
			return (Double)runList.get(0).get(name+"_sig");
		} else {
			return Math.sqrt(devSum/weightSum/(i-1));
		}
	}
		
	/**
	 * Calculates mean weighted with runtime and current a
	 * @param runList
	 * @param name
	 * @return sigma weight
	 * @throws NullPointerException
	 */
	public static Double sigmaNotNull(ArrayList<Run> runList, String name) {
		double mean = 0;
		int i, k=0;
		Double temp;
		double weightSum = 0;
		for (i=0; i<runList.size(); i++) {
			try {
				temp = runList.get(i).weight() * (Double)runList.get(i).get(name);
				if (!temp.isNaN()) {
					mean += temp;
					weightSum += runList.get(i).weight() ;
				}
			} catch (NullPointerException e) {;}
		}
		mean = mean / weightSum;
		double devSum = 0;
		for (i=0; i<runList.size(); i++){
			try {
				temp = ( runList.get(i).weight() ) * Math.pow( mean - (Double)runList.get(i).get(name) , 2);
				if (!temp.isNaN()) {
					devSum += temp;
					k++;
				}
			} catch (NullPointerException e) {;}
		}
		if (runList.size()==1) {
			return (Double)runList.get(0).get(name+"_sig");
		} else {
			return Math.sqrt(devSum/weightSum/(k-1));
		}
	}
		
	/**
	 * @param runList
	 * @param name
	 * @return sigma
	 * @throws NullPointerException
	 */
	public static Double sigma(ArrayList<Run> runList, String name) throws NullPointerException{
		double mean = meanTime(runList, name);
		double devSum = 0;
		int i;
		for (i=0; i<runList.size(); i++){
			devSum += Math.pow( mean - (Double)runList.get(i).get(name) , 2);
		}
//		log.debug("mean: "+mean+"  devSum: "+devSum+"  i: "+i);
		return Math.sqrt(devSum/(i-1));
	}
	
	/**
	 * @param runList
	 * @param name
	 * @return mean of mean
	 * @throws NullPointerException
	 */
	public static Double sigmaOfMean(ArrayList<Run> runList, String name) throws NullPointerException{
		if (runList.size()==1) {
			return 0.0;
		} else {
			return sigma(runList, name)/Math.sqrt(runList.size());
		}
	}
	
	/**
	 * @param runs
	 * @param name
	 * @return error
	 */
	public static double getErr(ArrayList<Run> runs, String name) {
		double err=0;
		for (int i=0; i<runs.size(); i++) {
			err += Math.pow((Double)runs.get(i).get(name),2);
		}
		return Math.sqrt(err);
	}
	
	/**
	 * @param runs 
	 * @param name 
	 * @return mean, error, sigma
	 */
	public static double[] meanErr(ArrayList<Run> runs, String name) {
		double m;
		double err;
		double sig;
		double s1 = 0.0;
		double s2 = 0.0;
		double s3 = 0.0;
		double s4 = 0.0;
		double p;
		double val;
		int i;
		for (i=0; i<runs.size(); i++) {
			val = (Double)runs.get(i).get(name);
			err = (Double)runs.get(i).get(name+"_err");
			p = runs.get(i).weight();
			s1 += p;
			s2 += p * val;
			s3 += p * Math.pow(val, 2);
			s4 += 1/Math.pow(val*err,2);
		}
		m = s2 / s1;
		if (runs.size()>1) {
			sig = Math.sqrt( (s3 - Math.pow(s2,2)/s1) / (s1 *(i-1)) ) / m;
		} else { 
			sig = (Double) runs.get(0).get("ba_sig");
		}
		err = 1 / Math.sqrt(s4) / (s2/s1);
		double[] a={m, err, sig};
		return a;
	}
	
	/**
	 * @param counts
	 * @return poisson error (>100 normal distribution)
	 */
	public static Double poissonErr(Double counts) {
		if (counts<100&&counts>=0&&Setting.getBoolean("/bat/isotope/calc/poisson")) {
			try {return poisson[(int) Math.round(counts)];}
			catch (ArrayIndexOutOfBoundsException e) {log.error("Can't get poisson error for: "+counts);return -1.0;}
		} else {
			return Math.sqrt(counts);
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @param data 
	 * @return regression [a,b,R^2,err_a,err_b] y=ax+b R^2
	 */
	public static double[] regression(ArrayList<DataSet> data, String x, String y) {
		double[] result = new double[5];
		SimpleRegression regression = new SimpleRegression();
		
		for (int i=0; i<data.size(); i++) {
			try {regression.addData((Double)data.get(i).get(x), (Double)data.get(i).get(y));}
			catch (NullPointerException e) {;}
		}
		result[0]=regression.getSlope();
		result[1]=regression.getIntercept();
		result[2]=regression.getRSquare();
		result[3]=regression.getSlopeStdErr();
		result[4]=regression.getInterceptStdErr();
		
//		log.debug("Regression: "+result);
		return result;
	}
	
	
	/**
	 * @param data
	 * @param x
	 * @param y
	 * @return slope [a,err_a] y=ax, regression through 0
	 */
	public static double[] slope(ArrayList<DataSet> data, String x, String y) {
		double[] result = new double[3];
		double s0=0, s1=0;
		for (int i=0; i<data.size(); i++) {
			s0 += (Double)data.get(i).get(x) * (Double)data.get(i).get(y) 
					/ Math.pow((Double)data.get(i).get(y+"_err"),2);
			s1 += Math.pow((Double)data.get(i).get(x),2) / Math.pow((Double)data.get(i).get(y+"_err"),2);
		}
		result[0] = s0 / s1;
		result[1] = Math.sqrt( 1 / s1);
		log.debug("Calculated slope for \""+x+"\" and \""+y+"\": "+result[0]+"+-"+result[1]);			
		return result;	
	}
	
	/**
	 * @param slope
	 * @param max
	 * @return XYDataset with regression line through 0
	 */
	public static XYDataset xYSlope(double[] slope, double max) {
		XYSeries series1 = new XYSeries("+1s");
		XYSeries series2 = new XYSeries("-1s");
		XYSeries series3 = new XYSeries("mean");
		series1.add(0,0);
		series1.add(max,(slope[0]+slope[1])*max);
		series2.add(0,0);
		series2.add(max,(slope[0]-slope[1])*max);
		series3.add(0,0);
		series3.add(max,(slope[0])*max);
		XYSeriesCollection dataSet = new XYSeriesCollection();
		dataSet.addSeries(series1);
		dataSet.addSeries(series2);
		dataSet.addSeries(series3);
		return dataSet;
	}
	
    /**
     * @param list 
     * @param corrIndex 
     * @param x 
     * @param y 
     * @param x_multi 
     * @param y_multi 
     * @return xy dataset for JFreePlot
     */
	public static XYDataset getXY(ArrayList<DataSet> list, Integer corrIndex, String x, String y, int x_multi, int y_multi){
		XYSeriesCollection dataSet = new XYSeriesCollection();
		String lastLabel="";
		String thisLabel="";
	    
		if (list!=null&&list.size()>0) 
		{
			lastLabel = (String) list.get(0).get("label");
			XYSeries series = new XYSeries(lastLabel);
			
			for (int k=0; k<list.size(); k++)
			{
				if (corrIndex==null||list.get(k).get("corrIndex").equals(corrIndex)) {
					if (list.get(k).get("active").equals(true)) {
						thisLabel = ((String)list.get(k).get("label"));
						if (!thisLabel.equals(lastLabel)) {
							dataSet.addSeries(series);
				            series = new XYSeries(((String)list.get(k).get("commentinfo"))+"-"+thisLabel);
				            lastLabel = (String) list.get(k).get("label");
						}
						try {
							series.add(
									(Number) ((Double)list.get(k).get(x) * x_multi),
									(Double) list.get(k).get(y) 
									* y_multi 
									);
						}
						catch (ClassCastException e)
						{
							log.warn("Field must be a number!(CalcData)");
						} catch (NullPointerException e){
							log.warn(""+e.getMessage());
						}
					} else {;}
				}
			}
			dataSet.addSeries(series);
		} else {
			log.info("No data for plot!");
		}
		dataSet.addChangeListener(null);
		return dataSet;        
    }
	
	
    /**
     * @param list 
     * @param corrIndex 
     * @param x 
     * @param y 
     * @return xy dataset for JFreePlot
     */
	public static XYDataset getXY(ArrayList<DataSet> list, Integer corrIndex, String x, String y){
    	return getXY(list, corrIndex, x, y, 1, 1);
    }
    
	/**
	 * @param cycleData
	 * @param max 
	 * @return runs
	 */
	public static ArrayList<Run> reduceCycle(ArrayList<Run> cycleData, int max) {
		ArrayList<Run> redData = new ArrayList<Run>();
		ArrayList<Run> tempData = null;		
		String tempLabel="";
		int k=0;
		int l=0;
		for (int i=0;i<cycleData.size();i++) {
			if (!cycleData.get(i).reduced) {
				if (cycleData.get(i).sample.label.equals(tempLabel)) {
					if (k<(max-1)) {
						tempData.add(cycleData.get(i));					
	//					log.debug("same label: "+tempLabel+"-"+i+"("+k+")");
						k++;
					} else {
						redData.add(meanCycle(tempData,++l));
						tempData = new ArrayList<Run>();
						tempData.add(cycleData.get(i));
	//					log.debug("same label, new run: "+tempLabel+"-"+i+"("+k+")");
						k=0;
					}
				} else if (tempLabel=="") {
					tempData = new ArrayList<Run>();
					tempData.add(cycleData.get(i));
					tempLabel = cycleData.get(i).sample.label;
					k++;
	//				log.debug("first label: "+tempLabel);
				} else {
					redData.add(meanCycle(tempData, ++l));
					tempData = new ArrayList<Run>();
					tempData.add(cycleData.get(i));
					tempLabel = cycleData.get(i).sample.label;
					k=0;
					l=0;
	//				log.debug("new label, new run: "+tempLabel);
				}
			}
		}
		if (tempLabel!="") {
			redData.add(meanCycle(tempData, ++l));
		}
//		log.debug(redData.size());
		return redData;
	}

	/**
	 * @param cycleData
	 * @param runNum
	 * @return mean of cycles
	 */
	public static Run meanCycle(ArrayList<Run> cycleData, int runNum) {
		Run run = cycleData.get(0);
		
		NumberFormatter nf = new NumberFormatter(new DecimalFormat("00"));
//		run.run = cycleData.get(0).run+cycleData.get(0).cycles;
//		
		if ( runNum>0) {
			try {run.run = cycleData.get(0).run+nf.valueToString(runNum);} 
			catch (ParseException e) {run.run = cycleData.get(0).run+cycleData.get(0).cycles;}
		} else {
			;
		}
//
//		try {run.label = cycleData.get(0).label;}
//		catch (NullPointerException e) {run.label = null;}
//		try {run.timedat = cycleData.get(0).timedat;}
//		catch (NullPointerException e) {run.timedat = null;}
		
		try {run.a = Func.meanTime(cycleData, "a");}
		catch (NullPointerException e) {run.a = null;}
		try {run.a_cur = Func.meanTime(cycleData, "a_cur");}
		catch (NullPointerException e) {run.a_cur = null;}		
		try {run.ana = Func.meanTime(cycleData, "ana");}
		catch (NullPointerException e) {run.ana = null;}
		try {run.anb = Func.meanTime(cycleData, "anb");}
		catch (NullPointerException e) {run.anb = null;}
		try {run.anbana = run.anb/run.ana;}
		catch (NullPointerException e) {run.anbana = null;}		
		try {run.b = Func.meanTime(cycleData, "b");}
		catch (NullPointerException e) {run.b = null;}
		try {run.b_cur = Func.meanTime(cycleData, "b_cur");}
		catch (NullPointerException e) {run.b_cur = null;}
		try {run.r = Func.sum(cycleData, "r");}
		catch (NullPointerException e) {run.r = null;}		
		try {run.iso = Func.meanTime(cycleData, "iso");}
		catch (NullPointerException e) {run.iso = null;}		
		try {run.g1 = Func.sum(cycleData, "g1");}
		catch (NullPointerException e) {run.g1 = null;}		
		try {run.g2 = Func.sum(cycleData, "g2");}
		catch (NullPointerException e) {run.g2 = null;}		
		try {run.tra = Func.meanTime(cycleData, "tra");}
		catch (NullPointerException e) {run.tra = null;}
		run.cycles = cycleData.size();
		
		try {run.runtime = Func.sum(cycleData, "runtime");}
		catch (NullPointerException e) {run.runtime = null;}
		
		// calculate sigma
		try { run.ba_sig=Func.sigmaOfMean(cycleData, "ba"); } 
		catch (NullPointerException e) { log.debug("Creating ba_sig from cyles error! ("+run.run+")"); }
		try { run.ra_sig=Func.sigmaOfMean(cycleData, "ra")*1E12;} 
		catch (NullPointerException e) { log.debug("Creating ra_sig from cyles error! ("+run.run+")"); }
		try { run.rb_sig=Func.sigmaOfMean(cycleData, "rb")*1E12; } 
		catch (NullPointerException e) { log.debug("Creating rb_sig from cyles error! ("+run.run+")"); }
		run.reduced=true;
		
		return run;
	}

    static double[] poisson = {1.07,1.54,1.88,2.16,2.40,2.62,2.82,3.01,3.18,3.35,
    					3.51,3.66,3.80,3.94,4.07,4.20,4.32,4.44,4.56,4.67,
    					4.79,4.89,5.00,5.10,5.21,5.31,5.40,5.50,5.59,5.69,
    					5.78,5.87,5.95,6.04,6.13,6.21,6.29,6.38,6.46,6.54,
    					6.62,6.69,6.77,6.85,6.92,7.00,7.07,7.14,7.22,7.29,
    					7.36,7.43,7.50,7.56,7.63,7.70,7.77,7.83,7.90,7.96,
    					8.03,8.09,8.15,8.22,8.28,8.34,8.40,8.46,8.53,8.59,
    					8.65,8.70,8.76,8.82,8.88,8.94,8.99,9.05,9.11,9.16,
    					9.22,9.28,9.33,9.39,9.44,9.49,9.55,9.60,9.65,9.71,
    					9.76,9.81,9.86,9.92,9.97,10.02,10.07,10.12,10.17,10.22,};

}
