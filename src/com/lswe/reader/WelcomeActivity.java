package com.lswe.reader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.lswe.db.LocalDBHelper;
import com.lswe.db.LocalTable;

public class WelcomeActivity extends Activity {
	private myThread thread;
	private LocalDBHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// �������ݿⲢ�һ�����ݿ��·��
		dbHelper = new LocalDBHelper(WelcomeActivity.this,
				ConstDefine.LOCAL_DB_NAME, null, 4);
		ConstDefine.DB_STORAGE_PATH = dbHelper.getReadableDatabase().getPath();
		// �����Ļ�ĳߴ�
		WindowManager manage = getWindowManager();
		Display display = manage.getDefaultDisplay();
		ConstDefine.ScreenWidth = display.getWidth();
		ConstDefine.ScreenHeight = display.getHeight();
		thread = new myThread();
		thread.start();
		//System.out.println("getAPKVersion:" + getAPKVersion());
	}

	@Override
	/**
	 * ����¼���ֱ��������ӭ����
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (thread) {
				thread.notifyAll();
			}
		}
		return true;
	}

	/**
	 * �߳̿��ƻ�ӭ������ʾʱ��
	 * 
	 * @author Ennis
	 */
	class myThread extends Thread {
		public void run() {
			Intent intent = new Intent();
			String[] locuser = LocalTable.getLocUser();
			if ((locuser[0] != null) && (locuser[1] != null)
					&& (locuser[2] != null)) {
				ConstDefine.SESSIONID = locuser[3];
				intent.setClass(WelcomeActivity.this, BookList.class);
			} else {
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
			}
			try {
				synchronized (this) {
					wait(3000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startActivity(intent);
			WelcomeActivity.this.finish();
		}
	}

	/**
	 * �õ�����Ӧ�õİ汾��Ϣ
	 * 
	 * @return
	 */
	private int getAPKVersion() {
		// APK�汾�ж�
		int sdcardVersion = 0;
		String apkFilePath = "sdcard/demo.apk"; // ��װ��·��
		PackageManager pm = getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkFilePath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			sdcardVersion = info.versionCode; // �õ��汾��Ϣ
			Log.v("TAG", "Version=" + sdcardVersion);
		}
		return sdcardVersion;
	}
}