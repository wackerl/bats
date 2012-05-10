import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

import plot.Data;


/**
 * @author lukas
 *
 */
abstract class Calc {
	final static Logger log = Logger.getLogger(Calc.class);

	/**
	 * first run of magazine
	 */
	public String firstRun = "no run";
	
	/**
	 * last run of magazine
	 */
	public String lastRun = "no run";
	
	/**
	 * magazine name
	 */
	public String magazine = Setting.magazine;
	
	/**
	 * magazine name
	 */
	public String calcSet = Setting.magazine;
	
	/**
	 * isotope
	 */
	public String isotope = "";	
	
	/**
	 * 
	 */
	public String isobar;
	
	/**
	 * 
	 */
	public StdNom stdNom;
	
	/**
	 * 
	 */
	public Comment comment = new Comment("");
	
	protected static double MICRO_A = 6.24150948E12;
	protected double a_errR;
	protected double b_errR;
	protected double iso_errR;
	protected double a_err;
	protected double b_err;
	protected double iso_err;
	protected double a_off;
	protected double b_off;
	protected double iso_off;
	protected double deadtime;
	protected int charge;

	protected ArrayList<Corr> corrList = new ArrayList<Corr>();
	

	protected ArrayList<Run> runStd = new ArrayList<Run>();					// std14 run
	protected ArrayList<DataSet> allStd = new ArrayList<DataSet>();
	protected ArrayList<DataSet> allStdR = new ArrayList<DataSet>();					// std14 run with average
	
	protected ArrayList<Run> runBlank = new ArrayList<Run>();					// blank run
	protected ArrayList<DataSet> allBlank = new ArrayList<DataSet>();
	protected ArrayList<DataSet> allBlankR = new ArrayList<DataSet>();					// blank run with average
	
	protected ArrayList<Run> runIso = new ArrayList<Run>();					// isobar CalcData
	
	protected ArrayList<Run> runListL = new ArrayList<Run>();			// array with run sorted Runs
	protected ArrayList<Run> runListR = new ArrayList<Run>();		// array with label sorted Runs
	protected ArrayList<ArrayList<Run>> runLabelList = new ArrayList<ArrayList<Run>>();
	protected ArrayList<DataSet> runSampleList  = new ArrayList<DataSet>();		// array with label sorted Runs and Mean
	protected ArrayList<DataSet> runSamplePosList  = new ArrayList<DataSet>();		// array with label sorted Runs and Mean
	/**
	 * 
	 */
	public ArrayList<Sample> sampleList  = new ArrayList<Sample>();		// array with label sorted Sample mean
	
	Calc(){
		loadSetting();
		Setting.c++;
		Setting.autocalc=false;
	}
	
	/**
	 * (re)load settings
	 */
	public void loadSetting() {
		try {
			a_errR=Setting.getDouble("/bat/isotope/calc/current/a/error_rel");
			b_errR=Setting.getDouble("/bat/isotope/calc/current/b/error_rel");
			iso_errR=Setting.getDouble("/bat/isotope/calc/current/iso/error_rel");
			a_err=Setting.getDouble("/bat/isotope/calc/current/a/error_abs");
			b_err=Setting.getDouble("/bat/isotope/calc/current/b/error_abs");
			iso_err=Setting.getDouble("/bat/isotope/calc/current/iso/error_abs");
			a_off=Setting.getDouble("/bat/isotope/calc/current/a/offset");
			b_off=Setting.getDouble("/bat/isotope/calc/current/b/offset");
			deadtime=Setting.getDouble("/bat/isotope/calc/dead_time");
			iso_off=Setting.getDouble("/bat/isotope/calc/current/iso/offset");
			charge=Setting.getInt("/bat/isotope/calc/current/charge");
			isobar = Setting.getString("/bat/isotope/calc/bg/isobar");
			log.debug("Settings loaded in DataCalc");
		} catch (NullPointerException e) {
			log.error("Could not load Settings for calculation ("+e.getMessage()+")");
			JOptionPane.showMessageDialog(null,"Could not load Settings for calculation ("+e.getMessage()+")");
		}
	}
	
	/**
	 * 
	 */
	public void reinitData() {
		initData(new ArrayList<Corr>());
	}
	
	
	/**
	 * 
	 */
	public void removeData() {
		magazine = "";
		calcSet = magazine;
		firstRun = "";
		lastRun = "";
		corrList = new ArrayList<Corr>();
		
		runStd = new ArrayList<Run>();					// std14 run
		allStd = new ArrayList<DataSet>();
		allStdR = new ArrayList<DataSet>();					// std14 run with average
		
		runBlank = new ArrayList<Run>();					// blank run
		allBlank = new ArrayList<DataSet>();
		allBlankR = new ArrayList<DataSet>();					// blank run with average
		
		runIso = new ArrayList<Run>();					// isobar CalcData
		
		runListL = new ArrayList<Run>();			// array with run sorted Runs
		runListR = new ArrayList<Run>();		// array with label sorted Runs
		runLabelList = new ArrayList<ArrayList<Run>>();
		runSampleList  = new ArrayList<DataSet>();		// array with label sorted Runs and Mean
		runSamplePosList  = new ArrayList<DataSet>();		// array with label sorted Runs and Mean
		sampleList  = new ArrayList<Sample>();		// array with label sorted Sample mean
		log.debug("All data removed!");
	}
	
	
	/**
	 * Initialises sample mean and corrections
	 * @param corrL
	 */
	@SuppressWarnings("unchecked")
	protected void initData (ArrayList<Corr> corrL) {
		if (runListL!=null && runListL.size()!=0){
			runListL.trimToSize();
			Collections.sort(runListL, new SortLabel());
			this.runListR = (ArrayList<Run>) runListL.clone();
			Collections.sort(runListR, new SortRun());
			magInfo(runListR);
			if (corrL.size()==0) {
				Corr correction = this.newCorrection();
				correction.firstRun=firstRun;
				correction.lastRun=lastRun;
				corrL.add(correction);
				for(int i=0; i<runListR.size(); i++) {
					runListR.get(i).corr_index=0;
				}
				this.corrList = corrL;
				log.debug("Correction initialised");
			} else {
				this.corrList = corrL;
				corrL.get(0).firstRun=firstRun;
				corrL.get(corrL.size()-1).lastRun=lastRun;
				int i=0;
				for (int k=0; k<runListR.size(); k++) {
//					log.debug(corrList.get(i).lastRun);
//					log.debug(runListR.size());
//					log.debug(runListR.get(k).run);
					if (runListR.get(k).run.compareToIgnoreCase(corrList.get(i).lastRun)>0) {
						i++;
						try {
							corrList.get(i).firstRun = runListR.get(k).run;
						} catch (IndexOutOfBoundsException e) {;}
					}
					runListR.get(k).corr_index=i;
				}

			}
			log.info("Correction list size: "+corrL.size());
	
			sampleList = new ArrayList<Sample>();
			runLabelList = new ArrayList<ArrayList<Run>>();
			runSampleList = new ArrayList<DataSet>();
			runSamplePosList = new ArrayList<DataSet>();
			
			ArrayList<Run> labelList = new ArrayList<Run>();
			
			initCalc(runListL);
			log.debug("Initial run calc done.");
			
			Sample sample = runListL.get(0).sample;
			int k;
			for (k=0; k<runListL.size(); k++){
				if (!sample.equals(runListL.get(k).sample)){
					runLabelList.add(labelList);
					runSampleList.add(sample);
					runSamplePosList.add(sample);
					sampleList.add(sample);
					labelList = new ArrayList<Run>();	
					sample = runListL.get(k).sample;
				}
				runSampleList.add(runListL.get(k));
				runSamplePosList.add(runListL.get(k));
				labelList.add(runListL.get(k));
			}
			runSampleList.add(sample);
			runSamplePosList.add(sample);
			runLabelList.add(labelList);
			sampleList.add(sample);
			
			runSampleList.trimToSize();
			runSamplePosList.trimToSize();
			sampleList.trimToSize();
			runLabelList.trimToSize();
			
			Collections.sort(runSamplePosList, new SortPos());
			
			log.debug("Initialised samples ("+sampleList.size()+").");
			log.debug("Magazine "+this.magazine+" in calcset "+this.calcSet);
			
			Setting.no_data=false;
		} else {
			String message = String.format( "No data was loaded!!!");
			JOptionPane.showMessageDialog( null, message );
			this.removeData();
//			Setting.magazine="";
//			magazine=Setting.magazine;
//			Setting.no_data=true;
//			log.info("Init data: No data was loaded!");			
		}
	}
	
	public void autocalcStdBl() {
//		log.debug("Test for autocalc");
		boolean isOn=true;
		for (int i=0; i<corrList.size(); i++) {
			if (corrList.get(i).std.active == false) isOn=false;
			if (corrList.get(i).blank.active == false) isOn=false;
//			log.debug(i+". corr: "+corrList.get(i).std.active+" / "+isOn );
		}
		if (isOn==false) {
			int option = JOptionPane.showConfirmDialog(null, "Do you want to turn autocalc \n of standards and blanks? \n (YES is suggested to avoid inconsistancies)", "Autocalc std/bl?", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				for (int i=0; i<corrList.size(); i++) {
					corrList.get(i).std.active = true;
					corrList.get(i).blank.active = true;			
				}
				log.debug("Autocalc of std/bl turn on!");
			}
		}
	}
	
	
	/**
	 * @param label
	 * @return sample
	 */
	public Sample setSample(String label) {
		Sample sample = null;
		int i;
		for (i=0; i<sampleList.size(); i++) {
//			log.debug("Set sample with label: *"+label+"*"+sampleList.get(i).label+"*");
			if (sampleList.get(i).label.equalsIgnoreCase(label)) {
				sample=sampleList.get(i);
				i=sampleList.size()+1;
			} 
		}
		if (i==sampleList.size()) {
			sample = new Sample(label);
			sampleList.add(sample);
//			log.debug("New sample created: "+label);
		}		
		return sample;
	}
	
	/**
	 * @param pos
	 * @return sample
	 */
	public Sample getSamplePos(int pos) {
		Sample sample = null;
		int i;
		for (i=0; i<sampleList.size(); i++) {
			if (sampleList.get(i).posit.equals(pos)) {
				sample=sampleList.get(i);
				i=sampleList.size()+1;
			} 
		}
		if (i==sampleList.size()) {
			String message = String.format( "Sample position does not exist: "+pos);
			JOptionPane.showMessageDialog( null, message );
			log.warn("Sample does not exist: "+pos);
		}		
		return sample;
	}
	
	/**
	 * @param label
	 * @return sample
	 */
	public Sample getSample(String label) {
		Sample sample = null;
		int i;
		for (i=0; i<sampleList.size(); i++) {
//			log.debug("Set sample with label: "+label);
			if (sampleList.get(i).label.equalsIgnoreCase(label)) {
				sample=sampleList.get(i);
				i=sampleList.size()+1;
			} 
		}
		if (i==sampleList.size()) {
			String message = String.format( "Sample does not exist: "+label);
			JOptionPane.showMessageDialog( null, message );
			log.warn("Sample does not exist: "+label);
		}		
		return sample;
	}
	
	/**
	 * Recalculates data in Calc*.java
	 */
	protected void calcAll(){
		if (runListL!=null && corrList!=null && corrList.size()!=0){
			loadSetting();
			log.debug("Data (size: "+runListL.size()+") will be recalculated!");
			runPreCalc(runListL);
			for (int i=0; i<sampleList.size();i++) {
				sampleList.set(i,this.samplePreCalc(sampleList.get(i), runLabelList.get(i)));
			}
			initCorrection();
			if (corrList.get(0).std.std_ra>=0.0 && corrList.get(0).blank.ra_bg>-1.0) {
				runCalc(runListL, corrList);
				for (int k=0; k<sampleList.size(); k++) {
					ArrayList<Run> labelRuns = runLabelList.get(k);
					sampleCalc(sampleList.get(k), labelRuns, corrList);
				}
							
				log.debug("Runs and Samples were recalculated.");
			}
		} else {
			log.info("No correction or run exists! (runs: "+runListL.size()+" corrections: "+corrList.size()+")");
		}
	}
	
	/**
	 * Calculates standards in Calc*.java
	 */
	@SuppressWarnings("unchecked")
	protected void initCorrection(){	
		runStd = new ArrayList();
		allStd = new ArrayList<DataSet>();
		allStdR = new ArrayList();
		
		runBlank = new ArrayList();
		allBlank = new ArrayList<DataSet>();
		allBlankR = new ArrayList();			

		runIso = new ArrayList();
		
		for (int k=0; k<sampleList.size(); k++){
//			log.debug("Correction -- "+sampleList.get(k).label+": "+sampleList.get(k).sampletype);
			if (Setting.blankLabel.contains(sampleList.get(k).type.toLowerCase())){
				ArrayList<Run> labelRuns = runLabelList.get(k);
				runBlank.addAll(labelRuns);
			} else if ((sampleList.get(k).type.toLowerCase()).equals("iso")){
					ArrayList<Run> labelRuns = runLabelList.get(k);
					runIso.addAll(labelRuns);
//					log.debug("Sample taken for Isobar correction: "+sampleList.get(k).label);
			} else if (stdNom.isStd(sampleList.get(k).type)){
//				log.debug("Is std: "+sampleList.get(k).sampletype);
				ArrayList<Run> labelRuns = runLabelList.get(k);
				runStd.addAll(labelRuns);
			}
		}	
		runBlank.trimToSize();
		runStd.trimToSize();
		// in case the data reduction is split, we have to calculate the additional means of standards...		
		ArrayList<Sample> sampleStdT;
		ArrayList<Run> runStdT;
		ArrayList<Sample> sampleBlT;		
		ArrayList<Run> runBlT;	
		Sample sample;
		Sample lastSample;
		for (int k=0; k<corrList.size(); k++) {
			sampleBlT = new ArrayList<Sample>();
			runBlT = new ArrayList<Run>();
			sampleStdT = new ArrayList<Sample>();
			runStdT = new ArrayList<Run>();
			
			try {lastSample = runBlank.get(0).sample;} 
			catch (IndexOutOfBoundsException e) {lastSample=null;}
			ArrayList<Run> labelRun = new ArrayList<Run>();
			for (int i=0; i<runBlank.size(); i++) {
				if (runBlank.get(i).run.compareToIgnoreCase(corrList.get(k).lastRun)<=0 
						&& runBlank.get(i).run.compareToIgnoreCase(corrList.get(k).firstRun)>=0) {
					if (runBlank.get(i).sample.equals(lastSample)) {
						labelRun.add(runBlank.get(i));						
					} else {
						sample = initSampleCalc(lastSample);
						sample = samplePreCalc(sample, labelRun);
						sampleBlT.add(sample);
						runBlT.addAll(labelRun);
						allBlank.addAll(labelRun);
						allBlank.add(sample);
						
						labelRun = new ArrayList<Run>();
						labelRun.add(runBlank.get(i));
						lastSample = runBlank.get(i).sample;
					}
				}
			}
			if (runBlank.size()>0) {
				sample = initSampleCalc(lastSample);
				sample = samplePreCalc(sample, labelRun);
				sampleBlT.add(sample);
				runBlT.addAll(labelRun);
				allBlank.addAll(labelRun);
				allBlank.add(sample);
			}
			
			try {lastSample = runStd.get(0).sample;} 
			catch (IndexOutOfBoundsException e) {lastSample=null;}
			labelRun = new ArrayList<Run>();
			for (int i=0; i<runStd.size(); i++) {
				if (runStd.get(i).run.compareToIgnoreCase(corrList.get(k).lastRun)<=0 
						&& runStd.get(i).run.compareToIgnoreCase(corrList.get(k).firstRun)>=0) {
					if (runStd.get(i).sample.equals(lastSample)) {
						labelRun.add(runStd.get(i));						
					} else {
						sample = initSampleCalc(lastSample);
						sample = samplePreCalc(sample, labelRun);
						sampleStdT.add(sample);
						runStdT.addAll(labelRun);
						allStd.addAll(labelRun);
						allStd.add(sample);
						
						labelRun = new ArrayList<Run>();
						labelRun.add(runStd.get(i));						
						lastSample = runStd.get(i).sample;
					}
				} else { 
//					log.debug(runStd.get(i).run);
//					log.debug(corrList.get(k).firstRun);
//					log.debug(corrList.get(k).lastRun);
				}
			}
			if (runStd.size()>0) {
				sample = initSampleCalc(lastSample);
				sample = samplePreCalc(sample, labelRun);
				sampleStdT.add(sample);
				runStdT.addAll(labelRun);
				allStd.addAll(labelRun);
				allStd.add(sample);
			}
			
			corrList.get(k).setBlank(sampleBlT, runBlT);
			if (sampleStdT.size()!=0) {
				log.debug("Set Std");
				corrList.get(k).setStandard(sampleStdT, runStdT);
			}
			
			Collections.sort(runStdT, new SortRun());
			Collections.sort(runBlT, new SortRun());
			allStdR.addAll(runStdT);
			allBlankR.addAll(runBlT);
			
			allBlank.add(corrList.get(k).blankR);
			allBlank.add(corrList.get(k).blankS);
			allBlank.add(corrList.get(k).blank);
			allStd.add(corrList.get(k).stdR);			
			allStd.add(corrList.get(k).stdS);			
			allStd.add(corrList.get(k).stdSc);			
			allStd.add(corrList.get(k).std);	
			
			allBlankR.add(corrList.get(k).blankR);
			allBlankR.add(corrList.get(k).blankS);
			allBlankR.add(corrList.get(k).blank);
			allStdR.add(corrList.get(k).stdR);			
			allStdR.add(corrList.get(k).stdS);			
			allStdR.add(corrList.get(k).stdSc);			
			allStdR.add(corrList.get(k).std);
			
			allBlankR.trimToSize();
			allBlank.trimToSize();
			allStdR.trimToSize();			
			allStd.trimToSize();			
			
			log.debug("Calc Std and Blank for correction "+k);
		}
	}
	
	/**
	 * @param runListR 
	 * 
	 */
	private void magInfo(ArrayList<Run> runListR){
		try { firstRun = (String)runListR.get(0).run;}
		catch(IndexOutOfBoundsException e) {log.error("magInfo: magazine");}
		try { lastRun = (String)runListR.get(runListL.size()-1).run;}
		catch(IndexOutOfBoundsException e) {log.error("magInfo: last run");}
		try { 
			if ((String)runListR.get(0).sample.magazine==null) {
				Setting.magazine = "";
				Setting.no_data = true;
			} else {
				magazine = (String)runListR.get(0).sample.magazine;
				Setting.setMagazine(magazine);
//				Setting.no_data = false;
			}
//			if (calcSet==null) {
				calcSet = magazine;
//			}
		} catch(NullPointerException e) {
			Setting.magazine = "";
			Setting.no_data = true;
			log.error("magInfo: Magazine could not be set!");
		} catch(IndexOutOfBoundsException e) {
			Setting.magazine = "";
			Setting.no_data = true;
			log.error("magInfo: Magazine could not be set!");
		}
	}
	
	/**
	 * 
	 */
	public void removeSplit() {
		corrList.get(0).firstRun=firstRun;
		corrList.get(0).lastRun=lastRun;
		for (int i=1; i<corrList.size();i++) {
			corrList.remove(i);
		}
		for (int k=0; k<runListR.size(); k++) {
			runListR.get(k).corr_index=0;
		}		
	}
	
	/**
	 * 
	 * @param run split correction after this run.
	 */
	public void splitCorrection(String run) {
		for (int i=0; i<corrList.size(); i++) {
			if (run.compareToIgnoreCase(corrList.get(i).lastRun)<=0 
					&& run.compareToIgnoreCase(corrList.get(i).firstRun)>=0) {
				Corr corr = corrList.get(i).copy();
				corr.lastRun = corrList.get(i).lastRun;
				corrList.get(i).lastRun = run;
				corrList.add(i+1, corr);
				break;
			}
		}
		int i=0;
		for (int k=0; k<runListR.size(); k++) {
			if (runListR.get(k).run.compareToIgnoreCase(corrList.get(i).lastRun)>0) {
				i++;
				try {
					corrList.get(i).firstRun = runListR.get(k).run;
				} catch (IndexOutOfBoundsException e) {;}
			}
			runListR.get(k).corr_index=i;
		}
		log.info("Correction inserted after "+run+". Total "+corrList.size()+" corrections.");
	}
	
	/**
	 * @param run1 First run to (de-)activate
	 * @param run2 last run to (de-)activate
	 * @param active set active if true, set deactivated if false
	 */
	public void activeRuns1(String run1, String run2, Boolean active) {
		for (int k=0; k<runListR.size(); k++) {
			if (runListR.get(k).run.compareToIgnoreCase(run1)>=0
					&& runListR.get(k).run.compareToIgnoreCase(run2)<=0) {
				runListR.get(k).active=active;				
			}
		}	
	}
	
	/**
	 * @param runs 
	 * @param active set active if true, set deactivated if false
	 */
	public void activeRuns1(ArrayList<String> runs, Boolean active) {
		for (int k=0; k<runListR.size(); k++) {
			if (runs.contains(runListR.get(k).run)) {
				runListR.get(k).active=active;				
			}
		}	
	}
	
	/**
	 * @param order 
	 * @return sorted results
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DataSet> get(String order){
		ArrayList list = null;
		if (order.equals("runAll")){
			list = runListR;
		}else if (order.equals("runAllLabel")){
			list = runListL;
		}else if (order.equals("runSample")){
			list = runSampleList;
		}else if (order.equals("runSamplePos")){
			list = runSamplePosList;
		}else if (order.equals("sample")){
			list = new ArrayList<DataSet>();
			for (int i=0; i<sampleList.size(); i++) {
				list.add(sampleList.get(i));
			}
		}else if (order.equals("runBlank")){
			list = runBlank;
		}else if (order.equals("runIso")){
			list = runIso;
		}else if (order.equals("runStd")){
			list = runStd;
		}else if (order.equals("allStd")){
			list = allStd;
		}else if (order.equals("allBlank")){
			list = allBlank;
		}else if (order.equals("allStdR")){
			list = allStdR;
		}else if (order.equals("allBlankR")){
			list = allBlankR;
		}else{
			log.error("Error: CalcData.get ("+order+")");
		}
		return (ArrayList<DataSet>)list;
	}
	
	/**
	 * @param name
	 * @return list of values with name
	 */
	public Object[] getRunObjects(String name) {
		Object[] list = new Object[runListR.size()+1];
		for (int i=0; i<runListR.size(); i++) {
			list[i] = runListR.get(i).get(name);
		}
		return list;
	}
//	
	/**
	 * @param start 
	 * @param end 
	 * @return list of values with name
	 */
	public Object[] getRuns(String start, String end) {
		if (runListR.size()>0) {
			if (start==null) { start=runListR.get(0).run; }
			if (end==null) { end=runListR.get(runListR.size()-1).run; }
			ArrayList<String> arr = new ArrayList<String>();
			for (int i=0; i<runListR.size(); i++) {
				if (runListR.get(i).run.compareToIgnoreCase(start)>=0
						&& runListR.get(i).run.compareToIgnoreCase(end)<=0) {
					arr.add(runListR.get(i).run);
				}
			}			
			return arr.toArray();
		} else return null;
	}
	
    /**
     * @param type 
     * @return dataset for StdData and Bl plot
     */
    public CategoryDataset getDatasetStdBl(String type) 
    {
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		dataset.addChangeListener(null);
		DataSet list;
		
		if (type.equals("Blanks"))
		{
			for (int i=0; i<runBlank.size(); i++)
			{
				list = (DataSet) runBlank.get(i);
    	        dataset.add(
    	        		(double)(Double)list.get("ra_cur"), 
    	        		(double)(Double)list.get("ra_cur_err"), 
    	        		(String)list.get("label"), 
    	        		(String)list.get("run")+" pos "+list.get("posit").toString());
			}
			return dataset;   			
		}
		else if (type.equals("Standards"))
		{
			for (int i=0; i<runStd.size(); i++)
			{
				list = (DataSet) runBlank.get(i);
    	        dataset.add(
    	        		(double)(Double)list.get("ra_cur"), 
    	        		(double)(Double)list.get("ra_cur_err"), 
    	        		(String)list.get("label"), 
    	        		(String)list.get("run")+" pos "+list.get("posit").toString());
		}  		
        return dataset; 	
		}
		else
		{
 			return dataset;
		}
    }
    
    /**
     * @param sampleLabel 
     * @param field 
     * @return dataset for StdData and Bl plot
     */
    public ArrayList<Data> getPlotData(String sampleLabel, String field) 
    {
    	ArrayList<Data> values = new ArrayList<Data>();
    	for (int sam=0; sam<runLabelList.size(); sam++) {
			if (runLabelList.get(sam).get(0).sample.label.equals(sampleLabel)) {
				ArrayList<Run> samples = runLabelList.get(sam);
				for (int i=0; i<samples.size(); i++) {
					values.add(new Data((i+1.0), 
							(Double)samples.get(i).get(field), 
							(Double)samples.get(i).get(field+"_err"), 
							(Double)samples.get(i).get(field+"_sig"), 
							(Boolean)samples.get(i).get("active")));
				}
			}
    	}
    	return values;
    }
    
    /**
     * @param type 
     * @return dataset for StdData and Bl plot
     */
    public CategoryDataset getDatasetStdBl13(String type) 
    {
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		dataset.addChangeListener(null);
		DataSet list;
		
		if (type.equals("Blanks"))
		{
			for (int i=0; i<runBlank.size(); i++)
			{
				list = (DataSet) runBlank.get(i);
    	        dataset.add(
    	        		(double)(Double)list.get("ba_cur"), 
    	        		(double)(Double)list.get("ba_cur_err"), 
    	        		(String)list.get("label"), 
    	        		(String)list.get("run")+" pos "+list.get("posit").toString());
			}
			return dataset;   			
		}
		else if (type.equals("Standards"))
		{
			for (int i=0; i<runStd.size(); i++)
			{
				list = (DataSet) runBlank.get(i);
    	        dataset.add(
       	        		(double)(Double)list.get("ba_cur"), 
    	        		(double)(Double)list.get("ba_cur_err"), 
    	        		(String)list.get("label"), 
    	        		(String)list.get("run")+" pos "+list.get("posit").toString());
			}  		
	        return dataset; 	
		}
		else
		{
 			return dataset;
		}
    }
    
	/**
	 * initialises sample
	 * @param sam 
	 * @return sample
	 */
	protected Sample initSampleCalc(Sample sam) {
		Sample sample;
		
		try {sample = new Sample(sam.label);}
		catch (NullPointerException e) {sample = new Sample("error");}

		try {sample.carrier = sam.carrier;}
		catch (NullPointerException e) {sample.carrier = null;}
		
		try {sample.weight= sam.weight;}
		catch (NullPointerException e) {sample.weight = null;}
		
		try {sample.desc1 = sam.desc1;}
		catch (NullPointerException e) {sample.desc1 = null;}

		try {sample.userlabel = sam.userlabel;}
		catch (NullPointerException e) {sample.userlabel = null;}
		try {sample.label = sam.label;}
		catch (NullPointerException e) {sample.username = null;}
		try {sample.username = sam.label;}
		catch (NullPointerException e) {sample.username = null;}
		try {sample.magazine = sam.magazine;}
		catch (NullPointerException e) {sample.magazine = null;}
		try {sample.posit = sam.posit;}
		catch (NullPointerException e) {sample.posit = null;}
		try {sample.timedat = sam.timedat;}
		catch (NullPointerException e) {sample.timedat = null;}
		
		try {sample.type = String.valueOf(sam.type);}
		catch (NullPointerException e) {sample.type = "";}
			
		sample.run = "mean";
		return sample;
	}
	

	/**
	 * Initialize new correction
	 * @return correction for the right isotope
	 */
	abstract protected Corr newCorrection();
	
	/**
	 * recalc current for GraphCurrent.java
	 */
//	abstract protected void calcCurrent();

	/**
	 * initialises sample
	 * @param runs
	 */
	abstract protected void initCalc(ArrayList<Run> runs);
	
	/**
	 * Precalculate sample mean (without blank and std correction)
	 * @param sample 
	 * @param runLabelList 
	 * @return sample
	 */	
	abstract protected Sample samplePreCalc(Sample sample, ArrayList<Run> runLabelList);
		
	/**
	 * Recalculate sample mean
	 * @param sample 
	 * @param runL 
	 * @param correction 
	 * @return sample
	 */	
	abstract protected Sample sampleCalc(Sample sample, ArrayList<Run> runL, ArrayList<Corr> correction);
		
	/**
	 * Calculate run mean
	 * @param runs 
	 * @param correction 
	 */		
	abstract protected void runCalc(ArrayList<Run> runs, ArrayList<Corr> correction);

	/**
	 * Pre-calculate run mean (with blank and std correction)
	 * @param runs 
	 */		
	abstract protected void runPreCalc(ArrayList<Run> runs);

}
