package com.biginsect.myzhihu.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.biginsect.myzhihu.R;
import com.biginsect.myzhihu.util.HttpCallbackListener;
import com.biginsect.myzhihu.util.Util;
import com.biginsect.myzhihu.util.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by administration on 2016/8/20.
 */
public class NewsWebActivity extends AppCompatActivity {

    private WebView webView ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);

        webView = (WebView)findViewById(R.id.web_view);

        String address = getIntent().getStringExtra("address");
        Util.sendWebViewRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //将获取到的html代码以及css代码存储
                String[] code = Utility.handleResponseForWebview(response);
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

    //将存储的html代码取出来
    public String loadHtmlCode(){
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
}
