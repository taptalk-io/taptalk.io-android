package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkInterface;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public abstract class TAPListener implements TapTalkInterface {
    @Override
    public void onRefreshTokenExpiredOrInvalid() {
    }

    @Override
    public void onUserProfileClicked(Activity activity, TAPUserModel userModel) {
        TapTalk.openTapTalkUserProfile(activity, userModel);
    }

    @Override
    public void onCustomKeyboardItemClicked(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
    }

    @Override
    public List<TAPCustomKeyboardItemModel> onRequestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
        return null;
    }

    @Override
    public void onProductLeftButtonClicked(TAPProductModel productModel, String recipientXcUserID, String roomID) {

    }

    @Override
    public void onProductRightButtonClicked(TAPProductModel productModel, String recipientXcUserID, String roomID) {

    }
}
