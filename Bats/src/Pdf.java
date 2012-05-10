import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.*;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.adobe.acrobat.Viewer;
import com.adobe.acrobat.ViewerCommand;
import com.lowagie.text.DocumentException;


/**
 * @author lukas
 *
 */
public class Pdf {
	final static Logger log = Logger.getLogger(MenuBar.class);

	/**
	 * 
	 */
	public Pdf() {
	}
	
	static void save(String inputFile, String outputFile)  {
		try {
	        String url = new File(inputFile).toURI().toURL().toString();
	        OutputStream os = new FileOutputStream(outputFile);	        
	        ITextRenderer renderer = new ITextRenderer();
	        renderer.setDocument(url);
	        renderer.layout();
	        renderer.createPDF(os);	        
	        os.close();	
		} catch (IOException e) {
			log.error("File I/O problem!");
		} catch (DocumentException e) {
			log.error("Document exception (pdf)!");
		}
	}
	
	static void preview(String inputFile, String outputFile)  {
		try {
//	        String inputFile = Setting.homeDir+"/"+Setting.magazine+"/"+Setting.magazine+".xhtml";
	        String url = new File(inputFile).toURI().toURL().toString();
	        OutputStream os = new FileOutputStream(outputFile);	        
	        ITextRenderer renderer = new ITextRenderer();
	        renderer.setDocument(url);
	        renderer.layout();
	        renderer.createPDF(os);	        
	        os.close();
	        
			JFrame frame = new JFrame("PDF Viewer");
			frame.setLayout(new BorderLayout());		
			frame.setPreferredSize(new Dimension(900, 700));
			try {
				String[] dis = {ViewerCommand.Open_K, ViewerCommand.OpenURL_K , ViewerCommand.EditCut_K , ViewerCommand.ShowBookmarks_K};
				Viewer viewer = new Viewer(dis);
				frame.add(viewer, BorderLayout.CENTER);
				InputStream input = new FileInputStream (new File(outputFile));
				viewer.setDocumentInputStream(input);
				viewer.setProperty("Default_Zoom_Type","FitPage");
				viewer.activate();
				frame.pack(); 
		        frame.setVisible(true);
			} catch (Exception e) {
				log.debug("Could not open pdf viewer!");
				e.printStackTrace();
			}

		} catch (IOException e) {
			log.error("File I/O problem!");
		} catch (DocumentException e) {
			log.error("Document exception (pdf)!");
		}
	}
}
