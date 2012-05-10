import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import org.jdom.Element;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lukas
 * Creats a table for showing the data
 */
public class StdNomTable extends JPanel {
	final static Logger log = Logger.getLogger(StdNomTable.class);
    
	StdNom stdNom;
	MyTableModel myTableModel;
	private ArrayList<String> columnField = new ArrayList<String>();
	private ArrayList<String> columnName = new ArrayList<String>();
	
    /**
     *  
     */
	public void updateTable() {
		myTableModel.fireTableDataChanged();
    }
	
	/**
     * @param stdNom 
     */
    @SuppressWarnings("unchecked")
	public StdNomTable(StdNom stdNom) {
		this.stdNom = stdNom;
		setIsotope(stdNom.isotope);
		myTableModel = new MyTableModel();
	    JTable table = new JTable(myTableModel);
	    // Table settings
	    table.setPreferredScrollableViewportSize(new Dimension(120+columnName.size()*80, 330));	    
		int gapWidth = 4;
		int gapHeight = 0;
		table.setIntercellSpacing(new Dimension(gapWidth, gapHeight));
		table.setShowGrid(false);
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		//Add the scroll pane to this panel.
		add(scrollPane);		
		// Set column width
		TableColumn column;		
		for (int i = 0; i < columnName.size(); i++) {
			int width = 80;
			if (i==0) {
				width = 120;
			}
    		column = table.getColumnModel().getColumn(i);
    		column.setMinWidth( width );
    		column.setPreferredWidth( width );
    		column.setHeaderRenderer(new MyTableHeaderRenderer());
	    }
	    	
	    TableCellRenderer renderer = new EvenOddRenderer();
	    table.setDefaultRenderer(Double.class, renderer);
	    table.setDefaultRenderer(String.class, renderer);
	    table.setDefaultRenderer(Integer.class, renderer);	    
   } 
    
    /**
     * @author lukas
     *
     */
    protected class MyTableHeaderRenderer extends JLabel implements TableCellRenderer, SwingConstants {
		/**
		 * 
		 */
		public final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
			
		public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col) {
			Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, 
					  isSelected, hasFocus, row, col);			  
			renderer.setForeground(Setting.getColor("/bat/general/table/color/fg_head"));
			renderer.setBackground(Setting.getColor("/bat/general/table/color/bg_head"));
			((JLabel) renderer).setHorizontalAlignment(LEFT);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return renderer;
		}
    }
        
    class MyTableModel extends AbstractTableModel { 	
		// Get number of columns
		public int getColumnCount() {
		    return columnName.size();
		}
		// get number of rows
		public int getRowCount() {
		    return stdNom.stdList.size();
		}
		// get column names
		public String getColumnName(int col) {
		    return columnName.get(col);
		}
		// get values for each field of the table
		public Object getValueAt(int row, int col) {
			return stdNom.stdList.get(row).get(columnField.get(col)); 
		}
        //determine the default renderer editor for each cell ( important for check box)
		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			try {
				return getValueAt(0, c).getClass();
			} catch (NullPointerException e) {
				return null;
			}
       }
        // here some cells are set editable
        public boolean isCellEditable(int row, int col) {
			return true;
        }
        // Only needed when table is editable
        public void setValueAt(Object value, int row, int col) {
			stdNom.stdList.get(row).set(value, columnField.get(col));
            fireTableCellUpdated(row, col);
       }
    }
    
    class EvenOddRenderer implements TableCellRenderer, SwingConstants {
    	  /**
    	 * 
    	 */
    	public final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    	  public Component getTableCellRendererComponent(JTable table, Object value, 
    			  boolean isSelected, boolean hasFocus, int row, int column) {
    		  Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, 
    				  hasFocus, row, column);
    		  Color foreground, background;
    		  if (isSelected) {
	    	      foreground = Color.black;
	    	      background = Color.green;
    		  } else {
	    	      if (row % 2 == 0) {
	    	    	  foreground = Color.black;
	    	    	  background = Color.white;
	    	      } else {
	    	    	  foreground = new Color(0,0,160);;
	    	    	  background =  new Color(230,230,230);
	    	      }
    	      }
    		  renderer.setForeground(foreground);
    		  renderer.setBackground(background);    		  
//    		  ((JLabel) renderer).setHorizontalAlignment(RIGHT);
    		  return renderer;
    	  }

    	}
    
    @SuppressWarnings("unchecked")
	private void setIsotope(String isotope) {
    	List<Element> list = Setting.getElement("/bat/isotope/std-setup").getChildren();
    	for (int i=0; i<list.size(); i++) {
    		columnField.add(list.get(i).getChildText("field"));
    		columnName.add(list.get(i).getChildText("name"));   		
    	}
    }
}