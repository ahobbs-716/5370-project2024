Summary of team members and changes to code:

Ijeoma

2.1
Modified DataManager class to have a private field, cache. Before making a request to the API, we check if this already exists in the cache if it does then we can return without having to make a call, if it doesn;t we make the request and add that id to the cache


2.9 

Implemented a listAllDonations method which loops through funds and all donations to a bigger list. The list is then sorted in descending order. In addition, modified the UserInterface class to accommodate another option which lets user see all the donations that exist in that Organization by referencing the listAllDonations method

Nico

2.2
Defensive programming was added to throw appropriate errors when applicable, and the user is given a description and prompted if they want to retry. The test cases were also updated.

2.4
Modified DataManager to have a cache as a hashmap. Before requesting the fund name, it checks the cache. And if the cache does not have the ID, it will update the cache.


2.8
You are now able to log out and log into a new user. The logout prompt comes up with the fund selection prompt. 


Alice

2.7
DataManager.java now includes a deleteFund method that calls the RESTful API /deleteFund endpoint.
Organization.java includes a deleteFund() function to delete a fund object from the funds list.
UserInterface.java includes additional prompts to enable the user to delete a fund when viewing information about that fund.
Change to fund menu to be more specific about how to get back to all funds, to improve useability.
Testing included for deleteFund_class


Sam

2.3
Added the functionality to see the aggregate contributor information in sorted order.




Full google doc for report: https://docs.google.com/document/d/1hQDdIbdWBmGO_chOH03qEONVQ6wT_ZzrKLtwXhNXTkM/edit

