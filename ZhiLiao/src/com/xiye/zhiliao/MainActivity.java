package com.xiye.zhiliao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.util.NetUtils;
import com.xiye.zhiliao.adapter.Main_ViewPagerAdapter;
import com.xiye.zhiliao.ui.ContactListFragment;
import com.xiye.zhiliao.ui.ConversationListFragment;
import com.xiye.zhiliao.ui.LoginActivity;
import com.xiye.zhiliao.ui.base.BaseActivity;
import com.xiye.zhiliao.util.PreferencesManager;


public class MainActivity extends BaseActivity implements EMEventListener{
	
	private List<Fragment> fragments;
	private ConversationListFragment conversationList;
	private ContactListFragment contactList;
	private ViewPager viewPager;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        boolean isRegister = PreferencesManager.getInstance().getLoginState();
        
        UserState.isLogin = isRegister;
        if(UserState.isLogin == false){
        	//未登陆，去登陆
        	Log.i("zhiliao", "去登陆");
        	Intent intent = new Intent(this,LoginActivity.class);
        	startActivity(intent);
        	finish();
        }else{
        	String user_name = PreferencesManager.getInstance().getUsername();
        	UserState.user_name = user_name;
        	Log.i("zhiliao", "已经登陆过了");
        }
        
        fragments = new ArrayList<Fragment>();
        conversationList = new ConversationListFragment();
        contactList = new ContactListFragment();
        fragments.add(conversationList);
        fragments.add(contactList);
        viewPager = (ViewPager) findViewById(R.id.main_vp);
        registerBroadcastReceiver();
        
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Log.i("zhiliao", "当前page == "+arg0);
				if(arg0 == 1 ){
					updateUnreadLabel();
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        /**/
    }
    boolean init = false;
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(UserState.isLogin && !init){
    		init = true;
    		viewPager.setAdapter(new Main_ViewPagerAdapter(getSupportFragmentManager(), fragments));
    	}
    	EaseUI.getInstance().pushActivity(this);
     	EMChatManager.getInstance().registerEventListener(this);
     	Log.i("zhiliao", "MainActiivty  注册 监听 完成 ");
    	super.onResume();
    }
    
  private class MyConnectionListener implements EMConnectionListener{
    	
    	@Override
    	public void onConnected() {
    		runOnUiThread(new Runnable() {
				@SuppressLint("ShowToast") 
				public void run() {
					EMChat.getInstance().setAppInited();
					Toast.makeText(MainActivity.this, "登陆成功", 0).show();
					updateUnreadLabel();
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
				conversationList.refresh();
				contactList.refresh();
				Log.i("zhiliao", "有新消息，刷新UI");
			}
		});
  }
  

	@Override
	public void onEvent(EMNotifierEvent event) {
//		Toast.makeText(MainActivity.this, "你有新消息", 0).show();
		EMMessage message = null;
		 if(event.getData() instanceof EMMessage){
             message = (EMMessage)event.getData();
             Log.i("zhiliao", "MainActivity  收到消息 : " + event.getEvent() + ",id : " + message.getMsgId());
         }
		 
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{

			// 提示新消息
//			Toast.makeText(MainActivity.this, "你有新消息", 0).show();

			updateUnreadLabel();
			break;
		}

		case EventOfflineMessage: {
			updateUnreadLabel();
			break;
		}

		case EventConversationListChanged: {
			updateUnreadLabel();
		    break;
		}
		
		default:
			break;
		}
	}
	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
//                updateUnreadAddressLable();
               
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationList != null) {
                        conversationList.refresh();
                    }
                    if(contactList != null) {
                    	contactList.refresh();
                    }
                
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    /*if (EaseCommonUtils.getTopActivity(IMActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }*/
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
}