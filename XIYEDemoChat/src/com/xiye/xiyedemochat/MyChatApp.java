package com.xiye.xiyedemochat;

import android.app.Application;
import android.content.Context;

import com.xiye.chat.dispatcher.ChatHelper;

public class MyChatApp extends Application {


	public static MyChatApp instance;
	
	public static Context applicationContext;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * ��ǰ�û�nickname,Ϊ��ƻ�����Ͳ���userid�����ǳ�
	 */
	public static String currentUserNick = "";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		applicationContext = this;
        instance = this;
        
        //init demo helper
        ChatHelper.getInstance().init(applicationContext);
	}

	public static MyChatApp getInstance() {
		return instance;
	}	
}
