import java.util.Comparator;


@SuppressWarnings("unchecked")
class SortLabel implements Comparator
{
	public int compare(Object aa, Object bb )
	{
		int result = 0;
		Run a = (Run)aa;
		Run b = (Run)bb;
		if ((result = a.sample.label.compareTo(b.sample.label)) == 0)
		{
			//If same last name, sort on second element
			try {
				result = a.run.compareTo(b.run);
			} catch (NullPointerException e) 
			{;}
		}			 
		return result;
	}
}
