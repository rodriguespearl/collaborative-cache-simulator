import java.util.ArrayList;

public interface CacheAlgorithm {
	

	
	public void swap(ArrayList<Integer> cache, int item);
	public void remove(ArrayList<Integer> cache);
	public void add(ArrayList<Integer> cache, int item);
	
	public static boolean validCacheSize(ArrayList<Integer> cache, int CACHE_SIZE)
	{
		if ( cache.size() > CACHE_SIZE )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

}
