import 'package:fshare_downloader/download_info.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'fshare_downloader_method_channel.dart';

abstract class FshareDownloaderPlatform extends PlatformInterface {
  /// Constructs a FshareDownloaderPlatform.
  FshareDownloaderPlatform() : super(token: _token);

  static final Object _token = Object();

  static FshareDownloaderPlatform _instance = MethodChannelFshareDownloader();

  /// The default instance of [FshareDownloaderPlatform] to use.
  ///
  /// Defaults to [MethodChannelFshareDownloader].
  static FshareDownloaderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FshareDownloaderPlatform] when
  /// they register themselves.
  static set instance(FshareDownloaderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> sendDownloadEvent(
      {required DownloadEvent event, DownloadInfo? info}) {
    throw UnimplementedError('sendDownloadEvent() has not been implemented.');
  }

  Future<int?> getDownloadEvent() {
    throw UnimplementedError('getDownloadEvent() has not been implemented.');
  }
}
