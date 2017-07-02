package com.example.newdemo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.newdemo.util.MyToast;


import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText et_phone;
	private EditText et_pwd;
	private ImageButton ib_delete_phone;
	private CheckBox cb_eye;
//	private TextView tv_forget_pwd;
	private Button bt_login;
//	private Button bt_register;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private String userName, password;
	private CheckBox remeber_pswd;

	private static String login_url = "http://192.168.1.101:8080/JsonWeb/login.action?";
	private final String login_url_constant = "http://192.168.1.101:8080/JsonWeb/login.action?";

	// private static String login_url =
	// "http://192.168.1.6:8080/JsonWeb/login.action?";
	// private final String login_url_constant =
	// "http://192.168.1.6:8080/JsonWeb/login.action?";

	private ApplicationParameters appPara;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// /在Android2.2以后必须添加以下代码
		// 本应用采用的Android4.0
		// 设置线程的策略
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork() // or
																		// .detectAll()
																		// for
																		// all
																		// detectable
																		// problems
				.penaltyLog().build());
		// 设置虚拟机的策略
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				// .detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏  
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		ib_delete_phone = (ImageButton) findViewById(R.id.ib_delete_phone);
		cb_eye = (CheckBox) findViewById(R.id.cb_eye);
//		tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
		bt_login = (Button) findViewById(R.id.bt_login);
//		bt_register = (Button) findViewById(R.id.bt_register);

		bt_login.setOnClickListener(this);
//		bt_register.setOnClickListener(this);
//		tv_forget_pwd.setOnClickListener(this);
		ib_delete_phone.setOnClickListener(this);

		// remeber_pswd = (CheckBox)findViewById(R.id.remeber_pswd);
		// boolean isRemeber = pref.getBoolean("remeberPswd", false);

		// 密码显示隐藏
		cb_eye.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {// 选中隐藏
					et_pwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				} else {
					et_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				}
			}
		});

		initModle();
	}

	private void initModle() {
		pref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public void onClick(View v) {
		// Intent intent = new Intent();
		switch (v.getId()) {
		// 登录
		case R.id.bt_login:
			String phoneNumber = et_phone.getText().toString();
			String password = et_pwd.getText().toString();
			if (phoneNumber.equals("18888888888") && password.equals("1")) {
				// editor = pref.edit();
				// if (remeber_pswd.isChecked()) {
				// editor.putBoolean("remeberPswd", true);
				// editor.putString("userName", telephone);
				// editor.putString("password", password);
				// } else {
				// editor.clear();
				// }
				// editor.commit();
				// Intent intent = new Intent(LoginActivity.this,
				// MapActivity.class);
				// startActivity(intent);

				Intent intent = new Intent(LoginActivity.this,
						UploadActivity.class);
				startActivity(intent);

				appPara = (ApplicationParameters) getApplicationContext();
				appPara.setphoneNumber(phoneNumber);// 赋值操作
				LoginActivity.this.finish();
			} else {
				MyToast.showToast(LoginActivity.this, "请输入正确的手机号或密码");
			}

			// 登录验证,连接数据库
			// loginRemoteService(phoneNumber, password);
			break;
		// 注册
//		case R.id.bt_register:
			// intent.setClass(this, LoginRegisterActivity.class);
			// startActivity(intent);
//			break;
		// 忘记密码
//		case R.id.tv_forget_pwd:
			// intent.setClass(this, ForgetPwdActivity.class);
			// startActivity(intent);
//			break;
		// 删除手机号
		case R.id.ib_delete_phone:
			et_phone.setText("");
			break;
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
	public void loginRemoteService(String userName, String password) {
		String result = null;
		try {

			// 创建一个HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// 远程登录URL
			// 下面这句是原有的
			// processURL=processURL+"userName="+userName+"&password="+password;
			login_url = login_url_constant + "userName=" + userName
					+ "&password=" + password;
			Log.d("远程URL", login_url);
			// 创建HttpGet对象
			HttpGet request = new HttpGet(login_url);
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

			}
			if (result == null) {
				json = "登录失败请重新登录";
			}
			if (result.equals("登录成功")) {
				Intent intent = new Intent(LoginActivity.this,
						UploadActivity.class);
				startActivity(intent);
				appPara = (ApplicationParameters) getApplicationContext();
				appPara.setphoneNumber(userName);// 赋值操作

				LoginActivity.this.finish();
			} else {
				MyToast.showToast(LoginActivity.this, "请输入正确的手机号或密码");
			}

			// 创建提示框提醒是否登录成功
			// AlertDialog.Builder builder = new Builder(LoginActivity.this);
			// builder.setTitle("提示")
			// .setMessage(result)
			// .setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// dialog.dismiss();
			// }
			// }).create().show();

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

}
