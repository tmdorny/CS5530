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
	    	String login = User.loginRegister(con.statement, scanner);

	    	//TODO: Here is where the rest of the project will go
	    	loop: while (true) {
	    		System.out.println("Enter command: ($command for a list of available commands)");
		    	String command = scanner.nextLine();
	    		switch (command) {
	    			// list of commands
	    		case "$command":
	    			System.out.println("Commands:");
	    			System.out.println("$h  - register a new temporary home");
	    			System.out.println("$rs - record a stay at a temporary home");
	    			System.out.println("$f  - leave feedback on a temporary home");
	    			System.out.println("$t  - create or change a trust rating for another user");
	    			System.out.println("$exit - exit the application");
	    			break;
	    		case "$h":
	    			TemporaryHome.newTH(con.statement, login, scanner);
	    			break;
	    		case "$rs":
	    			TemporaryHome.recordStay(con.statement, login, scanner);
	    			break;
	    		case "$f":
	    			TemporaryHome.recordFeedback(con.statement, login, scanner);
	    			break;
	    		case "$t":
	    			User.declareTrust(con.statement, login, scanner);
	    			break;
	    		case "$exit":
	    			System.out.println("Thank you for using Uotel!");
	    			break loop;
	    		default:
	    			System.out.println("Invalid command.");
	    			break;
	    		}
	    			
	    	}
	    	con.closeConnection();
	    	scanner.close();
	    } 
	    catch (Exception e) {
	    	System.out.println("The following error occurred: ");
	    	System.out.println(e.getMessage());
	    }
	}
}
