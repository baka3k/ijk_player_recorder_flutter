import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:ijk_player_recorder/ijk_player_recorder.dart';
import 'package:ijk_player_recorder/video_view.dart';
import 'package:ijk_player_recorder/video_view_controller.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  VideoViewController? _videoViewController;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await IjkPlayerRecorder.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.max,
            children: [
              Text("rtsp://wowzaec2demo.streamlock.net/vod/mp4"),
              Row(
                children: [
                  FlatButton(onPressed: setURL, child: Text("SetURL")),
                  FlatButton(
                      onPressed: startPreview, child: Text("StartPreview")),
                  FlatButton(
                      onPressed: stopPreview, child: Text("StopPreview")),
                ],
              ),
              Row(
                children: [
                  FlatButton(
                      onPressed: startRecord, child: Text("StartRecord")),
                  FlatButton(onPressed: stopRecord, child: Text("StopRecord")),
                ],
              ),
              Expanded(child: VideoView(
                onVideoViewCreatedCallback: (VideoViewController controller) {
                  setState(() {
                    print("view inited");
                    _videoViewController = controller;
                  });
                },
              ))
            ],
          ),
        ),
      ),
    );
  }

  void startPreview() {
    // rtsp://wowzaec2demo.streamlock.net/vod/mp4

    _videoViewController?.start();
  }

  void stopPreview() {
    _videoViewController?.stopPlayback();
  }

  void startRecord() {
    _videoViewController?.startRecord("sdcard/Download/a.mp4");
  }

  void stopRecord() {
    _videoViewController?.stopRecord();
  }

  void setURL() {
    print("setURL $_videoViewController");
    _videoViewController
        ?.setVideoPath("rtsp://wowzaec2demo.streamlock.net/vod/mp4");
  }
}
