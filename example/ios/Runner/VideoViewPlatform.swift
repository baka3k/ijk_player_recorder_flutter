//
//  VideoViewPlatform.swift
//  Runner
//
//  Created by quanghiep on 6/11/21.
//

import Foundation
import IJKMediaFramework
import AVKit
import os
class VideoViewPlatform:NSObject, FlutterPlatformView{
    private var _view: UIView
    private var url = URL(string: "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov")
    
    private var methodChannel:FlutterMethodChannel
    private var ijkContronller:IJKFFMoviePlayerController?
    init(
        frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?,
        binaryMessenger messenger: FlutterBinaryMessenger?
    ) {
        _view = UIView()
        methodChannel = FlutterMethodChannel(name: "VideoViewPlatform/\(viewId)", binaryMessenger: messenger!)
        super.init()
        initCallBackChannel()
    }
    
    func view() -> UIView {
        createNativeView(view: _view)
        return _view
    }
    func createNativeView(view _view: UIView){
//        loadIIJK(view: _view)
    }
    func initCallBackChannel()
    {
        methodChannel.setMethodCallHandler({
            [weak self] (call: FlutterMethodCall, result: FlutterResult) -> Void in
            // Note: this method is invoked on the UI thread.
            
            let methodName = call.method
            print("methodName:\(methodName)")
            if(methodName == "setVideoPath")
            {
                self?.setVideoPath(videoPath: call.arguments as! String)
            }
            else if (methodName == "start"){
                self?.startPlayback()
            }
            else if (methodName == "stopPlayback"){
                self?.stopPlayback()
            }
            else if (methodName == "stopRecord") {
                self?.stopRecord()
            }
            else if (methodName == "startRecord"){
                self?.startRecord(videoOutput: call.arguments as! String)
            }
            else{
                result(FlutterMethodNotImplemented)
            }
        })
    }
    func setVideoPath(videoPath _path:String){
        url = URL(string: _path)
        loadIIJK(view: _view)
    }
    func startPlayback() {
        ijkContronller?.play()
    }
    func stopPlayback() {
        ijkContronller?.stop()
    }
    func startRecord(videoOutput _path:String) {
        ijkContronller?.startRecord(withFileName: _path)
    }
    func stopRecord() {
        ijkContronller?.stopRecord()
    }
    func loadIIJK(view _view: UIView){
        cleanView(view: _view)
        let options = IJKFFOptions.byDefault()
        guard let player = IJKFFMoviePlayerController(contentURL: url, with: options) else {
            print("Create RTSP Player failed")
            return
        }
        let autoresize = UIView.AutoresizingMask.flexibleWidth.rawValue |
            UIView.AutoresizingMask.flexibleHeight.rawValue
        player.view.autoresizingMask = UIView.AutoresizingMask(rawValue: autoresize)
        player.view.frame = self._view.bounds
        player.scalingMode = IJKMPMovieScalingMode.aspectFit
        player.shouldAutoplay = true
        self._view.autoresizesSubviews = true
        self._view.addSubview(player.view)
        self.ijkContronller = player
        self.ijkContronller?.prepareToPlay()
    }
    func cleanView(view _view:UIView)
    {
        for view in _view.subviews {
            view.removeFromSuperview()
        }
    }
}
