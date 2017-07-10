package com.landry.aws.lambda.lcadapter.lcclient;

public class TooManyRequestsException extends Exception {

	private static final long serialVersionUID = 7712768265127722162L;
	int secondsToSleep = 60;
	    public TooManyRequestsException ( int secondsToSleep ) {
	        super( "Too many requests. Please sleep " + secondsToSleep + " seconds before trying again" );
	        this.secondsToSleep = secondsToSleep;
	    }   
	        /**
	         * Gets the secondsToSleep for this instance.
	         *
	         * @return The secondsToSleep.
	         */
	        public int getSecondsToSleep()
	        {
	                return this.secondsToSleep;
	        }

}
