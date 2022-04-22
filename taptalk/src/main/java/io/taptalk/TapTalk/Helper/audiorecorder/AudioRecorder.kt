package io.taptalk.TapTalk.Helper.audiorecorder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import io.taptalk.TapTalk.Helper.TapTalk
import java.io.File
import java.io.IOException
import java.util.*

class AudioRecorder(val instanceKey: String) {

    companion object {
        @Volatile
        private var instance: AudioRecorder? = null

        fun getInstance(instanceKey: String) =
            instance ?: synchronized(this) {
                instance ?: AudioRecorder(instanceKey).also { instance = it }
            }
    }

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private val pathName = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${TapTalk.getClientAppName(instanceKey)}/Voice Notes"
    private val dir: File = File(pathName)
    private lateinit var outputFile: File


    private var recordingTime: Long = 0
    private var timer = Timer()
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
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            startTimer()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("RestrictedApi")
    fun stopRecording(){
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder?.release()
        mediaRecorder = null
        stopTimer()
        resetTimer()

        initRecorder()
    }


    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun pauseRecording(){
        stopTimer()
        mediaRecorder?.pause()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    fun resumeRecording(){
        timer = Timer()
        startTimer()
        mediaRecorder?.resume()
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
        timer.cancel()
        recordingTime = 0
        recordingTimeString.postValue("00:00")
    }

    private fun updateDisplay(){
        val minutes = recordingTime / (60)
        val seconds = recordingTime % 60
        val str = String.format("%d:%02d", minutes, seconds)
        recordingTimeString.postValue(str)
    }

    fun getRecordingTime() = recordingTimeString

    fun getRecording() = outputFile
}