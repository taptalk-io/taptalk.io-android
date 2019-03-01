package io.taptalk.TapTalk.View.Adapter

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Model.TAPLocationItem
import io.taptalk.TapTalk.Model.TAPLocationItem.MyReturnType.*
import io.taptalk.Taptalk.R

class TAPSearchLocationAdapter(items: List<TAPLocationItem>) : TAPBaseAdapter<TAPLocationItem, TAPBaseViewHolder<TAPLocationItem>>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TAPBaseViewHolder<TAPLocationItem> {
        when (TAPLocationItem.MyReturnType.values()[p1]) {
            FIRST -> return BasicViewHolder(p0, R.layout.tap_location_result_first)
            MIDDLE -> return BasicViewHolder(p0, R.layout.tap_location_result_middle)
            LAST -> return BasicViewHolder(p0, R.layout.tap_location_result_bottom)
            else -> return BasicViewHolder(p0, R.layout.tap_location_result_only_one)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items.get(position).myReturnType.ordinal
    }

    class BasicViewHolder : TAPBaseViewHolder<TAPLocationItem>, View.OnClickListener {
        var tvLocation : TextView? = null

        constructor(parent: ViewGroup?, itemLayoutId: Int) : super(parent, itemLayoutId) {
            tvLocation = itemView.findViewById(R.id.tv_location)
        }

        override fun onBind(item: TAPLocationItem?, position: Int) {
            tvLocation?.text = item?.prediction?.getFullText(StyleSpan(Typeface.NORMAL))
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}