package com.xiye.chat.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;

public class ChatListenerRegister {
	
private static ChatListenerRegister chatListenerRegister;
	
	private ChatListenerRegister(){
	}
	
	public static ChatListenerRegister getInstance(){
		synchronized (ChatListenerRegister.class) {
			if(null == chatListenerRegister){
				chatListenerRegister = new ChatListenerRegister();
			}
		}
		return chatListenerRegister;
	}
	
	public void registerMessageListener(Context context) {
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		context.registerReceiver(msgReceiver, intentFilter);
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ע���㲥
			abortBroadcast();

			// ��Ϣid��ÿ����Ϣ��������Ψһ��һ��id��Ŀǰ��SDK���ɣ�
			String msgId = intent.getStringExtra("msgid");
			// ���ͷ�
			String username = intent.getStringExtra("from");
			// �յ�����㲥��ʱ��message�Ѿ���db���ڴ����ˣ�����ͨ��id��ȡmesage����
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(username);
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

	public void registerAckListener(Context context) {
		
		BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				abortBroadcast();
				String msgid = intent.getStringExtra("msgid");
				String from = intent.getStringExtra("from");
				EMConversation conversation = EMChatManager.getInstance()
						.getConversation(from);
				if (conversation != null) {
					// ��message��Ϊ�Ѷ�
					EMMessage msg = conversation.getMessage(msgid);
					if (msg != null) {
						msg.isAcked = true;
					}
				}
			}
		};

		EMChatManager.getInstance().getChatOptions().setRequireAck(true);
		// ����õ��Ѷ��Ļ�ִ��Ҫ�����flag���ó�true

		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		context.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
	}
}
