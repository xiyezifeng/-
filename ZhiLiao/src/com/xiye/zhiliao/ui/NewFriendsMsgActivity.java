package com.xiye.zhiliao.ui;

import java.util.zip.Inflater;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.xiye.zhiliao.AppHelper;
import com.xiye.zhiliao.R;
import com.xiye.zhiliao.ui.base.BaseActivity;

public class NewFriendsMsgActivity extends BaseActivity{
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_friends_msg);

		listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(new ListAdapter());
		/*List<InviteMessage> msgs = dao.getMessagesList();
		//…Ë÷√adapter
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);*/
		
	}

	public void back(View view) {
		finish();
	}
	
	private class ListAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return AppHelper.friendMessage.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return AppHelper.friendMessage.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(NewFriendsMsgActivity.this).inflate(R.layout.new_friend_item, null);
			Button button = (Button) convertView.findViewById(R.id.agree);
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(AppHelper.friendMessage.get(position));
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String form = AppHelper.friendMessage.get(position);
					try {
						EMChatManager.getInstance().acceptInvitation(form);
					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			return convertView;
		}
		
	}
}
