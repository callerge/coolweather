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
 * 用于解析服务器返回的数据.<br/>
 * api:http://www.weather.com.cn/adat/cityinfo/101010100.html<br/>
 * 格式：{"weatherinfo":{"city":"北京","cityid":"101010100","temp1":"15℃","temp2":
 * "5℃","weather":"多云","img1":"d1.gif","img2":"n1.gif","ptime":"08:00"}}
 * 城市，城市id，temp1最高温，temp2最低温，weather天气信息描述，img1和img2对应天气图片，ptime为天气发布时间.<br/>
 * 
 * 
 */

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
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
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
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
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
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
					// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器返回的天气数据，并存入到本地SharedPreferences
	 */
	public static void handleWeatherResponse(Context context, String response) {
		/*
		 * {"weatherinfo":
		 * {"city":"昆山","cityid":"101190404","temp1":"20℃","temp2":"11℃",
		 * "weather":"小雨","img1":"d7.gif","img2":"n7.gif","ptime":"08:00"} }
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

			// 存储
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weather,ptime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的数据存储到SharedPreferences文件
	 * @param contex
	 * @param cityName 城市名
	 * @param weatherCode 城市代码
	 * @param temp1 最高温度
	 * @param temp2 最低温度
	 * @param weather 天气概述
	 * @param ptime 发布时间
	 */
	public static void saveWeatherInfo(Context contex,String cityName, String weatherCode,
			String temp1, String temp2, String weather, String ptime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
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