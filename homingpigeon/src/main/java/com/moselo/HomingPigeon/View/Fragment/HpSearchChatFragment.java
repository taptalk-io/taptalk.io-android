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
import android.widget.TextView;

import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.HpSearchChatModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.HpSearchChatAdapter;
import com.moselo.HomingPigeon.ViewModel.HpSearchChatViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.CHAT_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.CONTACT_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.EMPTY_STATE;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.MESSAGE_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.RECENT_ITEM;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.RECENT_TITLE;
import static com.moselo.HomingPigeon.Model.HpSearchChatModel.Type.SECTION_TITLE;

public class HpSearchChatFragment extends Fragment {

    private static final String TAG = HpSearchChatFragment.class.getSimpleName();
    private Activity activity;

    private ConstraintLayout clActionBar;
    private ImageView ivButtonBack;
    private TextView tvTitle;
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

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpSearchChatViewModel.class);
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        tvTitle = view.findViewById(R.id.tv_title);
        etSearch = view.findViewById(R.id.et_search);
        ivButtonAction = view.findViewById(R.id.iv_button_action);
        recyclerView = view.findViewById(R.id.recyclerView);

        ivButtonBack.setOnClickListener(v -> ((HpRoomListActivity) activity).showRoomList());
        ivButtonAction.setOnClickListener(v -> toggleSearchBar());

        etSearch.addTextChangedListener(searchTextWatcher);

        adapter = new HpSearchChatAdapter(vm.getSearchResults());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void toggleSearchBar() {
        if (vm.isSearchActive()) {
            showToolbar();
            showRecentSearches();
        } else {
            showSearchBar();
            setDummyDataforSearchChat();
        }
    }

    private void showToolbar() {
        vm.setSearchActive(false);
        tvTitle.setVisibility(View.VISIBLE);
        etSearch.setVisibility(View.GONE);
        etSearch.setText("");
        etSearch.clearFocus();
        ivButtonAction.setImageResource(R.drawable.hp_ic_search_grey);
        HpUtils.getInstance().dismissKeyboard(getActivity());
    }

    private void showSearchBar() {
        vm.setSearchActive(true);
        tvTitle.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
        ivButtonAction.setImageResource(R.drawable.hp_ic_close_grey);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            showToolbar();
            showRecentSearches();
        }
    }

    // TODO: 24/09/18 apusin dummy ini kalau udah ada datanya
    private void showRecentSearches() {
        vm.getSearchResults().clear();

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

        vm.getSearchResults().clear();
        vm.addSearchResult(emptyTitle);
        vm.addSearchResult(emptyItem);
        activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private void setDummyDataforSearchChat() {
        vm.getSearchResults().clear();

        HpSearchChatModel sectionTitleChats = new HpSearchChatModel(SECTION_TITLE);
        sectionTitleChats.setSectionTitle("CHATS");
        HpSearchChatModel sectionTitleMessages = new HpSearchChatModel(SECTION_TITLE);
        sectionTitleMessages.setSectionTitle("MESSAGES");
        HpSearchChatModel sectionTitleContacts = new HpSearchChatModel(SECTION_TITLE);
        sectionTitleContacts.setSectionTitle("OTHER CONTACTS");

        HpSearchChatModel chatItem = new HpSearchChatModel(CHAT_ITEM);
        HpSearchChatModel messageItem = new HpSearchChatModel(MESSAGE_ITEM);
        HpSearchChatModel messageItemLast = new HpSearchChatModel(MESSAGE_ITEM);
        messageItemLast.setLastInSection(true);
        HpSearchChatModel contactItem = new HpSearchChatModel(CONTACT_ITEM);

        vm.addSearchResult(sectionTitleChats);
        vm.addSearchResult(chatItem);
        vm.addSearchResult(sectionTitleMessages);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItem);
        vm.addSearchResult(messageItemLast);
        vm.addSearchResult(sectionTitleContacts);
        vm.addSearchResult(contactItem);
        vm.addSearchResult(contactItem);
        vm.addSearchResult(contactItem);
        vm.addSearchResult(contactItem);
        activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            vm.getSearchResults().clear();
            String searchKeyword = etSearch.getText().toString().toLowerCase().trim();
            if (searchKeyword.isEmpty()) {
                showRecentSearches();
            } else {
                HpDataManager.getInstance().searchAllMessagesFromDatabase(searchKeyword, messageSearchListener);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private HpDatabaseListener<HpMessageEntity> messageSearchListener = new HpDatabaseListener<HpMessageEntity>() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            if (vm.isProcessingSearchResults()) {
                vm.setMessageResultsPending(true);
            } else if (entities.size() > 0) {
                for (HpMessageEntity entity : entities) {
                    HpSearchChatModel result = new HpSearchChatModel(MESSAGE_ITEM);
                    result.setMessage(entity);
                    vm.addSearchResult(result);
                }
                vm.getSearchResults().get(vm.getSearchResults().size() - 1).setLastInSection(true);
                activity.runOnUiThread(() -> adapter.setItems(vm.getSearchResults(), false));
            } else {
                // TODO: 17 October 2018 CHECK IF OTHER RESULTS (CHAT ROOM, CONTACT) IS EMPTY
                setEmptyState();
            }
        }
    };
}
