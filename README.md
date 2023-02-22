# ijk_player_recorder

IJKPlayerRecorder

## Getting Started

This project is a plugin for Flutter,It provides VideoView allow play & record RTSP streaming  

If you want to get config IJKPlayerRecorder please get from [IJKPlayer source code](https://github.com/baka3k/IjkPlayerRecorder/),

For only android app get from [IJKPlayerRecorder](https://github.com/baka3k/RTSPRecorder/),

## Table of contents

- [Features](#features)
- [Requirements](#requirements)
- [Usage](#usage)
- [Sample](#sample)
- [Authors](#authors)
- [License](#license)

## Features

- [x] Play RTSP video with LOW LATENCY on Flutter
- [x] Play & Record video on Android
- [x] Capture video frame and save to file on Android
- [x] Apply Effect to video on Android
- [  ] Support IOS: Todo - doese not yet support at this time

## Requirements

- Flutter 
  sdk: '>=2.19.2 <3.0.0'
  

## Usage

```dart
import 'package:streaming_recorder/video_libs.dart'; // import Plugin
```

Record & Play Streaming RTSP

```dart

VideoViewController? _videoViewController;
.....
Expanded(child: VideoView(
                onVideoViewCreatedCallback: (VideoViewController controller) {
                  setState(() {
                    _videoViewController = controller;
                  });
                },
              ))
.....
```
Method Support

```dart
 void startPreview() {
    _videoViewController?.start();
  }

  void stopPreview() {
    _videoViewController?.stopPlayback();
  }

  Future<void> startRecord() async {
    // Please make sure that you have storage permission, in this sample
    Directory appDocDir = await getApplicationDocumentsDirectory();
    setState(() {
      outVideo = appDocDir.path + "/a.mp4";
    });
    _videoViewController?.startRecord(outVideo);
  }

  void stopRecord() {
    // MUST call stopRecord after call startRecord - to make sure the video format is CORRECT - if not - you CAN NOT play video file
    _videoViewController?.stopRecord();
  }

  void setURL() {
    _videoViewController?.setVideoPath(videoURL);
  }
  capturePhoto() async {
    _videoViewController?.capturePhoto(outPhoto);
  }
```

Apply Filter fo video
```dart
    _videoViewController?.setFilter(i);
```

## Sample
TBD

## Authors

baka3k@gmail.com

## License

[MIT License](https://github.com/baka3k/ijk_player_recorder_flutter/blob/main/LICENSE),
MIT 2.0 Copyright <2021> baka3k@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
