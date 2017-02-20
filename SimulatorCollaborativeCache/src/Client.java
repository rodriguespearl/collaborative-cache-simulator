import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{

	int id;
	ArrayList<Integer> cache;
	boolean running;
	int cacheSize;
	int totalClients;
	Random random;
	int upperBoundOnBlockGeneration;
	boolean needBlock;
	int blockNeeded;
	Master master;
	Tick tickObj;
	ArrayList<Double> time;
	//FIFO cacheAlgorithm;
	String algorithm;
	CacheReplacementAlgorithm algorithmObj;
	String name;
	
	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";
	
	
	public Client(int id, ArrayList<Integer> cache, int cacheSize, Master m, int totalClients, String runningAlgo){
		this.name = "CLIENT" + " " + id;
		this.cache = cache;
		this.cacheSize = cacheSize;
		this.running = true;
		random = new Random();
		this.id = id;
		master = m;
		this.totalClients = totalClients;
		this.upperBoundOnBlockGeneration = Constants.upperBoundOnBlockGeneration;
		//upperBoundOnBlockGeneration = 500;
		//upperBoundOnBlockGeneration = totalClients * cacheSize;
		needBlock = false;
		time = new ArrayList<>();
		
		algorithm = runningAlgo;
		algorithm = algorithm.toUpperCase();
		
		switch(algorithm){
		case "FIFO":
			algorithmObj = new FIFO(cacheSize, this.upperBoundOnBlockGeneration);
			break;
		case "LIFO":
			algorithmObj = new LIFO(cacheSize, this.upperBoundOnBlockGeneration);
			break;
		case "LRU":
			algorithmObj = new LRU(cacheSize, this.upperBoundOnBlockGeneration);
			break;
		case "MRU":
			algorithmObj = new MRU(cacheSize, this.upperBoundOnBlockGeneration);
			break;
		case "RANDOM":
			algorithmObj = new RANDOM(cacheSize, this.upperBoundOnBlockGeneration);
			break;
		default:
			//System.out.println("CLIENT: INVALID ALGORITHM");
			SimulatorLogger.writeLog(name, "INVALID ALGORITHM", severeLevel);
			break;
		}
		
		algorithmObj.initCache(id, cache); // method to initially fill the cache with random blocks
		
		//cacheAlgorithm = new FIFO(this.cacheSize);
		
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
		//upperBoundOnBlockGeneration = 500;
		//upperBoundOnBlockGeneration = totalClients * cacheSize;
		needBlock = false;
	}
	
	public void startThread(){
		running = true;
	}
	
	public void stopThread(){
		running = false;
	}
	
	
	
	public void requestBlock(){
		int requestBlock = random.nextInt(upperBoundOnBlockGeneration)+1;
		//System.out.println("Client "+ id + ": Generated request for " + requestBlock);
		SimulatorLogger.writeLog(name, "Generated request for " + requestBlock, infoLevel);
		
		boolean foundBlock = algorithmObj.checkForBlock(cache, requestBlock);
		
		if (!foundBlock){
			needBlock = true;
			//System.out.println("Client "+ id + ": Need to check other client's caches");
			SimulatorLogger.writeLog(name, "Need to check other client's caches", infoLevel);
			setRequestBlock(requestBlock);
		}
		else{
			needBlock = false;
			//System.out.println("Client "+ id + ": I have the block");
			SimulatorLogger.writeLog(name, "I have the block", infoLevel);
		}
	}
	
	public void setRequestBlock(int block){
		blockNeeded = block;
	}
	
	public int getRequestBlock(){
		return blockNeeded;
	}
	
	public void showCache(){
		/*System.out.print("Client "+id + ": Cache: ");
		for( int index = 0; index < cacheSize; index++ ){
			System.out.print(cache.get(index) + " ");
		}
		System.out.println();*/
	}
	
	public double avgTime(){
		double avg = 0;
		//System.out.print("Client " + id + ": Time entires: ");
		for ( double num:time){
			//System.out.print(num+ " ");
			avg += num;
		}
		avg/= time.size();
		//System.out.println();
		//System.out.println("Client " + id + ": Total requests: " + time.size() + " Average time: " + avg);
		SimulatorLogger.writeLog(name, "Total requests: " + time.size() + " Average time: " + avg, infoLevel);
		return avg;
	}
	
	public void run(){
		//Make sure setCache is called before starting anything
		int startTime, endTime;
		while(running){
			try{
				Thread.sleep(2000); // pausing the thread for a bit
				startTime = tickObj.getTicker();
				
				//System.out.println("Client "+ id + ": Generating request at time: " + startTime);
				SimulatorLogger.writeLog(name, "Generating request at time: " + startTime, infoLevel);
				
				//showCache();
				requestBlock();
				tickObj.increaseTickerBy(Constants.tick_increase_own_cache);
				
				if (needBlock){ //needBlock set only if the client does not have the block itself
					//System.out.println("Client "+ id + ": Need to get the block from other clients of present");
					SimulatorLogger.writeLog(name, "Need to get the block from other clients of present", infoLevel);
					
					int request = Master.getBlockFromClientCache(id, blockNeeded);
					tickObj.increaseTickerBy(Constants.tick_increase_other_client);
					if ( request == -1 ){
						//System.out.println("Client "+ id + ": Block is not present in any client, need to check server cache");
						SimulatorLogger.writeLog(name, "Block is not present in any client, need to check server cache", infoLevel);
						
						tickObj.increaseTickerBy(Constants.tick_increase_server_cache);
						
						request = Master.checkForBlockFromServerCache(id, blockNeeded);
						if ( request >= 0 ){
							//System.out.println("Client " + id + ": Block retrieved from server cache");
							SimulatorLogger.writeLog(name, "Block retrieved from server cache", infoLevel);
						}
						
						//retrieve from server disk
						else
						{
							//Tick.ticker += 2000; // adding more time for disk retrieval 
							tickObj.increaseTickerBy(Constants.tick_increase_server_disk);
							//System.out.println("Client " + id + ": Block retrieved from server disk");
							SimulatorLogger.writeLog(name, "Block retrieved from server disk", infoLevel);
						}
						
						
					}
					//System.out.println("Client "+ id + ": Adding the block to the cache");
					SimulatorLogger.writeLog(name, "Adding the block to the cache", infoLevel);
					
					algorithmObj.swap(this.cache, blockNeeded); // adding newly retrieved block to own cache
				}
				
				endTime = tickObj.getTicker();//Tick.ticker;
				//System.out.println("Client "+ id + ": End time " + endTime);
				SimulatorLogger.writeLog(name, "End time " + endTime, infoLevel);
				Integer diff = endTime-startTime;
				time.add(diff.doubleValue());
			}
			catch ( Exception e)
			{
				SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
				e.printStackTrace();
			}
		}
	}
}
