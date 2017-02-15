import java.io.IOException;
import java.io.PrintWriter;

public class Test{
	public static void main(String args[]){
		String s = String.format("%.2f", 3000.090909090909);
		System.out.println(s);
		/*try{
		    PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
		    writer.print("The first line");
		    writer.println("The second line");
		    writer.close();
		} catch (IOException e) {
		   // do something
		}*/
		/*
		Tick ticker1 = new Tick();
		Tick ticker2 = new Tick();
		ticker2.setTickerFlag();
		ticker2.start();
		System.out.println("One: "+ticker2.getTicker());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Two: "+ticker2.getTicker());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Three: "+ticker2.getTicker());*/
	}
}
