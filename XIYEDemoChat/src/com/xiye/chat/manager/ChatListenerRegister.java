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
			// 注销广播
			abortBroadcast();

			// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
			String msgId = intent.getStringExtra("msgid");
			// 发送方
			String username = intent.getStringExtra("from");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(username);
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
					// 把message设为已读
					EMMessage msg = conversation.getMessage(msgid);
					if (msg != null) {
						msg.isAcked = true;
					}
				}
			}
		};

		EMChatManager.getInstance().getChatOptions().setRequireAck(true);
		// 如果用到已读的回执需要把这个flag设置成true

		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		context.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
	}
}
