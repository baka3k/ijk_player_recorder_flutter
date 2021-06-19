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
- [x] Support IOS & Android Record video 
- [ ] Add filter/ effect to video - not at this time

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
