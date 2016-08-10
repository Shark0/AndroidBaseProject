package com.shark.baseproject.webservice.worker;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.shark.baseproject.manager.ApplicationManager;

public class VolleyRequestQueueManager {

    private static VolleyRequestQueueManager instance;

    private RequestQueue requestQueue;

    private VolleyRequestQueueManager() {
        requestQueue = Volley.newRequestQueue(ApplicationManager.getInstance().getContext(), new OkHttpHurlStack());
    }

    public static VolleyRequestQueueManager getInstance() {
        if (instance == null) {
            instance = new VolleyRequestQueueManager();
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
