package Uotel;

import java.util.Scanner;
//import java.sql.*;
//import java.io.*;

public class Driver {
	
	public static void main(String[] args){
	    System.out.println("Welcome to Uotel!");

	    
	    try {
	    	Connector con = new Connector();
	    	System.out.println("Now connected to database");
		
	    	Scanner scanner = new Scanner(System.in);
		
	    	// Login / register // login variable contains the login field of the currently logged in user
	    	String login = User.loginRegister(con.statement);

		
	    	//TODO: Here is where the rest of the project will go
	    	loop: while (true) {
	    		System.out.println("Enter command: ($command for a list of available commands)");
	    		String command = scanner.nextLine();
	    		switch (command) {
	    			// list of commands
	    		case "$command":
	    			System.out.println("Commands:");
	    			System.out.println("$h - register a new temporary home");
	    			System.out.println("$exit - exit the application");
	    		case "$h":
	    			TemporaryHome.newTH(con.statement, login);
	    		case "$exit":
	    			break loop;
	    		default:
	    			System.out.println("Invalid command.");
	    		}
	    			
	    	}
	    	con.closeConnection();
	    	scanner.close();
	    } 
	    catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
}
