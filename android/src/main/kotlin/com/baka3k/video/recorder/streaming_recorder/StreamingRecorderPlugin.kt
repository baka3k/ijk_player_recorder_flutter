package com.baka3k.video.recorder.streaming_recorder

import androidx.annotation.NonNull
import com.baka3k.video.recorder.streaming_recorder.player.VideoViewFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin

/** StreamingRecorderPlugin */
class StreamingRecorderPlugin : FlutterPlugin {
    //
    private var _videoViewFactory: VideoViewFactory? = null
    private val videoViewFactory: VideoViewFactory get() = _videoViewFactory!!

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        initPlatformView(flutterPluginBinding)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        _videoViewFactory = null
    }

    private fun initPlatformView(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        _videoViewFactory = VideoViewFactory(flutterPluginBinding.binaryMessenger)
        flutterPluginBinding.platformViewRegistry.registerViewFactory(
            "VideoViewPlatform", videoViewFactory
        )
    }
}
