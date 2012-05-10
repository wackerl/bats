import java.util.Comparator;


@SuppressWarnings("unchecked")
class SortPos implements Comparator {
	public int compare(Object aa, Object bb ){
		int result = 0;
		Integer a = (Integer)(((DataSet)aa).get("position"));
		Integer b = (Integer)(((DataSet)bb).get("position"));
		try {
			if ((result = a.compareTo(b)) == 0) {
				String a1 = (String) ((DataSet)aa).get("label");
				String b1 = (String)(((DataSet)bb).get("label"));
				result = a1.compareTo(b1);
				if ((result = a1.compareTo(b1)) == 0) {
					String a2 = (String) ((DataSet)aa).get("run");
					String b2 = (String)(((DataSet)bb).get("run"));
					result = a2.compareTo(b2);
				}
			}
		}  catch (NullPointerException e) {
			result =0;
		}
		return result;
	}
}
