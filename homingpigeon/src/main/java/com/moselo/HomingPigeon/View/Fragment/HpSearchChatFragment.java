package com.moselo.HomingPigeon.View.Fragment;

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

import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Model.SearchChatModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.HpSearchChatAdapter;
import com.moselo.HomingPigeon.ViewModel.HpSearchChatViewModel;

import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.CHAT_ITEM;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.CONTACT_ITEM;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.EMPTY_STATE;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.MESSAGE_ITEM;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.RECENT_ITEM;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.RECENT_TITLE;
import static com.moselo.HomingPigeon.Model.SearchChatModel.MyReturnType.SECTION_TITLE;

public class HpSearchChatFragment extends Fragment {

    private static final String TAG = HpSearchChatFragment.class.getSimpleName();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hp_fragment_search_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initView(view);
        setDummyDataforRecent();
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

        ivButtonBack.setOnClickListener(v -> ((HpRoomListActivity) getActivity()).showRoomList());
        ivButtonAction.setOnClickListener(v -> {
            if (vm.isSearchActive()) {
                showToolbar();
                setDummyDataforRecent();
            } else {
                showSearchBar();
                setDummyDataforSearchChat();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().toLowerCase().equals("empty")) {
                    setEmptyState();
                } else {
                    setDummyDataforSearchChat();
                }
            }
        });

        adapter = new HpSearchChatAdapter(vm.getSearchList());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
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
            setDummyDataforRecent();
        }
    }

    // TODO: 24/09/18 apusin dummy ini kalau udah ada datanya
    private void setDummyDataforRecent() {
        vm.getSearchList().clear();

        SearchChatModel recentTitleItem = new SearchChatModel(RECENT_TITLE);
        vm.addSearchList(recentTitleItem);

        SearchChatModel recentItem = new SearchChatModel(RECENT_ITEM);
        RecentSearchEntity entity = new RecentSearchEntity("Mo Salah", System.currentTimeMillis());
        recentItem.setRecentSearch(entity);
        vm.addSearchList(recentItem);
        vm.addSearchList(recentItem);
        vm.addSearchList(recentItem);
        vm.addSearchList(recentItem);
        adapter.setItems(vm.getSearchList(), false);
    }

    private void setEmptyState() {
        vm.getSearchList().clear();

        SearchChatModel emptyTitle = new SearchChatModel(SECTION_TITLE);
        emptyTitle.setSectionTitle("SEARCH RESULTS");

        SearchChatModel emptyItem = new SearchChatModel(EMPTY_STATE);

        vm.addSearchList(emptyTitle);
        vm.addSearchList(emptyItem);
        adapter.setItems(vm.getSearchList(), false);
    }

    private void setDummyDataforSearchChat() {
        vm.getSearchList().clear();

        SearchChatModel sectionTitleChats = new SearchChatModel(SECTION_TITLE);
        sectionTitleChats.setSectionTitle("CHATS");
        SearchChatModel sectionTitleMessages = new SearchChatModel(SECTION_TITLE);
        sectionTitleMessages.setSectionTitle("MESSAGES");
        SearchChatModel sectionTitleContacts = new SearchChatModel(SECTION_TITLE);
        sectionTitleContacts.setSectionTitle("OTHER CONTACTS");

        SearchChatModel chatItem = new SearchChatModel(CHAT_ITEM);
        SearchChatModel messageItem = new SearchChatModel(MESSAGE_ITEM);
        SearchChatModel messageItemLast = new SearchChatModel(MESSAGE_ITEM);
        messageItemLast.setLastInSection(true);
        SearchChatModel contactItem = new SearchChatModel(CONTACT_ITEM);

        vm.addSearchList(sectionTitleChats);
        vm.addSearchList(chatItem);
        vm.addSearchList(sectionTitleMessages);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItem);
        vm.addSearchList(messageItemLast);
        vm.addSearchList(sectionTitleContacts);
        vm.addSearchList(contactItem);
        vm.addSearchList(contactItem);
        vm.addSearchList(contactItem);
        vm.addSearchList(contactItem);
        adapter.setItems(vm.getSearchList(), false);
    }

}
