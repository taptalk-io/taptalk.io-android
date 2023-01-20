package io.taptalk.TapTalk.View.Adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.R

class TapSelectableStringListAdapter(selectableStrings: List<String>, private val onClickListener: TAPGeneralListener<String>) : TAPBaseAdapter<String, TAPBaseViewHolder<String>>() {

    private var selectedText : String = ""
    private var isEnabled = true

    init {
        items = selectableStrings
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TAPBaseViewHolder<String> {
        return SelectableStringViewHolder(parent, R.layout.tap_cell_report_item)
    }

    inner class SelectableStringViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<String>(parent, viewType) {
        private val ivSelect = itemView.findViewById<ImageView>(R.id.iv_select)
        private val tvOption = itemView.findViewById<TextView>(R.id.tv_option)
        override fun onBind(item: String, position: Int) {
            val resource =
            if (item == getSelectedText()) {
                if (isEnabled)
                    R.drawable.tap_ic_circle_active_check
                else
                    R.drawable.tap_ic_circle_inactive_check
            } else
                R.drawable.tap_ic_radio_button_inactive
            ivSelect.setImageResource(resource)
            tvOption.text = item
            itemView.setOnClickListener {
                onClickListener.onClick(position, item)
            }
        }
    }

    fun setSelectedText(text: String) {
        this.selectedText = text
    }

    fun getSelectedText() : String {
        return this.selectedText
    }

    fun setEnabled(isEnabled : Boolean) {
        this.isEnabled = isEnabled
        notifyDataSetChanged()
    }

    fun isEnabled() : Boolean {
        return this.isEnabled
    }
}