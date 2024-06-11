Summary of team members and changes to code:

Alice

1.1
Completed createLogin() test suite
Added createFund() tests and getContributions() test
Tests updated to reflect tasks from rest of team (esp. Where exception behaviors have been added)

1.2
Changed line 43, DataManager: correction to spelling of ‘description’ 

Nico 

1.3
The organization app menu will now gracefully handle non integers and values out of range. It will also close on entering “quit” or “q” (case sensitive). This is done in the UserInterface start method.

1.4
The organization app now displays the total donations when a fund is chosen. It also shows the percentage of the goal raised, rounded to the nearest integer. If this rounding gives 100% but the total is still under the goal “>99%” is shown. This is done in the UserInterface stdisplayFund method.

Ijeoma

1.8
The UserInterface createFund method now does not accept blank fund name or description. It does not accept negative values for target fund and handles non numeric values. For these “wrong inputs” it prompts the user to re-enter a valid input

1.9
The DataManager.attemptLogin() throws an IllegalStateException when an error occurs and null if it is unsuccessful. Similarly, in UserInterface when an IllegalStateException is thrown it displays an error with server message

Sam

1.6
Updated ViewDonationsActivity to include a double called total. Changed the size to include 2 extra spaces, to ensure a gap between donations and total. Displayed the total to two decimal places

1.7
Changed the tostring method of Donation to parse the date in the provided form, and return the date as Month, day, Year. 


Remaining bugs (whole team)
Potential bug: “/findContributrNameByID (line 109 DataManager) [have not fixed as unclear as to whether this is expected behavior and assume will test with makeRequest]

Any changes to start routine required (whole team)
None


Full google doc for report: https://docs.google.com/document/d/1hQDdIbdWBmGO_chOH03qEONVQ6wT_ZzrKLtwXhNXTkM/edit

