package io.taptalk.TapTalk.Helper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.taptalk.TapTalk.R

class TapVerticalIndicator(context : Context, attrs : AttributeSet?) : ConstraintLayout(context, attrs) {

    private var vIndicator1 : View
    private var vIndicator2 : View
    private var vIndicator3 : View
    private var vIndicator4 : View
    private var size = 1
    private var selectedIndex = 0
    private var indicators : ArrayList<View> = arrayListOf()

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.tap_layout_vertical_indicator, this, true)
        vIndicator1 = view.findViewById(R.id.v_indicator_1)
        vIndicator2 = view.findViewById(R.id.v_indicator_2)
        vIndicator3 = view.findViewById(R.id.v_indicator_3)
        vIndicator4 = view.findViewById(R.id.v_indicator_4)
        setSize(size)
    }

    fun setSize(size: Int) {
        this.size = size
        indicators.clear()
        if (size > 0) {
            indicators.add(vIndicator4)
            vIndicator4.isVisible = true
        } else {
            vIndicator4.isVisible = false
        }
        if (size > 1) {
            indicators.add(vIndicator3)
            vIndicator3.isVisible = true
        } else {
            vIndicator3.isVisible = false
        }
        if (size > 2) {
            indicators.add(vIndicator2)
            vIndicator2.isVisible = true
        } else {
            vIndicator2.isVisible = false
        }
        if (size > 3) {
            indicators.add(vIndicator1)
            vIndicator1.isVisible = true
        } else {
            vIndicator1.isVisible = false
        }
        select(selectedIndex)
    }

    fun select(index : Int) {
        selectedIndex = when {
            index >= size -> 0
            index < 0 -> size - 1
            else -> index
        }
        if (size > indicators.size) {
            val viewIndex : Int =
                when {
                    selectedIndex == size -1 -> indicators.size - 1
                    selectedIndex >= indicators.size -1 -> indicators.size - 2
                    else -> selectedIndex
                }
            setSelectedIndicator(viewIndex)
        } else {
            setSelectedIndicator(selectedIndex)
        }
    }

    private fun setSelectedIndicator(index: Int) {
        for (i in 0 until indicators.size) {
            if (i == index) {
                indicators[i].setBackgroundColor(ContextCompat.getColor(context, R.color.tapColorPrimary))
            } else {
                if (indicators[i] == vIndicator1) {
                    indicators[i].setBackgroundColor(ContextCompat.getColor(context, R.color.tapOrange10))
                } else {
                    indicators[i].setBackgroundColor(ContextCompat.getColor(context, R.color.tapOrange20))
                }
            }
        }
    }
}