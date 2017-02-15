import java.util.ArrayList;

public class FIFO implements CacheAlgorithm{
	
	static int cacheSize;
	final int FIRST_INDEX = 0;
	
	public FIFO(int cacheSize){
		this.cacheSize = cacheSize;
	}
	
	/*public FIFO(ArrayList<Integer> cache, int size){
		CACHE_SIZE = size;
		this.cache = cache;
	}*/

	@Override
	public void swap(ArrayList<Integer> cache, int item) {
		if (cache.size() >= cacheSize ){
			System.out.println("Cache is full, need to remove some items");
			remove(cache);
			
		}
		
		add(cache, item); // add either after one remove, if that was needed, else directly add
	}
	
	@Override
	public void add(ArrayList<Integer> cache, int item) {
		if (cache.size() < cacheSize ){
			cache.add(item);
		}
		else{
			System.err.println("Something went wrong before add because cache is full and we are trying to add to it");
		}
		
	}

	@Override
	public void remove(ArrayList<Integer> cache) {
		// TODO Auto-generated method stub
		System.out.println("Removing whoever is in front");
		int item = cache.remove(FIRST_INDEX);
		System.out.println("Removed " + item);
	}

}
