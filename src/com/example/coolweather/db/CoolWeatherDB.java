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
 * �ѳ��õ����ݿ�������˵��������.<br/>
 */
public class CoolWeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "coo_weather";

	/**
	 * �汾��
	 */
	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * ��ȡCoolWeatherDBʵ����������ģʽ.<br/>
	 * һ�㲻����д����׼д����JavaSE���߳�
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * ��Provinceд�����ݿ�
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
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ
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
	 * ��Cityд�����ݿ�
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
	 * �����ݿ��ȡĳʡ�����еĳ�����Ϣ
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
				// �������ֱ���þͲ�Ҫ�ٲ�ѯ��O(��_��)O
				city.setProvinceId(provinceId);
				
				lists.add(city);
				
			} while (cursor.moveToNext());
		}
		return lists;
	}
	
	/**
	 * ��countyʵ���洢�����ݿ�
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
	 * �����ݿ��ȡĳ�����µ������س�
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
