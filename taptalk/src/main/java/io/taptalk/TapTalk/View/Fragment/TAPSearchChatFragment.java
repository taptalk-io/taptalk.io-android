package io.taptalk.TapTalk.View.Fragment;

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

import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPSearchChatModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPSearchChatAdapter;
import io.taptalk.TapTalk.ViewModel.TAPSearchChatViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.EMPTY_STATE;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.MESSAGE_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.RECENT_TITLE;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.ROOM_ITEM;
import static io.taptalk.TapTalk.Model.TAPSearchChatModel.Type.SECTION_TITLE;

public class TAPSearchChatFragment extends Fragment {

    private static final String TAG = TAPSearchChatFragment.class.getSimpleName();

    private ConstraintLayout clActionBar;
    private ImageView ivButtonBack;
    private EditText etSearch;
    private ImageView ivButtonAction;
    private RecyclerView recyclerView;

    private TAPSearchChatViewModel vm;
    private TAPSearchChatAdapter adapter;

    public TAPSearchChatFragment() {
    }

    public static TAPSearchChatFragment newInstance() {
        TAPSearchChatFragment fragment = new TAPSearchChatFragment();
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
        startSearch();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            clearSearch();
        } else {
            TAPUtils.getInstance().showKeyboard(getActivity(), etSearch);
        }
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPSearchChatViewModel.class);
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        etSearch = view.findViewById(R.id.et_search);
        ivButtonAction = view.findViewById(R.id.iv_button_action);
        recyclerView = view.findViewById(R.id.recyclerView);

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new TAPSearchChatAdapter(vm.getSearchResults());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                TAPUtils.getInstance().dismissKeyboard(getActivity());
            }
        });

        ivButtonBack.setOnClickListener(v -> {
            TAPMainRoomListFragment fragment = (TAPMainRoomListFragment) this.getParentFragment();
            if (null != fragment)
                fragment.showRoomList();
            TAPUtils.getInstance().dismissKeyboard(getActivity());
        });
        ivButtonAction.setOnClickListener(v -> clearSearch());
    }

    private void clearSearch() {
        etSearch.setText("");
        etSearch.clearFocus();
        TAPUtils.getInstance().dismissKeyboard(getActivity());
    }

    private void setRecentSearchItemsFromDatabase() {
        //observe databasenya pake live data
        vm.getRecentSearchList().observe(this, hpRecentSearchEntities -> {
            vm.clearRecentSearches();

            if (null != hpRecentSearchEntities && hpRecentSearchEntities.size() > 0) {
                TAPSearchChatModel recentTitleItem = new TAPSearchChatModel(RECENT_TITLE);
                vm.addRecentSearches(recentTitleItem);
            }

            if (null != hpRecentSearchEntities) {
                for (TAPRecentSearchEntity entity : hpRecentSearchEntities) {
                    TAPSearchChatModel recentItem = new TAPSearchChatModel(ROOM_ITEM);
                    TAPRoomModel roomModel = new TAPRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            TAPUtils.getInstance().fromJSON(new TypeReference<TAPImageURL>() {
                            }, entity.getRoomImage()),
                            entity.getRoomColor());
                    recentItem.setRoom(roomModel);
                    vm.addRecentSearches(recentItem);
                }
            }

            //kalau ada perubahan sama databasenya ga lgsg diubah karena nnti bakal ngilangin hasil search yang muncul
            //kalau lagi muncul hasil search updatenya tunggu fungsi showRecentSearch dipanggil
            //kalau lagi muncul halaman recent search baru set items
            if (vm.isRecentSearchShown() && null != getActivity())
                getActivity().runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
        });

        showRecentSearches();
    }

    //ini function buat munculin recent search nya lagi
    //jadi dy gantiin isi recyclerView nya sama list yang diisi di setRecentSearchItemsFromDatabase (dari LiveData)
    private void showRecentSearches() {
        if (null != getActivity()) {
            getActivity().runOnUiThread(() -> adapter.setItems(vm.getRecentSearches(), false));
            //flag untuk nandain kalau skrg lagi munculin halaman recent Search
            vm.setRecentSearchShown(true);
        }
    }

    //ini fungsi buat set tampilan kalau lagi empty
    private void setEmptyState() {
        TAPSearchChatModel emptyItem = new TAPSearchChatModel(EMPTY_STATE);
        vm.clearSearchResults();
        vm.addSearchResult(emptyItem);
        if (null != getActivity())
            getActivity().runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private void startSearch() {
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
            TAPDataManager.getInstance().searchAllRoomsFromDatabase(vm.getSearchKeyword(), roomSearchListener);
            //flag untuk nandain kalau skrg lagi tidak munculin halaman recent Search
            vm.setRecentSearchShown(false);
        }
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            startSearch();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TAPDatabaseListener<TAPMessageEntity> roomSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap) {
            if (entities.size() > 0 && null != getActivity()) {
                TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.chats_and_contacts));
                vm.addSearchResult(sectionTitleChatsAndContacts);
                for (TAPMessageEntity entity : entities) {
                    TAPSearchChatModel result = new TAPSearchChatModel(ROOM_ITEM);
                    // Convert message to room model
                    TAPRoomModel room = new TAPRoomModel(
                            entity.getRoomID(),
                            entity.getRoomName(),
                            entity.getRoomType(),
                            // TODO: 18 October 2018 REMOVE CHECK
                            /* TEMPORARY CHECK FOR NULL IMAGE */null != entity.getRoomImage() ?
                            TAPUtils.getInstance().fromJSON(new TypeReference<TAPImageURL>() {
                            }, entity.getRoomImage())
                            /* TEMPORARY CHECK FOR NULL IMAGE */ : null,
                            entity.getRoomColor());
                    room.setUnreadCount(unreadMap.get(room.getRoomID()));
                    result.setRoom(room);
                    vm.addSearchResult(result);
                }
                getActivity().runOnUiThread(() -> {
                    adapter.setItems(vm.getSearchResults(), false);
                    TAPDataManager.getInstance().searchAllMyContacts(vm.getSearchKeyword(), contactSearchListener);
                });
            } else {
                TAPDataManager.getInstance().searchAllMyContacts(vm.getSearchKeyword(), contactSearchListener);
            }
        }
    };

    private TAPDatabaseListener<TAPUserModel> contactSearchListener = new TAPDatabaseListener<TAPUserModel>() {
        @Override
        public void onSelectFinished(List<TAPUserModel> entities) {
            if (entities.size() > 0) {
                if (vm.getSearchResults().size() == 0) {
                    TAPSearchChatModel sectionTitleChatsAndContacts = new TAPSearchChatModel(SECTION_TITLE);
                    sectionTitleChatsAndContacts.setSectionTitle(getString(R.string.chats_and_contacts));
                    vm.addSearchResult(sectionTitleChatsAndContacts);
                }
                for (TAPUserModel contact : entities) {
                    TAPSearchChatModel result = new TAPSearchChatModel(ROOM_ITEM);
                    // Convert contact to room model
                    // TODO: 18 October 2018 LENGKAPIN DATA
                    TAPRoomModel room = new TAPRoomModel(
                            TAPChatManager.getInstance().arrangeRoomId(TAPDataManager.getInstance().getActiveUser().getUserID(), contact.getUserID()),
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
                if (null != getActivity())
                    getActivity().runOnUiThread(() -> {
                        adapter.setItems(vm.getSearchResults(), false);
                        TAPDataManager.getInstance().searchAllMessagesFromDatabase(vm.getSearchKeyword(), messageSearchListener);
                    });
            } else {
                TAPDataManager.getInstance().searchAllMessagesFromDatabase(vm.getSearchKeyword(), messageSearchListener);
            }
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> messageSearchListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            if (entities.size() > 0 && null != getActivity()) {
                TAPSearchChatModel sectionTitleMessages = new TAPSearchChatModel(SECTION_TITLE);
                sectionTitleMessages.setSectionTitle(getString(R.string.messages));
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
}
