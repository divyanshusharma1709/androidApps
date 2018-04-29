package com.music.smile.musicplayerfinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by divyanshu on 3/1/18.
 */
public class TrackAdapter extends BaseAdapter {
    private Context mContext;
    private List<TrackObject> albumList;


    public TrackAdapter(Context c, List<TrackObject> albums) {
        mContext = c;
        albumList = albums;

    }

    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.track_list_item, parent, false);
            TextView textView = (TextView) listItemView.findViewById(R.id.album_name);
            ImageView coverArt = (ImageView) listItemView.findViewById(R.id.album_art);
            TrackObject current = albumList.get(position);
            textView.setText(current.getSongName());
            Bitmap album = new ImageLoader(listItemView.getContext(), albumList, position, coverArt).loadInBackground();
            listItemView.setTag(position);
        } else {
            listItemView = (View) convertView;
            TextView textView = (TextView) listItemView.findViewById(R.id.album_name);
            ImageView coverArt = (ImageView) listItemView.findViewById(R.id.album_art);
            TrackObject current = albumList.get(position);
            textView.setText(current.getSongName());
            Bitmap album = new ImageLoader(listItemView.getContext(), albumList, position, coverArt).loadInBackground();
            listItemView.setTag(position);
        }
        return listItemView;

    }


}