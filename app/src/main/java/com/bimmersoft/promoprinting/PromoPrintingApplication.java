package com.bimmersoft.promoprinting;

import android.app.Application;
import android.util.Log;


import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy;
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.bimmersoft.promoprinting.base.AppInfo;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PromoPrintingApplication extends Application {

    //  private static final String BASE_URL = "http://10.0.2.2:3010/graphql/";
//  private static final String SUBSCRIPTION_BASE_URL = "ws://10.0.2.2:3010/subscriptions";
    private static final String BASE_URL = "https://api.takeshape.io/project/8014a888-f449-48ad-abd5-6808f5074dc9/graphql";
    private static final String API_KEY = "32a40e4542c14b459cd5d9ce0986f3cf";
    //private static final String SUBSCRIPTION_BASE_URL = "wss://api.githunt.com/subscriptions";

    private static final String SQL_CACHE_NAME = "takeshape";
    public static ApolloClient apolloClient;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInfo.init(getApplicationContext());

/*
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String authHeader = "Bearer " + API_KEY;
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", authHeader);
                    Log.d("AUTH_TAG", authHeader);
                    return chain.proceed(builder.build());
                })
                .build();
        ApolloSqlHelper apolloSqlHelper = new ApolloSqlHelper(this, SQL_CACHE_NAME);

        NormalizedCacheFactory normalizedCacheFactory = new LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)
                .chain(new SqlNormalizedCacheFactory(apolloSqlHelper));


        CacheKeyResolver cacheKeyResolver = new CacheKeyResolver() {
            @NotNull
            @Override
            public CacheKey fromFieldRecordSet(@NotNull ResponseField field, @NotNull Map<String, Object> recordSet) {
                String typeName = (String) recordSet.get("__typename");
                if (recordSet.containsKey("campaignId")) {
                    String typeNameAndIDKey = (String) recordSet.get("campaignId");
                    return CacheKey.from(typeNameAndIDKey);
                }
                return CacheKey.NO_KEY;
            }

            // Use this resolver to customize the key for fields with variables: eg entry(repoFullName: $repoFullName).
            // This is useful if you want to make query to be able to resolved, even if it has never been run before.
            @NotNull
            @Override
            public CacheKey fromFieldArguments(@NotNull ResponseField field, @NotNull Operation.Variables variables) {
                return CacheKey.NO_KEY;
            }
        };


        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                //.normalizedCache(normalizedCacheFactory, cacheKeyResolver)
                /*.subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))*/
                .build();
    }

    public ApolloClient apolloClient() {
        return apolloClient;
    }

}
