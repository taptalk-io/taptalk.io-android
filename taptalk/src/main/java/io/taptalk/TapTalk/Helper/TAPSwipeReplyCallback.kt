package io.taptalk.TapTalk.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter
import kotlin.math.min

class TAPSwipeReplyCallback(
        context: Context,
        private val swipeReplyInterface: SwipeReplyInterface) :
        ItemTouchHelper.Callback() {
    private val imageDrawable = ContextCompat.getDrawable(context, R.drawable.tap_ic_reply_circle_white)!!

    private lateinit var itemView: View
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var dX = 0f
    private var replyButtonProgress: Float = 0.toFloat()
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false

    interface SwipeReplyInterface {
        fun onItemSwiped(position: Int)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder is TAPMessageAdapter.DeletedVH ||
                (viewHolder is TAPBaseChatViewHolder &&
                        null != viewHolder.item.failedSend &&
                        viewHolder.item.failedSend!!)) {
            // Disable swipe for deleted messages & messages that failed to send
            return 0
        }
        itemView = viewHolder.itemView
        return makeMovementFlags(ACTION_STATE_IDLE, RIGHT)
    }

    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
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

        if (itemView.translationX < (130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        currentItemViewHolder = viewHolder
        drawReplyButton(c)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (Math.abs(itemView.translationX) >= (100)) {
                    swipeReplyInterface.onItemSwiped(viewHolder.adapterPosition)
                    recyclerView.setOnTouchListener(null)
                }
            }
            false
        }
    }

    private fun drawReplyButton(canvas: Canvas) {
        if (currentItemViewHolder == null) {
            return
        }
        val translationX = itemView.translationX
        val newTime = System.currentTimeMillis()
        val dt = min(17, newTime - lastReplyButtonAnimationTime)
        lastReplyButtonAnimationTime = newTime
        val showing = translationX >= TAPUtils.dpToPx(30)
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f
                } else {
                    itemView.invalidate()
                }
            }
        } else if (translationX <= 0.0f) {
            replyButtonProgress = 0f
            startTracking = false
            isVibrate = false
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f
                } else {
                    itemView.invalidate()
                }
            }
        }
//        val alpha: Int
        val scale: Float
        if (showing) {
            scale = if (replyButtonProgress <= 0.8f) {
                1.2f * (replyButtonProgress / 0.8f)
            } else {
                1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f)
            }
//            alpha = min(255f, 255 * (replyButtonProgress / 0.8f)).toInt()
        } else {
            scale = replyButtonProgress
//            alpha = min(255f, 255 * replyButtonProgress).toInt()
        }

//        imageDrawable.alpha = alpha
        if (startTracking) {
            if (!isVibrate && itemView.translationX >= (100)) {
                itemView.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
                isVibrate = true
            }
        }

        val x: Int = if (itemView.translationX > TAPUtils.dpToPx(120)) {
            TAPUtils.dpToPx(120) / 2
        } else {
            (itemView.translationX / 2).toInt()
        }

        val y = (itemView.top + itemView.measuredHeight / 2).toFloat()
        imageDrawable.setBounds(
                (x - TAPUtils.dpToPx(16) * scale).toInt() - 1,
                (y - TAPUtils.dpToPx(16) * scale).toInt() - 1,
                (x + TAPUtils.dpToPx(16) * scale).toInt() + 1,
                (y + TAPUtils.dpToPx(16) * scale).toInt() + 1
        )
        imageDrawable.draw(canvas)
//        imageDrawable.alpha = 255
    }
}
