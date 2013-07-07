package com.ideacode.news.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class LoadingActivity extends IdeaCodeActivity {

    private HashMap params;
    private TbUser tbUser = null;
    private AppContext appContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        appContext = (AppContext) getApplication();

        if (getIntent().getSerializableExtra(CommonSetting.FileNameTag) != null) {
            tbUser = (TbUser) getIntent().getSerializableExtra(CommonSetting.FileNameTag);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void init() {
        Task ts = null;
        switch (tbUser.getTaskType()) {
            case TaskType.TS_EXAM_UPDATEUSERINFO:
                params = new HashMap();
                params.put("tbUser", tbUser);
                ts = new Task(TaskType.TS_EXAM_UPDATEUSERINFO, params);
                MainService.newTask(ts);
                break;
            case TaskType.TS_EXAM_CHECKUSER:
                params = new HashMap();
                params.put("reg_name", tbUser.getUserName());
                params.put("reg_email", tbUser.getUserEmail());
                ts = new Task(TaskType.TS_EXAM_CHECKUSER, params);
                MainService.newTask(ts);
                break;
        }
    }

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];
        switch (type) {
            case TaskType.TS_EXAM_UPDATEUSERINFO:
                if (param[2] != null && (Integer) param[2] != 0) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                    finish();
                } else {
                    int updateUser_code = (Integer) param[1];
                    if (updateUser_code == CommonSetting.Success) {
                        appContext.saveLoginInfo(tbUser);
                        setResult(TaskType.TS_EXAM_UPDATEUSERINFO_SUCCESS, null);
                        finish();
                        UIHelper.ToastMessage(this, R.string.msg_updateuserinfo_success);
                    } else {
                        finish();
                        UIHelper.ToastMessage(this, R.string.msg_updateuserinfo_failure);
                    }
                }
                break;
            case TaskType.TS_EXAM_CHECKUSER:
                if (param[2] != null && (Integer) param[2] != 0) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                    finish();
                } else {
                    int check_emailcode = (Integer) param[1];
                    if (check_emailcode == CommonSetting.Success) {
                        params = new HashMap();
                        params.put("tbUser", tbUser);
                        Task ts = new Task(TaskType.TS_EXAM_REGUSER, params);
                        MainService.newTask(ts);
                    } else {
                        Intent intent = new Intent();
                        setResult(TaskType.TS_EXAM_CHECKUSER, intent);
                        finish();
                        UIHelper.ToastMessage(this, R.string.checkEmailFail);
                    }
                }
                break;
            case TaskType.TS_EXAM_REGUSER:
                if (param[2] != null && (Integer) param[2] != 0) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                    finish();
                } else {
                    Map reg_Map = (Map) param[1];
                    int reg_code = Integer.parseInt(reg_Map.get("code").toString());
                    if (reg_code == CommonSetting.Fail) {
                        Intent intent = new Intent();
                        setResult(TaskType.TS_EXAM_REGUSER, intent);
                        finish();
                        UIHelper.ToastMessage(this, R.string.regUserFail);
                    } else {
                        appContext.saveLoginInfo(tbUser);
                        setResult(TaskType.TS_EXAM_REGUSER, null);
                        finish();
                        UIHelper.ToastMessage(this, R.string.regUserSuccess);
                    }
                }
                break;
        }
    }
}
