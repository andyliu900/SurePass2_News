package com.ideacode.news.ui;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class EditUserInfoActivity extends IdeaCodeActivity {

	private static final String TAG = "EditUserInfoActivity";

	private ImageButton back_bt, save_bt;
	private TextView title_tv, tips_tv;
	private EditText content_et;
	private InputMethodManager imm;
	private Task task = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_userinfo);

		if (getIntent().getSerializableExtra(CommonSetting.FileNameTag) != null) {
			task = (Task) getIntent().getSerializableExtra(CommonSetting.FileNameTag);
		}

		initViews();
	}

	private void initViews() {
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(R.id.edit_content, 0);

		back_bt = (ImageButton) findViewById(R.id.main_head_back_button);
		back_bt.setOnClickListener(buttonClickListener);
		save_bt = (ImageButton) findViewById(R.id.main_head_save_button);
		save_bt.setOnClickListener(buttonClickListener);

		title_tv = (TextView) findViewById(R.id.systv);
		tips_tv = (TextView) findViewById(R.id.tips_text);
		content_et = (EditText) findViewById(R.id.edit_content);
		content_et.requestFocus();
	}

	private final Button.OnClickListener buttonClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {

			imm.hideSoftInputFromWindow(content_et.getWindowToken(), 0);
			switch (view.getId()) {
			case R.id.main_head_back_button:
				finish();
				break;
			case R.id.main_head_save_button:
				String content_txt = content_et.getText().toString();
				if (task.getTaskID() == TaskType.TS_EXAM_CHANGEUSEREMAIL) {
					if ("".equals(content_txt)) {
						UIHelper.ToastMessage(EditUserInfoActivity.this, R.string.msg_login_email_null);
						return;
					}
					if (!StringUtils.checkEmailInput(content_txt)) {
						UIHelper.ToastMessage(EditUserInfoActivity.this, R.string.msg_login_email_format_error);
						return;
					}
				}

				HashMap map = new HashMap();
				map.put("update_txt", content_txt);
				Task task_1 = new Task(task.getTaskID(), map);

				Intent intent = new Intent();
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(CommonSetting.FileNameTag, task_1);
				intent.putExtras(mBundle);
				setResult(task.getTaskID(), intent);
				finish();

				break;
			}
		}
	};

	@Override
	public void init() {
		switch (task.getTaskID()) {
		case TaskType.TS_EXAM_CHANGEUSEREMAIL:
			title_tv.setText(R.string.userEmail);
			content_et.setText(task.getTaskParam().get("userEmail").toString());
			tips_tv.setText(R.string.userEmailtTips);
			break;
		case TaskType.TS_EXAM_CHANGEUSERSUMMARY:
			title_tv.setText(R.string.summary);
			content_et.setText(task.getTaskParam().get("userSummary").toString());
			tips_tv.setText(R.string.summaryTips);
			break;
		case TaskType.TS_EXAM_CHANGEQQ:
			title_tv.setText(R.string.qq);
			content_et.setText(task.getTaskParam().get("userQq").toString());
			tips_tv.setText(R.string.qqtTips);
			break;
		}

		content_et.selectAll();
	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub

	}

}
