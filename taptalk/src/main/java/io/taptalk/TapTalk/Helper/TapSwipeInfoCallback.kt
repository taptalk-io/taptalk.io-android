package io.taptalk.TapTalk.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.*
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import kotlin.math.abs
import kotlin.math.min

class TapSwipeInfoCallback(
    private val instanceKey: String,
    private val context: Context,
    private val swipeReplyInterface: SwipeReplyInterface
) : ItemTouchHelper.Callback() {

    private lateinit var itemView: View
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var dX = 0f
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false
    var isSwipeEnabled = true

    interface SwipeReplyInterface {
        fun onItemSwiped(position: Int)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (!TapUI.getInstance(instanceKey).isMessageInfoMenuEnabled) {
            // Message info menu disabled from TapUI
            return 0
        }
        if (viewHolder is TAPMessageAdapter.DeletedVH) {
            // Disable swipe for deleted messages
            return 0
        }
        if (viewHolder is TAPBaseChatViewHolder) {
            if (null != viewHolder.item.isFailedSend && viewHolder.item.isFailedSend!!) {
                // Disable swipe for messages that failed to send
                return 0
            }
            else if (null == viewHolder.item?.user?.userID || viewHolder.item.user.userID != TAPChatManager.getInstance(instanceKey).activeUser?.userID) {
                // Disable swipe for others' messages
                return 0
            }
            else if (
                viewHolder.item?.room?.roomID != null &&
                TAPDataManager.getInstance(instanceKey).blockedUserIds.contains(
                    TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(viewHolder.item?.room?.roomID)
                )
            ) {
                // Disable swipe for blocked user chat room
                return 0
            }
            else if (viewHolder.item.type != TYPE_TEXT &&
                    viewHolder.item.type != TYPE_LINK &&
                    viewHolder.item.type != TYPE_IMAGE &&
                    viewHolder.item.type != TYPE_VIDEO &&
                    viewHolder.item.type != TYPE_FILE &&
                    viewHolder.item.type != TYPE_LOCATION &&
                    viewHolder.item.type != TYPE_VOICE
            ) {
                // Disable swipe for custom message types
                return 0
            }
        }
        itemView = viewHolder.itemView
        return makeMovementFlags(ACTION_STATE_IDLE, LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }
//        if (itemView.translationX < TAPUtils.dpToPx(56) || dX < this.dX) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//            this.dX = dX
//            startTracking = true
//        }
        val swipeLimit = TAPUtils.dpToPx(-56)
        val newX: Float = if (itemView.translationX > swipeLimit || dX > this.dX) {
            dX
        }
        else {
            swipeLimit.toFloat()
        }
        super.onChildDraw(c, recyclerView, viewHolder, newX, dY, actionState, isCurrentlyActive)
        this.dX = newX
        startTracking = true
        currentItemViewHolder = viewHolder

        if (currentItemViewHolder == null) {
            return
        }

        if (itemView.translationX >= 0.0f) {
            startTracking = false
            isVibrate = false
        }

        if (startTracking) {
            if (!isVibrate && itemView.translationX <= TAPUtils.dpToPx(-48)) {
                itemView.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
                isVibrate = true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (abs(itemView.translationX) >= TAPUtils.dpToPx(48)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        swipeReplyInterface.onItemSwiped(viewHolder.bindingAdapterPosition)
                    }, 100L)
                    recyclerView.setOnTouchListener(null)
                }
            }
            false
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return isSwipeEnabled
    }

}
