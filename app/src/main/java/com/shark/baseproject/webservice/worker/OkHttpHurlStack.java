package com.shark.baseproject.webservice.worker;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by laphy on 2015/10/22.
 */
public class OkHttpHurlStack extends HurlStack {

    private final OkUrlFactory factory;

    public OkHttpHurlStack() {
        this(new OkHttpClient());
    }

    public OkHttpHurlStack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client can not be null.");
        }
        factory = new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return factory.open(url);
    }
}
