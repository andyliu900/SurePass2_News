package com.ideacode.news.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.youmi.android.offers.OffersManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.adapter.ListViewPopFavouriteNewsAdapter;
import com.ideacode.news.adapter.ListViewPopMoodAdapter;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;
import com.ideacode.news.widget.NewDataToast;
import com.ideacode.news.widget.PullToRefreshListView;
import com.ideacode.news.widget.ScrollLayout;

public class TabFindActivity extends IdeaCodeActivity {

    private static final String TAG = "TabFindActivity";

    private ProgressBar mTitleProgress;
    private Button bt_head_mood;
    private Button bt_head_favourite;
    private ImageButton surprisedButton, refreshButton;

    private ScrollLayout mScrollLayout;
    private int mViewCount;
    private int mCurSel;

    private PullToRefreshListView lvMood;
    private View lvMood_footer;
    private TextView lvMood_foot_more;
    private ProgressBar lvMood_foot_progress;
    private ListViewPopMoodAdapter lvMoodAdapter;
    private ArrayList<Mood> lvMoodData = new ArrayList<Mood>();
    private int moodNowpage = 1;

    private PullToRefreshListView lvFavourite;
    private View lvFavourite_footer;
    private TextView lvFavourite_foot_more;
    private ProgressBar lvFavourite_foot_progress;
    private ListViewPopFavouriteNewsAdapter lvFavouriteAdapter;
    private ArrayList<Map<String, Object>> lvNewsData = new ArrayList<Map<String, Object>>();
    private int favouriteNowpage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        initViews();
        initPopMoodViews();
        initPopFavouriteViews();
        initPageScroll();

        loadPopMoodList(UIHelper.LISTVIEW_ACTION_INIT);
        loadPopFavouriteList(UIHelper.LISTVIEW_ACTION_INIT);
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        surprisedButton = (ImageButton) findViewById(R.id.main_head_surprised_button);
        surprisedButton.setOnClickListener(onClickListener);
        mTitleProgress = (ProgressBar) findViewById(R.id.main_head_progress);
        refreshButton = (ImageButton) findViewById(R.id.main_head_refresh_button);
        refreshButton.setOnClickListener(onClickListener);
        bt_head_mood = (Button) findViewById(R.id.activity_find_head_mood);
        bt_head_favourite = (Button) findViewById(R.id.activity_find_head_userinfo);
        bt_head_mood.setOnClickListener(onClickListener);
        bt_head_favourite.setOnClickListener(onClickListener);

        bt_head_mood.setEnabled(false);
    }

    private void initPopMoodViews() {
        lvMoodAdapter = new ListViewPopMoodAdapter(this, lvMoodData, R.layout.popmood_listitem);
        lvMood_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvMood_foot_more = (TextView) lvMood_footer.findViewById(R.id.listview_foot_more);
        lvMood_foot_more.setText(R.string.load_more);
        lvMood_foot_progress = (ProgressBar) lvMood_footer.findViewById(R.id.listview_foot_progress);
        lvMood = (PullToRefreshListView) findViewById(R.id.find_pop_mood_listview);
        lvMood.addFooterView(lvMood_footer);// 添加底部视图 必须在setAdapter前
        lvMood.setAdapter(lvMoodAdapter);
        lvMood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvMood_footer)
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
                    moodNowpage++;
                    loadPopMoodList(UIHelper.LISTVIEW_ACTION_SCROLL);
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
                loadPopMoodList(UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    private void initPopFavouriteViews() {
        lvFavouriteAdapter = new ListViewPopFavouriteNewsAdapter(this, lvNewsData, R.layout.popnews_listitem);
        lvFavourite_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvFavourite_foot_more = (TextView) lvFavourite_footer.findViewById(R.id.listview_foot_more);
        lvFavourite_foot_more.setText(R.string.load_more);
        lvFavourite_foot_progress = (ProgressBar) lvFavourite_footer.findViewById(R.id.listview_foot_progress);
        lvFavourite = (PullToRefreshListView) findViewById(R.id.find_pop_favourite_listview);
        lvFavourite.addFooterView(lvFavourite_footer);// 添加底部视图 必须在setAdapter前
        lvFavourite.setAdapter(lvFavouriteAdapter);
        lvFavourite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvFavourite_footer)
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
                Intent intent = new Intent(TabFindActivity.this, NewsDetailActivity.class);
                intent.putExtra("newsType", Integer.parseInt(newsTitle.get("newsType").toString()));
                intent.putExtra("newsDetail_url", newsTitle.get("url").toString());
                TabFindActivity.this.startActivity(intent);
            }
        });
        lvFavourite.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvFavourite.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvNewsData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvFavourite_footer) == view.getLastVisiblePosition()) {
                        scrollEnd = true;
                    }
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvFavourite.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvFavourite.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvFavourite_foot_more.setText(R.string.load_ing);
                    lvFavourite_foot_progress.setVisibility(View.VISIBLE);
                    favouriteNowpage++;
                    loadPopFavouriteList(UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lvFavourite.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        lvFavourite.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPopFavouriteList(UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }

    /**
     * 初始化水平翻页
     */
    private void initPageScroll() {
        mScrollLayout = (ScrollLayout) findViewById(R.id.find_scrolllayout);
        mViewCount = mScrollLayout.getChildCount();
        mCurSel = 0;
        mScrollLayout.snapToScreen(mCurSel);
        mScrollLayout.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
            @Override
            public void OnViewChange(int viewIndex) {
                // 切换列表视图-如果列表数据为空：加载数据
                switch (viewIndex) {
                    case 0:// 热门心情
                        bt_head_mood.setEnabled(false);
                        bt_head_favourite.setEnabled(true);
                        break;
                    case 1:// 热门收藏
                        bt_head_mood.setEnabled(true);
                        bt_head_favourite.setEnabled(false);
                        break;
                }
            }
        });
    }

    /**
     * 加载热门心情数据
     * 
     * @param action
     *            动作标识
     */
    private void loadPopMoodList(final int action) {
        lvMood_foot_progress.setVisibility(ProgressBar.VISIBLE);
        lvMood_foot_more.setText(R.string.load_ing);
        mTitleProgress.setVisibility(ProgressBar.VISIBLE);
        refreshButton.setVisibility(ImageButton.GONE);
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH) {
            moodNowpage = 1;
            isRefresh = true;
            Paging p = new Paging(moodNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_MOOD, params);
            MainService.newTask(ts);
        } else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            Paging p = new Paging(moodNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE, params);
            MainService.newTask(ts);
        } else {
            Paging p = new Paging(moodNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_MOOD, params);
            MainService.newTask(ts);
        }
    }

    /**
     * 加载热门新闻收藏
     * 
     * @param action
     */
    private void loadPopFavouriteList(int action) {
        lvFavourite_foot_progress.setVisibility(ProgressBar.VISIBLE);
        lvFavourite_foot_more.setText(R.string.load_ing);
        mTitleProgress.setVisibility(ProgressBar.VISIBLE);
        refreshButton.setVisibility(ImageButton.GONE);
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH) {
            favouriteNowpage = 1;
            isRefresh = true;
            Paging p = new Paging(favouriteNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_FAVOURITE, params);
            MainService.newTask(ts);
        } else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            Paging p = new Paging(favouriteNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE, params);
            MainService.newTask(ts);
        } else {
            Paging p = new Paging(favouriteNowpage, AppContext.PAGE_SIZE);
            HashMap params = new HashMap();
            params.put("paging", p);
            params.put("isRefresh", isRefresh);
            Task ts = new Task(TaskType.TS_EXAM_SEARCH_POP_FAVOURITE, params);
            MainService.newTask(ts);
        }
    }

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_head_surprised_button:
                    // 跳转到积分墙
                    OffersManager.getInstance(TabFindActivity.this).showOffersWallDialog(TabFindActivity.this);
                    break;
                case R.id.main_head_refresh_button:
                    mTitleProgress.setVisibility(ProgressBar.VISIBLE);
                    refreshButton.setVisibility(ImageButton.GONE);
                    if (mScrollLayout.getCurScreen() == 0) {
                        loadPopMoodList(UIHelper.LISTVIEW_ACTION_REFRESH);
                    }
                    if (mScrollLayout.getCurScreen() == 1) {
                        loadPopFavouriteList(UIHelper.LISTVIEW_ACTION_REFRESH);
                    }
                    break;
                case R.id.activity_find_head_mood:
                    bt_head_mood.setEnabled(false);
                    bt_head_favourite.setEnabled(true);
                    mScrollLayout.snapToScreen(0);
                    break;
                case R.id.activity_find_head_userinfo:
                    bt_head_mood.setEnabled(true);
                    bt_head_favourite.setEnabled(false);
                    mScrollLayout.snapToScreen(1);
                    break;
            }
        }
    };

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(Object... param) {
        lvMood_foot_progress.setVisibility(ProgressBar.GONE);
        lvFavourite_foot_progress.setVisibility(ProgressBar.GONE);
        mTitleProgress.setVisibility(ProgressBar.GONE);
        refreshButton.setVisibility(ImageButton.VISIBLE);
        int type = (Integer) param[0];
        switch (type) {
            case TaskType.TS_EXAM_SEARCH_POP_MOOD:
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
                        lvMoodData = moodList;
                        lvMoodAdapter = new ListViewPopMoodAdapter(this, lvMoodData, R.layout.popmood_listitem);
                        lvMood.setAdapter(lvMoodAdapter);
                        lvMood.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                        lvMood_foot_more.setText(R.string.load_empty);
                        NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
                    } else {
                        lvMoodData = moodList;
                        lvMoodAdapter = new ListViewPopMoodAdapter(this, lvMoodData, R.layout.popmood_listitem);
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
            case TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE:
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
                        ListViewPopMoodAdapter resultadapter = (ListViewPopMoodAdapter) listAdapter.getWrappedAdapter();
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
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE:
                lvFavourite.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
                lvFavourite.setSelection(0);
                if (param[2] != null && (Integer) param[2] != 0) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    lvFavourite.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    lvFavourite_foot_more.setText(R.string.load_error);
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                } else {
                    ArrayList<Map<String, Object>> newsList = (ArrayList<Map<String, Object>>) param[1];
                    if (newsList == null || newsList.size() == 0) {
                        lvNewsData = newsList;
                        lvFavouriteAdapter = new ListViewPopFavouriteNewsAdapter(this, lvNewsData, R.layout.popnews_listitem);
                        lvFavourite.setAdapter(lvFavouriteAdapter);
                        lvFavourite.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                        lvFavourite_foot_more.setText(R.string.load_empty);
                        NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
                    } else {
                        lvNewsData = newsList;
                        lvFavouriteAdapter = new ListViewPopFavouriteNewsAdapter(this, lvNewsData, R.layout.popnews_listitem);
                        lvFavourite.setAdapter(lvFavouriteAdapter);

                        if (newsList.size() < AppContext.PAGE_SIZE) {
                            lvFavourite.setTag(UIHelper.LISTVIEW_DATA_FULL);
                            lvFavouriteAdapter.notifyDataSetChanged();
                            lvFavourite_foot_more.setText(R.string.load_full);
                            NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvNewsData.size())).show();
                        } else {
                            lvFavourite.setTag(UIHelper.LISTVIEW_DATA_MORE);
                            lvFavouriteAdapter.notifyDataSetChanged();
                            lvFavourite_foot_more.setText(R.string.load_more);
                            NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvNewsData.size())).show();
                        }
                    }
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE:
                if (param[2] != null && (Integer) param[2] != 0) {
                    // 有异常--显示加载出错 & 弹出错误消息
                    lvFavourite.setTag(UIHelper.LISTVIEW_DATA_ERROR);
                    lvFavourite_foot_more.setText(R.string.load_error);
                    UIHelper.ToastMessage(this, R.string.http_exception_error);
                } else {
                    ArrayList<Map<String, Object>> newsList = (ArrayList<Map<String, Object>>) param[1];
                    if (newsList == null || newsList.size() == 0) {
                        lvFavourite.setTag(UIHelper.LISTVIEW_DATA_FULL);
                        lvFavouriteAdapter.notifyDataSetChanged();
                        lvFavourite_foot_more.setText(R.string.load_full);
                    } else {
                        lvFavourite.removeFooterView(lvFavourite_footer);
                        lvNewsData = newsList;
                        HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) lvFavourite.getAdapter();
                        ListViewPopFavouriteNewsAdapter resultadapter = (ListViewPopFavouriteNewsAdapter) listAdapter.getWrappedAdapter();
                        resultadapter.addNewData(lvNewsData);
                        NewDataToast.makeText(this, getString(R.string.new_data_toast_message, lvNewsData.size())).show();
                        // 加载完成后将tag设置为more
                        lvFavourite.setTag(UIHelper.LISTVIEW_DATA_MORE);
                        lvFavouriteAdapter.notifyDataSetChanged();
                        lvFavourite_foot_more.setText(R.string.load_more);
                        lvFavourite.addFooterView(lvFavourite_footer);
                    }
                }
                break;
        }
    }
}
