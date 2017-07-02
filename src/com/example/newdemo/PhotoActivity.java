package com.example.newdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;

public class PhotoActivity extends Activity {
	//自定义的ImageView控制，可对图片进行多点触控缩放和拖动
	private ZoomImageView zoomView;
	//待展示的图片
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo);
		zoomView = (ZoomImageView)findViewById(R.id.zoom_image_view);
		Intent intent = getIntent();
		//取出图片路径，并解析成Bitmap对象，然后在ZoomImageView中显示
		bitmap = BitmapFactory.decodeFile(intent.getStringExtra("photo"));
		zoomView.setImageBitmap(bitmap);
	}
	
	
	//图片没法进行缩放操作
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.photo);
//		ImageView image = (ImageView)findViewById(R.id.photo);
//		Intent intent = getIntent();
//		Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra("photo"));
//		image.setImageBitmap(bitmap);
//	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//记得将Bitmap对象回收掉
		if (bitmap != null) {
			bitmap.recycle();
		}
	}
}
