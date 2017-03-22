package Uotel;

import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class Driver {
	
	public static void main(String[] args){
	    System.out.println("Welcome to Uotel!");

	    
	    try{
		Connector con = new Connector();
		System.out.println("Now connected to database");
		
		
		Scanner scanner = new Scanner (System.in);
		System.out.println("Enter Username or '$r' to register:");
		String user = scanner.nextLine();
		if (user.equals("$r")){
		    // TODO handle registration
		    //int name = 0;
		    System.out.println("Enter desired username:");
		    while (true){
			String desiredName = scanner.nextLine();
			// check desiredName for uniqueness in DB
			// placeholder if/else for DB check
			
			String checkUnique = "select sum(login) from Users where login = '"+desiredName+"'";
			ResultSet rs = null;
			String result = "";
			System.out.println("Checking if desired username is unique");
			try {
			    rs = con.statement.executeQuery(checkUnique);
			    rs.next();
			    result = rs.getString("sum(login)");
			}
			catch (Exception e) {
				System.out.println("");
			}
			
			if (result == null){
			    System.out.println("Username available!");
			    
			    String password = "";
			    
			    while (true){
				System.out.println("Enter password:");
				password = scanner.nextLine();
				System.out.println("Verify password:");
				String passVerif = scanner.nextLine();
				if (password.equals(passVerif)){
				    break;
				}
				else {
				    System.out.println("Passwords do not match... Please try again.");
				}
			    }
			    
			    System.out.println("Please Enter name: ");
			    String name = scanner.nextLine();
			    
			    System.out.println("Please enter address: ");
			    String address = scanner.nextLine();
			    
			    System.out.println("Please enter phone number in the following format ( XXX-XXX-XXXX ): ");
			    String phone = scanner.nextLine();
			    
			    System.out.println("Registering new user...");
			    
			    String regNewUser = "insert into Users (login, name, userType, password, user_address, user_phone) values( '"+desiredName+"', '"+name+"', 1, '"+password+"', '"+address+"', '"+phone+"')";
			    // Enter new user into database
			    try{
				int statusCode = con.statement.executeUpdate(regNewUser);
				break;
			    }
			    catch (Exception e) {System.out.println(e.getMessage());}
			}
			else {
			    System.out.println("Desired username is not available, please try again:");
			}
		    }
		}
		else {
		    
		    while (true)
			{
			    System.out.println("Welcome " + user + ", please enter your password:");
			    String pass = scanner.nextLine();
			    // TODO check pass against db
			    
			    String checkPass = "select login, password from Users where login = '"+user+"'";
			    ResultSet rs = null;
			    String result1 = "";
			    String result2 = "";
			    System.out.println("Attempting to login...");
			    try {
				rs = con.statement.executeQuery(checkPass);
				rs.next();
				result1 = rs.getString("login");
				//rs.next();
				result2 = rs.getString("password");
				System.out.println(result1 + " " + result2);
			    }
			    catch (Exception e) {System.out.println(e.getMessage());}
			    
			    if (result1.equals(user) && result2.equals(pass)){
				System.out.println("Login successful!");
				break;
			    }
			    else{
				System.out.println("Login unsuccessful, please try again.");
			    }
			}
		}
		
		//TODO: Here is where the rest of the project will go
		System.out.println("Press $h to register a Temporary Home");
		String command = scanner.nextLine();
		if (command.equals("$h")){
			// register TH
		    System.out.println("Please Enter House Category (Home/Apartment/Duplex): ");
		    String category = scanner.nextLine();
		    System.out.println("Please Enter House Name: ");
		    String ThName = scanner.nextLine();
		    System.out.println("Please Enter House Address: ");
		    String ThAddress = scanner.nextLine();
		    System.out.println("Please Enter House URL: ");
		    String ThUrl = scanner.nextLine();
		    System.out.println("Please Enter House Phone Number (XXX-XXX-XXXX): ");
		    String ThPhone = scanner.nextLine();
		    System.out.println("Please Enter Year House was Built: ");
		    int yearBuilt = Integer.parseInt(scanner.nextLine());
		    String owner = user;
		    TemporaryHome tempHome = new TemporaryHome();
		    tempHome.newTH(con.statement, category, ThName, ThAddress, ThUrl, ThPhone, yearBuilt, owner);
			
		}
		con.closeConnection();
		scanner.close();
		// user/pass verified or created
	    } 
	    catch (Exception e) {
	    }
	}
}
