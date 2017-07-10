package com.landry.aws.lambda.lcproxy.functions;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.landry.aws.lambda.common.model.LCProxyInput;
import com.landry.aws.lambda.common.util.LambdaFunctions;
import com.landry.aws.lambda.lcadapter.lcclient.LcApiCaller;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LCProxy implements RequestHandler<LCProxyInput, String>
{

    private static Logger logger = LoggerFactory.getLogger(LCProxy.class);

	@Override
	public String handleRequest( LCProxyInput input, Context context )
	{

		logger.info("Function: " + LambdaFunctions.LC_PROXY);
		logger.debug("In with given input: " + input);
		if (input != null && (input.getPing() != null && input.getPing()) )
			return null;
		LcApiCaller caller = new LcApiCaller(input.getApiCommand());
		return caller.get();

	}

}