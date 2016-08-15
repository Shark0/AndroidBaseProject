package com.shark.base.helper;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shark0 on 2016/7/7.
 */
public class MultiTaskCheckHelper {
    private ArrayMap<String, Object> arrayMap;

    public MultiTaskCheckHelper() {
        this.arrayMap = new ArrayMap();
    }

    public void addTaskTag(String taskTag) {
        arrayMap.put(taskTag, null);
    }

    public void addTaskData(String taskTag, Object data) {
        arrayMap.put(taskTag, data);
    }

    public boolean isAllTaskSuccess() {
        for(String taskTag: arrayMap.keySet()) {
            if(arrayMap.get(taskTag) == null) {
                return false;
            }
        }
        return true;
    }

    private Object getData(String taskTag) {
        return arrayMap.get(taskTag);
    }

    public List<String> getFailedTaskTagList() {
        List<String> failedTaskTagList = new ArrayList<>();
        for(String taskTag: arrayMap.keySet()) {
            if(arrayMap.get(taskTag) == null) {
                failedTaskTagList.add(taskTag);
            }
        }
        return failedTaskTagList;
    }

}
