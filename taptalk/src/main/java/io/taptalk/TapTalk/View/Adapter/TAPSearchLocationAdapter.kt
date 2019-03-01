package io.taptalk.TapTalk.View.Adapter

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Model.TAPLocationItem
import io.taptalk.TapTalk.Model.TAPLocationItem.MyReturnType.*
import io.taptalk.Taptalk.R

class TAPSearchLocationAdapter : TAPBaseAdapter<TAPLocationItem, TAPBaseViewHolder<TAPLocationItem>> {
    var generalListener: TAPGeneralListener<TAPLocationItem>? = null

    constructor(items: List<TAPLocationItem>, generalListener: TAPGeneralListener<TAPLocationItem>) {
        setItems(items)
        this.generalListener = generalListener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TAPBaseViewHolder<TAPLocationItem> {
        return when (TAPLocationItem.MyReturnType.values()[p1]) {
            FIRST -> BasicViewHolder(p0, R.layout.tap_location_result_first, generalListener)
            MIDDLE -> BasicViewHolder(p0, R.layout.tap_location_result_middle, generalListener)
            LAST -> BasicViewHolder(p0, R.layout.tap_location_result_bottom, generalListener)
            else -> BasicViewHolder(p0, R.layout.tap_location_result_only_one, generalListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items.get(position).myReturnType.ordinal
    }

    class BasicViewHolder : TAPBaseViewHolder<TAPLocationItem>, View.OnClickListener {
        var tvLocation: TextView? = null
        var generalListener :TAPGeneralListener<TAPLocationItem>? = null

        constructor(parent: ViewGroup?, itemLayoutId: Int, generalListener: TAPGeneralListener<TAPLocationItem>?) : super(parent, itemLayoutId) {
            tvLocation = itemView.findViewById(R.id.tv_location)
            this.generalListener = generalListener
        }

        override fun onBind(item: TAPLocationItem?, position: Int) {
            tvLocation?.text = item?.prediction?.getFullText(StyleSpan(Typeface.NORMAL))
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            generalListener?.onClick(position, item)
        }

    }
}