package io.taptalk.TapTalk.View.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.API.Api.TAPApiManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.*
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_my_account.*
import kotlinx.android.synthetic.main.tap_activity_my_account.civ_profile_picture_overlay
import kotlinx.android.synthetic.main.tap_activity_my_account.iv_edit_profile_picture_icon
import kotlinx.android.synthetic.main.tap_activity_my_account.pb_profile_picture_progress
import kotlinx.android.synthetic.main.tap_activity_my_account.tv_label_change_profile_picture
import kotlinx.android.synthetic.main.tap_activity_my_account.tv_label_password
import kotlinx.android.synthetic.main.tap_activity_my_account.civ_profile_picture
import kotlinx.android.synthetic.main.tap_activity_my_account.cl_form_container
import kotlinx.android.synthetic.main.tap_activity_my_account.cl_password
import kotlinx.android.synthetic.main.tap_activity_my_account.et_email_address
import kotlinx.android.synthetic.main.tap_activity_my_account.et_full_name
import kotlinx.android.synthetic.main.tap_activity_my_account.et_mobile_number
import kotlinx.android.synthetic.main.tap_activity_my_account.et_username
import kotlinx.android.synthetic.main.tap_activity_my_account.fl_container
import kotlinx.android.synthetic.main.tap_activity_my_account.tv_country_code
import kotlinx.android.synthetic.main.tap_activity_my_account.tv_label_email_address_error
import kotlinx.android.synthetic.main.tap_activity_my_account.tv_label_full_name_error
import kotlinx.android.synthetic.main.tap_activity_my_account.v_password_separator
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

    private lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_my_account)

        glide = Glide.with(this)
        initViewModel()
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
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_CAMERA_CAMERA, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA -> {
                    vm.profilePictureUri = TAPUtils.getInstance().takePicture(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_CAMERA)
                }
                PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> {
                    TAPUtils.getInstance().pickImageFromGallery(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_GALLERY, false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PICK_PROFILE_IMAGE_CAMERA -> {
                        if (null != intent?.data) {
                            vm.profilePictureUri = intent.data
                        }
                        reloadProfilePicture(vm.profilePictureUri, showErrorMessage = true, uploadPicture = true)
                    }
                    PICK_PROFILE_IMAGE_GALLERY -> {
                        vm.profilePictureUri = intent?.data
                        reloadProfilePicture(vm.profilePictureUri, showErrorMessage = true, uploadPicture = true)
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPRegisterViewModel::class.java)
        vm.currentProfilePicture = vm.myUserModel.avatarURL.thumbnail
        vm.countryFlagUrl = TAPDataManager.getInstance().myCountryFlagUrl
    }

    @SuppressLint("PrivateResource")
    private fun initView() {
        window?.setBackgroundDrawable(null)

        //et_full_name.onFocusChangeListener = fullNameFocusListener
        //et_email_address.onFocusChangeListener = emailAddressFocusListener

        //et_full_name.addTextChangedListener(fullNameWatcher)
        //et_email_address.addTextChangedListener(emailWatcher)

        if (TapUI.getInstance().isLogoutButtonVisible) {
            cl_logout.visibility = View.VISIBLE
        } else {
            cl_logout.visibility = View.GONE
        }

        if (vm.currentProfilePicture.isEmpty()) {
            ImageViewCompat.setImageTintList(civ_profile_picture, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(this@TAPMyAccountActivity, vm.myUserModel.name)))
            civ_profile_picture.setImageDrawable(ContextCompat.getDrawable(this@TAPMyAccountActivity, R.drawable.tap_bg_circle_9b9b9b))
            tv_profile_picture_label.text = TAPUtils.getInstance().getInitials(vm.myUserModel.name, 2)
            tv_profile_picture_label.visibility = View.VISIBLE
        } else {
            glide.load(vm.currentProfilePicture)
                    .apply(RequestOptions().placeholder(R.drawable.tap_bg_circle_9b9b9b))
                    .into(civ_profile_picture)
            ImageViewCompat.setImageTintList(civ_profile_picture, null)
            tv_profile_picture_label.visibility = View.GONE
        }

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                    .apply(RequestOptions().placeholder(R.drawable.tap_ic_default_flag))
                    .into(iv_country_flag)
        }
        et_full_name.setText(vm.myUserModel.name)
        et_username.setText(vm.myUserModel.username)
        tv_country_code.text = "+" + vm.myUserModel.countryCallingCode
        et_mobile_number.setText(vm.myUserModel.phoneNumber)
        et_email_address.setText(vm.myUserModel.email)

        iv_button_close.setOnClickListener { onBackPressed() }
        fl_container.setOnClickListener { clearAllFocus() }
        cl_form_container.setOnClickListener { clearAllFocus() }
        civ_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        fl_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        cl_password.setOnClickListener { openChangePasswordPage() }
        cl_logout.setOnClickListener { promptUserLogout() }

        // Obtain text field style attributes
        val textFieldArray = obtainStyledAttributes(R.style.tapFormTextFieldStyle, R.styleable.TextAppearance)
        vm.fontResourceId = textFieldArray.getResourceId(R.styleable.TextAppearance_android_fontFamily, -1)
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
        fl_button_update.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cl_password.background = getDrawable(R.drawable.tap_bg_text_field_inactive_ripple)
            cl_logout.background = getDrawable(R.drawable.tap_bg_text_field_inactive_ripple)
            sv_profile.viewTreeObserver.addOnScrollChangedListener(scrollViewListener)
        }
    }

    private fun registerBroadcastReceiver() {
        TAPBroadcastManager.register(this@TAPMyAccountActivity, uploadBroadcastReceiver,
                UploadProgressLoading, UploadProgressFinish, UploadFailed)
    }

    private fun showProfilePicturePickerBottomSheet() {
        TAPUtils.getInstance().dismissKeyboard(this@TAPMyAccountActivity)
        TAPAttachmentBottomSheet(true, profilePicturePickerListener).show(supportFragmentManager, "")
    }

    private fun removeProfilePicture() {
        vm.profilePictureUri = null
        reloadProfilePicture(vm.profilePictureUri, false, false)
    }

    private fun reloadProfilePicture(imageUri: Uri?, showErrorMessage: Boolean, uploadPicture: Boolean) {
        if (null == imageUri) {
            vm.formCheck[indexProfilePicture] = stateEmpty

            ImageViewCompat.setImageTintList(civ_profile_picture, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(this@TAPMyAccountActivity, vm.myUserModel.name)))
            civ_profile_picture.setImageDrawable(ContextCompat.getDrawable(this@TAPMyAccountActivity, R.drawable.tap_bg_circle_9b9b9b))
            tv_profile_picture_label.text = TAPUtils.getInstance().getInitials(vm.myUserModel.name, 2)
            tv_profile_picture_label.visibility = View.VISIBLE
            // TODO temporarily disabled removing profile picture
//            fl_remove_profile_picture.visibility = View.GONE

            if (showErrorMessage) {
                Toast.makeText(this@TAPMyAccountActivity, getString(R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
            }
        } else {
            vm.formCheck[indexProfilePicture] = stateValid

            glide.load(imageUri).into(civ_profile_picture)
            ImageViewCompat.setImageTintList(civ_profile_picture, null)
            tv_profile_picture_label.visibility = View.GONE
            // TODO temporarily disabled removing profile picture
//            fl_remove_profile_picture.visibility = View.VISIBLE

            if (uploadPicture) {
                uploadProfilePicture()
            }
        }
    }

    private fun reloadProfilePicture(imageUrl: String?, placeholder: Drawable) {
        if (null == imageUrl) {
            vm.formCheck[indexProfilePicture] = stateEmpty

            ImageViewCompat.setImageTintList(civ_profile_picture, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(this@TAPMyAccountActivity, vm.myUserModel.name)))
            glide.load(R.drawable.tap_bg_circle_9b9b9b).apply(RequestOptions().placeholder(placeholder)).into(civ_profile_picture)
            tv_profile_picture_label.text = TAPUtils.getInstance().getInitials(vm.myUserModel.name, 2)
            tv_profile_picture_label.visibility = View.VISIBLE
            // TODO temporarily disabled removing profile picture
//            fl_remove_profile_picture.visibility = View.GONE
        } else {
            vm.formCheck[indexProfilePicture] = stateValid
            ImageViewCompat.setImageTintList(civ_profile_picture, null)
            glide.load(imageUrl).apply(RequestOptions().placeholder(placeholder)).into(civ_profile_picture)
            tv_profile_picture_label.visibility = View.GONE
            // TODO temporarily disabled removing profile picture
//            fl_remove_profile_picture.visibility = View.VISIBLE
        }
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (et_full_name.text.toString() == vm.myUserModel.name) {
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
            enableContinueButton()
        } else {
            // No changes / has invalid forms
            disableContinueButton()
        }
    }

    private fun enableContinueButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fl_button_update.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        } else {
            fl_button_update.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active)
        }
        //fl_button_update.setOnClickListener { register() }
    }

    private fun disableContinueButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fl_button_update.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive_ripple)
        } else {
            fl_button_update.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        }
        fl_button_update.setOnClickListener(null)
    }

    private fun clearAllFocus() {
        TAPUtils.getInstance().dismissKeyboard(this)
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
                .setPrimaryButtonListener { TAPDataManager.getInstance().logout(logoutView) }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setSecondaryButtonListener(true) {}
                .show()
    }

    private fun logout() {
        TAPDataManager.getInstance().deleteAllPreference()
        TAPDataManager.getInstance().deleteAllFromDatabase()
        TAPDataManager.getInstance().deleteAllManagerData()
        TAPApiManager.getInstance().isLogout = true
        TAPRoomListViewModel.setShouldNotLoadFromAPI(false)
        TAPChatManager.getInstance().disconnectAfterRefreshTokenExpired()

        hideLoading()

        for (listener in TapTalk.getTapTalkListeners()) {
            listener.onUserLogout()
        }
    }

    private fun uploadProfilePicture() {
        vm.isUploadingProfilePicture = true
        showProfilePictureUploading()
        TAPFileUploadManager.getInstance().uploadProfilePicture(this@TAPMyAccountActivity, vm.profilePictureUri, vm.myUserModel.userID)
    }

    private fun showErrorDialog(message: String) {
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
        civ_profile_picture.setOnClickListener(null)
        ll_change_profile_picture.setOnClickListener(null)
        fl_remove_profile_picture.setOnClickListener(null)
        fl_button_update.setOnClickListener(null)

        iv_button_close.setImageDrawable(ContextCompat.getDrawable(this@TAPMyAccountActivity, R.drawable.tap_ic_loading_progress_circle_white))
        TAPUtils.getInstance().rotateAnimateInfinitely(this@TAPMyAccountActivity, iv_button_close)

        // TODO temporarily disable editing
//        et_full_name.isEnabled = false
//        et_email_address.isEnabled = false
//
//        et_full_name.setTextColor(resources.getColor(R.color.tap_grey_9b))
//        et_email_address.setTextColor(resources.getColor(R.color.tap_grey_9b))
//
//        tv_button_update.visibility = View.GONE
//        iv_update_progress.visibility = View.VISIBLE
//        TAPUtils.getInstance().rotateAnimateInfinitely(this@TAPMyAccountActivity, iv_update_progress)
    }

    private fun enableEditing() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        }
        iv_button_close.setOnClickListener { onBackPressed() }
        civ_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        fl_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        //fl_button_update.setOnClickListener { updateProfile() }

        iv_button_close.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey))
        iv_button_close.clearAnimation()

        tv_label_change_profile_picture.text = getString(R.string.tap_change)
        tv_label_change_profile_picture.setTextColor(vm.clickableLabelFontColor)
        iv_edit_profile_picture_icon.visibility = View.VISIBLE
        civ_profile_picture_overlay.visibility = View.GONE
        pb_profile_picture_progress.visibility = View.GONE
        pb_profile_picture_progress.progress = 0

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

    private fun showProfilePictureUploading() {
        iv_button_close.setOnClickListener(null)
        civ_profile_picture.setOnClickListener(null)
        ll_change_profile_picture.setOnClickListener(null)

        iv_button_close.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
        TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_button_close)

        tv_label_change_profile_picture.text = getString(R.string.tap_uploading)
        tv_label_change_profile_picture.setTextColor(ContextCompat.getColor(this, R.color.tapColorTextMedium))
        iv_edit_profile_picture_icon.visibility = View.GONE
        civ_profile_picture_overlay.visibility = View.VISIBLE
        pb_profile_picture_progress.visibility = View.VISIBLE
        pb_profile_picture_progress.progress = 0
    }

    private val profilePicturePickerListener = object : TAPAttachmentListener() {
        override fun onCameraSelected() {
            vm.profilePictureUri = TAPUtils.getInstance().takePicture(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_CAMERA)
        }

        override fun onGallerySelected() {
            TAPUtils.getInstance().pickImageFromGallery(this@TAPMyAccountActivity, PICK_PROFILE_IMAGE_GALLERY, false)
        }
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
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
                view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
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
                TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_loading_image)
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

    private val fullNameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            disableContinueButton()
            checkFullNameTimer.cancel()
            checkFullNameTimer.start()
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            disableContinueButton()
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
            val h = iv_profile_background.height
            when {
                y == 0 ->
                    cl_action_bar.elevation = 0f
                y < h ->
                    cl_action_bar.elevation = TAPUtils.getInstance().dpToPx(1).toFloat()
                else ->
                    cl_action_bar.elevation = TAPUtils.getInstance().dpToPx(2).toFloat()
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
            showErrorDialog(error!!.message)
        }

        override fun onError(errorMessage: String?) {
            hideLoading()
            showErrorDialog(getString(R.string.tap_error_message_general))
        }
    }

    private val uploadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                UploadProgressLoading -> {
                    pb_profile_picture_progress.progress = intent.getIntExtra(UploadProgress, 0)
                    pb_profile_picture_progress.visibility = View.VISIBLE
                }
                UploadProgressFinish -> {
                    val updatedUserModel = intent.getParcelableExtra<TAPUserModel>(K_USER)
                    vm.currentProfilePicture = updatedUserModel.avatarURL.thumbnail
                    if (updatedUserModel?.userID == vm.myUserModel.userID) {
                        vm.isUploadingProfilePicture = false
                        enableEditing()
                        reloadProfilePicture(vm.currentProfilePicture, civ_profile_picture.drawable)
                    }
                    LocalBroadcastManager.getInstance(this@TAPMyAccountActivity).sendBroadcast(Intent(RELOAD_PROFILE_PICTURE))
                }
                UploadFailed -> {
                    val userID = intent.getStringExtra(K_USER_ID)
                    if (null != userID && userID == vm.myUserModel.userID) {
                        vm.isUploadingProfilePicture = false
                        reloadProfilePicture(vm.currentProfilePicture, civ_profile_picture.drawable)
                        enableEditing()
                        TapTalkDialog.Builder(this@TAPMyAccountActivity)
                                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                                .setTitle(getString(R.string.tap_error_unable_to_upload))
                                .setMessage(intent.getStringExtra(UploadFailedErrorMessage)
                                        ?: getString(R.string.tap_error_upload_profile_picture))
                                .setPrimaryButtonTitle(getString(R.string.tap_retry))
                                .setPrimaryButtonListener(true) { reloadProfilePicture(vm.profilePictureUri, true, true) }
                                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                                .setCancelable(true)
                                .show()
                    }
                }
            }
        }
    }
}
