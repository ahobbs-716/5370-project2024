package edu.upenn.cis573.project;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class DataManager {

    private WebClient client;
    private HashMap<String, String> cache;
    public DataManager(WebClient client) {
        this.client = client;
        cache = new HashMap<>();
    }


    /**
     * Attempt to log in to the Contributor account using the specified login and password.
     * This method uses the /findContributorByLoginAndPassword endpoint in the API
     * @return the Contributor object if successfully logged in, null otherwise
     */
    public Contributor attemptLogin(String login, String password) {

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            String response = client.makeRequest("/findContributorByLoginAndPassword", map);

            JSONObject json = new JSONObject(response);
            System.out.println(json);
            String status = (String)json.get("status");

            if (status.equals("success")) {
                JSONObject data = (JSONObject)json.get("data");
                String id = (String)data.get("_id");
                String name = (String)data.get("name");
                String email = (String)data.get("email");
                String creditCardNumber = (String)data.get("creditCardNumber");
                String creditCardCVV = (String)data.get("creditCardCVV");
                String creditCardExpiryMonth = ((Integer)data.get("creditCardExpiryMonth")).toString();
                String creditCardExpiryYear = ((Integer)data.get("creditCardExpiryYear")).toString();
                String creditCardPostCode = (String)data.get("creditCardPostCode");

                Contributor contributor = new Contributor(id, name, email, creditCardNumber, creditCardCVV, creditCardExpiryYear, creditCardExpiryMonth, creditCardPostCode);

                List<Donation> donationList = new LinkedList<>();

                JSONArray donations = (JSONArray)data.get("donations");

                for (int i = 0; i < donations.length(); i++) {

                    JSONObject jsonDonation = donations.getJSONObject(i);

                    String fund = getFundName((String)jsonDonation.get("fund"));
                    String date = (String)jsonDonation.get("date");
                    long amount = (Integer)jsonDonation.get("amount");

                    Donation donation = new Donation(fund, name, amount, date);
                    donationList.add(donation);

                }

                contributor.setDonations(donationList);

                return contributor;

            }

            return null;

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the name of the fund with the specified ID using the /findFundNameById endpoint
     * @return the name of the fund if found, "Unknown fund" if not found, null if an error occurs
     */
    public String getFundName(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            String response = client.makeRequest("/findFundNameById", map);

            JSONObject json = new JSONObject(response);
            String status = (String)json.get("status");

            if (status.equals("success")) {
                String name = (String)json.get("data");
                cache.put(id, name);
                return name;
            }
            else {
                String name = "Unknown Fund";
                cache.put(id, name);
                return name;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get information about all of the organizations and their funds.
     * This method uses the /allOrgs endpoint in the API
     * @return a List of Organization objects if successful, null otherwise
     */
    public List<Organization> getAllOrganizations() {
        try {
            Map<String, Object> map = new HashMap<>();
            String response = client.makeRequest("/allOrgs", map);

            JSONObject json = new JSONObject(response);
            String status = (String)json.get("status");

            if (status.equals("success")) {

                List<Organization> organizations = new LinkedList<>();

                JSONArray data = (JSONArray)json.get("data");

                for (int i = 0; i < data.length(); i++) {

                    JSONObject obj = data.getJSONObject(i);

                    String id = (String)obj.get("_id");
                    String name = (String)obj.get("name");

                    Organization org = new Organization(id, name);

                    List<Fund> fundList = new LinkedList<>();

                    JSONArray array = (JSONArray)obj.get("funds");

                    for (int j = 0; j < array.length(); j++) {

                        JSONObject fundObj = array.getJSONObject(j);

                        id = (String)fundObj.get("_id");
                        name = (String)fundObj.get("name");
                        long target = (Integer)fundObj.get("target");
                        long totalDonations = (Integer)fundObj.get("totalDonations");

                        Fund fund = new Fund(id, name, target, totalDonations);

                        fundList.add(fund);

                    }

                    org.setFunds(fundList);

                    organizations.add(org);

                }

                return organizations;

            }

            return null;

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Make a donation to the specified fund for the specified amount.
     * This method uses the /makeDonation endpoint in the API
     * @return true if successful, false otherwise
     */
    public boolean makeDonation(String contributorId, String fundId, String amount) {

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("contributor", contributorId);
            map.put("fund", fundId);
            map.put("amount", amount);
            String response = client.makeRequest("/makeDonation", map);
            
            JSONObject json = new JSONObject(response);
            String status = (String)json.get("status");

            return status.equals("success");

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void updatePassword(String id, String currentPassword, String newPassword) {

        //defensive programming checks
        if (client == null) {
            throw new IllegalStateException("The internal communication has catastrophically failed. This is " +
                    "unlikely to resolve itself");
        }

        if (newPassword == null || currentPassword == null || id == null) {
            throw new IllegalArgumentException("Error in the password or the ID");
        }

        //make the request to the RESTful API
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("currentPassword", currentPassword);
        map.put("newPassword", newPassword);
        String response = client.makeRequest("/changePassword", map);

        //check that the request has been successful
        String status;
        JSONObject json;


        try {

            //set up the parser
            json = new JSONObject(response);
            status = (String) json.get("status");
        } catch (JSONException | IllegalStateException e) {
            throw new IllegalStateException("Error in communicating with server.");
        }
            //check if a 'success' response
        if (status.equals("success")) {
            return;
        } else {
            String error;
            try {
                error = (String) json.get("message");
            } catch (JSONException | IllegalStateException e) {
                throw new IllegalStateException();
            }
            if (error.equals("Current password is incorrect")) {
                throw new IllegalArgumentException("Current password was incorrect");
            }

            throw new IllegalStateException();
        }


    }


}
