package com.xiye.zhiliao.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.xiye.zhiliao.MainActivity;
import com.xiye.zhiliao.R;
import com.xiye.zhiliao.ui.base.BaseActivity;
import com.xiye.zhiliao.util.PreferencesManager;

public class LoginActivity extends BaseActivity{
	
	private EditText usernameEditText,passwordEditText;
	
	private Button confirm,to_register;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		usernameEditText = (EditText) findViewById(R.id.user_id);
		passwordEditText = (EditText) findViewById(R.id.user_pwd);
		confirm = (Button) findViewById(R.id.login_btn);
		to_register = (Button) findViewById(R.id.to_register_btn);
		
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}
		});
		
		to_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private String currentUsername;
	private String currentPassword;
	private boolean progressShow;
	
	/**
	 * ��¼
	 */
	public void login() {
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

//		final long start = System.currentTimeMillis();
		// ����sdk��½������½���������
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				// ��½�ɹ��������û���
//				DemoHelper.getInstance().setCurrentUserName(currentUsername);
				// ע��Ⱥ�����ϵ�˼���
//				DemoHelper.getInstance().registerGroupAndContactListener();

				// ** ��һ�ε�¼����֮ǰlogout���ٵ�¼���������б���Ⱥ�ͻػ�
				// ** manually load all local groups and
			    EMGroupManager.getInstance().loadAllGroups();
				EMChatManager.getInstance().loadAllConversations();
				
				// ���µ�ǰ�û���nickname �˷�������������ios��������ʱ�ܹ���ʾ�û�nick
//				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
//						DemoApplication.currentUserNick.trim());
//				if (!updatenick) {
//					Log.e("LoginActivity", "update current user nick fail");
//				}
				//�첽��ȡ��ǰ�û����ǳƺ�ͷ��(���Լ���������ȡ��demoʹ�õ�һ������������)
//				DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
				
				if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}
				// ������ҳ��
				/*Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);*/
				PreferencesManager.getInstance().setLoginState(true);
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(LoginActivity.this, "��½�ɹ�", 0).show();
					}
				});
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
}
