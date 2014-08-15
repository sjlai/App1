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

	/* �ѵ�¼�ɹ�������д�����ݿ� */
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

	/* �õ���ǰ�˻���������Ϣ */
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
			System.out.println("��¼��ѯ��ѯʧ��");
		}
		closeDB();
		return UesrInfo;
	}

	/* �������ݿ� */
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
			// �õ������Ĭ�ϴ�С
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

	// �����ݿ��в���������Ϣ
	public static synchronized void InsertAuthor(String[] Author) {
		openDB();
		//Ӧ���ж����ݿ�����û��
		ContentValues authorvalues = new ContentValues();
		authorvalues.put("AuthorName", Author[0]);
		authorvalues.put("AuthorID", Author[1]);
		db.insert("Author", null, authorvalues);
		System.out.println("��������:" + Author[0] + " " + Author[1]);
		closeDB();
	}
	// ��Ҫ����������Ϣ
	public static synchronized void UpdateAuthor(String[] Author){
		
	}

	// �����ݿ��в�������Ϣ
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
		System.out.println("��ǩ����--->"+bookmark);
	}
	//�����ݿ��и��������Ϣ
	public static synchronized void UpdateBooks(String[] book){
		
	}
	// �ڔ���������¹��б�
	public static synchronized void InsertChapList(String[] chapinfo) {
		openDB();
		ContentValues chapvalues = new ContentValues();
		chapvalues.put("ChapterID", chapinfo[0]);
		chapvalues.put("BOOKID", chapinfo[1]);
		chapvalues.put("title", chapinfo[2]);
		chapvalues.put("updatetime", chapinfo[3]);
		db.insert("Chapter", null, chapvalues);
		System.out.println("�����½���Ϣ��" + chapinfo[0] + " " + chapinfo[1] + " "
				+ chapinfo[2] + " " + chapinfo[3]);
		closeDB();
	}
	// �����ݿ��и����½���Ϣ
	public static synchronized void UpdateChapList(String chapinfo){
		
	}

	// �����ݿ�����½�����
	public static synchronized void IsertChapContent(String[] chapcontent) {
		openDB();
		//Ӧ�ü�����ݿ����װ���û��������ݣ�������и��£�û�вŽ��в���
		ContentValues chapvalues = new ContentValues();
		chapvalues.put("ChapterID", chapcontent[0]);
		chapvalues.put("ChapterContent", chapcontent[1]);
		db.insert("ChapterContent", null, chapvalues);
		System.out.println("�����½����ݣ�" + chapcontent[0] + "  " + chapcontent[1]);
		closeDB();
	}
	
	public static synchronized void UpdateChapContent(String[] chapcontent){
		
	}

	// �����������
	public static void DeleteLoc() {
		openDB();
		db.delete("Books", null, null);
		db.delete("Author", null, null);
		db.delete("Chapter", null, null);
		db.delete("CHapterContent", null, null);
		closeDB();
	}
	//�����ݿ��������Ӧ���ߵ������鼮����
	public static void DeleteAuthor(Integer AuthorID){
		
	}
	//�����ݿ��������Ӧ�鱾�������½�����
	public static void DeleteBook(Integer BookID){
		
	}
	//�����Ӧ�½ڵ���������
	public static void DeleteContent(Integer chapID){
		
	}
	/* ���ݷ���õ����ݿ�Cursor */
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
	// �õ��½�Ŀ¼Cursor
	public static synchronized Cursor GetChapList(String BookID) {
		openDB();
		Cursor cursor = db.query("Chapter", null, "BOOKID=?",
				new String[] { BookID }, null, null, null);
		return cursor;
	}

	// �����½�ID��ȡ�½�����
	public static synchronized Cursor GetChapContent(String ChapID) {
		openDB();
		Cursor cursor = db.query("ChapterContent", null, "ChapterID=?",
				new String[] { ChapID }, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	// ��������ID��ȡ��������
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