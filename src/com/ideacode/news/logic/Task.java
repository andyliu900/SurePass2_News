package com.ideacode.news.logic;

import java.io.Serializable;
import java.util.HashMap;

public class Task implements Serializable {

    private static final long serialVersionUID = -10544600464481L;

    private int taskID;
    private HashMap taskParam;

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public HashMap getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(HashMap taskParam) {
        this.taskParam = taskParam;
    }

    public Task(int tsid, HashMap hm) {
        this.taskID = tsid;
        this.taskParam = hm;
    }
}
