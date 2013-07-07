package com.ideacode.news.bean;

public class Mood extends Entity {

    private int moodId;
    private long userId;
	private String userName;
	private String moodContent;
	private int moodPraiseCount;
	private int moodBelittleCount;
	private String moodLocation;
	private String moodCreateDate;
    private boolean heatFlag;


    public Mood() {

	}

    public int getMoodId() {
        return moodId;
    }

    public void setMoodId(int moodId) {
        this.moodId = moodId;
    }

	public long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getMoodContent() {
		return moodContent;
	}

	public int getMoodPraiseCount() {
		return moodPraiseCount;
	}

	public int getMoodBelittleCount() {
		return moodBelittleCount;
	}

	public String getMoodLocation() {
		return moodLocation;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setMoodContent(String moodContent) {
		this.moodContent = moodContent;
	}

	public void setMoodPraiseCount(int moodPraiseCount) {
		this.moodPraiseCount = moodPraiseCount;
	}

	public void setMoodBelittleCount(int moodBelittleCount) {
		this.moodBelittleCount = moodBelittleCount;
	}

	public void setMoodLocation(String moodLocation) {
		this.moodLocation = moodLocation;
	}

	public String getMoodCreateDate() {
		return moodCreateDate;
	}

	public void setMoodCreateDate(String moodCreateDate) {
		this.moodCreateDate = moodCreateDate;
	}

    public boolean isHeatFlag() {
        return heatFlag;
    }

    public void setHeatFlag(boolean heatFlag) {
        this.heatFlag = heatFlag;
    }
}
