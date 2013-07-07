package com.ideacode.news.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;

public class NewDataToast extends Toast {

    public NewDataToast(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 获取控件实例
     * @param context
     * @param text 提示消息
     * @param isSound 是否播放声音
     * @return
     */
    public static NewDataToast makeText(Context context, CharSequence text) {
        NewDataToast result = new NewDataToast(context);

        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        View v = inflate.inflate(R.layout.new_data_toast, null);
        v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度

        TextView tv = (TextView) v.findViewById(R.id.new_data_toast_message);
        tv.setText(text);

        result.setView(v);
        result.setDuration(600);
        result.setGravity(Gravity.BOTTOM, 0, AppContext.bottomHeight);

        return result;
    }

}
