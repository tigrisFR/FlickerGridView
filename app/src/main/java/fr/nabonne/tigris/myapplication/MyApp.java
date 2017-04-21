package fr.nabonne.tigris.myapplication;

import android.app.Application;

import fr.nabonne.tigris.myapplication.data.ImagesStore;

/**
 * Created by tigris on 4/20/17.
 */

public class MyApp extends Application{

    private ImagesStore mDataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataStore = new ImagesStore();
    }

    public ImagesStore getDataStore() {
        return mDataStore;
    }
}
