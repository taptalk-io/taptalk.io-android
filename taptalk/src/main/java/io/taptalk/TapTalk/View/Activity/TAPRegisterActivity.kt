package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.API.View.TapDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.*
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCheckUsernameResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_register.*

class TAPRegisterActivity : TAPBaseActivity() {

    private val indexProfilePicture = 0
    private val indexFullName = 1
    private val indexUsername = 2
    private val indexMobileNumber = 3
    private val indexEmail = 4
    private val indexPassword = 5
    private val indexPasswordRetype = 6

    private val stateEmpty = 0
    private val stateValid = 1
    private val stateInvalid = -1

    private lateinit var vm: TAPRegisterViewModel

    private lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_register)

        glide = Glide.with(this)
        initViewModel()
        initView()
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this@TAPRegisterActivity, uploadBroadcastReceiver)
    }

    override fun onBackPressed() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_CAMERA_CAMERA, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA -> {
                    vm.profilePictureUri = TAPUtils.getInstance().takePicture(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_CAMERA)
                }
                PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> {
                    TAPUtils.getInstance().pickImageFromGallery(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_GALLERY, false)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PICK_PROFILE_IMAGE_CAMERA -> {
                        reloadProfilePicture(true)
                    }
                    PICK_PROFILE_IMAGE_GALLERY -> {
                        vm.profilePictureUri = intent?.data
                        reloadProfilePicture(true)
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPRegisterViewModel::class.java)
        vm.countryID = intent.getIntExtra(COUNTRY_ID, 1)
        vm.countryCallingCode = intent.getStringExtra(COUNTRY_CALLING_CODE)
        vm.countryFlagUrl = intent.getStringExtra(COUNTRY_FLAG_URL)
    }

    private fun initView() {
        window?.setBackgroundDrawable(null)

        et_full_name.onFocusChangeListener = fullNameFocusListener
        et_username.onFocusChangeListener = usernameFocusListener
        et_email_address.onFocusChangeListener = emailAddressFocusListener
        et_password.onFocusChangeListener = passwordFocusListener
        et_retype_password.onFocusChangeListener = passwordRetypeFocusListener

        et_full_name.addTextChangedListener(fullNameWatcher)
        et_username.addTextChangedListener(usernameWatcher)
        et_email_address.addTextChangedListener(emailWatcher)
        et_password.addTextChangedListener(passwordWatcher)
        et_retype_password.addTextChangedListener(passwordRetypeWatcher)

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                    .apply(RequestOptions().placeholder(R.drawable.tap_ic_default_flag))
                    .into(iv_country_flag)
        }

        et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_retype_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        et_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)
        et_retype_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)

        tv_country_code.text = "+" + vm.countryCallingCode
        et_mobile_number.isEnabled = false
        et_mobile_number.setText(intent.getStringExtra(MOBILE_NUMBER))
        if (et_mobile_number.text.isNotEmpty()) {
            vm.formCheck[indexMobileNumber] = stateValid
        }

        TAPUtils.getInstance().showKeyboard(this, et_full_name)

        fl_container.setOnClickListener { clearAllFocus() }
        cl_form_container.setOnClickListener { clearAllFocus() }
        iv_button_back.setOnClickListener { onBackPressed() }
        civ_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        iv_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        iv_view_password.setOnClickListener { togglePasswordVisibility(et_password, iv_view_password) }
        iv_view_password_retype.setOnClickListener { togglePasswordVisibility(et_retype_password, iv_view_password_retype) }

        et_retype_password.setOnEditorActionListener { v, a, e -> fl_button_continue.callOnClick() }

        sv_register.viewTreeObserver.addOnScrollChangedListener(scrollViewListener)

        // TODO TEMPORARILY REMOVED PASSWORD
        tv_label_password.visibility = View.GONE
        tv_label_password_optional.visibility = View.GONE
        cl_password.visibility = View.GONE
        tv_label_password_guide.visibility = View.GONE
        tv_label_password_error.visibility = View.GONE
        tv_label_retype_password.visibility = View.GONE
        tv_label_retype_password_error.visibility = View.GONE
        cl_retype_password.visibility = View.GONE
    }

    private fun registerBroadcastReceiver() {
        TAPBroadcastManager.register(this@TAPRegisterActivity, uploadBroadcastReceiver,
                UploadProgressLoading, UploadProgressFinish, UploadFailed)
    }

    private fun showProfilePicturePickerBottomSheet() {
        TAPUtils.getInstance().dismissKeyboard(this@TAPRegisterActivity)
        TAPAttachmentBottomSheet(true, profilePicturePickerListener).show(supportFragmentManager, "")
    }

    private fun removeProfilePicture() {
        vm.profilePictureUri = null
        reloadProfilePicture(false)
    }

    private fun reloadProfilePicture(showErrorMessage: Boolean) {
        if (null == vm.profilePictureUri) {
            vm.formCheck[indexProfilePicture] = stateEmpty
            glide.load(R.drawable.tap_img_default_avatar).into(civ_profile_picture)
            iv_remove_profile_picture.visibility = View.GONE
            if (showErrorMessage) {
                Toast.makeText(this@TAPRegisterActivity, getString(R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
            }
        } else {
            vm.formCheck[indexProfilePicture] = stateValid
            glide.load(vm.profilePictureUri).into(civ_profile_picture)
            iv_remove_profile_picture.visibility = View.VISIBLE
        }
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (et_full_name.text.isNotEmpty() && et_full_name.text.matches(Regex("[A-Za-z ]*"))) {
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
            et_full_name.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkUsername(hasFocus: Boolean) {
        TAPDataManager.getInstance().cancelCheckUsernameApiCall()
        if (et_username.text.isNotEmpty() && et_username.text.length in 4..32 &&
                et_username.text.matches(Regex("[a-z0-9._]*")) &&
                et_username.text[0].isLetter() &&
                et_username.text[et_username.text.lastIndex].isLetterOrDigit()) {
            // Valid username, continue to check if username exists
            TAPDataManager.getInstance().checkUsernameExists(et_username.text.toString(), checkUsernameView)
        } else if (et_username.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexUsername] = stateEmpty
            tv_label_username_error.visibility = View.GONE
            updateEditTextBackground(et_username, hasFocus)
            checkContinueButtonAvailability()
        } else {
            // Invalid username
            vm.formCheck[indexUsername] = stateInvalid
            if (et_username.text.length in 4..32) {
                tv_label_username_error.text = getString(R.string.tap_error_invalid_username)
            } else {
                tv_label_username_error.text = getString(R.string.tap_error_username_length)
            }
            tv_label_username_error.visibility = View.VISIBLE
            et_username.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
            checkContinueButtonAvailability()
        }
    }

    private fun checkEmailAddress(hasFocus: Boolean) {
        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
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
            et_email_address.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkPassword(hasFocus: Boolean) {
        // Regex to match string containing lowercase, uppercase, number, and special character
        val regex = Regex("^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" + "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$")
        if (et_password.text.isNotEmpty() && et_password.text.matches(regex)) {
            // Valid password
            vm.formCheck[indexPassword] = stateValid
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else if (et_password.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexPassword] = stateEmpty
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else {
            // Invalid password
            vm.formCheck[indexPassword] = stateInvalid
            tv_label_password_error.visibility = View.VISIBLE
            cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
            v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapWatermelonRed))
        }
        if (et_retype_password.text.isNotEmpty()) {
            checkRetypedPassword(et_retype_password.hasFocus())
        }
        checkContinueButtonAvailability()
    }

    private fun checkRetypedPassword(hasFocus: Boolean) {
        if (et_password.text.isNotEmpty() && et_password.text.toString() == et_retype_password.text.toString()) {
            // Password matches
            vm.formCheck[indexPasswordRetype] = stateValid
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else if (et_retype_password.text.isEmpty()) {
            // Not filled
            vm.formCheck[indexPasswordRetype] = stateEmpty
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else {
            // Password does not match
            vm.formCheck[indexPasswordRetype] = stateInvalid
            tv_label_retype_password_error.visibility = View.VISIBLE
            cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
            v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapWatermelonRed))
        }
        checkContinueButtonAvailability()
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
        } else {
            view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_dcdcdc_1dp)
        }
        if (view == cl_password) {
            if (hasFocus) {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorAccent))
            } else {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapGreyDc))
            }
        } else if (view == cl_retype_password) {
            if (hasFocus) {
                v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorAccent))
            } else {
                v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapGreyDc))
            }
        }
    }

    private fun checkContinueButtonAvailability() {
        if (vm.formCheck[indexFullName] == stateValid &&
                vm.formCheck[indexUsername] == stateValid &&
                vm.formCheck[indexMobileNumber] == stateValid &&
                (vm.formCheck[indexEmail] == stateEmpty || vm.formCheck[indexEmail] == stateValid) && (
                        (vm.formCheck[indexPassword] == stateEmpty && vm.formCheck[indexPasswordRetype] == stateEmpty) ||
                                (vm.formCheck[indexPassword] == stateValid && vm.formCheck[indexPasswordRetype] == stateValid))) {
            // All forms valid
            enableContinueButton()
        } else {
            // Has invalid forms
            disableContinueButton()
        }
    }

    private fun enableContinueButton() {
        fl_button_continue.background = getDrawable(R.drawable.tap_bg_primary_primarydark_stroke_primarydark_1dp_rounded_6dp_ripple)
        fl_button_continue.setOnClickListener { register() }
    }

    private fun disableContinueButton() {
        fl_button_continue.background = getDrawable(R.drawable.tap_bg_gradient_cecece_9b9b9b_rounded_8dp_stroke_cecece_1dp)
        fl_button_continue.setOnClickListener(null)
    }

    private fun clearAllFocus() {
        TAPUtils.getInstance().dismissKeyboard(this)
        fl_container.clearFocus()
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageView) {
        val cursorPosition = editText.selectionStart
        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            //button.setImageDrawable(getDrawable(R.drawable.tap_ic_view_orange))
            button.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorPrimaryDark))
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            //button.setImageDrawable(getDrawable(R.drawable.tap_ic_view_grey))
            button.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapGrey9b))
        }
        editText.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)
        editText.setSelection(cursorPosition)
    }

    private fun register() {
        TAPDataManager.getInstance().register(
                et_full_name.text.toString(),
                et_username.text.toString(),
                vm.countryID,
                et_mobile_number.text.toString(),
                et_email_address.text.toString(),
                et_password.text.toString(),
                registerView
        )
    }

    private fun showErrorDialog(message: String) {
        vm.isUpdatingProfile = false
        enableEditing()
        TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.tap_error))
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(true) { }
                .setCancelable(true)
                .show()
    }

    private fun disableEditing() {
        iv_button_back.setOnClickListener(null)
        civ_profile_picture.setOnClickListener(null)
        ll_change_profile_picture.setOnClickListener(null)
        iv_remove_profile_picture.setOnClickListener(null)
        fl_button_continue.setOnClickListener(null)

        iv_button_back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_orange))
        TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_button_back)

        et_full_name.isEnabled = false
        et_username.isEnabled = false
        et_email_address.isEnabled = false
        et_password.isEnabled = false
        et_retype_password.isEnabled = false

        et_full_name.setTextColor(ContextCompat.getColor(this, R.color.tapGrey9b))
        et_username.setTextColor(ContextCompat.getColor(this, R.color.tapGrey9b))
        et_email_address.setTextColor(ContextCompat.getColor(this, R.color.tapGrey9b))
        et_password.setTextColor(ContextCompat.getColor(this, R.color.tapGrey9b))
        et_retype_password.setTextColor(ContextCompat.getColor(this, R.color.tapGrey9b))

        tv_button_continue.visibility = View.GONE
        iv_register_progress.visibility = View.VISIBLE
        TAPUtils.getInstance().rotateAnimateInfinitely(this, iv_register_progress)
    }

    private fun enableEditing() {
        iv_button_back.setOnClickListener { onBackPressed() }
        civ_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        iv_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        fl_button_continue.setOnClickListener { register() }

        iv_button_back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_back_arrow_pumpkin_orange))
        iv_button_back.clearAnimation()

        et_full_name.isEnabled = true
        et_username.isEnabled = true
        et_email_address.isEnabled = true
        et_password.isEnabled = true
        et_retype_password.isEnabled = true

        et_full_name.setTextColor(ContextCompat.getColor(this, R.color.tapBlack19))
        et_username.setTextColor(ContextCompat.getColor(this, R.color.tapBlack19))
        et_email_address.setTextColor(ContextCompat.getColor(this, R.color.tapBlack19))
        et_password.setTextColor(ContextCompat.getColor(this, R.color.tapBlack19))
        et_retype_password.setTextColor(ContextCompat.getColor(this, R.color.tapBlack19))

        tv_button_continue.visibility = View.VISIBLE
        iv_register_progress.visibility = View.GONE
        iv_register_progress.clearAnimation()

    }

    private fun uploadProfilePicture() {
        vm.isUploadingProfilePicture = true
        pb_profile_picture_progress.progress = 0
        TAPFileUploadManager.getInstance().uploadProfilePicture(this@TAPRegisterActivity, vm.profilePictureUri, vm.myUserModel.userID)
    }

    private fun finishRegisterAndOpenRoomList() {
        setResult(RESULT_OK)
        finish()
    }

    private val profilePicturePickerListener = object : TAPAttachmentListener() {

        override fun onCameraSelected() {
            vm.profilePictureUri = TAPUtils.getInstance().takePicture(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_CAMERA)
        }

        override fun onGallerySelected() {
            TAPUtils.getInstance().pickImageFromGallery(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_GALLERY, false)
        }
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (vm.formCheck[indexFullName] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
            }
        } else {
            view.elevation = 0f
            if (vm.formCheck[indexFullName] != stateInvalid) {
                updateEditTextBackground(et_full_name, hasFocus)
            }
        }
    }

    private val usernameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (vm.formCheck[indexUsername] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
            }
        } else {
            view.elevation = 0f
            if (vm.formCheck[indexUsername] != stateInvalid) {
                updateEditTextBackground(et_username, hasFocus)
            }
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (vm.formCheck[indexEmail] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
            }
        } else {
            view.elevation = 0f
            if (vm.formCheck[indexEmail] != stateInvalid) {
                updateEditTextBackground(et_email_address, hasFocus)
            }
        }
    }

    private val passwordFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (vm.formCheck[indexPassword] != stateInvalid) {
                cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
                v_password_separator.setBackgroundColor(resources.getColor(R.color.tapColorAccent))
            }
        } else {
            cl_password.elevation = 0f
            if (vm.formCheck[indexPassword] != stateInvalid) {
                updateEditTextBackground(cl_password, hasFocus)
            }
        }
    }

    private val passwordRetypeFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_retype_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
                cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
                v_retype_password_separator.setBackgroundColor(resources.getColor(R.color.tapColorAccent))
            }
        } else {
            cl_retype_password.elevation = 0f
            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
                updateEditTextBackground(cl_retype_password, hasFocus)
            }
        }
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

    private val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            disableContinueButton()
            checkUsernameTimer.cancel()
            checkUsernameTimer.start()
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

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            disableContinueButton()
            checkPasswordTimer.cancel()
            checkPasswordTimer.start()
        }
    }

    private val passwordRetypeWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            disableContinueButton()
            checkRetypedPasswordTimer.cancel()
            checkRetypedPasswordTimer.start()
        }
    }

    private val checkFullNameTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkFullName(et_full_name.hasFocus())
        }
    }

    private val checkUsernameTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkUsername(et_username.hasFocus())
        }
    }

    private val checkEmailAddressTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkEmailAddress(et_email_address.hasFocus())
        }
    }

    private val checkPasswordTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkPassword(et_password.hasFocus())
        }
    }

    private val checkRetypedPasswordTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            checkRetypedPassword(et_retype_password.hasFocus())
        }
    }

    private val checkUsernameView = object : TapDefaultDataView<TAPCheckUsernameResponse>() {
        override fun onSuccess(response: TAPCheckUsernameResponse?) {
            if (response?.exists == false) {
                vm.formCheck[indexUsername] = stateValid
                tv_label_username_error.visibility = View.GONE
                updateEditTextBackground(et_username, et_username.hasFocus())
            } else {
                setErrorResult(getString(R.string.tap_error_username_exists))
            }
        }

        override fun onError(error: TAPErrorModel?) {
            setErrorResult(error?.message
                    ?: getString(R.string.tap_error_unable_to_verify_username))
        }

        override fun onError(throwable: Throwable?) {
            setErrorResult(getString(R.string.tap_error_unable_to_verify_username))
        }

        override fun endLoading() {
            checkContinueButtonAvailability()
        }

        private fun setErrorResult(message: String) {
            vm.formCheck[indexUsername] = stateInvalid
            tv_label_username_error.text = message
            tv_label_username_error.visibility = View.VISIBLE
            et_username.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
    }

    private val scrollViewListener = ViewTreeObserver.OnScrollChangedListener {
        when (sv_register.scrollY) {
            0 ->
                fl_action_bar.elevation = 0f
            else ->
                fl_action_bar.elevation = TAPUtils.getInstance().dpToPx(2).toFloat()
        }
    }

    private val registerView = object : TapDefaultDataView<TAPRegisterResponse>() {
        override fun startLoading() {
            vm.isUpdatingProfile = true
            disableEditing()
        }

        override fun onSuccess(response: TAPRegisterResponse?) {
            TapTalk.saveAuthTicketAndGetAccessToken(response?.ticket
                    ?: "", object : TAPVerifyOTPInterface {
                override fun verifyOTPSuccessToLogin() {
                    TAPDataManager.getInstance().saveMyCountryCode(vm.countryCallingCode)
                    TAPDataManager.getInstance().saveMyCountryFlagUrl(vm.countryFlagUrl)
                    if (null != vm.profilePictureUri) {
                        uploadProfilePicture()
                    } else {
                        finishRegisterAndOpenRoomList()
                    }
                }

                override fun verifyOTPSuccessToRegister() {

                }

                override fun verifyOTPFailed(errorCode: String?, errorMessage: String?) {
                    TapTalkDialog.Builder(this@TAPRegisterActivity)
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setTitle(getString(R.string.tap_error))
                            .setMessage(errorMessage)
                            .setPrimaryButtonTitle(getString(R.string.tap_retry))
                            .setPrimaryButtonListener(true) {
                                TapTalk.saveAuthTicketAndGetAccessToken(response?.ticket
                                        ?: "", this)
                            }
                            .setCancelable(false)
                            .show()
                }
            })
            vm.isUpdatingProfile = false
        }

        override fun onError(error: TAPErrorModel?) {
            showErrorDialog(error?.message ?: getString(R.string.tap_error_message_general))
        }

        override fun onError(throwable: Throwable?) {
            showErrorDialog(throwable?.message ?: getString(R.string.tap_error_message_general))
        }
    }

    private val uploadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                UploadProgressLoading -> {
                    pb_profile_picture_progress.progress = TAPFileUploadManager.getInstance()
                            .getUploadProgressPercent(vm.myUserModel.userID) ?: 0
                }
                UploadProgressFinish -> {
                    finishRegisterAndOpenRoomList()
                }
                UploadFailed -> {
                    val userID = intent.getStringExtra(TAPDefaultConstant.K_USER_ID)
                    if (null != userID && userID == vm.myUserModel.userID) {
                        vm.isUploadingProfilePicture = false
                        TapTalkDialog.Builder(this@TAPRegisterActivity)
                                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                                .setTitle(getString(R.string.tap_error_unable_to_upload))
                                .setMessage(intent.getStringExtra(UploadFailedErrorMessage)
                                        ?: getString(R.string.tap_error_upload_profile_picture))
                                .setPrimaryButtonTitle(getString(R.string.tap_retry))
                                .setPrimaryButtonListener(true) { uploadProfilePicture() }
                                .setSecondaryButtonTitle(getString(R.string.tap_skip))
                                .setSecondaryButtonListener { finishRegisterAndOpenRoomList() }
                                .setCancelable(false)
                                .show()
                    }
                }
            }
        }
    }
}
