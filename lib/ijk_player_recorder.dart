
import 'dart:async';

import 'package:flutter/services.dart';

class IjkPlayerRecorder {
  static const MethodChannel _channel =
      const MethodChannel('ijk_player_recorder');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
