package io.moselo.SampleApps.CustomBubbleViewHolder;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Locale;

import io.moselo.SampleApps.CustomBubbleListener.OrderCardBubbleListener;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPCourierModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOrderModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRecipientModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.ACCEPTED_BY_SELLER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.ACTIVE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.CANCELLED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.CONFIRMED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.DECLINED_BY_SELLER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.DISAGREED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.NOT_CONFIRMED_BY_CUSTOMER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.OVERPAID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.PAYMENT_INCOMPLETE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.REVIEW_COMPLETED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.WAITING_PAYMENT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OrderStatus.WAITING_REVIEW;

public class OrderCardVH extends TAPBaseChatViewHolder {
    private ConstraintLayout clContainer, clCard, clButtonDetail, clProductPreview, clButtonMoreItems, clDateTime;
    private ConstraintLayout clRecipient, clNotes, clCourier, clAdditionalCost, clDiscount, clTotalPrice;
    private LinearLayout llOrderStatusGuide, llButtonOrderStatus;
    private TextView tvOrderID, tvProductName, tvProductPrice, tvProductQty, tvButtonMoreItems;
    private TextView tvDate, tvTime, tvRecipientDetails, tvCourierType, tvCourierCost, tvNotes;
    private TextView tvAdditionalCost, tvDiscount, tvTotalPrice, tvOrderStatus, tvReportOrder, tvButtonOrderAction;
    private ImageView ivProductThumbnail, ivCourierLogo;
    private View vCardMarginLeft, vCardMarginRight, vBadgeAdditional, vBadgeDiscount, vTotalPriceSeparator;
    private TAPMessageAdapter adapter;
    private TAPUserModel myUserModel;
    private OrderCardBubbleListener listener;

    public OrderCardVH(ViewGroup parent, int itemLayoutId, TAPMessageAdapter adapter, TAPUserModel myUserModel, OrderCardBubbleListener listener) {
        super(parent, itemLayoutId);
        this.adapter = adapter;
        this.myUserModel = myUserModel;
        this.listener = listener;
        clContainer = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_container);
        clCard = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_card);
        clButtonDetail = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_button_detail);
        clProductPreview = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_product_preview);
        clButtonMoreItems = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_button_more_items);
        clDateTime = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_date_time);
        clRecipient = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_recipient);
        clNotes = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_notes);
        clCourier = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_courier);
        clAdditionalCost = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_additional_cost);
        clDiscount = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_discount);
        clTotalPrice = itemView.findViewById(io.taptalk.Taptalk.R.id.cl_total_price);
        llOrderStatusGuide = itemView.findViewById(io.taptalk.Taptalk.R.id.ll_order_status_guide);
        llButtonOrderStatus = itemView.findViewById(io.taptalk.Taptalk.R.id.ll_button_order_status);
        tvOrderID = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_order_id);
        tvProductName = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_product_name);
        tvProductPrice = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_product_price);
        tvProductQty = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_product_qty);
        tvButtonMoreItems = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_button_more_items);
        tvDate = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_date);
        tvTime = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_time);
        tvRecipientDetails = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_recipient_details);
        tvCourierType = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_courier_type);
        tvCourierCost = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_courier_cost);
        tvNotes = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_notes);
        tvAdditionalCost = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_additional_cost);
        tvDiscount = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_discount);
        tvTotalPrice = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_total_price);
        tvOrderStatus = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_order_status);
        tvReportOrder = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_report_order);
        tvButtonOrderAction = itemView.findViewById(io.taptalk.Taptalk.R.id.tv_button_order_action);
        ivProductThumbnail = itemView.findViewById(io.taptalk.Taptalk.R.id.iv_product_thumbnail);
        ivCourierLogo = itemView.findViewById(io.taptalk.Taptalk.R.id.iv_courier_logo);
        vCardMarginLeft = itemView.findViewById(io.taptalk.Taptalk.R.id.v_card_margin_left);
        vCardMarginRight = itemView.findViewById(io.taptalk.Taptalk.R.id.v_card_margin_right);
        vBadgeAdditional = itemView.findViewById(io.taptalk.Taptalk.R.id.v_badge_additional_updated);
        vBadgeDiscount = itemView.findViewById(io.taptalk.Taptalk.R.id.v_badge_discount_updated);
        vTotalPriceSeparator = itemView.findViewById(io.taptalk.Taptalk.R.id.v_total_price_separator);
    }

    @Override
    protected void onBind(TAPMessageModel item, int position) {
        TAPOrderModel order = TAPUtils.getInstance().fromJSON(new TypeReference<TAPOrderModel>() {
        }, item.getBody());

        // Set initial data
        tvOrderID.setText(order.getOrderID());
        tvDate.setText(TAPTimeFormatter.getInstance().formatTime(order.getOrderTime(), "E dd MMM yyyy"));
        tvTime.setText(TAPTimeFormatter.getInstance().formatTime(order.getOrderTime(), "HH:mm"));
//            clContainer.setVisibility(View.VISIBLE);
//            clContainer.setLayoutParams(clContainer.getLayoutParams());
        showProductPreview(order);

        if (isMessageFromMySelf(item)) {
            // Show bubble on right side
            vCardMarginLeft.setVisibility(View.VISIBLE);
            vCardMarginRight.setVisibility(View.GONE);
            clButtonDetail.setBackground(itemView.getContext().getDrawable(io.taptalk.Taptalk.R.drawable.tap_bg_purple_header_right_ripple));
        } else {
            // Show bubble on left side
            vCardMarginLeft.setVisibility(View.GONE);
            vCardMarginRight.setVisibility(View.VISIBLE);
            clButtonDetail.setBackground(itemView.getContext().getDrawable(io.taptalk.Taptalk.R.drawable.tap_bg_purple_header_left_ripple));
        }

        // Update layout according to order status
        Context c = itemView.getContext();
        switch (order.getOrderStatus()) {
            case NOT_CONFIRMED_BY_CUSTOMER:
                // Customer can confirm order
                // Seller waits for user confirmation
                showRecipient(order);
                showNotes(order);
                showCourier(order);
                llOrderStatusGuide.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showOrderPrice(order, false);
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.review_and_confirm));
                    tvOrderStatus.setVisibility(View.GONE);
                } else {
                    showOrderPrice(order, true);
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.waiting_user_confirmation), c.getResources().getColor(io.taptalk.Taptalk.R.color.orangeish));
                    tvButtonOrderAction.setVisibility(View.GONE);
                }
                break;
            case CANCELLED_BY_CUSTOMER:
                // Order is canceled
                showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.order_canceled), c.getResources().getColor(io.taptalk.Taptalk.R.color.tomato));
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                tvButtonOrderAction.setVisibility(View.GONE);
                break;
            case CONFIRMED_BY_CUSTOMER:
                // Customer waits for seller confirmation
                // Seller can confirm order
                showRecipient(order);
                showNotes(order);
                showCourier(order);
                llOrderStatusGuide.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showOrderPrice(order, true);
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.waiting_confirmation), c.getResources().getColor(io.taptalk.Taptalk.R.color.orangeish));
                    tvButtonOrderAction.setVisibility(View.GONE);
                } else {
                    showOrderPrice(order, false);
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.review_and_confirm));
                    tvOrderStatus.setVisibility(View.GONE);
                }
                break;
            case DECLINED_BY_SELLER:
                // Order is canceled
                showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.order_declined), c.getResources().getColor(io.taptalk.Taptalk.R.color.tomato));
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                tvButtonOrderAction.setVisibility(View.GONE);
                break;
            case ACCEPTED_BY_SELLER:
            case WAITING_PAYMENT:
                // User can proceed to payment
                // Seller waits for user to complete payment
                showRecipient(order);
                showNotes(order);
                showCourier(order);
                llOrderStatusGuide.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showOrderPrice(order, false);
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.pay_now));
                    tvOrderStatus.setVisibility(View.GONE);
                } else {
                    showOrderPrice(order, true);
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.waiting_payment), c.getResources().getColor(io.taptalk.Taptalk.R.color.orangeish));
                    tvButtonOrderAction.setVisibility(View.GONE);
                }
                break;
            case DISAGREED_BY_CUSTOMER:
                // Order is canceled
                showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.user_disagreed), c.getResources().getColor(io.taptalk.Taptalk.R.color.tomato));
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                tvButtonOrderAction.setVisibility(View.GONE);
                break;
            case PAYMENT_INCOMPLETE:
                // User is required to complete payment before proceeding
                // Seller waits for user to complete payment
                showRecipient(order);
                showNotes(order);
                showCourier(order);
                llOrderStatusGuide.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showOrderPrice(order, false);
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.pay_now));
                    tvOrderStatus.setVisibility(View.GONE);
                } else {
                    showOrderPrice(order, true);
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.payment_incomplete), c.getResources().getColor(io.taptalk.Taptalk.R.color.orangeish));
                    tvButtonOrderAction.setVisibility(View.GONE);
                }
                break;
            case ACTIVE:
                // User waits for seller to complete the service
                // Seller can mark order as finished
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.payment_confirmed), c.getResources().getColor(io.taptalk.Taptalk.R.color.tealish));
                    tvButtonOrderAction.setVisibility(View.GONE);
                } else {
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.active_order), c.getResources().getColor(io.taptalk.Taptalk.R.color.tealish));
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.mark_as_finished));
                }
                break;
            case OVERPAID:
                // Order is canceled
                showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.order_overpaid), c.getResources().getColor(io.taptalk.Taptalk.R.color.tomato));
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                tvButtonOrderAction.setVisibility(View.GONE);
                break;
            case WAITING_REVIEW:
                // User can write a review on the product
                // Seller has completed the order
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                if (isCustomer(order)) {
                    showActionButton(c.getString(io.taptalk.Taptalk.R.string.write_review));
                    llOrderStatusGuide.setVisibility(View.VISIBLE);
                    tvOrderStatus.setVisibility(View.GONE);
                } else {
                    showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.order_completed), c.getResources().getColor(io.taptalk.Taptalk.R.color.tealish));
                    llOrderStatusGuide.setVisibility(View.GONE);
                }
                break;
            case REVIEW_COMPLETED:
                // Order is completed for both sides
                showOrderStatus(c.getString(io.taptalk.Taptalk.R.string.order_completed), c.getResources().getColor(io.taptalk.Taptalk.R.color.tealish));
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                break;
            default:
                // Undefined status
                showOrderStatus("", 0);
                hideOrderPrice();
                clRecipient.setVisibility(View.GONE);
                clNotes.setVisibility(View.GONE);
                clCourier.setVisibility(View.GONE);
                llOrderStatusGuide.setVisibility(View.GONE);
                tvButtonOrderAction.setVisibility(View.GONE);
                break;
        }

        // Set listeners
        //clContainer.setOnClickListener(v -> listener.onOutsideClicked());
        //clCard.setOnClickListener(v -> viewOrderDetail());
        clCard.setOnClickListener(v -> listener.onOrderDetail());
        clButtonDetail.setOnClickListener(v -> viewOrderDetail());
        tvReportOrder.setOnClickListener(v -> reportOrder());
        llButtonOrderStatus.setOnClickListener(v -> viewOrderStatus());

        // TODO: 15 November 2018 TESTING ORDER STATUS
        clButtonDetail.setOnClickListener(v -> {
            if (order.getOrderStatus() < 12) {
                order.setOrderStatus(order.getOrderStatus() + 1);
            } else {
                order.setOrderStatus(0);
            }
            switch (order.getOrderStatus()) {
                case NOT_CONFIRMED_BY_CUSTOMER:
                    Toast.makeText(c, order.getOrderStatus() + " - NOT_CONFIRMED_BY_CUSTOMER", Toast.LENGTH_LONG).show();
                    break;
                case CANCELLED_BY_CUSTOMER:
                    Toast.makeText(c, order.getOrderStatus() + " - CANCELLED_BY_CUSTOMER", Toast.LENGTH_LONG).show();
                    break;
                case CONFIRMED_BY_CUSTOMER:
                    Toast.makeText(c, order.getOrderStatus() + " - CONFIRMED_BY_CUSTOMER", Toast.LENGTH_LONG).show();
                    break;
                case DECLINED_BY_SELLER:
                    Toast.makeText(c, order.getOrderStatus() + " - DECLINED_BY_SELLER", Toast.LENGTH_LONG).show();
                    break;
                case ACCEPTED_BY_SELLER:
                    Toast.makeText(c, order.getOrderStatus() + " - ACCEPTED_BY_SELLER", Toast.LENGTH_LONG).show();
                    break;
                case DISAGREED_BY_CUSTOMER:
                    Toast.makeText(c, order.getOrderStatus() + " - DISAGREED_BY_CUSTOMER", Toast.LENGTH_LONG).show();
                    break;
                case WAITING_PAYMENT:
                    Toast.makeText(c, order.getOrderStatus() + " - WAITING_PAYMENT", Toast.LENGTH_LONG).show();
                    break;
                case PAYMENT_INCOMPLETE:
                    Toast.makeText(c, order.getOrderStatus() + " - PAYMENT_INCOMPLETE", Toast.LENGTH_LONG).show();
                    break;
                case ACTIVE:
                    Toast.makeText(c, order.getOrderStatus() + " - ACTIVE", Toast.LENGTH_LONG).show();
                    break;
                case OVERPAID:
                    Toast.makeText(c, order.getOrderStatus() + " - OVERPAID", Toast.LENGTH_LONG).show();
                    break;
                case WAITING_REVIEW:
                    Toast.makeText(c, order.getOrderStatus() + " - WAITING_REVIEW", Toast.LENGTH_LONG).show();
                    break;
                case REVIEW_COMPLETED:
                    Toast.makeText(c, order.getOrderStatus() + " - REVIEW_COMPLETED", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(c, order.getOrderStatus() + " - UNDEFINED", Toast.LENGTH_LONG).show();
                    break;
            }
            TAPMessageModel orderCard = TAPMessageModel.Builder(
                    TAPUtils.getInstance().toJsonString(order),
                    item.getRoom(),
                    TYPE_ORDER_CARD,
                    System.currentTimeMillis(),
                    item.getUser(),
                    item.getRecipientID());
            adapter.setItemAt(position, orderCard);
            adapter.notifyItemChanged(position);
            //listener.onBubbleExpanded();
        });
    }

    private boolean isCustomer(TAPOrderModel order) {
        return order.getCustomer().getUserID().equals(myUserModel.getUserID());
    }

    private void showProductPreview(TAPOrderModel order) {
        if (!order.getProducts().isEmpty()) {
            // Show product preview
            TAPProductModel product = order.getProducts().get(0);
            Glide.with(itemView.getContext()).load(product.getProductImage().getThumbnail()).into(ivProductThumbnail);
            tvProductName.setText(product.getName());
            tvProductPrice.setText(TAPUtils.getInstance().formatCurrencyRp(product.getPrice()));
            tvProductQty.setText(String.format(Locale.getDefault(), "%s %d",
                    itemView.getContext().getString(io.taptalk.Taptalk.R.string.order_quantity), product.getQuantity()));
            int size = order.getProducts().size();
            if (1 < size) {
                // Show more items layout if there are more than 1 product
                tvButtonMoreItems.setText(String.format(Locale.getDefault(), itemView.getContext().getString(io.taptalk.Taptalk.R.string.order_more_items), size));
                clButtonMoreItems.setVisibility(View.VISIBLE);
            } else {
                clButtonMoreItems.setVisibility(View.GONE);
            }
            clProductPreview.setVisibility(View.VISIBLE);
        } else {
            // Hide product preview
            clProductPreview.setVisibility(View.GONE);
        }
    }

    private void showRecipient(TAPOrderModel order) {
        TAPRecipientModel recipient = order.getRecipient();
        if (null != recipient) {
            // Show recipient
            tvRecipientDetails.setText(new StringBuilder()
                    .append(recipient.getRecipientName()).append("\n")
                    .append(recipient.getPhoneNumber()).append("\n")
                    .append(recipient.getAddress()).append(", ")
                    .append(recipient.getRegion()).append(", ")
                    .append(recipient.getCity()).append(", ")
                    .append(recipient.getProvince()).append(" ")
                    .append(recipient.getPostalCode()));
            clRecipient.setVisibility(View.VISIBLE);
        } else {
            // Hide recipient
            clRecipient.setVisibility(View.GONE);
        }
    }

    private void showNotes(TAPOrderModel order) {
        if (!order.getNotes().isEmpty()) {
            // Show notes
            tvNotes.setText(order.getNotes());
            clNotes.setVisibility(View.VISIBLE);
        } else {
            // Hide notes
            clNotes.setVisibility(View.GONE);
        }
    }

    private void showCourier(TAPOrderModel order) {
        if (null != order.getCourier()) {
            // Show courier
            TAPCourierModel courier = order.getCourier();
            clCourier.setVisibility(View.VISIBLE);
            tvCourierType.setText(courier.getCourierType());
            tvCourierCost.setText(TAPUtils.getInstance().formatCurrencyRp(courier.getCourierCost()));
            Glide.with(itemView.getContext()).load(courier.getCourierLogo().getThumbnail()).into(ivCourierLogo);
        } else {
            // Hide courier
            clCourier.setVisibility(View.GONE);
        }
    }

    private void showAdditionalCost(TAPOrderModel order) {
        if (0 < order.getAdditionalCost()) {
            // Show additional cost
            tvAdditionalCost.setText(TAPUtils.getInstance().formatCurrencyRp(order.getAdditionalCost()));
            clAdditionalCost.setVisibility(View.VISIBLE);
            // TODO: 13 November 2018 CHECK IF ADDITIONAL WAS UPDATED
            vBadgeAdditional.setVisibility(View.VISIBLE);
        } else {
            // Hide additional cost
            clAdditionalCost.setVisibility(View.GONE);
        }
    }

    private void showDiscount(TAPOrderModel order) {
        if (0 < order.getDiscount()) {
            // Show discount
            tvDiscount.setText(String.format("(%s)", TAPUtils.getInstance().formatCurrencyRp(order.getDiscount())));
            clDiscount.setVisibility(View.VISIBLE);
            // TODO: 13 November 2018 CHECK IF DISCOUNT WAS UPDATED
            vBadgeDiscount.setVisibility(View.VISIBLE);
        } else {
            // Hide discount
            clDiscount.setVisibility(View.GONE);
        }
    }

    private void showOrderPrice(TAPOrderModel order, boolean showSeparator) {
        tvTotalPrice.setText(TAPUtils.getInstance().formatCurrencyRp(order.getTotalPrice()));
        clTotalPrice.setVisibility(View.VISIBLE);
        if (showSeparator) {
            vTotalPriceSeparator.setVisibility(View.VISIBLE);
        } else {
            vTotalPriceSeparator.setVisibility(View.GONE);
        }
        showAdditionalCost(order);
        showDiscount(order);
    }

    private void hideOrderPrice() {
        clAdditionalCost.setVisibility(View.GONE);
        clDiscount.setVisibility(View.GONE);
        clTotalPrice.setVisibility(View.GONE);
    }

    private void showOrderStatus(String statusText, int textColor) {
        tvOrderStatus.setText(statusText);
        tvOrderStatus.setTextColor(textColor);
        tvOrderStatus.setVisibility(View.VISIBLE);
    }

    private void showActionButton(String buttonText) {
        tvButtonOrderAction.setText(buttonText);
        tvButtonOrderAction.setVisibility(View.VISIBLE);
    }

    private void viewOrderDetail() {
        // TODO: 13 November 2018 viewOrderDetail
    }

    private void reportOrder() {
        // TODO: 14 November 2018 reportOrder
    }

    private void viewOrderStatus() {
        // TODO: 15 November 2018 viewOrderStatus
    }

    private boolean isMessageFromMySelf(TAPMessageModel messageModel) {
        return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }
}
