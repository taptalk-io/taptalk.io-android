package io.moselo.SampleApps.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.res.ColorStateList
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
import io.taptalk.TapTalkSample.databinding.TapActivityRegisterBinding

class TAPRegisterActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityRegisterBinding
    private lateinit var vm: TAPRegisterViewModel
    private lateinit var glide: RequestManager

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
            context.overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_slide_up, io.taptalk.TapTalk.R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityRegisterBinding.inflate(layoutInflater)
        setContentView(vb.root)

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
        if (vb.tvPhoneNumber.text.isNotEmpty() ||
            vb.etFullName.text.isNotEmpty() ||
            vb.etUsername.text.isNotEmpty() ||
            vb.etEmailAddress.text.isNotEmpty()
        ) {
            showPopupDiscardChanges()
            return
        }
        try {
            super.onBackPressed()
            overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_down)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
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
                        val uri = intent?.data
                        if (uri == null || !TAPFileUtils.getMimeTypeFromUri(this, uri).contains("image")) {
                            showErrorSnackbar(getString(io.taptalk.TapTalk.R.string.tap_error_invalid_file_format))
                            return
                        }
                        vm.profilePictureUri = uri
                        reloadProfilePicture(true)
                    }
                }
            }
        }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapColorPrimary))
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

        vb.etFullName.imeOptions = EditorInfo.IME_ACTION_NEXT
        vb.etFullName.setRawInputType(InputType.TYPE_CLASS_TEXT)

        vb.etFullName.onFocusChangeListener = fullNameFocusListener
        vb.etUsername.onFocusChangeListener = usernameFocusListener
        vb.etEmailAddress.onFocusChangeListener = emailAddressFocusListener
//        binding.etPassword.onFocusChangeListener = passwordFocusListener
//        binding.etRetypePassword.onFocusChangeListener = passwordRetypeFocusListener

        vb.etFullName.addTextChangedListener(fullNameWatcher)
        vb.etUsername.addTextChangedListener(usernameWatcher)
        vb.etEmailAddress.addTextChangedListener(emailWatcher)
//        binding.etPassword.addTextChangedListener(passwordWatcher)
//        binding.etRetypePassword.addTextChangedListener(passwordRetypeWatcher)

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                .apply(RequestOptions().placeholder(io.taptalk.TapTalk.R.drawable.tap_ic_default_flag))
                .into(vb.ivCountryFlag)
        }

        vm.textFieldFontColor = ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapFormTextFieldColor)
        vm.textFieldFontColorHint = ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapFormTextFieldPlaceholderColor)

        // Set password field input type and typeface
        vb.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        vb.etRetypePassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Set mobile number & disable editing
        vb.tvCountryCode.text = String.format("+%s", vm.countryCallingCode)
        vb.tvPhoneNumber.text = TAPUtils.beautifyPhoneNumber(intent.getStringExtra(MOBILE_NUMBER), false)
        vb.tvCountryCode.setTextColor(vm.textFieldFontColorHint)
        vb.tvPhoneNumber.setTextColor(vm.textFieldFontColorHint)

        // Set url span for privacy policy label
        val spannableString = SpannableString(vb.tvLabelPrivacyPolicy.text)
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
        vb.tvLabelPrivacyPolicy.text = spannableString
        vb.tvLabelPrivacyPolicy.movementMethod = movementMethod

        vb.flContainer.setOnClickListener { clearAllFocus() }
        vb.clFormContainer.setOnClickListener { clearAllFocus() }
        vb.llChangeProfilePicture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        vb.flRemoveProfilePicture.setOnClickListener { removeProfilePicture() }
        vb.ivViewPassword.setOnClickListener { togglePasswordVisibility(vb.etPassword, vb.ivViewPassword) }
        vb.ivViewPasswordRetype.setOnClickListener { togglePasswordVisibility(vb.etRetypePassword, vb.ivViewPasswordRetype) }
        vb.cbPrivacyPolicy.setOnCheckedChangeListener { _, _ -> checkPrivacyPolicy() }
        vb.etRetypePassword.setOnEditorActionListener { v, a, e -> vb.clButtonContinue.callOnClick() }

        enableContinueButton()

        // Temporarily removed password
        vb.tvLabelPassword.visibility = View.GONE
        vb.tvLabelPasswordOptional.visibility = View.GONE
        vb.clPassword.visibility = View.GONE
        vb.tvLabelPasswordGuide.visibility = View.GONE
        vb.tvLabelPasswordError.visibility = View.GONE
        vb.tvLabelRetypePassword.visibility = View.GONE
        vb.tvLabelRetypePasswordError.visibility = View.GONE
        vb.clRetypePassword.visibility = View.GONE
        vb.etEmailAddress.setOnEditorActionListener { v, a, e -> vb.clButtonContinue.callOnClick() }
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
            vb.ivProfilePicturePlaceholder.visibility = View.VISIBLE
            vb.civProfilePicture.visibility = View.INVISIBLE
            glide.load(io.taptalk.TapTalk.R.drawable.tap_img_default_avatar).into(vb.civProfilePicture)
            vb.tvLabelChangeProfilePicture.text = getString(io.taptalk.TapTalk.R.string.tap_upload_image)
//            binding.flRemoveProfilePicture.visibility = View.GONE
            if (showErrorMessage) {
                Toast.makeText(this@TAPRegisterActivity, getString(io.taptalk.TapTalk.R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            vb.ivProfilePicturePlaceholder.visibility = View.INVISIBLE
            vb.civProfilePicture.visibility = View.VISIBLE
            glide.load(vm.profilePictureUri).into(vb.civProfilePicture)
            vb.tvLabelChangeProfilePicture.text = getString(io.taptalk.TapTalk.R.string.tap_change)
//            binding.flRemoveProfilePicture.visibility = View.VISIBLE
        }
    }

    private fun validateFullName(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && vb.etFullName.text.isEmpty()) {
            // Full name empty
            setFullNameError(getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required))
            return false
        }
        if (vb.etFullName.text.length > FULL_NAME_MAX_LENGTH) {
            // Exceeds max length
            setFullNameError(String.format(getString(R.string.tap_error_register_full_name_max), FULL_NAME_MAX_LENGTH))
            return false
        }
        if (vb.etFullName.text.isNotEmpty() && !vb.etFullName.text.matches(Regex("[A-Za-z ]*"))) {
            // Invalid full name
            setFullNameError(getString(io.taptalk.TapTalk.R.string.tap_error_invalid_full_name))
            return false
        }
        vb.llFullNameError.visibility = View.GONE
        updateEditTextBackground(vb.etFullName, vb.etFullName.hasFocus())
        vm.formCheck[indexFullName] = stateValid
        return true
    }

    private fun validateUsername(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && vb.etUsername.text.isEmpty()) {
            // Username empty
            setUsernameError(getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required))
            return false
        }
        val firstChar = if (vb.etUsername.text.isNullOrEmpty()) Character.MIN_VALUE else vb.etUsername.text[0]
        if (firstChar.isDigit() || firstChar == '.' || firstChar == '_') {
            // Starts with number or symbol
            setUsernameError(getString(R.string.tap_error_register_username_start))
            return false
        }
        if ((checkMinLength && vb.etUsername.text.length < USERNAME_MIN_LENGTH) || vb.etUsername.text.length > USERNAME_MAX_LENGTH) {
            // Invalid username length
            setUsernameError(String.format(getString(R.string.tap_error_register_username_length), USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH))
            return false
        }
        if (vb.etUsername.text.isNotEmpty()) {
            if (!vb.etUsername.text.matches(Regex("[a-zA-Z0-9._]*"))) {
                // Invalid symbol
                setUsernameError(getString(R.string.tap_error_register_username_symbol))
                return false
            }
            if (vb.etUsername.text.contains("..") || vb.etUsername.text.contains("__") || vb.etUsername.text.contains("._") || vb.etUsername.text.contains("_.")) {
                // Consecutive symbols
                setUsernameError(getString(R.string.tap_error_register_username_consecutive_symbols))
                return false
            }
            if (vb.etUsername.text.endsWith('.') || vb.etUsername.text.endsWith('_')) {
                // Ends with symbol
                setUsernameError(getString(R.string.tap_error_register_username_end))
                return false
            }
        }
        vb.llUsernameError.visibility = View.GONE
        vb.ivUsernameStatusIcon.visibility = View.GONE
        updateEditTextBackground(vb.etUsername, vb.etUsername.hasFocus())
        vm.formCheck[indexUsername] = stateValid
        return true
    }

    private fun validateEmailAddress(): Boolean {
        try {
            if (vb.etEmailAddress.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(vb.etEmailAddress.text).matches()) {
                // Invalid email address
                setEmailAddressError(getString(io.taptalk.TapTalk.R.string.tap_error_invalid_email_address))
                return false
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        vb.llEmailAddressError.visibility = View.GONE
        updateEditTextBackground(vb.etEmailAddress, vb.etEmailAddress.hasFocus())
        vm.formCheck[indexEmail] = stateValid
        return true
    }

    private fun validateForm(): Boolean {
        val isFullNameValid = validateFullName()
        val isEmailValid = validateEmailAddress()
        if (!vm.isUsernameValid) {
            validateUsername(true)
        }

        return isFullNameValid && vm.isUsernameValid && isEmailValid && vb.cbPrivacyPolicy.isChecked
    }

    private fun setFullNameError(errorMessage: String) {
        vb.llFullNameError.visibility = View.VISIBLE
        vb.tvLabelFullNameError.text = errorMessage
        vb.etFullName.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
        vm.formCheck[indexFullName] = stateInvalid
    }

    private fun setUsernameError(errorMessage: String) {
        vb.llUsernameError.visibility = View.VISIBLE
        vb.tvLabelUsernameError.text = errorMessage
        vb.etUsername.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
        if (errorMessage != getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required)) {
            vb.ivUsernameStatusIcon.visibility = View.VISIBLE
            vb.ivUsernameStatusIcon.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_close_circle_red))
        }
        else {
            vb.ivUsernameStatusIcon.visibility = View.GONE
        }
        vm.formCheck[indexUsername] = stateInvalid
    }

    private fun setEmailAddressError(errorMessage: String) {
        vb.llEmailAddressError.visibility = View.VISIBLE
        vb.tvLabelEmailAddressError.text = errorMessage
        vb.etEmailAddress.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
        vm.formCheck[indexEmail] = stateInvalid
    }

//    private fun checkPassword(hasFocus: Boolean) {
//        // Regex to match string containing lowercase, uppercase, number, and special character
//        val regex = Regex("^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" + "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$")
//        if (binding.etPassword.text.isNotEmpty() && binding.etPassword.text.matches(regex)) {
//            // Valid password
//            vm.formCheck[indexPassword] = stateValid
//            tv_label_password_error.visibility = View.GONE
//            updateEditTextBackground(binding.clPassword, hasFocus)
//        } else if (binding.etPassword.text.isEmpty()) {
//            // Not filled
////            AnalyticsManager.getInstance(instanceKey).trackEvent("Password Empty")
//            vm.formCheck[indexPassword] = stateEmpty
//            tv_label_password_error.visibility = View.GONE
//            updateEditTextBackground(binding.clPassword, hasFocus)
//        } else {
//            // Invalid password
////            AnalyticsManager.getInstance(instanceKey).trackEvent("Password Invalid")
//            vm.formCheck[indexPassword] = stateInvalid
//            tv_label_password_error.visibility = View.VISIBLE
//            binding.clPassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
//            binding.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorError))
//        }
//        if (binding.etRetypePassword.text.isNotEmpty()) {
//            checkRetypedPassword(binding.etRetypePassword.hasFocus())
//        }
//        checkContinueButtonAvailability()
//    }
//
//    private fun checkRetypedPassword(hasFocus: Boolean) {
//        if (binding.etPassword.text.isNotEmpty() && binding.etPassword.text.toString() == binding.etRetypePassword.text.toString()) {
//            // Password matches
//            vm.formCheck[indexPasswordRetype] = stateValid
//            tv_label_retype_password_error.visibility = View.GONE
//            updateEditTextBackground(binding.clRetypePassword, hasFocus)
//        } else if (binding.etRetypePassword.text.isEmpty()) {
//            // Not filled
//            vm.formCheck[indexPasswordRetype] = stateEmpty
//            tv_label_retype_password_error.visibility = View.GONE
//            updateEditTextBackground(binding.clRetypePassword, hasFocus)
//        } else {
//            // Password does not match
//            vm.formCheck[indexPasswordRetype] = stateInvalid
//            tv_label_retype_password_error.visibility = View.VISIBLE
//            binding.clRetypePassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
//            binding.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, R.color.tapColorError))
//        }
//        checkContinueButtonAvailability()
//    }

    private fun checkPrivacyPolicy() {
        if (vb.cbPrivacyPolicy.isChecked) {
            vb.cbPrivacyPolicy.animate().alpha(1f).setDuration(200L).start()
            vb.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorPrimary))
        }
        else {
            vb.cbPrivacyPolicy.animate().alpha(0.2f).setDuration(200L).start()
            vb.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19))
        }
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
        }
        if (view == vb.clPassword) {
            if (hasFocus) {
                vb.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
            } else {
                vb.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1910))
            }
        } else if (view == vb.clRetypePassword) {
            if (hasFocus) {
                vb.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
            } else {
                vb.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1910))
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
        vb.clButtonContinue.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_active_ripple)
        vb.ivButtonIcon.visibility = View.VISIBLE
        vb.pbButtonLoading.visibility = View.INVISIBLE
        vb.clButtonContinue.setOnClickListener { register() }
    }

    private fun disableContinueButton() {
        vb.clButtonContinue.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
        vb.ivButtonIcon.visibility = View.INVISIBLE
        vb.pbButtonLoading.visibility = View.VISIBLE
        vb.clButtonContinue.setOnClickListener(null)
    }

    private fun clearAllFocus() {
        TAPUtils.dismissKeyboard(this, vb.etFullName)
        TAPUtils.dismissKeyboard(this, vb.etUsername)
        TAPUtils.dismissKeyboard(this, vb.etEmailAddress)
        TAPUtils.dismissKeyboard(this, vb.etPassword)
        TAPUtils.dismissKeyboard(this, vb.etRetypePassword)
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageView) {
        val cursorPosition = editText.selectionStart
        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapIconViewPasswordActive)))
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapIconViewPasswordInactive)))
        }
        editText.setSelection(cursorPosition)
    }

    private fun register() {
        if (!validateForm()) {
            if (!vb.cbPrivacyPolicy.isChecked) {
                showErrorSnackbar(getString(R.string.tap_error_register_privacy_policy))
                vb.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorError))
                vb.cbPrivacyPolicy.animate().alpha(1f).setDuration(200L).start()
            }
            return
        }
        TAPDataManager.getInstance(instanceKey).register(
                vb.etFullName.text.toString(),
                vb.etUsername.text.toString(),
                vm.countryID,
                vb.tvPhoneNumber.text.toString().replace(" ", ""),
                vb.etEmailAddress.text.toString(),
                vb.etPassword.text.toString(),
                registerView
        )
    }

    private fun showErrorDialog(message: String) {
        vm.isUpdatingProfile = false
        enableEditing()
        TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(io.taptalk.TapTalk.R.string.tap_error))
                .setMessage(message)
                .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok))
                .setPrimaryButtonListener(true) { }
                .setCancelable(true)
                .show()
    }

    private fun disableEditing() {
        clearAllFocus()

        vb.llChangeProfilePicture.setOnClickListener(null)
        vb.flRemoveProfilePicture.setOnClickListener(null)
        disableContinueButton()

        vb.etFullName.isEnabled = false
        vb.etUsername.isEnabled = false
        vb.etEmailAddress.isEnabled = false
        vb.etPassword.isEnabled = false
        vb.etRetypePassword.isEnabled = false
        vb.cbPrivacyPolicy.isEnabled = false

        vb.tvLabelChangeProfilePicture.setTextColor(vm.textFieldFontColorHint)
        ImageViewCompat.setImageTintList(vb.ivEditProfilePictureIcon, ColorStateList.valueOf(vm.textFieldFontColorHint))

        vb.tvLabelFullName.setTextColor(vm.textFieldFontColorHint)
        vb.tvLabelUsername.setTextColor(vm.textFieldFontColorHint)
        vb.tvLabelMobileNumber.setTextColor(vm.textFieldFontColorHint)
        vb.tvLabelEmailAddress.setTextColor(vm.textFieldFontColorHint)
        vb.tvLabelPrivacyPolicy.setTextColor(vm.textFieldFontColorHint)
        vb.tvLabelPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1960))
        vb.tvButtonContinue.setTextColor(vm.textFieldFontColorHint)

        vb.etFullName.setTextColor(vm.textFieldFontColorHint)
        vb.etUsername.setTextColor(vm.textFieldFontColorHint)
        vb.etEmailAddress.setTextColor(vm.textFieldFontColorHint)
        vb.etPassword.setTextColor(vm.textFieldFontColorHint)
        vb.etRetypePassword.setTextColor(vm.textFieldFontColorHint)

        vb.etFullName.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        vb.etUsername.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        vb.etEmailAddress.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        vb.etPassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        vb.etRetypePassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)

        vb.ivUsernameStatusIcon.alpha = 0.4f
        ImageViewCompat.setImageTintList(vb.ivUsernameStatusIcon, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19)))

        vb.cbPrivacyPolicy.alpha = 0.4f
        vb.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19))
    }

    private fun enableEditing() {
        vb.llChangeProfilePicture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        vb.flRemoveProfilePicture.setOnClickListener { removeProfilePicture() }
        enableContinueButton()

        vb.etFullName.isEnabled = true
        vb.etUsername.isEnabled = true
        vb.etEmailAddress.isEnabled = true
        vb.etPassword.isEnabled = true
        vb.etRetypePassword.isEnabled = true
        vb.cbPrivacyPolicy.isEnabled = true

        vb.tvLabelChangeProfilePicture.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        ImageViewCompat.setImageTintList(vb.ivEditProfilePictureIcon, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapIconChangePicture)))

        vb.tvLabelFullName.setTextColor(vm.textFieldFontColor)
        vb.tvLabelUsername.setTextColor(vm.textFieldFontColor)
        vb.tvLabelMobileNumber.setTextColor(vm.textFieldFontColor)
        vb.tvLabelEmailAddress.setTextColor(vm.textFieldFontColor)
        vb.tvLabelPrivacyPolicy.setTextColor(vm.textFieldFontColor)
        vb.tvLabelPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        vb.tvButtonContinue.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapButtonLabelColor))

        vb.etFullName.setTextColor(vm.textFieldFontColor)
        vb.etUsername.setTextColor(vm.textFieldFontColor)
        vb.etEmailAddress.setTextColor(vm.textFieldFontColor)
        vb.etPassword.setTextColor(vm.textFieldFontColor)
        vb.etRetypePassword.setTextColor(vm.textFieldFontColor)

        updateEditTextBackground(vb.etFullName, vb.etFullName.hasFocus())
        updateEditTextBackground(vb.etUsername, vb.etUsername.hasFocus())
        updateEditTextBackground(vb.etEmailAddress, vb.etEmailAddress.hasFocus())
        updateEditTextBackground(vb.etPassword, vb.etPassword.hasFocus())
        updateEditTextBackground(vb.etRetypePassword, vb.etRetypePassword.hasFocus())

        vb.ivUsernameStatusIcon.alpha = 1f
        ImageViewCompat.setImageTintList(vb.ivUsernameStatusIcon, null)

        checkPrivacyPolicy()
    }

    private fun uploadProfilePicture() {
        vm.isUploadingProfilePicture = true
        vb.pbProfilePictureProgress.progress = 0
        TAPFileUploadManager.getInstance(instanceKey).uploadProfilePicture(this@TAPRegisterActivity, vm.profilePictureUri, vm.myUserModel.userID)
    }

    private fun finishRegisterAndOpenRoomList() {
        TapUIRoomListActivity.start(this, instanceKey)
        TAPDataManager.getInstance(instanceKey).checkAndRequestAutoStartPermission(this)
        finish()
    }

    private fun showPopupDiscardChanges() {
        TapTalkDialog.Builder(this)
            .setTitle(String.format("%s?",getString(io.taptalk.TapTalk.R.string.tap_discard_changes)))
            .setMessage(getString(io.taptalk.TapTalk.R.string.tap_unsaved_progress_description))
            .setCancelable(false)
            .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_discard_changes))
            .setPrimaryButtonListener {
                finish()
                overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_down)
            }
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setSecondaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_cancel))
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
//                binding.clPassword.elevation = TAPUtils.dpToPx(4).toFloat()
//            }
//            if (vm.formCheck[indexPassword] != stateInvalid) {
//                binding.clPassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_active)
//                binding.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                binding.clPassword.elevation = 0f
//            }
//            if (vm.formCheck[indexPassword] != stateInvalid) {
//                updateEditTextBackground(binding.clPassword, hasFocus)
//            }
//        }
//    }
//
//    private val passwordRetypeFocusListener = View.OnFocusChangeListener { view, hasFocus ->
//        if (hasFocus) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                binding.clRetypePassword.elevation = TAPUtils.dpToPx(4).toFloat()
//            }
//            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
//                binding.clRetypePassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_active)
//                binding.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                binding.clRetypePassword.elevation = 0f
//            }
//            if (vm.formCheck[indexPasswordRetype] != stateInvalid) {
//                updateEditTextBackground(binding.clRetypePassword, hasFocus)
//            }
//        }
//    }

    private val fullNameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            validateFullName(vb.llFullNameError.visibility == View.VISIBLE && vb.etFullName.text.isNotEmpty())
        }
    }

    private val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm.isUsernameValid = false
            checkUsernameTimer.cancel()
            TAPDataManager.getInstance(instanceKey).cancelCheckUsernameApiCall()
            if (validateUsername(vb.llUsernameError.visibility == View.VISIBLE && vb.etUsername.text.length >= USERNAME_MIN_LENGTH)) {
                if (vb.etUsername.text.length >= USERNAME_MIN_LENGTH) {
                    checkUsernameTimer.start()
                }
            }
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (vb.llEmailAddressError.visibility == View.VISIBLE) {
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
            val username = vb.etUsername.text.toString()
            TAPDataManager.getInstance(instanceKey).checkUsernameExists(vb.etUsername.text.toString(), object : TAPDefaultDataView<TAPCheckUsernameResponse>() {
                override fun onSuccess(response: TAPCheckUsernameResponse?) {
                    if (username != vb.etUsername.text.toString()) {
                        return
                    }
                    if (response?.exists == false) {
                        vm.isUsernameValid = true
                        vb.llUsernameError.visibility = View.GONE
                        vb.ivUsernameStatusIcon.visibility = View.VISIBLE
                        vb.ivUsernameStatusIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPRegisterActivity, io.taptalk.TapTalk.R.drawable.tap_ic_check_circle_green))
                        updateEditTextBackground(vb.etUsername, vb.etUsername.hasFocus())
                        vm.formCheck[indexUsername] = stateValid
                    }
                    else {
                        setUsernameError(getString(R.string.tap_error_register_username_taken))
                    }
                }

                override fun onError(error: TAPErrorModel?) {
                    setUsernameError(error?.message ?: getString(io.taptalk.TapTalk.R.string.tap_error_unable_to_verify_username))
                }

                override fun onError(throwable: Throwable?) {
                    setUsernameError(getString(io.taptalk.TapTalk.R.string.tap_error_unable_to_verify_username))
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
                                    .setTitle(getString(io.taptalk.TapTalk.R.string.tap_error))
                                    .setMessage(errorMessage)
                                    .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_retry))
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
            onError(error?.message ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
        }

        override fun onError(errorMessage: String?) {
            vm.isUpdatingProfile = false
            enableEditing()
            if (TAPNetworkStateManager.getInstance("").hasNetworkConnection(this@TAPRegisterActivity)) {
                showErrorDialog(errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
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
                    vb.pbProfilePictureProgress.progress = TAPFileUploadManager.getInstance(instanceKey)
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
                            .setTitle(getString(io.taptalk.TapTalk.R.string.tap_error_unable_to_upload))
                            .setMessage(intent.getStringExtra(UploadFailedErrorMessage) ?: getString(io.taptalk.TapTalk.R.string.tap_error_upload_profile_picture))
                            .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_retry))
                            .setPrimaryButtonListener(true) { uploadProfilePicture() }
                            .setSecondaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_skip))
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
            vb.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
            )
        }
        else {
            vb.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_wifi_off_red,
                io.taptalk.TapTalk.R.string.tap_error_check_your_network
            )
        }
    }
}
