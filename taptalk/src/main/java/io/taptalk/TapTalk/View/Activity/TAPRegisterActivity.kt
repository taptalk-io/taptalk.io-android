package io.taptalk.TapTalk.View.Activity

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_register.*

class TAPRegisterActivity : TAPBaseActivity() {

    private var formCheck = arrayOf(0, 0, 0, 0, 0, 0)

    private val INDEX_FULL_NAME = 0
    private val INDEX_USERNAME = 1
    private val INDEX_MOBILE_NUMBER = 2
    private val INDEX_EMAIL = 3
    private val INDEX_PASSWORD = 4
    private val INDEX_PASSWORD_RETYPE = 5

    private val STATE_EMPTY = 0
    private val STATE_VALID = 1
    private val STATE_INVALID = -1

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
        et_mobile_number.onFocusChangeListener = mobileNumberFocusListener
        et_email_address.onFocusChangeListener = emailAddressFocusListener
        et_password.onFocusChangeListener = passwordFocusListener
        et_retype_password.onFocusChangeListener = passwordRetypeFocusListener

        et_full_name.addTextChangedListener(fullNameWatcher)
        et_username.addTextChangedListener(usernameWatcher)
        et_mobile_number.addTextChangedListener(numberWatcher)
        et_email_address.addTextChangedListener(emailWatcher)
        et_password.addTextChangedListener(passwordWatcher)
        et_retype_password.addTextChangedListener(passwordRetypeWatcher)

        et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_retype_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        et_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)
        et_retype_password.typeface = ResourcesCompat.getFont(this, R.font.tap_font_pt_root_regular)

        fl_container.setOnClickListener{ clearAllFocus() }
        cl_form_container.setOnClickListener{ clearAllFocus() }
        iv_button_back.setOnClickListener { onBackPressed() }
        iv_view_password.setOnClickListener{ togglePasswordVisibility(et_password, iv_view_password) }
        iv_view_password_retype.setOnClickListener{ togglePasswordVisibility(et_retype_password, iv_view_password_retype) }

        et_retype_password.setOnEditorActionListener { v, a, e -> fl_button_continue.callOnClick() }
    }

    private fun clearAllFocus() {
        TAPUtils.getInstance().dismissKeyboard(this)
        fl_container.clearFocus()
    }

    private fun checkFullName(hasFocus: Boolean) {
        if (et_full_name.text.isNotEmpty() && et_full_name.text.matches(Regex("[A-Za-z ]*"))) {
            // Valid full name
            formCheck[INDEX_FULL_NAME] = STATE_VALID
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else if (et_full_name.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_FULL_NAME] = STATE_EMPTY
            tv_label_full_name_error.visibility = View.GONE
            updateEditTextBackground(et_full_name, hasFocus)
        } else {
            // Invalid full name
            formCheck[INDEX_FULL_NAME] = STATE_INVALID
            tv_label_full_name_error.visibility = View.VISIBLE
            et_full_name.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkUsername(hasFocus: Boolean) {
        if (et_username.text.isNotEmpty() && et_username.text.matches(Regex("[a-z0-9_]*")) && et_username.text[0].isLetter()) {
            // Valid username
            formCheck[INDEX_USERNAME] = STATE_VALID
            tv_label_username_error.visibility = View.GONE
            updateEditTextBackground(et_username, hasFocus)
        } else if (et_username.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_USERNAME] = STATE_EMPTY
            tv_label_username_error.visibility = View.GONE
            updateEditTextBackground(et_username, hasFocus)
        } else {
            // Invalid username
            formCheck[INDEX_USERNAME] = STATE_INVALID
            tv_label_username_error.visibility = View.VISIBLE
            et_username.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkMobileNumber(hasFocus: Boolean) {
        val phoneNumber = StringBuilder().append(tv_country_code.text).append(et_mobile_number.text)
        if (et_mobile_number.text.isNotEmpty() && Patterns.PHONE.matcher(phoneNumber).matches() && et_mobile_number.text.length >= 7 && et_mobile_number.text.length <= 15) {
            // Valid phone number
            formCheck[INDEX_MOBILE_NUMBER] = STATE_VALID
            tv_label_mobile_number_error.visibility = View.GONE
            updateEditTextBackground(et_mobile_number, hasFocus)
        } else if (et_mobile_number.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_MOBILE_NUMBER] = STATE_EMPTY
            tv_label_mobile_number_error.visibility = View.GONE
            updateEditTextBackground(et_mobile_number, hasFocus)
        } else {
            // Invalid phone number
            formCheck[INDEX_MOBILE_NUMBER] = STATE_INVALID
            tv_label_mobile_number_error.visibility = View.VISIBLE
            et_mobile_number.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkEmailAddress(hasFocus: Boolean) {
        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            // Valid email address
            formCheck[INDEX_EMAIL] = STATE_VALID
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else if (et_email_address.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_EMAIL] = STATE_EMPTY
            tv_label_email_address_error.visibility = View.GONE
            updateEditTextBackground(et_email_address, hasFocus)
        } else {
            // Invalid email address
            formCheck[INDEX_EMAIL] = STATE_INVALID
            tv_label_email_address_error.visibility = View.VISIBLE
            et_email_address.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkPassword(hasFocus: Boolean) {
        // TODO: validate password
        if (et_password.text.isNotEmpty() && et_password.text.length >= 6) {
            // Valid password
            formCheck[INDEX_PASSWORD] = STATE_VALID
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else if (et_password.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_PASSWORD] = STATE_EMPTY
            tv_label_password_error.visibility = View.GONE
            updateEditTextBackground(cl_password, hasFocus)
        } else {
            // Invalid password
            formCheck[INDEX_PASSWORD] = STATE_INVALID
            tv_label_password_error.visibility = View.VISIBLE
            cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun checkRetypedPassword(hasFocus: Boolean) {
        if (et_password.text.isNotEmpty() && et_password.text.toString() == et_retype_password.text.toString()) {
            // Password matches
            formCheck[INDEX_PASSWORD_RETYPE] = STATE_VALID
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else if (et_retype_password.text.isEmpty()) {
            // Not filled
            formCheck[INDEX_PASSWORD_RETYPE] = STATE_EMPTY
            tv_label_retype_password_error.visibility = View.GONE
            updateEditTextBackground(cl_retype_password, hasFocus)
        } else {
            // Password does not match
            formCheck[INDEX_PASSWORD_RETYPE] = STATE_INVALID
            tv_label_retype_password_error.visibility = View.VISIBLE
            cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_watermelon_1dp)
        }
        checkContinueButtonAvailability()
    }

    private fun updateEditTextBackground(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
        } else {
            view.background = getDrawable(R.drawable.tap_bg_rounded_8dp_stroke_dcdcdc_1dp)
        }
    }

    private fun checkContinueButtonAvailability() {
        if (formCheck[INDEX_FULL_NAME] == STATE_VALID &&
                formCheck[INDEX_USERNAME] == STATE_VALID &&
                formCheck[INDEX_MOBILE_NUMBER] == STATE_VALID &&
                formCheck[INDEX_EMAIL] == STATE_VALID && (
                        (formCheck[INDEX_PASSWORD] == STATE_EMPTY && formCheck[INDEX_PASSWORD_RETYPE] == STATE_EMPTY) ||
                                (formCheck[INDEX_PASSWORD] == STATE_VALID && formCheck[INDEX_PASSWORD_RETYPE] == STATE_VALID))) {
            // All forms valid
            fl_button_continue.background = getDrawable(R.drawable.tap_bg_gradient_ff9833_ff7e00_rounded_8dp_stroke_ff7e00_1dp)
            fl_button_continue.setOnClickListener { register() }
        } else {
            // Has invalid forms
            fl_button_continue.background = getDrawable(R.drawable.tap_bg_gradient_cecece_9b9b9b_rounded_8dp_stroke_cecece_1dp)
            fl_button_continue.setOnClickListener(null)
        }
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
        // TODO
        finish()
    }

    private val fullNameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_FULL_NAME] != STATE_INVALID) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            checkFullName(hasFocus)
        }
    }

    private val usernameFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_USERNAME] != STATE_INVALID) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            checkUsername(hasFocus)
        }
    }

    private val mobileNumberFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_MOBILE_NUMBER] != STATE_INVALID) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            checkMobileNumber(hasFocus)
        }
    }

    private val emailAddressFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_EMAIL] != STATE_INVALID) {
                view.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            view.elevation = 0f
            checkEmailAddress(hasFocus)
        }
    }

    private val passwordFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_PASSWORD] != STATE_INVALID) {
                cl_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            cl_password.elevation = 0f
            checkPassword(hasFocus)
        }
    }

    private val passwordRetypeFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            cl_retype_password.elevation = TAPUtils.getInstance().dpToPx(4).toFloat()
            if (formCheck[INDEX_PASSWORD_RETYPE] != STATE_INVALID) {
                cl_retype_password.background = getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_bluepurple_1dp)
            }
        } else {
            cl_retype_password.elevation = 0f
            checkRetypedPassword(hasFocus)
        }
    }

    private val fullNameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (formCheck[INDEX_FULL_NAME] != STATE_EMPTY) {
                checkFullName(et_full_name.hasFocus())
            }
        }
    }

    private val usernameWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (formCheck[INDEX_USERNAME] != STATE_EMPTY) {
                checkUsername(et_username.hasFocus())
            }
        }
    }

    private val numberWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (formCheck[INDEX_MOBILE_NUMBER] != STATE_EMPTY) {
                checkMobileNumber(et_mobile_number.hasFocus())
            }
        }
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (formCheck[INDEX_EMAIL] != STATE_EMPTY) {
                checkEmailAddress(et_email_address.hasFocus())
            }
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkPassword(et_password.hasFocus())
        }
    }

    private val passwordRetypeWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            checkRetypedPassword(et_retype_password.hasFocus())
        }
    }
}
