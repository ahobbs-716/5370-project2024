import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataManager_MakeDonation_Test {



    @Test
    public void successfulDonation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/makeDonation")) {
                    return "{\"status\":\"success\",\"message\":\"Donation successful\"}";
                }
                return null;
            }
        });

        assertTrue(dm.makeDonation("6672ed0f926b0b73131c05f6", "66664ba1f39985059eb2", "200"));
    }

    @Test
    public void invalidContributor() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/makeDonation")) {
                    return "{\"status\":\"error\",\"message\":\"Invalid contributor ID\"}";
                }
                return null;
            }
        });

        assertFalse(dm.makeDonation("invalidContributor", "66664ba1f39985059eb2", "200"));
    }




    @Test
    public void nonNumericAmount() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/makeDonation")) {
                    return "{\"status\":\"error\",\"message\":\"Invalid amount\"}";
                }
                return null;
            }
        });


        dm.makeDonation("6672ed0f926b0b73131c05f6", "66664ba1f39985059eb2", "abc");

    }

    @Test
    public void negativeAmount() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/makeDonation")) {
                    return "{\"status\":\"error\",\"message\":\"Negative amount\"}";
                }
                return null;
            }
        });

        assertFalse(dm.makeDonation("6672ed0f926b0b73131c05f6", "66664ba1f39985059eb2", "-100"));
    }

    @Test
    public void serverError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/makeDonation")) {
                    return "{\"status\":\"error\",\"message\":\"Server error\"}";
                }
                return null;
            }
        });

        assertFalse(dm.makeDonation("6672ed0f926b0b73131c05f6", "66664ba1f39985059eb2", "200"));
    }


//    @Test
//    public void successfulChange() {
//
//
//        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
//
//
//            @Override
//            public String makeRequest(String resource, Map<String, Object> queryParams) {
//                if (resource.equals("/makeDonation")) {
//                    return "{\"status\":\"success\",\"data\":\"name\"}";
//                }
//                return null;
//            }
//
//
//        });
//
//
////        assert(dm.changeOrgInfo("Name", "Description", "Id"));
//        assert(dm.makeDonation("6672ed0f926b0b73131c05f6", "66664ba1f39985059eb2", "200"));
//
//
//    }
}

