package Uotel;

import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.*;

public class TemporaryHome {
	
	public TemporaryHome() {
	}
	public static void newTH(Statement statement, String owner) {
		Scanner scanner = new Scanner(System.in);
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
		scanner.close();
		return;
	}

}
