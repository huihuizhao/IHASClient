package com.example.newdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MSQLiteDB {
	
	public String dbName = "msqlite.db";
	private int mSQLiteDB_Version = 1;
	private SQLiteDatabase db;
	private DatabaseHelper mDatabaseHelper;
	public static MSQLiteDB mInstance;
	
	//省份的缓存表------
	public static final String PROVINCE_TABLE = "table_kungfu_province"; //表名
	public static final String PROVINCE_NAME = "province_name"; 
	private String createProvinceTable = "create table " + PROVINCE_TABLE
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ PROVINCE_NAME + " TEXT )";
	
	
	//城市的缓存表------
		public static final String CITY_TABLE = "table_kungfu_city"; //表名
		public static final String CITY_PROV_NAME = "city_prov_name"; 
		public static final String CITY_NAME = "city_name"; 
		private String createCityTable = "create table " + CITY_TABLE
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CITY_PROV_NAME + " TEXT ,"
				+ CITY_NAME + " TEXT )";
	
	//区县的缓存表------
	public static final String COUNTRY_TABLE = "table_kungfu_country"; //表名
	public static final String COUNTRY_PROV_NAME = "country_prov_name";
	public static final String COUNTRY_CITY_NAME = "country_city_name";
	public static final String COUNTRY_NAME = "country_name";
	private String createCountryTable = "create table " + COUNTRY_TABLE
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COUNTRY_PROV_NAME + " TEXT ,"
			+ COUNTRY_CITY_NAME + " TEXT ,"
			+ COUNTRY_NAME + " TEXT )";
	
	public MSQLiteDB(Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
		try {
			db = mDatabaseHelper.getWritableDatabase();
		} catch (Exception e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			db = mDatabaseHelper.getWritableDatabase();
			e.printStackTrace();
		}
	}
	
	public SQLiteDatabase getSQLiteDatabase(){
		return db;
	}
	
	public static synchronized MSQLiteDB getInstance(Context context){
		if(mInstance == null){
			mInstance = new MSQLiteDB(context);
		}
		return mInstance;
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper{
		public DatabaseHelper(Context context) {
			super(context, dbName, null, mSQLiteDB_Version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createProvinceTable);
			db.execSQL(createCityTable);
			db.execSQL(createCountryTable);
		}
		/**
		 * 注意当数据库升级时要考虑好版本,如果3覆盖1那就将2新增的也要添加进去
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//				if(newVersion > oldVersion){
//					if(oldVersion >= 1){
//						upgradeVersion2(db);
//					}
//				}
		}
	}
	
//		/**
//		 * 升级SQL数据库版本为2
//		 */
//		private void upgradeVersion2(SQLiteDatabase db) {
//			
//		}
	
	/** 清空所有数据库记录 */
	public void deleteAllRecords() {
		db.beginTransaction();
		try {
			db.delete(createProvinceTable, null, null);
			db.delete(createCityTable, null, null);
			db.delete(createCountryTable, null, null);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
