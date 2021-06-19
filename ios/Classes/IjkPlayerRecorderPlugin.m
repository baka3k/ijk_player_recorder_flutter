#import "IjkPlayerRecorderPlugin.h"
#if __has_include(<ijk_player_recorder/ijk_player_recorder-Swift.h>)
#import <ijk_player_recorder/ijk_player_recorder-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "ijk_player_recorder-Swift.h"
#endif

@implementation IjkPlayerRecorderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftIjkPlayerRecorderPlugin registerWithRegistrar:registrar];
}
@end
