package io.taptalk.TapTalk.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPSearchChatAdapter;
import io.taptalk.TapTalk.ViewModel.TAPSearchChatViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.FORWARD_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SHORT_ANIMATION_TIME;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.EMPTY_STATE;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.ROOM_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.SECTION_TITLE;

public class TAPForwardPickerActivity extends TAPBaseActivity {

    private ConstraintLayout clActionBar;
    private ImageView ivButtonClose, ivButtonSearch, ivButtonClearText;
    private TextView tvTitle;
    private EditText etSearch;
    private RecyclerView rvForwardList;

    private TAPSearchChatViewModel vm;
    private TAPSearchChatAdapter adapter;

    public static void start(
            Activity context,
            String instanceKey,
            ArrayList<TAPMessageModel> message
    ) {
        Intent intent = new Intent(context, TAPForwardPickerActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putParcelableArrayListExtra(MESSAGE, message);
        context.startActivityForResult(intent, FORWARD_MESSAGE);
        context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_forward_picker);

        initViewModel();
        initView();
        setRecentChatsFromDatabase();
    }

    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            showToolbar();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
        }
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this,
                new TAPSearchChatViewModel.TAPSearchChatViewModelFactory(
                        getApplication(), instanceKey))
                .get(TAPSearchChatViewModel.class);
        vm.setSelectedMessages(getIntent().getParcelableArrayListExtra(MESSAGE));
    }

    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        ivButtonClearText = findViewById(R.id.iv_button_clear_text);
        tvTitle = findViewById(R.id.tv_title);
        etSearch = findViewById(R.id.et_search);
        rvForwardList = findViewById(R.id.rv_forward_list);

        etSearch.addTextChangedListener(searchTextWatcher);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> showSearchBar());
        ivButtonClearText.setOnClickListener(v -> etSearch.setText(""));

        adapter = new TAPSearchChatAdapter(instanceKey, vm.getSearchResults(), Glide.with(this), roomListInterface);
        rvForwardList.setAdapter(adapter);
        rvForwardList.setHasFixedSize(false);
        rvForwardList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(rvForwardList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        rvForwardList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.dismissKeyboard(TAPForwardPickerActivity.this);
            }
        });
    }

    private void showToolbar() {
        TAPUtils.dismissKeyboard(this);
        ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        ivButtonSearch.setVisibility(View.VISIBLE);
        ((TransitionDrawable) clActionBar.getBackground()).reverseTransition(SHORT_ANIMATION_TIME);
    }

    private void showSearchBar() {
        ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        ivButtonSearch.setVisibility(View.GONE);
        TAPUtils.showKeyboard(this, etSearch);
        ((TransitionDrawable) clActionBar.getBackground()).startTransition(SHORT_ANIMATION_TIME);
    }

    private void startSearch() {
        if (etSearch.getText().toString().equals(" ")) {
            // Clear keyword when EditText only contains a space
            etSearch.setText("");
            return;
        }

        vm.clearSearchResults();
        vm.setSearchKeyword(etSearch.getText().toString().toLowerCase().trim());
        adapter.setSearchKeyword(vm.getSearchKeyword());
        if (vm.getSearchKeyword().isEmpty()) {
            showRecentChats();
            ivButtonClearText.setVisibility(View.GONE);
        } else {
            TAPDataManager.getInstance(instanceKey).searchAllRoomsFromDatabase(vm.getSearchKeyword(), roomSearchListener);
            ivButtonClearText.setVisibility(View.VISIBLE);
        }
    }

    private void setRecentChatsFromDatabase() {
        TAPDataManager.getInstance(instanceKey).getRoomList(false, new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionCount) {
                onSelectFinished(entities);
            }

            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                if (null != entities && entities.size() > 0) {
                    TAPSearchChatModel recentTitleItem = new TAPSearchChatModel(SECTION_TITLE);
                    recentTitleItem.setSectionTitle(getString(R.string.tap_recent_chats));
                    vm.addRecentSearches(recentTitleItem);

                    boolean isSavedMessagesExist = false;
                    String myId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
                    for (TAPMessageEntity entity : entities) {
                        if (entity.getRoomDeleted() == null || !entity.getRoomDeleted()) {
                            TAPSearchChatModel recentItem = new TAPSearchChatModel(ROOM_ITEM);
                            TAPRoomModel roomModel = TAPRoomModel.Builder(entity);
                            recentItem.setRoom(roomModel);
                            if (TAPUtils.isSavedMessagesRoom(recentItem.getRoom().getRoomID(), instanceKey)) {
                                if (TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                                    vm.addRecentSearches(0, recentItem);
                                }
                                isSavedMessagesExist = true;
                            } else {
                                vm.addRecentSearches(recentItem);
                            }
                        }
                    }
                    if (!isSavedMessagesExist && TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                        TAPSearchChatModel savedMessagesRoom = new TAPSearchChatModel(ROOM_ITEM);
                        // Add saved messages to search result
                        String savedMessagesRoomID = String.format("%s-%s", myId, myId);
                        TAPRoomModel room = TAPRoomModel.Builder(savedMessagesRoomID, getString(R.string.tap_saved_messages), TYPE_PERSONAL, new TAPImageURL("", ""), "");
                        savedMessagesRoom.setRoom(room);
                        vm.addRecentSearches(0, savedMessagesRoom);
                    }
                }
                showRecentChats();
            }
        });
    }

    private void showRecentChats() {
        runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
    }

    private void setEmptyState() {
        vm.clearSearchResults();
        vm.addSearchResult(new TAPSearchChatModel(EMPTY_STATE));
        runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            startSearch();
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TapTalkRoomListInterface roomListInterface = new TapTalkRoomListInterface() {
        @Override
        public void onRoomSelected(TAPRoomModel roomModel) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(MESSAGE, vm.getSelectedMessages());
            intent.putExtra(ROOM, roomModel);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> roomSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            if (entities.size() > 0) {
                TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.tap_chats_and_contacts));
                vm.addSearchResult(sectionTitleChatsAndContacts);
                boolean isSavedMessagesExist = false;
                String myId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
                for (TAPMessageEntity entity : entities) {
                    if (entity.getRoomDeleted() == null || !entity.getRoomDeleted()) {
                        TAPSearchChatModel result = new TAPSearchChatModel(ROOM_ITEM);
                        // Convert message to room model
                        TAPRoomModel room = TAPRoomModel.Builder(entity);
                        result.setRoom(room);
                        if (TAPUtils.isSavedMessagesRoom(result.getRoom().getRoomID(), instanceKey)) {
                            if (TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                                vm.addSearchResult(0, result);
                            }
                            isSavedMessagesExist = true;
                        } else {
                            room.setUnreadCount(unreadMap.get(room.getRoomID()));
                            vm.addSearchResult(result);
                        }
                    }
                }
                if (!isSavedMessagesExist && TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                    TAPSearchChatModel savedMessagesRoom = new TAPSearchChatModel(ROOM_ITEM);
                    // Add saved messages to search result
                    String savedMessagesRoomID = String.format("%s-%s", myId, myId);
                    TAPRoomModel room = TAPRoomModel.Builder(savedMessagesRoomID, getString(R.string.tap_saved_messages), TYPE_PERSONAL, new TAPImageURL("", ""), "");
                    savedMessagesRoom.setRoom(room);
                    vm.addSearchResult(0, savedMessagesRoom);
                }
                runOnUiThread(() -> {
                    adapter.setItems(vm.getSearchResults(), false);
                    TAPDataManager.getInstance(instanceKey).searchContactsByName(vm.getSearchKeyword(), contactSearchListener);
                });
            } else {
                TAPDataManager.getInstance(instanceKey).searchContactsByName(vm.getSearchKeyword(), contactSearchListener);
            }
        }
    };

    private TAPDatabaseListener<TAPUserModel> contactSearchListener = new TAPDatabaseListener<TAPUserModel>() {
        @Override
        public void onSelectFinished(List<TAPUserModel> entities) {
            if (entities.size() > 0) {
                if (vm.getSearchResults().size() == 0) {
                    TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                    sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.tap_chats_and_contacts));
                    vm.addSearchResult(sectionTitleChatsAndContacts);
                }
                for (TAPUserModel contact : entities) {
                    if (contact.getDeleted() == null || contact.getDeleted() <= 0) {
                        TAPSearchChatModel result = new TAPSearchChatModel(ROOM_ITEM);
                        // Convert contact to room model
                        // TODO: 18 October 2018 LENGKAPIN DATA
                        TAPRoomModel room = new TAPRoomModel(
                                TAPChatManager.getInstance(instanceKey).arrangeRoomId(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), contact.getUserID()),
                                contact.getFullname(),
                                TYPE_PERSONAL,
                                contact.getImageURL(),
                                /* SET DEFAULT ROOM COLOR*/""
                        );
                        // Check if result already contains contact from chat room query
                        if (!vm.resultContainsRoom(room.getRoomID())) {
                            result.setRoom(room);
                            vm.addSearchResult(result);
                        }
                    }
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            } else if (vm.getSearchResults().size() == 0) {
                setEmptyState();
            }
        }
    };
}
