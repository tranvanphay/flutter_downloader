package com.fti.fshare_downloader;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("downloadId")
    private int downloadId;

    @SerializedName("url")
    private String url;

    @SerializedName("path")
    private String path;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("progress")
    private int progress;

    @SerializedName("start")
    private int start;

    @SerializedName("end")
    private int end;

    public Info(int downloadId, String url, String path,String fileName, int progress, int start, int end) {
        this.downloadId = downloadId;
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.progress = progress;
        this.start = start;
        this.end = end;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
