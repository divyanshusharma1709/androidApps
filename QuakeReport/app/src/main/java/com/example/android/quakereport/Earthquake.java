package com.example.android.quakereport;

/**
 * Created by divyanshu on 2/11/17.
 */

public class Earthquake {
    private double mag;
    private String place;
    private long date;
    private String url;
    public Earthquake(double imag, String iplace, long idate, String iurl)
    {
        mag = imag;
        place = iplace;
        date = idate;
        url = iurl;
    }
    public double getMag()
    {
        return mag;
    }
    public String getPlace()
    {
        return place;
    }
    public long getDate()
    {
        return date;
    }
    public String getURL(){return url;}
}
