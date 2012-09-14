package com.example.opendroid.ws;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.opendroid.ws.models.WSModel;

public class AsyncWebServiceAdapter<T extends WSModel> {

	T[] resultArray;
	T resultObject;
	int type;

	WebServiceCallCompleteListener wsccl;

	public void getResponseArray(WebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncArrayAdapter().execute(params);
	}

	public void getResponseObject(WebService<T> params,
			WebServiceCallCompleteListener wsccl, int type) {
		this.wsccl = wsccl;
		new AsyncObjectAdapter().execute(params);
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
			WebService<T> ws = (WebService<T>) params[0];
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
			WebService<T> ws = (WebService<T>) params[0];
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