import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import org.apache.log4j.Logger;
import org.jdom.DataConversionException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;


/**
 * @author lukas
 *
 */
public class GraphIso extends JPanel implements ActionListener {
	final static Logger log = Logger.getLogger(GraphIso.class);
	
//    private JSlider slider;
    JFormattedTextField textField;
    JFormattedTextField textField2;
    private Bats main;
    private Calc data;
    private JFreeChart chart;
    private XYDataset dataSet;
    JFormattedTextField slopeField;
    JFormattedTextField interceptField;
    JFormattedTextField rField;
	NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.00E0"));
    String x;
    String y;
    String dataType;
    int x_multi;
    int y_multi;
    String type;
    private int corrIndex;
    
    private static int h1;
    private static int p;
    private static String ft;
	private static Font fTitel;
	private static Font fText;
	private static Font fAxes;


	/**
     * @param main
     * @param type 
	 * @param corrIndex 
     */
	public GraphIso(Bats main, String type, int corrIndex) {
		this.corrIndex=corrIndex;
		this.type=type;

        h1 = Setting.getInt("/bat/general/font/h1");
        p = Setting.getInt("/bat/general/font/p");
        ft = Setting.getString("/bat/general/font/type");
    	fTitel = new Font(ft, Font.PLAIN, h1);
    	fAxes = new Font(ft, Font.PLAIN, p);
    	fText = new Font(ft, Font.PLAIN, p);
		
		this.main = main;
		this.data = main.data;
	    x = Setting.getString("/bat/isotope/graph/"+type+"/x_value");
	    y = Setting.getString("/bat/isotope/graph/"+type+"/y_value");
	    dataType = Setting.getString("/bat/isotope/graph/"+type+"/data");
	    try {
			x_multi = Setting.getElement("/bat/isotope/graph/"+type+"/x_value").getAttribute("multi").getIntValue();
			y_multi = Setting.getElement("/bat/isotope/graph/"+type+"/y_value").getAttribute("multi").getIntValue();
		} catch (DataConversionException e) {
			x_multi=1;
			y_multi=1;
		}
		
		JLabel textLabel = new JLabel("Isobar correction on radioisotope:", JLabel.LEFT);  		
		textLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		JLabel textLabel2 = new JLabel(Setting.getString("/bat/isotope/graph/"+type+"/unit"), JLabel.LEFT);  		
		textLabel2.setAlignmentX(Component.RIGHT_ALIGNMENT);

		nf.setOverwriteMode(false);
	    nf.setMinimum(0.000);
        textField = new JFormattedTextField(nf);
        try {
        	textField.setValue(data.corrList.get(corrIndex).isoFact);
        } catch (IndexOutOfBoundsException e) {
        	log.debug("Could not set isobar factor.");
        }
        textField.setColumns(5); //get some space

        textField.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField.selectAll();
                }else try{                    //The text is valid,
                    textField.commitEdit();     //so use it.
                    fieldChange();
                } catch (java.text.ParseException exc) { }
            }
        });
        
        textField2 = new JFormattedTextField(nf);
        textField2.setValue(data.corrList.get(corrIndex).isoErr);
        textField2.setColumns(5); //get some space

        textField2.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField2.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField2.selectAll();
                }else try{                    //The text is valid,
                    textField2.commitEdit();     //so use it.
                    fieldChange2();
                } catch (java.text.ParseException exc) { }
            }
        });
                
        JPanel labelAndText = new JPanel();
        labelAndText.add(textLabel);
        labelAndText.add(textField);
        labelAndText.add(new JLabel("±"));
        labelAndText.add(textField2);
        labelAndText.add(textLabel2);

        
        JPanel regPane = new JPanel();
        regPane.add(new JLabel("slope: "));
	    NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.0E0"));
        slopeField = new JFormattedTextField(nf);
        slopeField.setPreferredSize(new Dimension(68,20));
        slopeField.setEditable(false);
        regPane.add(slopeField);
        regPane.add(new JLabel("intercept: "));
        interceptField = new JFormattedTextField(nf);
        interceptField.setPreferredSize(new Dimension(68,20));
        interceptField.setEditable(false);
        regPane.add(interceptField);
        regPane.add(new JLabel("R^2: "));
	    nf = new NumberFormatter(new DecimalFormat("0.0E0"));
        rField = new JFormattedTextField(nf);
        rField.setPreferredSize(new Dimension(68,20));
        rField.setEditable(false);
        regPane.add(rField);
        this.makeReg();
               
        JPanel regTextPanel = new JPanel(new GridLayout(1, 2));
        regTextPanel.add(labelAndText);
        regTextPanel.add(regPane);
        
        JPanel controlPanel = new JPanel(new GridLayout(1, 1));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 5, 10));
        controlPanel.add(regTextPanel);

		this.setPreferredSize(new Dimension(Setting.getInt("/bat/general/frame/correction/width"),Setting.getInt("/bat/general/frame/correction/height")));
        this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.SOUTH);
	    dataSet = Func.getXY(data.get(dataType),null,x,y,x_multi,y_multi);
        chart = createChart(dataSet);
        this.add(new ChartPanel(chart));
    }

    private JFreeChart createChart(XYDataset dataset) {
    		JFreeChart chart = ChartFactory.createXYLineChart(
    		Setting.getString("/bat/isotope/graph/"+type+"/title"),      // chart title
    		Setting.getString("/bat/isotope/graph/"+type+"/x_axes"),     // x axis label
    		Setting.getString("/bat/isotope/graph/"+type+"/y_axes"),     // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, 
            false,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
        chart.setBackgroundPaint(Setting.getColor("/bat/isotope/graph/background"));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Setting.getColor("/bat/isotope/graph/background"));
        plot.setRangeGridlinePaint(Setting.getColor("/bat/isotope/graph/background"));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseSeriesVisible(true);
        renderer.setBaseShapesFilled(true);
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setSeriesItemLabelFont(1,fText);
        renderer.setBaseItemLabelFont(fText);
        chart.getTitle().setFont(fTitel);
        chart.setTitle(Setting.getString("/bat/isotope/graph/"+type+"/title"));
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setLabelFont(fAxes);
        plot.getDomainAxis().setLabelFont(fAxes);
        return chart;
    }

	/**
	 * 
	 */
	public void fieldChange(){
		Double value = (Double)textField.getValue();
		log.debug("Changed isoFact to: "+value);
		if (value.isInfinite() || value.isNaN()) {
			value = 0.0;
		}
//		Setting.getElement("/bat/isotope/calc/"+type+"/factor").setText(value.toString());
		data.corrList.get(corrIndex).isoFact=value;
		data.calcAll();
		makeReg();
	    dataSet = Func.getXY(data.get(dataType),null,x,y,x_multi,y_multi);
		chart.getXYPlot().setDataset(0,dataSet);
		log.debug("Isobar correction factor set to: "+value+"-"+Setting.getDouble("/bat/isotope/calc/"+type+"/factor"));
	}

	/**
	 * 
	 */
	public void fieldChange2(){
		Double value = (Double)textField2.getValue();
		log.debug("Changed isoFact to: "+value);
		if (value.isInfinite() || value.isNaN()) {
			value = 0.0;
		}
//		Setting.getElement("/bat/isotope/calc/"+type+"/error").setText(value.toString());
		data.corrList.get(corrIndex).isoErr=value;
		data.calcAll();
//		makeReg();
//	    dataSet = Func.getXY(data.get(dataType),null,x,y,x_multi,y_multi);
//		chart.getXYPlot().setDataset(0,dataSet);
		log.debug("Isobar correction factor error set to: "+value+"-"+Setting.getDouble("/bat/isotope/calc/"+type+"/error"));
	}

	private void makeReg() {
        double[] regres = Func.regression(data.get(dataType),x,y);
        slopeField.setValue(regres[0]);
        interceptField.setValue(regres[1]);
        rField.setValue(regres[2]);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("check")) {
//			if (((JCheckBox)e.getSource()).isSelected()==true) {
//			    plotIndex=null;				
//			} else {
//				plotIndex=corrIndex;
//			}
		    dataSet = Func.getXY(data.get(dataType),corrIndex,x,y,x_multi,y_multi);
			chart.getXYPlot().setDataset(0,dataSet);
			log.debug("Set plot all data: "+((JCheckBox)e.getSource()).isSelected());			
		} 
	}

}