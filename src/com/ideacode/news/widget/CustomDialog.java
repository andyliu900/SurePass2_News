package com.ideacode.news.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.adapter.ProvinceAdapter;
import com.ideacode.news.bean.CityEntity;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.listener.DialogButtonClickListener;
import com.ideacode.news.ui.TabUserActivity;
import com.ideacode.news.widget.adapter.ArrayWheelAdapter;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 * 
 */
public class CustomDialog extends Dialog {

	private static String TAG = "CustomDialog";

	private static boolean scrolling = false;

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private final Context context;
		private String title;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private int provinceid;
		private int cityid;

		private DialogButtonClickListener positiveButtonClickListener, negativeButtonClickListener;

		public Builder(Context context, int provinceid, int cityid) {
			this.context = context;
			this.provinceid = provinceid;
			this.cityid = cityid;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogButtonClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogButtonClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogButtonClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogButtonClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.custom_dialog, null);

			/* 构造wheel start */
			final WheelView province = (WheelView) layout.findViewById(R.id.country);
			province.setVisibleItems(5);
			province.setViewAdapter(new ProvinceAdapter(context));

			final WheelView city = (WheelView) layout.findViewById(R.id.city);
			city.setVisibleItems(5);

			province.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					Log.i(TAG, "provinceid = " + newValue);
					provinceid = newValue;
					// if (!scrolling) {
					// updateCities(city, CommonSetting.cities, newValue);
					// }
				}
			});

			city.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					Log.i(TAG, "cityid = " + newValue);
					cityid = newValue;
				}
			});

			province.addScrollingListener(new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {
					scrolling = true;
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					scrolling = false;
					updateCities(city, CommonSetting.cities, province.getCurrentItem());
				}
			});

			if (provinceid == -1) {
				province.setCurrentItem(3);
				updateCities(city, CommonSetting.cities, province.getCurrentItem());
			} else {
				province.setCurrentItem(provinceid);
				initializeCities(city, CommonSetting.cities, provinceid, cityid);
			}
			/* 构造wheel end */

			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE, provinceid, cityid);
							CommonSetting.ids = positiveButtonClickListener.getIds();
							Log.i(TAG, CommonSetting.ids[0] + "  " + CommonSetting.ids[1]);
							TabUserActivity.checkUpdate();
							dialog.dismiss();
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// negativeButtonClickListener.onClick(dialog,
							// DialogInterface.BUTTON_NEGATIVE);
							dialog.dismiss();
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
			}
			if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			dialog.setCancelable(false);
			return dialog;
		}

		/**
		 * Updates the city wheel
		 */
		private void updateCities(WheelView city, CityEntity cities[][], int index) {
			ArrayWheelAdapter<CityEntity> adapter = new ArrayWheelAdapter<CityEntity>(context, cities[index]);
			adapter.setTextSize(20);
			city.setViewAdapter(adapter);
			city.setCurrentItem(cities[index].length / 2);
		}

		/**
		 * 初次打开wheel时，显示的内容
		 * 
		 * @param city
		 * @param cities
		 * @param index
		 * @param cityid
		 */
		private void initializeCities(WheelView city, CityEntity cities[][], int index, int cityid) {
			ArrayWheelAdapter<CityEntity> adapter = new ArrayWheelAdapter<CityEntity>(context, cities[index]);
			adapter.setTextSize(20);
			city.setViewAdapter(adapter);
			city.setCurrentItem(cityid);
		}
	}
}