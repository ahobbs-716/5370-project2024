
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataManager {

	private final WebClient client;

	public DataManager(WebClient client) {
		this.client = client;
	}

	/**
	 * Attempt to log the user into an Organization account using the login and password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {

		if (login == null || password == null) {
			throw new IllegalArgumentException();
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {

				//get the JSON object details
				JSONObject data = (JSONObject)json.get("data");
				String fundId = (String)data.get("_id");
				String name = (String)data.get("name");
				String description = (String)data.get("description");
				Organization org = new Organization(fundId, name, description);

				//and the funds
				JSONArray funds = (JSONArray)data.get("funds");

				//iterate through fund info
				Iterator it = funds.iterator();
				while(it.hasNext()){
					JSONObject fund = (JSONObject) it.next(); 
					fundId = (String)fund.get("_id");
					name = (String)fund.get("name");
					description = (String)fund.get("description");
					long target = (Long)fund.get("target");

					//set up funds
					Fund newFund = new Fund(fundId, name, description, target);

					//iterate through info about each donation in the fund
					JSONArray donations = (JSONArray)fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while(it2.hasNext()){
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String)donation.get("fundID");		//check this
						String contributorName = (String)donation.get("contributorName");		//issue?
						long amount = (Long)donation.get("amount");
						String date = (String)donation.get("date");

						//and add to the donation list
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					//associate donations with the fund
					newFund.setDonations(donationList);

					//and the fund with the organisation
					org.addFund(newFund);

				}

				return org;
			} else if (status.equals("error")) {
				throw new IllegalStateException("Error in communicating with server");
			} else return null;
		}
		catch (Exception e) {
//			e.printStackTrace();
			throw new IllegalStateException("Error in communicating with server", e);
//			return null;
		}
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * @return the name of the contributor on success; null if no contributor is found
	 */
	public String getContributorName(String id) {

		if (client == null) {
			throw new IllegalStateException();
		}

		if (id == null) {
			throw new IllegalArgumentException();
		}

		try {

			Map<String, Object> map = new HashMap<>();
			map.put("_id", id);
			String response = client.makeRequest("/findContributrNameById", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				String name = (String)json.get("data");
				return name;
			} else if (status.equals("error")) {
				throw new IllegalStateException("An error occurred in the database");
			} else return null;


		}
		catch (Exception e) {
			throw new IllegalStateException("Error in communicating with the server");
		}
	}

	/**
	 * This method creates a new fund in the database using the /createFund endpoint in the API
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {
		if (client == null) {
			throw new IllegalStateException("The internal communication has catastrophically failed. This is " +
					"unlikely to resolve itself");
		}
		if (orgId == null || name == null || description == null) {
			throw new IllegalArgumentException("Invalid data was given");
		}

		try {

			//create object to represent fund
			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);

			//feed this to the RESTful API as an http request
			String response = client.makeRequest("/createFund", map);

			//create parser
			String status;
			JSONObject json;
			try {
				JSONParser parser = new JSONParser();
				json = (JSONObject) parser.parse(response);
				status = (String) json.get("status");
			} catch (NullPointerException e) {
				throw new IllegalStateException("The request to the data base gave an invalid return");
			}
			//if successful, create the fund as a JSON object
			if (status.equals("success")) {
				JSONObject fund = (JSONObject)json.get("data");
				String fundId = (String)fund.get("_id");
				return new Fund(fundId, name, description, target);
			} else if (status.equals("error")) {
				throw new IllegalStateException("An error occurred in the database");
			} else return null;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("An unknown error occurred in trying to communicate with the database");
		}
	}


}
