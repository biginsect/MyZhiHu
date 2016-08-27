package com.biginsect.myzhihu.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.biginsect.myzhihu.Adapter.MyViewPagerAdapter;
import com.biginsect.myzhihu.Adapter.NewsAdapter;
import com.biginsect.myzhihu.R;
import com.biginsect.myzhihu.db.ZhihuDB;
import com.biginsect.myzhihu.db.ZhihuOpenHelper;
import com.biginsect.myzhihu.model.News;
import com.biginsect.myzhihu.util.HttpCallbackListener;
import com.biginsect.myzhihu.util.Util;
import com.biginsect.myzhihu.util.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administration on 2016/8/20.
 */
public class NewsWebActivity extends AppCompatActivity implements View.OnClickListener{

    private Button back;
    private Button colleted;
    private WebView webView ;
    private ZhihuDB zhihuDB = ZhihuDB.getInstance(this);
    private static int flag = 0;

    private final static String DB_NAME = "MyZhiHu";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web_layout);
        getSupportActionBar().hide();



        //返回最新新闻界面
        back = (Button)findViewById(R.id.title_back);
        back.setOnClickListener(this) ;
        //点击收藏按钮触发的事件
        colleted = (Button)findViewById(R.id.collected) ;
        colleted.setOnClickListener(this);

        //向list添加数据
        //收藏news的listview

        webView = (WebView)findViewById(R.id.web_view);
        //标题栏的title

        String address = getIntent().getStringExtra("address");
        Util.sendWebViewRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //将获取到的html代码以及css代码存储
                String[] code = Utility.handleResponseForWebview(response);
                saveCode(code);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        //将保存的html代码读取出来，通过方法加载这个webview
        String htmlCode = loadHtmlCode();
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadDataWithBaseURL("data/data/com.biginsect.zhihu/files/css.css",htmlCode,"text/html","UTF-8",null);


    }

    //存储html代码和css代码
    private synchronized void saveCode(String[] code){
        if (code.length > 1){
            FileOutputStream out =null;
            BufferedWriter writer = null;
            try{
                out = openFileOutput("css.css", Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(code[1]);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if (writer != null) writer.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            try{
                out = openFileOutput("html",Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                //后面添加的一段html代码是为了缩放图片达到一定的尺寸不至于超出手机屏幕
                //但是会导致图片变模糊，要寻找第二种方法
                writer.write(code[0]+"<head><style>img{width:320px !important;}</style></head>");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    if (writer != null) writer.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //将存储的html代码取出来
    private synchronized String loadHtmlCode(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder htmlCode = new StringBuilder();
        try {
            in = openFileInput("html");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null){
                htmlCode.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return  htmlCode.toString();
    }


    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                Intent intent = new Intent(NewsWebActivity.this,ZhiHuActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.collected:
                flag += 1;
                ZhihuOpenHelper dbHelper = new ZhihuOpenHelper(NewsWebActivity.this,DB_NAME,null,1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                String newsId = getIntent().getStringExtra("id");
                if ((flag % 2) == 0){
                    values.put("news_isCollected",0);
                    Log.d("-----------","cancel");
                }
                else {
                    values.put("news_isCollected",1);
                    Log.d("-------------","succeed");
                }
                db.update("News",values,"id = ?",new String[]{newsId});
//                Cursor cursor = db.query("News",new String[]{"news_isCollected"},"id = ?",new String[]{newsId},null,null,null);
//                isCollect = cursor.getString(cursor.getColumnIndex("news_isCollected"));
//                cursor.close();
                db.close();
                break;
            default:
                break;
        }
    }
}
