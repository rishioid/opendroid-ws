package com.opendroid.ws.parsers;

import com.opendroid.ws.models.WSModel;

public interface Parser<T extends WSModel> {
	
	public T getResponseObject();
	public T[] getResponseArray();

}
