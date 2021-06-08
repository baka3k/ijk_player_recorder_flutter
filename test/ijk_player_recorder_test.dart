import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ijk_player_recorder/ijk_player_recorder.dart';

void main() {
  const MethodChannel channel = MethodChannel('ijk_player_recorder');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await IjkPlayerRecorder.platformVersion, '42');
  });
}
