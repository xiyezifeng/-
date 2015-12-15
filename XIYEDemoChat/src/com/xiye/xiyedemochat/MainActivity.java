package com.xiye.xiyedemochat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xiye.chat.dispatcher.ChatHelper;
import com.xiye.xiyedemochat.ui.IMActivity;

/**
 * 启动页,展示页,demo所以不做启动页直接进主页面
 * 
 * @author windows
 */
public class MainActivity extends Activity {

	private Button chat_btn;
	private SharedPreferences preferences;
//	private String username = "xiye_05";  //小米
	private String username = "xiye_04";  //nexus 7
	private String pwd = "xiguangudan";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setview 时，判断 是否第一次进入，YES 加载 main NO 加载启动页
		setContentView(R.layout.activity_main);

		preferences = getSharedPreferences("huanxin", Activity.MODE_PRIVATE);
		boolean register = preferences.getBoolean("register", false);
		Toast.makeText(MainActivity.this, " 环信 注册 状态 " + register, 0).show();
		if (register) {
			login_IM();
		} else {
			register_IM();
			login_IM();
		}

		chat_btn = (Button) findViewById(R.id.chat_btn);
		chat_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, IMActivity.class);
				startActivity(intent);
			}
		});
	}

	private void register_IM() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(username,
							pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							// 保存用户名
							ChatHelper.getInstance().setCurrentUserName(
									username);
							Toast.makeText(MainActivity.this, " 环信   注册成功", 0)
									.show();
							Editor editor = preferences.edit();
							editor.putBoolean("register", true);
							editor.commit();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							int errorCode = e.getErrorCode();
							if (errorCode == EMError.NONETWORK_ERROR) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.network_anomalies),
										Toast.LENGTH_SHORT).show();
							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.User_already_exists),
										Toast.LENGTH_SHORT).show();
							} else if (errorCode == EMError.UNAUTHORIZED) {
								Toast.makeText(
										getApplicationContext(),
										getResources()
												.getString(
														R.string.registration_failed_without_permission),
										Toast.LENGTH_SHORT).show();
							} else if (errorCode == EMError.ILLEGAL_USER_NAME) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.illegal_user_name),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.Registration_failed)
												+ e.getMessage(),
										Toast.LENGTH_SHORT).show();
							}
						}
					});
					Editor editor = preferences.edit();
					editor.putBoolean("register", false);
					editor.commit();
				}
			}
		}).start();
	}

	private void login_IM() {
		EMChatManager.getInstance().login(username, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名
				ChatHelper.getInstance().setCurrentUserName(username);
				// 注册群组和联系人监听
				ChatHelper.getInstance().registerGroupAndContactListener();

				// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
				// ** manually load all local groups and
				EMGroupManager.getInstance().loadAllGroups();
				EMChatManager.getInstance().loadAllConversations();

				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager
						.getInstance()
						.updateCurrentUserNick(MyChatApp.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				// 异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
				ChatHelper.getInstance().getUserProfileManager()
						.asyncGetCurrentUserInfo();

				// 进入主页面
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, "环信  登陆成功",
								Toast.LENGTH_SHORT).show();
					}
				});
				
			}
			
			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(),
								getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}
}
