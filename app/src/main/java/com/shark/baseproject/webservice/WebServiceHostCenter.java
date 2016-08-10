package com.shark.baseproject.webservice;


import com.shark.baseproject.manager.ApplicationManager;

public class WebServiceHostCenter {
    public enum HostType {Production, Test, Develop}

    //FIXME
    private static String develop = "please_change_your_project_developer_web_service_host";
    //FIXME
    private static String test = "please_change_your_project_test_web_service_host";
    //FIXME
    private static String production = "please_change_your_project_production_web_service_host";

    public static String getServiceHost() {
        switch (ApplicationManager.hostType) {
            case Production:
                return production;
            case Test:
                return test;
            case Develop:
                return develop;
        }
        return develop;
    }
}
