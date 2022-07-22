import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:fshare_downloader/download_info.dart';

import 'fshare_downloader_platform_interface.dart';

/// An implementation of [FshareDownloaderPlatform] that uses method channels.
class MethodChannelFshareDownloader extends FshareDownloaderPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('fshare_downloader');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool?> sendDownloadEvent(
      {required DownloadEvent event, DownloadInfo? info}) async {
    return await methodChannel.invokeMethod(
        'com.fti.fshare_downloader.sendDownloadEvent',
        {'event': event.value, 'downloadInfo': jsonEncode(info)});
  }

  @override
  Future<int?> getDownloadEvent() async {
    return await methodChannel
        .invokeMethod<int>('com.fti.fshare_downloader.getDownloadEvent');
  }
}
