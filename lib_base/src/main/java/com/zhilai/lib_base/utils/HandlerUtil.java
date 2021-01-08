package com.zhilai.lib_base.utils;

import android.os.Handler;
import android.os.Message;

public class HandlerUtil {

    public static void sendMsg(Handler handler, int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    public static void sendMsg(Handler handler, int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
}
