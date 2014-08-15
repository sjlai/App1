package com.lswe.reader;

import android.database.Cursor;

public class ConstDefine {

	public static final int NetStateConnected = 0x00;
	public static final int NetStateBreak = 0x01;
	public static final int DataInitComplete = 0x02;
	public static final int LoginSuccess = 0x03;
	public static final int LoginFaild = 0x04;

	public static Boolean DEBUG = true;

	public static String DB_STORAGE_PATH = "/data/data/com.lswe.reader/databases";
	public static String LOCAL_DB_NAME = "shallwe.local.sqlite";
	public static String[] FENLEI = null;
	public static String ALLFENLEI = "全部分类";
	public static String[] ZUOZHE = null;
	public static String ALLZUOZHE = "全部作者";
	public static String DIRPATH = null;
	public static String SESSIONID = null;

	public static Cursor cursor = null;
	public static Cursor secondcursor = null;

	public static int ScreenWidth;
	public static int ScreenHeight;

	public static String VERSION = "1.0";
}