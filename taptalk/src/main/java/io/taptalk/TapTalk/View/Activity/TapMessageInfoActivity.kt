package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration
import io.taptalk.TapTalk.Listener.TapCoreGetMessageDetailsListener
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapMessageInfoAdapter
import io.taptalk.TapTalk.ViewModel.TapMessageInfoViewModel
import kotlinx.android.synthetic.main.tap_activity_message_info.*

class TapMessageInfoActivity : TAPBaseActivity() {

    private val vm : TapMessageInfoViewModel by lazy {
        ViewModelProvider(this)[TapMessageInfoViewModel::class.java]
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?,
            message: TAPMessageModel,
            room: TAPRoomModel,
            isStarred: Boolean,
            isPinned: Boolean
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapMessageInfoActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
                intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
                intent.putExtra(TAPDefaultConstant.Extras.IS_STARRED, isStarred)
                intent.putExtra(TAPDefaultConstant.Extras.IS_PINNED, isPinned)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_message_info)
        if (intent.getParcelableExtra<TAPMessageModel>(TAPDefaultConstant.Extras.MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(TAPDefaultConstant.Extras.MESSAGE)
        }
        if (vm.message == null) {
            onBackPressed()
        }
        if (intent.getParcelableExtra<TAPRoomModel>(TAPDefaultConstant.Extras.ROOM) != null) {
            vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        }
        vm.isStarred = intent.getBooleanExtra(TAPDefaultConstant.Extras.IS_STARRED, false)
        vm.isPinned = intent.getBooleanExtra(TAPDefaultConstant.Extras.IS_PINNED, false)

        val resultList = listOf(TapMessageRecipientModel())
        val adapter = TapMessageInfoAdapter(instanceKey, vm.message!!, resultList)
        rv_message_info.adapter = adapter
        rv_message_info.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_message_info.setHasFixedSize(true)
        rv_message_info.addItemDecoration(
            TAPVerticalDecoration(
                TAPUtils.dpToPx(12),
                TAPUtils.dpToPx(16),
                0
            )
        )

        iv_button_back.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        ll_loading.visibility = View.VISIBLE
        TapCoreMessageManager.getInstance(instanceKey).getMessageDetails(vm.message?.messageID, messageInfoView)
    }

    private val messageInfoView = object : TapCoreGetMessageDetailsListener() {
        override fun onSuccess(
            message: TAPMessageModel,
            deliveredTo: List<TapMessageRecipientModel>?,
            readBy: List<TapMessageRecipientModel>?
        ) {
            ll_loading.visibility = View.GONE

            vm.readList.clear()
            val resultList = ArrayList<TapMessageRecipientModel>()
            if (readBy != null) {
                vm.readList.addAll(ArrayList(readBy))
            }
            vm.deliveredList.clear()
            if (deliveredTo != null) {
                vm.deliveredList.addAll(ArrayList(deliveredTo))
            }

            resultList.add(TapMessageRecipientModel())
            if (vm.readList.isNotEmpty() && !TapUI.getInstance(instanceKey).isReadStatusHidden) {
                resultList.add(TapMessageRecipientModel(vm.readList.size.toLong(), null))
                resultList.addAll(vm.readList)
                if (vm.deliveredList.isNotEmpty()) {
                    resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
                    resultList.addAll(vm.deliveredList)
                }
            } else {
                resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
                if (vm.deliveredList.isNotEmpty()) {
                    resultList.addAll(vm.deliveredList)
                }
            }
            (rv_message_info.adapter as TapMessageInfoAdapter).items = resultList

            rv_message_info.post {
                addStickyHeaderScrollListener(vm.readList.size, vm.deliveredList.size)
                val layoutManager = rv_message_info.layoutManager as LinearLayoutManager?
                if (!vm.isFirstLoadFinished) {
                    vm.isFirstLoadFinished = true
                    layoutManager?.scrollToPositionWithOffset(1, TAPUtils.getScreenHeight() / 2)
                }
            }
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            ll_loading.visibility = View.GONE
            Toast.makeText(this@TapMessageInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addStickyHeaderScrollListener(readCount: Int, deliveredCount: Int) {
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Handle sticky section header
                if (readCount <= 0 && deliveredCount <= 0) {
                    fl_sticky_section_container.visibility = View.GONE
                    return
                }

                val layoutManager = rv_message_info.layoutManager as LinearLayoutManager?
                val adapter = rv_message_info.adapter as TapMessageInfoAdapter?
                val firstVisibleIndex = layoutManager?.findFirstVisibleItemPosition() ?: -1

                if (firstVisibleIndex < 0 || firstVisibleIndex >= (adapter?.itemCount ?: 0)) {
                    return
                }

                val firstVisibleItem = adapter?.getItemAt(firstVisibleIndex)

                if (null == firstVisibleItem/* || cl_sticky_section_container.visibility != View.VISIBLE*/) {
                    return
                }

                if (
                    firstVisibleItem.userID.isNullOrEmpty() &&
                    (firstVisibleItem.readTime == null || firstVisibleItem.readTime <= 0L) &&
                    (firstVisibleItem.deliveredTime == null || firstVisibleItem.deliveredTime <= 0L)
                ) {
                    fl_sticky_section_container.visibility = View.GONE
                    return
                }

                if (firstVisibleItem.readTime != null && firstVisibleItem.readTime > 0L) {
                    // Set Read by
                    tv_sticky_section_title.text = String.format(getString(R.string.tap_read_by_d_format), readCount)
                    iv_sticky_section_icon.setImageDrawable(ContextCompat.getDrawable(this@TapMessageInfoActivity, R.drawable.tap_ic_read_orange))
                }
                else if (firstVisibleItem.deliveredTime != null && firstVisibleItem.deliveredTime > 0L) {
                    // Set Delivered to
                    tv_sticky_section_title.text = String.format(getString(R.string.tap_delivered_to_d_format), deliveredCount)
                    iv_sticky_section_icon.setImageDrawable(ContextCompat.getDrawable(this@TapMessageInfoActivity, R.drawable.tap_ic_delivered_grey))
                }

                val secondVisibleCell: View? = layoutManager?.findViewByPosition(firstVisibleIndex + 1)
                val secondVisibleItem = adapter.getItemAt(firstVisibleIndex + 1)

                if (null == secondVisibleCell || null == secondVisibleItem) {
                    return
                }

                val recyclerViewLocation = IntArray(2)
                rv_message_info?.getLocationOnScreen(recyclerViewLocation)
                val recyclerViewY = recyclerViewLocation[1]

                fl_sticky_section_container.visibility = View.VISIBLE

                if (secondVisibleItem.userID.isNullOrEmpty()) {
                    // Animate sticky header translation if second visible item is header
                    val cellLocation = IntArray(2)
                    secondVisibleCell.getLocationOnScreen(cellLocation)

                    val heightDiff = cellLocation[1] - recyclerViewY
                    val headerHeight = fl_sticky_section_container.height
                    if (heightDiff < headerHeight) {
                        cl_sticky_section_container.translationY = -((headerHeight - heightDiff).toFloat())
                    }
                    else {
                        cl_sticky_section_container.translationY = 0f
                    }
                }
                else {
                    cl_sticky_section_container.translationY = 0f
                }
            }
        }
        rv_message_info.clearOnScrollListeners()
        rv_message_info.addOnScrollListener(onScrollListener)
    }
}
