package com.shark.base.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.View;

import com.shark.base.webservice.WebServiceTaskManager;
import com.shark.base.webservice.WebServiceWorker;

/**
 * Created by Shark on 2015/3/13.
 */
public class BaseFragment extends Fragment {

    protected View contentView;

    protected Dialog loadingDialog;
    protected int contentContainerId = -1;
    protected int loadingViewContainerId = -1;
    protected int messageViewContainerId = -1;
    protected int emptyViewContainerId = -1;
    protected int networkErrorViewContainerId = -1;

    @Override
    public void onDestroyView() {
        cancelAllWebServiceTask();
        hideLoadingDialog();
        super.onDestroyView();
    }

    protected void setContentContainerId(int contentContainerId) {
        this.contentContainerId = contentContainerId;
    }

    protected void showContentContainer() {
        if(contentView == null) {
            return;
        }
        if(contentContainerId == -1) {
            return;
        }
        View contentContainerView = contentView.findViewById(contentContainerId);
        if (contentContainerView == null) {
            return;
        }
        contentContainerView.setVisibility(View.VISIBLE);
        hideLoadingView();
        hideEmptyView();
        hideNetworkErrorView();
    }

    protected void hideContentContainer() {
        if(contentView == null) {
            return;
        }
        if(contentContainerId == -1) {
            return;
        }
        View contentContainerView = contentView.findViewById(contentContainerId);
        if (contentContainerView == null) {
            return;
        }
        contentContainerView.setVisibility(View.GONE);
    }

    public void setLoadingViewContainerId(int loadingViewContainerId) {
        this.loadingViewContainerId = loadingViewContainerId;
    }

    protected void showLoadingView() {
        if(contentView == null) {
            return;
        }
        if(loadingViewContainerId == -1) {
            return;
        }
        View loadingView = contentView.findViewById(loadingViewContainerId);
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
        if(contentView == null) {
            return;
        }
        if(loadingViewContainerId == -1) {
            return;
        }
        View loadingView = contentView.findViewById(loadingViewContainerId);
        if (loadingView == null) {
            return;
        }
        loadingView.setVisibility(View.GONE);
    }

    public void setMessageViewContainerId(int messageViewContainerId) {
        this.messageViewContainerId = messageViewContainerId;
    }

    protected void showMessageView() {
        if(contentView == null) {
            return;
        }
        if(messageViewContainerId == -1) {
            return;
        }
        View messageView = contentView.findViewById(messageViewContainerId);
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
        if(contentView == null) {
            return;
        }
        if(messageViewContainerId == -1) {
            return;
        }
        View messageView = contentView.findViewById(messageViewContainerId);
        if (messageView == null) {
            return;
        }
        messageView.setVisibility(View.GONE);
    }

    public void setEmptyViewContainerId(int emptyViewContainerId) {
        this.emptyViewContainerId = emptyViewContainerId;
    }

    protected void showEmptyView() {
        if(contentView == null) {
            return;
        }
        if(emptyViewContainerId == -1) {
            return;
        }
        View emptyView = contentView.findViewById(emptyViewContainerId);
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
        if(contentView == null) {
            return;
        }
        if(emptyViewContainerId == -1) {
            return;
        }
        View emptyView = contentView.findViewById(emptyViewContainerId);
        if (emptyView == null) {
            return;
        }
        emptyView.setVisibility(View.GONE);
    }

    public void setNetworkErrorViewContainerId(int networkErrorViewContainerId) {
        this.networkErrorViewContainerId = networkErrorViewContainerId;
    }

    protected void showNetworkErrorView() {
        if(contentView == null) {
            return;
        }
        if(networkErrorViewContainerId == -1) {
            return;
        }
        View networkErrorView = contentView.findViewById(networkErrorViewContainerId);
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
        if(contentView == null) {
            return;
        }
        if(networkErrorViewContainerId == -1) {
            return;
        }
        View networkErrorView = contentView.findViewById(networkErrorViewContainerId);
        if (networkErrorView == null) {
            return;
        }
        networkErrorView.setVisibility(View.GONE);
    }

    protected void showLoadingDialog(String title, String message, boolean isCanCancel) {
        if(getActivity() == null) {
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(getActivity());
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

    public void startWebServiceTask(WebServiceWorker worker) {
        WebServiceTaskManager.getInstance().startTask(worker, this);
    }

    public void cancelAllWebServiceTask() {
        WebServiceTaskManager.getInstance().cancelTasks(this);
    }


}
