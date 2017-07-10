package com.landry.aws.lambda.lcadapter.lcclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.landry.aws.lambda.dynamo.dao.DynamoLCOAuthClientDAO;
import com.landry.aws.lambda.dynamo.domain.LCOAuthClient;

public class OAuthClientBucketLevelChecker
{


    private static Logger logger = LoggerFactory.getLogger(OAuthClientBucketLevelChecker.class);

	public static final DynamoLCOAuthClientDAO repo = DynamoLCOAuthClientDAO.instance();
	public static final String BUCKET_LEVEL_URL = "Employee?employeeID=43";
	private LcApi lcApi = new LcApi(BUCKET_LEVEL_URL);

	public OAuthClientBucketLevelChecker()
	{
		super();
	}

	public int check() throws LCDownException
	{
		try
		{
			int i = 0;
			Iterable<LCOAuthClient> beans = repo.findAll();

			for (LCOAuthClient client : beans)
			{
				i++;
				logger.debug("Checking client: " + client.toString());
				lcApi.getInputStreamForBucketLevelCheck(client);
			}
			return i;
		}

		catch (Exception e)
		{
			logger.info(e.getMessage());
			e.printStackTrace();
			throw new LCDownException(e.getMessage());
		}
	}
}