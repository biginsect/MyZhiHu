package com.biginsect.myzhihu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.biginsect.myzhihu.db.ZhihuDB;
import com.biginsect.myzhihu.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by administration on 2016/8/19.
 */
public class Utility {

    //解析服务器返回的JSON数据，解析出其中的html代码以及css代码
    public synchronized static String[]  handleResponseForWebview(String response){
        String[] code = new String[2];
        try{
            JSONObject jsonObject = new JSONObject(response);
            code[0] = jsonObject.getString("body");
            code[1] = jsonObject.getString("css");
        }catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }
    //解析服务器返回的JSON数据，将解析出来的数据存储到本地
    public synchronized static boolean handleNewsResponse(ZhihuDB zhihuDB,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String date = jsonObject.getString("date");
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            String[] stories = new String[jsonArray.length()];
            for (int i = 0;i < stories.length; i++ ){
                stories[i] = jsonArray.getString(i);//遍历
            }
            //每一个字符串数组的元素都是JSON
            handelJSONArray(zhihuDB,date,stories);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public  synchronized static void handelJSONArray(ZhihuDB zhihuDB,String date,String[] arr){
        int i = 0;
        String newsDate = date;
        while(i != arr.length){
            try {
                News news = new News();
                JSONObject jsonObject = new JSONObject(arr[i]);
                JSONArray jsonArray = jsonObject.getJSONArray("images");
                String newsBitmapUrl = jsonArray.get(0).toString();
                String newsId = jsonObject.getString("id");
                String newsTitle = jsonObject.getString("title");
                news.setDate(newsDate);
                news.setTitle(newsTitle);
                news.setId(newsId);
                String tempUrl = newsBitmapUrl.replace("\"","");
                news.setBitmapUrl(tempUrl);
                zhihuDB.saveNews(news);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            i++;
        }
    }

//    public static Bitmap handelBitmapUrl(String newsBitmapUrl){
//        Bitmap bitmap ;
//        try {
//            URL url = new URL(newsBitmapUrl);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setDoInput(true);
//            InputStream in = connection.getInputStream();
//            bitmap = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            e.printStackTrace();
//            bitmap =  null;
//        }
//
//        return bitmap;
//    }

}
