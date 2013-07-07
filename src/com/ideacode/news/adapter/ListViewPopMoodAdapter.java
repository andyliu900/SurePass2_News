package com.ideacode.news.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.common.util.ExpressionUtil;
import com.ideacode.news.common.util.StringUtils;

public class ListViewPopMoodAdapter extends BaseAdapter {

    private final Context context;// 运行上下文
    private final ArrayList<Mood> listItems;// 数据集合
    private final LayoutInflater listContainer;// 视图容器
    private final int itemViewResource;// 自定义项视图源

    static class ListItemView { // 自定义控件集合
        public TextView userName;
        public TextView moodContent;
        public TextView moodDate;
        public TextView moodLocation;
        public ImageView flag;
        public ImageView heatFlag;
        public TextView moodPraiseCount;
        public TextView moodBelittleCount;
    }

    public ListViewPopMoodAdapter(Context context, ArrayList<Mood> data, int resource) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
    }

    public void addMoodData(ArrayList<Mood> data) {
        listItems.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;

        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();
            // 获取控件对象
            listItemView.userName = (TextView) convertView.findViewById(R.id.mood_listitem_username);
            listItemView.moodContent = (TextView) convertView.findViewById(R.id.mood_listitem_content);
            listItemView.moodDate = (TextView) convertView.findViewById(R.id.mood_listitem_date);
            listItemView.moodLocation = (TextView) convertView.findViewById(R.id.mood_listitem_location);
            listItemView.flag = (ImageView) convertView.findViewById(R.id.mood_listitem_flag);
            listItemView.heatFlag = (ImageView) convertView.findViewById(R.id.mood_listitem_heatflag);
            listItemView.moodPraiseCount = (TextView) convertView.findViewById(R.id.mood_listitem_praisecount);
            listItemView.moodBelittleCount = (TextView) convertView.findViewById(R.id.mood_listitem_belittlecount);

            // 设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        Mood mood = listItems.get(position);

        listItemView.userName.setText(mood.getUserName());
        String str = mood.getMoodContent();
        String zhengze = "f0[0-9]{2}|f10[0-6]";
        try {
            SpannableString spannableString = ExpressionUtil.getExpressionString(context, str, zhengze);
            listItemView.moodContent.setText(spannableString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        listItemView.moodDate.setText(StringUtils.friendly_time(mood.getMoodCreateDate()));
        listItemView.moodLocation.setText(mood.getMoodLocation());
        if (StringUtils.isToday(mood.getMoodCreateDate()))
            listItemView.flag.setVisibility(View.VISIBLE);
        else
            listItemView.flag.setVisibility(View.GONE);
        if (mood.isHeatFlag())
            listItemView.heatFlag.setVisibility(View.VISIBLE);
        else
            listItemView.heatFlag.setVisibility(View.GONE);
        listItemView.moodPraiseCount.setText(String.valueOf(mood.getMoodPraiseCount()));
        listItemView.moodBelittleCount.setText(String.valueOf(mood.getMoodBelittleCount()));

        return convertView;
    }

}
