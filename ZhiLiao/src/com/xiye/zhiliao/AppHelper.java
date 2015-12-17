package com.xiye.zhiliao;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;

public class AppHelper {
	
	private static AppHelper appHelper;
	
	private Context context;
	
	private SharedPreferences preferences;
	
	private EaseUI easeUI;
	
	public static AppHelper getInstanse(){
		synchronized(AppHelper.class){
			if(null == appHelper){
				appHelper = new AppHelper();
			}
		}
		return appHelper;
	}

	private AppHelper(){}
	
	public void init(Context context){
		if(EaseUI.getInstance().init(context)){
			this.context = context;
			
			int pid = android.os.Process.myPid();
			String processAppName = getAppName(pid);
			
			if (processAppName == null ||!processAppName.equalsIgnoreCase("com.easemob.chatuidemo")) {
			    //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
			    // 则此application::onCreate 是被service 调用的，直接返回
			    return;
			}
			
			EMChat.getInstance().init(context);
			EMChat.getInstance().setDebugMode(true);
			
			preferences = context.getSharedPreferences("zhiliao", Activity.MODE_PRIVATE);
			//设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
		    EMChat.getInstance().setDebugMode(true);
		    //get easeui instance
		    easeUI = EaseUI.getInstance();
		    //调用easeui的api设置providers
		    
		    //启用好友系统
		    EMChatManager.getInstance().getChatOptions().setUseRoster(true);
		    
		    setEaseUIProviders();
		    
		    addGlobalLisener();
		    
		    //最后要通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		    EMChat.getInstance().setAppInited();
		}
	}
	
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
		Iterator<RunningAppProcessInfo> i = l.iterator();
		PackageManager pm = context.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

    protected void setEaseUIProviders() {
        //需要easeui库显示用户头像和昵称设置此provider
//        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {
//            
//            @Override
//            public EaseUser getUser(String username) {
//                return getUserInfo(username);
//            }
//        });
        
        //不设置，则使用easeui默认的
        /*easeUI.setSettingsProvider(new EaseSettingsProvider() {
            
            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }
            
            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return demoModel.getSettingMsgVibrate();
            }
            
            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return demoModel.getSettingMsgSound();
            }
            
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if(message == null){
                    return demoModel.getSettingMsgNotification();
                }
                if(!demoModel.getSettingMsgNotification()){
                    return false;
                }else{
                    //如果允许新消息提示
                    //屏蔽的用户和群组不提示用户
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // 获取设置的不提示新消息的用户或者群组ids
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });*/
        //设置表情provider
        /*easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
            
            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for(EaseEmojicon emojicon : data.getEmojiconList()){
                    if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                //返回文字表情emoji文本和图片(resource id或者本地路径)的映射map
                return null;
            }
        });*/
        
        //不设置，则使用easeui默认的
        /*easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //修改标题,这里使用默认
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //设置小图标，这里为默认
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, MyApplication.getApplication());
                if(message.getType() == Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if(user != null){
                    return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
                }else{
                    return message.getFrom() + ": " + ticker;
                }
            }
            
            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }*/
            
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
               //设置点击通知栏跳转事件
//            	 Intent intent = new Intent();
                //有电话时优先跳转到通话页面
            	 /* if(isVideoCalling){
                    intent = new Intent(appContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else{
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("userId", message.getTo());
                        if(chatType == ChatType.GroupChat){
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        }else{
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }
                        
                    }
                }*/
//                return intent;
//            }
//        });
    }	
	
	
	private EaseUser getUserInfo(String username){
	    //获取user信息，demo是从内存的好友列表里获取，
        //实际开发中，可能还需要从服务器获取用户信息,
        //从服务器获取的数据，最好缓存起来，避免频繁的网络请求
        /*EaseUser user = null;
        if(username.equals(EMChatManager.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        //TODO 获取不在好友列表里的群成员具体信息，即陌生人信息，demo未实现
        if(user == null && getRobotList() != null){
            user = getRobotList().get(username);
        }*/
        return null;
	}
	
	private void addGlobalLisener(){
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		context.registerReceiver(msgReceiver, intentFilter);
		
		EMChatManager.getInstance().getChatOptions().setRequireAck(true);
		//如果用到已读的回执需要把这个flag设置成true
		AckBroadcastReceiver ackMessageReceiver = new AckBroadcastReceiver();
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		context.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
		
		EMContactManager.getInstance().setContactListener(new MyContactListener());
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			abortBroadcast();
			 
			// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
			String msgId = intent.getStringExtra("msgid");
			//发送方
			String username = intent.getStringExtra("from");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			if (!username.equals(username)) {
				// 消息不是发给当前会话，return
				return;
			}
		}
	}
	
	private class AckBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			abortBroadcast();
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
		}
	}
	
	private class MyContactListener implements EMContactListener{

		@Override
		public void onContactAdded(List<String> arg0) {
			// 保存增加的联系人
		}

		@Override
		public void onContactAgreed(String arg0) {
			//同意好友请求
			
		}

		@Override
		public void onContactDeleted(List<String> arg0) {
			// 被删除
			
		}

		@Override
		public void onContactInvited(String arg0, String arg1) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			
		}

		@Override
		public void onContactRefused(String arg0) {
			// 拒绝好友请求
			
		}
		
	}
}
