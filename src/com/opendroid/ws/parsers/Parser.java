package com.opendroid.ws.parsers;

import com.opendroid.ws.models.WsModel;

public interface Parser<T extends WsModel> {
	
	public T getResponseObject();
	public T[] getResponseArray();

}
