package com.example.newdemo.util;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

public class DeviceTools {
	/**
	 * 获取MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		// return "14-FE-B5-A8-A0-8C";
		String mac = "";
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (null != wifi) {
			WifiInfo info = wifi.getConnectionInfo();
			if (null != info) {
				mac = info.getMacAddress();
				if (mac == null) {
					mac = "";
				}
			}
		}
		return mac;
	}
	
	/**
	 * 获取设备的deviceID(IMEI)
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String str_imei = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null)
			str_imei = telephonyManager.getDeviceId();
		if (str_imei == null)
			str_imei = "";
		return str_imei;
	}
	
	/**
	 * 获取设备的IMSI
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		String str_imsi = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null)
			str_imsi = telephonyManager.getSubscriberId();
		if (str_imsi == null)
			str_imsi = "";
		return str_imsi;
	}
	
	/**
	 * 得到运营商类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getCarrierType(Context context) {
		int carrierType = 0;
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")) { // 中国移动
				carrierType = 1;

			} else if (operator.equals("46001")) { // 中国联通
				carrierType = 2;

			} else if (operator.equals("46003")) { // 中国电信
				carrierType = 3;

			} else {
				carrierType = 0;
			}
		}
		return carrierType;
	}
	
	/**
	 * 得到网络类型和运营商类型
	 * @param context
	 * @return String : wifi, 3g, 2g
	 */
	public static String getNetType(Context context) {
		String netType = "";
		ConnectivityManager mag = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mag.getActiveNetworkInfo();
		if (info != null) {
			switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				netType = "wifi";
				break;
			case ConnectivityManager.TYPE_MOBILE:
				NetworkInfo mobInfo = mag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				switch (mobInfo.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
				case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
				case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
					netType = "2g";
					break;
					
				case TelephonyManager.NETWORK_TYPE_UMTS: // 联通3g
				case TelephonyManager.NETWORK_TYPE_HSDPA: // 联通3g
				case TelephonyManager.NETWORK_TYPE_EVDO_0: // 电信3g
				case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
					// case TelephonyManager.NETWORK_TYPE_EVDO_B: //电信3g
					netType = "3g";
					break;
					
				// case TelephonyManager.NETWORK_TYPE_LTE: //4g
				// case TelephonyManager.NETWORK_TYPE_EHRPD:
				// case TelephonyManager.NETWORK_TYPE_HSPAP:
				// netType = "4g";
				}
				break;
			default:
				netType = "";
				break;
			}
		}
		return netType;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	
	/**
	 * 设置键盘的隐藏和显示
	 * @param activity
	 * @param visibility 0：隐藏，1：显示
	 */
	@SuppressWarnings("static-access")
	public static void setKeyboardVisibility(Activity activity, int visibility) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), visibility);
		}
	}
	
	/**
	 * gps是否开启
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasOpenGps(Context context) {
		LocationManager systemLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (null != systemLocationManager) {
			return systemLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} else {
			return false;
		}
	}
	
}
