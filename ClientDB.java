package ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientDB {
	//Use a map to store/add clients accessing the Server
	static final Map<String, String> users = new HashMap<String,String>();
	
	//Initialize the HashMap with the CSV file values.
	static {		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("ftp/clientDB.csv"));
		
		    String line =  null;	    
		    while ((line = br.readLine()) != null) {
		        String[] parts = line.split(":", 2);
		        if (parts.length >= 2) {
		            String key = parts[0];
		            String value = parts[1];
		            users.put(key, value);
		        } else {
		            System.out.println("ignoring line: " + line);
		        }
		    }
		    
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getpwd(String username) {
		return users.get(username);
	}
	
	public static void printMap() {
		users.forEach((key, value) -> System.out.println(key + ":" + value));
	}
	
	public static void main(String args[]) throws IOException {
		createMap();
		printMap();
	}
	
	public static void createMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("ftp/clientDB.csv"));
	    String line =  null;	    
	    while ((line = br.readLine()) != null) {
	        String[] parts = line.split(":", 2);
	        if (parts.length >= 2) {
	            String key = parts[0];
	            String value = parts[1];
	            users.put(key, value);
	        } else {
	            System.out.println("ignoring line: " + line);
	        }
	    }
	    br.close();
	}
	
	public static void AddtoMap(String username, String password) throws IOException {
		 users.put(username, password);
		 //append the new user to the DB
		 try{
			 FileWriter fw = new FileWriter(new File("ftp/clientDB.csv"), true);
			 fw.write(System.lineSeparator());
			 fw.write(username+":"+password);
			 fw.close();
			    
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
}
