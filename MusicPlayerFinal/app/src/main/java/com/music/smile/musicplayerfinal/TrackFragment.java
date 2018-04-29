package com.music.smile.musicplayerfinal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TrackFragment extends android.support.v4.app.Fragment {
    /**
     * Handles audio focus when playing a sound file
     */
    //song list
    ArrayList<TrackObject> albums = new ArrayList<TrackObject>();
    public static MusicService musicSrv;
    Cursor musicCursor = null;
    private Intent playIntent;
    AudioManager am;
    private boolean musicBound = false;
    NotificationManager notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_track_fragment, container, false);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "Music Player");
        getSongs();
        TrackAdapter adapter = new TrackAdapter(getActivity(), albums);
        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.3f;
        int thirtyVolume = (int) (maxVolume * percent);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, thirtyVolume, 0);
        ListView gridView = (ListView) view.findViewById(R.id.grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
                musicSrv.initPlayer();
                musicSrv.play();
                int pos = musicSrv.getCurrentSongPosn();
                ImageView album_art = (ImageView) getActivity().findViewById(R.id.art_main);
                Bitmap imageBack = new ImageLoader(getActivity(), albums, pos, album_art).loadInBackground();
                Bitmap blur = BlurBuilder.blur(getActivity(), imageBack);
                if (imageBack != null) {
                    Palette p = Palette.from(imageBack).generate();
                    Palette.Swatch swatch = p.getDarkMutedSwatch();
                    Palette.Swatch swatch1 = p.getLightMutedSwatch();

                    if (swatch != null) {
                        int color = swatch.getRgb();
                        Bitmap image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                        image.eraseColor(color);
                        Bitmap blurredBitmap = BlurBuilder.blur(getActivity(), image);
                        getActivity().getCurrentFocus().setBackground((new BitmapDrawable(getResources(), blurredBitmap)));
                    }
                    }
                Intent notifyIntent = new Intent(getActivity(), NowPlaying.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setSmallIcon(R.drawable.icon).setLargeIcon(imageBack).setContentTitle(musicSrv.albums.get(pos).getSongName()).setContentInfo("Music Service")
                        .setContentIntent(pendingIntent).setOngoing(true).setLargeIcon(imageBack).setPriority(Notification.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Hello"))
                ;
                notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(001, mBuilder.build());
                TextView song_name = (TextView) getActivity().findViewById(R.id.song_name_main);
                song_name.setText(albums.get(pos).getSongName());
            }
        });
        return view;
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(albums);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    public void getSongs() {
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicCursor = musicResolver.query(musicUri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            String path = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);

            //add songs to list
            do {
                int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String data = musicCursor.getString(dataColumn);
                long thisId = musicCursor.getLong(idColumn);
                String thisPath = musicCursor.getString(titleColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                albums.add(new TrackObject(thisId, data, thisPath));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(001);
        super.onDestroy();
    }
}