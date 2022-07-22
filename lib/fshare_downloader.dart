import 'package:fshare_downloader/download_info.dart';

import 'fshare_downloader_platform_interface.dart';

class FshareDownloader {
  Future<String?> getPlatformVersion() {
    return FshareDownloaderPlatform.instance.getPlatformVersion();
  }

  Future<bool?> sendDownloadEvent(
      {required DownloadEvent event, DownloadInfo? info}) {
    return FshareDownloaderPlatform.instance
        .sendDownloadEvent(event: event, info: info);
  }

  Future<int?> getDownloadEvent() {
    return FshareDownloaderPlatform.instance.getDownloadEvent();
  }
}
