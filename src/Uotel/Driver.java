package Uotel;

import java.util.Scanner;

public class Driver {
	
	public static void main(String[] args){
		System.out.println("Welcome to Uotel!");
		Scanner scanner = new Scanner (System.in);
		System.out.println("Enter Username or '$r' to register:");
		String user = scanner.nextLine();
		if (user.equals("$r")){
			// TODO handle registration
			int name = 0;
			System.out.println("Enter desired username:");
			while (name == 0){
				String desiredName = scanner.nextLine();
				// check desiredName for uniqueness in DB
				// placeholder if/else for DB check
				int result = 1;
				if (result == 0){
					System.out.println("Desired username is not available, please try again:");
				}
				else {
					break;
				}
			}
			while (true){
				System.out.println("Enter password:");
				String newPass = scanner.nextLine();
				System.out.println("Verify password:");
				String passVerif = scanner.nextLine();
				if (newPass.equals(passVerif)){
					break;
				}
				else {
					System.out.println("Passwords do not match... Please try again.");
				}
			}
			// Enter new username and password into database
			
			
		}
		else {
			System.out.println("Welcome " + user + ", please enter your password:");
			String pass = scanner.nextLine();
			// TODO check pass against db
			System.out.println("You entered " + pass + ", that's great!");
		}
		scanner.close();
		// user/pass verified or created
	}

}
