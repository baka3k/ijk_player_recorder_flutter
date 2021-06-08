import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:ijk_player_recorder/video_view_controller.dart';

typedef void VideoViewCreatedCallback(VideoViewController controller);

class VideoView extends StatelessWidget {
  static const StandardMessageCodec _decoder = StandardMessageCodec();
  VideoViewCreatedCallback? onVideoViewCreatedCallback;

  VideoView({
    Key? key,
    this.onVideoViewCreatedCallback
  }) :super(key: key);


  @override
  Widget build(BuildContext context) {
    final Map<String, String> args = {"someInit": "initData"};
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
          viewType: 'VideoViewPlatform',
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: args,
          creationParamsCodec: _decoder);
    }
    else {
      return UiKitView(
          viewType: 'VideoViewPlatform',
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: args,
          creationParamsCodec: _decoder);
    }
  }

  void _onPlatformViewCreated(int id) {
    if (onVideoViewCreatedCallback == null) {
      return;
    }
    onVideoViewCreatedCallback!(VideoViewController(id));
  }
}