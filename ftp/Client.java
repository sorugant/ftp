package ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	  public static void main(String [] args) throws Exception {
	      String serverName = "in-csci-rrpc01.cs.iupui.edu";
	      
	      //Get the port number
	      System.out.println("Please enter port number: ");
	      BufferedReader get_port = new BufferedReader(new InputStreamReader(System.in));
		  String port_str = get_port.readLine();
		  int port = Integer.parseInt(port_str);
	      
	      try {	         
	         //Register a user
	         System.out.println("New User? [Enter 0 if Yes. Enter 1 if No]: ");
	         BufferedReader con_reader = new BufferedReader(new InputStreamReader(System.in));
			 String login_str = con_reader.readLine();
			 int login = Integer.parseInt(login_str);			 	
			 
			 /*if (login == 0) {
				 System.out.println("Creating New User...");
				 createUser();
			 } */
			 
			 //Connecting to the server
	         System.out.println("Connecting to " + serverName + " on port " + port);
	         Socket client = new Socket(serverName, port);
	         
			 //create print writer for sending login to server
	         PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
	         
	         //open buffered reader for reading data from client
	         BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        	         
	         //Login the User
	         loginUser(output, input, con_reader, login);	
	         
	         //File transfer process
	         
	         //Request a file from the server
	         System.out.println("Please enter filename: ");
	         
	         String filename = con_reader.readLine();			 
			 System.out.println(filename);	
	         
	         
	         
	         int checkFTP;
        	 checkFTP = requestFile(filename, output, input, con_reader);
        	         	 
        	 //Re-send the request for file if received file was erroneous.
	         for(int i = 0; i < 5; i++) { 
	        	 
		         if(checkFTP == 1) {
		        	 checkFTP = requestFile(filename, output, input, con_reader);
		         } else {
		        	 System.out.println("File Transfer Successful!");
		        	 break;
		         }  
		         
		         //Print error in file transfer
        	 	 if(i == 4) {
        	 		 System.out.println("Error in Data Received. Trying again...");
        	 	}
	         }	         
	         
	         //Close all readers
	         con_reader.close();
	         output.close();
             input.close();
             
             //Close the client connection
             client.close();
	         
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	  }

	  
	  //loginUser method gets the user credentials and sends them to the server
	  public static void loginUser(PrintWriter output, BufferedReader input, BufferedReader con_reader, int login) throws IOException {
		  	 
		  	 //send the login type to the server (registration/authentication)
		  	 output.println(login);
		  
		  	 //prompt for user name
	         System.out.println("Enter User Name:");
	         String username = con_reader.readLine();
	         
	         //send user name to server
	         output.println(username);
	         
	         //prompt for password
	         System.out.println("Enter Password: ");
	         String password = con_reader.readLine();         
	         
	         //send password to server
	         output.println(password);	         
	         output.flush();
	         
	         //Get Login Attempt Result
	         String attempt = input.readLine();
	         System.out.println(attempt);
	         
	         if(attempt == null) {
	        	 System.out.println("Login failed...Closing Connection!!!");
	        	 output.close();
	             input.close();
	             System.exit(0);
	         } else {
	        	 return;
	         }
	  }
	  
	  	  
	  //requestFile does the FTP 
	  public static int requestFile(String filename, PrintWriter output, BufferedReader input, BufferedReader con_reader) throws Exception {
		  		         
			 //Send filename to server
	         output.println(filename);
	         output.flush();
	         	         
	         //Set a key
			 final String secretKey = "Here is the secret key!";
	         
	         //get encrypted filename From Server
	         String fileRcvd = input.readLine();
	         
	         //get checksum from the server
	         String checksumStr = input.readLine();
	         int checksum = Integer.parseInt(checksumStr);
	         
	         //Evaluate the Checksum 
	         int syndrome = ChecksumMethod.receive(fileRcvd, checksum);
	         
	         if (syndrome == 0) {
	             System.out.println("Data is received without error.");	  
	             
	             //Now decrypt the file if everything is right.
	             String decrypted = AES.decrypt(fileRcvd, secretKey);
				 System.out.println("File contents: " + decrypted);
				 
				 //Save the decrypted contents into a file
				 try (PrintWriter outFile = new PrintWriter("ftp/ClientText.txt")) {
					    outFile.println(decrypted);
				 }
				 return  0;
				 
	         } else {	
	        	 return 1;
	         }
	             
	  }
	    
}
