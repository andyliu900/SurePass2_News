package com.ideacode.news.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.adapter.ListViewNewsAdapter;
import com.ideacode.news.adapter.ListViewUserMoodAdapter;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.listener.ChoiceOnClickListener;
import com.ideacode.news.listener.DialogButtonClickListener;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;
import com.ideacode.news.widget.CustomDialog;
import com.ideacode.news.widget.NewDataToast;
import com.ideacode.news.widget.PullToRefreshListView;
import com.ideacode.news.widget.ScrollLayout;

/**
 * MySpace(我的空间)
 * 
 * 2013-4-22下午5:25:51
 */
public class TabUserActivity extends IdeaCodeActivity {

	private static final String TAG = "TabUserActivity";

	private AppContext appContext;

	private ProgressBar mTitleProgress;
	private Button bt_head_mood;
	private Button bt_head_userinfo;
	private Button bt_head_favourite;
	private static Button submit_btn;
	private ImageButton refreshButton;// 刷新按钮

	private ScrollLayout mScrollLayout;
	private int mViewCount;
	private int mCurSel;

	private PullToRefreshListView lvMood;
	private View lvMood_footer;
	private TextView headTv, lvMood_foot_more;
	private ProgressBar lvMood_foot_progress;
	private ListViewUserMoodAdapter lvMoodAdapter;
	private ArrayList<Mood> lvMoodData = new ArrayList<Mood>();
	private int moodNowpage = 1;

	private PullToRefreshListView lvFavourite;
	private View lvFavourite_footer;
	private TextView lvFavourite_foot_more;
	private ProgressBar lvFavourite_foot_progress;
	private ListViewNewsAdapter lvFavouriteAdapter;
	private ArrayList<Map<String, Object>> lvNewsData = new ArrayList<Map<String, Object>>();
	private int favouriteNowpage = 1;

	// 用户信息相关对象声明
	private static TextView username_tv, useremail_tv, usersex_tv, userlocation_tv, usersummary_tv, birthday_tv, qq_tv;
	private Calendar c = null;

	private final int SEX_PICK_DIALOG = 1;
	private final int DATE_PICK_DIALOG = 2;
	private final int LOCATION_PICK_DIALOG = 3;

	// 全局变量保存原始用户信息
	private static TbUser tbUser = null;
	private static int provinceid = -1;
	private static int cityid = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		appContext = (AppContext) getApplication();
		initViews();
		initUserMoodViews();
		initUserInfoViews();
		initFavouriteViews();
		initPageScroll();

        if (appContext.isLogin()) {
			loadUserMoodList(UIHelper.LISTVIEW_ACTION_INIT);
			loadUserInfo();
			loadUserFavouriteList(UIHelper.LISTVIEW_ACTION_INIT);
		} else {
			refreshButton.setVisibility(View.GONE);
			lvMood_foot_more.setText(R.string.load_empty);
			lvMood_foot_progress.setVisibility(View.GONE);
			lvFavourite_foot_more.setText(R.string.load_empty);
			lvFavourite_foot_progress.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!appContext.isLogin() && AppContext.SHOW_LOGIN_FLAG) {
			UIHelper.showLoginDialog(this);
			cleanData();
		} else {
            // refreshButton.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		mTitleProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		refreshButton = (ImageButton) findViewById(R.id.main_head_refresh_button);
		refreshButton.setOnClickListener(onClickListener);
		headTv = (TextView) findViewById(R.id.systv);
		headTv.setText(R.string.user_space_name);
		bt_head_mood = (Button) findViewById(R.id.activity_user_head_mood);
		bt_head_userinfo = (Button) findViewById(R.id.activity_user_head_userinfo);
		bt_head_favourite = (Button) findViewById(R.id.activity_user_head_favourite);
		bt_head_mood.setOnClickListener(btnOnClick(bt_head_mood));
		bt_head_userinfo.setOnClickListener(btnOnClick(bt_head_userinfo));
		bt_head_favourite.setOnClickListener(btnOnClick(bt_head_favourite));

		bt_head_mood.setEnabled(false);
	}

	private void initUserInfoViews() {
		username_tv = (TextView) findViewById(R.id.user_info_username);
		useremail_tv = (TextView) findViewById(R.id.useremail_tv);
		usersex_tv = (TextView) findViewById(R.id.usersex_tv);
		userlocation_tv = (TextView) findViewById(R.id.userlocation_tv);
		usersummary_tv = (TextView) findViewById(R.id.usersummary_tv);
		birthday_tv = (TextView) findViewById(R.id.birthday_tv);
		qq_tv = (TextView) findViewById(R.id.qq_tv);
		submit_btn = (Button) this.findViewById(R.id.submit_bt);
		submit_btn.setOnClickListener(onClickListener);
	}

	/**
	 * 初始化水平翻页
	 */
	private void initPageScroll() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.user_scrolllayout);
		mViewCount = mScrollLayout.getChildCount();
		mCurSel = 0;
		mScrollLayout.snapToScreen(mCurSel);
		mScrollLayout.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
			@Override
			public void OnViewChange(int viewIndex) {
				// 切换列表视图-如果列表数据为空：加载数据
				switch (viewIndex) {
				case 0:// 我的心情
					bt_head_mood.setEnabled(false);
					bt_head_userinfo.setEnabled(true);
					bt_head_favourite.setEnabled(true);
					if (!appContext.isLogin()) {
						UIHelper.showLoginDialog(TabUserActivity.this);
					}
					break;
				case 1:// 个人信息
					bt_head_mood.setEnabled(true);
					bt_head_userinfo.setEnabled(false);
					bt_head_favourite.setEnabled(true);
					if (!appContext.isLogin()) {
						UIHelper.showLoginDialog(TabUserActivity.this);
					}
					break;
				case 2:// 我的收藏
					bt_head_mood.setEnabled(true);
					bt_head_userinfo.setEnabled(true);
					bt_head_favourite.setEnabled(false);
					if (!appContext.isLogin()) {
						UIHelper.showLoginDialog(TabUserActivity.this);
					}
					break;
				}
			}
		});
	}

	private void initUserMoodViews() {
		lvMoodAdapter = new ListViewUserMoodAdapter(this, lvMoodData, R.layout.usermood_listitem);
		lvMood_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvMood_foot_more = (TextView) lvMood_footer.findViewById(R.id.listview_foot_more);
		lvMood_foot_more.setText(R.string.load_more);
		lvMood_foot_progress = (ProgressBar) lvMood_footer.findViewById(R.id.listview_foot_progress);
		lvMood = (PullToRefreshListView) findViewById(R.id.user_mood_listview);
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
					loadUserMoodList(UIHelper.LISTVIEW_ACTION_SCROLL);
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
				loadUserMoodList(UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	private void initFavouriteViews() {
		lvFavouriteAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
		lvFavourite_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvFavourite_foot_more = (TextView) lvFavourite_footer.findViewById(R.id.listview_foot_more);
		lvFavourite_foot_more.setText(R.string.load_more);
		lvFavourite_foot_progress = (ProgressBar) lvFavourite_footer.findViewById(R.id.listview_foot_progress);
		lvFavourite = (PullToRefreshListView) findViewById(R.id.user_favourite_listview);
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
				Intent intent = new Intent(TabUserActivity.this, NewsDetailActivity.class);
				intent.putExtra("newsType", Integer.parseInt(newsTitle.get("newsType").toString()));
				intent.putExtra("newsDetail_url", newsTitle.get("url").toString());
				TabUserActivity.this.startActivity(intent);
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
					loadUserFavouriteList(UIHelper.LISTVIEW_ACTION_SCROLL);
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
				loadUserFavouriteList(UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	private final View.OnClickListener btnOnClick(final Button btn) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnEnable(btn);
			}
		};
	}

	private final OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_head_refresh_button:
				mTitleProgress.setVisibility(ProgressBar.VISIBLE);
				refreshButton.setVisibility(ImageButton.GONE);
				if (mScrollLayout.getCurScreen() == 0) {
					loadUserMoodList(UIHelper.LISTVIEW_ACTION_REFRESH);
				}
				if (mScrollLayout.getCurScreen() == 1) {
					loadUserInfo();
				}
				if (mScrollLayout.getCurScreen() == 2) {
					loadUserFavouriteList(UIHelper.LISTVIEW_ACTION_REFRESH);
				}
				break;
			case R.id.submit_bt:
				TbUser tbUser = appContext.getLoginInfo();
				long userId = tbUser.getUserId();
				tbUser.setUserId(userId);
				tbUser.setUserEmail(useremail_tv.getText().toString());
				tbUser.setUserSex(usersex_tv.getText().toString());
				tbUser.setUserProvinceId(CommonSetting.ids[0]);
				tbUser.setUserCityId(CommonSetting.ids[1]);
				tbUser.setUserSummary(usersummary_tv.getText().toString());
				tbUser.setUserBirthday(StringUtils.toDate2(birthday_tv.getText().toString()));
				tbUser.setUserQQ(qq_tv.getText().toString());
				tbUser.setTaskType(TaskType.TS_EXAM_UPDATEUSERINFO);

				Intent intent = new Intent();
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(CommonSetting.FileNameTag, tbUser);
				intent.putExtras(mBundle);
				intent.setClass(TabUserActivity.this, LoadingActivity.class);
				startActivityForResult(intent, 0);
				break;
			}
		}
	};

	private void btnEnable(Button btn) {
		if (btn == bt_head_mood) {
			bt_head_mood.setEnabled(false);
			bt_head_userinfo.setEnabled(true);
			bt_head_favourite.setEnabled(true);
			mScrollLayout.snapToScreen(0);
		}
		if (btn == bt_head_userinfo) {
			bt_head_mood.setEnabled(true);
			bt_head_userinfo.setEnabled(false);
			bt_head_favourite.setEnabled(true);
			mScrollLayout.snapToScreen(1);
		}
		if (btn == bt_head_favourite) {
			bt_head_mood.setEnabled(true);
			bt_head_userinfo.setEnabled(true);
			bt_head_favourite.setEnabled(false);
			mScrollLayout.snapToScreen(2);
		}
	}

	    /**
     * 加载用户所发的心情数据
     * 
     * @param action
     *            动作标识
     */
	private void loadUserMoodList(final int action) {
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
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_MOOD, params);
			MainService.newTask(ts);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			Paging p = new Paging(moodNowpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE, params);
			MainService.newTask(ts);
		} else {
			Paging p = new Paging(moodNowpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_MOOD, params);
			MainService.newTask(ts);
		}
	}

	/**
	 * 加载用户资料
	 */
	private void loadUserInfo() {
		HashMap params = new HashMap();
		params.put("userId", appContext.getLoginUid());
		Task ts = new Task(TaskType.TS_EXAM_USER_INFO, params);
		MainService.newTask(ts);
	}

	/**
	 * 加载用户新闻收藏夹
	 * 
	 * @param action
	 */
	private void loadUserFavouriteList(int action) {
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
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_FAVOURITE, params);
			MainService.newTask(ts);
		} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {
			Paging p = new Paging(favouriteNowpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE, params);
			MainService.newTask(ts);
		} else {
			Paging p = new Paging(favouriteNowpage, AppContext.PAGE_SIZE);
			HashMap params = new HashMap();
			params.put("userId", appContext.getLoginUid());
			params.put("paging", p);
			params.put("isRefresh", isRefresh);
			Task ts = new Task(TaskType.TS_EXAM_SEARCH_USER_FAVOURITE, params);
			MainService.newTask(ts);
		}
	}

	/**
	 * 修改用户邮箱
	 * 
	 * @param view
	 */
	public void changeUserEmail(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			HashMap map = new HashMap();
			map.put("userEmail", useremail_tv.getText().toString());
			Task task = new Task(TaskType.TS_EXAM_CHANGEUSEREMAIL, map);

			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(CommonSetting.FileNameTag, task);
			intent.putExtras(mBundle);
			intent.setClass(TabUserActivity.this, EditUserInfoActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	/**
	 * 选择性别
	 * 
	 * @param view
	 */
	public void choiceSex(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			showDialog(SEX_PICK_DIALOG);
		}
	}

	/**
	 * 修改所在地
	 * 
	 * @param view
	 */
	public void changeUserLocation(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			showDialog(LOCATION_PICK_DIALOG);
		}
	}

	/**
	 * 修改用户简介
	 * 
	 * @param view
	 */
	public void changeUserSummary(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			HashMap map = new HashMap();
			map.put("userSummary", usersummary_tv.getText().toString());
			Task task = new Task(TaskType.TS_EXAM_CHANGEUSERSUMMARY, map);

			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(CommonSetting.FileNameTag, task);
			intent.putExtras(mBundle);
			intent.setClass(TabUserActivity.this, EditUserInfoActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	/**
	 * 修改生日
	 * 
	 * @param view
	 */
	public void changeBirthday(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			showDialog(DATE_PICK_DIALOG);
		}
	}

	/**
	 * 
	 * @param view
	 */
	public void changeQQ(View view) {
		if (!appContext.isLogin()) {
			UIHelper.showLoginDialog(this);
		} else {
			HashMap map = new HashMap();
			map.put("userQq", qq_tv.getText().toString());
			Task task = new Task(TaskType.TS_EXAM_CHANGEQQ, map);

			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putSerializable(CommonSetting.FileNameTag, task);
			intent.putExtras(mBundle);
			intent.setClass(TabUserActivity.this, EditUserInfoActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Task task = null;
		String update_txt = null;
		if (data != null) {
			task = (Task) data.getSerializableExtra(CommonSetting.FileNameTag);
			update_txt = task.getTaskParam().get("update_txt").toString();
		}

		switch (resultCode) {
		case TaskType.TS_EXAM_CHANGEUSEREMAIL:
			useremail_tv.setText(update_txt);
			break;
		case TaskType.TS_EXAM_CHANGEUSERSUMMARY:
			usersummary_tv.setText(update_txt);
			break;
		case TaskType.TS_EXAM_CHANGEQQ:
			qq_tv.setText(update_txt);
			break;
		case TaskType.TS_EXAM_UPDATEUSERINFO_SUCCESS:
			submit_btn.setEnabled(false);
			break;
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case SEX_PICK_DIALOG:
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("性别");
			final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
			builder.setSingleChoiceItems(R.array.sexArray, 0, choiceListener);

			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {

					int choiceWhich = choiceListener.getWhich();
					String sexStr = getResources().getStringArray(R.array.sexArray)[choiceWhich];
					usersex_tv.setText(sexStr);
					checkUpdate();
				}
			};
			builder.setPositiveButton("确定", btnListener);
			dialog = builder.create();
			break;
		case DATE_PICK_DIALOG:
			if ("".equals(birthday_tv.getText())) {
				c = Calendar.getInstance();
				dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
						String datePickStr = year + "-" + (month < 10 ? "0" + (month + 1) : (month + 1)) + "-"
								+ (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
						birthday_tv.setText(datePickStr);
						checkUpdate();
					}
				}, c.get(Calendar.YEAR), // 传入年份
						c.get(Calendar.MONTH), // 传入月份
						c.get(Calendar.DAY_OF_MONTH) // 传入天数
				);
			} else {
				String[] dates = birthday_tv.getText().toString().split("-");
				dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
						String datePickStr = year + "-" + (month < 10 ? "0" + (month + 1) : (month + 1)) + "-"
								+ (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
						birthday_tv.setText(datePickStr);
						checkUpdate();
					}
				}, Integer.parseInt(dates[0]), // 传入年份
						Integer.parseInt(dates[1]) - 1, // 传入月份
						Integer.parseInt(dates[2]) // 传入天数
				);
			}

			break;
		case LOCATION_PICK_DIALOG:
			CustomDialog.Builder customBuilder = new CustomDialog.Builder(TabUserActivity.this, provinceid, cityid);
			DialogButtonClickListener negativeButtonClickListener = new DialogButtonClickListener();
			final DialogButtonClickListener positiveButtonClickListener = new DialogButtonClickListener();
			customBuilder.setTitle(this.getResources().getString(R.string.locationDialogTitle)).setNegativeButton("取消", negativeButtonClickListener)
					.setPositiveButton("确定", positiveButtonClickListener);
			dialog = customBuilder.create();
			break;
		}
		return dialog;
	}

	/**
	 * 检查数据是否更新
	 */
	public static void checkUpdate() {
		if (!tbUser.getUserName().equals(username_tv.getText().toString()) || !tbUser.getUserEmail().equals(useremail_tv.getText().toString())
				|| !tbUser.getUserSummary().equals(usersummary_tv.getText().toString()) || !tbUser.getUserQQ().equals(qq_tv.getText().toString())
				|| !tbUser.getUserSex().equals(usersex_tv.getText().toString())
				|| !StringUtils.date2String(tbUser.getUserBirthday()).equals(birthday_tv.getText().toString()) || provinceid != CommonSetting.ids[0]
				|| cityid != CommonSetting.ids[1]) {
			if (provinceid != CommonSetting.ids[0] || cityid != CommonSetting.ids[1]) {
				userlocation_tv.setText(CommonSetting.provinces[CommonSetting.ids[0]].getName() + "  "
						+ CommonSetting.cities[CommonSetting.ids[0]][CommonSetting.ids[1]].getName());
			}
			submit_btn.setEnabled(true);
		} else {
			if ((provinceid == CommonSetting.ids[0] && provinceid != -1) || (cityid == CommonSetting.ids[1] && cityid != -1)) {
				userlocation_tv.setText(CommonSetting.provinces[CommonSetting.ids[0]].getName() + "  "
						+ CommonSetting.cities[CommonSetting.ids[0]][CommonSetting.ids[1]].getName());
			}
			submit_btn.setEnabled(false);
		}
	}

	private void cleanData() {
		tbUser = null;
		lvMoodData.clear();
		lvNewsData.clear();

		username_tv.setText("");
		useremail_tv.setText("");
		usersex_tv.setText("");
		userlocation_tv.setText("");
		usersummary_tv.setText("");
		birthday_tv.setText("");
		qq_tv.setText("");

		lvMoodAdapter = new ListViewUserMoodAdapter(this, lvMoodData, R.layout.usermood_listitem);
		lvMood_foot_more.setText(R.string.load_empty);
		lvMood.setAdapter(lvMoodAdapter);
		lvFavouriteAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
		lvFavourite_foot_more.setText(R.string.load_empty);
		lvFavourite.setAdapter(lvFavouriteAdapter);
	}

	@Override
	public void init() {
		if (tbUser != null) {
			checkUpdate();
		}
	}

	@Override
	public void refresh(Object... param) {
		lvMood_foot_progress.setVisibility(ProgressBar.GONE);
		lvFavourite_foot_progress.setVisibility(ProgressBar.GONE);
		mTitleProgress.setVisibility(ProgressBar.GONE);
		refreshButton.setVisibility(ImageButton.VISIBLE);
		int type = (Integer) param[0];
		switch (type) {
		case TaskType.TS_EXAM_SEARCH_USER_MOOD:
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
					lvMoodAdapter = new ListViewUserMoodAdapter(this, lvMoodData, R.layout.usermood_listitem);
					lvMood.setAdapter(lvMoodAdapter);
					lvMood.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvMood_foot_more.setText(R.string.load_empty);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
				} else {
					lvMoodData = moodList;
					lvMoodAdapter = new ListViewUserMoodAdapter(this, lvMoodData, R.layout.usermood_listitem);
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
		case TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE:
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
					ListViewUserMoodAdapter resultadapter = (ListViewUserMoodAdapter) listAdapter.getWrappedAdapter();
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
		case TaskType.TS_EXAM_USER_INFO:
			submit_btn.setEnabled(false);
			if (param[2] != null && (Integer) param[2] != 0) {
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				tbUser = (TbUser) param[1];
				if (tbUser.getUserName() == null) {
					UIHelper.ToastMessage(this, R.string.msg_getuserinfo_failure);
				} else {
					provinceid = tbUser.getUserProvinceId();
					cityid = tbUser.getUserCityId();
					CommonSetting.ids[0] = tbUser.getUserProvinceId();
					CommonSetting.ids[1] = tbUser.getUserCityId();
					username_tv.setText(tbUser.getUserName());
					useremail_tv.setText(tbUser.getUserEmail());
					usersex_tv.setText(tbUser.getUserSex());
					if (provinceid != -1 && cityid != -1) {
						userlocation_tv.setText(CommonSetting.provinces[tbUser.getUserProvinceId()].getName() + "  "
								+ CommonSetting.cities[tbUser.getUserProvinceId()][tbUser.getUserCityId()].getName());
					} else {
						userlocation_tv.setText("");
					}
					usersummary_tv.setText(tbUser.getUserSummary());
					String birthday = StringUtils.date2String(tbUser.getUserBirthday());
					birthday_tv.setText(birthday);
					qq_tv.setText(tbUser.getUserQQ());
				}
			}
			break;
		case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE:
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
					lvFavouriteAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
					lvFavourite.setAdapter(lvFavouriteAdapter);
					lvFavourite.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvFavourite_foot_more.setText(R.string.load_empty);
					NewDataToast.makeText(this, getString(R.string.new_data_toast_none)).show();
				} else {
					lvNewsData = newsList;
					lvFavouriteAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
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
		case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE:
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
					ListViewNewsAdapter resultadapter = (ListViewNewsAdapter) listAdapter.getWrappedAdapter();
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
