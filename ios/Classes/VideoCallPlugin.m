#import "EkoJitsiPlugin.h"
#if __has_include(<video_call/video_call-Swift.h>)
#import <video_call/video_call-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "video_call-Swift.h"
#endif

@implementation VideoCallPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftVideoCallPlugin registerWithRegistrar:registrar];
}
@end