package com.moselo.HomingPigeon.View.BottomSheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moselo.HomingPigeon.Listener.TAPAttachmentListener;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpAttachmentAdapter;

public class HpAttachmentBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private TAPAttachmentListener attachmentListener;
    private View.OnClickListener onClickListener = v -> dismiss();

    public HpAttachmentBottomSheet() {
        // Required empty public constructor
    }

    public HpAttachmentBottomSheet(TAPAttachmentListener attachmentListener) {
        this.attachmentListener = attachmentListener;
    }

    public static HpAttachmentBottomSheet newInstance() {
        HpAttachmentBottomSheet fragment = new HpAttachmentBottomSheet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hp_bottom_sheet_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setAdapter(new HpAttachmentAdapter(attachmentListener, onClickListener));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }
}
