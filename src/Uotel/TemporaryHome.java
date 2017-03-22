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

}
