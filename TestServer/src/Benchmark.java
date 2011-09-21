import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Benchmark {
		
	static Scanner scan = new Scanner(System.in);
	
	final static int nbPlayers = scan.nextInt();
	
	final static String ruleSet =
		"do(raise(X0), 1) :- X0 is 10, rank(XRval), X1 =XRval, rank(XRval), X2 =XRval, handkaarten(X3), members([card(X1, _), card(X2, _)],X3). do(raise(X0), 2) :- X0 is 5, rank(XRval), X1 =XRval, rank(XRval), X2 =XRval, allekaarten(X3), members([card(X1, _), card(X2, _)],X3). do(fold, 3) :- tekort(X0), X1 is 6, (X0 > X1). do(call, 4) :- true.";

	public static void main(String args[]) throws UnknownHostException, IOException, InterruptedException{
		
		final String startTableBench = 				
				"{\"request\": {" + 
				     "\"type\": \"startTable\"," +  
				     "\"tableName\": \"bench\" ," +
				     "\"nbPlayers\": " +nbPlayers +" ," +
				     "\"password\": \"bench\"" +
				     "}" +
				"}";

		PrintWriter out = null;
		InputStream inputStream = null;
		Socket socket = null;

		//Create socket connection
		socket = new Socket("borgraf", 20000);
		out = new PrintWriter(socket.getOutputStream(), true);
		
		
		//Start a table
		out.print(startTableBench);
		out.close();
		System.out.println("Inputstream closed. Table should be started.");
		
		Thread.sleep(3000);
		
		//Add players
		for(int i=0; i<nbPlayers; i++){
			socket = new Socket("borgraf", 20000);
			out = new PrintWriter(socket.getOutputStream(), true);
			out.print(addPlayer(i));
			out.close();
			
			//System.out.println(addPlayer(i));
		}
		
		//Set up a test table for every person
		
		for(int i=0; i<nbPlayers; i++){
			socket = new Socket("borgraf", 20000);
			out = new PrintWriter(socket.getOutputStream(), true);
			out.print(setupTesttable(i));
			out.close();
		}

	}
	
	public static String addPlayer(int i){
		String playerName = "Player" + i;
		return
			"{\"request\": { " +
		    "\"type\": \"joinTable\"," +
		    "\"tableName\":\"bench\" ," +
		    "\"playerName\": \"" + playerName +"\" ,"+
		    "\"description\": \"" + ruleSet + "\"" +
		    "}" +
		    "}";
	}
	
	public static String setupTesttable(int i){
		String tableName = "Test" + i;
		return 
			"{\"request\": {" + 
		     "\"type\": \"startTable\"," +  
		     "\"tableName\":\"" + tableName + "\" ," +
		     "\"nbPlayers\": 3 ," +
		     "\"password\": bench" +
		     "}" +
		     "}";
	}
}
