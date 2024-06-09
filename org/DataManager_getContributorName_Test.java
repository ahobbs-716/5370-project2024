import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataManager_getContributorName_Test {

    @Test
    public void getContributorNameExceptionTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

            }

        });

        assertNull(dm.getContributorName("data"));

    }


    @Test
    public void getContributorNameSuccessTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"name\"}";

            }

        });

        assertEquals("name",dm.getContributorName("name"));

    }

    @Test
    public void getContributorNameInvalidTest() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\",\"data\":\"name\"}";

            }

        });

        assertEquals(null, dm.getContributorName("not_a_name"));

    }
}
