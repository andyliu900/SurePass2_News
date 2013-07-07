package com.ideacode.news.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.common.util.StringUtils;

public class ListViewNewsAdapter extends BaseAdapter {

    private final Context                     context;//运行上下文
    private final ArrayList<Map<String,Object>>                  listItems;//数据集合
    private final LayoutInflater              listContainer;//视图容器
    private final int                         itemViewResource;//自定义项视图源
    static class ListItemView{              //自定义控件集合  
        public TextView title;  
        public TextView date; 
        public ImageView flag;
    } 

    public ListViewNewsAdapter(Context context, ArrayList<Map<String,Object>> data,int resource) {
        this.context = context;         
        this.listContainer = LayoutInflater.from(context);  //创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = data;
    }

    public void addNewData(ArrayList<Map<String,Object>> data) {
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
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView  listItemView = null;

        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();
            //获取控件对象
            listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
            listItemView.date= (TextView)convertView.findViewById(R.id.news_listitem_date);
            listItemView.flag= (ImageView)convertView.findViewById(R.id.news_listitem_flag);

            //设置控件集到convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        Map map = listItems.get(position);
        
        listItemView.title.setText(map.get("title").toString());
        listItemView.title.setTag(map);//设置隐藏参数(实体类)
        listItemView.date.setText(StringUtils.friendly_time(map.get("date").toString()));
        if (StringUtils.isToday(map.get("date").toString()))
            listItemView.flag.setVisibility(View.VISIBLE);
        else
            listItemView.flag.setVisibility(View.GONE);

        return convertView;
    }
}
