package com.xiye.xiyedemochat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class IM_PagerAdapter extends FragmentPagerAdapter{
	
	private Fragment[] fragments;

	public IM_PagerAdapter(FragmentManager fm,Fragment[]  fs) {
		super(fm);
		this.fragments = fs;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments[arg0];
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.length;
	}

}
