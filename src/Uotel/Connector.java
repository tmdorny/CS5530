package Uotel;
import java.sql.*;

public class Connector {
	public Connection connect;
	public Statement statement;	
	public Connector (String username, String password) throws Exception {
		String url = "georgia.eng.utah.edu";
		Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		try {
			connect = DriverManager.getConnection(url, username, password);
			statement = connect.createStatement();
		}
		catch (Exception e) {
			System.out.println("Error");
		}
		
	}

}
