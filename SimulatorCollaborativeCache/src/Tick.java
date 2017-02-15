
public class Tick extends Thread{

	int ticker = 0;
	static boolean runTicker = true;

	public Tick(){
		//runTicker = true;
		ticker = 0;
	}
	
	public void increaseTickerBy(int num){
		ticker += num;
	}

	public int getTicker(){
		return ticker;
	}

	public void setTickerFlag(){
		runTicker = true;
	}

	public void stopTickerFlag(){
		runTicker = false;
	}

	public void run(){
		while(runTicker){
			try{
				Thread.sleep(100);
				ticker++;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
