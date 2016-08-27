package com.biginsect.myzhihu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.biginsect.myzhihu.Adapter.MyViewPagerAdapter;
import com.biginsect.myzhihu.Adapter.NewsAdapter;
import com.biginsect.myzhihu.R;
import com.biginsect.myzhihu.View.MyViewPager;
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
    private ListView listView,collectedNews;
    private NewsAdapter adapter,colAdapter;
    private ProgressDialog progressDialog;
    private List<News> newsList = new ArrayList<News>();
    private List<News> colNews = new ArrayList<>();
    private ZhihuDB zhihuDB ;
    private List<News> dataList = new ArrayList<News>();
    private List<News> listCol = new ArrayList<>();

    //实现viewpager所需要的属性
    View view1 , view2;
    private ViewPager viewPager;
    private PagerTitleStrip titleStrip;
    private List<View> viewList;
    private List<String> titles;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        titleStrip = (PagerTitleStrip)findViewById(R.id.titleStrip);

        //tab之间的距离,并没有什么效果。
//        pagerTabStrip.setTextSpacing(30);
        initDatas();
        myViewPagerAdapter = new MyViewPagerAdapter(viewList,titles);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(0);
        //滑动这个viewpager的响应事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                    case 1:
                        initCollectedNews();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        zhihuDB = ZhihuDB.getInstance(this);

        //加载新闻主页
        adapter = new NewsAdapter(this,R.layout.news_item,dataList);
        queryNews();
        //错误的写法
//        listView = (ListView)getLayoutInflater().inflate(R.layout.zhihu_layout,null).findViewById(R.id.listView);
        listView = (ListView)view1.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedNews = newsList.get(position);
                String id = selectedNews.getId();
                String address = "http://news-at.zhihu.com/api/4/news/"+id;
                Intent intent = new Intent(ZhiHuActivity.this,NewsWebActivity.class);
                intent.putExtra("address",address);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });





    }

    private void initCollectedNews(){
        //加载收藏news页面所需内容
        colAdapter = new NewsAdapter(this,R.layout.news_item,listCol);
        queryCollectedNews();
        collectedNews = (ListView)view2.findViewById(R.id.collectedNews);
        collectedNews.setAdapter(colAdapter);
    }
    //初始化viewpager中所需要的title view等

    private void initDatas(){
        viewList = new ArrayList<>();
        titles = new ArrayList<>();
        //动态加载listview，一般放于容器中使用
        view1 = LayoutInflater.from(this).inflate(R.layout.zhihu_layout,null);
        view2 = LayoutInflater.from(this).inflate(R.layout.collectednews,null);
        viewList.add(view1);
        viewList.add(view2);
        titles.add("最新新闻");
        titles.add("你的收藏");
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

    //加载收藏页中的news
    private void queryCollectedNews(){
        colNews = zhihuDB.loadCollectedNews();
        if (colNews.size() > 0){
            listCol.clear();
            for (News news : colNews){
                listCol.add(news);
            }
            colAdapter.notifyDataSetChanged();
        }
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
