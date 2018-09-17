package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Model.AttachmentModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

import static com.moselo.HomingPigeon.Model.AttachmentModel.createAttachMenu;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    private List<AttachmentModel> attachmentList;

    public AttachmentAdapter() {
        this.attachmentList = createAttachMenu();
    }

    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_attachment_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        holder.onBind(attachmentList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    class AttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAttachIcon;
        private TextView tvAttachTitle;
        private View vAttachMenuSeparator;


        AttachmentViewHolder(View itemView) {
            super(itemView);

            ivAttachIcon = itemView.findViewById(R.id.iv_attach_icon);
            tvAttachTitle = itemView.findViewById(R.id.tv_attach_title);
            vAttachMenuSeparator = itemView.findViewById(R.id.v_attach_menu_separator);
        }

        void onBind(AttachmentModel item, int position) {
            ivAttachIcon.setImageDrawable(itemView.getResources().getDrawable(item.getIcon()));
            tvAttachTitle.setText(itemView.getResources().getText(item.getTitleIds()));

            if (attachmentList.size()-1 == position)
                vAttachMenuSeparator.setVisibility(View.GONE);
            else vAttachMenuSeparator.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
