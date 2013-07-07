package com.ideacode.news.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.common.util.ExpressionUtil;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.ui.TabMoodActivity;

public class ListViewMoodAdapter extends BaseAdapter {

	private final TabMoodActivity activity;
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
		public TextView moodPraiseCount;
		public TextView moodBelittleCount;
		public ImageButton praiseBtn;
		public ImageButton belittleBtn;
		public LinearLayout praiseLayout;
		public LinearLayout belittleLayout;
	}

	public ListViewMoodAdapter(TabMoodActivity activity, Context context, ArrayList<Mood> data, int resource) {
		this.activity = activity;
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
	public long getItemId(int arg0) {
		return 0;
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
			listItemView.moodPraiseCount = (TextView) convertView.findViewById(R.id.mood_praise_count);
			listItemView.moodBelittleCount = (TextView) convertView.findViewById(R.id.mood_belittle_count);
			listItemView.praiseBtn = (ImageButton) convertView.findViewById(R.id.mood_praise_img);
			listItemView.belittleBtn = (ImageButton) convertView.findViewById(R.id.mood_belittle_img);
			listItemView.praiseLayout = (LinearLayout) convertView.findViewById(R.id.mood_praise_layout);
			listItemView.belittleLayout = (LinearLayout) convertView.findViewById(R.id.mood_belittle_layout);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		Mood mood = listItems.get(position);

		listItemView.userName.setText(mood.getUserName());
		listItemView.praiseBtn.setOnClickListener(onClickListener);
		listItemView.belittleBtn.setOnClickListener(onClickListener);
		listItemView.praiseLayout.setOnClickListener(onClickListener);
		listItemView.praiseLayout.setTag(mood);// 设置隐藏参数(实体类)
		listItemView.belittleLayout.setOnClickListener(onClickListener);
		listItemView.belittleLayout.setTag(mood);
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
		listItemView.moodPraiseCount.setText(String.valueOf(mood.getMoodPraiseCount()));
		listItemView.moodBelittleCount.setText(String.valueOf(mood.getMoodBelittleCount()));

		return convertView;
	}

	private final OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mood_praise_img:
				activity.doPraise(v);
				break;
			case R.id.mood_belittle_img:
				activity.doBelittle(v);
				break;
			case R.id.mood_praise_layout:
				activity.doPraise(v);
				break;
			case R.id.mood_belittle_layout:
				activity.doBelittle(v);
				break;
			}
		}
	};
}
