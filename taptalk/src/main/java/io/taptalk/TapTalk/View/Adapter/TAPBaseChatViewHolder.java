package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.regex.Pattern;

import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPBetterLinkMovementMethod;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressMention;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone;

public class TAPBaseChatViewHolder extends TAPBaseViewHolder<TAPMessageModel> {

    protected TAPBaseChatViewHolder(ViewGroup parent, int itemLayoutId) {
        super(parent, itemLayoutId);
    }

    @Override
    protected void onBind(TAPMessageModel item, int position) {
        // TODO MARK MESSAGE AS READ AUTOMATICALLY ON BIND
        //if (itemView.getContext() instanceof TAPBaseActivity) {
        //    String instanceKey = ((TAPBaseActivity) itemView.getContext()).instanceKey;
        //    markMessageAsRead(item, TAPChatManager.getInstance(instanceKey).getActiveUser());
        //}
    }

    protected void onMessageSending(TAPMessageModel message) {

    }

    protected void onMessageFailedToSend(TAPMessageModel message) {

    }

    protected void onMessageSent(TAPMessageModel message) {

    }

    protected void onMessageDelivered(TAPMessageModel message) {

    }

    protected void onMessageRead(TAPMessageModel message) {

    }

    protected void markMessageAsRead(TAPMessageModel item, TAPUserModel myUserModel) {
        String instanceKey = "";
        if (itemView.getContext() instanceof TAPBaseActivity) {
            instanceKey = ((TAPBaseActivity) itemView.getContext()).instanceKey;
        }
        if (!myUserModel.getUserID().equals(item.getUser().getUserID()) &&
                (null == item.getIsRead() || !item.getIsRead()) &&
                (null != item.getIsSending() && !item.getIsSending()) &&
                !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(item.getMessageID()) &&
                !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(item.getMessageID())
        ) {
            item.updateReadMessage();
            String finalInstanceKey = instanceKey;
            new Thread(() -> {
                TAPMessageStatusManager.getInstance(finalInstanceKey).addUnreadListByOne(item.getRoom().getRoomID());
                if (TAPUtils.isActiveUserMentioned(item, myUserModel)) {
                    TAPMessageStatusManager.getInstance(finalInstanceKey).addUnreadMentionByOne(item.getRoom().getRoomID());
                }
                TAPMessageStatusManager.getInstance(finalInstanceKey).addReadMessageQueue(item.getMessageID());
            }).start();
        }
    }

    protected void enableLongPress(Context context, View view, TAPMessageModel message) {
        if (null != view) {
            view.setOnLongClickListener(v -> {
                Intent intent = new Intent(LongPressChatBubble);
                intent.putExtra(MESSAGE, message);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (BuildConfig.DEBUG || TapTalk.isLoggingEnabled) {
                    Log.d(this.getClass().getSimpleName(), "Message model: " + TAPUtils.toJsonString(message));
                }
                return true;
            });
        }
    }

    protected void setLinkDetection(Context context, TAPMessageModel message, TextView tvMessageBody) {
        TAPBetterLinkMovementMethod movementMethod = TAPBetterLinkMovementMethod.newInstance()
                .setOnLinkClickListener((textView, url, originalText) -> {
                    if (null != url && url.contains("mailto:")) {
                        // Email
                        return false;
                    } else if (null != url && url.contains("tel:")) {
                        // Phone Number
                        return false;
                    } else if (null != url && url.contains("@")) {
                        // Mention
                        return false;
                    } else if (null != url) {
                        // Url
                        if (itemView.getContext() != null) {
                            if (itemView.getContext() instanceof TAPBaseActivity) {
                                TAPBaseActivity activity = (TAPBaseActivity) itemView.getContext();
                                String instanceKey = activity.instanceKey;
                                TAPUtils.openUrl(instanceKey, activity, url);
                            }
                            else if (itemView.getContext() instanceof Activity) {
                                TAPUtils.openUrl((Activity) itemView.getContext(), url);
                            }
                        }
                        return true;
                    }
                    return false;
                }).setOnLinkLongClickListener((textView, url, originalText) -> {
                    if (null != url && url.contains("mailto:")) {
                        // Email
                        Intent intent = new Intent(LongPressEmail);
                        intent.putExtra(MESSAGE, message);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    } else if (null != url && url.contains("tel:")) {
                        // Phone Number
                        Intent intent = new Intent(LongPressPhone);
                        intent.putExtra(MESSAGE, message);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    } else if (null != url && url.contains("@")) {
                        // Mention
                        Intent intent = new Intent(LongPressMention);
                        intent.putExtra(MESSAGE, message);
                        intent.putExtra(URL_MESSAGE, url);
                        intent.putExtra(COPY_MESSAGE, originalText);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        return true;
                    } else if (null != url) {
                        // Url
                        Intent intent = new Intent(LongPressLink);
                        intent.putExtra(MESSAGE, message);
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
