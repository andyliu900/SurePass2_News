package com.ideacode.news.bean;

public class TbFeedBack extends Base {

    private int feedbackId;
    private long userId;
    private String feedbackTitle;
    private String feedbackContent;
    private String feedbackLoc;
    private String Udf1;
    private String Udf2;
    private String Udf3;
    private int taskType;

    public int getFeedbackId() {
        return feedbackId;
    }

    public long getUserId() {
        return userId;
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public String getFeedbackLoc() {
        return feedbackLoc;
    }

    public String getUdf1() {
        return Udf1;
    }

    public String getUdf2() {
        return Udf2;
    }

    public String getUdf3() {
        return Udf3;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setFeedbackTitle(String feedbackTitle) {
        this.feedbackTitle = feedbackTitle;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public void setFeedbackLoc(String feedbackLoc) {
        this.feedbackLoc = feedbackLoc;
    }

    public void setUdf1(String udf1) {
        Udf1 = udf1;
    }

    public void setUdf2(String udf2) {
        Udf2 = udf2;
    }

    public void setUdf3(String udf3) {
        Udf3 = udf3;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}
