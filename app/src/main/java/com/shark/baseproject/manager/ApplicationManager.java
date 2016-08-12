package com.shark.baseproject.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.shark.base.util.StringUtil;
import com.shark.baseproject.webservice.WebServiceHost;

/**
 * Created by Shark0 on 2015/8/4.
 */
public class ApplicationManager {
    private static ApplicationManager instance;

    public static boolean debug = false;
    public static WebServiceHost.HostType hostType = WebServiceHost.HostType.Develop;

    private String SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION = "SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION";
    private String SHARED_PREFERENCES_APPLICATION_STRING_GCM_REGISTER_ID = "SHARED_PREFERENCES_APPLICATION_STRING_GCM_REGISTER_ID";
    private String SHARED_PREFERENCES_APPLICATION_STRING_UNIQUE_ID = "SHARED_PREFERENCES_APPLICATION_STRING_UNIQUE_ID";

    private SharedPreferences applicationSharedPreferences;

    private Context context;
    private String gcmRegisterId;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getGcmRegisterId() {
        if (!StringUtil.isEmpty(gcmRegisterId)) {
            return gcmRegisterId;
        }
        if (applicationSharedPreferences == null) {
            applicationSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION, Context.MODE_PRIVATE);
        }
        gcmRegisterId = applicationSharedPreferences.getString(SHARED_PREFERENCES_APPLICATION_STRING_GCM_REGISTER_ID, "");
        return gcmRegisterId;
    }

    public void setGcmRegisterId(String gcmRegisterId) {
        this.gcmRegisterId = gcmRegisterId;
        if (applicationSharedPreferences == null) {
            applicationSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = applicationSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_APPLICATION_STRING_GCM_REGISTER_ID, gcmRegisterId);
        editor.commit();
    }

    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    public String getUniqueId() {
        if (applicationSharedPreferences == null) {
            applicationSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION, Context.MODE_PRIVATE);
        }
        String uniqueId = applicationSharedPreferences.getString(SHARED_PREFERENCES_APPLICATION_STRING_UNIQUE_ID, "");
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        if (applicationSharedPreferences == null) {
            applicationSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_APPLICATION_FILE_APPLICATION, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = applicationSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_APPLICATION_STRING_UNIQUE_ID, uniqueId);
        editor.commit();
    }
}
