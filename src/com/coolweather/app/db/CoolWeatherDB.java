package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	public static final String DB_NAME = "cool_weather";
	public static final int VERSION = 1;
	public static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
	}

	public synchronized CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null)
			coolWeatherDB = new CoolWeatherDB(context);
		return coolWeatherDB;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	public List<Province> loadProvinces() {
		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				int idIndex = cursor.getColumnIndex("id");
				int id = cursor.getInt(idIndex);
				province.SetId(id);

				int nameIdx = cursor.getColumnIndex("province_name");
				String name = cursor.getString(nameIdx);
				province.setProvinceName(name);

				int codeIdx = cursor.getColumnIndex("province_code");
				String code = cursor.getString(codeIdx);
				province.setProvinceCode(code);
				provinces.add(province);
			} while (cursor.moveToNext());

		}
		return provinces;
	}

	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			db.insert("City", null, values);
		}
	}

	public List<City> loadCities() {
		List<City> cities=new ArrayList<City>();
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		if( cursor.moveToFirst())
		{
			do
			{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				cities.add(city);
			}
			while(cursor.moveToNext());
		}
		return cities;
	}
	
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			db.insert("County", null, values);
		}
	}

	public List<County> loadCounties() {
		List<County> counties=new ArrayList<County>();
		Cursor cursor = db.query("County", null, null, null, null, null, null);
		if( cursor.moveToFirst())
		{
			do
			{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				counties.add(county);
			}
			while(cursor.moveToNext());
		}
		return counties;
	}
}
