package com.landry.aws.lambda.lcadapter.lcclient;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landry.aws.lambda.dynamo.domain.LCOAuthClient;
import com.landry.aws.lambda.lcadapter.oauthclient.Secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthClientTokenRefresher
{

    private static Logger logger = LoggerFactory.getLogger(OAuthClientTokenRefresher.class);

	private ObjectMapper objectMapper = new ObjectMapper();
	
	private RefreshTokenBean refreshTokenBean= null;

	private String refreshUrl="https://cloud.merchantos.com/oauth/access_token.php";

	private HttpsURLConnection uc;

	private InputStream inputStream;

	public OAuthClientTokenRefresher()
	{
		super();
	}

	public void refreshToken(LCOAuthClient client) throws LCDownException
	{
		try
		{
				setUpRefreshConnection(client.getClientId(), Secure.CLIENT_SECRET, client.getRefreshToken());
				inputStream = uc.getInputStream();
				processInputStream();
				logger.info("Updating token to " + refreshTokenBean.getAccessToken()); 
				client.setToken(refreshTokenBean.getAccessToken());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(e.getMessage());
			throw new LCDownException(e.getMessage());
		}

	}

	private  void processInputStream() throws LCDownException, Exception
	{

		if (inputStream == null)
			throw new LCDownException("Input stream is null. LC is probably unavailable.");

		refreshTokenBean = objectMapper.readValue(inputStream, RefreshTokenBean.class);

	}

	private HttpsURLConnection setUpRefreshConnection( String clientId, String clientSecret, String refreshToken )
			throws Exception
	{
		String payload = "client_id=" + clientId  + "&"
				+ "grant_type=refresh_token" + "&"
				+ "client_secret=" + clientSecret + "&"
				+ "refresh_token=" + refreshToken;
		logger.info("Setting up the refresh endpoint to:\n" + payload);

		URL lc = null;

		logger.info("Refreshing token...");
		lc = new URL(refreshUrl);
		uc = (HttpsURLConnection) lc.openConnection();
		uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod("POST");
		uc.setDoOutput(true);
		uc.setDoInput(true);
		OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
		return uc;
	}

}