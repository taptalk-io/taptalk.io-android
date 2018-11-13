package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.TAPHorizontalDecoration;
import com.moselo.HomingPigeon.Helper.TAPUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Model.HpImageURL;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.HpContactListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpGroupViewModel;

import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_IMAGE;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_MEMBERS;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.GROUP_NAME;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.Extras.MY_ID;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.GROUP_MEMBER_LIMIT;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE;
import static com.moselo.HomingPigeon.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE;

public class HpGroupSubjectActivity extends HpBaseActivity {

    private ImageView ivButtonBack, ivCamera;
    private CircleImageView civGroupImage;
    private TextView tvTitle, tvMemberCount;
    private EditText etGroupName;
    private Button btnCreateGroup;
    private RecyclerView rvGroupMembers;

    private HpContactListAdapter adapter;
    private HpGroupViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_activity_group_subject);

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
                        HpImageURL groupImage = new HpImageURL();
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
                case PERMISSION_READ_EXTERNAL_STORAGE:
                    TAPUtils.getInstance().pickImageFromGallery(HpGroupSubjectActivity.this, PICK_GROUP_IMAGE);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (null != vm.getGroupData().getRoomName())
            intent.putExtra(GROUP_NAME, vm.getGroupData().getRoomName());
        if (null != vm.getGroupData().getRoomImage())
            intent.putExtra(GROUP_IMAGE, vm.getGroupData().getRoomImage());
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpGroupViewModel.class);
        vm.setMyID(getIntent().getStringExtra(MY_ID));
        vm.getGroupData().setGroupParticipants(getIntent().getParcelableArrayListExtra(GROUP_MEMBERS));
        vm.getGroupData().setRoomName(getIntent().getStringExtra(GROUP_NAME));
        vm.getGroupData().setRoomImage(getIntent().getParcelableExtra(GROUP_IMAGE));
    }

    @Override
    protected void initView() {
        ivButtonBack = findViewById(R.id.iv_button_back);
        ivCamera = findViewById(R.id.iv_camera);
        civGroupImage = findViewById(R.id.civ_group_image);
        tvTitle = findViewById(R.id.tv_title);
        tvMemberCount = findViewById(R.id.tv_member_count);
        etGroupName = findViewById(R.id.et_group_name);
        btnCreateGroup = findViewById(R.id.btn_create_group);
        rvGroupMembers = findViewById(R.id.rv_group_members);

        etGroupName.addTextChangedListener(groupNameWatcher);

        adapter = new HpContactListAdapter(HpContactListAdapter.SELECTED_MEMBER, vm.getGroupData().getGroupParticipants());
        rvGroupMembers.setAdapter(adapter);
        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGroupMembers.addItemDecoration(new TAPHorizontalDecoration(0, 0,
                0, TAPUtils.getInstance().dpToPx(16), adapter.getItemCount(),
                0, 0));
        OverScrollDecoratorHelper.setUpOverScroll(rvGroupMembers, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        tvMemberCount.setText(String.format(getString(R.string.group_member_count), adapter.getItemCount(), GROUP_MEMBER_LIMIT));
        btnCreateGroup.setBackgroundResource(R.drawable.hp_bg_d9d9d9_rounded_6dp);
        loadGroupName();
        loadGroupImage();

        ivButtonBack.setOnClickListener(v -> onBackPressed());
        civGroupImage.setOnClickListener(v -> TAPUtils.getInstance().pickImageFromGallery(HpGroupSubjectActivity.this, PICK_GROUP_IMAGE));
        btnCreateGroup.setOnClickListener(v -> validateAndCreateGroup());
    }

    private void loadGroupName() {
        if (null == vm.getGroupData().getRoomName()) return;
        etGroupName.setText(vm.getGroupData().getRoomName());
    }

    private void loadGroupImage() {
        if (null == vm.getGroupData().getRoomImage()) return;
        GlideApp.with(this).load(vm.getGroupData().getRoomImage().getThumbnail()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                civGroupImage.setImageResource(R.drawable.hp_bg_circle_d9d9d9);
                Toast.makeText(HpGroupSubjectActivity.this, R.string.failed_to_load_image, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ivCamera.setVisibility(View.GONE);
                return false;
            }
        }).into(civGroupImage);

    }

    private void validateAndCreateGroup() {
        String groupName = etGroupName.getText().toString();
        if (!groupName.trim().isEmpty() && null != vm.getGroupData().getGroupParticipants() && vm.getGroupData().getGroupParticipants().size() > 0) {
            // TODO: 19 September 2018 CREATE GROUP
            Intent intent = new Intent(this, HpProfileActivity.class);
            intent.putExtra(K_ROOM, vm.getGroupData());
            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, R.string.error_message_group_name_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher groupNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                btnCreateGroup.setBackgroundResource(R.drawable.hp_bg_aquamarine_tealish_stroke_greenblue_1dp_rounded_6dp);
                vm.getGroupData().setRoomName(s.toString());
            } else {
                btnCreateGroup.setBackgroundResource(R.drawable.hp_bg_d9d9d9_rounded_6dp);
                vm.getGroupData().setRoomName("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
