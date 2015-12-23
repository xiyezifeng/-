package com.xiye.zhiliao.ui;

import android.os.Bundle;

import com.easemob.easeui.ui.EaseChatFragment;
import com.xiye.zhiliao.R;
import com.xiye.zhiliao.ui.base.BaseActivity;

public class ChatActivity extends BaseActivity{
	
	
	public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //�����˻�Ⱥid
        toChatUsername = getIntent().getExtras().getString("userId");
        //����ֱ��new EaseChatFratFragmentʹ��
        chatFragment = new EaseChatFragment();
        //�������
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        
    }
}
