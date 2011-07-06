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
			  socket = new Socket("joske", 20000);
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
		
		String startTable2 = 
			
			"{\"request\": {" + 
			     "\"type\": \"startTable\"," +  
			     "\"tableName\": \"two\" ," +
			     "\"nbPlayers\": 4 " +
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
		
		String addBot2 = 
			
			"{\"request\": { " +
			    "\"type\": \"joinTable\"," +
			    "\"tableName\":\"two\" ," +
			    "\"id\": 3 , " +
			    "\"playerName\": \"Jonas\","+
			    "\"description\": \"do(call, 1) :- true.\" " +
			    "}" +
			"}";
		
		String addBot3 = 
			
			"{\"request\": { " +
			    "\"type\": \"joinTable\"," +
			    "\"tableName\":\"one\" ," +
			    "\"id\": 2 , " +
			    "\"playerName\": \"Jonas\","+
			    "\"description\": \"do(call, 1) :- true.\" " +
			    "}" +
			"}";

		
		String fetchData = 
			
		"{\"request\": { " +
		    "\"type\": \"fetchData\"," +
		    "\"tableName\": one" +
		    "}" +
		"}";
		
		String fetchData2 = 
			
			"{\"request\": { " +
			    "\"type\": \"fetchData\"," +
			    "\"tableName\": two" +
			    "}" +
			"}";



			
		//Start a table
		out.println(startTable);
		out.close();
		System.out.println("inputstream closed");
		
		Thread.sleep(5000);
		System.out.println("waking up");
		
		
		//Add a bot to the table
			  try {
				  socket = new Socket("joske", 20000);
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
		
		
		//Fetch data from the table
			  try {
				  socket = new Socket("joske", 20000);
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		out.println(fetchData);
		socket.shutdownOutput();
		
		try {
			//socket = new Socket("joske", 20000);
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String reply = IOUtils.toString(inputStream);
		
		inputStream.close();
		
		System.out.println(reply);
		
		Thread.sleep(5000);

		//Start up a second table
		try{
			  socket = new Socket("joske", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		out.println(startTable2);
		out.close();
		
		Thread.sleep(5000);

		//Add a user to the second table
		try{
			  socket = new Socket("joske", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		out.println(addBot2);
		out.close();
		
		Thread.sleep(5000);

		//Add a second user to the first table
		
		try{
			  socket = new Socket("joske", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		out.println(addBot3);
		out.close();
		Thread.sleep(5000);

		
		//Receive info from the second table
		
		try{
			  socket = new Socket("joske", 20000);
			  out = new PrintWriter(socket.getOutputStream(), true);
		}	
		catch(Exception e){}
		
		out.println(fetchData2);
		socket.shutdownOutput();
		
		try {
			//socket = new Socket("joske", 20000);
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reply = IOUtils.toString(inputStream);
		
		inputStream.close();
		
		System.out.println(reply);
		
		
	}


}
