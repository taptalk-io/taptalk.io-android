package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPImagePreviewPagerAdapter extends PagerAdapter {

    private ArrayList<TAPMediaPreviewModel> images;
    private Context context;
    private int maxCharacter = 100;

    public TAPImagePreviewPagerAdapter(Context context, ArrayList<TAPMediaPreviewModel> images) {
        this.context = context;
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
        TAPMediaPreviewModel mediaPreview = images.get(position);
        ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.tap_image_preview, container, false);

        ImageView ivImagePreview = layout.findViewById(R.id.iv_image);
        ImageView ivVideoIcon = layout.findViewById(R.id.iv_video_icon);
        EditText etCaption = layout.findViewById(R.id.et_caption);
        TextView tvTypingIndicator = layout.findViewById(R.id.tv_typing_indicator);

        Glide.with(context).load(mediaPreview.getUri()).into(ivImagePreview);

        String caption = mediaPreview.getCaption();

        if (mediaPreview.getType() == TYPE_VIDEO) {
            ivVideoIcon.setVisibility(View.VISIBLE);
            ivImagePreview.setOnClickListener(v -> TAPUtils.getInstance().openVideoPreview(context, mediaPreview.getUri()));
        } else {
            ivVideoIcon.setVisibility(View.GONE);
            ivImagePreview.setOnClickListener(null);
        }

        if (null != caption) {
            etCaption.setText(caption);
            etCaption.setSelection(caption.length());
            tvTypingIndicator.setVisibility(View.VISIBLE);
            tvTypingIndicator.setText(caption.length() + "/" + maxCharacter);
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
                mediaPreview.setCaption(s.toString());
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
