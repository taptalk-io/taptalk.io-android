package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TapSelectedGroupMemberAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.ViewModel.TAPGroupViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_ACTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBER_IDS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.CREATE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.EDIT_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_SELECTED_GROUP_MEMBER;

public class TAPEditGroupSubjectActivity extends TAPBaseActivity {

    private ConstraintLayout clActionBar, clSelectedMembers;
    private FrameLayout flRemoveGroupPicture, flButtonCreateGroup, flButtonUpdateGroup;
    private LinearLayout llChangeGroupPicture;
    private ImageView ivButtonBack, ivButtonClose, ivGroupPicBackground, ivLoadingProgressCreateGroup, ivLoadingProgressUpdateGroup;
    private CircleImageView civGroupImage;
    private TextView tvTitle, tvGroupPictureLabel, tvMemberCount, tvButtonCreateGroup, tvButtonUpdateGroup;
    private EditText etGroupName;
    private RecyclerView rvGroupMembers;
    private ScrollView svGroupSubject;

    private TapSelectedGroupMemberAdapter adapter;
    private TAPGroupViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_edit_group_subject);

        initViewModel();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_GROUP_IMAGE_CAMERA:
                case PICK_GROUP_IMAGE_GALLERY:
                    if (requestCode == PICK_GROUP_IMAGE_GALLERY && null == data.getData()) {
                        return;
                    } else if (requestCode == PICK_GROUP_IMAGE_GALLERY) {
                        vm.setRoomImageUri(data.getData());
                    }

                    vm.setGroupPictureChanged(true);

                    if (vm.getGroupAction() == EDIT_GROUP) {
                        loadGroupImage(vm.getRoomImageUri().toString());
                        checkEditButtonAvailable();
                    } else {
                        TAPImageURL groupImage = new TAPImageURL();
                        groupImage.setThumbnail(vm.getRoomImageUri().toString());
                        groupImage.setFullsize(vm.getRoomImageUri().toString());
                        vm.getGroupData().setRoomImage(groupImage);
                        loadGroupImage();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                case PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA:
                    vm.setRoomImageUri(TAPUtils.getInstance().takePicture(TAPEditGroupSubjectActivity.this, PICK_GROUP_IMAGE_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.getInstance().pickImageFromGallery(TAPEditGroupSubjectActivity.this, PICK_GROUP_IMAGE_GALLERY, false);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (vm.isLoading()) {
            return;
        }
        if (vm.getGroupAction() == EDIT_GROUP) {
            super.onBackPressed();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
        } else {
            Intent intent = new Intent();
            if (null != vm.getGroupData().getRoomName()) {
                intent.putExtra(GROUP_NAME, vm.getGroupData().getRoomName());
            }
            if (null != vm.getGroupData().getRoomImage()) {
                intent.putExtra(GROUP_IMAGE, vm.getGroupData().getRoomImage());
            }
            if (null != vm.getRoomImageUri()) {
                intent.putExtra(URI, vm.getRoomImageUri());
            }
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPGroupViewModel.class);

        vm.setGroupAction(getIntent().getIntExtra(GROUP_ACTION, CREATE_GROUP));

        if (vm.getGroupAction() == EDIT_GROUP) {
            // Edit group
            vm.setGroupData(getIntent().getParcelableExtra(ROOM));
            if (null == vm.getGroupData().getRoomImage() || vm.getGroupData().getRoomImage().getThumbnail().isEmpty()) {
                vm.setGroupPictureStartsEmpty(true);
            }
        } else {
            // Create group
            vm.getGroupData().setRoomName(getIntent().getStringExtra(GROUP_NAME));
            vm.getGroupData().setRoomImage(getIntent().getParcelableExtra(GROUP_IMAGE));
            vm.setRoomImageUri(getIntent().getParcelableExtra(URI));
            vm.setMyID(getIntent().getStringExtra(MY_ID));
            vm.setParticipantsIDs(getIntent().getStringArrayListExtra(GROUP_MEMBER_IDS));
            vm.setGroupPictureStartsEmpty(true);
            List<TAPUserModel> groupParticipants = getIntent().getParcelableArrayListExtra(GROUP_MEMBERS);
            vm.getGroupData().setGroupParticipants(groupParticipants);
            List<TapContactListModel> contactListModels = new ArrayList<>();
            for (TAPUserModel user : groupParticipants) {
                contactListModels.add(new TapContactListModel(user, TYPE_SELECTED_GROUP_MEMBER));
            }
            vm.setAdapterItems(contactListModels);
        }
    }

    private void initView() {
        clActionBar = findViewById(R.id.cl_action_bar);
        clSelectedMembers = findViewById(R.id.cl_selected_members);
        flRemoveGroupPicture = findViewById(R.id.fl_remove_group_picture);
        flButtonCreateGroup = findViewById(R.id.fl_button_create_group);
        flButtonUpdateGroup = findViewById(R.id.fl_button_update_group);
        llChangeGroupPicture = findViewById(R.id.ll_change_group_picture);
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivLoadingProgressCreateGroup = findViewById(R.id.iv_loading_progress_create_group);
        ivLoadingProgressUpdateGroup = findViewById(R.id.iv_loading_progress_update_group);
        ivGroupPicBackground = findViewById(R.id.iv_group_pic_background);
        civGroupImage = findViewById(R.id.civ_group_image);
        tvTitle = findViewById(R.id.tv_title);
        tvGroupPictureLabel = findViewById(R.id.tv_group_picture_label);
        tvButtonCreateGroup = findViewById(R.id.tv_button_create_group);
        tvButtonUpdateGroup = findViewById(R.id.tv_button_update_group);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etGroupName = findViewById(R.id.et_group_name);
        rvGroupMembers = findViewById(R.id.rv_group_members);
        svGroupSubject = findViewById(R.id.sv_group_subject);

        etGroupName.setOnFocusChangeListener(focusListener);

        if (vm.getGroupAction() == EDIT_GROUP) {
            // Show edit group layout
            tvTitle.setText(R.string.tap_edit_group);
            ivButtonBack.setVisibility(View.INVISIBLE);
            clSelectedMembers.setVisibility(View.GONE);
            etGroupName.addTextChangedListener(editGroupNameWatcher);

            ivButtonClose.setOnClickListener(v -> onBackPressed());
            flButtonUpdateGroup.setOnClickListener(v -> validateAndEditGroupDetails());
            loadGroupImage(null == vm.getGroupData().getRoomImage() ? "" : vm.getGroupData().getRoomImage().getThumbnail());
        } else {
            // Show create group layout
            tvTitle.setText(R.string.tap_group_subject);
            ivButtonClose.setVisibility(View.INVISIBLE);
            flButtonUpdateGroup.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flButtonCreateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_inactive_ripple));
            } else {
                flButtonCreateGroup.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive));
            }
            etGroupName.addTextChangedListener(createGroupNameWatcher);

            adapter = new TapSelectedGroupMemberAdapter(vm.getAdapterItems());
            tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), adapter.getItemCount(), TAPGroupManager.Companion.getGetInstance().getGroupMaxParticipants()));
            rvGroupMembers.setAdapter(adapter);
            rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvGroupMembers.addItemDecoration(new TAPHorizontalDecoration(0, 0,
                    0, TAPUtils.getInstance().dpToPx(16), adapter.getItemCount(),
                    0, 0));
            OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

            ivButtonBack.setOnClickListener(v -> onBackPressed());
            flButtonCreateGroup.setOnClickListener(v -> validateAndCreateGroup());
            loadGroupImage();
        }

        if (null != vm.getGroupData().getRoomName()) {
            etGroupName.setText(vm.getGroupData().getRoomName());
        }

        civGroupImage.setOnClickListener(v -> showProfilePicturePickerBottomSheet());
        llChangeGroupPicture.setOnClickListener(v -> showProfilePicturePickerBottomSheet());
        flRemoveGroupPicture.setOnClickListener(v -> removeGroupPicture());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            flButtonCreateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_inactive_ripple));
            flButtonUpdateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_inactive_ripple));
            svGroupSubject.getViewTreeObserver().addOnScrollChangedListener(toolbarScrollListener);
        }
    }

    private void loadGroupImage() {
        if (null == vm.getGroupData().getRoomImage()) {
            return;
        }
        Glide.with(this).load(vm.getGroupData().getRoomImage().getThumbnail()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                civGroupImage.setImageDrawable(ContextCompat.getDrawable(TAPEditGroupSubjectActivity.this, R.drawable.tap_bg_circle_9b9b9b));
                Toast.makeText(TAPEditGroupSubjectActivity.this, R.string.tap_failed_to_load_image, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //fl_remove_group_picture.visibility = View.VISIBLE
                return false;
            }
        }).into(civGroupImage);
    }

    private void loadGroupImage(String imageUrl) {
        if (null == imageUrl || imageUrl.isEmpty()) {
            ImageViewCompat.setImageTintList(civGroupImage, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getGroupData().getRoomName())));
            civGroupImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_9b9b9b));
            tvGroupPictureLabel.setText(TAPUtils.getInstance().getInitials(vm.getGroupData().getRoomName(), 1));
            tvGroupPictureLabel.setVisibility(View.VISIBLE);
        } else {
            Glide.with(this).load(imageUrl).into(civGroupImage);
            ImageViewCompat.setImageTintList(civGroupImage, null);
            tvGroupPictureLabel.setVisibility(View.GONE);
            //fl_remove_group_picture.visibility = View.VISIBLE
        }
    }

    private void showProfilePicturePickerBottomSheet() {
        TAPUtils.getInstance().dismissKeyboard(this);
        TAPUtils.getInstance().animateClickButton(llChangeGroupPicture, 0.95f);
        new TAPAttachmentBottomSheet(true, profilePicturePickerListener).show(getSupportFragmentManager(), "");
    }

    private void validateAndCreateGroup() {
        String groupName = etGroupName.getText().toString().trim();
        if (!groupName.trim().isEmpty() && null != vm.getGroupData().getGroupParticipants() && vm.getGroupData().getGroupParticipants().size() > 0) {
            TAPDataManager.getInstance().createGroupChatRoom(groupName, vm.getParticipantsIDs(), createGroupRoomView);
        } else {
            Toast.makeText(this, R.string.tap_error_message_group_name_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEditButtonAvailable() {
        if ((!vm.isGroupPictureChanged() && !vm.isGroupNameChanged()) || etGroupName.getText().toString().trim().length() == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flButtonUpdateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_inactive_ripple));
            } else {
                flButtonUpdateGroup.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flButtonUpdateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
            } else {
                flButtonUpdateGroup.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active));
            }
        }
    }

    private void validateAndEditGroupDetails() {
        if (!vm.isGroupPictureChanged() && !vm.isGroupNameChanged()) {
            return;
        }
        String groupName = etGroupName.getText().toString().trim();
        if (groupName.trim().isEmpty()) {
            Toast.makeText(this, R.string.tap_error_message_group_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        vm.getGroupData().setRoomName(groupName);
        if (vm.isGroupNameChanged()) {
            callUpdateGroupChatAPI();
        } else if (vm.isGroupPictureChanged()) {
            callUpdateGroupPictureAPI();
        }
    }

    private void callUpdateGroupChatAPI() {
        TAPDataManager.getInstance().updateChatRoom(vm.getGroupData().getRoomID(), etGroupName.getText().toString(), updateRoomDataView);
    }

    private void callUpdateGroupPictureAPI() {
        TAPFileUploadManager.getInstance().uploadRoomPicture(this, vm.getRoomImageUri(), vm.getGroupData().getRoomID(), changeGroupPictureView);
    }

    private void removeGroupPicture() {
        vm.getGroupData().setRoomImage(null);

        if (vm.getGroupAction() == EDIT_GROUP) {
            ImageViewCompat.setImageTintList(civGroupImage, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(vm.getGroupData().getRoomName())));
            civGroupImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_9b9b9b));
            tvGroupPictureLabel.setText(TAPUtils.getInstance().getInitials(vm.getGroupData().getRoomName(), 1));
            tvGroupPictureLabel.setVisibility(View.VISIBLE);
            checkEditButtonAvailable();
        } else {
            ImageViewCompat.setImageTintList(civGroupImage, null);
            civGroupImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_img_default_group_avatar));
        }

        flRemoveGroupPicture.setVisibility(View.GONE);
        vm.setGroupPictureChanged(!vm.isGroupPictureStartsEmpty());

    }

    private void showCreateGroupButtonLoading() {
        vm.setLoading(true);
        runOnUiThread(() -> {
            tvButtonCreateGroup.setVisibility(View.GONE);
            ivLoadingProgressCreateGroup.setVisibility(View.VISIBLE);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivLoadingProgressCreateGroup);
        });
    }

    private void hideCreateGroupButtonLoading() {
        vm.setLoading(false);
        runOnUiThread(() -> {
            tvButtonUpdateGroup.setVisibility(View.VISIBLE);
            ivLoadingProgressUpdateGroup.setVisibility(View.GONE);
            TAPUtils.getInstance().stopViewAnimation(ivLoadingProgressUpdateGroup);
        });
    }

    private void showUpdateGroupButtonLoading() {
        vm.setLoading(true);
        runOnUiThread(() -> {
            tvButtonUpdateGroup.setVisibility(View.GONE);
            ivLoadingProgressUpdateGroup.setVisibility(View.VISIBLE);
            TAPUtils.getInstance().rotateAnimateInfinitely(this, ivLoadingProgressUpdateGroup);
        });
    }

    private void hideUpdateGroupButtonLoading() {
        vm.setLoading(false);
        runOnUiThread(() -> {
            tvButtonCreateGroup.setVisibility(View.VISIBLE);
            ivLoadingProgressCreateGroup.setVisibility(View.GONE);
            TAPUtils.getInstance().stopViewAnimation(ivLoadingProgressCreateGroup);
        });
    }

    private void openChatGroupProfile() {
        Intent intent = new Intent(this, TAPChatProfileActivity.class);
        intent.putExtra(ROOM, vm.getGroupData());
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void openChatRoom() {
        TAPUtils.getInstance().startChatActivity(this, vm.getGroupData());
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void finishGroupUpdate() {
        Intent intent = new Intent();
        intent.putExtra(ROOM, vm.getGroupData());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
    }

    private TextWatcher createGroupNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0 && s.toString().trim().length() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    flButtonCreateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_active_ripple));
                } else {
                    flButtonCreateGroup.setBackground(ContextCompat.getDrawable(TAPEditGroupSubjectActivity.this, R.drawable.tap_bg_button_active));
                }
                vm.getGroupData().setRoomName(s.toString());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    flButtonCreateGroup.setBackground(getDrawable(R.drawable.tap_bg_button_inactive_ripple));
                } else {
                    flButtonCreateGroup.setBackground(ContextCompat.getDrawable(TAPEditGroupSubjectActivity.this, R.drawable.tap_bg_button_inactive));
                }
                vm.getGroupData().setRoomName("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher editGroupNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0 && !s.toString().equals(vm.getGroupData().getRoomName())) {
                vm.setGroupNameChanged(true);
            } else if (s.length() > 0 && s.toString().equals(vm.getGroupData().getRoomName())) {
                vm.setGroupNameChanged(false);
            }
            checkEditButtonAvailable();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
        if (hasFocus) {
            etGroupName.setBackground(getResources().getDrawable(R.drawable.tap_bg_text_field_active));
        } else {
            etGroupName.setBackground(getResources().getDrawable(R.drawable.tap_bg_text_field_inactive));
        }
    };

    private ViewTreeObserver.OnScrollChangedListener toolbarScrollListener = () -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int y = svGroupSubject.getScrollY();
            int x = ivGroupPicBackground.getHeight();

            if (y == 0) {
                clActionBar.setElevation(0);
            } else if (y < x) {
                clActionBar.setElevation(TAPUtils.getInstance().dpToPx(1));
            } else {
                clActionBar.setElevation(TAPUtils.getInstance().dpToPx(2));
            }
        }
    };

    private TAPAttachmentListener profilePicturePickerListener = new TAPAttachmentListener() {
        @Override
        public void onCameraSelected() {
            vm.setRoomImageUri(TAPUtils.getInstance().takePicture(TAPEditGroupSubjectActivity.this, PICK_GROUP_IMAGE_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            TAPUtils.getInstance().pickImageFromGallery(TAPEditGroupSubjectActivity.this, PICK_GROUP_IMAGE_GALLERY, false);
        }
    };

    private TAPDefaultDataView<TAPCreateRoomResponse> createGroupRoomView = new TAPDefaultDataView<TAPCreateRoomResponse>() {
        @Override
        public void startLoading() {
            showCreateGroupButtonLoading();
        }

        @Override
        public void onSuccess(TAPCreateRoomResponse response) {
            updateGroupData(response);

            if (null != vm.getRoomImageUri()) {
                callUpdateGroupPictureAPI();
            } else {
                hideCreateGroupButtonLoading();
                openChatRoom();
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideCreateGroupButtonLoading();
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            hideCreateGroupButtonLoading();
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        private void updateGroupData(TAPCreateRoomResponse response) {
            vm.setGroupData(response.getRoom());
            vm.getGroupData().setGroupParticipants(response.getParticipants());
            vm.getGroupData().setAdmins(response.getAdmins());
            TAPGroupManager.Companion.getGetInstance().addGroupData(vm.getGroupData());
        }
    };

    private TAPDefaultDataView<TAPUpdateRoomResponse> updateRoomDataView = new TAPDefaultDataView<TAPUpdateRoomResponse>() {
        @Override
        public void startLoading() {
            showUpdateGroupButtonLoading();
        }

        @Override
        public void onSuccess(TAPUpdateRoomResponse response) {
            if (null == vm.getRoomImageUri() && null != response.getRoom()) {
                hideUpdateGroupButtonLoading();
                vm.getGroupData().setRoomName(response.getRoom().getRoomName());
                TAPGroupManager.Companion.getGetInstance().updateRoomDataNameAndImage(response.getRoom());
                finishGroupUpdate();
            } else {
                callUpdateGroupPictureAPI();
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }
    };

    private TAPDefaultDataView<TAPUpdateRoomResponse> changeGroupPictureView = new TAPDefaultDataView<TAPUpdateRoomResponse>() {
        @Override
        public void startLoading() {
            if (vm.getGroupAction() == EDIT_GROUP) {
                showUpdateGroupButtonLoading();
            }
        }

        @Override
        public void onSuccess(TAPUpdateRoomResponse response) {
            if (null == response.getRoom()) {
                return;
            }
            vm.getGroupData().setRoomImage(response.getRoom().getRoomImage());
            TAPGroupManager.Companion.getGetInstance().updateGroupDataFromResponse(response);

            if (vm.getGroupAction() == EDIT_GROUP) {
                hideUpdateGroupButtonLoading();
                finishGroupUpdate();
            } else {
                hideCreateGroupButtonLoading();
                openChatRoom();
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            hideCreateGroupButtonLoading();
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            hideCreateGroupButtonLoading();
            new TapTalkDialog.Builder(TAPEditGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show();
        }
    };
}
