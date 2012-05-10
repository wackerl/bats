import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author lukas
 *
 */
public class Magazine
{
	final static Logger log = Logger.getLogger(Magazine.class);
	/**
	 * 
	 */
	public String runStart;
	
	/**
	 * 
	 */
	public String runEnd;
	
	/**
	 * 
	 */
	public String magazine;
	
	/**
	 * 
	 */
	public Date timeDat;
	
	Magazine( String run1, String run2, String magazine, String timeDatOra)
	{
		runStart=run1;
		runEnd=run2;
		this.magazine=magazine;
		SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy kk:mm:ss" );
		try {
			timeDat=df.parse(timeDatOra);
		} catch (ParseException e) {
			log.error("Could not parse timeDat in magazine: "+timeDatOra);
		}
//		log.debug("magazine set: "+magazine+"/"+runStart+"/"+runEnd+"/"+timeDat);
	}
	
//	/**
//	 * @return Start run
//	 */
//	public String getRunStart()
//	{
//		return runStart;
//	}
//	
//	/**
//	 * @return end Run
//	 */
//	public String getRunEnd()
//	{
//		return runEnd;
//	}
//	
//	/**
//	 * @return Magazine
//	 */
//	public String getMagazine()
//	{
//		return magazine;
//	}
//	/**
//	 * @return time
//	 */
//	public String getTime()
//	{
//		return timeDat;
//	}
}
