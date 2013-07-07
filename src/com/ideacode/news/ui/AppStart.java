package com.ideacode.news.ui;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class AppStart extends IdeaCodeActivity {

	private TextView dataTips;
	boolean isFirstIn = false;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);

		// 启动系统服务
		if (!MainService.isrun) {
			Intent it = new Intent(this, MainService.class);
			this.startService(it);
		}

		SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		dataTips = (TextView) findViewById(R.id.dataloading); // 显示1.5秒钟后自动消失

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				dataTips.setVisibility(View.VISIBLE);

				HashMap params = new HashMap();
				params.put("context", AppStart.this);
				Task ts = new Task(TaskType.TS_EXAM_GETINITIALIZEDATA, params);
				MainService.newTask(ts);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

		// 兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name", "cookie_value", "cookie_version", "cookie_path");
			}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
		int type = (Integer) param[0];
		switch (type) {
		case TaskType.TS_EXAM_GETINITIALIZEDATA:
			if (CommonSetting.InitSystemDataException == (Integer) param[2]) {
				Toast.makeText(this, getResources().getString(R.string.initsystemdataexception), Toast.LENGTH_LONG).show();
				Intent it = new Intent(this, MainService.class);
				this.stopService(it);
				UIHelper.Exit(this);
			} else {
				dataTips.setText(R.string.dataloadend);
				if (!isFirstIn) {
					Intent homeIntent = new Intent(AppStart.this, MainActivity.class);
					startActivity(homeIntent);
					finish();
				} else {
					Intent guideIntent = new Intent(AppStart.this, GuideActivity.class);
					startActivity(guideIntent);
					finish();
				}
			}
			break;
		}
	}
}
