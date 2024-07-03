import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.NoSuchElementException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

public class DataManager_deleteFund_Test {

    @Test
    public void testDeleteFund_successfulDeletion() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"," +
                        "\"data\":" +
                        "{\"target\":2," +
                        "\"_id\":\"6672d769c286ce21bc8ceba5\"," +
                        "\"name\":\"2\"," +
                        "\"description\":\"2\"," +
                        "\"org\":\"6672348ea7071e4ecc0983fb\"," +
                        "\"donations\":[]," +
                        "\"__v\":0}}";
            };
        });

        assertTrue(dm.deleteFund("6672d769c286ce21bc8ceba5"));

    }

    @Test (expected = IllegalStateException.class)
    public void TestDeleteFund_TestClientIsNull() {

        DataManager dm = new DataManager(null);
        dm.deleteFund("6672d769c286ce21bc8ceba5");

    }

    @Test (expected = IllegalStateException.class)
    public void TestDeleteFund_IssueWithAPI() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"," +
                        "\"data\":" +
                        "{\"target\":2," +
                        "\"_id\":\"6672d769c286ce21bc8ceba5\"," +
                        "\"name\":\"2\"," +
                        "\"description\":\"2\"," +
                        "\"org\":\"6672348ea7071e4ecc0983fb\"," +
                        "\"donations\":[]," +
                        "\"__v\":0}}";
            }

            ;

        });

        dm.deleteFund("6672d769c286ce21bc8ceba5");
    }

    @Test(expected=IllegalStateException.class)
    public void testDeleteFund_WebClientCannotConnectToServer() {

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        // this assumes no server is running on port 3002
        DataManager dm = new DataManager(new WebClient("localhost", 3002));
        dm.deleteFund("6672d769c286ce21bc8ceba5");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testDeleteFund_WebClientIsNull() {

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        // this assumes no server is running on port 3002
        DataManager dm = new DataManager(null);
        dm.deleteFund("6672d769c286ce21bc8ceba5");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient cannot connect to server");

    }

    @Test(expected=IllegalStateException.class)
    public void testDeleteFund_WebClientReturnsNull() {

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.deleteFund("6672d769c286ce21bc8ceba5");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns null");

    }

    @Test(expected=IllegalStateException.class)
    public void testDeleteFund_ReturnsError() {

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.deleteFund("6672d769c286ce21bc8ceba5");

    }

    @Test(expected=IllegalStateException.class)
    public void testDeleteFund_WebClientReturnsMalformedJSON() {

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.deleteFund("6672d769c286ce21bc8ceba5");
        fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns malformed JSON");

    }

}

