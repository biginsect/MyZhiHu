package com.biginsect.myzhihu.util;

/**
 * Created by administration on 2016/8/18.
 */
public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
