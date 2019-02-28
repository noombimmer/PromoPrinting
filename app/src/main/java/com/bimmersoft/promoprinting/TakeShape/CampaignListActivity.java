package com.bimmersoft.promoprinting.TakeShape;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.exception.ApolloException;
import com.bimmersoft.promoprinting.PromoPrintingApplication;
import com.bimmersoft.promoprinting.R;


import java.util.TimerTask;

import javax.annotation.Nonnull;



public class CampaignListActivity extends AppCompatActivity {
    private static Button mBTNSync;
    MainRecyclerViewAdapter postsAdapter;
    ViewGroup content;
    ProgressBar progressBar;
    int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    int READ_STORAGE_PERMISSION_REQUEST_CODE = 0x3;
    private int mInterval = 60000; // 60 seconds by default, can be changed later
    private Handler mHandler;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            Log.d("OnClickListener", "Fetch posts ....");

            //ApolloClient takeshape = apolloClient;
            //fetchPosts();
            Log.e("mStatusChecker"," mProcRun :" + mProcRun);
           //if (!mProcRun) {
                progressBar.setVisibility(View.VISIBLE);

                fetchPosts(); //this function can change value of mInterval.
            //}


        }
    };
    private static TimerTask mTt1;
    private static Handler mTimerHandler = new Handler();
    private static boolean mProcRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list_content);
        mBTNSync = findViewById(R.id.btnSync);
        mBTNSync.setOnClickListener(mClickListener);
        postsAdapter = new MainRecyclerViewAdapter(getApplicationContext());

        RecyclerView postsRecyclerView = (RecyclerView) findViewById(R.id.campaign_list_items);

        content = (ViewGroup) findViewById(R.id.content_holder);
        progressBar = (ProgressBar) findViewById(R.id.loading_bar);

        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);

        mHandler = new Handler();
        startRepeatingTask();
    }

    private ApolloCall.Callback<GetCampaignListQuery.Data> GetCampaignListCallback = new ApolloCall.Callback<GetCampaignListQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<GetCampaignListQuery.Data> dataResponse) {
            Log.e("", "Campaign Count: " + dataResponse.data().getCampaignList().total);

            CampaignListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv1 = CampaignListActivity.this.findViewById(R.id.txtView);
                    tv1.setText("Campaign Count: " + dataResponse.data().getCampaignList().total);
                    for (int i = 0; i < dataResponse.data().getCampaignList().total; i++) {
                        Log.e("runOnUiThread", "Campaign ID: " + dataResponse.data().getCampaignList().items().get(i).campaignId);
                    }
                    postsAdapter.setCampaigns(dataResponse.data().getCampaignList.items());
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    mProcRun = false;
                }
            });

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("GetCampaignListCallback", "onFailure Error:" + e.toString());
        }
    };

    private void fetchPosts() {
        Log.e("fetchPosts", "Fetch posts ....");
        mProcRun = true;
        PromoPrintingApplication.apolloClient.query(
                GetCampaignListQuery.builder()
                        .build()
        ).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
                .enqueue(GetCampaignListCallback);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("mStatusChecker"," mProcRun :" + mProcRun);
                if (!mProcRun) {
                    fetchPosts(); //this function can change value of mInterval.
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
