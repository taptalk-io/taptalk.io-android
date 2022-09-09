package io.taptalk.TapTalk.View.Adapter

import android.view.ViewGroup
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.R

class TapStringListAdapter(strings: List<String?>, private val onClickListener: TAPGeneralListener<String>) :
    TAPBaseAdapter<String, TAPBaseViewHolder<String>>() {

    init {
        items = strings
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<String> {
        return StringViewHolder(parent, R.layout.tap_cell_mute_menu)
    }

    inner class StringViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<String>(parent, viewType) {
        override fun onBind(item: String?, position: Int) {
            val title = itemView.findViewById<TextView>(R.id.tv_title)
            title.text = item
            itemView.setOnClickListener {
                onClickListener.onClick(position, item)
            }
        }
    }
}