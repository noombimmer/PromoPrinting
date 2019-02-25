package com.bimmersoft.promoprinting;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.bimmersoft.promoprinting.takeshape.TakeshapInf;
import com.bimmersoft.promoprinting.takeshape.*;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class RestTest extends AppCompatActivity {
    public static final String TAG = RestTest.class.getName();
    public static final String API_BASE_URL = "https://api.takeshape.io/project/8014a888-f44 9-48ad-abd5-6808f5074dc9/graphql";
    public static final String API_KEY = "32a40e4542c14b459cd5d9ce0986f3cf";
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_test);

        mGridView = (GridView) findViewById(R.id.gridview);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE_URL)
                .build();
        TakeshapInf retrofit = restAdapter.create(TakeshapInf.class);
        retrofit = ServiceGenerator.createService(TakeshapInf.class, API_KEY);

        retrofit.getCampaignList(new Callback<CampaignList>() {
            @Override
            public void success(CampaignList campaingns, Response response) {
                mGridView.setAdapter(new GridAdapter(RestTest.this, campaingns));
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG,"ERROR: " + error.getMessage());
            }
        });
        ListView listView = new ListView(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(RestTest.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });


    }
    public class HttpAsyncTask extends AsyncTask<Void, Void, Campaign> {
        @Override
        protected Campaign doInBackground(Void... params) {

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.dribbble.com")
                    .build();

            TakeshapInf retrofit = restAdapter.create(TakeshapInf.class);
//            Shot shot = retrofit.getShot();

            Campaign shot = retrofit.getShotById(30000);

            return shot;
        }

        @Override
        protected void onPostExecute(Campaign shot) {

            Toast.makeText(getApplicationContext(),
                    "Name : " + shot.getTitle() + " URL : " + shot.getUrl(),
                    Toast.LENGTH_LONG).show();
            super.onPostExecute(shot);
        }
    }

}
class ServiceGenerator {
    //public static final String API_BASE_URL = "https://your.api-base.url";
    public static final String API_BASE_URL = "https://api.takeshape.io/project/8014a888-f44 9-48ad-abd5-6808f5074dc9/graphql";


    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(API_BASE_URL)
            .setClient(new OkClient(new OkHttpClient()));

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (authToken != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", authToken);
                }
            });
        }

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}

