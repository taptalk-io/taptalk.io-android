package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;

public class TAPProductListAdapter extends TAPBaseAdapter<TAPProductModel, TAPBaseViewHolder<TAPProductModel>> {

    private String instanceKey;
    private TAPMessageModel messageModel;
    private TAPUserModel myUserModel, recipientUser;
    private TAPChatListener chatListener;
    private final int TYPE_CUSTOMER = 1;
    private final int TYPE_SELLER = 2;

    public TAPProductListAdapter(String instanceKey, List<TAPProductModel> productModels, TAPMessageModel messageModel, TAPUserModel myUserModel, TAPChatListener chatListener) {
        this.instanceKey = instanceKey;
        setItems(productModels);

        if (null == TAPChatManager.getInstance(instanceKey).getActiveRoom()) {
            this.recipientUser = TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey)
                    .getOtherUserIdFromRoom(TAPChatManager.getInstance(instanceKey).getOpenRoom()));
        } else {
            this.recipientUser = TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey)
                    .getOtherUserIdFromRoom(TAPChatManager.getInstance(instanceKey).getActiveRoom().getRoomID()));
        }

        this.messageModel = messageModel;
        this.myUserModel = myUserModel;
        this.chatListener = chatListener;
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
        TextView tvProductName, tvPrice, tvRating, tvProductDescription, tvButtonOne, tvButtonTwo;
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
            tvButtonOne = itemView.findViewById(R.id.tv_button_one);
            tvButtonTwo = itemView.findViewById(R.id.tv_button_two);
            ivRatingIcon = itemView.findViewById(R.id.iv_rating_icon);
            vButtonSeparator = itemView.findViewById(R.id.v_button_separator);
        }

        @Override
        protected void onBind(TAPProductModel item, int position) {
            tvButtonOne.setText(item.getButtonOption1Text());
            if (!item.getButtonOption1Color().isEmpty()) {
                tvButtonOne.setTextColor(Color.parseColor("#" + item.getButtonOption1Color()));
            }
            if (getItemViewType() == TYPE_SELLER) {
                // My products
                vButtonSeparator.setVisibility(View.GONE);
                tvButtonTwo.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvButtonOne.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_single_button_ripple));
                } else {
                    tvButtonOne.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_single_button));
                }
                rcivProductImage.setCornerRadius(TAPUtils.dpToPx(11), TAPUtils.dpToPx(2), 0, 0);
                flContainer.setForeground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_rounded_8dp_1dp_8dp_8dp_stroke_eaeaea_1dp));
            } else {
                // Other seller's products
                tvButtonTwo.setText(item.getButtonOption2Text());
                if (!item.getButtonOption2Color().isEmpty()) {
                    tvButtonTwo.setTextColor(Color.parseColor("#" + item.getButtonOption2Color()));
                }
                vButtonSeparator.setVisibility(View.VISIBLE);
                tvButtonTwo.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvButtonOne.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_left_button_ripple));
                    tvButtonTwo.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_right_button_ripple));
                } else {
                    tvButtonOne.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_left_button));
                    tvButtonTwo.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_product_list_right_button));
                }
                rcivProductImage.setCornerRadius(TAPUtils.dpToPx(2), TAPUtils.dpToPx(11), 0, 0);
                flContainer.setForeground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_rounded_1dp_8dp_8dp_8dp_stroke_eaeaea_1dp));
            }

            Glide.with(itemView.getContext()).load(item.getImageURL()).into(rcivProductImage);
            tvProductName.setText(item.getName());
//            tvPrice.setText(TAPUtils.formatCurrencyRp(Long.parseLong(item.getPrice())));
            tvPrice.setText(String.format("%s %s", item.getCurrency(), TAPUtils.formatThousandSeperator(item.getPrice())));
            if ("".equals(item.getDescription()))
                tvProductDescription.setText(itemView.getResources().getString(R.string.tap_no_description));
            else {
                tvProductDescription.setText(item.getDescription());
            }
            if (!item.getRating().equals("0.0") && !item.getRating().equals("0") && !item.getRating().equals("")) {
                // Show rating
                String ratingString = item.getRating();
                ivRatingIcon.setVisibility(View.VISIBLE);
                tvRating.setText(ratingString);
                tvRating.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapColorAccent));
            } else {
                // Product has no rating
                ivRatingIcon.setVisibility(View.GONE);
                tvRating.setText(itemView.getContext().getString(R.string.tap_no_review_yet));
                tvRating.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.tapColorTextMedium));
            }

            flContainer.setOnClickListener(v -> chatListener.onOutsideClicked());
            tvButtonOne.setOnClickListener(v -> buttonLeftClicked(item));
            tvButtonTwo.setOnClickListener(v -> buttonRightClicked(item));
        }

        private void buttonLeftClicked(TAPProductModel item) {
            TAPChatManager.getInstance(instanceKey).triggerProductListBubbleLeftOrSingleButtonTapped(((Activity) itemView.getContext()), item, TAPChatManager.getInstance(instanceKey).getActiveRoom(), recipientUser, getItemViewType() == TYPE_SELLER);
        }

        private void buttonRightClicked(TAPProductModel item) {
            TAPChatManager.getInstance(instanceKey).triggerProductListBubbleRightButtonTapped(((Activity) itemView.getContext()), item, TAPChatManager.getInstance(instanceKey).getActiveRoom(), recipientUser, getItemViewType() == TYPE_SELLER);
        }
    }
}
