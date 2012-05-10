import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.jdom.DataConversionException;

/**
 * @author lukas
 *
 */
public class CycleData implements WindowListener {

	static Logger log = Logger.getLogger(CycleData.class);

	ArrayList<DataSet> data;
	Bats main;
	FormatT format;
	Run run;
	
	/**
	 * @param main
	 * @param format 
	 */
	public CycleData(Bats main, FormatT format) {
		this.main = main;
		this.format = format;
		run=null;
	}
	
	/**
	 * @param run
	 */
	public void getCycleTable(Run run) {
		this.run=run;
		ArrayList<Cycle> cycList=null;
		DbCycle cycleConn= main.db.getConn();
		log.debug(cycleConn);
    	log.debug("Edit run: "+run.run);
    	if (cycleConn!=null) {
    		cycList = cycleConn.getCycleList(run.run);
			if (cycList!=null) {
				JDialog dialog = new JDialog(main,"Cycles of run "+run.run);
				dialog.setModal(true);
				CycleTable table = new CycleTable(cycList, run, format, main);
				dialog.add(table);
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				dialog.addWindowListener((WindowListener) this);
				//Display the window.
			    dialog.pack();
			    dialog.setVisible(true);
			}
    	} else {
    		log.debug("Could not connect to DB.");
    	}
	}
	
	public void windowActivated(WindowEvent e) {;}

	public void windowClosed(WindowEvent e) {
		try {
			if (Setting.getElement("/bat/isotope/db").getAttribute("active").getBooleanValue()) {
				main.db.updateRun(run);
				main.dataRecalc();

			} else {
				main.db.updateRun(run);
				main.dataRecalc();
			}
		} catch (DataConversionException e1) {
			main.db.updateRun(run);
			main.dataRecalc();
		}
	}

	public void windowClosing(WindowEvent e) {;}

	public void windowDeactivated(WindowEvent e) {;}

	public void windowDeiconified(WindowEvent e) {;}

	public void windowIconified(WindowEvent e) {;}

	public void windowOpened(WindowEvent e) {;}
}
