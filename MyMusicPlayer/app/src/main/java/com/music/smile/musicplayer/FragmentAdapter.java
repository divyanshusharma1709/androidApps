package com.music.smile.musicplayer;

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
            return new AlbumFragment();
        }
        else
        {
            return new TrackFragment();
        }
    }

    @Override
    public int getCount() {
        return 0;
    }
}
