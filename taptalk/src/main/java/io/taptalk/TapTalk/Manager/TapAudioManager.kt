package io.taptalk.TapTalk.Manager

import android.content.Context
import io.taptalk.TapTalk.Helper.audiorecorder.AudioRecorder

class TapAudioManager(private val audioRecorder: AudioRecorder) {

    fun startRecording() = audioRecorder.startRecording()

    fun stopRecording() = audioRecorder.stopRecording()

    fun pauseRecording() = audioRecorder.pauseRecording()

    fun resumeRecording() = audioRecorder.resumeRecording()

    fun getRecordingTime() = audioRecorder.getRecordingTime()

    fun getRecording() = audioRecorder.getRecording()

    fun deleteRecording(context: Context) = audioRecorder.deleteRecording(context)
}