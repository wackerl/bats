import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class XhtmlFile {
	final static Logger log = Logger.getLogger(XhtmlFile.class);
	/**
	 * 
	 */
	public FormatT format=null;
	String fileName;
	Calc data;
	
	/**
	 * @param data
	 * @param fileName
	 * @param format
	 */
	public XhtmlFile(String fileName, Calc data, FormatT format) {
		this.fileName = fileName;
		this.data =data;
		this.format = format;
		if (!Setting.no_data) {		
	    	save(fileName);
		}
	}
	
	/**
	 * @param data
	 * @param file 
	 */
	public void update(Calc data, String file) {
		if (!Setting.no_data) {		
			this.data = data;
			save(file);
		}
	}
	
	/**
	 * @param file
	 */
	public void save(String file) {
		if (new File(file).exists()) { 
			new File(file).renameTo(new File(new File(file)+".old"));
			log.debug("Old file renamed: "+file);
		}
		try {
		    PrintWriter pw;
			FileOutputStream fs = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			pw = new PrintWriter(writer);
		    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		    pw.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		    pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			pw.println("<head>");
			pw.println("  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
			pw.println("  <title>"+data.magazine+"</title>");
			File cssF = new File(Setting.batDir+"/stylesheet.css");
			pw.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\""+cssF.toURI().toURL()+"\" title=\"Style\" />");
			pw.print(
					"<style type=\"text/css\">"+
					"@page  {" +
					"size: landscape;" +
					"margin: 14mm 10mm;" +
					"border: thin solid gray;" +
					"padding: 1em;" +
					"counter-increment: page;" +
					"@bottom-center {" +
					"font-family: Helvetica, Geneva, Arial, SunSans-Regular, sans-serif;" +
					"font-style: normal;" +
					"content: counter(page);" +
					"}" +
					"@top-left {" +
					"font-family: Helvetica, Geneva, Arial, SunSans-Regular, sans-serif;" +
					"font-style: normal;" +
					"content: \""+Setting.magazine+" ("+Setting.isotope+"), "+new Date().toString()+", "+Setting.user+"\""+
					"}" +
					"@top-right {" +
					"font-family: Helvetica, Geneva, Arial, SunSans-Regular, sans-serif;" +
					"font-style: normal;" +
					"content: \"Bats "+Setting.version+" by L. Wacker\""+
					"}" +
					"}"+
					"</style>");
			pw.println("</head>");
			pw.println("<body>");
			pw.println("  <table>");
			pw.print("    <tr class=\"TableHeading\">");
			for (int i=0;i<format.colName.size();i++) {
				pw.print("<td width=\""+format.colWidth.get(i)+"\">"+format.colName.get(i).replaceAll("<html>","").replaceAll("</html>","").replaceAll("<br>","<br />")+"</td>");
			}
		    pw.println("</tr>");
		    String align;
			int p=0;
		    if (Setting.getBoolean("/bat/general/file/output/sample-only")) {
				ArrayList<Sample> dat = data.sampleList;
//				log.debug("Sample size"+dat.size());
			    for (int i=0; i<dat.size(); i++){
			    	if (dat.get(i).get("active").equals(true)) {
				    	String cl="", cl2="";
				    	if (Setting.blankLabel.contains(dat.get(i).get("sampletype").toString().toLowerCase()) ) {
							cl="bl";
				    	} else if (dat.get(i).get("sampletype").toString().equalsIgnoreCase("nb")) {
							cl="nb";
				    	} else if (data.stdNom.isStd(dat.get(i).get("sampletype").toString())){
				    		cl="std";
				    	} 
				    	if (i % 2 != 0) {
				    		cl2="g";
				    	} 
					    if (++p>Setting.getInt("/bat/general/file/output/page_break/sample")) {
					    	pw.print("    <tr style=\"page-break-before:always\" align=\"center\" valign=\"bottom\" class=\"TableHeading\">");
							for (int j=0;j<format.colName.size();j++) {
								pw.print("<td width=\""+format.colWidth.get(j)+"\">"+format.colName.get(j).replaceAll("<html>","").replaceAll("</html>","").replaceAll("<br>","<br />")+"</td>");
							}
							pw.println("</tr>");
							p=0;
					    }
					    pw.print("    <tr class=\""+(cl+" "+cl2).trim()+"\">");
				    	for (int k=0; k<format.colValue.size(); k++) {
							Object value = dat.get(i).get(format.colValue.get(k));
							if (format.colFormat.get(k)!="" && (value instanceof Number)) {
								Double val = ((Number) value).doubleValue()*format.colMulti.get(k);
							    DecimalFormat form = new DecimalFormat(format.colFormat.get(k));
								value = form.format(val);
								align="right";
							}
							else if (format.colFormat.get(k)!="" && (value instanceof Integer)) {
								align="right";
							}
							else if (format.colFormat.get(k)!="" && (value instanceof Date)){
							      Date dateValue = (Date) value;
							      SimpleDateFormat form = new SimpleDateFormat(format.colFormat.get(k));
								  value = form.format(dateValue);
									align="right";
							} 
							else if (format.colFormat.get(k)!="" && (value instanceof String)){
							    value = ((String) value).replaceAll("&","+").replaceAll("<","-").replaceAll(">","-");
								align="left";
							} else {
								align="left";
							}
				    		pw.print("<td align=\""+align+"\">"+value+"</td>");
				    	}
					    pw.println("</tr>");
			    	}
			    }
		    } else {
				ArrayList<DataSet> dat = (ArrayList<DataSet>)data.runSampleList;
//				log.debug("Run+Sample size"+dat.size());
			    for (int i=0; i<dat.size(); i++){
			    	if (dat.get(i).get("active").equals(true)) {
				    	String cl="",cl2="",cl3="";
				    	if (i % 2 != 0) {
				    		cl3="g";
				    	}
				    	if (dat.get(i).get("active").equals(false)) {
				    		cl="False";
				    	} else {
				    		cl=dat.get(i).getClass().getName();
				    	}
				    	if (Setting.blankLabel.contains(dat.get(i).get("sampletype").toString().toLowerCase()) 
								|| dat.get(i).get("sampletype").toString().equalsIgnoreCase("nb")) {
							cl2="bl";
				    	} else if (data.stdNom.isStd(dat.get(i).get("sampletype").toString())){
				    		cl2="std";
				    	}
					    if (++p>Setting.getInt("/bat/general/file/output/page_break/run")) {
					    	pw.print("    <tr style=\"page-break-before:always\" align=\"left\" valign=\"bottom\" class=\"TableHeading\">");
							for (int j=0;j<format.colName.size();j++) {
								pw.print("<td width=\""+format.colWidth.get(j)+"\">"+format.colName.get(j).replaceAll("<html>","").replaceAll("</html>","").replaceAll("<br>","<br />")+"</td>");
							}
							pw.println("</tr>");
							p=0;
					    }
					    if (p>Setting.getInt("/bat/general/file/output/page_break/sample") && cl.equals("Sample")) {
					    	p=10000000;	
					    }
					    pw.print("    <tr class=\""+((cl+" "+cl2).trim()+" "+cl3).trim()+"\">");
				    	for (int k=0; k<format.colValue.size(); k++) {
							Object value = dat.get(i).get(format.colValue.get(k));
							if (format.colFormat.get(k)!="" && (value instanceof Number)) {
								Double val = ((Number) value).doubleValue()*format.colMulti.get(k);
							    DecimalFormat form = new DecimalFormat(format.colFormat.get(k));
								value = form.format(val);
								align="right";
							}
							else if (format.colFormat.get(k)!="" && (value instanceof Date)){
							      Date dateValue = (Date) value;
							      SimpleDateFormat form = new SimpleDateFormat(format.colFormat.get(k));
								  value = form.format(dateValue);
									align="right";
							} else {
								align="left";
							}
				    		pw.print("<td align=\""+align+"\">"+value+"</td>");
				    	}
					    pw.println("</tr>");
			    	}
			    }
		    }
		    pw.println("  </table>");
	    // corrections
	    	String unit;
	    	if (Setting.isotope.contains("P")) {
				unit = "";
			} else {
				unit = "10<sup>-12</sup>";
			}
	   	    DecimalFormat df = new DecimalFormat("0.0000");
		    if (Setting.getBoolean("/bat/general/file/output/corr")== true) {
				pw.println("  <table style=\"page-break-before:always\">");
				pw.print("    <tr align=\"left\" valign=\"bottom\" class=\"TableHeading\">");
				pw.println("<td colspan=\"5\">Corrections</td></tr>");
				if (Setting.isotope.contains("C14")) {
					pw.print("    <tr><td>Nominal value: </td><td>"+Setting.getDouble("bat/isotope/calc/nominal_ra")/100+"</td><td align=\"left\">F<sup>14</sup>C</td></tr>");
					pw.println("<tr><td>(δ<sup>13</sup>C)</td><td>"+(Setting.getDouble("bat/isotope/calc/nominal_ba")*1000-1000)+"</td><td align=\"left\"> ‰</td></tr>");
				} else {
					pw.print("    <tr><td>Nomainal value: </td><td>"+Setting.getDouble("bat/isotope/calc/nominal_ra")+"</td><td align=\"left\">"+unit+"</td></tr>");
				}
				if (Setting.getDouble("/bat/isotope/calc/scatter")!=0.0) {
					pw.println("    <tr><td width=\"180\">Sample scatter: </td><td  width=\"80\">"+Setting.getDouble("/bat/isotope/calc/scatter")*100+"</td><td width=\"40\" align=\"left\">%</td><td width=\"70\"></td></tr>");
				}
				if (Setting.isotope.contains("C14")) {
					pw.println("    <tr><td><sup>13</sup>C/<sup>12</sup>C corr: </td><td>"+Setting.getBoolean("/bat/isotope/calc/fract")+"</td><td></td><td></td></tr>");
				}
				pw.println("    <tr><td>Poisson stat.: </td><td>"+Setting.getBoolean("/bat/isotope/calc/poisson")+"</td><td></td><td></td></tr>");
		    	if (Setting.isotope.contains("P")) {
		    		;
		    	} else {
					pw.println("    <tr><td>Current a (off/err-abs/err-rel)</td><td align=\"center\">"+Setting.getString("/bat/isotope/calc/current/a/offset")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/a/error_abs")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/a/error_rel")+"</td><td></td></tr>");
					pw.println("    <tr><td>Current b (off/err-abs/err-rel)</td><td align=\"center\">"+Setting.getString("/bat/isotope/calc/current/b/offset")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/b/error_abs")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/b/error_rel")+"</td><td></td></tr>");
					pw.println("    <tr><td>Current iso (off/err-abs/err-rel)</td><td align=\"center\">"+Setting.getString("/bat/isotope/calc/current/iso/offset")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/iso/error_abs")+" μA</td><td>"+Setting.getString("/bat/isotope/calc/current/iso/error_rel")+"</td><td></td></tr>");
		    	}
				pw.println("    <tr><td>Dead time</td><td>"+(Setting.getDouble("/bat/isotope/calc/dead_time")*1000000)+" µs</td><td></td><td></td></tr>");
				pw.println("    <tr><td>Charge state</td><td>"+Setting.getString("/bat/isotope/calc/current/charge")+"+</td><td></td><td></td></tr>");
		    	String cl;
//				log.debug("Correction size"+data.corrList.size());
		    	for (int i=0; i<data.corrList.size(); i++) {
			    	if (i % 2 == 0) {
			    		cl="g";
			    	} else { cl=""; }
					Corr corr = data.corrList.get(i);
					pw.println("    <tr class=\""+cl+"\"><td>Correction: </td><td>"+(i+1)+" </td><td></td><td></td><td></td></tr>");
						try {
						pw.println("    <tr class=\""+cl+"\"><td></td><td>"+corr.firstRun+"</td><td align=\"center\">-</td><td>"+corr.lastRun+"</td><td></td></tr>");
						if (Setting.getDouble("/bat/isotope/calc/bg/factor")!=0.0 || Setting.getDouble("/bat/isotope/calc/bg/error")!=0.0) {
							pw.println("    <tr class=\""+cl+"\"><td>Isobar slope</td><td>"+corr.isoFact+"</td><td align=\"center\">±</td><td>"+corr.isoErr+"</td><td align=\"left\">"+Setting.getString("/bat/isotope/graph/bg/unit")+"</td></tr>");
						}
						if (corr.constBG!=0.0) {
							pw.println("    <tr class=\""+cl+"\"><td>Constant BG" + "</td><td>"+corr.constBG+"</td><td>±</td><td>"+corr.constBGErr+"</td><td align=\"left\">/s</td></tr>");
						}					
						if (corr.constBlWeight!=0.0) {
							pw.println("    <tr class=\""+cl+"\"><td>Constant Bl ("+corr.constBlWeight+" μg)</td><td>"+corr.constBlRatio+"</td><td>±</td><td>"+corr.constBlErr+"</td><td align=\"left\">"+unit+"</td></tr>");
						}
						if (corr.blank.ra_bg!=0.0 || corr.blank.ra_bg_err!=0.0) {
							pw.print("    <tr class=\""+cl+"\"><td>"+Setting.getString("/bat/isotope/tab_pane/tab3").replaceAll("<html>","").replaceAll("</html>","")+"</td><td>"+df.format(corr.blank.ra_bg));
							pw.print("</td><td align=\"center\">+-</td><td>"+df.format(corr.blank.ra_bg_err));
							if (!corr.blank.active) {pw.print("*");}
							pw.println("</td><td align=\"left\">"+unit+"</td></tr>");
						}
						pw.print("    <tr class=\""+cl+"\"><td>"+Setting.getString("/bat/isotope/tab_pane/tab4").replaceAll("<html>","").replaceAll("</html>","")+"</td><td>"+df.format(corr.std.std_ra));
						pw.print("</td><td align=\"center\">±</td><td>"+df.format(corr.std.std_ra_err));
						if (!corr.std.active) {pw.print("*");}
						pw.println("</td><td align=\"left\">"+unit+"</td></tr>");
						if (Setting.isotope.contains("C14")) {
							pw.print("    <tr class=\""+cl+"\"><td>"+Setting.getString("/bat/isotope/tab_pane/tab5").replaceAll("<html>","").replaceAll("</html>","")+"</td><td>"+df.format(corr.std.std_ba*100));
							pw.print("</td><td align=\"center\">±</td><td>"+df.format(corr.std.std_ba_err*100));
							if (!corr.blank.active) {pw.print("*");}
							pw.println("</td><td align=\"left\"> %</td></tr>");
						} else if (Setting.isotope.contains("P")) {
							pw.print("    <tr class=\""+cl+"\"><td>"+Setting.getString("/bat/isotope/tab_pane/tab5").replaceAll("<html>","").replaceAll("</html>","")+"</td><td>"+df.format(corr.std.std_ba));
							pw.print("</td><td align=\"center\">±</td><td>"+df.format(corr.std.std_ba_err));
							if (!corr.blank.active) {pw.print("*");}
							pw.println("</td><td align=\"left\">"+unit+"</td></tr>");
							pw.print("    <tr class=\""+cl+"\"><td>"+Setting.getString("/bat/isotope/tab_pane/tab8").replaceAll("<html>","").replaceAll("</html>","")+"</td><td>"+df.format(corr.std.std_rb));
							pw.print("</td><td align=\"center\">±</td><td>"+df.format(corr.std.std_rb_err));
							if (!corr.blank.active) {pw.print("*");}
							pw.println("</td><td align=\"left\">"+unit+"</td></tr>");
						}
					} catch (IllegalArgumentException e) {e.printStackTrace(System.out);}
					if (corr.timeCorr!=0.0) {
						pw.println("    <tr class=\""+cl+"\"><td>Time corr.</td><td>"+corr.timeCorr+"</td><td>%/h</td><td></td></tr>");
					}
					if (corr.a_slope!=0.0) {
						pw.println("    <tr class=\""+cl+"\"><td>Current a corr.</td><td>"+corr.a_slope+"</td><td>Off.</td><td>"+corr.a_slope_off+"</td><td align=\"left\"> %/μA</td></tr>");
					}
				}
		    	try {
		    		pw.println("    <tr class=\"top\"><td>Comment: </td><td colspan=\"3\" align=\"left\">"+data.comment.getText()+"</td></tr>");
		    	} catch (NullPointerException e) {;}
	    		pw.println("    <tr class=\"top\"><td colspan=\"5\"><i>* value set manually</i></td></tr>");
		    	pw.println("  </table>");
		    }
		    pw.println("</body>");
		    pw.println("</html>");
		    pw.close();
		    fs.close();
		    log.info("Saved data to XHTML-file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData! "+e);
		}
	}	
}
