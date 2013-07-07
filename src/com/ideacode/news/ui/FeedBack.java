package com.ideacode.news.ui;

import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.TbFeedBack;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class FeedBack extends IdeaCodeActivity {

    private ImageButton mBack;
    private ImageButton mSend;
    private ProgressBar mRefresh;
    private EditText mContent;
    private Button mPlace;
    private TextView mCount;

    private AppContext application;
    private LocationClient mClient;
    private LocationClientOption mOption;

    private boolean mLBSIsReceiver;
    private String mLBSAddress;
    private Drawable mPoi_off_icon;
    private Drawable mPoi_on_icon;

    private String moodAddPoint;
    private String moodPointing;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        application = (AppContext) this.getApplicationContext();

        initLBS();
        initViews();
        setListener();

        mPoi_off_icon = getResources().getDrawable(R.drawable.addmood_poi_icon);
        mPoi_off_icon.setBounds(0, 0, mPoi_off_icon.getMinimumWidth(), mPoi_off_icon.getMinimumHeight());
        mPoi_on_icon = getResources().getDrawable(R.drawable.addmood_poiactive_icon);
        mPoi_on_icon.setBounds(0, 0, mPoi_on_icon.getMinimumWidth(), mPoi_on_icon.getMinimumHeight());

        mClient.start();
        mLBSIsReceiver = true;
        mClient.requestLocation();
    }

    private void initLBS() {
        mOption = new LocationClientOption();
        mOption.setOpenGps(true);
        mOption.setCoorType("bd09ll");
        mOption.setAddrType("all");
        mOption.setScanSpan(100);
        mClient = new LocationClient(getApplicationContext(), mOption);
    }

    private void initViews() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        moodAddPoint = FeedBack.this.getResources().getString(R.string.mood_add_point);
        moodPointing = FeedBack.this.getResources().getString(R.string.mood_pointting);

        mBack = (ImageButton) findViewById(R.id.main_head_back_button);
        mSend = (ImageButton) findViewById(R.id.main_head_send_button);
        mRefresh = (ProgressBar) findViewById(R.id.main_head_progress);
        mContent = (EditText) findViewById(R.id.addmood_content);
        mPlace = (Button) findViewById(R.id.addmood_poi_place);
        mCount = (TextView) findViewById(R.id.addmood_count);
    }

    private void setListener() {
        mBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        mSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContent.getText().toString().trim().length() == 0) {
                    UIHelper.ToastMessage(FeedBack.this, R.string.mood_send_empty);
                } else {
                    if (mContent.getText().length() > 150) {
                        UIHelper.ToastMessage(FeedBack.this, R.string.feedback_send_toolong_error);
                    } else {
                        TbFeedBack tbFeedback = new TbFeedBack();
                        tbFeedback.setUserId(application.isLogin() ? application.getLoginUid() : -1);
                        tbFeedback.setFeedbackTitle(FeedBack.this.getResources().getString(R.string.feedback_def_title));
                        tbFeedback.setFeedbackContent(mContent.getText().toString());
                        if (mPlace.getText().toString().equals(moodAddPoint) || mPlace.getText().toString().equals(moodPointing)) {
                            tbFeedback.setFeedbackLoc("");
                        } else {
                            tbFeedback.setFeedbackLoc(mPlace.getText().toString());
                        }
                        tbFeedback.setTaskType(TaskType.TS_EXAM_SENDFEEDBACK);

                        HashMap params = new HashMap();
                        params.put("feedback", tbFeedback);
                        Task ts = new Task(TaskType.TS_EXAM_SENDFEEDBACK, params);
                        MainService.newTask(ts);

                        mSend.setVisibility(ImageButton.GONE);
                        mRefresh.setVisibility(ImageButton.VISIBLE);
                    }
                }
            }
        });

        mContent.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.length();
                mCount.setText(String.valueOf(number));
                selectionStart = mContent.getSelectionStart();
                selectionEnd = mCount.getSelectionEnd();
                if (temp.length() > 150) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    mContent.setText(s);
                    mContent.setSelection(tempSelection);
                }
            }
        });

        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // œ‘ æ»Ìº¸≈Ã
                imm.showSoftInput(mContent, 0);
            }
        });

        mPlace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mPlace.getText().toString().equals(moodAddPoint)) {
                    mLBSIsReceiver = true;
                    mPlace.setText(R.string.mood_pointting);
                    if (!mClient.isStarted()) {
                        mClient.start();
                    }
                    mClient.requestLocation();
                } else if (mPlace.getText().toString().equals(moodPointing)) {
                    if (mClient.isStarted()) {
                        mClient.stop();
                        mLBSIsReceiver = false;
                        mLBSAddress = null;
                        mPlace.setCompoundDrawables(mPoi_off_icon, null, null, null);
                        mPlace.setText(R.string.mood_add_point);
                    }
                }
            }
        });

        mClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceivePoi(BDLocation arg0) {

            }

            @Override
            public void onReceiveLocation(BDLocation arg0) {
                mLBSAddress = arg0.getAddrStr();
                application.mLocation = arg0.getAddrStr();
                application.mLatitude = arg0.getLatitude();
                application.mLongitude = arg0.getLongitude();
                handler.sendEmptyMessage(1);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mLBSAddress != null) {
                        mPlace.setText(mLBSAddress);
                        mPlace.setCompoundDrawables(mPoi_on_icon, null, null, null);
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];

        mSend.setVisibility(ImageButton.VISIBLE);
        mRefresh.setVisibility(ImageButton.GONE);
        switch (type) {
            case TaskType.TS_EXAM_SENDFEEDBACK:
                if (param[2] != null && (Integer) param[2] != 0) {
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                } else {
                    int code = (Integer) param[1];
                    if (code == CommonSetting.Success) {
                        finish();
                        UIHelper.ToastMessage(this, R.string.msg_sendfeedback_success);
                    } else {
                        UIHelper.ToastMessage(this, R.string.msg_sendfeedback_error);
                    }
                }
                break;
        }
    }

}
