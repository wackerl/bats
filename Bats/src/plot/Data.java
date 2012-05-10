package plot;

/**
 * @author lukas
 *
 */
public class Data {
		/**
		 * 
		 */
		public Double y_sig;
		/**
		 * 
		 */
		public Double y_err;
		/**
		 * 
		 */
		public Double y_value;
		/**
		 * 
		 */
		public Double x_value;
	
		
		Boolean active;
		
		int p_err;
		int p_sig;
		int p_value;
		
		/**
		 * @param x 
		 * @param y 
		 * @param err 
		 * @param sig 
		 * @param active 
		 * 
		 */
		public Data(Double x, Double y, Double err, Double sig, Boolean active) {
			x_value = x;
			y_value = y;
			y_sig = sig;
			y_err = err;
			this.active = active;
		}

}
