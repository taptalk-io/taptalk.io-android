package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TapMessageRecipientModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TapMessageInfoAdapter
import io.taptalk.TapTalk.ViewModel.TapMessageInfoViewModel
import kotlinx.android.synthetic.main.tap_activity_message_info.*

class TapMessageInfoActivity : TAPBaseActivity() {

    private val vm : TapMessageInfoViewModel by lazy {
        ViewModelProvider(this)[TapMessageInfoViewModel::class.java]
    }

    companion object {
        fun start(
            context: Context,
            instanceKey: String?,
            message: TAPMessageModel
        ) {
            if (context is Activity) {
                val intent = Intent(context, TapMessageInfoActivity::class.java)
                intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
                intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
                context.startActivityForResult(intent, TAPDefaultConstant.RequestCode.OPEN_REPORT_USER)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tap_activity_message_info)
        if (intent.getParcelableExtra<TAPMessageModel>(TAPDefaultConstant.Extras.MESSAGE) != null) {
            vm.message = intent.getParcelableExtra(TAPDefaultConstant.Extras.MESSAGE)
        }
        // TODO: get list from API MU
        val messageInfoList = ArrayList<TapMessageRecipientModel>()
        val resultList = ArrayList<TapMessageRecipientModel>()
        val adapter = TapMessageInfoAdapter(resultList)
        rv_message_info.adapter = adapter
        rv_message_info.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_message_info.setHasFixedSize(true)

        for (item in messageInfoList) {
            if (item.readTime != null && item.readTime > 0) {
                vm.readList.add(item)
            } else if (item.deliveredTime != null && item.deliveredTime > 0) {
                vm.deliveredList.add(item)
            }
        }
        if (vm.readList.isNotEmpty() && !TapUI.getInstance(instanceKey).isReadStatusHidden) {
            resultList.add(TapMessageRecipientModel(vm.readList.size.toLong(), null))
            resultList.addAll(vm.readList)
            if (vm.deliveredList.isNotEmpty()) {
                resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
                resultList.addAll(vm.deliveredList)
            }
        } else {
            resultList.add(TapMessageRecipientModel(null, vm.deliveredList.size.toLong()))
            if (vm.deliveredList.isNotEmpty()) {
                resultList.addAll(vm.deliveredList)
            }
        }
        adapter.items = resultList

    }
}