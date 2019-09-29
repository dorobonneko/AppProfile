package com.moe.appprofile;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class Settings
{
	public static final String AUTHORITY="com.moe.appprofile.SettingProvider";
	public static final Uri URI=Uri.parse("content://"+AUTHORITY);
	public static int getMode(Context context,String packageName){
		int mode=0;
		Cursor cursor=context.getContentResolver().query(URI,new String[]{"mode"},"packagename=?",new String[]{packageName},null);
		if(cursor!=null){
			if(cursor.moveToNext()){
				mode=cursor.getInt(0);
			}
			cursor.close();
		}
		return mode;
	}
	public static void putMode(Context context,String packageName,int mode){
		ContentValues cv=new ContentValues();
		cv.put("packagename",packageName);
		cv.put("mode",mode);
		context.getContentResolver().insert(URI,cv);
	}
	public static class Provider extends ContentProvider
	{
		private SQLiteDatabase sql;
		@Override
		public boolean onCreate()
		{
			sql=getContext().openOrCreateDatabase("setting",0,null);
			sql.disableWriteAheadLogging();
			if(sql.getVersion()==0)
			sql.execSQL("create table setting(id INTEGER PRIMARY KEY,packagename TEXT UNIQUE,mode INTEGER)");
			sql.setVersion(1);
			return true;
		}

		@Override
		public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5)
		{
			// TODO: Implement this method
			return sql.query("setting",p2,p3,p4,null,null,p5);
		}

		@Override
		public String getType(Uri p1)
		{
			// TODO: Implement this method
			return null;
		}

		@Override
		public Uri insert(Uri p1, ContentValues cv)
		{
			try{sql.insertOrThrow("setting",null,cv);}catch(Exception e){
				sql.update("setting",cv,"packagename=?",new String[]{cv.getAsString("packagename")});
			}
			return null;
		}

		@Override
		public int delete(Uri p1, String p2, String[] p3)
		{
			// TODO: Implement this method
			return 0;
		}

		@Override
		public int update(Uri p1, ContentValues p2, String p3, String[] p4)
		{
			// TODO: Implement this method
			return 0;
		}
	} 
}
