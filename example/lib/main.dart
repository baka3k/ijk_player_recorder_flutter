import 'dart:core';
import 'dart:core';

import 'package:flutter/material.dart';
import 'package:streaming_recorder/video_libs.dart';

const Map<String, String> outPutPhotoCapture = {
  "android": "/storage/emulated/0/Download",
  "ios": "/storage/emulated/0/Download",
};
const Map<String, String> outPutVideooRecoder = {
  "android": "/storage/emulated/0/Download",
  "ios": "/storage/emulated/0/Download",
};
const String videoURL = "rtsp://wowzaec2demo.streamlock.net/vod/mp4";

String outVideo = "${outPutVideooRecoder["android"]}/abc.mp4";
String outPhoto = "${outPutPhotoCapture["android"]}/abc.jpg";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  VideoViewController? _videoViewController;
  String _recordButton = "StartRecord";
  String _playButton = "Play";

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    _videoViewController?.stopPlayback();
    _videoViewController?.release();
    super.dispose();
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
            children: [
              const Text(
                "url:$videoURL",
                overflow: TextOverflow.ellipsis,
              ),
              Row(
                children: [
                  IntrinsicWidth(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        buildTextButton(_playButton, () {
                          _handlePlayButton();
                        }),
                        buildTextButton(_recordButton, () {
                          _handleRecordButton();
                        }),
                        buildTextButton("Capture Photo Frame", () {
                          capturePhoto();
                        }),
                      ],
                    ),
                  ),
                  IntrinsicWidth(
                    child: Column(
                      children: [
                        buildTextButton("Effect 2", () {
                          _handleClickedButtonEffect(2);
                        }),
                        buildTextButton("Effect 3", () {
                          _handleClickedButtonEffect(8);
                        }),
                        buildTextButton("Effect 4", () {
                          _handleClickedButtonEffect(4);
                        }),
                      ],
                    ),
                  ),
                  IntrinsicWidth(
                    child: Column(
                      children: [
                        buildTextButton("Effect 5", () {
                          _handleClickedButtonEffect(5);
                        }),
                        buildTextButton("Effect 6", () {
                          _handleClickedButtonEffect(6);
                        }),
                        buildTextButton("Effect 7", () {
                          _handleClickedButtonEffect(7);
                        }),
                      ],
                    ),
                  )
                ],
              ),
              buildVideoView()
            ],
          ),
        ),
      ),
    );
  }

  void _handleClickedButtonEffect(int i) {
    _videoViewController?.setFilter(i);
  }

  void _handleRecordButton() {
    if (_allowClick()) {
      if (_recordButton == "StartRecord") {
        setState(() {
          _recordButton = "Recording";
        });
        startRecord();
      } else {
        setState(() {
          _recordButton = "StartRecord";
        });
        stopRecord();
      }
    }
  }

  void _handlePlayButton() {
    if (_allowClick()) {
      if (_playButton == "Play") {
        setState(() {
          _playButton = "Pause";
        });
        playVideo();
      } else {
        setState(() {
          _playButton = "Play";
        });
        pauseVideo();
      }
    }
  }

  DateTime loginClickTime = DateTime.now();

  bool _allowClick() {
    var currentTime = DateTime.now();
    if (loginClickTime == null) {
      loginClickTime = currentTime;
      return true;
    }
    if (currentTime.difference(loginClickTime).inSeconds < 1) {
      return false;
    }

    loginClickTime = currentTime;
    return true;
  }

  playVideo() async {
    _videoViewController?.setVideoPath(videoURL);
    // _videoViewController?.start();
  }

  startRecord() async {
    _videoViewController?.startRecord(outVideo);
  }

  stopRecord() {
    _videoViewController?.stopRecord();
  }

  capturePhoto() async {
    _videoViewController?.capturePhoto(outPhoto);
  }

  pauseVideo() async {
    _videoViewController?.stopPreview();
  }

  Container buildTextButton(String text, VoidCallback? onPressed) {
    return Container(
      width: double.infinity,
      child: TextButton(
        style: ButtonStyle(
            foregroundColor: MaterialStateProperty.all<Color>(Colors.blue),
            shape: MaterialStateProperty.all<RoundedRectangleBorder>(
                RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(18.0),
                    side: const BorderSide(color: Colors.blue)))),
        onPressed: onPressed,
        child: Text(text),
      ),
    );
  }

  SizedBox buildVideoView() {
    return SizedBox(
      width: 400,
      height: 200,
      child: VideoView(
        onVideoViewCreatedCallback: (VideoViewController controller) {
          _videoViewController = controller;
        },
      ),
    );
  }
}
