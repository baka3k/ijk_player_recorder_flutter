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
- [x] Android Play & Record video
- [  ] Support IOS: Todo
- [  ] Add filter/ effect to video - not at this time

## Requirements

- Flutter sdk: ">=2.12.0 <3.0.0"


## Usage

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

```kotlin
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
