package com.music.smile.musicplayerfinal;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by divyanshu on 2/1/18.
 */

public class FragmentAdapter extends FragmentPagerAdapter{

    private Context mContext;
    public FragmentAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
        {
            return new TrackFragment();
        }
        else
        {
            return new AlbumFragment();
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Tracks";
        } else {
            return "Albums";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
