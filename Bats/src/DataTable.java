import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
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
import org.jdom.DataConversionException;

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
public class DataTable extends JPanel {
    final static Logger log = Logger.getLogger(DataTable.class);
    
    Calc data;
    Bats main;
    TableColumn column = null;
    JTable table;
    String type;
    ArrayList<String> locked;
    Boolean mean;
    static int CH;
    static int T;
    static int gapX;
    static int gapY;
    static int fs;
	static String ft;
	boolean cycEdit;
	boolean calib;
        
    MyTableModel myTableModel;
    
    /**
     * 
     */
    public FormatT format;
	
    /**
     * @param main 
     * @param data 
     * @param type 
     * @param format 
     * @param mean 
     */
    public DataTable(Bats main, Calc data, String type, FormatT format, Boolean mean) {
    	
    	super(new GridLayout(1,0));

    	CH = Setting.getInt("/bat/general/table/c_hight");
        T = Setting.getInt("/bat/general/table/top");
        gapX = Setting.getInt("/bat/general/table/gap_x");
        gapY = Setting.getInt("/bat/general/table/gap_y");
        fs = Setting.getInt("/bat/general/font/p");
    	ft = Setting.getString("/bat/general/font/type");
    	cycEdit = Setting.getBoolean("/bat/isotope/db/cycle_edit");
    	if (Setting.isotope.equals("C14")) {
	    	try {
				calib = Setting.getElement("/bat/calib/oxcal").getAttribute("active").getBooleanValue();
			} catch (DataConversionException e) {
				log.warn("Could not read oxcal attribute!");
				calib=false;
			}
    	} else {
    		calib=false;
    	}

    	this.data = data;
    	this.main = main;
    	this.type = type;
    	if (type.equals("runSample")&&Setting.getInt("/bat/general/table/sort")==1) {
    		this.type = "runSamplePos";
    	}
    	this.format = format;
    	this.mean = mean;

//    	locked.a{"a","b","g1","g2","r"};
    	
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
    	table.setDefaultRenderer(JButton.class, renderer);
	    table.setDefaultRenderer(JButton.class, new ButtonRenderer());	
	    table.setDefaultEditor(JButton.class, new ButtonEditor(new JCheckBox()));	
		//Create the scroll pane and add the table to it.
//	    table.setPreferredScrollableViewportSize(new Dimension(900, 800));	    
		JScrollPane scrollPane = new JScrollPane(table);
//		table.setPreferredScrollableViewportSize(new Dimension(1000, 70));
		this.add(scrollPane);
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
		    return data.get(type).size();
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
				cell = (data.get(type).get(row)).get(format.colValue.get(col));
				if (data.get(type).get(row).getClass().equals(Sample.class)) {
					if (cell.getClass().equals(Double.class)) {
						cell = (Double)cell * format.colMulti.get(col);
					} else if (format.colValue.get(col).equalsIgnoreCase("run") || format.colValue.get(col).equalsIgnoreCase("label")) {
						cell = new JButton((String)((Sample)data.get(type).get(row)).get(format.colValue.get(col)));
					}
				} else {
					if (format.colValue.get(col).equalsIgnoreCase("run") || format.colValue.get(col).equalsIgnoreCase("label")) {
						cell = new JButton((String)((Run)data.get(type).get(row)).get(format.colValue.get(col)));
					} else if (cell.getClass().equals(Double.class)) {
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
				if (((DataSet)data.get(type).get(row)).active()||format.colValue.get(col).equals("active")) {
					return true;
				}
				else return false;
        	} 
			else return false;
        }
        // Only needed when table is editable
        public void setValueAt(Object value, int row, int col) {
        	if (format.colValue.get(col).equals("a") || format.colValue.get(col).equals("b")
        			|| format.colValue.get(col).equals("r") || format.colValue.get(col).equals("runtime")) {
        		JOptionPane.showMessageDialog( null, "<html>You are not allowed to change raw data!<br> ("+format.colValue.get(col)+")</html>");
        		log.debug("You are not allowed to change raw data! ("+format.colValue.get(col)+")");
        	} else if (!data.get(type).get(row).active()&&!format.colValue.get(col).equals("active")) {
        		if (format.colValue.get(col).equals("active")) {
        			;
				}
        	} else {
	        	if (value.getClass().equals(Double.class)) {
					value = (Double)value / format.colMulti.get(col);
				}
	        	if ((format.colValue.get(col).equals("sampletype") && cycEdit)) {
	        		data.get(type).get(row).set(value, format.colValue.get(col));
	        		main.dataRecalc();
	        	} else if (data.get(type).get(row).getClass().equals(Sample.class)) {
		        	if ((format.colValue.get(col).equals("label") || format.colValue.get(col).equals("comment"))) {
		        		if (calib) {
		    				log.debug("Open oxcal");
		    				new CalibOxcal((Sample)data.get(type).get(row));
		        		} else {
		    				log.debug("Calibrate sample: "+((Sample)data.get(type).get(row)).label);
		    				main.calib.plotSample((Sample)data.get(type).get(row));
		        		}
		        	} else if (format.colValue.get(col).equals("active")) {		        		
		        		String label =((Sample)data.get(type).get(row)).label;
		        		Boolean bval = (Boolean)value;
		        		log.debug("set active of all runs");		        		
		        		for (int i=0; i<data.sampleList.size(); i++) {
		        			
		        			if (data.sampleList.get(i).label.equals(label)) {
		        				data.sampleList.get(i).active=bval;
		        				for (int k=0; k<data.runLabelList.get(i).size(); k++){
		        					if (main.db.runTrue(data.runLabelList.get(i).get(k), bval)) {
			        					try {data.runLabelList.get(i).get(k).active = bval;}
			        					catch (NullPointerException e) {data.runLabelList.get(i).get(k).active = true;log.error("Could not get active from sample. active set to true!");}
		        					}
		        				}
		        			}
		        		}
	    				log.debug("Set "+((Sample)data.get(type).get(row)).label);
					} else {
						((Sample)data.get(type).get(row)).set(value, format.colValue.get(col));
	    				log.debug("Set "+((Sample)data.get(type).get(row)));
					}
				} else {
			       	if ((format.colValue.get(col).equals("run") && cycEdit)) {
		        		main.cycDat.getCycleTable((Run)data.get(type).get(row));
		        	} else if (format.colValue.get(col).equals("active")) {
		        		if (main.db.runTrue((Run)data.get(type).get(row), (Boolean)value)) {
	        				((Run)data.get(type).get(row)).set(value, "active");	
		        		} else {
		        			log.warn("Could not activate/deactivate run!");
		        		}
		        	} else if ( format.colValue.get(col).equals("label") && calib) {
//	    				log.debug("Open calib");
//	    				new Calib((Sample)data.get(type).get(row));
		        	} else {
		        		((Run)data.get(type).get(row)).set(value, format.colValue.get(col));	
		        	}
				}
				
				if (format.colValue.get(col).equals("active")) {
					if (Setting.getBoolean("/bat/general/calc/autocalc")) {
						main.dataRecalc();
					} else {
						data.calcAll();
						updateTableDataChanged(data);
					}
				}
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
			
			  Color foreground=null, background=null;
			  if (isSelected) {
				  foreground = Setting.getColor("/bat/general/table/color/fg_select");
				  background = Setting.getColor("/bat/general/table/color/bg_select");
			  } else if (data.get(type).get(row).getClass().equals(Sample.class) && mean) {
				  if (((Sample)data.get(type).get(row)).active) {
					  foreground = Setting.getColor("/bat/general/table/color/fg_sample");
					  background = Setting.getColor("/bat/general/table/color/bg_sample");
				  } else {
					  foreground = Setting.getColor("/bat/general/table/color/fg_false");
					  background = Setting.getColor("/bat/general/table/color/bg_false");
				  }
			  } else {
				  if (!(data.get(type).get(row)).active()) {
					  foreground = Setting.getColor("/bat/general/table/color/fg_false");
					  background = Setting.getColor("/bat/general/table/color/bg_false");
				  } else if (row % 2 == 0) {
	    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_even");
	    	    	  	background = Setting.getColor("/bat/general/table/color/bg_even");
	    	      } else {
	    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_odd");
	    	    	  	background = Setting.getColor("/bat/general/table/color/bg_odd");
	    	      }
		      } 

			  int align = LEFT;
			  if (format.colFormat.get(column)!="" && (value instanceof Number) && data.get(type).get(row).active()){
			      Number numberValue = (Number) value;
			      DecimalFormat form = new DecimalFormat(format.colFormat.get(column));
				  value = form.format(numberValue.doubleValue());
				  align = RIGHT;
				  if (format.colChi.get(column)==true) {
					  int a = ((DataSet)data.get(type).get(row)).number()-1;
					  if (a<=0 || a>100) {foreground = Color.orange;}
					  else if (numberValue.doubleValue()<Setting.chiLimitL[a-1]/a || numberValue.doubleValue()>Setting.chiLimitH[a-1]/a) {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_limit");
					  }
				  } else if (!format.colLimitL.get(column).equals(format.colLimitH.get(column))) {
					  if (numberValue.doubleValue()<format.colLimitL.get(column) || numberValue.doubleValue()>format.colLimitH.get(column)) {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_limit");
					  }
				  }
			  } else if (format.colFormat.get(column)!="" && (value instanceof Date)){
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
			  if (data.get(type).get(row) instanceof Sample && mean) {
		    	  	((JComponent) renderer).setBorder(BorderFactory.createMatteBorder(1,0,2,0,Setting.getColor("/bat/general/table/color/line_sample")));
			  }
			  return renderer;
		 }
    }
    
    /**
     * @param data 
     */
	public void updateTableDataChanged(Calc data) {
		this.data = data;
    	cycEdit = Setting.getBoolean("/bat/isotope/db/cycle_edit");
    	if (Setting.isotope.equalsIgnoreCase("c14")) {
	    	try {
				calib = Setting.getElement("/bat/calib/oxcal").getAttribute("active").getBooleanValue();
			} catch (DataConversionException e) {
				log.warn("Could not read oxcal attribute!");
				calib=false;
			}
    	} else {
    		calib=false;
    	}
		setCol();
		myTableModel.fireTableDataChanged();
    }
    
    /**
     * @param data 
     */
	public void updateTableChanged(Calc data) {
		this.data = data;
    	cycEdit = Setting.getBoolean("/bat/isotope/db/cycle_edit");
    	try {
			calib = Setting.getElement("/bat/isotope/calib/oxcal").getAttribute("active").getBooleanValue();
		} catch (DataConversionException e) {
			log.warn("Could not read oxcal attribute!");
			calib=false;
		}
		setCol();
		myTableModel.fireTableChanged(null);
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
	class ButtonRenderer extends JButton implements TableCellRenderer {

		  /**
		 * button renderer
		 */
		public ButtonRenderer() {
			this.setSize(new Dimension(16,16));
			this.setBorder(BorderFactory.createEmptyBorder());
		    this.setOpaque(true);
		  }
		  
		  public Component getTableCellRendererComponent(JTable table, Object value,
		                   boolean isSelected, boolean hasFocus, int row, int column) {
			  if (isSelected) {
				  setForeground(Setting.getColor("/bat/general/table/color/fg_select"));
				  setBackground(Setting.getColor("/bat/general/table/color/bg_select"));
			  } else if (data.get(type).get(row).getClass().equals(Sample.class) && mean) {
				  if (((Sample)data.get(type).get(row)).active) {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_sample"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_sample"));
				  } else {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_false"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_false"));
				  }
			  } else {
				  if (!(data.get(type).get(row)).active()) {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_false"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_false"));
				  }  else if (row % 2 == 0) {
			    	  	setForeground(Setting.getColor("/bat/general/table/color/fg_even"));
			    	  	setBackground(Setting.getColor("/bat/general/table/color/bg_even"));
			      } else {
			    	  	setForeground(Setting.getColor("/bat/general/table/color/fg_odd"));
			    	  	setBackground(Setting.getColor("/bat/general/table/color/bg_odd"));
			      }
			  }
			  this.setText( ((JButton)value).getText() );
			  this.setPreferredSize(new Dimension(26,26));
			  if (data.get(type).get(row) instanceof Sample && mean) {
		    	  	this.setBorder(BorderFactory.createMatteBorder(1,0,2,0,Setting.getColor("/bat/general/table/color/line_sample")));
			  } else {
				  this.setBorder(BorderFactory.createEmptyBorder());
			  }
			  return this;
		  }
	}

	/**
	 * 
	 */
	class ButtonEditor extends DefaultCellEditor {
		  protected JButton button;
		  String label;
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
			  label = ((JButton)value).getText();
			  button.setText( label );
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