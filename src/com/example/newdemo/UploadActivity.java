package com.example.newdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.newdemo.AudioSourceMic;
import com.example.newdemo.net.HttpUtil;
import com.example.newdemo.util.MyLog;
import com.example.newdemo.util.MyToast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UploadActivity extends Activity implements OnClickListener {

	// 调用系统相机变量
	public static final int UI_SYSTEM_CAMERA_BACK = 1;// 调用系统相机返回标识
	public static final int UI_PHOTO_ZOOM_BACK = 2;// 图片剪裁后返回标识
	public static final String IMAGE_UNSPECIFIED = "image/*";
	// 照片存放路径及照片名
	private String path;
	private String newName;

	// 控件
	private Button takephoto_bt;
	private Button record_bt;
	private Button mPlayButton;
	private Button send_bt;
	private ImageView image;
	private EditText ipInfo;
	private TextView record_name;
	private TextView videoTextView;
	// private Spinner type_sp, question_sp;

	// Spinner中的内容数组
	private String[] typeArray;
	private String[] questionArray;
	private String type_str, question_str;
	private StringBuilder totleInfo = new StringBuilder();
	private String totle_str;

	// 录音控件
	private MediaRecorder mRecorder = null;// 录音类
	private MediaPlayer mPlayer = null;// 播放类
	private boolean mStartRecording = true;
	private boolean mStartPlaying = true;
	private static String mVoiceFileName = null;
	// 录音后文件
	private File file;
	// 照片文件
	private File photoFile;

	private ProgressDialog dialog;

	private String urlParameters = "";
	private String url_constant_parameters = "";
	private String uploadServerUrl = "";

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String date = dateFormat.format(new java.util.Date());
	Time timeImage = new Time();
	Time timeVoice = new Time();
	Time timeVideo = new Time();
	Time timeSubmit = new Time();
	String strTimeImage;
	String strTimeVoice;
	String strTimeVideo;
	String strTimeSubmit;

	String imagePath;
	String voicePath;
	String videoPath;
	String recordCode;

	String imageName;
	String voiceName;
	String videoName;

	AudioSourceMic mAudioSourceMic = new AudioSourceMic();

	private ApplicationParameters appPara;
	String currentPhoneNumber;

	// 处理消息，让主界面提示上传成功
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MyToast.showToast(UploadActivity.this, "上传成功");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivityCollector.addActivity(UploadActivity.this);
		setContentView(R.layout.upload);

		initModel();
		initView();
		appPara = (ApplicationParameters) getApplicationContext();
		currentPhoneNumber = appPara.getphoneNumber();
	}

	private void initModel() {
		typeArray = this.getResources().getStringArray(R.array.type_array);
		questionArray = this.getResources().getStringArray(
				R.array.question_array);
	}

	private void initView() {
		// 标题栏
		RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.layout_titlebar);
		// TextView center_tx = (TextView) titleBar
		// .findViewById(R.id.tv_titlebar_title);
		// center_tx.setText("报警");
		// Button rightBtn = (Button) titleBar
		// .findViewById(R.id.btn_titlebar_right);
		// rightBtn.setText("取消");
		// Button leftBtn = (Button)
		// titleBar.findViewById(R.id.btn_titlebar_back);
		// leftBtn.setText("返回");
		// rightBtn.setOnClickListener(this);
		// leftBtn.setOnClickListener(this);

		// type_sp = (Spinner) findViewById(R.id.type_spinner);
		// question_sp = (Spinner) findViewById(R.id.question_spinner);
		record_name = (TextView) findViewById(R.id.record_name);
		takephoto_bt = (Button) findViewById(R.id.takePhoto_bt);
		record_bt = (Button) findViewById(R.id.record_bt);
		mPlayButton = (Button) findViewById(R.id.play_bt);
		send_bt = (Button) findViewById(R.id.send_bt);
		takephoto_bt.setOnClickListener(this);
		record_bt.setOnClickListener(this);
		mPlayButton.setOnClickListener(this);
		send_bt.setOnClickListener(this);
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
		ipInfo = (EditText) findViewById(R.id.ipInfo);
		videoTextView = (TextView) findViewById(R.id.videoTextView);

		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
				UploadActivity.this, android.R.layout.simple_spinner_item,
				typeArray);
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// type_sp.setAdapter(typeAdapter);
		ArrayAdapter<String> questionAdapter = new ArrayAdapter<String>(
				UploadActivity.this, android.R.layout.simple_spinner_item,
				questionArray);
		questionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.takePhoto_bt:

			timeImage.setToNow();
			// strTimeImage = Integer.toString(timeImage.hour)
			// + Integer.toString(timeImage.minute)
			// + Integer.toString(timeImage.second);

			strTimeImage = timeImage.format("%Y%m%d%H%M%S");
			imagePath = Environment.getExternalStorageDirectory()
					+ File.separator + currentPhoneNumber + strTimeImage
					+ ".jpg";
			imageName = currentPhoneNumber + strTimeImage + ".jpg";
			invokSystemCamera();
			break;
		case R.id.image:
			Intent picture = new Intent(UploadActivity.this,
					PhotoActivity.class);
			picture.putExtra("photo", imagePath);
			startActivity(picture);
			break;
		case R.id.record_bt:

			if (mStartRecording) {
				timeVoice.setToNow();
				strTimeVoice = timeVoice.format("%Y%m%d%H%M%S");
				voicePath = Environment.getExternalStorageDirectory()
						+ File.separator + currentPhoneNumber + strTimeVoice
						+ ".wav";
				voiceName = currentPhoneNumber + strTimeVoice + ".wav";
				record_bt.setText("停止");
			} else {
				record_bt.setText("录音");
			}

			onRecord(mStartRecording);
			mStartRecording = !mStartRecording;
			break;
		case R.id.play_bt:
			// if (!(record_name.getText().equals(""))
			// && (record_name.getText().toString().trim() != null)) {
			// onPlay(mStartPlaying);
			// if (mStartPlaying) {
			// mPlayButton.setText("停止播放");
			// } else {
			// mPlayButton.setText("开始播放");
			// }
			// mStartPlaying = !mStartPlaying;
			// }
			timeVideo.setToNow();
			strTimeVideo = timeVideo.format("%Y%m%d%H%M%S");
			videoPath = Environment.getExternalStorageDirectory()
					+ File.separator + currentPhoneNumber + strTimeVideo
					+ ".mp4";
			videoName = currentPhoneNumber + strTimeVideo + ".mp4";
			// appPara = (ApplicationParameters) getApplicationContext();
			appPara.setvideoPath(videoPath);// 赋值操作
			// String vp = appPara.getvideoPath();
			Intent videoIntent = new Intent(UploadActivity.this,
					VideoActivity.class);
			startActivity(videoIntent);
			// UploadActivity.this.finish();

			videoTextView.setText(videoName);
			break;
		case R.id.send_bt:
			if (!(image.getDrawable() == null)
					&& !(record_name.getText().toString().trim().equals(""))
					&& !(videoTextView.getText().toString().trim().equals(""))
					&& !(ipInfo.getText().toString().trim().equals(""))) {
				// if (!(record_name.getText().toString().trim().equals("")))
				// {
				String ip = ipInfo.getText().toString();

				urlParameters = "http://" + ip + ":8080/IHASWeb/Insert.action?";
				url_constant_parameters = "http://" + ip
						+ ":8080/IHASWeb/Insert.action?";
				uploadServerUrl = "http://" + ip
						+ ":8080/IHASWeb/UploadServlet?";

				showProgressDialog();

				// totleInfo.append(type_str).append(',').append(question_str)
				// .append(',').append(ipInfo.getText().toString());
				// totle_str = totleInfo.toString().trim();
				// Timer timer = new Timer();
				// timer.schedule(new TimerTask() {
				// @Override
				// public void run() {
				// dialog.dismiss();
				// // Intent intent = new Intent(UploadActivity.this,
				// // MapActivity.class);
				// // startActivity(intent);
				// // UploadActivity.this.finish();
				// Message msg = new Message();
				// handler.sendMessage(msg);
				// }
				// }, 5000);

				timeSubmit.setToNow();
				strTimeSubmit = timeSubmit.format("%Y%m%d%H%M%S");
				recordCode = currentPhoneNumber + strTimeSubmit;

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// 上传图片、音频和视频
							File fileImage = new File(imagePath);
							HttpUtil.uploadFile(fileImage, uploadServerUrl);

							File fileVoice = new File(voicePath);
							HttpUtil.uploadFile(fileVoice, uploadServerUrl);

							File fileVideo = new File(videoPath);
							HttpUtil.uploadFile(fileVideo, uploadServerUrl);

							// 上传数据库表格字段信息
							InsertToDatabaseService(recordCode, date,
									imageName, voiceName, videoName);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				thread.start();

			} else {
				MyToast.showToast(UploadActivity.this,
						"请上传照片、音频、视频各项数据，并设置服务器IP地址");
			}
			break;
		// case R.id.btn_titlebar_right:
		// takephoto_bt.setText("拍照");
		// image.setImageBitmap(null);
		// record_name.setText("");
		// ipInfo.setText("");
		// break;
		// case R.id.btn_titlebar_back:
		// Intent intent = new Intent(UploadActivity.this, LoginActivity.class);
		// startActivity(intent);
		// UploadActivity.this.finish();
		// break;
		default:
			break;
		}
	}

	/**
	 * 获取Struts2 Http 登录的请求信息
	 * 
	 * @param userName
	 * @param password
	 */
	public void InsertToDatabaseService(String phoneNumber, String date,
			String imagePath, String voicePath, String videoPath) {
		// public void loginRemoteService(String userName, String password) {
		String result = null;
		try {

			// 创建一个HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// 远程登录URL
			// 下面这句是原有的
			// processURL=processURL+"userName="+userName+"&password="+password;
			urlParameters = url_constant_parameters + "phoneNumber="
					+ phoneNumber + "&date=" + date + "&imagePath=" + imagePath
					+ "&voicePath=" + voicePath + "&videoPath=" + videoPath;
			Log.d("远程URL", urlParameters);
			// 创建HttpGet对象
			HttpGet request = new HttpGet(urlParameters);
			// 请求信息类型MIME每种响应类型的输出（普通文本、html 和 XML，json）。允许的响应类型应当匹配资源类中生成的 MIME
			// 类型
			// 资源类生成的 MIME 类型应当匹配一种可接受的 MIME 类型。如果生成的 MIME 类型和可接受的 MIME 类型不
			// 匹配，那么将
			// 生成 com.sun.jersey.api.client.UniformInterfaceException。例如，将可接受的
			// MIME 类型设置为 text/xml，而将
			// 生成的 MIME 类型设置为 application/xml。将生成 UniformInterfaceException。
			request.addHeader("Accept", "text/json");
			// 获取响应的结果
			HttpResponse response = httpclient.execute(request);
			// 获取HttpEntity
			HttpEntity entity = response.getEntity();
			// 获取响应的结果信息
			String json = EntityUtils.toString(entity, "UTF-8");
			// JSON的解析过程
			if (json != null) {
				JSONObject jsonObject = new JSONObject(json);
				result = jsonObject.get("message").toString();
				dialog.dismiss();
				Message msg = new Message();
				handler.sendMessage(msg);

			}
			if (result == null) {
				json = "登录失败请重新登录";
			}
			// 创建提示框提醒是否登录成功
			AlertDialog.Builder builder = new Builder(UploadActivity.this);
			builder.setTitle("提示")
					.setMessage(result)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 显示对话框
	private void showProgressDialog() {
		dialog = new ProgressDialog(UploadActivity.this);
		dialog.setMessage("上传中...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	// 开始播放
	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			// 设置播放路径
			mPlayer.setDataSource(mVoiceFileName);
			mPlayer.prepare();
			// 开始
			mPlayer.start();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mPlayButton.setText("视频");
					mStartPlaying = !mStartPlaying;
				}
			});
		} catch (IOException e) {
			MyLog.d("prepare() failed");
		}
	}

	// 停止播放
	private void stopPlaying() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecord();
		} else {
			stopRecord();
		}
	}

	// 开始录音
	private void startRecord() {
		// mRecorder = new MediaRecorder();
		// mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//
		// 设置音源为Micphone
		// mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//
		// 设置封装格式
		// mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//
		// 设置编码格式
		// mRecorder.setOutputFile(mFileName);
		// file = new File(mFileName);
		// try {
		// file.createNewFile();
		// mRecorder.prepare();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// mRecorder.start();

		// AudioSourceMic mAudioSourceMic = new AudioSourceMic();
		mAudioSourceMic.mRecordfile = voicePath;
		mAudioSourceMic.Create(16000);
		if (mAudioSourceMic != null) {
			mAudioSourceMic.Start();
		}

	}

	// 停止录音
	private void stopRecord() {
		// mRecorder.stop();
		// mRecorder.release();
		// mRecorder = null;
		// record_name.setText(mFileName.toString());
		// record_name.setText(file.getName());

		mAudioSourceMic.Close();
		// record_name.setText(mAudioSourceMic.mRecordfile);
		record_name.setText(voiceName);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Activity暂停时释放录音和播放对象
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Activity销毁时释放录音和播放对象
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * 调用系统相机方法
	 */
	private void invokSystemCamera() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {// 判断一个activity是否存在于系统中
			// photoFile = new File(Environment.getExternalStorageDirectory(),
			// "temp.jpg");
			// photoFile = new File(Environment.getExternalStorageDirectory(),
			// "18888888888" + strTimeVoice + ".jpg");
			photoFile = new File(imagePath);
			if (photoFile != null) {
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1); // 调用前置摄像头
				startActivityForResult(cameraIntent, UI_SYSTEM_CAMERA_BACK);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case UI_SYSTEM_CAMERA_BACK:
				// 设置文件保存路径这里放在跟目录下
				File picture = new File(imagePath);
				// File picture = new File(
				// Environment.getExternalStorageDirectory()
				// + "/18888888888" + strTimeVoice + ".jpg");
				startPhotoZoom(Uri.fromFile(picture));
				
//				Bitmap photo = BitmapFactory.decodeFile(imagePath);
				// photo.compress(CompressFormat.JPEG, 100, new
				// FileOutputStream(file));
//				image.setImageBitmap(photo);
				
				break;
			case UI_PHOTO_ZOOM_BACK:
				File file = new File(imagePath);
				if (file.exists()) {
					Bitmap photo = BitmapFactory.decodeFile(imagePath);
					// photo.compress(CompressFormat.JPEG, 100, new
					// FileOutputStream(file));
					image.setImageBitmap(photo);
					takephoto_bt.setText("重拍");
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 压缩照片
	 */
	private void startPhotoZoom(Uri uri) {
		try {
			Intent photoIntent = new Intent("com.android.camera.action.CROP");
			photoIntent.setDataAndType(uri, IMAGE_UNSPECIFIED);
			photoIntent.putExtra("crop", "true");// 设置了参数，就会调用裁剪，如果不设置，就会跳过裁剪的过程。

			// aspectX aspectY 是宽高的比例 (设置aspectX与
			// aspectY后，裁剪框会按照所指定的比例出现，放大缩小都不会更改。如果不指定，裁剪框可以随意调整)
			photoIntent.putExtra("aspectX", 1);
			photoIntent.putExtra("aspectY", 1); // (注意： aspectX, aspectY
												// ，两个值都需要为
												// 整数，如果有一个为浮点数，就会导致比例失效)

			// outputX outputY 是裁剪图片宽高
			photoIntent.putExtra("outputX", 600); // 返回数据的时候的 X 像素大小。
			photoIntent.putExtra("outputY", 600); // 返回的时候 Y 的像素大小。

			// 以上两个值，设置之后会按照两个值生成一个Bitmap,
			// 两个值就是这个bitmap的横向和纵向的像素值，如果裁剪的图像和这个像素值不符合，那么空白部分以黑色填充。
			// photoIntent.putExtra("noFaceDetection", true); // 是否去除面部检测，
			// 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
			photoIntent.putExtra("return-data", false); // 是否要返回值。 一般都要。否则取的是空值。

			// path = Environment.getExternalStorageDirectory() +
			// File.separator;
			// // newName = "temp" + ".jpg";
			// newName = "18888888888" + strTimeVoice + ".jpg";

			// photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(new File(path + newName)));
			photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(imagePath)));
			startActivityForResult(photoIntent, UI_PHOTO_ZOOM_BACK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(UploadActivity.this, LoginActivity.class);
			startActivity(intent);
			UploadActivity.this.finish();
		}
		return false;
	}
}
