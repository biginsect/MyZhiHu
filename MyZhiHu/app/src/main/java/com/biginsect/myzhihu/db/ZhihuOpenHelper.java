package com.biginsect.myzhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by administration on 2016/8/18.
 */
public class ZhihuOpenHelper extends SQLiteOpenHelper {
    //建表
    private static final String CREATE_NEWS = "create table News(" +
            "id text primary key ," +
            "news_date text," +
            "news_title text," +
            "news_bitmapUrl text)";//数据库保存的是图片的url

    public ZhihuOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
