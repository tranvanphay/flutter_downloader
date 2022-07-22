package com.fti.fshare_downloader.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.fti.fshare_downloader.downloader.DownloadService;
import com.fti.fshare_downloader.downloader.domain.DownloadInfo;

import org.greenrobot.eventbus.EventBus;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        DownloadInfo info=
                (DownloadInfo)bundle.getSerializable("value");
        Log.e("NotificationReceiver:::","On Receiver " + info.hashCode());
        EventBus.getDefault().post(new MessageEvent(action,info));

    }
}