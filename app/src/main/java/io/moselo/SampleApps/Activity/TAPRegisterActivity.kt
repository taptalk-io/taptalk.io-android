package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.URLSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.*
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_CAMERA
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_PROFILE_IMAGE_GALLERY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.*
import io.taptalk.TapTalk.Helper.*
import io.taptalk.TapTalk.Listener.TAPAttachmentListener
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileUploadManager
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCheckUsernameResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIRoomListActivity
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet
import io.taptalk.TapTalk.ViewModel.TAPRegisterViewModel
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.tap_activity_login.tap_custom_snackbar
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

    private val FULL_NAME_MAX_LENGTH = 200
    private val USERNAME_MIN_LENGTH = 4
    private val USERNAME_MAX_LENGTH = 32

    private lateinit var vm: TAPRegisterViewModel

    private lateinit var glide: RequestManager

    companion object {
        fun start(
                context: Activity,
                instanceKey: String,
                countryID: Int,
                countryCallingCode: String,
                countryFlagUrl: String,
                phoneNumber: String
        ) {
            val intent = Intent(context, TAPRegisterActivity::class.java)
            intent.putExtra(INSTANCE_KEY, instanceKey)
            intent.putExtra(COUNTRY_ID, countryID)
            intent.putExtra(COUNTRY_CALLING_CODE, countryCallingCode)
            intent.putExtra(COUNTRY_FLAG_URL, countryFlagUrl)
            intent.putExtra(MOBILE_NUMBER, phoneNumber)
            context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.REGISTER)
            context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_register)

        glide = Glide.with(this)
        initViewModel()
        initView()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
//        checkFullNameTimer.cancel()
        checkUsernameTimer.cancel()
//        checkEmailAddressTimer.cancel()
//        checkPasswordTimer.cancel()
//        checkRetypedPasswordTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        TAPBroadcastManager.unregister(this@TAPRegisterActivity, uploadBroadcastReceiver)
    }

    override fun onBackPressed() {
        if (vm.isUpdatingProfile || vm.isUploadingProfilePicture) {
            return
        }
        if (tv_phone_number.text.isNotEmpty() ||
            et_full_name.text.isNotEmpty() ||
            et_username.text.isNotEmpty() ||
            et_email_address.text.isNotEmpty()
        ) {
            showPopupDiscardChanges()
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (TAPUtils.allPermissionsGranted(grantResults)) {
            when (requestCode) {
                PERMISSION_CAMERA_CAMERA, PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA -> {
                    vm.profilePictureUri = TAPUtils.takePicture(instanceKey, this@TAPRegisterActivity, PICK_PROFILE_IMAGE_CAMERA)
                }
                PERMISSION_READ_EXTERNAL_STORAGE_GALLERY -> {
                    TAPUtils.pickImageFromGallery(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_GALLERY, false)
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
                        if (null != intent?.data) {
                            vm.profilePictureUri = intent.data
                        }
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
        vm = ViewModelProvider(this, TAPRegisterViewModel.TAPRegisterViewModelFactory(application, instanceKey)).get(TAPRegisterViewModel::class.java)
        vm.countryID = intent.getIntExtra(COUNTRY_ID, 1)
        vm.countryCallingCode = intent.getStringExtra(COUNTRY_CALLING_CODE)
        vm.countryFlagUrl = intent.getStringExtra(COUNTRY_FLAG_URL)
    }

    @SuppressLint("PrivateResource")
    private fun initView() {
        window?.setBackgroundDrawable(null)

        et_full_name.imeOptions = EditorInfo.IME_ACTION_NEXT
        et_full_name.setRawInputType(InputType.TYPE_CLASS_TEXT)

        et_full_name.onFocusChangeListener = fullNameFocusListener
        et_username.onFocusChangeListener = usernameFocusListener
        et_email_address.onFocusChangeListener = emailAddressFocusListener
//        et_password.onFocusChangeListener = passwordFocusListener
//        et_retype_password.onFocusChangeListener = passwordRetypeFocusListener

        et_full_name.addTextChangedListener(fullNameWatcher)
        et_username.addTextChangedListener(usernameWatcher)
        et_email_address.addTextChangedListener(emailWatcher)
//        et_password.addTextChangedListener(passwordWatcher)
//        et_retype_password.addTextChangedListener(passwordRetypeWatcher)

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                    .apply(RequestOptions().placeholder(R.drawable.tap_ic_default_flag))
                    .into(iv_country_flag)
        }

        // Obtain text field style attributes
        val typedArray = obtainStyledAttributes(R.style.tapFormTextFieldStyle, R.styleable.TextAppearance)
        vm.textFieldFontColor = typedArray.getColor(R.styleable.TextAppearance_android_textColor, -1)
        vm.textFieldFontColorHint = typedArray.getColor(R.styleable.TextAppearance_android_textColorHint, -1)

        typedArray.recycle()

        // Set password field input type and typeface
        et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_retype_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Set mobile number & disable editing
        tv_country_code.text = String.format("+%s", vm.countryCallingCode)
        tv_phone_number.text = TAPUtils.beautifyPhoneNumber(intent.getStringExtra(MOBILE_NUMBER), false)
        tv_country_code.setTextColor(vm.textFieldFontColorHint)
        tv_phone_number.setTextColor(vm.textFieldFontColorHint)

        // Set url span for privacy policy label
        val spannableString = SpannableString(tv_label_privacy_policy.text)
        spannableString.setSpan(
            URLSpan("https://taptalk.io/privacy-policy/"),
            spannableString.length - 27,
            spannableString.length, 0
        )

        val movementMethod = TAPBetterLinkMovementMethod.newInstance()
            .setOnLinkClickListener { textView: TextView?, url: String?, originalText: String? ->
                false
            }
            .setOnLinkLongClickListener { textView: TextView?, url: String?, originalText: String? ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(url, url)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@TAPRegisterActivity, "Link Copied", Toast.LENGTH_SHORT).show()
                true
            }
        tv_label_privacy_policy.text = spannableString
        tv_label_privacy_policy.movementMethod = movementMethod

        TAPUtils.showKeyboard(this, et_full_name)

        fl_container.setOnClickListener { clearAllFocus() }
        cl_form_container.setOnClickListener { clearAllFocus() }
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        fl_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        iv_view_password.setOnClickListener { togglePasswordVisibility(et_password, iv_view_password) }
        iv_view_password_retype.setOnClickListener { togglePasswordVisibility(et_retype_password, iv_view_password_retype) }
        cb_privacy_policy.setOnCheckedChangeListener { _, _ -> checkPrivacyPolicy() }
        et_retype_password.setOnEditorActionListener { v, a, e -> cl_button_continue.callOnClick() }

        enableContinueButton()

        // Temporarily removed password
        tv_label_password.visibility = View.GONE
        tv_label_password_optional.visibility = View.GONE
        cl_password.visibility = View.GONE
        tv_label_password_guide.visibility = View.GONE
        tv_label_password_error.visibility = View.GONE
        tv_label_retype_password.visibility = View.GONE
        tv_label_retype_password_error.visibility = View.GONE
        cl_retype_password.visibility = View.GONE
        et_email_address.setOnEditorActionListener { v, a, e -> cl_button_continue.callOnClick() }
    }

    private fun registerBroadcastReceiver() {
        TAPBroadcastManager.register(this@TAPRegisterActivity, uploadBroadcastReceiver,
                UploadProgressLoading, UploadProgressFinish, UploadFailed)
    }

    private fun showProfilePicturePickerBottomSheet() {
        TAPUtils.dismissKeyboard(this@TAPRegisterActivity)
        TAPAttachmentBottomSheet(instanceKey, true, profilePicturePickerListener).show(supportFragmentManager, "")
    }

    private fun removeProfilePicture() {
        vm.profilePictureUri = null
        reloadProfilePicture(false)
    }

    private fun reloadProfilePicture(showErrorMessage: Boolean) {
        if (null == vm.profilePictureUri) {
            iv_profile_picture_placeholder.visibility = View.VISIBLE
            civ_profile_picture.visibility = View.INVISIBLE
            glide.load(R.drawable.tap_img_default_avatar).into(civ_profile_picture)
            tv_label_change_profile_picture.text = getString(R.string.tap_upload_image)
//            fl_remove_profile_picture.visibility = View.GONE
            if (showErrorMessage) {
                Toast.makeText(this@TAPRegisterActivity, getString(R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            iv_profile_picture_placeholder.visibility = View.INVISIBLE
            civ_profile_picture.visibility = View.VISIBLE
            glide.load(vm.profilePictureUri).into(civ_profile_picture)
            tv_label_change_profile_picture.text = getString(R.string.tap_change)
//            fl_remove_profile_picture.visibility = View.VISIBLE
        }
    }

    private fun validateFullName(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && et_full_name.text.isEmpty()) {
            // Full name empty
            setFullNameError(getString(R.string.tap_this_field_is_required))
            return false
        }
        if (et_full_name.text.length > FULL_NAME_MAX_LENGTH) {
            // Exceeds max length
            setFullNameError(String.format(getString(R.string.tap_error_register_full_name_max), FULL_NAME_MAX_LENGTH))
            return false
        }
        if (et_full_name.text.isNotEmpty() && !et_full_name.text.matches(Regex("[A-Za-z ]*"))) {
            // Invalid full name
            setFullNameError(getString(R.string.tap_error_invalid_full_name))
            return false
        }
        ll_full_name_error.visibility = View.GONE
        updateEditTextBackground(et_full_name, et_full_name.hasFocus())
        vm.formCheck[indexFullName] = stateValid
        return true
    }

    private fun validateUsername(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && et_username.text.isEmpty()) {
            // Username empty
            setUsernameError(getString(R.string.tap_this_field_is_required))
            return false
        }
        val firstChar = if (et_username.text.isNullOrEmpty()) Character.MIN_VALUE else et_username.text[0]
        if (firstChar.isDigit() || firstChar == '.' || firstChar == '_') {
            // Starts with number or symbol
            setUsernameError(getString(R.string.tap_error_register_username_start))
            return false
        }
        if ((checkMinLength && et_username.text.length < USERNAME_MIN_LENGTH) || et_username.text.length > USERNAME_MAX_LENGTH) {
            // Invalid username length
            setUsernameError(String.format(getString(R.string.tap_error_register_username_length), USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH))
            return false
        }
        if (et_username.text.isNotEmpty()) {
            if (!et_username.text.matches(Regex("[a-zA-Z0-9._]*"))) {
                // Invalid symbol
                setUsernameError(getString(R.string.tap_error_register_username_symbol))
                return false
            }
            if (et_username.text.contains("..") || et_username.text.contains("__") || et_username.text.contains("._") || et_username.text.contains("_.")) {
                // Consecutive symbols
                setUsernameError(getString(R.string.tap_error_register_username_consecutive_symbols))
                return false
            }
            if (et_username.text.endsWith('.') || et_username.text.endsWith('_')) {
                // Ends with symbol
                setUsernameError(getString(R.string.tap_error_register_username_end))
                return false
            }
        }
        ll_username_error.visibility = View.GONE
        iv_username_status_icon.visibility = View.GONE
        updateEditTextBackground(et_username, et_username.hasFocus())
        return true
    }

    private fun validateEmailAddress(): Boolean {
        if (et_email_address.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            // Invalid email address
            setEmailAddressError(getString(R.string.tap_error_invalid_email_address))
            return false
        }
        ll_email_address_error.visibility = View.GONE
        updateEditTextBackground(et_email_address, et_email_address.hasFocus())
        vm.formCheck[indexEmail] = stateValid
        return true
    }

    private fun validateForm(): Boolean {
        val isFullNameValid = validateFullName()
        val isEmailValid = validateEmailAddress()
        if (!vm.isUsernameValid) {
            validateUsername(true)
        }

        return isFullNameValid && vm.isUsernameValid && isEmailValid && cb_privacy_policy.isChecked
    }

    private fun setFullNameError(errorMessage: String) {
        ll_full_name_error.visibility = View.VISIBLE
        tv_label_full_name_error.text = errorMessage
        et_full_name.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        vm.formCheck[indexFullName] = stateInvalid
    }

    private fun setUsernameError(errorMessage: String) {
        ll_username_error.visibility = View.VISIBLE
        tv_label_username_error.text = errorMessage
        et_username.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        if (errorMessage != getString(R.string.tap_this_field_is_required)) {
            iv_username_status_icon.visibility = View.VISIBLE
            iv_username_status_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_circle_red))
        }
        else {
            iv_username_status_icon.visibility = View.GONE
        }
        vm.formCheck[indexUsername] = stateInvalid
    }

    private fun setEmailAddressError(errorMessage: String) {
        ll_email_address_error.visibility = View.VISIBLE
        tv_label_email_address_error.text = errorMessage
        et_email_address.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
        vm.formCheck[indexEmail] = stateInvalid
    }

//    private fun checkPassword(hasFocus: Boolean) {
//        // Regex to match string containing lowercase, uppercase, number, and special character
//        val regex = Regex("^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" + "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$")
//        if (et_password.text.isNotEmpty() && et_password.text.matches(regex)) {
//            // Valid password
//            vm.formCheck[indexPassword] = stateValid
//            tv_label_password_error.visibility = View.GONE
//            updateEditTextBackground(cl_password, hasFocus)
//        } else if (et_password.text.isEmpty()) {
//            // Not filled
////            AnalyticsManager.getInstance(instanceKey).trackEvent("Password Empty")
//            vm.formCheck[indexPassword] = stateEmpty
//            tv_label_password_error.visibility = View.GONE
//            updateEditTextBackground(cl_password, hasFocus)
//        } else {
//            // Invalid password
////            AnalyticsManager.getInstance(instanceKey).trackEvent("Password Invalid")
//            vm.formCheck[indexPassword] = stateInvalid
//            tv_label_password_error.visibility = View.VISIBLE
//            cl_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
//            v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorError))
//        }
//        if (et_retype_password.text.isNotEmpty()) {
//            checkRetypedPassword(et_retype_password.hasFocus())
//        }
//        checkContinueButtonAvailability()
//    }
//
//    private fun checkRetypedPassword(hasFocus: Boolean) {
//        if (et_password.text.isNotEmpty() && et_password.text.toString() == et_retype_password.text.toString()) {
//            // Password matches
//            vm.formCheck[indexPasswordRetype] = stateValid
//            tv_label_retype_password_error.visibility = View.GONE
//            updateEditTextBackground(cl_retype_password, hasFocus)
//        } else if (et_retype_password.text.isEmpty()) {
//            // Not filled
//            vm.formCheck[indexPasswordRetype] = stateEmpty
//            tv_label_retype_password_error.visibility = View.GONE
//            updateEditTextBackground(cl_retype_password, hasFocus)
//        } else {
//            // Password does not match
//            vm.formCheck[indexPasswordRetype] = stateInvalid
//            tv_label_retype_password_error.visibility = View.VISIBLE
//            cl_retype_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_error)
//            v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorError))
//        }
//        checkContinueButtonAvailability()
//    }

    private fun checkPrivacyPolicy() {
        if (cb_privacy_policy.isChecked) {
            cb_privacy_policy.animate().alpha(1f).setDuration(200L).start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cb_privacy_policy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorPrimary))
            }
            //tv_label_privacy_policy_error.visibility = View.GONE
        }
        else {
            cb_privacy_policy.animate().alpha(0.2f).setDuration(200L).start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cb_privacy_policy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapBlack19))
            }
            //tv_label_privacy_policy_error.visibility = View.GONE
        }
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_light)
        }
        if (view == cl_password) {
            if (hasFocus) {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
            } else {
                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1910))
            }
        } else if (view == cl_retype_password) {
            if (hasFocus) {
                v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
            } else {
                v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1910))
            }
        }
    }

//    private fun checkContinueButtonAvailability() {
//        if (vm.formCheck[indexFullName] == stateValid &&
//            vm.formCheck[indexUsername] == stateValid &&
//            vm.formCheck[indexMobileNumber] == stateValid &&
//            (vm.formCheck[indexEmail] == stateEmpty || vm.formCheck[indexEmail] == stateValid) && (
//            (vm.formCheck[indexPassword] == stateEmpty && vm.formCheck[indexPasswordRetype] == stateEmpty) ||
//            (vm.formCheck[indexPassword] == stateValid && vm.formCheck[indexPasswordRetype] == stateValid))
//        ) {
//            // All forms valid
//            enableContinueButton()
//        } else {
//            // Has invalid forms
//            disableContinueButton()
//        }
//    }

    private fun enableContinueButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cl_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active_ripple)
        }
        else {
            cl_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_active)
        }
        iv_button_icon.visibility = View.VISIBLE
        pb_button_loading.visibility = View.INVISIBLE
        cl_button_continue.setOnClickListener { register() }
    }

    private fun disableContinueButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cl_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        }
        else {
            cl_button_continue.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_button_inactive)
        }
        iv_button_icon.visibility = View.INVISIBLE
        pb_button_loading.visibility = View.VISIBLE
        cl_button_continue.setOnClickListener(null)
    }

    private fun clearAllFocus() {
        TAPUtils.dismissKeyboard(this)
        fl_container.clearFocus()
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageView) {
        val cursorPosition = editText.selectionStart
        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapIconViewPasswordActive)))
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapIconViewPasswordInactive)))
        }
        editText.setSelection(cursorPosition)
    }

    private fun register() {
        if (!validateForm()) {
            if (!cb_privacy_policy.isChecked) {
                showErrorSnackbar(getString(R.string.tap_error_register_privacy_policy))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cb_privacy_policy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorError))
                    cb_privacy_policy.animate().alpha(1f).setDuration(200L).start()
                }
            }
            return
        }
        TAPDataManager.getInstance(instanceKey).register(
                et_full_name.text.toString(),
                et_username.text.toString(),
                vm.countryID,
                tv_phone_number.text.toString().replace(" ", ""),
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
        TAPUtils.dismissKeyboard(this, et_full_name)
        TAPUtils.dismissKeyboard(this, et_username)
        TAPUtils.dismissKeyboard(this, et_email_address)

        ll_change_profile_picture.setOnClickListener(null)
        fl_remove_profile_picture.setOnClickListener(null)
        disableContinueButton()

        et_full_name.isEnabled = false
        et_username.isEnabled = false
        et_email_address.isEnabled = false
        et_password.isEnabled = false
        et_retype_password.isEnabled = false
        cb_privacy_policy.isEnabled = false

        tv_label_change_profile_picture.setTextColor(vm.textFieldFontColorHint)
        ImageViewCompat.setImageTintList(iv_edit_profile_picture_icon, ColorStateList.valueOf(vm.textFieldFontColorHint))

        tv_label_full_name.setTextColor(vm.textFieldFontColorHint)
        tv_label_username.setTextColor(vm.textFieldFontColorHint)
        tv_label_mobile_number.setTextColor(vm.textFieldFontColorHint)
        tv_label_email_address.setTextColor(vm.textFieldFontColorHint)
        tv_label_privacy_policy.setTextColor(vm.textFieldFontColorHint)
        tv_label_privacy_policy.setLinkTextColor(ContextCompat.getColor(this, R.color.tapTransparentBlack1960))
        tv_button_continue.setTextColor(vm.textFieldFontColorHint)

        et_full_name.setTextColor(vm.textFieldFontColorHint)
        et_username.setTextColor(vm.textFieldFontColorHint)
        et_email_address.setTextColor(vm.textFieldFontColorHint)
        et_password.setTextColor(vm.textFieldFontColorHint)
        et_retype_password.setTextColor(vm.textFieldFontColorHint)

        et_full_name.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_disabled)
        et_username.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_disabled)
        et_email_address.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_disabled)
        et_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_disabled)
        et_retype_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_disabled)

        iv_username_status_icon.alpha = 0.4f
        ImageViewCompat.setImageTintList(iv_username_status_icon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapBlack19)))

        cb_privacy_policy.alpha = 0.4f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cb_privacy_policy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapBlack19))
        }
    }

    private fun enableEditing() {
        ll_change_profile_picture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        fl_remove_profile_picture.setOnClickListener { removeProfilePicture() }
        enableContinueButton()

        et_full_name.isEnabled = true
        et_username.isEnabled = true
        et_email_address.isEnabled = true
        et_password.isEnabled = true
        et_retype_password.isEnabled = true
        cb_privacy_policy.isEnabled = true

        tv_label_change_profile_picture.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        ImageViewCompat.setImageTintList(iv_edit_profile_picture_icon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapIconChangePicture)))

        tv_label_full_name.setTextColor(vm.textFieldFontColor)
        tv_label_username.setTextColor(vm.textFieldFontColor)
        tv_label_mobile_number.setTextColor(vm.textFieldFontColor)
        tv_label_email_address.setTextColor(vm.textFieldFontColor)
        tv_label_privacy_policy.setTextColor(vm.textFieldFontColor)
        tv_label_privacy_policy.setLinkTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        tv_button_continue.setTextColor(ContextCompat.getColor(this, R.color.tapButtonLabelColor))

        et_full_name.setTextColor(vm.textFieldFontColor)
        et_username.setTextColor(vm.textFieldFontColor)
        et_email_address.setTextColor(vm.textFieldFontColor)
        et_password.setTextColor(vm.textFieldFontColor)
        et_retype_password.setTextColor(vm.textFieldFontColor)

        updateEditTextBackground(et_full_name, et_full_name.hasFocus())
        updateEditTextBackground(et_username, et_username.hasFocus())
        updateEditTextBackground(et_email_address, et_email_address.hasFocus())
        updateEditTextBackground(et_password, et_password.hasFocus())
        updateEditTextBackground(et_retype_password, et_retype_password.hasFocus())

        iv_username_status_icon.alpha = 1f
        ImageViewCompat.setImageTintList(iv_username_status_icon, null)

        checkPrivacyPolicy()
    }

    private fun uploadProfilePicture() {
        vm.isUploadingProfilePicture = true
        pb_profile_picture_progress.progress = 0
        TAPFileUploadManager.getInstance(instanceKey).uploadProfilePicture(this@TAPRegisterActivity, vm.profilePictureUri, vm.myUserModel.userID)
    }

    private fun finishRegisterAndOpenRoomList() {
        TapUIRoomListActivity.start(this, instanceKey)
        finish()
    }

    private fun showPopupDiscardChanges() {
        TapTalkDialog.Builder(this)
                .setTitle(String.format("%s?",getString(R.string.tap_discard_changes)))
                .setMessage(getString(R.string.tap_unsaved_progress_description))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_discard_changes))
                .setPrimaryButtonListener {
                    finish()
                    overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
                }
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setSecondaryButtonListener(true){}
                .show()
    }

    private val profilePicturePickerListener = object : TAPAttachmentListener(instanceKey) {

        override fun onCameraSelected() {
            vm.profilePictureUri = TAPUtils.takePicture(instanceKey, this@TAPRegisterActivity, PICK_PROFILE_IMAGE_CAMERA)
        }

        override fun onGallerySelected() {
            TAPUtils.pickImageFromGallery(this@TAPRegisterActivity, PICK_PROFILE_IMAGE_GALLERY, false)
        }
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (vm.formCheck[indexFullName] != stateInvalid) {
            updateEditTextBackground(view, hasFocus)
        }
    }

    private val usernameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (vm.formCheck[indexUsername] != stateInvalid) {
            updateEditTextBackground(view, hasFocus)
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (vm.formCheck[indexEmail] != stateInvalid) {
            updateEditTextBackground(view, hasFocus)
        }
    }

//    private val passwordFocusListener = View.OnFocusChangeListener { view, hasFocus ->
//        if (hasFocus) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                cl_password.elevation = TAPUtils.dpToPx(4).toFloat()
//            }
//            if (vm.formCheck[indexPassword] != stateInvalid) {
//                cl_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
//                v_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                cl_password.elevation = 0f
//            }
//            if (vm.formCheck[indexPassword] != stateInvalid) {
//                updateEditTextBackground(cl_password, hasFocus)
//            }
//        }
//    }
//
//    private val passwordRetypeFocusListener = View.OnFocusChangeListener { view, hasFocus ->
//        if (hasFocus) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                cl_retype_password.elevation = TAPUtils.dpToPx(4).toFloat()
//            }
//            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
//                cl_retype_password.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
//                v_retype_password_separator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapTextFieldBorderActiveColor))
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                cl_retype_password.elevation = 0f
//            }
//            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
//                updateEditTextBackground(cl_retype_password, hasFocus)
//            }
//        }
//    }

    private val fullNameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            validateFullName(ll_full_name_error?.visibility == View.VISIBLE && et_full_name.text.isNotEmpty())
        }
    }

    private val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm.isUsernameValid = false
            checkUsernameTimer.cancel()
            TAPDataManager.getInstance(instanceKey).cancelCheckUsernameApiCall()
            if (validateUsername(ll_username_error?.visibility == View.VISIBLE && et_username.text.length >= USERNAME_MIN_LENGTH)) {
                if (et_username.text.length >= USERNAME_MIN_LENGTH) {
                    checkUsernameTimer.start()
                }
            }
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (ll_email_address_error?.visibility == View.VISIBLE) {
                validateEmailAddress()
            }
        }
    }

//    private val passwordWatcher = object : TextWatcher {
//        override fun afterTextChanged(p0: Editable?) {}
//
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//    }
//
//    private val passwordRetypeWatcher = object : TextWatcher {
//        override fun afterTextChanged(p0: Editable?) {}
//
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//    }

    private val checkUsernameTimer = object : CountDownTimer(1000L, 100L) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            val username = et_username.text.toString()
            TAPDataManager.getInstance(instanceKey).checkUsernameExists(et_username.text.toString(), object : TAPDefaultDataView<TAPCheckUsernameResponse>() {
                override fun onSuccess(response: TAPCheckUsernameResponse?) {
                    if (username != et_username.text.toString()) {
                        return
                    }
                    if (response?.exists == false) {
                        vm.isUsernameValid = true
                        ll_username_error.visibility = View.GONE
                        iv_username_status_icon.visibility = View.VISIBLE
                        iv_username_status_icon.setImageDrawable(ContextCompat.getDrawable(this@TAPRegisterActivity, R.drawable.tap_ic_check_circle_green))
                        updateEditTextBackground(et_username, et_username.hasFocus())
                        vm.formCheck[indexUsername] = stateValid
                    }
                    else {
                        setUsernameError(getString(R.string.tap_error_register_username_taken))
                    }
                }

                override fun onError(error: TAPErrorModel?) {
                    setUsernameError(error?.message ?: getString(R.string.tap_error_unable_to_verify_username))
                }

                override fun onError(throwable: Throwable?) {
                    setUsernameError(getString(R.string.tap_error_unable_to_verify_username))
                }
            })
        }
    }

    private val registerView = object : TAPDefaultDataView<TAPRegisterResponse>() {
        override fun startLoading() {
            vm.isUpdatingProfile = true
            disableEditing()
        }

        override fun onSuccess(response: TAPRegisterResponse?) {
//            AnalyticsManager.getInstance(instanceKey).identifyUser()
//            AnalyticsManager.getInstance(instanceKey).trackEvent("Register Success")
            TapTalk.authenticateWithAuthTicket(instanceKey,response?.ticket ?: "", true,
                    object : TapCommonListener() {
                        override fun onSuccess(successMessage: String?) {
//                            AnalyticsManager.getInstance(instanceKey).identifyUser()
                            TAPDataManager.getInstance(instanceKey).saveMyCountryCode(vm.countryCallingCode)
                            TAPDataManager.getInstance(instanceKey).saveMyCountryFlagUrl(vm.countryFlagUrl)
                            if (null != vm.profilePictureUri) {
                                uploadProfilePicture()
                            } else {
                                finishRegisterAndOpenRoomList()
                            }
                        }

                        override fun onError(errorCode: String?, errorMessage: String?) {
                            TapTalkDialog.Builder(this@TAPRegisterActivity)
                                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                    .setTitle(getString(R.string.tap_error))
                                    .setMessage(errorMessage)
                                    .setPrimaryButtonTitle(getString(R.string.tap_retry))
                                    .setPrimaryButtonListener(true) {
                                        TapTalk.authenticateWithAuthTicket(instanceKey,response?.ticket
                                                ?: "", true, this)
                                    }
                                    .setCancelable(false)
                                    .show()
                        }
                    }
            )
            vm.isUpdatingProfile = false
        }

        override fun onError(error: TAPErrorModel?) {
//            AnalyticsManager.getInstance(instanceKey).trackErrorEvent("Register Failed", error?.code, error?.message)
            onError(error?.message ?: getString(R.string.tap_error_message_general))
        }

        override fun onError(errorMessage: String?) {
            vm.isUpdatingProfile = false
            enableEditing()
            if (TAPNetworkStateManager.getInstance("").hasNetworkConnection(this@TAPRegisterActivity)) {
                showErrorDialog(errorMessage ?: getString(R.string.tap_error_message_general))
            }
            else {
                TAPUtils.showNoInternetErrorDialog(this@TAPRegisterActivity)
            }
        }
    }

    private val uploadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                UploadProgressLoading -> {
                    pb_profile_picture_progress.progress = TAPFileUploadManager.getInstance(instanceKey)
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

    private fun showErrorSnackbar(errorMessage: String?) {
        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            tap_custom_snackbar?.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(R.string.tap_error_message_general)
            )
        }
        else {
            tap_custom_snackbar?.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                R.drawable.tap_ic_wifi_off_red,
                R.string.tap_error_check_your_network
            )
        }
    }
}
