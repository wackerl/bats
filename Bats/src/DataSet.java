import java.util.ArrayList;




/**
 * @author lukas
 *
 */
public interface DataSet
{
	/**
	 * @return cycles
	 * 
	 */
	abstract public Integer number();
	
	/**
	 * @return runtime
	 * 
	 */
	abstract public double runtime();
	
	/**
	 * @return active
	 * 
	 */
	abstract public boolean active();
	
	/**
	 * @return weight for calculating mean (normally runtime*a)
	 * 
	 */
	abstract public double weight();
	
	/**
	 * @param name
	 * @return cell
	 */
	abstract public Object get(String name);
	
	/**
	 * @param value
	 * @param name
	 */
	abstract public void set( Object value, String name );
	
	/**
	 * @param name
	 * @param sTag
	 * @param eTag
	 * @return Set of data by name
	 */
	public abstract String getSet(ArrayList<String> name, String sTag, String eTag);
	
	/**
	 * @param format
	 * @param type 
	 * @return Set of data by name for excel-xml
	 */
	public abstract String getSetEx(FormatT format, String type);
	
	/**
	 * @param value
	 * @param name
	 */
//	abstract public void setValues( ArrayList<String> value, String[] name);
	
	/**
	 * @param name
	 * @return
	 */
//	abstract public String getValues( String[] name );
	
//	abstract public Object getValue(String name);
}
