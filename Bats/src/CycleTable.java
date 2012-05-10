import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * @author lukas
 * Creats a table for showing the data
 */
public class CycleTable extends JPanel {
    final static Logger log = Logger.getLogger(CycleTable.class);
    
    ArrayList<Cycle> cycList;
    ArrayList<DataSet> data;
    Bats main;
    TableColumn column = null;
    JTable table;
    Run run;
    ArrayList<String> locked;
    Boolean mean;
    static int CH = Setting.getInt("/bat/general/table/c_hight");
    static int T = Setting.getInt("/bat/general/table/top");
    static int gapX = Setting.getInt("/bat/general/table/gap_x");
    static int gapY = Setting.getInt("/bat/general/table/gap_y");
    static int fs = Setting.getInt("/bat/general/font/p");
	static String ft = Setting.getString("/bat/general/font/type");
        
    MyTableModel myTableModel;
    
    /**
     * 
     */
    public FormatT format;
	
    /**
     * @param cycList 
     * @param run 
     * @param format 
     * @param main 
      */
    public CycleTable(ArrayList<Cycle> cycList, Run run, FormatT format, Bats main) {
    	super(new GridLayout(1,0));
    	this.run = run;
    	this.main = main;
    	data = new ArrayList<DataSet>();
     	for (int i=0; i<cycList.size(); i++) {
    		data.add(cycList.get(i));
    	}
    	data.add(run);
    	this.format = format;

    	myTableModel = new MyTableModel();
    	table = new JTable(myTableModel);
    
		table.setIntercellSpacing(new Dimension(gapX, gapY));
		table.setShowGrid(false);       
		
		table.setRowHeight(CH);
		table.setFont(new Font(ft,0 , fs));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Set column width
		this.setCol();
		// Set own renderer for the right format of number and cell
    	TableCellRenderer renderer = new Renderer(format);
    	table.setDefaultRenderer(String.class, renderer);
    	table.setDefaultRenderer(Integer.class, renderer);
    	table.setDefaultRenderer(Double.class, renderer);
    	table.setDefaultRenderer(Number.class, renderer);
    	table.setDefaultRenderer(Date.class, renderer);
	    table.setDefaultEditor(ImageIcon.class, new ButtonEditor(new JCheckBox()));	    
		//Create the scroll pane and add the table to it.
	    table.setPreferredScrollableViewportSize(this.getSize());	    
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);
   }
    
    
    // for autoresize
    public Dimension getSize() {
		int width = Setting.getInt("/bat/general/frame/cycle/width");
        int height = Setting.getInt("/bat/general/frame/cycle/height");
		Dimension dim = table.getPreferredSize();
		log.debug(dim.height+"--"+dim.getHeight());
		log.debug(dim.width+"--"+dim.getWidth());
		if (dim.getHeight()>height) {
			dim.height=height;
		}
		if (dim.getWidth()>width) {
			dim.width=width;
		}
    	return dim;
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
			renderer.setPreferredSize(new Dimension(0,T));
			((JLabel) renderer).setHorizontalAlignment(LEFT);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return renderer;
		}
    }
    
    private void setCol(){
		for (int i = 0; i < format.colEdit.size(); i++) {
	    		column = table.getColumnModel().getColumn(i);
	    		column.setPreferredWidth( format.colWidth.get(i) );
	    		column.setHeaderRenderer(new MyTableHeaderRenderer());
	    		column.setHeaderValue(format.colName.get(i));
	    }
    }
    
    class MyTableModel extends AbstractTableModel { 	
    		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		// Get number of columns
		public int getColumnCount() 
		{
		    return format.colEdit.size();
		}
		// get number of rows
		public int getRowCount() 
		{
		    return data.size();
		}
		// get column names
		public String getColumnName(int col) 
		{
		    return (String) format.colName.get(col);
		}
		@SuppressWarnings("unchecked")
		public Class getColumnClass(int col) 
		{ 
			try {
				return getValueAt(0, col).getClass();
			} catch (NullPointerException e) {
				return String.class;
			}
		}
		// get values for each field of the table
		public Object getValueAt(int row, int col) {			
			Object cell;
			try {
				if (data.get(row).getClass().equals(Run.class)) {
					cell = ((Run)data.get(row)).get(format.colValue.get(col));				
					if (cell.getClass().equals(Double.class)) {
						cell = (Double)cell * format.colMulti.get(col);
					}
				} else {
					cell = ((Cycle)data.get(row)).get(format.colValue.get(col));				
					if (cell.getClass().equals(Double.class)) {
						cell = (Double)cell * format.colMulti.get(col);
					}
				}
			}
			catch(NullPointerException e) {
				cell=null;
			}
			return cell;
		}

		public boolean isCellEditable(int row, int col) {
			if (format.colEdit.get(col)==true) {
				return true;
			}
			else return false;
        }
        // Only needed when table is editable
        public void setValueAt(Object value, int row, int col) {
        	if (data.get(row).getClass().equals(Run.class)) {
//	        		((Run)data.get(type).get(row)).set(value, format.colValue.get(col));		
        	} else if (format.colValue.get(col).equals("active")) {
        		((Cycle)data.get(row)).set(value, "active");
                main.db.getConn().setActive(run.run,((Cycle)data.get(row)).cycle,((Cycle)data.get(row)).active);
				main.db.updateRun(run);
				main.dataRecalc();
				this.fireTableDataChanged();
        	} else {
        		JOptionPane.showMessageDialog( null, "<html>You are not allowed to change raw data!<br> ("+format.colValue.get(col)+")</html>");
        		log.debug("You are not allowed to change raw data! ("+format.colValue.get(col)+")");
        	}
        }
    }
    
    class Renderer implements TableCellRenderer, SwingConstants {
		FormatT format;
		Renderer(FormatT form) {
			format=form;
    	}
		/**
		 * 
		 */
		public final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
	
		public Component getTableCellRendererComponent(JTable table, Object value, 
				  boolean isSelected, boolean hasFocus, int row, int column) {
			
			  Color foreground, background;
			  if (isSelected) {
				  foreground = Setting.getColor("/bat/general/table/color/fg_select");
				  background = Setting.getColor("/bat/general/table/color/bg_select");
			  } 
			  else if (data.get(row).getClass().equals(Run.class)) {
				  foreground = Setting.getColor("/bat/general/table/color/fg_sample");
				  background = Setting.getColor("/bat/general/table/color/bg_sample");
			  }
			  else{
	    	      if (row % 2 == 0) {
	    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_even");
	    	    	  	background = Setting.getColor("/bat/general/table/color/bg_even");
	    	      } else {
	    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_odd");
	    	    	  	background = Setting.getColor("/bat/general/table/color/bg_odd");
	    	      }
		      }

			  int align = LEFT;
			  if (format.colFormat.get(column)!="" && (value instanceof Number)){
			      Number numberValue = (Number) value;
			      DecimalFormat form = new DecimalFormat(format.colFormat.get(column));
				  value = form.format(numberValue.doubleValue());
				  align = RIGHT;
				  if (format.colChi.get(column)==true) {
					  int a = ((DataSet)data.get(row)).number()-1;
					  if (a<=0 || a>100) {foreground = Color.orange;}
					  else if (numberValue.doubleValue()<Setting.chiLimitL[a-1]/a || numberValue.doubleValue()>Setting.chiLimitH[a-1]/a) {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_limit");
					  }
				  } else if (!format.colLimitL.get(column).equals(format.colLimitH.get(column))) {
					  if (numberValue.doubleValue()<format.colLimitL.get(column) || numberValue.doubleValue()>format.colLimitH.get(column)) {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_limit");
					  }
				  }
			  }
			  else if (format.colFormat.get(column)!="" && (value instanceof Date)){
			      Date dateValue = (Date) value;
			      SimpleDateFormat form = new SimpleDateFormat(format.colFormat.get(column));
				  value = form.format(dateValue);
				  align = RIGHT;
			  }

			  Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, 
					  isSelected, hasFocus, row, column);
			  
			  ((JLabel) renderer).setHorizontalAlignment(align);
			  renderer.setForeground(foreground);
			  renderer.setBackground(background);
			  if (data.get(row) instanceof Run) {
		    	  	((JComponent) renderer).setBorder(BorderFactory.createMatteBorder(0,0,2,0,Setting.getColor("/bat/general/table/color/line_sample")));
			  }
			  return renderer;
		 }
    }
    
    /**
     *
     */
	public void updateTableStructureChanged() {
		myTableModel.fireTableStructureChanged();
		setCol();
    }


	/**
	 * 
	 */
	class ButtonEditor extends DefaultCellEditor {
	  protected JButton button;
	  ImageIcon label;
	  private boolean isPushed;

		  /**
		 * @param checkBox
		 */
		public ButtonEditor(JCheckBox checkBox) {
		    super(checkBox);
		    button = new JButton();
		    button.setOpaque(true);
		    button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        fireEditingStopped();
		      }
		    });
	  }

	  public Component getTableCellEditorComponent(JTable table, Object value,
	                   boolean isSelected, int row, int column) {
		  label = (ImageIcon)value;
		  button.setIcon( label );
		  isPushed = true;
		  return button;
	  }

	  public Object getCellEditorValue() {
	    if (isPushed)  {
	      log.debug("Button pressed! ("+label+")");
	    }
	    isPushed = false;
	    return label ;
	  }
	  
	  public boolean stopCellEditing() {
	    isPushed = false;
	    return super.stopCellEditing();
	  }

	  protected void fireEditingStopped() {
	    super.fireEditingStopped();
	  }
	}
}