package com.lswe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHelper extends SQLiteOpenHelper {

	public LocalDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public LocalDBHelper(Context context){
		super(context, null, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table  User (ID INTEGER PRIMARY KEY NOT NULL, UserName VARCHAR, Password VARCHAR, Email VARCHAR, SESSIONID VARCHAR)");
		db.execSQL("create table  Books(_id INTEGER PRIMARY KEY NOT NULL, BOOKID VARCHAR, title VARCHAR, AuthorID VARCHAR, photoUrl VARCHAR,status VARCHAR)");
		db.execSQL("create table  Author(ID INTEGER PRIMARY KEY NOT NULL, AuthorName VARCHAR, AuthorID)");
		db.execSQL("create table  Chapter(_id INGEGER,ChapterID VARCHAR, BOOKID VARCHAR, title VARCHAR, updatetime VARCHAR)");
		db.execSQL("create table  ChapterContent(ID INTEGER PRIMARY KEY NOT NULL, ChapterID VARCHAR, ChapterContent VARCHAR)");
		db.execSQL("create table  Setting(ID INTEGER PRIMARY KEY NOT NULL, ZiTiDX VARCHAR)");
		db.execSQL("create table Bookmarks(ID INTEGER PRIMARY KEY NOT NULL, BOOKID VARCHAR, position VARCHAR, BookName VARCHAR, BookChapname VARCHAR,Author VARCHAR,Percent VARCHAR)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}
}