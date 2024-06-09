import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_attemptLogin_Test {

    @Test
    public void successfulLoginTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"," +
                        "\"data\":{" +
                        "\"_id\":\"12345\"," +
                        "\"name\":\"new fund\"," +
                        "\"description\":\"this is the new fund\"," +
                        "\"funds\"[" +
                        "{" +
                        "\"_id\":\"1029\"" +
                        "\"name\":\"fund1\"" +
                        "\"description\":\"the first fund\"" +
                        "\"target\":10000" +
                        "\"donations\"[" +
                        "{" +
                        "\"contributorID\":\"23433\"" +
                        "\"contributor\":\"person1\"" +
                        "\"amount\":1000" +
                        "\"date\":\"01/01/2024\"" +
                        "}" +
                        "]" +
                        "}" +
                        "]" +
                        "\"target\":10000" +
                        "\"org\":\"5678\"," +
                        "\"donations\":[]," +
                        "\"__v\":0}" +
                        "}";

            }

        });

        assertNotNull(dm.attemptLogin("login", "passcode"));

    }

    @Test
    public void failureLoginTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\",}";

            }

        });

        assertNull(dm.attemptLogin("login", "password"));

    }

    @Test
    public void invalidLoginExceptionTest() {

        ByteArrayOutputStream error = new ByteArrayOutputStream();
        System.setErr(new PrintStream(error));

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;

            }

        });

        assertNull(dm.attemptLogin(null, null));
        assertEquals("javalangNullPointerExceptionCannotinvokeStringlengthbecausesisnullatjavabasejava", error.toString().replaceAll("[^a-zA-Z0-9]", "").substring(0,80));

    }

    @Test
    public void fundedLoginTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"," +
                        "\"data\":{" +
                            "\"_id\":\"12345\"," +
                            "\"name\":\"organisation1\"," +
                            "\"description\":\"this is organisation1\"," +
                            "\"funds\"[" +
                                "{" +
                                    "\"_id\":\"1029\"" +
                                    "\"name\":\"fund1\"" +
                                    "\"description\":\"the first fund\"" +
                                    "\"target\":10000" +
                                    "\"donations\"[" +
                                        "{" +
                                            "\"fundID\":\"1029\"" +
                                            "\"contributorName\":\"person1\"" +
                                            "\"amount\":1000" +
                                            "\"date\":\"01/01/2024\"" +
                                        "}" +
                                    "]" +
                                "}" +
                            "]" +
                            "\"target\":10001" +
                            "\"org\":\"5678\"," +
                            "\"donations\":[]," +
                            "\"__v\":0}" +
                        "}";

            }

        });

        Organization org = dm.attemptLogin("login", "passcode");

        assertNotNull(org);

        //test orgs
        assertEquals("12345", org.getId());              //assume id should be string
        assertEquals("organisation1", org.getName());
        assertEquals("this is organisation1", org.getDescription());

        //test funds
        assertEquals(1, org.getFunds().size());
        Fund fund = (Fund)org.getFunds().getFirst();
        assertEquals("1029", fund.getId());
        assertEquals("fund1", fund.getName());
        assertEquals("the first fund", fund.getDescription());
        assertEquals(1, fund.getDonations().size());

        //test donations
        Donation donation = fund.getDonations().getFirst();
        assertEquals("1029", donation.getFundId());   //shouldn't this be donationID?
        assertEquals("person1", donation.getContributorName());
        assertEquals(1000, donation.getAmount());
        assertEquals("01/01/2024", donation.getDate());

    }

}
