import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;


/**
 * @author lukas
 * static class for saving files
 */
public class IOFile {
	final static Logger log = Logger.getLogger(IOFile.class);
	
	static String homeDir = Setting.homeDir;;
	protected static double MICRO_A = 6.24150948E12;
	
	/**
	 * @param file
	 * @param dataList
	 * @param format 
	 * @param sTag start tag
	 * @param eTag end tag
	 * @param sLine start line tag
	 * @param eLine end line tag
	 */
	@SuppressWarnings("unchecked")
	static public void save(String file, ArrayList dataList, FormatT format, String sTag, String eTag, String sLine, String eLine) {
		if (new File(file).renameTo(new File(new File(file)+".old"))) { 
			log.debug("Old file renamed!");
		}
	    PrintWriter pw;
	    
		if (new File(file).renameTo(new File(file+".old"))) { 
			log.debug("Old file renamed");
		}
		try{
			pw = new PrintWriter(new FileWriter(file));
		    pw.println(sLine+Setting.magazine+eLine);
		    pw.println(sLine+new Date().toString()+eLine);
		    String names = sLine;
			for (int i=0;i<format.colName.size();i++) {
				names += sTag+format.colName.get(i).replace("<html>","").replace("</html>","").replace("<br>","").replace("<br />","")
				.replace("<sup>","").replace("</sup>","").replace("<sub>","").replace("</sub>","")+eTag;
			}
			names+=eLine;
			pw.println(names);
		    for (int i=0; i<dataList.size(); i++){
//		    	if (((DataSet)dataList.get(i)).get("active").equals(true)) {
		    		pw.println(sLine+((DataSet)dataList.get(i)).getSet(format.colValue, sTag, eTag)+eLine);
//		    	}
		    }
		    pw.close();
		    log.info("Saved data to file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData!");
		}
	}
	
	/**
	 * @param file
	 * @param dataList
	 * @param format 
	 */
	@SuppressWarnings("unchecked")
	static public void saveExcel(String file, ArrayList dataList, FormatT format, Calc data) {
		if (new File(file).renameTo(new File(new File(file)+".old"))) { 
			log.debug("Old file renamed!");
		}
		if (new File(file).renameTo(new File(file+".old"))) { 
			log.debug("Old file renamed");
		}
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
					"    <Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Bold=\"1\"/>\n"+
					"    <Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/>\n"+
					"  </Style>\n"+
					"  <Style ss:ID=\"default\">\n"+
					"    <Font ss:FontName=\"Verdana\" ss:Size=\"10\"/>\n"+
					"    <Alignment ss:Vertical=\"Bottom\"/>\n"+
					"  </Style>\n"+
					"  <Style ss:ID=\"5\">\n"+
					"    <Font ss:FontName=\"Verdana\" ss:Size=\"12\" ss:Bold=\"1\"/>\n"+
					"    <Alignment ss:Vertical=\"Bottom\" />\n"+
					"  </Style>\n");
//			String 
			pw.println("   <Style ss:ID=\"bl\"><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#0000D4\"/></Style>");
			pw.println("   <Style ss:ID=\"nb\"><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#00611C\"/></Style>");
			pw.println("   <Style ss:ID=\"st\"><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#900000\"/></Style>");
			for (int i=0;i<format.colName.size();i++) {				
				pw.println("   <Style ss:ID=\"bl"+(i)+"\"> <NumberFormat ss:Format=\""+format.colFormat.get(i)+"\"/><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#0000D4\"/></Style>");
				pw.println("   <Style ss:ID=\"nb"+(i)+"\"> <NumberFormat ss:Format=\""+format.colFormat.get(i)+"\"/><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#00611C\"/></Style>");
				pw.println("   <Style ss:ID=\"st"+(i)+"\"> <NumberFormat ss:Format=\""+format.colFormat.get(i)+"\"/><Font ss:FontName=\"Verdana\" ss:Size=\"10\" ss:Color=\"#900000\"/></Style>");
				pw.println("   <Style ss:ID=\"sa"+(i)+"\"> <NumberFormat ss:Format=\""+format.colFormat.get(i)+"\"/><Font ss:FontName=\"Verdana\" ss:Size=\"10\" /></Style>");
			}
			pw.println("  </Styles>\n"+
					"<Worksheet ss:Name=\""+Setting.magazine+"\">\n"+
					"  <Table>");
			for (int i=0;i<format.colWidth.size();i++) {
				pw.println("   <Column ss:Index=\""+(i+1)+"\" ss:AutoFitWidth=\"0\" ss:Width=\""+format.colWidth.get(i)+"\"/>");
			}
			
			pw.println("   <Row ss:StyleID=\"5\">");
			pw.println("      <Cell><Data ss:Type=\"String\">"+Setting.magazine+"</Data></Cell>");
			pw.println("   </Row>");
			pw.println("   <Row />");
			pw.println("   <Row ss:StyleID=\"default\">");
			pw.println("      <Cell><Data ss:Type=\"String\">Bats version "+Setting.version+" written by L. Wacker</Data></Cell>");
			pw.println("   </Row>");
			pw.println("   <Row />");
			
			pw.println("   <Row ss:StyleID=\"0\">");
			String name;
			for (int i=0;i<format.colName.size();i++) {
				name=format.colName.get(i).replace("<html>","").replace("</html>","").replace("<br>"," ").replace("<br />"," ").replace("</sub>","</Sub>").replace("</sup>","</Sup>")
				.replace("<sub>","<Sub>").replace("<sup>","<Sup>");
				pw.println("      <Cell><Data ss:Type=\"String\">"+"<B xmlns=\"http://www.w3.org/TR/REC-html40\">"+name+"</B>"+"</Data></Cell>");
			}
			pw.println("   </Row>");
			String cl="";
			Boolean stTrue=false;
			Boolean blTrue=false;
			Boolean nbTrue=false;
		    for (int i=0; i<dataList.size(); i++){
		    	if (((DataSet)dataList.get(i)).get("active").equals(true)) {
			    	if (Setting.blankLabel.contains(((DataSet)dataList.get(i)).get("sampletype").toString().toLowerCase()) ) {
						cl="bl"; blTrue=true;
			    	} else if (((DataSet)dataList.get(i)).get("sampletype").toString().equalsIgnoreCase("nb")) {
						cl="nb"; nbTrue=true;
			    	} else if (data.stdNom.isStd(((DataSet)dataList.get(i)).get("sampletype").toString())){
			    		cl="st"; stTrue=true;
			    	} else {
						cl="sa";			    		
			    	}
		    		pw.println("   <Row>"+((DataSet)dataList.get(i)).getSetEx(format, cl)+"   </Row>\n");
		    	}
		    }
    		pw.println("   <Row><Cell><Data ss:Type=\"String\"></Data></Cell></Row>\n");
    		if(stTrue) pw.println("   <Row><Cell  ss:StyleID=\"st\"><Data ss:Type=\"String\">Used as standard</Data></Cell></Row>\n");
    		if(blTrue) pw.println("   <Row><Cell  ss:StyleID=\"bl\"><Data ss:Type=\"String\">Used as blank, no blank subtracted</Data></Cell></Row>\n");
    		if(nbTrue) pw.println("   <Row><Cell  ss:StyleID=\"nb\"><Data ss:Type=\"String\">No blank subtracted</Data></Cell></Row>\n");
			pw.println("  </Table>\n </Worksheet>\n</Workbook>");
		    pw.close();
		    fs.close();
		    log.info("Saved data to file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData!");
		}
	}
	
	/**
	 * @param file
	 * @param data
	 */
	public static void saveXML(String file, Calc data) {
		if (new File(file).renameTo(new File(new File(file)+".old"))) { 
			log.debug("Old file renamed!");
		}
		if (new File(file).renameTo(new File(file+".old"))) { 
			log.debug("Old file renamed");
		}
	    ArrayList<String> nameListRun = Setting.colSaveRun;
	    ArrayList<String> nameListSample = Setting.colSaveSample;
		try{
		    PrintWriter pw;
			FileOutputStream fs = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			pw = new PrintWriter(writer);
		    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		    pw.println("<bats_data isotope=\""+data.isotope+"\" date=\""+new Date().toString()+"\">");
		    pw.println("  <version>"+Setting.version+"</version>");
		    pw.println("  <parameter>");
		    pw.println("    <magazine>"+data.magazine+"</magazine>");
		    pw.println("    <firstRun>"+data.firstRun+"</firstRun>");
		    pw.println("    <lastRun>"+data.lastRun+"</lastRun>");
		    pw.println("    <a>");
		    pw.println("      <error_abs>"+data.a_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.a_errR+"</error_rel>");
		    pw.println("      <offset>"+data.a_off+"</offset>");
		    pw.println("    </a>");
		    pw.println("    <b>");
		    pw.println("      <error_abs>"+data.b_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.b_errR+"</error_rel>");
		    pw.println("      <offset>"+data.b_off+"</offset>");
		    pw.println("    </b>");
		    pw.println("    <iso>");
		    pw.println("      <error_abs>"+data.iso_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.iso_errR+"</error_rel>");
		    pw.println("      <offset>"+data.iso_off+"</offset>");
		    pw.println("    </iso>");
		    pw.println("    <isobar>"+data.isobar+"</isobar>");
		    pw.println("    <charge>"+data.charge+"</charge>");
		    pw.println("    <fract>"+Setting.getBoolean("/bat/isotope/calc/fract")+"</fract>");
		    pw.println("    <dead_time>"+data.deadtime+"</dead_time>");
		    pw.println("    <scatter>"+Setting.getDouble("/bat/isotope/calc/scatter")+"</scatter>");		    	
		    pw.println("    <poisson>"+Setting.getBoolean("/bat/isotope/calc/poisson")+"</poisson>");		    	
		    pw.println("    <weighting>"+Setting.getInt("/bat/isotope/calc/mean")+"</weighting>");		    	
		    pw.println("    <nom_ra>"+Setting.getDouble("bat/isotope/calc/nominal_ra")+"</nom_ra>");
		    pw.println("    <nom_ba>"+Setting.getDouble("bat/isotope/calc/nominal_ba")+"</nom_ba>");
		    pw.println("    <comment><![CDATA["+data.comment.getText()+"]]></comment>");
		    for (int i=0; i<data.corrList.size(); i++) {
		    	Corr corr = data.corrList.get(i);
			    pw.println("    <correction>");
			    pw.println("      <iso_factor>"+corr.isoFact+"</iso_factor>");
			    pw.println("      <iso_error>"+corr.isoErr+"</iso_error>");		    	
			    pw.println("      <bg_const>"+corr.constBG+"</bg_const>");		    	
			    pw.println("      <bg_const_err>"+corr.constBGErr+"</bg_const_err>");		    	
			    pw.println("      <bl_const>"+corr.constBlRatio+"</bl_const>");		    	
			    pw.println("      <bl_const_err>"+corr.constBlErr+"</bl_const_err>");		    	
			    pw.println("      <bl_mass>"+corr.constBlWeight+"</bl_mass>");		    	
			    pw.println("      <std_ra>"+corr.std.std_ra+"</std_ra>");		    	
			    pw.println("      <std_ra_err>"+corr.std.std_ra_err+"</std_ra_err>");		    	
			    pw.println("      <std_ba>"+corr.std.std_ba+"</std_ba>");		    	
			    pw.println("      <std_ba_err>"+corr.std.std_ba_err+"</std_ba_err>");		    	
			    pw.println("      <bl_ra>"+corr.blank.ra_bg+"</bl_ra>");		    	
			    pw.println("      <bl_ra_err>"+corr.blank.ra_bg_err+"</bl_ra_err>");		    	
			    pw.println("      <a>");
			    pw.println("        <slope>"+corr.a_slope+"</slope>");
			    pw.println("        <slope_off>"+corr.a_slope_off+"</slope_off>");
			    pw.println("      </a>");
			    pw.println("      <b>");
			    pw.println("        <slope>"+corr.b_slope+"</slope>");
			    pw.println("        <slope_off>"+corr.b_slope_off+"</slope_off>");
			    pw.println("      </b>");
			    pw.println("      <time_corr>"+corr.timeCorr+"</time_corr>");
			    pw.println("      <firstRun>"+corr.firstRun+"</firstRun>");
			    pw.println("      <lastRun>"+corr.lastRun+"</lastRun>");
			    pw.println("    </correction>");
		    }
		    pw.println("  </parameter>");
		    pw.println("  <sample>");
		    pw.print("    <name>");
			for (int i=0;i<nameListSample.size();i++) {
			    pw.print("<td>"+nameListSample.get(i)+"</td>");
			}
		    pw.println("</name>");
		    for (int i=0; i<data.sampleList.size(); i++) {
		    	pw.println("    <set nr=\""+i+"\">"+data.sampleList.get(i).getSet(nameListSample, "<td>", "</td>")+"</set>");
		    }
		    pw.println("  </sample>");
		    pw.println("  <data>");
		    pw.print("    <name>");
			for (int i=0;i<nameListRun.size();i++) {
			    pw.print("<td>"+nameListRun.get(i)+"</td>");
			}
		    pw.println("</name>");
		    for (int i=0; i<data.runListL.size(); i++) {
		    	pw.println("    <set nr=\""+i+"\">"+data.runListL.get(i).getSet(nameListRun, "<td>", "</td>")+"</set>");
		    }
		    pw.println("  </data>");
		    pw.println("</bats_data>");
		    pw.close();
		    fs.close();
			Setting.setLastFile(file);
		    log.info("Saved data to XHTML-file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData!");
		}
	}

	/**
	 * @param file
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public static void saveXMLOld(String file, Calc data) {
		if (new File(file).renameTo(new File(new File(file)+".old"))) { 
			log.debug("Old file renamed!");
		}
		if (new File(file).renameTo(new File(file+".old"))) { 
			log.debug("Old file renamed");
		}
	    ArrayList<String> nameList = Setting.colSaveRun;
		try{
		    PrintWriter pw;
			FileOutputStream fs = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(fs, "UTF-8");
			pw = new PrintWriter(writer);
		    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		    pw.println("<bats_data isotope=\""+data.isotope+"\" date=\""+new Date().toString()+"\">");
		    pw.println("  <version>"+Setting.version+"</version>");
		    pw.println("  <parameter>");
		    pw.println("    <magazine>"+data.magazine+"</magazine>");
		    pw.println("    <firstRun>"+data.firstRun+"</firstRun>");
		    pw.println("    <lastRun>"+data.lastRun+"</lastRun>");
		    pw.println("    <a>");
		    pw.println("      <error_abs>"+data.a_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.a_errR+"</error_rel>");
		    pw.println("      <offset>"+data.a_off+"</offset>");
		    pw.println("    </a>");
		    pw.println("    <b>");
		    pw.println("      <error_abs>"+data.b_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.b_errR+"</error_rel>");
		    pw.println("      <offset>"+data.b_off+"</offset>");
		    pw.println("    </b>");
		    pw.println("    <iso>");
		    pw.println("      <error_abs>"+data.iso_err+"</error_abs>");
		    pw.println("      <error_rel>"+data.iso_errR+"</error_rel>");
		    pw.println("      <offset>"+data.iso_off+"</offset>");
		    pw.println("    </iso>");
		    pw.println("    <isobar>"+data.isobar+"</isobar>");
		    pw.println("    <charge>"+data.charge+"</charge>");
		    pw.println("    <fract>"+Setting.getBoolean("/bat/isotope/calc/fract")+"</fract>");
		    pw.println("    <comment><![CDATA["+data.comment.getText()+"]]></comment>");
		    for (int i=0; i<data.corrList.size(); i++) {
		    	Corr corr = data.corrList.get(i);
			    pw.println("    <correction>");
			    pw.println("      <nom_ra>"+Setting.getDouble("bat/isotope/calc/nominal_ra")+"</nom_ra>");
			    pw.println("      <nom_ba>"+Setting.getDouble("bat/isotope/calc/nominal_ba")+"</nom_ba>");
			    pw.println("      <iso_factor>"+corr.isoFact+"</iso_factor>");
			    pw.println("      <iso_error>"+corr.isoErr+"</iso_error>");		    	
			    pw.println("      <bg_const>"+corr.constBG+"</bg_const>");		    	
			    pw.println("      <bg_const_err>"+corr.constBGErr+"</bg_const_err>");		    	
			    pw.println("      <bl_const>"+corr.constBlRatio+"</bl_const>");		    	
			    pw.println("      <bl_const_err>"+corr.constBlErr+"</bl_const_err>");		    	
			    pw.println("      <bl_mass>"+corr.constBlWeight+"</bl_mass>");		    	
			    pw.println("      <std_ra>"+corr.std.std_ra+"</std_ra>");		    	
			    pw.println("      <std_ra_err>"+corr.std.std_ra_err+"</std_ra_err>");		    	
			    pw.println("      <std_ba>"+corr.std.std_ba+"</std_ba>");		    	
			    pw.println("      <std_ba_err>"+corr.std.std_ba_err+"</std_ba_err>");		    	
			    pw.println("      <bl_ra>"+corr.blank.ra_bg+"</bl_ra>");		    	
			    pw.println("      <bl_ra_err>"+corr.blank.ra_bg_err+"</bl_ra_err>");		    	
			    pw.println("      <scatter>"+Setting.getDouble("/bat/isotope/calc/scatter")+"</scatter>");		    	
			    pw.println("      <a>");
			    pw.println("        <slope>"+corr.a_slope+"</slope>");
			    pw.println("        <slope_off>"+corr.a_slope_off+"</slope_off>");
			    pw.println("      </a>");
			    pw.println("      <b>");
			    pw.println("        <slope>"+corr.b_slope+"</slope>");
			    pw.println("        <slope_off>"+corr.b_slope_off+"</slope_off>");
			    pw.println("      </b>");
			    pw.println("      <time_corr>"+corr.timeCorr+"</time_corr>");
			    pw.println("      <firstRun>"+corr.firstRun+"</firstRun>");
			    pw.println("      <lastRun>"+corr.lastRun+"</lastRun>");
			    pw.println("    </correction>");
		    }
		    pw.println("  </parameter>");
		    pw.print("  <name>");
			for (int i=0;i<nameList.size();i++) {
			    pw.print("    <td nr=\""+i+"\">"+nameList.get(i)+"</td>");
			}
		    pw.println("</name>");
		    pw.println("  <data>");
		    for (int i=0; i<data.runListL.size(); i++) {
		    	pw.println("      <set nr=\""+i+"\">"+data.runListL.get(i).getSet(nameList, "<td>", "</td>")+"</set>");
		    }
		    pw.println("  </data>");
		    pw.println("</bats_data>");
		    pw.close();
		    fs.close();
			Setting.setLastFile(file);
		    log.info("Saved data to XHTML-file: "+file);
		} catch (IOException e) {
			log.error("Couldn't open file ("+file+") to colSaveBat runData!");
		}
	}

	/**
	 * @param file 
	 * Opens file with XML settings and data
	 * @param main 
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	static public void openXML(File file, Bats main) throws JDOMException, IOException {
		Calc data;
	    Document doc;
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(file);
		String value;
		Double version=null;
		String isotope = ((Element)XPath.selectSingleNode(doc, "bats_data")).getAttributeValue("isotope");
		if (!isotope.equalsIgnoreCase(Setting.isotope)) {
			String message = String.format( "The isotope of the file ("+isotope+") is not equal to "+Setting.isotope+"!");
		    JOptionPane.showMessageDialog( null, message );
		} else {
//		try {
//			main.data = Setting.initCalcIso(Setting.isotope);			
//		} catch (NullPointerException e) {
//			String message = String.format( "The isotope is not defined in file "+file+"!");
//		    JOptionPane.showMessageDialog( null, message );
//			log.error(message);
//			String isotope = Setting.setIsotope();
//			main.data = Setting.initCalcIso(isotope);	
//		}
			data = main.data;
			try {
				version = Double.valueOf(((Element)XPath.selectSingleNode(doc, "bats_data/version")).getText().split(" ")[0]);
				log.debug("File was created with version: "+version);
				// get parameter
				data.magazine = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/magazine")).getText();
				Setting.setMagazine(data.magazine);
				data.firstRun = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/firstRun")).getText();
				data.lastRun = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/lastRun")).getText();
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/a/error_abs")).getText();
				data.a_err = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/a/error_abs")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/a/error_rel")).getText();
				data.a_errR = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/a/error_rel")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/a/offset")).getText();
				data.a_off = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/a/offset")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/b/error_abs")).getText();
				data.b_err = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/b/error_abs")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/b/error_rel")).getText();
				data.b_errR = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/b/error_rel")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/b/offset")).getText();
				data.b_off = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/b/offset")).setText(value);
				data.isobar = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/isobar")).getText();
			} catch (NullPointerException e) {
				String message = String.format( "File "+file+" has no proper settings!");
			    JOptionPane.showMessageDialog( null, message );
				log.error(message);
				log.error(e);
			}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/comment")).getText();
				data.comment.setText(value);
			} catch (NullPointerException e) {;}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/fract")).getText();
				((Element)Setting.getElement("/bat/isotope/calc/fract")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/poisson")).getText();
				((Element)Setting.getElement("/bat/isotope/calc/poisson")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/weighting")).getText();
				((Element)Setting.getElement("/bat/isotope/calc/mean")).setText(value);
			} catch (NullPointerException e) { ; }
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/iso/error_abs")).getText();
				data.iso_err = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/iso/error_abs")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/iso/error_rel")).getText();
				data.iso_errR = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/iso/error_rel")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/iso/offset")).getText();
				data.iso_off = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/iso/offset")).setText(value);
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/charge")).getText();
				data.charge = Integer.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/current/charge")).setText(value);
			} catch (NullPointerException e) {
				log.debug("old version of bats-file!");
			}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/dead_time")).getText();
				data.deadtime = Double.valueOf(value);
				((Element)Setting.getElement("/bat/isotope/calc/dead_time")).setText(value);
			} catch (NullPointerException e) {
				log.info("Could not get dead time!");
			}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/nom_ra")).getText();
				((Element)Setting.getElement("bat/isotope/calc/nominal_ra")).setText(value);
			} catch (NullPointerException e) {
				log.info("Could not get nominal value for ra!");
			}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/nom_ba")).getText();
				((Element)Setting.getElement("bat/isotope/calc/nominal_ba")).setText(value);
			} catch (NullPointerException e) {
				log.info("Could not get nominal value for ba!");
			} catch (NumberFormatException e) {
				log.info("Could not get nominal value for ba!");
			}
			try {
				value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/scatter")).getText();
				((Element)Setting.getElement("/bat/isotope/calc/scatter")).setText(value);
			} catch (NullPointerException e) {
				try {
					value = ((Element)XPath.selectSingleNode(doc, "bats_data/parameter/correction/scatter")).getText();
					((Element)Setting.getElement("/bat/isotope/calc/scatter")).setText(value);
				} catch (NullPointerException ex) {
					log.info("Could not get scatter!");
				}
			}
			// get correction
			List<Element> corrEL = (List<Element>)XPath.selectNodes(doc, "bats_data/parameter/correction");
			ArrayList<Corr> corrL = new ArrayList<Corr>();
			for (int i=0; i<corrEL.size(); i++) {
				Corr corr = data.newCorrection();
				Element corrE = corrEL.get(i);
				try {
					corr.firstRun = corrE.getChild("firstRun").getText();
				} catch (NullPointerException e) {
					corr.firstRun= data.firstRun;
				}
				try {
					corr.lastRun = corrE.getChild("lastRun").getText();
				} catch (NullPointerException e) {
					corr.lastRun= data.lastRun;
				}
				try {
					value = corrE.getChild("iso_factor").getText();				
					corr.isoFact = Double.valueOf(value);
					if (corr.isoFact.isInfinite() || corr.isoFact.isNaN()) {
						corr.isoFact=0.0;
						log.warn("isoFact was not correct in file!");
					}
					((Element)Setting.getElement("/bat/isotope/calc/bg/factor")).setText(corr.isoFact.toString());
					value = corrE.getChild("iso_error").getText();
					corr.isoErr = Double.valueOf(value);
					if (corr.isoErr.isInfinite() || corr.isoErr.isNaN()) {
						corr.isoErr=0.0;
						log.warn("isoErr was not correct in file!");
					}
					((Element)Setting.getElement("/bat/isotope/calc/bg/error")).setText(corr.isoErr.toString());
					corr.std.active=false;
					corr.blank.active=false;
					value = corrE.getChild("std_ra").getText();
					corr.std.std_ra = Double.valueOf(value);
					value = corrE.getChild("std_ra_err").getText();
					corr.std.std_ra_err = Double.valueOf(value);
					value = corrE.getChild("std_ba").getText();
					corr.std.std_ba = Double.valueOf(value);
					value = corrE.getChild("std_ba_err").getText();
					corr.std.std_ba_err = Double.valueOf(value);
					value = corrE.getChild("bl_ra").getText();
					corr.blank.ra_bg = Double.valueOf(value);
					value = corrE.getChild("bl_ra_err").getText();
					corr.blank.ra_bg_err = Double.valueOf(value);
					value = corrE.getChild("bl_ra_err").getText();
					corr.blank.ra_bg_err = Double.valueOf(value);
					value = corrE.getChild("bl_ra_err").getText();
					corr.blank.ra_bg_err = Double.valueOf(value);
				} catch (NumberFormatException e) {
					log.warn("no complete blank and std values available!");
				}
				try {
					value = corrE.getChild("a").getChild("slope").getText();
					log.debug("slope: "+value);
					corr.a_slope = Double.valueOf(value);
				} catch (NullPointerException e) {
					log.info("Could not get slope of a!");
				}
				try {
					value = corrE.getChild("a").getChild("slope_off").getText();
					corr.a_slope_off = Double.valueOf(value);
				} catch (NullPointerException e) {
					log.info("Could not get slope offset of a!");
				}
				try {
					value = corrE.getChild("b").getChild("slope").getText();
					corr.b_slope = Double.valueOf(value);
				} catch (NullPointerException e) {
					log.info("Could not get slope of b!");
				}
				try {
					value = corrE.getChild("b").getChild("slope_off").getText();
					corr.b_slope_off = Double.valueOf(value);
				} catch (NullPointerException e) {
					log.info("Could not get slope offset of b!");
				}
				try {
					value = corrE.getChild("time_corr").getText();
					corr.timeCorr = Double.valueOf(value);			
				} catch (NullPointerException e) {
					corr.timeCorr = 0.0;
					log.info("Could not get time correction!");
				}
				catch (NumberFormatException e) {
					log.info("Could not get scatter! Set to 0!");
					((Element)Setting.getElement("/bat/isotope/calc/scatter")).setText("0.0");
				}
				try {
					value = corrE.getChild("bg_const").getText();
					corr.constBG= Double.valueOf(value);
					value = corrE.getChild("bg_const_err").getText();
					corr.constBGErr= Double.valueOf(value);
					value = corrE.getChild("bl_const").getText();
					corr.constBlRatio= Double.valueOf(value);
					value = corrE.getChild("bl_const_err").getText();
					corr.constBlErr= Double.valueOf(value);
					value = corrE.getChild("bl_mass").getText();
					corr.constBlWeight= Double.valueOf(value);
				} catch (NullPointerException e) {
					log.info("Old bats file: could not get constant bg and bl! (-> 0+-0)");
					corr.constBG=0.0;
					corr.constBGErr=0.0;
					corr.constBlRatio=0.0;
					corr.constBlErr=0.0;
					corr.constBlWeight=0.0;
				}
				corrL.add(corr);
			}
			if (version>=2.5) {
				// get names
				List<Element> nameElement = (List<Element>)XPath.selectNodes(doc, "bats_data/sample/name/td");
				ArrayList<String> nameSample = new ArrayList<String>();
				int labelIndex=0;
				for (int i=0; i<nameElement.size(); i++) {
					nameSample.add(nameElement.get(i).getText());
					if (nameElement.get(i).getText().equalsIgnoreCase("label")) {
						labelIndex=i;
					}
				}
				log.debug("Label index: "+labelIndex);
				log.debug(nameSample);
				List<Element> dataElement = (List<Element>)XPath.selectNodes(doc, "bats_data/sample/set");
				log.debug("Size of samples: "+dataElement.size());
				List sampleValues;
				for (int i=0; i<dataElement.size(); i++) {
		//			log.debug("Label: "+((Element)dataElement.get(i).getChildren().get(labelIndex)).getText());
					Sample sample = data.setSample(((Element)dataElement.get(i).getChildren().get(labelIndex)).getText());
					sampleValues = dataElement.get(i).getChildren();
					for (int j=0; j<sampleValues.size(); j++){
						sample.setValue(((Element)sampleValues.get(j)).getText(), nameSample.get(j));
					}
				}
				// get names
				nameElement = (List<Element>)XPath.selectNodes(doc, "bats_data/data/name/td");
				ArrayList<String> name = new ArrayList<String>();
				labelIndex=0;
				for (int i=0; i<nameElement.size(); i++) {
					name.add(nameElement.get(i).getText());
					if (nameElement.get(i).getText().equalsIgnoreCase("label")) {
						labelIndex=i;
					}
				}
	//			log.debug("Label index: "+labelIndex);
				log.debug(name);
				dataElement = (List<Element>)XPath.selectNodes(doc, "bats_data/data/set");
				log.debug("Size of runs: "+dataElement.size());
				for (int i=0; i<dataElement.size(); i++) {
		//			log.debug("Label: "+((Element)dataElement.get(i).getChildren().get(labelIndex)).getText());
					Run tempData = new Run( data.setSample(((Element)dataElement.get(i).getChildren().get(labelIndex)).getText() ) );
					tempData.setRunSet( dataElement.get(i).getChildren(), name );
					data.runListL.add(tempData);
				}
			} else {		
				// get names
				List<Element> nameElement = (List<Element>)XPath.selectNodes(doc, "bats_data/name/td");
				ArrayList<String> name = new ArrayList<String>();
				int labelIndex=0;
				for (int i=0; i<nameElement.size(); i++) {
					name.add(nameElement.get(i).getText());
					if (nameElement.get(i).getText().equalsIgnoreCase("label")) {
						labelIndex=i;
					}
				}
				log.debug("Label index: "+labelIndex);
				log.debug(name);
				List<Element> dataElement = (List<Element>)XPath.selectNodes(doc, "bats_data/data/set");
				log.debug("Size of runs: "+dataElement.size());
				for (int i=0; i<dataElement.size(); i++) {
		//			log.debug("Label: "+((Element)dataElement.get(i).getChildren().get(labelIndex)).getText());
					Run tempData = new Run( data.setSample(((Element)dataElement.get(i).getChildren().get(labelIndex)).getText() ) );
					tempData.setRunSet( dataElement.get(i).getChildren(), name );
					data.runListL.add(tempData);
				}
			}
			data.initData(corrL);
			log.debug("Initialised samples ("+data.sampleList.size()+").");
			log.debug("Initialised runs ("+data.runListL.size()+").");
			log.debug("Initialised runs ("+data.runListR.size()+").");
			data.calcSet = file.getName().substring(0,file.getName().length()-5);
	    }
	}

	/**
	 * @param file
	 * @param data 
	 */
	static public void openDBXL(File file, Calc data) {
		Scanner input = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		File setFil = new File(Setting.batDir+"/"+Setting.isotope+"/file_io/dbxl.xml");
		try
		{
			doc = builder.build(setFil);
			List<Element> list = doc.getRootElement().getChildren();
			ArrayList<String> names = new ArrayList<String>();
			String[] values;
			int labIndex = 0;
			for (int i=0; i<list.size(); i++) {
				names.add(list.get(i).getText());
				if (list.get(i).getText().equalsIgnoreCase("label")) {
					labIndex=i;
				}
			}
			try {
				input = new Scanner( file );
			}
			catch ( FileNotFoundException e) {
				String message = String.format( "Could not open file %s!", file );
				JOptionPane.showMessageDialog( null, message );
				log.debug(message);
			}
			try {
				while ( input.hasNextLine() ){
					if (input.hasNext( "#" )){
						input.nextLine();
					}
					else{
						values = input.nextLine().split(",");
						Run tempData = new Run( data.setSample(values[labIndex]) );
						tempData.setValues(values, names);
						data.runListL.add(tempData);
					}
				}
				log.debug("dbxl-file successfully read!");
			}
			catch ( NoSuchElementException elementException){
				String message = String.format( "<html>Could not open file:<br>File improperly formed!</html>");
				log.error("Could not open file: File improperly formed!");
				JOptionPane.showMessageDialog( null, message );
			}
		} catch (JDOMException e1) {
			log.debug("dbxl-settings file could not be read: "+setFil);
		} catch (IOException e1) {
			log.debug("dbxl-settings file could not be opend."+setFil);
		}
	}

//	/**
//	 * @param file
//	 * @param data 
//	 */
//	@SuppressWarnings("unchecked")
//	static public void openHVold(File file, Calc data) {
//		try {
//			data = Setting.initCalcIso(Setting.isotope);			
//		} catch (NullPointerException e) {
//			String message = String.format( "The isotope is not defined in file "+file+"!");
//		    JOptionPane.showMessageDialog( null, message );
//			log.error(message);
//			String isotope = Setting.setIsotope();
//			data = Setting.initCalcIso(isotope);	
//		}
//		
//		ArrayList<Run> runData = new ArrayList<Run>();
//		Scanner input = null;
//		SAXBuilder builder = new SAXBuilder();
//		Document doc;
//		File setFil = new File(Setting.batDir+"/"+Setting.isotope+"/hv.xml");
//		try
//		{
//			doc = builder.build(setFil);
//			List<Element> list;
//			list = doc.getRootElement().getChildren();
//			ArrayList<String> names = new ArrayList<String>();
//			int labIndex=0;
//			for (int i=0; i<list.size(); i++) {
//				names.add(list.get(i).getText());
//				if (list.get(i).getText().equalsIgnoreCase("label")) {
//					labIndex=i;
//				}
//			}
//			try {
//				input = new Scanner( file );
//				input.nextLine();
//			}
//			catch ( FileNotFoundException e) {
//				String message = String.format( "Could not open file %s!", file );
//				JOptionPane.showMessageDialog( null, message );
//				log.debug(message);
//			}
//			int j=0;
//			String[] line;
//			try {
//				while ( input.hasNextLine() ){
//					if (input.hasNext( "#" )){
//						input.nextLine();
//					}
//					else{
//						log.debug(++j);
//						line = input.nextLine().split("\\t");
//						Run tempData = new Run(data.getSample(line[labIndex]));
//						tempData.setValues(line, names);
//						data.runListL.add(tempData);
//					}
//				}
//				log.debug("hv-file successfully read!");
//			}
//			catch ( NoSuchElementException elementException){
//				String message = String.format( "<html>Could not open file:<br>File improperly formed!</html>");
//				log.error("Could not open file: File improperly formed!");
//				JOptionPane.showMessageDialog( null, message );
//			}
//		} catch (JDOMException e1) {
//			log.debug("hv-settings file could not be read: "+setFil);
//		} catch (IOException e1) {
//			log.debug("hv-settings file could not be opend."+setFil);
//		}
//		data.initData(new ArrayList<Corr>());
//		data.calcSet = file.getName().substring(0,file.getName().length()-5);
//	}


	/**
	 * @param file
	 * @param data 
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	static public Boolean openHV(File file, Calc data) {
		int charge=Setting.getInt("/bat/isotope/calc/current/charge");
		
		ArrayList<Run> cycleData;
		Scanner input = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		File setFil = new File(Setting.batDir+"/"+Setting.isotope+"/file_io/hv.xml");
		try
		{
			doc = builder.build(setFil);
			List<Element> listCycle;			
			List<Element> listRun;
			
			listRun = doc.getRootElement().getChild("run").getChildren();
			ArrayList<String> nameRunList = new ArrayList<String>();
			ArrayList<String> nameRunSetList = new ArrayList<String>();
			for (int i=0; i<listRun.size(); i++){
				nameRunList.add(listRun.get(i).getText());
				nameRunSetList.add(listRun.get(i).getAttributeValue("name"));
			}

			listCycle = doc.getRootElement().getChild("cycle").getChildren();
			ArrayList<Integer> nameCycleList = new ArrayList<Integer>();
			ArrayList<String> nameCycleSetList = new ArrayList<String>();
			for (int i=0; i<listCycle.size(); i++){
				nameCycleList.add(Integer.decode(listCycle.get(i).getText()));
				nameCycleSetList.add(listCycle.get(i).getAttributeValue("name"));
			}

			try {
				String mag = file.getName();
				int fn = 1;
				File file2 = new File(file.toString()+"/"+fn+".res");
				Run run;
				while (file2.exists()) {
					log.debug(file2);
					input = new Scanner( file2 );
					String[] line;
					ArrayList<String> param = new ArrayList<String>();
					ArrayList<String> value = new ArrayList<String>();
					Run lastData;
					int labIndex=0;
					try {
						while ( !input.hasNext( "\\u005BRESULTS\\u005D") ) {
							String temp = input.nextLine();
//							log.debug(temp);
							line = temp.split(": ");
							for (int i=0; i<listRun.size(); i++) {
								if (line[0].startsWith(nameRunList.get(i))) {
									value.add(line[1].trim());
									param.add(nameRunSetList.get(i));
									if (nameRunSetList.get(i).equalsIgnoreCase("label")) {
										labIndex=value.size()-1;
									}
								} 
							}
						}	
						Run tempData = new Run(data.setSample(value.get(labIndex)));
						for (int i=0; i<value.size(); i++) {
							tempData.setValue(value.get(i), param.get(i));
						}
						while ( !input.hasNext( "\\u005BBLOCK") ) {
							input.nextLine();					
						}	
						input.nextLine();					
						input.nextLine();					
						cycleData = new ArrayList<Run>();
						while (input.hasNextLine()) {
							String temp = input.nextLine();
//							log.debug(temp);
							line = temp.split(" ");
//						    line = input.nextLine().split("\\t");
							if (line.length>listCycle.size()) {
								for (int i=0; i<listCycle.size(); i++) {
//									log.debug(fn);
//									log.debug(nameCycleSetList.get(i));
//									log.debug(line[nameCycleList.get(i)]);
									tempData.setValue(line[nameCycleList.get(i)],nameCycleSetList.get(i));
								}
								try {tempData.ra = tempData.r / (tempData.a * tempData.runtime * MICRO_A / charge);}
								catch (NullPointerException e) {tempData.ra = null;}	
								try {tempData.ba = tempData.b / tempData.a ;}
								catch (NullPointerException e) {tempData.ba = null;}	
								try {tempData.rb = tempData.r / (tempData.b * tempData.runtime * MICRO_A / charge);}
								catch (NullPointerException e) {tempData.rb = null;}	
								cycleData.add(tempData);
								lastData = tempData;
								tempData = new Run(null/*data.getSample(lastData.label)*/);
								tempData.runtime = lastData.runtime;
								tempData.runtime_a = lastData.runtime_a;
								tempData.runtime_b = lastData.runtime_b;
								
							} else {
								log.info("Input has too short line!");
							}
						}
						NumberFormatter nf = new NumberFormatter(new DecimalFormat("000"));
						run = Func.meanCycle(cycleData, 0);
						run.run = mag+"-"+nf.valueToString(fn);
						run.sample.magazine = mag;
						data.runListL.add(run);
						log.debug("hv-file read! ("+data.runListL.size()+" runs)");
						log.debug("Run info "+run.run+"-"+run.sample.label+"-"+run.cycles);
					}
					catch ( NoSuchElementException elementException){
						String message = String.format( "<html>Could not open file:<br>File improperly formed!</html>");
						log.error("Could not open file: File improperly formed!");
						JOptionPane.showMessageDialog( null, message );
					} catch (ParseException e) {
						log.debug("Parse exception formatting run field.");
					}
					fn++;
					file2 = new File(file.toString()+"/"+fn+".res");
				}
			}
			catch ( FileNotFoundException e) {
				String message = String.format( "Could not open file '%s'!", file );
				JOptionPane.showMessageDialog( null, message );
				log.debug(message);
			}
			data.calcSet = file.getName();
			return true;
		} catch (JDOMException e1) {
			log.debug("hv-settings file could not be read: "+setFil);
			return false;
		} catch (IOException e1) {
			log.debug("hv-settings file could not be opend."+setFil);
			return false;
		}
	}

	/**
	 * @param file
	 * @param data 
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	static public Boolean openNEC(File file, Calc data) {
//		int charge=Setting.getInt("/bat/isotope/calc/current/charge");
		
//		ArrayList<Run> cycleData;
		Scanner input = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		Integer preLines;
		File setFil = new File(Setting.batDir+"/"+Setting.isotope+"/file_io/nec.xml");
		try {
			doc = builder.build(setFil);
			List<Element> listRun;			
			List<Element> listSample;
			
			int sampleIndex = Integer.valueOf(doc.getRootElement().getChild("sample").getChild("index").getText());
			int runIndex = Integer.valueOf(doc.getRootElement().getChild("run").getChild("index").getText());
			Integer pass = Integer.valueOf(doc.getRootElement().getChild("run").getChild("pass").getText());
			
			listSample = doc.getRootElement().getChild("sample").getChildren("val");
			ArrayList<Integer> nameSampleList = new ArrayList<Integer>();
			ArrayList<String> nameSampleSetList = new ArrayList<String>();
			for (int i=0; i<listSample.size(); i++){
				nameSampleList.add(Integer.valueOf(listSample.get(i).getText()));
				nameSampleSetList.add(listSample.get(i).getAttributeValue("name"));
			}
			preLines = doc.getRootElement().getChild("run").getAttribute("pre").getIntValue();
			log.debug(preLines);
			listRun = doc.getRootElement().getChild("run").getChildren();
			ArrayList<Integer> nameRunList = new ArrayList<Integer>();
			ArrayList<String> nameRunSetList = new ArrayList<String>();
			for (int i=0; i<listRun.size(); i++){
				nameRunList.add(Integer.valueOf(listRun.get(i).getText()));
				nameRunSetList.add(listRun.get(i).getAttributeValue("name"));
			}

//			try {
			String mag = file.getName();
			String[] lineArr;
			File file2 = new File(file.toString()+"/runlist");
			Sample sample;
			if (file2.exists()) {
				log.debug(file2);
				input = new Scanner( file2 );
				input.findWithinHorizon("# cathode",0);
				while  (!(input.findWithinHorizon("cat ",0)==null)) {
					lineArr= input.nextLine().split("\\s+");
					sample = data.setSample(lineArr[sampleIndex]);
					log.debug(sample.label);
					if (sample==null) {
						log.error("Sample "+lineArr[sampleIndex]+" does not exist!");
					} 
					for (int i=0; i<nameSampleList.size(); i++) {
						sample.setValue(lineArr[nameSampleList.get(i)],nameSampleSetList.get(i));
						log.debug(lineArr[nameSampleList.get(i)]+"--"+nameSampleSetList.get(i));
					}
//					data.sampleList.add(sample);
//					log.debug(lineArr);
//					log.debug(lineArr.length);
				}
				input.close();
			}
				
			File file3 = new File(file.toString()+"/runlog");
			Run run;
			String line;
			if (file3.exists()) {
				log.debug(file3);
				input = new Scanner( file3 );
//					String[] line;
//					ArrayList<String> param = new ArrayList<String>();
//					ArrayList<String> value = new ArrayList<String>();
//					Run lastData;
//					int labIndex=0;
				for (int i=0; i<(preLines-1);i++) {
					input.nextLine();
				}
				char[] underLine = input.nextLine().toCharArray();
				ArrayList<Integer> lenList = new ArrayList<Integer>();
				lenList.add(-1);
				for (int i=0; i<underLine.length; i++) {
					if (underLine[i]!='=') {
						lenList.add(i);
//						log.debug(i);
					}
				}
//				lenList.add(underLine.length);
				lineArr = new String[lenList.size()-1];
				Integer runNumber=1;
				while  (input.hasNextLine()) {
					line = input.nextLine();
//					log.debug(line.length());
					for (int i=0; i<(lenList.size()-1); i++) {
//						log.debug((lenList.get(i)+1)+"--"+(lenList.get(i+1)));
						lineArr[i]=line.substring(lenList.get(i)+1,lenList.get(i+1)).trim();
//						log.debug(lineArr[i]);
					}
					sample = data.getSamplePos(Integer.valueOf(lineArr[runIndex]));
					run = new Run(sample);
					for(int i=0;i<nameRunList.size();i++) {
//						log.debug(nameRunList.get(i)+"--"+nameRunSetList.get(i)+"--"+lineArr[nameRunList.get(i)]);
						run.setValue(lineArr[nameRunList.get(i)],nameRunSetList.get(i));
					}
					run.setValue(((Integer)(10000*pass+runNumber++)).toString(), "run");
					run.sample.magazine = mag;
					data.runListL.add(run);
				}
				input.close();
			}
			return true;
		} catch (JDOMException e1) {
			log.debug("nec-settings file could not be read: "+setFil);
			return false;
		} catch (IOException e1) {
			log.debug("nec-settings file could not be opend."+setFil);
			return false;
		}
	}
}
