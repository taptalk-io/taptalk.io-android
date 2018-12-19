package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.Taptalk.R;

public class TAPImagePreviewPagerAdapter extends PagerAdapter {

    private ArrayList<TAPImagePreviewModel> images;
    private Context mContext;

    public TAPImagePreviewPagerAdapter(Context mContext, ArrayList<TAPImagePreviewModel> images) {
        this.mContext = mContext;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TAPImagePreviewModel imageUri = images.get(position);
        ImageView layout = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.tap_image_preview, container, false);
        Glide.with(mContext).load(imageUri.getImageUris()).into(layout);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
