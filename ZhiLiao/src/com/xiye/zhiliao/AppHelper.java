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
			    //"com.easemob.chatuidemo"Ϊdemo�İ����������Լ���Ŀ��Ҫ�ĳ��Լ�����
			    // ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
			    return;
			}
			
			EMChat.getInstance().init(context);
			EMChat.getInstance().setDebugMode(true);
			
			preferences = context.getSharedPreferences("zhiliao", Activity.MODE_PRIVATE);
			//��Ϊ����ģʽ�������ʽ��ʱ�������Ϊfalse���������Ķ������Դ
		    EMChat.getInstance().setDebugMode(true);
		    //get easeui instance
		    easeUI = EaseUI.getInstance();
		    //����easeui��api����providers
		    
		    //���ú���ϵͳ
		    EMChatManager.getInstance().getChatOptions().setUseRoster(true);
		    
		    setEaseUIProviders();
		    
		    addGlobalLisener();
		    
		    //���Ҫ֪ͨsdk��UI �Ѿ���ʼ����ϣ�ע������Ӧ��receiver��listener, ���Խ���broadcast��
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
        //��Ҫeaseui����ʾ�û�ͷ����ǳ����ô�provider
//        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {
//            
//            @Override
//            public EaseUser getUser(String username) {
//                return getUserInfo(username);
//            }
//        });
        
        //�����ã���ʹ��easeuiĬ�ϵ�
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
                    //�����������Ϣ��ʾ
                    //���ε��û���Ⱥ�鲻��ʾ�û�
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // ��ȡ���õĲ���ʾ����Ϣ���û�����Ⱥ��ids
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
        //���ñ���provider
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
                //�������ֱ���emoji�ı���ͼƬ(resource id���߱���·��)��ӳ��map
                return null;
            }
        });*/
        
        //�����ã���ʹ��easeuiĬ�ϵ�
        /*easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //�޸ı���,����ʹ��Ĭ��
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //����Сͼ�꣬����ΪĬ��
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // ����״̬������Ϣ��ʾ�����Ը���message����������Ӧ��ʾ
                String ticker = EaseCommonUtils.getMessageDigest(message, MyApplication.getApplication());
                if(message.getType() == Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[����]");
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
                // return fromUsersNum + "�����ѣ�������" + messageNum + "����Ϣ";
            }*/
            
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
               //���õ��֪ͨ����ת�¼�
//            	 Intent intent = new Intent();
                //�е绰ʱ������ת��ͨ��ҳ��
            	 /* if(isVideoCalling){
                    intent = new Intent(appContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else{
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // ������Ϣ
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // Ⱥ����Ϣ
                        // message.getTo()ΪȺ��id
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
	    //��ȡuser��Ϣ��demo�Ǵ��ڴ�ĺ����б����ȡ��
        //ʵ�ʿ����У����ܻ���Ҫ�ӷ�������ȡ�û���Ϣ,
        //�ӷ�������ȡ�����ݣ���û�������������Ƶ������������
        /*EaseUser user = null;
        if(username.equals(EMChatManager.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        //TODO ��ȡ���ں����б����Ⱥ��Ա������Ϣ����İ������Ϣ��demoδʵ��
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
		//����õ��Ѷ��Ļ�ִ��Ҫ�����flag���ó�true
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
			 
			// ��Ϣid��ÿ����Ϣ��������Ψһ��һ��id��Ŀǰ��SDK���ɣ�
			String msgId = intent.getStringExtra("msgid");
			//���ͷ�
			String username = intent.getStringExtra("from");
			// �յ�����㲥��ʱ��message�Ѿ���db���ڴ����ˣ�����ͨ��id��ȡmesage����
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
			// �����Ⱥ����Ϣ����ȡ��group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			if (!username.equals(username)) {
				// ��Ϣ���Ƿ�����ǰ�Ự��return
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
				// ��message��Ϊ�Ѷ�
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
			// �������ӵ���ϵ��
		}

		@Override
		public void onContactAgreed(String arg0) {
			//ͬ���������
			
		}

		@Override
		public void onContactDeleted(List<String> arg0) {
			// ��ɾ��
			
		}

		@Override
		public void onContactInvited(String arg0, String arg1) {
			// �ӵ��������Ϣ�����������(ͬ���ܾ�)�����ߺ󣬷��������Զ��ٷ����������Կͻ��˲�Ҫ�ظ�����
			
		}

		@Override
		public void onContactRefused(String arg0) {
			// �ܾ���������
			
		}
		
	}
}
