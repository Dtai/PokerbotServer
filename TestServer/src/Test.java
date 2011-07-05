import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Test {
	
	public static void main(String[] args){
		PrintWriter out = null;
		Socket socket;
		
		//Create socket connection
		try{
			  socket = new Socket("localhost", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		
		String startTable = 
					
		"{\"request\": {" + 
		     "\"type\": \"startTable\"," +  
		     "\"tableName\": one ," +
		     "\"nbPlayers\": 3 " +
		     "}" +
		"}";
		
		String fetchData = 
			
		"{\"request\": { " +
		    "\"type\": \"fetchData\"," +
		    "\"tableName\": one" +
		    "}" +
		"}";



			
			
		out.println(startTable);
	//	out.println(text);
	}


}
