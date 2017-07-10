package com.landry.aws.lambda.lcadapter.lcclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.landry.aws.lambda.dynamo.dao.DynamoLCOAuthClientDAO;
import com.landry.aws.lambda.dynamo.domain.LCOAuthClient;

public class OAuthTokenGetter
{

	public static final DynamoLCOAuthClientDAO lCOAuthClientDao = DynamoLCOAuthClientDAO.instance();

	public OAuthTokenGetter()
	{
		super();
	}

	public LCOAuthClient getToken() throws TooManyRequestsException
	{
		List<LCOAuthClient> beans = lCOAuthClientDao.findAll();
		List<LCOAuthClient> newList = new ArrayList<LCOAuthClient>(beans);
		Collections.sort(newList, new Comparator<LCOAuthClient>()
		{
			public int compare( LCOAuthClient p1, LCOAuthClient p2 )
			{
				return p1.getBucketLevel().compareTo(p2.getBucketLevel());
			}
		});

		if (newList != null && !newList.isEmpty())
			return newList.get(0);
		else
			throw new TooManyRequestsException(60);
	}
}
