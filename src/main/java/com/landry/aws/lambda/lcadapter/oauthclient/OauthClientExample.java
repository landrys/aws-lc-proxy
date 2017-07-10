package com.landry.aws.lambda.lcadapter.oauthclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Example of the OAuth client credentials flow using the Apache Oltu OAuth2 client.
 */
public class OauthClientExample {
	private HttpsURLConnection uc;

    /**
     * URL for requesting OAuth access tokens.
     */
    public static final String TOKEN_REQUEST_URL = "https://cloud.merchantos.com/oauth/access_token.php";

	public static final String REFRESH_TOKEN = "5ef98f0f218db86ba106b4c74e3c7a162369af61";
	/**
	 * Client ID of your client credential. Change this to match whatever
	 * credential you have created.
	 */
    public static final String CLIENT_ID = "lcproxy";

    /**
     * Client secret of your client credential.  Change this to match whatever credential you have created.
     */
    public static final String CLIENT_SECRET =
            "le0nB1kes";

    /**
     * Account on which you want to request a resource. Change this to match the account you want to
     * retrieve resources on.
     */
    public static final String ACCOUNT_ID = "58217";

    /**
     * URL from which you are going to request a resource.  The example below is for the Analytics
     * resource server. :account-id will be replaced with {@link ACCOUNT_ID} below.
     */
	public static final String RESOURCE_URL_TPL =  "https://api.merchantos.com/API/Account/30427/Vendor";
//            "https://analytics.api.brightcove.com/v1/data?accounts=:account-id&dimensions=video";

    /**
     * Request a fresh access token using the given client ID, client secret, and token request URL,
     * then request the resource at the given resource URL using that access token, and get the resource
     * content.  If an exception is thrown, print the stack trace instead.
     *
     * @param args Command line arguments are ignored.
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	OauthClientExample oac = new OauthClientExample();
    	oac.shit3();
    }

 /*  
	private void shit4() throws IOException, ProtocolError, ProtocolException
{

		// If you want to continue this got to https://github.com/dmfs/oauth2-essentials
//	org.apache.http.HttpRequest url;

	HttpRequestExecutor executor = new HttpUrlConnectionExecutor();

	OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
		    URI.create("http://example.com/auth"),
		    URI.create("http://example.com/token"),
		    new Duration(1,0,3600) );

	// Create OAuth2 client credentials
	OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(
	    "client-id", "client-password");
	
	// Create OAuth2 client
	OAuth2Client client = new BasicOAuth2Client(
	    provider,
	    credentials,
	    new LazyUri(new Precoded("http://localhost")));
	
	OAuth2AccessToken token = new TokenRefreshGrant(client, oldToken).accessToken(executor);
	
	
	
//	OAuth2AccessToken token = new OAuth2AccessToken("f092d9910a412f0375f365fbe7b96acf");
//	OAuth2AccessToken token = new Tok

//	result = executor.execute(url, new BearerAuthenticatedRequest(request, token));

	
}
*/


	private void shit3() throws Exception
{
	String apiCommand ="https://api.merchantos.com/API/Account/30427/Item.json?itemID=17124";
	HttpsURLConnection con =	setUpApiConnection(apiCommand, "GET");
	System.out.println(con.toString());
	System.out.println(con.getURL().toString());
	InputStream is = con.getInputStream();
	BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    String line = null;
    while ((line = r.readLine()) != null) {
        System.out.println(line);
    }
	
}

	/*
	private void shit2() throws OAuthSystemException, OAuthProblemException
{
	// TODO Auto-generated method stub
		// SEE https://cwiki.apache.org/confluence/display/OLTU/OAuth+2.0+Client+Quickstart
		 // Maybe try demo that may shed some light...
		// I GIVE UP!!!!
	
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
//		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest("https://api.merchantos.com/API/Account/30427/Item")
//		         .setAccessToken("f092d9910a412f0375f365fbe7b96acf").buildHeaderMessage();

		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest("https://api.merchantos.com/API/Account/30427/Item")
		         .buildQueryMessage();

		         //.setAccessToken("28bdfd3b20fecaf844e8e3e8fa97c3a6ff55436c").buildHeaderMessage();
//	bearerClientRequest.setHeader("Content-Type", "application/vnd.merchantos.pos-v1+xml");
//	bearerClientRequest.setHeader("Content-Type", "application/json");
	bearerClientRequest.setHeader("Authorization", "OAuth f092d9910a412f0375f365fbe7b96acf");
	System.out.println(bearerClientRequest.getLocationUri());
	System.out.println(bearerClientRequest.getBody());
		System.out.println(bearerClientRequest.getHeaders());
		OAuthResourceResponse resourceResponse = null;
		resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
		System.out.println(resourceResponse.getContentType());
		System.out.println(resourceResponse.getBody());
		System.out.println(resourceResponse.getHeaders());
}

*/
	private void shit() throws Exception
	{
		setUpRefreshConnection();
		InputStream inputStream = uc.getInputStream();
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = null;
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }
		
	}
	
	
	private HttpsURLConnection setUpRefreshConnection() throws Exception
	{
		String payload = "client_id=" + CLIENT_ID  + "&"
				+ "grant_type=refresh_token" + "&"
				+ "client_secret=" + CLIENT_SECRET + "&"
				+ "refresh_token=" + REFRESH_TOKEN;

		System.out.println(payload);

		URL lc = null;

		lc = new URL(TOKEN_REQUEST_URL);
		uc = (HttpsURLConnection) lc.openConnection();
		//uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod("POST");
		uc.setDoOutput(true);
		uc.setDoInput(true);
		OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
		return uc;
	}

	private HttpsURLConnection setUpApiConnection(String apiCommand, String requestMethod) throws Exception {

		URL lc = null;
		HttpsURLConnection uc = null;

		lc = new URL(apiCommand);
		uc = (HttpsURLConnection) lc.openConnection();
		uc.setRequestProperty("Authorization", "OAuth " + "f092d9910a412f0375f365fbe7b96acf");
		//uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod( requestMethod );
        uc.setDoOutput(true);
        uc.setDoInput(true); 
        uc.setRequestProperty ( "Accept", "application/json");
        return uc;
	}


}