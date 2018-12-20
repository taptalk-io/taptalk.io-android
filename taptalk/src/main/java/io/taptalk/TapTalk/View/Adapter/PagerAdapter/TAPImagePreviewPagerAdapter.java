package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.taptalk.TapTalk.Model.TAPImagePreviewModel;
import io.taptalk.Taptalk.R;

public class TAPImagePreviewPagerAdapter extends PagerAdapter {

    private ArrayList<TAPImagePreviewModel> images;
    private Context mContext;
    private int maxCharacter = 100;

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
        ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tap_image_preview, container, false);

        ImageView ivImagePreview = layout.findViewById(R.id.iv_image);
        Glide.with(mContext).load(imageUri.getImageUris()).into(ivImagePreview);

        EditText etCaption = layout.findViewById(R.id.et_caption);
        TextView tvTypingIndicator = layout.findViewById(R.id.tv_typing_indicator);

        String imageCaption = imageUri.getImageCaption();

        if (null != imageCaption) {
            etCaption.setText(imageCaption);
            etCaption.setSelection(imageCaption.length());
            tvTypingIndicator.setVisibility(View.VISIBLE);
            tvTypingIndicator.setText(imageCaption.length() + "/" + maxCharacter);
        }

        etCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 == s.length()) tvTypingIndicator.setVisibility(View.GONE);
                else {
                    tvTypingIndicator.setVisibility(View.VISIBLE);
                    tvTypingIndicator.setText(s.length() + "/" + maxCharacter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imageUri.setImageCaption(s.toString());
            }
        });

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
