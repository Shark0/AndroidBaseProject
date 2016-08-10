package com.shark.baseproject.webservice.factory;

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
                headerValues.put("content-type", "application/json");
                break;
            case UrlEncoded:
                headerValues.put("Content-Type", "application/x-www-form-urlencoded");
                headerValues.put("content-type", "application/x-www-form-urlencoded"); // samsung s3 - Shark.M.Lin
                break;
            case BinaryOctetStream:
                headerValues.put("Content-Type", "binary/octet-stream");
                headerValues.put("content-type", "binary/octet-stream");
                break;
            case Multipart:
                headerValues.put("Content-Type", "multipart/form-data");
                headerValues.put("content-type", "multipart/form-data");
                break;
        }
        return headerValues;
    }
}
