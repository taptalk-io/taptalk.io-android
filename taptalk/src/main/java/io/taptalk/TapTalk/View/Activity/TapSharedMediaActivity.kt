package io.taptalk.TapTalk.View.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.taptalk.TapTalk.API.View.TAPDefaultDataView
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_SHARED_MEDIA
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPEncryptorManager
import io.taptalk.TapTalk.Model.ResponseModel.TapGetSharedContentResponse
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_DOCUMENT
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_LINK
import io.taptalk.TapTalk.Model.ResponseModel.TapSharedMediaItemModel.Companion.TYPE_MEDIA
import io.taptalk.TapTalk.Model.TAPErrorModel
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import io.taptalk.TapTalk.View.Fragment.TapSharedMediaFragment
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import io.taptalk.TapTalk.databinding.TapActivitySharedMediaBinding

class TapSharedMediaActivity : TAPBaseActivity() {

    private lateinit var vb : TapActivitySharedMediaBinding
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
            intent.putExtra(TAPDefaultConstant.Extras.INSTANCE_KEY, instanceKey)
            intent.putExtra(TAPDefaultConstant.Extras.ROOM, room)
            context.startActivityForResult(intent, OPEN_SHARED_MEDIA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TapActivitySharedMediaBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        vb.vpSharedMedia.adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager, vm.room)
        //viewpager cache fragments
        vb.vpSharedMedia.offscreenPageLimit = 3
        vb.tabLayout.setupWithViewPager(vb.vpSharedMedia)
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
        val message = data?.getParcelableExtra<TAPMessageModel>(TAPDefaultConstant.Extras.MESSAGE)
        val intent = Intent()
        intent.putExtra(TAPDefaultConstant.Extras.MESSAGE, message)
        setResult(RESULT_OK, intent)
        finish()
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
                        super.onSuccess(response)
                        if (response == null) {
                            return
                        }
                        if (!response.media.isNullOrEmpty() && vm.remoteMedias.isEmpty()) {
                            for (media in response.media) {
                                val data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(media?.data as String, media.localID))
                                val message = TAPMessageModel.Builder(
                                    "",
                                    vm.room,
                                    media.messageType,
                                    media.created,
                                    TAPUserModel(media.userID.orEmpty(), media.userFullname),
                                    "",
                                    data
                                )
                                vm.remoteMedias.add(message)
                            }
                        }
                        if (!response.files.isNullOrEmpty() && vm.remoteDocuments.isEmpty()) {
                            for (files in response.files) {
                                val data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(files?.data as String, files.localID))
                                val message = TAPMessageModel.Builder(
                                    "",
                                    vm.room,
                                    files.messageType,
                                    files.created,
                                    TAPUserModel(files.userID.orEmpty(), files.userFullname),
                                    "",
                                    data
                                )
                                vm.remoteDocuments.add(message)
                            }
                        }
                        if (!response.links.isNullOrEmpty() && vm.remoteLinks.isEmpty()) {
                            for (links in response.links) {
                                val data = TAPUtils.toHashMap(TAPEncryptorManager.getInstance().decrypt(links?.data as String, links.localID))
                                val message = TAPMessageModel.Builder(
                                    "",
                                    vm.room,
                                    links.messageType,
                                    links.created,
                                    TAPUserModel(links.userID.orEmpty(), links.userFullname),
                                    "",
                                    data
                                )
                                vm.remoteLinks.add(message)
                            }
                        }
                        vm.isLoading = false
                        vm.isRemoteContentFetched = true
                        loadSharedMedia(type)
                    }

                    override fun onError(error: TAPErrorModel?) {
                        super.onError(error)
                        vm.isLoading = false
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        vm.isLoading = false
                    }
                })
            }
            else {
                getMoreSharedMedias(type)
            }
        }
    }

    private fun loadSharedMedia(type: Int) {
        when (type) {
            TYPE_MEDIA -> (vb.vpSharedMedia.adapter?.instantiateItem(vb.vpSharedMedia, 0) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteMedias)
            TYPE_LINK -> (vb.vpSharedMedia.adapter?.instantiateItem(vb.vpSharedMedia, 1) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteLinks)
            TYPE_DOCUMENT -> (vb.vpSharedMedia.adapter?.instantiateItem(vb.vpSharedMedia, 2) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteDocuments)
        }
    }
}
