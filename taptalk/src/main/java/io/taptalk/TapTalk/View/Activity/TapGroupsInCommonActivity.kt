package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPSearchChatAdapter
import io.taptalk.TapTalk.ViewModel.TapGroupsInCommonViewModel
import kotlinx.android.synthetic.main.tap_activity_groups_in_common.*

class TapGroupsInCommonActivity :  TAPBaseActivity() {

    private val vm : TapGroupsInCommonViewModel by lazy {
        ViewModelProvider(this)[TapGroupsInCommonViewModel::class.java]
    }
    private lateinit var adapter : TAPSearchChatAdapter

    companion object {
        fun start(
            context: Context,
            instanceKey: String?,
            room: TAPRoomModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapGroupsInCommonActivity::class.java)
                intent.putExtra(Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(Extras.ROOM, room)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.tap_activity_groups_in_common)
        if (intent.getParcelableExtra<TAPRoomModel>(Extras.ROOM) != null) {
            vm.room = intent.getParcelableExtra(Extras.ROOM)
        }
        // TODO: call get groups in common api MU
        adapter = TAPSearchChatAdapter(
            instanceKey,
            vm.groups,
            Glide.with(this),
            roomListInterface
        )
        rv_groups.adapter = adapter
        rv_groups.setHasFixedSize(false)
        rv_groups.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun showLoading() {
        ll_loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        ll_loading.visibility = View.GONE
    }

    private fun showViewState() {
        g_view_state.visibility = View.VISIBLE
        g_empty_state.visibility = View.GONE
    }

    private fun showEmptyState() {
        g_view_state.visibility = View.GONE
        g_empty_state.visibility = View.VISIBLE
    }

    private val roomListInterface =
        TapTalkRoomListInterface { room ->
            // Open chat room
            TapUIChatActivity.start(this, instanceKey, room)
        }
}