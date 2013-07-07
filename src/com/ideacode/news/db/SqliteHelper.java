package com.ideacode.news.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ideacode.news.bean.TableUserPraiseBelittleEntity;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqliteHelper";

    public static final String TB_USER_PRAISE_BELITTLE = "tb_user_praise_belittle";

    public SqliteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_USER_PRAISE_BELITTLE + "(" + TableUserPraiseBelittleEntity._ID + " integer primary key,"
                + TableUserPraiseBelittleEntity.USERID + " integer," + TableUserPraiseBelittleEntity.MOODID + " integer,"
                + TableUserPraiseBelittleEntity.USEROPTIONTYPE + " varchar)");
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_USER_PRAISE_BELITTLE);
        onCreate(db);
        Log.e(TAG, "onUpgrade");
    }

    // ¸üÐÂÁÐ
    public void updateColumn(SQLiteDatabase db, String tableName, String oldColumn, String newColumn, String typeColumn) {
        try {
            db.execSQL("ALTER TABLE " + tableName + " CHANGE " + oldColumn + " " + newColumn + " " + typeColumn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
