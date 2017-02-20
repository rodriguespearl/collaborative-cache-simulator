import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Master extends Thread{

	static ArrayList<ArrayList<Integer>> cacheArrays; // all clients caches stored here
	static int currentEndClient;
	Client clients[];
	Tick tickObj;
	String algorithm;
	static String name;
	
	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";

	public Master(String algo){
		name = "MASTER";
		currentEndClient = 1;
		tickObj = new Tick();
		algorithm = new String();
		algorithm = algo;
	}

	public static synchronized int getBlockFromClientCache(int clientId, int blockRequested){
		int index;
		for( index = 0; index < currentEndClient; index++ ){
			if ( index != clientId ){
				if ( cacheArrays.get(index).contains(blockRequested)){
					//System.out.println("Master: For client "+ clientId + " Found block with client " + index);
					SimulatorLogger.writeLog(name, "For client "+ clientId + " Found block with client " + index, infoLevel);
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
		try{
			
		PrintWriter writerClientNumbers = new PrintWriter("clientsFIFO.txt", "UTF-8");
		
		PrintWriter writerTimeTaken = new PrintWriter("timesFIFO.txt", "UTF-8");
		
		writerClientNumbers.print("numberOfClients = [");
		writerTimeTaken.print("time = [");
		
		while(currentEndClient < Constants.endClient){
			//System.out.println("\n\n\n\n\n");
			//System.out.println("Master: Current client end " + currentEndClient);
			SimulatorLogger.writeLog(name, "\n\n\n\n\nCurrent client end " + currentEndClient, infoLevel);
			
			//reset server cache
			Server.fillCache(Constants.serverCachSize);
			
			//reset all clients
			clients = new Client[currentEndClient];
			cacheArrays = new ArrayList<>();

			//create the clients
			for(index = 0; index < currentEndClient; index++){
				ArrayList<Integer> cacheForClient = new ArrayList<>();
				cacheArrays.add(cacheForClient);
				Client curClient = new Client(index, cacheForClient, Constants.clientCacheSize, this, currentEndClient, algorithm);
				clients[index] = curClient;
				//clients[index].fillCache();
				clients[index].showCache();
			}
			
			// start the clients
			for(index = 0; index < currentEndClient; index++){
				clients[index].startThread();
				clients[index].start();
			}
			
			Thread.sleep(2000);

			int ctr = 0;
			
			while ( ctr < 20 ){
				Thread.sleep(1000);
				ctr++;
			}
			
			//System.out.println("MASTER: GOING TO ATTEMPT STOPPING CLIENTS");
			SimulatorLogger.writeLog(name, "GOING TO ATTEMPT STOPPING CLIENTS", infoLevel);
			for(index = 0; index < currentEndClient; index++){
				clients[index].tickObj.stopTickerFlag();
				clients[index].stopThread();
			}
			for(index = 0; index < currentEndClient; index++){
				clients[index].tickObj.join();
				clients[index].join();
			}
			
			//System.out.println("MASTER: STOPPED ALL THE CLIENTS AND CHECKING");
			SimulatorLogger.writeLog(name, "STOPPED ALL THE CLIENTS AND CHECKING", infoLevel);
			
			for(index = 0; index < currentEndClient; index++){
				//System.out.println("MASTER: CLIENT " + index + " IS ALIVE " + clients[index].isAlive());
				SimulatorLogger.writeLog(name, "CLIENT " + index + " IS ALIVE " + clients[index].isAlive(), severeLevel);
			}
			
			double averageTime = 0;
			for(index = 0; index < currentEndClient; index++){
				averageTime += clients[index].avgTime();
			}
			
			double avgVal = (averageTime/currentEndClient);
			
			SimulatorLogger.writeLog(name, "Total clients " + currentEndClient + " avg time " 
							+ averageTime + " average: " + (averageTime/currentEndClient), infoLevel);
			//System.out.println("MASTER: Total clients " + currentEndClient + " avg time " + averageTime + " average: " + (averageTime/currentEndClient));
			
			writerClientNumbers.print(currentEndClient + ", ");
			writerTimeTaken.print(String.format("%.2f", avgVal) + ", ");
			
			currentEndClient += Constants.increaseBy;
			
			//System.out.println("Master: New end client number " + currentEndClient);
			SimulatorLogger.writeLog(name, "New end client number " + currentEndClient, infoLevel);
		}
		writerClientNumbers.print("]");
		writerTimeTaken.print("]");
		
		writerClientNumbers.close();
		writerTimeTaken.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
			e.printStackTrace();
		} catch (InterruptedException e) {
			SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
			Constants.setConstants(); // setting constants from the properties file
			Tick ticker = new Tick();
			ticker.start();
			Master master = new Master(Constants.algo);
			master.start();
		
	}
}
