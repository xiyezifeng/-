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
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
	}

	// 实现ConnectionListener接口
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			// 已连接到服务器
		}

		@Override
		public void onDisconnected(final int error) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆
					} else {
						/*if (NetUtils.hasNetwork(MainActivity.this)) {
							// 连接不到聊天服务器
						} else {
							// 当前网络不可用，请检查网络设置
						}*/
					}
				}
			});
		}
	}
}
