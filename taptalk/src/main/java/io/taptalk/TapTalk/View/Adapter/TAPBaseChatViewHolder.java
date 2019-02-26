package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.util.Linkify;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.regex.Pattern;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPBetterLinkMovementMethod;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone;

public class TAPBaseChatViewHolder extends TAPBaseViewHolder<TAPMessageModel> {

    protected TAPBaseChatViewHolder(ViewGroup parent, int itemLayoutId) {
        super(parent, itemLayoutId);
    }

    @Override
    protected void onBind(TAPMessageModel item, int position) {

    }

    protected void setMessage(TAPMessageModel message) {

    }

    protected void receiveSentEvent(TAPMessageModel message) {

    }

    protected void receiveDeliveredEvent(TAPMessageModel message) {

    }

    protected void receiveReadEvent(TAPMessageModel message) {

    }

    protected void markUnreadForMessage(TAPMessageModel item, TAPUserModel myUserModel) {
        if ((null == item.getIsRead() || !item.getIsRead()) && !myUserModel.getUserID().equals(item.getUser().getUserID())
                && (null != item.getSending() && !item.getSending())) {
            item.updateReadMessage();
            new Thread(() -> {
                TAPMessageStatusManager.getInstance().addUnreadListByOne(item.getRoom().getRoomID());
                TAPMessageStatusManager.getInstance().addReadMessageQueue(item.copyMessageModel());
            }).start();
        }
    }

    protected void enableLongPress(Context context, View view, TAPMessageModel message) {
        view.setOnLongClickListener(v -> {
            Intent intent = new Intent(LongPressChatBubble);
            intent.putExtra(MESSAGE, message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        });
    }

    protected void setLinkDetection(Context context, TextView tvMessageBody) {
        TAPBetterLinkMovementMethod movementMethod = TAPBetterLinkMovementMethod.newInstance()
                .setOnLinkClickListener((textView, url, originalText) -> {
                    if (null != url && url.contains("mailto:")) {
                        //for Email
                        return false;
                    } else if (null != url && url.contains("tel:")) {
                        //For Phone Number
                        return false;
                    } else if (null != url) {
                        //For Url
                        TAPUtils.getInstance().openCustomTabLayout((Activity) itemView.getContext(), url);
                        return true;
                    }
                    return false;
                }).setOnLinkLongClickListener((textView, url, originalText) -> {
                    if (null != url && url.contains("mailto:")) {
                        //for Email
                        Intent intent = new Intent(LongPressEmail);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    } else if (null != url && url.contains("tel:")) {
                        //For Phone Number
                        Intent intent = new Intent(LongPressPhone);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    } else if (null != url) {
                        //For Url
                        Intent intent = new Intent(LongPressLink);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    }
                    return true;
                });

        tvMessageBody.setMovementMethod(movementMethod);
        tvMessageBody.setClickable(false);
        tvMessageBody.setLongClickable(false);

        Linkify.addLinks(tvMessageBody, Linkify.PHONE_NUMBERS | Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        Linkify.TransformFilter filter = (match, url) -> url.replaceAll("/", "");
        Pattern pattern = Pattern.compile("[0-9/]+");
        Linkify.addLinks(tvMessageBody, pattern, "tel:", (s, start, end) -> {
            int digitCount = 0;

            for (int i = start; i < end; i++) {
                if (Character.isDigit(s.charAt(i))) {
                    digitCount++;
                    if (digitCount >= 7) {
                        return true;
                    }
                }
            }
            return false;
        }, filter);
    }
}
