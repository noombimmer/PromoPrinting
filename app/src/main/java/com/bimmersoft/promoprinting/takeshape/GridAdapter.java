package com.bimmersoft.promoprinting.takeshape;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private CampaignList mCampaignList;

    public GridAdapter(Context context, CampaignList campaignList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCampaignList = campaignList;
    }

    public int getCount() {
        return mCampaignList.getShots().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mCampaignList.getShots().get(position).getImageUrl())
                .into(imageView);

        return imageView;
    }

}
