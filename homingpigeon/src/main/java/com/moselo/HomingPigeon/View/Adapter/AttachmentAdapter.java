package com.moselo.HomingPigeon.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Model.AttachmentModel;
import com.moselo.HomingPigeon.R;

import static com.moselo.HomingPigeon.Model.AttachmentModel.createAttachMenu;

public class AttachmentAdapter extends BaseAdapter<AttachmentModel, BaseViewHolder<AttachmentModel>> {

    public AttachmentAdapter() {
        setItems(createAttachMenu(), false);
    }

    @Override
    public BaseViewHolder<AttachmentModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentVH(parent, R.layout.cell_attachment_menu);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class AttachmentVH extends BaseViewHolder<AttachmentModel> implements View.OnClickListener {
        private ImageView ivAttachIcon;
        private TextView tvAttachTitle;
        private View vAttachMenuSeparator;

        protected AttachmentVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivAttachIcon = itemView.findViewById(R.id.iv_attach_icon);
            tvAttachTitle = itemView.findViewById(R.id.tv_attach_title);
            vAttachMenuSeparator = itemView.findViewById(R.id.v_attach_menu_separator);
        }

        @Override
        protected void onBind(AttachmentModel item, int position) {
            ivAttachIcon.setImageDrawable(itemView.getResources().getDrawable(item.getIcon()));
            tvAttachTitle.setText(itemView.getResources().getText(item.getTitleIds()));

            if (getItemCount()-1 == position)
                vAttachMenuSeparator.setVisibility(View.GONE);
            else vAttachMenuSeparator.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

    }
}
