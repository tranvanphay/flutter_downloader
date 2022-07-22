package com.fti.fshare_downloader.downloader.core;

import com.fti.fshare_downloader.downloader.domain.DownloadInfo;
import com.fti.fshare_downloader.downloader.exception.DownloadException;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public interface DownloadResponse {

    void onStatusChanged(DownloadInfo downloadInfo);

    void handleException(DownloadInfo downloadInfo, DownloadException exception);
}
