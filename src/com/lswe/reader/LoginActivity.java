package com.lswe.reader;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lswe.db.LocalTable;
import com.lswe.net.HttpSer;

public class LoginActivity extends Activity {
	private Button loginBtn, regist;
	private EditText UserName, Userpsw;
	private TextView tv;
	private TextView changepsw;
	private String username, password;
	private HttpSer mHttpSer;
	private Handler handler = new Handler();
	private ProgressDialog mProgressDialog;
	private String uuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		FindView();
		tv.setFocusable(true);
		tv.setFocusableInTouchMode(true);
		tv.requestFocus();
		loginBtn.setOnClickListener(new logInListener());
		regist.setOnClickListener(new logInListener());
		changepsw.setOnClickListener(new logInListener());
	}

	private void FindView() {
		loginBtn = (Button) findViewById(R.id.loginbtn);
		regist = (Button) findViewById(R.id.regist);
		UserName = (EditText) findViewById(R.id.username);
		Userpsw = (EditText) findViewById(R.id.userpassword);
		tv = (TextView) findViewById(R.id.textview_sx);
		changepsw = (TextView)findViewById(R.id.changepsw);
		String[] userinfo = LocalTable.getLocUser();
		UserName.setText(userinfo[0]);
		Userpsw.setText(userinfo[1]);
	}
    private String getMyUUID() {

        final TelephonyManager tm = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                        + android.provider.Settings.Secure.getString(
                                        getContentResolver(),
                                        android.provider.Settings.Secure.ANDROID_ID);
		//加密UUID设备编号
        UUID deviceUuid = new UUID(androidId.hashCode(),
                        ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
}

	class logInListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginbtn:
				username = UserName.getText().toString();
				password = Userpsw.getText().toString();
				uuid = getMyUUID();
				if ((!username.equals("")) && (!password.equals(""))) {
					LoginThread lt = new LoginThread(username, password);
					lt.start();
					mProgressDialog = ProgressDialog.show(LoginActivity.this,
							"登录中", "请稍后...");
				} else {
					DisToast("输入完整信息");
				}
				break;
			case R.id.regist:
				username = "test";
				password = "111111";
				LoginThread lt = new LoginThread(username, password);
				lt.start();
				mProgressDialog = ProgressDialog.show(LoginActivity.this,
						"浏览登录中", "请稍后...");
				break;
			case R.id.changepsw:
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this,ChangeActivity.class);
				startActivity(intent);
			default:
				break;
			}
		}
	}
	class LoginThread extends Thread {

		String name;
		String psw;
		//String uuid;
		HashMap<String, Object> getback;
		int status = 403;
		String SESSIONID;

		LoginThread(String name, String psw) {
			this.name = name;
			this.psw = psw;
			//this.uuid = uuid;
		}

		@Override
		public void run() {
			super.run();
			mHttpSer = new HttpSer();
			//doPost_Login返回HashMap，map中存有status(登陆状态)，SESSIONID(设备id)，datas(返回数据)
			getback = mHttpSer.doPost_Login(name, psw);
			try {
				status = Integer.valueOf(getback.get("status").toString());
				SESSIONID = getback.get("SESSIONID").toString();
			} catch (Exception e) {
				SESSIONID = null;
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					System.out.println(status);
					if (status == 200) {
						// 登录成功
						String[] UesrInfo = { username, password, "", SESSIONID };
						System.out.println(username + "----------------<");
						LocalTable.InsertLocaUser(UesrInfo);
						//字体大小40
						LocalTable.insertSetting();
						ConstDefine.SESSIONID = SESSIONID;
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, BookList.class);
						startActivity(intent);
						LoginActivity.this.finish();
					} else {
						// 登录失败
						DisToast("用户名或者密码错误");
					}
				}
			};
			handler.post(r);
		}
	}

	private void DisToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
}