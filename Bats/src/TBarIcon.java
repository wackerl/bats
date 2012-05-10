import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.jdom.DataConversionException;

/**
 * @author lukas
 *
 */
public class TBarIcon extends JToolBar implements ActionListener {
	
	final static Logger log = Logger.getLogger(TBarIcon.class);
	
	Action action;
	
	ToolBarButton button1, button2, button3, button4, button5, button6, button7, button8, button9;
	
	/**
	 * @param action 
	 * 
	 */	
	public TBarIcon(Action action) { 
		this.action = action;
		Insets margins = new Insets(2, 2, 2, 2);
		
		ToolBarButton button = new ToolBarButton(Setting.batDir+"/icon/open20.png");
		button.setFocusPainted(true);
		button.setToolTipText("Open data file...");
		button.setName("Open data file...");
		button.setMargin(margins);
		button.addActionListener(this);
		this.add(button);
		
		button1 = new ToolBarButton(Setting.batDir+"/icon/save20.png");
		button1.setFocusPainted(true);
		button1.setToolTipText("Save data file...");
		button1.setName("Save data file...");
		button1.setMargin(margins);
		button1.addActionListener(this);
	    button1.setEnabled(!Setting.no_data);
		this.add(button1);

		this.addSeparator();

		if (Setting.db!=null) {
			button = new ToolBarButton(Setting.batDir+"/icon/open_db20.png");
			button.setFocusPainted(true);
			button.setToolTipText("New magazine from database");
			button.setName("New db");
			button.setMargin(margins);
			button.addActionListener(this);
			this.add(button);
			
			button = new ToolBarButton(Setting.batDir+"/icon/open_a_db20.png");
			button.setFocusPainted(true);
			button.setToolTipText("New data from latest magazine in database");
			button.setName("Latest db");
			button.setMargin(margins);
			button.addActionListener(this);
			this.add(button);
			
			button2 = new ToolBarButton(Setting.batDir+"/icon/open_add20.png");
			button2.setFocusPainted(true);
			button2.setToolTipText("Add magazine from database");
			button2.setName("Add db");
			button2.setMargin(margins);
			button2.addActionListener(this);
		    button2.setEnabled(!Setting.no_data);
			this.add(button2);
			Boolean active;
	        try {
				active = Setting.getElement("/bat/isotope/db/sql").getAttribute("active").getBooleanValue();
			} catch (DataConversionException e) { active = false; log.debug(""); }
			button3 = new ToolBarButton(Setting.batDir+"/icon/save_db20.png");
			if (active) {
				button3.setFocusPainted(true);
				button3.setToolTipText("Save to DB...");
				button3.setName("Save as db");
				button3.setMargin(margins);
				button3.addActionListener(this);
			    button3.setEnabled(!Setting.no_data);
				this.add(button3);
			}
	
			this.addSeparator();
		}

		button4 = new ToolBarButton(Setting.batDir+"/icon/notes20.png");
		button4.setFocusPainted(true);
		button4.setToolTipText("Comment for data-set");
		button4.setName("Comment");
		button4.setMargin(margins);
		button4.addActionListener(this);
	    button4.setEnabled(!Setting.no_data);
		this.add(button4);
		
		button5 = new ToolBarButton(Setting.batDir+"/icon/calc20.png");
		button5.setFocusPainted(true);
		button5.setToolTipText("Force recalculate");
		button5.setName("Force recalculate");
		button5.setMargin(margins);
		button5.addActionListener(this);
	    button5.setEnabled(!Setting.no_data);
		this.add(button5);
		
		button9 = new ToolBarButton(Setting.batDir+"/icon/calib20.png");
		button9.setFocusPainted(true);
		button9.setToolTipText("Calibrate with SwissCal");
		button9.setName("calibMan");
		button9.setMargin(margins);
		button9.addActionListener(this);
		this.add(button9);

		this.addSeparator();

		button6 = new ToolBarButton(Setting.batDir+"/icon/xhtml20.png");
		button6.setFocusPainted(true);
		button6.setToolTipText("Output to default browser");
		button6.setName("Browser");
		button6.setMargin(margins);
		button6.addActionListener(this);
	    button6.setEnabled(!Setting.no_data);
		this.add(button6);

		button7 = new ToolBarButton(Setting.batDir+"/icon/print20.png");
		button7.setFocusPainted(true);
		button7.setToolTipText("Print preview (pdf)");
		button7.setName("Print pdf");
		button7.setMargin(margins);
		button7.addActionListener(this);
	    button7.setEnabled(!Setting.no_data);
		this.add(button7);

		this.addSeparator();

		button = new ToolBarButton(Setting.batDir+"/icon/pref20.png");
		button.setFocusPainted(true);
		button.setToolTipText("Preferences");
		button.setName("Preferences");
		button.setMargin(margins);
		button.addActionListener(this);
		this.add(button);

		button = new ToolBarButton(Setting.batDir+"/icon/help20.png");
		button.setFocusPainted(true);
		button.setToolTipText("Help");
		button.setName("Help");
		button.setMargin(margins);
		button.addActionListener(this);
		this.add(button);
		
		button8 = new ToolBarButton(Setting.batDir+"/icon/exit20.png");
		button8.setFocusPainted(true);
		button8.setToolTipText("Quit");
		button8.setName("Quit");
		button8.setMargin(margins);
		button8.addActionListener(this);
		this.add(button8);
		
	}
	
	/**
	 * 
	 */
	public void refresh() {
		if (Setting.db!=null) {
			button1.setEnabled(!Setting.no_data);
			button2.setEnabled(!Setting.no_data);
			button3.setEnabled(!Setting.no_data);
		}
		button4.setEnabled(!Setting.no_data);
		button5.setEnabled(!Setting.no_data);
		button6.setEnabled(!Setting.no_data);
		button7.setEnabled(!Setting.no_data);
	}
	
	public void actionPerformed(ActionEvent e) {
		ToolBarButton source = (ToolBarButton)(e.getSource());
		action.exec(source.getName());
	}
}
