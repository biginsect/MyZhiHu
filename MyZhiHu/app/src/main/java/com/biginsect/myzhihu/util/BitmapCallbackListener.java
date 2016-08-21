package com.biginsect.myzhihu.util;

import android.graphics.Bitmap;

/**
 * Created by administration on 2016/8/20.
 */
public interface BitmapCallbackListener {
    void onFinish(Bitmap bitmap);
    void onError(Exception e);
}
