import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:fshare_downloader/download_info.dart';
import 'package:fshare_downloader/fshare_downloader.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _fshareDownloaderPlugin = FshareDownloader();
  final edtUrl = TextEditingController();
  final edtFileName = TextEditingController();
  final edtId = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  Future<bool> startDownloadService(
      String url, String fileName, int fileId) async {
    bool sendEventCallback;

    try {
      sendEventCallback = await _fshareDownloaderPlugin.sendDownloadEvent(
              event: DownloadEvent.init,
              info: DownloadInfo(
                  downloadId: fileId,
                  url: url,
                  path: 'def',
                  progress: 1,
                  start: 0,
                  fileName: fileName,
                  end: 1000)) ??
          false;
    } on PlatformException {
      sendEventCallback = false;
    }
    return sendEventCallback;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(children: [
            TextField(
              decoration: const InputDecoration(hintText: 'URL'),
              controller: edtUrl,
            ),
            TextField(
              decoration: const InputDecoration(hintText: 'File name'),
              controller: edtFileName,
            ),
            TextField(
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(hintText: 'File id'),
              controller: edtId,
            ),
            MaterialButton(
                color: Colors.red,
                onPressed: () {
                  if (edtUrl.text.isNotEmpty) {
                    startDownloadService(
                        edtUrl.text, edtFileName.text, int.parse(edtId.text));
                    edtUrl.clear;
                  }
                })
          ]),
        ),
      ),
    );
  }
}
