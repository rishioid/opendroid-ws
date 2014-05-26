/*
 * @author Rishi Kolvekar
 * 05-10-2012
 */
package com.opendroid.ws;

import java.io.IOException;

import com.opendroid.ws.models.WsModel;

/**
 * The Interface IWebService, for maintaining a standard interface of data communication between 
 * REST, SOAP and WCF based web-services.
 *
 * @param <T> the generic type
 */
public interface IWebService<T extends WsModel> {
	//enum for method type
	enum Method{TYPE_GET,TYPE_POST,TYPE_PUT,TYPE_DELETE};
	
	/**
	 * Gets the response object returned from webservices.
	 *
	 * @return the response object
	 * @throws IllegalAccessException 
	 */
	public Object getResponseObject() throws IOException, IllegalAccessException;
	
	/**
	 * Gets the response array, useful for services that return array of objects.
	 *
	 * @return the response array
	 * @throws IllegalAccessException 
	 */
	public Object[] getResponseArray() throws IOException, IllegalAccessException;
	
	/**
	 * Gets the response as plain  String, useful for services that return plain text.
	 *
	 * @return the response in string format
	 * @throws IllegalAccessException 
	 */
	public String getResponseString() throws IOException, IllegalAccessException;
}
