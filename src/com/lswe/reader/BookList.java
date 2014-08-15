package com.lswe.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lswe.db.LocalTable;
import com.lswe.net.HttpSer;

public class BookList extends Activity {
	private ImageButton set_btn, bl_ibtn, bl_drawer;
	private Button markStart;
	private ListView blv;
	private PopupWindow popupwindow;
	View contentView = null;
	private long exitTime = 0;
	private Handler handler = new Handler();
	private HttpSer mHttpSer;
	BroadcastReceiver connectionReceiver;
	private ProgressDialog pd;
	private String CurVersion = "1.0";
	private String appName = "reader";
	private boolean isUpdateBook = false;
	//��Ļ���ȵ���ֵ
	private int brighnessPro = 85;
	UpdateAuthorThread uat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booklist);
		//System.out.println("welcome booklist");
		FindViewByid();
		blv.setAdapter(new cursorAdapter(getApplicationContext(), LocalTable
				.getCursor(), false));
		blv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), BookChapList.class);
				//����½ڵ�λ�ã������Ա����ݿ��ȡ
				intent.setFlags(position);
				startActivity(intent);
			}
		});
		bl_ibtn.setOnClickListener(new ImageBtnlistener());
		set_btn.setOnClickListener(new ImageBtnlistener());
		bl_drawer.setOnClickListener(new ImageBtnlistener());
		//bnChange.setOnClickListener(new ImageBtnlistener());
		//ͨ������������jason�ж���ǰ�汾�ǲ������£�����ǲ������£�ͨ�����ص�ַ���и�������
		updateThread ut = new updateThread();
		ut.start();
		//ͨ�����������ص�jason�ж���ǰ���鼮��û�и��£����������£�û�����ã��Դ�Ϊ�Զ�����
		//���ȡ���ֶ����£�ֻ�����Զ����£�ͨ��jason����ֵ�Զ��������ݴ�С
		//updatebookThread ubt = new updatebookThread();
		//ubt.start();
	}

	/* ��ȡ�ؼ�����÷�����б� */
	private void FindViewByid() {
		set_btn = (ImageButton) findViewById(R.id.set_btn);
		bl_ibtn = (ImageButton) findViewById(R.id.bl_ibtn);
		bl_drawer = (ImageButton) findViewById(R.id.bl_drawer);
		blv = (ListView) findViewById(R.id.blv);
	}

	private void DisToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	private class ImageBtnlistener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.set_btn:
				// ���ð�ť
				DisToast("���ð�ť");
				PopUpWindowSow();
				break;
			case R.id.bl_ibtn:
				// ���°�ť
				LocalTable.DeleteLoc();
				DisToast("���°�ť");
				connectionReceiver = new BroadcastReceiver() {

					@SuppressWarnings("deprecation")
					@Override
					public void onReceive(Context context, Intent intent) {
						ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); 
						NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
						NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 

						if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) { 
							uat.stop();
							if (pd.isShowing()) {
								pd.dismiss();
							}	
							AlertDialog.Builder dialog = new AlertDialog.Builder(BookList.this);
							dialog.setTitle("����");
							dialog.setMessage("����Ͽ�����");
							dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									uat.start();
								}
							});
							dialog.setNegativeButton("ȡ��", null);
							}

					} 
					
				};
				 uat = new UpdateAuthorThread();
				uat.start();
				pd = ProgressDialog.show(BookList.this, "������", "���Ժ�...");
				break;
			case R.id.bl_drawer:
				// ���뿪��
				break;	
			default:
				break;
			}
		}
	}

	class cursorAdapter extends CursorAdapter {

		public cursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
			View view = new View(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.booklistcontent, null);
			ListViewHolder holder = new ListViewHolder();
			holder.bookname = (TextView) view.findViewById(R.id.bookname);
			holder.zuozhe = (TextView) view.findViewById(R.id.author);
			holder.baifen = (TextView) view.findViewById(R.id.bf);
			holder.tupian = (ImageView) view.findViewById(R.id.blc_img);
			// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ListViewHolder holder = (ListViewHolder) view.getTag();
			holder.bookname.setText(cursor.getString(cursor
					.getColumnIndex("title")));
			String AuthorName = LocalTable.GetAuthorName(cursor
					.getString(cursor.getColumnIndex("AuthorID")));
			holder.zuozhe.setText(AuthorName);
			holder.tupian.setImageResource(R.drawable.cover);
			holder.baifen.setText("�Ķ����ȣ�" + Integer.valueOf("0") + "/"
					+ Integer.valueOf("100"));
		}

		final class ListViewHolder {
			public TextView bookname;
			public TextView zuozhe;
			public TextView baifen;
			public ImageView tupian;
		}
	}

	/* ���ð�ť���� */
	private void PopUpWindowSow() {
		if (popupwindow == null) {
			contentView = getLayoutInflater()
					.inflate(R.layout.pop_window, null);
			ImageButton backbtn = new ImageButton(getApplicationContext());
			backbtn = (ImageButton) contentView.findViewById(R.id.back_btn);
			backbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupwindow.dismiss();
				};
			});
		}
		class SettingAdp extends BaseAdapter {
			private LayoutInflater inflater;
			//���ذ���SettingInfo��List��������趨�������������
			private List<SettingInfo> SetInfos = SettingInfo.getSettingInfo();

			public SettingAdp(Context c) {
				inflater = LayoutInflater.from(c);
			}

			@Override
			public int getCount() {
				return SetInfos.size();
			}

			@Override
			public Object getItem(int position) {
				return SetInfos.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				SetViewHolder svholder = null;
				if (convertView == null) {
					// �����Զ����Item���ּ��ز���
					if (SetInfos.get(position).bjw == 1) {
						convertView = inflater.inflate(
								R.layout.pop_listview_simplecontent, null);
						svholder = new SetViewHolder();
						svholder.SzInfo = (TextView) convertView
								.findViewById(R.id.userId);
						// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
						convertView.setTag(svholder);
					} else {
						convertView = inflater.inflate(
								R.layout.pop_list_content, null);
						svholder = new SetViewHolder();
						svholder.SzInfo = (TextView) convertView
								.findViewById(R.id.firsttv);
						// �����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag
						convertView.setTag(svholder);
					}
				} else {
					// ȡ��ViewHolder����
					svholder = (SetViewHolder) convertView.getTag();
				}
				svholder.SzInfo.setText(SetInfos.get(position).ftv);
				return convertView;
			}

			final class SetViewHolder {
				public TextView SzInfo;
			}
		}
		ListView szlv = (ListView) contentView.findViewById(R.id.szlist);
		szlv.setAdapter(new SettingAdp(getApplicationContext()));
		szlv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					DisToast("xiugaimima");
					Intent intent = new Intent();
					intent.setClass(BookList.this,ChangeActivity.class);
					startActivity(intent);
					break;
				case 2:
					if ((System.currentTimeMillis() - exitTime) > 2000) {
						DisToast("�ٰ�һ��ע����¼");
						exitTime = System.currentTimeMillis();
					} else {
						popupwindow.dismiss();
						String[] user = new String[4];
						LocalTable.InsertLocaUser(user);
						Intent intent1 = new Intent();
						intent1.setClass(getApplicationContext(),
								LoginActivity.class);
						startActivity(intent1);
						BookList.this.finish();
					}
					break;
				case 3:
					DisToast("�Ӵ��ֺ�" + "  " + "��ǰ�����С��"
							+ LocalTable.updateSetting(true));
					break;
				case 4:
					DisToast("��С�ֺ�" + "  " + "��ǰ�����С��"
							+ LocalTable.updateSetting(false));
					break;
				case 5:
					if(brighnessPro<=255){
						brighnessPro+=5;
					setBrightnesss(brighnessPro);
					}
					DisToast("��Ļ����");
					break;
				case 6:
					if(brighnessPro>=55){
						brighnessPro-=5;
					setBrightnesss(brighnessPro);
					}
					DisToast("��Ļ�䰵");
					break;
				default:
					break;
				}
			}
		});
		// ����������������
		popupwindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		popupwindow.setFocusable(true);
		//����popupwindow��Ӧ���ؼ������һ�仰
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		popupwindow.showAtLocation(findViewById(R.id.drawer_layout),
				Gravity.CENTER, 0, 0);
	}

	//�����Ҫ�Զ����£�����ж����µ����ݣ�
	class UpdateAuthorThread extends Thread {		
		List<Integer> authorlist = new ArrayList<Integer>();
		@Override
		public void run() {
			super.run();
			
			IntentFilter intentFilter = new IntentFilter(); 
			intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
			registerReceiver(connectionReceiver, intentFilter); 
			mHttpSer = new HttpSer();
			//ͨ������������������ֺ�����id����������id��ÿһλ������Ψһ��ʾ��id
			authorlist = mHttpSer.doPost_AuthorList();
			for (int i = 0; i < authorlist.size(); i++) {
				List<Integer> booklist = new ArrayList<Integer>();
				//ͨ������id�����ÿһλ���ߵ��鼮�б�����ID, title, AuthorID,photoUrl, status
				booklist = mHttpSer.doPost_AuthorBookList(authorlist.get(i));
				for (int j = 0; j < booklist.size(); j++) {
					//ͨ���鼮���ƻ���½��б�
					List<Integer> chaplist = new ArrayList<Integer>();
					chaplist = mHttpSer.doPost_AuthorBookChapterList(booklist
							.get(j));
					for (int k = 0; k < chaplist.size(); k++) {
						//��õ�ǰ�˻��û���
						String name = LocalTable.getLocUser()[0];
						System.out.println(name);
						boolean permission = ("test").equals(name);
						//���Խ���ֻ��õ�һ���������
						if (permission) {
							if (j == 0) {
								mHttpSer.doPost_getChapterContent(chaplist
										.get(k));
							}
						} else {
							mHttpSer.doPost_getChapterContent(chaplist.get(k));
						}
					}
				}
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (pd.isShowing()) {
						pd.dismiss();
					}
					blv.setAdapter(new cursorAdapter(getApplicationContext(),
							LocalTable.getCursor(), false));
					if (connectionReceiver != null) { 
						unregisterReceiver(connectionReceiver); 
						} 

				}
			};
			handler.post(r);
		}
	}

	class updateThread extends Thread {
		int currentVersion = 0;
		String[] version = new String[2];

		@Override
		public void run() {
			super.run();
			HttpSer hs = new HttpSer();
			//����һ��stringһά���飬��һ������Ϊ�汾�ţ��ڶ�������Ϊ���ص�ַ
			version = hs.getVersion();
			if (!CurVersion.equals(version[0])) {
				String url = "http://" + version[1];
				downAppFile(url);
			}
		}
	}
	
	//���ҵ���һλ���ߵ���һ�������һ�½ڸ���
	class updatebookThread extends Thread{

		@Override
		public void run() {
			super.run();
			String[] version = new String[3];
			HttpSer hs = new HttpSer();
			version = hs.getUpdatebook();
			isUpdateBook = Boolean.getBoolean(version[0]);
			final String all = version[1];
			final String AuthorID = version[2];
			final String BookID = version[3];
			final String chapID = version[4];
			//����һ�����飬�����Ƿ��и��£����µ����ߺ��½�
			
			if(isUpdateBook){
				AlertDialog.Builder builder = new AlertDialog.Builder(BookList.this);
				builder.setTitle("������ʾ");
				builder.setMessage("�鼮�����и����Ƿ���Ҫ����");
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//���и������ݣ�ͨ���õ������������鼮�����½������и���,Ӧ���ڼ�һ������all�����������ݶ�Ҫ����
						if(Boolean.getBoolean(all)){
							//�����������ݣ�����ڵ�һ�ε�¼
							LocalTable.DeleteLoc();
							UpdateAuthorThread uat = new UpdateAuthorThread();
							uat.start();
							pd = ProgressDialog.show(BookList.this, "������", "���Ժ�...");
						}else{
						if(!AuthorID.equals("")){
							LocalTable.DeleteAuthor(Integer.parseInt(AuthorID));
							onlyUpdateAuthorThread  ouat = new onlyUpdateAuthorThread(AuthorID);
							ouat.start();
							pd = ProgressDialog.show(BookList.this, "������", "���Ժ�...");
						}else{
							if(!BookID.equals("")){
								LocalTable.DeleteBook(Integer.parseInt(BookID));
								onlyUpdateBookThread oubt = new onlyUpdateBookThread(BookID);
								oubt.start();
								pd = ProgressDialog.show(BookList.this, "������", "���Ժ�...");
							}else{
								if(!chapID.equals("")){
								LocalTable.DeleteContent(Integer.parseInt(chapID));
								onlyUpdateChapThread ouct = new onlyUpdateChapThread(chapID);
								pd = ProgressDialog.show(BookList.this, "������", "���Ժ�...");
									}
							}
						}
					}
					}
				});
				builder.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create();
				
			}
		}			
	}
	
	class onlyUpdateAuthorThread extends Thread{
		String AuthorID;
		List<Integer> booklist = new ArrayList<Integer>();
		onlyUpdateAuthorThread(String AuthorID){
			this.AuthorID = AuthorID;
		}
		@Override
		public void run() {
			super.run();
			mHttpSer = new HttpSer();
			booklist = mHttpSer.doPost_AuthorBookList(Integer.parseInt(AuthorID));
			for (int j = 0; j < booklist.size(); j++) {
				//ͨ���鼮���ƻ���½��б�
				List<Integer> chaplist = new ArrayList<Integer>();
				chaplist = mHttpSer.doPost_AuthorBookChapterList(booklist
						.get(j));
				for (int k = 0; k < chaplist.size(); k++) {
					//��õ�ǰ�˻��û���
					String name = LocalTable.getLocUser()[0];
					System.out.println(name);
					boolean permission = ("test").equals(name);
					//���Խ���ֻ��õ�һ���������
					if (permission) {
						if (j == 0) {
							mHttpSer.doPost_getChapterContent(chaplist
									.get(k));
						}
					} else {
						mHttpSer.doPost_getChapterContent(chaplist.get(k));
					}
				}
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (pd.isShowing()) {
						pd.dismiss();
					}
					blv.setAdapter(new cursorAdapter(getApplicationContext(),
							LocalTable.getCursor(), false));
				}
			};
			handler.post(r);
		}
		
	}
	
	class onlyUpdateBookThread extends Thread{
		String BookID;
		
		
		onlyUpdateBookThread(String BookID){
			this.BookID = BookID;
		}
		@Override
		public void run() {
			super.run();
			mHttpSer = new HttpSer();
			//ͨ���鼮���ƻ���½��б�
			List<Integer> chaplist = new ArrayList<Integer>();
			chaplist = mHttpSer.doPost_AuthorBookChapterList(Integer.parseInt(BookID));
			for (int k = 0; k < chaplist.size(); k++) {
					mHttpSer.doPost_getChapterContent(chaplist.get(k));
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (pd.isShowing()) {
						pd.dismiss();
					}
					blv.setAdapter(new cursorAdapter(getApplicationContext(),
							LocalTable.getCursor(), false));
				}
			};
			handler.post(r);
		}
		
	}
	
	class onlyUpdateChapThread extends Thread{
		String chapID;
		onlyUpdateChapThread(String chapID){
			this.chapID = chapID;
		}
		@Override
		public void run() {
			super.run();
			mHttpSer = new HttpSer();
			mHttpSer.doPost_getChapterContent(Integer.parseInt(chapID));
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (pd.isShowing()) {
						pd.dismiss();
					}
					blv.setAdapter(new cursorAdapter(getApplicationContext(),
							LocalTable.getCursor(), false));
				}
			};
			handler.post(r);
		}
		
	}
	
	protected void downAppFile(final String url) {
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					Log.isLoggable("DownTag", (int) length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is == null) {
						throw new RuntimeException("isStream is null");
					}
					File file = new File(
							Environment.getExternalStorageDirectory(), appName);
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					do {
						ch = is.read(buf);
						if (ch <= 0)
							break;
						fileOutputStream.write(buf, 0, ch);
					} while (true);
					is.close();
					fileOutputStream.close();
					haveDownLoad();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	protected void haveDownLoad() {
		handler.post(new Runnable() {
			public void run() {
				// pd.cancel();
				Dialog installDialog = new AlertDialog.Builder(BookList.this)
						.setTitle("�������")
						.setMessage("�Ƿ�װ�µ�Ӧ��")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										installNewApk();
										finish();
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// finish();
									}
								}).create();
				installDialog.show();
			}
		});
	}

	protected void installNewApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), appName)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popupwindow != null && popupwindow.isShowing()) {
				popupwindow.dismiss();
			} else {
				if ((System.currentTimeMillis() - exitTime) > 2000) {
					DisToast("�ٰ�һ���˳�����");
					exitTime = System.currentTimeMillis();
				} else
					finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//������Ļ����
	public void setBrightnesss(int brightness) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		if (brightness <= 1) {
			return;
		} else {
			lp.screenBrightness = brightness / 255.0f;
			getWindow().setAttributes(lp);
		}
	}
	//������������
	public static void saveBrightness(ContentResolver resolver, int brightness) {
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness",
				brightness);
		resolver.notifyChange(uri, null);
	}

	@Override
	public void finish() {
		super.finish();
	}
}