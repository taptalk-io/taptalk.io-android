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
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.PagerAdapter.TapSharedMediaPagerAdapter
import io.taptalk.TapTalk.View.Fragment.TapSharedMediaFragment
import io.taptalk.TapTalk.ViewModel.TapSharedMediaViewModel
import kotlinx.android.synthetic.main.tap_activity_shared_media.*

class TapSharedMediaActivity : TAPBaseActivity() {

    private lateinit var vm : TapSharedMediaViewModel

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
        setContentView(R.layout.tap_activity_shared_media)
        vm = ViewModelProvider(this, TapSharedMediaViewModel.TapSharedMediaViewModelFactory(application))[TapSharedMediaViewModel::class.java]
        vm.room = intent.getParcelableExtra(TAPDefaultConstant.Extras.ROOM)
        vp_shared_media.adapter = TapSharedMediaPagerAdapter(this, instanceKey, supportFragmentManager, vm.room)
        //viewpager cache fragments
        vp_shared_media.offscreenPageLimit = 3
        tab_layout.setupWithViewPager(vp_shared_media)
        iv_button_back.setOnClickListener { onBackPressed() }
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
        } else {
            vm.isLoading = true
            if (vm.oldestCreatedTime != null) {
                // LOAD DATA FROM API
                TAPDataManager.getInstance(instanceKey).getSharedMedia(vm.room?.roomID, 0L, vm.oldestCreatedTime, object : TAPDefaultDataView<TapGetSharedContentResponse>() {
                    override fun onSuccess(response: TapGetSharedContentResponse?) {
                        super.onSuccess(response)
                        if (response == null) {
                            return
                        }
                        if (response.media != null && response.media.isNotEmpty() && vm.remoteMedias.isEmpty()) {
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
                        if (response.files != null && response.files.isNotEmpty() && vm.remoteDocuments.isEmpty()) {
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
                        if (response.links != null && response.links.isNotEmpty() && vm.remoteLinks.isEmpty()) {
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
            } else {
                getMoreSharedMedias(type)
            }
        }
    }

    private fun loadSharedMedia(type: Int) {
        when(type) {
            TYPE_MEDIA -> (vp_shared_media.adapter?.instantiateItem(vp_shared_media,
                0
            ) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteMedias)
            TYPE_LINK -> (vp_shared_media.adapter?.instantiateItem(vp_shared_media,
                1
            ) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteLinks)
            TYPE_DOCUMENT -> (vp_shared_media.adapter?.instantiateItem(vp_shared_media,
                2
            ) as TapSharedMediaFragment).addRemoteSharedMedias(vm.remoteDocuments)
        }
    }
}