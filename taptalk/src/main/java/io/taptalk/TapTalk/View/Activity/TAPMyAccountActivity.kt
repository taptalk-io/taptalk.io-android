package io.taptalk.TapTalk.View.Activity

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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER
import io.taptalk.TapTalk.Const.TAPDefaultConstant.K_USER_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_PROFILE_PICTURE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailedErrorMessage
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapTalkActionInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Manager.TapCoreContactManager
import io.taptalk.TapTalk.Manager.TapUI
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
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel.ViewState.EDIT
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel.ViewState.VIEW
import io.taptalk.TapTalk.databinding.TapActivityMyAccountBinding

class TAPMyAccountActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityMyAccountBinding
    private lateinit var vm: TAPRegisterViewModel
    private lateinit var glide: RequestManager
    private lateinit var profilePicturePagerAdapter: TapProfilePicturePagerAdapter

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
        const val MAX_PHOTO_SIZE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(vb.root)

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
        }
        else if (EDIT == vm.state) {
            if (vm.myUserModel.bio != vb.etBio.text.toString()) {
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
            }
            else {
                showViewState()
            }
        }
        else {
            try {
                super.onBackPressed()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (TAPUtils.allPermissionsGranted(grantResults)) {
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
                    PICK_PROFILE_IMAGE_CAMERA -> {
                        if (vm.profilePictureUri == null || !TAPFileUtils.getMimeTypeFromUri(this, vm.profilePictureUri).contains("image")) {
                            Toast.makeText(this, getString(R.string.tap_error_invalid_file_format), Toast.LENGTH_SHORT).show()
                            return
                        }
                        reloadProfilePicture(vm.profilePictureUri)
                    }
                    PICK_PROFILE_IMAGE_GALLERY -> {
                        val uri = intent?.data
                        if (uri == null || !TAPFileUtils.getMimeTypeFromUri(this, uri).contains("image")) {
                            Toast.makeText(this, getString(R.string.tap_error_invalid_file_format), Toast.LENGTH_SHORT).show()
                            return
                        }
                        vm.profilePictureUri = intent.data
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

        //binding.etFullName.onFocusChangeListener = fullNameFocusListener
        //binding.etEmailAddress.onFocusChangeListener = emailAddressFocusListener

        //binding.etFullName.addTextChangedListener(fullNameWatcher)
        //binding.etEmailAddress.addTextChangedListener(emailWatcher)
        vb.etBio.addTextChangedListener(bioWatcher)

        if (TapUI.getInstance(instanceKey).isChangeProfilePictureButtonVisible) {
            vb.tvEditProfilePicture.visibility = View.VISIBLE
        }
        else {
            vb.tvEditProfilePicture.visibility = View.GONE
        }

        // TODO: 25/02/22 handle onError and save list on preference later MU 
        if (vm.currentProfilePicture.isEmpty()) {
            showDefaultProfilePicture()
        }
        else {
            val profilePictureModel = TapPhotosItemModel()
            profilePictureModel.fullsizeImageURL = vm.currentProfilePicture
            vm.profilePictureList.add(profilePictureModel)
            vb.vpProfilePicture.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack))
            vb.vpProfilePicture.adapter = profilePicturePagerAdapter
            vb.tvProfilePictureLabel.visibility = View.GONE
        }

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                .apply(RequestOptions().placeholder(R.drawable.tap_ic_default_flag))
                .into(vb.ivCountryFlag)
        }
        vb.etFullName.setText(vm.myUserModel.fullname)
        vb.etUsername.setText(vm.myUserModel.username)
        vb.tvCountryCode.text = String.format("+%s", vm.myUserModel.countryCallingCode)
        vb.etMobileNumber.setText(TAPUtils.beautifyPhoneNumber(vm.myUserModel.phone, false))
        vb.etEmailAddress.setText(vm.myUserModel.email)

        if (vm.state == EDIT) {
            showEditState()
        }
        else {
            showViewState()
        }
        
        if (TapUI.getInstance(instanceKey).isEditBioTextFieldVisible ){
            vb.clBasicInfo.gBio.visibility = View.VISIBLE
            setProfileInformation(vb.clBasicInfo.tvBioView, vb.clBasicInfo.gBio, vm.myUserModel.bio)
            if (!vm.myUserModel.bio.isNullOrEmpty()) {
                vb.etBio.setText(vm.myUserModel.bio)
            }
        }
        else {
            vb.clBasicInfo.gBio.visibility = View.GONE
        }
        if (vm.myUserModel.phoneWithCode.isNullOrEmpty()) {
            vb.clBasicInfo.gMobileNumber.visibility = View.GONE
        }
        else {
            vb.clBasicInfo.gMobileNumber.visibility = View.VISIBLE
            vb.clBasicInfo.tvMobileNumberView.text = TAPUtils.beautifyPhoneNumber(String.format("%s %s", vm.myUserModel.countryCallingCode, vm.myUserModel.phone), true)
        }
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled) {
            vb.btnBlockedContacts.clContainer.visibility = View.VISIBLE
        }
        else {
            vb.btnBlockedContacts.clContainer.visibility = View.GONE
        }
        setProfileInformation(vb.clBasicInfo.tvUsernameView, vb.clBasicInfo.gUsername, vm.myUserModel.username)
        setProfileInformation(vb.clBasicInfo.tvEmailView, vb.clBasicInfo.gEmail, vm.myUserModel.email)
        setTextVersionApp()

        vb.ivButtonClose.setOnClickListener { onBackPressed() }
        vb.flContainer.setOnClickListener { clearAllFocus() }
        vb.clFormContainer.setOnClickListener { clearAllFocus() }
        vb.clPassword.setOnClickListener { openChangePasswordPage() }
        vb.clLogout.setOnClickListener { promptUserLogout() }
        vb.btnBlockedContacts.clContainer.setOnClickListener {
            TAPBlockedListActivity.start(this, instanceKey)
        }
        vb.btnDeleteMyAccount.setOnClickListener {
            TapUI.getInstance(instanceKey).triggerDeleteButtonInMyAccountPageTapped(this)
        }

        vm.textFieldFontColor = ContextCompat.getColor(this, R.color.tapFormTextFieldColor)
        vm.textFieldFontColorHint = ContextCompat.getColor(this, R.color.tapFormTextFieldPlaceholderColor)
        vm.clickableLabelFontColor = ContextCompat.getColor(this, R.color.tapClickableLabelColor)

        // TODO temporarily disable editing
        vb.etFullName.isEnabled = false
        vb.etUsername.isEnabled = false
        vb.etMobileNumber.isEnabled = false
        vb.etEmailAddress.isEnabled = false

        vb.etFullName.setTextColor(vm.textFieldFontColorHint)
        vb.etUsername.setTextColor(vm.textFieldFontColorHint)
        vb.tvCountryCode.setTextColor(vm.textFieldFontColorHint)
        vb.etMobileNumber.setTextColor(vm.textFieldFontColorHint)
        vb.etEmailAddress.setTextColor(vm.textFieldFontColorHint)

        vb.tvLabelPassword.visibility = View.GONE
        vb.clPassword.visibility = View.GONE

        vb.svProfile.viewTreeObserver.addOnScrollChangedListener(scrollViewListener)
        getPhotoList(null)
    }

    private fun showViewState() {
        vm.state = VIEW
        TAPUtils.dismissKeyboard(this)
        vb.tvTitle.text = vm.myUserModel.fullname
        vb.tvEditProfilePicture.visibility = View.VISIBLE
        vb.tvEditSaveBtn.setOnClickListener { showEditState() }
        vb.tvEditSaveBtn.text = getString(R.string.tap_edit)
        vb.tvEditProfilePicture.text = getString(R.string.tap_set_new_profile_picture)
        vb.tvEditProfilePicture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        vb.gEdit.visibility = View.GONE
        vb.clBasicInfo.clBasicInformationContainer.visibility = View.VISIBLE
        vb.tvVersionCode.visibility = View.VISIBLE
        vb.clLogout.visibility = View.GONE
        vb.btnDeleteMyAccount.visibility = View.GONE
        vb.etBio.isEnabled = false
        vb.etBio.setText(vm.myUserModel.bio)
        if (TapUI.getInstance(instanceKey).isBlockUserMenuEnabled) {
            vb.btnBlockedContacts.clContainer.visibility = View.VISIBLE
        }
    }

    private fun showEditState() {
        vm.state = EDIT
        vb.tvTitle.text = getString(R.string.tap_account_details)
        vb.tvEditSaveBtn.setOnClickListener {
                saveProfile()
             }
        vb.tvEditSaveBtn.text = getString(R.string.tap_save)
        vb.tvEditProfilePicture.text = getString(R.string.tap_edit_profile_picture)
        vb.tvEditProfilePicture.setOnClickListener { showProfilePictureOptionsBottomSheet() }
        if (vb.tvProfilePictureLabel.visibility == View.VISIBLE) {
            vb.tvEditProfilePicture.visibility = View.GONE
        }
        else {
            vb.tvEditProfilePicture.visibility = View.VISIBLE
        }
        vb.gEdit.visibility = View.VISIBLE
        if (TapUI.getInstance(instanceKey).isEditBioTextFieldVisible) {
            vb.gBioFields.visibility = View.VISIBLE
        }
        else {
            vb.gBioFields.visibility = View.GONE
        }
        vb.clBasicInfo.clBasicInformationContainer.visibility = View.GONE
        vb.tvVersionCode.visibility = View.GONE
        if (TapUI.getInstance(instanceKey).isLogoutButtonVisible) {
            vb.clLogout.visibility = View.VISIBLE
        }
        else {
            vb.clLogout.visibility = View.GONE
        }
        if (TapUI.getInstance(instanceKey).isDeleteAccountButtonVisible) {
            vb.btnDeleteMyAccount.visibility = View.VISIBLE
        }
        else {
            vb.btnDeleteMyAccount.visibility = View.GONE
        }
        vb.etBio.isEnabled = true
        vb.btnBlockedContacts.clContainer.visibility = View.GONE
    }

    private fun setProfileInformation(textView: TextView, group: View, textValue: String?) {
        if (textValue.isNullOrEmpty()) {
            group.visibility = View.GONE
        }
        else {
            group.visibility = View.VISIBLE
            textView.text = textValue
        }
    }

    private fun saveProfile() {
        if (vm.myUserModel.bio != vb.etBio.text.toString()) {
            TapTalkDialog.Builder(this@TAPMyAccountActivity)
                .setTitle(getString(R.string.tap_save_changes_question))
                .setMessage(getString(R.string.tap_save_changes_confirmation))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_save))
                .setPrimaryButtonListener {
                    vm.isUpdatingProfile = true
                    disableEditing()
                    showLoading(getString(R.string.tap_updating))
                    TapCoreContactManager.getInstance(instanceKey).updateActiveUserBio(vb.etBio.text.toString(), updateBioListener)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        }
        else {
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
        }
        else {
            if (vm.profilePictureList.size >= MAX_PHOTO_SIZE) {
                TapTalkDialog.Builder(this)
                    .setTitle(getString(R.string.tap_max_profile_picture_title))
                    .setMessage(getString(R.string.tap_max_profile_picture_message))
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .setPrimaryButtonListener(true) { }
                    .setCancelable(true)
                    .show()
            }
            else {
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
        }
        else {
            TAPUtils.dismissKeyboard(this@TAPMyAccountActivity)
            TAPAttachmentBottomSheet(instanceKey, vb.vpProfilePicture.currentItem, profilePictureOptionListener).show(supportFragmentManager, "")
        }
    }

    private fun showDefaultProfilePicture() {
        vb.vpProfilePicture.setBackgroundColor(TAPUtils.getRandomColor(this@TAPMyAccountActivity, vm.myUserModel.fullname))
        vb.vpProfilePicture.adapter = null
        vb.tabLayout.visibility = View.GONE
        vb.tvProfilePictureLabel.text = TAPUtils.getInitials(vm.myUserModel.fullname, 2)
        vb.tvProfilePictureLabel.visibility = View.VISIBLE
        if (vm.state == EDIT) {
            vb.tvEditProfilePicture.visibility = View.GONE
        }
        else {
            vb.tvEditProfilePicture.visibility = View.VISIBLE
        }
    }

    private fun reloadProfilePicture(imageUri: Uri?) {
        if (null == imageUri) {
            vm.formCheck[indexProfilePicture] = stateEmpty
            Toast.makeText(this@TAPMyAccountActivity, getString(R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
        }
        else {
            vm.formCheck[indexProfilePicture] = stateValid
            uploadProfilePicture(imageUri)
        }
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (vb.etFullName.text.toString() == vm.myUserModel.fullname) {
            // Unchanged
            vm.formCheck[indexFullName] = stateUnchanged
            vb.tvLabelFullNameError.visibility = View.GONE
            updateEditTextBackground(vb.etFullName, hasFocus)
        }
        else if (vb.etFullName.text.isNotEmpty() && vb.etFullName.text.matches(Regex("[A-Za-z ]*"))) {
            // Valid full name
            vm.formCheck[indexFullName] = stateValid
            vb.tvLabelFullNameError.visibility = View.GONE
            updateEditTextBackground(vb.etFullName, hasFocus)
        }
        else if (vb.etFullName.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexFullName] = stateEmpty
            vb.tvLabelFullNameError.visibility = View.GONE
            updateEditTextBackground(vb.etFullName, hasFocus)
        }
        else {
            // Invalid full name
            vm.formCheck[indexFullName] = stateInvalid
            vb.tvLabelFullNameError.visibility = View.VISIBLE
            vb.etFullName.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        }
        checkContinueButtonAvailability()
    }

    private fun checkEmailAddress(hasFocus: Boolean) {
        var isEmailPatternValid = false
        try {
            isEmailPatternValid = Patterns.EMAIL_ADDRESS.matcher(vb.etEmailAddress.text).matches()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        if (vb.etEmailAddress.text.toString() == vm.myUserModel.email) {
            // Unchanged
            vm.formCheck[indexEmail] = stateUnchanged
            vb.tvLabelEmailAddressError.visibility = View.GONE
            updateEditTextBackground(vb.etEmailAddress, hasFocus)
        }
        else if (vb.etEmailAddress.text.isNotEmpty() && isEmailPatternValid) {
            // Valid email address
            vm.formCheck[indexEmail] = stateValid
            vb.tvLabelEmailAddressError.visibility = View.GONE
            updateEditTextBackground(vb.etEmailAddress, hasFocus)
        }
        else if (vb.etEmailAddress.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexEmail] = stateEmpty
            vb.tvLabelEmailAddressError.visibility = View.GONE
            updateEditTextBackground(vb.etEmailAddress, hasFocus)
        }
        else {
            // Invalid email address
            vm.formCheck[indexEmail] = stateInvalid
            vb.tvLabelEmailAddressError.visibility = View.VISIBLE
            vb.etEmailAddress.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        }
        checkContinueButtonAvailability()
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
        }
        else {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_inactive)
        }
        if (view == vb.clPassword) {
            if (hasFocus) {
                vb.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
            }
            else {
                vb.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapGreyDc))
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
        }
        else {
            // No changes / has invalid forms
        }
    }

    private fun clearAllFocus() {
        TAPUtils.dismissKeyboard(this)
        vb.flContainer.clearFocus()
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
        val listeners = TapTalk.getTapTalkListeners(instanceKey);
        if (!listeners.isNullOrEmpty()) {
            for (listener in listeners) {
                listener.onUserLogout()
            }
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
        vb.ivButtonClose.setOnClickListener(null)
        vb.tvEditProfilePicture.isEnabled = false

        vb.ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this@TAPMyAccountActivity, R.drawable.tap_ic_loading_progress_circle_white))
        TAPUtils.rotateAnimateInfinitely(this@TAPMyAccountActivity, vb.ivButtonClose)

        // TODO temporarily disable editing
//        binding.etFullName.isEnabled = false
//        binding.etEmailAddress.isEnabled = false
//
//        binding.etFullName.setTextColor(resources.getColor(R.color.tap_grey_9b))
//        binding.etEmailAddress.setTextColor(resources.getColor(R.color.tap_grey_9b))
//
//        tv_button_update.visibility = View.GONE
//        iv_update_progress.visibility = View.VISIBLE
//        TAPUtils.rotateAnimateInfinitely(this@TAPMyAccountActivity, iv_update_progress)
    }

    private fun enableEditing() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        }
        vb.ivButtonClose.setOnClickListener { onBackPressed() }
        vb.tvEditProfilePicture.isEnabled = true

        vb.ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white))
        vb.ivButtonClose.clearAnimation()

        // TODO temporarily disable editing
//        binding.etFullName.isEnabled = true
//        binding.etEmailAddress.isEnabled = true
//
//        binding.etFullName.setTextColor(ContextCompat.getColor(this, R.color.tap_black_19))
//        binding.etEmailAddress.setTextColor(ContextCompat.getColor(this, R.color.tap_black_19))
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
            setProfileInformation(vb.clBasicInfo.tvBioView, vb.clBasicInfo.gBio, user?.bio)
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
        if (!TAPUtils.hasPermissions(this, *TAPUtils.getStoragePermissions(false))) {
            // Request storage permission
            ActivityCompat.requestPermissions(
                this,
                TAPUtils.getStoragePermissions(false),
                PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE
            )
        }
        else {
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
        }
        else {
            vm.formCheck[indexProfilePicture] = stateValid
            vm.profilePictureList.addAll(photoList)
            vb.vpProfilePicture.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack))
            vb.vpProfilePicture.adapter = profilePicturePagerAdapter
            if (vm.profilePictureList.size > 1) {
                vb.tabLayout.visibility = View.VISIBLE
                vb.tabLayout.setupWithViewPager(vb.vpProfilePicture)
            }
            else {
                vb.tabLayout.visibility = View.GONE
            }
            vb.tvProfilePictureLabel.visibility = View.GONE
        }
        if (loadingText.isNullOrEmpty()) {
            hideLoading()
        }
        else {
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
            LocalBroadcastManager.getInstance(this@TAPMyAccountActivity).sendBroadcast(Intent(RELOAD_PROFILE_PICTURE))
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
            Glide.with(this@TAPMyAccountActivity).asBitmap().load(vm.profilePictureList[vb.vpProfilePicture.currentItem].fullsizeImageURL).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImage(vm.profilePictureList[vb.vpProfilePicture.currentItem].fullsizeImageURL, resource)
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
            view.elevation = TAPUtils.dpToPx(4).toFloat()
            if (vm.formCheck[indexFullName] != stateInvalid) {
                view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
            }
        }
        else {
            view.elevation = 0f
            if (vm.formCheck[indexFullName] != stateInvalid) {
                updateEditTextBackground(vb.etFullName, hasFocus)
            }
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.dpToPx(4).toFloat()
            if (vm.formCheck[indexEmail] != stateInvalid) {
                view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
            }
        }
        else {
            view.elevation = 0f
            if (vm.formCheck[indexEmail] != stateInvalid) {
                updateEditTextBackground(vb.etEmailAddress, hasFocus)
            }
        }
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
            if (null == vb.layoutPopupLoadingScreen.ivLoadingImage.animation)
                TAPUtils.rotateAnimateInfinitely(this, vb.layoutPopupLoadingScreen.ivLoadingImage)
            vb.layoutPopupLoadingScreen.tvLoadingText.text = message
            vb.layoutPopupLoadingScreen.flLoading.visibility = View.VISIBLE
        }
    }

    private fun endLoading(message: String) {
        runOnUiThread {
            vb.layoutPopupLoadingScreen.ivLoadingImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_checklist_pumpkin))
            vb.layoutPopupLoadingScreen.ivLoadingImage.clearAnimation()
            vb.layoutPopupLoadingScreen.tvLoadingText.text = message
            vb.layoutPopupLoadingScreen.flLoading.setOnClickListener { hideLoading() }

            Handler().postDelayed({
                this.hideLoading()
            }, 1000L)
        }
    }

    private fun hideLoading() {
        vb.layoutPopupLoadingScreen.flLoading.visibility = View.GONE
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
            }
            else {
                info.versionCode
            }
            if (BuildConfig.DEBUG) {
                vb.tvVersionCode.text = String.format("V %s(%s)", versionName, versionNumber)
            }
            else {
                vb.tvVersionCode.text = String.format("V %s", versionName)
            }
        }
        catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private val bioWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            vb.tvCharacterCount.text = "${vb.etBio.text.length}/100"
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
            checkFullName(vb.etFullName.hasFocus())
        }
    }

    private val checkEmailAddressTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkEmailAddress(vb.etEmailAddress.hasFocus())
        }
    }

    private val scrollViewListener = ViewTreeObserver.OnScrollChangedListener {
        val y = vb.svProfile.scrollY
        val h = vb.vpProfilePicture.height
        when {
            y == 0 ->
                vb.clActionBar.elevation = 0f
            y < h ->
                vb.clActionBar.elevation = TAPUtils.dpToPx(1).toFloat()
            else ->
                vb.clActionBar.elevation = TAPUtils.dpToPx(2).toFloat()
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
                    }
                    else {
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
