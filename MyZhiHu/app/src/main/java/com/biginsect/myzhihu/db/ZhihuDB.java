package com.biginsect.myzhihu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.biginsect.myzhihu.model.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administration on 2016/8/18.
 */
public class ZhihuDB {
    //数据库名称
    public static final String DB_NAME = "MyZhiHu";

    //数据库的版本
    public static final int VERSION = 1;
    private static ZhihuDB zhihuDB;
    private SQLiteDatabase db;
    //私有化构造方法，单例模式
    private ZhihuDB(Context context){
        ZhihuOpenHelper dbHelper = new ZhihuOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }
    //获取实例
    public synchronized static ZhihuDB getInstance(Context context){
        if (zhihuDB == null){
            zhihuDB = new ZhihuDB(context);
        }
        return zhihuDB;
    }

    //将News存储到数据库
    public void saveNews(News news){
        if (news != null){
            ContentValues values = new ContentValues();
            values.put("id",news.getId());
            values.put("news_date",news.getDate());
            values.put("news_title",news.getTitle());
            values.put("news_bitmapUrl",news.getBitmapUrl());
            //values.put("news_isCollected",news.getIsCollected());
            db.insert("News",null,values);
        }
    }

    //从数据库读取News
    public List<News> loadNews(){
        List<News> list = new ArrayList<News>();
        Cursor cursor = db.query("News",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                News news = new News();
                news.setId(cursor.getString(cursor.getColumnIndex("id")));
                news.setDate(cursor.getString(cursor.getColumnIndex("news_date")));
                news.setTitle(cursor.getString(cursor.getColumnIndex("news_title")));
                news.setBitmapUrl(cursor.getString(cursor.getColumnIndex("news_bitmapUrl")));
                //news.setIsCollected(cursor.getInt(cursor.getColumnIndex("isCollected")));
                list.add(news);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }

        return list;
    }
}
