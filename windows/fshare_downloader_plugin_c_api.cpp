#include "include/fshare_downloader/fshare_downloader_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "fshare_downloader_plugin.h"

void FshareDownloaderPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  fshare_downloader::FshareDownloaderPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
