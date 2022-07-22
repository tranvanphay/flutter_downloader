#import "FshareDownloaderPlugin.h"
#if __has_include(<fshare_downloader/fshare_downloader-Swift.h>)
#import <fshare_downloader/fshare_downloader-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "fshare_downloader-Swift.h"
#endif

@implementation FshareDownloaderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFshareDownloaderPlugin registerWithRegistrar:registrar];
}
@end
