package com.xiye.zhiliao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatDB;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier;
import com.easemob.exceptions.EaseMobException;

public class AppHelper {
	
	private static AppHelper appHelper;
	
	private Context context;
	
	private SharedPreferences preferences;
	
	private EaseUI easeUI;
	
	public static List<String> friendMessage = new ArrayList<String>();
	public static Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
	
	public EMEventListener   eventListener ;
	
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
			
			if (processAppName == null ||!processAppName.equalsIgnoreCase("com.xiye.zhiliao")) {
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
//		    EMChat.getInstance().setAppInited();
		}else{
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
        // ��ȡ���ں����б����Ⱥ��Ա������Ϣ����İ������Ϣ��demoδʵ��
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
		
		/*IntentFilter inviteIntentFilter = new IntentFilter(EMChatManager.getInstance().getContactInviteEventBroadcastAction());
		inviteIntentFilter.setPriority(3);
		context.registerReceiver(new ContactInviteReceiver(), inviteIntentFilter);*/
		registerEventListener();
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			Log.i("zhiliao", "�յ�һ����Ϣ����Ҫˢ��UI" );
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
		   public void onContactAgreed(String username) {
		       //��������ͬ��
			Log.i("zhiliao", "onContactAgreed  ��������ͬ�� :" + username +"____��Ϣ : ");
		   }
		   
		   @Override
		   public void onContactRefused(String username) {
		       //�������󱻾ܾ�
			   Log.i("zhiliao", "onContactAgreed  �������󱻾ܾ� :" + username +"____��Ϣ : ");
		   }
		   
		   @Override
		   public void onContactInvited(String username, String reason) {
		       //�յ���������
			   Log.i("zhiliao", "�յ��������� :" + username +"____��Ϣ : "+reason);
			   if(!friendMessage.contains(username)){
				   friendMessage.add(username);
				   Log.i("zhiliao", "������������ ����һ�� :" + username );
			   }
		   }
		   
		   @Override
		   public void onContactDeleted(List<String> usernameList) {
		       //��ɾ��ʱ�ص��˷���
			   Log.i("zhiliao", "��ɾ��ʱ :" + usernameList.size() +"____�Ƴ� : ");
		   }
		   
		   @Override
		   public void onContactAdded(List<String> usernameList) {
		       //��������ϵ��ʱ�ص��˷���
			   Log.i("zhiliao", "��������ϵ �� :" + usernameList.size() +"____���� : ");
		   }
		
	}
	 /**
     * ȫ���¼�����
     * ��Ϊ���ܻ���UIҳ���ȴ��������Ϣ������һ�����UIҳ���Ѿ���������Ͳ���Ҫ�ٴδ���
     * activityList.size() <= 0 ��ζ������ҳ�涼�Ѿ��ں�̨���У������Ѿ��뿪Activity Stack
     */
    protected void registerEventListener() {
    	eventListener = new EMEventListener() {
//            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    Log.i("zhiliao", "AppHelper   �յ���Ϣ : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                	
                    //Ӧ���ں�̨������Ҫˢ��UI,֪ͨ����ʾ����Ϣ
                    if(!easeUI.hasForegroundActivies()){
                    	//֪ͨ���յ�����Ϣ��sdk������ɺ�ᷢ��֪ͨ���¼�����mainactivity����
                        getNotifier().onNewMsg(message);
                        Log.i("zhiliao", "��̨����    ��Ҫ��֪ͨ�� : " + message.getBody());
                    }else{
                    	Log.i("zhiliao", "�յ�����Ϣ     :   " + message.getBody());
                    }
                    
                    break;
                case EventOfflineMessage:
                    if(!easeUI.hasForegroundActivies()){
                        /*EMLog.d(TAG, "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        getNotifier().onNewMesg(messages);*/
                        Log.i("zhiliao", "������Ϣ");
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                { 
                    
                	Log.i("zhiliao", "�յ�͸����Ϣ ");
                    //��ȡ��Ϣbody
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//��ȡ�Զ���action
                    
                    //��ȡ��չ���� �˴�ʡ��
                    //message.getStringAttribute("");
                    /*EMLog.d(TAG, String.format("͸����Ϣ��action:%s,message:%s", action,message.toString()));
                    final String str = appContext.getString(R.string.receive_the_passthrough);
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //ע��㲥������
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);*/
                    
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    Log.i("zhiliao", "App Helper  ������Ϣ�� EventDeliveryAck");
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    Log.i("zhiliao", "App Helper  ������Ϣ�� EventReadAck");
                    break;
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
    }
    
    public EaseNotifier getNotifier(){
	    return easeUI.getNotifier();
	}
}
