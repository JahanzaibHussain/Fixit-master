package com.jahanzaib.fixit.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jahanzaib.fixit.home.HomeActivity;
import com.jahanzaib.fixit.location.LocationActivity;
import com.jahanzaib.fixit.mywall.WallActivity;

/**
 * Created by Jahanzaib on 1/7/17.
 */

public class SimpleFragmentPager extends FragmentStatePagerAdapter {

	public SimpleFragmentPager(FragmentManager fm) {
		super(fm);
	}


	@Override
	public Fragment getItem(int position) {

		switch (position) {
			case 0:
				return new WallActivity();
			case 1:
				return new HomeActivity();
			case 2:
				return new LocationActivity();
			default:
				break;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Wall";
			case 1:
				return "Home";
			case 2:
				return "Location";
			default:
				break;
		}
		return null;
	}
}
