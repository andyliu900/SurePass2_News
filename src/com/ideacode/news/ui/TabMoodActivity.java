package com.ideacode.news.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.adapter.ListViewMoodAdapter;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.bean.UserOptionType;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.db.DataHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;
import com.ideacode.news.widget.NewDataToast;
import com.ideacode.news.widget.PullToRefreshListView;

public class TabMoodActivity extends IdeaCodeActivity {

	private static final String TAG = "TabMoodActivity";

	private AppContext appContext;
	private ProgressBar mHeadProgress;
	private ImageButton addMoodButton, refreshButton;

	private View lvMood_footer;
	private TextView headTv, lvMood_foot_more;
	private ProgressBar lvMood_foot_progress;
	private PullToRefreshListView lvMood;
	private ListViewMoodAdapter lvMoodAdapter;
	private ArrayList<Mood> lvMoodData = new ArrayList<Mood>();
	private int currentpage = 1;

	private DataHelper datahelp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood);
		appContext = (AppContext) getApplication();

		initViews();
		// 初始化listview控件
		initMoodListView();

        datahelp = new DataHelper(this);
	}

	private void initViews() {
		headTv = (TextView) findViewById(R.id.systv);
		headTv.setText(R.string.mood_head_titld);
		addMoodButton = (ImageButton) findViewById(R.id.main_head_addmood_button);
		addMoodButton.setOnClickListener(onClickListener);
		mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		refreshButton = (ImageButton) findViewById(R.id.main_head_refresh_button);
		refreshButton.setOnClickListener(onClickListener);
	}

	private void initMoodListView() {
		lvMoodAdapter = new ListViewMoodAdapter(this, this, lvMoodData, R.layout.mood_listitem);
		lvMood_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvMood_foot_more = (TextView) lvMood_footer.findViewById(R.id.listview_foot_more);
		lvMood_foot_more.setText(R.string.load_ing);
		lvMood_foot_progress = (ProgressBar) lvMood_footer.findViewById(R.id.listview_foot_progress);
		lvMood = (PullToRefreshListView) findViewById(R.id.frame_listview_mood);
		lvMood.addFooterView(lvMood_footer);// 添加底部视图 必须在setAdapter前
		lvMood.setAdapter(lvMoodAdapter);
		lvMood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvMood_footer)
					return;

				Mood mood = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					mood = (Mood) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.mood_listitem_content);
					mood = (Mood) tv.getTag();
				}
				if (mood == null)
					return;
			}

		});
		lvMood.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvMood.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvMoodData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvMood_footer) == view.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvMood.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvMood.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvMood_foot_more.setText(R.string.load_ing);
					lvMood_foot_progress.setVisibility(View.VISIBLE);
					currentpage++;
					loadLvMoodData(UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lvMood.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		lvMood.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadLvMoodData(UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	/**
	 * 加载心情数据
	 * 
	 * @param action
	 *            动作标识
	 */
	public void loadLvMoodData(final int action) {
		lvMood_foot_progress.setVisibility(ProgressBar.VISIBLE);
		lvMood_foot_more.setText(R.string.load_ing);
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		refreshButton.setVisibility(ImageButton.GONE);
		addMoodButton.setVisibility(ImageButton.GONE);
		boolean isRefresh = false;
		if (action == UIHelper.LISTVIEW_ACTION_REFRESH) {
			currentpage = 1;
			isRefresh = true;
			Paging p = new Paging(currentpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_MOOD, params);
			MainService.newTask(ts);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			Paging p = new Paging(currentpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_MOOD_MORE, params);
			MainService.newTask(ts);
		} else {
			currentpage = 1;
			Paging p = new Paging(currentpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_MOOD, params);
			MainService.newTask(ts);
		}
	}

	private final OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			currentpage = 1;
			switch (v.getId()) {
			case R.id.main_head_refresh_button:
				refreshButton.setVisibility(ImageButton.GONE);
				addMoodButton.setVisibility(ImageButton.GONE);
				loadLvMoodData(UIHelper.LISTVIEW_ACTION_REFRESH);
				break;
			case R.id.main_head_addmood_button:
				if (appContext.isLogin()) {
					Intent intent = new Intent(TabMoodActivity.this, AddMoodActivity.class);
					TabMoodActivity.this.startActivityForResult(intent, 0);
				} else {
					UIHelper.showLoginDialog(TabMoodActivity.this);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case TaskType.TS_EXAM_SEND_MOOD:
			loadLvMoodData(UIHelper.LISTVIEW_ACTION_REFRESH);
			break;
		}
	}

	/**
	 * 顶一下按钮事件
	 * 
	 * @param view
	 */
	public void doPraise(View view) {
		if (view instanceof ImageButton) {
			view = (View) view.getParent();
		}
		if (appContext.isLogin()) {
			Mood mood = (Mood) view.getTag();

            int code = datahelp.addUserPraiseBelittle(appContext.getLoginUid(), mood.getMoodId(), UserOptionType.ADD_PRAISE);
			if (code == CommonSetting.Success) {
				int praiseCount = mood.getMoodPraiseCount() + 1;
				TextView praiseCountTv = (TextView) view.findViewById(R.id.mood_praise_count);
				praiseCountTv.setText(String.valueOf(praiseCount));
				mood.setMoodPraiseCount(praiseCount);

				mHeadProgress.setVisibility(ProgressBar.VISIBLE);
				refreshButton.setVisibility(ImageButton.GONE);
				// 提交后台
				HashMap params = new HashMap();
				params.put("mood", mood);
				Task ts = new Task(TaskType.TS_EXAM_PRAISE_MOOD, params);
				MainService.newTask(ts);
			} else {
				UIHelper.ToastMessage(this, R.string.msg_mood_praise_error);
			}
		} else {
			UIHelper.showLoginDialog(this);
		}
	}

	/**
	 * 踩一下按钮事件
	 * 
	 * @param view
	 */
	public void doBelittle(View view) {
		if (view instanceof ImageButton) {
			view = (View) view.getParent();
		}
		if (appContext.isLogin()) {
			Mood mood = (Mood) view.getTag();
            int code = datahelp.addUserPraiseBelittle(appContext.getLoginUid(), mood.getMoodId(), UserOptionType.ADD_BELITTLE);
			if (code == CommonSetting.Success) {
				int belittleCount = mood.getMoodBelittleCount() + 1;
				TextView belittleeCountTv = (TextView) view.findViewById(R.id.mood_belittle_count);
				belittleeCountTv.setText(String.valueOf(belittleCount));
				mood.setMoodBelittleCount(belittleCount);

				mHeadProgress.setVisibility(ProgressBar.VISIBLE);
				refreshButton.setVisibility(ImageButton.GONE);
				// 提交后台
				HashMap params = new HashMap();
				params.put("mood", mood);
				Task ts = new Task(TaskType.TS_EXAM_BELITTLE_MOOD, params);
				MainService.newTask(ts);
			} else {
				UIHelper.ToastMessage(this, R.string.msg_mood_belittle_error);
			}
		} else {
			UIHelper.showLoginDialog(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadLvMoodData(UIHelper.LISTVIEW_ACTION_REFRESH);
	}

	@Override
	public void init() {

	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];

		lvMood_foot_progress.setVisibility(ProgressBar.GONE);
		mHeadProgress.setVisibility(ProgressBar.GONE);
		refreshButton.setVisibility(ImageButton.VISIBLE);
		addMoodButton.setVisibility(ImageButton.VISIBLE);
		switch (type) {
		case TaskType.TS_EXAM_SEARCH_MOOD:
			lvMood.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			lvMood.setSelection(0);
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				lvMood.setTag(UIHelper.LISTVIEW_DATA_MORE);
				lvMood_foot_more.setText(R.string.load_error);
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				ArrayList<Mood> moodList = (ArrayList<Mood>) param[1];
				if (moodList == null || moodList.size() == 0) {
					lvMood.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvMood_foot_more.setText(R.string.load_empty);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
				} else {
					lvMoodData = moodList;
					lvMoodAdapter = new ListViewMoodAdapter(this, this, lvMoodData, R.layout.mood_listitem);
					lvMood.setAdapter(lvMoodAdapter);
					if (moodList.size() < AppContext.PAGE_SIZE) {
						lvMood.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvMoodAdapter.notifyDataSetChanged();
						lvMood_foot_more.setText(R.string.load_full);
						NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvMoodData.size())).show();
					} else {
						lvMood.setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvMoodAdapter.notifyDataSetChanged();
						lvMood_foot_more.setText(R.string.load_more);
						NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvMoodData.size())).show();
					}
				}
			}
			break;
		case TaskType.TS_EXAM_SEARCH_MOOD_MORE:
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				lvMood.setTag(UIHelper.LISTVIEW_DATA_ERROR);
				lvMood_foot_more.setText(R.string.load_error);
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				ArrayList<Mood> moodList = (ArrayList<Mood>) param[1];
				if (moodList == null || moodList.size() == 0) {
					lvMood.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvMoodAdapter.notifyDataSetChanged();
					lvMood_foot_more.setText(R.string.load_full);
				} else {
					lvMood.removeFooterView(lvMood_footer);
					lvMoodData = moodList;
					HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) lvMood.getAdapter();
					ListViewMoodAdapter resultadapter = (ListViewMoodAdapter) listAdapter.getWrappedAdapter();
					resultadapter.addMoodData(lvMoodData);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvMoodData.size())).show();
					// 加载完成后将tag设置为more
					lvMood.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvMoodAdapter.notifyDataSetChanged();
					lvMood_foot_more.setText(R.string.load_more);
					lvMood.addFooterView(lvMood_footer);
				}
			}
			break;
		case TaskType.TS_EXAM_PRAISE_MOOD:
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				int code = (Integer) param[1];
				if (code == CommonSetting.Success) {
					UIHelper.ToastMessage(this, R.string.msg_mood_addpraise_success);
				} else {
					UIHelper.ToastMessage(this, R.string.msg_mood_addpraise_error);
				}
			}
			break;
		case TaskType.TS_EXAM_BELITTLE_MOOD:
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				int code = (Integer) param[1];
				if (code == CommonSetting.Success) {
					UIHelper.ToastMessage(this, R.string.msg_mood_addbelittle_success);
				} else {
					UIHelper.ToastMessage(this, R.string.msg_mood_addbelittle_error);
				}
			}
			break;
		}
	}

}
