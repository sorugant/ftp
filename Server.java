package ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server implements Runnable{
	  
    //Socket connection
    Socket conn;
	Server(Socket sock) {
		// store the socket for the connection
		this.conn = sock; 
	}
		
	public static void main(String[] args) throws IOException {	
		
		//Get the port number
		System.out.println("Please enter port number: ");
        BufferedReader con_reader = new BufferedReader(new InputStreamReader(System.in));
		String port_str = con_reader.readLine();
		int port = Integer.parseInt(port_str);
		
		ServerSocket serverSocket = new ServerSocket(port);		
		System.out.println("Waiting for client on port: " + port);
				
		while (true) {
			//Get a connection from client
			Socket conn = serverSocket.accept();	
			new Thread(new Server(conn)).start();					
		}
	}
	
	public void run() {
		try {
			//open buffered reader for reading data from client
			BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//open print writer for writing data to client
			PrintWriter output = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));
			
			//Get the User credentials from the client
	        String loginStr = input.readLine();
	        int login = Integer.parseInt(loginStr);
			String username = input.readLine();
			String password = input.readLine();
			
			if (login == 0) {
				 System.out.println("Creating New User...");
				 ClientDB.AddtoMap(username, password);
				 System.out.println("Welcome, " + username);	
				 output.println("Registration Success!");
				 output.flush();
				 
			 } else if (login == 1) {
				//Authenticate the User credentials
				System.out.println("Authenticating...");				
				authentication(conn, output, username, password);
				
			 } else {
				 System.out.println("Please enter an appropriate login value.");
				 conn.close();
			 }
				
						
			//Get filename from client
			String filename;
			while((filename = input.readLine()) != null) {
				
				System.out.println("Filename: " + filename);
				File file = new File(filename);
				
				//Process the transfer if file exists
				if (file.exists()) {
					BufferedReader fileReader = new BufferedReader(new FileReader(file));	
					StringBuffer stringBuffer = new StringBuffer();
					String content = null;
					
					//read the file 
					while ((content = fileReader.readLine()) != null) {
						stringBuffer.append(content).append(" ");						
					}
					
					String fileContent = stringBuffer.toString();
					
					//Set a key
					final String secretKey = "Here is the secret key!";
					
					//Encrypt the file
					String encrypted = AES.encrypt(fileContent, secretKey);
					
					//Generate Checksum
					int checksum = ChecksumMethod.generateChecksum(encrypted);
					
					//Implement Byzantine behavior
					Random random = new Random();
					int randomInteger = random.nextInt(100);
					
					//Corrupt the file based on byzantine event
					if(randomInteger > 80) {
						encrypted = "Adding byzantine behavior!!!!" + encrypted;
					}
										
					//Send encryption to client
					output.println(encrypted);
					
					//Send checksum to client
					output.println(checksum);
					output.flush();
					
					System.out.println("File has been sent...");
				
				} else {
					output.println("File Not Found");
					output.flush();
					
					//Close the socket connection, as file does not exist. 
					conn.close();
				}
			}
			
			//Close the connection with the client
			conn.close();
			
		} catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//authentication method validates the user credentials
	public void authentication(Socket server, PrintWriter output, String username, String password) throws IOException {
		
		//Get the password from the DB with the given user name
		String pwd = (ClientDB.users).get(username);	
		
		//Validate password against the given user name
		if(password.equals(pwd)) {			
			System.out.println("Welcome, " + username);	
			output.println("Login Success!");
		} else {
			System.out.println("Login failed...Closing Connection!!!");
			output.println("Login failed...Closing Connection!!!");
			server.close();
		} 	
		output.flush();
		
	}	
}
