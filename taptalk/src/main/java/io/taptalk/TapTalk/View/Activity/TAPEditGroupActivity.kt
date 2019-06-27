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
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_GROUP_IMAGE
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.ViewModel.TAPEditGroupViewModel
import io.taptalk.Taptalk.R
import kotlinx.android.synthetic.main.tap_activity_edit_group.*

class TAPEditGroupActivity : TAPBaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_remove_group_picture -> {
                groupViewModel?.groupData?.roomImage = null
                civ_group_picture.setImageResource(R.drawable.tap_img_default_avatar)
                iv_remove_group_picture.visibility = View.GONE
                groupViewModel?.isGroupPicChanged = groupViewModel?.isGroupPicStartEmpty != true
                showingButton()
            }

            R.id.ll_change_group_picture -> {
                TAPUtils.getInstance().animateClickButton(v, 0.95f)
                TAPUtils.getInstance().pickImageFromGallery(this, PICK_GROUP_IMAGE, false)
            }

            R.id.fl_create_group_btn -> {

                if (groupViewModel?.isGroupPicChanged == true || groupViewModel?.isGroupNameChanged == true) {
                    groupViewModel?.groupData?.roomName = et_group_name.text.toString()
                    //TODO() API EDIT CALL

                    //TODO() Apus Setelah ada API CALL / Flow yang pasti
                    val intent = Intent(this, TAPGroupMemberListActivity::class.java)
                    intent.putExtra(ROOM, groupViewModel?.groupData)
                    startActivity(intent)
                }
            }
        }
    }

    var groupViewModel: TAPEditGroupViewModel? = null

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
                when (requestCode) {
                    PICK_GROUP_IMAGE -> {
                        groupViewModel?.isGroupPicChanged = true
                        loadImage(data?.data.toString())
                        showingButton()
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(TAPEditGroupViewModel::class.java)
    }

    private fun initView() {
        et_group_name.onFocusChangeListener = groupNameFocusListener
        et_group_name.addTextChangedListener(groupNameWatcher)

        groupViewModel?.groupData = intent.getParcelableExtra(ROOM)

        et_group_name.setText(groupViewModel?.groupData?.roomName ?: "")

        iv_remove_group_picture.setOnClickListener(this)
        ll_change_group_picture.setOnClickListener(this)
        fl_create_group_btn.setOnClickListener(this)

        if (null != groupViewModel?.groupData?.roomImage) {
            val imageURL = groupViewModel?.groupData?.roomImage
            loadImage(imageURL?.thumbnail ?: "")
        } else {
            groupViewModel?.isGroupPicStartEmpty = true
        }

        fl_create_group_btn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple)
    }

    private val groupNameWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.isNotEmpty() == true && s.toString() != groupViewModel?.groupData?.roomName ?: "") {
                groupViewModel?.isGroupNameChanged = true
            } else if (s?.isNotEmpty() == true && s.toString() == groupViewModel?.groupData?.roomName ?: "") {
                groupViewModel?.isGroupNameChanged = false
            }

            showingButton()
        }
    }

    private val groupNameFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        when (hasFocus) {
            true -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_text_field_active)
            else -> et_group_name.background = resources.getDrawable(R.drawable.tap_bg_text_field_inactive)
        }
    }

    private fun loadImage(imageURL: String) {
        Glide.with(this).load(imageURL)
                .apply(RequestOptions().centerCrop()).into(civ_group_picture)
        iv_remove_group_picture.visibility = View.VISIBLE
    }

    private fun showingButton() {
        if (groupViewModel?.isGroupPicChanged == false && groupViewModel?.isGroupNameChanged == false) {
            fl_create_group_btn.setBackgroundResource(R.drawable.tap_bg_button_inactive_ripple)
        } else {
            fl_create_group_btn.setBackgroundResource(R.drawable.tap_bg_button_active_ripple)
        }
    }

}
