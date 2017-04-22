package fr.nabonne.tigris.trials.flckrGrd.data;

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
import java.util.TreeSet;

/**
 * Created by tigris on 4/20/17.
 */

public class ImagesStore implements IObservableData{
    final static String urlPrefix = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=949e98778755d1982f537d56236bbb42&tags=tile&format=json&nojsoncallback=1&page=";
    final static String urlSuffix = "&extras=url_t,url_l";
    int mPages = -1;
    int mNextPage = 1;
    IObserver mObserver;
    ArrayList<ImageData> data;
    TreeSet<String> exclusions = new TreeSet<String>();// assuming it's not expected to survive application destruction

    @Override
    public void registerObserver(IObserver observer) {
        mObserver = observer;
    }

    /**
     * Calls for an additional set(page) of data to be retrieved from the endpoint
     */
    @Override
    public void getMoreData() {
        if (mNextPage == 1)
            ;// nothing to do
        else if (mNextPage > mPages) {// mPages should be known by now
            // we've reached the end we're done
            mObserver.onRefreshed();
            return;
        }
        new QueryTask(this).execute(new String[] {urlPrefix + mNextPage + urlSuffix});
    }

    /**
     * Calls for a refresh of the data. Current data will be wiped out
     */
    @Override
    public void refresh() {
        mNextPage = 1;
        if (data != null) {
            data.clear();
            mObserver.notifyDataSetChanged();
        }
        getMoreData();
    }

    @Override
    public ArrayList<ImageData> getData() {
        return data;
    }

    @Override
    public void addExcludedImage(int index) {
        // First exclude from future download
        exclusions.add(data.get(index).id);
        // Now remove from the current data
        // which is safe to do because AsyncTask only modifies the data on onPostExecute (main/ui thread)
        data.remove(index);
        mObserver.notifyItemRemoved(index);
    }

    //AsyncTask to do network work
    public static class QueryTask extends AsyncTask<String, Object, String> {
        private ImagesStore dataStore;

        public QueryTask(ImagesStore dataStore) {
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
                    dataStore.data = new ArrayList<ImageData>(perPage);
                JSONArray photo = photos.getJSONArray("photo");
                //traverse array and extract urls
                for (int i=0; i<perPage; i++) {
                    final String urlT = ((JSONObject)photo.get(i)).getString("url_t");
                    final String urlL = ((JSONObject)photo.get(i)).getString("url_l");
                    final String title = ((JSONObject)photo.get(i)).getString("title");
                    final String id = ((JSONObject)photo.get(i)).getString("id");
                    if (!urlL.isEmpty() && !urlT.isEmpty()
                            && !title.isEmpty() && !id.isEmpty()
                            && !dataStore.exclusions.contains(id)) {
                        dataStore.data.add(new ImageData(urlT, urlL, title, id));
                        final int index = dataStore.data.size()-1;
                        dataStore.mObserver.notifyItemInserted(index);
//                        dataStore.data.add(0, new ImageData(urlT, urlL, title, id));
//                        dataStore.mObserver.notifyItemInserted(0);
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
}
