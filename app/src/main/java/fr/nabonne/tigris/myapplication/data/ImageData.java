package fr.nabonne.tigris.myapplication.data;

/**
 * Created by tigris on 4/20/17.
 */
public class ImageData {
    final public String thumbnail;
    final public String fullRez;
    final public String title;
    final public String id;

    public ImageData(String thumbnail, String fullRez, String title, String id) {
        this.thumbnail = thumbnail;
        this.fullRez = fullRez;
        this.title = title;
        this.id = id;
    }
}
