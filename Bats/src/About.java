import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;

/**
 * @author lukas
 *
 */
public class About extends JFrame {
	final static Logger log = Logger.getLogger(About.class);
	private XHTMLPanel xp;
	FormatT format;
	
	About(String fileName) {
    	xp = new XHTMLPanel();
    	xp.setFontScalingFactor((float) 1.0);
//    	xp.incrementFontSize();
		xp.setPreferredSize(new Dimension(635,335));
    	File file = new File(fileName);
    	try {
			xp.setDocument(file);
			log.debug("About file added: "+file);
		} catch (FileNotFoundException e) {
			log.error("xhtml-file not found: "+file);
		} catch (Exception e) {
			log.error("Could not open xhtml-file: "+file);
		}
		this.add(new FSScrollPane(xp));	
		this.setPreferredSize(new Dimension(635,500));
		this.setContentPane(new FSScrollPane(xp));
        this.pack();
        this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
