package com.xiye.zhiliao;

import android.app.Application;
import android.content.Context;

import com.xiye.zhiliao.util.PreferencesManager;

public class MyApplication extends Application{
	
	private static final String TAG = "MyApplication";
	
	private static MyApplication application;
	
	private static Context appContext;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		application = this;
		appContext = this;
		PreferencesManager.getInstance().init(appContext);
		AppHelper.getInstanse().init(appContext);
		super.onCreate();
	}
	
	public static MyApplication getApplication(){
		return application;
	}
	
}
