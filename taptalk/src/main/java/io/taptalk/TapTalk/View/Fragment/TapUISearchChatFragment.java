package io.taptalk.TapTalk.View.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPSearchChatAdapter;
import io.taptalk.TapTalk.ViewModel.TAPSearchChatViewModel;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.EMPTY_STATE;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.MESSAGE_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.RECENT_TITLE;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.ROOM_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.SECTION_TITLE;

public class TapUISearchChatFragment extends Fragment {

    private static final String TAG = TapUISearchChatFragment.class.getSimpleName();

    private String instanceKey = "";
    private TapUIMainRoomListFragment mainRoomListFragment;
    private ConstraintLayout clActionBar;
    private ImageView ivButtonBack;
    private EditText etSearch;
    private ImageView ivButtonClearText;
    private RecyclerView recyclerView;

    private TAPSearchChatViewModel vm;
    private TAPSearchChatAdapter adapter;

    public TapUISearchChatFragment() {
    }

    public TapUISearchChatFragment(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapUISearchChatFragment newInstance(String instanceKey) {
        TapUISearchChatFragment fragment = new TapUISearchChatFragment(instanceKey);
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
        mainRoomListFragment = (TapUIMainRoomListFragment) this.getParentFragment();
        return inflater.inflate(R.layout.tap_fragment_search_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initView(view);
        setRecentSearchItemsFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        startSearch(etSearch.getText().toString());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            etSearch.setText("");
        } else {
            TAPUtils.showKeyboard(getActivity(), etSearch);
        }
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this,
                new TAPSearchChatViewModel.TAPSearchChatViewModelFactory(
                        getActivity().getApplication(), instanceKey))
                .get(TAPSearchChatViewModel.class);
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        etSearch = view.findViewById(R.id.et_search);
        ivButtonClearText = view.findViewById(R.id.iv_button_clear_text);
        recyclerView = view.findViewById(R.id.recyclerView);

        boolean isContactAvailable = !TapUI.getInstance(instanceKey).isAddContactDisabled() &&
                (TapUI.getInstance(instanceKey).isNewContactMenuButtonVisible() ||
                        TapUI.getInstance(instanceKey).isScanQRMenuButtonVisible());
        boolean isGroupChatAvailable = TapUI.getInstance(instanceKey).isNewGroupMenuButtonVisible();

        if (isContactAvailable && isGroupChatAvailable) {
            etSearch.setHint(getString(R.string.tap_search_chat_placeholder));
        } else if (isContactAvailable) {
            etSearch.setHint(getString(R.string.tap_search_chat_placeholder_chats_contacts));
        } else if (isGroupChatAvailable) {
            etSearch.setHint(getString(R.string.tap_search_chat_placeholder_chats_group));
        } else {
            etSearch.setHint(getString(R.string.tap_search_chat_placeholder_chats_only));
        }

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new TAPSearchChatAdapter(instanceKey, vm.getSearchResults(), Glide.with(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.dismissKeyboard(getActivity());
            }
        });

        ivButtonBack.setOnClickListener(v -> {
            TapUIMainRoomListFragment fragment = (TapUIMainRoomListFragment) this.getParentFragment();
            if (null != fragment)
                fragment.showRoomList();
            TAPUtils.dismissKeyboard(getActivity());
        });
        ivButtonClearText.setOnClickListener(v -> etSearch.setText(""));

        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            TAPUtils.dismissKeyboard(getActivity());
            return false;
        });
    }

    private void setRecentSearchItemsFromDatabase() {
        // Observe database with live data
        vm.getRecentSearchList().observe(getViewLifecycleOwner(), hpRecentSearchEntities -> {
            vm.clearRecentSearches();

            if (null != hpRecentSearchEntities && hpRecentSearchEntities.size() > 0) {
                TAPSearchChatModel recentTitleItem = new TAPSearchChatModel(RECENT_TITLE);
                recentTitleItem.setSectionTitle(getString(R.string.tap_recent));
                vm.addRecentSearches(recentTitleItem);
            }

            if (null != hpRecentSearchEntities) {
                for (TAPRecentSearchEntity entity : hpRecentSearchEntities) {
                    TAPSearchChatModel recentItem = new TAPSearchChatModel(ROOM_ITEM);
                    TAPRoomModel roomModel = new TAPRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            TAPUtils.fromJSON(new TypeReference<TAPImageURL>() {
                            }, entity.getRoomImage()),
                            entity.getRoomColor());
                    recentItem.setRoom(roomModel);
                    vm.addRecentSearches(recentItem);
                }
            }

            if (vm.getSearchState() == vm.STATE_RECENT_SEARCHES && null != getActivity())
                // Set items when search is not is progress
                getActivity().runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
        });

        showRecentSearches();
    }

    private void showRecentSearches() {
        if (null != getActivity()) {
            getActivity().runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
            vm.setSearchState(vm.STATE_RECENT_SEARCHES);
        }
    }

    private void setEmptyState() {
        TAPSearchChatModel emptyItem = new TAPSearchChatModel(EMPTY_STATE);
        vm.clearSearchResults();
        vm.addSearchResult(emptyItem);
        vm.setSearchState(vm.STATE_IDLE);
        if (null != getActivity()) {
            getActivity().runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
        }
    }

    private void startSearch(String keyword) {
        vm.clearSearchResults();
        vm.setSearchKeyword(keyword.toLowerCase().trim());
        adapter.setSearchKeyword(vm.getSearchKeyword());
        if (vm.getSearchKeyword().isEmpty()) {
            // Show recent searches if keyword is empty
            showRecentSearches();
            ivButtonClearText.setVisibility(View.GONE);
        } else if (vm.getSearchState() == vm.STATE_RECENT_SEARCHES || vm.getSearchState() == vm.STATE_IDLE) {
            // Search with keyword
            vm.setSearchState(vm.STATE_SEARCHING);
            TAPDataManager.getInstance(instanceKey).searchAllRoomsFromDatabase(vm.getSearchKeyword(), roomSearchListener);
            ivButtonClearText.setVisibility(View.VISIBLE);
        } else {
            // Set search as pending
            vm.setPendingSearch(vm.getSearchKeyword());
            vm.setSearchState(vm.STATE_PENDING);
        }
    }

    private TAPDatabaseListener<TAPMessageEntity> roomSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            if (vm.getSearchState() == vm.STATE_PENDING && !vm.getPendingSearch().isEmpty()) {
                vm.setSearchState(vm.STATE_IDLE);
                startSearch(vm.getPendingSearch());
                return;
            } else if (vm.getSearchState() != vm.STATE_SEARCHING) {
                return;
            }
            boolean isSavedMessagesExist = false;
            String myId = TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID();
            if (entities.size() > 0 && null != getActivity()) {
                TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.tap_chats_and_contacts));
                vm.addSearchResult(sectionTitleChatsAndContacts);
                for (TAPMessageEntity entity : entities) {
                    // Exclude active user's own room
                    if (!entity.getRoomID().equals(TAPChatManager.getInstance(instanceKey).arrangeRoomId(myId, myId))) {
                        TAPSearchChatModel result = new TAPSearchChatModel(ROOM_ITEM);
                        // Convert message to room model
                        TAPRoomModel room = TAPRoomModel.Builder(entity);
                        Integer unreadCount = unreadMap.get(room.getRoomID());
                        if (null != unreadCount) {
                            room.setUnreadCount(unreadCount);
                        }
                        result.setRoom(room);
                        Integer mentionCount = mentionMap.get(room.getRoomID());
                        if (null != mentionCount) {
                            result.setRoomMentionCount(mentionCount);
                        }
                        if (TAPUtils.isSavedMessagesRoom(result.getRoom().getRoomID(), instanceKey)) {
                            if (TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                                vm.addSearchResult(0, result);
                            }
                            isSavedMessagesExist = true;
                        } else {
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
                if (null != contactSearchListener) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setItems(vm.getSearchResults(), false);
                        TAPDataManager.getInstance(instanceKey).searchContactsByName(vm.getSearchKeyword(), contactSearchListener);
                    });
                }
            } else  {
                if (TapUI.getInstance(instanceKey).isSavedMessagesMenuEnabled()) {
                    TAPSearchChatModel savedMessagesRoom = new TAPSearchChatModel(ROOM_ITEM);
                    // Add saved messages to search result
                    String savedMessagesRoomID = String.format("%s-%s", myId, myId);
                    TAPRoomModel room = TAPRoomModel.Builder(savedMessagesRoomID, getString(R.string.tap_saved_messages), TYPE_PERSONAL, new TAPImageURL("", ""), "");
                    savedMessagesRoom.setRoom(room);
                    vm.addSearchResult(0, savedMessagesRoom);
                }
                if (null != contactSearchListener) {
                    TAPDataManager.getInstance(instanceKey).searchContactsByName(vm.getSearchKeyword(), contactSearchListener);
                }
            }
        }
    };

    private TAPDatabaseListener<TAPUserModel> contactSearchListener = new TAPDatabaseListener<TAPUserModel>() {
        @Override
        public void onSelectFinished(List<TAPUserModel> entities) {
            if (vm.getSearchState() == vm.STATE_PENDING && !vm.getPendingSearch().isEmpty()) {
                vm.setSearchState(vm.STATE_IDLE);
                startSearch(vm.getPendingSearch());
                return;
            } else if (vm.getSearchState() != vm.STATE_SEARCHING) {
                return;
            }
            if (entities.size() > 0) {
                if (vm.getSearchResults().size() == 0) {
                    TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                    sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.tap_chats_and_contacts));
                    vm.addSearchResult(sectionTitleChatsAndContacts);
                }
                for (TAPUserModel contact : entities) {
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
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                if (null != getActivity() && null != messageSearchListener) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setItems(vm.getSearchResults(), false);
                        TAPDataManager.getInstance(instanceKey).searchAllMessagesFromDatabase(vm.getSearchKeyword(), messageSearchListener);
                    });
                }
            } else if (null != messageSearchListener) {
                TAPDataManager.getInstance(instanceKey).searchAllMessagesFromDatabase(vm.getSearchKeyword(), messageSearchListener);
            }
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> messageSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            if (vm.getSearchState() == vm.STATE_PENDING && !vm.getPendingSearch().isEmpty()) {
                vm.setSearchState(vm.STATE_IDLE);
                startSearch(vm.getPendingSearch());
                return;
            } else if (vm.getSearchState() != vm.STATE_SEARCHING) {
                return;
            }
            vm.setPendingSearch("");
            vm.setSearchState(vm.STATE_IDLE);
            if (entities.size() > 0 && null != getActivity()) {
                TAPSearchChatModel sectionTitleMessages = new TAPSearchChatModel(SECTION_TITLE);
                sectionTitleMessages.setSectionTitle(getString(R.string.tap_messages));
                vm.addSearchResult(sectionTitleMessages);
                for (TAPMessageEntity entity : entities) {
                    TAPSearchChatModel result = new TAPSearchChatModel(MESSAGE_ITEM);
                    result.setMessage(entity);
                    vm.addSearchResult(result);
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                getActivity().runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            } else if (vm.getSearchResults().size() == 0) {
                setEmptyState();
            }
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.removeTextChangedListener(this);
            if (etSearch.getText().toString().equals(" ")) {
                // Clear keyword when EditText only contains a space
                etSearch.setText("");
            }
            startSearch(etSearch.getText().toString());
            etSearch.addTextChangedListener(this);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
