import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Benchmark {
			
	static Scanner scan = new Scanner(System.in);
	
	static int nbPlayers;
	static int rulesUsed;
	static String tableName;
	
	static String ruleSet;
	final static String ruleSetBench =
		"do(raise(X0), 1) :- X0 is 10, rank(XRval), X1 =XRval, rank(XRval), X2 =XRval, handkaarten(X3), members([card(X1, _), card(X2, _)],X3). do(raise(X0), 2) :- X0 is 5, rank(XRval), X1 =XRval, rank(XRval), X2 =XRval, allekaarten(X3), members([card(X1, _), card(X2, _)],X3). do(fold, 3) :- tekort(X0), X1 is 6, (X0 > X1). do(call, 4) :- true.";
	final static String ruleSetPair = 
		"do(fold, 1) :- rank(XRval), X0 =XRval, rank(XRval), X1 =XRval, allekaarten(X2), members([card(X0, _), card(X1, _)],X2).";
	final static String ruleSetStraightFlush =
		"do(fold, 1) :- suit(XScolor), X0 = XScolor, rank(XRval), X1 =XRval, suit(XScolor), X2 = XScolor, rank(XRval), suit(XScolor), X4 = XScolor, rank(XRval), suit(XScolor), X6 = XScolor, rank(XRval), suit(XScolor), X8 = XScolor, rank(XRval), allekaarten(X10), members([card(X1, X0), card(X3, X2), card(X5, X4), card(X7, X6), card(X9, X8)],X10), X3 =:=XRval+1, X5 =:=XRval+2, X7 =:=XRval-1, X9 =:=XRval-2.";
	final static String ruleSetAceHigh = 
		"do(fold, 1) :- X0 =14, allekaarten(X1), members([card(X0, _)],X1).";
	final static String ruleSetBankroll = 
		"do(fold, 1) :- saldo(X0), X1 is 10, (X0 > X1).";
	final static String ruleSetFold = 
		"do(fold,1):- true.";

	public static void main(String args[]) throws Exception{
		
		System.out.println("How many players?");
		nbPlayers = scan.nextInt();
		System.out.println("What rulesset?");
		System.out.println("1) benchmark");
		System.out.println("2) pair");
		System.out.println("3) straight flush");
		System.out.println("4) ace high");
		System.out.println("5) bankroll > 10");
		System.out.println("6) fold");
		rulesUsed = scan.nextInt();
		
		System.out.println("Table name?");
		tableName = scan.next();
		
		final String startTableBench = 				
			"{\"request\": {" + 
			     "\"type\": \"startTable\"," +  
			     "\"tableName\": \"" + tableName +"\" ," +
			     "\"nbPlayers\": " + nbPlayers +" ," +
			     "\"password\": \"bench\"" +
			     "}" +
			"}";
		
		switch(rulesUsed){
		case 1: ruleSet = ruleSetBench; break;
		case 2: ruleSet = ruleSetPair; break;
		case 3: ruleSet = ruleSetStraightFlush; break;
		case 4: ruleSet = ruleSetAceHigh; break;
		case 5: ruleSet = ruleSetBankroll;break;
		case 6: ruleSet = ruleSetFold; break;
		
		default: 
			System.out.println("Please enter a number between 1 and 6.");
			throw new Exception("Please enter a number between 1 and 6.");
		}

		PrintWriter out = null;
		Socket socket = null;

		//Create socket connection
		socket = new Socket("borgraf", 20000);
		out = new PrintWriter(socket.getOutputStream(), true);
		
		
		//Start a table
		out.print(startTableBench);
		out.close();
		System.out.println("Inputstream closed. Table should be started.");
		
		Thread.sleep(5000);
		
		//Add players
		for(int i=0; i<nbPlayers; i++){
			socket = new Socket("borgraf", 20000);
			out = new PrintWriter(socket.getOutputStream(), true);
			out.print(addPlayer(i));
			out.close();
			
			//System.out.println(addPlayer(i));
		}
		
		//Set up a test table for every person
		
//		for(int i=0; i<nbPlayers; i++){
//			socket = new Socket("borgraf", 20000);
//			out = new PrintWriter(socket.getOutputStream(), true);
//			out.print(setupTesttable(i));
//			out.close();
//		}

	}
	
	public static String addPlayer(int i){
		String playerName = "Player" + i;
		return
			"{\"request\": { " +
		    "\"type\": \"joinTable\"," +
		    "\"tableName\":\"" + tableName +"\" ," +
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
