package com.ideacode.news.ui;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ideacode.news.R;
import com.ideacode.news.adapter.GridViewFaceAdapter;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class AddMoodActivity extends IdeaCodeActivity {

	private ImageButton mBack;
	private ImageButton mSend;
	private ProgressBar mRefresh;
	private EditText mContent;
	private Button mPlace;
	private TextView mCount;
	private ImageButton mPoi;
	private ImageButton mEmoticon;

	private AppContext application;
	private LocationClient mClient;
	private LocationClientOption mOption;

	private boolean mLBSIsReceiver;
	private String mLBSAddress;
	private Drawable mPoi_off_icon;
	private Drawable mPoi_on_icon;

	private String moodAddPoint;
	private String moodPointing;

	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_mood);

		application = (AppContext) this.getApplicationContext();

		initLBS();
		initViews();
		setListener();

        mPoi_off_icon = getResources().getDrawable(R.drawable.addmood_poi_icon);
        mPoi_off_icon.setBounds(0, 0, mPoi_off_icon.getMinimumWidth(), mPoi_off_icon.getMinimumHeight());
        mPoi_on_icon = getResources().getDrawable(R.drawable.addmood_poiactive_icon);
        mPoi_on_icon.setBounds(0, 0, mPoi_on_icon.getMinimumWidth(), mPoi_on_icon.getMinimumHeight());

		mClient.start();
		mLBSIsReceiver = true;
		mClient.requestLocation();

		// 初始化表情视图
		initGridView();
	}

	private void initLBS() {
		mOption = new LocationClientOption();
		mOption.setOpenGps(true);
		mOption.setCoorType("bd09ll");
		mOption.setAddrType("all");
		mOption.setScanSpan(100);
		mClient = new LocationClient(getApplicationContext(), mOption);
	}

	private void initViews() {
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		moodAddPoint = AddMoodActivity.this.getResources().getString(R.string.mood_add_point);
		moodPointing = AddMoodActivity.this.getResources().getString(R.string.mood_pointting);

		mBack = (ImageButton) findViewById(R.id.main_head_back_button);
		mSend = (ImageButton) findViewById(R.id.main_head_send_button);
		mRefresh = (ProgressBar) findViewById(R.id.main_head_progress);
		mContent = (EditText) findViewById(R.id.addmood_content);
		mPlace = (Button) findViewById(R.id.addmood_poi_place);
		mCount = (TextView) findViewById(R.id.addmood_count);
		mPoi = (ImageButton) findViewById(R.id.addmood_poi);
		mEmoticon = (ImageButton) findViewById(R.id.addmood_emoticon);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() > 0) {
					backDialog();
				} else {
					finish();
				}
			}
		});
		mSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() == 0) {
					UIHelper.ToastMessage(AddMoodActivity.this, R.string.mood_send_empty);
				} else {
                    if (mContent.getText().length() > 300) {
                        UIHelper.ToastMessage(AddMoodActivity.this, R.string.mood_send_toolong_error);
                    } else {
                        Mood mood = new Mood();
                        mood.setUserId(application.getLoginUid());
                        mood.setUserName(application.getLoginInfo().getUserName());
                        mood.setMoodContent(mContent.getText().toString());
                        mood.setMoodPraiseCount(0);
                        mood.setMoodBelittleCount(0);
                        if (mPlace.getText().toString().equals(moodAddPoint) || mPlace.getText().toString().equals(moodPointing)) {
                            mood.setMoodLocation("");
                        } else {
                            mood.setMoodLocation(mPlace.getText().toString());
                        }

                        HashMap params = new HashMap();
                        params.put("mood", mood);
                        Task ts = new Task(TaskType.TS_EXAM_SEND_MOOD, params);
                        MainService.newTask(ts);

                        mSend.setVisibility(ImageButton.GONE);
                        mRefresh.setVisibility(ImageButton.VISIBLE);
                    }
				}
			}
		});
		mContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = s.length();
				mCount.setText(String.valueOf(number));
				selectionStart = mContent.getSelectionStart();
				selectionEnd = mCount.getSelectionEnd();
				if (temp.length() > 300) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mContent.setText(s);
					mContent.setSelection(tempSelection);
				}
			}
		});
		mContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 显示软键盘&隐藏表情
				showIMM();
			}
		});
		mPlace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mPlace.getText().toString().equals(moodAddPoint)) {
					mLBSIsReceiver = true;
					mPlace.setText(R.string.mood_pointting);
					mPoi.setImageResource(R.drawable.addmood_poi_button_on);
					if (!mClient.isStarted()) {
						mClient.start();
					}
					mClient.requestLocation();
				} else if (mPlace.getText().toString().equals(moodPointing)) {
					if (mClient.isStarted()) {
						mClient.stop();
						mLBSIsReceiver = false;
						mLBSAddress = null;
						mPlace.setCompoundDrawables(mPoi_off_icon, null, null, null);
						mPlace.setText(R.string.mood_add_point);
						mPoi.setImageResource(R.drawable.addmood_poi_button);
					}
				}
			}
		});
		mPoi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLBSIsReceiver) {
					mLBSIsReceiver = false;
					mLBSAddress = null;
					mPlace.setCompoundDrawables(mPoi_off_icon, null, null, null);
					mPlace.setText(R.string.mood_add_point);
					mPoi.setImageResource(R.drawable.addmood_poi_button);
				} else {
					mLBSIsReceiver = true;
					mPlace.setText(R.string.mood_pointting);
					mPoi.setImageResource(R.drawable.addmood_poi_button_on);
					if (!mClient.isStarted()) {
						mClient.start();
					}
					mClient.requestLocation();
				}
			}
		});
		mEmoticon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showOrHideIMM();
			}
		});
		mClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceivePoi(BDLocation arg0) {

			}

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				mLBSAddress = arg0.getAddrStr();
				application.mLocation = arg0.getAddrStr();
				application.mLatitude = arg0.getLatitude();
				application.mLongitude = arg0.getLongitude();
				handler.sendEmptyMessage(1);
			}
		});
	}

	private void initGridView() {
		mGVFaceAdapter = new GridViewFaceAdapter(this);
		mGridView = (GridView) findViewById(R.id.addmood_face_emoticons);
		mGridView.setAdapter(mGVFaceAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 插入的表情
                SpannableString ss = new SpannableString(view.getTag().toString());
                Drawable d = getResources().getDrawable((int) mGVFaceAdapter.getItemId(position));
                d.setBounds(0, 0, 40, 40);// 设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 在光标所在处插入表情
                mContent.getText().insert(mContent.getSelectionStart(), ss);
			}
		});
	}

	private void showIMM() {
		mEmoticon.setTag(1);
		showOrHideIMM();
	}

	private void showFace() {
		mEmoticon.setTag(1);
		mGridView.setVisibility(View.VISIBLE);
	}

	private void hideFace() {
		mEmoticon.setTag(null);
		mGridView.setVisibility(View.GONE);
	}

	private void showOrHideIMM() {
		if (mEmoticon.getTag() == null) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);
			// 显示表情
			showFace();
		} else {
			// 显示软键盘
			imm.showSoftInput(mContent, 0);
			// 隐藏表情
			hideFace();
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (mLBSAddress != null) {
					mPlace.setText(mLBSAddress);
					mPlace.setCompoundDrawables(mPoi_on_icon, null, null, null);
				}
				break;
			}
		}
	};

	private void backDialog() {
		AlertDialog.Builder builder = new Builder(AddMoodActivity.this);
		builder.setTitle(R.string.mood_dialog_title);
		builder.setMessage(R.string.mood_dialog_message);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mContent.getText().toString().trim().length() > 0) {
				backDialog();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];

		mSend.setVisibility(ImageButton.VISIBLE);
		mRefresh.setVisibility(ImageButton.GONE);
		switch (type) {
		case TaskType.TS_EXAM_SEND_MOOD:
			if (param[2] != null && (Integer) param[2] != 0) {
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				int code = (Integer) param[1];
				if (code == CommonSetting.Success) {
					Intent intent = new Intent();
					setResult(TaskType.TS_EXAM_SEND_MOOD, intent);
					finish();
					UIHelper.ToastMessage(this, R.string.msg_sendmood_success);
				} else {
					UIHelper.ToastMessage(this, R.string.msg_sendmood_error);
				}
			}
			break;
		}
	}

}
