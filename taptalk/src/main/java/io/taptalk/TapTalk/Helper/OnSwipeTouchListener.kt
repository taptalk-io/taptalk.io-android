package io.taptalk.TapTalk.Helper

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


open class OnSwipeTouchListener(private val ctx: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector by lazy {GestureDetector(ctx, GestureListener()) }

    companion object {

        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val action: Int = event.action
        when (action) {
            MotionEvent.ACTION_UP -> {
                return onActionUp()
            }
        }

        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        result = if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    result = if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }
    }

    open fun onSwipeRight() : Boolean { return false}

    open fun onSwipeLeft() : Boolean { return false}

    open fun onSwipeTop() : Boolean { return false}

    open fun onSwipeBottom() : Boolean { return false}

    open fun onActionUp() : Boolean { return false}
}