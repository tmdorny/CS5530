package Uotel;

import java.sql.ResultSet;
import java.sql.*;

public class TemporaryHome {
	
	public TemporaryHome() {
	}
	public void newTH(Statement statement, String category, String ThName, String ThAddress, String Url, String ThPhone, int yearBuilt, String owner) {
		// get hid
		int hid;
		String checkUnique = "select count(hid) from TH";
		ResultSet rs = null;
		String result = "";
		try {
			rs = statement.executeQuery(checkUnique);
			rs.next();
			hid = Integer.parseInt(rs.getString("count(hid)"));
			String regNewTH = "insert into TH (hid, category, TH_name, TH_address, URL, TH_phone, yearbuilt, owner) values( '"+hid+"', '"+category+"', '"+ThName+"', '"+ThAddress+"', '"+Url+"', '"+ThPhone+"', '"+yearBuilt+"', '"+owner+"')";
			int statusCode = statement.executeUpdate(regNewTH);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//The user will enter the hid of the TH they stayed at and the dates during which they stayed
	//This will return a sql query to the main method. The main method should store these
	//queries in a list, then when a user tries to exit, the main method should prompt 
	//the user to confirm each stay before executing the queries
	//If null is returned, an error was encountered
	public String recordStay(Statement statement, String hid, String from, String to, String username)
	{	
		String getPid = "select pid from Period where from = '"+from+"' AND to = '"+to+"'";
		
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
	//text should be less than 100 chars (handle in main method)
	//Returns status code 0 - okay, 1 - error
	//****NOTE: database needs to be updated so that Feedback can store a score variable**** 
	public int recordFeedback(Statement statement, String username, String hid, String date, String score, String text)
	{
		//Find if user has already left feedback on this TH
		String findFeedback = "select fid from Feedback where login = '"+username+"' AND hid = '"+hid+"'";
		ResultSet rs = null;
		String fid;
		
		try {
			rs = statement.executeQuery(findFeedback);
			rs.next();
			fid = rs.getString("fid");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when checking Feedback: ");
			System.out.println(e.getMessage());
			return 1;
		}
		
		if (fid != null)
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
	
	//When called, will add a trust rating to the Trust table of the database
	//Main method will pass in a boolean to denote whether or not the current user trusts the user they entered
	//Returns status code 0 - okay, 1 - error
	public int declareTrust(Statement statement, String currentUser, String userToJudge, boolean isTrusted)
	{
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
		
		
		//Check if we have already rated a user, and if so, change the ranking to what was given
		String findTrust = "select login2 from Trust where login1 = '"+currentUser+"' AND login2 = '"+userToJudge+"'";
		String userJudged;
		
		try {
			rs = statement.executeQuery(findTrust);
			rs.next();
			userJudged = rs.getString("login2");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when searching Trust ratings: ");
			System.out.println(e.getMessage());
			return 1;
		}
		
		//Convert isTrusted to a string
		String rating;
		if (isTrusted)
		{
			rating = "1";
		}
		else
		{
			rating = "0";
		}
		
		String rateUser = "";
		if (userJudged == null)
		{
			//Add new trust rating
			rateUser = "insert into Trust (login1, login2, isTrusted) values( '"+currentUser+"', '"+userToJudge+"', '"+rating+"')";
		}
		else if (userJudged.equals(userToJudge))
		{
			//Modify existing rating
			rateUser = "update Trust set isTrusted = '"+rating+"' where login1 = '"+currentUser+"' AND login2 = '"+userToJudge+"'";
		}
		else
		{
			System.out.println("An error occurred when ranking other user.");
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
}