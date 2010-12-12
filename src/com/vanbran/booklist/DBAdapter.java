package com.vanbran.booklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter 
{
//	public static final String KEY_ROWID = "_id";
//	public static final String KEY_YEAROFREWARDS = "YearOfRewards";
//	public static final String KEY_MONTHOFREWARDS = "MonthOfRewards";
//	public static final String KEY_DAYOFREWARDS = "DayOfRewards";
//	public static final String KEY_NUMOFREWARDS = "NumOfRewards";
	private static final String TAG = "DBAdapter";
	
	private static final String DB_NAME = "BookList";
	private static final String DB_TABLE = "tblBookList";
	private static final int DB_VERSION = 1;
	
	private static final String DATABASE_CREATE =
		"create table " + DB_TABLE + " (_id integer primary key autoincrement, " +
		"Title text not null, " +
		"Author text not null, " +
		"Status text not null );" ;
	
	private final Context context;
	
	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	
	
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DbHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading db from version " + oldVersion + " to " + newVersion +
					" which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}

	//Open the DB
	public DBAdapter open() throws SQLException
	{
		db = DbHelper.getWritableDatabase();
		return this;
	}

	//Close the DB
	public void close()
	{
			DbHelper.close();
	}
	
	//Insert a Book into the db
	public long insertBook(String _Author, String _Title, String _Status)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put("Author", _Author);
		initialValues.put("Title", _Title);
		initialValues.put("Status", _Status);
				
		return db.insert(DB_TABLE, null, initialValues);
	}
	
	public Cursor searchBooks(String _Author, String _Title, String _Status)
	{
		String qStr = "SELECT * FROM " + DB_TABLE + " WHERE Author like " + _Author + " AND Title like " + _Title  +
			" AND Status like " + _Status ;
		
		//String qStr = "SELECT * FROM " + DB_TABLE ; 
		
		Cursor cursor = db.rawQuery(qStr, null);
		return cursor ;
	}
	
	public boolean deleteBook(long rowId){
		return db.delete(DB_TABLE, "_id" + "=" + rowId, null) > 0;
	}
}


