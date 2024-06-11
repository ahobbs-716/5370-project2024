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
                return "{\"status\":\"success\",\"data\":{\"_id\":\"6661df6d04a990204448071c\",\"login\":\"ah716\",\"password\":\"ah716passcode\",\"name\":\"AliceOrganisation\",\"description\":\"Alice's Organisation\",\"funds\":[{\"target\":1000,\"_id\":\"6666133edafd6265e01f090c\",\"name\":\"fund1\",\"description\":\"Alice's fund\",\"org\":\"6661df6d04a990204448071c\",\"donations\":[{\"_id\":\"66661349dafd6265e01f0913\",\"fund\":\"6666133edafd6265e01f090c\",\"date\":\"2024-06-09T20:40:41.040Z\",\"amount\":1001,\"__v\":0}],\"__v\":0}],\"__v\":0}}";
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

    @Test(expected=IllegalStateException.class)
    public void invalidLoginExceptionTest() {

        ByteArrayOutputStream error = new ByteArrayOutputStream();
        System.setErr(new PrintStream(error));

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;

            }

        });
        dm.attemptLogin(null, null);
    }

    @Test
    public void fundedLoginTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"6661df6d04a990204448071c\",\"login\":\"ah716\",\"password\":\"ah716passcode\",\"name\":\"AliceOrganisation\",\"description\":\"Alice's Organisation\",\"funds\":[{\"target\":1000,\"_id\":\"6666133edafd6265e01f090c\",\"name\":\"fund1\",\"description\":\"Alice's fund\",\"org\":\"6661df6d04a990204448071c\",\"donations\":[{\"_id\":\"66661349dafd6265e01f0913\",\"fund\":\"6666133edafd6265e01f090c\",\"date\":\"2024-06-09T20:40:41.040Z\",\"amount\":1001,\"__v\":0}],\"__v\":0}],\"__v\":0}}";
            }

        });

        Organization org = dm.attemptLogin("login", "passcode");

        assertNotNull(org);

        //test orgs
        assertEquals("6661df6d04a990204448071c", org.getId());
        assertEquals("AliceOrganisation", org.getName());
        assertEquals("Alice's Organisation", org.getDescription());

        //test funds
        assertEquals(1, org.getFunds().size());
        Fund fund = (Fund)org.getFunds().getFirst();
        assertEquals("6666133edafd6265e01f090c", fund.getId());
        assertEquals("fund1", fund.getName());
        assertEquals("Alice's fund", fund.getDescription());
        assertEquals(1, fund.getDonations().size());

        //test donations
        Donation donation = fund.getDonations().getFirst();
        assertEquals("6666133edafd6265e01f090c", donation.getFundId());
        assertEquals(1001, donation.getAmount());
        assertEquals("2024-06-09T20:40:41.040Z", donation.getDate());

    }

}
