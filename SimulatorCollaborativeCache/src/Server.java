import java.util.ArrayList;
import java.util.Random;

public class Server {

	static int cacheSize;
	static ArrayList<Integer> serverCache;
	
	// method to initially fill the cache with random blocks
	public static void fillCache(int size){
		cacheSize = size;
		serverCache = new ArrayList<>();
		Random random = new Random();
		int upperBoundOnBlockGeneration = 500; //(currentEndClient * clientCacheSize) + 10;
		int block;
		while(serverCache.size() < cacheSize ){
			block = random.nextInt(upperBoundOnBlockGeneration)+1;
			if (!serverCache.contains(block)){
				serverCache.add(block);
			}
		}
		System.out.println("Server: SERVER CACHE: ");
		for ( int val:serverCache){
			System.out.print(val + " ");
		}
		System.out.println();
	}

	public static synchronized int getBlockFromServerCache(int blockRequested){
		
		if (serverCache.indexOf(blockRequested) != -1){
			System.out.println("Server: I have the block");
		}
		else{
			System.out.println("Server: I don't have the block");
		}
		
		return serverCache.indexOf(blockRequested);
	}
}
