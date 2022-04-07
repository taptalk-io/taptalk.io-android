package io.taptalk.TapTalk.Helper.audiorecorder

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.File

class AudioPlayer {
    companion object {
        @Volatile
        private var instance: AudioPlayer? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AudioPlayer().also { instance = it }
            }


        fun playRecording(context: Context, title: String){
            val path = Uri.parse(Environment.getExternalStorageDirectory().absolutePath+"/voicenotes/$title")


            val manager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if(manager.isMusicActive) {
                Toast.makeText(context, "Another recording is just playing! Wait until it's finished!", Toast.LENGTH_SHORT).show()
            }else{
                val mediaPlayer: MediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(context, path)
                    prepare()
                    start()
                }
            }

        }
    }

    private val recorderDirectory = File(Environment.getExternalStorageDirectory().absolutePath+"/voicenotes/")
    private var file : ArrayList<String>? = null

    init {
        file = ArrayList()
        getRecording()
    }

    private fun getRecording(){
        val files: Array<out File>? = recorderDirectory.listFiles()
        for(i in files!!){
            println(i.name)
            file?.add(i.name)
        }
    }

    fun getRecordings() = file

}