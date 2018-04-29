package com.music.smile.musicplayerfinal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by divyanshu on 5/1/18.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media player
    public MediaPlayer player;
    Handler seekHandler;
    int duration = 0;
    //current position
    private int songPosn;
    ArrayList<TrackObject> albums = new ArrayList<TrackObject>();
    private final IBinder musicBind = new MusicBinder();
    AudioManager am;

    @Override
    public void onCreate() {
        super.onCreate();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        songPosn = 0;
    }
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                player.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                player.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (player != null) {
                    player.release();
                    player = null;
                }
            }
        }
    };
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }
    public void getNextSong()
    {
        songPosn++;
        if(songPosn >= albums.size())
        {
            songPosn = 0;
        }
        initPlayer();
        play();

    }
    public void getPreviousSong()
    {
        songPosn--;
        if(songPosn>=0) {
            initPlayer();
            play();
        }
    }

    public void setList(ArrayList<TrackObject> theSongs) {
        albums = theSongs;
    }

    public void initPlayer() {
        TrackObject playSong = albums.get(songPosn);
        int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            try {
                killSounds();
                player = new MediaPlayer();
                player.setDataSource(playSong.getSongPath());
                player.prepare();
                duration = player.getDuration();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                killSounds();// finish current activity
            }
        });


    }

    @Override
    public boolean onUnbind(Intent intent) {
        am.abandonAudioFocus(mOnAudioFocusChangeListener);
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    private void killSounds() {
        try {
            player.stop();
            player.release();
        } catch (Exception e) {
            //Eat it and do nothing because it's just going to be an NPE when mPlayer is null, which doesn't matter because we're handling it
        }

    }
    public void play()
    {

        player.start();
    }
    public void pause(){player.pause();}
    public boolean isPlaying()
    {
        if (player.isPlaying())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public int getCurrentSongPosn()
    {
        return songPosn;
    }
    public int getPlayerDuration()
    {
        return duration;
    }
}