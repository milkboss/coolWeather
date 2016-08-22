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
   * ���ö�ʱ����
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
int anHour=8*60*60*1000;//����8Сʱ�ĺ�����
long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
Intent i=new Intent(this,AutoUpdateReceive.class);
PendingIntent pi=PendingIntent.getBroadcast
(this, 0, i, 0);
manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP
		, triggerAtTime, pi);

return super.onStartCommand(intent, flags, startId);

}
/**
 * ����������Ϣ
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
Log.d("MainActivity", "����������");
Log.d("MainActivity", "����õ�����ϢΪ"+response);
	}
	
	@Override
	public void onError(Exception e) {
		e.printStackTrace();
		
	}
});
	
	
}
}
