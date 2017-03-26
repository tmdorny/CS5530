package Uotel;

import java.sql.ResultSet;
import java.text.*;
import java.util.*;
import java.sql.*;

public class TemporaryHome {
	
	public TemporaryHome() {
	}
	public static void newTH(Statement statement, String owner, Scanner scanner) {
		// get hid
		int hid;
		String checkUnique = "select count(hid) from TH";
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(checkUnique);
			rs.next();
			hid = Integer.parseInt(rs.getString("count(hid)"));
			// Fill the rest of the fields from user input
			// register TH
		    System.out.println("Please Enter House Category (Home/Apartment/Duplex): ");
		    String category = scanner.nextLine();
		    System.out.println("Please Enter House Name: ");
		    String ThName = scanner.nextLine();
		    System.out.println("Please Enter House Address: ");
		    String ThAddress = scanner.nextLine();
		    System.out.println("Please Enter House URL: ");
		    String Url = scanner.nextLine();
		    System.out.println("Please Enter House Phone Number (XXX-XXX-XXXX): ");
		    String ThPhone = scanner.nextLine();
		    System.out.println("Please Enter Year House was Built: ");
		    int yearBuilt = Integer.parseInt(scanner.nextLine());
			String regNewTH = "insert into TH (hid, category, TH_name, TH_address, URL, TH_phone, yearbuilt, owner) values( '"+hid+"', '"+category+"', '"+ThName+"', '"+ThAddress+"', '"+Url+"', '"+ThPhone+"', '"+yearBuilt+"', '"+owner+"')";
			statement.executeUpdate(regNewTH);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//scanner.close();
		return;
	}
	
	//The user will enter the hid of the TH they stayed at and the dates during which they stayed
	//This will return a sql query to the main method. The main method should store these
	//queries in a list, then when a user tries to exit, the main method should prompt 
	//the user to confirm each stay before executing the queries
	//If null is returned, an error was encountered
	//Command is $rs
	public static String recordStay(Statement statement, String username, Scanner scanner)
	{	
		System.out.println("Please enter the ID of the house you stayed in: ");
	    String hid = scanner.nextLine();
	    System.out.println("Please enter the start date of your stay (format: YYYY/MM/DD): ");
	    String from = scanner.nextLine();
	    System.out.println("Please enter the end date of your stay (format: YYYY/MM/DD): ");
	    String to = scanner.nextLine();
	    //scanner.close();
		
		String getPid = "select pid from Period where from = "+from+" AND to = "+to;
		
		//Find pid of period of stay
		ResultSet rs = null;
		String pid;
		
		try {
			rs = statement.executeQuery(getPid);
			rs.next();
			pid = rs.getString("pid");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when finding the period of time: ");
			System.out.println(e.getMessage());
			return null;
		}
		
		if (pid == null)
		{
			System.out.println("The period of time specified could not be found");
			return null;
		}
		
		//Find the reservation (and cost) of their stay
		String getReservation = "select cost from Reserve where login = '"+username+"' AND hid = '"+hid+"' AND pid = '"+pid+"'";
		String cost;
		
		try {
			rs = statement.executeQuery(getReservation);
			rs.next();
			cost = rs.getString("cost");
		}
		catch (Exception e){
			System.out.println("The following error occured while getting the reservation: " + e.getMessage());
			return null;
		}
		
		if (cost == null)
		{
			System.out.println("No matching reservation found.");
			return null;
		}
		else
		{
			String recordStayQuery = "insert into Visit (login, hid, pid, cost) values( '"+username+"', '"+hid+"', '"+pid+"', '"+cost+"')";
			return recordStayQuery;
		}
	}
	
	//When called, allows a user to record feedback in the database
	//date should just be todays date when called from the main method
	//score should be 0-10 inclusive
	//text should be less than 100 chars
	//Returns status code 0 - okay, 1 - error
	//Command is $f
	public static int recordFeedback(Statement statement, String username, Scanner scanner)
	{
		System.out.println("Please enter the ID of the house to review: ");
	    String hid = scanner.nextLine();
		String date = new SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date());
		System.out.println("Please enter a numerical score for the house (0-10, with 10 being the best score): ");
	    String score = scanner.nextLine();
		System.out.println("If you want, enter a short review of the house (less than 100 characters). Press enter when done: ");
	    String text = scanner.nextLine();
	    //scanner.close();
		
	    while (text.length() > 100)
	    {
	    	int chars = text.length() - 100;
	    	System.out.println("Too many characters ("+chars+" too many), please try again:");
	    	text = scanner.nextLine();
	    }
		
		//Find if user has already left feedback on this TH
		String findFeedback = "select count(fid) from Feedback where login = '"+username+"' AND hid = '"+hid+"'";
		ResultSet rs = null;
		String fid;
		
		try {
			rs = statement.executeQuery(findFeedback);
			rs.next();
			fid = rs.getString("count(fid)");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when checking Feedback: ");
			System.out.println(e.getMessage());
			return 1;
		}
		
		if (!fid.equals("0"))
		{
			System.out.println("You have already left feedback on this house.");
			return 1;
		}
		
		//Get fid for the new feedback
		String newFid = " ";
		String getFid = "select count(fid) from Feedback";
		try {
			rs = statement.executeQuery(getFid);
			rs.next();
			newFid = rs.getString("count(fid)");
		}
		catch (Exception e){
			System.out.println("The following error occurred when getting the id for the feedback: ");
			System.out.println(e.getMessage());
			return 1;
		}
		
		if (newFid.equals(" "))
		{
			return 1;
		}
		
		//Insert the new feedback
		String recordFeedback = "insert into Feedback (fid, score, text, fbDate, login, hid) values( '"+newFid+"', '"+score+"', '"+text+"', '"+date+"', '"+username+"', '"+hid+"')";
		
		try{
			int statusCode = statement.executeUpdate(recordFeedback);
			return 0;
		}
		catch (Exception e) {
			System.out.println("Could not record feedback, the following error occurred: ");
			System.out.println(e.getMessage());
			return 1;
		}
	}
	
	//Gets the top n useful feedbacks for a TH
	//Command: $gfhid, 
	public static List<String> getFeedback(Statement statement, Scanner scanner)
	{
		System.out.println("Please enter the ID of the home you wish to access feedback for:");
		String hid = scanner.nextLine();
		System.out.println("Please enter a number indicating how many feedback entries you want: ");
		int n = Integer.parseInt(scanner.nextLine());
		
		String q = "select f.hid, f.login, f.score, f.text, f.fbDate from Feedback f, Rates r where "
				+ "f.fid = r.fid and f.hid = '"+hid+"' and "
				+ "(select avg(r1.rating) from Rates r1 where r1.fid = f.fid group by r1.fid) >= 1.0";
		
		ResultSet rs = null;
		List<String> results = new ArrayList<String>();
		
		try {
			rs = statement.executeQuery(q);
			
			for (int i = 0; i < n; i++)
			{
				if (rs.next())
				{
					results.add("House ID: " + rs.getString("hid") + "\nUser: " + rs.getString("login") + "\nScore: " + rs.getString("score") + "\nComment: " + rs.getString("text") + "\nDate: " + rs.getString("fbDate"));
				}
			}
		}
		catch (Exception e) {
			System.out.println("The following error occurred: " + e.getMessage());
			return null;
		}
		
		return results;
	}
	
	//Prints out the most visited, most expensive, and highest rated houses
	//Command: $st
	public static void statistics(Statement statement, Scanner scanner)
	{
		System.out.println("Please enter a number for how many homes you wanted to see the top statistics for: ");
		int m = Integer.parseInt(scanner.nextLine());
		
		String getMostVisited = "select h.hid, h.TH_name, v1.visitors from TH h, (select v.hid, count(v.login) as visitors from Visit v group by v.hid order by visitors desc) as v1 where h.hid = v1.hid";

		String getMostExpensive = "select h.hid, h.TH_name, v1.cost from TH h, (select v.hid, avg(v.cost) as cost from Visit v group by v.hid order by cost desc) as v1 where h.hid = v1.hid";
		
		String getHighestRated = "select h.hid, h.TH_name, f1.avgscore from TH h, (select f.hid, avg(f.score) as avgscore from Feedback f group by f.hid order by avgscore desc) as f1 where h.hid = f1.hid";
		
		ResultSet rs = null;
		List<String> mv = new ArrayList<String>();
		List<String> me = new ArrayList<String>();
		List<String> hr = new ArrayList<String>();
		
		try {
			rs = statement.executeQuery(getMostVisited);
			
			for (int i = 0; i < m; i++)
			{
				if (rs.next())
				{
					mv.add((i+1) +".\nHouse ID: " + rs.getString("hid") + "\nHouse Name: " + rs.getString("TH_name") + "\nNumber of Visitors: " + rs.getString("visitors") + "\n");
				}
			}
			
			rs = statement.executeQuery(getMostExpensive);
			
			for (int i = 0; i < m; i++)
			{
				if (rs.next())
				{
					me.add((i+1) +".\nHouse ID: " + rs.getString("hid") + "\nHouse Name: " + rs.getString("TH_name") + "\nAverage Cost: " + rs.getString("cost") + "\n");
				}
			}
			
			rs = statement.executeQuery(getHighestRated);
			
			for (int i = 0; i < m; i++)
			{
				if (rs.next())
				{
					hr.add((i+1) +".\nHouse ID: " + rs.getString("hid") + "\nHouse Name: " + rs.getString("TH_name") + "\nAverage Score: " + rs.getString("avgscore") + "\n");
				}
			}
		}
		catch (Exception e){
			System.out.println("The following error occurred: " + e.getMessage());
			return;
		}
		
		System.out.println("Most Visited Houses:");
		for(String s: mv)
		{
			System.out.println(s);
		}
		
		System.out.println("Most Expensive Houses:");
		for(String s: me)
		{
			System.out.println(s);
		}
		
		System.out.println("Highest Rated Houses:");
		for(String s: hr)
		{
			System.out.println(s);
		}
	}
}