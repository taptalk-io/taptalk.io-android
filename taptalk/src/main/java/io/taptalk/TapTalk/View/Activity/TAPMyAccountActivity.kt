package io.taptalk.TapTalk.View.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.BuildConfig
import io.taptalk.TapTalk.Const.TAPDefaultConstant.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.*
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapTalkActionInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener
import io.taptalk.TapTalk.Manager.*
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapGetPhotoListResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.Model.TapPhotosItemModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapProfilePicturePagerAdapter
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel
import kotlinx.android.synthetic.main.tap_activity_my_account.*
import kotlinx.android.synthetic.main.tap_layout_basic_information.*
import kotlinx.android.synthetic.main.tap_layout_popup_loading_screen.*

class TAPMyAccountActivity : TAPBaseActivity() {

    private val indexProfilePicture = 0
    private val indexFullName = 1
    private val indexUsername = 2
    private val indexMobileNumber = 3
    private val indexEmail = 4
    private val indexPassword = 5

    private val stateUnchanged = 0
    private val stateValid = 1
    private val stateInvalid = -1
    private val stateEmpty = -2

    private lateinit var vm: TAPRegisterViewModel
    private var state: ViewState = ViewState.VIEW
    private lateinit var glide: RequestManager
    private lateinit var profilePicturePagerAdapter: TapProfilePicturePagerAdapter

    companion object {
        fun start(
                context: Context,
                instanceKey: String
        ) {
            val intent = Intent(context, TAPMyAccountActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
            }
        }
        enum class ViewState {
            VIEW, EDIT
        }
        const val MAX_PHOTO_SIZE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_my_account)

        glide = Glide.with(this)
        initViewModel()
        profilePicturePagerAdapter = TapProfilePicturePagerAdapter(this, vm.profilePictureList, null)
        initView()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        checkFullNameTimer.cancel()
        checkEmailAddressTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this@TAPMyAccountActivity, uploadBroadcastReceiver)
    }

    override fun onBackPressed() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        } else if (ViewState.EDIT == state) {
            if (vm.myUserModel.bio != et_bio.text.toString()) {
                TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_you_have_unsaved_changes))
                    .setMessage(getString(R.string.tap_unsaved_changes_confirmation))
                    .setCancelable(false)
                    .setPrimaryButtonTitle(getString(R.string.tap_yes))
                    .setPrimaryButtonListener { showViewState() }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setSecondaryButtonListener(true) {}
                    .show()
            } else {
                showViewState()
            }
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_CAMERA_CAMERA, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA -> {
                    vm.profilePictureUri = TAPUtils.takePicture(instanceKey, this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_CAMERA)
                }
                PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> {
                    TAPUtils.pickImageFromGallery(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_GALLERY, false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PICK_PROFILE_IMAGE_CAMERA, PICK_PROFILE_IMAGE_GALLERY -> {
                        if (null != intent?.data) {
                            vm.profilePictureUri = intent.data
                        }
                        reloadProfilePicture(vm.profilePictureUri)
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        vm = ViewModelProvider(this,
                TAPRegisterViewModel.TAPRegisterViewModelFactory(application, instanceKey))
                .get(TAPRegisterViewModel::class.java)
        vm.currentProfilePicture = vm.myUserModel.imageURL.fullsize
        vm.countryFlagUrl = TAPDataManager.getInstance(instanceKey).myCountryFlagUrl
    }

    @SuppressLint("PrivateResource")
    private fun initView() {
        window?.setBackgroundDrawable(null)

        //et_full_name.onFocusChangeListener = fullNameFocusListener
        //et_email_address.onFocusChangeListener = emailAddressFocusListener

        //et_full_name.addTextChangedListener(fullNameWatcher)
        //et_email_address.addTextChangedListener(emailWatcher)
        et_bio.addTextChangedListener(bioWatcher)

        if (TapUI.getInstance(instanceKey).isChangeProfilePictureButtonVisible) {
            tv_edit_profile_picture.visibility = View.VISIBLE
        } else {
            tv_edit_profile_picture.visibility = View.GONE
        }

        // TODO: 25/02/22 handle onError and save list on preference later MU 
        if (vm.currentProfilePicture.isEmpty()) {
            showDefaultProfilePicture()
        } else {
            val profilePictureModel = TapPhotosItemModel()
            profilePictureModel.fullsizeImageURL = vm.currentProfilePicture
            vm.profilePictureList.add(profilePictureModel)
            vp_profile_picture.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack))
            vp_profile_picture.adapter = profilePicturePagerAdapter
            tv_profile_picture_label.visibility = View.GONE
        }

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                    .apply(RequestOptions().placeholder(R.drawable.tap_ic_default_flag))
                    .into(iv_country_flag)
        }
        et_full_name.setText(vm.myUserModel.fullname)
        et_username.setText(vm.myUserModel.username)
        tv_country_code.text = "+" + vm.myUserModel.countryCallingCode
        et_mobile_number.setText(vm.myUserModel.phone)
        et_email_address.setText(vm.myUserModel.email)
        showViewState()
        if (TapUI.getInstance(instanceKey).isEditBioTextFieldVisible ){
            g_bio.visibility = View.VISIBLE
            setProfileInformation(tv_bio_view, g_bio, vm.myUserModel.bio)
            if (!vm.myUserModel.bio.isNullOrEmpty()) {
                et_bio.setText(vm.myUserModel.bio)
            }
        } else {
            g_bio.visibility = View.GONE
        }
        if (vm.myUserModel.phoneWithCode.isNullOrEmpty()) {
            g_mobile_number.visibility = View.GONE
        } else {
            g_mobile_number.visibility = View.VISIBLE
            tv_mobile_number_view.text = "+" + vm.myUserModel.countryCallingCode + " " + vm.myUserModel.phone
        }
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled) {
            btn_blocked_contacts.visibility = View.VISIBLE
        } else {
            btn_blocked_contacts.visibility = View.GONE
        }
        setProfileInformation(tv_username_view, g_username, vm.myUserModel.username)
        setProfileInformation(tv_email_view, g_email, vm.myUserModel.email)
        setTextVersionApp()

        iv_button_close.setOnClickListener { onBackPressed() }
        fl_container.setOnClickListener { clearAllFocus() }
        cl_form_container.setOnClickListener { clearAllFocus() }
        cl_password.setOnClickListener { openChangePasswordPage() }
        cl_logout.setOnClickListener { promptUserLogout() }
        btn_blocked_contacts.setOnClickListener {
            TAPBlockedListActivity.start(this, instanceKey)
        }
        btn_delete_my_account.setOnClickListener {
            TapUI.getInstance(instanceKey).triggerDeleteButtonInMyAccountPageTapped(this)
        }

        // Obtain text field style attributes
        val textFieldArray = obtainStyledAttributes(R.style.tapFormTextFieldStyle, R.styleable.TextAppearance)
        vm.textFieldFontColor = textFieldArray.getColor(R.styleable.TextAppearance_android_textColor, -1)
        vm.textFieldFontColorHint = textFieldArray.getColor(R.styleable.TextAppearance_android_textColorHint, -1)
        textFieldArray.recycle()

        val clickableLabelArray = obtainStyledAttributes(R.style.tapClickableLabelStyle, R.styleable.TextAppearance)
        vm.clickableLabelFontColor = clickableLabelArray.getColor(R.styleable.TextAppearance_android_textColor, -1)
        clickableLabelArray.recycle()

        // TODO temporarily disable editing
        et_full_name.isEnabled = false
        et_username.isEnabled = false
        et_mobile_number.isEnabled = false
        et_email_address.isEnabled = false

        et_full_name.setTextColor(vm.textFieldFontColorHint)
        et_username.setTextColor(vm.textFieldFontColorHint)
        tv_country_code.setTextColor(vm.textFieldFontColorHint)
        et_mobile_number.setTextColor(vm.textFieldFontColorHint)
        et_email_address.setTextColor(vm.textFieldFontColorHint)

        tv_label_password.visibility = View.GONE
        cl_password.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sv_profile.viewTreeObserver.addOnScrollChangedListener(scrollViewListener)
        }
        getPhotoList(null)
    }

    private fun showViewState() {
        state = ViewState.VIEW
        TAPUtils.dismissKeyboard(this)
        tv_title.text = vm.myUserModel.fullname
        tv_edit_profile_picture.visibility = View.VISIBLE
        tv_edit_save_btn.setOnClickListener { showEditState() }
        tv_edit_save_btn.text = getString(R.string.tap_edit)
        tv_edit_profile_picture.text = getString(R.string.tap_set_new_profile_picture)
        tv_edit_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        g_edit.visibility = View.GONE
        cl_basic_info.visibility = View.VISIBLE
        tv_version_code.visibility = View.VISIBLE
        cl_logout.visibility = View.GONE
        btn_delete_my_account.visibility = View.GONE
        et_bio.isEnabled = false
        et_bio.setText(vm.myUserModel.bio)
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled) {
            btn_blocked_contacts.visibility = View.VISIBLE
        }
    }

    private fun showEditState() {
        state = ViewState.EDIT
        tv_title.text = getString(R.string.tap_account_details)
        tv_edit_save_btn.setOnClickListener {
                saveProfile()
             }
        tv_edit_save_btn.text = getString(R.string.tap_save)
        tv_edit_profile_picture.text = getString(R.string.tap_edit_profile_picture)
        tv_edit_profile_picture.setOnClickListener { showProfilePictureOptionsBottomSheet() }
        if (tv_profile_picture_label.visibility == View.VISIBLE) {
            tv_edit_profile_picture.visibility = View.GONE
        } else {
            tv_edit_profile_picture.visibility = View.VISIBLE
        }
        g_edit.visibility = View.VISIBLE
        if (TapUI.getInstance(instanceKey).isEditBioTextFieldVisible) {
            g_bio_fields.visibility = View.VISIBLE
        } else {
            g_bio_fields.visibility = View.GONE
        }
        cl_basic_info.visibility = View.GONE
        tv_version_code.visibility = View.GONE
        if (TapUI.getInstance(instanceKey).isLogoutButtonVisible) {
            cl_logout.visibility = View.VISIBLE
        } else {
            cl_logout.visibility = View.GONE
        }
        if (TapUI.getInstance(instanceKey).isDeleteAccountButtonVisible) {
            btn_delete_my_account.visibility = View.VISIBLE
        } else {
            btn_delete_my_account.visibility = View.GONE
        }
        et_bio.isEnabled = true
        btn_blocked_contacts.visibility = View.GONE
    }

    private fun setProfileInformation(textView: TextView, group: View, textValue: String?) {
        if (textValue.isNullOrEmpty()) {
            group.visibility = View.GONE
        } else {
            group.visibility = View.VISIBLE
            textView.text = textValue
        }
    }

    private fun saveProfile() {
        if (vm.myUserModel.bio != et_bio.text.toString()) {
            TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setTitle(getString(R.string.tap_save_changes_question))
                .setMessage(getString(R.string.tap_save_changes_confirmation))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_save))
                .setPrimaryButtonListener {
                    vm.isUpdatingProfile = true
                    disableEditing()
                    showLoading(getString(R.string.tap_updating))
                    TapCoreContactManager.getInstance(instanceKey).updateActiveUserBio(et_bio.text.toString(), updateBioListener)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        } else {
            showViewState()
        }
    }

    private fun registerBroadcastReceiver() {
        TAPBroadcastManager.register(this@TAPMyAccountActivity, uploadBroadcastReceiver,
                UploadProgressLoading, UploadProgressFinish, UploadFailed)
    }

    private fun showProfilePicturePickerBottomSheet() {
        if (vm.isLoadPhotoFailed) {
            getPhotoListWithDialog()
        } else {
            if (vm.profilePictureList.size >= MAX_PHOTO_SIZE) {
                TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_max_profile_picture_title))
                    .setMessage(getString(R.string.tap_max_profile_picture_message))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(true) { }
                    .setCancelable(true)
                    .show()
            } else {
                TAPUtils.dismissKeyboard(this@TAPMyAccountActivity)
                TAPAttachmentBottomSheet(instanceKey, true, profilePicturePickerListener).show(
                    supportFragmentManager,
                    ""
                )
            }
        }
    }

    private fun showProfilePictureOptionsBottomSheet() {
        if (vm.isLoadPhotoFailed) {
            getPhotoListWithDialog()
        } else {
            TAPUtils.dismissKeyboard(this@TAPMyAccountActivity)
            TAPAttachmentBottomSheet(instanceKey, vp_profile_picture.currentItem, profilePictureOptionListener).show(supportFragmentManager, "")
        }
    }

    private fun showDefaultProfilePicture() {
        vp_profile_picture.setBackgroundColor(TAPUtils.getRandomColor(this@TAPMyAccountActivity, vm.myUserModel.fullname))
        vp_profile_picture.adapter = null
        tab_layout.visibility = View.GONE
        tv_profile_picture_label.text = TAPUtils.getInitials(vm.myUserModel.fullname, 2)
        tv_profile_picture_label.visibility = View.VISIBLE
        if (state == ViewState.EDIT) {
            tv_edit_profile_picture.visibility = View.GONE
        } else {
            tv_edit_profile_picture.visibility = View.VISIBLE
        }
    }

    private fun reloadProfilePicture(imageUri: Uri?) {
        if (null == imageUri) {
            vm.formCheck[indexProfilePicture] = stateEmpty
            Toast.makeText(this@TAPMyAccountActivity, getString(R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
        } else {
            vm.formCheck[indexProfilePicture] = stateValid
            uploadProfilePicture(imageUri)
        }
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (et_full_name.text.toString() == vm.myUserModel.fullname) {
            // Unchanged
            vm.formCheck[indexFullName] = stateUnchanged
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else if (et_full_name.text.isNotEmpty() && et_full_name.text.matches(Regex("[A-Za-z ]*"))) {
            // Valid full name
            vm.formCheck[indexFullName] = stateValid
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else if (et_full_name.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexFullName] = stateEmpty
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else {
            // Invalid full name
            vm.formCheck[indexFullName] = stateInvalid
            tv_label_full_name_error.visibility = View.VISIBLE
            et_full_name.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        }
        checkContinueButtonAvailability()
    }

    private fun checkEmailAddress(hasFocus: Boolean) {
        if (et_email_address.text.toString() == vm.myUserModel.email) {
            // Unchanged
            vm.formCheck[indexEmail] = stateUnchanged
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            // Valid email address
            vm.formCheck[indexEmail] = stateValid
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else if (et_email_address.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexEmail] = stateEmpty
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else {
            // Invalid email address
            vm.formCheck[indexEmail] = stateInvalid
            tv_label_email_address_error.visibility = View.VISIBLE
            et_email_address.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        }
        checkContinueButtonAvailability()
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_inactive)
        }
        if (view == cl_password) {
            if (hasFocus) {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
            } else {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapGreyDc))
            }
        }
    }

    private fun checkContinueButtonAvailability() {
        if ((vm.formCheck[indexFullName] == stateValid || vm.formCheck[indexFullName] == stateUnchanged) &&
                vm.formCheck[indexEmail] != stateInvalid &&
                vm.formCheck[indexPassword] != stateInvalid && (
                        vm.formCheck[indexFullName] != stateUnchanged ||
                                vm.formCheck[indexEmail] != stateUnchanged ||
                                vm.formCheck[indexPassword] != stateUnchanged)) {
            // All forms valid
        } else {
            // No changes / has invalid forms
        }
    }

    private fun clearAllFocus() {
        TAPUtils.dismissKeyboard(this)
        fl_container.clearFocus()
    }

    private fun openChangePasswordPage() {
        // TODO
    }

    private fun promptUserLogout() {
        TapTalkDialog.Builder(this)
                .setTitle(getString(R.string.tap_log_out))
                .setMessage(getString(R.string.tap_log_out_confirmation))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_log_out))
                .setPrimaryButtonListener { TAPDataManager.getInstance(instanceKey).logout(logoutView) }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setSecondaryButtonListener(true) {}
                .show()
    }

    private fun logout() {
//        AnalyticsManager.getInstance(instanceKey).trackEvent("Logout")
        TapTalk.clearAllTapTalkData(instanceKey)
        hideLoading()
//        AnalyticsManager.getInstance(instanceKey).identifyUser()
        for (listener in TapTalk.getTapTalkListeners(instanceKey)) {
            listener.onUserLogout()
        }
        val intent = Intent(CLEAR_ROOM_LIST)
        LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent)
        finish()
    }

    private fun uploadProfilePicture(imageUri: Uri?) {
        vm.isUploadingProfilePicture = true
        disableEditing()
        TAPFileUploadManager.getInstance(instanceKey).uploadProfilePicture(this@TAPMyAccountActivity, imageUri, vm.myUserModel.userID)
    }

    private fun showErrorDialog(message: String?) {
        vm.isUpdatingProfile = false
        enableEditing()
        TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.tap_error))
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(true) { }
                .setCancelable(true)
                .show()
    }

    private fun disableEditing() {
        iv_button_close.setOnClickListener(null)
        tv_edit_profile_picture.isEnabled = false

        iv_button_close.setImageDrawable(ContextCompat.getDrawable(this@TAPMyAccountActivity, R.drawable.tap_ic_loading_progress_circle_white))
        TAPUtils.rotateAnimateInfinitely(this@TAPMyAccountActivity, iv_button_close)

        // TODO temporarily disable editing
//        et_full_name.isEnabled = false
//        et_email_address.isEnabled = false
//
//        et_full_name.setTextColor(resources.getColor(R.color.tap_grey_9b))
//        et_email_address.setTextColor(resources.getColor(R.color.tap_grey_9b))
//
//        tv_button_update.visibility = View.GONE
//        iv_update_progress.visibility = View.VISIBLE
//        TAPUtils.rotateAnimateInfinitely(this@TAPMyAccountActivity, iv_update_progress)
    }

    private fun enableEditing() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        }
        iv_button_close.setOnClickListener { onBackPressed() }
        tv_edit_profile_picture.isEnabled = true

        iv_button_close.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white))
        iv_button_close.clearAnimation()

        // TODO temporarily disable editing
//        et_full_name.isEnabled = true
//        et_email_address.isEnabled = true
//
//        et_full_name.setTextColor(ContextCompat.getColor(this, R.color.tap_black_19))
//        et_email_address.setTextColor(ContextCompat.getColor(this, R.color.tap_black_19))
//
//        tv_button_update.visibility = View.VISIBLE
//        iv_update_progress.visibility = View.GONE
//        iv_update_progress.clearAnimation()
    }

    private val updateBioListener = object : TapCoreGetContactListener() {
        override fun onSuccess(user: TAPUserModel?) {
            super.onSuccess(user)
            vm.isUpdatingProfile = false
            enableEditing()
            hideLoading()
            vm.myUserModel = user
            setProfileInformation(tv_bio_view, g_bio, user?.bio)
            showViewState()
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            super.onError(errorCode, errorMessage)
            hideLoading()
            showErrorDialog(errorMessage.toString())
        }
    }

    private fun saveImage(url: String?, bitmap: Bitmap?) {
        if (url.isNullOrEmpty()) {
            return
        }
        if (!TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE
            )
        } else {
            showLoading(getString(R.string.tap_downloading))
            TAPFileDownloadManager.getInstance(instanceKey).writeImageFileToDisk(
                this,
                System.currentTimeMillis(),
                bitmap,
                MediaType.IMAGE_JPEG,
                saveImageListener
            )
        }
    }

    private fun getPhotoList(loadingText: String?) {
        TAPDataManager.getInstance(instanceKey).getPhotoList(vm.myUserModel.userID, object : TAPDefaultDataView<TapGetPhotoListResponse>() {
            override fun onSuccess(response: TapGetPhotoListResponse?) {
                super.onSuccess(response)
                vm.isLoadPhotoFailed = false
                reloadProfilePicture(response?.photos?.toCollection(ArrayList()), loadingText)
            }

            override fun onError(error: TAPErrorModel?) {
                super.onError(error)
                vm.isLoadPhotoFailed = true
            }

            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                vm.isLoadPhotoFailed = true
            }
        })
    }

    private fun getPhotoListWithDialog() {
        TAPDataManager.getInstance(instanceKey).getPhotoList(vm.myUserModel.userID, object : TAPDefaultDataView<TapGetPhotoListResponse>() {
            override fun startLoading() {
                super.startLoading()
                vm.isUpdatingProfile = true
                disableEditing()
                showLoading(getString(R.string.tap_loading))
            }

            override fun endLoading() {
                super.endLoading()
                vm.isUpdatingProfile = false
                enableEditing()
            }
            override fun onSuccess(response: TapGetPhotoListResponse?) {
                super.onSuccess(response)
                vm.isLoadPhotoFailed = false
                reloadProfilePicture(response?.photos?.toCollection(ArrayList()), null)
            }

            override fun onError(error: TAPErrorModel?) {
                super.onError(error)
                endLoading()
                vm.isLoadPhotoFailed = true
                TapTalkDialog.Builder(this@TAPMyAccountActivity)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_could_not_retrieve_photo))
                    .setCancelable(false)
                    .setPrimaryButtonTitle(getString(R.string.tap_retry))
                    .setPrimaryButtonListener {
                        getPhotoListWithDialog()
                    }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setSecondaryButtonListener(true) {}
                    .show()
            }

            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                endLoading()
                vm.isLoadPhotoFailed = true
                enableEditing()
                TapTalkDialog.Builder(this@TAPMyAccountActivity)
                    .setTitle(getString(R.string.tap_error))
                    .setMessage(getString(R.string.tap_could_not_retrieve_photo))
                    .setCancelable(false)
                    .setPrimaryButtonTitle(getString(R.string.tap_retry))
                    .setPrimaryButtonListener {
                        getPhotoListWithDialog()
                    }
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setSecondaryButtonListener(true) {}
                    .show()
            }
        })
    }

    private fun setMainPhoto(id: Int) {
        vm.isUpdatingProfile = true
        disableEditing()
        TAPDataManager.getInstance(instanceKey).setMainPhoto(id, editPhotoView)
    }

    private fun removePhoto(id: Int, createdTime: Long?) {
        vm.isUpdatingProfile = true
        disableEditing()
        TAPDataManager.getInstance(instanceKey).removePhoto(id, createdTime, editPhotoView)
    }

    private fun reloadProfilePicture(photoList: ArrayList<TapPhotosItemModel>?, loadingText: String?) {
        vm.profilePictureList.clear()
        if (photoList.isNullOrEmpty()) {
            vm.formCheck[indexProfilePicture] = stateEmpty
            showDefaultProfilePicture()
        } else {
            vm.formCheck[indexProfilePicture] = stateValid
            vm.profilePictureList.addAll(photoList)
            vp_profile_picture.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack))
            vp_profile_picture.adapter = profilePicturePagerAdapter
            if (vm.profilePictureList.size > 1) {
                tab_layout.visibility = View.VISIBLE
                tab_layout.setupWithViewPager(vp_profile_picture)
            } else {
                tab_layout.visibility = View.GONE
            }
            tv_profile_picture_label.visibility = View.GONE
        }
        if (loadingText.isNullOrEmpty()) {
            hideLoading()
        } else {
            endLoading(loadingText)
        }
        enableEditing()
    }

    private val editPhotoView = object : TAPDefaultDataView<TAPGetUserResponse>() {

        override fun startLoading() {
            super.startLoading()
            showLoading(getString(R.string.tap_updating))
        }

        override fun onSuccess(response: TAPGetUserResponse?) {
            super.onSuccess(response)
            vm.isUpdatingProfile = false
            TAPDataManager.getInstance(instanceKey).saveActiveUser(response?.user)
            getPhotoList(null)
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            showErrorDialog(errorMessage)
        }
    }

    private val saveImageListener: TapTalkActionInterface = object : TapTalkActionInterface {
        override fun onSuccess(message: String) {
            endLoading(getString(R.string.tap_image_saved))
        }

        override fun onError(errorMessage: String) {
            runOnUiThread {
                hideLoading()
                Toast.makeText(this@TAPMyAccountActivity, errorMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val profilePicturePickerListener = object : TAPAttachmentListener(instanceKey) {
        override fun onCameraSelected() {
            vm.profilePictureUri = TAPUtils.takePicture(instanceKey, this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_CAMERA)
        }

        override fun onGallerySelected() {
            TAPUtils.pickImageFromGallery(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_GALLERY, false)
        }
    }

    private val profilePictureOptionListener = object : TAPAttachmentListener(instanceKey) {
        override fun onSaveImageToGallery(message: TAPMessageModel?) {
            Glide.with(this@TAPMyAccountActivity).asBitmap().load(vm.profilePictureList[vp_profile_picture.currentItem].fullsizeImageURL).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImage(vm.profilePictureList[vp_profile_picture.currentItem].fullsizeImageURL, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) { }
            })
        }

        override fun setAsMain(imagePosition: Int) {
            TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setTitle(getString(R.string.tap_set_as_main_question))
                .setMessage(getString(R.string.tap_set_as_main_confirmation))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_replace))
                .setPrimaryButtonListener {
                    setMainPhoto(vm.profilePictureList[imagePosition].id)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        }

        override fun onImageRemoved(imagePosition: Int) {
            TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setTitle(getString(R.string.tap_remove_photo_question))
                .setMessage(getString(R.string.tap_remove_photo_confirmation))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_remove))
                .setPrimaryButtonListener {
                    removePhoto(vm.profilePictureList[imagePosition].id, vm.profilePictureList[imagePosition].createdTime)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setSecondaryButtonListener(true) {}
                .show()
        }
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = TAPUtils.dpToPx(4).toFloat()
            }
            if (vm.formCheck[indexFullName] != stateInvalid) {
                view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = 0f
            }
            if (vm.formCheck[indexFullName] != stateInvalid) {
                updateEditTextBackground(et_full_name, hasFocus)
            }
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = TAPUtils.dpToPx(4).toFloat()
            }
            if (vm.formCheck[indexEmail] != stateInvalid) {
                view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = 0f
            }
            if (vm.formCheck[indexEmail] != stateInvalid) {
                updateEditTextBackground(et_email_address, hasFocus)
            }
        }
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
            if (null == iv_loading_image.animation)
                TAPUtils.rotateAnimateInfinitely(this, iv_loading_image)
            tv_loading_text.text = message
            fl_loading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            iv_loading_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin))
            iv_loading_image.clearAnimation()
            tv_loading_text.text = message
            fl_loading.setOnClickListener { hideLoading() }

            Handler().postDelayed({
                this.hideLoading()
            }, 1000L)
        }
    }

    private fun hideLoading() {
        fl_loading.visibility = View.GONE
    }

    private fun showErrorDialog(title: String, message: String) {
        TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener {}
                .show()
    }

    private fun setTextVersionApp() {
        try {
            val manager = packageManager
            val info = manager.getPackageInfo(
                    packageName, 0
            )

            val versionName = info.versionName
            val versionNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode
            }
            if (BuildConfig.DEBUG) {
                tv_version_code.text = String.format("V %s(%s)", versionName, versionNumber)
            } else {
                tv_version_code.text = String.format("V %s", versionName)
            }
        }catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private val bioWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            tv_character_count.text = "${et_bio.text.length}/100"
        }
    }

    private val fullNameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkFullNameTimer.cancel()
            checkFullNameTimer.start()
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkEmailAddressTimer.cancel()
            checkEmailAddressTimer.start()
        }
    }

    private val checkFullNameTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkFullName(et_full_name.hasFocus())
        }
    }

    private val checkEmailAddressTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkEmailAddress(et_email_address.hasFocus())
        }
    }

    private val scrollViewListener = ViewTreeObserver.OnScrollChangedListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val y = sv_profile.scrollY
            val h = vp_profile_picture.height
            when {
                y == 0 ->
                    cl_action_bar.elevation = 0f
                y < h ->
                    cl_action_bar.elevation = TAPUtils.dpToPx(1).toFloat()
                else ->
                    cl_action_bar.elevation = TAPUtils.dpToPx(2).toFloat()
            }
        }
    }

    private val logoutView = object : TAPDefaultDataView<TAPCommonResponse>() {
        override fun startLoading() {
            this@TAPMyAccountActivity.showLoading(getString(R.string.tap_logging_out))
        }

        override fun onSuccess(response: TAPCommonResponse?) {
            logout()
        }

        override fun onError(error: TAPErrorModel?) {
            hideLoading()
            logout()
        }

        override fun onError(errorMessage: String?) {
            hideLoading()
            logout()
        }
    }

    private val uploadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                UploadProgressLoading -> {
                    showLoading(getString(R.string.tap_updating))
                }
                UploadProgressFinish -> {
                    val updatedUserModel = intent.getParcelableExtra<TAPUserModel>(K_USER)
                    vm.currentProfilePicture = updatedUserModel?.imageURL?.fullsize
                    if (updatedUserModel?.userID == vm.myUserModel.userID) {
                        vm.isUploadingProfilePicture = false
                        getPhotoList(getString(R.string.tap_picture_uploaded))
                    } else {
                        endLoading(getString(R.string.tap_picture_uploaded))
                    }
                    LocalBroadcastManager.getInstance(this@TAPMyAccountActivity).sendBroadcast(Intent(RELOAD_PROFILE_PICTURE))
                }
                UploadFailed -> {
                    hideLoading()
                    val userID = intent.getStringExtra(K_USER_ID)
                    if (null != userID && userID == vm.myUserModel.userID) {
                        vm.isUploadingProfilePicture = false
                        enableEditing()
                        TapTalkDialog.Builder(this@TAPMyAccountActivity)
                                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                                .setTitle(getString(R.string.tap_error_unable_to_upload))
                                .setMessage(intent.getStringExtra(UploadFailedErrorMessage)
                                        ?: getString(R.string.tap_error_upload_profile_picture))
                                .setPrimaryButtonTitle(getString(R.string.tap_retry))
                                .setPrimaryButtonListener(true) { reloadProfilePicture(vm.profilePictureUri) }
                                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                                .setCancelable(true)
                                .show()
                    }
                }
            }
        }
    }
}
