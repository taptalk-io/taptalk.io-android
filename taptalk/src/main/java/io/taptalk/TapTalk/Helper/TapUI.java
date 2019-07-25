package io.taptalk.TapTalk.Helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.HashMap;

import io.taptalk.TapTalk.Interface.TapTalkOpenChatRoomInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPCustomBubbleManager;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPCreateNewGroupActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Activity.TAPRoomListActivity;
import io.taptalk.TapTalk.View.Fragment.TAPMainRoomListFragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TapUI {

    public static TAPMainRoomListFragment getRoomListFragment() {
        return TAPMainRoomListFragment.newInstance();
    }

    public static void openRoomList(Context context) {
        Intent intent = new Intent(context, TAPRoomListActivity.class);
        context.startActivity(intent);
    }

    public static void openGroupChatCreator(Context context) {
        Intent intent = new Intent(context, TAPCreateNewGroupActivity.class);
        context.startActivity(intent);
    }

    public static void openNewChat(Context context) {
        Intent intent = new Intent(context, TAPNewChatActivity.class);
        context.startActivity(intent);
    }

    public static void openBarcodeScanner(Context context) {
        Intent intent = new Intent(context, TAPBarcodeScannerActivity.class);
        context.startActivity(intent);
    }

    public static void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel) {
        TAPUtils.getInstance().startChatActivity(context, roomModel, null, null);
    }

    public static void openChatRoomWithRoomModel(Context context, TAPRoomModel roomModel, String scrollToMessageWithLocalID) {
        TAPUtils.getInstance().startChatActivity(context, roomModel, null, scrollToMessageWithLocalID);
    }

    public static void openChatRoom(Context context, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        TAPUtils.getInstance().startChatActivity(
                context,
                roomID,
                roomName,
                roomImage,
                roomType,
                roomColor);
    }

    public static void openChatRoomWithXCUserID(
            Context context,
            String xcUserID,
            @Nullable String prefilledText,
            @Nullable String quoteTitle,
            @Nullable String quoteContent,
            @Nullable String quoteImageURL,
            @Nullable HashMap<String, Object> userInfo,
            TapTalkOpenChatRoomInterface listener) {
        TAPUtils.getInstance().getUserFromXcUserID(xcUserID, new TAPDatabaseListener<TAPUserModel>() {
            @Override
            public void onSelectFinished(TAPUserModel user) {
                String roomID = TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        user.getUserID());
                if (null != quoteTitle) {
                    TAPChatManager.getInstance().setQuotedMessage(roomID, quoteTitle, quoteContent, quoteImageURL);
                    if (null != userInfo) {
                        TAPChatManager.getInstance().saveUserInfo(roomID, userInfo);
                    }
                }
                if (null != prefilledText) {
                    TAPChatManager.getInstance().saveMessageToDraft(roomID, prefilledText);
                }
                TapUI.openChatRoom(
                        context,
                        roomID,
                        user.getName(),
                        user.getAvatarURL(),
                        TYPE_PERSONAL,
                        "");
                listener.onOpenRoomSuccess();
            }

            @Override
            public void onSelectFailed(String errorMessage) {
                listener.onOpenRoomFailed(errorMessage);
            }
        });
    }

    public static void openChatRoomWithOtherUser(Context context, TAPUserModel otherUser) {
        TapUI.openChatRoom(
                context,
                TAPChatManager.getInstance().arrangeRoomId(TAPChatManager.getInstance().getActiveUser().getUserID(), otherUser.getUserID()),
                otherUser.getName(),
                otherUser.getAvatarURL(),
                TYPE_PERSONAL,
                "");
    }

    public static void openChatRoomWithRoomModel(
            Context context,
            TAPRoomModel roomModel,
            @Nullable String prefilledText,
            @Nullable String quoteTitle,
            @Nullable String quoteContent,
            @Nullable String quoteImageURL,
            @Nullable HashMap<String, Object> userInfo) {
        if (null != quoteTitle) {
            TAPChatManager.getInstance().setQuotedMessage(roomModel.getRoomID(), quoteTitle, quoteContent, quoteImageURL);
            if (null != userInfo) {
                TAPChatManager.getInstance().saveUserInfo(roomModel.getRoomID(), userInfo);
            }
        }
        openChatRoomWithRoomModel(context, roomModel);
    }

    public static void addCustomBubble(TAPBaseCustomBubble baseCustomBubble) {
        TAPCustomBubbleManager.getInstance().addCustomBubbleMap(baseCustomBubble);
    }
}
