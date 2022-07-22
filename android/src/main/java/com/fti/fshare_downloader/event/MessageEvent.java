package com.fti.fshare_downloader.event;

import com.fti.fshare_downloader.downloader.domain.DownloadInfo;

public class MessageEvent {
    String event;
    DownloadInfo downloadInfo;

    public MessageEvent(String event, DownloadInfo downloadInfo) {
        this.event = event;
        this.downloadInfo = downloadInfo;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }
}
