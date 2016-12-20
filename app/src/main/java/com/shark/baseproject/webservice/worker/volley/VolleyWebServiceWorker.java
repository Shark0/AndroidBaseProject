package com.shark.baseproject.webservice.worker.volley;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonSyntaxException;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.base.webservice.WebServiceWorker;
import com.shark.baseproject.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class VolleyWebServiceWorker extends WebServiceWorker implements Response.ErrorListener, Response.Listener<R> {

    private RequestQueue requestQueue;

    public VolleyWebServiceWorker(WebServiceTask task, WorkListener workListener) {
        super(task, workListener);
        requestQueue = VolleyRequestQueueManager.getInstance().getRequestQueue();
    }

    @Override
    protected void startHttpGetRequest(String serviceUrl, Map headers, Type type, boolean debug) {
        JsonRequest request = new JsonRequest(Request.Method.GET, serviceUrl, type, this, this, headers, debug);
        request.setTag(this);
        requestQueue.add(request);
    }

    @Override
    protected void startHttpPostRequest(String serviceUrl, byte[] body, Map headers, Type type, boolean debug) {
        JsonRequest request = new JsonRequest(Request.Method.POST, serviceUrl, body, type, this, this, headers, debug);
        request.setTag(this);
        requestQueue.add(request);
    }

    @Override
    protected void startHttpPutRequest(String serviceUrl, byte[] body, Map headers, Type type, boolean debug) {
        JsonRequest request = new JsonRequest(Request.Method.PUT, serviceUrl, body, type, this, this, headers, debug);
        request.setTag(this);
        requestQueue.add(request);
    }

    @Override
    protected void startHttpDeleteRequest(String serviceUrl, Map headers, Type type, boolean debug) {
        JsonRequest request = new JsonRequest(Request.Method.DELETE, serviceUrl, type, this, this, headers, debug);
        request.setTag(this);
        requestQueue.add(request);
    }

    @Override
    protected void cancelRequest() {
        requestQueue.cancelAll(this);
    }

    @Override
    public void onResponse(R response) {
        if (response != null) {
            onWorkSucceed(response);
        } else {
            onWorkFailed(WebServiceErrorType.WEB_SERVER_ERROR, "");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.getClass() == NoConnectionError.class) {
            onWorkFailed(WebServiceErrorType.WEB_CLIENT_ERROR, "");
            return;
        }

        if (error.networkResponse == null) {
            onWorkFailed(WebServiceErrorType.WEB_SERVER_ERROR, "");
            return;
        }

        String response = null;
        if (error.networkResponse.data != null) {
            try {
                response = new String(error.networkResponse.data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (error instanceof JsonRequestException) {
            JsonRequestException e = (JsonRequestException) error;
            if (e.getCause().getClass() == JsonSyntaxException.class
                    || e.getCause().getClass() == JsonRequestException.class) {
                onWorkFailed(WebServiceErrorType.JSON_ERROR, response);
                return;
            }
            if (e.getCause().getClass() == IOException.class) {
                onWorkFailed(WebServiceErrorType.WEB_IO_ERROR, response);
                return;
            }
        }
        onWorkFailed(WebServiceErrorType.UNKNOWN_ERROR, response);
    }
}
