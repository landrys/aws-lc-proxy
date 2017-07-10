package com.landry.aws.lambda.lcproxy;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.landry.aws.lambda.lcproxy.functions.LCBucketLevelChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class BucketLevelCheckerTest {

    private static String input;

    private static Logger logger = LoggerFactory.getLogger(BucketLevelCheckerTest.class);

    @BeforeClass
    public static void createString() throws IOException {
        input = "Not needed";
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("checkLCBucketLevels");
        return ctx;
    }

    @Test
    public void testCheckBucketLevels() {

        LCBucketLevelChecker handler = new LCBucketLevelChecker();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);

        if (output != null) {
            logger.info(output.toString());
        }

    }

}
