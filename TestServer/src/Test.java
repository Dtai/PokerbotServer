import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;


public class Test {
	
	public static void main(String[] args) throws InterruptedException, IOException{
		PrintWriter out = null;
		InputStream inputStream = null;
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



			
			
		out.println(startTable);
		out.close();
		System.out.println("inputstream closed");
		
		Thread.sleep(15000);
		System.out.println("waking up");
		
			  try {
				  socket = new Socket("localhost", 20000);
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		out.println(addBot);
		
		out.close();
		System.out.println("inputstream closed");
		
		Thread.sleep(15000);
		System.out.println("waking up");
		
			  try {
				  socket = new Socket("localhost", 20000);
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		out.println(fetchData);
		socket.shutdownOutput();
		
		try {
			//socket = new Socket("localhost", 20000);
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String reply = IOUtils.toString(inputStream);
		
		inputStream.close();
		
		System.out.println(reply);
		
		
	}


}
