package com.shark.base.webservice;

import android.os.AsyncTask;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class WebServiceWorker<R> {

    private WebServiceTask<R> task;
    private WorkListener workListener;
    private Object tag;

    private AsyncTask asyncTask;

    public WebServiceWorker(WebServiceTask<R> task, WorkListener workListener) {
        this.task = task;
        this.workListener = workListener;
    }

    public void startTask() {
        asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // use second thread to create header - Shark.M.Lin
                task.setHeader(task.generateHttpHeaders());
                task.setBody(task.generateBody());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                task.start();
                switch (task.getMethod()) {
                    case GET:
                        startHttpGetRequest(task.generateServiceUrl(), task.getHeader(),
                                task.generateResultType(), task.isDebug());
                        break;
                    case POST:
                        startHttpPostRequest(task.generateServiceUrl(),
                                task.getBody(), task.getHeader(), task.generateResultType(), task.isDebug());
                        break;
                    case PUT:
                        startHttpPutRequest(task.generateServiceUrl(),
                                task.getBody(), task.getHeader(),
                                task.generateResultType(), task.isDebug());
                        break;
                    case DELETE:
                        startHttpDeleteRequest(task.generateServiceUrl(),
                                task.getHeader(), task.generateResultType(), task.isDebug());
                        break;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void cancelTask() {
        asyncTask.cancel(true);
        cancelRequest();
    }

    protected abstract void startHttpGetRequest(String serviceUrl,
                                                Map<String, String> headerValues, Type type, boolean debug);

    protected abstract void startHttpPostRequest(String serviceUrl,
                                                 byte[] body, Map<String, String> headerValue, Type type, boolean debug);

    protected abstract void startHttpPutRequest(String serviceUrl,
                                                byte[] body, Map<String, String> headerValue, Type type, boolean debug);

    protected abstract void startHttpDeleteRequest(String serviceUrl,
                                                   Map<String, String> headerValue, Type type, boolean debug);

    protected abstract void cancelRequest();

    protected void onWorkSucceed(R result) {
        task.end();
        task.onTaskSucceed(result);
        workListener.onWorkDone(this);
    }

    protected void onWorkFailed(WebServiceErrorType errorType, String response) {
        task.onTaskFailed(errorType);
        workListener.onWorkDone(this);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }


    public static interface WorkListener {
        public void onWorkDone(WebServiceWorker<?> worker);
    }
}
