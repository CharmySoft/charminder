package com.pujoy.charminder.data;

import java.util.Calendar;
import java.util.Locale;

import com.pujoy.charminder.R;
import com.pujoy.charminder.activities.ReminderListActivity;
import com.pujoy.charminder.activities.WakeUpScreen;
import com.pujoy.charminder.other.G;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class Reminder {
	public int iType;//Reminder Type starting from 1
	public Calendar mTimeCreated;
	public Calendar mTimeToRemind;
	public String sNote;
	public boolean bValidity;
	public int iPriority;
	public int iRepeat;
	public String sTitle;
	public String sLocation;
	public String sTimePhrase;
	public Reminder(int type_of_reminder){
		mTimeCreated = Calendar.getInstance();
		mTimeToRemind = Calendar.getInstance();
		bValidity = true;
		iType = type_of_reminder;
	}
	public void Notify(){
		if(G.settings.mPrioritySetting[iPriority - 1].bVibrate){
			Vibrator v = (Vibrator)G.context.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = {0, 100, 100, 100, 100};
			v.vibrate(pattern, -1);
		}
		if(G.settings.mPrioritySetting[iPriority - 1].bSound){
			RingtoneManager.getRingtone(G.context, 
					RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
		}
		if(G.settings.mPrioritySetting[iPriority - 1].bWakeScreen){
			PowerManager powerManager = (PowerManager) G.context.getSystemService(Context.POWER_SERVICE);
			if (!powerManager.isScreenOn())
				G.context.startActivity(new Intent(G.context, WakeUpScreen.class));
		}
		if(G.settings.mPrioritySetting[iPriority - 1].bBubble){
			G.mCharmy.pushBubble(formatNotificationText(this));
		}
		if(G.settings.mPrioritySetting[iPriority - 1].bNotification){
			NotificationManager notificationManager = (NotificationManager) 
					  G.context.getSystemService(Context.NOTIFICATION_SERVICE); 
			TypedArray drawable = G.context.getResources().obtainTypedArray(
					R.array.reminder_list_icons);
			String[] names = G.context.getResources().getStringArray(
					R.array.main_menu_names);
			Intent intent = new Intent(G.context, ReminderListActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(G.context, 0, intent, 0);
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(G.context)
				    .setSmallIcon(drawable.getResourceId(iType - 1, -1))
			        .setContentIntent(pIntent)
			        .setContentTitle(names[iType - 1])
			        .setContentText(formatNotificationText(this));
			notificationManager.notify(1, mBuilder.build()); 
			drawable.recycle();
		}
		switch(iRepeat){
		case 0:// Never
			bValidity = false;
			break;
		case 1:// Hourly
			mTimeToRemind.add(Calendar.HOUR, 1);
			break;
		case 2:// Daily
			mTimeToRemind.add(Calendar.DAY_OF_MONTH, 1);
			break;
		case 3:// Weekly
			mTimeToRemind.add(Calendar.WEEK_OF_MONTH, 1);
			break;
		case 4:// Monthly
			mTimeToRemind.add(Calendar.MONTH, 1);
			break;
		case 5:// Yearly
			mTimeToRemind.add(Calendar.YEAR, 1);
			break;
		}
	}
	
	public static String formatNotificationText(Reminder rem) {
		String[] bubble = G.context.getResources().getStringArray(
				R.array.bubble_notify);
		Calendar cal = Calendar.getInstance();
		return String.format(bubble[rem.iType - 1], formatRemindTime(rem), 
				String.valueOf(cal.get(Calendar.MINUTE)));
	}
	
	public static String formatTime(Calendar time){
		return time.get(Calendar.DAY_OF_MONTH) + "/" + (time.get(Calendar.MONTH) + 1) + "/"
				+ time.get(Calendar.YEAR) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" 
				+ time.get(Calendar.MINUTE);
	}
	
	public static String formatRemindTime(Reminder rem) {
		Calendar cal = rem.mTimeToRemind;
		String ret = new String();
		switch (rem.iType){
		case 1:
		{
			long diffs = rem.mTimeToRemind.getTimeInMillis() - rem.mTimeCreated.getTimeInMillis();
			return formatCountdownText(diffs);
		}
		case 2:
			//English
			if(G.getLanguage() == 0){
				if(cal.get(Calendar.YEAR) != rem.mTimeCreated.get(Calendar.YEAR)){
					ret = ", " + cal.get(Calendar.YEAR);
				}
				if(cal.get(Calendar.DAY_OF_MONTH) != rem.mTimeCreated.get(Calendar.DAY_OF_MONTH) || 
						cal.get(Calendar.MONTH) != rem.mTimeCreated.get(Calendar.MONTH) ||
						!ret.isEmpty()){
					ret = cal.get(Calendar.DAY_OF_MONTH) + " " + 
						cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) + ret;
				}
				if(!ret.isEmpty()){
					return cal.get(Calendar.HOUR_OF_DAY) + ":" + 
							cal.get(Calendar.MINUTE) + ", " + ret;
				}					
				return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);	
			}
			//Chinese
			else{
				if(cal.get(Calendar.YEAR) != rem.mTimeCreated.get(Calendar.YEAR)){
					ret += cal.get(Calendar.YEAR) + G.context.getString(R.string.unit_year);
				}
				if(cal.get(Calendar.MONTH) != rem.mTimeCreated.get(Calendar.MONTH) || !ret.isEmpty()){
					ret += cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CHINESE);
				}
				if(cal.get(Calendar.DAY_OF_MONTH) != rem.mTimeCreated.get(Calendar.DAY_OF_MONTH) || !ret.isEmpty()){
					ret += cal.get(Calendar.DAY_OF_MONTH) + G.context.getString(R.string.unit_day);
				}
				return ret + cal.get(Calendar.HOUR_OF_DAY) + G.context.getString(R.string.unit_hour)
						+ cal.get(Calendar.MINUTE) + G.context.getString(R.string.unit_minute);	
			}
		case 3:
			return String.valueOf(cal.get(Calendar.MINUTE)) + G.context.getString(R.string.unit_minute);
		case 4:
			return rem.sTimePhrase;
		}
		return ret;
	}

	public static String formatCountdownText(long diffs) {
		String ret = new String();
		int temp = (int) TimeHelper.getInDays(diffs);
		if(temp != 0)
		{
		ret = String.valueOf(temp) + G.context.getString(R.string.unit_day);
		}
		temp = (int) TimeHelper.getHours(diffs);
		if(temp != 0)
		{
		ret += String.valueOf(temp) + G.context.getString(R.string.unit_hour);
		}
		temp = (int) TimeHelper.getMinutes(diffs);
		if(temp != 0 || ret.isEmpty()){
			ret += String.valueOf(temp) + G.context.getString(R.string.unit_minute);
		}
		temp = (int) TimeHelper.getSeconds(diffs);
		if(temp != 0){
			ret += String.valueOf(temp) + G.context.getString(R.string.unit_second);
		}
		return ret;
	}
	

	static class TimeHelper{
		public static long getSeconds(long millis){
			return getInSeconds(millis) % 60;
		}
		public static long getMinutes(long millis){
			return getInMinutes(millis) % 60;
		}
		public static long getHours(long millis){
			return getInHours(millis) % 24;
		}

		public static long getInSeconds(long millis){
			return millis / (1000);
		}
		public static long getInMinutes(long millis){
			return millis / (60 * 1000);
		}
		public static long getInHours(long millis){
			return millis / (60 * 60 * 1000);
		}
		public static long getInDays(long millis){
			return millis / (24 * 60 * 60 * 1000);
		}
	}
}
