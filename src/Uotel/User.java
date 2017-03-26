package Uotel;

import java.sql.*;
import java.util.*;

public class User {
	public User() {
	}

	public static String loginRegister(Statement statement, Scanner scanner) {
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
						//scanner.close();
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
			    	//scanner.close();
			    	return "";
			    }
			    // If username and password are correct, login
			    else if (result1.equals(user) && result2.equals(pass)){
			    	System.out.println("Login successful!");
			    	//scanner.close();
			    	return user;
			    }
			    else{
			    	System.out.println("Login unsuccessful, please try again.");
			    	//scanner.close();
			    	return "";
			    }
			}
		}
		//scanner.close();
		return "";
	}
	
	//When called, will add a trust rating to the Trust table of the database
	//Main method will pass in a boolean to denote whether or not the current user trusts the user they entered
	//Returns status code 0 - okay, 1 - error
	//Command is $t
	public static int declareTrust(Statement statement, String currentUser, Scanner scanner)
	{
		System.out.println("Please enter the name of the user you which to rate: ");
		String userToJudge = scanner.nextLine();
		System.out.println("Enter 1 to indicate you trust this user or 0 to indicate you do not trust this user:");
		String rating = scanner.nextLine();
		//scanner.close();
			
		//Verify the username entered is valid
		String findUser2 = "select login from Users where login = '"+userToJudge+"'";
		ResultSet rs = null;
		String user2;
		try {
			rs = statement.executeQuery(findUser2);
			rs.next();
			user2 = rs.getString("login");
		}
		catch (Exception e) {
			System.out.println("The following error occurred when searching for the user entered:");
			System.out.println(e.getMessage());
			return 1;
		}
			
		if (user2 == null)
		{
			System.out.println("The user entered could not be found.");
			return 1;
		}
			
		//Check if we have already rated this user
		String findTrust = "select count(login2) from Trust where login1 = '"+currentUser+"' AND login2 = '"+userToJudge+"'";
		String userJudged;
			
		try {
			rs = statement.executeQuery(findTrust);
			rs.next();
			userJudged = rs.getString("count(login2)");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when searching Trust ratings: ");
			System.out.println(e.getMessage());
			return 1;
		}
			
		String rateUser = "";
		if (userJudged.equals("0"))
		{
			//Add new trust rating
			rateUser = "insert into Trust (login1, login2, isTrusted) values( '"+currentUser+"', '"+userToJudge+"', '"+rating+"')";
		}
		else if (userJudged.equals("1"))
		{
			//Modify existing rating
			rateUser = "update Trust set isTrusted = '"+rating+"' where login1 = '"+currentUser+"' AND login2 = '"+userToJudge+"'";
		}
		else
		{
			System.out.println("An error occurred when ranking other user.");
			return 1;
		}
		
		try {
			int statusCode = statement.executeUpdate(rateUser);
			return 0;
		}
		catch (Exception e) {
			System.out.println("Could not record user rating, the following error occurred: ");
			System.out.println(e.getMessage());
			return 1;
		}
	}
	
	//Gets the degrees of separation between two users
	//Command: $ds
	public static void degreesOfSeparation(Statement statement, Scanner scanner)
	{
		System.out.println("Enter the name of the first user: ");
		String user1 = scanner.nextLine();
		System.out.println("Enter the name of the second user: ");
		String user2 = scanner.nextLine();
		
		String findPairs = "select f1.login as login1, f2.login as login2 from Favorites f1, Favorites f2 where "
				+ "f1.hid = f2.hid and f1.login != f2.login";
		
		ResultSet rs = null;
		Map<String, List<String>> graph = new HashMap<String, List<String>>();
		Map<String, Integer> indexes = new HashMap<String, Integer>(); 
		
		//Build a graph out of the users given and any user that favorited things they favorited as well
		try {
			rs = statement.executeQuery(findPairs);
			
			while (rs.next())
			{
				String n1 = rs.getString("login1");
				String n2 = rs.getString("login2");
				
				if (graph.containsKey(n1))
				{
					graph.get(n1).add(n2);
				}
				else
				{
					graph.put(n1, new ArrayList<String>());	
					graph.get(n1).add(n2);
					indexes.put(n1, graph.size()-1);
				}
				
				if (graph.containsKey(n2))
				{
					graph.get(n2).add(n1);
				}
				else
				{
					graph.put(n2, new ArrayList<String>());	
					graph.get(n2).add(n1);
					indexes.put(n2, graph.size()-1);
				}	
			}
		}
		catch (Exception e) {
			System.out.println("The following error occurred: " + e.getMessage());
			return;
		}
		
		//Perform a Breadth-First Search on the graph created by the pairs of users
		//The distance from user1 to user2 will be their degrees of separation
		String[] prev = new String[graph.size()];
		int[] dist = new int[graph.size()];
		
		for (int i = 0; i < indexes.size(); i++)
		{
			prev[i] = null;
			dist[i] = 2000000;
		}
		
		dist[indexes.get(user1)] = 0;
		
		Queue<String> Q = new LinkedList<String>();
		Q.add(user1);
		
		while (Q.size() != 0)
		{
			String currentNode = Q.remove();
			for(String s: graph.get(currentNode))
			{
				int index = indexes.get(s);
				if (dist[index] == 2000000)
				{
					Q.add(s);
					dist[index] = dist[indexes.get(currentNode)] + 1;
					prev[index] = currentNode;
				}
			}
		}
		
		System.out.println("These users are " +dist[indexes.get(user2)]+ " degree(s) apart.");
	}
	
	//Prints out the most trusted and the most useful users
	//Command: $tu
	public static void getTopUsers(Statement statement, Scanner scanner)
	{
		System.out.println("Please enter a number for how many users you wanted to see the top statistics for: ");
		int m = Integer.parseInt(scanner.nextLine());
			
		String getMostTrusted = "select u.login, u.name, t1.numTrust from Users u, (select t.login2, count(t.login1) as numTrust from Trust t where t.isTrusted = 1 group by t.login2 order by numtrust desc) as t1 where u.login = t1.login2";

		String getMostUseful = "select u.login, u.name, r1.avgrate from Users u, (select r.login, avg(r.rating) as avgrate from Rates r group by r.login order by avgrate desc) as r1 where u.login = r1.login";
			
		ResultSet rs = null;
		List<String> mt = new ArrayList<String>();
		List<String> mu = new ArrayList<String>();
			
		try {
			rs = statement.executeQuery(getMostTrusted);
			
			for (int i = 0; i < m; i++)
			{
				if (rs.next())
				{
					mt.add((i+1) +".\nUser ID: " + rs.getString("login") + "\nUser Name: " + rs.getString("name") + "\nNumber of Users Who Trust Them: " + rs.getString("numTrust") + "\n");
				}
			}
			
			rs = statement.executeQuery(getMostUseful);
			
			for (int i = 0; i < m; i++)
			{
				if (rs.next())
				{
					mu.add((i+1) +".\nUser ID: " + rs.getString("login") + "\nUser Name: " + rs.getString("name") + "\nAverage Useful Rating of Feedback: " + rs.getString("avgrate") + "\n");
				}
			}
			
		}
		catch (Exception e){
			System.out.println("The following error occurred: " + e.getMessage());
			return;
		}
			
		System.out.println("Most Trusted Users: \n");
		for(String s: mt)
		{
			System.out.println(s);
		}
			
		System.out.println("Most Useful Users: \n");
		for(String s: mu)
		{
			System.out.println(s);
		}
	}
}
