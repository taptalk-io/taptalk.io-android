package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import io.taptalk.Taptalk.R;

public class TAPImagePreviewPagerAdapter extends PagerAdapter {

    private ArrayList<Uri> imageUris;
    private Context mContext;

    public TAPImagePreviewPagerAdapter(Context mContext, ArrayList<Uri> imageUris) {
        this.mContext = mContext;
        this.imageUris = imageUris;
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Uri imageUri = imageUris.get(position);
        ImageView layout = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.tap_image_preview, container, false);
        //ImageView ivImage = layout.findViewById(R.id.iv_image);
        Glide.with(mContext).load(imageUri).apply(new RequestOptions().centerCrop()).into(layout);
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
