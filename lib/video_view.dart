import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:streaming_recorder/video_view_controller.dart';

typedef VideoViewCreatedCallback = void Function(
    VideoViewController controller);

class VideoView extends StatelessWidget {
  static const StandardMessageCodec _decoder = StandardMessageCodec();
  VideoViewCreatedCallback? onVideoViewCreatedCallback;
  static const String viewType = 'VideoViewPlatform';

  VideoView({Key? key, this.onVideoViewCreatedCallback}) : super(key: key);

  Map<String, dynamic> creationParams = <String, dynamic>{};

  @override
  Widget build(BuildContext context) {
    // return buildVirtualPlatformView();
    return buildHybridPlatformView();
  }

  Widget buildVirtualPlatformView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
          viewType: viewType,
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: creationParams,
          creationParamsCodec: _decoder);
    } else {
      return UiKitView(
          viewType: viewType,
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: creationParams,
          creationParamsCodec: _decoder);
    }
  }

  Widget buildHybridPlatformView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return PlatformViewLink(
        viewType: viewType,
        surfaceFactory:
            (BuildContext context, PlatformViewController controller) {
          return AndroidViewSurface(
            controller: controller as AndroidViewController,
            gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
            hitTestBehavior: PlatformViewHitTestBehavior.opaque,
          );
        },
        onCreatePlatformView: (PlatformViewCreationParams params) {
          return PlatformViewsService.initSurfaceAndroidView(
            id: params.id,
            viewType: viewType,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
            onFocus: () {
              params.onFocusChanged(true);
            },
          )
            ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
            ..addOnPlatformViewCreatedListener(_onPlatformViewCreated)
            ..create();
        },
      );
    } else {
      return UiKitView(
          viewType: viewType,
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: creationParams,
          creationParamsCodec: _decoder);
    }
  }

  void _onPlatformViewCreated(int id) {
    log("_onPlatformViewCreated id $onVideoViewCreatedCallback");
    if (onVideoViewCreatedCallback != null) {
      onVideoViewCreatedCallback!(VideoViewController(id));
    }
  }
}
