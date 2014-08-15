package com.lswe.reader;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lswe.net.HttpSer;

public class ChangeActivity extends Activity{
	EditText Oldpsw,Newpsw,Surepsw;
	Button bnConfirm,bnCancel;
	String oldpsw,newpsw,surepsw;
	HttpSer mHttpSer;
	ProgressDialog mProgressDialog;
	private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_change);
		Oldpsw = (EditText)findViewById(R.id.oldpsw);
		Newpsw = (EditText)findViewById(R.id.newpsw);
		Surepsw = (EditText)findViewById(R.id.surepsw);
		bnConfirm = (Button)findViewById(R.id.confirm);
		bnConfirm.setOnClickListener(new ConfirmListener());
		bnCancel = (Button)findViewById(R.id.cancel);
		bnCancel.setOnClickListener(new ConfirmListener());
	}
	class ConfirmListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
			case R.id.confirm:
				oldpsw = Oldpsw.getText().toString();
				newpsw = Newpsw.getText().toString();
				surepsw = Surepsw.getText().toString();
				if ((!oldpsw.equals("")) && (!newpsw.equals("")) && (!surepsw.equals(""))) {
					if(newpsw.equals(surepsw)){
						LoginThread lt = new LoginThread(oldpsw,newpsw,surepsw);
						lt.start();
						mProgressDialog = ProgressDialog.show(ChangeActivity.this,
								"登录中", "请稍后...");
					}else{
						DisToast("确定密码与新密码不一致请确定");
					}

				} else {
					DisToast("输入完整信息");
				}
				break;
			case R.id.cancel:
				ChangeActivity.this.finish();
				break;
			}
		}
	}
	class LoginThread extends Thread {

		String oldpsw;
		String newpsw;
		String surepsw;
		HashMap<String, Object> getback;
		int status = 403;
		String result;

		LoginThread(String oldpsw,String newpsw,String surepsw) {
			this.oldpsw = oldpsw;
			this.newpsw = newpsw;
			this.surepsw = surepsw;
		}

		@Override
		public void run() {
			super.run();
			mHttpSer = new HttpSer();
			//doPost_Login返回HashMap，map中存有status(登陆状态)，SESSIONID(设备id)，datas(返回数据)
			getback = mHttpSer.doPost_ChangePsw(oldpsw,newpsw,surepsw);
					try {
				status = Integer.valueOf(getback.get("status").toString());
				//System.out.println(status);
				result = getback.get("result").toString();
			} catch (Exception e) {
				result = null;
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					
					switch(status){
						case 200:
							break;
						case 404:
							// 登录失败
							DisToast("密码错误");
							break;
						case 500:
							// 登录成功
							DisToast(result);
							Intent intent = new Intent();
							intent.setClass(ChangeActivity.this, LoginActivity.class);
							startActivity(intent);
							ChangeActivity.this.finish();
							DisToast("修改成功");
							break;
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
