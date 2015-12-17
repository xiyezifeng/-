package com.xiye.zhiliao;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.util.NetUtils;
import com.xiye.zhiliao.adapter.Main_ViewPagerAdapter;
import com.xiye.zhiliao.ui.ContactListFragment;
import com.xiye.zhiliao.ui.ConversationListFragment;
import com.xiye.zhiliao.ui.LoginActivity;
import com.xiye.zhiliao.ui.base.BaseActivity;
import com.xiye.zhiliao.util.PreferencesManager;


public class MainActivity extends BaseActivity {
	
	private List<Fragment> fragments;
	private ConversationListFragment conversationList;
	private ContactListFragment contactList;
	private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        boolean isRegister = PreferencesManager.getInstance().getLoginState();
        UserState.isLogin = isRegister;
        fragments = new ArrayList<Fragment>();
        conversationList = new ConversationListFragment();
        contactList = new ContactListFragment();
        fragments.add(conversationList);
        fragments.add(contactList);
        viewPager = (ViewPager) findViewById(R.id.main_vp);
        viewPager.setAdapter(new Main_ViewPagerAdapter(getSupportFragmentManager(), fragments));
        if(!UserState.isLogin){
        	//未登陆，去登陆
        	Intent intent = new Intent(this,LoginActivity.class);
        	startActivity(intent);
        }else{
        	String user_name = PreferencesManager.getInstance().getUsername();
        	UserState.user_name = user_name;
        }
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	
    	super.onResume();
    }
    
  private class MyConnectionListener implements EMConnectionListener{
    	
    	@Override
    	public void onConnected() {
    		runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this, "连接成功", 0).show();
				}
			});
    	}
    	
    	@Override
    	public void onDisconnected(final int error) {
    		// TODO Auto-generated method stub
    		runOnUiThread(new Runnable() {
    			
    			@Override
    			public void run() {
    				if(error == EMError.USER_REMOVED){
    					// 显示帐号已经被移除
    					Toast.makeText(MainActivity.this, "账号被移除", 0).show();
    				}else if (error == EMError.CONNECTION_CONFLICT) {
    					// 显示帐号在其他设备登陆
    					Toast.makeText(MainActivity.this, "账号在其他设备登陆", 0).show();
    				} else {
    					if (NetUtils.hasNetwork(MainActivity.this)){
    						//连接不到聊天服务器
    						Toast.makeText(MainActivity.this, "无法连接服务器", 0).show();
    					}
    					else{
    						//当前网络不可用，请检查网络设置
    						Toast.makeText(MainActivity.this, "网路不可用", 0).show();
    					}
    				}
    			}
    		});
    	}
    }
  
  public void updateUnreadLabel(){
	  runOnUiThread(new Runnable() {
			public void run() {
			}
		});
  }
}