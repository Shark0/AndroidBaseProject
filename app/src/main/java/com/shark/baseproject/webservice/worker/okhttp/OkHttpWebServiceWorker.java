package com.shark.baseproject.webservice.worker.okhttp;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shark.base.util.StringUtil;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.base.webservice.WebServiceWorker;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Shark0 on 2016/12/16.
 */

public class OkHttpWebServiceWorker extends WebServiceWorker implements Callback {

    private final String TAG = "OkHttpWorker";

    private Handler handler;
    private OkHttpClient okHttpClient;
    private Request request;
    private Call call;

    private Type resultType;
    private boolean debug;

    public OkHttpWebServiceWorker(WebServiceTask task, WorkListener workListener) {
        super(task, workListener);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    }

    @Override
    protected void startHttpGetRequest(String serviceUrl, Map headers, Type type, boolean debug) {
        this.debug = debug;
        this.resultType = type;
        Request.Builder builder = new Request.Builder().url(serviceUrl);
        //header
        if(headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            for (Object key: headers.keySet()) {
                if(debug) {
                    Log.e(TAG, "header key: " + key + ", value: " + headers.get(key));
                }
                builder.addHeader((String) key, (String) headers.get(key));
            }
        }
        request = builder.get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    protected void startHttpPostRequest(String serviceUrl, byte[] body, Map headers, Type type, boolean debug) {
        this.debug = debug;
        this.resultType = type;
        Request.Builder builder = new Request.Builder().url(serviceUrl);
        //header
        if(headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            for (Object key: headers.keySet()) {
                if(debug) {
                    Log.e("OkHttpWorker", "header key: " + key + ", value: " + headers.get(key));
                }
                builder.addHeader((String) key, (String) headers.get(key));
            }
        }
        //body
        //TODO
        builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), body));
        request = builder.build();
        call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    protected void startHttpDeleteRequest(String serviceUrl, Map headers, Type type, boolean debug) {
        this.debug = debug;
        this.resultType = type;
        Request.Builder builder = new Request.Builder().url(serviceUrl);
        //header
        if(headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            for (Object key: headers.keySet()) {
                if(debug) {
                    Log.e("OkHttpWorker", "header key: " + key + ", value: " + headers.get(key));
                }
                builder.addHeader((String) key, (String) headers.get(key));
            }
        }

        request = builder.delete().build();
        call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    protected void startHttpPutRequest(String serviceUrl, byte[] body, Map headers, Type type, boolean debug) {
        this.debug = debug;
        this.resultType = type;
        Request.Builder builder = new Request.Builder().url(serviceUrl);
        //header
        if(headers != null && headers.keySet() != null && headers.keySet().size() > 0) {
            for (Object key: headers.keySet()) {
                if(debug) {
                    Log.e("OkHttpWorker", "header key: " + key + ", value: " + headers.get(key));
                }
                builder.addHeader((String) key, (String) headers.get(key));
            }
        }
        //body
        //TODO
        builder.put(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), body));
        request = builder.build();
        call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    protected void cancelRequest() {
        if(call == null) {
            return;
        }
        call.cancel();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if(debug) {
            Log.e(TAG, "onFailure");
        }
        e.printStackTrace();
        callBackWorkFailed(WebServiceErrorType.WEB_CLIENT_ERROR, e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int responseCode = response.code();
        if(debug) {
            Log.e(TAG, "onResponse code: " + responseCode);
        }
        if(response == null || response.body() == null || responseCode < 200 || responseCode > 300 ) {
            callBackWorkFailed(WebServiceErrorType.WEB_SERVER_ERROR, "");
            return;
        }

        try {
            final String json = response.body().string();
            if(debug) {
                Log.e(TAG, "onResponse json: " + json);
            }
            if(resultType == String.class) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onWorkSucceed(json);
                    }
                });
            }
            if(StringUtil.isEmpty(json)) {
                callBackWorkFailed(WebServiceErrorType.JSON_ERROR, "Json Empty");
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    try {
                        onWorkSucceed(gson.fromJson(json, resultType));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        onWorkFailed(WebServiceErrorType.JSON_ERROR, e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackWorkFailed(WebServiceErrorType.UNKNOWN_ERROR, e.getMessage());
        }
    }


    private void callBackWorkFailed(final WebServiceErrorType errorType, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onWorkFailed(errorType, message);
            }
        });
    }
}
