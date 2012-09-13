package com.example.opendroid.ws;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.opendroid.ws.models.WSModel;

public class AsyncWebServiceAdapter<T extends WSModel> {

	T[] resultArray;
	T resultObject;
	
	public Context context;
	WebServiceCallCompleteListener wsccl;

	public void getResponseArray(Context context, WebService<T> params,
			WebServiceCallCompleteListener wsccl) {
		this.wsccl = wsccl;
		this.context = context;
		new AsyncArrayAdapter().execute(params);
	}

	public void getResponseObject(Context context, WebService<T> params,
			WebServiceCallCompleteListener wsccl) {
		this.wsccl = wsccl;
		this.context = context;
		new AsyncObjectAdapter().execute(params);
	}

	class AsyncArrayAdapter extends AsyncTask<Object, Void, T[]> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(context, "Calling",
					"Time Service...", true);
		}

		@Override
		protected void onPostExecute(T[] result) {
			wsccl.onCallComplete(result);
			this.dialog.cancel();

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

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(context, "Calling",
					"Time Service...", true);
		}

		@Override
		protected void onPostExecute(Object result) {
			wsccl.onCallComplete(result);
			this.dialog.cancel();

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
