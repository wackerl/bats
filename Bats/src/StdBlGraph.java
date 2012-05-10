import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
@SuppressWarnings("serial")
public class StdBlGraph extends JPanel{
	final static Logger log = Logger.getLogger(StdBlGraph.class);
	
	Calc data;
	ArrayList<DataSet> tableDat;
	static int T = Setting.getInt("/bat/general/plot/stdBl/top");
	static int B = Setting.getInt("/bat/general/plot/stdBl/bottom");
	static int PW = Setting.getInt("/bat/general/plot/stdBl/p_width");
	static int L = Setting.getInt("/bat/general/plot/stdBl/left");
	static int R = Setting.getInt("/bat/general/plot/stdBl/right");
	static int CH = Setting.getInt("/bat/general/table/c_hight")+Setting.getInt("/bat/general/table/gap_y");
	static int SCALE = Setting.getInt("/bat/general/plot/stdBl/scale");
	boolean saveTrue = Setting.getBoolean("/bat/general/file/autosave/plot");
	
	Double cal;
	Double p_min;
	Double p_max;
	int multi = 1;
	PlotData[] plotData;

	String error;
	String sig;
	String value;
	String format;
	String dataName, dataName_o;
	
	BufferedImage img;
	
	/**
	 * @param data 
	 * @param format 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public StdBlGraph(Calc data, String format){	
		this.data = data;
		this.format = format;
		if (format.equals("std13")){
			dataName_o = "allStd";
			value = "std_ba";
			error = "std_ba_err";
			sig = "std_ba_sig";
			multi = 100;
		}
		else if (format.equals("stdPu")){
			dataName_o = "allStd";
			value = "std_rb";
			error = "std_rb_err";
			sig = "std_rb_sig";
		}
		else if (format.equals("std")){
			dataName_o = "allStd";
			value = "std_ra";
			error = "std_ra_err";
			sig = "std_ra_sig";
		}
		else if (format.equals("blank")||format.equals("bl")){
			dataName_o = "allBlank";
			value = "ra_bg";
			error = "ra_bg_err";
			sig = "ra_bg_sig";
		}
		else { log.error("tableDat not set for: "+format); }
		
		if (Setting.getBoolean("/bat/general/table/run_order")==true) {
			dataName = dataName_o+"R";
		} else {
			dataName = dataName_o;			
		}

		makePlotData();
	}
	
	@SuppressWarnings("unchecked")
	private void makePlotData(){
		tableDat = data.get(dataName);
		this.removeAll();
		this.setPreferredSize(new Dimension(PW+L+R, tableDat.size()*CH+T+B));
		plotData = new PlotData[tableDat.size()];				
		Double max = 0.0;
		Double min = 1000000.0;
		DataSet data;
		double pm;
		
	    for (int i = 0; i< tableDat.size(); i++){
	    	data = tableDat.get(i);
	    	plotData[i] = new PlotData();
	    	try {
		    	plotData[i].value = (Double)data.get(value)*multi;
	    	}
	    	catch (ClassCastException e){ 
	    		log.error("ClassCastException ("+format+"): "+data.get(value)+" / "+value);
		    	plotData[i].err = 0.0;
	    	}
	    	catch (NullPointerException e){ 
		    	plotData[i].value = 0.0;
	    	}
	    	
	    	try {
		    	plotData[i].err = (Double)data.get(error)*multi;
	    	}
	    	catch (ClassCastException e){ 
	    		log.error("ClassCastException ("+format+"): "+data.get(error)+" / "+error);
		    	plotData[i].err = 0.0;
	    	}catch (NullPointerException e){ 
		    	plotData[i].err = 0.0;
	    	}
	    	
	    	try {
		    	plotData[i].sig = (Double)data.get(sig)*multi;
	    	}catch (ClassCastException e) { 
	    		log.error("ClassCastException ("+format+"): "+data.get(sig)+" / "+sig);
		    	plotData[i].sig = 0.0;
	    	}catch (NullPointerException e) { 
		    	plotData[i].sig = 0.0;
	    	}
	    	try {
	    		plotData[i].active = (Boolean)data.get("active");
//	    		log.debug("active "+i+": "+plotData[i].active);
	    	}catch(ClassCastException e) {
	    		log.error("ClassCastException ("+format+"): "+data.get("active")+" / "+"active");
	    		plotData[i].active = true;	    		
	    	}catch(NullPointerException e) {
	    		log.error("NullPointerException ("+format+"): "+data.get("active")+" / "+"active");
	    		plotData[i].active = true;	    		
	    	}
	    	
	    	if ((Boolean)data.get("active")==true && !(tableDat.get(i) instanceof StdBl)) {
		    	pm = Math.max(plotData[i].err, plotData[i].sig);
		    	if ( (plotData[i].value - 2*pm) < min ){
		    		min = (plotData[i].value - 2*pm);
		    	}
		    	if ( (plotData[i].value + 2*pm) > max ){
		    		max = (plotData[i].value + 2*pm);
		    	}
	    	}
	    }
	    if (max<min)min=max;
	    
	    Double div = Math.pow(10,Math.floor(Math.log10 ((max-min)/SCALE)));
	    
	    p_min = Math.max(0,Math.round(Math.pow(10,15)*Math.floor(min/div)*div)/Math.pow(10,15));
	    p_max = Math.round(Math.pow(10,15)*Math.ceil(max/div)*div)/Math.pow(10,15);
	    	    		
		cal = PW/(p_max-p_min);
		
	    for (int i = 0; i< tableDat.size(); i++){
	    	plotData[i].p_sig = (int) Math.round((plotData[i].sig)*cal);
	    	plotData[i].p_err = (int) Math.round((plotData[i].err)*cal);
	    	plotData[i].p_value = (int) Math.round((plotData[i].value-p_min)*cal);
	    }
	}
	
	private BufferedImage drawImage() {
		BufferedImage image = new BufferedImage( PW+L+R, tableDat.size()*CH+T+B, BufferedImage.TYPE_INT_RGB );
		Graphics2D g = image.createGraphics();
	    Color color;
	    //Plot box
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(0, 0, PW+L+R, T+tableDat.size()*CH + B);
		g.setBackground(Color.yellow);
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(L, 0, PW+L, T);
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(L, T+tableDat.size()*CH, PW+L, T+tableDat.size()*CH+B);
		//Plot number
	    if (plotData.length>=0) {
		    g.setColor(Setting.getColor("/bat/general/plot/stdBl/fg_color"));
			g.setFont( new Font( "Monospaced", Font.BOLD, 14 ) );
			g.drawString(Double.toString(p_min),L,T-4);
			g.drawString(Double.toString(p_max),PW+L-getFontMetrics(getFont()).stringWidth(Double.toString(p_max))-2,T-4);
			g.drawString(Double.toString(p_min),L,T+plotData.length*CH+16);
			g.drawString(Double.toString(p_max),PW+L-getFontMetrics(getFont()).stringWidth(Double.toString(p_max))-2,T+plotData.length*CH+16);
	    }
		// Plot background	    
	    for (int i = 0; i< plotData.length; i++) {
	    	if (plotData[i].active!=true && !(tableDat.get(i) instanceof StdBl)) {
	    		g.setColor(Setting.getColor("/bat/general/table/color/bg_false"));
	    	}
			else if (tableDat.get(i) instanceof Run) {
	    	    if (i % 2 == 0) {
	    	    	g.setColor(Setting.getColor("/bat/general/table/color/bg_even"));
	    	    } else {
	    	    	g.setColor(Setting.getColor("/bat/general/table/color/bg_odd"));
	    	    }
			}
		    else if (tableDat.get(i) instanceof StdBl) {
		    	if (((StdBl)tableDat.get(i)).label.equals("used")) {
				    g.setColor(Setting.getColor("/bat/general/table/color/bg_std2"));
		    	} else {
				    g.setColor(Setting.getColor("/bat/general/table/color/bg_std1"));		    		
		    	}
			}
		    else if (tableDat.get(i) instanceof Sample) {
			    g.setColor(Setting.getColor("/bat/general/table/color/bg_sample"));
			}
		    g.fillRect(L, T+i*CH, PW, CH);
	    }
	    // Plot 1 sigma range of mean
	    if (plotData.length>0) {
	    	g.setColor(Setting.getColor("/bat/general/table/color/fg_std2"));
	    	int k=0;
		    for (int i = 0; i< plotData.length; i++) {
		    	if (tableDat.get(i) instanceof StdBl) {
			    	if (((StdBl)tableDat.get(i)).label.equals("used")) {
						g.drawLine(plotData[i].p_value+L, T+k*CH, plotData[i].p_value+L, T+(i+1)*CH);
						g.drawLine(plotData[i].p_value-plotData[i].p_err+L, T+k*CH, plotData[i].p_value-plotData[i].p_err+L, T+(i+1)*CH);
						g.drawLine(plotData[i].p_value+plotData[i].p_err+L, T+k*CH, plotData[i].p_value+plotData[i].p_err+L, T+(i+1)*CH);
						k=i;
			    	}
		    	}
		    }
	    }
		// Plot error bars
	    for (int i = 0; i< plotData.length; i++){
	    	if (plotData[i].active!=true && !(tableDat.get(i) instanceof StdBl)) {
	    		color = Setting.getColor("/bat/general/table/color/fg_false");
	    	}
	    	else if (i==plotData.length-1){
				color = Setting.getColor("/bat/general/table/color/fg_std2");
			}
		    else if (tableDat.get(i) instanceof Sample){
		    	color = Setting.getColor("/bat/general/table/color/fg_sample");
			}
			else if (tableDat.get(i) instanceof Run){
				if (i % 2 == 0) {
					color = Setting.getColor("/bat/general/table/color/fg_even");
			    } else {
			    	color = Setting.getColor("/bat/general/table/color/fg_odd");
			    }
			}
			else{
				color = Setting.getColor("/bat/general/table/color/fg_std1");
			}
	    	// Plot sigma
	    	g.setColor(Setting.getColor("/bat/general/plot/stdBl/sig_color"));
	    	g.drawLine(plotData[i].p_value-plotData[i].p_sig+L, T+i*CH+CH/2-3, plotData[i].p_value-plotData[i].p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(plotData[i].p_value+plotData[i].p_sig+L, T+i*CH+CH/2-3, plotData[i].p_value+plotData[i].p_sig+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_sig+L, T+i*CH+CH/2-3, plotData[i].p_value-2*plotData[i].p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(plotData[i].p_value+2*plotData[i].p_sig+L, T+i*CH+CH/2-3, plotData[i].p_value+2*plotData[i].p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_sig+L, T+i*CH+CH/2-1, plotData[i].p_value+2*plotData[i].p_sig+L, T+i*CH+CH/2-1);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_sig+L, T+i*CH+CH/2, plotData[i].p_value+2*plotData[i].p_sig+L, T+i*CH+CH/2);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_sig+L, T+i*CH+CH/2+1, plotData[i].p_value+2*plotData[i].p_sig+L, T+i*CH+CH/2+1);
		    // Plot value and error in color
	    	g.setColor(color);
	    	g.drawOval(plotData[i].p_value+L-3, T+i*CH+CH/2-3, 6, 6);
//	    	g.drawLine(plotData[i].p_value+L, T+i*CH+CH/2-2, plotData[i].p_value+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value-plotData[i].p_err+L, T+i*CH+CH/2-2, plotData[i].p_value-plotData[i].p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value+plotData[i].p_err+L, T+i*CH+CH/2-2, plotData[i].p_value+plotData[i].p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_err+L, T+i*CH+CH/2-2, plotData[i].p_value-2*plotData[i].p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value+2*plotData[i].p_err+L, T+i*CH+CH/2-2, plotData[i].p_value+2*plotData[i].p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(plotData[i].p_value-2*plotData[i].p_err+L, T+i*CH+CH/2, plotData[i].p_value+2*plotData[i].p_err+L, T+i*CH+CH/2);
	    }
	    //Plot frame
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/fg_color"));
		g.drawLine(L, T, L, T+plotData.length*CH);
		g.drawLine(PW+L, T, PW+L, T+plotData.length*CH);
		g.drawLine(L, T, L+PW, T);
		g.drawLine(L, T+plotData.length*CH, PW+L, T+plotData.length*CH);
		return image;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		this.removeAll();
	    super.paintComponent(g);
	    img = drawImage();
	    g.drawImage(img,0,0,null);
	}

	/**
	 * @param bufferedImage
	 * @return image
	 */
	public static Image toImage(BufferedImage bufferedImage) {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }

	/**
	 * @param fileName 
	 * @param typ 
	 * @return filename
	 * @throws IOException 
	 * 
	 */
	public String save(String fileName, String typ) throws IOException {
//	    this.toImage(drawImage());	
		if (!Setting.no_data) {
		    File file = new File( fileName+"."+typ );
			ImageIO.write( drawImage(), typ, file );
		    return file.toString();
		} else {
			return "no data";
		}
	}
	
	/**
	 * @param data
	 */
	public void repaint(Calc data){
		this.saveTrue = Setting.getBoolean("/bat/general/file/autosave/plot"); 
		this.data = data;
		if (Setting.getBoolean("/bat/general/table/run_order")==true) {
			dataName = dataName_o+"R";
		} else {
			dataName = dataName_o;			
		}
		this.makePlotData();
		this.setPreferredSize(new Dimension(PW+L+R, tableDat.size()*CH+T+B));
		this.repaint();
		if (saveTrue&&!Setting.no_data) {
			try {
				String file = this.save(Setting.homeDir+"/"+Setting.magazine+"/"+value, "png");
				log.debug("Saved plot: "+file);
			} catch (IOException e) {
				log.error("Could not write image to file: "+Setting.homeDir+"/"+Setting.magazine+"/"+value+".png");
			}
		}
	}
	

	private class PlotData{
		Double sig;
		Double err;
		Double value;
		
		Boolean active;
		
		int p_err;
		int p_sig;
		int p_value;
	}
}


