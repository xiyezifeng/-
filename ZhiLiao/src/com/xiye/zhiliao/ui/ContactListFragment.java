package com.xiye.zhiliao.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment;
import com.easemob.exceptions.EaseMobException;
import com.xiye.zhiliao.AppHelper;
import com.xiye.zhiliao.R;
import com.xiye.zhiliao.ui.widget.ChatContactItemView;

public class ContactListFragment extends EaseContactListFragment {
	private static final String TAG = ContactListFragment.class.getSimpleName();
    private View loadingView;
    private ChatContactItemView applicationItem;

    @Override
    protected void initView() {
        super.initView();
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_contacts_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        applicationItem = (ChatContactItemView) headerView.findViewById(R.id.application_item);  
        applicationItem.setOnClickListener(clickListener);
        //���headerview
        listView.addHeaderView(headerView);
        //������ڼ���������ʾ��loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);
        //ע�������Ĳ˵�
        registerForContextMenu(listView);
        
        
    }
    
    @Override
    public void refresh() {
    	try {
			List<String> friends = 	EMContactManager.getInstance().getContactUserNames();
			Log.i("zhiliao","APPhelper ��� ����"+ friends.size());
			for (int i = 0; i < friends.size(); i++) {
				EaseUser user = new EaseUser(friends.get(i));
				AppHelper.toAddUsers.put(friends.get(i), user);
			}
			setContactsMap(AppHelper.toAddUsers);
		} catch (EaseMobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        super.refresh();
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void setUpView() {
        titleBar.setRightImageResource(R.drawable.em_add);
        titleBar.setRightLayoutClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	//����һ����ϵ�� ҳ��
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });
        
        //������ϵ������
        super.setUpView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = ((EaseUser)listView.getItemAtPosition(position)).getUsername();
                // demo��ֱ�ӽ�������ҳ�棬ʵ��һ���ǽ����û�����ҳ
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", username));
            }
        });

        
        // ������Ӻ���ҳ
        titleBar.getRightLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Toast.makeText(getActivity(), "��������  :  " + contactList.size(), 0).show(); 
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });
        
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
	
	protected class HeaderItemClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.application_item:
                // ����������֪ͨҳ��
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                break;
            default:
                break;
            }
        }
	    
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);
	    toBeProcessUsername = toBeProcessUser.getUsername();
		getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			try {
                // ɾ������ϵ��
                deleteContact(toBeProcessUser);
                // ɾ����ص�������Ϣ
            } catch (Exception e) {
                e.printStackTrace();
            }
			return true;
		}else if(item.getItemId() == R.id.add_to_blacklist){
			moveToBlacklist(toBeProcessUsername);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	


	/**
	 * ɾ����ϵ��
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final EaseUser tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
					// ɾ��db���ڴ��д��û�������
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							contactList.remove(tobeDeleteUser);
							contactListLayout.refresh();

						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), 1).show();
						}
					});

				}

			}
		}).start();

	}
}
