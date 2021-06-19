package com.hi.ijk_player_recorder.player

import android.content.Context
import android.util.Log
import android.view.View
import com.hi.sample.videoplayer.player.view.RtspViewer
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class VideoViewPlatform internal constructor(
    context: Context,
    messager: BinaryMessenger,
    viewId: Int, args: Any?
) : PlatformView, MethodChannel.MethodCallHandler {
    private val methodChannel = MethodChannel(messager, "VideoViewPlatform/$viewId")
    private val rtspView = RtspViewer(context)

    init {
        Log.d("VideoViewPlatform", "init args:$args")
        methodChannel.setMethodCallHandler(this)
    }

    override fun getView(): View {
        return rtspView
    }

    override fun dispose() {
        rtspView.stopRecord()
        rtspView.stopPlayback()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val callMethod = call.method
        val arguments = call.arguments
        Log.d("VideoViewPlatform", " callMethod:$callMethod - arguments:$arguments")
        when (callMethod) {
            "setVideoPath" -> {
                val videoPath = arguments as String
                rtspView.setVideoPath(videoPath)
            }
            "start" -> rtspView.start()
            "stopPlayback" -> rtspView.stopPlayback()
            "stopRecord" -> rtspView.stopRecord()
            "startRecord" -> {
                val outPutPath = arguments as String
                rtspView.startRecord(outPutPath)
            }
            else -> {
                result.notImplemented()
                return
            }
        }
        result.success(1)
    }
}