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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
public class StdBlTable extends JPanel 
{
	final static Logger log = Logger.getLogger(StdBlTable.class);
	StdBlGraph plot;
	Calc data;
	Bats main;
	ArrayList<Integer> rowFormat;
	static int CH = Setting.getInt("/bat/general/table/c_hight");
	static int T = Setting.getInt("/bat/general/table/top");
	static int B = Setting.getInt("/bat/general/table/bottom");
	static int gapX = Setting.getInt("/bat/general/table/gap_x");
	static int gapY = Setting.getInt("/bat/general/table/gap_y");
    static int fs = Setting.getInt("/bat/general/font/p");
	static String ft = Setting.getString("/bat/general/font/type");
	TableColumn column = null;
	JTable table;
	JPanel tablePane;
	String type, type_o;
	Boolean cycEdit;

	MyTableModel myTableModel;
	
	/**
	 * 
	 */
	public FormatT format;
	
    /**
     * @param main 
     * @param data 
     * @param type_o 
     * @param format 
     */
	public StdBlTable(Bats main, Calc data, String type_o, FormatT format) {
		super(new GridLayout(1,0));
		this.main = main;
		this.data = data;
		this.type_o = type_o;
    	cycEdit = Setting.getBoolean("/bat/isotope/db/cycle_edit");
		if (Setting.getBoolean("/bat/general/table/run_order")==true) {
			this.type = type_o+"R";
		} else {
			this.type = type_o;			
		}
		this.format = format;
		
		myTableModel = new MyTableModel();
	    table = new JTable(myTableModel);
	    
		table.setIntercellSpacing(new Dimension(gapX, gapY));
		table.setRowHeight(CH);
		table.setFont(new Font(ft,0 , fs));
		table.setShowGrid(false);
		
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// Set column width
		int width = this.setCol();
		// Set own renderer for the right format of number and cell
	    TableCellRenderer renderer = new MyTableCellRenderer(format);
	    table.setDefaultRenderer(Double.class, renderer);
	    table.setDefaultRenderer(String.class, renderer);
	    table.setDefaultRenderer(Integer.class, renderer);	    
    	table.setDefaultRenderer(JButton.class, renderer);
	    table.setDefaultRenderer(JButton.class, new ButtonRenderer());	
	    table.setDefaultEditor(JButton.class, new ButtonEditor(new JCheckBox()));	
	    table.getTableHeader().setPreferredSize(new Dimension(0,T));
    	table.setBorder(BorderFactory.createEmptyBorder());
	    
		plot = new StdBlGraph(data, format.name);
		
		tablePane = new JPanel(new BorderLayout());
		tablePane.setBorder( BorderFactory.createEmptyBorder() );	
		tablePane.add(table,BorderLayout.CENTER);
		tablePane.add(table.getTableHeader(),BorderLayout.PAGE_START);
		tablePane.setPreferredSize(new Dimension(width, data.get(type).size()*CH+B+T));
				
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    pane.setBackground(new Color(255,255,255));
	    pane.setBorder(BorderFactory.createEmptyBorder());
	    pane.add(tablePane);
	    pane.add(plot);
		
		//Create the scroll pane and add the table pane to it.
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setBorder( BorderFactory.createEmptyBorder() );
		scrollPane.setWheelScrollingEnabled(false);
		
		//Add the scroll pane to this panel.
		this.add(scrollPane);
   }
    
    /**
     * @author lukas
     *
     */
    public class MyTableHeaderRenderer extends JLabel implements TableCellRenderer, SwingConstants 
    {
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;
		
		/**
		 * 
		 */
		public final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
			
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col)
		{
			Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, 
					  isSelected, hasFocus, row, col);			  

			renderer.setForeground(Setting.getColor("/bat/general/table/color/fg_head"));
			renderer.setBackground(Setting.getColor("/bat/general/table/color/bg_head"));
			((JLabel) renderer).setHorizontalAlignment(LEFT);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return renderer;
		}
    	;
    }
    
    private int setCol()
    {
    	int width = 0;
		for (int i = 0; i < format.colEdit.size(); i++) 
	    {
	    		column = table.getColumnModel().getColumn(i);
	    		column.setPreferredWidth( format.colWidth.get(i) );
	    		width += format.colWidth.get(i)+2;
	    		column.setHeaderRenderer(new MyTableHeaderRenderer());
	    		column.setHeaderValue(format.colName.get(i));
	    }
		return width;
    }
    
    class MyTableModel extends AbstractTableModel 
    { 	
		// Get number of columns
		public int getColumnCount() 
		{
		    return format.colValue.size();
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
		// get values for each field of the table
		public Object getValueAt(int row, int col) 
		{	
			Object cell;
			cell = ((DataSet)data.get(type).get(row)).get(format.colValue.get(col));
			try
			{
				if (cell instanceof Double) {
					cell = (Double)cell * format.colMulti.get(col);
				} else if (format.colValue.get(col).equalsIgnoreCase("run") ) {
					cell = new JButton((String)((DataSet)data.get(type).get(row)).get(format.colValue.get(col)));
				}
			} catch(NullPointerException e) {;}
			return cell;
		}

		public boolean isCellEditable(int row, int col) {
			if ( (Boolean) ((DataSet)data.get(type).get(row)).get("editable") )
			{
				if (data.get(type).get(row) instanceof Run || data.get(type).get(row) instanceof Sample)
				{
					if (((DataSet)data.get(type).get(row)).active()||format.colValue.get(col).equals("active")) {
						log.debug("Sample is set active/inactive");
						return format.colEdit.get(col);
					}
					else return false;
				}
				else return true;
        	} 
			else return false;
        }

		//determine the default renderer editor for each cell ( important for check box)
        @SuppressWarnings("unchecked")
		public Class getColumnClass(int col) {
			try {
				return getValueAt(0, col).getClass();
			} catch (NullPointerException e) {
				return String.class;
			}
        }
		
        // Only needed when table is editable
        public void setValueAt(Object value, int row, int col) {
//			if (value.getClass().equals(Double.class)) {
//				value = (Double)value / format.colMulti.get(col);
//			}
//			((DataSet)data.get(type).get(row)).set(value, format.colValue.get(col));
//			if (data.get(type).get(row).getClass().equals(Sample.class)) {
//				if (format.colValue.get(col).equals("active")) {
//					data.setTrueType(((Sample)data.get(type).get(row)).label,(Boolean)value);
//				} else {
//					((Sample)data.get(type).get(row)).set(value, format.colValue.get(col));
//				}
//			}
//			else {
//				log.debug(data.get(type).get(row).getClass().toString());
//				((DataSet)data.get(type).get(row)).set(value, format.colValue.get(col));				
//			}
//			if (format.colValue.get(col).equals("active")) {
//				if (Setting.getBoolean("/bat/general/calc/autocalc")) {
//					main.dataRecalc();
//				} else {
//					data.calcAll();
//					updateTableDataChanged(data);
//				}
//			}
//			
//			if (data.get(type).get(row).getClass().equals(StdBl.class)) {
//				if (Setting.getBoolean("/bat/general/calc/autocalc")) {
//					main.dataRecalc();
//				} else {
//					data.calcAll();
//					updateTableDataChanged(data);
//				}
//			}
        	if (format.colValue.get(col).equals("a") || format.colValue.get(col).equals("b") || format.colValue.get(col).equals("label")
        			|| format.colValue.get(col).equals("r") || format.colValue.get(col).equals("runtime")) {
        		JOptionPane.showMessageDialog( null, "<html>You are not allowed to change raw data!<br> ("+format.colValue.get(col)+")</html>");
        		log.debug("You are not allowed to change raw data! ("+format.colValue.get(col)+")");
        	} else {
	        	if (value.getClass().equals(Double.class)) {
					value = (Double)value / format.colMulti.get(col);
				}
	        	if ((format.colValue.get(col).equals("sampletype") && cycEdit)) {
	        		data.get(type).get(row).set(value, format.colValue.get(col));
	        		main.dataRecalc();
	        	} else if (data.get(type).get(row).getClass().equals(Sample.class)) {
		        	if ((format.colValue.get(col).equals("label") || format.colValue.get(col).equals("comment"))) {
		        		;
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
				} else if (data.get(type).get(row).getClass().equals(Run.class)){
			       	if ((format.colValue.get(col).equals("run") && cycEdit)) {
		        		main.cycDat.getCycleTable((Run)data.get(type).get(row));
		        	} else if (format.colValue.get(col).equals("active")) {
		        		if (main.db.runTrue((Run)data.get(type).get(row), (Boolean)value)) {
		        			((Run)data.get(type).get(row)).set(value, "active");	
		        		} else {
		        			log.warn("Could not activate/deactivate run!");
		        		}
		        	}  else {
		        		((Run)data.get(type).get(row)).set(value, format.colValue.get(col));	
		        	}
				} else {
					log.debug(data.get(type).get(row).getClass().toString());
					((DataSet)data.get(type).get(row)).set(value, format.colValue.get(col));				
	        		main.dataRecalc();
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
    
    class MyTableCellRenderer extends DefaultTableCellRenderer implements SwingConstants {
		FormatT format;
		MyTableCellRenderer(FormatT form) {
			format=form;
		}
		  /**
		 * 
		 */
	
		  public Component getTableCellRendererComponent(JTable table, Object value, 
				  boolean isSelected, boolean hasFocus, int row, int column) 
		  {
//			  Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, 
//					  isSelected, hasFocus, row, column);
			 			  
			  Color foreground, background;
			  if (isSelected) {
				  foreground = Setting.getColor("/bat/general/table/color/fg_select");
				  background = Setting.getColor("/bat/general/table/color/bg_select");
			  } else if (data.get(type).get(row).getClass().equals(Sample.class)) {
				  if (!((Sample)data.get(type).get(row)).active) {
					  foreground = Setting.getColor("/bat/general/table/color/fg_false");
					  background = Setting.getColor("/bat/general/table/color/bg_false");
				  } else {
					  foreground = Setting.getColor("/bat/general/table/color/fg_sample");
					  background = Setting.getColor("/bat/general/table/color/bg_sample");
				  }
			  } else if (data.get(type).get(row).getClass().equals(Run.class)) {
				  if (!((Run)data.get(type).get(row)).sample.active||!((Run)data.get(type).get(row)).active) {
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
			  else {
				  if (((DataSet)data.get(type).get(row)).get("label").equals("used")) {
			    	  	foreground = Setting.getColor("/bat/general/table/color/fg_std2");
			    	  	background =  Setting.getColor("/bat/general/table/color/bg_std2");
				  } else {
		    	  	foreground = Setting.getColor("/bat/general/table/color/fg_std1");
		    	  	background =  Setting.getColor("/bat/general/table/color/bg_std1");
				  }
		      }
			  
			  int align = LEFT;
			  if (value!=null && (value instanceof Number)) {
			      Number numberValue = (Number) value;
			      DecimalFormat form = new DecimalFormat(format.colFormat.get(column));
				  value = form.format(numberValue.doubleValue());
//				  table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(format(numberValue.doubleValue()));
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
			  }
			  else if (format.colFormat.get(column)!="" && (value instanceof Date)){
			      Date dateValue = (Date) value;
			      SimpleDateFormat form = new SimpleDateFormat(format.colFormat.get(column));
				  value = form.format(dateValue);
				  align = RIGHT;
			  }
			  else if (format.colFormat.get(column)!="" && (value instanceof Date)){
			      Date dateValue = (Date) value;
			      SimpleDateFormat form = new SimpleDateFormat(format.colFormat.get(column));
				  value = form.format(dateValue);
				  align = RIGHT;
			  }
			  
			  ((JLabel) this).setHorizontalAlignment(align);
			  
			  this.setForeground(foreground);
			  this.setBackground(background);
			  Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			  if (data.get(type).get(row) instanceof Sample) {
		    	  	((JComponent) renderer).setBorder(BorderFactory.createMatteBorder(1,0,2,0,Setting.getColor("/bat/general/table/color/line_sample")));
			  } else if (((DataSet)data.get(type).get(row)).get("label").equals("used")) {
		    	  	((JComponent) renderer).setBorder(BorderFactory.createMatteBorder(1,0,2,0,Setting.getColor("/bat/general/table/color/line_sample")));
			  }
			  return renderer;
		  }
    	}
    /**
     * @param data  
     */
	public void updateTableDataChanged(Calc data)
    {
		if (Setting.getBoolean("/bat/general/table/run_order")==true) {
			this.type = type_o+"R";
		} else {
			this.type = type_o;			
		}
		this.data = data;
		myTableModel.fireTableDataChanged();
		int width = 0;
		for (int i=0; i<format.colWidth.size(); i++) {
			width+=format.colWidth.get(i);
		}
	    tablePane.setPreferredSize(new Dimension(width, data.get(type).size()*CH+B+T));
	    table.revalidate();
    	plot.repaint(data);
   }
    
    /**
     * 
     */
	public void updateTableStructureChanged()
    {
		myTableModel.fireTableStructureChanged();
		int width = 0;
		for (int i=0; i<format.colWidth.size(); i++) {
			width+=format.colWidth.get(i);
		}
	    tablePane.setPreferredSize(new Dimension(width, data.get(type).size()*CH+B+T));
		setCol();
	    table.revalidate();
	    plot.repaint(data);
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
			  } else if (data.get(type).get(row).getClass().equals(Sample.class)) {
				  if (((Sample)data.get(type).get(row)).active) {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_sample"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_sample"));
				  } else {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_false"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_false"));
				  }
			  } else if (data.get(type).get(row).getClass().equals(Run.class)) {
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
			  else {
				  if (((DataSet)data.get(type).get(row)).get("label").equals("used")) {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_std2"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_std2"));
				  } else {
					  setForeground(Setting.getColor("/bat/general/table/color/fg_std1"));
					  setBackground(Setting.getColor("/bat/general/table/color/bg_std1"));
				  }
		      }

			  
			  
			  this.setText( ((JButton)value).getText() );
			  this.setPreferredSize(new Dimension(26,26));
			  if (data.get(type).get(row) instanceof Sample) {
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