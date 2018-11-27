package io.taptalk.TapTalk.View.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPCourierModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOrderModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRecipientModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_LEFT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_BUBBLE_TEXT_RIGHT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_EMPTY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.BubbleType.TYPE_LOG;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_ORDER_CARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_PRODUCT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
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

public class TAPMessageAdapter extends TAPBaseAdapter<TAPMessageModel, TAPBaseViewHolder<TAPMessageModel>> {

    private static final String TAG = TAPMessageAdapter.class.getSimpleName();
    private TAPChatListener listener;
    private TAPMessageModel expandedBubble;
    private TAPUserModel myUserModel;
    private Drawable bubbleOverlayLeft, bubbleOverlayRight;
    private float initialTranslationX = TAPUtils.getInstance().dpToPx(-16);
    private long defaultAnimationTime = 200L;

    public TAPMessageAdapter(TAPChatListener listener) {
        myUserModel = TAPDataManager.getInstance().getActiveUser();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPMessageModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUBBLE_TEXT_RIGHT:
                return new TextVH(parent, R.layout.tap_cell_chat_text_right, viewType);
            case TYPE_BUBBLE_TEXT_LEFT:
                return new TextVH(parent, R.layout.tap_cell_chat_text_left, viewType);
            case TYPE_BUBBLE_IMAGE_RIGHT:
                return new ImageVH(parent, R.layout.tap_cell_chat_image_right, viewType);
            case TYPE_BUBBLE_IMAGE_LEFT:
                return new ImageVH(parent, R.layout.tap_cell_chat_image_left, viewType);
            case TYPE_BUBBLE_PRODUCT_LIST:
                return new ProductVH(parent, R.layout.tap_cell_chat_product_list);
            case TYPE_BUBBLE_ORDER_CARD:
                return new OrderVH(parent, R.layout.tap_cell_chat_order_card);
            case TYPE_EMPTY:
                return new EmptyVH(parent, R.layout.tap_cell_empty);
            default:
                return new LogVH(parent, R.layout.tap_cell_chat_log);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            TAPMessageModel messageModel = getItemAt(position);
            int messageType = 0;
            if (null != messageModel && null != messageModel.getHidden() && messageModel.getHidden()) {
                // Return empty layout if item is hidden
                return TYPE_EMPTY;
            } else if (null != messageModel) {
                messageType = messageModel.getType();
            }

            switch (messageType) {
                case TYPE_TEXT:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_TEXT_RIGHT;
                    } else {
                        return TYPE_BUBBLE_TEXT_LEFT;
                    }
                case TYPE_IMAGE:
                    if (isMessageFromMySelf(messageModel)) {
                        return TYPE_BUBBLE_IMAGE_RIGHT;
                    } else {
                        return TYPE_BUBBLE_IMAGE_LEFT;
                    }
                case TYPE_PRODUCT:
                    return TYPE_BUBBLE_PRODUCT_LIST;
                case TYPE_ORDER_CARD:
                    return TYPE_BUBBLE_ORDER_CARD;
                default:
                    return TYPE_LOG;
            }
        } catch (Exception e) {
            return TYPE_LOG;
        }
    }

    private boolean isMessageFromMySelf(TAPMessageModel messageModel) {
        return myUserModel.getUserID().equals(messageModel.getUser().getUserID());
    }

    public class TextVH extends TAPBaseViewHolder<TAPMessageModel> {

        private ConstraintLayout clContainer, clReply;
        private FrameLayout flBubble;
        private CircleImageView civAvatar;
        private ImageView ivMessageStatus, ivReply, ivSending;
        private TextView tvUsername, tvMessageBody, tvMessageStatus, tvReplySenderName, tvReplyBody;
        private View vReplyBackground;

        TextVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            clReply = itemView.findViewById(R.id.cl_reply);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            ivReply = itemView.findViewById(R.id.iv_reply);
            tvMessageBody = itemView.findViewById(R.id.tv_message_body);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            tvReplySenderName = itemView.findViewById(R.id.tv_reply_sender);
            tvReplyBody = itemView.findViewById(R.id.tv_reply_body);
            vReplyBackground = itemView.findViewById(R.id.v_reply_background);

            if (bubbleType == TYPE_BUBBLE_TEXT_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
                tvUsername = itemView.findViewById(R.id.tv_user_name);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            tvMessageBody.setText(item.getBody());

            if ((null == item.getIsRead() || !item.getIsRead()) && !isMessageFromMySelf(item)
                    && (null != item.getSending() && !item.getSending())) {
                //Log.e(TAG, "onBind: "+item.getBody() );
                item.updateReadMessage();
                new Thread(() -> TAPMessageStatusManager.getInstance().addReadMessageQueue(item.copyMessageModel())).start();
            }

            // TODO: 1 November 2018 TESTING REPLY LAYOUT
            if (null != item.getReplyTo() && !item.getReplyTo().getBody().isEmpty()) {
                clReply.setVisibility(View.VISIBLE);
                vReplyBackground.setVisibility(View.VISIBLE);
                tvReplySenderName.setText(item.getReplyTo().getUser().getName());
                tvReplyBody.setText(item.getReplyTo().getBody());
            } else {
                clReply.setVisibility(View.GONE);
                vReplyBackground.setVisibility(View.GONE);
            }

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, tvUsername, civAvatar, ivMessageStatus, ivReply, ivSending);
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, false);

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> onBubbleClicked(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply));
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }
    }

    public class ImageVH extends TAPBaseViewHolder<TAPMessageModel> {

        private ConstraintLayout clContainer;
        private FrameLayout flBubble, flProgress;
        private CircleImageView civAvatar;
        private TAPRoundedCornerImageView rcivImageBody;
        private ImageView ivMessageStatus, ivReply, ivSending, ivProgress;
        private TextView tvMessageStatus;
        private ProgressBar pbProgress;

        ImageVH(ViewGroup parent, int itemLayoutId, int bubbleType) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            flBubble = itemView.findViewById(R.id.fl_bubble);
            flProgress = itemView.findViewById(R.id.fl_progress);
            rcivImageBody = itemView.findViewById(R.id.rciv_image);
            ivReply = itemView.findViewById(R.id.iv_reply);
            ivProgress = itemView.findViewById(R.id.iv_progress);
            tvMessageStatus = itemView.findViewById(R.id.tv_message_status);
            pbProgress = itemView.findViewById(R.id.pb_progress);

            if (bubbleType == TYPE_BUBBLE_IMAGE_LEFT) {
                civAvatar = itemView.findViewById(R.id.civ_avatar);
            } else {
                ivMessageStatus = itemView.findViewById(R.id.iv_message_status);
                ivSending = itemView.findViewById(R.id.iv_sending);
            }
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            tvMessageStatus.setText(TAPTimeFormatter.getInstance().durationString(item.getCreated()));

            checkAndUpdateMessageStatus(item, itemView, flBubble, tvMessageStatus, null, civAvatar, ivMessageStatus, ivReply, ivSending);

            if (item.isFirstLoadFinished()) {
                flProgress.setVisibility(View.GONE);
            }

            if (!item.getBody().isEmpty()) {
                rcivImageBody.setImageDimensions(item.getImageWidth(), item.getImageHeight());
                int placeholder = isMessageFromMySelf(item) ? R.drawable.tap_bg_amethyst_mediumpurple_270_rounded_8dp_1dp_8dp_8dp : R.drawable.tap_bg_white_rounded_1dp_8dp_8dp_8dp_stroke_eaeaea_1dp;
                Glide.with(itemView.getContext()).load(item.getBody()).apply(new RequestOptions().placeholder(placeholder)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (item.isFirstLoadFinished()) {
                            // Image has been loaded once
                            return false;
                        }

                        // Image is loading for the first time
                        item.setFirstLoadFinished(true);
                        listener.onLayoutLoaded(item);
                        // TODO: 31 October 2018 TESTING DUMMY IMAGE PROGRESS BAR
                        if (isMessageFromMySelf(item)) {
                            flBubble.setForeground(bubbleOverlayRight);
                        } else {
                            flBubble.setForeground(bubbleOverlayLeft);
                        }
                        flProgress.setVisibility(View.VISIBLE);
                        new CountDownTimer(1000, 10) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                pbProgress.setProgress((int) (1000 - millisUntilFinished) / 10);
                            }

                            @Override
                            public void onFinish() {
                                flProgress.setVisibility(View.GONE);
                                flBubble.setForeground(null);
                            }
                        }.start();
                        return false;
                    }
                }).into(rcivImageBody);
            }

            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            flBubble.setOnClickListener(v -> {
                // TODO: 5 November 2018 VIEW IMAGE
            });
            ivReply.setOnClickListener(v -> onReplyButtonClicked(item));
        }
    }

    public class ProductVH extends TAPBaseViewHolder<TAPMessageModel> {

        RecyclerView rvProductList;
        TAPProductListAdapter adapter;

        ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            if (null == adapter) {
                adapter = new TAPProductListAdapter(item, myUserModel, listener);
            }

            rvProductList.setAdapter(adapter);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            if (rvProductList.getItemDecorationCount() > 0) {
                rvProductList.removeItemDecorationAt(0);
            }
            rvProductList.addItemDecoration(new TAPHorizontalDecoration(
                    0, 0,
                    TAPUtils.getInstance().dpToPx(16),
                    TAPUtils.getInstance().dpToPx(8),
                    adapter.getItemCount(),
                    0, 0));
            OverScrollDecoratorHelper.setUpOverScroll(rvProductList, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        }
    }

    public class OrderVH extends TAPBaseViewHolder<TAPMessageModel> {

        private ConstraintLayout clContainer, clCard, clButtonDetail, clProductPreview, clButtonMoreItems, clDateTime;
        private ConstraintLayout clRecipient, clNotes, clCourier, clAdditionalCost, clDiscount, clTotalPrice;
        private LinearLayout llOrderStatusGuide, llButtonOrderStatus;
        private TextView tvOrderID, tvProductName, tvProductPrice, tvProductQty, tvButtonMoreItems;
        private TextView tvDate, tvTime, tvRecipientDetails, tvCourierType, tvCourierCost, tvNotes;
        private TextView tvAdditionalCost, tvDiscount, tvTotalPrice, tvOrderStatus, tvReportOrder, tvButtonOrderAction;
        private ImageView ivProductThumbnail, ivCourierLogo;
        private View vCardMarginLeft, vCardMarginRight, vBadgeAdditional, vBadgeDiscount, vTotalPriceSeparator;

        OrderVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            clCard = itemView.findViewById(R.id.cl_card);
            clButtonDetail = itemView.findViewById(R.id.cl_button_detail);
            clProductPreview = itemView.findViewById(R.id.cl_product_preview);
            clButtonMoreItems = itemView.findViewById(R.id.cl_button_more_items);
            clDateTime = itemView.findViewById(R.id.cl_date_time);
            clRecipient = itemView.findViewById(R.id.cl_recipient);
            clNotes = itemView.findViewById(R.id.cl_notes);
            clCourier = itemView.findViewById(R.id.cl_courier);
            clAdditionalCost = itemView.findViewById(R.id.cl_additional_cost);
            clDiscount = itemView.findViewById(R.id.cl_discount);
            clTotalPrice = itemView.findViewById(R.id.cl_total_price);
            llOrderStatusGuide = itemView.findViewById(R.id.ll_order_status_guide);
            llButtonOrderStatus = itemView.findViewById(R.id.ll_button_order_status);
            tvOrderID = itemView.findViewById(R.id.tv_order_id);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQty = itemView.findViewById(R.id.tv_product_qty);
            tvButtonMoreItems = itemView.findViewById(R.id.tv_button_more_items);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRecipientDetails = itemView.findViewById(R.id.tv_recipient_details);
            tvCourierType = itemView.findViewById(R.id.tv_courier_type);
            tvCourierCost = itemView.findViewById(R.id.tv_courier_cost);
            tvNotes = itemView.findViewById(R.id.tv_notes);
            tvAdditionalCost = itemView.findViewById(R.id.tv_additional_cost);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvReportOrder = itemView.findViewById(R.id.tv_report_order);
            tvButtonOrderAction = itemView.findViewById(R.id.tv_button_order_action);
            ivProductThumbnail = itemView.findViewById(R.id.iv_product_thumbnail);
            ivCourierLogo = itemView.findViewById(R.id.iv_courier_logo);
            vCardMarginLeft = itemView.findViewById(R.id.v_card_margin_left);
            vCardMarginRight = itemView.findViewById(R.id.v_card_margin_right);
            vBadgeAdditional = itemView.findViewById(R.id.v_badge_additional_updated);
            vBadgeDiscount = itemView.findViewById(R.id.v_badge_discount_updated);
            vTotalPriceSeparator = itemView.findViewById(R.id.v_total_price_separator);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
//            if (null != item.getHidden() && item.getHidden()) {
//                Log.e(TAG, "onBind item hidden :" + item.getBody());
//                clContainer.setVisibility(View.GONE);
//                clContainer.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
//                return;
//            }
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
                clButtonDetail.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_purple_header_right_ripple));
            } else {
                // Show bubble on left side
                vCardMarginLeft.setVisibility(View.GONE);
                vCardMarginRight.setVisibility(View.VISIBLE);
                clButtonDetail.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_purple_header_left_ripple));
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
                        showActionButton(c.getString(R.string.review_and_confirm));
                        tvOrderStatus.setVisibility(View.GONE);
                    } else {
                        showOrderPrice(order, true);
                        showOrderStatus(c.getString(R.string.waiting_user_confirmation), c.getResources().getColor(R.color.orangeish));
                        tvButtonOrderAction.setVisibility(View.GONE);
                    }
                    break;
                case CANCELLED_BY_CUSTOMER:
                    // Order is canceled
                    showOrderStatus(c.getString(R.string.order_canceled), c.getResources().getColor(R.color.tomato));
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
                        showOrderStatus(c.getString(R.string.waiting_confirmation), c.getResources().getColor(R.color.orangeish));
                        tvButtonOrderAction.setVisibility(View.GONE);
                    } else {
                        showOrderPrice(order, false);
                        showActionButton(c.getString(R.string.review_and_confirm));
                        tvOrderStatus.setVisibility(View.GONE);
                    }
                    break;
                case DECLINED_BY_SELLER:
                    // Order is canceled
                    showOrderStatus(c.getString(R.string.order_declined), c.getResources().getColor(R.color.tomato));
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
                        showActionButton(c.getString(R.string.pay_now));
                        tvOrderStatus.setVisibility(View.GONE);
                    } else {
                        showOrderPrice(order, true);
                        showOrderStatus(c.getString(R.string.waiting_payment), c.getResources().getColor(R.color.orangeish));
                        tvButtonOrderAction.setVisibility(View.GONE);
                    }
                    break;
                case DISAGREED_BY_CUSTOMER:
                    // Order is canceled
                    showOrderStatus(c.getString(R.string.user_disagreed), c.getResources().getColor(R.color.tomato));
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
                        showActionButton(c.getString(R.string.pay_now));
                        tvOrderStatus.setVisibility(View.GONE);
                    } else {
                        showOrderPrice(order, true);
                        showOrderStatus(c.getString(R.string.payment_incomplete), c.getResources().getColor(R.color.orangeish));
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
                        showOrderStatus(c.getString(R.string.payment_confirmed), c.getResources().getColor(R.color.tealish));
                        tvButtonOrderAction.setVisibility(View.GONE);
                    } else {
                        showOrderStatus(c.getString(R.string.active_order), c.getResources().getColor(R.color.tealish));
                        showActionButton(c.getString(R.string.mark_as_finished));
                    }
                    break;
                case OVERPAID:
                    // Order is canceled
                    showOrderStatus(c.getString(R.string.order_overpaid), c.getResources().getColor(R.color.tomato));
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
                        showActionButton(c.getString(R.string.write_review));
                        llOrderStatusGuide.setVisibility(View.VISIBLE);
                        tvOrderStatus.setVisibility(View.GONE);
                    } else {
                        showOrderStatus(c.getString(R.string.order_completed), c.getResources().getColor(R.color.tealish));
                        llOrderStatusGuide.setVisibility(View.GONE);
                    }
                    break;
                case REVIEW_COMPLETED:
                    // Order is completed for both sides
                    showOrderStatus(c.getString(R.string.order_completed), c.getResources().getColor(R.color.tealish));
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
            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
            clCard.setOnClickListener(v -> viewOrderDetail());
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
                setItemAt(position, orderCard);
                notifyItemChanged(position);
                listener.onBubbleExpanded();
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
                        itemView.getContext().getString(R.string.order_quantity), product.getQuantity()));
                int size = order.getProducts().size();
                if (1 < size) {
                    // Show more items layout if there are more than 1 product
                    tvButtonMoreItems.setText(String.format(Locale.getDefault(), itemView.getContext().getString(R.string.order_more_items), size));
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
    }

    public class LogVH extends TAPBaseViewHolder<TAPMessageModel> {

        private ConstraintLayout clContainer;
        private TextView tvLogMessage;

        LogVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            clContainer = itemView.findViewById(R.id.cl_container);
            tvLogMessage = itemView.findViewById(R.id.tv_message);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {
            tvLogMessage.setText(item.getBody());
            clContainer.setOnClickListener(v -> listener.onOutsideClicked());
        }
    }

    public class EmptyVH extends TAPBaseViewHolder<TAPMessageModel> {

        EmptyVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(TAPMessageModel item, int position) {

        }
    }

    private void checkAndUpdateMessageStatus(TAPMessageModel item, View itemView, FrameLayout flBubble,
                                             TextView tvMessageStatus, @Nullable TextView tvUsername,
                                             @Nullable CircleImageView civAvatar, @Nullable ImageView ivMessageStatus,
                                             @Nullable ImageView ivReply, @Nullable ImageView ivSending) {
        if (isMessageFromMySelf(item) && null != ivMessageStatus && null != ivSending) {
            // Set timestamp text on non-text or expanded bubble
            if (item.getType() != TYPE_TEXT || item.isExpanded()) {
                tvMessageStatus.setText(String.format("%s %s", itemView.getContext().getString(R.string.sent_at), TAPTimeFormatter.getInstance().formatDate(item.getCreated())));
            }
            // Message has been read
            if (null != item.getIsRead() && item.getIsRead()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_message_read_green);
                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                ivSending.setAlpha(0f);
                // Show status text and reply button for non-text bubbles
                if (item.getType() == TYPE_TEXT) {
                    tvMessageStatus.setVisibility(View.GONE);
                } else if (null != ivReply) {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivReply.setVisibility(View.VISIBLE);
                }
            }
            // Message is delivered
            else if (null != item.getDelivered() && item.getDelivered()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_message_delivered_grey);
                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(0f);
                // Show status text and reply button for non-text bubbles
                if (item.getType() == TYPE_TEXT) {
                    tvMessageStatus.setVisibility(View.GONE);
                } else if (null != ivReply) {
                    tvMessageStatus.setVisibility(View.VISIBLE);
                    ivReply.setVisibility(View.VISIBLE);
                }
            }
            // Message failed to send
            else if (null != item.getFailedSend() && item.getFailedSend()) {
                tvMessageStatus.setText(itemView.getContext().getString(R.string.message_send_failed));
                ivMessageStatus.setImageResource(R.drawable.tap_ic_retry_circle_purple);

                flBubble.setTranslationX(0);
                ivMessageStatus.setVisibility(View.VISIBLE);
                tvMessageStatus.setVisibility(View.VISIBLE);
                ivSending.setAlpha(0f);
                if (null != ivReply) {
                    ivReply.setVisibility(View.GONE);
                }
            }
            // Message sent
            else if (null != item.getSending() && !item.getSending()) {
                ivMessageStatus.setImageResource(R.drawable.tap_ic_message_sent_grey);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.VISIBLE);
                animateSend(item, flBubble, ivSending, ivMessageStatus, ivReply);
            }
            // Message is sending
            else if (null != item.getSending() && item.getSending()) {
                item.setNeedAnimateSend(true);
                tvMessageStatus.setText(itemView.getContext().getString(R.string.sending));

                flBubble.setTranslationX(initialTranslationX);
                ivSending.setTranslationX(0);
                ivSending.setTranslationY(0);
                tvMessageStatus.setVisibility(View.GONE);
                ivMessageStatus.setVisibility(View.GONE);
                ivSending.setAlpha(1f);
                if (null != ivReply) {
                    ivReply.setVisibility(View.GONE);
                }
            }
            ivMessageStatus.setOnClickListener(v -> onStatusImageClicked(item));
        } else {
            // Message from others
            // TODO: 26 September 2018 LOAD USER NAME AND AVATAR IF ROOM TYPE IS GROUP
            if (null != civAvatar && null != item.getUser().getAvatarURL()) {
                Glide.with(itemView.getContext()).load(item.getUser().getAvatarURL().getThumbnail()).into(civAvatar);
                //civAvatar.setVisibility(View.VISIBLE);
            }
            if (null != tvUsername) {
                tvUsername.setText(item.getUser().getUsername());
                //tvUsername.setVisibility(View.VISIBLE);
            }
            listener.onMessageRead(item);
        }
    }

    private void expandOrShrinkBubble(TAPMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, @Nullable ImageView ivMessageStatus, ImageView ivReply, boolean animate) {
        if (item.isExpanded()) {
            // Expand bubble
            expandedBubble = item;
            animateFadeInToBottom(tvMessageStatus);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                // Right Bubble
                if (animate) {
                    // Animate expand
                    animateFadeOutToBottom(ivMessageStatus);
                    animateShowToLeft(ivReply);
                } else {
                    ivMessageStatus.setVisibility(View.GONE);
                    ivReply.setVisibility(View.VISIBLE);
                }
                if (null == bubbleOverlayRight) {
                    bubbleOverlayRight = itemView.getContext().getDrawable(R.drawable.tap_bg_transparent_black_8dp_1dp_8dp_8dp);
                }
                flBubble.setForeground(bubbleOverlayRight);
            } else {
                // Left Bubble
                if (animate) {
                    // Animate expand
                    animateShowToRight(ivReply);
                } else {
                    ivReply.setVisibility(View.VISIBLE);
                }
                if (null == bubbleOverlayRight) {
                    bubbleOverlayLeft = itemView.getContext().getDrawable(R.drawable.tap_bg_transparent_black_1dp_8dp_8dp_8dp);
                }
                flBubble.setForeground(bubbleOverlayLeft);
            }
        } else {
            // Shrink bubble
            flBubble.setForeground(null);
            if (isMessageFromMySelf(item) && null != ivMessageStatus) {
                // Right bubble
                if ((null != item.getFailedSend() && item.getFailedSend())) {
                    // Message failed to send
                    ivReply.setVisibility(View.GONE);
                    ivMessageStatus.setVisibility(View.VISIBLE);
                    ivMessageStatus.setImageResource(R.drawable.tap_ic_retry_circle_purple);
                    tvMessageStatus.setVisibility(View.VISIBLE);
                } else if (null != item.getSending() && !item.getSending()) {
                    if (null != item.getIsRead() && item.getIsRead()) {
                        // Message has been read
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_read_green);
                    } else if (null != item.getDelivered() && item.getDelivered()) {
                        // Message is delivered
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_delivered_grey);
                    } else if (null != item.getSending() && !item.getSending()) {
                        // Message sent
                        ivMessageStatus.setImageResource(R.drawable.tap_ic_message_sent_grey);
                    }
                    if (animate) {
                        // Animate shrink
                        animateHideToRight(ivReply);
                        animateFadeInToTop(ivMessageStatus);
                        animateFadeOutToTop(tvMessageStatus);
                    } else {
                        ivReply.setVisibility(View.GONE);
                        ivMessageStatus.setVisibility(View.VISIBLE);
                        tvMessageStatus.setVisibility(View.GONE);
                    }
                } else if (null != item.getSending() && item.getSending()) {
                    // Message is sending
                    ivReply.setVisibility(View.GONE);
                }
            }
            // Message from others
            else if (animate) {
                // Animate shrink
                animateHideToLeft(ivReply);
                animateFadeOutToTop(tvMessageStatus);
            } else {
                ivReply.setVisibility(View.GONE);
                tvMessageStatus.setVisibility(View.GONE);
            }
        }
    }

    private void onBubbleClicked(TAPMessageModel item, View itemView, FrameLayout flBubble, TextView tvMessageStatus, ImageView ivMessageStatus, ImageView ivReply) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        } else if ((null != item.getSending() && !item.getSending()) ||
                (null != item.getDelivered() && item.getDelivered()) ||
                (null != item.getIsRead() && item.getIsRead())) {
            if (item.isExpanded()) {
                // Shrink bubble
                item.setExpanded(false);
            } else {
                // Expand clicked bubble
                tvMessageStatus.setText(TAPTimeFormatter.getInstance().durationChatString(itemView.getContext(), item.getCreated()));
                shrinkExpandedBubble();
                item.setExpanded(true);
            }
            expandOrShrinkBubble(item, itemView, flBubble, tvMessageStatus, ivMessageStatus, ivReply, true);
        }
    }

    private void onStatusImageClicked(TAPMessageModel item) {
        if (null != item.getFailedSend() && item.getFailedSend()) {
            resendMessage(item);
        }
    }

    private void onReplyButtonClicked(TAPMessageModel item) {
        listener.onReplyMessage(item);
    }

    private void resendMessage(TAPMessageModel item) {
        removeMessage(item);
        listener.onRetrySendMessage(item);
    }

    private void animateSend(TAPMessageModel item, FrameLayout flBubble, ImageView ivSending,
                             ImageView ivMessageStatus, @Nullable ImageView ivReply) {
        if (!item.isNeedAnimateSend()) {
            // Set bubble state to post-animation
            flBubble.setTranslationX(0);
            ivMessageStatus.setTranslationX(0);
            ivSending.setAlpha(0f);
        } else {
            // Animate bubble
            item.setNeedAnimateSend(false);
            //ivMessageStatus.setTranslationX(initialTranslationX);
            flBubble.setTranslationX(initialTranslationX);
            ivSending.setTranslationX(0);
            ivSending.setTranslationY(0);
            new Handler().postDelayed(() -> {
//                ivMessageStatus.animate()
//                        .translationX(0)
//                        .setDuration(160L)
//                        .start();
                flBubble.animate()
                        .translationX(0)
                        .setDuration(160L)
                        .start();
                ivSending.animate()
                        .translationX(TAPUtils.getInstance().dpToPx(36))
                        .translationY(TAPUtils.getInstance().dpToPx(-23))
                        .setDuration(360L)
                        .setInterpolator(new AccelerateInterpolator(0.5f))
                        .withEndAction(() -> ivSending.setAlpha(0f))
                        .start();
            }, 200L);

            // Animate reply button
            if (null != ivReply) {
                animateShowToLeft(ivReply);
            }
        }
    }

    private void animateFadeInToTop(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(TAPUtils.getInstance().dpToPx(24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateFadeInToBottom(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(TAPUtils.getInstance().dpToPx(-24));
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        new Handler().postDelayed(() -> listener.onBubbleExpanded(), 50L);
    }

    private void animateFadeOutToTop(View view) {
        view.animate()
                .translationY(TAPUtils.getInstance().dpToPx(-24))
                .alpha(0)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationY(0);
                })
                .start();
    }

    private void animateFadeOutToBottom(View view) {
        view.animate()
                .translationY(TAPUtils.getInstance().dpToPx(24))
                .alpha(0)
                .setDuration(defaultAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationY(0);
                })
                .start();
    }

    private void animateShowToLeft(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(TAPUtils.getInstance().dpToPx(32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .start();
    }

    private void animateShowToRight(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(TAPUtils.getInstance().dpToPx(-32));
        view.setAlpha(0f);
        view.animate()
                .translationX(0)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .start();
    }

    private void animateHideToLeft(View view) {
        view.animate()
                .translationX(TAPUtils.getInstance().dpToPx(-32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationX(0);
                })
                .start();
    }

    private void animateHideToRight(View view) {
        view.animate()
                .translationX(TAPUtils.getInstance().dpToPx(32))
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(defaultAnimationTime)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1);
                    view.setTranslationX(0);
                })
                .start();
    }

    public void setMessages(List<TAPMessageModel> messages) {
        setItems(messages, false);
    }

    public void addMessage(TAPMessageModel message) {
        addItem(0, message);
    }

    public void addMessage(TAPMessageModel message, int position, boolean isNotify) {
        getItems().add(position, message);
        if (isNotify) notifyItemInserted(position);
    }

    public void addMessage(List<TAPMessageModel> messages) {
        addItem(messages, true);
    }

    public void addMessage(int position, List<TAPMessageModel> messages) {
        addItem(position, messages, true);
    }

    public void setMessageAt(int position, TAPMessageModel message) {
        setItemAt(position, message);
        notifyItemChanged(position);
    }

    public void removeMessageAt(int position) {
        removeItemAt(position);
    }

    public void removeMessage(TAPMessageModel message) {
        removeItem(message);
    }

    public void shrinkExpandedBubble() {
        if (null == expandedBubble) return;
        expandedBubble.setExpanded(false);
        notifyItemChanged(getItems().indexOf(expandedBubble));
    }
}
