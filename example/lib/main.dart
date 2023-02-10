import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:ijk_player_recorder/video_view.dart';
import 'package:ijk_player_recorder/video_view_controller.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:math';

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
      // "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
      "rtsp://wowzaec2demo.streamlock.net/vod/mp4";
  String videoURL =
      // "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
      "rtsp://wowzaec2demo.streamlock.net/vod/mp4";
  String outVideo = "";
  VideoViewController? _videoViewController;
  final textEditingController = TextEditingController();

  @override
  void initState() {
    super.initState();
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
              Text(
                "outputvideo:" + outVideo,
                overflow: TextOverflow.ellipsis,
              ),
              Row(
                children: [
                  TextButton(onPressed: setURL, child: Text("SetURL")),
                  TextButton(
                      onPressed: startPreview, child: Text("StartPreview")),
                  TextButton(
                      onPressed: stopPreview, child: Text("StopPreview")),
                ],
              ),
              Row(
                children: [
                  TextButton(
                      onPressed: startRecord, child: Text("StartRecord")),
                  TextButton(onPressed: stopRecord, child: Text("StopRecord")),
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
  String generateRandomString(int len) {
    var r = Random();
    return String.fromCharCodes(List.generate(len, (index) => r.nextInt(33) + 89));
  }
  Future<void> startRecord() async {
    Directory appDocDir = await getApplicationDocumentsDirectory();
    setState(() {
      var rng = generateRandomString(10);
      outVideo = appDocDir.path + "/" + rng + ".mp4";
      print(outVideo);
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
