package com.example.newdemo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class VideoActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "VideoActivity";

	private SurfaceView mSurfaceview;
	private Button mBtnStartStop;
	// private Button mBtnPlay;
	private boolean mStartedFlg = false;// 是否正在录像
	private boolean mIsPlay = false;// 是否正在播放录像
	private MediaRecorder mRecorder;
	private SurfaceHolder mSurfaceHolder;
	private ImageView mImageView;
	private Camera camera;
	private MediaPlayer mediaPlayer;
	private String path;
	private TextView longTextView;
	private int videoLength = 0;
	private int cameraIndex = 0;

	private ApplicationParameters appPara;

	private android.os.Handler handler = new android.os.Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			videoLength++;
			longTextView.setText("时长：" + videoLength + "秒");
			handler.postDelayed(this, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video);

		mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
		mImageView = (ImageView) findViewById(R.id.imageview);
		mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
		// mBtnPlay = (Button) findViewById(R.id.btnPlayVideo);
		longTextView = (TextView) findViewById(R.id.longTextView);

		// 单选框
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupCamera);

		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group,
							@IdRes int checkedId) {
						if (mStartedFlg) {
							Toast.makeText(VideoActivity.this, "请先点击”按钮“完成当前录制，然后重新打开录像界面，选择摄像头",
									Toast.LENGTH_LONG).show();
						} else {
							if (checkedId == R.id.frontRadioButton) {
								cameraIndex = 0;
								Toast.makeText(VideoActivity.this,
										"您选择了前置摄像头录像", Toast.LENGTH_LONG)
										.show();

							}
							if (checkedId == R.id.backRadioButton) {
								cameraIndex = 1;
								Toast.makeText(VideoActivity.this,
										"您选择了后置摄像头录像", Toast.LENGTH_LONG)
										.show();
							}

						}

					}
				});

		mBtnStartStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mIsPlay) {
					if (mediaPlayer != null) {
						mIsPlay = false;
						mediaPlayer.stop();
						mediaPlayer.reset();
						mediaPlayer.release();
						mediaPlayer = null;
					}
				}
				if (!mStartedFlg) {
					handler.postDelayed(runnable, 1000);
					mImageView.setVisibility(View.GONE);
					if (mRecorder == null) {
						mRecorder = new MediaRecorder();
					}

					if (cameraIndex == 0) {
						camera = Camera
								.open(Camera.CameraInfo.CAMERA_FACING_FRONT);// 前置摄像头录像
					} else if (cameraIndex == 1) {
						camera = Camera
								.open(Camera.CameraInfo.CAMERA_FACING_BACK);// 后置摄像头
					}

					if (camera != null) {
						camera.setDisplayOrientation(90);

						camera.unlock();
						mRecorder.setCamera(camera);
					}

					try {
						// 这两项需要放在setOutputFormat之前
						mRecorder
								.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
						mRecorder
								.setVideoSource(MediaRecorder.VideoSource.CAMERA);

						// Set output file format
						mRecorder
								.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

						// 这两项需要放在setOutputFormat之后
						// mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
						// mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
						mRecorder
								.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
						mRecorder
								.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
						// html5 视频格式
						// http://baike.baidu.com/view/3637005.htm
						// Ogg = 带有 Thedora 视频编码和 Vorbis 音频编码的 Ogg 文件
						// MPEG4 = 带有 H.264 视频编码和 AAC 音频编码的 MPEG 4 文件

						mRecorder.setVideoSize(640, 480);
						mRecorder.setVideoFrameRate(30);
						mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
						// 2.1竖屏情况下：
						// 如果是前置摄像头：
						mRecorder.setOrientationHint(270); // 翻转
						// 设置记录会话的最大持续时间（毫秒）
						mRecorder.setMaxDuration(30 * 1000);
						mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

						// path = getSDPath();
						// if (path != null) {
						// File dir = new File(path + "/recordtest");
						// if (!dir.exists()) {
						// dir.mkdir();
						// }
						// path = dir + "/" + getDate() + ".mp4";
						// path="/storage/emulated/0/1888888888820170628072031.mp4";
						// path = appPara.getvideoPath();
						// path = ((ApplicationParameters)
						// getApplication()).getvideoPath();
						appPara = (ApplicationParameters) getApplicationContext();
						String path = appPara.getvideoPath();
						System.out.println(path);
						mRecorder.setOutputFile(path);
						mRecorder.prepare();
						mRecorder.start();
						mStartedFlg = true;
						mBtnStartStop.setText("完成");
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// stop
					if (mStartedFlg) {
						try {
							handler.removeCallbacks(runnable);
							mRecorder.stop();
							mRecorder.reset();
							mRecorder.release();
							mRecorder = null;
							mBtnStartStop.setText("开始");
							if (camera != null) {
								camera.release();
								camera = null;
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mStartedFlg = false;

					VideoActivity.this.finish();

				}
			}
		});

		// mBtnPlay.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// mIsPlay = true;
		// mImageView.setVisibility(View.GONE);
		// if (mediaPlayer == null) {
		// mediaPlayer = new MediaPlayer();
		// }
		// mediaPlayer.reset();
		// Uri uri = Uri.parse(path);
		// mediaPlayer = MediaPlayer.create(VideoActivity.this, uri);
		// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// mediaPlayer.setDisplay(mSurfaceHolder);
		// try {
		// mediaPlayer.prepare();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// mediaPlayer.start();
		// }
		// });

		SurfaceHolder holder = mSurfaceview.getHolder();
		holder.addCallback(this);
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mStartedFlg) {
			mImageView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public static String getDate() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR); // 获取年份
		int month = ca.get(Calendar.MONTH); // 获取月份
		int day = ca.get(Calendar.DATE); // 获取日
		int minute = ca.get(Calendar.MINUTE); // 分
		int hour = ca.get(Calendar.HOUR); // 小时
		int second = ca.get(Calendar.SECOND); // 秒

		String date = "" + year + (month + 1) + day + hour + minute + second;
		Log.d(TAG, "date:" + date);

		return date;
	}

	/**
	 * 获取SD path
	 * 
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}

		return null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1,
			int i2) {
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
		mSurfaceHolder = surfaceHolder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		mSurfaceview = null;
		mSurfaceHolder = null;
		handler.removeCallbacks(runnable);
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
			Log.d(TAG, "surfaceDestroyed release mRecorder");
		}
		if (camera != null) {
			camera.release();
			camera = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent(VideoActivity.this,
			// UploadActivity.class);
			// startActivity(intent);
			VideoActivity.this.finish();
		}
		return false;
	}

}
