package fr.nabonne.tigris.myapplication.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.nabonne.tigris.myapplication.R;
import fr.nabonne.tigris.myapplication.data.IObservableData;

/**
 * Created by tigris on 4/20/17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {
    Context mAppContext;
    IObservableData data;
    private View.OnClickListener mCommonClickListener;

    private static String[] testURLs = {
            "https://farm3.staticflickr.com/2849/33969447032_727bc1954a_t.jpg",
            "https://farm3.staticflickr.com/2867/33969443852_81af9555d9_b.jpg",
            "https://farm4.staticflickr.com/3931/33742057300_0398372013_t.jpg"
    };

    public MyRecyclerViewAdapter(Context applicationContext, IObservableData data) {
        mAppContext = applicationContext;
        this.data = data;// observable will call observer methods to notify of updates
    }

    public void setCommonClickListener(View.OnClickListener l) {
        mCommonClickListener = l;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerview_element)
        ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_element, parent, false);

        // set the view's size, margins, paddings and layout parameters
        //...

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ViewHolder holder2 = (ViewHolder) holder ;

        Picasso.with(mAppContext)
                .load(data.getData().get(position).thumbnail)
                .resize(120, 120)
                .centerCrop()
                .into(holder2.mImageView);

        //click listener:
        if (mCommonClickListener == null)
            return;
        else
            holder2.itemView.setOnClickListener(mCommonClickListener);
//        final String dataSetId = mDataset.get(position);//not actually an ID, we're traversing the list
//        holder2.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //do not use position to ID the item, as IT MAY HAVE CHANGED
//                MyRecyclerViewAdapter.this.removeItem(dataSetId);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.getData()==null? 0:data.getData().size();
    }
}
