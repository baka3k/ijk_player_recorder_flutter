import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:streaming_recorder/logger.dart';

class VideoViewController {
  late final MethodChannel _channel;

  VideoViewController(int viewId) {
    _channel = MethodChannel("VideoViewPlatform/$viewId");
  }

  Future _handleMethod(MethodCall call) async {
    final String method = call.method;
    final dynamic arguments = call.arguments;
    log("Flutter receive native $method / $arguments");
  }

  setVideoPath(String url) async {
    try {
      final int result = await _channel.invokeMethod('setVideoPath', url);
      log("#setVideoPath: result: $result - url: $url");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  start() async {
    try {
      final int result = await _channel.invokeMethod('start');
      log("#start(): $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  stopPlayback() async {
    try {
      final int result = await _channel.invokeMethod('stopPlayback');
      log("#stopPlayback() result: $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  stopRecord() async {
    try {
      final int result = await _channel.invokeMethod('stopRecord');
      log("#stopRecord() $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  startRecord(String output) async {
    try {
      final int result = await _channel.invokeMethod('startRecord', output);
      log("#startRecord() result: $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  capturePhoto(String output) async {
    try {
      final int result = await _channel.invokeMethod('capturePhoto', output);
      log("#capturePhoto(): result $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  release() async {
    try {
      final int result = await _channel.invokeMethod('release');
      log("#release() result $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }

  void stopPreview() {
    stopPlayback();
    release();
  }

  setFilter(int i) async {
    try {
      final int result = await _channel.invokeMethod('setFilter', i);
      log("#setFilter() result $result ");
    } on PlatformException catch (e) {
      log("Error from native: $e.message");
    }
  }
}
