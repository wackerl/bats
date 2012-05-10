import java.util.Comparator;


@SuppressWarnings("unchecked")
class SortRun implements Comparator
{
	public int compare(Object aa, Object bb )
	{
		int result = 0;
		Run a = (Run)aa;
		Run b = (Run)bb;
		try {
			result = a.run.compareTo(b.run);	
		} catch (NullPointerException e) {
			;
		}
		return result;
	}
}
