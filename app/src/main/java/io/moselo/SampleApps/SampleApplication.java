package io.moselo.SampleApps;

import android.app.Application;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import io.moselo.SampleApps.CustomBubbleClass.OrderCardBubbleClass;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TAPListener;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TaptalkSample.R;

public class SampleApplication extends Application {

    TAPListener TAPListener = new TAPListener() {
        @Override
        public void onRefreshTokenExpiredOrInvalid() {
            TapTalk.refreshTokenExpired();
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
        public void onCustomKeyboardItemClicked(TAPCustomKeyboardItemModel customKeyboardItemModel, TAPUserModel activeUser, TAPUserModel otherUser) {
            if (customKeyboardItemModel.getItemID().equals("1")) {
                Toast.makeText(SampleApplication.this, "See price list clicked", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TapTalk.init(this, TAPListener);
        TapTalk.saveAppInfo(R.mipmap.ic_launcher, getResources().getString(R.string.app_name));
        TapTalk.addCustomBubble(new OrderCardBubbleClass(R.layout.sample_cell_chat_order_card, 3001, () -> Toast.makeText(SampleApplication.this, "OrderDetails Click", Toast.LENGTH_SHORT).show()));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
