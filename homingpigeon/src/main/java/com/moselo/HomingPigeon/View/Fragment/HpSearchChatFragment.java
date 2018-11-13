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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;
import com.moselo.HomingPigeon.Manager.TAPChatManager;
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
import java.util.Map;

import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.EMPTY_STATE;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.MESSAGE_ITEM;
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
        setRecentSearchItemsFromDatabase();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            clearSearch();
        } else {
            TAPUtils.getInstance().showKeyboard(activity, etSearch);
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.getInstance().dismissKeyboard(activity);
            }
        });

        ivButtonBack.setOnClickListener(v -> {
            ((HpRoomListActivity) activity).showRoomList();
            TAPUtils.getInstance().dismissKeyboard(activity);
        });
        ivButtonAction.setOnClickListener(v -> clearSearch());
    }

    private void clearSearch() {
        etSearch.setText("");
        etSearch.clearFocus();
        TAPUtils.getInstance().dismissKeyboard(activity);
    }

    private void setRecentSearchItemsFromDatabase() {
        //observe databasenya pake live data
        vm.getRecentSearchList().observe(this, hpRecentSearchEntities -> {
            vm.clearRecentSearches();

            if (null != hpRecentSearchEntities && hpRecentSearchEntities.size() > 0) {
                HpSearchChatModel recentTitleItem = new HpSearchChatModel(RECENT_TITLE);
                vm.addRecentSearches(recentTitleItem);
            }

            if (null != hpRecentSearchEntities) {
                for (TAPRecentSearchEntity entity : hpRecentSearchEntities) {
                    HpSearchChatModel recentItem = new HpSearchChatModel(ROOM_ITEM);
                    HpRoomModel roomModel = new HpRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            TAPUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {}, entity.getRoomImage()),
                            entity.getRoomColor());
                    recentItem.setRoom(roomModel);
                    vm.addRecentSearches(recentItem);
                }
            }

            //kalau ada perubahan sama databasenya ga lgsg diubah karena nnti bakal ngilangin hasil search yang muncul
            //kalau lagi muncul hasil search updatenya tunggu fungsi showRecentSearch dipanggil
            //kalau lagi muncul halaman recent search baru set items
            if (vm.isRecentSearchShown())
                activity.runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
        });

        showRecentSearches();
    }

    //ini function buat munculin recent search nya lagi
    //jadi dy gantiin isi recyclerView nya sama list yang diisi di setRecentSearchItemsFromDatabase (dari LiveData)
    private void showRecentSearches() {
        activity.runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
        //flag untuk nandain kalau skrg lagi munculin halaman recent Search
        vm.setRecentSearchShown(true);
    }

    //ini fungsi buat set tampilan kalau lagi empty
    private void setEmptyState() {
        HpSearchChatModel emptyItem = new HpSearchChatModel(EMPTY_STATE);
        vm.clearSearchResults();
        vm.addSearchResult(emptyItem);
        activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etSearch.getText().toString().equals(" ")) {
                // Clear keyword when EditText only contains a space
                etSearch.setText("");
                return;
            }

            vm.clearSearchResults();
            vm.setSearchKeyword(etSearch.getText().toString().toLowerCase().trim().replaceAll("[^A-Za-z0-9 ]", ""));
            adapter.setSearchKeyword(vm.getSearchKeyword());
            if (vm.getSearchKeyword().isEmpty()) {
                showRecentSearches();
            } else {
                HpDataManager.getInstance().searchAllRoomsFromDatabase(vm.getSearchKeyword(), roomSearchListener);
                //flag untuk nandain kalau skrg lagi tidak munculin halaman recent Search
                vm.setRecentSearchShown(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TAPDatabaseListener<TAPMessageEntity> roomSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap) {
            if (entities.size() > 0) {
                HpSearchChatModel sectionTitleChatsAndContacts = new HpSearchChatModel(SECTION_TITLE);
                sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.chats_and_contacts));
                vm.addSearchResult(sectionTitleChatsAndContacts);
                for (TAPMessageEntity entity : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(ROOM_ITEM);
                    // Convert message to room model
                    HpRoomModel room = new HpRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            // TODO: 18 October 2018 REMOVE CHECK
                            /* TEMPORARY CHECK FOR NULL IMAGE */null != entity.getRoomImage() ?
                            TAPUtils.getInstance().fromJSON(new TypeReference<HpImageURL>() {
                            }, entity.getRoomImage())
                            /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                            entity.getRoomColor());
                    room.setUnreadCount(unreadMap.get(room.getRoomID()));
                    result.setRoom(room);
                    vm.addSearchResult(result);
                }
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            }
            HpDataManager.getInstance().searchAllMyContacts(vm.getSearchKeyword(), contactSearchListener);
        }
    };

    private TAPDatabaseListener<HpUserModel> contactSearchListener = new TAPDatabaseListener<HpUserModel>() {
        @Override
        public void onSelectFinished(List<HpUserModel> entities) {
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
                            TAPChatManager.getInstance().arrangeRoomId(HpDataManager.getInstance().getActiveUser().getUserID(), contact.getUserID()),
                            contact.getName(),
                            /* 1 ON 1 ROOM TYPE */ 1,
                            contact.getAvatarURL(),
                            /* SET DEFAULT ROOM COLOR*/""
                    );
                    // Check if result already contains contact from chat room query
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

    private TAPDatabaseListener<TAPMessageEntity> messageSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            if (entities.size() > 0) {
                HpSearchChatModel sectionTitleMessages = new HpSearchChatModel(SECTION_TITLE);
                sectionTitleMessages.setSectionTitle(getString(R.string.messages));
                vm.addSearchResult(sectionTitleMessages);
                for (TAPMessageEntity entity : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(MESSAGE_ITEM);
                    result.setMessage(entity);
                    vm.addSearchResult(result);
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            } else if (vm.getSearchResults().size() == 0) {
                setEmptyState();
            }
        }
    };
}
