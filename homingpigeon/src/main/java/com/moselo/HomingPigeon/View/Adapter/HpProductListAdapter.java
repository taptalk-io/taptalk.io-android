package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpRoundedCornerImageView;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpProductModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.ArrayList;
import java.util.List;

public class HpProductListAdapter extends HpBaseAdapter<HpProductModel, HpBaseViewHolder<HpProductModel>> {

    private List<HpProductModel> items = new ArrayList<>();
    private HpMessageModel messageModel;
    private HpUserModel myUserModel;
    private final int TYPE_CUSTOMER = 1;
    private final int TYPE_SELLER = 2;

    public HpProductListAdapter(HpMessageModel messageModel, HpUserModel myUserModel) {
        setItems(HpUtils.getInstance().fromJSON(
                new TypeReference<List<HpProductModel>>() {
                },
                messageModel.getBody()), false);
        this.messageModel = messageModel;
        this.myUserModel = myUserModel;
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpProductModel> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProductVH(viewGroup, R.layout.hp_cell_chat_product_item);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModel.getUser().getUserID().equals(myUserModel.getUserID())) {
            return TYPE_SELLER;
        } else {
            return TYPE_CUSTOMER;
        }
    }

    public class ProductVH extends HpBaseViewHolder<HpProductModel> {

        HpRoundedCornerImageView rcivProductImage;
        TextView tvProductName, tvPrice, tvRating, tvProductDescription, tvButtonDetails, tvButtonOrder;
        ImageView ivRatingIcon;
        View vButtonSeparator;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rcivProductImage = itemView.findViewById(R.id.rciv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
            tvButtonDetails = itemView.findViewById(R.id.tv_button_details);
            tvButtonOrder = itemView.findViewById(R.id.tv_button_order);
            ivRatingIcon = itemView.findViewById(R.id.iv_rating_icon);
            vButtonSeparator = itemView.findViewById(R.id.v_button_separator);
        }

        @Override
        protected void onBind(HpProductModel item, int position) {
            if (getItemViewType() == TYPE_SELLER) {
                vButtonSeparator.setVisibility(View.GONE);
                tvButtonOrder.setVisibility(View.GONE);
            } else {
                vButtonSeparator.setVisibility(View.VISIBLE);
                tvButtonOrder.setVisibility(View.VISIBLE);
            }

            GlideApp.with(itemView.getContext()).load(item.getThumbnail().getThumbnail()).into(rcivProductImage);
            tvProductName.setText(item.getName());
            tvPrice.setText(HpUtils.getInstance().formatCurrencyRp(item.getPrice()));
            tvProductDescription.setText(item.getDescription());
            if (item.getRating() > 0) {
                // Show rating
                String ratingString = Float.toString(item.getRating());
                ivRatingIcon.setVisibility(View.VISIBLE);
                tvRating.setText(ratingString);
                tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.purply));
            } else {
                // Hide rating
                ivRatingIcon.setVisibility(View.GONE);
                tvRating.setText(itemView.getContext().getString(R.string.no_review_yet));
                tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.grey_9b));
            }

            tvButtonDetails.setOnClickListener(v -> viewProductDetail());
            tvButtonOrder.setOnClickListener(v -> orderProduct());
        }

        private void viewProductDetail() {
            // TODO: 12 November 2018 viewProductDetail
        }

        private void orderProduct() {
            // TODO: 12 November 2018 orderProduct
        }
    }
}
