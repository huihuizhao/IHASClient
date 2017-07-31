package com.example.newdemo.net;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;
import java.net.URL;  
import java.security.KeyManagementException;  
import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
  
import javax.net.ssl.HostnameVerifier;  
import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLSession;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  

public class HttpsUtil {
	static String phoneNumberHttps;
	static String dateHttps;
	static String imagePathHttps;
	static String voicePathHttps;
	static String videoPathHttps;
	public void InsertToDatabase(String url,String phoneNumber,String date,String imagePath,String voicePath,String videoPath) { 
		phoneNumberHttps=phoneNumber;
		dateHttps=date;
		imagePathHttps=imagePath;
		voicePathHttps=voicePath;
		videoPathHttps=videoPath;
        new HttpsRequestThread(url).start();  
    }  
  
    /** 
     * 发起HTTPS请求，并返回结果 
     * 
     * @param url https连接 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     * @throws IOException 
     * @return  返回服务器输出结果 
     */  
    private static String httpsRequest(String url) throws NoSuchAlgorithmException, KeyManagementException, IOException {  
        StringBuilder stringBuilder = new StringBuilder();  
        SSLContext sslContext = SSLContext.getInstance("TLS");  
        sslContext.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());  
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());  
        HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());  
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();  
        connection.setDoOutput(true);  
        connection.setDoInput(true);  
        connection.setConnectTimeout(10000);  
        connection.connect();  
       
        
        OutputStreamWriter osw=new OutputStreamWriter(connection.getOutputStream());
//        osw.write("uid="+uid+"&pass_word="+password);
        osw.write("phoneNumber="
				+ phoneNumberHttps + "&date=" + dateHttps+ "&imagePath=" + imagePathHttps
				+ "&voicePath=" + voicePathHttps + "&videoPath=" + videoPathHttps);

        osw.flush();
        osw.close();
        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb=new StringBuilder();
        String aLine=null;
        while ((aLine=reader.readLine())!=null){
            sb.append(aLine).append("\n");
        }
        reader.close();
        return sb.toString();
    }  
  
    private static class MyHostnameVerifier implements HostnameVerifier {  
  
        @Override  
        public boolean verify(String hostname, SSLSession session) {  
            return true;  
        }  
    }  
  
    private static class MyTrustManager implements X509TrustManager {  
  
        @Override  
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
  
        }  
  
        @Override  
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
  
        }  
  
        @Override  
        public X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
    }  
  
    private class HttpsRequestThread extends Thread {  
        private String url;  
  
        public HttpsRequestThread(String url) {  
            this.url = url;  
        }  
  
        @Override  
        public void run() {  
            try {  
                httpsRequest(this.url);  
            } catch (NoSuchAlgorithmException e) {  
                e.printStackTrace();  
            } catch (KeyManagementException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  

	
	
	

}
