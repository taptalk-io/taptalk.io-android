package io.taptalk.TapTalk.View.BottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.View.Adapter.TAPAttachmentAdapter;
import io.taptalk.TapTalk.R;

public class TAPAttachmentBottomSheet extends BottomSheetDialogFragment {

    private String instanceKey = "";
    private RecyclerView recyclerView;
    private TAPAttachmentListener attachmentListener;
    private View.OnClickListener onClickListener = v -> dismiss();
    private boolean isImagePickerBottomSheet;

    public TAPAttachmentBottomSheet() {
        // Required empty public constructor
    }

    public TAPAttachmentBottomSheet(String instanceKey, TAPAttachmentListener attachmentListener) {
        this.instanceKey = instanceKey;
        this.attachmentListener = attachmentListener;
    }

    public TAPAttachmentBottomSheet(String instanceKey, boolean isImagePickerBottomSheet, TAPAttachmentListener attachmentListener) {
        this.instanceKey = instanceKey;
        this.isImagePickerBottomSheet = isImagePickerBottomSheet;
        this.attachmentListener = attachmentListener;
    }

    public static TAPAttachmentBottomSheet newInstance() {
        TAPAttachmentBottomSheet fragment = new TAPAttachmentBottomSheet();
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
        return inflater.inflate(R.layout.tap_bottom_sheet_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setAdapter(new TAPAttachmentAdapter(instanceKey, isImagePickerBottomSheet, attachmentListener, onClickListener));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
