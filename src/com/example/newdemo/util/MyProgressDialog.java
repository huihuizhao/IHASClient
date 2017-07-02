package com.example.newdemo.util;

import com.example.newdemo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class MyProgressDialog extends ProgressDialog {
	
	private Context mContext;
	private Animation animation;
	private ImageView imageView_progress;
	
	public MyProgressDialog(Context context) {
		super(context);
		init(context);
	}
	
	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}
	private void init(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取对话框当前的参数值
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.gravity = Gravity.CENTER;
		p.width = DeviceTools.dip2px(mContext, 60);
		getWindow().setAttributes(p);
		animation = AnimationUtils.loadAnimation(mContext, R.anim.loading_ani);
		animation.setInterpolator(new LinearInterpolator());//设置动画的速率，LinearInterpolator为以均匀的速率改变
		setContentView(R.layout.progress_dialog);
		imageView_progress = (ImageView)findViewById(R.id.progress_image);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		imageView_progress.clearAnimation();
	}

	@Override
	public void cancel() {
		super.cancel();
		imageView_progress.clearAnimation();
	}

	@Override
	public void show() {
		super.show();
		imageView_progress.startAnimation(animation);
	}
}
