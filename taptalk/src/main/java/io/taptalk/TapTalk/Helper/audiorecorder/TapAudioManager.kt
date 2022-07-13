package io.taptalk.TapTalk.Helper.audiorecorder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Interface.TapAudioListener
import java.io.File
import java.io.IOException
import java.util.*


class TapAudioManager(val instanceKey: String, val listener: TapAudioListener) {

    companion object {
        @Volatile
        private var instance: TapAudioManager? = null

        fun getInstance(instanceKey: String, listener: TapAudioListener) =
            instance ?: synchronized(this) {
                instance ?: TapAudioManager(instanceKey, listener).also { instance = it }
            }
    }

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private val pathName = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${TapTalk.getClientAppName(instanceKey)}/Voice Notes"
    private val dir: File = File(pathName)
    private lateinit var outputFile: File


    private var recordingTime: Long = 0
    private var timer = Timer()
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var previousDuration = 0
    var isRecordingState = false
    private val recordingTimeString = MutableLiveData<String>()

    init {
        try{
            // create a File object for the parent directory
            val recorderDirectory = File(pathName)
            // have the object build the directory structure, if needed.
            recorderDirectory.mkdirs()
        }catch (e: IOException){
            e.printStackTrace()
        }
        mediaPlayer?.setOnCompletionListener {
            stopTimer()
            updateDisplay()
            resetTimer()
            it.release()
            mediaPlayer = null
            listener.onPlayComplete()
        }
        mediaPlayer?.setOnPreparedListener {
            listener.onPrepared()
        }
        mediaPlayer?.setOnSeekCompleteListener {
            listener.onSeekComplete()
        }
        if(dir.exists()){
            output = "$pathName/${System.currentTimeMillis()}.mp3"
            outputFile = File(output)
        }
    }

    @SuppressLint("RestrictedApi")
    fun startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()

            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(output)
        }

        try {
            println("Starting recording!")
            recordingTimeString.postValue("00:00")
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startTimer()
            isRecordingState = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("RestrictedApi")
    fun stopRecording(){
        isRecordingState = false
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
        }

        mediaRecorder = null
        stopTimer()
        updateDisplay()
        resetTimer()

        initRecorder()
    }

    fun deleteRecording(context: Context) {
        // Delete file from TapTalk folder
        recordingTimeString.postValue("00:00")
        MediaScannerConnection.scanFile(context, arrayOf(outputFile.absolutePath), null
        ) { _, uri ->
            Log.i("onScanCompleted", uri.path.orEmpty())
            if (uri.path.orEmpty().isNotEmpty()) {
                TapTalk.appContext.contentResolver.delete(uri, null, null)
            }
        }
    }

    private fun initRecorder() {
        mediaRecorder = MediaRecorder()

        if(dir.exists()){
            outputFile = File(output)
            output = "$pathName/${System.currentTimeMillis()}.mp3"
        }

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
    }

    private fun initPlayer() {

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            stopTimer()
            resetTimer()
            it.release()
            mediaPlayer = null
            listener.onPlayComplete()
        }
        mediaPlayer?.setOnPreparedListener {
            listener.onPrepared()
        }
        mediaPlayer?.setOnSeekCompleteListener {
            listener.onSeekComplete()
        }
    }

    private fun startTimer(){
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordingTime += 1
                updateDisplay()
            }
        }, 1000, 1000)
    }

    private fun stopTimer(){
        timer.cancel()
    }


    private fun resetTimer() {
        timer = Timer()
        recordingTime = 0
    }

    private fun updateDisplay(){
        val minutes = recordingTime / (60)
        val seconds = recordingTime % 60
        val str = String.format("%02d:%02d", minutes, seconds)
        recordingTimeString.postValue(str)
    }

    fun getRecordingTime() = recordingTimeString

    fun getRecordingSeconds() = recordingTime % 60

    fun getRecording() = outputFile

    fun isPlayingRecord() = mediaPlayer?.isPlaying
}