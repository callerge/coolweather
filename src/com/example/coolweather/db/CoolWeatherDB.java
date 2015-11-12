package com.example.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 把常用的数据库操作封账到这个类中.<br/>
 */
public class CoolWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "coo_weather";

	/**
	 * 版本号
	 */
	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB实例——单例模式.<br/>
	 * 一般不这样写，标准写法见JavaSE多线程
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province写入数据库
	 */
	public void savaProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	/**
	 * 从数据库获取全国所有的省份信息
	 */
	public List<Province> loadProvinces() {
		List<Province> lists = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();

				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));

				lists.add(province);
			} while (cursor.moveToNext());
		}

		return lists;
	}

	/**
	 * 将City写入数据库
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库获取某省下所有的城市信息
	 */
	public List<City> loadCitys(int provinceId) {
		List<City> lists = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ? ",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				// 这里可以直接用就不要再查询了O(∩_∩)O
				city.setProvinceId(provinceId);
				
				lists.add(city);
				
			} while (cursor.moveToNext());
		}
		return lists;
	}
	
	/**
	 * 将county实例存储到数据库
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values = new ContentValues() ;
			values.put("id", county.getId());
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			
			db.insert("County", null, values);
		}
	}
	
	/**
	 * 从数据库读取某城市下的所有县城
	 */
	public List<County> loadCounty(int cityId){
		List<County> lists = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				
				lists.add(county);
			} while (cursor.moveToNext());
		}
		
		return lists;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
