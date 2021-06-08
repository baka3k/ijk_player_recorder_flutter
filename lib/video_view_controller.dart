import 'package:flutter/services.dart';

class VideoViewController {
  late final MethodChannel _channel;

  VideoViewController(int viewId) {
    _channel = new MethodChannel("VideoViewPlatform/$viewId");
  }

  Future _handleMethod(MethodCall call) async {
    final String method = call.method;
    final dynamic arguments = call.arguments;
    print("Flutter receive native " + method + " / " + arguments);
  }

  Future<void> setVideoPath(String url) async {
    try {
      final int result = await _channel.invokeMethod('setVideoPath', url);
      print("setVideoPath $result " + url);
    } on PlatformException catch (e) {
      print("Error from native: $e.message");
    }
  }

  Future<void> start() async {
    try {
      final int result = await _channel.invokeMethod('start');
    } on PlatformException catch (e) {
      print("Error from native: $e.message");
    }
  }

  Future<void> stopPlayback() async {
    try {
      final int result = await _channel.invokeMethod('stopPlayback');
    } on PlatformException catch (e) {
      print("Error from native: $e.message");
    }
  }

  Future<void> stopRecord() async {
    try {
      final int result = await _channel.invokeMethod('stopRecord');
    } on PlatformException catch (e) {
      print("Error from native: $e.message");
    }
  }

  Future<void> startRecord(String output) async {
    try {
      final int result = await _channel.invokeMethod('startRecord', output);
    } on PlatformException catch (e) {
      print("Error from native: $e.message");
    }
  }
}
