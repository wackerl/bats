import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

/**
 * @author lukas
 * Creats a table for showing the data
 */
public class FormatTable extends JPanel implements WindowListener{    
	final static Logger log = Logger.getLogger(FormatTable.class);

    static int T = Setting.getInt("/bat/general/table/top");
    static int gapX = Setting.getInt("/bat/general/table/gap_x");
    static int gapY = Setting.getInt("/bat/general/table/gap_y");
	Color bgH = new Color(240,240,240); // header background color
	Color bgU = new Color(200,225,255); // used background color
	
	FormatT format;
	MyTableModel myTableModel;
    @SuppressWarnings("unchecked")
	ArrayList formatList= new ArrayList();	
	String[] name = {
			"name",
			"value",
			"column width",
			"number format",
			"multipl. factor",
			"low-limit",
			"high-limit",
			"chi2(red)",
			"editable",
			"",
			"",
			"",
			""
	};

    /**
     * @param format 
     */
    @SuppressWarnings("unchecked")
	public FormatTable(FormatT format) {
        super(new GridLayout(1,1));
        this.format=format;
		
	    formatList.add(format.colName);
	    formatList.add(format.colValName);
	    formatList.add(format.colWidth);
	    formatList.add(format.colFormat);
	    formatList.add(format.colMulti);
	    formatList.add(format.colLimitL);
	    formatList.add(format.colLimitH);
	    formatList.add(format.colChi);
	    formatList.add(format.colEdit);

	    formatList.add(new ImageIcon(Setting.batDir+"/icon/list-add.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/list-remove.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/go-up.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/go-down.png"));

		myTableModel = new MyTableModel();
	    JTable table = new JTable(myTableModel);
	    
	    // Table settings
	    table.setPreferredScrollableViewportSize(new Dimension(1200, 750));	    
		table.setIntercellSpacing(new Dimension(gapX, gapY));	           
		table.setShowGrid(false);
		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		//Add the scroll pane to this panel.
		this.add(scrollPane);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// Set column width
		TableColumn column = null;
		int[] width = {
				150,
				250,
				80,
				120,
				100,
				60,
				60,
				60,
				60,
				30,
				30,
				30,
				30
			};
		for (int i = 0; i < name.length; i++) {
	    		column = table.getColumnModel().getColumn(i);
	    		column.setMinWidth( width[i] );
	    		column.setPreferredWidth( width[i] );
	    		column.setHeaderRenderer(new MyTableHeaderRenderer());
	    }
		table.setRowHeight(T);

	    TableCellRenderer renderer = new EvenOddRenderer();
	    table.setDefaultRenderer(Double.class, renderer);
//	    table.setDefaultRenderer(Boolean.class, renderer);
	    table.setDefaultRenderer(String.class, renderer);
	    table.setDefaultRenderer(Integer.class, renderer);	    
	    table.setDefaultRenderer(ImageIcon.class, new ButtonRenderer());
	    table.setDefaultEditor(ImageIcon.class, new ButtonEditor(new JCheckBox()));	    
        //Set the cell editors for ComboBox.
        setUpBoxColumn1(table, table.getColumnModel().getColumn(1));
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
    
    /**
     * @param format
     */
    @SuppressWarnings("unchecked")
	public void updateTable(FormatT format) {
    	this.format = format;
    	formatList=null;
    	formatList = new ArrayList();
	    formatList.add(format.colName);
	    formatList.add(format.colValName);
	    formatList.add(format.colWidth);
	    formatList.add(format.colFormat);
	    formatList.add(format.colMulti);
	    formatList.add(format.colLimitH);
	    formatList.add(format.colLimitL);
	    formatList.add(format.colChi);
	    formatList.add(format.colEdit);
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/list-add.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/list-remove.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/go-up.png"));
	    formatList.add(new ImageIcon(Setting.batDir+"/icon/go-down.png"));


		myTableModel.fireTableDataChanged();
    }

	/**
	 * @param table
	 * @param nameColumn
	 */
	public void setUpBoxColumn1(JTable table, TableColumn nameColumn) {
		JComboBox comboBox = new JComboBox();
		for (int i=0; i<Setting.selectColN.size(); i++) {
			comboBox.addItem(Setting.selectColN.get(i));
		}
		comboBox.setSelectedItem("/bat/isotope/calc/bg/isobar");
		nameColumn.setCellEditor(new DefaultCellEditor(comboBox));		
	}
	
    
    class MyTableModel extends AbstractTableModel { 	
			// Get number of columns
		public int getColumnCount() {
		    return name.length;
		}
		// get number of rows
		@SuppressWarnings("unchecked")
		public int getRowCount() {
		    return ((ArrayList)formatList.get(0)).size();
		}
		// get column names
		public String getColumnName(int col) {
		    return name[col];
		}
		// get values for each field of the table
		@SuppressWarnings("unchecked")
		public Object getValueAt(int row, int col) {	
			ArrayList column;
			Object value;
			if (col>8) {
				value = formatList.get(col);
			} else {
				column = (ArrayList) formatList.get(col);
				value = column.get(row);
			}
			return value;
		}
        //determine the default renderer editor for each cell ( important for check box)
 		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
        }
        // here some cells are set editable
        public boolean isCellEditable(int row, int col) {
			return true;
        }
        // Only needed when table is editable
        @SuppressWarnings("unchecked")
		public void setValueAt(Object value, int row, int col) {
        	if (col > 8) {
        		if (col==9) {
        			format.insert(row);
           		} else if (col==10) {
        			format.remove(row);
           		} else if (col==11) {
        			format.shift(row,-1);
           		} else if (col==12) {
        			format.shift(row,1);
        		}
        		updateTable(format);
        	}
        	else {
				if (col==1) {
//					log.debug(format.colValName.get(row));
//					log.debug(format.colValue.get(row));
//					log.debug(Setting.selectColN.indexOf(value));
					log.debug("Set value to: "+Setting.selectCol.get(Setting.selectColN.indexOf(value)));
					format.colValue.set(row,Setting.selectCol.get(Setting.selectColN.indexOf(value)));
				}
				else if (col==2) {
					try {
						if(Integer.valueOf(String.valueOf(value))<20) {
							value=20;
						}
						else if (Integer.valueOf(String.valueOf(value))>300) {
							value=300;
						}
						else {
							value=Integer.valueOf(String.valueOf(value));
						}
					}
					catch( NumberFormatException e ) {
						log.error("Number Exception");
						value=40;
					}
				}
				ArrayList<Object> column;				
				column = (ArrayList<Object>) formatList.get(col);
				column.set(row, value);
	            fireTableCellUpdated(row, col);
			}         
        }
    }
    
    class EvenOddRenderer implements TableCellRenderer, SwingConstants {
    	  /**
    	 * 
    	 */
    	public final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    	  public Component getTableCellRendererComponent(JTable table, Object value, 
    			  boolean isSelected, boolean hasFocus, int row, int column)  {
    		  Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, 
    				  hasFocus, row, column);
    		  Color foreground, background;
			  if (isSelected) {
	  	    	  	foreground = Color.black;
		    	  	background = Color.LIGHT_GRAY;
				  } 
				  else {
		    	      if (row % 2 == 0) {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_even");
		    	    	  	background = Setting.getColor("/bat/general/table/color/bg_even");
		    	      } else {
		    	    	  	foreground = Setting.getColor("/bat/general/table/color/fg_odd");
		    	    	  	background = Setting.getColor("/bat/general/table/color/bg_odd");
		    	      }
			      }
    		  renderer.setForeground(foreground);
    		  renderer.setBackground(background);    		  
    		  ((JLabel) renderer).setHorizontalAlignment(RIGHT);
    		  return renderer;
    	  }
    	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub	
	}

	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub	
	    format.action("update");
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
	    log.debug("Format window closed");
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub	
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub		
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
//	    log.debug("windowActivated");
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub		
//	    log.debug("windowDeactivated");
	}


	/**
	 * @version 1.0 11/09/98
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
			  setForeground(Color.black);
			  setBackground(Color.LIGHT_GRAY);
		  } else {
		      if (row % 2 == 0) {
		    	  	setForeground(Setting.getColor("/bat/general/table/color/fg_even"));
		    	  	setBackground(Setting.getColor("/bat/general/table/color/bg_even"));
		      } else {
		    	  	setForeground(Setting.getColor("/bat/general/table/color/fg_odd"));
		    	  	setBackground(Setting.getColor("/bat/general/table/color/bg_odd"));
		      }
		  }  
		  setIcon( (ImageIcon)value );
		  setPreferredSize(new Dimension(26,26));
		  return this;
	  }
	}


	/**
	 *
	 */
	class ButtonEditor extends DefaultCellEditor {
	  protected JButton button;
	  ImageIcon label;
	  private boolean   isPushed;

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
		  if (isSelected) {
			  button.setForeground(Color.black);
			  button.setBackground(Color.LIGHT_GRAY);
		  } else {
		      if (row % 2 == 0) {
		    	  button.setForeground(Setting.getColor("/bat/general/table/color/fg_even"));
		    	  button.setBackground(Setting.getColor("/bat/general/table/color/bg_even"));
		      } else {
		    	  button.setForeground(Setting.getColor("/bat/general/table/color/fg_odd"));
		    	  button.setBackground(Setting.getColor("/bat/general/table/color/bg_odd"));
		      }
		  }  
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

