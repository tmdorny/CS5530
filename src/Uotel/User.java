package Uotel;

import java.sql.*;
import java.util.Scanner;

public class User {
	public User() {
	}

	public static String loginRegister(Statement statement) {

		// Create scanner
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter $login for existing users or enter $r to register new user:");
		String user = scanner.nextLine();

		// New user registration
		if (user.equals("$r")) {
			System.out.println("Enter desired username:");
			while (true) {
				String desiredName = scanner.nextLine();

				// Make sure desired login name is not already in database
				String checkUnique = "select sum(login) from Users where login = '" + desiredName + "'";
				ResultSet rs = null;
				String result = "";
				System.out.println("Checking if desired username is unique");
				try {
					rs = statement.executeQuery(checkUnique);
					rs.next();
					result = rs.getString("sum(login)");
				} 
				catch (Exception e) {
					System.out.println(e.getMessage());
				}

				// If login is available, add user to the database
				if (result == null) {

					System.out.println("Username available!");

					// Password creation
					String password = "";
					while (true) {
						System.out.println("Enter password:");
						password = scanner.nextLine();
						System.out.println("Verify password:");
						String passVerif = scanner.nextLine();
						if (password.equals(passVerif)) {
							break;
						} 
						else {
							System.out.println("Passwords do not match... Please try again.");
						}
					}

					// Once password is successfully created, prompt user to
					// enter other desired information
					System.out.println("Please Enter name: ");
					String name = scanner.nextLine();

					System.out.println("Please enter address: ");
					String address = scanner.nextLine();

					System.out.println("Please enter phone number in the following format ( XXX-XXX-XXXX ): ");
					String phone = scanner.nextLine();

					System.out.println("Registering new user...");

					// Add new user to database
					String regNewUser = "insert into Users (login, name, userType, password, user_address, user_phone) values( '"
							+ desiredName + "', '" + name + "', 1, '" + password + "', '" + address + "', '" + phone
							+ "')";
					try {
						statement.executeUpdate(regNewUser);
						scanner.close();
						return desiredName;
					} 
					catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} 
				else {
					System.out.println("Desired username is not available, please try again:");
				}
			}
		}
		// If login already
		else {
		    // Allow x
			int loginAttempts = 3;
			int i = 0;
		    while (i < loginAttempts)
			{
			    System.out.println("Welcome " + user + ", please enter your password:");
			    String pass = scanner.nextLine();
			    
			    String checkPass = "select login, password from Users where login = '"+user+"'";
			    ResultSet rs = null;
			    String result1 = "";
			    String result2 = "";
			    System.out.println("Attempting to login...");
			    try {
			    	rs = statement.executeQuery(checkPass);
			    	rs.next();
			    	result1 = rs.getString("login");
			    	result2 = rs.getString("password");
			    }
			    catch (Exception e) {
			    	System.out.println(e.getMessage());
			    }
			    // If username doesn't exist in DB, prompt user to register and break
			    if (result1 == null) {
			    	System.out.println("No user with the name " + user + " exists, please use the register function for new users.");
			    	scanner.close();
			    	return "";
			    }
			    // If username and password are correct, login
			    else if (result1.equals(user) && result2.equals(pass)){
			    	System.out.println("Login successful!");
			    	scanner.close();
			    	return user;
			    }
			    else{
			    	System.out.println("Login unsuccessful, please try again.");
			    	scanner.close();
			    	return "";
			    }
			}
		}
		scanner.close();
		return "";
	}
}
