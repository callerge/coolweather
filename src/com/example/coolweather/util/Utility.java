package com.example.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

/**
 * 
 * ���ڽ������������ص�����.<br/>
 * api:http://www.weather.com.cn/adat/cityinfo/101010100.html<br/>
 * ��ʽ��{"weatherinfo":{"city":"����","cityid":"101010100","temp1":"15��","temp2":
 * "5��","weather":"����","img1":"d1.gif","img2":"n1.gif","ptime":"08:00"}}
 * ���У�����id��temp1����£�temp2����£�weather������Ϣ������img1��img2��Ӧ����ͼƬ��ptimeΪ��������ʱ��.<br/>
 * 
 * 
 */

public class Utility {

	/**
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢��Province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// ���������������ݴ洢��City��
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// ���������������ݴ洢��County��
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �������������ص��������ݣ������뵽����SharedPreferences
	 */
	public static void handleWeatherResponse(Context context, String response) {
		/*
		 * {"weatherinfo":
		 * {"city":"��ɽ","cityid":"101190404","temp1":"20��","temp2":"11��",
		 * "weather":"С��","img1":"d7.gif","img2":"n7.gif","ptime":"08:00"} }
		 */
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weather = weatherInfo.getString("weather");
			String ptime = weatherInfo.getString("ptime");

			// �洢
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weather,ptime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����������ص����ݴ洢��SharedPreferences�ļ�
	 * @param contex
	 * @param cityName ������
	 * @param weatherCode ���д���
	 * @param temp1 ����¶�
	 * @param temp2 ����¶�
	 * @param weather ��������
	 * @param ptime ����ʱ��
	 */
	public static void saveWeatherInfo(Context contex,String cityName, String weatherCode,
			String temp1, String temp2, String weather, String ptime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(contex).edit(); 
		
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weather);
		editor.putString("publish_time", ptime);
		editor.putString("current_date", sdf.format(new Date()));
		
		editor.commit();
	}
}