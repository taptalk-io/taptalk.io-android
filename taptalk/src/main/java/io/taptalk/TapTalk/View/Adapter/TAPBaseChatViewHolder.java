package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import io.taptalk.TapTalk.Helper.CustomTabLayout.TAPCustomTabActivityHelper;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPBetterLinkMovementMethod;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPWebBrowserActivity;
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet;

import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.LINK_TYPE;

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

    protected void setLinkDetection(Context context, TextView tvMessageBody) {
        TAPBetterLinkMovementMethod movementMethod = TAPBetterLinkMovementMethod.newInstance()
                .setOnLinkClickListener((textView, url) -> {
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
                }).setOnLinkLongClickListener((textView, url) -> {

                    if (null != url && url.contains("mailto:")) {
                        //for Email
                        Toast.makeText(context, "Email Long Click", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (null != url && url.contains("tel:")) {
                        //For Phone Number
                        Toast.makeText(context, "Phone Long Click", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (null != url) {
                        //For Url
                        TAPLongPressActionBottomSheet bottomSheet = new TAPLongPressActionBottomSheet(LINK_TYPE);
                        bottomSheet.show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), "");
                        Toast.makeText(context, "Link Url Long Click", Toast.LENGTH_SHORT).show();
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
