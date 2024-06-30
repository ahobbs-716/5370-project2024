import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

public class WebClient {
	
	private String host;
	private int port;
	
	public WebClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Make an HTTP request to the RESTful API at the object's host:port
	 * The request will be of the form http://[host]:[port]/[resource]?
	 * followed by key=value& for each of the entries in the queryParams map.
	 * @return the JSON object returned by the API if successful, null if unsuccessful
	 */
	public String makeRequest(String resource, Map<String, Object> queryParams) {
		
		try {
		
			String request = "http://" + host + ":" + port + resource + "?";
			
			for (String key : queryParams.keySet()) {
				request += key + "=" + queryParams.get(key).toString().replaceAll(" ", "%20") + "&";
				
			}
			System.out.println(request);
			//System.out.println("Web Client request: " + request);
			
			URL url = new URL(request);
		    url.openConnection(); 
	    	Scanner in = new Scanner(url.openStream());
	    	String response = "";
	    	while (in.hasNext()) {
	    		String line = in.nextLine();
	    		response += line;
	    	}

	    	//System.out.println("Web Client response: " + response);
	    	
	    	in.close();

	    	return response;
			
			
		}
		catch (Exception e) {
			return null;
		}
		
	}
	public HttpResponse<String> makePostRequest(String resource, Map<String, Object> queryParams){
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder().
                    uri(new URI(resource))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(queryParams.toString())).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
			try {
				throw new RuntimeException(e);
			} catch (RuntimeException ex) {
				throw new RuntimeException(ex);
			} finally {
			}
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
