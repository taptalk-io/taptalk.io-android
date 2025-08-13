package io.taptalk.TapTalk.View.BottomSheet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Const.TAPDefaultConstant.BroadcastEvent.REOPEN_TIME_PICKER_BOTTOM_SHEET
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.ViewModel.TapScheduledMessageViewModel
import io.taptalk.TapTalk.databinding.TapFragmentTimePickerBottomSheetBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TapTimePickerBottomSheetFragment(
    private val svm: TapScheduledMessageViewModel,
    private val listener: TAPGeneralListener<Long>?,
    private val defaultTimestamp: Long?
) : BottomSheetDialogFragment() {

    private lateinit var vb : TapFragmentTimePickerBottomSheetBinding
    private lateinit var currentDate : Date
    private lateinit var defaultDate : Date
    private lateinit var currentCal : Calendar
    private var dateIndex = 0
    private val sdf = SimpleDateFormat("E dd MMM", Locale.getDefault())
    private val resultSdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    private val fullSdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    private val dates = arrayOfNulls<String>(365)
    private val hours = arrayOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    )
    private val minutes = arrayOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
        "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
        "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
        "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
        "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
    )

    constructor() : this(TapScheduledMessageViewModel(), null, null)

    constructor(svm: TapScheduledMessageViewModel, listener: TAPGeneralListener<Long>) : this(svm, listener, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (listener == null) {
            val currentActivity = activity
            Handler(Looper.getMainLooper()).postDelayed({
                if (currentActivity != null && !currentActivity.isFinishing) {
                    LocalBroadcastManager.getInstance(currentActivity).sendBroadcast(Intent(REOPEN_TIME_PICKER_BOTTOM_SHEET))
                }
            }, 0L)
            dismiss()
            return
        }

        currentDate = Date()
        currentCal = Calendar.getInstance()
        if (defaultTimestamp != null) {
            defaultDate = Date(defaultTimestamp)
            currentCal.timeInMillis = defaultTimestamp
        }
        else {
            defaultDate = currentDate
        }
        val currentHour = currentCal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCal.get(Calendar.MINUTE)
        val increment = if (currentHour == 23 && currentMinute == 59) {
            val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(1)))
            dates[0] = sdf.format(date)
            1
        }
        else {
            dates[0] = getString(R.string.tap_today)
            0
        }
        for (i in 1 until 365) {
            val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(i + increment.toLong())))
            dates[i] = sdf.format(date)
            if (sdf.format(date).equals(sdf.format(defaultDate))) {
                dateIndex = i
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vb = TapFragmentTimePickerBottomSheetBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vb.npDate.displayedValues = null
        vb.npDate.minValue = 0
        vb.npDate.maxValue = dates.size - 1
        vb.npDate.wrapSelectorWheel = false
        vb.npDate.displayedValues = dates
        vb.npDate.value = dateIndex
        vb.npDate.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            vb.btnSend.text = getTimeResult()
        }

        vb.npHour.displayedValues = null
        vb.npHour.minValue = hours[0].toInt()
        vb.npHour.maxValue = hours[hours.size-1].toInt()
        vb.npHour.displayedValues = hours
        vb.npHour.value = currentCal.get(Calendar.HOUR_OF_DAY)
        if (currentCal.get(Calendar.MINUTE) >= 59) {
            vb.npHour.value += 1
        }
        vb.npHour.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            vb.btnSend.text = getTimeResult()
        }

        vb.npMinute.displayedValues = null
        vb.npMinute.minValue = minutes[0].toInt()
        vb.npMinute.maxValue = minutes[minutes.size-1].toInt()
        vb.npMinute.displayedValues = minutes
        vb.npMinute.setOnValueChangedListener { _, _, _ ->
            checkTimeValue()
            vb.btnSend.text = getTimeResult()
        }
        vb.npMinute.value = currentCal.get(Calendar.MINUTE) + 1

        vb.tvCancelBtn.setOnClickListener {
            dismiss()
        }
        vb.btnSend.text = getTimeResult()
        vb.btnSend.setOnClickListener {
            listener?.onClick(0, getScheduledTime())
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun getTimeResult(): String {
        val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(vb.npDate.value.toLong())))
        return "Send on ${resultSdf.format(date)} at ${hours[vb.npHour.value]}:${minutes[ vb.npMinute.value]}"
    }

    private fun checkTimeValue() {
        val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(vb.npDate.value.toLong())))
        val shownTime = "${resultSdf.format(date)} ${hours[vb.npHour.value]}:${minutes[vb.npMinute.value]}"
        val shownDate = fullSdf.parse(shownTime)
        if (shownDate != null) {
            if (shownDate.time < System.currentTimeMillis()) {
                // time less than current, reset value
                currentCal = Calendar.getInstance()
                vb.npDate.value = 0
                vb.npHour.value = currentCal.get(Calendar.HOUR_OF_DAY)
                vb.npMinute.value = currentCal.get(Calendar.MINUTE) + 1
            }
            svm.pendingScheduledMessageTime = shownDate.time
        }
    }

    private fun getScheduledTime(): Long {
        val date = Date(currentDate.time.plus(TimeUnit.DAYS.toMillis(vb.npDate.value.toLong())))
        val shownTime = "${resultSdf.format(date)} ${hours[vb.npHour.value]}:${minutes[vb.npMinute.value]}"
        val shownDate = fullSdf.parse(shownTime)
        return shownDate?.time ?: 0L
    }
}