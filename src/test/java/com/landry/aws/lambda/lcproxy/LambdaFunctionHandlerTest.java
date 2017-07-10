package com.landry.aws.lambda.lcproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landry.aws.lambda.common.model.LCProxyInput;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;
import com.landry.aws.lambda.lcadapter.lcclient.InputStreamWrapper;
import com.landry.aws.lambda.lcproxy.functions.LCProxy;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static LCProxyInput input;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void createString() throws IOException {
        // TODO: set up your sample input object here.
        input = new LCProxyInput();
        input.setApiCommand("Employee.json?employeeID=in,43,45,46,48");
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testLambdaFunctionHandler2() {

        LCProxy handler = new LCProxy();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);

        if (output != null) {
            System.out.println(output.toString());
        }

    }

    private void outputRaw( InputStreamWrapper result ) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(result.getIs(), "UTF-8"));
	    String line = null;
	    while ((line = r.readLine()) != null) {
	        System.out.println(line);
	    }
	}

	@Test
	public void outputJson() throws Exception {
		VendorShipTimeSupport vsts = new VendorShipTimeSupport();
		System.out.println(objectMapper.writeValueAsString(vsts));
	}

}
