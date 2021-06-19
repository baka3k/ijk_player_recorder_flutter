import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:ijk_player_recorder/ijk_player_recorder.dart';
import 'package:ijk_player_recorder/video_view.dart';
import 'package:ijk_player_recorder/video_view_controller.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String defaultValue =
      "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
  String videoURL =
      "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
  String outVideo = "";
  VideoViewController? _videoViewController;
  final textEditingController = TextEditingController();

  @override
  void initState() {
    super.initState();
    initPlatformState();
    textEditingController.text = videoURL;
    textEditingController.addListener(_onTextChanged);
  }

  @override
  void dispose() {
    cleanUp();
    super.dispose();
  }

  void cleanUp() {
    stopPreview();
    textEditingController.dispose();
  }

  void _onTextChanged() {
    print('Second text field: ${textEditingController.text}');
    String value = textEditingController.text;
    if (value.isEmpty) {
      videoURL = defaultValue;
    }
    setState(() {
      videoURL = value;
    });
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
              Row(
                children: [
                  Expanded(
                      child: TextField(
                    controller: textEditingController,
                  )),
                ],
              ),
              Row(
                children: [
                  Text("outputvideo:" + outVideo)
                ],
              ),
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
    _videoViewController?.start();
  }

  void stopPreview() {
    _videoViewController?.stopPlayback();
  }

  Future<void> startRecord() async {
    Directory appDocDir = await getApplicationDocumentsDirectory();
    setState(() {
      outVideo = appDocDir.path + "/a.mp4";
    });
    _videoViewController?.startRecord(outVideo);
  }

  void stopRecord() {
    _videoViewController?.stopRecord();
  }

  void setURL() {
    _videoViewController?.setVideoPath(videoURL);
  }
}
