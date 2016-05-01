package com.dreamworks.musicwanted.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dreamworks.musicwanted.global.MainApplication;


public class MusicWantedDBHelper extends SQLiteOpenHelper {

	private static MusicWantedDBHelper instance;
	private static final String dbName = "musicwanted.db";
	private static int version = MainApplication.getApp().getAppVersion();

	public static MusicWantedDBHelper getMusicWantedDBHelper(Context context, DatabaseErrorHandler mDatabaseErrorHandler) {
		if (instance == null) {
			synchronized (MusicWantedDBHelper.class) {
				if (instance == null) {
					instance = new MusicWantedDBHelper(context, mDatabaseErrorHandler);
				}
			}
		}
		return instance;
	}

	private MusicWantedDBHelper(Context context, DatabaseErrorHandler mDatabaseErrorHandler) {
		super(context, dbName, null, version, mDatabaseErrorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		initMusicWantedDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void initMusicWantedDB(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE artist_info (_id integer primary key AUTOINCREMENT, name text, song_count integer default 0, UNIQUE(name) ON CONFLICT IGNORE)";
		db.execSQL(sql);
		sql = "CREATE TABLE album_info (_id integer primary key AUTOINCREMENT, name text, song_count integer default 0, UNIQUE(name) ON CONFLICT IGNORE)";
		db.execSQL(sql);
		sql = "CREATE TABLE song_info (_id integer primary key AUTOINCREMENT, title text, artist_id int, artist text, album_id int, album text, duration int, file_path text, UNIQUE(file_path) ON CONFLICT IGNORE)";
		db.execSQL(sql);
	}
}
