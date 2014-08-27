package com.artigile.android;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * @author ivanbahdanau
 */
public class MazeApp extends Application{


    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
