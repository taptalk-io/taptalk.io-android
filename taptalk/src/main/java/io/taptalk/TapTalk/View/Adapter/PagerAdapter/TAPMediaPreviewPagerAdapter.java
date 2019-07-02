package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPMediaPreviewPagerAdapter extends PagerAdapter {

    private ArrayList<TAPMediaPreviewModel> images;
    private Context context;
    private int maxCharacter = 100;

    public TAPMediaPreviewPagerAdapter(Context context, ArrayList<TAPMediaPreviewModel> images) {
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
        ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.tap_cell_media_preview, container, false);

        ConstraintLayout clErrorMessage = layout.findViewById(R.id.cl_error_message);
        ImageView ivImagePreview = layout.findViewById(R.id.iv_image);
        ImageView ivVideoIcon = layout.findViewById(R.id.iv_video_icon);
        ImageView ivLoading = layout.findViewById(R.id.iv_loading);
        TextView tvTypingIndicator = layout.findViewById(R.id.tv_caption_letter_count);
        TextView tvErrorTitle = layout.findViewById(R.id.tv_error_title);
        EditText etCaption = layout.findViewById(R.id.et_caption);
        View vSeparator = layout.findViewById(R.id.v_separator);

        Glide.with(context).load(mediaPreview.getUri()).into(ivImagePreview);

        String caption = mediaPreview.getCaption();

        if (mediaPreview.getType() == TYPE_VIDEO) {
            if (mediaPreview.isLoading()) {
                ivVideoIcon.setVisibility(View.GONE);
                ivLoading.setVisibility(View.VISIBLE);
                etCaption.setVisibility(View.GONE);
                tvTypingIndicator.setVisibility(View.GONE);
                vSeparator.setVisibility(View.GONE);
                clErrorMessage.setVisibility(View.GONE);
                ivImagePreview.setOnClickListener(null);
                TAPUtils.getInstance().rotateAnimateInfinitely(context, ivLoading);
            } else {
                ivVideoIcon.setVisibility(View.VISIBLE);
                ivLoading.clearAnimation();
                ivLoading.setVisibility(View.GONE);
                ivImagePreview.setOnClickListener(v -> TAPUtils.getInstance().openVideoPreview(context, mediaPreview.getUri()));
                if (null != mediaPreview.isSizeExceedsLimit() && mediaPreview.isSizeExceedsLimit()) {
                    etCaption.setVisibility(View.GONE);
                    tvTypingIndicator.setVisibility(View.GONE);
                    vSeparator.setVisibility(View.GONE);
                    clErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorTitle.setText(String.format(context.getString(R.string.tap_error_exceed_upload_limit),
                            TAPUtils.getInstance().getStringSizeLengthFile(TAPFileUploadManager.getInstance().maxUploadSize)));
                } else {
                    etCaption.setVisibility(View.VISIBLE);
                    tvTypingIndicator.setVisibility(View.VISIBLE);
                    vSeparator.setVisibility(View.VISIBLE);
                    clErrorMessage.setVisibility(View.GONE);
                }
            }
        } else {
            ivVideoIcon.setVisibility(View.GONE);
            ivLoading.clearAnimation();
            ivLoading.setVisibility(View.GONE);
            etCaption.setVisibility(View.VISIBLE);
            tvTypingIndicator.setVisibility(View.VISIBLE);
            vSeparator.setVisibility(View.VISIBLE);
            clErrorMessage.setVisibility(View.GONE);
            ivImagePreview.setOnClickListener(null);
        }

        if (null != caption && !mediaPreview.isLoading()) {
            etCaption.setText(caption);
            etCaption.setSelection(caption.length());
            tvTypingIndicator.setVisibility(View.VISIBLE);
            tvTypingIndicator.setText(String.format(context.getString(R.string.tap_letter_count), caption.length(), maxCharacter));
        }

        etCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTypingIndicator.setText(String.format(context.getString(R.string.tap_letter_count), s.length(), maxCharacter));
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
