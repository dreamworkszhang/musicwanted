package com.dreamworks.musicwanted.global;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.dreamworks.musicwanted.db.MusicWantedDBHelper;
import com.dreamworks.musicwanted.db.MusicWantedDao;

/**
 * Created by zhang on 2016/4/22.
 */
public class MainApplication extends Application {

    private static MainApplication instance;
    private int version = -1;
    private String appName = null;
    private MusicWantedDBHelper musicWantedDBHelper;
    private MusicWantedDao mMusicWantedDao;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        PackageManager manager = this.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appName = info.versionName; // 版本名
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        musicWantedDBHelper = MusicWantedDBHelper.getMusicWantedDBHelper(this, mDatabaseErrorHandler);
        mMusicWantedDao = MusicWantedDao.getMusicWantedDao();
    }

    public static MainApplication getApp() {
        return instance;
    }

    public int getAppVersion() {
        return version;
    }

    public String getAppName() {
        return appName;
    }

    public MusicWantedDBHelper getMusicWantedDBHelper() {
        return musicWantedDBHelper;
    }

    public MusicWantedDao getMusicWantedDao() {
        return mMusicWantedDao;
    }

    /**
     * 数据库损坏时回调的Handler
     */
    private DatabaseErrorHandler mDatabaseErrorHandler = new DatabaseErrorHandler() {

        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            // TODO Auto-generated method stub
            // 数据库损坏的时候调用
        }
    };
}
