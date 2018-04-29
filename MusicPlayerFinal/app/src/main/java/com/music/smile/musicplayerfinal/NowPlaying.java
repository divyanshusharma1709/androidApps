package com.music.smile.musicplayerfinal;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by divyanshu on 6/1/18.
 */

public class NowPlaying extends AppCompatActivity {
    Handler seekHandler = new Handler();
    SeekBar seekBar;
    String notification_ID = "MusicService";
    int ID = 1;
    final MusicService ms = TrackFragment.musicSrv;
    Palette.Swatch swatch;
    Runnable run = new Runnable() {

        @Override
        public void run() {
            seekUpdation();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playing);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        ImageView album_art = (ImageView) findViewById(R.id.play_art);
        TextView song_name = (TextView) findViewById(R.id.song_name_main);
        //Get the Music Service
        TextView song_nowPlaying = (TextView) findViewById(R.id.play_song_name);
        int pos = ms.getCurrentSongPosn();
        int duration = ms.getPlayerDuration();
        //Buttons
        Button play_now_play = (Button) findViewById(R.id.now_playing);
        Button play_previous = (Button) findViewById(R.id.previous_now);
        Button play_next = (Button) findViewById(R.id.forward_now);
        play_now_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ms.player.isPlaying())
                    {ms.pause();}
                else
                    ms.play();
            }
        });
        play_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ms.getPreviousSong();
                TextView song_name = (TextView) findViewById(R.id.play_song_name);
                song_name.setText(ms.albums.get(ms.getCurrentSongPosn()).getSongName());
                ImageView album_art = (ImageView) findViewById(R.id.play_art);
                Bitmap image = new ImageLoader(getApplicationContext(), ms.albums, ms.getCurrentSongPosn(),album_art).loadInBackground();
                if (image != null) {
                    Palette p = Palette.from(image).generate();
                    Palette.Swatch swatch = p.getDarkMutedSwatch();
                    Palette.Swatch swatch1 = p.getLightMutedSwatch();

                    if (swatch != null) {
                        int color = swatch.getRgb();
                        Bitmap imageBack = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                        imageBack.eraseColor(color);
                        getWindow().getDecorView().setBackground((new BitmapDrawable(getResources(), imageBack)));
                    }
                }
            }
        });

        play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ms.getNextSong();
                TextView song_name = (TextView) findViewById(R.id.play_song_name);
                song_name.setText(ms.albums.get(ms.getCurrentSongPosn()).getSongName());
                ImageView album_art = (ImageView) findViewById(R.id.play_art);
                Bitmap image = new ImageLoader(getApplicationContext(), ms.albums, ms.getCurrentSongPosn(),album_art).loadInBackground();
                if (image != null) {
                    Palette p = Palette.from(image).generate();
                    Palette.Swatch swatch = p.getDarkMutedSwatch();
                    Palette.Swatch swatch1 = p.getLightMutedSwatch();

                    if (swatch != null) {
                        int color = swatch.getRgb();
                        Bitmap imageBack = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                        imageBack.eraseColor(color);
                        getWindow().getDecorView().setBackground((new BitmapDrawable(getResources(), imageBack)));
                    }
                }
            }
        });
        seekBar.setMax(duration);
        seekUpdation();
        Log.v("Track Duration", String.valueOf(seekBar.getMax()));
        song_nowPlaying.setText(ms.albums.get(pos).getSongName());
        //Set blurred album art to the Now Playing activity
        Bitmap imageBackground = new ImageLoader(this, ms.albums, ms.getCurrentSongPosn(), album_art).loadInBackground();
        if (imageBackground != null) {
            Palette p = Palette.from(imageBackground).generate();
            swatch = p.getDarkMutedSwatch();
            Palette.Swatch swatch1 = p.getLightMutedSwatch();

            if (swatch != null) {
                int color = swatch.getRgb();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
                Bitmap image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                image.eraseColor(color);
                Bitmap blurredBitmap = BlurBuilder.blur(this, image);
                View view = this.getWindow().getDecorView();
                view.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
            }
        }
    }
    public void seekUpdation() {

        seekBar.setProgress(ms.player.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }
    public Palette.Swatch getColor()
    {
        return swatch;
    }
}
