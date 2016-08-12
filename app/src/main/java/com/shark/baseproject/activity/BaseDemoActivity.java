package com.shark.baseproject.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.shark.base.activity.BaseActivity;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.R;
import com.shark.baseproject.webservice.worker.VolleyWebServiceWorker;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class BaseDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindNetworkStateViews();
    }

    protected void startWebServiceTask(WebServiceTask task) {
        startWebServiceTask(new VolleyWebServiceWorker(task, this));
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

    protected void bindNetworkStateViews() {
        setLoadingViewContainerId(R.id.viewLoading_container);
        setMessageViewContainerId(R.id.viewMessage_container);
        setEmptyViewContainerId(R.id.viewEmpty_container);
        setNetworkErrorViewContainerId(R.id.viewNetworkError_container);
    }

    protected void showMessageView(String message) {
        super.showMessageView();
        TextView textView = (TextView) findViewById(R.id.viewMessage_messageTextView);
        textView.setText(message);
    }

    protected void showEmptyView(String message) {
        super.showEmptyView();
        TextView textView = (TextView) findViewById(R.id.viewEmpty_messageTextView);
        textView.setText(message);
    }

    protected void showNetworkErrorView(String message) {
        super.showNetworkErrorView();
        TextView textView = (TextView) findViewById(R.id.viewNetworkError_messageTextView);
        textView.setText(message);

        findViewById(R.id.viewNetworkError_retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNetworkErrorRetryButtonClick();
            }
        });
    }

    protected void onNetworkErrorRetryButtonClick() {
        //TODO if application use service data to  layout, please override this function when first connect fail - Shark.M.Lin
    }

}
