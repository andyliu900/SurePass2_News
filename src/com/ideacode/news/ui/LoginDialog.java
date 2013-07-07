package com.ideacode.news.ui;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.MD5;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class LoginDialog extends IdeaCodeActivity {

	private ViewSwitcher mViewSwitcher;
	/** 账号 */
	private AutoCompleteTextView mAccount;
	/** 密码 */
	private EditText mPwd;
	/** 复选框 */
	private CheckBox chb_rememberMe;
	/** 登录按钮 */
	private Button btn_login;
	/** 注册按钮 */
	private Button btn_reg;
	/** 关闭按钮 */
	private ImageButton btn_close;

	private RelativeLayout loginLoading;
	/** 只有在数据加载完成后，才可以点击“返回键” **/
	private boolean canBack = true;

	private int curLoginType;
	private InputMethodManager imm;

	public final static int LOGIN_OTHER = 0x00;
	public final static int LOGIN_MAIN = 0x01;
	public final static int LOGIN_SETTING = 0x02;

	AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_dialog);
		appContext = (AppContext) getApplication();
		initView();
	}

	/**
	 * 初始化控件
	 * 
	 * @author Simon Xu 2013-4-23下午3:34:44
	 */
	private void initView() {
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.logindialog_view_switcher);
		mAccount = (AutoCompleteTextView) findViewById(R.id.login_account);
		mPwd = (EditText) findViewById(R.id.login_password);
		chb_rememberMe = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);
		loginLoading = (RelativeLayout) findViewById(R.id.progressBar);

		btn_close = (ImageButton) findViewById(R.id.login_close_button);
		btn_close.setVisibility(View.VISIBLE);
		btn_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppContext.SHOW_LOGIN_FLAG = false;
				LoginDialog.this.finish();
			}
		});

		btn_login = (Button) findViewById(R.id.login_btn_login);
		btn_login.setOnClickListener(onClickListener);
		btn_reg = (Button) findViewById(R.id.login_btn_reg);
		btn_reg.setOnClickListener(onClickListener);
	}

	private final OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			switch (v.getId()) {
			case R.id.login_btn_login:
				String account = mAccount.getText().toString();
				String pwd = mPwd.getText().toString();
				boolean isRememberMe = chb_rememberMe.isChecked();
				// 判断输入
				if (StringUtils.isEmpty(account)) {
					UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
					return;
				}
				if (StringUtils.isEmpty(pwd)) {
					UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
					return;
				}

				btn_close.setVisibility(View.GONE);
				mViewSwitcher.showNext();

				login(account, pwd, isRememberMe);
				break;
			case R.id.login_btn_reg:
				Intent intent = new Intent(LoginDialog.this, RegUserActivity.class);
				LoginDialog.this.startActivity(intent);
				LoginDialog.this.finish();
				break;
			}
		}
	};

	private void login(final String account, final String pwd, final boolean isRememberMe) {
		TbUser tbUser = new TbUser();
		tbUser.setUserName(account);
		tbUser.setUserEmail(account);
		tbUser.setUserPassword(MD5.MD5Encode(pwd));
		tbUser.setRememberMe(isRememberMe);
		HashMap params = new HashMap();
		params.put("tbUser", tbUser);

		Task ts = new Task(TaskType.TS_EXAM_LOGIN, params);
		MainService.newTask(ts);
		canBack = false;
	}

	@Override
	public void init() {

	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];
		switch (type) {
		case TaskType.TS_EXAM_LOGIN:
			if (param[2] != null && (Integer) param[2] == AppException.TYPE_SOAP) {
				appContext.cleanLoginInfo();
				mViewSwitcher.showPrevious();
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				HashMap<String, Object> hashMap = null;
				;
				if (param[1] != null) {
					hashMap = (HashMap<String, Object>) param[1];
				}

				if (param[1] != null && Integer.parseInt(hashMap.get("code").toString()) == 1) {
					TbUser tbUser = new TbUser();
					tbUser.setUserId(Long.parseLong(hashMap.get("userId").toString()));
					tbUser.setUserName(hashMap.get("userName").toString());
					tbUser.setUserPassword(mPwd.getText().toString());
					tbUser.setLocation("");
					tbUser.setRememberMe(Boolean.parseBoolean(hashMap.get("isRememberMe").toString()));
					appContext.saveLoginInfo(tbUser);
					UIHelper.ToastMessage(this, R.string.msg_login_success);
					LoginDialog.this.finish();
				} else if (param[1] != null && Integer.parseInt(hashMap.get("code").toString()) == 0) {
					appContext.cleanLoginInfo();
					UIHelper.ToastMessage(this, R.string.msg_login_error);
					// loadingAnimation.stop();
					mViewSwitcher.showPrevious();
				}
			}
			canBack = true;
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (canBack) {
				AppContext.SHOW_LOGIN_FLAG = false;
				LoginDialog.this.finish();
				return super.onKeyDown(keyCode, event);
			} else {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
