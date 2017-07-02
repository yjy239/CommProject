package com.example.easyioclibrary.http;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by asus on 2017/6/29.
 * 默认网络引擎
 */
public class OkHttpEngine implements IHttpEngine {

    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public void get(Context context, String url, Map<String, Object> parms, final EngineCallBack callBack) {
        final String joinUrl = HttpUtils.joinParms(url,parms);
//        if(callBack!=null){
//            callBack.onPreExcute(joinUrl);
//        }

        Log.e("http->get",joinUrl);
        RequestBody body = appendBody(parms);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(context);
        builder.method("GET",null);
        Request request = builder.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onSuccess(response.body().string());
            }
        });


    }

    private RequestBody appendBody(Map<String, Object> parms) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder,parms);
        return builder.build();
    }

    private void addParams(MultipartBody.Builder builder, Map<String, Object> parms) {
        if(parms!=null && !parms.isEmpty()){
            for(String key : parms.keySet()){
                builder.addFormDataPart(key,parms.get(key)+"");
                Object value = parms.get(key);
                if(value instanceof File){
                    File file = (File) value;
                    builder.addFormDataPart(key,file.getName(),RequestBody
                            .create(MediaType.parse(guessMineType(file.getAbsolutePath())),file));
                }else if(value instanceof List){
                    //提交是一个集合
                    try {
                        List<File> listfile = (List<File>)value;
                        //循环获取文件列表
                        for(int i = 0;i<listfile.size();i++){
                            File file = listfile.get(i);
                            builder.addFormDataPart(key,file.getName(),RequestBody
                                    .create(MediaType.parse(guessMineType(file.getAbsolutePath())),file));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    builder.addFormDataPart(key,value+"");
                }
            }
        }
    }

    private String guessMineType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if(contentTypeFor ==null){
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    @Override
    public void post(Context context,String url, Map<String, Object> parms, final EngineCallBack callBack) {
        final String joinUrl = HttpUtils.joinParms(url,parms);
//        if(callBack!=null){
//            callBack.onPreExcute(joinUrl);
//        }

        Log.e("http->post",joinUrl);

        RequestBody body = appendBody(parms);
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(body)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onSuccess(response.body().string());
            }
        });
    }
}
