import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class DataManager_changeOrgInfo_Test {
    @Test
    public void successfulChange() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/updateOrg")) {
                    return "{\"status\":\"success\",\"data\":\"name\"}";
                }
                return null;
            }


        });


        assert(dm.changeOrgInfo("Name", "Description", "Id"));


    }


    @Test
    public void failedChange() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/updateOrg")) {
                    return "{\"status\":\"failure\"}";
                }
                return null;
            }


        });


        assertFalse(dm.changeOrgInfo("Name", "Description", "Id"));


    }


    @Test(expected=IllegalStateException.class)
    public void errorChange() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/updateOrg")) {
                    return "{\"status\":\"error\"}";
                }
                return null;
            }


        });


        assertFalse(dm.changeOrgInfo("Name", "Description", "Id"));


    }


    @Test(expected=IllegalStateException.class)
    public void nullClient() {


        ByteArrayOutputStream error = new ByteArrayOutputStream();
        System.setErr(new PrintStream(error));


        DataManager dm = new DataManager(null);
        dm.changeOrgInfo("Name", "Description", "Id");
    }


    @Test(expected=IllegalArgumentException.class)
    public void nullInputs() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/updateOrg")) {
                    return "{\"status\":\"success\",\"data\":\"name\"}";
                }
                return null;
            }


        });
        dm.changeOrgInfo(null, "Description", "Id");




    }


    @Test(expected=IllegalArgumentException.class)
    public void emptyInputs() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/updateOrg")) {
                    return "{\"status\":\"success\",\"data\":\"name\"}";
                }
                return null;
            }


        });
        dm.changeOrgInfo("", "", "Id");




    }


    @Test(expected=IllegalStateException.class)
    public void clientError() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException();
            }


        });
        dm.changeOrgInfo("Name", "Description", "Id");




    }


    @Test(expected=IllegalStateException.class)
    public void clientReturnsNull() {


        DataManager dm = new DataManager(new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }


        });
        dm.changeOrgInfo("Name", "Description", "Id");




    }
}
