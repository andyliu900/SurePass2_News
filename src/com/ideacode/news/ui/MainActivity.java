package com.ideacode.news.ui;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppManager;
import com.ideacode.news.common.util.UIHelper;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    TabHost tabHost;
    TabHost.TabSpec tabSpec;
    RadioGroup radioGroup;

    private AppContext appContext;// 全局Context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppManager.getAppManager().addActivity(this);

        appContext = (AppContext) getApplication();
        // 网络连接判断
        if (!appContext.isNetworkConnected())
            UIHelper.ToastMessage(this, R.string.network_not_connected);

        // 初始化登录
        appContext.initLoginInfo();

        initViews();

        // 有米广告配置 初始化应用的发布ID和密钥，以及设置测试模式
        AdManager.getInstance(this).init(appContext.YOUMI_ID, appContext.YOUMI_KEY, false);
        // 请务必调用以下代码，告诉SDK应用启动，可以让SDK进行一些初始化操作。该接口务必在SDK的初始化接口之后调用。
        OffersManager.getInstance(this).onAppLaunch();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 有米广告配置 如果使用积分广告，请务必调用积分广告的初始化接口:
        OffersManager.getInstance(this).onAppExit();
        AppManager.getAppManager().finishActivity(this);
    }

    private void initViews() {
        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("news").setIndicator("News").setContent(new Intent(this, TabNewsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("mood").setIndicator("Mood").setContent(new Intent(this, TabMoodActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("user").setIndicator("User").setContent(new Intent(this, TabUserActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("find").setIndicator("Find").setContent(new Intent(this, TabFindActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("Setting").setContent(new Intent(this, TabSettingActivity.class)));

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
    }

    private final OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_news:
                    tabHost.setCurrentTabByTag("news");
                    break;
                case R.id.radio_mood:
                    tabHost.setCurrentTabByTag("mood");
                    break;
                case R.id.radio_user:
                    AppContext.SHOW_LOGIN_FLAG = true;
                    tabHost.setCurrentTabByTag("user");
                    break;
                case R.id.radio_find:
                    tabHost.setCurrentTabByTag("find");
                    break;
                case R.id.radio_setting:
                    tabHost.setCurrentTabByTag("setting");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        AppContext.bottomHeight = radioGroup.getHeight();
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            UIHelper.Exit(this);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
