package com.xiye.chat.manager;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;

public class ChatStateManager {

	private static ChatStateManager chatStateManager;

	private ChatStateManager() {
		init();
	}

	public static ChatStateManager getInstance() {
		synchronized (ChatStateManager.class) {
			if (null == chatStateManager) {
				chatStateManager = new ChatStateManager();
			}
		}
		return chatStateManager;
	}

	private void init() {
		// ע��һ����������״̬��listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
	}

	// ʵ��ConnectionListener�ӿ�
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			// �����ӵ�������
		}

		@Override
		public void onDisconnected(final int error) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// ��ʾ�ʺ��Ѿ����Ƴ�
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// ��ʾ�ʺ��������豸��½
					} else {
						/*if (NetUtils.hasNetwork(MainActivity.this)) {
							// ���Ӳ������������
						} else {
							// ��ǰ���粻���ã�������������
						}*/
					}
				}
			});
		}
	}
}
