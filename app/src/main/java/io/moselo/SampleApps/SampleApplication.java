package io.moselo.SampleApps;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TAPSendMessageWithIDListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    TAPListener TAPListener = new TAPListener() {
        @Override
        public void onRefreshTokenExpiredOrInvalid() {
            Intent intent = new Intent(getApplicationContext(), TAPLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        public List<TAPCustomKeyboardItemModel> onRequestCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
            // DUMMY CUSTOM KEYBOARD ITEMS
            TAPCustomKeyboardItemModel seePriceList = new TAPCustomKeyboardItemModel("1", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_star_yellow), "See price list");
            TAPCustomKeyboardItemModel readExpertNotes = new TAPCustomKeyboardItemModel("2", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_search_grey), "Read expert's notes");
            TAPCustomKeyboardItemModel sendServices = new TAPCustomKeyboardItemModel("3", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_gallery_green_blue), "Send services");
            TAPCustomKeyboardItemModel createOrderCard = new TAPCustomKeyboardItemModel("4", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_documents_green_blue), "Create order card");

            if (null != activeUser.getUserRole() && activeUser.getUserRole().getUserRoleID().equals("1") &&
                    null != otherUser.getUserRole() && otherUser.getUserRole().getUserRoleID().equals("2")) {
                List<TAPCustomKeyboardItemModel> userToExpert = new ArrayList<>();
                userToExpert.add(seePriceList);
                userToExpert.add(readExpertNotes);
                return userToExpert;
            } else if (null != activeUser.getUserRole() && activeUser.getUserRole().getUserRoleID().equals("2") &&
                    null != otherUser.getUserRole() && otherUser.getUserRole().getUserRoleID().equals("1")) {
                List<TAPCustomKeyboardItemModel> expertToUser = new ArrayList<>();
                expertToUser.add(sendServices);
                expertToUser.add(createOrderCard);
                return expertToUser;
            } else if (null != activeUser.getUserRole() && activeUser.getUserRole().getUserRoleID().equals("2") &&
                    null != otherUser.getUserRole() && otherUser.getUserRole().getUserRoleID().equals("2")) {
                List<TAPCustomKeyboardItemModel> expertToExpert = new ArrayList<>();
                expertToExpert.add(seePriceList);
                expertToExpert.add(readExpertNotes);
                expertToExpert.add(sendServices);
                expertToExpert.add(createOrderCard);
                return expertToExpert;
            } else if (null != activeUser.getUserRole() && activeUser.getUserRole().getUserRoleID().equals("0") &&
                    null != otherUser.getUserRole() && otherUser.getUserRole().getUserRoleID().equals("0")) {
                List<TAPCustomKeyboardItemModel> expertToExpert = new ArrayList<>();
                expertToExpert.add(seePriceList);
                expertToExpert.add(readExpertNotes);
                expertToExpert.add(sendServices);
                expertToExpert.add(createOrderCard);
                expertToExpert.add(seePriceList);
                expertToExpert.add(readExpertNotes);
                expertToExpert.add(sendServices);
                expertToExpert.add(createOrderCard);
                return expertToExpert;
            }
            return null;
        }

        @Override
        public void onCustomKeyboardItemClicked(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
            if (customKeyboardItemModel.getItemID().equals("1")) {
                String message = "Hi "+ otherUser.getName() + ", I want to see services & pricing";
                TapTalk.sendTextMessageWithRecipientUser(message, otherUser, new TAPSendMessageWithIDListener() {
                    @Override
                    public void sendSuccess() {
                        Log.e("><><><", "sendSuccess: " );
                    }

                    @Override
                    public void sendFailed(TAPErrorModel errorModel) {
                        Log.e("><><><", "sendFailed: ");
                    }
                });
            }
        }

        @Override
        public void onUserProfileClicked(Activity activity, TAPUserModel userModel) {
            TapTalk.openTapTalkUserProfile(activity, userModel);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, "b43b48745dfa0e44k1",
                "MzI5XzEuMV/9hcHBfa2V5X2lkX2FuZD/oxNTM2OTk3ODc3MjI0NzI4",
                "android",
                TAPListener);
        TapTalk.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        TapTalk.addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
        TapTalk.setOpenTapTalkUserProfileByDefaultEnabled(false);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
