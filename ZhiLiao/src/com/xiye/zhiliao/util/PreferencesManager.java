package com.xiye.zhiliao.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesManager {
	public static SharedPreferences preferences;
	private static PreferencesManager manager;
	private Context context;
	
	private PreferencesManager(){
		
	}
	
	public static PreferencesManager getInstance(){
		synchronized (PreferencesManager.class) {
			if(null == manager){
				manager = new PreferencesManager();
			}
		}
		return manager;
	}
	
	public void init(Context context){
		this.context = context;
		preferences = context.getSharedPreferences("zhiliao", Activity.MODE_PRIVATE);
		
	}
	
	public void setUsername(String name){
		preferences.edit().putString("user_name", name).commit();
	}
	
	public String getUsername(){
		return preferences.getString("user_name", "null");
	}
	
	public void setLoginState(boolean islogin){
		preferences.edit().putBoolean("login_state", islogin).commit();
	}
	
	public boolean getLoginState(){
		return preferences.getBoolean("login_state", false);
	}
}
