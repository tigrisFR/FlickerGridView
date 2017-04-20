package fr.nabonne.tigris.myapplication.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.nabonne.tigris.myapplication.MyApp;
import fr.nabonne.tigris.myapplication.R;
import fr.nabonne.tigris.myapplication.data.IObservableData;
import fr.nabonne.tigris.myapplication.data.Images;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.itemsRecyclerView)
    RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerViewAdapter mAdapter;
    private IObservableData mDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Data is stored at the application level to avoid retrieving it again for every activity
        mDataStore = ((MyApp)getApplication()).getDataStore();
        mAdapter = new MyRecyclerViewAdapter(this.getApplicationContext(), mDataStore);
        mDataStore.registerObserver(new IObservableData.IObserver() {
            @Override
            public void notifyItemInserted(int position) {
                mAdapter.notifyItemInserted(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onRefreshed() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mDataStore.refresh();
    }
}
