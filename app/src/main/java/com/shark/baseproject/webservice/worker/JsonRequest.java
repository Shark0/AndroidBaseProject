package com.shark.baseproject.webservice.worker;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shark.base.util.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class JsonRequest<T> extends Request<T> {

    private Gson gson = new Gson();
    private Type resultType;
    private Listener<T> listener;
    private Map<String, String> headers;
    private byte[] body;
    private boolean debug;

    public JsonRequest(int method, String url, Type resultType,
                       Listener<T> listener, ErrorListener errorListener,
                       Map<String, String> headers, boolean debug) {
        super(method, url, errorListener);
        this.resultType = resultType;
        this.headers = headers;
        this.listener = listener;
        this.debug = debug;
        setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if(debug) {
            Log.e("JsonRequest", "url: " + url);
        }
    }

    public JsonRequest(int method, String url, byte[] body, Type type,
                       Listener<T> listener, ErrorListener errorListener,
                       Map<String, String> headers, boolean debug) {

        this(method, url, type, listener, errorListener, headers, debug);
        this.body = body;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers != null && headers.size() > 0) {
            if(debug) {
                for(String key: headers.keySet()) {
                    Log.e("getHeaders", "key: " + key + ", value: " + headers.get(key));
                }
            }
            return headers;
        } else {
            return super.getHeaders();
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if(debug) {
            Log.e("parseNetworkResponse", "response status code: " + response.statusCode);
        }
        try {
            String json = convertResponseToString(response);
            if(debug) {
                Log.e("parseNetworkResponse", "json: " + json);
            }
            if(resultType == String.class) {
                return Response.success((T) json, HttpHeaderParser.parseCacheHeaders(response));
            }

            if(StringUtil.isEmpty(json)) {
                return Response.error(new JsonRequestException(response, new JsonSyntaxException("Response Json Empty")));
            }

            return Response.success((T) gson.fromJson(json, resultType),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new JsonRequestException(response, e));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new JsonRequestException(response, e));
        }
    }

    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if(debug) {
            Log.e("parseNetworkError", "response status code: " + volleyError.toString());
            try {
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    String error = convertResponseToString(volleyError.networkResponse);
                    Log.e("parseNetworkError", "error: " + error);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.parseNetworkError(volleyError);
    }

    private String convertResponseToString(NetworkResponse response)
            throws IOException {
        String encoding = response.headers.get("Content-Encoding");
        if (encoding == null) {
            return new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        }
        BufferedReader reader = null;
        try {
            reader = generateBufferedReader(new ByteArrayInputStream(
                    response.data), encoding);
            return readBufferedString(reader);
        } finally {
            reader.close();
        }
    }

    private String readBufferedString(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String stringHolder;
        while ((stringHolder = reader.readLine()) != null) {
            stringBuilder.append(stringHolder);
        }
        return stringBuilder.toString();
    }

    private BufferedReader generateBufferedReader(InputStream inputStream,
                                                  String encodingType) throws IOException {
        if (encodingType.equals("gzip")) {
            return new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(inputStream)));
        }
        if (encodingType.equals("deflate")) {
            return new BufferedReader(new InputStreamReader(
                    new InflaterInputStream(inputStream)));
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public String getBodyContentType() {
        return headers.get("Content-Type");
    }

    @Override
    public byte[] getBody() {
        return body;
    }
}