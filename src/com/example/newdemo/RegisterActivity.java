package com.example.newdemo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.newdemo.net.NetRequestAddress;
import com.example.newdemo.util.MyLog;
import com.example.newdemo.util.MyToast;

public class RegisterActivity extends Activity implements OnClickListener {
	
	private EditText et_phone;
	private EditText et_code;
	private EditText et_pswd;
	private EditText et_pswdAgine;
	private Button btn_code;
	private Button btn_show;
	private Button btn_register;
	private String strPhone;
	private String strPswd;
	private String strPswdAgine;
	/**
	 * 是否显示密码
	 */
	private boolean showPswd = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
	}

	private void initView() {
		RelativeLayout titleBar = (RelativeLayout)findViewById(R.id.layout_titlebar);
		TextView center_tx = (TextView)titleBar.findViewById(R.id.tv_titlebar_title);
		center_tx.setText("注册");
//		Button rightBtn = (Button)titleBar.findViewById(R.id.btn_titlebar_right);
//		rightBtn.setText("登录");
//		Button leftBtn = (Button)titleBar.findViewById(R.id.btn_titlebar_back);
//		leftBtn.setText("返回");
		
		et_phone = (EditText)findViewById(R.id.et_mobile);
		et_code = (EditText)findViewById(R.id.et_code);
		et_pswd = (EditText)findViewById(R.id.et_pswd);
//		et_pswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		et_pswdAgine = (EditText)findViewById(R.id.et_pswdAgine);
		btn_code = (Button)findViewById(R.id.btn_verify);
		btn_show = (Button)findViewById(R.id.btn_show);
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_show.setText("显示");
		btn_code.setOnClickListener(this);
		btn_show.setOnClickListener(this);
		btn_register.setOnClickListener(this);
//		rightBtn.setOnClickListener(this);
//		leftBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_verify:
			MyToast.showToast(RegisterActivity.this, "该功能下个版本将加入，敬请期待！");
			break;
		case R.id.btn_register:
			strPhone = et_phone.getText().toString();
			strPswd = et_pswd.getText().toString();
			strPswdAgine = et_pswdAgine.getText().toString();
			if(strPhone == null || strPhone.trim().equals("")){
				MyToast.showToast(RegisterActivity.this, "请输入手机号");
			}else if(isMobileNumber(strPhone)){
				MyToast.showToast(RegisterActivity.this, "请输入正确的手机号");
			} else if(strPswd == null || strPswd.trim().equals("")){
				MyToast.showToast(RegisterActivity.this, "请输入密码");
			}else if(strPswdAgine == null || strPswdAgine.trim().equals("")){
				MyToast.showToast(RegisterActivity.this, "请再次输入密码");
			}else if(!strPswdAgine.equals(strPswd)){
				et_pswdAgine.setText("");
				MyToast.showToast(RegisterActivity.this, "密码不一致，请再次输入密码");
			}else{
				new Thread(new Runnable() {
					@Override
					public void run() {
						getRegisterInfo(strPswd, strPhone);
					}
				});
				MyToast.showToast(RegisterActivity.this, "注册成功");
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			}
			break;
		case R.id.btn_show:
			showPswd = !showPswd;
			setPswdVisibility(showPswd);
			break;
//		case R.id.btn_titlebar_right:
//		case R.id.btn_titlebar_back:
//			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//			startActivity(intent);
//			RegisterActivity.this.finish();
//			break;
		default:
			break;
		}
	}
	private void getRegisterInfo(String pswd, String phoneNo){
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpPost mHttpPost = new HttpPost(NetRequestAddress.REQUEST_ADDR_REGISTER);
		//组装数据放到HttpEntity中发送到服务器
		ArrayList<BasicNameValuePair> dataList = new ArrayList<BasicNameValuePair>();
		dataList.add(new BasicNameValuePair("password", pswd));
		dataList.add(new BasicNameValuePair("phone", phoneNo));
		HttpEntity mHttpEntity = null;
		try {
			mHttpEntity = new UrlEncodedFormEntity(dataList, "UTF-8");//对参数进行编码操作
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//生成一个post请求对象
		mHttpPost.setEntity(mHttpEntity);
		//向服务器发送POST请求并获取服务器返回的结果
		HttpResponse mResponse = null;
		try {
			mResponse = mHttpClient.execute(mHttpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//获取响应的结果信息 
		String result = "";
		try { 
			result = EntityUtils.toString(mResponse.getEntity(), "utf-8");
			MyLog.d("KungFuLife", "注册result ＝ " + result);
			if(result != null && !result.trim().equals("")){
				parseResultData(result);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void parseResultData(String result) {
		try {
			JSONObject sellerObject = new JSONObject(result);
			String status = sellerObject.getString("status");
			String info = "";
			if(sellerObject.has("info")){
				info = sellerObject.getString("info");
			}
			if(status != null && status.equals("success")){
				MyToast.showToast(RegisterActivity.this, "注册成功");
				Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(mIntent);
				RegisterActivity.this.finish();
			}else{
				MyToast.showToast(RegisterActivity.this, info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMobileNumber(String strParam){
		Pattern p = Pattern.compile("[1][34578]\\d{9}");
		Matcher m = p.matcher(strParam);
		return m.matches();
	}
	
	/**
	 * 设置密码的可见性
	 */
	private void setPswdVisibility(boolean isShow) {
		if(isShow){
			btn_show.setText("隐藏");
			et_pswd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else{
			btn_show.setText("显示");
			et_pswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
