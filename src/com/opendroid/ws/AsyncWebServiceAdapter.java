package com.opendroid.ws;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.opendroid.ws.models.WsModel;

// TODO: Auto-generated Javadoc
/**
 * The Class AsyncWebServiceAdapter.
 *
 * @param <T> the generic type
 */
public class AsyncWebServiceAdapter<T extends WsModel> {

	/** The result array. */
	T[] resultArray;
	
	/** The result object. */
	T resultObject;
	
	/** The type. */
	int type = -1;
	
	/** The exception. */
	Exception exception;

	/** The wsccl. */
	WebServiceCallCompleteListener wsccl;

	/**
	 * Gets the response array.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response array
	 */
	public void getResponseArray(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncArrayAdapter().execute(params);
		this.type = type;
	}

	/**
	 * Gets the response object.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response object
	 */
	public void getResponseObject(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncObjectAdapter().execute(params);
		this.type = type;
	}

	/**
	 * Gets the response string.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response string
	 */
	public void getResponseString(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncPlainStringAdapter().execute(params);
		this.type = type;
	}

	/**
	 * The Class AsyncArrayAdapter.
	 */
	class AsyncArrayAdapter extends AsyncTask<Object, Void, T[]> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(T[] result) {
			wsccl.onCallComplete(result, type);
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected T[] doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			try {
				resultArray = (T[]) ws.getResponseArray();
			} catch (IOException e) {
				exception = e;
				e.printStackTrace();
			}
			if (resultArray != null) {
				return resultArray;
			} else {
				return null;
			}

		}

	}

	/**
	 * The Class AsyncObjectAdapter.
	 */
	class AsyncObjectAdapter extends AsyncTask<Object, Void, Object> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {

		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			wsccl.onCallComplete(result, type);

		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			try {
				resultObject = (T) ws.getResponseObject();
			} catch (IOException e) {
				exception = e;
				e.printStackTrace();
			}
			if (resultObject != null) {

				return resultObject;
			} else {
				return null;
			}

		}

	}

	/**
	 * The Class AsyncPlainStringAdapter.
	 */
	class AsyncPlainStringAdapter extends AsyncTask<Object, Void, String> {

		/** The Constant TAG. */
		private static final String TAG = "AsyncPlainStringAdapter";

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {

		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			wsccl.onCallComplete(result, type);
			wsccl.onError(exception.getMessage());
			
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			String resultString = null;

			try {
				resultString = ws.getResponseString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				exception = e;
				e.printStackTrace();
			}

			if (resultString != null) {

				return resultString;
			} else {
				return null;
			}

		}

	}

}

/*
 * this.dialog = ProgressDialog.show(context, "Calling", "Time Service...",
 * true);
 */