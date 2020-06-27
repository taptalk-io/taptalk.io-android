package io.taptalk.TapTalk.View.Adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Model.TAPAttachmentModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_AUDIO;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_CAMERA;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_CONTACT;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_DOCUMENT;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_LOCATION;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_CALL;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_COMPOSE_EMAIL;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_COPY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_DELETE;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_FORWARD;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_OPEN_LINK;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_REPLY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_SAVE_DOWNLOADS;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_SAVE_IMAGE_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_SAVE_VIDEO_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_SEND_MESSAGE;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_SEND_SMS;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.LONG_PRESS_VIEW_PROFILE;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_PICTURE_CAMERA;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_PICTURE_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.createAttachMenu;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.createImagePickerMenu;

public class TAPAttachmentAdapter extends TAPBaseAdapter<TAPAttachmentModel, TAPBaseViewHolder<TAPAttachmentModel>> {

    private String instanceKey = "";
    private TAPAttachmentListener attachmentListener;
    private View.OnClickListener onClickListener;
    private String messageToCopy = "";
    private String linkifyResult = "";
    private TAPMessageModel message;

    public TAPAttachmentAdapter(String instanceKey, boolean isImagePickerBottomSheet, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        this.instanceKey = instanceKey;
        this.attachmentListener = attachmentListener;
        this.onClickListener = onClickListener;
        if (isImagePickerBottomSheet) {
            setItems(createImagePickerMenu(instanceKey), false);
        } else {
            setItems(createAttachMenu(instanceKey), false);
        }
    }

    public TAPAttachmentAdapter(String instanceKey, List<TAPAttachmentModel> items, String messageToCopy, String linkifyResult, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        this.instanceKey = instanceKey;
        setItems(items);
        this.attachmentListener = attachmentListener;
        this.messageToCopy = messageToCopy;
        this.onClickListener = onClickListener;
        this.linkifyResult = linkifyResult;
    }

    // Mention long press menu
    public TAPAttachmentAdapter(String instanceKey, List<TAPAttachmentModel> items, TAPMessageModel message, String messageToCopy, String linkifyResult, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        this.instanceKey = instanceKey;
        setItems(items);
        this.attachmentListener = attachmentListener;
        this.message = message;
        this.messageToCopy = messageToCopy;
        this.onClickListener = onClickListener;
        this.linkifyResult = linkifyResult;
    }

    public TAPAttachmentAdapter(String instanceKey, List<TAPAttachmentModel> items, TAPMessageModel message, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        this.instanceKey = instanceKey;
        setItems(items);
        this.attachmentListener = attachmentListener;
        this.onClickListener = onClickListener;
        if (null != message) {
            this.message = message;
            switch (message.getType()) {
                case TYPE_IMAGE:
                    // TODO: 4 March 2019 TEMPORARY CLIPBOARD FOR IMAGE
                    if (null != message.getData()) {
                        this.messageToCopy = (String) message.getData().get(CAPTION);
                    }
                    break;
                case TYPE_LOCATION:
                    if (null != message.getData()) {
                        this.messageToCopy = (String) message.getData().get(ADDRESS);
                    }
                    break;
                default:
                    this.messageToCopy = message.getBody();
                    break;
            }
        }
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPAttachmentModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttachmentVH(parent, R.layout.tap_cell_attachment_menu);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class AttachmentVH extends TAPBaseViewHolder<TAPAttachmentModel> {

        private ImageView ivAttachIcon;
        private TextView tvAttachTitle;
        private View vAttachMenuSeparator;

        AttachmentVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivAttachIcon = itemView.findViewById(R.id.iv_attach_icon);
            tvAttachTitle = itemView.findViewById(R.id.tv_attach_title);
            vAttachMenuSeparator = itemView.findViewById(R.id.v_attach_menu_separator);
        }

        @Override
        protected void onBind(TAPAttachmentModel item, int position) {
            ivAttachIcon.setImageDrawable(itemView.getResources().getDrawable(item.getIcon()));
            tvAttachTitle.setText(itemView.getResources().getText(item.getTitleIds()));

            int id = item.getId();
            switch (id) {
                case SELECT_PICTURE_CAMERA:
                    setComponentColors(R.color.tapIconSelectPictureCamera, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case SELECT_PICTURE_GALLERY:
                    setComponentColors(R.color.tapIconSelectPictureGallery, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_DOCUMENT:
                    setComponentColors(R.color.tapIconAttachDocuments, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_CAMERA:
                    setComponentColors(R.color.tapIconAttachCamera, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_GALLERY:
                    setComponentColors(R.color.tapIconAttachGallery, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_AUDIO:
                    setComponentColors(R.color.tapIconAttachAudio, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_LOCATION:
                    setComponentColors(R.color.tapIconAttachLocation, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case ATTACH_CONTACT:
                    setComponentColors(R.color.tapIconAttachContact, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_REPLY:
                    setComponentColors(R.color.tapIconLongPressActionReply, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_FORWARD:
                    setComponentColors(R.color.tapIconLongPressActionForward, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_COPY:
                    setComponentColors(R.color.tapIconLongPressActionCopy, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_OPEN_LINK:
                    setComponentColors(R.color.tapIconLongPressActionOpenLink, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_COMPOSE_EMAIL:
                    setComponentColors(R.color.tapIconLongPressActionComposeEmail, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_CALL:
                    setComponentColors(R.color.tapIconLongPressActionCallNumber, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_SEND_SMS:
                    setComponentColors(R.color.tapIconLongPressActionSmsNumber, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_SAVE_IMAGE_GALLERY:
                case LONG_PRESS_SAVE_VIDEO_GALLERY:
                case LONG_PRESS_SAVE_DOWNLOADS:
                    setComponentColors(R.color.tapIconLongPressActionSaveToGallery, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_VIEW_PROFILE:
                    setComponentColors(R.color.tapIconLongPressActionViewProfile, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_SEND_MESSAGE:
                    setComponentColors(R.color.tapIconLongPressActionSendMessage, R.style.tapActionSheetDefaultLabelStyle);
                    break;
                case LONG_PRESS_DELETE:
                    setComponentColors(R.color.tapIconLongPressActionDelete, R.style.tapActionSheetDestructiveLabelStyle);
                    break;
            }

            if (getItemCount() - 1 == position) {
                vAttachMenuSeparator.setVisibility(View.GONE);
            } else {
                vAttachMenuSeparator.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> onAttachmentClicked(item));
        }

        @SuppressLint("PrivateResource")
        private void setComponentColors(@ColorRes int iconColorRes, @StyleRes int textStyleRes) {
            // Set icon color
            ImageViewCompat.setImageTintList(ivAttachIcon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), iconColorRes)));

            // Set text color
            TypedArray typedArray = itemView.getContext().obtainStyledAttributes(textStyleRes, R.styleable.TextAppearance);
            tvAttachTitle.setTextColor(typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1));
            typedArray.recycle();
        }

        private void onAttachmentClicked(TAPAttachmentModel item) {
            String instanceKey;
            try {
                TAPBaseActivity activity = (TAPBaseActivity) itemView.getContext();
                instanceKey = activity.instanceKey;
            } catch (Exception e) {
                instanceKey = "";
            }
            switch (item.getId()) {
                case SELECT_PICTURE_CAMERA:
                case ATTACH_CAMERA:
                    attachmentListener.onCameraSelected();
                    break;
                case SELECT_PICTURE_GALLERY:
                case ATTACH_GALLERY:
                    attachmentListener.onGallerySelected();
                    break;
                case ATTACH_DOCUMENT:
                    attachmentListener.onDocumentSelected();
                    break;
                case ATTACH_AUDIO:
                    attachmentListener.onAudioSelected();
                    break;
                case ATTACH_LOCATION:
                    attachmentListener.onLocationSelected();
                    break;
                case ATTACH_CONTACT:
                    attachmentListener.onContactSelected();
                    break;
                case LONG_PRESS_REPLY:
                    attachmentListener.onReplySelected(message);
                    break;
                case LONG_PRESS_FORWARD:
                    attachmentListener.onForwardSelected(message);
                    break;
                case LONG_PRESS_COPY:
                    attachmentListener.onCopySelected(messageToCopy);
                    break;
                case LONG_PRESS_OPEN_LINK:
                    attachmentListener.onOpenLinkSelected(linkifyResult);
                    break;
                case LONG_PRESS_COMPOSE_EMAIL:
                    attachmentListener.onComposeSelected(linkifyResult);
                    break;
                case LONG_PRESS_CALL:
                    attachmentListener.onPhoneCallSelected(linkifyResult);
                    break;
                case LONG_PRESS_SEND_SMS:
                    attachmentListener.onPhoneSmsSelected(messageToCopy);
                    break;
                case LONG_PRESS_SAVE_IMAGE_GALLERY:
                    attachmentListener.onSaveImageToGallery(message);
                    break;
                case LONG_PRESS_SAVE_VIDEO_GALLERY:
                    attachmentListener.onSaveVideoToGallery(message);
                    break;
                case LONG_PRESS_SAVE_DOWNLOADS:
                    attachmentListener.onSaveToDownloads(message);
                    break;
                case LONG_PRESS_VIEW_PROFILE:
                    attachmentListener.onViewProfileSelected(linkifyResult.substring(1), message);
                    break;
                case LONG_PRESS_SEND_MESSAGE:
                    attachmentListener.onSendMessageSelected(linkifyResult.substring(1));
                    break;
                case LONG_PRESS_DELETE:
                    if (null != TAPChatManager.getInstance(instanceKey).getOpenRoom())
                        attachmentListener.onDeleteMessage(TAPChatManager.getInstance(instanceKey).getOpenRoom(), message);
                    break;
            }
            onClickListener.onClick(itemView);
        }
    }
}
