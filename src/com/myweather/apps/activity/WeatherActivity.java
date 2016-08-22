package com.myweather.apps.activity;


import com.myweather.appss.R;
import com.myweather.apps.service.AutoUpdateService;
import com.myweather.apps.util.HttpCallbackListener;
import com.myweather.apps.util.HttpUtil;
import com.myweather.apps.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.youmi.android.normal.banner.BannerManager;

public class WeatherActivity extends Activity implements OnClickListener {
   
	/**
	 * ����������ť
	 */
	private Button refreshWeather;
	/**
     * �л����а�ť
     */
    private Button switchCity;
    
	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ������������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ������
	 */
	private TextView currentDateText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//��ʼ�����ؼ�
	switchCity=(Button) findViewById(R.id.switch_city);
	refreshWeather=(Button) findViewById(R.id.refresh_weather);
	switchCity.setOnClickListener(this);
	refreshWeather.setOnClickListener(this);
		
		
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		String countyCode=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ����ž�ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//û���ؼ�����ʱ����ʾ��������
			showWeather();
		}
		//�������׹��������Ϣ
		//ʵ���������
		// ʵ���������
		View adView = BannerManager.getInstance(this).getBanner(this);

		// ��ȡҪǶ�������Ĳ���
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

		// ����������뵽������
		adLayout.addView(adView);
		
		
		
	}
	/**
	 * ��sharedPreferences�ļ��ж�ȡ�洢��������Ϣ,������ʾ��������
	 */
	private void showWeather() {
SharedPreferences prefs=PreferenceManager.
getDefaultSharedPreferences(this);
cityNameText.setText(prefs.getString("city_name", ""));
temp1Text.setText(prefs.getString("temp1", ""));
temp2Text.setText(prefs.getString("temp2", ""));
weatherDespText.setText(prefs.getString("weather_desp", ""));
publishText.setText("����"+prefs.getString("publish_time", "")+"����");
currentDateText.setText(prefs.getString("current_date", ""));

weatherInfoLayout.setVisibility(View.VISIBLE);
cityNameText.setVisibility(View.VISIBLE);
		
	Intent intent=new Intent(this,AutoUpdateService.class);
	startService(intent);
	}
	/**
	 * @param countyCode
	 * ��ѯ�ؼ���������Ӧ����������
	 */
	private void queryWeatherCode(String countyCode) {
String address="http://www.weather.com.cn/data/list3/city"+
countyCode+".xml";
queryFromServer(address,"countyCode");
	}
	/**
	 * @param weatherCode
	 * ��ѯ������������Ӧ������
	 */
	private void queryweatherInfo(String weatherCode){
String address="http://www.weather.com.cn/data/cityinfo/"+
	weatherCode+".html";
queryFromServer(address,"weatherCode");
	}
	/**
	 * @param address
	 * @param string
	 * ��������ĵ�ַ������ȥ��������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(String address, final String type) {
HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
	
	@Override
	public void onFinish(String response) {
		if("countyCode".equals(type)){
	if(!TextUtils.isEmpty(response)){
		//�ӷ��������ص������н�������������
		String[] array=response.split("\\|");
		if(array!=null&&array.length==2){
			String weatherCode=array[1];
			queryweatherInfo(weatherCode);
		}
		
	}
		}else if("weatherCode".equals(type)){
			//�������ص�������Ϣ
Utility.handleWeatherResponse(WeatherActivity.this, response);	
runOnUiThread(new Runnable() {
	
	@Override
	public void run() {
		showWeather();
	}
});
		}
	}
	
	@Override
	public void onError(Exception e) {
	runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
		publishText.setText("ͬ��ʧ��");
			
		}
	});
		
	}
});	
		
	}
	/**
	 * ���ð�ť����¼�
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
		Intent intent=new Intent(this,ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
		SharedPreferences prefs=PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode=prefs.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode)){
		queryweatherInfo(weatherCode);
		break;
		}
		default:
			break;
		
		}
	}
	
	

}