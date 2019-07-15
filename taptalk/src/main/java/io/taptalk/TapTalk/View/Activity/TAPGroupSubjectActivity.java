package io.taptalk.TapTalk.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPHorizontalDecoration;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPGroupViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_MEMBERSIDs;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MY_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.GROUP_MEMBER_LIMIT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE;

public class TAPGroupSubjectActivity extends TAPBaseActivity {

    private ImageView ivButtonBack, ivGroupPicBackground, ivLoadingProgressCreateGroup;
    private CircleImageView civGroupImage;
    private TextView tvTitle, tvMemberCount, tvCreateGroupBtn;
    private EditText etGroupName;
    private FrameLayout flCreateGroupBtn;
    private RecyclerView rvGroupMembers;
    private LinearLayout llChangeGroupPicture;
    private ConstraintLayout clActionBar;
    private ScrollView svGroupSubject;

    private TAPContactListAdapter adapter;
    private TAPGroupViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_group_subject);

        initViewModel();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case PICK_GROUP_IMAGE:
                        if (null == data.getData()) return;
                        vm.setRoomImageUri(data.getData());
                        TAPImageURL groupImage = new TAPImageURL();
                        groupImage.setThumbnail(data.getData().toString());
                        groupImage.setFullsize(data.getData().toString());
                        vm.getGroupData().setRoomImage(groupImage);
                        loadGroupImage();
                        break;
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.getInstance().pickImageFromGallery(TAPGroupSubjectActivity.this, PICK_GROUP_IMAGE, false);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (null != vm.getGroupData().getRoomName()) {
            intent.putExtra(GROUP_NAME, vm.getGroupData().getRoomName());
        }
        if (null != vm.getGroupData().getRoomImage()) {
            intent.putExtra(GROUP_IMAGE, vm.getGroupData().getRoomImage());
        }
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPGroupViewModel.class);
        vm.setMyID(getIntent().getStringExtra(MY_ID));
        vm.getGroupData().setGroupParticipants(getIntent().getParcelableArrayListExtra(GROUP_MEMBERS));
        vm.setParticipantsIDs(getIntent().getStringArrayListExtra(GROUP_MEMBERSIDs));
        vm.getGroupData().setRoomName(getIntent().getStringExtra(GROUP_NAME));
        vm.getGroupData().setRoomImage(getIntent().getParcelableExtra(GROUP_IMAGE));
    }

    private void initView() {
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivLoadingProgressCreateGroup = findViewById(R.id.iv_loading_progress_create_group);
        ivGroupPicBackground = findViewById(R.id.iv_group_pic_background);
        civGroupImage = findViewById(R.id.civ_group_image);
        tvTitle = findViewById(R.id.tv_title);
        tvCreateGroupBtn = findViewById(R.id.tv_create_group_btn);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etGroupName = findViewById(R.id.et_group_name);
        flCreateGroupBtn = findViewById(R.id.fl_create_group_btn);
        rvGroupMembers = findViewById(R.id.rv_group_members);
        llChangeGroupPicture = findViewById(R.id.ll_change_group_picture);
        svGroupSubject = findViewById(R.id.sv_group_subject);
        clActionBar = findViewById(R.id.cl_action_bar);

        etGroupName.addTextChangedListener(groupNameWatcher);
        etGroupName.setOnFocusChangeListener(focusListener);
        svGroupSubject.getViewTreeObserver().addOnScrollChangedListener(toolbarScrollListener);

        adapter = new TAPContactListAdapter(TAPContactListAdapter.SELECTED_MEMBER, vm.getGroupData().getGroupParticipants());
        rvGroupMembers.setAdapter(adapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGroupMembers.addItemDecoration(new TAPHorizontalDecoration(0, 0,
                0, TAPUtils.getInstance().dpToPx(16), adapter.getItemCount(),
                0, 0));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        tvMemberCount.setText(String.format(getString(R.string.tap_selected_member_count), adapter.getItemCount(), GROUP_MEMBER_LIMIT));
        flCreateGroupBtn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple);
        loadGroupName();
        loadGroupImage();

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        llChangeGroupPicture.setOnClickListener(v -> {
            TAPUtils.getInstance().animateClickButton(llChangeGroupPicture, 0.95f);
            TAPUtils.getInstance().pickImageFromGallery(TAPGroupSubjectActivity.this, PICK_GROUP_IMAGE, false);
        });
        flCreateGroupBtn.setOnClickListener(v -> validateAndCreateGroup());
    }

    private void loadGroupName() {
        if (null == vm.getGroupData().getRoomName()) return;
        etGroupName.setText(vm.getGroupData().getRoomName());
    }

    private void loadGroupImage() {
        if (null == vm.getGroupData().getRoomImage()) return;
        Glide.with(this).load(vm.getGroupData().getRoomImage().getThumbnail()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                civGroupImage.setImageResource(R.drawable.tap_bg_circle_9b9b9b);
                Toast.makeText(TAPGroupSubjectActivity.this, R.string.tap_failed_to_load_image, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(civGroupImage);
    }

    private void validateAndCreateGroup() {
        String groupName = etGroupName.getText().toString();
        if (!groupName.trim().isEmpty() && null != vm.getGroupData().getGroupParticipants() && vm.getGroupData().getGroupParticipants().size() > 0) {
            TAPDataManager.getInstance().createGroupChatRoom(groupName, vm.getParticipantsIDs(), createGroupRoomView);
        } else {
            Toast.makeText(this, R.string.tap_error_message_group_name_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher groupNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                flCreateGroupBtn.setBackgroundResource(R.drawable.tap_bg_button_active_ripple);
                vm.getGroupData().setRoomName(s.toString());
            } else {
                flCreateGroupBtn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple);
                vm.getGroupData().setRoomName("");
            }
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
        int y = svGroupSubject.getScrollY();
        int x = ivGroupPicBackground.getHeight();

        if (y == 0) {
            clActionBar.setElevation(0);
        } else if (y < x) {
            clActionBar.setElevation(TAPUtils.getInstance().dpToPx(1));
        } else {
            clActionBar.setElevation(TAPUtils.getInstance().dpToPx(2));
        }
    };

    private void btnStartLoadingState() {
        tvCreateGroupBtn.setVisibility(View.GONE);
        ivLoadingProgressCreateGroup.setVisibility(View.VISIBLE);
        TAPUtils.getInstance().rotateAnimateInfinitely(this, ivLoadingProgressCreateGroup);
    }

    private void btnStopLoadingState() {
        tvCreateGroupBtn.setVisibility(View.VISIBLE);
        ivLoadingProgressCreateGroup.setVisibility(View.GONE);
        TAPUtils.getInstance().stopViewAnimation(ivLoadingProgressCreateGroup);
    }

    private void updateGroupData(TAPCreateRoomResponse response) {
        vm.setGroupData(response.getRoom());
        vm.getGroupData().setGroupParticipants(response.getParticipants());
        vm.getGroupData().setAdmins(response.getAdmins());
        TAPGroupManager.Companion.getGetInstance().addGroupData(vm.getGroupData());
        //vm.getGroupData().setRoomImage();
    }

    private TAPDefaultDataView<TAPCreateRoomResponse> createGroupRoomView = new TAPDefaultDataView<TAPCreateRoomResponse>() {
        @Override
        public void startLoading() {
            btnStartLoadingState();
        }

        @Override
        public void onSuccess(TAPCreateRoomResponse response) {
            updateGroupData(response);

            if (null != vm.getRoomImageUri()) {
                TAPFileUploadManager.getInstance().uploadRoomPicture(TAPGroupSubjectActivity.this,
                        vm.getRoomImageUri(), vm.getGroupData().getRoomID(), changeGroupPictureView);
            } else {
                btnStopLoadingState();
//                overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
                //openChatGroupProfile();
                openChatRoom();
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            btnStopLoadingState();
            new TapTalkDialog.Builder(TAPGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener(v -> onBackPressed())
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            super.onError(errorMessage);
            btnStopLoadingState();
            new TapTalkDialog.Builder(TAPGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener(v -> onBackPressed())
                    .show();
        }
    };

    private TAPDefaultDataView<TAPUpdateRoomResponse> changeGroupPictureView = new TAPDefaultDataView<TAPUpdateRoomResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
        }

        @Override
        public void onSuccess(TAPUpdateRoomResponse response) {
            super.onSuccess(response);
            vm.getGroupData().setRoomImage(response.getRoom().getRoomImage());
            btnStopLoadingState();
            if (null != response.getRoom())
                TAPGroupManager.Companion.getGetInstance().updateRoomDataNameAndImage(response.getRoom());
            //openChatGroupProfile();
            openChatRoom();
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            btnStopLoadingState();
            new TapTalkDialog.Builder(TAPGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error.getMessage())
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener(v -> onBackPressed())
                    .show();
        }

        @Override
        public void onError(String errorMessage) {
            super.onError(errorMessage);
            btnStopLoadingState();
            new TapTalkDialog.Builder(TAPGroupSubjectActivity.this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener(v -> onBackPressed())
                    .show();
        }
    };

    public void openChatGroupProfile() {
        Intent intent = new Intent(this, TAPChatProfileActivity.class);
        intent.putExtra(ROOM, vm.getGroupData());
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    public void openChatRoom() {
        TAPUtils.getInstance().startChatActivity(this, vm.getGroupData());
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }
}
