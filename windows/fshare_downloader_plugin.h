#ifndef FLUTTER_PLUGIN_FSHARE_DOWNLOADER_PLUGIN_H_
#define FLUTTER_PLUGIN_FSHARE_DOWNLOADER_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace fshare_downloader {

class FshareDownloaderPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  FshareDownloaderPlugin();

  virtual ~FshareDownloaderPlugin();

  // Disallow copy and assign.
  FshareDownloaderPlugin(const FshareDownloaderPlugin&) = delete;
  FshareDownloaderPlugin& operator=(const FshareDownloaderPlugin&) = delete;

 private:
  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace fshare_downloader

#endif  // FLUTTER_PLUGIN_FSHARE_DOWNLOADER_PLUGIN_H_
