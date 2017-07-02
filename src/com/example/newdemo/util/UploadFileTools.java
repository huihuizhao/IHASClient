package com.example.newdemo.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 上传文件或者图片
 * @author wp
 */
public class UploadFileTools {

	/* 上传文件至Server的方法 */
	public static String uploadFile(String actionUrl, String filePath, String newName,
                                    Map<String, String> postString) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String tag = "UploadFileTools";
		int fileLenth = 0;
		int bodyLenth = 0;
		
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			fileLenth = getBytes(filePath+newName).length;
			
			StringBuffer sbBody = new StringBuffer();
			
			sbBody.append(twoHyphens + boundary + end);

            Set<String> keySet = postString.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
                String name = it.next();
                String value = postString.get(name);

                sbBody.append(twoHyphens + boundary + end);

                sbBody.append("Content-Disposition: form-data; name=\"" + name +"\"" + end);
                sbBody.append(end);
                sbBody.append(URLEncoder.encode(value) + end);
            }

			sbBody.append(twoHyphens + boundary + end);

			sbBody.append("Content-Disposition: form-data; name=\"file\";filename=\"" + newName + "\"" + end);
            sbBody.append("Content-Type: " + "image/jpg" + "\r\n");
            sbBody.append(end);
			
			StringBuffer sbBodyEnd = new StringBuffer();
			sbBodyEnd.append(end + end + twoHyphens + boundary + twoHyphens + end);
			
			bodyLenth = fileLenth + sbBody.toString().getBytes("UTF-8").length
                    + sbBodyEnd.toString().getBytes("UTF-8").length;
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Length", String.valueOf(bodyLenth));
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(sbBody.toString());


			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(filePath+newName);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;

			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}

			ds.writeBytes(sbBodyEnd.toString());

			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			ds.close();
			MyLog.d(tag, "上传成功" + b.toString().trim());
			MyLog.d("response=======" + b.toString().trim());
            
			if(con.getResponseCode() == 200){
				String imgUrl = "";
				String strResponse  = b.toString().trim();
				MyLog.d(tag, "strResponse=====" + strResponse);
				try {
					JSONObject jsb = new JSONObject(strResponse);
					if(jsb.has("data") && !jsb.isNull("data")){
                        JSONObject jsbData= jsb.getJSONObject("data");
                        if(jsbData.has("avatar") && !jsbData.isNull("avatar")) {
                            imgUrl = jsbData.getString("avatar");
                            MyLog.d(tag, "imgUrl =====" + imgUrl);
                        }
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return imgUrl;				
			}else{
				return null;
			}
		} catch (Exception e) {
			MyLog.d(tag, "上传失败" + e);
			return null;
		}
	}


    /**
     * 上传多个文件
     * @param actionUrl
     * @param filePathList
     * @param postString
     * @return
     */
    public static String uploadFiles(String actionUrl, List<String> filePathList,
                                    Map<String, String> postString) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String tag = "UploadFileTools";

        try {

            StringBuffer sbBodyEnd = new StringBuffer();
            sbBodyEnd.append(end + end + twoHyphens + boundary + twoHyphens + end);


            StringBuffer sbBody = new StringBuffer();
            //post参数
            sbBody.append(twoHyphens + boundary + end);

            Set<String> keySet = postString.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String name = it.next();
                String value = postString.get(name);

                sbBody.append(twoHyphens + boundary + end);

                sbBody.append("Content-Disposition: form-data; name=\"" + name +"\"" + end);
                sbBody.append(end);
                sbBody.append(value);
                sbBody.append(end);
            }

            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
			/* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream ds = new DataOutputStream(con.getOutputStream());

            MyLog.d("body======="+sbBody.toString());
            ds.writeBytes(sbBody.toString());

            ds.writeBytes(twoHyphens + boundary + end);

            //文件
            for (int i = 0; i < filePathList.size(); i ++) {
                String path = filePathList.get(i);
                String name = "file"+(i+1);
                File file = new File(path);
                MyLog.d("file length========"+getBytes(path).length);
                if(file.exists()) {
                    if(i != 0)
                        ds.writeBytes(twoHyphens + boundary + end);

                    sbBody = new StringBuffer();
                    sbBody.append("Content-Disposition: form-data; name=\""+name+"\";filename=\"" + file.getName()+".jpg" + "\"" + end);
                    sbBody.append("Content-Type: " + "image/jpg" + "\r\n");
                    sbBody.append(end);

                    ds.writeBytes(sbBody.toString());

                    /* 取得文件的FileInputStream */
                    FileInputStream fStream = new FileInputStream(file);
			        /* 设置每次写入1024bytes */
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length;

			        /* 从文件读取数据至缓冲区 */
                    while ((length = fStream.read(buffer)) != -1) {
				        /* 将资料写入DataOutputStream中 */
                        ds.write(buffer, 0, length);
                    }
                    /* close streams */
                    fStream.close();

                    ds.writeBytes(end);
                }
            }


            ds.writeBytes(sbBodyEnd.toString());

            ds.flush();

			/* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            MyLog.d(tag, "上传成功" + b.toString().trim());
            MyLog.d("response=======" + b.toString().trim());

            if(con.getResponseCode() == 200){
                String strResponse  = b.toString().trim();
                MyLog.d(tag, "strResponse=====" + strResponse);
                return strResponse;
            }else{
                return null;
            }
        } catch (IOException e) {
        	MyLog.d("上传失败" + e);
            return null;
        }
    }

	/**
	 * 获得指定文件的byte数组
	 */
	private static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
}
