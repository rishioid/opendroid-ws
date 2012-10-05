package com.opendroid.ws;

import android.os.AsyncTask;

import com.opendroid.ws.models.WsModel;

public class AsyncWebServiceAdapter<T extends WsModel> {

	T[] resultArray;
	T resultObject;
	int type = -1;

	WebServiceCallCompleteListener wsccl;

	public void getResponseArray(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncArrayAdapter().execute(params);
		this.type  =  type;
	}

	public void getResponseObject(IWebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncObjectAdapter().execute(params);
		this.type  =  type;
	}

	class AsyncArrayAdapter extends AsyncTask<Object, Void, T[]> {


		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(T[] result) {
			wsccl.onCallComplete(result, type);
		}

		@Override
		protected T[] doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			resultArray = (T[]) ws.getResponseArray();
			if (resultArray != null) {
				return resultArray;
			} else {
				return null;
			}

		}

	}

	class AsyncObjectAdapter extends
			AsyncTask<Object, Void, Object> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Object result) {
			wsccl.onCallComplete(result, type);

		}

		@Override
		protected Object doInBackground(Object... params) {
			IWebService<T> ws = (IWebService<T>) params[0];
			resultObject = (T) ws.getResponseObject();
			if (resultObject != null) {
				
				return resultObject;
			} else {
				return null;
			}

		}

	}

}

/*
 * 			this.dialog = ProgressDialog.show(context, "Calling",
					"Time Service...", true);
*/