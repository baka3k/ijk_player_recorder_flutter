//
//  VideoViewFactory.swift
//  Runner
//
//  Created by quanghiep on 6/11/21.
//

import Foundation

class VideoViewFactory:NSObject,FlutterPlatformViewFactory{
    private var messenger:FlutterBinaryMessenger
    init(messenger: FlutterBinaryMessenger) {
        self.messenger = messenger
        super.init()
    }
    func create(
        withFrame frame: CGRect,
        viewIdentifier viewId: Int64,
        arguments args: Any?
    ) -> FlutterPlatformView {
        return VideoViewPlatform(
            frame: frame,
            viewIdentifier: viewId,
            arguments: args,
            binaryMessenger: messenger)
    }
}
