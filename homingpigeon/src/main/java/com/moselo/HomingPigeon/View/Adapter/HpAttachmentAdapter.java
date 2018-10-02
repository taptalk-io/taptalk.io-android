package com.moselo.HomingPigeon.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HomingPigeonDialog;
import com.moselo.HomingPigeon.Model.AttachmentModel;
import com.moselo.HomingPigeon.R;

import static com.moselo.HomingPigeon.Model.AttachmentModel.createAttachMenu;

public class HpAttachmentAdapter extends HpBaseAdapter<AttachmentModel, HpBaseViewHolder<AttachmentModel>> {

    public HpAttachmentAdapter() {
        setItems(createAttachMenu(), false);
    }

    @Override
    public HpBaseViewHolder<AttachmentModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentVH(parent, R.layout.hp_cell_attachment_menu);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public class AttachmentVH extends HpBaseViewHolder<AttachmentModel> implements View.OnClickListener {
        private ImageView ivAttachIcon;
        private TextView tvAttachTitle;
        private View vAttachMenuSeparator;
        private int position = -1;

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

            this.position = position;

            if (getItemCount() - 1 == position)
                vAttachMenuSeparator.setVisibility(View.GONE);
            else vAttachMenuSeparator.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position % 2 == 0)
                new HomingPigeonDialog.Builder(itemView.getContext())
                        .setTitle("Title Here")
                        .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the standard.")
                        .setPrimaryButtonTitle("Primary")
                        .setPrimaryButtonListener(false, v1 -> Toast.makeText(itemView.getContext(), "Primary Button", Toast.LENGTH_SHORT).show())
                        .setSecondaryButtonTitle("Secondary")
                        .setSecondaryButtonListener(true, v12 -> Toast.makeText(itemView.getContext(), "Secondary Button", Toast.LENGTH_SHORT).show())
                        .show();
            else
                new HomingPigeonDialog.Builder(itemView.getContext())
                        .setTitle("Title Here")
                        .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the standard.")
                        .setPrimaryButtonTitle("Primary")
                        .setPrimaryButtonListener(true, v1 -> Toast.makeText(itemView.getContext(), "Primary Button", Toast.LENGTH_SHORT).show())
                        .show();
        }

    }
}
