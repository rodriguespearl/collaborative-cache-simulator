import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{

	int id;
	ArrayList<Integer> cache;
	boolean running;
	int cacheSize;
	int totalClients;
	Random random;
	int upperBoundOnBlockGeneration = 500;
	boolean needBlock;
	int blockNeeded;
	Master master;
	Tick tickObj;
	ArrayList<Double> time;
	FIFO cacheAlgorithm;
	
	
	public Client(int id, ArrayList<Integer> cache, int cacheSize, Master m, int totalClients){
		this.cache = cache;
		this.cacheSize = cacheSize;
		this.running = true;
		random = new Random();
		this.id = id;
		master = m;
		this.totalClients = totalClients;
		upperBoundOnBlockGeneration = 500;
		//upperBoundOnBlockGeneration = totalClients * cacheSize;
		needBlock = false;
		time = new ArrayList<>();
		
		cacheAlgorithm = new FIFO(this.cacheSize);
		
		try{
			tickObj = new Tick();
			tickObj.start();
		}
		catch ( Exception e){
			e.getMessage();
		}
		
	}
	
	public void resetCache(int id, ArrayList<Integer> cache, int totalClients){
		this.id = id;
		this.cache = cache;
		this.totalClients = totalClients;
		upperBoundOnBlockGeneration = 500;
		//upperBoundOnBlockGeneration = totalClients * cacheSize;
		needBlock = false;
	}
	
	public void startThread(){
		running = true;
	}
	
	public void stopThread(){
		running = false;
	}
	
	// method to initially fill the cache with random blocks
	public void fillCache(){
		//upperBoundOnBlockGeneration = (totalClients * cacheSize) + 10;
		upperBoundOnBlockGeneration = 500;
		System.out.println("Client "+ id + ": Current upper bound " + upperBoundOnBlockGeneration + " total clients " + totalClients + " cache size " + cacheSize);
		int block;
		//for ( int index = 0; index < cacheSize; index++ ){
		while(cache.size() < cacheSize ){
			block = random.nextInt(upperBoundOnBlockGeneration)+1;
			if (!cache.contains(block)){
				cache.add(block);
			}
		}
	}
	
	public void requestBlock(){
		int requestBlock = random.nextInt(upperBoundOnBlockGeneration)+1;
		System.out.println("Client "+ id + ": Generated request for " + requestBlock);
		
		if ( cache.contains(requestBlock)){
			System.out.println("Client "+ id + ": I have the block");
			needBlock = false;
		}
		else{
			System.out.println("Client "+ id + ": Need to check other client's caches");
			needBlock = true;
			setRequestBlock(requestBlock);
		}
	}
	
	public void setRequestBlock(int block){
		blockNeeded = block;
	}
	
	public int getRequestBlock(){
		return blockNeeded;
	}
	
	public void showCache(){
		System.out.print("Client "+id + ": Cache: ");
		for( int index = 0; index < cacheSize; index++ ){
			System.out.print(cache.get(index) + " ");
		}
		System.out.println();
	}
	
	public double avgTime(){
		double avg = 0;
		System.out.print("Client " + id + ": Time entires: ");
		for ( double num:time){
			System.out.print(num+ " ");
			avg += num;
		}
		avg/= time.size();
		System.out.println();
		System.out.println("Client " + id + ": Total requests: " + time.size() + " Average time: " + avg);
		return avg;
	}
	
	public void run(){
		//Make sure setCache is called before starting anything
		int startTime, endTime;
		while(running){
			try{
				Thread.sleep(2000); // pausing the thread for a bit
				startTime = tickObj.getTicker();
				System.out.println("Client "+ id + ": Generating request at time: " + startTime);
				showCache();
				requestBlock();
				if (needBlock){ //needBlock set only if the client does not have the block itself
					System.out.println("Client "+ id + ": Need to get the block from other clients of present");
					int request = Master.getBlockFromClientCache(id, blockNeeded);
					if ( request == -1 ){
						System.out.println("Client "+ id + ": Block is not present in any client, need to check server cache");
						tickObj.increaseTickerBy(1000);
						//Tick.ticker += 1000;
						//check server cache
						
						request = Master.checkForBlockFromServerCache(id, blockNeeded);
						if ( request >= 0 ){
							System.out.println("Client " + id + ": Block retrieved from server cache");
						}
						
						//retrieve from server disk
						else
						{
							//Tick.ticker += 2000; // adding more time for disk retrieval 
							tickObj.increaseTickerBy(2000);
							System.out.println("Client " + id + ": Block retrieved from server disk");
						}
						
						
					}
					System.out.println("Client "+ id + ": Adding the block to the cache");
					cacheAlgorithm.swap(this.cache, blockNeeded);
				}
				else{
					//System.out.println("Client "+ id + ": Got block from self at " + Tick.ticker);
				}
				
				endTime = tickObj.getTicker();//Tick.ticker;
				System.out.println("Client "+ id + ": End time " + endTime);
				Integer diff = endTime-startTime;
				time.add(diff.doubleValue());
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
