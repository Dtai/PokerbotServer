import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Test {
	
	public static void main(String[] args) throws InterruptedException{
		PrintWriter out = null;
		Socket socket = null;
		
		//Create socket connection
		try{
			  socket = new Socket("localhost", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		
		String startTable = 
					
		"{\"request\": {" + 
		     "\"type\": \"startTable\"," +  
		     "\"tableName\": \"one\" ," +
		     "\"nbPlayers\": 3 " +
		     "}" +
		"}";
		
		String addBot = 
			
		"{\"request\": { " +
		    "\"type\": \"joinTable\"," +
		    "\"tableName\":\"one\" ," +
		    "\"id\": 1 , " +
		    "\"playerName\": \"Kwinten\","+
		    "\"description\": \"do(call, 1) :- true.\" " +
		    "}" +
		"}";

		
		String fetchData = 
			
		"{\"request\": { " +
		    "\"type\": \"fetchData\"," +
		    "\"tableName\": one" +
		    "}" +
		"}";



			
			
		//out.println(startTable);
		//out.close();
		System.out.println("flushed");
		
		/*Thread.sleep(15000);
		System.out.println("waking up");
		
		try{
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}*/
		
		out.println(addBot);
	}


}
