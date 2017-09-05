package com.bohemian.board;


import org.cocos2d.types.CGPoint;

import com.bohemian.engine.CellStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	public static final String TAG ="DBAdapter";
	
	public static final String KEY_ID = "_id";
	public static final String KEY_POS = "pos";
	public static final String KEY_POSX = "posX";
	public static final String KEY_POSY = "posY";
	public static final String KEY_STATUS = "status";
	public static final String KEY_ROW = "row";
	public static final String KEY_COL = "col";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDB;
	
	private static final String DATABASE_CREATE = "create table Board (_id integer primary key autoincrement, "
		+ "pos text not null,posX float,posY float,row int,col int,status text not null);";
	private static final String DATABASE_NAME = "Database_Board.db";
	private static final String DATABASE_TABLE = "Board";
	private static final int DATABASE_VERSION = 2;
	
	private final Context mContext;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "Upgrading DB");
			db.execSQL("DROP TABLE IF EXISTS Board");
			onCreate(db);
		}
	}
	
	public DBAdapter(Context ctx){
		this.mContext = ctx;
	}
	
	public DBAdapter open()
	{
		mDbHelper = new DatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		mDB = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public long createItem(String pos,float x,float y,int row,int col,String status){
		ContentValues inititalValues = new ContentValues();
		inititalValues.put(KEY_POS, pos);
		inititalValues.put(KEY_POSX, x);
		inititalValues.put(KEY_POSY, y);
		inititalValues.put(KEY_STATUS, status);
		inititalValues.put(KEY_ROW, row);
		inititalValues.put(KEY_COL, col);
		
		return mDB.insert(DATABASE_TABLE, null, inititalValues);
	}
	
	public boolean deleteCell(String pos)
	{
		return mDB.delete(DATABASE_TABLE, KEY_POS + "=" + "\"" + pos + "\"", null) >0;
	}
	public Cursor getAllBoard(){
		return mDB.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_POS,KEY_POSX,KEY_POSY,KEY_ROW,KEY_COL,KEY_STATUS}, null, null, null, null, null);
	}
	public Cursor search(String pos) throws SQLException {
    return  mDB.query(DATABASE_TABLE, new String[] { KEY_ID,
            KEY_POS},KEY_POS + "="
            + "\"" + pos + "\"" , null, null, null, null, null);
	}
//+ " AND " + KEY_STATUS +"=" + "\"" + status + "\""
}
