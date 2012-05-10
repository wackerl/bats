import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.apache.log4j.Logger;


/**
 * @author lukas
 *
 */
public class LogPanel extends JFrame implements HyperlinkListener {
	final static Logger log = Logger.getLogger(LogPanel.class);
	JEditorPane htmlPane;
	LogPanel(String file) {
		this.setTitle("Help");
		try 
		{
			htmlPane = new JEditorPane( "File://"+file);
			htmlPane.setEditable( false );
		    htmlPane.addHyperlinkListener( this );
		    try
		    {
		      hyperlinkUpdate( new HyperlinkEvent(this,
                  HyperlinkEvent.EventType.ACTIVATED,
                  new URL("File://"+file) ) );
		    }
		    catch ( MalformedURLException e ) { e.printStackTrace(); }
		    
		    htmlPane.setOpaque(true);
    		htmlPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			JScrollPane sPane = new JScrollPane(htmlPane);
			sPane.setPreferredSize(new Dimension(800,600));
            this.setContentPane(sPane);
            this.pack();
            this.setVisible(true);


		} catch( IOException e ) {
			log.error( "Error displaying: "+e );
		}
	}
	
	 public void hyperlinkUpdate( HyperlinkEvent event )
	  {
	    HyperlinkEvent.EventType typ = event.getEventType();
	    if ( typ == HyperlinkEvent.EventType.ACTIVATED )
	    {
	      try
	      {
	        setTitle( "" + event.getURL() );
	        htmlPane.setPage( event.getURL() );
	      }
	      catch( IOException e ) {
	        JOptionPane.showMessageDialog( this,
	                                      "Can't follow link to "
	                                        + event.getURL().toExternalForm(),
	                                      "Error",
	                                      JOptionPane.ERROR_MESSAGE);
	      }
	    }
	  }
}
