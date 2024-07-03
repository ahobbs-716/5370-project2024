import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_createFund_Test {

	@Test
	public void testSuccessfulCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

			}

		});

		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());
		
	}

	@Test
	public void testJSONFailureInCreateFund() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failure\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

			}

		});

		assertEquals(null,dm.createFund("12345", "new fund", "this is the new fund", 10000));

	}


	@Test(expected = IllegalStateException.class)
	public void testJSONNullInCreateFund() {		//assume that this is ok

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;

			}
		});

        dm.createFund("12345", "new fund", "this is the new fund", 10000);

	}


}
