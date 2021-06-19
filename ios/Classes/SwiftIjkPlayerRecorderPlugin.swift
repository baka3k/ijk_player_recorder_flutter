import Flutter
import UIKit

public class SwiftIjkPlayerRecorderPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ijk_player_recorder", binaryMessenger: registrar.messenger())
    let instance = SwiftIjkPlayerRecorderPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
    
//    let factory = VideoViewFactory(messenger: registrar.messenger())
//    registrar.register(factory, withId: "VideoViewPlatform")
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
