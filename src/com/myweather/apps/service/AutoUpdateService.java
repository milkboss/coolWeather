package com.myweather.apps.service;

import com.myweather.apps.receiver.AutoUpdateReceive;
import com.myweather.apps.util.HttpCallbackListener;
import com.myweather.apps.util.HttpUtil;
import com.myweather.apps.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
		
	}
  /** 
   * 设置定时启动
   * 
   */
@Override
public int onStartCommand
(Intent intent, int flags, int startId) {
	
	new Thread(new Runnable(){

		@Override
		public void run() {
			updateweather();
		}
	}).start();
	
AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
int anHour=8*60*60*1000;//这是8小时的毫秒数
long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
Intent i=new Intent(this,AutoUpdateReceive.class);
PendingIntent pi=PendingIntent.getBroadcast
(this, 0, i, 0);
manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP
		, triggerAtTime, pi);

return super.onStartCommand(intent, flags, startId);

}
/**
 * 更新天气信息
 */
protected void updateweather() {
SharedPreferences pref=PreferenceManager
.getDefaultSharedPreferences(this);
String weatherCode =pref.getString("weather_code", "");
String address="http://www.weather.com.cn/data/cityinfo/"+
weatherCode+".html";
HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
	
	@Override
	public void onFinish(String response) {
Utility.handleWeatherResponse(AutoUpdateService.this, response);
Log.d("MainActivity", "服务重启了");
Log.d("MainActivity", "服务得到的信息为"+response);
	}
	
	@Override
	public void onError(Exception e) {
		e.printStackTrace();
		
	}
});
	
	
}
}
