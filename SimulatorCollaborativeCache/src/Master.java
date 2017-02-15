import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class Master extends Thread{

	//ArrayList<Integer> clients;
	static ArrayList<ArrayList<Integer>> cacheArrays; // all clients caches stored here
	int maxClients;
	int currentClients;
	int clientCacheSize;
	int increaseBy;
	static int currentEndClient;
	Client clients[];
	Tick tickObj;

	public Master(int maxClients, int clientCacheSize, int increaseBy){
		this.maxClients = maxClients;
		this.clientCacheSize = clientCacheSize;
		this.increaseBy = increaseBy;
		currentEndClient = 1;
		tickObj = new Tick();
	}

	public static synchronized int getBlockFromClientCache(int clientId, int blockRequested){
		int index;
		for( index = 0; index < currentEndClient; index++ ){
			if ( index != clientId ){
				if ( cacheArrays.get(index).contains(blockRequested)){
					System.out.println("Master: For client "+ clientId + " Found block with client " + index);
					return index;
				}
			}
		}
		return -1;
	}
	
	public static synchronized int checkForBlockFromServerCache(int clientId, int blockRequested){

		return Server.getBlockFromServerCache(blockRequested);
		
	}

	public static synchronized int getBlockFromServerDisk(int clientId, int blockRequested){

		return 0;
		
	}

	public void run(){
		int index;
		//int startTick = Tick.ticker;
		//int currentTick = startTick;
		try{
			PrintWriter writerClientNumbers = new PrintWriter("clients.txt", "UTF-8");
			PrintWriter writerTimeTaken = new PrintWriter("times.txt", "UTF-8");
			
			writerClientNumbers.print("numberOfClients = [");
			writerTimeTaken.print("time = [");
			
			while(currentEndClient < maxClients){
				System.out.println("\n\n\n\n\n");
				System.out.println("Master: Current client end " + currentEndClient);
				
				//reset server cache
				Server.fillCache(clientCacheSize);
				//serverCache = new ArrayList<>();
				//fillCache(); // fill the server cache
				
				//reset all clients
				clients = new Client[currentEndClient];
				cacheArrays = new ArrayList<>();

				//create the clients
				for(index = 0; index < currentEndClient; index++){
					ArrayList<Integer> cacheForClient = new ArrayList<>();
					cacheArrays.add(cacheForClient);
					Client curClient = new Client(index, cacheForClient, clientCacheSize, this, currentEndClient);
					clients[index] = curClient;
					clients[index].fillCache();
					clients[index].showCache();
				}
				
				// start the clients
				for(index = 0; index < currentEndClient; index++){
					clients[index].startThread();
					clients[index].start();
				}
				
				Thread.sleep(2000);

				//create and use ticker, do the same in client as well
				//Need to save the ticker for every request

				//run for certain time - maybe based on ticks?
				//System.out.println("Master: Current tick " + currentTick + " other val " + ((startTick * currentEndClient * 1000) + 100));
				int ctr = 0;
				
				while ( ctr < 20 ){
					Thread.sleep(1000);
					ctr++;
				}
				
				
//				while ( currentTick < ((startTick * currentEndClient * 1000) + 100) ){
//					Thread.sleep(1000);
//					currentTick = Tick.ticker;
//				}
				
				System.out.println("MASTER: GOING TO ATTEMPT STOPPING CLIENTS");
				//stop all clients (i.e. join)
				for(index = 0; index < currentEndClient; index++){
					clients[index].tickObj.stopTickerFlag();
					clients[index].stopThread();
				}
				for(index = 0; index < currentEndClient; index++){
					clients[index].tickObj.join();
					clients[index].join();
				}
				
				System.out.println("MASTER: STOPPED ALL THE CLIENTS AND CHECKING");
				for(index = 0; index < currentEndClient; index++){
					System.out.println("MASTER: CLIENT " + index + " IS ALIVE " + clients[index].isAlive());
				}
				
				double averageTime = 0;
				for(index = 0; index < currentEndClient; index++){
					averageTime += clients[index].avgTime();
				}
				
				double avgVal = (averageTime/currentEndClient);
				
				System.out.println("MASTER: Total clients " + currentEndClient + " avg time " + averageTime + " average: " + (averageTime/currentEndClient));
				
				writerClientNumbers.print(currentEndClient + ", ");
				writerTimeTaken.print(String.format("%.2f", avgVal) + ", ");
				
				currentEndClient += increaseBy;
				System.out.println("Master: New end client number " + currentEndClient);
			}
			writerClientNumbers.print("]");
			writerTimeTaken.print("]");
			
			writerClientNumbers.close();
			writerTimeTaken.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		if ( args.length != 3){
			System.out.println("Master: Enter end client ID, client cache size and jump by. Example: 10 3 2");
		}
		else{
			int endClient = Integer.parseInt(args[0]);
			int cacheSize = Integer.parseInt(args[1]);
			int jumpBy = Integer.parseInt(args[2]);
			Tick ticker = new Tick();
			ticker.start();
			Master master = new Master(endClient, cacheSize, jumpBy);
			master.start();
			/*while(master.currentEndClient < master.maxClients){

			}*/
		}
	}
}
