package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.Model.HpRoomModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.HpSearchChatAdapter;
import com.moselo.HomingPigeon.ViewModel.HpSearchChatViewModel;

import java.util.List;

import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.EMPTY_STATE;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.MESSAGE_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.RECENT_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.RECENT_TITLE;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.ROOM_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.SECTION_TITLE;

public class HpSearchChatFragment extends Fragment {

    private static final String TAG = HpSearchChatFragment.class.getSimpleName();
    private Activity activity;

    private ConstraintLayout clActionBar;
    private ImageView ivButtonBack;
    private EditText etSearch;
    private ImageView ivButtonAction;
    private RecyclerView recyclerView;

    private HpSearchChatViewModel vm;
    private HpSearchChatAdapter adapter;

    public HpSearchChatFragment() {
    }

    public static HpSearchChatFragment newInstance() {
        HpSearchChatFragment fragment = new HpSearchChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.hp_fragment_search_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initView(view);
        showRecentSearches();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            clearSearch();
        } else {
            HpUtils.getInstance().showKeyboard(activity, etSearch);
        }
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpSearchChatViewModel.class);
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        etSearch = view.findViewById(R.id.et_search);
        ivButtonAction = view.findViewById(R.id.iv_button_action);
        recyclerView = view.findViewById(R.id.recyclerView);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new HpSearchChatAdapter(vm.getSearchResults());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ivButtonBack.setOnClickListener(v -> ((HpRoomListActivity) activity).showRoomList());
        ivButtonAction.setOnClickListener(v -> clearSearch());
    }

    private void clearSearch() {
        etSearch.setText("");
        etSearch.clearFocus();
        HpUtils.getInstance().dismissKeyboard(activity);
    }

    // TODO: 24/09/18 apusin dummy ini kalau udah ada datanya
    private void showRecentSearches() {
        vm.clearSearchResults();

        HpSearchChatModel recentTitleItem = new HpSearchChatModel(RECENT_TITLE);
        vm.addSearchResult(recentTitleItem);

        HpSearchChatModel recentItem = new HpSearchChatModel(RECENT_ITEM);
        HpRecentSearchEntity entity = new HpRecentSearchEntity("Mo Salah", System.currentTimeMillis());
        recentItem.setRecentSearch(entity);
        vm.addSearchResult(recentItem);
        vm.addSearchResult(recentItem);
        vm.addSearchResult(recentItem);
        vm.addSearchResult(recentItem);
        activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private void setEmptyState() {
        HpSearchChatModel emptyTitle = new HpSearchChatModel(SECTION_TITLE);
        emptyTitle.setSectionTitle(getString(R.string.search_results));
        HpSearchChatModel emptyItem = new HpSearchChatModel(EMPTY_STATE);

        vm.clearSearchResults();
        vm.addSearchResult(emptyTitle);
        vm.addSearchResult(emptyItem);
        activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            vm.clearSearchResults();
            vm.setSearchKeyword(etSearch.getText().toString().toLowerCase().trim().replaceAll("[^A-Za-z0-9 ]", ""));
            adapter.setSearchKeyword(vm.getSearchKeyword());
            if (vm.getSearchKeyword().isEmpty()) {
                showRecentSearches();
            } else {
                //etSearch.removeTextChangedListener(this);
                Log.e(TAG, "onTextChanged search started: " + vm.getSearchKeyword());
                HpDataManager.getInstance().searchAllRoomsFromDatabase(vm.getSearchKeyword(), roomSearchListener);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private HpDatabaseListener<HpMessageEntity> roomSearchListener = new HpDatabaseListener<HpMessageEntity>() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            Log.e(TAG, "onSelectFinished search room finished: " + entities.size());
            if (entities.size() > 0) {
                HpSearchChatModel sectionTitleChatsAndContacts = new HpSearchChatModel(SECTION_TITLE);
                sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.chats_and_contacts));
                vm.addSearchResult(sectionTitleChatsAndContacts);
                for (HpMessageEntity entity : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(ROOM_ITEM);
                    // Convert message to room model
                    HpRoomModel room = new HpRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            // TODO: 18 October 2018 REMOVE CHECK
                            /* TEMPORARY CHECK FOR NULL IMAGE */null != entity.getRoomImage() ?
                            HpUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {
                            }, entity.getRoomImage())
                            /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                            entity.getRoomColor());
                    result.setRoom(room);
                    vm.addSearchResult(result);
                }
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            }
            HpDataManager.getInstance().searchAllMyContacts(vm.getSearchKeyword(), contactSearchListener);
        }
    };

    private HpDatabaseListener<HpUserModel> contactSearchListener = new HpDatabaseListener<HpUserModel>() {
        @Override
        public void onSelectFinished(List<HpUserModel> entities) {
            Log.e(TAG, "onSelectFinished search contact finished: " + entities.size());
            if (entities.size() > 0) {
                if (vm.getSearchResults().size() == 0) {
                    HpSearchChatModel sectionTitleChatsAndContacts = new HpSearchChatModel(SECTION_TITLE);
                    sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.chats_and_contacts));
                    vm.addSearchResult(sectionTitleChatsAndContacts);
                }
                for (HpUserModel contact : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(ROOM_ITEM);
                    // Convert contact to room model
                    // TODO: 18 October 2018 LENGKAPIN DATA
                    HpRoomModel room = new HpRoomModel(
                            HpChatManager.getInstance().arrangeRoomId(HpDataManager.getInstance().getActiveUser().getUserID(), contact.getUserID()),
                            contact.getName(),
                            /* 1 ON 1 ROOM TYPE */ 1,
                            contact.getAvatarURL(),
                            /* SET DEFAULT ROOM COLOR*/""
                    );
                    if (!vm.resultContainsRoom(room.getRoomID())) {
                        result.setRoom(room);
                        vm.addSearchResult(result);
                    }
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            }
            HpDataManager.getInstance().searchAllMessagesFromDatabase(vm.getSearchKeyword(), messageSearchListener);
        }
    };

    private HpDatabaseListener<HpMessageEntity> messageSearchListener = new HpDatabaseListener<HpMessageEntity>() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            Log.e(TAG, "onSelectFinished search message finished: " + entities.size());
            if (entities.size() > 0) {
                HpSearchChatModel sectionTitleMessages = new HpSearchChatModel(SECTION_TITLE);
                sectionTitleMessages.setSectionTitle(getString(R.string.messages));
                vm.addSearchResult(sectionTitleMessages);
                for (HpMessageEntity entity : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(MESSAGE_ITEM);
                    result.setMessage(entity);
                    vm.addSearchResult(result);
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            } else if (vm.getSearchResults().size() == 0) {
                setEmptyState();
            }
            //etSearch.addTextChangedListener(searchTextWatcher);
        }
    };
}
