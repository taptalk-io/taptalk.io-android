package com.moselo.HomingPigeon.View.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.RoomListActivity;

public class SearchChatFragment extends Fragment {

    private ConstraintLayout clActionBar;
    private ImageView ivButtonBack;
    private TextView tvTitle;
    private EditText etSearch;
    private ImageView ivButtonAction;
    private RecyclerView recyclerView;

    public SearchChatFragment() {
    }

    public static SearchChatFragment newInstance() {
        SearchChatFragment fragment = new SearchChatFragment();
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
        return inflater.inflate(R.layout.fragment_search_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        tvTitle = view.findViewById(R.id.tv_title);
        etSearch = view.findViewById(R.id.et_search);
        ivButtonAction = view.findViewById(R.id.iv_button_action);
        recyclerView = view.findViewById(R.id.recyclerView);

        ivButtonBack.setOnClickListener(v -> ((RoomListActivity) getActivity()).showRoomList());
    }
}
