package com.landry.aws.lambda.lcproxy.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.landry.aws.lambda.common.util.LambdaFunctions;
import com.landry.aws.lambda.lcadapter.lcclient.LCDownException;
import com.landry.aws.lambda.lcadapter.lcclient.OAuthClientBucketLevelChecker;

public class LCBucketLevelChecker implements RequestHandler<String, String>
{
	/*
	 * 
	 * Why am I doing this?
	 * 
	 * The LCProxy client requests will get an available OAuth client from the 
	 * LCOAuthClient table. If the proxy is getting hammered they will be all full
	 * and none will be available. We do not do a get before the actual call to
	 * get the bucket levels in order to avoid making many unnecessary calls.
	 * 
	 * 
	 * This function is called by cloudwatch every minute in order to reset the bucket levels
	 * of every OAuth client we have in the table.
	 * 
	 *  
	 */


    private static Logger logger = LoggerFactory.getLogger(LCBucketLevelChecker.class);

	@Override
	public String handleRequest( String input, Context context )
	{

		logger.debug("Function: " + LambdaFunctions.LC_BUCKET_LEVEL_CHECKER);
		logger.debug("In with given input: " + input);

		OAuthClientBucketLevelChecker checker = new OAuthClientBucketLevelChecker();
		int numberOfClientsChecked = 0;
		try
		{
			numberOfClientsChecked = checker.check();
		}
		catch (LCDownException e)
		{
			// TODO Handle errors somehow...
			e.printStackTrace();
			return e.getMessage();
		}
		return "Done checking " + numberOfClientsChecked + " clients for bucket levels.";
	}

}