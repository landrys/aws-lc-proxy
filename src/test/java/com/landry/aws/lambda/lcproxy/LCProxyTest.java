package com.landry.aws.lambda.lcproxy;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.landry.aws.lambda.common.model.LCProxyInput;
import com.landry.aws.lambda.lcproxy.functions.LCBucketLevelChecker;
import com.landry.aws.lambda.lcproxy.functions.LCProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LCProxyTest {

    private static LCProxyInput input;

    private static Logger logger = LoggerFactory.getLogger(LCProxyTest.class);

    @BeforeClass
    public static void createString() throws IOException {
    	input = new LCProxyInput(); 
        input.setApiCommand("Employee?employeeID=43");
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("lCProxy");
        return ctx;
    }

    @Test
    public void testLCProxy() {

        LCProxy handler = new LCProxy();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);

        if (output != null) {
            logger.info(output.toString());
        }

    }

}
