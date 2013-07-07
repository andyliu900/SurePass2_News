package com.ideacode.news.logic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

/**
 * <p>
 * FileName: IdeaCodeActivity.java
 * </p>
 * <p>
 * Description: 具有列表数据展示的activity基类
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * @author Vic Su
 * </p>
 * <p>
 * @content andyliu900@gmail.com
 * </p>
 * <p>
 * @version 1.0
 * </p>
 * <p>
 * CreatDate: 2012-9-4 下午3:48:57
 * </p>
 * <p>
 * Modification History
 * </p>
 */
public abstract class IdeaCodeActivity extends Activity {

    private static String Tag = "IdeaCodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i(Tag, "启动create");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        MainService.addActivity(this);
    }

    public abstract void init();

    public abstract void refresh(Object... param);

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(Tag, "启动destroy");
        MainService.removeActivity(this);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i(Tag, "启动start");
        init();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.i(Tag, "启动ReStart");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(Tag, "启动resume");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i(Tag, "启动pause");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.i(Tag, "启动stop");
    }
}
