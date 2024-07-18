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
import io.taptalk.TapTalkSample.databinding.TapActivityRegisterBinding

class TAPRegisterActivity : TAPBaseActivity() {

    private lateinit var binding: TapActivityRegisterBinding
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
        binding = TapActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        if (binding.tvPhoneNumber.text.isNotEmpty() ||
            binding.etFullName.text.isNotEmpty() ||
            binding.etUsername.text.isNotEmpty() ||
            binding.etEmailAddress.text.isNotEmpty()
        ) {
            showPopupDiscardChanges()
            return
        }
        super.onBackPressed()
        overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_down)
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

    private fun initViewModel() {
        vm = ViewModelProvider(this, TAPRegisterViewModel.TAPRegisterViewModelFactory(application, instanceKey)).get(TAPRegisterViewModel::class.java)
        vm.countryID = intent.getIntExtra(COUNTRY_ID, 1)
        vm.countryCallingCode = intent.getStringExtra(COUNTRY_CALLING_CODE)
        vm.countryFlagUrl = intent.getStringExtra(COUNTRY_FLAG_URL)
    }

    @SuppressLint("PrivateResource")
    private fun initView() {
        window?.setBackgroundDrawable(null)

        binding.etFullName.imeOptions = EditorInfo.IME_ACTION_NEXT
        binding.etFullName.setRawInputType(InputType.TYPE_CLASS_TEXT)

        binding.etFullName.onFocusChangeListener = fullNameFocusListener
        binding.etUsername.onFocusChangeListener = usernameFocusListener
        binding.etEmailAddress.onFocusChangeListener = emailAddressFocusListener
//        binding.etPassword.onFocusChangeListener = passwordFocusListener
//        binding.etRetypePassword.onFocusChangeListener = passwordRetypeFocusListener

        binding.etFullName.addTextChangedListener(fullNameWatcher)
        binding.etUsername.addTextChangedListener(usernameWatcher)
        binding.etEmailAddress.addTextChangedListener(emailWatcher)
//        binding.etPassword.addTextChangedListener(passwordWatcher)
//        binding.etRetypePassword.addTextChangedListener(passwordRetypeWatcher)

        if (vm.countryFlagUrl != "") {
            glide.load(vm.countryFlagUrl)
                .apply(RequestOptions().placeholder(io.taptalk.TapTalk.R.drawable.tap_ic_default_flag))
                .into(binding.ivCountryFlag)
        }

        vm.textFieldFontColor = ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapFormTextFieldColor)
        vm.textFieldFontColorHint = ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapFormTextFieldPlaceholderColor)

        // Set password field input type and typeface
        binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.etRetypePassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Set mobile number & disable editing
        binding.tvCountryCode.text = String.format("+%s", vm.countryCallingCode)
        binding.tvPhoneNumber.text = TAPUtils.beautifyPhoneNumber(intent.getStringExtra(MOBILE_NUMBER), false)
        binding.tvCountryCode.setTextColor(vm.textFieldFontColorHint)
        binding.tvPhoneNumber.setTextColor(vm.textFieldFontColorHint)

        // Set url span for privacy policy label
        val spannableString = SpannableString(binding.tvLabelPrivacyPolicy.text)
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
        binding.tvLabelPrivacyPolicy.text = spannableString
        binding.tvLabelPrivacyPolicy.movementMethod = movementMethod

        binding.flContainer.setOnClickListener { clearAllFocus() }
        binding.clFormContainer.setOnClickListener { clearAllFocus() }
        binding.llChangeProfilePicture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        binding.flRemoveProfilePicture.setOnClickListener { removeProfilePicture() }
        binding.ivViewPassword.setOnClickListener { togglePasswordVisibility(binding.etPassword, binding.ivViewPassword) }
        binding.ivViewPasswordRetype.setOnClickListener { togglePasswordVisibility(binding.etRetypePassword, binding.ivViewPasswordRetype) }
        binding.cbPrivacyPolicy.setOnCheckedChangeListener { _, _ -> checkPrivacyPolicy() }
        binding.etRetypePassword.setOnEditorActionListener { v, a, e -> binding.clButtonContinue.callOnClick() }

        enableContinueButton()

        // Temporarily removed password
        binding.tvLabelPassword.visibility = View.GONE
        binding.tvLabelPasswordOptional.visibility = View.GONE
        binding.clPassword.visibility = View.GONE
        binding.tvLabelPasswordGuide.visibility = View.GONE
        binding.tvLabelPasswordError.visibility = View.GONE
        binding.tvLabelRetypePassword.visibility = View.GONE
        binding.tvLabelRetypePasswordError.visibility = View.GONE
        binding.clRetypePassword.visibility = View.GONE
        binding.etEmailAddress.setOnEditorActionListener { v, a, e -> binding.clButtonContinue.callOnClick() }
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
            binding.ivProfilePicturePlaceholder.visibility = View.VISIBLE
            binding.civProfilePicture.visibility = View.INVISIBLE
            glide.load(io.taptalk.TapTalk.R.drawable.tap_img_default_avatar).into(binding.civProfilePicture)
            binding.tvLabelChangeProfilePicture.text = getString(io.taptalk.TapTalk.R.string.tap_upload_image)
//            binding.flRemoveProfilePicture.visibility = View.GONE
            if (showErrorMessage) {
                Toast.makeText(this@TAPRegisterActivity, getString(io.taptalk.TapTalk.R.string.tap_failed_to_load_image), Toast.LENGTH_SHORT).show()
            }
        }
        else {
            binding.ivProfilePicturePlaceholder.visibility = View.INVISIBLE
            binding.civProfilePicture.visibility = View.VISIBLE
            glide.load(vm.profilePictureUri).into(binding.civProfilePicture)
            binding.tvLabelChangeProfilePicture.text = getString(io.taptalk.TapTalk.R.string.tap_change)
//            binding.flRemoveProfilePicture.visibility = View.VISIBLE
        }
    }

    private fun validateFullName(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && binding.etFullName.text.isEmpty()) {
            // Full name empty
            setFullNameError(getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required))
            return false
        }
        if (binding.etFullName.text.length > FULL_NAME_MAX_LENGTH) {
            // Exceeds max length
            setFullNameError(String.format(getString(R.string.tap_error_register_full_name_max), FULL_NAME_MAX_LENGTH))
            return false
        }
        if (binding.etFullName.text.isNotEmpty() && !binding.etFullName.text.matches(Regex("[A-Za-z ]*"))) {
            // Invalid full name
            setFullNameError(getString(io.taptalk.TapTalk.R.string.tap_error_invalid_full_name))
            return false
        }
        binding.llFullNameError.visibility = View.GONE
        updateEditTextBackground(binding.etFullName, binding.etFullName.hasFocus())
        vm.formCheck[indexFullName] = stateValid
        return true
    }

    private fun validateUsername(checkMinLength: Boolean = true): Boolean {
        if (checkMinLength && binding.etUsername.text.isEmpty()) {
            // Username empty
            setUsernameError(getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required))
            return false
        }
        val firstChar = if (binding.etUsername.text.isNullOrEmpty()) Character.MIN_VALUE else binding.etUsername.text[0]
        if (firstChar.isDigit() || firstChar == '.' || firstChar == '_') {
            // Starts with number or symbol
            setUsernameError(getString(R.string.tap_error_register_username_start))
            return false
        }
        if ((checkMinLength && binding.etUsername.text.length < USERNAME_MIN_LENGTH) || binding.etUsername.text.length > USERNAME_MAX_LENGTH) {
            // Invalid username length
            setUsernameError(String.format(getString(R.string.tap_error_register_username_length), USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH))
            return false
        }
        if (binding.etUsername.text.isNotEmpty()) {
            if (!binding.etUsername.text.matches(Regex("[a-zA-Z0-9._]*"))) {
                // Invalid symbol
                setUsernameError(getString(R.string.tap_error_register_username_symbol))
                return false
            }
            if (binding.etUsername.text.contains("..") || binding.etUsername.text.contains("__") || binding.etUsername.text.contains("._") || binding.etUsername.text.contains("_.")) {
                // Consecutive symbols
                setUsernameError(getString(R.string.tap_error_register_username_consecutive_symbols))
                return false
            }
            if (binding.etUsername.text.endsWith('.') || binding.etUsername.text.endsWith('_')) {
                // Ends with symbol
                setUsernameError(getString(R.string.tap_error_register_username_end))
                return false
            }
        }
        binding.llUsernameError.visibility = View.GONE
        binding.ivUsernameStatusIcon.visibility = View.GONE
        updateEditTextBackground(binding.etUsername, binding.etUsername.hasFocus())
        vm.formCheck[indexUsername] = stateValid
        return true
    }

    private fun validateEmailAddress(): Boolean {
        if (binding.etEmailAddress.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binding.etEmailAddress.text).matches()) {
            // Invalid email address
            setEmailAddressError(getString(io.taptalk.TapTalk.R.string.tap_error_invalid_email_address))
            return false
        }
        binding.llEmailAddressError.visibility = View.GONE
        updateEditTextBackground(binding.etEmailAddress, binding.etEmailAddress.hasFocus())
        vm.formCheck[indexEmail] = stateValid
        return true
    }

    private fun validateForm(): Boolean {
        val isFullNameValid = validateFullName()
        val isEmailValid = validateEmailAddress()
        if (!vm.isUsernameValid) {
            validateUsername(true)
        }

        return isFullNameValid && vm.isUsernameValid && isEmailValid && binding.cbPrivacyPolicy.isChecked
    }

    private fun setFullNameError(errorMessage: String) {
        binding.llFullNameError.visibility = View.VISIBLE
        binding.tvLabelFullNameError.text = errorMessage
        binding.etFullName.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
        vm.formCheck[indexFullName] = stateInvalid
    }

    private fun setUsernameError(errorMessage: String) {
        binding.llUsernameError.visibility = View.VISIBLE
        binding.tvLabelUsernameError.text = errorMessage
        binding.etUsername.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
        if (errorMessage != getString(io.taptalk.TapTalk.R.string.tap_this_field_is_required)) {
            binding.ivUsernameStatusIcon.visibility = View.VISIBLE
            binding.ivUsernameStatusIcon.setImageDrawable(ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_ic_close_circle_red))
        }
        else {
            binding.ivUsernameStatusIcon.visibility = View.GONE
        }
        vm.formCheck[indexUsername] = stateInvalid
    }

    private fun setEmailAddressError(errorMessage: String) {
        binding.llEmailAddressError.visibility = View.VISIBLE
        binding.tvLabelEmailAddressError.text = errorMessage
        binding.etEmailAddress.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_error)
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
        if (binding.cbPrivacyPolicy.isChecked) {
            binding.cbPrivacyPolicy.animate().alpha(1f).setDuration(200L).start()
            binding.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorPrimary))
        }
        else {
            binding.cbPrivacyPolicy.animate().alpha(0.2f).setDuration(200L).start()
            binding.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19))
        }
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_light)
        }
        if (view == binding.clPassword) {
            if (hasFocus) {
                binding.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
            } else {
                binding.vPasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1910))
            }
        } else if (view == binding.clRetypePassword) {
            if (hasFocus) {
                binding.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTextFieldBorderActiveColor))
            } else {
                binding.vRetypePasswordSeparator.setBackgroundColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1910))
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
        binding.clButtonContinue.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_active_ripple)
        binding.ivButtonIcon.visibility = View.VISIBLE
        binding.pbButtonLoading.visibility = View.INVISIBLE
        binding.clButtonContinue.setOnClickListener { register() }
    }

    private fun disableContinueButton() {
        binding.clButtonContinue.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_button_inactive)
        binding.ivButtonIcon.visibility = View.INVISIBLE
        binding.pbButtonLoading.visibility = View.VISIBLE
        binding.clButtonContinue.setOnClickListener(null)
    }

    private fun clearAllFocus() {
        TAPUtils.dismissKeyboard(this, binding.etFullName)
        TAPUtils.dismissKeyboard(this, binding.etUsername)
        TAPUtils.dismissKeyboard(this, binding.etEmailAddress)
        TAPUtils.dismissKeyboard(this, binding.etPassword)
        TAPUtils.dismissKeyboard(this, binding.etRetypePassword)
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
            if (!binding.cbPrivacyPolicy.isChecked) {
                showErrorSnackbar(getString(R.string.tap_error_register_privacy_policy))
                binding.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tapColorError))
                binding.cbPrivacyPolicy.animate().alpha(1f).setDuration(200L).start()
            }
            return
        }
        TAPDataManager.getInstance(instanceKey).register(
                binding.etFullName.text.toString(),
                binding.etUsername.text.toString(),
                vm.countryID,
                binding.tvPhoneNumber.text.toString().replace(" ", ""),
                binding.etEmailAddress.text.toString(),
                binding.etPassword.text.toString(),
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

        binding.llChangeProfilePicture.setOnClickListener(null)
        binding.flRemoveProfilePicture.setOnClickListener(null)
        disableContinueButton()

        binding.etFullName.isEnabled = false
        binding.etUsername.isEnabled = false
        binding.etEmailAddress.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.etRetypePassword.isEnabled = false
        binding.cbPrivacyPolicy.isEnabled = false

        binding.tvLabelChangeProfilePicture.setTextColor(vm.textFieldFontColorHint)
        ImageViewCompat.setImageTintList(binding.ivEditProfilePictureIcon, ColorStateList.valueOf(vm.textFieldFontColorHint))

        binding.tvLabelFullName.setTextColor(vm.textFieldFontColorHint)
        binding.tvLabelUsername.setTextColor(vm.textFieldFontColorHint)
        binding.tvLabelMobileNumber.setTextColor(vm.textFieldFontColorHint)
        binding.tvLabelEmailAddress.setTextColor(vm.textFieldFontColorHint)
        binding.tvLabelPrivacyPolicy.setTextColor(vm.textFieldFontColorHint)
        binding.tvLabelPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapTransparentBlack1960))
        binding.tvButtonContinue.setTextColor(vm.textFieldFontColorHint)

        binding.etFullName.setTextColor(vm.textFieldFontColorHint)
        binding.etUsername.setTextColor(vm.textFieldFontColorHint)
        binding.etEmailAddress.setTextColor(vm.textFieldFontColorHint)
        binding.etPassword.setTextColor(vm.textFieldFontColorHint)
        binding.etRetypePassword.setTextColor(vm.textFieldFontColorHint)

        binding.etFullName.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        binding.etUsername.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        binding.etEmailAddress.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        binding.etPassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)
        binding.etRetypePassword.background = ContextCompat.getDrawable(this, io.taptalk.TapTalk.R.drawable.tap_bg_text_field_disabled)

        binding.ivUsernameStatusIcon.alpha = 0.4f
        ImageViewCompat.setImageTintList(binding.ivUsernameStatusIcon, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19)))

        binding.cbPrivacyPolicy.alpha = 0.4f
        binding.cbPrivacyPolicy.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapBlack19))
    }

    private fun enableEditing() {
        binding.llChangeProfilePicture.setOnClickListener { showProfilePicturePickerBottomSheet() }
        binding.flRemoveProfilePicture.setOnClickListener { removeProfilePicture() }
        enableContinueButton()

        binding.etFullName.isEnabled = true
        binding.etUsername.isEnabled = true
        binding.etEmailAddress.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.etRetypePassword.isEnabled = true
        binding.cbPrivacyPolicy.isEnabled = true

        binding.tvLabelChangeProfilePicture.setTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        ImageViewCompat.setImageTintList(binding.ivEditProfilePictureIcon, ColorStateList.valueOf(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapIconChangePicture)))

        binding.tvLabelFullName.setTextColor(vm.textFieldFontColor)
        binding.tvLabelUsername.setTextColor(vm.textFieldFontColor)
        binding.tvLabelMobileNumber.setTextColor(vm.textFieldFontColor)
        binding.tvLabelEmailAddress.setTextColor(vm.textFieldFontColor)
        binding.tvLabelPrivacyPolicy.setTextColor(vm.textFieldFontColor)
        binding.tvLabelPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(this, R.color.tapColorPrimary))
        binding.tvButtonContinue.setTextColor(ContextCompat.getColor(this, io.taptalk.TapTalk.R.color.tapButtonLabelColor))

        binding.etFullName.setTextColor(vm.textFieldFontColor)
        binding.etUsername.setTextColor(vm.textFieldFontColor)
        binding.etEmailAddress.setTextColor(vm.textFieldFontColor)
        binding.etPassword.setTextColor(vm.textFieldFontColor)
        binding.etRetypePassword.setTextColor(vm.textFieldFontColor)

        updateEditTextBackground(binding.etFullName, binding.etFullName.hasFocus())
        updateEditTextBackground(binding.etUsername, binding.etUsername.hasFocus())
        updateEditTextBackground(binding.etEmailAddress, binding.etEmailAddress.hasFocus())
        updateEditTextBackground(binding.etPassword, binding.etPassword.hasFocus())
        updateEditTextBackground(binding.etRetypePassword, binding.etRetypePassword.hasFocus())

        binding.ivUsernameStatusIcon.alpha = 1f
        ImageViewCompat.setImageTintList(binding.ivUsernameStatusIcon, null)

        checkPrivacyPolicy()
    }

    private fun uploadProfilePicture() {
        vm.isUploadingProfilePicture = true
        binding.pbProfilePictureProgress.progress = 0
        TAPFileUploadManager.getInstance(instanceKey).uploadProfilePicture(this@TAPRegisterActivity, vm.profilePictureUri, vm.myUserModel.userID)
    }

    private fun finishRegisterAndOpenRoomList() {
        TapUIRoomListActivity.start(this, instanceKey)
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
            validateFullName(binding.llFullNameError.visibility == View.VISIBLE && binding.etFullName.text.isNotEmpty())
        }
    }

    private val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm.isUsernameValid = false
            checkUsernameTimer.cancel()
            TAPDataManager.getInstance(instanceKey).cancelCheckUsernameApiCall()
            if (validateUsername(binding.llUsernameError.visibility == View.VISIBLE && binding.etUsername.text.length >= USERNAME_MIN_LENGTH)) {
                if (binding.etUsername.text.length >= USERNAME_MIN_LENGTH) {
                    checkUsernameTimer.start()
                }
            }
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (binding.llEmailAddressError.visibility == View.VISIBLE) {
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
            val username = binding.etUsername.text.toString()
            TAPDataManager.getInstance(instanceKey).checkUsernameExists(binding.etUsername.text.toString(), object : TAPDefaultDataView<TAPCheckUsernameResponse>() {
                override fun onSuccess(response: TAPCheckUsernameResponse?) {
                    if (username != binding.etUsername.text.toString()) {
                        return
                    }
                    if (response?.exists == false) {
                        vm.isUsernameValid = true
                        binding.llUsernameError.visibility = View.GONE
                        binding.ivUsernameStatusIcon.visibility = View.VISIBLE
                        binding.ivUsernameStatusIcon.setImageDrawable(ContextCompat.getDrawable(this@TAPRegisterActivity, io.taptalk.TapTalk.R.drawable.tap_ic_check_circle_green))
                        updateEditTextBackground(binding.etUsername, binding.etUsername.hasFocus())
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
                    binding.pbProfilePictureProgress.progress = TAPFileUploadManager.getInstance(instanceKey)
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
            binding.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_info_outline_primary,
                errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
            )
        }
        else {
            binding.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.TapTalk.R.drawable.tap_ic_wifi_off_red,
                io.taptalk.TapTalk.R.string.tap_error_check_your_network
            )
        }
    }
}
