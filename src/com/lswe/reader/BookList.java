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
	//屏幕亮度调节值
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
				//点击章节的位置，传入以便数据库读取
				intent.setFlags(position);
				startActivity(intent);
			}
		});
		bl_ibtn.setOnClickListener(new ImageBtnlistener());
		set_btn.setOnClickListener(new ImageBtnlistener());
		bl_drawer.setOnClickListener(new ImageBtnlistener());
		//bnChange.setOnClickListener(new ImageBtnlistener());
		//通过服务器传回jason判定当前版本是不是最新，如果是不是最新，通过传回地址进行更新下载
		updateThread ut = new updateThread();
		ut.start();
		//通过服务器传回的jason判定当前的书籍有没有更新，如果有则更新，没有则不用，以此为自动更新
		//借此取消手动更新，只允许自动更新，通过jason返回值自定更新内容大小
		//updatebookThread ubt = new updatebookThread();
		//ubt.start();
	}

	/* 获取控件并获得分类和列表 */
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
				// 设置按钮
				DisToast("设置按钮");
				PopUpWindowSow();
				break;
			case R.id.bl_ibtn:
				// 更新按钮
				LocalTable.DeleteLoc();
				DisToast("更新按钮");
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
							dialog.setTitle("警告");
							dialog.setMessage("网络断开连接");
							dialog.setPositiveButton("重试", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									uat.start();
								}
							});
							dialog.setNegativeButton("取消", null);
							}

					} 
					
				};
				 uat = new UpdateAuthorThread();
				uat.start();
				pd = ProgressDialog.show(BookList.this, "加载中", "请稍后...");
				break;
			case R.id.bl_drawer:
				// 抽屉开关
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
			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
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
			holder.baifen.setText("阅读进度：" + Integer.valueOf("0") + "/"
					+ Integer.valueOf("100"));
		}

		final class ListViewHolder {
			public TextView bookname;
			public TextView zuozhe;
			public TextView baifen;
			public ImageView tupian;
		}
	}

	/* 设置按钮窗口 */
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
			//返回包含SettingInfo的List，添加了设定界面的所有文字
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
					// 根据自定义的Item布局加载布局
					if (SetInfos.get(position).bjw == 1) {
						convertView = inflater.inflate(
								R.layout.pop_listview_simplecontent, null);
						svholder = new SetViewHolder();
						svholder.SzInfo = (TextView) convertView
								.findViewById(R.id.userId);
						// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
						convertView.setTag(svholder);
					} else {
						convertView = inflater.inflate(
								R.layout.pop_list_content, null);
						svholder = new SetViewHolder();
						svholder.SzInfo = (TextView) convertView
								.findViewById(R.id.firsttv);
						// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
						convertView.setTag(svholder);
					}
				} else {
					// 取出ViewHolder对象
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
						DisToast("再按一次注销登录");
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
					DisToast("加大字号" + "  " + "当前字体大小："
							+ LocalTable.updateSetting(true));
					break;
				case 4:
					DisToast("减小字号" + "  " + "当前字体大小："
							+ LocalTable.updateSetting(false));
					break;
				case 5:
					if(brighnessPro<=255){
						brighnessPro+=5;
					setBrightnesss(brighnessPro);
					}
					DisToast("屏幕变亮");
					break;
				case 6:
					if(brighnessPro>=55){
						brighnessPro-=5;
					setBrightnesss(brighnessPro);
					}
					DisToast("屏幕变暗");
					break;
				default:
					break;
				}
			}
		});
		// 设置搜索窗口属性
		popupwindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		popupwindow.setFocusable(true);
		//设置popupwindow响应返回键必须的一句话
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		popupwindow.showAtLocation(findViewById(R.id.drawer_layout),
				Gravity.CENTER, 0, 0);
	}

	//如果需要自动更新，如何判定更新的内容？
	class UpdateAuthorThread extends Thread {		
		List<Integer> authorlist = new ArrayList<Integer>();
		@Override
		public void run() {
			super.run();
			
			IntentFilter intentFilter = new IntentFilter(); 
			intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
			registerReceiver(connectionReceiver, intentFilter); 
			mHttpSer = new HttpSer();
			//通过服务器获得作者名字和作者id，返回作者id，每一位作者有唯一标示的id
			authorlist = mHttpSer.doPost_AuthorList();
			for (int i = 0; i < authorlist.size(); i++) {
				List<Integer> booklist = new ArrayList<Integer>();
				//通过作者id，获得每一位作者的书籍列表，包括ID, title, AuthorID,photoUrl, status
				booklist = mHttpSer.doPost_AuthorBookList(authorlist.get(i));
				for (int j = 0; j < booklist.size(); j++) {
					//通过书籍名称获得章节列表
					List<Integer> chaplist = new ArrayList<Integer>();
					chaplist = mHttpSer.doPost_AuthorBookChapterList(booklist
							.get(j));
					for (int k = 0; k < chaplist.size(); k++) {
						//获得当前账户用户名
						String name = LocalTable.getLocUser()[0];
						System.out.println(name);
						boolean permission = ("test").equals(name);
						//测试进入只获得第一本书的内容
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
			//返回一个string一维数组，第一个数据为版本号，第二个数据为下载地址
			version = hs.getVersion();
			if (!CurVersion.equals(version[0])) {
				String url = "http://" + version[1];
				downAppFile(url);
			}
		}
	}
	
	//得找到那一位作者的哪一本书的哪一章节更新
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
			//返回一个数组，包括是否有更新，更新的作者和章节
			
			if(isUpdateBook){
				AlertDialog.Builder builder = new AlertDialog.Builder(BookList.this);
				builder.setTitle("更新提示");
				builder.setMessage("书籍内容有更新是否需要更新");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//进行更新内容，通过得到的作者名，书籍名，章节名进行更新,应该在加一个参数all代表所有数据都要更新
						if(Boolean.getBoolean(all)){
							//更新所有内容，针对于第一次登录
							LocalTable.DeleteLoc();
							UpdateAuthorThread uat = new UpdateAuthorThread();
							uat.start();
							pd = ProgressDialog.show(BookList.this, "加载中", "请稍后...");
						}else{
						if(!AuthorID.equals("")){
							LocalTable.DeleteAuthor(Integer.parseInt(AuthorID));
							onlyUpdateAuthorThread  ouat = new onlyUpdateAuthorThread(AuthorID);
							ouat.start();
							pd = ProgressDialog.show(BookList.this, "加载中", "请稍后...");
						}else{
							if(!BookID.equals("")){
								LocalTable.DeleteBook(Integer.parseInt(BookID));
								onlyUpdateBookThread oubt = new onlyUpdateBookThread(BookID);
								oubt.start();
								pd = ProgressDialog.show(BookList.this, "加载中", "请稍后...");
							}else{
								if(!chapID.equals("")){
								LocalTable.DeleteContent(Integer.parseInt(chapID));
								onlyUpdateChapThread ouct = new onlyUpdateChapThread(chapID);
								pd = ProgressDialog.show(BookList.this, "加载中", "请稍后...");
									}
							}
						}
					}
					}
				});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
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
				//通过书籍名称获得章节列表
				List<Integer> chaplist = new ArrayList<Integer>();
				chaplist = mHttpSer.doPost_AuthorBookChapterList(booklist
						.get(j));
				for (int k = 0; k < chaplist.size(); k++) {
					//获得当前账户用户名
					String name = LocalTable.getLocUser()[0];
					System.out.println(name);
					boolean permission = ("test").equals(name);
					//测试进入只获得第一本书的内容
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
			//通过书籍名称获得章节列表
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
						.setTitle("下载完成")
						.setMessage("是否安装新的应用")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										installNewApk();
										finish();
									}
								})
						.setNegativeButton("取消",
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
					DisToast("再按一次退出程序");
					exitTime = System.currentTimeMillis();
				} else
					finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置屏幕亮度
	public void setBrightnesss(int brightness) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		if (brightness <= 1) {
			return;
		} else {
			lp.screenBrightness = brightness / 255.0f;
			getWindow().setAttributes(lp);
		}
	}
	//保存亮度设置
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