package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Manager.TAPGroupManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPUpdateRoomResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.ViewModel.TAPEditGroupViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_edit_group.*

class TAPEditGroupActivity : TAPBaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close_btn -> {
                onBackPressed()
            }

            R.id.fl_remove_group_picture -> {
                groupViewModel?.groupData?.roomImage = null
                civ_group_picture.setImageResource(R.drawable.tap_img_default_avatar)
                fl_remove_group_picture.visibility = View.GONE
                groupViewModel?.isGroupPicChanged = groupViewModel?.isGroupPicStartEmpty != true
                showingButton()
            }

            R.id.ll_change_group_picture -> {
                TAPUtils.getInstance().animateClickButton(v, 0.95f)
                TAPUtils.getInstance().pickImageFromGallery(this, PICK_GROUP_IMAGE, false)
            }

            R.id.fl_update_group_btn -> {
                if (groupViewModel?.isGroupPicChanged == true || groupViewModel?.isGroupNameChanged == true) {
                    groupViewModel?.groupData?.roomName = et_group_name.text.toString()
                    //TODO() API EDIT CALL
                    //Ini buat ngecek kalau dy cuman ubah gambar lgsg panggil api upload room pic aja
                    if (groupViewModel?.isGroupNameChanged == true)
                        TAPDataManager.getInstance().updateChatRoom(groupViewModel?.groupData?.roomID, et_group_name.text.toString(), updateRoomDataView)
                    else if (groupViewModel?.isGroupPicChanged == true) {
                        TAPFileUploadManager.getInstance().uploadRoomPicture(this@TAPEditGroupActivity,
                                groupViewModel?.groupPicUri, groupViewModel?.groupData?.roomID
                                ?: "0", uploadGroupView)
                    }
                }
            }
        }
    }

    var groupViewModel: TAPEditGroupViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_edit_group)
        initViewModel()
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PICK_GROUP_IMAGE -> {
                        groupViewModel?.isGroupPicChanged = true
                        groupViewModel?.groupPicUri = data?.data
                        loadImage(data?.data.toString())
                        showingButton()
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPEditGroupViewModel::class.java)
        groupViewModel?.groupData = intent.getParcelableExtra(ROOM)
    }

    private fun initView() {
        et_group_name.onFocusChangeListener = groupNameFocusListener
        et_group_name.addTextChangedListener(groupNameWatcher)

        et_group_name.setText(groupViewModel?.groupData?.roomName ?: "")

        if (null != groupViewModel?.groupData?.roomImage && "" != groupViewModel?.groupData?.roomImage?.thumbnail) {
            val imageURL = groupViewModel?.groupData?.roomImage
            loadImage(imageURL?.thumbnail ?: "")
        } else {
            groupViewModel?.isGroupPicStartEmpty = true
        }

        //fl_remove_group_picture.setOnClickListener(this)
        ll_change_group_picture.setOnClickListener(this)
        fl_update_group_btn.setOnClickListener(this)
        iv_close_btn.setOnClickListener(this)

        fl_update_group_btn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple)
    }

    private val groupNameWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.isNotEmpty() == true && s.toString() != groupViewModel?.groupData?.roomName ?: "") {
                groupViewModel?.isGroupNameChanged = true
            } else if (s?.isNotEmpty() == true && s.toString() == groupViewModel?.groupData?.roomName ?: "") {
                groupViewModel?.isGroupNameChanged = false
            }

            showingButton()
        }
    }

    private val groupNameFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        when (hasFocus) {
            true -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_text_field_active)
            else -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_text_field_inactive)
        }
    }

    private fun loadImage(imageURL: String) {
        Glide.with(this).load(imageURL)
                .apply(RequestOptions().centerCrop()).into(civ_group_picture)
        //fl_remove_group_picture.visibility = View.VISIBLE
    }

    private fun showingButton() {
        if (groupViewModel?.isGroupPicChanged == false && groupViewModel?.isGroupNameChanged == false) {
            fl_update_group_btn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple)
        } else {
            fl_update_group_btn.setBackgroundResource(R.drawable.tap_bg_button_active_ripple)
        }
    }

    private fun btnStartLoadingState() {
        runOnUiThread {
            tv_update_group_btn.visibility = View.GONE
            iv_loading_progress_update_group.visibility = View.VISIBLE
            TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_loading_progress_update_group)
        }
    }

    private fun btnStopLoadingState() {
        runOnUiThread {
            tv_update_group_btn.visibility = View.VISIBLE
            iv_loading_progress_update_group.visibility = View.GONE
            TAPUtils.getInstance().stopViewAnimation(iv_loading_progress_update_group)
        }
    }

    private val updateRoomDataView = object : TAPDefaultDataView<TAPUpdateRoomResponse>() {
        override fun startLoading() {
            btnStartLoadingState()
        }

        override fun onSuccess(response: TAPUpdateRoomResponse?) {
            //TODO() Apus Setelah ada API CALL / Flow yang pasti
            if (null == groupViewModel?.groupPicUri) {
                btnStopLoadingState()
                groupViewModel?.groupData?.roomName = response?.room?.roomName
                if (null != response && null != response.room)
                    TAPGroupManager.getInstance.updateRoomDataNameAndImage(response.room!!)

                finishGroupUpdate()
            } else {
                TAPFileUploadManager.getInstance().uploadRoomPicture(this@TAPEditGroupActivity,
                        groupViewModel?.groupPicUri, groupViewModel?.groupData?.roomID
                        ?: "0", uploadGroupView)
            }
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            btnStopLoadingState()
            TapTalkDialog.Builder(this@TAPEditGroupActivity)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error!!.message)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(true) { }
                    .setCancelable(true)
                    .show()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            btnStopLoadingState()
            TapTalkDialog.Builder(this@TAPEditGroupActivity)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener { v -> onBackPressed() }
                    .show()
        }
    }

    private val uploadGroupView = object : TAPDefaultDataView<TAPUpdateRoomResponse>() {
        override fun startLoading() {
            super.startLoading()
            btnStartLoadingState()
        }

        override fun onSuccess(response: TAPUpdateRoomResponse?) {
            super.onSuccess(response)
            btnStopLoadingState()
            groupViewModel?.groupData?.roomImage = response?.room?.roomImage
            if (null != response && null != response.room)
                TAPGroupManager.getInstance.updateRoomDataNameAndImage(response.room!!)
            finishGroupUpdate()
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            btnStopLoadingState()
            TapTalkDialog.Builder(this@TAPEditGroupActivity)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(error!!.message)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(true) { }
                    .setCancelable(true)
                    .show()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            btnStopLoadingState()
            TapTalkDialog.Builder(this@TAPEditGroupActivity)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_error_message_general))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
//                    .setPrimaryButtonListener { v -> onBackPressed() }
                    .show()
        }
    }

    fun finishGroupUpdate() {
        val intent = Intent()
        intent.putExtra(ROOM, groupViewModel?.groupData)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> TAPUtils.getInstance().pickImageFromGallery(this@TAPEditGroupActivity, PICK_GROUP_IMAGE, false)
            }
        }
    }

}
