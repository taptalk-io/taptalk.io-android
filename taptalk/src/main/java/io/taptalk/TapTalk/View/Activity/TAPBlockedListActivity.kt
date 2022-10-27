package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapBlockedListAdapter
import io.taptalk.TapTalk.ViewModel.TapBlockedListViewModel
import kotlinx.android.synthetic.main.tap_activity_blocked_list.*

class TAPBlockedListActivity : TAPBaseActivity() {
    private var adapter: TapBlockedListAdapter? = null
    val vm: TapBlockedListViewModel by lazy {
        ViewModelProvider(this)[TapBlockedListViewModel::class.java]
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?
        ) {
            val intent = Intent(context, TAPBlockedListActivity::class.java)
            intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_blocked_list)
        initViewModel()
        initView()
    }

    override fun onBackPressed() {
        if (adapter?.isEditState() == true) {
            adapter?.setViewState()
            tv_edit_save_btn.text = getString(R.string.tap_edit)
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        }
    }

    private fun initViewModel() {
        // TODO: for testing purposes only MU
        //Dummy Contacts
        if (vm.blockedList.size == 0) {
            val u1 = TAPUserModel("u1", "Dummy Spam 1")
            val u2 = TAPUserModel("u2", "Dummy Spam 2")
            val u3 = TAPUserModel("u3", "Tummy Spam 3")
            vm.blockedList.add(u1)
            vm.blockedList.add(u2)
            vm.blockedList.add(u3)
        }
        //End Dummy
    }

    private fun initView() {
        window.setBackgroundDrawable(null)
        adapter = TapBlockedListAdapter(
                vm.blockedList,
                blockedContactsListener
            )
        rv_blocked_list.adapter = adapter
        rv_blocked_list.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        OverScrollDecoratorHelper.setUpOverScroll(
            rv_blocked_list,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        tv_edit_save_btn.setOnClickListener {
            if (adapter?.isEditState() == true) {
                adapter?.setViewState()
                tv_edit_save_btn.text = getString(R.string.tap_edit)
            } else {
                adapter?.setEditState()
                tv_edit_save_btn.text = getString(R.string.tap_done)
            }
        }
        iv_button_back.setOnClickListener { onBackPressed() }
    }

    private fun showLoading() {

    }

    private fun hideLoading() {

    }

    private val blockedContactsListener = object : TAPGeneralListener<TAPUserModel>() {
        override fun onClick(position: Int) {
            super.onClick(position)
            // open user profile
            val user = vm.blockedList[position]
            val roomModel = TAPRoomModel.Builder(
                TAPChatManager.getInstance(instanceKey).arrangeRoomId(
                    TAPChatManager.getInstance(instanceKey).activeUser.userID,
                    user.userID
                ),
                user.fullname,
                TAPDefaultConstant.RoomType.TYPE_PERSONAL,
                user.imageURL,
                ""
            )
            TAPChatProfileActivity.start(this@TAPBlockedListActivity, instanceKey, roomModel, user, false)
        }

        override fun onClick(position: Int, item: TAPUserModel?) {
            super.onClick(position, item)
            if (item == null) return
            // unblock user
            TapTalkDialog.Builder(this@TAPBlockedListActivity)
                .setTitle("${getString(R.string.tap_unblock)} ${item.fullname}?")
                .setMessage(getString(R.string.tap_sure_unblock_wording))
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_yes))
                .setPrimaryButtonListener {
                    // TODO: call unblock api MU
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        }
    }
}