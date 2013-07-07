package com.ideacode.news.ui;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ideacode.news.R;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.MD5;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.TaskType;

public class RegUserActivity extends IdeaCodeActivity {

    private static final String TAG = "RegUserActivity";

    private ImageButton back_bt, save_bt;
    private EditText reg_user_edit, reg_email_edit, reg_birthday_edit, reg_password_edit, reg_password2_edit;
    private RadioGroup group;
    private String sex = "男";

    private Calendar c = null;
    private final int DATE_PICK_DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg);

        initViews();
    }

    private void initViews() {
        back_bt = (ImageButton) this.findViewById(R.id.main_head_back_button);
        back_bt.setOnClickListener(buttonClickListener);
        save_bt = (ImageButton) this.findViewById(R.id.main_head_save_button);
        save_bt.setOnClickListener(buttonClickListener);

        reg_user_edit = (EditText) this.findViewById(R.id.reg_user_edit);
        reg_email_edit = (EditText) this.findViewById(R.id.reg_email_edit);
        reg_birthday_edit = (EditText) this.findViewById(R.id.reg_birthday_edit);
        reg_birthday_edit.setOnTouchListener(touchListener);
        reg_password_edit = (EditText) this.findViewById(R.id.reg_password_edit);
        reg_password2_edit = (EditText) this.findViewById(R.id.reg_password2_edit);

        group = (RadioGroup) this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(groupChangeListener);
    }

    // RadioGroup监听器
    private final RadioGroup.OnCheckedChangeListener groupChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 获取变更后的选中项的ID
            int radioButtonId = group.getCheckedRadioButtonId();
            // 根据ID获取RadioButton的实例
            RadioButton rb = (RadioButton) RegUserActivity.this.findViewById(radioButtonId);
            sex = rb.getText().toString();
            Log.i(TAG, sex);
        }
    };

    private final Button.OnClickListener buttonClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(RegUserActivity.this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            switch (view.getId()) {
                case R.id.main_head_back_button:
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    break;
                case R.id.main_head_save_button:
                    if ("".equals(reg_user_edit.getText().toString()) || "".equals(reg_email_edit.getText().toString())
                            || "".equals(reg_birthday_edit.getText().toString()) || "".equals(reg_password_edit.getText().toString())
                            || "".equals(reg_password2_edit.getText().toString())) {
                        UIHelper.ToastMessage(RegUserActivity.this, R.string.regEmptyError);
                        return;
                    }
                    if (StringUtils.checkUsernameInput(reg_user_edit.getText().toString())) {
                        if (StringUtils.checkEmailInput(reg_email_edit.getText().toString())) {
                            if (StringUtils.check2Password(reg_password_edit.getText().toString(), reg_password2_edit.getText().toString())) {
                                TbUser tbUser = new TbUser();
                                tbUser.setUserId(Long.parseLong(StringUtils.date2UserId()));
                                tbUser.setUserBirthday(StringUtils.toDate2(reg_birthday_edit.getText().toString()));
                                tbUser.setUserName(reg_user_edit.getText().toString());
                                tbUser.setUserEmail(reg_email_edit.getText().toString());
                                tbUser.setUserSex(sex);
                                tbUser.setUserPassword(MD5.MD5Encode(reg_password2_edit.getText().toString()));// 通过MD5加密
                                tbUser.setUserLogintime(StringUtils.genCurrentDate());
                                tbUser.setTaskType(TaskType.TS_EXAM_CHECKUSER);
                                tbUser.setLocation("");
                                tbUser.setRememberMe(true);

                                Intent intent = new Intent();
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable(CommonSetting.FileNameTag, tbUser);
                                intent.putExtras(mBundle);
                                intent.setClass(RegUserActivity.this, LoadingActivity.class);
                                startActivityForResult(intent, 0);
                            } else {
                                reg_password2_edit.setText("");
                                reg_password2_edit.requestFocus();
                                UIHelper.ToastMessage(RegUserActivity.this, R.string.regPasswordError);
                            }
                        } else {
                            reg_email_edit.setText("");
                            reg_email_edit.requestFocus();
                            UIHelper.ToastMessage(RegUserActivity.this, R.string.regEmailError);
                        }
                    } else {
                        reg_user_edit.setText("");
                        reg_user_edit.requestFocus();
                        UIHelper.ToastMessage(RegUserActivity.this, R.string.regNameError);
                    }
                    break;
            }
        }
    };

    private final View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.reg_birthday_edit:
                    Log.i(TAG, "修改用户生日");
                    showDialog(DATE_PICK_DIALOG);
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case TaskType.TS_EXAM_CHECKUSER:
                reg_user_edit.setText("");
                reg_user_edit.requestFocus();
                reg_email_edit.setText("");
                break;
            case TaskType.TS_EXAM_REGUSER:
                finish();
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DATE_PICK_DIALOG:
                if ("".equals(reg_birthday_edit.getText().toString())) {
                    c = Calendar.getInstance();
                    dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                            String datePickStr = year + "-" + (month < 10 ? "0" + (month + 1) : (month + 1)) + "-"
                                    + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                            reg_birthday_edit.setText(datePickStr);
                        }
                    }, c.get(Calendar.YEAR), // 传入年份
                            c.get(Calendar.MONTH), // 传入月份
                            c.get(Calendar.DAY_OF_MONTH) // 传入天数
                    );
                } else {
                    String[] dates = reg_birthday_edit.getText().toString().split("-");
                    dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                            String datePickStr = year + "-" + (month < 10 ? "0" + (month + 1) : (month + 1)) + "-"
                                    + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                            reg_birthday_edit.setText(datePickStr);
                        }
                    }, Integer.parseInt(dates[0]), // 传入年份
                            Integer.parseInt(dates[1]) - 1, // 传入月份
                            Integer.parseInt(dates[2]) // 传入天数
                    );
                }

                break;
        }
        return dialog;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object... param) {
        // TODO Auto-generated method stub

    }

}
