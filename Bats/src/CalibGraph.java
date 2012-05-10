import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;


/**
 * @author lukas
 *
 */
public class CalibGraph extends JFrame{
	
	final static Logger log = Logger.getLogger(CalibGraph.class);	

    int left;
    int right;
    int top;
    int bottom;
    int height;
    int width;
    int addXl;
    int addYl;
    int addXh;
    int addYh;

	static int MIN = 0;
	static int MAX = 1;
	static int X = 0;
	static int Y = 1;
	static int YE = 2;
	static double[] SIG = { 0.6827, 0.9545, 0.9973 };
	
	int[][] limit;
	
    int div;
    int divSub;
    int tic;
    float lineW;
    int ticSub;
    int labelInc;
    float lineWn;
    int h1;
    int p;
    String ft;
    String prefix;
    DecimalFormat numform = new DecimalFormat("0");
    DecimalFormat probform = new DecimalFormat("0.00");
    String xLabel;
    String y1Label;

    double scaleX;
    double scaleY;
    
    double age;
    double ageSig;

    SVGGraphics2D g;
    SVGDocument doc;
    JSVGCanvas canvas;
    String curve;
    
	private Calib calib;
	private String sample;
	private String version;
    
    /**
     * @param sample 
     * @param age 
     * @param ageSig 
     * @param calib 
     * 
     */
    public CalibGraph(String sample, double age, double ageSig, Calib calib) {
		log.debug("Calibration of the following age will be started: "+age+" +- "+ageSig+" ("+sample+")");
    	this.calib = calib;
		this.sample = sample;
		this.age = age;
        this.ageSig = ageSig;       
	    this.curve = calib.curve;

	    version = "SwissCal 1.0 L. Wacker (2010)";
	    left = Setting.getInt("/bat/calib/graph/dimension/left");
        right = Setting.getInt("/bat/calib/graph/dimension/right");
        top = Setting.getInt("/bat/calib/graph/dimension/top");
        bottom = Setting.getInt("/bat/calib/graph/dimension/bottom");
        height = Setting.getInt("/bat/calib/graph/dimension/height");
        width = Setting.getInt("/bat/calib/graph/dimension/width");
        
        div = Integer.valueOf(Setting.getElement("/bat/calib/graph/line/tic").getAttributeValue("div"));
        divSub = Integer.valueOf(Setting.getElement("/bat/calib/graph/line/tic_sub").getAttributeValue("div"));
        labelInc = Setting.getInt("/bat/calib/graph/line/label_inc");
        tic = Setting.getInt("/bat/calib/graph/line/tic");
        ticSub = Setting.getInt("/bat/calib/graph/line/tic_sub");
        lineW = Setting.getDouble("/bat/calib/graph/line/width").floatValue();
        lineWn = Setting.getDouble("/bat/calib/graph/line/width_n").floatValue();
        h1 = Setting.getInt("/bat/calib/graph/font/h1/size");
        p = Setting.getInt("/bat/calib/graph/font/p/size");
        ft = Setting.getString("/bat/calib/graph/font/type");
        prefix = Setting.getString("/bat/calib/graph/legend/prefix");
        log.debug("init finished");
    }
    
    private void startPlot() {
        log.debug("Start plot");           	
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        log.debug(impl.toString());           	
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        log.debug(svgNS);
        Document d = impl.createDocument(svgNS, "svg", null);
        log.debug(d.getDoctype());
        doc = (SVGDocument) d;
        log.debug(doc.getDoctype());

        this.getContentPane().removeAll();
        log.debug("removed all");
		g = new SVGGraphics2D(doc);
		log.debug("new SVGGraphics2D");
        //        g.translate(left,top);
        AffineTransform trans = new AffineTransform(1.0,0.0,0.0,1.0,left+width,top+height);
        g.transform(trans);
        
        Shape rectangle = new Rectangle2D.Double(-width,-height,width,height);
        g.setPaint(Color.WHITE);
        g.fill(rectangle);
        g.setColor(Color.black);
        log.debug("New graph started ("+width+"x"+height+").");           	
    }
    
	/**
	 * @throws NullPointerException 
	 * 
	 */
	public void displayAuto() throws NullPointerException {
		this.startPlot();
		log.debug("Removed all from Panel");
        this.setAutoLimit();
        this.plotRadAge(Setting.getColor("/bat/calib/graph/age/color"),Setting.getFloat("/bat/calib/graph/age/trans")
        		,Setting.getFloat("/bat/calib/graph/age/line_width"));
        if (Setting.getActive("/bat/calib/graph/curve/sig1")) {
			this.plotCalib(1, Setting.getColor("/bat/calib/graph/curve/sig1/color"), 
					Setting.getFloat("/bat/calib/graph/curve/sig1/trans"));
        }
        if (Setting.getActive("/bat/calib/graph/curve/sig2")) {
			this.plotCalib(2, Setting.getColor("/bat/calib/graph/curve/sig2/color"), 
					Setting.getFloat("/bat/calib/graph/curve/sig2/trans"));
        }
		ArrayList<ProbaP> probArr = calib.getProbability(age, ageSig);
        if (Setting.getActive("/bat/calib/graph/proba/all")) {
			this.plotProba(probArr, Setting.getColor("/bat/calib/graph/proba/all/color"),
					Setting.getFloat("/bat/calib/graph/proba/all/trans"),true);
        }
		ArrayList<ProbaP> prob1sig = calib.sigmaProba(probArr,1);
		ArrayList<ProbaP> prob2sig = calib.sigmaProba(probArr,2);
        if (Setting.getActive("/bat/calib/graph/proba/sig1")) {
			this.plotProba(prob1sig, Setting.getColor("/bat/calib/graph/proba/sig1/color"),
					Setting.getFloat("/bat/calib/graph/proba/sig1/trans"),false);
        }
        if (Setting.getActive("/bat/calib/graph/proba/sig2")) {
			this.plotProba(prob2sig, Setting.getColor("/bat/calib/graph/proba/sig2/color"),
					Setting.getFloat("/bat/calib/graph/proba/sig2/trans"),false);
        }
		this.plotLegend(calib.getRanges(prob2sig));
		this.plotRanges(calib.getRanges(prob2sig));
		this.plotAxes();
		this.display();
	}
	
	/**
	 * 
	 */
	public void displayManual() {
        if (this.setManLimit()) {
    		this.startPlot();
	        this.plotRadAge(Setting.getColor("/bat/calib/graph/age/color"),Setting.getFloat("/bat/calib/graph/age/trans")
	        		,Setting.getFloat("/bat/calib/graph/age/line_width"));
	        if (Setting.getActive("/bat/calib/graph/curve/sig1")) {
				this.plotCalib(1, Setting.getColor("/bat/calib/graph/curve/sig1/color"), 
						Setting.getFloat("/bat/calib/graph/curve/sig1/trans"));
	        }
	        if (Setting.getActive("/bat/calib/graph/curve/sig2")) {
				this.plotCalib(2, Setting.getColor("/bat/calib/graph/curve/sig2/color"), 
						Setting.getFloat("/bat/calib/graph/curve/sig2/trans"));
	        }
			ArrayList<ProbaP> probArr = calib.getProbability(age, ageSig);
	        if (Setting.getActive("/bat/calib/graph/proba/all")) {
				this.plotProba(probArr, Setting.getColor("/bat/calib/graph/proba/all/color"),
						Setting.getFloat("/bat/calib/graph/proba/all/trans"),true);
	        }
			ArrayList<ProbaP> prob1sig = calib.sigmaProba(probArr,1);
			ArrayList<ProbaP> prob2sig = calib.sigmaProba(probArr,2);
	        if (Setting.getActive("/bat/calib/graph/proba/sig1")) {
				this.plotProba(prob1sig, Setting.getColor("/bat/calib/graph/proba/sig1/color"),
						Setting.getFloat("/bat/calib/graph/proba/sig1/trans"),false);
	        }
	        if (Setting.getActive("/bat/calib/graph/proba/sig2")) {
				this.plotProba(prob2sig, Setting.getColor("/bat/calib/graph/proba/sig2/color"),
						Setting.getFloat("/bat/calib/graph/proba/sig2/trans"),false);
	        }
			this.plotLegend(calib.getRanges(prob2sig));
			this.plotRanges(calib.getRanges(prob2sig));
			this.plotAxes();
			this.display();
        }
	}
	
	
	/**
	 * @param file
	 */
	public void saveCalibExcel(String file) {
		try{
		    PrintWriter pw;
			FileOutputStream fs = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			pw = new PrintWriter(writer);
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"+
					"xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n"+
					"xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"+
					"xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n"+
					"<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n"+
					"<Author>"+Setting.user+"</Author>\n"+
					"<Created>"+new Date().toString()+"</Created>\n"+
					"<Company>ETH</Company>\n"+
					"<Version>"+Setting.version+"</Version>\n"+
					"</DocumentProperties>\n"+
					"<Styles>\n"+
					"  <Style ss:ID=\"0\">\n"+
					"    <Font ss:FontName=\"Verdana\" ss:Bold=\"1\"/>\n"+
					"    <Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/>\n"+
					"  </Style>\n");
			pw.println("  </Styles>\n");
			pw.println("<Worksheet ss:Name=\""+this.sample+"\">\n"+
					"  <Table>");
	
			ArrayList<ProbaP> probArr = calib.getProbability(age, ageSig);
	
			pw.println("   <Row>");
			pw.println("      <Cell><Data ss:Type=\"String\">"+this.sample+"</Data></Cell>");
			pw.println("   </Row>");
			
			pw.println("   <Row ss:StyleID=\"0\">");
			pw.println("      <Cell><Data ss:Type=\"String\"> </Data></Cell>");
			pw.println("   </Row>");
			
			pw.println("   <Row>");
			pw.println("      <Cell><Data ss:Type=\"String\">Calendar age</Data></Cell>");
			pw.println("      <Cell><Data ss:Type=\"String\">Radiocarbon age</Data></Cell>");
			pw.println("      <Cell><Data ss:Type=\"String\">unc</Data></Cell>");
			pw.println("      <Cell><Data ss:Type=\"String\">Probability</Data></Cell>");
			pw.println("      <Cell><Data ss:Type=\"String\">Probability normalised</Data></Cell>");
			pw.println("   </Row>");
			
			for (int i=0; i<probArr.size();i++) {
				pw.println("   <Row>");
				pw.println("      <Cell><Data ss:Type=\"Number\">"+probArr.get(i).calAge+"</Data></Cell>");
				pw.println("      <Cell><Data ss:Type=\"Number\">"+probArr.get(i).radAge+"</Data></Cell>");
				pw.println("      <Cell><Data ss:Type=\"Number\">"+probArr.get(i).radAgeSig+"</Data></Cell>");
				pw.println("      <Cell><Data ss:Type=\"Number\">"+probArr.get(i).proba+"</Data></Cell>");
				pw.println("      <Cell><Data ss:Type=\"Number\">"+probArr.get(i).probaN+"</Data></Cell>");
				pw.println("   </Row>");
			}	
			pw.println("  </Table>\n </Worksheet>\n</Workbook>");
		    pw.close();
		    fs.close();
		    log.info("Saved data to file: "+file);
		} catch (IOException e) {
			log.error("Couldn't save file ("+file+")");
		}
	}
	

	/**
	 * 
	 */
	public void display() {
        // Populate the document root with the generated SVG content.
        Element root = doc.getDocumentElement();
        g.getRoot(root);
        // display
	    canvas = new JSVGCanvas();
        canvas.setBackground(Color.white);
        canvas.setPreferredSize(new Dimension(width+left+right, height+top+bottom));
        canvas.setSVGDocument(doc);
        this.setJMenuBar(new CalibMenu(this));
		this.getContentPane().add(new CalibTBar(this), BorderLayout.NORTH); 		
		this.getContentPane().add(canvas,BorderLayout.SOUTH);
	    this.getContentPane().add(canvas);
        this.pack();
        this.setVisible(true);		
		log.debug("Displayed plot.");
	}

	/**
	 * @param color 
	 * @param trans 
	 * @param lw 
	 *
	 */
	public void plotRadAge(Color color, float trans, float lw) {
		g.setColor(color);
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trans/2);
		g.setComposite(composite);
		g.setStroke (new BasicStroke( lw/2 ));
		g.setColor(color);
		Polygon2D poly = new Polygon2D();                
		for (int i=limit[Y][MIN]; i<limit[Y][MAX]; i++) {
			poly.addPoint((float)(Math.exp(-0.5*Math.pow((i-age)/ageSig,2))*width/8-width), (float) (-(i-limit[Y][MIN])*scaleY));
		}
		g.fill(poly);
		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trans);
		g.setComposite(composite);
		g.draw(poly);
		g.setStroke (new BasicStroke( lw));
		Line2D line;		
		line = new Line2D.Double(0,-(age-2*ageSig-limit[Y][MIN])*scaleY,-width,-(age-2*ageSig-limit[Y][MIN])*scaleY);			
		g.setStroke (new BasicStroke( lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4f, new float[] {6f}, 0f));
		g.draw(line);
		line = new Line2D.Double(0,-(age+2*ageSig-limit[Y][MIN])*scaleY,-width,-(age+2*ageSig-limit[Y][MIN])*scaleY);			
		g.draw(line);
		line = new Line2D.Double(0,-(age-ageSig-limit[Y][MIN])*scaleY,-width,-(age-ageSig-limit[Y][MIN])*scaleY);			
		g.setStroke (new BasicStroke( lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4f, new float[] {4f}, 0f));
		g.draw(line);
		line = new Line2D.Double(0,-(age+ageSig-limit[Y][MIN])*scaleY,-width,-(age+ageSig-limit[Y][MIN])*scaleY);			
		g.draw(line);
		g.setColor(color);
        g.setStroke(new BasicStroke(lw));
		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trans);
		g.setComposite(composite);
		line = new Line2D.Double(0,-(age-limit[Y][MIN])*scaleY,-width,-(age-limit[Y][MIN])*scaleY);			
		g.draw(line);
			    
		log.debug("Ploted radiocarbon age.");
	}
	
	/**
	 * @param probArr
	 * @param color 
	 * @param transp 
	 * @param border 
	 */
	public void plotProba(ArrayList<ProbaP> probArr, Color color, Float transp, boolean border) {
		int offProY = 40;
		Polygon2D polygon = new Polygon2D();
		polygon.addPoint(-width,-offProY);
		polygon.addPoint(0,-offProY);
		int k=0;
		for (int i=limit[X][MIN]; i<=limit[X][MAX]; i++) {
			if (k<probArr.size() && i==probArr.get(k).calAge) {
				polygon.addPoint(-(float)(scaleX*(i-limit[X][MIN])) ,-(float)(probArr.get(k).probaN*(height/4)+offProY));
				k++;
			} else {
				polygon.addPoint(-(float)(scaleX*(i-limit[X][MIN])) ,-(float)offProY);
			}
		}
		g.setPaint(color);
		AlphaComposite composite = 
			 AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transp);
		g.setComposite(composite);
		g.fill(polygon);
		if (border) {
			g.setStroke(new BasicStroke(lineW/2));
			g.draw(polygon);
		}
	}
	
	/**
	 * @param ranges
	 */
	public void plotLegend(ArrayList<Range> ranges) {
        int legBord = Setting.getInt("/bat/calib/graph/legend/border");
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g.setComposite(composite);
	    FontMetrics fm;
    	Font font = new Font(ft, Font.PLAIN, p);
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
    	float x=0;
    	String label;
        ArrayList<String> labelList = new ArrayList<String>();
        
	    DecimalFormat format = new DecimalFormat("0");
		label = "("+format.format(age)+" ± "+format.format(ageSig)+")";
        labelList.add(label);
		label = "2-σ ranges:";
        labelList.add(label);
        x = Math.max(fm.stringWidth(label),x)+legBord;
        
		for (int i=0; i<ranges.size(); i++) {
			label = rangeFormat(ranges.get(i).ll,ranges.get(i).hl,ranges.get(i).proba);
	        labelList.add(label);
	        x = Math.max(fm.stringWidth(label),x)+legBord;
		}
		
	   	font = new Font(ft, Font.PLAIN, h1);
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
        x = Math.max(fm.stringWidth(prefix+sample),x)+legBord;
        float y = fm.getAscent()+legBord;			
		g.setColor(Setting.getColor("/bat/calib/graph/font/h1/color"));
    	g.drawString(prefix+sample, -x, y-height);
    	
    	font = new Font(ft, Font.PLAIN, p);
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
		g.setColor(Setting.getColor("/bat/calib/graph/font/p/color"));
		for (int i=0;i<labelList.size(); i++) {
		   	y+=fm.getHeight()+2;
	    	g.drawString(labelList.get(i), -x, y-height);
		}		
		log.debug("Ploted legend.");
	}
	
	/**
	 * @param age1
	 * @param age2
	 * @param probability
	 * @return formated string
	 */
	public String rangeFormat(int age1, int age2, double probability) {
	    DecimalFormat format = new DecimalFormat("0");
	    String str;
	    if (calib.bp) {
	    	str = format.format(age1)+" - "+format.format(age2)+" BP (";
	    } else if (age1>0 && age2>0) {
	    	str = format.format(age1)+" - "+format.format(age2)+" BC (";
	    } else if (age1<0 && age2<0) {
	    	str = format.format(-age1)+" - "+format.format(-age2)+" AD (";
	    } else {
	    	str = format.format(age1)+" BC - "+format.format(-age2)+" AD (";	    	
	    }
	    str = str+probform.format(probability*100)+" %)";
	    return str;
	}
	
	/**
	 * @param ranges 
	 */
	public void plotRanges(ArrayList<Range> ranges) {
		AlphaComposite composite = 
			  AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g.setComposite(composite);
		g.setColor(Setting.getColor("/bat/calib/graph/proba/ranges/color"));
        g.setStroke(new BasicStroke(lineW));
		int offRanY = 30;
	    FontMetrics fm;
    	Font font = new Font(ft, Font.PLAIN, p);
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
    	String label;
		Line2D line;
		for (int i=0; i<ranges.size(); i++) {
			float ll =  (float) ((float)(ranges.get(i).ll-limit[X][MIN])*scaleX);
			float hl = (float) ((float)(ranges.get(i).hl-limit[X][MIN])*scaleX);
	        line = new Line2D.Double(-ll,-offRanY,-hl,-offRanY);			
	        g.draw(line);
	        line = new Line2D.Double(-ll,-offRanY,-ll,-offRanY+tic);			
	        g.draw(line);
	        line = new Line2D.Double(-hl,-offRanY,-hl,-offRanY+tic);			
	        g.draw(line);
	        label = probform.format(ranges.get(i).proba*100);
	    	g.drawString(label, -(ll+hl)/2-fm.stringWidth(label)/2, -offRanY+Math.round(fm.getHeight()+tic));
		}
		log.debug("Ploted ranges.");
	}
	
	/**
	 * @param sig 
	 * @param color 
	 * @param transp 
	 * 
	 */
	public void plotCalib(int sig, Color color, float transp) {       
		ArrayList<CaliP> calArr = calib.calArr;
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		Double temp1, temp2;
		Polygon2D polygon = new Polygon2D();
		AlphaComposite composite = 
			  AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transp);
		g.setComposite(composite);
	    g.setPaint(color);
	
		for (int i=0; i<calArr.size(); i++) {
			if ( calArr.get(i).calAge>=(limit[X][MIN]) && calArr.get(i).calAge<=(limit[X][MAX]) ) {
				temp1 = (calArr.get(i).calAge- limit[X][MIN]) * scaleX;
				x.add(temp1);
	
				temp2 = calArr.get(i).radAge+sig*calArr.get(i).radAgeSig;
				temp2 = Math.max(temp2,limit[Y][MIN]);
				temp2 = Math.min(temp2,limit[Y][MAX]);
				y.add((temp2-limit[Y][MIN]) * scaleY);
				
				temp2 = calArr.get(i).radAge-sig*calArr.get(i).radAgeSig;
				temp2 = Math.max(temp2, limit[Y][MIN]);
				temp2 = Math.min(temp2, limit[Y][MAX]);
				temp2 = (temp2-limit[Y][MIN]) * scaleY;
				polygon.addPoint(-temp1.floatValue(),-temp2.floatValue());
			}
		}
		for (int i=x.size(); i>0; i--) {
			polygon.addPoint(-x.get(i-1).floatValue(),-y.get(i-1).floatValue());
		}
		g.fill(polygon);
		g.setStroke (new BasicStroke(lineW/2));
		g.draw(polygon);
		log.debug("Ploted calibration ("+sig+").");
	}
	
	/**
	 * 
	 */
	public void plotAxes() {
		
	    y1Label = Setting.getString("/bat/calib/graph/axes/y1_title");		
        if (!calib.bp) {
        	if (limit[X][MIN]<0) {
        	    xLabel = Setting.getString("/bat/calib/graph/axes/x_title_ad");
        	} else if (limit[X][MAX]<0){
        	    xLabel = Setting.getString("/bat/calib/graph/axes/x_title_ad_bc");
        	} else {
        	    xLabel = Setting.getString("/bat/calib/graph/axes/x_title_bc");
        	}
        } else {
    	    xLabel = Setting.getString("/bat/calib/graph/axes/x_title_bp");
        }
		
		AlphaComposite composite =  AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g.setComposite(composite);
		g.setColor(Color.black);
        g.setStroke(new BasicStroke(lineW));
        Line2D line = new Line2D.Double(0,0,-width,0);
        g.draw(line);
        line.setLine(0,-height,-width,-height);
        g.draw(line);
        line.setLine(0,0,0,-height);
        g.draw(line);
        line.setLine(-width,0,-width,-height);
        g.draw(line);
        g.setSVGCanvasSize(new Dimension(width+left+right, height+top+bottom));
        
        float fLabX = (float) (Math.ceil(((float)limit[X][MIN])/labelInc)*labelInc);
        float incX = (float) (Math.ceil(((float)limit[X][MAX]-limit[X][MIN])/labelInc/div)*labelInc);
        
        float fLabY1 = (float) (Math.ceil((float)(limit[Y][MIN])/labelInc)*labelInc);
        float incY1 = (float) (Math.ceil((float)(limit[Y][MAX]-limit[Y][MIN])/labelInc/div)*labelInc);
        String label="";
        
    	Font font = new Font(ft, Font.PLAIN, Math.max(Setting.getInt("/bat/calib/graph/axes/info/size"),8));
    	g.setFont(font);
    	FontMetrics fm = g.getFontMetrics(font);
        label = version+"  /  "+curve;
    	g.drawString(label, -width, -height-fm.getDescent());
        
    	// plot x labels
    	font = new Font(ft, Font.PLAIN, Setting.getInt("/bat/calib/graph/axes/label/size"));
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
    	float coord;
        for (float d=fLabX; d<=limit[X][MAX]; d+=incX) {
         	coord= (float)((d-limit[X][MIN])*scaleX);
            g.setStroke(new BasicStroke(lineW));
        	line.setLine(-coord,0,-coord,tic);
        	g.draw(line);
        	if (limit[X][MIN]<0) {
            	label = numform.format(-d);
        	} else {
            	label = numform.format(d);
        	}
        	g.drawString(label, -coord-fm.stringWidth(label)/2, tic+fm.getHeight());
        }
        float ticP;
        for (float d=fLabX-incX; d<=limit[X][MAX]; d+=incX) {
         	coord= (float)((d-limit[X][MIN])*scaleX);
            g.setStroke(new BasicStroke(lineWn));
         	for (int i=1; i<divSub; i++) {
         		ticP = (float) (coord + incX*scaleX/divSub*i);
         		if (ticP<width && ticP>0) {
                	line.setLine(-ticP,0,-ticP,ticSub);
                	g.draw(line);         		
         		}
         	}
        }
        // draw x title
    	font = new Font(ft, Font.PLAIN, Setting.getInt("/bat/calib/graph/axes/title/size"));
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
       	g.drawString(xLabel, -width/2-fm.stringWidth(xLabel)/2, tic+Math.round(fm.getAscent()*2.5));
        
        // draw y labels
    	font = new Font(ft, Font.PLAIN, Setting.getInt("/bat/calib/graph/axes/label/size"));
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
        for (double d=fLabY1; d<=limit[Y][MAX]; d+=incY1) {
        	coord= (int)Math.round((d-limit[Y][MIN])*scaleY);
            g.setStroke(new BasicStroke(lineW));
           	line.setLine(-width, -coord ,-width-tic,-coord);
        	g.draw(line);
        	label = numform.format(d);
        	g.drawString(label, -width-fm.stringWidth(label)-tic-2, -(coord-fm.getAscent()/2));
        }
        // draw y title
    	font = new Font(ft, Font.PLAIN, Setting.getInt("/bat/calib/graph/axes/title/size"));
    	g.setFont(font);
    	fm = g.getFontMetrics(font);
        g.rotate(Math.toRadians(-90));
    	g.drawString(y1Label, height/2-fm.stringWidth(y1Label)/2, -(width+fm.stringWidth(label)+tic+2+fm.getAscent()/2));
        g.rotate(Math.toRadians(90));		

        for (float d=fLabY1-incY1; d<=limit[Y][MAX]; d+=incY1) {
         	coord= (float)((d-limit[Y][MIN])*scaleY);
            g.setStroke(new BasicStroke(lineWn));
         	for (int i=1; i<divSub; i++) {
         		ticP = (float) (coord + incY1*scaleY/divSub*i);
         		if (ticP<height && ticP>0) {
                	line.setLine(-width, -ticP, -width-ticSub, -ticP);
                	g.draw(line);         		
         		}
         	}
        }
		log.debug("Ploted axes.");
	}
	
	/**
	 * @param text 
	 * 
	 */
	public void action(String text) {
       if (text == "Manual scale axes"){
    		log.debug("execute: "+text);
    		this.displayManual();
        } else if (text == "Auto scale axes"){
    		log.debug("execute: "+text);
    		this.displayAuto();
        } else if (text == "Save..."){
    		log.debug("execute: "+text);
    		this.saveAs();
        } else if (text == "Save svg") {
        	log.debug("execute: "+text);
			String fileName = Setting.homeDir+"/"+Setting.magazine+"/"+this.sample+".svg";
			this.saveSvg(fileName);
        } else if (text == "Save png") {
        	log.debug("execute: "+text);
			String fileName = Setting.homeDir+"/"+Setting.magazine+"/"+this.sample+".png";
			this.savePng(fileName);
	    }  
	}
	
	private void saveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(Setting.homeDir+"/"+Setting.magazine+"/"+this.sample));
		fc.setDialogTitle("Save to file");
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".xls");
			}
			public String getDescription() {
				return "excel file (data)";
			}
		});	
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".svg");
			}
			public String getDescription() {
				return "svg file";
			}
		});					
//		fc.addChoosableFileFilter(new FileFilter() {
//			public boolean accept( File f ) {
//				return f.isDirectory() ||
//				f.getName().toLowerCase().endsWith(".pdf");
//			}
//			public String getDescription() {
//				return "pdf file";
//			}
//		});	
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept( File f ) {
				return f.isDirectory() ||
				f.getName().toLowerCase().endsWith(".png");
			}
			public String getDescription() {
				return "png file";
			}
		});	
//		fc.addChoosableFileFilter(new FileFilter() {
//			public boolean accept( File f ) {
//				return f.isDirectory() ||
//				f.getName().toLowerCase().endsWith(".eps");
//			}
//			public String getDescription() {
//				return "eps file";
//			}
//		});	
		int returnVal = fc.showSaveDialog(this);
		log.debug(-2);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			String fileName = file.toString();
			if (fc.getFileFilter().getDescription()=="svg file") {
				saveSvg(fileName);
//				} else if (fc.getFileFilter().getDescription()=="eps file") {
//		    		if (fileName.toLowerCase().endsWith(".eps") == false){
//						fileName = fileName + ".eps";
//					}
//					FileOutputStream fileStream = new FileOutputStream(fileName);
//		    		TranscoderInput input = new TranscoderInput(doc);
//		    		TranscoderOutput output = new TranscoderOutput(fileStream);
//		    		Transcoder t = new EPSTranscoder();
//		    		t.addTranscodingHint(EPSTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 2.52f/width*10);
//					t.transcode(input, output);
//		    		log.info("Saved file: "+fileName);
//		    		fileStream.flush();
//		    		fileStream.close();
			} else if (fc.getFileFilter().getDescription()=="png file") {
				this.savePng(fileName);
			} else if (fc.getFileFilter().getDescription()=="excel file (data)") {
				if (fileName.toLowerCase().endsWith(".xls") == false){
					fileName = fileName + ".xls";
				}
				this.saveCalibExcel(fileName);
//				} else if (fc.getFileFilter().getDescription()=="pdf file") {
//		    		if (fileName.toLowerCase().endsWith(".pdf") == false){
//						fileName = fileName + ".pdf";
//					}
//					FileOutputStream fileStream = new FileOutputStream(fileName);
//		    		TranscoderInput input = new TranscoderInput(doc);
//		    		TranscoderOutput output = new TranscoderOutput(fileStream);
//		    		Transcoder t = new PDFTranscoder();
//					t.transcode(input, output);
//		    		log.info("Saved file: "+fileName);
//		    		fileStream.flush();
//		    		fileStream.close();
			} else {
				log.error("Not proper file format (programming error!");
			}
		} else {
			log.debug("colSaveBat canceled by user.");
		}
	}
	
	private void savePng(String fileName) {
		FileOutputStream fileStream;
		try {
    		if (fileName.toLowerCase().endsWith(".png") == false){
				fileName = fileName + ".png";
			}
			fileStream = new FileOutputStream(fileName);
			// TODO Auto-generated catch block
			TranscoderInput input = new TranscoderInput(doc);
			TranscoderOutput output = new TranscoderOutput(fileStream);
			Transcoder t = new PNGTranscoder();
			t.transcode(input, output);
			log.info("Saved file: "+fileName);
			fileStream.flush();
			fileStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	   
	private void saveSvg(String fileName) {
		FileOutputStream fileStream;
		try {
    		if (fileName.toLowerCase().endsWith(".svg") == false){
				fileName = fileName + ".svg";
			}
			fileStream = new FileOutputStream(fileName);
			Writer writer = new OutputStreamWriter(fileStream, "UTF-8");
    		TranscoderInput input = new TranscoderInput(doc);
    		TranscoderOutput output = new TranscoderOutput(writer);
    		Transcoder t = new SVGTranscoder();
			t.transcode(input, output);
    		log.info("Saved file: "+fileName);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	   
	/**
	 * 
	 */
	public void setAutoLimit()
	{
		ArrayList<CaliP> calArr = calib.calArr;
		limit = new int[2][2];
		limit[X][MIN] = 100000;
		limit[X][MAX] = -100000;
		limit[Y][MIN] = 100000;
		limit[Y][MAX] = -100000;
        addXl = Setting.getInt("/bat/calib/graph/dimension/add_x_low");
        addYl = Setting.getInt("/bat/calib/graph/dimension/add_y_low");
        addXh = Setting.getInt("/bat/calib/graph/dimension/add_x_high");
        addYh = Setting.getInt("/bat/calib/graph/dimension/add_y_high");
        int range = Setting.getInt("/bat/calib/graph/dimension/range");
        log.debug(addXl+"-"+addYl+"-"+addXh+"-"+addYh);
		limit[Y][MAX] = (int) Math.ceil((age+range*ageSig+addYh)/labelInc)*labelInc;
		limit[Y][MIN] = (int) Math.floor((age-range*ageSig-addYl)/labelInc)*labelInc;
		for (int i=0; i<calArr.size(); i++) {
			CaliP caliP = calArr.get(i);
			double rAge = caliP.radAge;
			double rSig = caliP.radAgeSig;
			if ( (rAge-range*rSig)<(age+range*ageSig) && (rAge+range*rSig)>(age-range*ageSig) ) {
//				if ( (rAge+range*rSig+addYh) > limit[Y][MAX]) {
//					limit[Y][MAX] = (int) Math.ceil((rAge+range*rSig+addYh)/labelInc)*labelInc;
//				}
//				if ( (rAge-range*rSig-addYl) < limit[Y][MIN]) {
//					limit[Y][MIN] = (int) Math.floor((rAge-range*rSig-addYl)/labelInc)*labelInc;
//								}
				if ( (caliP.calAge+addXh) > limit[X][MAX]) {
			        limit[X][MAX] = (int) Math.ceil((caliP.calAge+addXh)/labelInc)*labelInc;
				} 
				if ( (caliP.calAge-addXl) < limit[X][MIN]) {
					limit[X][MIN] = (int) Math.floor((caliP.calAge-addXl)/labelInc)*labelInc;
				}
			}
		}
        scaleX = 1.0*width/(limit[X][MAX]-limit[X][MIN]);
        scaleY = 1.0*height/(limit[Y][MAX]-limit[Y][MIN]);
		log.debug("Set automatically limits for plot (x: "+limit[X][MIN]+"-"+limit[X][MAX]+" y: "+limit[Y][MIN]+"-"+limit[Y][MAX]+").");
	}
	
	private boolean setManLimit() {
        JPanel panel;
        String[] ConnectOptionNames = { "OK", "Cancel" };
	 	
	    final JFormattedTextField textField = new JFormattedTextField(new DecimalFormat("##0"));
        textField.setValue(this.limit[X][MIN]);
        textField.setColumns(6); //get some space

        textField.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField.selectAll();
                }else try{                    //The text is valid,
                    textField.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField2 = new JFormattedTextField(new DecimalFormat("##0"));
        textField2.setValue(this.limit[X][MAX]);
        textField2.setColumns(6); //get some space

        textField2.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField2.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField2.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField2.selectAll();
                }else try{                    //The text is valid,
                    textField2.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField3 = new JFormattedTextField(new DecimalFormat("##0"));
        textField3.setValue(this.limit[Y][MIN]);
        textField3.setColumns(6); //get some space

        textField3.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField3.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField3.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField3.selectAll();
                }else try{                    //The text is valid,
                    textField3.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
	    final JFormattedTextField textField4 = new JFormattedTextField(new DecimalFormat("##0"));
        textField4.setValue(this.limit[Y][MAX]);
        textField4.setColumns(6); //get some space

        textField4.getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0) ,"check");
        textField4.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField4.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField4.selectAll();
                }else try{                    //The text is valid,
                    textField4.commitEdit();     //so use it.
                } catch (java.text.ParseException exc) { }
            }
        });
	 	
		panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		JPanel namePanel = new JPanel(false);
		namePanel.setLayout(new GridLayout(0, 1));
 		// Create the labels
		JLabel labXmin = new JLabel("X min:   ", JLabel.RIGHT);
		JLabel  labXmax = new JLabel("X max:   ", JLabel.RIGHT);
		JLabel  labYmin = new JLabel("Y min:   ", JLabel.RIGHT);
		JLabel  labYmax = new JLabel("Y max:   ", JLabel.RIGHT);
		namePanel.add(labXmin);
		namePanel.add(labXmax);
		namePanel.add(labYmin);
		namePanel.add(labYmax);
		JPanel fieldPanel = new JPanel(false);
		fieldPanel.setLayout(new GridLayout(0, 1));
		fieldPanel.add(textField);
		fieldPanel.add(textField2);
		fieldPanel.add(textField3);
		fieldPanel.add(textField4);
		panel.add(namePanel);
		panel.add(fieldPanel);
		if(JOptionPane.showOptionDialog(
				this, panel, 
				"Scaling of axes",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.INFORMATION_MESSAGE,
                null, ConnectOptionNames, 
                ConnectOptionNames[0]) 
                != 0
                ) 
		{
			log.debug("Aborted rescaling.");
			return false;
		} else {
			limit[X][MIN] = Integer.valueOf(textField.getText().replace("'",""));
			limit[X][MAX] = Integer.valueOf(textField2.getText().replace("'",""));
			limit[Y][MIN] = Integer.valueOf(textField3.getText().replace("'",""));
			limit[Y][MAX] = Integer.valueOf(textField4.getText().replace("'",""));
		    scaleX = 1.0*width/(limit[X][MAX]-limit[X][MIN]);
		    scaleY = 1.0*height/(limit[Y][MAX]-limit[Y][MIN]);
			log.debug("Set manually limits for plot (x: "+limit[X][MIN]+"-"+limit[X][MAX]+" y: "+limit[Y][MIN]+"-"+limit[Y][MAX]+").");
			return true;
		}
	}
}

/**
 * @author lukas
 *
 */
class CalibMenu extends JMenuBar implements ActionListener {
	/**
	 * 
	 */
	// Ask AWT which menu modifier we should be using.
	final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	CalibGraph calibG;
	Bats main;
	
	/**
	 * @param calibG
	 */
	public CalibMenu(CalibGraph calibG) {
		this.calibG=calibG;
		
	    JMenu menu;
	    JMenuItem menuItem;
	
	    //Build the first menu.
	    menu = new JMenu("File");
	    menu.setMnemonic(KeyEvent.VK_F);
	    menu.getAccessibleContext().setAccessibleDescription("File");
	    this.add(menu);

	    menuItem = new JMenuItem("Save...");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save as...");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);

	    menuItem = new JMenuItem("Save svg");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save svg to default location");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);
	    
	    menuItem = new JMenuItem("Save png");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Save png to default location");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);
	    
	    menu.addSeparator();

	    menuItem = new JMenuItem("Auto scale axes");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Auto scale axes");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);	
	    
	    menuItem = new JMenuItem("Manual scale axes");
//	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,  MENU_MASK));
	    menuItem.getAccessibleContext().setAccessibleDescription("Manual scale axes");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);		
	}

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        calibG.action(source.getText());
    } 
}

/**
 * @author lukas
 *
 */
class CalibTBar extends JToolBar implements ActionListener
{
	CalibGraph calibG;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @param calibG 
	 * 
	 */
	public CalibTBar(CalibGraph calibG) { 
		this.calibG = calibG;
		Insets margins = new Insets(2, 2, 2, 2);

//		ToolBarButton button = new ToolBarButton(Setting.batDir+"/icon/pref20.png");
//		button.setToolTipText("Preferences");
//		button.setMargin(margins);
//		button.addActionListener(this);
//		add(button);
//		addSeparator();
//		
		ToolBarButton button2 = new ToolBarButton(Setting.batDir+"/icon/save20.png");
		button2.setToolTipText("Save png");
		button2.setMargin(margins);
		button2.addActionListener(this);
		add(button2);
		
		ToolBarButton button3 = new ToolBarButton(Setting.batDir+"/icon/save_as20.png");
		button3.setToolTipText("Save...");
		button3.setMargin(margins);
		button3.addActionListener(this);
		add(button3);
		addSeparator();
		
		ToolBarButton button4 = new ToolBarButton(Setting.batDir+"/icon/axes_auto.png");
		button4.setToolTipText("Auto scale axes");
		button4.setMargin(margins);
		button4.addActionListener(this);
		add(button4);

		ToolBarButton button5 = new ToolBarButton(Setting.batDir+"/icon/axes_man.png");
		button5.setToolTipText("Manual scale axes");
		button5.setMargin(margins);
		button5.addActionListener(this);
		add(button5);
		addSeparator();
}
	
	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
        ToolBarButton source = (ToolBarButton)(e.getSource());
        calibG.action(source.getToolTipText());
	}
}
       
