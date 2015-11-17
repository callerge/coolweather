package com.example.coolweather.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.coolweather.receiver.AutoUpdateReceiver;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.LogUtil;
import com.example.coolweather.util.Utility;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 输出当前时间
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 执行天气更新
				updateWeather();
				// 在这里执行Toast将会报错-----寻求解决
				// Toast.makeText(GlobalContext.getContext(),
				// "updateWeather executed:"+System.currentTimeMillis(),
				// Toast.LENGTH_SHORT).show();
				Looper.prepare();  
                Toast.makeText(  AutoUpdateService.this,  "updateWeather executed:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(new Date()), Toast.LENGTH_LONG).show();  
                Looper.loop();    
            }    
		}).start();
		// 定时打开广播接收器——20s一次
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		/*
		 * type还可以用RTC，RTC_WAKEUP，对应的触发时间应该使用System.currenTimetMillis()
		 */
		// (int type, long triggerAtMillis, PendingIntent operation)
		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP; // 从系统开机时累积的总时间
		long triggerAtMillis = SystemClock.elapsedRealtime() + 10 * 1000;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, i, 0);

		// 通过set定时执行可能会延迟，4.4之后，因为手机会有省电设计，如果要准确无误，用setExact()
		alarmManager.set(type, triggerAtMillis, operation);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		String address = "http://www.weather.com.cn/adat/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// response是服务器返回的天气信息，所以还需要解析存储
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();

			}
		});
		LogUtil.d("test", "同步天气得到执行");
	}

}
