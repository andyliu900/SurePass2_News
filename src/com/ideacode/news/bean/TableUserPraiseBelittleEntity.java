package com.ideacode.news.bean;

public class TableUserPraiseBelittleEntity {

    public static final String _ID = "_id";
    public static final String USERID = "user_id";
    public static final String MOODID = "mood_id";
    public static final String USEROPTIONTYPE = "user_option_type";

    private String _id;
    private String userId;
    private String moodId;
    private UserOptionType userOptionType;

    public String get_id() {
        return _id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMoodId() {
        return moodId;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMoodId(String moodId) {
        this.moodId = moodId;
    }

    public UserOptionType getUserOptionType() {
        return userOptionType;
    }

    public void setUserOptionType(UserOptionType userOptionType) {
        this.userOptionType = userOptionType;
    }
}
