import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
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
public class GraphTime extends JPanel implements ActionListener,ChangeListener {
	final static Logger log = Logger.getLogger(GraphTime.class);
	
    private JSlider slider;
    JFormattedTextField textField;
    JFormattedTextField slopeField;
    JFormattedTextField interceptField;
    JFormattedTextField rField;
    private Calc data;
    private Corr corr;
    private int corrIndex;
    private Integer plotIndex;
    private JCheckBox box;
    private JFreeChart chart;
    private XYDataset dataSet;
    private String type;
    String x;
    String y;
    String dataType;
    int x_multi;
    int y_multi;
    Double s_range;

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
	public GraphTime(Bats main, String type, int corrIndex) {
		this.type=type;

        h1 = Setting.getInt("/bat/general/font/h1");
        p = Setting.getInt("/bat/general/font/p");
        ft = Setting.getString("/bat/general/font/type");
    	fTitel = new Font(ft, Font.PLAIN, h1);
    	fAxes = new Font(ft, Font.PLAIN, p);
    	fText = new Font(ft, Font.PLAIN, p);

    	x = Setting.getString("/bat/isotope/graph/"+type+"/x_value");
	    y = Setting.getString("/bat/isotope/graph/"+type+"/y_value");
	    dataType = Setting.getString("/bat/isotope/graph/"+type+"/data");
		s_range = Setting.getDouble("/bat/isotope/graph/"+type+"/s_range");
	    try {
			x_multi = Setting.getElement("/bat/isotope/graph/"+type+"/x_value").getAttribute("multi").getIntValue();
			y_multi = Setting.getElement("/bat/isotope/graph/"+type+"/y_value").getAttribute("multi").getIntValue();
		} catch (DataConversionException e) {
			x_multi=1;
			y_multi=1;
		}
		
		log.debug("GraphCurrent start");
		this.data = main.data;
		
		this.corr = data.corrList.get(corrIndex);
		this.corrIndex = corrIndex;
		this.plotIndex = corrIndex;
		
		JLabel textLabel = new JLabel("Correction:", JLabel.LEFT);  		
		textLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		JLabel textLabel2 = new JLabel("%/h", JLabel.LEFT);  		
		textLabel2.setAlignmentX(Component.RIGHT_ALIGNMENT);

        NumberFormat numberFormat = NumberFormat.getInstance();
        textField = new JFormattedTextField(0);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
		formatter.setOverwriteMode(false);
        formatter.setMinimum(new Double(-s_range));
        formatter.setMaximum(new Double(s_range));
        textField = new JFormattedTextField(formatter);
        textField.setValue(corr.timeCorr*100);
        textField.setColumns(5); //get some space

        textField.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField.selectAll();
                } else try {                    //The text is valid,
                    textField.commitEdit();     //so use it.
                    fieldChange();
                } catch (java.text.ParseException exc) { }
            }
        });
        
        JPanel labelAndText = new JPanel();
        labelAndText.add(textLabel);
        labelAndText.add(textField);
        labelAndText.add(textLabel2);
        
        slider = new JSlider(SwingConstants.HORIZONTAL, (int)(-100*s_range), (int)(100*s_range), (int)(corr.timeCorr*10000));
        slider.setMajorTickSpacing(200);
        slider.setMinorTickSpacing(20);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setToolTipText("Change slope of time correction");
        slider.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        slider.setPaintTrack(true);
        slider.addChangeListener(this);
        
        JPanel regPane = new JPanel();
        regPane.add(new JLabel("slope: "));
        NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.00E00"));
		nf.setOverwriteMode(false);
        slopeField = new JFormattedTextField(nf);
        slopeField.setPreferredSize(new Dimension(80,20));
        slopeField.setEditable(false);
        regPane.add(slopeField);
        regPane.add(new JLabel("intercept: "));
        interceptField = new JFormattedTextField(nf);
        interceptField.setPreferredSize(new Dimension(80,20));
        interceptField.setEditable(false);
        regPane.add(interceptField);
        regPane.add(new JLabel("R^2: "));
	    nf = new NumberFormatter(new DecimalFormat("0.0000"));
		nf.setOverwriteMode(false);
        rField = new JFormattedTextField(nf);
        rField.setPreferredSize(new Dimension(60,20));
        rField.setEditable(false);
        regPane.add(rField);
        this.makeReg();
               
        JPanel checkPane = new JPanel();
        box = new JCheckBox("all data", false);
        box.setActionCommand("check");
        box.addActionListener(this);
        checkPane.add(box);
               
        JPanel regTextPanel = new JPanel(new GridLayout(1, 2));
        regTextPanel.add(labelAndText);
        regTextPanel.add(regPane);
        regTextPanel.add(checkPane);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 5, 10));
        controlPanel.add(regTextPanel);
        controlPanel.add(slider);

        this.setPreferredSize(new Dimension(Setting.getInt("/bat/general/frame/correction/width"),Setting.getInt("/bat/general/frame/correction/height")));
        this.setLayout(new BorderLayout());
        this.add(controlPanel, BorderLayout.SOUTH);
	    dataSet = Func.getXY(data.get(dataType),plotIndex,x,y,x_multi,y_multi);
	    dataSet.addChangeListener(null);
        chart = createChart(dataSet);
        this.add(new ChartPanel(chart));

	}

    private JFreeChart createChart(XYDataset dataset) {
    		JFreeChart chart = ChartFactory.createXYLineChart(
    		Setting.getString("/bat/isotope/graph/"+type+"/title"),      // chart title
    		Setting.getString("/bat/isotope/graph/"+type+"/x_axes"),     // x axis label
    		Setting.getString("/bat/isotope/graph/"+type+"/y_axes"),     // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                    // include legend
            true,                     // tooltips
            false                     // urls
        );

        XYPlot plot = (XYPlot) chart.getPlot();
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

		log.debug("GraphCurrent created");

        return chart;
    }

	public void stateChanged(ChangeEvent event) {
		corr.timeCorr = Double.valueOf(slider.getValue())/10000;
		data.calcAll();
		makeReg();
	    dataSet = Func.getXY(data.get(dataType),plotIndex,x,y,x_multi,y_multi);
		chart.getXYPlot().setDataset(0,dataSet);
		textField.setValue(corr.timeCorr*100);
		log.debug("time correction (slider): "+corr.timeCorr);
    }

	/**
	 * 
	 */
	public void fieldChange(){
		corr.timeCorr = (Double) textField.getValue()/100;
		data.calcAll();
		makeReg();
	    dataSet = Func.getXY(data.get(dataType),plotIndex,x,y,x_multi,y_multi);
		chart.getXYPlot().setDataset(0,dataSet);
		slider.setValue((int)(corr.timeCorr*10000));
		log.debug("time correction (text field): "+corr.timeCorr);
	}
	
	private void makeReg() {
        double[] regres = Func.regression(data.get(dataType),x,y);
        slopeField.setValue(regres[0]);
        interceptField.setValue(regres[1]);
        rField.setValue(regres[2]);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("check")) {
			if (((JCheckBox)e.getSource()).isSelected()==true) {
				plotIndex = null;
			} else {
			    plotIndex=corrIndex;
			}
		    dataSet = Func.getXY(data.get(dataType),plotIndex,x,y,x_multi,y_multi);
			chart.getXYPlot().setDataset(0,dataSet);				
			log.debug("Set plot all data: "+((JCheckBox)e.getSource()).isSelected());			
		} else if (e.getActionCommand().equals("offset")) {
			corr.a_slope_off = (Double) ((JFormattedTextField)e.getSource()).getValue();
			log.debug("regression offset changed to "+corr.a_slope_off);
		}
	}

}