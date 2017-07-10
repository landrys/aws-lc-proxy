package com.landry.aws.lambda.lcadapter.lcclient;

import java.net.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.landry.aws.lambda.common.util.RestVerb;
import com.landry.aws.lambda.dynamo.domain.LCOAuthClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcApi
{
    private static Logger logger = LoggerFactory.getLogger(LcApi.class);

	private static ConcurrentHashMap<Integer, Integer> lCErrors = new ConcurrentHashMap<Integer, Integer>();
	static String LEAKY_BUCKET_INFO_HEADER_NAME = "X-LS-API-Bucket-Level";

	OAuthTokenGetter oAuthTokenGetter = new OAuthTokenGetter();

	private String commonURL = LcUtils.URL + "/" + LcUtils.PRODACCOUNT + "/";
	private String clientApiCommand;
	private String apiCommand;

	HttpsURLConnection uc;

	private int numOfCalls;

	public LcApi( String clientApiCommand)
	{
		super();
		logger.info("*** Instantiating LcApi ***");
		this.clientApiCommand = clientApiCommand;
		prepare();
	}

	public void getInputStreamForBucketLevelCheck( LCOAuthClient client ) throws Exception
	{
		handleGetInputStream(RestVerb.GET.name(), null, client);
	}

	public InputStreamWrapper getInputStream( String requestMethod, Object content )
			throws TooManyRequestsException, Exception
	{
		LCOAuthClient client = oAuthTokenGetter.getToken();
		return handleGetInputStream( requestMethod, content, client);
	}

	private InputStreamWrapper handleGetInputStream( String requestMethod, Object content, LCOAuthClient client ) throws  Exception
	{
        InputStreamWrapper isw = new InputStreamWrapper();

		try
		{
			isw = getInputStreamUsingToken(requestMethod, content, client);
		}
		catch (Exception e)
		{
			if (uc.getResponseCode() == 401)
			{
				OAuthClientTokenRefresher refresher = new OAuthClientTokenRefresher();
				refresher.refreshToken(client);
				try
				{
					isw = getInputStreamUsingToken( requestMethod, content, client);
				}
				catch (Exception e2)
				{
					getErrorStream(isw, uc, e2);
					trackErrors(uc.getResponseCode());
					isw.setResponseCode(uc.getResponseCode());
					isw.setUrlConnection(uc);
					System.out.println(uc.getResponseCode());
					System.out.println(e2.getMessage());
				}
			}

		}
		return isw;
		
	}

	private InputStreamWrapper getInputStreamUsingToken( String requestMethod, Object obj,
			LCOAuthClient client ) throws Exception
	{

		numOfCalls++;

		logger.info("The obj:requestMethod:apiCommand is: " + obj + ":" + requestMethod + ":" + apiCommand);

		if (apiCommand.contains("json"))
			uc = setUpApiConnection(requestMethod, "json", client.getToken());
		else
			uc = setUpApiConnection(requestMethod, "xml", client.getToken());

		if (obj != null)
			setUpForPUTOrPOST(obj, uc);

		InputStreamWrapper isw = new InputStreamWrapper();

		isw.setIs((InputStream) uc.getInputStream());

		if (uc.getHeaderField(LEAKY_BUCKET_INFO_HEADER_NAME) != null)
		{
			Integer bucketLevel = getBucketLevel(uc);
			client.setBucketLevel(bucketLevel);
			logger.info("Writing new bucket level(%full) " + bucketLevel + " to client: " +  client.getClientId());
     		OAuthClientBucketLevelChecker.repo.write(client);
		}

		isw.setResponseCode(uc.getResponseCode());
		isw.setUrlConnection(uc);
		return isw;
	}

	// TODO put in Dynamo table.
	private void trackErrors( int rc )
	{
		// Put into a hashMap to track Errors
		if (lCErrors.get(rc) == null)
		{
			lCErrors.put(rc, 1);
			// Running sum
			if (lCErrors.get(rc + 1000) != null)
			{
				int i = lCErrors.get(rc + 1000);
				lCErrors.put(rc + 1000, ++i);
			}
			else
			{
				lCErrors.put(rc + 1000, 1);
			}

		}
		else
		{
			int i = lCErrors.get(rc);
			lCErrors.put(rc, ++i);
			// Running sum
			int j = lCErrors.get(rc + 1000);
			lCErrors.put(rc + 1000, ++j);
		}
	}

	private void setUpForPUTOrPOST( Object obj, HttpsURLConnection uc ) throws Exception
	{
		if (obj != null)
		{
			OutputStream os = uc.getOutputStream();
			OutputStreamWriter wout = new OutputStreamWriter(os);
			// Need to decode URL encodings...
			obj = java.net.URLDecoder.decode((String) obj, "UTF-8");

			StringReader reader = new StringReader((String) obj);
			StringReader reader1 = new StringReader((String) obj);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			logger.info("PUT or POST: The content being sent is:");
			StreamResult result = new StreamResult(System.out);
			transformer.transform(new javax.xml.transform.stream.StreamSource(reader), result);
			StreamResult result1 = new StreamResult(wout);
			transformer.transform(new javax.xml.transform.stream.StreamSource(reader1), result1);
			wout.flush();
			os.close();
		}
	}

	private HttpsURLConnection setUpApiConnection( String requestMethod, String format,
			String token ) throws Exception
	{

		URL lc = null;
		HttpsURLConnection uc = null;

		lc = new URL(apiCommand);
		uc = (HttpsURLConnection) lc.openConnection();
		uc.setRequestProperty("Authorization", "OAuth " + token);
		uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod(requestMethod);
		uc.setDoOutput(true);
		uc.setDoInput(true);
		// uc.setRequestProperty ( "Accept", "application/json");
		uc.setRequestProperty("Accept", "application/" + format);
		return uc;
	}

	private void prepare()
	{
		apiCommand = commonURL + clientApiCommand;
		cleanAndPrepare();
	}

	private void getErrorStream( InputStreamWrapper isw, HttpsURLConnection uc, Exception e ) throws Exception
	{
		logger.info(
				"Caught Exception Getting InputStream. Will get the error stream and return that.\n" + e.getMessage());
		isw.setIs((InputStream) uc.getErrorStream());
		isw.setErrorStream(true);
	}

	public String getAccount()
	{
		return LcUtils.PRODACCOUNT;
	}

	private void cleanAndPrepare()
	{
		apiCommand = apiCommand.trim();
		apiCommand = apiCommand.replace(" ", "%20");
		apiCommand = apiCommand.replace("#", "%23");
		apiCommand = apiCommand.replace("?or=timeStamp=>", "?or=timeStamp%3d%3e");
		apiCommand = apiCommand.replace("ItemShops.timeStamp=>", "ItemShops.timeStamp%3d%3e");
		apiCommand = apiCommand.replace("!=", "!%3d");
		/*
		 * apiCommand = apiCommand.replace("=>", "%3d%3e"); apiCommand =
		 * apiCommand.replace("=<", "%3d%3c"); apiCommand =
		 * apiCommand.replace("=", "%3d"); apiCommand = apiCommand.replace("?",
		 * "%3f"); apiCommand = apiCommand.replace("&", "%26"); apiCommand =
		 * apiCommand.replace(">", "%3e"); apiCommand = apiCommand.replace("<",
		 * "%3c"); apiCommand = apiCommand.replace(",", "%2c");
		 */
	}

	private Integer getBucketLevel( HttpsURLConnection uc )
	{
		String bucketInfo = uc.getHeaderField(LEAKY_BUCKET_INFO_HEADER_NAME);
		logger.info("Bucket Info From LC Call: " + bucketInfo);
		String[] data = bucketInfo.split("/");
		BigDecimal level = new BigDecimal(data[0]);
		BigDecimal size = new BigDecimal(data[1]);
		BigDecimal bucketLevel = level.divide(size, 4, RoundingMode.HALF_UP);
		bucketLevel = bucketLevel.multiply(BigDecimal.valueOf(100));
		return bucketLevel.intValue() + 1;
	}

}