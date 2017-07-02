package com.example.newdemo.db;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CashDB {
	public SQLiteDatabase sqLiteDatabase;
	
	public CashDB(Context context) {
		MSQLiteDB mboxDB = MSQLiteDB.getInstance(context);
		sqLiteDatabase = mboxDB.getSQLiteDatabase();
	}
	//－－－－－－－－省份缓存相关－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	/**
	 * 删除待办列表里的所有数据
	 */
	public void delAllProvinceCache(){
//		sqLiteDatabase.beginTransaction();
		sqLiteDatabase.delete(MSQLiteDB.PROVINCE_TABLE, null, null);
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 插入一条数据
	 * @param 
	 */
	public void insertProvinceCache(String province) {
//		sqLiteDatabase.beginTransaction();
		ContentValues conValues = new ContentValues();
		conValues.put(MSQLiteDB.PROVINCE_NAME, province);
		sqLiteDatabase.insert(MSQLiteDB.PROVINCE_TABLE, null, conValues);
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 获得缓存数据
	 * @return
	 */
	public ArrayList<String> getProvinceCache() {
		ArrayList<String> provList = new ArrayList<String>();
		//以id字段为依据降序排列
		Cursor cursor = sqLiteDatabase.query(MSQLiteDB.PROVINCE_TABLE, null, 
				null, null, null, null, null);
		if(cursor != null){
			while (cursor.moveToNext()) {
				String prov = cursor.getString(cursor.getColumnIndex(MSQLiteDB.PROVINCE_NAME));
				provList.add(prov);
			}
			cursor.close();
		}
		return provList;
	}
	
	//－－－－－－－－市缓存相关－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	/**
	 * 删除待办列表里的所有数据
	 */
	public void delAllCityCache(String prov){
//		sqLiteDatabase.beginTransaction();
		sqLiteDatabase.delete(MSQLiteDB.CITY_TABLE, MSQLiteDB.CITY_PROV_NAME + " = ? ", new String[]{ prov });
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 插入一条数据
	 * @param 
	 */
	public void insertCityCache(String prov, String strCity) {
//		sqLiteDatabase.beginTransaction();
		ContentValues conValues = new ContentValues();
		conValues.put(MSQLiteDB.CITY_PROV_NAME, prov);
		conValues.put(MSQLiteDB.CITY_NAME, strCity);
		sqLiteDatabase.insert(MSQLiteDB.CITY_TABLE, null, conValues);
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 获得缓存数据
	 * @return
	 */
	public ArrayList<String> getCityCache(String prov) {
		ArrayList<String> cityList = new ArrayList<String>();
		sqLiteDatabase.beginTransaction();
		//以id字段为依据降序排列
		Cursor cursor = sqLiteDatabase.query(MSQLiteDB.CITY_TABLE, null, 
				MSQLiteDB.CITY_PROV_NAME + " = ? ", new String[]{ prov }, 
				null, null, null);
		if(cursor != null){
			while (cursor.moveToNext()) {
				String city = cursor.getString(cursor.getColumnIndex(MSQLiteDB.CITY_NAME));
				cityList.add(city);
			}
			cursor.close();
		}
		sqLiteDatabase.endTransaction();
		return cityList;
	}
	
	
	//－－－－－－－－县区缓存相关－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
	/**
	 * 删除待办列表里的所有数据
	 */
	public void delAllCountryCache(String province, String city){
//		sqLiteDatabase.beginTransaction();
		sqLiteDatabase.delete(MSQLiteDB.COUNTRY_TABLE,
				MSQLiteDB.COUNTRY_PROV_NAME + " = ? and " + MSQLiteDB.COUNTRY_CITY_NAME + " = ? ",
				new String[]{province, city});
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 插入一条数据
	 * @param 
	 */
	public void insertCountryCache(String province, String city, String country) {
//		sqLiteDatabase.beginTransaction();
		ContentValues conValues = new ContentValues();
		conValues.put(MSQLiteDB.COUNTRY_PROV_NAME, province);
		conValues.put(MSQLiteDB.COUNTRY_CITY_NAME, city);
		conValues.put(MSQLiteDB.COUNTRY_NAME, country);
		sqLiteDatabase.insert(MSQLiteDB.COUNTRY_TABLE, null, conValues);
//		sqLiteDatabase.setTransactionSuccessful();
//		sqLiteDatabase.endTransaction();
	}
	
	/**
	 * 获得缓存数据
	 * @return
	 */
	public ArrayList<String> getCountryCache(String province, String city) {
		ArrayList<String> countList = new ArrayList<String>();
		//以id字段为依据降序排列
		Cursor cursor = sqLiteDatabase.query(MSQLiteDB.COUNTRY_TABLE, null, 
				MSQLiteDB.COUNTRY_PROV_NAME + " = ? and " + MSQLiteDB.COUNTRY_CITY_NAME + " = ? ",
				new String[]{province, city}, null, null, null);
		if(cursor != null){
			while (cursor.moveToNext()) {
				String prov = cursor.getString(cursor.getColumnIndex(MSQLiteDB.COUNTRY_NAME));
				countList.add(prov);
			}
			cursor.close();
		}
		return countList;
	}
}