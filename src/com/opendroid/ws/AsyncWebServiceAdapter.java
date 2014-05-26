package com.opendroid.ws;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.opendroid.ws.models.WsModel;

/**
 * The Class AsyncWebServiceAdapter.
 *
 * @param <T> the generic type
 */
public class AsyncWebServiceAdapter<T extends WsModel> {

	/** The result array. */
	private T[] resultArray;
	
	/** The result object. */
	private T resultObject;
	
	/** The type. */
	private int type = -1;
	
	
	/** The exception. */
	private Exception exception;

	/** The wsccl. */
	private WebServiceCallCompleteListener wsccl;
	
	@SuppressWarnings("rawtypes")
	private AsyncTask taskInBackground=null;
	
	/**
	 * Gets the response array.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response array
	 */
	@SuppressWarnings("unchecked")
	public void getResponseArray(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		this.type = type;
		taskInBackground = new AsyncArrayAdapter();
		taskInBackground.execute(params);
	}

	/**
	 * Gets the response object.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response object
	 */
	@SuppressWarnings("unchecked")
	public void getResponseObject(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		this.type = type;
		taskInBackground = new AsyncObjectAdapter();
		taskInBackground.execute(params);
		
	}

	/**
	 * Gets the response string.
	 *
	 * @param params the params
	 * @param wsccl the wsccl
	 * @param type the type
	 * @return the response string
	 */
	@SuppressWarnings("unchecked")
	public void getResponseString(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		taskInBackground = new AsyncPlainStringAdapter();
		taskInBackground.execute(params);
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
			Log.d(	"size",
					"OnPost AsyncArrayAdapter : "
							+ result + " <> type : " + type + " :wsccl: " + wsccl.getClass()
																					.getName());
			wsccl.onCallComplete(result, type);
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			Log.d("size", "OnCancelled AsyncArrayAdapter");			
			
		}
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected T[] doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			
			if(!isCancelled()){
				
				Log.d("size", "doInBackground AsyncArrayAdapter");
				
				try {
					resultArray = (T[]) ws.getResponseArray();
				} catch (IOException e) {
					exception = e;
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				if (resultArray != null) {
					return resultArray;
				} else {
					return null;
				}
				
			} 
			return null;
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
		@SuppressWarnings("unchecked")
		@Override
		protected Object doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			
			if(!isCancelled()){
				
				try {
					resultObject = (T) ws.getResponseObject();
					Log.d("CHECK", "IN ASYNC RESULT : "+resultObject);
				} catch (IOException e) { 
					exception = e;
					e.printStackTrace();
					
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
//				if (resultObject != null) {

					return resultObject;
//				} else {
//					return null;
//				}
			}			
			return null;

		}

	}

	/**
	 * The Class AsyncPlainStringAdapter.
	 */
	class AsyncPlainStringAdapter extends AsyncTask<Object, Void, String> {

		/** The Constant TAG. */
//		private static final String TAG = "AsyncPlainStringAdapter";

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
		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			String resultString = null;

			if(!isCancelled()){
				
				try {
					resultString = ws.getResponseString();
				} catch (IOException e) {
					exception = e;
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				if (resultString != null) {

					return resultString;
				} else {
					return null;
				}
				
			}
			return null;
		}

	}

	public void cancel(){
		taskInBackground.cancel(true);
	}
} 

/*
 * this.dialog = ProgressDialog.show(context, "Calling", "Time Service...",
 * true);
 */