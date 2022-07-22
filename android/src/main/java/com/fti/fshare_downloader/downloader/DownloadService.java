package com.fti.fshare_downloader.downloader;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.fti.fshare_downloader.downloader.callback.DownloadManager;
import com.fti.fshare_downloader.downloader.config.Config;

import java.util.List;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    public static DownloadManager downloadManager;

    public static final String CANCEL_DOWNLOAD = "CANCEL_DOWNLOAD";
    public static final String PAUSE_DOWNLOAD = "PAUSE_DOWNLOAD";
    public static final String RESUME_DOWNLOAD = "RESUME_DOWNLOAD";

    public static final String CHANNEL_ID = "com.fti.fshare_downloader";


    public static DownloadManager getDownloadManager(Context context) {
        return getDownloadManager(context, null);
    }

    public static DownloadManager getDownloadManager(Context context, Config config) {
        if (!isServiceRunning(context)) {
            Intent downloadSvr = new Intent(context, DownloadService.class);
            context.startService(downloadSvr);
        }
        if (DownloadService.downloadManager == null) {
            DownloadService.downloadManager = DownloadManagerImpl.getInstance(context, config);
        }
        return downloadManager;
    }

    @SuppressWarnings("deprecation")
    private static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(
                    DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (downloadManager != null) {
            downloadManager.removeAll();
            downloadManager.destroy();
            downloadManager = null;
            Log.e("Phaydev","Destroy download service");
        }
        super.onDestroy();
    }
}
