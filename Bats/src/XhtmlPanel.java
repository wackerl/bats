import java.awt.BorderLayout;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.XHTMLPrintable;

/**
 * @author lukas
 *
 */
public class XhtmlPanel extends JPanel {
	final static Logger log = Logger.getLogger(XhtmlPanel.class);
	private XHTMLPanel xp;
	private XhtmlFile fileX;
	private File file;
	FormatT format;
	
	XhtmlPanel(Calc data, String fileName, FormatT format) {
		this.format = format;
        this.setLayout(new BorderLayout());
        fileX = new XhtmlFile(fileName, data, format);
    	xp = new XHTMLPanel();
    	xp.setMinFontScale((float) 0.5);
    	xp.setMaxFontScale((float) 2.0);
    	file = new File(fileName);
    	if (!Setting.no_data) {
	    	try {
				xp.setDocument(file);
			} catch (Exception e) {
				log.error("Could not open .xhtml output file: "+file);
			}
    	}
		this.add(new FSScrollPane(xp));	
	}
	
	/**
	 * @param data
	 * @param fileName
	 */
	public void update(Calc data, String fileName) {
        fileX.update(data, fileName);
    	file = new File(fileName);
    	log.debug(file.exists());
    	log.debug("Start update output view (XHTML).");
    	if (!Setting.no_data) {
	    	try {
				xp.setDocument(file);
			} catch (Exception e) {
				log.error("Could not open xhtml-file properly: "+file);
				log.error(e.toString());
			}
			log.debug("finsished update");
    	}
	}
	

	/**
	 * @param bool 
	 * 
	 */
	public void increase(Boolean bool) {
    	xp.setFontScalingFactor(Float.valueOf(Setting.getString("/bat/general/file/output/fontscaling")));
    	try {
    		if(bool) {
    			xp.incrementFontSize();
    		} else {
    			xp.decrementFontSize();
    		}
			xp.setDocument(file);
		} catch (Exception e) {
			log.error("Could not change font size.");
			log.debug(e.toString());
		}
		log.debug("font size increased");		
	}
	
    /**
     * Print document
     */
    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new XHTMLPrintable(xp));

        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException e) {
                log.debug("File could not be printed: "+e);
            }
        }
    }

}
