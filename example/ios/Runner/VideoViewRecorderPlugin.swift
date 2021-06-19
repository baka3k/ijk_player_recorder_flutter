//
//  IjkPlayerRecorderPlugin.swift
//  Runner
//
//  Created by quanghiep on 6/15/21.
//
//
//import Flutter
//import UIKit

public class VideoViewRecorderPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ijk_player_recorder", binaryMessenger: registrar.messenger())
    let instance = VideoViewRecorderPlugin()
    let viewFactory = VideoViewFactory(messenger: registrar.messenger());
    registrar.register(viewFactory,withId:"VideoViewPlatform");
    registrar.addMethodCallDelegate(instance, channel: channel)
    
//    let factory = VideoViewFactory(messenger: registrar.messenger())
//    registrar.register(factory, withId: "VideoViewPlatform")
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}

