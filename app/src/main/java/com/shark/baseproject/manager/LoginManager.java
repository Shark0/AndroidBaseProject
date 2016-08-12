package com.shark.baseproject.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.shark.base.util.StringUtil;
import com.shark.baseproject.webservice.task.login.entity.LoginResultEntity;

/**
 * Created by shark on 2015/3/23.
 */
public class LoginManager {

    private static LoginManager instance;

    private String SHARED_PREFERENCES_FILE_LOGIN = "SHARED_PREFERENCES_FILE_LOGIN";
    private String SHARED_PREFERENCES_STRING_LOGIN_DATA_JSON_STRING = "SHARED_PREFERENCES_STRING_LOGIN_DATA_JSON_STRING";
    private String SHARED_PREFERENCES_STRING_EMAIL = "SHARED_PREFERENCES_STRING_EMAIL";
    private String SHARED_PREFERENCES_STRING_PASSWORD = "SHARED_PREFERENCES_STRING_PASSWORD";

    private SharedPreferences loginSharedPreferences;
    private LoginResultEntity loginResult;
    private String email;
    private String password;

    private LoginManager() {
        if (loginSharedPreferences == null) {
            Context context = ApplicationManager.getInstance().getContext();
            loginSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_LOGIN, Context.MODE_PRIVATE);
        }
    }

    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    public LoginResultEntity getLoginResult() {
        if (loginResult == null) {
            String json = loginSharedPreferences.getString(SHARED_PREFERENCES_STRING_LOGIN_DATA_JSON_STRING, "");
            if (StringUtil.isEmpty(json)) {
                return loginResult;
            }
            Gson gson = new Gson();
            loginResult = gson.fromJson(json, LoginResultEntity.class);
        }
        return loginResult;
    }

    public void setLoginResult(LoginResultEntity loginResult) {
        this.loginResult = loginResult;
        if (loginResult == null) {
            loginSharedPreferences.edit().putString(SHARED_PREFERENCES_STRING_LOGIN_DATA_JSON_STRING, "").commit();
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(loginResult);
        loginSharedPreferences.edit().putString(SHARED_PREFERENCES_STRING_LOGIN_DATA_JSON_STRING, json).commit();
    }

    public String getEmail() {
        if (!StringUtil.isEmpty(email)) {
            return email;
        }
        email = loginSharedPreferences.getString(SHARED_PREFERENCES_STRING_EMAIL, "");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        SharedPreferences.Editor editor = loginSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_EMAIL, email);
        editor.commit();
    }

    public String getPassword() {
        if (!StringUtil.isEmpty(password)) {
            return password;
        }
        password = loginSharedPreferences.getString(SHARED_PREFERENCES_STRING_PASSWORD, "");
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        SharedPreferences.Editor editor = loginSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_PASSWORD, password);
        editor.commit();
    }

    public boolean isLogin() {
        return getLoginResult() != null;
    }

    public void logout() {
        setLoginResult(null);
        setPassword("");
    }
}
