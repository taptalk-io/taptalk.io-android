package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import io.taptalk.TapTalk.API.View.TapDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.*
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TAPVerifyOTPInterface
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCheckUsernameResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPRegisterResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_register.*

class TAPRegisterActivity : TAPBaseActivity() {

    private var formCheck = arrayOf(0, 0, 0, 0, 0, 0)
    private var countryID = 1
    private var countryCallingCode = "62"

    private val indexFullName = 0
    private val indexUsername = 1
    private val indexMobileNumber = 2
    private val indexEmail = 3
    private val indexPassword = 4
    private val indexPasswordRetype = 5

    private val stateEmpty = 0
    private val stateValid = 1
    private val stateChecking = 2
    private val stateInvalid = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_register)

        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
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

        et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_retype_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        et_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)
        et_retype_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)

        countryID = intent.getIntExtra(COUNTRY_ID, 1)
        countryCallingCode = intent.getStringExtra(COUNTRY_CALLING_CODE)
        tv_country_code.text = "+$countryCallingCode"
        et_mobile_number.isEnabled = false
        et_mobile_number.setText(intent.getStringExtra(MOBILE_NUMBER))
        if (et_mobile_number.text.isNotEmpty()) {
            formCheck[indexMobileNumber] = stateValid
        }

        TAPUtils.getInstance().showKeyboard(this, et_full_name)

        fl_container.setOnClickListener { clearAllFocus() }
        cl_form_container.setOnClickListener { clearAllFocus() }
        iv_button_back.setOnClickListener { onBackPressed() }
        iv_view_password.setOnClickListener { togglePasswordVisibility(et_password, iv_view_password) }
        iv_view_password_retype.setOnClickListener { togglePasswordVisibility(et_retype_password, iv_view_password_retype) }

        et_retype_password.setOnEditorActionListener { v, a, e -> fl_button_continue.callOnClick() }
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (et_full_name.text.isNotEmpty() && et_full_name.text.matches(Regex("[A-Za-z ]*"))) {
            // Valid full name
            formCheck[indexFullName] = stateValid
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else if (et_full_name.text.isEmpty()) {
            // Not filled
            formCheck[indexFullName] = stateEmpty
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else {
            // Invalid full name
            formCheck[indexFullName] = stateInvalid
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
            formCheck[indexUsername] = stateEmpty
            tv_label_username_error.visibility = View.GONE
            updateEditTextBackground(et_username, hasFocus)
            checkContinueButtonAvailability()
        } else {
            // Invalid username
            formCheck[indexUsername] = stateInvalid
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
            formCheck[indexEmail] = stateValid
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else if (et_email_address.text.isEmpty()) {
            // Not filled
            formCheck[indexEmail] = stateEmpty
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else {
            // Invalid email address
            formCheck[indexEmail] = stateInvalid
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
            formCheck[indexPassword] = stateValid
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else if (et_password.text.isEmpty()) {
            // Not filled
            formCheck[indexPassword] = stateEmpty
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else {
            // Invalid password
            formCheck[indexPassword] = stateInvalid
            tv_label_password_error.visibility = View.VISIBLE
            cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
            v_password_separator.setBackgroundColor(resources.getColor(R.color.tap_watermelon_red))
        }
        if (et_retype_password.text.isNotEmpty()) {
            checkRetypedPassword(et_retype_password.hasFocus())
        }
        checkContinueButtonAvailability()
    }

    private fun checkRetypedPassword(hasFocus: Boolean) {
        if (et_password.text.isNotEmpty() && et_password.text.toString() == et_retype_password.text.toString()) {
            // Password matches
            formCheck[indexPasswordRetype] = stateValid
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else if (et_retype_password.text.isEmpty()) {
            // Not filled
            formCheck[indexPasswordRetype] = stateEmpty
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else {
            // Password does not match
            formCheck[indexPasswordRetype] = stateInvalid
            tv_label_retype_password_error.visibility = View.VISIBLE
            cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
            v_retype_password_separator.setBackgroundColor(resources.getColor(R.color.tap_watermelon_red))
        }
        checkContinueButtonAvailability()
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
        } else {
            view.background = getDrawable(R.drawable.tap_bg_rounded_8dp_stroke_dcdcdc_1dp)
        }
        if (view == cl_password) {
            if (hasFocus) {
                v_password_separator.setBackgroundColor(resources.getColor(R.color.tap_blue_purple))
            } else {
                v_password_separator.setBackgroundColor(resources.getColor(R.color.tap_grey_dc))
            }
        } else if (view == cl_retype_password) {
            if (hasFocus) {
                v_retype_password_separator.setBackgroundColor(resources.getColor(R.color.tap_blue_purple))
            } else {
                v_retype_password_separator.setBackgroundColor(resources.getColor(R.color.tap_grey_dc))
            }
        }
    }

    private fun checkContinueButtonAvailability() {
        if (formCheck[indexFullName] == stateValid &&
                formCheck[indexUsername] == stateValid &&
                formCheck[indexMobileNumber] == stateValid &&
                (formCheck[indexEmail] == stateEmpty || formCheck[indexEmail] == stateValid) && (
                        (formCheck[indexPassword] == stateEmpty && formCheck[indexPasswordRetype] == stateEmpty) ||
                                (formCheck[indexPassword] == stateValid && formCheck[indexPasswordRetype] == stateValid))) {
            // All forms valid
            enableContinueButton()
        } else {
            // Has invalid forms
            disableContinueButton()
        }
    }

    private fun enableContinueButton() {
        fl_button_continue.background = getDrawable(R.drawable.tap_bg_orange_button_ripple)
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
            button.setImageDrawable(getDrawable(R.drawable.tap_ic_view_orange))
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageDrawable(getDrawable(R.drawable.tap_ic_view_grey))
        }
        editText.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)
        editText.setSelection(cursorPosition)
    }

    private fun register() {
        TAPDataManager.getInstance().register(
                et_full_name.text.toString(),
                et_username.text.toString(),
                countryID,
                et_mobile_number.text.toString(),
                et_email_address.text.toString(),
                et_password.text.toString(),
                registerView
        )
    }

    private fun showErrorDialog(message: String) {
        TapTalkDialog.Builder(this@TAPRegisterActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.tap_error))
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setPrimaryButtonListener(true) { enableEditing() }
                .setCancelable(false)
                .show()
    }

    private fun disableEditing() {
        et_full_name.isEnabled = false
        et_username.isEnabled = false
        et_email_address.isEnabled = false
        et_password.isEnabled = false
        et_retype_password.isEnabled = false

        et_full_name.setTextColor(resources.getColor(R.color.tap_grey_9b))
        et_username.setTextColor(resources.getColor(R.color.tap_grey_9b))
        et_email_address.setTextColor(resources.getColor(R.color.tap_grey_9b))
        et_password.setTextColor(resources.getColor(R.color.tap_grey_9b))
        et_retype_password.setTextColor(resources.getColor(R.color.tap_grey_9b))

        tv_button_continue.visibility = View.GONE
        iv_register_progress.visibility = View.VISIBLE
        TAPUtils.getInstance().rotateAnimateInfinitely(this@TAPRegisterActivity, iv_register_progress)

        fl_button_continue.setOnClickListener(null)
    }

    private fun enableEditing() {
        et_full_name.isEnabled = true
        et_username.isEnabled = true
        et_email_address.isEnabled = true
        et_password.isEnabled = true
        et_retype_password.isEnabled = true

        et_full_name.setTextColor(resources.getColor(R.color.tap_black_19))
        et_username.setTextColor(resources.getColor(R.color.tap_black_19))
        et_email_address.setTextColor(resources.getColor(R.color.tap_black_19))
        et_password.setTextColor(resources.getColor(R.color.tap_black_19))
        et_retype_password.setTextColor(resources.getColor(R.color.tap_black_19))

        tv_button_continue.visibility = View.VISIBLE
        iv_register_progress.visibility = View.GONE
        iv_register_progress.clearAnimation()

        fl_button_continue.setOnClickListener { register() }
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[indexFullName] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            if (formCheck[indexFullName] != stateInvalid) {
                updateEditTextBackground(et_full_name, hasFocus)
            }
        }
    }

    private val usernameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[indexUsername] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            if (formCheck[indexUsername] != stateInvalid) {
                updateEditTextBackground(et_username, hasFocus)
            }
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[indexEmail] != stateInvalid) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            if (formCheck[indexEmail] != stateInvalid) {
                updateEditTextBackground(et_email_address, hasFocus)
            }
        }
    }

    private val passwordFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[indexPassword] != stateInvalid) {
                cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
                v_password_separator.setBackgroundColor(resources.getColor(R.color.tap_blue_purple))
            }
        } else {
            cl_password.elevation = 0f
            if (formCheck[indexPassword] != stateInvalid) {
                updateEditTextBackground(cl_password, hasFocus)
            }
        }
    }

    private val passwordRetypeFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_retype_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[indexPasswordRetype] != stateInvalid) {
                cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
                v_retype_password_separator.setBackgroundColor(resources.getColor(R.color.tap_blue_purple))
            }
        } else {
            cl_retype_password.elevation = 0f
            if (formCheck[indexPasswordRetype] != stateInvalid) {
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
                formCheck[indexUsername] = stateValid
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
            formCheck[indexUsername] = stateInvalid
            tv_label_username_error.text = message
            tv_label_username_error.visibility = View.VISIBLE
            et_username.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
    }

    private val registerView = object : TapDefaultDataView<TAPRegisterResponse>() {
        override fun startLoading() {
            disableEditing()
        }

        override fun endLoading() {
            enableEditing()
        }

        override fun onSuccess(response: TAPRegisterResponse?) {
            TapTalk.saveAuthTicketAndGetAccessToken(response?.ticket
                    ?: "", object : TAPVerifyOTPInterface {
                override fun verifyOTPSuccessToLogin() {
                    TAPDataManager.getInstance().saveMyCountryCode(countryCallingCode)
                    setResult(RESULT_OK)
                    finish()
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
        }

        override fun onError(error: TAPErrorModel?) {
            showErrorDialog(error?.message ?: getString(R.string.tap_error_message_general))
        }

        override fun onError(throwable: Throwable?) {
            showErrorDialog(throwable?.message ?: getString(R.string.tap_error_message_general))
        }
    }
}
