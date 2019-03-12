package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.diveboard.model.Dive;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GalleryCarouselFragment extends Fragment{
	private Dive mDive;
	private int mPosition;
	private DownloadImageTask mDownloadImageTask;
	
	public GalleryCarouselFragment()
	{
		
	}
	
	public GalleryCarouselFragment(Dive dive, int position)
	{
		mDive = dive;
		mPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_gallery_carousel, container, false);
		//Threads for the picture
		ImageView main_image = (ImageView)rootView.findViewById(R.id.image_carousel);
		mDownloadImageTask = new DownloadImageTask(main_image);
		mDownloadImageTask.execute();
		return rootView;
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;
		
		public DownloadImageTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (getActivity() != null)
				{
					return mDive.getPictures().get(mPosition).getPicture(getActivity().getApplicationContext());
				}
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Bitmap result)
		{
			if (imageViewReference != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (result != null && imageView != null)
				{	
					imageView.setImageBitmap(result);
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}
}
