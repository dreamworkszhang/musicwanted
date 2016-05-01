package com.dreamworks.musicwanted.db;


import com.dreamworks.musicwanted.global.MainApplication;

public class MusicWantedDao {

    private static MusicWantedDao instance;
    private MusicWantedDBHelper mMusicWantedDBHelper = null;

    private MusicWantedDao() {
        mMusicWantedDBHelper = MainApplication.getApp().getMusicWantedDBHelper();
    }

    public static MusicWantedDao getMusicWantedDao() {
        if (instance == null) {
            synchronized (MusicWantedDao.class) {
                if (instance == null) {
                    instance = new MusicWantedDao();
                }
            }
        }
        return instance;
    }

}
