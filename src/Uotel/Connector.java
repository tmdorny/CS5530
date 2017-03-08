package Uotel;
import java.sql.*;

public class Connector {
    public Connection connect;
    public Statement statement;	
    public Connector (/*String username, String password*/) throws Exception {
	try {

	    //I changed this code to connect to the database using our username and password. 
	    //When a user starts the program, we'll keep track of their username and password in
	    //the database, so we need to connect to the database, check if it has their username 
	    //and password stored, then they can access info from it. 
	    String url = "jdbc:mysql://georgia.eng.utah.edu/5530db28";
	    String userName = "5530u28";
	    String password = "evb1micl";
	    
	    Class.forName ("com.mysql.jdbc.Driver").newInstance ();
	    connect = DriverManager.getConnection(url, userName, password);
	    statement = connect.createStatement();
	}
	catch (Exception e) {
	    System.out.println("The following error was encountered:");
	    System.out.println(e.getMessage());
	    throw(e);
	}
    }
    
    //Closes connection to database server
    public void closeConnection() throws Exception {
	connect.close();
    }

}
