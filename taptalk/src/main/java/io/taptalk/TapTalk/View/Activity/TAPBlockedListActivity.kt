package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
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

    override fun onResume() {
        super.onResume()
        getBlockedUsers()
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

    private fun showLoading() {
        ll_loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        ll_loading.visibility = View.GONE
    }

    private fun getBlockedUsers() {
        TAPDataManager.getInstance(instanceKey).getBlockedUserList(getBlockedUserListView)
    }

    private fun showErrorDialog(message : String?) {
        TapTalkDialog.Builder(this)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(getString(R.string.tap_error))
            .setCancelable(true)
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .show()
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
                    TAPDataManager.getInstance(instanceKey).unblockUser(item.userID, unblockUserView)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        }
    }

    private val unblockUserView = object : TAPDefaultDataView<TAPCommonResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoading()
        }

        override fun onSuccess(response: TAPCommonResponse?) {
            super.onSuccess(response)
            getBlockedUsers()
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            endLoading()
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            endLoading()
            showErrorDialog(errorMessage)
        }
    }

    private val getBlockedUserListView = object : TAPDefaultDataView<TAPGetMultipleUserResponse>() {

        override fun onSuccess(response: TAPGetMultipleUserResponse?) {
            super.onSuccess(response)
            vm.blockedList.clear()
            if (response?.users?.isNotEmpty() == true) {
                vm.blockedList.addAll(response.users)
            }
            runOnUiThread {
                adapter?.items = vm.blockedList
                adapter?.notifyDataSetChanged()
            }
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            showErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            showErrorDialog(errorMessage)
        }
    }
}