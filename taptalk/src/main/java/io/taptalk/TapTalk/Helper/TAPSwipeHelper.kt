package io.taptalk.TapTalk.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.R

class TAPSwipeHelper(
        private val context: Context,
        private val swipeControllerActions: SwipeControllerActions) :
        ItemTouchHelper.Callback() {

    private val imageDrawable = ContextCompat.getDrawable(context, R.drawable.tap_ic_reply_circle_white)!!

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private lateinit var mView: View
    private var dX = 0f

    private var replyButtonProgress: Float = 0.toFloat()
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false
    private var currentViewHolderSwiped = false

    interface SwipeControllerActions {
        fun showReplyUI(position: Int)
    }

//    override fun isItemViewSwipeEnabled(): Boolean {
//        return !currentViewHolderSwiped
//    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        Log.e("SwipeHelper", "getMovementFlags: ${makeMovementFlags(ACTION_STATE_IDLE, RIGHT)}")
        mView = viewHolder.itemView
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
            Log.e("SwipeHelper", "convertToAbsoluteDirection: 0")
            return 0
        }
        Log.e("SwipeHelper", "convertToAbsoluteDirection: ${super.convertToAbsoluteDirection(flags, layoutDirection)}")
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
        Log.e("SwipeHelper", "onChildDraw: action $actionState, ${currentItemViewHolder == viewHolder}, $currentViewHolderSwiped")
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }


        if (currentItemViewHolder == viewHolder && currentViewHolderSwiped) {
            Log.e("SwipeHelper", "onChildDraw: return")
            return
        }


        if (mView.translationX < (130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        currentItemViewHolder = viewHolder
        currentViewHolderSwiped = false
        drawReplyButton(c)
    }

    fun currentViewHolderSwiped() : Boolean {
        return currentViewHolderSwiped
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        Log.e("SwipeHelper", "clearView: ")
        super.clearView(recyclerView, viewHolder)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (Math.abs(mView.translationX) >= (100)) {
                    swipeControllerActions.showReplyUI(viewHolder.adapterPosition)
                    recyclerView.setOnTouchListener(null)
                    currentViewHolderSwiped = true
                }
            }
            false
        }
    }

    private fun drawReplyButton(canvas: Canvas) {
        if (currentItemViewHolder == null) {
            return
        }
        val translationX = mView.translationX
        val newTime = System.currentTimeMillis()
        val dt = Math.min(17, newTime - lastReplyButtonAnimationTime)
        lastReplyButtonAnimationTime = newTime
        val showing = translationX >= (30)
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f
                } else {
                    mView.invalidate()
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
                    mView.invalidate()
                }
            }
        }
        val alpha: Int
        val scale: Float
        if (showing) {
            scale = if (replyButtonProgress <= 0.8f) {
                1.2f * (replyButtonProgress / 0.8f)
            } else {
                1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f)
            }
            alpha = Math.min(255f, 255 * (replyButtonProgress / 0.8f)).toInt()
        } else {
            scale = replyButtonProgress
            alpha = Math.min(255f, 255 * replyButtonProgress).toInt()
        }
//        shareRound.alpha = alpha

        imageDrawable.alpha = alpha
        if (startTracking) {
            if (!isVibrate && mView.translationX >= (100)) {
                mView.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
                isVibrate = true
            }
        }

        val x: Int = if (mView.translationX > (130)) {
            (130) / 2
        } else {
            (mView.translationX / 2).toInt()
        }

        val y = (mView.top + mView.measuredHeight / 2).toFloat()
//        shareRound.colorFilter =
//                PorterDuffColorFilter(ContextCompat.getColor(context, R.color.tapColorPrimary), PorterDuff.Mode.MULTIPLY)

//        shareRound.setBounds(
//                (x - (18) * scale).toInt(),
//                (y - (18) * scale).toInt(),
//                (x + (18) * scale).toInt(),
//                (y + (18) * scale).toInt()
//        )
//        shareRound.draw(canvas)
        imageDrawable.setBounds(
                (x - (12) * scale).toInt(),
                (y - (11) * scale).toInt(),
                (x + (12) * scale).toInt(),
                (y + (10) * scale).toInt()
        )
        imageDrawable.draw(canvas)
//        shareRound.alpha = 255
        imageDrawable.alpha = 255
    }
}
