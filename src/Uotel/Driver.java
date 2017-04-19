package Uotel;

import java.util.*;
import java.util.Scanner;
//import java.sql.*;
//import java.io.*;

public class Driver {
	
	public static void main(String[] args){
	    System.out.println("Welcome to Uotel!");
	    List<String> reserveStrings = new ArrayList<String>();
	    List<String> stayStrings = new ArrayList<String>();
	    
	    try {
	    	Connector con = new Connector();
	    	System.out.println("Now connected to database");
	    	Scanner scanner = new Scanner(System.in);
		
	    	// Login / register // login variable contains the login field of the currently logged in user
	    	String login = "";
	    	while (login.equals("")) {
	    		//login = User.loginRegister(con.statement, scanner);
	    	}

	    	//TODO: Here is where the rest of the project will go
	    	loop: while (true) {
	    		System.out.println("Enter command: ($command for a list of available commands)");
		    	String command = scanner.nextLine();
	    		switch (command) {
	    			// list of commands
	    		case "$command":
	    			System.out.println("Commands:");
	    			System.out.println("$up - update the cost and available dates of a temporary home");
	    			System.out.println("$h  - register a new temporary home");
	    			System.out.println("$r  - make a reservation at a temporary home");
	    			System.out.println("$rs - record a stay at a temporary home");
	    			System.out.println("$b  - browse available temporary homes");
	    			System.out.println("$f  - leave feedback on a temporary home");
	    			System.out.println("$fav - add a temporary home to your favorites");
	    			System.out.println("$t  - create or change a trust rating for another user");
	    			System.out.println("$gf - get useful feedback for a specific home");
	    			System.out.println("$vf - view and rate feedback for a temporary home");
	    			System.out.println("$ds - get the degrees of separation between two users");
	    			System.out.println("$st - get a list of the most visited, most expensive, and highest rated homes");
	    			System.out.println("$tu - get a list of the most trusted and most useful users");
	    			System.out.println("$sug - get a list of suggested TH based on interest in certain TH");
	    			System.out.println("$exit - exit the application (must confirm any reservations or recorded stays before exiting");
	    			break;
	    		case "$up":
	    			System.out.println("Which temporary home would you like to update? (enter hid) :");
	    			int hid = Integer.parseInt(scanner.nextLine());
	    			TemporaryHome.updateTH(con.statement, login, hid, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$h":
	    			TemporaryHome.newTH(con.statement, login, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$r":
	    			System.out.println("Which temporary home would you like to make a reservation to? (enter hid) :");
	    			int reserveHid = Integer.parseInt(scanner.nextLine());
	    			System.out.println("How many nights will you be staying? :");
	    			int nights = Integer.parseInt(scanner.nextLine());
	    			String reserveString = TemporaryHome.startReserveTH(con.statement, reserveHid, login, nights, scanner);
	    			if (reserveString != ""){
	    				reserveStrings.add(reserveString);
	    			}
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$rs":
	    			String stayQ = TemporaryHome.recordStay(con.statement, login, scanner);
	    			if (stayQ != null)
	    			{
	    				stayStrings.add(stayQ);
	    			}
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$b":
	    			TemporaryHome.browse(con.statement, login, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$f":
	    			TemporaryHome.recordFeedback(con.statement, login, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$fav":
	    			User.addFavorite(con.statement, login, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$t":
	    			User.declareTrust(con.statement, login, scanner);
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		case "$gf":
	    			for (String r: TemporaryHome.getFeedback(con.statement, scanner))
	    			{
	    				System.out.println(r);
	    				System.out.println();
	    			}
	    			System.out.println();
	    			break;
	    		case "$vf":
	    			TemporaryHome.viewFeedback(con.statement, login, scanner);
	    			System.out.println();
	    			break;
	    		case "$ds":
	    			User.degreesOfSeparation(con.statement, scanner);
	    			System.out.println();
	    			break;
	    		case "$st":
	    			TemporaryHome.statistics(con.statement, scanner);
	    			break;
	    		case "$tu":
	    			User.getTopUsers(con.statement, scanner);
	    			break;
	    		case "$sug":
	    			TemporaryHome.getSuggestions(con.statement, scanner);
	    			break;
	    		case "$exit":
	    			for(String r: reserveStrings)
	    			{
	    				TemporaryHome.finishReserveTH(con.statement, r, login, scanner);
	    			}
	    			
	    			for (String q: stayStrings)
	    			{
	    				TemporaryHome.finishRecordStay(con.statement, q, scanner);
	    			}
	    			
	    			System.out.println("Thank you for using Uotel!");
	    			break loop;
	    		default:
	    			System.out.println("Invalid command.");
	    			System.out.println();
	    			System.out.println();
	    			break;
	    		}
	    			
	    	}
	    	//System.out.println("Thank you for using Uotel!");
	    	con.closeConnection();
	    	scanner.close();
	    } 
	    catch (Exception e) {
	    	System.out.println("The following error occurred: ");
	    	System.out.println(e.getMessage());
	    }
	}
}
