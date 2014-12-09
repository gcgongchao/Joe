package com.joe.orangee.listener;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.joe.orangee.util.Constants;
import com.joe.orangee.util.Utils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

public class OrangeeImageLoadingListener {

	public static class LoadingListener implements ImageLoadingListener{

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingComplete(String arg0, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				FadeInBitmapDisplayer.animate(imageView, 300);
			}
			
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}}
	
	public static class ParamsChangeLoadingListener implements ImageLoadingListener{

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingComplete(String arg0, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				if (loadedImage.getHeight()>500 || loadedImage.getWidth()>600) {
					int width=LayoutParams.WRAP_CONTENT;
					int height=LayoutParams.WRAP_CONTENT;
					if (loadedImage.getHeight()>500) {
						height=(int)(180*Constants.DENSITY+0.5f);
					}
					if (loadedImage.getWidth()>600) {
						width=(int)(220*Constants.DENSITY+0.5f);
					}
					imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
				}
				
				FadeInBitmapDisplayer.animate(imageView, 300);
			}
			
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}}
	
	public static class ProgressListener implements ImageLoadingProgressListener{

		private TextView tvProgress;
		
		public ProgressListener(TextView tvProgress) {
			super();
			this.tvProgress = tvProgress;
			
		}
		@Override
		public void onProgressUpdate(String imageUri, View view, int current, int total) {
			if (tvProgress!=null) {
				if (tvProgress.getVisibility()!=View.VISIBLE) {
					tvProgress.setVisibility(View.VISIBLE);
				}
				int progress =current/total*100;
				tvProgress.setText(progress+"%");
				if (progress==100) {
					Utils.hidePicLayout(tvProgress);
				}
				
			}
	}}

}