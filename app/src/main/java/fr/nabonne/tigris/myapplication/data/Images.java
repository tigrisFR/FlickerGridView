package fr.nabonne.tigris.myapplication.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tigris on 4/20/17.
 */

public class Images implements IObservableData{
    final static String urlPrefix = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=949e98778755d1982f537d56236bbb42&tags=tile&format=json&nojsoncallback=1&page=";
    final static String urlSuffix = "&extras=url_t,url_l";
    int mPages =-1;
    int mNextPage =-1;
    IObserver mObserver;
    ArrayList<ImageURLs> data;

    @Override
    public void registerObserver(IObserver observer) {
        mObserver = observer;
    }

    public void refresh() {
        if (mNextPage == -1)
            mNextPage = 1;
        else if (mNextPage > mPages) {
            // we've reached the end we're done
            mObserver.onRefreshed();
            return;
        }
        new QueryTask(this).execute(new String[] {urlPrefix + mNextPage + urlSuffix});
    }

    @Override
    public ArrayList<ImageURLs> getData() {
        return data;
    }

    //AsyncTask to do network work
    public static class QueryTask extends AsyncTask<String, Object, String> {
        private Images dataStore;

        public QueryTask(Images dataStore) {
            this.dataStore = dataStore;
        }

        @Override
        protected String doInBackground(String[] urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            HttpURLConnection urlConnection = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                final JSONObject respJSON = new JSONObject(s);
                JSONObject photos = respJSON.getJSONObject("photos");
                dataStore.mPages = photos.getInt("pages");
                final int perPage = photos.getInt("perpage");
                if (dataStore.data == null)
                    dataStore.data = new ArrayList<ImageURLs>(perPage);
                JSONArray photo = photos.getJSONArray("photo");
                //traverse array and extract urls
                for (int i=0; i<perPage; i++) {
                    final String urlT = ((JSONObject)photo.get(i)).getString("url_t");
                    final String urlL = ((JSONObject)photo.get(i)).getString("url_l");
                    if (!urlL.isEmpty() && !urlT.isEmpty()) {
//                        dataStore.data.add(new ImageURLs(urlT, urlL));
//                        final int index = dataStore.data.size()-1;
//                        dataStore.mObserver.notifyItemInserted(index);
                        dataStore.data.add(0, new ImageURLs(urlT, urlL));
                        dataStore.mObserver.notifyItemInserted(0);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                dataStore.mNextPage++;
                dataStore.mObserver.onRefreshed();
            }
        }
    }

//    public String queryFlickrPage(int page) {
//        URL url = null;
//        try {
//            url = new URL(urlPrefix + page + urlSuffix);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return null;
//        }
//        HttpURLConnection urlConnection = null;
//
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            // json is UTF-8 by default
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
//            StringBuilder sb = new StringBuilder();
//
//            String line = null;
//            while ((line = reader.readLine()) != null)
//            {
//                sb.append(line + "\n");
//            }
//            return sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }
//        return null;
//    }
//
//    public void mergeDataFromJSON(String s) {
//        try {
//            final JSONObject respJSON = new JSONObject(s);
//            JSONObject photos = respJSON.getJSONObject("photos");
//            mPages = photos.getInt("pages");
//            final int perPage = photos.getInt("perpage");
//            if (data == null)
//                data = new ArrayList<ImageURLs>(perPage);
//            JSONArray photo = photos.getJSONArray("photo");
//            //traverse array and extract urls
//            for (int i=0; i<perPage; i++) {
//                final String urlT = ((JSONObject)photo.get(i)).getString("url_t");
//                final String urlL = ((JSONObject)photo.get(i)).getString("url_l");
//                if (!urlL.isEmpty() && !urlT.isEmpty()) {
//                    data.add(new ImageURLs(urlT, urlL));
//                    final int index = data.size()-1;
//                    mObserver.notifyItemInserted(index);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}
