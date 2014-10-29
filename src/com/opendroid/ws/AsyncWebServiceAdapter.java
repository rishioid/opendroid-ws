package com.opendroid.ws;

import java.io.IOException;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
//	private Exception exception;
	
	private Handler mHandler;

	/** The wsccl. */
	private WebServiceCallCompleteListener wsccl;
	
	@SuppressWarnings("rawtypes")
	private AsyncTask taskInBackground=null;
	
	private boolean isResponseNeeded = true;
	
	private String mErrorMessage;
	
	private SendErrorMessageTask mSendErrorMessageTask;
	
	public AsyncWebServiceAdapter() {
		mHandler = new Handler(Looper.getMainLooper());
		mSendErrorMessageTask = new SendErrorMessageTask();
	}
	
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
		if(Looper.myLooper() == Looper.getMainLooper()){
			taskInBackground.execute(params);
		}else{
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					taskInBackground.execute();
				}
			});
		}
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
		if(Looper.myLooper() == Looper.getMainLooper()){
			taskInBackground.execute(params);
		}else{
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					taskInBackground.execute();
				}
			});
		}
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
		if(Looper.myLooper() == Looper.getMainLooper()){
			taskInBackground.execute(params);
		}else{
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					taskInBackground.execute();
				}
			});
		}
		this.type = type;
		
	}

	/**
	 * The Class AsyncArrayAdapter.
	 */
	class AsyncArrayAdapter extends AsyncTask<Object, Void, T[]> {


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(T[] result) {			
			Log.d(	"size",
					"OnPost AsyncArrayAdapter : "
							+ result + " <> type : " + type + " :wsccl: " + wsccl.getClass()
																					.getName());
			if(isResponseNeeded){
				if(result == null)
					wsccl.onError("Unable to fetch response", type);
				else
					wsccl.onCallComplete(result, type);
			}
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
				} catch (IOException | IllegalAccessException e) {//multiple catch needs java 7 
					cancel(true);
					e.printStackTrace();
					mErrorMessage = e.getMessage();
					mHandler.post(mSendErrorMessageTask);
					return null;
				} 
					return resultArray;
			} 
			return null;
		}

	}

	/**
	 * The Class AsyncObjectAdapter.
	 */
	class AsyncObjectAdapter extends AsyncTask<Object, Void, Object> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			if(isResponseNeeded){
				if(result == null)
					wsccl.onError("Unable to fetch response", type);
				else
					wsccl.onCallComplete(result, type);
			}
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
				} catch (IOException | IllegalAccessException e) {//multiple catch needs java 7 
					e.printStackTrace();
					cancel(true);
					mErrorMessage = e.getMessage();
					mHandler.post(mSendErrorMessageTask);
					
					return null;
				}
					return resultObject;
			}			
			return null;
		}
	}

	/**
	 * The Class AsyncPlainStringAdapter.
	 */
	class AsyncPlainStringAdapter extends AsyncTask<Object, Void, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if(isResponseNeeded){
				if(result == null)
					wsccl.onError("Unable to fetch response", type);
				else
					wsccl.onCallComplete(result, type);
			}
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
				} catch (IOException | IllegalAccessException e) {//multiple catch needs java 7
					e.printStackTrace();
					cancel(true);
					mErrorMessage = e.getMessage();
					mHandler.post(mSendErrorMessageTask);
					return null;
				}
						return resultString;
			}
			return null;
		}

	}

	public void cancel(){
		taskInBackground.cancel(true);
		/**
		 * don't deliver response when task is cancelled.
		 * @author Siddhesh
		 */
		isResponseNeeded = false;
	}

	/**
	 * whether response is really needed by the calling class
	 * @param isResponseNeeded
	 * @author Siddhesh
	 */
	public void setResponseNeeded(boolean isResponseNeeded) {
		this.isResponseNeeded = isResponseNeeded;
	}
	
	/**
	 * Runnable to send error message
	 * @author Siddhesh
	 *
	 */
	private class SendErrorMessageTask implements Runnable{
		@Override
		public void run() {
			wsccl.onError(mErrorMessage, type);
		}
	}
	
	
} 

/*
 * this.dialog = ProgressDialog.show(context, "Calling", "Time Service...",
 * true);
 */