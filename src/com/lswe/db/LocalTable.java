package com.lswe.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.lswe.reader.ConstDefine;

public class LocalTable {
	static Context context;
	private static SQLiteDatabase db;

	public LocalTable(Context context){
		LocalDBHelper localDBHelper = new LocalDBHelper(context);
		db = localDBHelper.getWritableDatabase();
	}
	public static synchronized void openDB() {
		db = SQLiteDatabase.openDatabase(ConstDefine.DB_STORAGE_PATH, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}



	public static synchronized void closeDB() {
		db.close();
	}

	/* 把登录成功的数据写入数据库 */
	public static synchronized void InsertLocaUser(String[] UesrInfo) {
		openDB();
		db.delete("User", null, null);
		ContentValues values = new ContentValues();
		values.put("UserName", UesrInfo[0]);
		values.put("Password", UesrInfo[1]);
		values.put("Email", UesrInfo[2]);
		values.put("SESSIONID", UesrInfo[3]);
		db.insert("User", null, values);
		closeDB();
	}

	/* 得到当前账户的所有信息 */
	public static synchronized String[] getLocUser() {
		openDB();
		String[] UesrInfo = new String[4];
		try {
			Cursor cursor = db
					.query("User", null, null, null, null, null, null);
			cursor.moveToFirst();
			UesrInfo[0] = cursor.getString(cursor.getColumnIndex("UserName"));
			UesrInfo[1] = cursor.getString(cursor.getColumnIndex("Password"));
			UesrInfo[2] = cursor.getString(cursor.getColumnIndex("Email"));
			UesrInfo[3] = cursor.getString(cursor.getColumnIndex("SESSIONID"));
			cursor.close();
		} catch (Exception e) {
			System.out.println("登录查询查询失败");
		}
		closeDB();
		return UesrInfo;
	}

	/* 设置数据库 */
	public static synchronized int GetSize() {
		int ZiTiDX = 0;
		openDB();
		try {
			Cursor cursor = db.query("Setting", null, null, null, null, null,
					null);
			cursor.moveToFirst();
			ZiTiDX = Integer.valueOf(cursor.getString(cursor
					.getColumnIndex("ZiTiDX")));
		} catch (Exception e) {
			// 得到字体的默认大小
			ZiTiDX = 40;
		}
		return ZiTiDX;
	}

	public static synchronized void insertSetting() {
		openDB();
		ContentValues advalues = new ContentValues();
		advalues.put("ZiTiDX", 40);
		db.insert("Setting", null, advalues);
		closeDB();
	}

	public static synchronized int updateSetting(boolean add) {
		int ZiTiDX = 0;
		openDB();
		Cursor cursor = db.query("Setting", null, null, null, null, null, null);
		cursor.moveToFirst();
		ZiTiDX = Integer.valueOf(cursor.getString(cursor
				.getColumnIndex("ZiTiDX")));
		if (add)
			ZiTiDX = ZiTiDX + 5;
		else
			ZiTiDX = ZiTiDX - 5;
		ContentValues advalues = new ContentValues();
		advalues.put("ZiTiDX", ZiTiDX);
		db.delete("Setting", null, null);
		db.insert("Setting", null, advalues);
		closeDB();
		return ZiTiDX;
	}

	// 在数据库中插入作者信息
	public static synchronized void InsertAuthor(String[] Author) {
		openDB();
		//应该判定数据库中有没有
		ContentValues authorvalues = new ContentValues();
		authorvalues.put("AuthorName", Author[0]);
		authorvalues.put("AuthorID", Author[1]);
		db.insert("Author", null, authorvalues);
		System.out.println("插入作者:" + Author[0] + " " + Author[1]);
		closeDB();
	}
	// 需要更新作者信息
	public static synchronized void UpdateAuthor(String[] Author){
		
	}

	// 在数据库中插入书信息
	public static synchronized void InsertBooks(String[] book) {
		openDB();
		ContentValues authorvalues = new ContentValues();
		authorvalues.put("BOOKID", book[0]);
		authorvalues.put("title", book[1]);
		authorvalues.put("AuthorID", book[2]);
		authorvalues.put("photoUrl", book[3]);
		authorvalues.put("status", book[4]);
		db.insert("Books", null, authorvalues);
		closeDB();
	}
	public static synchronized void InsertBookMark(String[] bookmark){
		openDB();
		ContentValues bookmarkValues = new ContentValues();
		bookmarkValues.put("BOOKID", bookmark[0]);
		bookmarkValues.put("position", bookmark[1]);
		bookmarkValues.put("BookName", bookmark[2]);
		bookmarkValues.put("BookChapname", bookmark[3]);
		bookmarkValues.put("Author", bookmark[4]);
		bookmarkValues.put("Percent", bookmark[5]);
		db.insert("Bookmarks", null, bookmarkValues);
		System.out.println("书签插入--->"+bookmark);
	}
	//在数据库中更新书的信息
	public static synchronized void UpdateBooks(String[] book){
		
	}
	// 在數據庫插入章節列表
	public static synchronized void InsertChapList(String[] chapinfo) {
		openDB();
		ContentValues chapvalues = new ContentValues();
		chapvalues.put("ChapterID", chapinfo[0]);
		chapvalues.put("BOOKID", chapinfo[1]);
		chapvalues.put("title", chapinfo[2]);
		chapvalues.put("updatetime", chapinfo[3]);
		db.insert("Chapter", null, chapvalues);
		System.out.println("插入章节信息：" + chapinfo[0] + " " + chapinfo[1] + " "
				+ chapinfo[2] + " " + chapinfo[3]);
		closeDB();
	}
	// 在数据库中更新章节信息
	public static synchronized void UpdateChapList(String chapinfo){
		
	}

	// 在数据库插入章节内容
	public static synchronized void IsertChapContent(String[] chapcontent) {
		openDB();
		//应该检测数据库厘米昂有没有这个数据，有则进行更新，没有才进行插入
		ContentValues chapvalues = new ContentValues();
		chapvalues.put("ChapterID", chapcontent[0]);
		chapvalues.put("ChapterContent", chapcontent[1]);
		db.insert("ChapterContent", null, chapvalues);
		System.out.println("插入章节内容：" + chapcontent[0] + "  " + chapcontent[1]);
		closeDB();
	}
	
	public static synchronized void UpdateChapContent(String[] chapcontent){
		
	}

	// 清楚本地数据
	public static void DeleteLoc() {
		openDB();
		db.delete("Books", null, null);
		db.delete("Author", null, null);
		db.delete("Chapter", null, null);
		db.delete("CHapterContent", null, null);
		closeDB();
	}
	//在数据库中清除对应作者的所有书籍数据
	public static void DeleteAuthor(Integer AuthorID){
		
	}
	//在数据库中清除对应书本的所有章节数据
	public static void DeleteBook(Integer BookID){
		
	}
	//清除对应章节的所有内容
	public static void DeleteContent(Integer chapID){
		
	}
	/* 根据分类得到数据库Cursor */
	public static synchronized Cursor getCursor() {
		openDB();
		Cursor cursor = db.query("Books", null, null, null, null, null, null);
		//closeDB();
		return cursor; 
	}
	
	public static synchronized Cursor getChapCursor(){
		openDB();
		Cursor cursor = db.query("Chapter", null, null, null, null, null, null);
		//closeDB();
		return cursor;
	}
	
	public static synchronized Cursor getBookMarkCursor(){
		openDB();
		Cursor cursor = db.query("Bookmarks", null, null, null, null, null, null);
		return cursor;
	}
	
	public static synchronized Cursor getBookidCursor(String[] BOOKID){
		openDB();
		Cursor cursor = db.rawQuery("select * from Books where BOOKID = ?",BOOKID);
		//closeDB();
		return null;
	}

	
	public static Map<String, String> queryBySQL(String sql, String[] selectionArgs) {
		openDB();
		Map<String, String> map = new HashMap<String, String>();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int cols_len = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			for (int i = 0; i < cols_len; i++) {
				String cols_name = cursor.getColumnName(i);
				String cols_value = cursor.getString(cursor
						.getColumnIndex(cols_name));
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;
	}
	
	public static List<Map<String, String>> getBookMarkList(String sql,
			String[] selectionArgs) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int cols_len = cursor.getColumnCount();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < cols_len; i++) {
				String cols_name = cursor.getColumnName(i);
				String cols_value = cursor.getString(cursor
						.getColumnIndex(cols_name));
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
		closeDB();
		return list;
	}
	
	public static boolean updateBySQL(String sql, Object[] bindArgs) {
		boolean flag = false;
		openDB();
		try {
			db.execSQL(sql, bindArgs);
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return flag;
	}
	// 得到章节目录Cursor
	public static synchronized Cursor GetChapList(String BookID) {
		openDB();
		Cursor cursor = db.query("Chapter", null, "BOOKID=?",
				new String[] { BookID }, null, null, null);
		return cursor;
	}

	// 根据章节ID获取章节内容
	public static synchronized Cursor GetChapContent(String ChapID) {
		openDB();
		Cursor cursor = db.query("ChapterContent", null, "ChapterID=?",
				new String[] { ChapID }, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	// 根据作者ID获取作者名字
	public static synchronized String GetAuthorName(String AuthorID) {
		openDB();
		Cursor cursor = db.query("Author", null, "AuthorID=?",
				new String[] { AuthorID }, null, null, null);
		cursor.moveToFirst();
		String AuthorName = cursor.getString(cursor
				.getColumnIndex("AuthorName"));
		cursor.close();
		closeDB();
		return AuthorName;
	}
	
}