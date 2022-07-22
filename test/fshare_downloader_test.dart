import 'package:flutter_test/flutter_test.dart';
import 'package:fshare_downloader/fshare_downloader.dart';
import 'package:fshare_downloader/fshare_downloader_platform_interface.dart';
import 'package:fshare_downloader/fshare_downloader_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFshareDownloaderPlatform 
    with MockPlatformInterfaceMixin
    implements FshareDownloaderPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FshareDownloaderPlatform initialPlatform = FshareDownloaderPlatform.instance;

  test('$MethodChannelFshareDownloader is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFshareDownloader>());
  });

  test('getPlatformVersion', () async {
    FshareDownloader fshareDownloaderPlugin = FshareDownloader();
    MockFshareDownloaderPlatform fakePlatform = MockFshareDownloaderPlatform();
    FshareDownloaderPlatform.instance = fakePlatform;
  
    expect(await fshareDownloaderPlugin.getPlatformVersion(), '42');
  });
}
