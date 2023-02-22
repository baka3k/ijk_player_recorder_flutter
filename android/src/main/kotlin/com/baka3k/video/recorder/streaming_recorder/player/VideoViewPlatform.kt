package com.baka3k.video.recorder.streaming_recorder.player

import android.content.Context
import android.util.Log
import android.view.View
import com.baka3k.stream.player.filter.FilterVideoView
import com.daasuu.gpuv.egl.filter.GlBilateralFilter
import com.daasuu.gpuv.egl.filter.GlBrightnessFilter
import com.daasuu.gpuv.egl.filter.GlBulgeDistortionFilter
import com.daasuu.gpuv.egl.filter.GlContrastFilter
import com.daasuu.gpuv.egl.filter.GlCrosshatchFilter
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlGammaFilter
import com.daasuu.gpuv.egl.filter.GlHazeFilter
import com.daasuu.gpuv.egl.filter.GlHighlightShadowFilter
import com.daasuu.gpuv.egl.filter.GlHueFilter
import com.daasuu.gpuv.egl.filter.GlInvertFilter
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

    private val rtspView = FilterVideoView(context)

    init {
        attachedToEngine()
    }

    override fun getView(): View {
        return rtspView
    }

    override fun dispose() {
        rtspView.stopRecord()
//        rtspView.stopPlayback()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val callMethod = call.method
        val arguments = call.arguments
        Log.d("VideoViewPlatform", " callMethod:$callMethod - arguments:$arguments")
        when (callMethod) {
            "setVideoPath" -> {
                val videoPath = arguments as String
                // NOTE: You must call serialized task - Do not call Prepare Async
                rtspView.prepareDataSourceOnMainThread(videoPath)
                rtspView.start()
            }

            "start" -> rtspView.start()
            "stopPlayback" -> rtspView.stopPlayer()
            "stopRecord" -> rtspView.stopRecord()
            "startRecord" -> {
                val outPutPath = arguments as String
                rtspView.startRecord(outPutPath)
            }

            "capturePhoto" -> {
                val outPutPath = arguments as String
                rtspView.capturePhotoFrame(outPutPath)
            }

            "release" -> {
                rtspView.stopPlayer()
            }

            "setFilter" -> {
                when (arguments as Int) {
                    1 -> {
                        val hazeFilter = GlHazeFilter()
                        hazeFilter.slope = -0.8f
                        rtspView.setFilter(hazeFilter)
                    }
                    2 -> {
                        rtspView.setFilter(GlBilateralFilter())
                    }
                    3 -> {
                        val filter = GlBrightnessFilter()
                        filter.setBrightness(0.5f)
                        rtspView.setFilter(filter)
                    }
                    4 -> {
                        val filter = GlBulgeDistortionFilter()
                        rtspView.setFilter(filter)
                    }
                    5 -> {
                        val filter = GlCrosshatchFilter()
                        rtspView.setFilter(filter)
                    }
                    6 -> {
                        val filter = GlContrastFilter()
                        filter.setContrast(2.5f)
                        rtspView.setFilter(filter)
                    }
                    7 -> {
                        val filter = GlGammaFilter()
                        filter.setGamma(2f)
                        rtspView.setFilter(filter)
                    }
                    8 -> {
                        val filter = GlHueFilter()
                        rtspView.setFilter(filter)
                    }
                    9 -> {
                        val filter = GlInvertFilter()
                        rtspView.setFilter(filter)
                    }
                    10 -> {
                        val filter = GlHighlightShadowFilter()
                        rtspView.setFilter(filter)
                    }
                    else -> {
                        rtspView.setFilter(GlFilter())
                    }
                }
            }

            else -> {
                result.notImplemented()
                return
            }
        }
        result.success(1)
    }

    private fun detachedFromEngine() {
        methodChannel.setMethodCallHandler(null)
    }

    private fun attachedToEngine() {
        methodChannel.setMethodCallHandler(this)
    }
}