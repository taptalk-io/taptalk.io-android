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
import io.taptalk.TapTalk.Listener.TapCoreGetContactListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TapCoreContactManager
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapBlockedListAdapter
import io.taptalk.TapTalk.ViewModel.TapBlockedListViewModel
import io.taptalk.TapTalk.databinding.TapActivityBlockedListBinding

class TAPBlockedListActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityBlockedListBinding
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
        vb = TapActivityBlockedListBinding.inflate(layoutInflater)
        setContentView(vb.root)
        window.setBackgroundDrawable(null)
        adapter = TapBlockedListAdapter(
            vm.blockedList,
            blockedContactsListener
        )
        vb.rvBlockedList.adapter = adapter
        vb.rvBlockedList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        OverScrollDecoratorHelper.setUpOverScroll(
            vb.rvBlockedList,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        vb.tvEditSaveBtn.setOnClickListener {
            if (adapter?.isEditState() == true) {
                adapter?.setViewState()
                vb.tvEditSaveBtn.text = getString(R.string.tap_edit)
            }
            else {
                adapter?.setEditState()
                vb.tvEditSaveBtn.text = getString(R.string.tap_done)
            }
        }
        vb.ivButtonBack.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getBlockedUsers()
    }

    override fun onBackPressed() {
        if (adapter?.isEditState() == true) {
            adapter?.setViewState()
            vb.tvEditSaveBtn.text = getString(R.string.tap_edit)
        }
        else {
            try {
                super.onBackPressed()
                overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLoading() {
        vb.llLoading.llLoadingContainer.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vb.llLoading.llLoadingContainer.visibility = View.GONE
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
            TAPChatProfileActivity.start(this@TAPBlockedListActivity, instanceKey, roomModel, user)
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
                    showLoading()
                    TapCoreContactManager.getInstance(instanceKey).unblockUser(item.userID, unblockUserView)
                }
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                .setSecondaryButtonListener(true) {}
                .show()
        }
    }

    private val unblockUserView = object : TapCoreGetContactListener() {
        override fun onSuccess(user: TAPUserModel?) {
            hideLoading()
            getBlockedUsers()
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            hideLoading()
            showErrorDialog(errorMessage)
        }
    }

    private val getBlockedUserListView = object : TAPDefaultDataView<TAPGetMultipleUserResponse>() {

        override fun onSuccess(response: TAPGetMultipleUserResponse?) {
            super.onSuccess(response)
            vm.blockedList.clear()
            if (response?.users?.isNotEmpty() == true) {
                vb.tvEditSaveBtn.visibility = View.VISIBLE
                vb.rvBlockedList.visibility = View.VISIBLE
                vb.gEmptyState.visibility = View.GONE
                vm.blockedList.addAll(response.users)
                runOnUiThread {
                    adapter?.items = vm.blockedList
                }
            }
            else {
                vb.tvEditSaveBtn.visibility = View.GONE
                vb.rvBlockedList.visibility = View.GONE
                vb.gEmptyState.visibility = View.VISIBLE
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
