package com.opendroid.ws;

public interface WebServiceCallCompleteListener {
	
	public void onCallComplete(Object result, int type);
	public void onError(String message,int type);

}

