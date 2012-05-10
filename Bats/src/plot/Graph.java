package plot;
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
public class Graph extends JPanel{
	final static Logger log = Logger.getLogger(RunGraph.class);
	
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
	double multi = 1;
	ArrayList<Data> data;

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
	public Graph(ArrayList<Data> data, double multi){	
		this.data = data;
		this.format = format;
		
		makePlotData();
	}
	
	@SuppressWarnings("unchecked")
	private void setPlotData(Run){
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
	    	plotdata.get(i) = new PlotData();
	    	try {
		    	plotdata.get(i).value = (Double)data.get(value)*multi;
	    	}
	    	catch (ClassCastException e){ 
	    		log.error("ClassCastException ("+format+"): "+data.get(value)+" / "+value);
		    	plotdata.get(i).err = 0.0;
	    	}
	    	catch (NullPointerException e){ 
		    	plotdata.get(i).value = 0.0;
	    	}
	    	
	    	try {
		    	plotdata.get(i).err = (Double)data.get(error)*multi;
	    	}
	    	catch (ClassCastException e){ 
	    		log.error("ClassCastException ("+format+"): "+data.get(error)+" / "+error);
		    	plotdata.get(i).err = 0.0;
	    	}catch (NullPointerException e){ 
		    	plotdata.get(i).err = 0.0;
	    	}
	    	
	    	try {
		    	plotdata.get(i).sig = (Double)data.get(sig)*multi;
	    	}catch (ClassCastException e) { 
	    		log.error("ClassCastException ("+format+"): "+data.get(sig)+" / "+sig);
		    	plotdata.get(i).sig = 0.0;
	    	}catch (NullPointerException e) { 
		    	plotdata.get(i).sig = 0.0;
	    	}
	    	try {
	    		plotdata.get(i).active = (Boolean)data.get("active");
//	    		log.debug("active "+i+": "+plotdata.get(i).active);
	    	}catch(ClassCastException e) {
	    		log.error("ClassCastException ("+format+"): "+data.get("active")+" / "+"active");
	    		plotdata.get(i).active = true;	    		
	    	}catch(NullPointerException e) {
	    		log.error("NullPointerException ("+format+"): "+data.get("active")+" / "+"active");
	    		plotdata.get(i).active = true;	    		
	    	}
	    	
	    	if ((Boolean)data.get("active")==true) {
		    	pm = Math.max(plotdata.get(i).err, plotdata.get(i).sig);
		    	if ( (plotdata.get(i).value - 2*pm) < min ){
		    		min = (plotdata.get(i).value - 2*pm);
		    	}
		    	if ( (plotdata.get(i).value + 2*pm) > max ){
		    		max = (plotdata.get(i).value + 2*pm);
		    	}
	    	}
	    }
	    if (max<min)min=max;
	    
	    Double div = Math.pow(10,Math.floor(Math.log10 ((max-min)/SCALE)));
	    
	    p_min = Math.max(0,Math.round(Math.pow(10,15)*Math.floor(min/div)*div)/Math.pow(10,15));
	    p_max = Math.round(Math.pow(10,15)*Math.ceil(max/div)*div)/Math.pow(10,15);
	    	    		
		cal = PW/(p_max-p_min);
		
	    for (int i = 0; i< tableDat.size(); i++){
	    	plotdata.get(i).p_sig = (int) Math.round((plotdata.get(i).sig)*cal);
	    	plotdata.get(i).p_err = (int) Math.round((plotdata.get(i).err)*cal);
	    	plotdata.get(i).p_value = (int) Math.round((plotdata.get(i).value-p_min)*cal);
	    }
	}
	
	private BufferedImage drawImage() {
		BufferedImage image = new BufferedImage( PW+L+R, data.size()*CH+T+B, BufferedImage.TYPE_INT_RGB );
		Graphics2D g = image.createGraphics();
	    Color color;
	    //Plot box
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(0, 0, PW+L+R, T+data.size()*CH + B);
		g.setBackground(Color.yellow);
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(L, 0, PW+L, T);
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/bg_color"));
	    g.fillRect(L, T+data.size()*CH, PW+L, T+data.size()*CH+B);
		//Plot number
	    if (data.size()>=0) {
		    g.setColor(Setting.getColor("/bat/general/plot/stdBl/fg_color"));
			g.setFont( new Font( "Monospaced", Font.BOLD, 14 ) );
			g.drawString(Double.toString(p_min),L,T-4);
			g.drawString(Double.toString(p_max),PW+L-getFontMetrics(getFont()).stringWidth(Double.toString(p_max))-2,T-4);
			g.drawString(Double.toString(p_min),L,T+data.size()*CH+16);
			g.drawString(Double.toString(p_max),PW+L-getFontMetrics(getFont()).stringWidth(Double.toString(p_max))-2,T+data.size()*CH+16);
	    }
		// Plot background	    
	    for (int i = 0; i< data.size(); i++) {
	    	if (data.get(i).active!=true) {
	    		g.setColor(Setting.getColor("/bat/general/table/color/bg_false"));
	    	}
			else {
	    	    if (i % 2 == 0) {
	    	    	g.setColor(Setting.getColor("/bat/general/table/color/bg_even"));
	    	    } else {
	    	    	g.setColor(Setting.getColor("/bat/general/table/color/bg_odd"));
	    	    }
			}
		    g.fillRect(L, T+i*CH, PW, CH);
	    }
	    // Plot 1 sigma range of mean
	    if (data.size()>0) {
	    	g.setColor(Setting.getColor("/bat/general/table/color/fg_std2"));
	    	int k=0;
		    for (int i = 0; i< data.size(); i++) {
				g.drawLine(data.get(i).p_value+L, T+k*CH, data.get(i).p_value+L, T+(i+1)*CH);
				g.drawLine(data.get(i).p_value-data.get(i).p_err+L, T+k*CH, data.get(i).p_value-data.get(i).p_err+L, T+(i+1)*CH);
				g.drawLine(data.get(i).p_value+data.get(i).p_err+L, T+k*CH, data.get(i).p_value+data.get(i).p_err+L, T+(i+1)*CH);
				k=i;
		    }
	    }
		// Plot error bars
	    for (int i = 0; i< data.size(); i++){
	    	if (data.get(i).active!=true) {
	    		color = Setting.getColor("/bat/general/table/color/fg_false");
	    	}
	    	else {
				if (i % 2 == 0) {
					color = Setting.getColor("/bat/general/table/color/fg_even");
			    } else {
			    	color = Setting.getColor("/bat/general/table/color/fg_odd");
			    }
			}

	    	// Plot sigma
	    	g.setColor(Setting.getColor("/bat/general/plot/stdBl/sig_color"));
	    	g.drawLine(data.get(i).p_value-data.get(i).p_sig+L, T+i*CH+CH/2-3, data.get(i).p_value-data.get(i).p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(data.get(i).p_value+data.get(i).p_sig+L, T+i*CH+CH/2-3, data.get(i).p_value+data.get(i).p_sig+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_sig+L, T+i*CH+CH/2-3, data.get(i).p_value-2*data.get(i).p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(data.get(i).p_value+2*data.get(i).p_sig+L, T+i*CH+CH/2-3, data.get(i).p_value+2*data.get(i).p_sig+L, T+i*CH+CH/2+3);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_sig+L, T+i*CH+CH/2-1, data.get(i).p_value+2*data.get(i).p_sig+L, T+i*CH+CH/2-1);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_sig+L, T+i*CH+CH/2, data.get(i).p_value+2*data.get(i).p_sig+L, T+i*CH+CH/2);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_sig+L, T+i*CH+CH/2+1, data.get(i).p_value+2*data.get(i).p_sig+L, T+i*CH+CH/2+1);
		    // Plot value and error in color
	    	g.setColor(color);
	    	g.drawOval(data.get(i).p_value+L-3, T+i*CH+CH/2-3, 6, 6);
//	    	g.drawLine(plotdata.get(i).p_value+L, T+i*CH+CH/2-2, plotdata.get(i).p_value+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value-data.get(i).p_err+L, T+i*CH+CH/2-2, data.get(i).p_value-data.get(i).p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value+data.get(i).p_err+L, T+i*CH+CH/2-2, data.get(i).p_value+data.get(i).p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_err+L, T+i*CH+CH/2-2, data.get(i).p_value-2*data.get(i).p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value+2*data.get(i).p_err+L, T+i*CH+CH/2-2, data.get(i).p_value+2*data.get(i).p_err+L, T+i*CH+CH/2+2);
	    	g.drawLine(data.get(i).p_value-2*data.get(i).p_err+L, T+i*CH+CH/2, data.get(i).p_value+2*data.get(i).p_err+L, T+i*CH+CH/2);
	    }
	    //Plot frame
	    g.setColor(Setting.getColor("/bat/general/plot/stdBl/fg_color"));
		g.drawLine(L, T, L, T+data.size()*CH);
		g.drawLine(PW+L, T, PW+L, T+data.size()*CH);
		g.drawLine(L, T, L+PW, T);
		g.drawLine(L, T+data.size()*CH, PW+L, T+data.size()*CH);
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
	    File file = new File( fileName+"."+typ );
		ImageIO.write( drawImage(), typ, file );
	    return file.toString();
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
		if (saveTrue) {
			try {
				String file = this.save(Setting.homeDir+"/"+Setting.magazine+"/"+value, "png");
				log.debug("Saved plot: "+file);
			} catch (IOException e) {
				log.error("Could not write image to file: "+Setting.homeDir+"/"+Setting.magazine+"/"+value+".png");
			}
		}
	}
	
}


