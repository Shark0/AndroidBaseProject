package com.shark.baseproject.webservice.factory;

import android.content.Context;

import com.shark.base.BuildConfig;
import com.shark.baseproject.manager.ApplicationManager;

import java.util.HashMap;

public class HeaderFactory {

    public enum ContentType {
        Json, UrlEncoded, BinaryOctetStream, Multipart
    }

    public static HashMap<String, String> generateHeaders(ContentType contentType) {
        HashMap<String, String> headerValues = new HashMap<String, String>();
        switch (contentType) {
            case Json:
                headerValues.put("Content-Type", "application/json");
                break;
            case UrlEncoded:
                headerValues.put("Content-Type", "application/x-www-form-urlencoded");
                break;
            case BinaryOctetStream:
                headerValues.put("Content-Type", "binary/octet-stream");
                break;
            case Multipart:
                headerValues.put("Content-Type", "multipart/form-data");
                break;
        }
        headerValues.put("Local", getLocale());

        String packageName = ApplicationManager.getInstance().getContext().getPackageName();
        headerValues.put("Package_Name", packageName);

        headerValues.put("Version", getSoftwareVersion());
        return headerValues;
    }

    public static String getLocale() {
        Context context = ApplicationManager.getInstance().getContext();
        String locale = context.getResources().getConfiguration().locale.toString();
        return locale;
    }

    public static String getSoftwareVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
