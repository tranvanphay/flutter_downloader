package com.fti.fshare_downloader.downloader.callback;

import com.fti.fshare_downloader.downloader.db.DownloadDBController;
import com.fti.fshare_downloader.downloader.domain.DownloadInfo;

import java.util.List;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public interface DownloadManager {

    void download(DownloadInfo downloadInfo);

    void pause(DownloadInfo downloadInfo);

    void resume(DownloadInfo downloadInfo);

    void remove(DownloadInfo downloadInfo);

    void removeAll();

    void destroy();

    DownloadInfo getDownloadById(String id);

    List<DownloadInfo> findAllDownloading();

    List<DownloadInfo> findAllDownloaded();

    DownloadDBController getDownloadDBController();

    void resumeAll();

    void pauseAll();

    void onDownloadFailed(DownloadInfo downloadInfo);
}
