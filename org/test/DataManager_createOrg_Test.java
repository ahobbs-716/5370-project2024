import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;

public class DataManager_createOrg_Test {

    @Test
    public void testSuccessfulCreation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"login\":\"testorg\",\"password\":\"testpass\",\"name\":\"Test Organization\",\"description\":\"This is a test organization\",\"funds\":[],\"__v\":0}}";
            }
        });

        Organization org = dm.createOrg("testorg", "testpass", "Test Organization", "This is a test organization");

        assertNotNull(org);
        assertEquals("12345", org.getId());
        assertEquals("Test Organization", org.getName());
        assertEquals("This is a test organization", org.getDescription());
        assertEquals("testpass", org.getPassword());
    }

    @Test
    public void testOrganizationCreationFailure() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"message\":\"Login already exists\"}";
            }
        });

        Organization org = dm.createOrg("existingorg", "testpass", "Test Organization", "This is a test organization");

        assertNull(org);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponseFromServer() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.createOrg("testorg", "testpass", "Test Organization", "This is a test organization");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrg(null, "testpass", "Test Organization", "This is a test organization");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPassword() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrg("testorg", null, "Test Organization", "This is a test organization");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrg("testorg", "testpass", null, "This is a test organization");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDescription() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrg("testorg", "testpass", "Test Organization", null);
    }
}