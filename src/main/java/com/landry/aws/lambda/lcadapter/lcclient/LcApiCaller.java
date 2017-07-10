package com.landry.aws.lambda.lcadapter.lcclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.landry.aws.lambda.common.util.RestVerb;

public class LcApiCaller
{
	private LcApi lcApi;

	public LcApiCaller(String query)
	{
		super();
	    this.lcApi = new LcApi(query);
	}


	public String get()
	{
		String result = null;
		try
		{
			InputStreamWrapper isw = lcApi.getInputStream(RestVerb.GET.name(), null);
			result = outputRaw(isw);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	private String outputRaw( InputStreamWrapper result ) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(result.getIs(), "UTF-8"));
		String line = null;
		String data = null;
		while ((data = r.readLine()) != null)
		{
			if (line == null)
				line = data;
			else
				line = line + data;
		}
		return line;
	}

}
