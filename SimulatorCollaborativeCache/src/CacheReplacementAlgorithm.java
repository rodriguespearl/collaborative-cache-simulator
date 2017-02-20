import java.util.ArrayList;

public abstract class CacheReplacementAlgorithm {
	
	public static String infoLevel = "INFO";
	public static String severeLevel = "SEVERE";
	
	public abstract void initCache(int clientID, ArrayList<Integer> cache);
	public abstract void swap(ArrayList<Integer> cache, int item);
	public abstract void remove(ArrayList<Integer> cache);
	public abstract void add(ArrayList<Integer> cache, int item);
	public abstract boolean checkForBlock(ArrayList<Integer> cache, int item);
	
	/*public static boolean validCacheSize(ArrayList<Integer> cache, int CACHE_SIZE)
	{
		if ( cache.size() > CACHE_SIZE )
		{
			return false;
		}
		else
		{
			return true;
		}
	}*/
}
