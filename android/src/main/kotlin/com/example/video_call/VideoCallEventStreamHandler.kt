package com.example.video_call

import android.util.Log
import com.example.video_call.VideoCallPlugin.Companion.video_call_TAG
import io.flutter.plugin.common.EventChannel
import java.io.Serializable

/**
 * StreamHandler to listen to conference events and broadcast it back to Flutter
 */
class VideoCallPluginEventStreamHandler private constructor(): EventChannel.StreamHandler, Serializable {
    companion object {
        val instance = VideoCallPluginEventStreamHandler()
    }

    private var eventSink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onListen")
        this.eventSink = eventSink
    }

    override fun onCancel(arguments: Any?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onCancel")
        eventSink = null
    }

    fun onConferenceWillJoin(data: MutableMap<String, Any>?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onConferenceWillJoin")
        data?.put("event", "onConferenceWillJoin")
        eventSink?.success(data)
    }

    fun onConferenceJoined(data: MutableMap<String, Any>?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onConferenceJoined")
        data?.put("event", "onConferenceJoined")
        eventSink?.success(data)
    }

    fun onConferenceTerminated(data: MutableMap<String, Any>?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onConferenceTerminated")
        data?.put("event", "onConferenceTerminated")
        eventSink?.success(data)
    }

    fun onParticipantLeft(data: MutableMap<String, Any>?) {
        Log.d(video_call_TAG, "VideoCallPluginEventStreamHandler.onParticipantLeft")
        data?.put("event", "onParticipantLeft")
        eventSink?.success(data)
    }

    fun onPictureInPictureWillEnter() {
        Log.d(video_call_TAG, "VideoCallEventStreamHandler.onPictureInPictureWillEnter")
        var data : HashMap<String, String>
                = HashMap<String, String> ()
        data?.put("event", "onPictureInPictureWillEnter")
        eventSink?.success(data)
    }

    fun onPictureInPictureTerminated() {
        Log.d(video_call_TAG, "VideoCallEventStreamHandler.onPictureInPictureTerminated")
        var data : HashMap<String, String>
                = HashMap<String, String> ()
        data?.put("event", "onPictureInPictureTerminated")
        eventSink?.success(data)
    }

    fun onWhiteboardClicked(){
        Log.d(video_call_TAG, "VideoCallEventStreamHandler.onWhiteboardClicked")
        var data : HashMap<String, String>
                = HashMap<String, String> ()
        data?.put("event", "onWhiteboardClicked")
        eventSink?.success(data)
    }

    

}