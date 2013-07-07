package com.ideacode.news.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.youmi.android.offers.OffersAdSize;
import net.youmi.android.offers.OffersBanner;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.adapter.ListViewNewsAdapter;
import com.ideacode.news.adapter.ScrollingTabsAdapter;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;
import com.ideacode.news.widget.NewDataToast;
import com.ideacode.news.widget.PullToRefreshListView;
import com.ideacode.news.widget.ScrollableTabView;

public class TabNewsActivity extends IdeaCodeActivity {

	private ProgressBar mHeadProgress;
	private ImageButton refreshButton;

	private View lvNews_footer;
	private TextView headTv, lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private ListViewNewsAdapter lvNewsAdapter;
	private ArrayList<Map<String, Object>> lvNewsData = new ArrayList<Map<String, Object>>();
	private int currentpage = 1;
	public static int newsType = AppContext.NEWSTYPE_FOCUS;

	private ScrollableTabView mScrollableTabView;
	private ScrollingTabsAdapter mScrollingTabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		initViews();

        // 有米广告配置
        initPointsBanner();

		// 初始化listview控件
		initNewsListView();
        initScrollableTabs();
	}

	private void initViews() {
		headTv = (TextView) findViewById(R.id.systv);
		headTv.setText(R.string.news_head_title);
		mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		refreshButton = (ImageButton) findViewById(R.id.main_head_refresh_button);
		refreshButton.setOnClickListener(onClickListener);
	}

	private void initNewsListView() {
		lvNewsAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvNews_foot_more = (TextView) lvNews_footer.findViewById(R.id.listview_foot_more);
		lvNews_foot_more.setText(R.string.load_ing);
		lvNews_foot_progress = (ProgressBar) lvNews_footer.findViewById(R.id.listview_foot_progress);
		lvNews = (PullToRefreshListView) findViewById(R.id.frame_listview_news);
		lvNews.addFooterView(lvNews_footer);// 添加底部视图 必须在setAdapter前
		lvNews.setAdapter(lvNewsAdapter);
		lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvNews_footer)
					return;

				Map newsTitle = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					newsTitle = (Map) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.news_listitem_title);
					newsTitle = (Map) tv.getTag();
				}
				if (newsTitle == null)
					return;

				// 跳转到新闻详情
				Intent intent = new Intent(TabNewsActivity.this, NewsDetailActivity.class);
				intent.putExtra("newsType", Integer.parseInt(newsTitle.get("newsType").toString()));
				intent.putExtra("newsDetail_url", newsTitle.get("url").toString());
				TabNewsActivity.this.startActivity(intent);
			}

		});
		lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvNews.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvNewsData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvNews_footer) == view.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);
					currentpage++;
					loadLvNewsData(UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lvNews.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadLvNewsData(UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	private void initScrollableTabs() {
		mScrollableTabView = (ScrollableTabView) this.findViewById(R.id.scrollabletabview);
		mScrollingTabsAdapter = new ScrollingTabsAdapter(this);
		mScrollableTabView.setAdapter(mScrollingTabsAdapter);
		mScrollableTabView.setViewPage(AppContext.NEWSTYPE_FOCUS);
	}

    private void initPointsBanner() {
        // (可选)使用积分Mini Banner-一个新的积分墙入口点，随时随地让用户关注新的积分广告
        OffersBanner mMiniBanner = new OffersBanner(this, OffersAdSize.SIZE_MATCH_SCREENx32);
        RelativeLayout layoutOffersMiniBanner = (RelativeLayout) findViewById(R.id.OffersMiniBannerLayout);
        layoutOffersMiniBanner.addView(mMiniBanner);
    }

	/**
	 * 加载新闻数据
	 * 
	 * @param action
	 *            动作标识
	 */
	public void loadLvNewsData(final int action) {
		lvNews_foot_progress.setVisibility(ProgressBar.VISIBLE);
		lvNews_foot_more.setText(R.string.load_ing);
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		refreshButton.setVisibility(ImageButton.GONE);
		boolean isRefresh = false;
		if (action == UIHelper.LISTVIEW_ACTION_REFRESH) {
			currentpage = 1;
			isRefresh = true;
			HashMap params = new HashMap();
			params.put("newsType", newsType);
			params.put("currentpage", currentpage);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_NEWS, params);
			MainService.newTask(ts);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			HashMap params = new HashMap();
			params.put("newsType", newsType);
			params.put("currentpage", currentpage);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_NEWS_MORE, params);
			MainService.newTask(ts);
		} else {
			currentpage = 1;
			HashMap params = new HashMap();
			params.put("newsType", newsType);
			params.put("currentpage", currentpage);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_NEWS, params);
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
				loadLvNewsData(UIHelper.LISTVIEW_ACTION_REFRESH);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void init() {

	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];

		lvNews_foot_progress.setVisibility(ProgressBar.GONE);
		mHeadProgress.setVisibility(ProgressBar.GONE);
		refreshButton.setVisibility(ImageButton.VISIBLE);
		switch (type) {
		case TaskType.TS_EXAM_SEARCH_NEWS:
			lvNews.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
			lvNews.setSelection(0);
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				lvNews.setTag(UIHelper.LISTVIEW_DATA_MORE);
				lvNews_foot_more.setText(R.string.load_error);
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				ArrayList<Map<String, Object>> newsList = (ArrayList<Map<String, Object>>) param[1];
				if (newsList == null || newsList.size() == 0) {
					lvNews.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvNews_foot_more.setText(R.string.load_empty);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
				} else {
					lvNewsData = newsList;
					lvNewsAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
					lvNews.setAdapter(lvNewsAdapter);

					lvNews.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvNewsAdapter.notifyDataSetChanged();
					lvNews_foot_more.setText(R.string.load_more);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvNewsData.size())).show();
					// if (newsList.size() < AppContext.PAGE_SIZE) {
					// lvNews.setTag(UIHelper.LISTVIEW_DATA_FULL);
					// lvNewsAdapter.notifyDataSetChanged();
					// lvNews_foot_more.setText(R.string.load_full);
					// NewDataToast.makeText(
					// this,
					// getString(R.string.new_data_toast_message,
					// lvNewsData.size())).show();
					// } else {
					// lvNews.setTag(UIHelper.LISTVIEW_DATA_MORE);
					// lvNewsAdapter.notifyDataSetChanged();
					// lvNews_foot_more.setText(R.string.load_more);
					// NewDataToast.makeText(
					// this,
					// getString(R.string.new_data_toast_message,
					// lvNewsData.size())).show();
					// }
				}
			}
			break;
		case TaskType.TS_EXAM_SEARCH_NEWS_MORE:
			if (param[2] != null && (Integer) param[2] != 0) {
				// 有异常--显示加载出错 & 弹出错误消息
				lvNews.setTag(UIHelper.LISTVIEW_DATA_ERROR);
				lvNews_foot_more.setText(R.string.load_error);
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				ArrayList<Map<String, Object>> newsList = (ArrayList<Map<String, Object>>) param[1];
				if (newsList == null || newsList.size() == 0) {
					lvNews.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvNewsAdapter.notifyDataSetChanged();
					lvNews_foot_more.setText(R.string.load_full);
				} else {
					lvNews.removeFooterView(lvNews_footer);
					lvNewsData = newsList;
					HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) lvNews.getAdapter();
					ListViewNewsAdapter resultadapter = (ListViewNewsAdapter) listAdapter.getWrappedAdapter();
					resultadapter.addNewData(lvNewsData);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvNewsData.size())).show();
					// 加载完成后将tag设置为more
					lvNews.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvNewsAdapter.notifyDataSetChanged();
					lvNews_foot_more.setText(R.string.load_more);
					lvNews.addFooterView(lvNews_footer);
				}
			}
			break;
		}
	}

}
