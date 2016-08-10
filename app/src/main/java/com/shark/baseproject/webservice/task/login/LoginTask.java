package com.shark.baseproject.webservice.task.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shark.base.webservice.HttpMethod;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.webservice.ResponseDataEntity;
import com.shark.baseproject.webservice.WebServiceHostCenter;
import com.shark.baseproject.webservice.factory.HeaderFactory;
import com.shark.baseproject.webservice.task.login.entity.LoginInputEntity;
import com.shark.baseproject.webservice.task.login.entity.LoginResultEntity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class LoginTask extends WebServiceTask<ResponseDataEntity<LoginResultEntity>> {

    private LoginResponseListener listener;
    private LoginInputEntity loginInput;
    private Gson gson = new Gson();

    public LoginTask(LoginResponseListener listener, LoginInputEntity loginInput) {
        this.listener = listener;
        this.loginInput = loginInput;
        setDebug(true);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String generateServiceUrl() {
        return WebServiceHostCenter.getServiceHost() + "your_login_api_path";
    }

    @Override
    public Map<String, String> generateHttpHeaders() {
        return HeaderFactory.generateHeaders(HeaderFactory.ContentType.Json);
    }

    @Override
    public byte[] generateBody() {
        String json = gson.toJson(loginInput);
        byte[] bytes = null;
        try {
            bytes = json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public Type generateResultType() {
        return new TypeToken<ResponseDataEntity<LoginResultEntity>>(){}.getType();
    }

    @Override
    public void onTaskSucceed(ResponseDataEntity<LoginResultEntity> entity) {
        if(entity.getResultCode() == 0) {
            listener.onLoginResponseSuccess(entity.getData());
        } else {
            listener.onLoginResponseError(entity.getResultCode(), entity.getDescription());
        }
    }

    @Override
    public void onTaskFailed(WebServiceErrorType errorType) {
        listener.onLoginNetworkError(errorType);
    }

    public interface LoginResponseListener {
        void onLoginResponseSuccess(LoginResultEntity result);

        void onLoginResponseError(int resultCode, String description);

        void onLoginNetworkError(WebServiceErrorType errorType);
    }
}
