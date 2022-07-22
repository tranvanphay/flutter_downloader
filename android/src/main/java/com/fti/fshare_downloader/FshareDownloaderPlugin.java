package com.fti.fshare_downloader;

import static com.fti.fshare_downloader.downloader.DownloadService.CANCEL_DOWNLOAD;
import static com.fti.fshare_downloader.downloader.DownloadService.CHANNEL_ID;
import static com.fti.fshare_downloader.downloader.DownloadService.PAUSE_DOWNLOAD;
import static com.fti.fshare_downloader.downloader.DownloadService.RESUME_DOWNLOAD;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_DOWNLOADING;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_ERROR;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_INIT;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_NONE;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_PAUSED;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_PREPARE_DOWNLOAD;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_REMOVED;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_WAIT;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.fti.fshare_downloader.callback.MyDownloadListener;
import com.fti.fshare_downloader.db.DBController;
import com.fti.fshare_downloader.domain.MyBusinessInfLocal;
import com.fti.fshare_downloader.downloader.DownloadService;
import com.fti.fshare_downloader.downloader.callback.DownloadManager;
import com.fti.fshare_downloader.downloader.domain.DownloadInfo;
import com.fti.fshare_downloader.event.MessageEvent;
import com.fti.fshare_downloader.event.NotificationReceiver;
import com.fti.fshare_downloader.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.SQLException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FshareDownloaderPlugin
 */
public class FshareDownloaderPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    public static final String SEND_DOWNLOAD_EVENT = "com.fti.fshare_downloader.sendDownloadEvent";
    public static final String GET_DOWNLOAD_EVENT = "com.fti.fshare_downloader.getDownloadEvent";

    private MethodChannel channel;
    private DownloadManager downloadManager;
    private DBController dbController;
    //    private DownloadInfo downloadInfo;
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "fshare_downloader");
        channel.setMethodCallHandler(this);
        downloadManager = DownloadService.getDownloadManager(flutterPluginBinding.getApplicationContext());
        try {
            dbController = DBController.getInstance(flutterPluginBinding.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        context = flutterPluginBinding.getApplicationContext();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        DownloadInfo info = downloadManager.getDownloadById(event.getDownloadInfo().getUri());
        switch (event.getEvent()) {
            case CANCEL_DOWNLOAD:
                Log.e("Phaydev::", event.getDownloadInfo().getPath());
                File file = new File(event.getDownloadInfo().getPath());
                downloadManager.remove(event.getDownloadInfo());
                boolean deleted = file.delete();
                if (deleted) {
                    Log.e("Phaydev::", "deleted");
                } else {
                    Log.e("Phaydev::", "delete error");
                }
                NotificationManager mNotificationManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(event.getDownloadInfo().hashCode());

                break;
            case PAUSE_DOWNLOAD:
                downloadManager.pause(info);
                Log.e("Phaydev::", "PAUSE ON" + event.getDownloadInfo().getUri());
                break;
            case RESUME_DOWNLOAD:
                downloadManager.resume(info);
                Log.e("Phaydev::", "RESUME");
                break;
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals(SEND_DOWNLOAD_EVENT)) {
            final int event = call.argument("event");
            String info = null;
            if (call.hasArgument("downloadInfo")) {
                info = call.argument("downloadInfo");
            }
            if (info != null) {
                info = info.substring(1, info.length() - 1);
                info = info.replaceAll("\\\\", "");
                Log.e("Phaydev", info);
                Gson gson = new Gson();
                try {
                    Info infoObject = gson.fromJson(info, Info.class);
                    boolean hasInfo = checkIsDownloading(infoObject.getUrl());
                    if (hasInfo) return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            switch (event) {
                case STATUS_NONE:
                    break;
                case STATUS_PREPARE_DOWNLOAD:
                    break;
                case STATUS_DOWNLOADING:
                    break;
                case STATUS_WAIT:
                    break;
                case STATUS_PAUSED:
                    break;

                case STATUS_COMPLETED:
                    break;

                case STATUS_ERROR:
                    break;

                case STATUS_REMOVED:
                    break;
                case STATUS_INIT:
                    startDownloading(info, result);
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void startDownloading(String info, Result result) {
        Gson gson = new Gson();
        Info infoObject = gson.fromJson(info, Info.class);
        File d = new File(Environment.getExternalStorageDirectory().getPath() + "/Download");
        if (!d.exists()) {
            d.mkdirs();
        }
        String path = d.getAbsolutePath().concat("/").concat(infoObject.getFileName());
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(infoObject.getUrl())
                .setPath(path)
                .setFileName(infoObject.getFileName())
                .build();
        downloadInfo
                .setDownloadListener(new MyDownloadListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onRefresh() {
                        Log.e("Phaydev", "Listen on " + downloadInfo.getUri());
                        refresh(downloadInfo.getUri());
                    }
                });
        downloadManager.download(downloadInfo);

        //save extra info to my database.
        MyBusinessInfLocal myBusinessInfLocal = new MyBusinessInfLocal(
                infoObject.getUrl(), infoObject.getFileName(), infoObject.getUrl());
        try {
            dbController.createOrUpdateMyDownloadInfo(myBusinessInfLocal);
            result.success(true);
        } catch (SQLException e) {
            e.printStackTrace();
            result.success(false);
        }
        initNotification(downloadInfo, (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()), FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                .formatFileSize(downloadInfo.getSize()), true);
    }

    private boolean checkIsDownloading(String url) {
        DownloadInfo downloadInfo = downloadManager.getDownloadById(url);
        if (downloadInfo != null) {
            downloadInfo
                    .setDownloadListener(new MyDownloadListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onRefresh() {
                            refresh(downloadInfo.getUri());
                        }
                    });
            if (downloadInfo.getStatus() == STATUS_DOWNLOADING) {
                Toast.makeText(context, context.getString(R.string.is_downloading), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refresh(String url) {
        DownloadInfo downloadInfo = downloadManager.getDownloadById(url);
        if (downloadInfo != null) {
            updateNoti(downloadInfo, (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()), FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                    .formatFileSize(downloadInfo.getSize()));
            switch (downloadInfo.getStatus()) {
                case STATUS_NONE:
                    Log.e("Phaydev", "Status none");
                    break;
                case STATUS_PAUSED:
                    Log.e("Phaydev", "Status paused: \n ProgressBar:" + (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()) + "\n downloadedSize: " + FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getSize()));
                    break;
                case STATUS_ERROR:
                    Log.e("Phaydev", "Status error: \n ProgressBar:" + (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()) + "\n downloadedSize: " + FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getSize()));
                    downloadManager.remove(downloadInfo);
                    break;

                case STATUS_DOWNLOADING:
                    Log.e("Phaydev", "Status downloading: \n ProgressBar:" + (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()) + "\n downloadedSize: " + FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getSize()));
                    break;
                case STATUS_PREPARE_DOWNLOAD:
                    Log.e("Phaydev", "Status predownload: \n ProgressBar:" + (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()) + "\n downloadedSize: " + FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getSize()));
                    break;
                case STATUS_COMPLETED:
                    Log.e("Phaydev", "Status completed: \n ProgressBar:" + (int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()) + "\n downloadedSize: " + FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getSize()));
                    downloadManager.remove(downloadInfo);

                    break;
                case STATUS_REMOVED:
                    Log.e("Phaydev", "Status removed");
                    downloadManager.remove(downloadInfo);

                    break;
                case STATUS_WAIT:
                    Log.e("Phaydev", "Status waiting");
                    break;
            }

        } else {
            Log.e("Phaydev", "DownloadInfo null");
        }
    }

    private NotificationCompat.Builder initNotification(
            DownloadInfo downloadInfo,
            int progress,
            String currentDownloaded,
            boolean needStart
    ) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download) // notification icon
                .setContentTitle(downloadInfo.getFileName()) // title for notification
                .setContentText(getNotificationStatus(downloadInfo.getStatus())) // mess
                .setSubText(getSubText(downloadInfo.getStatus(), currentDownloaded))
                .setProgress(100, progress, false)// age for notification
                .setVibrate(new long[0])
                .setAutoCancel(false) // clear notification after click
                .setOngoing(true)
                .addAction(
                        getNotiButtonIcon(downloadInfo.getStatus()),
                        getNotiButtonText(downloadInfo.getStatus()),
                        getNotificationAction(downloadInfo)
                )
                .addAction(
                        android.R.drawable.ic_menu_close_clear_cancel,
                        context.getString(R.string.btn_cancel),
                        buildNotificationActionButton(
                                CANCEL_DOWNLOAD, downloadInfo
                        )
                );

//        Intent notifyIntent = new Intent(this, Class.forName("ir.sibvas.testlibary1.HelloWorldActivity")).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK || Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pi = PendingIntent.getActivity(
//                this,
//                0,
//                notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT || PendingIntent.FLAG_IMMUTABLE
//        );
//        mBuilder.setContentIntent(pi)
        Intent notificationIntent = null;
        try {
            notificationIntent = new Intent(context, Class.forName("com.fti.fshare_downloader_example.MainActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(contentIntent);
        if (needStart) {
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(downloadInfo.hashCode(), mBuilder.build());
        }
        return mBuilder;
    }

    private CharSequence getSubText(int status, String currentDownloaded) {
        switch (status) {
            case STATUS_DOWNLOADING:
            case STATUS_PAUSED:
                return currentDownloaded;
        }
        return null;
    }

    private CharSequence getNotificationStatus(int status) {
        switch (status) {
            case STATUS_NONE:
                return context.getString(R.string.sts_unknown);
            case STATUS_COMPLETED:
                return context.getString(R.string.sts_completed);
            case STATUS_ERROR:
                return context.getString(R.string.sts_error);
            case STATUS_DOWNLOADING:
                return context.getString(R.string.sts_downloading);
            case STATUS_PREPARE_DOWNLOAD:
                return context.getString(R.string.sts_preparing);
            case STATUS_PAUSED:
                return context.getString(R.string.sts_paused);
        }
        return context.getString(R.string.sts_unknown);

    }

    private PendingIntent getNotificationAction(DownloadInfo downloadInfo) {
        if (downloadInfo.getStatus() == STATUS_PAUSED) {
            return buildNotificationActionButton(
                    RESUME_DOWNLOAD, downloadInfo
            );
        } else {
            return buildNotificationActionButton(
                    PAUSE_DOWNLOAD, downloadInfo
            );
        }
    }

    private int getNotiButtonIcon(int status) {
        if (status == STATUS_PAUSED) {
            return android.R.drawable.ic_media_pause;
        } else {
            return android.R.drawable.ic_media_play;
        }
    }

    private String getNotiButtonText(int status) {
        if (status == STATUS_PAUSED) {
            return context.getString(R.string.btn_resume);
        } else {
            return context.getString(R.string.btn_pause);
        }
    }

    private PendingIntent buildNotificationActionButton(String action, DownloadInfo downloadInfo) {
        Intent receiver = new Intent();
        Bundle bundle = new Bundle();
        receiver.setAction(action);
        bundle.putSerializable("value", downloadInfo);
        receiver.putExtras(bundle);
        return PendingIntent.getBroadcast(context, 2425, receiver, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private void updateNoti(DownloadInfo downloadInfo,
                            int progress,
                            String currentDownloaded) {
        NotificationCompat.Builder notificationBuilder = initNotification(
                downloadInfo, progress, currentDownloaded, true
        );

        if (downloadInfo.getStatus() == STATUS_COMPLETED || downloadInfo.getStatus() == STATUS_PAUSED) {
            stopNotificationAction(notificationBuilder, downloadInfo.getStatus());
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(downloadInfo.hashCode(), notificationBuilder.build());
        }

        if (downloadInfo.getStatus() == STATUS_ERROR) {
            stopNotificationAction(notificationBuilder, downloadInfo.getStatus());
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(downloadInfo.hashCode(), notificationBuilder.build());
        }

    }

    private void stopNotificationAction(
            NotificationCompat.Builder notificationBuilder,
            int status
    ) {
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setOngoing(false);
        if (status == STATUS_ERROR) {
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
        } else if (status == STATUS_COMPLETED) {
            notificationBuilder.clearActions();
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        } else if (status == STATUS_PAUSED) {
            notificationBuilder.setAutoCancel(false);
            notificationBuilder.setOngoing(true);
            notificationBuilder.setSmallIcon(android.R.drawable.ic_media_pause);
        }
    }
}
