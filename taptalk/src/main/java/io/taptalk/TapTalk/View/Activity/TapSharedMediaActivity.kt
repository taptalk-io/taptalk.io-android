package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.URLS
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_SHARED_MEDIA
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPEncryptorManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.ResponseModel.TapGetSharedContentResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import io.taptalk.TapTalk.View.Fragment.TapSharedMediaFragment
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import io.taptalk.TapTalk.databinding.TapActivitySharedMediaBinding

class TapSharedMediaActivity : TAPBaseActivity() {

    private lateinit var vb : TapActivitySharedMediaBinding
    private lateinit var adapter : TapSharedMediaPagerAdapter
    private lateinit var fragments : ArrayList<TapSharedMediaFragment>
    private val vm: TapSharedMediaViewModel by lazy {
        ViewModelProvider(this)[TapSharedMediaViewModel::class.java]
    }

    companion object {
        fun start(
            context: Activity,
            instanceKey: String?,
            room: TAPRoomModel
        ) {
            val intent = Intent(context, TapSharedMediaActivity::class.java)
            intent.putExtra(INSTANCE_KEY, instanceKey)
            intent.putExtra(ROOM, room)
            context.startActivityForResult(intent, OPEN_SHARED_MEDIA)
            context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivitySharedMediaBinding.inflate(layoutInflater)
        setContentView(vb.root)
        if (finishIfNotLoggedIn()) {
            return
        }

        vm.room = intent.getParcelableExtra(ROOM)

        fragments = ArrayList()
        if (TapUI.getInstance(instanceKey).isSharedMediaMediasTabVisible) {
            fragments.add(TapSharedMediaFragment.newInstance(instanceKey, TYPE_MEDIA, vm.room))
        }
        if (TapUI.getInstance(instanceKey).isSharedMediaLinksTabVisible) {
            fragments.add(TapSharedMediaFragment.newInstance(instanceKey, TYPE_LINK, vm.room))
        }
        if (TapUI.getInstance(instanceKey).isSharedMediaDocumentsTabVisible) {
            fragments.add(TapSharedMediaFragment.newInstance(instanceKey, TYPE_DOCUMENT, vm.room))
        }

        adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager, fragments, vm.room)
        vb.vpSharedMedia.adapter = adapter

        //viewpager cache fragments
        vb.vpSharedMedia.offscreenPageLimit = fragments.size
        if (fragments.size > 1) {
            vb.tabLayout.setupWithViewPager(vb.vpSharedMedia)
        }
        else {
            vb.tabLayout.visibility = View.GONE
        }
        vb.ivButtonBack.setOnClickListener { onBackPressed() }
        TAPDataManager.getInstance(instanceKey).getOldestCreatedTimeFromRoom(vm.room?.roomID, object : TAPDatabaseListener<Long>() {
            override fun onSelectFinished(entity: Long?) {
                super.onSelectFinished(entity)
                vm.oldestCreatedTime = entity
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val message = data?.getParcelableExtra<TAPMessageModel>(MESSAGE)
        val intent = Intent()
        intent.putExtra(MESSAGE, message)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMoreSharedMedias(type : Int) {
        if (vm.isRemoteContentFetched) {
            // LOAD DATA FROM VM
            loadSharedMedia(type)
        }
        else {
            vm.isLoading = true
            if (vm.oldestCreatedTime != null) {
                // LOAD DATA FROM API
                TAPDataManager.getInstance(instanceKey).getSharedMedia(vm.room?.roomID, 0L, vm.oldestCreatedTime, object : TAPDefaultDataView<TapGetSharedContentResponse>() {
                    override fun onSuccess(response: TapGetSharedContentResponse?) {
                        if (response == null) {
                            return
                        }
                        if (!response.media.isNullOrEmpty() && vm.remoteMedias.isEmpty()) {
                            for (media in response.media) {
                                val message = generateMediaMessage(media)
                                message?.let {
                                    vm.remoteMedias.add(it)
                                }
                            }
                        }
                        if (!response.files.isNullOrEmpty() && vm.remoteDocuments.isEmpty()) {
                            for (file in response.files) {
                                val message = generateMediaMessage(file)
                                message?.let {
                                    vm.remoteDocuments.add(it)
                                }
                            }
                        }
                        if (!response.links.isNullOrEmpty() && vm.remoteLinks.isEmpty()) {
                            for (link in response.links) {
                                val message = generateMediaMessage(link)
                                message?.let {
                                    vm.remoteLinks.add(it)
                                }
                            }
                        }
                        vm.isLoading = false
                        vm.isRemoteContentFetched = true
                        loadSharedMedia(type)
                    }

                    override fun onError(error: TAPErrorModel?) {
                        onError(error?.message)
                    }

                    override fun onError(errorMessage: String?) {
                        vm.isLoading = false
                        hideLoading(type)
                    }
                })
            }
            else {
                getMoreSharedMedias(type)
            }
        }
    }

    private fun generateMediaMessage(media: TapSharedMediaItemModel?): TAPMessageModel? {
        if (media == null) {
            return null
        }
        val data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(media.data as String, media.localID))
        if ((data?.get(URL) as String?).isNullOrEmpty() ||
            (data?.get(FILE_ID) as String?).isNullOrEmpty() ||
            (data?.get(URLS) as ArrayList<String>?).isNullOrEmpty()
        ) {
            return null
        }
        return TAPMessageModel.Builder(
            "",
            vm.room,
            media.messageType,
            media.created,
            TAPUserModel(media.userID.orEmpty(), media.userFullname),
            "",
            data
        )
    }

    private fun loadSharedMedia(type: Int) {
        when (type) {
            TYPE_MEDIA -> getFragmentForType(type)?.addRemoteSharedMedias(vm.remoteMedias)
            TYPE_LINK -> getFragmentForType(type)?.addRemoteSharedMedias(vm.remoteLinks)
            TYPE_DOCUMENT -> getFragmentForType(type)?.addRemoteSharedMedias(vm.remoteDocuments)
        }
    }

    private fun hideLoading(type: Int) {
        getFragmentForType(type)?.hideLoading()
    }

    private fun getFragmentForType(type: Int): TapSharedMediaFragment? {
        when (type) {
            TYPE_MEDIA -> {
                if (TapUI.getInstance(instanceKey).isSharedMediaMediasTabVisible && fragments.size > 0) {
                    return fragments[0]
                }
                else {
                    return null
                }
            }
            TYPE_LINK -> {
                if (!TapUI.getInstance(instanceKey).isSharedMediaLinksTabVisible) {
                    return null
                }
                val index = if (TapUI.getInstance(instanceKey).isSharedMediaMediasTabVisible) {
                    1
                }
                else {
                    0
                }
                if (fragments.size > index) {
                    return fragments[index]
                }
                else {
                    return null
                }
            }
            else -> {
                if (!TapUI.getInstance(instanceKey).isSharedMediaDocumentsTabVisible) {
                    return null
                }
                val index = if (TapUI.getInstance(instanceKey).isSharedMediaMediasTabVisible && TapUI.getInstance(instanceKey).isSharedMediaLinksTabVisible) {
                    2
                }
                else if (TapUI.getInstance(instanceKey).isSharedMediaMediasTabVisible || TapUI.getInstance(instanceKey).isSharedMediaLinksTabVisible) {
                    1
                }
                else {
                    0
                }
                if (fragments.size > index) {
                    return fragments[index]
                }
                else {
                    return null
                }
            }
        }
    }
}
