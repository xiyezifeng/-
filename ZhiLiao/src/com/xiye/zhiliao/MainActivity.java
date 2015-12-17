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
        	//δ��½��ȥ��½
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
					Toast.makeText(MainActivity.this, "���ӳɹ�", 0).show();
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
    					// ��ʾ�ʺ��Ѿ����Ƴ�
    					Toast.makeText(MainActivity.this, "�˺ű��Ƴ�", 0).show();
    				}else if (error == EMError.CONNECTION_CONFLICT) {
    					// ��ʾ�ʺ��������豸��½
    					Toast.makeText(MainActivity.this, "�˺��������豸��½", 0).show();
    				} else {
    					if (NetUtils.hasNetwork(MainActivity.this)){
    						//���Ӳ������������
    						Toast.makeText(MainActivity.this, "�޷����ӷ�����", 0).show();
    					}
    					else{
    						//��ǰ���粻���ã�������������
    						Toast.makeText(MainActivity.this, "��·������", 0).show();
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