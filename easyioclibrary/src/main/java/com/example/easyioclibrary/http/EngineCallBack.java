package com.example.easyioclibrary.http;

import android.content.Context;

import java.util.Map;

/**
 * Created by asus on 2017/6/29.
 */
public interface EngineCallBack {

    void onPreExcute(Context context, Map<String,Object>);

    void onError(Exception e);


    void onSuccess(Object result);

    //默认执行
    public final EngineCallBack DefaultCallback = new EngineCallBack() {
        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onSuccess(Object result) {

        }
    };

}
