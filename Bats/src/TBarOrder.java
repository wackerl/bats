import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class TBarOrder extends JToolBar implements ItemListener {
	
	final static Logger log = Logger.getLogger(TBarOrder.class);
    static int p = Setting.getInt("/bat/general/font/p");
	static String ft = Setting.getString("/bat/general/font/type");
	
	Action action;
	
	/**
	 * @param action 
	 * 
	 * 
	 */	
	public TBarOrder(Action action) { 
		this.action = action;
		
		String path;
						
		JCheckBox box = new JCheckBox("ordered by run (std/bl)");
		box.setFont(new Font(ft, 0, p));
		path = "/bat/general/table/run_order";
	    box.setSelected(Setting.getBoolean(path));
	    box.setName(path);
	    box.addItemListener(this);
        this.add(box);

//		this.addSeparator();
//		box = new JCheckBox("edit runs");
//		box.setFont(new Font(ft, 0, p));
//		path = "/bat/isotope/db/cycle_edit";
//	    box.setSelected(Setting.getBoolean(path));
//	    box.setName(path);
//	    box.addItemListener(this);
//        this.add(box);

	
	}
	
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox)e.getItemSelectable();
    	Setting.getElement(source.getName()).setText(Boolean.toString(source.isSelected()));
		log.debug(source.getName()+" changed to "+Boolean.toString(source.isSelected()));
		action.exec("Recalculate");
 	} 
}
