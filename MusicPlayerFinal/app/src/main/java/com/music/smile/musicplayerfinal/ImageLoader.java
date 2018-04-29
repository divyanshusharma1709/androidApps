package com.music.smile.musicplayerfinal;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader extends AsyncTaskLoader {
    List<TrackObject> songList = new ArrayList<TrackObject>();
    int mposition = 0;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    ImageView mAlbumArt;

    public ImageLoader(Context context, List<TrackObject> songs, int position, ImageView album) {
        super(context);
        songList = songs;
        mposition = position;
        mAlbumArt= album;
    }

    @Override
    public Bitmap loadInBackground() {
                TrackObject current = songList.get(mposition);
                final Bitmap image;
        FileOutputStream f1 = null;
        FileInputStream f2 = null;
        try {
            f2 = getContext().openFileInput(current.getSongName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(f2 !=null )
        {
           image = BitmapFactory.decodeStream(f2);
            mAlbumArt.setImageBitmap(image);
            return image;
        }
        else {
            mmr.setDataSource(current.getSongPath());
            byte[] imageData = mmr.getEmbeddedPicture();
            // convert the byte array to a bitmap
            if (imageData != null) {
                image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                image.setHeight(500);
                image.setWidth(500);
                mAlbumArt.setImageBitmap(image);
                  try {
                    f1 = getContext().openFileOutput(current.getSongName(), getContext().MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, f1);
                    f1.flush();
                    f1.close();
                } catch (FileNotFoundException e) {
                    Log.e("Save to Int storage", "Storage Failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            } else {
            mAlbumArt.setImageResource(R.drawable.ic_launcher_background);
            return null;
            }
        }

    }
}
