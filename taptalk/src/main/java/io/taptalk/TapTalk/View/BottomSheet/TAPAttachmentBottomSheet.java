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

import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPAttachmentModel;
import io.taptalk.TapTalk.View.Adapter.TAPAttachmentAdapter;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_CAMERA;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_DOCUMENT;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.ATTACH_LOCATION;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_PICTURE_CAMERA;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_PICTURE_GALLERY;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_REMOVE_PHOTO;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_SAVE_IMAGE;
import static io.taptalk.TapTalk.Model.TAPAttachmentModel.SELECT_SET_AS_MAIN;

public class TAPAttachmentBottomSheet extends BottomSheetDialogFragment {

    private String instanceKey = "";
    private int imagePosition = -1;
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

    public TAPAttachmentBottomSheet(String instanceKey, int imagePosition, TAPAttachmentListener attachmentListener) {
        this.instanceKey = instanceKey;
        this.attachmentListener = attachmentListener;
        this.imagePosition = imagePosition;
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

        List<TAPAttachmentModel> attachmentList;
        if (imagePosition != -1) {
            attachmentList = createImageOptionsMenu(instanceKey);
            recyclerView.setAdapter(new TAPAttachmentAdapter(instanceKey, imagePosition, attachmentList, attachmentListener, onClickListener));
        } else {
            if (isImagePickerBottomSheet) {
                attachmentList = createImagePickerMenu(instanceKey);
            } else {
                attachmentList = createAttachMenu(instanceKey);
            }
            recyclerView.setAdapter(new TAPAttachmentAdapter(instanceKey, attachmentList, attachmentListener, onClickListener));
        }
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

    private List<TAPAttachmentModel> createAttachMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();
        // TODO: 31 January 2019 TEMPORARILY DISABLED AUDIO AND CONTACT FROM ATTACHMENT

        if (!TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled()) {
            // Attach document
            imageResIds.add(R.drawable.tap_ic_documents_white);
            titleResIds.add(R.string.tap_document);
            ids.add(ATTACH_DOCUMENT);
        }

        if (!TapUI.getInstance(instanceKey).isCameraAttachmentDisabled()) {
            // Attach from camera
            imageResIds.add(R.drawable.tap_ic_camera_orange);
            titleResIds.add(R.string.tap_camera);
            ids.add(ATTACH_CAMERA);
        }

        if (!TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled()) {
            // Attach from gallery
            imageResIds.add(R.drawable.tap_ic_gallery_orange);
            titleResIds.add(R.string.tap_gallery);
            ids.add(ATTACH_GALLERY);
        }

//        imageResIds.add(R.drawable.tap_ic_audio_pumpkin_orange);
//        titleResIds.add(R.string.audio);
//        ids.add(ATTACH_AUDIO);

        if (Places.isInitialized() && !TapUI.getInstance(instanceKey).isLocationAttachmentDisabled()) {
            // Attach location
            imageResIds.add(R.drawable.tap_ic_location_orange);
            titleResIds.add(R.string.tap_location);
            ids.add(ATTACH_LOCATION);
        }

//        imageResIds.add(R.drawable.tap_ic_contact_pumpkin_orange);
//        titleResIds.add(R.string.contact);
//        ids.add(ATTACH_CONTACT);

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }

        return attachMenus;
    }

    private List<TAPAttachmentModel> createImagePickerMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (!TapUI.getInstance(instanceKey).isCameraAttachmentDisabled()) {
            // Take picture from camera
            imageResIds.add(R.drawable.tap_ic_camera_orange);
            titleResIds.add(R.string.tap_camera);
            ids.add(SELECT_PICTURE_CAMERA);
        }

        if (!TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled()) {
            // Pick image from gallery
            imageResIds.add(R.drawable.tap_ic_gallery_orange);
            titleResIds.add(R.string.tap_gallery);
            ids.add(SELECT_PICTURE_GALLERY);
        }

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }

    private List<TAPAttachmentModel> createImageOptionsMenu(String instanceKey) {
        List<Integer> imageResIds = new ArrayList<>(), titleResIds = new ArrayList<>(), ids = new ArrayList<>();

        if (imagePosition != 0) {
            imageResIds.add(R.drawable.tap_ic_gallery_orange);
            titleResIds.add(R.string.tap_set_as_main_photo);
            ids.add(SELECT_SET_AS_MAIN);
        }

        imageResIds.add(R.drawable.tap_ic_download_orange);
        titleResIds.add(R.string.tap_save_image);
        ids.add(SELECT_SAVE_IMAGE);

        imageResIds.add(R.drawable.tap_ic_delete_red);
        titleResIds.add(R.string.tap_remove_photo);
        ids.add(SELECT_REMOVE_PHOTO);

        List<TAPAttachmentModel> attachMenus = new ArrayList<>();
        int size = imageResIds.size();
        for (int index = 0; index < size; index++) {
            attachMenus.add(new TAPAttachmentModel(imageResIds.get(index), titleResIds.get(index), ids.get(index)));
        }
        return attachMenus;
    }
}
