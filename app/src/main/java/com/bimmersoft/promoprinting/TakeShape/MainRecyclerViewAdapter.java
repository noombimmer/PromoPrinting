package com.bimmersoft.promoprinting.TakeShape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.bimmersoft.promoprinting.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;



/**
 * Created by nburk on 26.10.17.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.PostViewHolder> {

    private List<GetCampaignListQuery.Item> mCampaigns = Collections.emptyList();
    private Context context;
    private static final String mTakeShapeRoot = "https://images.takeshape.io/";

    MainRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setCampaigns(List<GetCampaignListQuery.Item> Campaigns) {
        this.mCampaigns = Campaigns;
        this.notifyDataSetChanged();
        Log.d("setPosts", "Updated posts in adapter: " + mCampaigns.size());
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View itemView = layoutInflater.inflate(R.layout.post_item_entry, parent, false);

        return new PostViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final GetCampaignListQuery.Item Campaign = this.mCampaigns.get(position);
        holder.setCampaign(Campaign);
    }

    @Override public int getItemCount() {
        return mCampaigns.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView mStartDate;
        private TextView mEndDate;
        private TextView mActivated;
        private TextView mStoreName;
        private TextView mCampaignID;

        private ImageView mScreenImg;
        private ImageView mPrintImg;

        private View postEntryContainer;
        private Context context;

        PostViewHolder(View itemView, Context context) {
            super(itemView);
            mCampaignID = (TextView) itemView.findViewById(R.id.tv_CampainId);
            mStartDate = (TextView) itemView.findViewById(R.id.tv_StartDate);
            mEndDate = (TextView) itemView.findViewById(R.id.tv_EndDate);
            mActivated = (TextView) itemView.findViewById(R.id.tv_Actived);
            mStoreName = (TextView) itemView.findViewById(R.id.tv_StoreName);


            mScreenImg = (ImageView) itemView.findViewById(R.id.img_Screen);
            mPrintImg = (ImageView) itemView.findViewById(R.id.img_Print);

            postEntryContainer = itemView.findViewById(R.id.campaign_entry_container);
            this.context = context;
        }
        private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        void setCampaign(final GetCampaignListQuery.Item campaign) {



            String mUrlScreen = mTakeShapeRoot + Uri.encode(campaign.screenAsset.path(),ALLOWED_URI_CHARS);
            String mUrlPrint = mTakeShapeRoot + Uri.encode(campaign.printAsset.path(),ALLOWED_URI_CHARS);

            mCampaignID.setText(campaign.campaignId());
            mStartDate.setText(campaign.startDate());
            mEndDate.setText(campaign.endDate());
            mActivated.setText(campaign._enabledAt);
            mStoreName.setText(campaign.retailer().name);

            Picasso.with(context).load(mUrlScreen).into(mScreenImg);
            Picasso.with(context).load(mUrlPrint).into(mPrintImg);
            Log.e("setCampaign-screenUrl: ",mUrlScreen);
            Log.e("setCampaign-PrintUrl: ",mUrlPrint);
            fWriteImageFromURL(mUrlScreen,campaign.campaignId() + "I");
            fWriteImageFromURL(mUrlPrint,campaign.campaignId() + "P");

            postEntryContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                }
            });

        }
        private static File fGetAppPath(){
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "bimmersoft.cache" + File.separator);
            root.mkdirs();
            return root;
        }
        private static void fWriteImageFromURL(String strURL, String filename){
            URL imageurl = null;
            try {
                imageurl = new URL(strURL);
                Bitmap bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                fWriteBitmapToFile(bitmap,filename);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("fWriteImageFromURL :P " ,e.toString());
            } catch(IOException e){
                Log.e("fWriteImageFromURL :P " ,"IOException : " + e.toString());
            } catch (NetworkOnMainThreadException e){
                Log.e("fWriteImageFromURL :P " , "NetworkOnMainThreadException :" + e.toString());
            }

        }
        private static void fWriteBitmapToFile(Bitmap bmp, String filename){

            OutputStream fOut = null;
            Uri outputFileUri;
            try {
                File root = fGetAppPath();
                File sdImageMainDirectory = new File(root, filename);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
                fOut = new FileOutputStream(sdImageMainDirectory);
            } catch (Exception e) {
                Log.e("fWriteBitmapToFile BMP " + filename, "Error occured. Please try again later." + e.toString());
                //Log.e("fWriteBitmapToFile :P " + filename, "Error occured. Please try again later." + e.getMessage());
            }
            try {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                Log.e("fWriteBitmapToFile BMP " ,e.toString());
            }
        }
        private static void fWriteBitmapToFile(ImageView bmp, String filename){
            bmp.buildDrawingCache();
            Bitmap bm=bmp.getDrawingCache();
            OutputStream fOut = null;
            Uri outputFileUri;
            try {
                File root = fGetAppPath();
                File sdImageMainDirectory = new File(root, filename);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
                fOut = new FileOutputStream(sdImageMainDirectory);
            } catch (Exception e) {
                Log.e("fWriteBitmapToFile :P " + filename, "Error occured. Please try again later." + e.toString());
                //Log.e("fWriteBitmapToFile :P " + filename, "Error occured. Please try again later." + e.getMessage());
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {

            }
        }
    }

}
