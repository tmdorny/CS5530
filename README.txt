PHASE 2 SUBMISSION
Tim Dorny and Alexander Koumandarakis

	Ran into some unexpected delays so unfortunately our testing was fairly limited. We have 2 main users in the database: tim (password: 123) and Alex (password" password 1).
I struggled to properly adopt my (Tim's) implementation of the 'addReservation' function to work nicely with the "queue desired reservations and record them all at the end of the session".
It is currently implemented as a single function that displays the available dates for a specified TH and allows the user to enter their desired reservation. It then updates the period to accomodate the
newly unavailable section that was just reserved. It then adds the reservation to the DB. I had issues getting the multiple database edits to work with the queueing functionality.
	For the browsing function, the current "setup" in the database for testing its accuracy is as follows: Login as tim, (pass: 123) enter the browse ($b) command. Use 50 as the lower price bound and 70 as the high price bound.
The name of the "state/city" field to look for is "abc" (address is 123 abc street), keyword to match is 'spicy', and the category is Duplex. Sort by price and the TH with hid: 1 should be displayed, as it matches all of the conditions.
	The suggestion function runs on manually entered 'Visit' entries. It successfully suggests THs that have been visited by people that have also visited the user's selected TH.
All of the commands function at least to some degree. The accuracy of some is not perfect, but the functionality is there. We implemented the application to operate in the console. A list of available commands can be found in the main section of the code. Most commands rely on user input into the console