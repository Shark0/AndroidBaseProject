package com.shark.baseproject.activity;

import com.shark.base.activity.BaseActivity;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.base.webservice.WebServiceTaskManager;
import com.shark.baseproject.webservice.worker.VolleyWebServiceWorker;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class BaseDemoActivity extends BaseActivity {

    protected void startWebServiceTask(WebServiceTask task) {
        startWebServiceTask(new VolleyWebServiceWorker(task, WebServiceTaskManager.getInstance()));
    };

    protected String generateNetworkErrorDescription(WebServiceErrorType errorType) {
        String description = "Service Error ";
        switch (errorType) {
            case WEB_IO_ERROR:
                description = description + "(I)";
                break;
            case JSON_ERROR:
                description = description + "(J)";
                break;
            case WEB_SERVER_ERROR:
                description = description + "(S)";
                break;
            case WEB_CLIENT_ERROR:
                description = description + "(C)";
                break;
            case UNKNOWN_ERROR:
                description = description + "(U)";
                break;
        }
        return description;
    }
}
