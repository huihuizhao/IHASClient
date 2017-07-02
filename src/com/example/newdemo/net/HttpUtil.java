package com.example.newdemo.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.newdemo.util.MyLog;

import android.content.Context;
import android.util.Log;

public class HttpUtil {
	
	public void getLoginResponseInfo(Context context, String phone, String pswd) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(NetRequestAddress.REQUEST_ADDR_LOGIN);
		//组装数据放到HttpEntity中发送到服务器
		ArrayList<BasicNameValuePair> dataList = new ArrayList<BasicNameValuePair>();
		dataList.add(new BasicNameValuePair("phone", phone));
		dataList.add(new BasicNameValuePair("password", pswd));
		HttpEntity httpEntity = null;
		try {
			httpEntity = new UrlEncodedFormEntity(dataList, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		httpPost.setEntity(httpEntity);
		//向服务器发送POST请求并获取服务器返回的结果
		HttpResponse httpResponse = null;
		//获取响应的结果信息 
		String result = "";
		try {
			httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			MyLog.d("KungFuLife", "登录result ＝ " + result);
			if (result != null && !result.trim().toString().equals("")) {
				parseResultData(result, pswd);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//上传数据用的类
	public static void postInfoToServer(Context context, File mFileName, File actionUrl, String info) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(NetRequestAddress.REQUEST_ADDR_LOGIN);
		//组装数据放到HttpEntity中发送到服务器
		ArrayList<BasicNameValuePair> dataList = new ArrayList<BasicNameValuePair>();
//		dataList.add(new BasicNameValuePair("mFileName", mFileName));
//		dataList.add(new BasicNameValuePair("actionUrl", actionUrl));
		HttpEntity httpEntity = null;
		try {
			httpEntity = new UrlEncodedFormEntity(dataList, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		httpPost.setEntity(httpEntity);
		//向服务器发送POST请求并获取服务器返回的结果
		HttpResponse httpResponse = null;
		//获取响应的结果信息 
		String result = "";
		try {
			httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			MyLog.d("KungFuLife", "登录result ＝ " + result);
			if (result != null && !result.trim().toString().equals("")) {
//				parseResultData(result, actionUrl);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    /**
     * Android上传文件到服务端
     *
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                conn.connect();
                conn.connect();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                // if(res==200)
                // {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : " + result);
                // }
                // else{
                // Log.e(TAG, "request error");
                // }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	//Json解析返回的数据
	private static void parseResultData(String result, String actionUrl) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			String info = "";
			if(jsonObject.has("info")){
				info = jsonObject.getString("info");
			}
			if(status != null && status.equals("success")){
//				MyToast.showToast(LoginActivity.this, "登录成功");
				String userName = jsonObject.getString("USERNAME");
				String sex = jsonObject.getString("SEX");
				String province = jsonObject.getString("PROVINCE");
				String city = jsonObject.getString("CITY");
				String county = jsonObject.getString("COUNTY");
				String location = jsonObject.getString("LOCATION");
//				ldPref.setUserName(userName);
				MyLog.d("weipeipei", "userName = " + userName);
//				ldPref.setUserMobile(strPhone);
//				ldPref.setUserPswd(pswd);
//				ldPref.setUserSex(sex);
//				ldPref.setUserProvince(province);
//				ldPref.setUserCity(city);
//				ldPref.setUserCounty(county);
//				ldPref.setUserLocation(location);
//				setResult(RESULT_OK);
//				LoginActivity.this.finish();
			}else{
//				MyToast.showToast(LoginActivity.this, info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
