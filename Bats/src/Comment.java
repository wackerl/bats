import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;


/**
 * @author lukas
 *
 */
public class Comment implements WindowListener {
	final static Logger log = Logger.getLogger(Comment.class);
	private JEditorPane htmlPane;
//	Bats main;

	Comment(String text) {
//		this.main = main;
		htmlPane = new JEditorPane();
		htmlPane.setText(text);
		htmlPane.setEditable( true );
	    
	    htmlPane.setOpaque(true);
		htmlPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	/**
	 * @return frame with comment
	 */
	public JFrame show() {
		JScrollPane sPane = new JScrollPane(htmlPane);
		sPane.setPreferredSize(new Dimension(300,200));
		JFrame frame = new JFrame();
		frame.addWindowListener(this);
		frame.setAlwaysOnTop(true);
		frame.setTitle("Comment");
		frame.setContentPane(sPane);
	    frame.pack();
	    frame.setVisible(true);
	    return frame;
	}
	
	/**
	 * @return comment
	 */
	public String getText() {
		return htmlPane.getText();
	}
	
	/**
	 * @param text
	 */
	public void setText(String text) {
		htmlPane.setText(text);
	}
	 
	/**
	 * @param e
	 */
	public void windowClosing(WindowEvent e) {
//		main.dataRecalc();
//		log.debug("Closed comment");
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
