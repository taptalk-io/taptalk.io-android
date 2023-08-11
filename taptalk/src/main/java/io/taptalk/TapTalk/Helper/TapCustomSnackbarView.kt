package io.taptalk.TapTalk.Helper

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import io.taptalk.TapTalk.R

class TapCustomSnackbarView : ConstraintLayout {

    private var clSnackbarContainer: ConstraintLayout? = null
    private var ivSnackbarIcon: ImageView? = null
    private var tvSnackbarLabel: TextView? = null

    var showDuration = 3000L
    var animationDuration = 200L
    var hideOnClick = true

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.tap_layout_custom_snackbar, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.tap_layout_custom_snackbar, this)
    }

    companion object {
        enum class Type {
            DEFAULT, ERROR
        }
    }

    public override fun onFinishInflate() {
        super.onFinishInflate()

        clSnackbarContainer = findViewById(R.id.cl_tap_custom_snackbar_container)
        ivSnackbarIcon = findViewById(R.id.iv_tap_custom_snackbar_icon)
        tvSnackbarLabel = findViewById(R.id.tv_tap_custom_snackbar_label)

        clSnackbarContainer?.setOnClickListener {
            if (hideOnClick) {
                hide()
            }
        }
    }
    
    fun setType(type: Type) {
        if (type == Type.ERROR) {
            setContainerBackground(R.drawable.tap_bg_snackbar_error)
            setIconTintColor(R.color.tapColorDestructiveIcon)
            setTextColor(R.color.tapColorError)
        }
        else {
            setContainerBackground(R.drawable.tap_bg_black_19_rounded_8dp)
            setIconTintColor(R.color.tapColorWhiteIcon)
            setTextColor(R.color.tapColorTextLight)
        }
    }

    fun setIcon(iconRes: Int) {
        setIcon(ContextCompat.getDrawable(context, iconRes))
    }

    fun setIcon(icon: Drawable?) {
        if (icon != null) {
            ivSnackbarIcon?.setImageDrawable(icon)
            ivSnackbarIcon?.visibility = View.VISIBLE
        }
        else {
            ivSnackbarIcon?.visibility = View.GONE
        }
    }

    fun setText(stringRes: Int) {
        setText(context.getString(stringRes))
    }

    fun setText(text: String?) {
        if (!text.isNullOrEmpty()) {
            tvSnackbarLabel?.text = text
        }
    }

    fun setContainerBackground(drawableRes: Int) {
        setContainerBackground(ContextCompat.getDrawable(context, drawableRes))
    }

    fun setContainerBackground(backgroundDrawable: Drawable?) {
        if (backgroundDrawable != null) {
            clSnackbarContainer?.background = backgroundDrawable
        }
    }

    fun setIconTintColor(colorRes: Int) {
        ivSnackbarIcon?.let {
            ImageViewCompat.setImageTintList(
                it,
                ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
            )
        }
    }

    fun setTextColor(colorRes: Int) {
        tvSnackbarLabel?.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    fun show(type: Type, iconRes: Int, stringRes: Int) {
        setType(type)
        setIcon(iconRes)
        setText(stringRes)
        show()
    }

    fun show(type: Type, iconRes: Int, text: String?) {
        setType(type)
        setIcon(iconRes)
        setText(text)
        show()
    }

    fun show(type: Type, icon: Drawable?, stringRes: Int) {
        setType(type)
        setIcon(icon)
        setText(stringRes)
        show()
    }

    fun show(type: Type, icon: Drawable?, text: String?) {
        setType(type)
        setIcon(icon)
        setText(text)
        show()
    }

    fun show() {
        clSnackbarContainer?.visibility = View.VISIBLE
        clSnackbarContainer?.animate()
            ?.alpha(1f)
            ?.setDuration(animationDuration)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.withEndAction {
                if (showDuration > 0L) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        hide()
                    }, showDuration)
                }
            }
            ?.start()
    }

    fun hide() {
        clSnackbarContainer?.animate()
            ?.alpha(0f)
            ?.setDuration(animationDuration)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.withEndAction {
                clSnackbarContainer?.visibility = View.GONE
            }
            ?.start()
    }
}
