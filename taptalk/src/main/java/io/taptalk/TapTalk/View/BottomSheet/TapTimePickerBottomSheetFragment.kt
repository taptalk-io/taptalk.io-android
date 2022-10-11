package io.taptalk.TapTalk.View.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.R
import kotlinx.android.synthetic.main.tap_fragment_time_picker_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: updaet listener model when api ready MU
class TapTimePickerBottomSheetFragment(private val listener: TAPGeneralListener<TAPRoomModel>) : BottomSheetDialogFragment() {

    private lateinit var currentDate : Date
    private lateinit var currentCal : Calendar
    private val sdf = SimpleDateFormat("E dd MMM", Locale.getDefault())
    private val resultSdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    private val fullSdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    private val dates = arrayOfNulls<String>(365)
    private val hours = arrayOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
        "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    )
    private val minutes = arrayOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
        "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
        "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
        "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDate = Date()
        currentCal = Calendar.getInstance()
        val currentHour = currentCal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCal.get(Calendar.MINUTE)
        val increment = if (currentHour == 23 && currentMinute == 59) {
            val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(1)))
            dates[0] = sdf.format(date)
            1
        } else {
            dates[0] = getString(R.string.tap_today)
            0
        }
        for (i in 1 until 365) {
            val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(i + increment.toLong())))
            dates[i] = sdf.format(date)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tap_fragment_time_picker_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        np_date.displayedValues = null
        np_date.minValue = 0
        np_date.maxValue = dates.size - 1
        np_date.wrapSelectorWheel = false
        np_date.displayedValues = dates
        np_date.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            btn_send.text = getTimeResult()
        }

        np_hour.displayedValues = null
        np_hour.minValue = hours[0].toInt()
        np_hour.maxValue = hours[hours.size-1].toInt()
        np_hour.displayedValues = hours
        np_hour.value = currentCal.get(Calendar.HOUR_OF_DAY)
        np_hour.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            btn_send.text = getTimeResult()
        }

        np_minute.displayedValues = null
        np_minute.minValue = minutes[0].toInt()
        np_minute.maxValue = minutes[minutes.size-1].toInt()
        np_minute.displayedValues = minutes
        np_minute.value = currentCal.get(Calendar.MINUTE) + 1
        np_minute.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            btn_send.text = getTimeResult()
        }

        tv_cancel_btn.setOnClickListener {
            dismiss()
        }
        btn_send.text = getTimeResult()
        btn_send.setOnClickListener {
            listener.onClick()
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun getTimeResult(): String {
        val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(np_date.value.toLong())))
        return "Send on ${resultSdf.format(date)} at ${hours[np_hour.value]}:${minutes[ np_minute.value]}"
    }

    private fun checkTimeValue() {
        val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(np_date.value.toLong())))
        val shownTime = "${resultSdf.format(date)} ${hours[np_hour.value]}:${minutes[np_minute.value]}"
        val shownDate = fullSdf.parse(shownTime)
        if (shownDate != null) {
            if (shownDate.time < System.currentTimeMillis()) {
                // time less than current, reset value
                currentCal = Calendar.getInstance()
                np_date.value = 0
                np_hour.value = currentCal.get(Calendar.HOUR_OF_DAY)
                np_minute.value = currentCal.get(Calendar.MINUTE) + 1
            }
        }
    }
}