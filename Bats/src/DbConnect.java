
/**
 * @author lukas
 *
 */
public interface DbConnect {
	
	/**
	 * @return if connected
	 * 
	 */
	abstract public Boolean isConn();

	/**
	 * @return selected magazine
	 * 
	 */
	abstract public String selectMagazine();

	/**
	 * @return latest magazine
	 * 
	 */
	abstract public String latestMag();

	/**
	 * @param run 
	 * @param active 
	 * @return if successful
	 *
	 */
	abstract public Boolean runTrue(Run run, Boolean active);

	/**
	 *
	 */
	abstract public void save();

	/**
	 *
	 */
	abstract public void saveAs();

	/**
	 *
	 */
	abstract public void openCalc();
	
	/**
	 * @return true if data is loaded
	 *
	 */
	abstract public boolean downloadRuns();

	/**
	 * 
	 *  
	 */
	abstract public void addRuns();

    /**
     * 
     */
    abstract public void logout();
    
 	/**
 	 * @return DbCycle
 	 * 
 	 */
 	abstract public DbCycle getConn();
 	
	/**
	 * @param run 
	 * @return updated run
	 */
	 abstract public Run updateRun(Run run);
	 
	/**
	 * @param magazine
	 * @return true if data is loaded
	 */
	 abstract public boolean downloadMag(String magazine);
}
