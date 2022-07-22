// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:convert';

enum DownloadEvent {
  none(0),
  preparing(1),
  downloading(2),
  waiting(3),
  paused(4),
  completed(5),
  error(6),
  removed(7),
  init(8);

  const DownloadEvent(this.value);
  final int value;
}

class DownloadInfo {
  int downloadId;
  String url;
  String path;
  int progress;
  int start;
  int end;
  String fileName;

  DownloadInfo(
      {required this.downloadId,
      required this.url,
      required this.path,
      required this.progress,
      required this.start,
      required this.end,
      required this.fileName});

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'downloadId': downloadId,
      'url': url,
      'path': path,
      'progress': progress,
      'start': start,
      'end': end,
      'fileName': fileName,
    };
  }

  factory DownloadInfo.fromMap(Map<String, dynamic> map) {
    return DownloadInfo(
      downloadId: map['downloadId'] as int,
      url: map['url'] as String,
      path: map['path'] as String,
      fileName: map['fileName'] as String,
      progress: map['progress'] as int,
      start: map['start'] as int,
      end: map['end'] as int,
    );
  }

  String toJson() => json.encode(toMap());

  factory DownloadInfo.fromJson(String source) =>
      DownloadInfo.fromMap(json.decode(source) as Map<String, dynamic>);
}
