package io.taptalk.TapTalk.Helper.SwipedListItem

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPRoomListAdapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TapRoomListItemSwipeCallback(private val instanceKey: String) : ItemTouchHelper.Callback() {

    private var currentDx = 0f
    private var swipeBack = false
    private var currentRecyclerView: RecyclerView? = null
    private var previousViewHolder: RecyclerView.ViewHolder? = null
    private var clampRight: Float = 0f
    private var rightButtons: Float =
        if (TapUI.getInstance(instanceKey).isPinMessageMenuEnabled &&
            (TapUI.getInstance(instanceKey).isMarkAsReadRoomListSwipeMenuEnabled ||
            TapUI.getInstance(instanceKey).isMarkAsUnreadRoomListSwipeMenuEnabled))
            2f
        else
            1f
    private var clampLeft: Float = 0f
    private var leftButtons: Float =
        if (TapUI.getInstance(instanceKey).isMuteRoomListSwipeMenuEnabled &&
            (TapUI.getInstance(instanceKey).isDeleteRoomListSwipeMenuEnabled))
            2f
        else
            1f
    private var onMoveAndSwipedListener: OnMoveAndSwipeListener? = null

    enum class SwipeState {
        IDLE, CLAMP_LEFT, CLAMP_RIGHT
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (getTag(viewHolder) == SwipeState.CLAMP_LEFT ||
            getTag(viewHolder) == SwipeState.CLAMP_RIGHT ||
            (isSwipeLeftEnabled() && isSwipeRightEnabled(viewHolder))) {
            makeMovementFlags(0, LEFT or RIGHT)
        } else if (isSwipeRightEnabled(viewHolder)) {
            makeMovementFlags(0, RIGHT)
        } else if (isSwipeLeftEnabled()) {
            makeMovementFlags(0, LEFT)
        } else {
            0
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == RIGHT)
            if (TapUI.getInstance(instanceKey).isMarkAsUnreadRoomListSwipeMenuEnabled ||
                    TapUI.getInstance(instanceKey).isMarkAsReadRoomListSwipeMenuEnabled) {
                onMoveAndSwipedListener?.onReadOrUnreadItem(viewHolder.layoutPosition)
            } else {
                onMoveAndSwipedListener?.onPinOrUnpinItem(viewHolder.layoutPosition)
            }
        else if (direction == LEFT)
            if (TapUI.getInstance(instanceKey).isMuteRoomListSwipeMenuEnabled) {
                onMoveAndSwipedListener?.onMuteOrUnmuteItem(viewHolder.layoutPosition)
            } else {
                onMoveAndSwipedListener?.onDeleteItem(viewHolder.layoutPosition)
            }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val view = getContentView(viewHolder)
            val x = clampViewPosition(view, dX, viewHolder, isCurrentlyActive)
            currentDx = x
            getDefaultUIUtil().onDraw(c, recyclerView, view, x, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView(getContentView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        getDefaultUIUtil().onSelected(viewHolder?.let { getContentView(it) })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, currentlyActive: Boolean) {
        val x = when {
            getTag(viewHolder) == SwipeState.CLAMP_RIGHT -> {
                dX + (clampRight * rightButtons)
            }
            getTag(viewHolder) == SwipeState.CLAMP_LEFT -> {
                dX - (clampLeft * leftButtons)
            }
            else -> {
                dX
            }
        }
        val view = getContentView(viewHolder)
        recyclerView.setOnTouchListener { _, event ->
            currentRecyclerView = recyclerView
            removePreviousClamp()
            if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                swipeBack = if (x <= view.width/2 && dX > 0) {
                    when {
                        x >= (clampRight * rightButtons) -> {
                            setTag(viewHolder, SwipeState.CLAMP_RIGHT)
                        }
                        else -> {
                            setTag(viewHolder, SwipeState.IDLE)
                        }
                    }
                    previousViewHolder = viewHolder
                    true
                } else if (abs(x) <= view.width/2 && dX < 0) {
                    when {
                        x <= (clampLeft * leftButtons) -> {
                            setTag(viewHolder, SwipeState.CLAMP_LEFT)
                        }
                        else -> {
                            setTag(viewHolder, SwipeState.IDLE)
                        }
                    }
                    previousViewHolder = viewHolder
                    true
                } else if (dX > 0) {
                    setTag(viewHolder, SwipeState.IDLE)
                    onMoveAndSwipedListener?.onReadOrUnreadItem(viewHolder.layoutPosition)
                    true
                } else if (dX < 0) {
                    setTag(viewHolder, SwipeState.IDLE)
                    onMoveAndSwipedListener?.onMuteOrUnmuteItem(viewHolder.layoutPosition)
                    true
                } else {
                    false
                }
            }
            false
        }
    }


    private fun getContentView(viewHolder: RecyclerView.ViewHolder): View {
        return  viewHolder.itemView.findViewById(R.id.cl_content)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, swipeState: SwipeState) {
        viewHolder.itemView.tag = swipeState
        if (swipeState != SwipeState.CLAMP_LEFT && swipeState != SwipeState.CLAMP_RIGHT) {
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read).setOnClickListener {  }
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mute).setOnClickListener {  }
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin).setOnClickListener {  }
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete).setOnClickListener {  }
        } else if (swipeState == SwipeState.CLAMP_RIGHT){
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read).setOnClickListener {
                setTag(viewHolder, SwipeState.IDLE)
                swipeBack = false
                currentDx = 0f
                removePreviousClamp()
                onMoveAndSwipedListener?.onReadOrUnreadItem(viewHolder.layoutPosition)
            }
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin).setOnClickListener {
                setTag(viewHolder, SwipeState.IDLE)
                swipeBack = false
                currentDx = 0f
                removePreviousClamp()
                onMoveAndSwipedListener?.onPinOrUnpinItem(viewHolder.layoutPosition)
            }
        } else if (swipeState == SwipeState.CLAMP_LEFT) {
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mute).setOnClickListener {
                setTag(viewHolder, SwipeState.IDLE)
                swipeBack = false
                currentDx = 0f
                removePreviousClamp()
                onMoveAndSwipedListener?.onMuteOrUnmuteItem(viewHolder.layoutPosition)
            }
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete).setOnClickListener {
                setTag(viewHolder, SwipeState.IDLE)
                swipeBack = false
                currentDx = 0f
                removePreviousClamp()
                onMoveAndSwipedListener?.onDeleteItem(viewHolder.layoutPosition)
            }
        }
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): SwipeState {
        if (viewHolder.itemView.tag != null) {
            return viewHolder.itemView.tag as SwipeState
        }
        return SwipeState.IDLE
    }

    private fun clampViewPosition(view: View, dX: Float, viewHolder: RecyclerView.ViewHolder, isCurrentlyActive: Boolean): Float {
        val min = -view.width.toFloat()
        val max = view.width.toFloat()
        when {
            dX > 0 -> {
                hideRightButtons(viewHolder)
                showLeftButtons(viewHolder)
                if (dX > max / 2) {
                    hidePinButton(viewHolder)
                } else {
                    showPinButton(viewHolder)
                }
            }
            dX < 0 -> {
                hideLeftButtons(viewHolder)
                showRightButtons(viewHolder)
                if (dX < 0 - (max / 2)) {
                    hideDeleteButton(viewHolder)
                } else {
                    showDeleteButton(viewHolder)
                }
            }
            else -> {
                showLeftButtons(viewHolder)
                showRightButtons(viewHolder)
            }
        }
        val x: Float = if (viewHolder.itemView.tag == SwipeState.CLAMP_RIGHT) {
            if (isCurrentlyActive) {
                dX + clampRight * rightButtons
            } else {
                clampRight * rightButtons
            }
        } else if (viewHolder.itemView.tag == SwipeState.CLAMP_LEFT){
            if (isCurrentlyActive) {
                dX - clampLeft * leftButtons
            } else {
                clampLeft * leftButtons
            }
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    fun setClamp(clampRight: Float, clampLeft: Float) {
        this.clampRight = clampRight
        this.clampLeft = clampLeft
    }

    fun setListener(onMoveAndSwipedListener: OnMoveAndSwipeListener) {
        this.onMoveAndSwipedListener = onMoveAndSwipedListener
    }

    private fun removePreviousClamp() {
        previousViewHolder?.let { getContentView(it).translationX = 0f }
        previousViewHolder?.let { setTag(it, previousViewHolder?.itemView?.tag as SwipeState) }
        previousViewHolder = null
    }

    private fun isSwipeRightEnabled(viewHolder: RecyclerView.ViewHolder): Boolean {
        //mark as read
        return ((viewHolder as TAPRoomListAdapter.RoomListVH).item.isMarkedAsUnread || viewHolder.item.numberOfUnreadMessages > 0) &&
                TapUI.getInstance(instanceKey).isMarkAsUnreadRoomListSwipeMenuEnabled ||
                //mark as unread
            !viewHolder.item.isMarkedAsUnread && TapUI.getInstance(instanceKey).isMarkAsReadRoomListSwipeMenuEnabled ||
                TapUI.getInstance(instanceKey).isPinMessageMenuEnabled
    }

    private fun isSwipeLeftEnabled(): Boolean {
        //mute
        return TapUI.getInstance(instanceKey).isMuteRoomListSwipeMenuEnabled || TapUI.getInstance(instanceKey).isDeleteRoomListSwipeMenuEnabled
    }

    private fun showLeftButtons(viewHolder: RecyclerView.ViewHolder) {
        val markAsReadButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read)
        val pinButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin)
        if (markAsReadButton.visibility == View.INVISIBLE) {
            markAsReadButton.visibility = View.VISIBLE
        }
        if (pinButton.visibility == View.INVISIBLE) {
            pinButton.visibility = View.VISIBLE
        }
    }

    private fun showRightButtons(viewHolder: RecyclerView.ViewHolder) {
        val muteButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mute)
        val deleteButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete)
        if (muteButton.visibility == View.INVISIBLE) {
            muteButton.visibility = View.VISIBLE
        }
        if (deleteButton.visibility == View.INVISIBLE) {
            deleteButton.visibility = View.VISIBLE
        }
    }

    private fun hideLeftButtons(viewHolder: RecyclerView.ViewHolder) {
        val markAsReadButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read)
        val pinButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin)
        if (markAsReadButton.visibility == View.VISIBLE) {
            markAsReadButton.visibility = View.INVISIBLE
        }
        if (pinButton.visibility == View.VISIBLE) {
            pinButton.visibility = View.INVISIBLE
        }
    }

    private fun hidePinButton(viewHolder: RecyclerView.ViewHolder) {
        val pinButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin)
        if (pinButton.visibility == View.VISIBLE) {
            pinButton.visibility = View.INVISIBLE
        }
    }

    private fun showPinButton(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_pin).visibility = View.VISIBLE
    }
    private fun hideDeleteButton(viewHolder: RecyclerView.ViewHolder) {
        val deleteButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete)
        if (deleteButton.visibility == View.VISIBLE) {
            deleteButton.visibility = View.INVISIBLE
        }
    }

    private fun showDeleteButton(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete).visibility = View.VISIBLE
    }

    private fun hideRightButtons(viewHolder: RecyclerView.ViewHolder) {
        val muteButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mute)
        val deleteButton = viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_delete)
        if (muteButton.visibility == View.VISIBLE) {
            muteButton.visibility = View.INVISIBLE
        }
        if (deleteButton.visibility == View.VISIBLE) {
            deleteButton.visibility = View.INVISIBLE
        }
    }

}