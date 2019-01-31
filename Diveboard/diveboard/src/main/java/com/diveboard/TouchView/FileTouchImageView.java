/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.diveboard.TouchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.diveboard.mobile.R;
import com.diveboard.model.Picture;

public class FileTouchImageView extends UrlTouchImageView 
{
	
    public FileTouchImageView(Context ctx)
    {
        super(ctx);

    }
    public FileTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
    }

    public void setUrl(Picture imagePath)
    {
        new ImageLoadTask().execute(imagePath);
    }
    //No caching load
    public class ImageLoadTask extends AsyncTask<Picture, Integer, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Picture... pictures) {
        	try {
        		System.out.println("Entre dans ImageLoadTask");
        		Bitmap bm = pictures[0].getPicture(mContext, Picture.Size.LARGE);
//        		publishProgress(100);
				return bm; 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            String path = strings[0];
//            Bitmap bm = null;
//            try {
//            	File file = new File(pictures[0].getLocalPath(mContext, Picture.Size.LARGE));
//            	if (file.exists())
//            		System.out.println("FILE EXISTS -----------------------------------------");
//            	FileInputStream fis = mContext.openFileInput(file.getName());
//            	byte[] buffer = new byte[8192];
//            	
//                InputStreamWrapper bis = new InputStreamWrapper(fis, 8192, file.length());
//                bis.setProgressListener(new InputStreamProgressListener()
//				{					
//					@Override
//					public void onProgress(float progressValue, long bytesLoaded,
//							long bytesTotal)
//					{
//						publishProgress((int)(progressValue * 100));
//					}
//				});
//               
//                bm = BitmapFactory.decodeStream(bis);
//                bis.close();
//                if (bm == null)
//                {
//                	System.out.println("BM = NULL");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bm;
        	return null;
        }
        
        @Override
        protected void onPostExecute(Bitmap bitmap) {
        	if (bitmap == null) 
        	{
        		mImageView.setScaleType(ScaleType.CENTER);
        		mImageView.setImageResource(R.drawable.no_photo);
        		//bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
        		//mImageView.setImageBitmap(bitmap);
        	}
        	else 
        	{
        		mImageView.setScaleType(ScaleType.MATRIX);
	            mImageView.setImageBitmap(bitmap);
        	}
            mImageView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }

//		@Override
//		protected void onProgressUpdate(Integer... values)
//		{
//			mProgressBar.setProgress(values[0]);
//		}
    }
}
