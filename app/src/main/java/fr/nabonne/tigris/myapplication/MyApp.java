package fr.nabonne.tigris.myapplication;

import android.app.Application;

import fr.nabonne.tigris.myapplication.data.Images;

/**
 * Created by tigris on 4/20/17.
 */

public class MyApp extends Application{

    private Images mDataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataStore = new Images();
    }

    public Images getDataStore() {
        return mDataStore;
    }
}
