package io.taptalk.TapTalk.Helper

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.taptalk.TapTalk.R

//class TapCustomSnackbarView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    constructor(context: Context) : this(context, null, 0) {
//        View.inflate(context, R.layout.tap_layout_custom_snackbar, this)
//    }
//}

class TapCustomSnackbarView : ConstraintLayout {

    private var clSnackbarContainer: ConstraintLayout? = null
    private var ivSnackbarIcon: ImageView? = null
    private var tvSnackbarLabel: TextView? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.tap_layout_custom_snackbar, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.tap_layout_custom_snackbar, this)
    }

    /* Remaining constructors here */

    init {

    }

    public override fun onFinishInflate() {
        Log.e(">>>>>>>>>>>", "onFinishInflate: ")
        super.onFinishInflate()
        clSnackbarContainer = findViewById(R.id.cl_tap_custom_snackbar_container)
        ivSnackbarIcon = findViewById(R.id.iv_tap_custom_snackbar_icon)
        tvSnackbarLabel = findViewById(R.id.tv_tap_custom_snackbar_label)

        tvSnackbarLabel?.text = "TEST TEST TEST"
    }
    
    fun setType(type: Type) {
        if (type == Type.ERROR) {

        }
    }

    companion object {
        enum class Type {
            DEFAULT, ERROR
        }
    }
}