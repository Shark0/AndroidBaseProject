package com.shark.baseproject.application;

import android.app.Application;

import com.shark.baseproject.manager.ApplicationManager;

/**
 * Created by Shark on 2015/3/13.
 */
public class ProjectApplication extends Application{

    public ProjectApplication() {
        ApplicationManager.getInstance().setContext(this);
    }
}
