package com.music.smile.musicplayerfinal;

/**
 * Created by divyanshu on 3/1/18.
 */

public class TrackObject {
    private long ID;
    private String songName;
    private String songPath;
    private int mImageResourceID;
    TrackObject(long song_ID, String path, String name)
    {
        ID = song_ID;
        songName = name;
        songPath = path;
    }
        TrackObject(long song_ID, String song)
        {
            ID = song_ID;
            songName = song;
        }
    public String getSongPath()
    {
        return songPath;
    }
    public String getSongName() {return songName;}
    public int getmImageResourceID()
    {
        return mImageResourceID;
    }
    public long getID() {return ID;}
}
