package com.diveboard.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.diveboard.config.AppConfig;
import com.diveboard.model.Picture;

public class Wallet {

	private Integer 				mUserId;
	private ArrayList<Picture> 		mPicturesList;
	private Integer []		 		mPicturesIDS;
	private Integer					mSize;
	public boolean					isDownloaded;
	
	
	public Wallet(){
		mUserId = -1;
		mPicturesList = null;
		mPicturesIDS = null;
		mSize = 0;
		isDownloaded = false;
	}
	
	public Wallet(final JSONObject new_wallet) throws JSONException{
		
		System.err.println("~~~~~~~~~~~~~~NEW Wallet received " + new_wallet.toString());
		mUserId = new_wallet.getInt("user_id");
		mSize = new_wallet.getInt("size");
		JSONArray array = new_wallet.optJSONArray("wallet_picture_ids");
		mPicturesIDS = new Integer [array.length()];
		for (int i = 0; i < array.length(); i++) {
			mPicturesIDS[i] = array.optInt(i);
		}
			
	}
	
	public Integer [] getPicturesIds (){
		return mPicturesIDS;
	}

	public Integer getSize() {
		return mSize;
	}

	public void setSize(Integer mSize) {
		this.mSize = mSize;
	}
	
	public ArrayList<Picture> getPicturesList() {
		return mPicturesList;
	}

	public void setPicturesList(ArrayList<Picture> mPicturesList) {
		this.mPicturesList = mPicturesList;
		this.mSize = mPicturesList.size();
		
	}

	public ArrayList<Picture>  downloadWalletPictures (Context context){
		
		Integer [] 					picsIds = mPicturesIDS;
		final ArrayList<Picture> 	mPicturesArray = new ArrayList<Picture>();
//		DownloadWalletPicsTask downloadWalletPics = new DownloadWalletPicsTask(context, picsIds);
//		try {
//			return downloadWalletPics.execute().get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
		
		ConnectivityManager _connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			for (int i = 0; i < picsIds.length; i++){
				try {
					// Creating web client
					AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
					// Initiate POST request
					HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/picture/" + picsIds[i].toString());
					// Adding parameters
					ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
//					args.add(new BasicNameValuePair("auth_token", _token));
					args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
					args.add(new BasicNameValuePair("flavour", "mobile"));
					postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
					// Execute request
					HttpResponse response = client.execute(postRequest);
					// Get response
					HttpEntity entity = response.getEntity();
					String result = ContentExtractor.getASCII(entity);
					JSONObject json = new JSONObject(result);
					client.close();
					
					System.err.println("SERVER RESPONSE for picture " + i + " " + result);
					if (!json.isNull("success") && !json.isNull("result")){
						Picture mPicture = new Picture(json.getJSONObject("result"));
						mPicturesArray.add(mPicture);
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			isDownloaded = true;
			setPicturesList(mPicturesArray);
			return mPicturesArray;
		}
		
		return null;
		
	}
		
	private class DownloadWalletPicsTask extends AsyncTask< Void, Void, ArrayList<Picture>>{
		
		private Context				mContext;
		private Picture 			mPicture;
		private ArrayList<Picture> 	mPicturesArray;
		private Integer [] 			mPicIds; 
		private boolean 			flag;
		
		public DownloadWalletPicsTask(Context context, Integer [] picsIds){
			mContext = context;
			flag = false;
			mPicturesArray = new ArrayList<Picture>();
			mPicIds = picsIds;
		}

		@Override
		protected ArrayList<Picture> doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			ConnectivityManager _connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
			// Test connectivity
			if (networkInfo != null && networkInfo.isConnected())
			{
				for (int i = 0; i < mPicIds.length; i++){
					try {
						// Creating web client
						AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
						// Initiate POST request
						HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/picture/" + mPicIds[i].toString());
						// Adding parameters
						ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
	//					args.add(new BasicNameValuePair("auth_token", _token));
						args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
						args.add(new BasicNameValuePair("flavour", "mobile"));
						postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
						// Execute request
						HttpResponse response = client.execute(postRequest);
						// Get response
						HttpEntity entity = response.getEntity();
						String result = ContentExtractor.getASCII(entity);
						JSONObject json = new JSONObject(result);
						client.close();
						
						System.err.println("SERVER RESPONSE for picture " + i + " " + result);
						if (!json.isNull("success") && !json.isNull("result")){
							mPicture = new Picture(json.getJSONObject("result"));
							mPicturesArray.add(mPicture);
							
						}
							
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				isDownloaded = true;
				setPicturesList(mPicturesArray);
				return mPicturesArray;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Picture> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
}
