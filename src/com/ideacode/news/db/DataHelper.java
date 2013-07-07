package com.ideacode.news.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ideacode.news.bean.TableUserPraiseBelittleEntity;
import com.ideacode.news.bean.UserOptionType;

public class DataHelper {

    // 数据库名称
    private static String DB_NAME = "ideacode_news.db";
    // 数据库版本
    private static int DB_VERSION = 1;
    private final SQLiteDatabase db;
    private final SqliteHelper dbHelper;

    public DataHelper(Context context) {
        // 定义一个SQLite数据库
        dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void Close() {
        db.close();
        dbHelper.close();
    }

    /************************ 操作用户顶、踩表 tb_user_praise_belittle 开始************************/
    public int addUserPraiseBelittle(long userId, int moodId, UserOptionType userOptionType) {
        int code = 0;
        Cursor cursor = db.query(SqliteHelper.TB_USER_PRAISE_BELITTLE, null, TableUserPraiseBelittleEntity.USERID + " = ? and "
                + TableUserPraiseBelittleEntity.MOODID + " = ? and " + TableUserPraiseBelittleEntity.USEROPTIONTYPE + " = ? ",
                new String[]{String.valueOf(userId),
            String.valueOf(moodId), String.valueOf(userOptionType) }, null, null, null);
        boolean hasResult = cursor.moveToFirst();
        if (hasResult) {
            code = 0;
        } else {
            ContentValues values = new ContentValues();
            values.put(TableUserPraiseBelittleEntity.USERID, userId);
            values.put(TableUserPraiseBelittleEntity.MOODID, moodId);
            values.put(TableUserPraiseBelittleEntity.USEROPTIONTYPE, String.valueOf(userOptionType));

            Long resultId = db.insert(SqliteHelper.TB_USER_PRAISE_BELITTLE, TableUserPraiseBelittleEntity.USEROPTIONTYPE, values);
            Log.e("addUserPraiseBelittle", resultId + "");
            code = 1;
        }
        cursor.close();

        return code;
    }
    /************************ 操作用户顶、踩表 tb_user_praise_belittle 结束************************/

}
