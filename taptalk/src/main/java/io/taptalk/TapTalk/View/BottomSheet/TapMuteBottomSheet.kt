package io.taptalk.TapTalk.View.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Model.ResponseModel.TapMutedRoomListModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapStringListAdapter
import kotlinx.android.synthetic.main.tap_fragment_long_press_action_bottom_sheet.*
import java.util.concurrent.TimeUnit

class TapMuteBottomSheet(private val roomId: String, private  val clickListener: TAPGeneralListener<TapMutedRoomListModel>, private val menuType: MenuType = MenuType.MUTE) : BottomSheetDialogFragment() {
    enum class MenuType {
        MUTE, UNMUTE
    }

    constructor(roomId: String, clickListener: TAPGeneralListener<TapMutedRoomListModel>) : this(roomId, clickListener, MenuType.MUTE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tap_fragment_long_press_action_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val unmuteList = listOf(context?.getString(R.string.tap_unmute))
        val muteList = listOf(context?.getString(R.string.tap_one_hour), context?.getString(R.string.tap_eight_hours), context?.getString(
            R.string.tap_three_days), context?.getString(R.string.tap_always))
        val adapter = if (menuType == MenuType.MUTE) {
            TapStringListAdapter(muteList, onItemClickListener)
        } else {
            TapStringListAdapter(unmuteList, onItemClickListener)
        }
        rv_long_press.adapter = adapter
        rv_long_press.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_long_press.setHasFixedSize(true)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private val onItemClickListener = object : TAPGeneralListener<String>() {
        override fun onClick(position: Int, item: String?) {
            super.onClick(position, item)
            clickListener.onClick(position, TapMutedRoomListModel(
                when (item) {
                getString(R.string.tap_one_hour) -> {
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                }
                getString(R.string.tap_eight_hours) -> {
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8)
                }
                getString(R.string.tap_three_days) -> {
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)
                }
                getString(R.string.tap_always) -> 0L
                else -> -1L
            }, roomId))
            dismiss()
        }
    }
}