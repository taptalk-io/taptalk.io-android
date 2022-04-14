package io.taptalk.TapTalk.Manager

import io.taptalk.TapTalk.Helper.audiorecorder.AudioPlayer
import io.taptalk.TapTalk.Helper.audiorecorder.AudioRecorder

class TapAudioManager(private val audioRecorder: AudioRecorder, private val audioPlayer: AudioPlayer) {

    fun startRecording() = audioRecorder.startRecording()

    fun stopRecording() = audioRecorder.stopRecording()

    fun pauseRecording() = audioRecorder.pauseRecording()

    fun resumeRecording() = audioRecorder.resumeRecording()

    fun getRecordingTime() = audioRecorder.getRecordingTime()

    fun getRecordings() = audioPlayer.getRecordings()
}