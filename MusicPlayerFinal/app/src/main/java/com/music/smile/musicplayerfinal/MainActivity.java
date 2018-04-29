package com.music.smile.musicplayerfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

//Bind and create Service in MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FileInputStream r1 = openFileInput("last.txt");
            int pos =  r1.read();
            MusicService ms = TrackFragment.musicSrv;
            ms.setSong(pos);
            TextView song_name = (TextView) findViewById(R.id.song_name_main);
            song_name.setText(ms.albums.get(ms.getCurrentSongPosn()).getSongName());
            ImageView album_art = (ImageView) findViewById(R.id.art_main);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //Set initial sound level to 50 percent of max volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.2f;
        int twentyVolume = (int) (maxVolume*percent);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, twentyVolume, 0);

        final View mainView = (View)findViewById(android.R.id.content);
        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        FragmentAdapter adapter = new FragmentAdapter(this,getSupportFragmentManager());
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.PagerTabStrip);
        Typeface font = Typeface.createFromAsset(getAssets(),"Lucida Handwriting Italic.ttf");
        //Set font for PagerTabStrip
        for (int i = 0; i < tabStrip.getChildCount(); ++i) {
            View nextChild = tabStrip.getChildAt(i);
            if (nextChild instanceof TextView) {
                TextView textViewToConvert = (TextView) nextChild;
                textViewToConvert.setTypeface(font);
            }
        }
        final Intent intent = new Intent(this,NowPlaying.class);



        //Now Playing
        LinearLayout nowPlayingBar = (LinearLayout)findViewById(R.id.now_playing_bar);
        ImageButton play = (ImageButton) findViewById(R.id.play_main);
        ImageButton next = (ImageButton)findViewById(R.id.play_next);
        ImageButton previous = (ImageButton)findViewById(R.id.play_previous);

        //Previous Song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicService ms = TrackFragment.musicSrv;
                ms.getPreviousSong();
                TextView song_name = (TextView) findViewById(R.id.song_name_main);
                song_name.setText(ms.albums.get(ms.getCurrentSongPosn()).getSongName());
                ImageView album_art = (ImageView) findViewById(R.id.art_main);
                Bitmap image = new ImageLoader(getApplicationContext(), ms.albums, ms.getCurrentSongPosn(),album_art).loadInBackground();
            }
        });
        //Play Button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicService ms = TrackFragment.musicSrv;
                if(ms.isPlaying())
                {
                    ms.pause();
                }
                else
                {
                    ms.play();
                }
            }
        });
        //Next Button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicService ms = TrackFragment.musicSrv;
                ms.getNextSong();
                TextView song_name = (TextView) findViewById(R.id.song_name_main);
                song_name.setText(ms.albums.get(ms.getCurrentSongPosn()).getSongName());
                ImageView album_art = (ImageView) findViewById(R.id.art_main);
                Bitmap image = new ImageLoader(getApplicationContext(), ms.albums, ms.getCurrentSongPosn(),album_art).loadInBackground();
            }
        });
        nowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        MusicService ms = TrackFragment.musicSrv;
        int last = ms.getCurrentSongPosn();
        try {
            FileOutputStream outputStream = openFileOutput("last.txt", MODE_PRIVATE);
            outputStream.write(last);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
