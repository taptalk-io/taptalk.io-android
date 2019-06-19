package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Model.TAPImageURL
import io.taptalk.TapTalk.ViewModel.TAPGroupViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_edit_group.*

class TAPEditGroupActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_remove_group_picture -> {
                groupViewModel?.groupData?.roomImage = null
                civ_group_picture.setImageResource(R.drawable.tap_img_default_avatar)
                iv_remove_group_picture.visibility = View.GONE
                btn_edit_group.setBackgroundResource(R.drawable.tap_bg_primary_primarydark_stroke_primarydark_1dp_rounded_6dp_ripple)
            }

            R.id.ll_change_group_picture -> {
                TAPUtils.getInstance().animateClickButton(v, 0.95f)
                TAPUtils.getInstance().pickImageFromGallery(this, PICK_GROUP_IMAGE, false)
            }
        }
    }

    var groupViewModel: TAPGroupViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_edit_group)
        initViewModel()
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when(requestCode) {
                    PICK_GROUP_IMAGE -> {
                        val groupImage = TAPImageURL(data?.data.toString(), data?.data.toString())
                        groupViewModel?.groupData?.roomImage = groupImage
                        loadImage(data?.data.toString())
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPGroupViewModel::class.java)
    }

    private fun initView() {
        et_group_name.onFocusChangeListener = groupNameFocusListener
        et_group_name.addTextChangedListener(groupNameWatcher)

        groupViewModel?.groupData = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)

        et_group_name.setText(groupViewModel?.groupData?.roomName ?: "")

        iv_remove_group_picture.setOnClickListener(this)
        ll_change_group_picture.setOnClickListener(this)

        if (null != groupViewModel?.groupData?.roomImage) {
            val imageURL = groupViewModel?.groupData?.roomImage
            loadImage(imageURL?.thumbnail ?: "")
        }

        btn_edit_group.setBackgroundResource(R.drawable.tap_bg_d9d9d9_rounded_6dp)
    }

    private val groupNameWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.isNotEmpty() == true && s != groupViewModel?.groupData?.roomName ?: "") {
                btn_edit_group.setBackgroundResource(R.drawable.tap_bg_primary_primarydark_stroke_primarydark_1dp_rounded_6dp_ripple)
            } else {
                btn_edit_group.setBackgroundResource(R.drawable.tap_bg_d9d9d9_rounded_6dp)
            }
        }
    }

    private val groupNameFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        when (hasFocus) {
            true -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_accent_1dp)
            else -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_white_rounded_8dp_stroke_dcdcdc_1dp)
        }
    }

    private fun loadImage(imageURL: String) {
        Glide.with(this).load(imageURL)
                .apply(RequestOptions().centerCrop()).into(civ_group_picture)
        iv_remove_group_picture.visibility = View.VISIBLE
        btn_edit_group.setBackgroundResource(R.drawable.tap_bg_primary_primarydark_stroke_primarydark_1dp_rounded_6dp_ripple)
    }

}
