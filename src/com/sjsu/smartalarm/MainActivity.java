package com.sjsu.smartalarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView tvSelectedTime;
	private Button btnChangeTime, btnSetAlarm, btnStopAlarm;

	private int hour;
	private int minute;
	private Toast mToast;

	static final int TIME_DIALOG_ID = 999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setCurrentTimeOnView();
		addListenerOnButton();

		btnSetAlarm = (Button) findViewById(R.id.button1);
		btnSetAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AlarmRecieverActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						MainActivity.this, 2, intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				Calendar alarmCal = Calendar.getInstance();
				alarmCal.setTimeInMillis(System.currentTimeMillis());
				alarmCal.set(Calendar.HOUR_OF_DAY, hour);
				alarmCal.set(Calendar.MINUTE, minute);
				alarmCal.set(Calendar.SECOND, 0);
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(),
						pendingIntent);

				getPreferences(MODE_PRIVATE).edit()
						.putString("hour", String.valueOf(hour)).commit();
				getPreferences(MODE_PRIVATE).edit()
						.putString("minute", String.valueOf(minute)).commit();

				if (mToast != null) {
					mToast.cancel();
				}
				mToast = Toast.makeText(getApplicationContext(), "Alarm set",
						Toast.LENGTH_LONG);
				mToast.show();
			}
		});

		btnStopAlarm = (Button) findViewById(R.id.button3);
		btnStopAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AlarmRecieverActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						MainActivity.this, 2, intent, 0);
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(pendingIntent);

				getPreferences(MODE_PRIVATE).edit().putString("hour", "-")
						.commit();
				getPreferences(MODE_PRIVATE).edit().putString("minute", "-")
						.commit();
				tvSelectedTime.setText(new StringBuilder().append(("-"))
						.append(":").append(("-")));

				if (mToast != null) {
					mToast.cancel();
				}
				mToast = Toast.makeText(getApplicationContext(),
						"Alarm Cancelled", Toast.LENGTH_LONG);
				mToast.show();

			}
		});

	}

	public void setCurrentTimeOnView() {

		tvSelectedTime = (TextView) findViewById(R.id.tvSelectedTime);

		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		String setHour = getPreferences(MODE_PRIVATE).getString("hour", "-");
		String setMinute = getPreferences(MODE_PRIVATE)
				.getString("minute", "-");

		tvSelectedTime.setText(new StringBuilder().append((setHour))
				.append(":").append((setMinute)));

	}

	public void addListenerOnButton() {

		btnChangeTime = (Button) findViewById(R.id.btnChangeTime);

		btnChangeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListener, hour, minute,
					false);

		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;

			tvSelectedTime.setText(new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)));
			btnSetAlarm.performClick();
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("onResume", "onResume");
		setCurrentTimeOnView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("onActivityResult", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		String setHour = getPreferences(MODE_PRIVATE).getString("hour", "-");
		String setMinute = getPreferences(MODE_PRIVATE)
				.getString("minute", "-");
		tvSelectedTime.setText(new StringBuilder().append((setHour))
				.append(":").append((setMinute)));
	}

}
