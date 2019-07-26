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
import io.taptalk.TapTalk.Listener.TAPChatRoomListener;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPLoginActivity;
import io.taptalk.TaptalkSample.BuildConfig;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    private static final String TAG = "SampleApplication";

    TAPListener TAPListener = new TAPListener() {
        @Override
        public void onRefreshAuthTicket() {
            Intent intent = new Intent(getApplicationContext(), TAPLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }
    };

    TAPChatRoomListener tapChatRoomListener = new TAPChatRoomListener() {
        @Override
        public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPUserModel activeUser, TAPUserModel otherUser) {
//            // DUMMY CUSTOM KEYBOARD ITEMS
//            TAPCustomKeyboardItemModel customKeyboard1 = new TAPCustomKeyboardItemModel("1", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_new_group_orange), "Say Hello to " + otherUser.getName());
//            TAPCustomKeyboardItemModel customKeyboard2 = new TAPCustomKeyboardItemModel("2", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_view_grey), "View Profile");
//            TAPCustomKeyboardItemModel customKeyboard3 = new TAPCustomKeyboardItemModel("3", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_exit_red), "Leave Room");
//            List<TAPCustomKeyboardItemModel> customKeyboards = new ArrayList<>();
//            customKeyboards.add(customKeyboard1);
//            customKeyboards.add(customKeyboard2);
//            customKeyboards.add(customKeyboard3);
//            customKeyboards.add(customKeyboard1);
//            customKeyboards.add(customKeyboard2);
//            customKeyboards.add(customKeyboard3);
//            return customKeyboards;

//            TAPCustomKeyboardItemModel seePriceList = new TAPCustomKeyboardItemModel("1", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_star_yellow), "See price list");
//            TAPCustomKeyboardItemModel readExpertNotes = new TAPCustomKeyboardItemModel("2", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_star_yellow), "Read expert's notes");
//            TAPCustomKeyboardItemModel sendServices = new TAPCustomKeyboardItemModel("3", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_star_yellow), "Send services");
//            TAPCustomKeyboardItemModel createOrderCard = new TAPCustomKeyboardItemModel("4", getDrawable(io.taptalk.Taptalk.R.drawable.tap_ic_star_yellow), "Create order card");
//
//            if (null != activeUser.getUserRole() && activeUser.getUserRole().getCode().equals("1") &&
//                    null != otherUser.getUserRole() && otherUser.getUserRole().getCode().equals("2")) {
//                List<TAPCustomKeyboardItemModel> userToExpert = new ArrayList<>();
//                userToExpert.add(seePriceList);
//                userToExpert.add(readExpertNotes);
//                return userToExpert;
//            } else if (null != activeUser.getUserRole() && activeUser.getUserRole().getCode().equals("2") &&
//                    null != otherUser.getUserRole() && otherUser.getUserRole().getCode().equals("1")) {
//                List<TAPCustomKeyboardItemModel> expertToUser = new ArrayList<>();
//                expertToUser.add(sendServices);
//                expertToUser.add(createOrderCard);
//                return expertToUser;
//            } else if (null != activeUser.getUserRole() && activeUser.getUserRole().getCode().equals("2") &&
//                    null != otherUser.getUserRole() && otherUser.getUserRole().getCode().equals("2")) {
//                List<TAPCustomKeyboardItemModel> expertToExpert = new ArrayList<>();
//                expertToExpert.add(seePriceList);
//                expertToExpert.add(readExpertNotes);
//                expertToExpert.add(sendServices);
//                expertToExpert.add(createOrderCard);
//                return expertToExpert;
//            } else {
//                List<TAPCustomKeyboardItemModel> expertToExpert = new ArrayList<>();
//                expertToExpert.add(seePriceList);
//                expertToExpert.add(readExpertNotes);
//                expertToExpert.add(sendServices);
//                expertToExpert.add(createOrderCard);
//                return expertToExpert;
//            }
            return null;
        }

        @Override
        public void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
            if (customKeyboardItemModel.getItemID().equals("1")) {
                String message = "Hi " + otherUser.getName() + "! How are you?";
                TapTalk.sendTextMessageWithRecipientUser(message, otherUser, new TAPSendMessageWithIDListener() {
                    @Override
                    public void sendSuccess() {
                        Log.e("><><><", "sendSuccess: ");
                    }

                    @Override
                    public void sendFailed(TAPErrorModel errorModel) {
                        Log.e("><><><", "sendFailed: ");
                    }
                });
            } else if (customKeyboardItemModel.getItemID().equals("2")) {
                TapTalk.openTapTalkUserProfile(activity, otherUser);
            } else if (customKeyboardItemModel.getItemID().equals("3")) {
                activity.finish();
            }
//            if (customKeyboardItemModel.getItemID().equals("1")) {
//                String message = "Hi " + otherUser.getName() + ", I want to see services & pricing";
//                TapTalk.sendTextMessageWithRecipientUser(message, otherUser, new TAPSendMessageWithIDListener() {
//                    @Override
//                    public void sendSuccess() {
//                        Log.e("><><><", "sendSuccess: ");
//                    }
//
//                    @Override
//                    public void sendFailed(TAPErrorModel errorModel) {
//                        Log.e("><><><", "sendFailed: ");
//                    }
//                });
//            } else if (customKeyboardItemModel.getItemID().equals("3")) {
//                TAPProductModel prod1 = TAPProductModel.Builder("2eccad", "Button1",
//                        "2eccad", "Button2",
//                        "IDR", "", "4475",
//                        "https://pbs.twimg.com/profile_images/934765447144030211/kaK6qlJc_400x400.jpg",
//                        "A5 - lettering pieces", "75000", "0.0");
//
//                TAPProductModel prod2 = TAPProductModel.Builder("2eccad", "Button1",
//                        "2eccad", "Button2",
//                        "IDR", "", "4458",
//                        "https://pbs.twimg.com/profile_images/934765447144030211/kaK6qlJc_400x400.jpg",
//                        "Custom Mahar/Gift [3D frame]", "400000", "5.0");
//                List<TAPProductModel> productModels = new ArrayList<>();
//                productModels.add(prod1);
//                productModels.add(prod1);
//                productModels.add(prod2);
//                productModels.add(prod2);
//                productModels.add(prod2);
//                productModels.add(prod2);
//                productModels.add(prod2);
//                TapTalk.sendProductMessage(productModels, otherUser);
//            }
        }

        @Override
        public void onTapTalkProductListBubbleLeftButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room) {
            super.onTapTalkProductListBubbleLeftButtonTapped(activity, productModel, recipientXcUserID, room);
        }

        @Override
        public void onTapTalkProductListBubbleRightButtonTapped(Activity activity, TAPProductModel productModel, String recipientXcUserID, TAPRoomModel room) {
            super.onTapTalkProductListBubbleRightButtonTapped(activity, productModel, recipientXcUserID, room);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if ("dev".equals(BuildConfig.BUILD_TYPE)) {
            TapTalk.init(this, "d1e5dfe23d1e00bf54bc2316f",
                    "NTQzMTBjZDI5YWNjNTEuMS4x/ZDY4MTg3Yjg/OTA0MTQwNDFhMDYw/MGI0YjA5NTJjM2Fh",
                    R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name),
                    TAPListener);
            TapTalk.setTapTalkEnvironmentDevelopment();
            TapTalk.addChatRoomListener(tapChatRoomListener);
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        } else if ("staging".equals(BuildConfig.BUILD_TYPE)) {
            TapTalk.init(this, "b43b48745dfa0e44k1",
                    "MzI5XzEuMV/9hcHBfa2V5X2lkX2FuZD/oxNTM2OTk3ODc3MjI0NzI4",
                    R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name),
                    TAPListener);
            TapTalk.setTapTalkEnvironmentStaging();
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        } else {
            TapTalk.init(this, "d1e5dfe23d1e00bf54bc2316f",
                    "NTQzMTBjZDI5YWNjNTEuMS4x/ZDY4MTg3Yjg/OTA0MTQwNDFhMDYw/MGI0YjA5NTJjM2Fh",
                    R.mipmap.ic_launcher, getResources().getString(R.string.tap_app_name),
                    TAPListener);
            TapTalk.setTapTalkEnvironmentProduction();
        }
        TapTalk.setTapTalkScreenOrientation(TapTalk.TapTalkScreenOrientation.TapTalkOrientationPortrait); // FIXME: 23 May 2019 SCREEN ORIENTATION FORCED TO PORTRAIT
        TapTalk.addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
    }
}
