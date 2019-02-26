package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Model.TAPAttachmentModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_AUDIO;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_CALL;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_CAMERA;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_COMPOSE;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_CONTACT;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_COPY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_DOCUMENT;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_FORWARD;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_LOCATION;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_OPEN;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_REPLY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ID_SEND_SMS;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.createAttachMenu;

public class TAPAttachmentAdapter extends TAPBaseAdapter<TAPAttachmentModel, TAPBaseViewHolder<TAPAttachmentModel>> {

    private TAPAttachmentListener attachmentListener;
    View.OnClickListener onClickListener;
    private String messageToCopy = "", linkifyresult = "";
    private TAPMessageModel message;

    public TAPAttachmentAdapter(TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        this.attachmentListener = attachmentListener;
        this.onClickListener = onClickListener;
        setItems(createAttachMenu(), false);
    }

    public TAPAttachmentAdapter(List<TAPAttachmentModel> items, String messageToCopy, String linkifyresult, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        setItems(items);
        this.attachmentListener = attachmentListener;
        this.messageToCopy = messageToCopy;
        this.onClickListener = onClickListener;
        this.linkifyresult = linkifyresult;
    }

    public TAPAttachmentAdapter(List<TAPAttachmentModel> items, TAPMessageModel message, TAPAttachmentListener attachmentListener, View.OnClickListener onClickListener) {
        setItems(items);
        this.attachmentListener = attachmentListener;
        this.onClickListener = onClickListener;
        if (null != message) {
            this.message = message;
            this.messageToCopy = message.getBody();
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

            if (getItemCount() - 1 == position)
                vAttachMenuSeparator.setVisibility(View.GONE);
            else vAttachMenuSeparator.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(v -> onAttachmentClicked(item));
        }

        private void onAttachmentClicked(TAPAttachmentModel item) {
            switch (item.getId()) {
                case ID_DOCUMENT:
                    attachmentListener.onDocumentSelected();
                    break;
                case ID_CAMERA:
                    attachmentListener.onCameraSelected();
                    break;
                case ID_GALLERY:
                    attachmentListener.onGallerySelected();
                    break;
                case ID_AUDIO:
                    attachmentListener.onAudioSelected();
                    break;
                case ID_LOCATION:
                    attachmentListener.onLocationSelected();
                    break;
                case ID_CONTACT:
                    attachmentListener.onContactSelected();
                    break;
                case ID_REPLY:
                    attachmentListener.onReplySelected(message);
                    break;
                case ID_FORWARD:
                    attachmentListener.onForwardSelected(message);
                    break;
                case ID_COPY:
                    attachmentListener.onCopySelected(messageToCopy);
                    break;
                case ID_OPEN:
                    attachmentListener.onOpenLinkSelected(linkifyresult);
                    break;
                case ID_COMPOSE:
                    attachmentListener.onComposeSelected(linkifyresult);
                    break;
                case ID_CALL:
                    attachmentListener.onPhoneCallSelected(linkifyresult);
                    break;
                case ID_SEND_SMS:
                    attachmentListener.onPhoneSmsSelected(messageToCopy);
                    break;
            }
            onClickListener.onClick(itemView);
        }
    }
}
