package io.taptalk.TapTalk.View.Adapter.PagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.MaxHeightRecyclerView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Adapter.TapUserMentionListAdapter;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_CAPTION_LENGTH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

public class TAPMediaPreviewPagerAdapter extends PagerAdapter {

    private Context context;
    private String instanceKey;
    private ArrayList<TAPMediaPreviewModel> images;
    private ArrayList<TAPUserModel> roomParticipants;

    public TAPMediaPreviewPagerAdapter(Context context, String instanceKey, ArrayList<TAPMediaPreviewModel> images, ArrayList<TAPUserModel> roomParticipants) {
        this.context = context;
        this.instanceKey = instanceKey;
        this.images = images;
        this.roomParticipants = roomParticipants;
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
        ConstraintLayout clUserMentionList = layout.findViewById(R.id.cl_user_mention_list);
        MaxHeightRecyclerView rvUserMentionList = layout.findViewById(R.id.rv_user_mention_list);
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
                TAPUtils.rotateAnimateInfinitely(context, ivLoading);
            } else {
                ivVideoIcon.setVisibility(View.VISIBLE);
                ivLoading.clearAnimation();
                ivLoading.setVisibility(View.GONE);
                ivImagePreview.setOnClickListener(v ->
                        TAPVideoPlayerActivity.start(
                                context,
                                instanceKey,
                                mediaPreview.getUri()));
                if (null != mediaPreview.isSizeExceedsLimit() && mediaPreview.isSizeExceedsLimit()) {
                    etCaption.setVisibility(View.GONE);
                    tvTypingIndicator.setVisibility(View.GONE);
                    vSeparator.setVisibility(View.GONE);
                    clErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorTitle.setText(String.format(context.getString(R.string.tap_format_s_error_exceed_upload_limit),
                            TAPUtils.getStringSizeLengthFile(
                                    TAPFileUploadManager.getInstance(
                                            instanceKey)
                                            .getMaxFileUploadSize())));
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
            tvTypingIndicator.setText(String.format(context.getString(R.string.tap_format_dd_letter_count), caption.length(), MAX_CAPTION_LENGTH));
        }

        etCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTypingIndicator.setText(String.format(context.getString(R.string.tap_format_dd_letter_count), s.length(), MAX_CAPTION_LENGTH));
                if (etCaption.getText().length() > 0) {
                    checkAndSearchUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
                } else {
                    hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
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

    private void checkAndSearchUserMentionList(EditText etCaption, ConstraintLayout clUserMentionList, MaxHeightRecyclerView rvUserMentionList) {
        if (roomParticipants.isEmpty()) {
            hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
            return;
        }
        String s = etCaption.getText().toString();
        if (!s.contains("@")) {
            // Return if text does not contain @
            hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
            return;
        }
        int cursorIndex = etCaption.getSelectionStart();
        int loopIndex = etCaption.getSelectionStart();
        while (loopIndex > 0) {
            // Loop text from cursor index to the left
            loopIndex--;
            char c = s.charAt(loopIndex);
            if (c == ' ' || c == '\n') {
                // Found space before @, return
                hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
                return;
            }
            if (c == '@') {
                // Found @, start searching user
                String keyword = s.substring(loopIndex + 1, cursorIndex).toLowerCase();
                if (keyword.isEmpty()) {
                    // Show all participants
                    List<TAPUserModel> searchResult = new ArrayList<>(roomParticipants);
                    searchResult.remove(TAPChatManager.getInstance(instanceKey).getActiveUser());
                    showUserMentionList(searchResult, loopIndex, cursorIndex, etCaption, clUserMentionList, rvUserMentionList);
                } else {
                    // Search participants from keyword
                    int finalLoopIndex = loopIndex;
                    new Thread(() -> {
                        List<TAPUserModel> searchResult = new ArrayList<>();
                        for (TAPUserModel user : roomParticipants) {
                            if (null != user.getUsername() &&
                                    !user.getUsername().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUsername()) &&
                                    (user.getFullname().toLowerCase().contains(keyword) ||
                                            user.getUsername().toLowerCase().contains(keyword))) {
                                // Add result if name/username matches and not self
                                searchResult.add(user);
                            }
                        }
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> showUserMentionList(searchResult, finalLoopIndex, cursorIndex, etCaption, clUserMentionList, rvUserMentionList));
                        }
                    }).start();
                }
                return;
            }
        }
        hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
    }

    private void showUserMentionList(List<TAPUserModel> searchResult, int loopIndex, int cursorIndex, EditText etCaption, ConstraintLayout clUserMentionList, MaxHeightRecyclerView rvUserMentionList) {
        if (!searchResult.isEmpty()) {
            // Show search result in list
            TapUserMentionListAdapter userMentionListAdapter = new TapUserMentionListAdapter(searchResult, user -> {
                // Append username to typed text
                if (etCaption.getText().length() >= cursorIndex) {
                    etCaption.getText().replace(loopIndex + 1, cursorIndex, user.getUsername() + " ");
                }
            });
            rvUserMentionList.setMaxHeight(TAPUtils.dpToPx(160));
            rvUserMentionList.setAdapter(userMentionListAdapter);
            if (null == rvUserMentionList.getLayoutManager()) {
                rvUserMentionList.setLayoutManager(new LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                        try {
                            super.onLayoutChildren(recycler, state);
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            clUserMentionList.setVisibility(View.VISIBLE);
        } else {
            // Result is empty
            hideUserMentionList(etCaption, clUserMentionList, rvUserMentionList);
        }
    }

    private void hideUserMentionList(EditText etCaption, ConstraintLayout clUserMentionList, MaxHeightRecyclerView rvUserMentionList) {
        boolean hasFocus = etCaption.hasFocus();
        clUserMentionList.setVisibility(View.GONE);
        if (hasFocus) {
            clUserMentionList.post(() -> {
                rvUserMentionList.setAdapter(null);
                rvUserMentionList.post(etCaption::requestFocus);
            });
        }
    }
}
