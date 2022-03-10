package io.taptalk.TapTalk.Helper.SwipedListItem

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPRoomListAdapter
import kotlin.math.max
import kotlin.math.min

class ListItemSwipeCallback(private val instanceKey: String) : ItemTouchHelper.Callback() {

    private var currentDx = 0f
    private var swipeBack = false
    private var currentRecyclerView: RecyclerView? = null
    private var previousViewHolder: RecyclerView.ViewHolder? = null
    private var clamp: Float = 0f
    private var onMoveAndSwipedListener: OnMoveAndSwipeListener? = null

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (((viewHolder as TAPRoomListAdapter.RoomListVH).item.isMarkAsUnread || viewHolder.item.numberOfUnreadMessages > 0)
            && TapUI.getInstance(instanceKey).isMarkAsUnreadRoomListSwipeMenuEnabled
        ) {
            if (getTag((viewHolder as RecyclerView.ViewHolder))) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)

        } else if (!viewHolder.item.isMarkAsUnread && TapUI.getInstance(instanceKey).isMarkAsReadRoomListSwipeMenuEnabled) {
            if (getTag(viewHolder)) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)
        }
        else {
            return 0
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
        onMoveAndSwipedListener?.onItemClick(viewHolder.layoutPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val view = getContentView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x = clampViewPosition(view, dX, isClamped, isCurrentlyActive)
            currentDx = x
            getDefaultUIUtil().onDraw(c, recyclerView, view, x, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        (recyclerView.adapter as ManageListServiceAdapter).setSwiped(getTag(viewHolder))
        getDefaultUIUtil().clearView(getContentView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        getDefaultUIUtil().onSelected(viewHolder?.let { getContentView(it) })
    }

    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, currentlyActive: Boolean) {
        val x = if (getTag(viewHolder)) {
            dX + clamp
        } else {
            dX
        }
        val view = getContentView(viewHolder)
        recyclerView.setOnTouchListener { v, event ->
            currentRecyclerView = recyclerView
            removePreviousClamp()
            if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                swipeBack = if (x <= view.width/2) {
                    setTag(viewHolder,  x >= clamp)
                    previousViewHolder = viewHolder
                    true
                } else {
                    setTag(viewHolder, false)
                    true
                }
            }
            false
        }
    }


    private fun getContentView(viewHolder: RecyclerView.ViewHolder): View {
        return  viewHolder.itemView.findViewById(R.id.cl_content)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
        if (!isClamped) {
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read).setOnClickListener {  }
        } else {
            viewHolder.itemView.findViewById<LinearLayout>(R.id.ll_mark_read).setOnClickListener {
                setTag(viewHolder, false)
                swipeBack = false
                currentDx = 0f
                removePreviousClamp()
                onMoveAndSwipedListener?.onItemClick(viewHolder.layoutPosition)
            }
        }
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemView.tag != null) {
            return viewHolder.itemView.tag as Boolean
        }

        return false
    }

    private fun clampViewPosition(view: View, dX: Float, isClamped: Boolean, isCurrentlyActive: Boolean): Float {
        val min = 0f
        val max = view.width.toFloat()
        val x: Float = if (isClamped) {
            if (isCurrentlyActive) {
                dX + clamp
            } else {
                clamp
            }
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    fun setListener(onMoveAndSwipedListener: OnMoveAndSwipeListener) {
        this.onMoveAndSwipedListener = onMoveAndSwipedListener
    }

    fun removePreviousClamp() {
        previousViewHolder?.let { getContentView(it).translationX = 0f }
        previousViewHolder?.let { setTag(it, false) }
        previousViewHolder = null
    }
}