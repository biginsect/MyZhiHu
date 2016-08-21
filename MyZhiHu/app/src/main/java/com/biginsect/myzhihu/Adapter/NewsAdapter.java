package com.biginsect.myzhihu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biginsect.myzhihu.R;
import com.biginsect.myzhihu.model.News;
import com.biginsect.myzhihu.util.BitmapCallbackListener;
import com.biginsect.myzhihu.util.Util;

import java.util.List;

/**
 * Created by administration on 2016/8/18.
 */
public class NewsAdapter extends ArrayAdapter<News> {
    private int resourceId;
    private Context context;

    public NewsAdapter(Context context, int textViewResourceId, List<News> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView)view.findViewById(R.id.news_title);
            viewHolder.newsImage = (ImageView)view.findViewById(R.id.news_image);
            view.setTag(viewHolder);//
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.newsTitle.setText(news.getTitle());
        //设置ImageView
        //根据获取到的bitmapurl获取bitmap
        Util.sendBitmapRequest(news.getBitmapUrl(), new BitmapCallbackListener() {
            @Override
            public void onFinish(final Bitmap response) {
                Activity activity = (Activity)context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.newsImage.setImageBitmap(response);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    class ViewHolder{
        ImageView newsImage;
        TextView newsTitle;
    }

}
