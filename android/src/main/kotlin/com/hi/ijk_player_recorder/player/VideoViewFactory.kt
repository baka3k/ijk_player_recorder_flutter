package com.hi.ijk_player_recorder.player

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class VideoViewFactory(private val messenger: BinaryMessenger) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(
        context: Context,
        id: Int,
        args: Any?
    ): PlatformView {
        return VideoViewPlatform(
            context,
            messenger,
            id,
            args
        )
    }
}