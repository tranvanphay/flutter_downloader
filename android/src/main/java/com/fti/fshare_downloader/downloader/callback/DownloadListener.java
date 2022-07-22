package com.fti.fshare_downloader.downloader.callback;

import com.fti.fshare_downloader.downloader.exception.DownloadException;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public interface DownloadListener {

    void onStart();

    void onWaited();

    void onPaused();

    void onDownloading(long progress, long size);

    void onRemoved();

    void onDownloadSuccess();

    void onDownloadFailed(DownloadException e);
}
