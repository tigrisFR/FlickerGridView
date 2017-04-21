package fr.nabonne.tigris.myapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.nabonne.tigris.myapplication.R;

/**
 * Created by tigris on 4/20/17.
 */

public class FullRezImgActivity extends Activity {
    @BindView(R.id.imageView)
    ImageView imgView;
    @BindView(R.id.imgTitle)
    TextView titleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullrezimg);
        ButterKnife.bind(this);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            final String url = startingIntent.getStringExtra("urlL");
            if (url != null && !url.isEmpty()) {
                Picasso.with(this.getApplicationContext())
                        .load(url)
                        .into(imgView);
            }
            final String title = startingIntent.getStringExtra("title");
            if (title != null && !title.isEmpty()) {
                titleView.setText(title);
            }
        }
    }
}
