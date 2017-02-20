import java.util.ArrayList;
import java.util.Random;

public class FIFO extends CacheReplacementAlgorithm{

	static int cacheSize;
	final int FIRST_INDEX = 0;
	int upperBoundOnBlockGeneration;
	Random random;
	final String name = "FIFO";

	public FIFO(int cacheSize, int upperBoundOnBlockGeneration){
		this.cacheSize = cacheSize;
		this.upperBoundOnBlockGeneration = upperBoundOnBlockGeneration;
		random = new Random();
	}

	/*public FIFO(ArrayList<Integer> cache, int size){
		CACHE_SIZE = size;
		this.cache = cache;
	}*/

	// method to initially fill the cache with random blocks
	@Override
	public void initCache(int clientID, ArrayList<Integer> cache){
		int block;
		while(cache.size() < cacheSize ){
			block = random.nextInt(upperBoundOnBlockGeneration)+1;
			if (!cache.contains(block)){
				cache.add(block);
			}
		}
	}
	
	@Override
	public boolean checkForBlock(ArrayList<Integer> cache, int item){
		if (cache.contains(item)){
			return true;
		}
		return false;
	}

	@Override
	public void swap(ArrayList<Integer> cache, int item) {
		if (cache.size() >= cacheSize ){
			//System.out.println("CACHE ALGO: Cache is full, need to remove some items");
			SimulatorLogger.writeLog(name, "Cache is full, need to remove some items", infoLevel);
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
			SimulatorLogger.writeLog(name, "Something went wrong before add because cache is full "
					+ "and we are trying to add to it", severeLevel);
			System.err.println("CACHE ALGO: Something went wrong before add because cache is full and we are trying to add to it");
		}

	}

	@Override
	public void remove(ArrayList<Integer> cache) {
		//System.out.println("CACHE ALGO: Removing whoever is in front");
		SimulatorLogger.writeLog(name, "Removing whoever is in the front", infoLevel);
		int item = cache.remove(FIRST_INDEX);
		//System.out.println("CACHE ALGO: Removed " + item);
		SimulatorLogger.writeLog(name, "Removed " + item, infoLevel);
	}

}
