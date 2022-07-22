package com.fti.fshare_downloader.db;

import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static com.fti.fshare_downloader.downloader.domain.DownloadInfo.STATUS_PAUSED;

import android.content.Context;

import com.fti.fshare_downloader.downloader.db.DownloadDBController;
import com.fti.fshare_downloader.downloader.domain.DownloadInfo;
import com.fti.fshare_downloader.downloader.domain.DownloadThreadInfo;
import com.fti.fshare_downloader.domain.MyBusinessInfLocal;
import com.fti.fshare_downloader.domain.MyDownloadInfLocal;
import com.fti.fshare_downloader.domain.MyDownloadThreadInfoLocal;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 19/9/2021.
 */

public class DBController implements DownloadDBController {

    private static DBController instance;
    private final Context context;
    private final DBHelper dbHelper;
    private final Dao<MyBusinessInfLocal, String> myBusinessInfoLocalsDao;
    private final Dao<MyDownloadInfLocal, String> myDownloadInfLocalDao;
    private final Dao<MyDownloadThreadInfoLocal, String> myDownloadThreadInfoLocalDao;

    public DBController(Context context) throws SQLException {
        this.context = context;
        dbHelper = new DBHelper(context);
        try {
            myBusinessInfoLocalsDao = dbHelper.getDao(MyBusinessInfLocal.class);
            myDownloadInfLocalDao = dbHelper.getDao(MyDownloadInfLocal.class);
            myDownloadThreadInfoLocalDao = dbHelper.getDao(MyDownloadThreadInfoLocal.class);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static DBController getInstance(Context context) throws SQLException {
        if (instance == null) {
            instance = new DBController(context);
        }
        return instance;
    }


    public void createOrUpdateMyDownloadInfo(MyBusinessInfLocal downloadInfoLocal)
            throws SQLException {
        myBusinessInfoLocalsDao.createOrUpdate(downloadInfoLocal);
    }

    public int deleteMyDownloadInfo(String id)
            throws SQLException {
        return myBusinessInfoLocalsDao.deleteById(id);
    }

    public MyBusinessInfLocal findMyDownloadInfoById(String id)
            throws SQLException {
        return myBusinessInfoLocalsDao.queryForId(id);
    }

    @Override
    public List<DownloadInfo> findAllDownloading() {
        try {
            List<MyDownloadInfLocal> myDownloadInfLocals = myDownloadInfLocalDao.queryBuilder().where()
                    .ne("status", STATUS_COMPLETED).query();
            return convertDownloadInfos(myDownloadInfLocals);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    public List<DownloadInfo> findAllDownloaded() {
        try {
            List<MyDownloadInfLocal> myDownloadInfLocals = myDownloadInfLocalDao.queryBuilder().where()
                    .eq("status", STATUS_COMPLETED).query();
            return convertDownloadInfos(myDownloadInfLocals);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public DownloadInfo findDownloadedInfoById(String id) {
        try {
            return convertDownloadInfo(myDownloadInfLocalDao.queryForId(id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void pauseAllDownloading() {

        try {
            UpdateBuilder<MyDownloadInfLocal, String> myDownloadInfLocalIntegerUpdateBuilder = myDownloadInfLocalDao
                    .updateBuilder();
            myDownloadInfLocalIntegerUpdateBuilder.updateColumnValue("status", STATUS_PAUSED).where()
                    .ne("status", STATUS_COMPLETED);
            myDownloadInfLocalIntegerUpdateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOrUpdate(DownloadInfo downloadInfo) {
        try {
            myDownloadInfLocalDao.createOrUpdate(convertDownloadInfo(downloadInfo));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void createOrUpdate(DownloadThreadInfo downloadThreadInfo) {
        try {
            myDownloadThreadInfoLocalDao.createOrUpdate(convertDownloadThreadInfo(downloadThreadInfo));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DownloadInfo downloadInfo) {
        try {
            myDownloadInfLocalDao.deleteById(downloadInfo.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void delete(DownloadThreadInfo download) {
        try {
            myDownloadThreadInfoLocalDao.deleteById(download.getDownloadInfoId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<DownloadInfo> convertDownloadInfos(List<MyDownloadInfLocal> infos) {
        List<DownloadInfo> downloadInfos = new ArrayList<>();
        for (MyDownloadInfLocal downloadInfLocal : infos) {
            downloadInfos.add(convertDownloadInfo(downloadInfLocal));
        }
        return downloadInfos;
    }


    private DownloadInfo convertDownloadInfo(MyDownloadInfLocal downloadInfLocal) {
        if (downloadInfLocal == null) {
            return null;
        }
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setCreateAt(downloadInfLocal.getCreateAt());
        downloadInfo.setUri(downloadInfLocal.getUri());
        downloadInfo.setPath(downloadInfLocal.getPath());
        downloadInfo.setFileName(downloadInfLocal.getFileName());
        downloadInfo.setSize(downloadInfLocal.getSize());
        downloadInfo.setProgress(downloadInfLocal.getProgress());
        downloadInfo.setStatus(downloadInfLocal.getStatus());
        downloadInfo.setSupportRanges(downloadInfLocal.getSupportRanges());
        downloadInfo.setDownloadThreadInfos(
                converDownloadThreadInfos1(downloadInfLocal.getDownloadThreadInfos()));
        return downloadInfo;
    }

    private List<DownloadThreadInfo> converDownloadThreadInfos1(
            List<MyDownloadThreadInfoLocal> downloadThreadInfosLocal) {
        List<DownloadThreadInfo> downloadThreadInfos = new ArrayList<>();
        if (downloadThreadInfosLocal != null) {
            for (MyDownloadThreadInfoLocal d : downloadThreadInfosLocal
                    ) {
                downloadThreadInfos.add(convertDownloadThreadInfo(d));
            }
        }

        return downloadThreadInfos;
    }

    private List<MyDownloadThreadInfoLocal> convertDownloadThreadInfos(
            List<DownloadThreadInfo> downloadThreadInfos) {
        if (downloadThreadInfos == null) {
            return null;
        }
        List<MyDownloadThreadInfoLocal> downloadThreadInfosLocal = new ArrayList<>();
        for (DownloadThreadInfo d : downloadThreadInfos
                ) {
            downloadThreadInfosLocal.add(convertDownloadThreadInfo(d));
        }
        return downloadThreadInfosLocal;
    }


    private DownloadThreadInfo convertDownloadThreadInfo(MyDownloadThreadInfoLocal d) {
        DownloadThreadInfo downloadThreadInfo = new DownloadThreadInfo();
        downloadThreadInfo.setProgress(d.getId());
        downloadThreadInfo.setThreadId(d.getThreadId());
        downloadThreadInfo.setDownloadInfoId(d.getDownloadInfoId());
        downloadThreadInfo.setUri(d.getUri());
        downloadThreadInfo.setStart(d.getStart());
        downloadThreadInfo.setEnd(d.getEnd());
        downloadThreadInfo.setProgress(d.getProgress());
        return downloadThreadInfo;
    }

    private MyDownloadThreadInfoLocal convertDownloadThreadInfo(DownloadThreadInfo d) {
        MyDownloadThreadInfoLocal downloadThreadInfo = new MyDownloadThreadInfoLocal();
        downloadThreadInfo.setProgress(d.getId());
        downloadThreadInfo.setThreadId(d.getThreadId());
        downloadThreadInfo.setDownloadInfoId(d.getDownloadInfoId());
        downloadThreadInfo.setUri(d.getUri());
        downloadThreadInfo.setStart(d.getStart());
        downloadThreadInfo.setEnd(d.getEnd());
        downloadThreadInfo.setProgress(d.getProgress());
        return downloadThreadInfo;
    }

    private MyDownloadInfLocal convertDownloadInfo(DownloadInfo downloadInfo) {
        MyDownloadInfLocal downloadInfLocal = new MyDownloadInfLocal();
        downloadInfLocal.setCreateAt(downloadInfo.getCreateAt());
        downloadInfLocal.setUri(downloadInfo.getUri());
        downloadInfLocal.setPath(downloadInfo.getPath());
        downloadInfLocal.setFileName(downloadInfo.getFileName());
        downloadInfLocal.setSize(downloadInfo.getSize());
        downloadInfLocal.setProgress(downloadInfo.getProgress());
        downloadInfLocal.setStatus(downloadInfo.getStatus());
        downloadInfLocal.setSupportRanges(downloadInfo.getSupportRanges());
        downloadInfLocal
                .setDownloadThreadInfos(convertDownloadThreadInfos(downloadInfo.getDownloadThreadInfos()));
        return downloadInfLocal;
    }
}
