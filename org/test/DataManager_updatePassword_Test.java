import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.*;

public class DataManager_updatePassword_Test {

    @Test (expected = IllegalStateException.class)
    public void nullClientPasswordUpdateTest() {

        DataManager dm = new DataManager(null);

        dm.updatePassword("6672d769c286ce21bc8ceba5", "2");

    }

    @Test (expected = IllegalArgumentException.class)
    public void nullIDTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"6672348ea7071e4ecc0983fb\",\"login\":\"1\",\"password\":\"2\",\"name\":\"1\",\"description\":\"1\",\"funds\":[],\"__v\":0}}";
            };

        });

        dm.updatePassword(null, "2");

    }

    @Test (expected = IllegalArgumentException.class)
    public void nullNewPasswordTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"6672348ea7071e4ecc0983fb\",\"login\":\"1\",\"password\":\"2\",\"name\":\"1\",\"description\":\"1\",\"funds\":[],\"__v\":0}}";
            };

        });

        dm.updatePassword("6672d769c286ce21bc8ceba5", null);

    }

    @Test (expected = IllegalStateException.class)
    public void unsuccessfulAPIResponseTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"data\":{\"_id\":\"6672348ea7071e4ecc0983fb\",\"login\":\"1\",\"password\":\"2\",\"name\":\"1\",\"description\":\"1\",\"funds\":[],\"__v\":0}}";
            };

        });

        dm.updatePassword("6672d769c286ce21bc8ceba5", "2");

    }

    @Test
    public void successfulPasswordUpdateTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"6672348ea7071e4ecc0983fb\",\"login\":\"1\",\"password\":\"2\",\"name\":\"1\",\"description\":\"1\",\"funds\":[],\"__v\":0}}";
            };

        });

        assertTrue(dm.updatePassword("6672d769c286ce21bc8ceba5", "3"));

    }

}
