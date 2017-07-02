package com.example.newdemo;

import android.app.Application;

public class ApplicationParameters extends Application {
	private String videoPath;
	private String phoneNumber;
	private static final String PATH = "initPath";
	private static final String PHONENUMBER = "initPhoneNumber";
	
	
    @Override
    public void onCreate() {
            super.onCreate();
            setvideoPath(PATH); //初始化全局变量
            setphoneNumber(PHONENUMBER);
    }

	public String getvideoPath() {
		return videoPath;
	}

	public void setvideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	public String getphoneNumber() {
		return phoneNumber;
	}

	public void setphoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
