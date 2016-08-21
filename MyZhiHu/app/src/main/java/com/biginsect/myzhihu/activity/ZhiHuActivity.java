package com.biginsect.myzhihu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.biginsect.myzhihu.Adapter.NewsAdapter;
import com.biginsect.myzhihu.R;
import com.biginsect.myzhihu.db.ZhihuDB;
import com.biginsect.myzhihu.model.News;
import com.biginsect.myzhihu.util.HttpCallbackListener;
import com.biginsect.myzhihu.util.Util;
import com.biginsect.myzhihu.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administration on 2016/8/18.
 */
public class ZhiHuActivity extends AppCompatActivity {

    private News selectedNews;
    private ListView listView;
    private NewsAdapter adapter;
    private ProgressDialog progressDialog;
    private List<News> newsList = new ArrayList<News>();
    private ZhihuDB zhihuDB ;
    private List<News> dataList = new ArrayList<News>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihu_layout);
        zhihuDB = ZhihuDB.getInstance(this);
        adapter = new NewsAdapter(this,R.layout.news_item,dataList);
        queryNews();
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedNews = newsList.get(position);
                String id = selectedNews.getId();
                String address = "http://news-at.zhihu.com/api/4/news/"+id;
                Intent intent = new Intent(ZhiHuActivity.this,NewsWebActivity.class);
                intent.putExtra("address",address);
                startActivity(intent);
            }
        });


    }

    //最新news，获取其对应的title以及image
    private void queryNewsInfoFromServer(){
        String address = "http://news-at.zhihu.com/api/4/news/latest";
        showProgressDialog();
        Util.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result ;
                result = Utility.handleNewsResponse(zhihuDB,response);

                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            queryNews();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                    }
                });
            }
        });
    }


    //如果本地数据库保存有url等的信息，那么从数据库读取，否则直接访问url获取相对应的信息
    private void queryNews(){
        newsList = zhihuDB.loadNews();
        if (newsList.size() > 0){
            dataList.clear();
            for (News news : newsList){
                dataList.add(news);
            }
            adapter.notifyDataSetChanged();
        }else{
            queryNewsInfoFromServer();
        }
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
