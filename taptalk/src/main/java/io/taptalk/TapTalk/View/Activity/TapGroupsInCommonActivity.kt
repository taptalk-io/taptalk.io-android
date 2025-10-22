package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.ResponseModel.TapRoomModelsResponse
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPSearchChatModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPSearchChatAdapter
import io.taptalk.TapTalk.ViewModel.TapGroupsInCommonViewModel
import io.taptalk.TapTalk.databinding.TapActivityGroupsInCommonBinding

class TapGroupsInCommonActivity : TAPBaseActivity() {

    private lateinit var vb: TapActivityGroupsInCommonBinding
    private lateinit var adapter : TAPSearchChatAdapter

    private val vm : TapGroupsInCommonViewModel by lazy {
        ViewModelProvider(this)[TapGroupsInCommonViewModel::class.java]
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?,
            user: TAPUserModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapGroupsInCommonActivity::class.java)
                intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(Extras.USER, user)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivityGroupsInCommonBinding.inflate(layoutInflater)
        setContentView(vb.root)
        if (finishIfNotLoggedIn()) {
            return
        }

        val user = intent.getParcelableExtra<TAPUserModel>(Extras.USER)
        if (user != null) {
            vm.userId = user.userID
            vb.tvTitle.text = user.fullname
        }

        adapter = TAPSearchChatAdapter(
            instanceKey,
            vm.groups,
            Glide.with(this),
            roomListInterface
        )
        vb.rvGroups.adapter = adapter
        vb.rvGroups.setHasFixedSize(false)
        vb.rvGroups.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        vb.ivButtonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        TAPDataManager.getInstance(instanceKey).getGroupsInCommon(vm.userId, groupsInCommonView)
    }

    private fun showLoading() {
        vb.llLoading.llLoadingContainer.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vb.llLoading.llLoadingContainer.visibility = View.GONE
    }

    private fun showViewState() {
        vb.gViewState.visibility = View.VISIBLE
        vb.gEmptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        vb.gViewState.visibility = View.GONE
        vb.gEmptyState.visibility = View.VISIBLE
    }

    private val roomListInterface =
        TapTalkRoomListInterface { room ->
            // Open chat room
            TapUIChatActivity.start(this, instanceKey, room)
        }

    private val groupsInCommonView = object : TAPDefaultDataView<TapRoomModelsResponse>() {
        override fun startLoading() {
            super.startLoading()
            showLoading()
        }

        override fun endLoading() {
            super.endLoading()
            hideLoading()
        }

        override fun onSuccess(response: TapRoomModelsResponse?) {
            super.onSuccess(response)
            vm.groups.clear()
            if (response?.rooms?.isNotEmpty() == true) {
                for (item in response.rooms) {
                    val result = TAPSearchChatModel(TAPSearchChatModel.Type.ROOM_ITEM)
                    result.room = item
                    vm.groups.add(result)
                }
                showViewState()
                if (vm.groups.size > 1) {
                    vb.tvSectionTitle.text = String.format(getString(R.string.tap_s_format_groups_in_common), vm.groups.size.toString())
                }
                else if (vm.groups.size > 0) {
                    vb.tvSectionTitle.text = getString(R.string.tap_one_group_in_common)
                }
            }
            else {
                showEmptyState()
            }
        }

        override fun onError(error: TAPErrorModel?) {
            super.onError(error)
            endLoading()
            Toast.makeText(this@TapGroupsInCommonActivity, error?.message, Toast.LENGTH_SHORT).show()
            finish()
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            endLoading()
            Toast.makeText(this@TapGroupsInCommonActivity, errorMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
