package Uotel;

import java.text.*;
import java.util.*;
import java.sql.*;
import java.util.Date;

public class TemporaryHome {
	static int pid = 0;
	static int globalWid = 0;
	public TemporaryHome() {
	}
	public static int getHid(Statement statement, String name){
		String fromName = "select hid from TH where TH_name='"+name + "'";
		try {
			ResultSet rs = statement.executeQuery(fromName);
			rs.next();
			return rs.getInt("hid");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when checking ownership: ");
			System.out.println(e.getMessage());
			return -1;
		}
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
	public static void addKeyword (Statement statement, int hid, Scanner scanner) {
		
		// Get wid
		String checkUnique = "select MAX(wid) from Keywords";
		int wid = 0;
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(checkUnique);
			rs.next();
			wid = Integer.parseInt(rs.getString("Max(wid)")) + 1;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Enter the desired keyword:");
		String keyword = scanner.nextLine();
		System.out.println("Enter the keyword language:");
		String language = scanner.nextLine();
		try {
			String newKeyword = "insert into Keywords (wid, word, language) values( '"+wid+"', '"+keyword+"', '"+language+"')";
			statement.executeUpdate(newKeyword);
			String hasKeyword = "insert into HasKeywords (hid, wid) values( '"+hid+"', '"+wid+"')";
			statement.executeUpdate(hasKeyword);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	// Allows a user to update the price and/or availability for one of their registered homes
	public static void updateTH(Statement statement, String owner, int hid, Scanner scanner) {
		// check that th is registered to user
		String checkOwner = "select owner From TH Where hid= " + hid;
		//Find the registered owner of TH
		ResultSet rs = null;
		String registeredOwner;
				
		try {
			rs = statement.executeQuery(checkOwner);
			rs.next();
			registeredOwner = rs.getString("owner");	
		}
		catch (Exception e) {
			System.out.println("The following error occurred when checking ownership: ");
			System.out.println(e.getMessage());
			return;
		}
		if (registeredOwner.equals(owner)){
			// Proceed with update
			System.out.println("Please Enter Cost Per Night: ");
		    String cost = scanner.nextLine();
		    
		    
		    while (true) {
		    	System.out.println("Please Enter the Start Date of a Window of Your Temporary Home's Availabilty (YYYY-MM-DD) or \"$done\" to move on: ");
		    	String startDate = scanner.nextLine();
		    	if (startDate.equals("$done")) {
		    		break;
		    	}
		    	else {
		    		System.out.println("Please Enter the End Date of Your Temporary Home's Availabilty Window (YYYY-MM-DD): ");
		    		String endDate = scanner.nextLine();
		    		// Add availabilty into Period table (and get corresponding pid)
		    		int queryPid = addAvailabilityWindow(statement, startDate, endDate);
		    		String availableQuery = "insert into Available (hid, pid, price_per_night) values( '"+hid+"', '"+queryPid+"', '"+cost+"')";
		    		System.out.println("PID: " + queryPid);
		    		try{
		    			statement.executeUpdate(availableQuery);
		    		}
		    		catch (Exception e) {
		    			System.out.println("Could not add availability period, the following error occurred: ");
		    			System.out.println(e.getMessage());
		    		}
		    	}
		    }
		    // Add any desired keywords
		    System.out.println("Would you like to add a keyword describing your temporary home? (y/n)");
		    String key = scanner.nextLine();
		    if (key.equals("y")){
		    	addKeyword (statement, hid, scanner);
		    }
		}
		else {
			System.out.println("You do not own this temporary home!");
			return;
		}
		return;
	}
	// Updates the Period table with the specified start and end dates, returns the corresponding pid
	public static int addAvailabilityWindow(Statement statement, String start, String end) {
		
		// Get pid
		String checkUnique = "select MAX(pid) from Period";
		int pid = 0;
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(checkUnique);
			rs.next();
			pid = Integer.parseInt(rs.getString("Max(pid)")) + 1;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// TODO: add availability conflict resolutions
		String periodQuery = "insert into Period (pid, start, end) values( '"+pid+"', '"+start+"', '"+end+"')";
		try{
			statement.executeUpdate(periodQuery);
		}
		catch (Exception e) {
			System.out.println("Could not add availability period, the following error occurred: ");
			System.out.println(e.getMessage());
			return -1;
		}
		return pid;
	}
	// Gives suggestions based on what THs users who have also visited the specified hid have visited, 
	// suggestions are listed in order based on how many users have visited both THs (where user is staying and suggested TH)
	public static void getSuggestions(Statement statement, Scanner scanner) {
		System.out.println("Enter the hid of a temporary home you'd like to receive suggestions similar to: ");
		int hid = Integer.parseInt(scanner.nextLine());
		String getSuggestions = "select h.hid, count(v.login) as Visits From Visit v, (select login From Visit Where hid=" + hid +") as s where v.login=s.login";
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(getSuggestions);
			while (rs.next()) {
				System.out.println("Suggested TH hid: " + rs.getString("hid"));
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Sets up a reservation, updates the available dates of a TH and adds the reservation to the database.
	public static String startReserveTH(Statement statement, int hid, String user, int nights, Scanner scanner) {
		
		String getPids = "select pid, price_per_night From Available Where hid= " + hid;
		ResultSet rs = null;
		int currentPid;
		int price = 0;
		Map<Integer, String> availableDates = new HashMap<Integer, String>();
				
		try {
			rs = statement.executeQuery(getPids);
			ArrayList<Integer> pids = new ArrayList<Integer>();
			while(rs.next()){
				currentPid = rs.getInt("pid");
				price = rs.getInt("price_per_night");
				pids.add(currentPid);
			}
			System.out.println("Specified home is available from :");
			for (int i : pids) {
				String getDates = "select start, end From Period Where pid= " + i;
				ResultSet rs2 = statement.executeQuery(getDates);
				rs2.next();
				String start = rs2.getString("start");
				String end = rs2.getString("end");
				availableDates.put(i, start + end);
			}
		}
		catch (Exception e) {
			System.out.println("The following error occurred when checking reservable dates: ");
			System.out.println(e.getMessage());
			return "";
		}
		
		for (int i: availableDates.keySet())
		{
			System.out.print(availableDates.get(i).substring(0, 10));
			System.out.print(" - ");
			System.out.print(availableDates.get(i).substring(10,20));
			System.out.println();
		}
		
		// User can select a window from the displayed available dates
		System.out.println("Please Enter Desired Start Date (YYYY-MM-DD): ");
	    String startDate = scanner.nextLine();
	    System.out.println("Please Enter Desired End Date: (YYYY-MM-DD): ");
	    String endDate = scanner.nextLine();
	    // Determine which date window the desired dates fall into
	    // pid of the window in which the desired dates fall into
	    int windowPid = -1;
	    
	    int startYear = Integer.parseInt(startDate.substring(0, 4));
	    int startMonth = Integer.parseInt(startDate.substring(5, 7));
	    int startDay = Integer.parseInt(startDate.substring(8));
	    int endYear = Integer.parseInt(endDate.substring(0, 4));
	    int endMonth = Integer.parseInt(endDate.substring(5, 7));
	    int endDay = Integer.parseInt(endDate.substring(8));
	    
	    Date start = new Date(startYear, startMonth, startDay);
	    Date end = new Date(endYear, endMonth, endDay);
	    
	    
	    // Apologies for this mess, was unable to come up with a more elegant solution here
	    for (int j : availableDates.keySet()) {
	    	int aStartYear = Integer.parseInt(availableDates.get(j).substring(0, 4));
			int aStartMonth = Integer.parseInt(availableDates.get(j).substring(5, 7));
			int aStartDay = Integer.parseInt(availableDates.get(j).substring(8, 10));
			int aEndYear = Integer.parseInt(availableDates.get(j).substring(10, 14));
			int aEndMonth = Integer.parseInt(availableDates.get(j).substring(15, 17));
			int aEndDay = Integer.parseInt(availableDates.get(j).substring(18));
			
			Date aStart = new Date(aStartYear, aStartMonth, aStartDay);
			Date aEnd = new Date(aEndYear, aEndMonth, aEndDay);
			
			if (start.compareTo(aStart) >= 0 && end.compareTo(aEnd) <= 0)
			{
				windowPid = j;
			}
	    	
			/*
	    	// Check if stay is within available years
	    	if (startYear - aStartYear >= 0 && aEndYear - endYear >= 0) {
	    		
	    		// If stay is within available years, check if stay is within available months
	    		if (startMonth - aStartMonth >= 0 || ) {
	    			pastStartMonth = (startMonth - aStartMonth > 0);
	    			// Compare start days
	    			if (startDay - aStartDay >= 0) {
	    				// Compare End years
	    				if (Integer.parseInt(availableDates.get(j).substring(10, 14)) - Integer.parseInt(endDate.substring(0, 4)) >= 0) {
	    					// Compare End months
	    					if (Integer.parseInt(availableDates.get(j).substring(15, 17)) - Integer.parseInt(endDate.substring(5, 7)) >= 0) {
	    						// Compare End days
	    						if (Integer.parseInt(availableDates.get(j).substring(18)) - Integer.parseInt(endDate.substring(8)) >= 0) {
	    							windowPid = j;
	    							break;
	    						}
	    					}
	    				}
	    			}
	    		}
	    	} */
	    }
	    // END DISASTER
	    
	    if (windowPid == -1)
	    {
	    	System.out.println("Requested dates are not available.");
	    	return "";
	    }
	    else
	    {
	    	return startDate + " " + endDate + " " + availableDates.get(windowPid).substring(0, 10) 
	    					 + " " + availableDates.get(windowPid).substring(10) + " " 
	    					 + windowPid + " " + hid + " " + price + " " + nights;
	    }
	}
	    
    public static void finishReserveTH(Statement statement, String reserveString, String user, Scanner scanner){
	    // Update the period table with new dates
    	String[] split = reserveString.split("\\s+");
    	
    	String startDate = split[0];
    	String endDate = split[1];
    	String aStartDate = split[2];
    	String aEndDate = split[3];
    	int windowPid = Integer.parseInt(split[4]);
    	int hid = Integer.parseInt(split[5]);
    	int price = Integer.parseInt(split[6]);
    	int nights = Integer.parseInt(split[7]);
    	
    	System.out.println("Confirm Reservation at Home " + hid + " from " + startDate + " to " + endDate + " ? (y/n)");
    	String response = scanner.nextLine();
    	if (response.equals("n"))
    	{
    		return;
    	}
    	
	    String deletePeriod = "delete from Period where pid=" + windowPid;
	    try{
			statement.executeUpdate(deletePeriod);
		}
		catch (Exception e) {
			System.out.println("Could not delete from period, the following error occurred: ");
			System.out.println(e.getMessage());
		}
	    int queryPid;
	    String availableQuery;
	    // if start date is not at the beginning of the window
	    if (!startDate.equals(aStartDate)) {
	    	// if end date is not at end of window and start date is not at start of window
	    	if (!endDate.equals(aEndDate)) {
	    		
	    		queryPid = addAvailabilityWindow(statement, aStartDate, startDate);
	    		availableQuery = "insert into Available (hid, pid, price_per_night) values( '"+hid+"', '"+queryPid+"', '"+price+"')";
	    		try{
	    			statement.executeUpdate(availableQuery);
	    		}
	    		catch (Exception e) {
	    			System.out.println("Could not add time period, the following error occurred: ");
	    			System.out.println(e.getMessage());
	    		}
	    		
	    		queryPid = addAvailabilityWindow(statement, endDate, aEndDate);
	    		availableQuery = "insert into Available (hid, pid, price_per_night) values( '"+hid+"', '"+queryPid+"', '"+price+"')";
	    		try{
	    			statement.executeUpdate(availableQuery);
	    		}
	    		catch (Exception e) {
	    			System.out.println("Could not add time period, the following error occurred: ");
	    			System.out.println(e.getMessage());
	    		}
	    		
	    	}
	    	// if end date is not at end of window and start date is at start of window
	    	else {
	    		queryPid = addAvailabilityWindow(statement, aStartDate, startDate);
	    		availableQuery = "insert into Available (hid, pid, price_per_night) values( '"+hid+"', '"+queryPid+"', '"+price+"')";
	    		try{
	    			statement.executeUpdate(availableQuery);
	    		}
	    		catch (Exception e) {
	    			System.out.println("Could not add time period, the following error occurred: ");
	    			System.out.println(e.getMessage());
	    		}
	    	}
	    }
	    // if start date lines up with window but end date does not
	    else if (!endDate.equals(aEndDate.substring(0, 9))) {
	    	queryPid = addAvailabilityWindow(statement, endDate, aEndDate);
	    	availableQuery = "insert into Available (hid, pid, price_per_night) values( '"+hid+"', '"+queryPid+"', '"+price+"')";
    		try{
    			statement.executeUpdate(availableQuery);
    		}
    		catch (Exception e) {
    			System.out.println("Could not add time period, the following error occurred: ");
    			System.out.println(e.getMessage());
    		}
	    }
	    queryPid = addAvailabilityWindow(statement, startDate, endDate);
	    int cost = price * nights;
	    String addReserve = "insert into Reserve (login, hid, pid, cost) values( '"+user+"', '"+hid+"', '"+queryPid+"', '"+cost+"')";
	    try{
			statement.executeUpdate(addReserve);
		}
		catch (Exception e) {
			System.out.println("Could not add reservation, the following error occurred: ");
			System.out.println(e.getMessage());
		}
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
		
		String getPid = "select pid from Period where start = '"+from+"' AND end = '"+to+"'";
		
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
			String recordStayQuery = hid + "insert into Visit (login, hid, pid, cost) values( '"+username+"', '"+hid+"', '"+pid+"', '"+cost+"')";
			return recordStayQuery;
		}
	}
	
	public static void finishRecordStay(Statement statement, String query, Scanner scanner)
	{
		int hid = Integer.parseInt(query.substring(0, 1));
		String q = query.substring(1); 
		System.out.println("Confirm Stay at Home " + hid +"? (y/n)");
    	String response = scanner.nextLine();
    	if (response.equals("n"))
    	{
    		return;
    	}
		
		int result = 0;
		try
		{
			result = statement.executeUpdate(q);
		}
		catch (Exception e)
		{
			System.out.println("The following error occurred when recording stay: ");
			System.out.println(e.getMessage());
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
			statement.executeUpdate(recordFeedback);
			return 0;
		}
		catch (Exception e) {
			System.out.println("Could not record feedback, the following error occurred: ");
			System.out.println(e.getMessage());
			return 1;
		}
	}
	
	//Gets the top n useful feedbacks for a TH
	//Command: $gf, 
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
	// View all feedback for the specified temporary home with the ability to rate each review on its helpfulness
	public static void viewFeedback(Statement statement, String user, Scanner scanner) {
		System.out.println("Please enter the ID of the home you wish to view feedback for:");
		String hid = scanner.nextLine();
		String q = "select f.fid, f.hid, f.login, f.score, f.text, f.fbDate from Feedback f where "
				+ "f.hid ="+hid;
		ResultSet rs = null;
		Map<Integer, Integer> feedbackRatings = new HashMap<Integer, Integer>();
		try {
			rs = statement.executeQuery(q);
			while (rs.next()) {
				System.out.println("House ID: " + rs.getString("hid") + "\nUser: " + rs.getString("login") + "\nScore: " + rs.getString("score") + "\nComment: " + rs.getString("text") + "\nDate: " + rs.getString("fbDate"));
				System.out.println("How useful was this feedback to you? (0 - Useless; 1 - Fairly Useful; 2 - Very Useful) [Enter $q if you'd like to stop viewing feedback]");
				String input = scanner.nextLine();
				if (input.equals("$q")){
					break;
				}
				else {
					int score = Integer.parseInt(input);
					int fid = rs.getInt("fid");
					feedbackRatings.put(fid, score);
				}
			}
			for (int fid : feedbackRatings.keySet()){
				String addRating = "insert into Rates (login, fid, rating) values( '"+user+"', '"+fid+"', '"+feedbackRatings.get(fid)+"')";
			    try{
					statement.executeUpdate(addRating);
				}
				catch (Exception e) {
					System.out.println("Could not add favorite, the following error occurred: ");
					System.out.println(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			System.out.println("The following error occurred: " + e.getMessage());
			return;
		}
		
	}
	// Browse available temporary homes
	public static void browse(Statement statement, String login, Scanner scanner) {
		// Price query
		System.out.println("Enter a price (per night) threshold (lower bound) [enter no to disable price threshold]: ");
		String lowerBoundString = scanner.nextLine();
		String priceCondition;
		String locationCondition;
		String keywordCondition;
		String categoryCondition;
		if (lowerBoundString.equals("no")){
			priceCondition = "";
		}
		// Create conditional statement involving price
		else {
			int lowerBound = Integer.parseInt(lowerBoundString);
			System.out.println("Enter the upper bound of price threshold:");
			int upperBound = Integer.parseInt(scanner.nextLine());
			priceCondition = " AND a.price_per_night > " + lowerBound + " AND a.price_per_night < "+ upperBound + " AND a.hid=h.hid";
			
		}
		// Address
		System.out.println("Enter the name of the desired city or state [enter no to disable location constraint]: ");
		String location = scanner.nextLine();
		if (location.equals("no")){
			locationCondition = "";
		}
		else {
			locationCondition = " AND h.TH_address LIKE '%" + location + "%' ";
		}
		// Keyword
		System.out.println("Enter the name of the desired keyword to match [enter 'no' to ignore keywords]: ");
		String keyword = scanner.nextLine();
		if (keyword.equals("no")){
			keywordCondition = "";
		}
		else {
				keywordCondition = " AND h.hid=hk.hid AND hk.wid=k.wid AND k.word='" + keyword + "' ";			
		}
		// Category
		System.out.println("Enter the name of the desired category to match [enter 'no' to ignore category]: ");
		String category = scanner.nextLine();
		if (category.equals("no")){
			categoryCondition = "";
		}
		else {
			categoryCondition = " AND h.category='" + category + "' ";			
		}
		// Put together strings to create desired query
		String query = "Select DISTINCT h.TH_name, h.hid, a.price_per_night, f1.avgscore as UserFeedback, f3.avgscore2 as TrustedUserFeedback from TH h, Available a, HasKeywords hk, Keywords k, "
				+ "(select f.hid, avg(f.score) as avgscore from Feedback f group by f.hid order by avgscore desc) as f1, "
				+ "(select f2.hid, avg(f2.score) as avgscore2 from Feedback f2, Trust t where t.login1='" + login + "' AND f2.login=t.login2 group by f2.hid order by avgscore2 desc) as f3"
				+ " where h.hid = f1.hid AND h.hid = f3.hid " + priceCondition + locationCondition +  keywordCondition + categoryCondition;
		//String query;
		// Sort by
		String sortBy;
		System.out.println("How do you want to sort results? (Price[$p], Feedback Score[$fs], or Trusted Feedback Score[$tfs]");
		String sort = scanner.nextLine();
		if (sort.equals("$p")){
			sortBy = " order by a.price_per_night desc";
//			query = "Select DISTINCT h.TH_name, h.hid, a.price_per_night From TH h, Available a, HasKeywords hk, Keywords k where h.hid=a.hid" + priceCondition + locationCondition +  keywordCondition + categoryCondition;
		}
		else if (sort.equals("fs")){
			sortBy = " order by UserFeedback desc";
//			query = "Select DISTINCT h.TH_name, h.hid, f1.avgscore as UserFeedback From TH h, Available a, "
//					+ "(select f.hid, avg(f.score) as avgscore from Feedback f group by f.hid order by avgscore desc) as f1,"
//					+ " HasKeywords hk, Keywords k where h.hid = f1.hid" + priceCondition + locationCondition +  keywordCondition + categoryCondition;
		}
		else if (sort.equals("tfs")){
			sortBy = " order by TrustedUserFeedback desc";
//			query = "Select DISTINCT h.TH_name, h.hid, f1.avgscore as UserFeedback From TH h, Available a, "
//					+ "(select f2.hid, avg(f2.score) as avgscore2 from Feedback f2, Trust t where t.login1='" + login + "' AND f2.login=t.login2 group by f2.hid order by avgscore2 desc) as f3,"
//					+ " HasKeywords hk, Keywords k where h.hid = f3.hid" + priceCondition + locationCondition +  keywordCondition + categoryCondition;
		}
		else {
			System.out.println("Bad command entered.");
			return;
		}
		// Final Query
		String finalQuery = query + sortBy;
		System.out.println("FINAL QUERY IS: " + finalQuery);
		ResultSet rs = null;
		
		try {
			rs = statement.executeQuery(finalQuery);
			while (rs.next()){
				System.out.println("TH Name : " + rs.getString("TH_name") + "\nhid: " + rs.getString("hid") + "\nPrice per night: " + rs.getString("price_per_night") + "\nAverage User Feedback Score: " + rs.getString("UserFeedback") + "\nAverage Trusted User Feedback Score: " + rs.getString("TrustedUserFeedback"));
				
			}
		}
		catch (Exception e) {
			System.out.println("The following error occurred when browsing THs: ");
			System.out.println(e.getMessage());
			return;
		}
		return;
		
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