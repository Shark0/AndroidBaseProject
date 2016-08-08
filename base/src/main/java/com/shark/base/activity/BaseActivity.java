package com.shark.base.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.shark.base.webservice.WebServiceTask;
import com.shark.base.webservice.WebServiceTaskManager;
import com.shark.base.webservice.WebServiceWorker;
public class BaseActivity extends AppCompatActivity {

    protected Dialog loadingDialog;
    protected int contentContainerId = -1;
    protected int loadingViewContainerId = -1;
    protected int messageViewContainerId = -1;
    protected int emptyViewContainerId = -1;
    protected int networkErrorViewContainerId = -1;

    @Override
    protected void onDestroy() {
        cancelAllWebServiceTask();
        hideLoadingDialog();
        super.onDestroy();
    }

    protected void setContentContainerId(int contentContainerId) {
        this.contentContainerId = contentContainerId;
    }

    protected void showContentContainer() {
        if(contentContainerId == -1) {
            return;
        }
        View contentContainerView = findViewById(contentContainerId);
        if (contentContainerView == null) {
            return;
        }
        contentContainerView.setVisibility(View.VISIBLE);
        hideLoadingView();
        hideEmptyView();
        hideNetworkErrorView();
    }

    protected void hideContentContainer() {
        if(contentContainerId == -1) {
            return;
        }
        View contentContainerView = findViewById(contentContainerId);
        if (contentContainerView == null) {
            return;
        }
        contentContainerView.setVisibility(View.GONE);
    }

    public void setLoadingViewContainerId(int loadingViewContainerId) {
        this.loadingViewContainerId = loadingViewContainerId;
    }

    protected void showLoadingView() {
        if(loadingViewContainerId == -1) {
            return;
        }
        View loadingView = findViewById(loadingViewContainerId);
        if (loadingView == null) {
            return;
        }
        loadingView.setVisibility(View.VISIBLE);
        hideContentContainer();
        hideMessageView();
        hideEmptyView();
        hideNetworkErrorView();
    }

    protected void hideLoadingView() {
        if(loadingViewContainerId == -1) {
            return;
        }
        View loadingView = findViewById(loadingViewContainerId);
        if (loadingView == null) {
            return;
        }
        loadingView.setVisibility(View.GONE);
    }

    public void setMessageViewContainerId(int messageViewContainerId) {
        this.messageViewContainerId = messageViewContainerId;
    }

    protected void showMessageView() {
        if(messageViewContainerId == -1) {
            return;
        }
        View messageView = findViewById(messageViewContainerId);
        if (messageView == null) {
            return;
        }
        messageView.setVisibility(View.VISIBLE);
        hideContentContainer();
        hideLoadingView();
        hideEmptyView();
        hideNetworkErrorView();
    }

    protected void hideMessageView() {
        if(messageViewContainerId == -1) {
            return;
        }
        View messageView = findViewById(messageViewContainerId);
        if (messageView == null) {
            return;
        }
        messageView.setVisibility(View.GONE);
    }

    public void setEmptyViewContainerId(int emptyViewContainerId) {
        this.emptyViewContainerId = emptyViewContainerId;
    }

    protected void showEmptyView() {
        if(emptyViewContainerId == -1) {
            return;
        }
        View emptyView = findViewById(emptyViewContainerId);
        if (emptyView == null) {
            return;
        }
        emptyView.setVisibility(View.VISIBLE);
        hideContentContainer();
        hideLoadingView();
        hideMessageView();
        hideNetworkErrorView();
    }

    protected void hideEmptyView() {
        if(emptyViewContainerId == -1) {
            return;
        }
        View emptyView = findViewById(emptyViewContainerId);
        if (emptyView == null) {
            return;
        }
        emptyView.setVisibility(View.GONE);
    }

    public void setNetworkErrorViewContainerId(int networkErrorViewContainerId) {
        this.networkErrorViewContainerId = networkErrorViewContainerId;
    }

    protected void showNetworkErrorView() {
        if(networkErrorViewContainerId == -1) {
            return;
        }
        View networkErrorView = findViewById(networkErrorViewContainerId);
        if (networkErrorView == null) {
            return;
        }
        networkErrorView.setVisibility(View.VISIBLE);
        hideContentContainer();
        hideLoadingView();
        hideMessageView();
        hideEmptyView();
    }

    protected void hideNetworkErrorView() {
        if(networkErrorViewContainerId == -1) {
            return;
        }
        View networkErrorView = findViewById(networkErrorViewContainerId);
        if (networkErrorView == null) {
            return;
        }
        networkErrorView.setVisibility(View.GONE);
    }

    protected void showLoadingDialog(String title, String message, boolean isCanCancel) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
        }
        loadingDialog.setTitle(title);
        ((ProgressDialog) loadingDialog).setMessage(message);
        loadingDialog.setCancelable(isCanCancel);
        loadingDialog.setCanceledOnTouchOutside(isCanCancel);
        loadingDialog.show();
    }

    protected void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    protected void hideKeyboardView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void startWebServiceTask(WebServiceWorker worker, WebServiceTask task) {
        WebServiceTaskManager.getInstance().startTask(worker, task, this);
    }

    public void cancelAllWebServiceTask() {
        WebServiceTaskManager.getInstance().cancelTasks(this);
    }
}