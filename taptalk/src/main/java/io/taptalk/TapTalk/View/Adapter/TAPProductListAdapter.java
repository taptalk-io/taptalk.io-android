package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.GlideApp;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

public class TAPProductListAdapter extends TAPBaseAdapter<TAPProductModel, TAPBaseViewHolder<TAPProductModel>> {

    private List<TAPProductModel> items = new ArrayList<>();
    private TAPMessageModel messageModel;
    private TAPUserModel myUserModel;
    private final int TYPE_CUSTOMER = 1;
    private final int TYPE_SELLER = 2;

    public TAPProductListAdapter(TAPMessageModel messageModel, TAPUserModel myUserModel) {
        setItems(TAPUtils.getInstance().fromJSON(
                new TypeReference<List<TAPProductModel>>() {
                },
                messageModel.getBody()), false);
        this.messageModel = messageModel;
        this.myUserModel = myUserModel;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPProductModel> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProductVH(viewGroup, R.layout.tap_cell_chat_product_item);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModel.getUser().getUserID().equals(myUserModel.getUserID())) {
            return TYPE_SELLER;
        } else {
            return TYPE_CUSTOMER;
        }
    }

    public class ProductVH extends TAPBaseViewHolder<TAPProductModel> {

        FrameLayout flContainer;
        TAPRoundedCornerImageView rcivProductImage;
        TextView tvProductName, tvPrice, tvRating, tvProductDescription, tvButtonDetails, tvButtonOrder;
        ImageView ivRatingIcon;
        View vButtonSeparator;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            flContainer = itemView.findViewById(R.id.fl_container);
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
        protected void onBind(TAPProductModel item, int position) {
            if (getItemViewType() == TYPE_SELLER) {
                // My products
                vButtonSeparator.setVisibility(View.GONE);
                tvButtonOrder.setVisibility(View.GONE);
                rcivProductImage.setCornerRadius(TAPUtils.getInstance().dpToPx(11), TAPUtils.getInstance().dpToPx(2), 0, 0);
                flContainer.setForeground(itemView.getContext().getDrawable(R.drawable.tap_bg_rounded_8dp_1dp_8dp_8dp_stroke_ededed_1dp));
            } else {
                // Other seller's products
                vButtonSeparator.setVisibility(View.VISIBLE);
                tvButtonOrder.setVisibility(View.VISIBLE);
                rcivProductImage.setCornerRadius(TAPUtils.getInstance().dpToPx(2), TAPUtils.getInstance().dpToPx(11), 0, 0);
                flContainer.setForeground(itemView.getContext().getDrawable(R.drawable.tap_bg_rounded_1dp_8dp_8dp_8dp_stroke_ededed_1dp));
            }

            GlideApp.with(itemView.getContext()).load(item.getThumbnail().getThumbnail()).into(rcivProductImage);
            tvProductName.setText(item.getName());
            tvPrice.setText(TAPUtils.getInstance().formatCurrencyRp(item.getPrice()));
            tvProductDescription.setText(item.getDescription());
            if (item.getRating() > 0) {
                // Show rating
                String ratingString = Float.toString(item.getRating());
                ivRatingIcon.setVisibility(View.VISIBLE);
                tvRating.setText(ratingString);
                tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.purply));
            } else {
                // Product has no rating
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
