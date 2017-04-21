package fr.nabonne.tigris.myapplication.data;

import java.util.ArrayList;

/**
 * Created by tigris on 4/20/17.
 */

public interface IObservableData {
    public interface IObserver {
        void notifyItemInserted(int position);
        void notifyItemRemoved(int position);
        void notifyDataSetChanged();
        void onRefreshed();
    }
    void registerObserver(IObserver observer);
    void refresh();
    void getMoreData();
    ArrayList<ImageData> getData();
    void addExcludedImage(int position);
}
