package com.example.opendroid.ws.parsers;

import com.example.opendroid.ws.models.WSModel;

public interface Parser<T extends WSModel> {
	
	public T getResponseObject();
	public T[] getResponseArray();

}
