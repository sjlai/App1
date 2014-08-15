package com.lswe.reader;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lswe.db.LocalTable;

@SuppressLint("WrongCall")
public class ReaderActivityFornopage extends Activity{
	@Override
	public void finish() {
		super.finish();
	}

	private String BOOKID;
	private int position;// 书籍所在Cursor位置
	private int readHeight; // 屏幕高度
	private int screenWidth;// 屏幕宽度
	private int screenHeight;
	private ActionBar actionBar;
	private static final String TAG = "Read2";
	private static int begin = 0;// 记录的书籍开始位置
	private static String word = "";// 记录当前页面的文字
	View contentView = null;
	AlertDialog dialog;
	
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	//从数据库里面获得字体大小
	private int Size = LocalTable.GetSize();
	
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	
	PageWidgetForNothing mPageForNothingWidget;
	
	private PageMaker pagemaker;//页面绘制类
	private Dialog installDialog;
	private String Blmark;
	private String percent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fornopageread);
		Intent intent = getIntent();
		//listview的位置能不恩能够反应章节id？
		position = intent.getFlags();
		//BOOKID表征点击的是哪一本书
		BOOKID = intent.getStringExtra("BOOKID");
		Blmark =intent.getStringExtra("Blmark");

		percent = intent.getStringExtra("percent");
		screenWidth = ConstDefine.ScreenWidth;//需要确定是在哪个位置更新的ConstDefine。ScreenWidth
		screenHeight = ConstDefine.ScreenHeight;

		
		setmPageWidget(new PageWidgetForNothing(this,screenWidth,screenHeight));
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.readfornopagelayout);
		rlayout.addView(getmPageForNothingWidget());
		
		//阅读文字可达到的最大高度
		readHeight = screenHeight - (20 * screenWidth) / 320;
		//Bitmap.Config ARGB_8888 32 每个像素 占八位 
		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		
		actionBar = getActionBar();
		// 设置是否显示应用程序图标
		//actionBar.setDisplayShowHomeEnabled(true);
		// 将应用程序图标设置为可点击的按钮
		//actionBar.setHomeButtonEnabled(true);
		// 将应用程序图标设置为可点击的按钮，并在图标上添加向左箭头
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide();
		
		Button bnSet = new Button(this);
		bnSet.setLayoutParams(new ViewGroup.LayoutParams(150,480));
		bnSet.setX(screenWidth/2-50);
		bnSet.setY(screenHeight/2-50);
		bnSet.setAlpha(0);
		rlayout.addView(bnSet);
		
		bnSet.setOnClickListener(new MyListener());
		//设置两张bitmap进行绘制
		getmPageForNothingWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
		getmPageForNothingWidget().setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					if (e.getY() > readHeight) {// 超出范围了，表示单击到广告条，则不做翻页
						return false;
					}
					
					if(e.getX()>=screenWidth/2){
						System.out.println("take right");
						try {
							pagemaker.nextPage();
							begin = pagemaker.getbegin();// 获取当前阅读位置
							word = pagemaker.getFirstLineText();// 获取当前阅读位置的首行文字
						} catch (IOException e1) {
							Log.e(TAG, "onTouch->nextPage error", e1);
						}
						if (pagemaker.islastPage()) {
							DisToast("已经是最后一页了");
							return false;
						}
						
					}
					if(e.getX()<screenHeight/2-10){
						try {
							pagemaker.prePage();
							begin = pagemaker.getbegin();// 获取当前阅读位置
							word = pagemaker.getFirstLineText();// 获取当前阅读位置的首行文字
						} catch (IOException e1) {
							Log.e(TAG, "onTouch->prePage error", e1);
						}
						if (pagemaker.isfirstPage()) {
							DisToast("当前是第一页");
							return false;
						}
					}
					pagemaker.onDraw(mCurPageCanvas);
					getmPageForNothingWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
					getmPageForNothingWidget().UpdatePaint();
				}
				return false;
			}
			
		});
		
		//初始化绘制页面的参数，画笔设置，绘制区域，绘制每页的行数。
		pagemaker = new PageMaker(screenWidth, readHeight);
		//设置阅读背景
		pagemaker.setBgBitmap(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bg));
		//设置字体大小，颜色
		pagemaker.setFontSize(Size);
		pagemaker.setTextColor(Color.rgb(28, 28, 28));
		
		try {
			//通过booklist传入的书本id和章节位置，更新PageMaker类中的list<String>list.
			if(Blmark.equalsIgnoreCase("false")){
				pagemaker.openBook(BOOKID, position);
				pagemaker.onDraw(mCurPageCanvas);
				}
			if(Blmark.equalsIgnoreCase("true")){
				System.out.println("mark");
				pagemaker.openBookMark(BOOKID,position,percent);
				pagemaker.onDraw(mCurPageCanvas);	
				}
		} catch (Exception e) {
			installDialog = new AlertDialog.Builder(ReaderActivityFornopage.this)
					.setTitle("权限不足")
					.setMessage("请去购买李幺傻书迷会员卡")
					.setPositiveButton("去购买",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									Uri uri = Uri
											.parse("http://item.taobao.com/item.htm?spm=a1z10.1.w4004-6619519382.5.seAZLf&id=38542856255");
									intent = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									installDialog.dismiss();
									// Intent intent = new Intent();
									// intent.setClass(getApplicationContext(),
									// BookList.class);
									// startActivity(intent);
									// ReadActivity.this.finish();
								}
							}).create();
			installDialog.show();

		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflator = new MenuInflater(this);
		// 状态R.menu.context对应的菜单，并添加到menu中
		inflator.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	

	
	public class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
				// 点击屏幕中央部分 显示popwindowF
				/*if (popupwindow != null) {
					if (popupwindow.isShowing())
						popupwindow.dismiss();
					else
						PopUpWindowSow();

				} else {
					PopUpWindowSow();
				}*/
				
				//显示actionbar
				if(actionBar.isShowing()){
					actionBar.hide();
				}else{
					actionBar.show();
				}
			
		}
		
	}
	

	
	@Override
	// 选项菜单的菜单项被单击后的回调方法
	public boolean onOptionsItemSelected(MenuItem mi)
	{
		if(mi.isCheckable())
		{
			mi.setChecked(true);
		}
		// 判断单击的是哪个菜单项，并针对性的作出响应。
		switch (mi.getItemId())
		{
			case android.R.id.home:
				Intent intent = new Intent();
				intent.setClass(ReaderActivityFornopage.this, BookChapList.class);
				startActivity(intent);
				break;
			case R.id.lightSetting:
				Intent temintent=new Intent();
				temintent.setClass(this, DialogActivity.class);
				startActivity(temintent);
				//添加开启activity的动画
				overridePendingTransition(R.anim.dialog_enter, 0);
				break;
			case R.id.wordAdd:
				LocalTable.updateSetting(true);
				pagemaker.setFontSize(LocalTable.GetSize());
				pagemaker.updateFor();
				pagemaker.onDraw(mCurPageCanvas);
				getmPageForNothingWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
				getmPageForNothingWidget().UpdatePaint();
				break;
			case R.id.wordMinus:
				LocalTable.updateSetting(false);
				pagemaker.setFontSize(LocalTable.GetSize());
				pagemaker.updateFor();
				pagemaker.onDraw(mCurPageCanvas);
				getmPageForNothingWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
				getmPageForNothingWidget().UpdatePaint();
				break;
			case R.id.secure:
				Intent myIntent = new Intent();
				float percent = pagemaker.getPercent();
				String Blmark = "true";
				myIntent.setClass(ReaderActivityFornopage.this, BookMarkActivity.class);
				myIntent.setFlags(position);
				myIntent.putExtra("BOOKID",BOOKID);
				myIntent.putExtra("percent", percent+"");
				myIntent.putExtra("Blmark", Blmark);
				myIntent.putExtra("Select","false" );
				//myIntent.putExtra("percent",pagemaker.getPercent());
				
				//System.out.println("BOOKID=="+BOOKID+"position=="+position+"percent=="+percent);
				startActivity(myIntent);
				break;

			case R.id.plain_item:
				AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivityFornopage.this);
				builder.setTitle("确定要设置书签")
				.setMessage("书名，章节名，百分比")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent myIntent = new Intent();
						float percent = pagemaker.getPercent();
						myIntent.setClass(ReaderActivityFornopage.this, BookMarkActivity.class);
						myIntent.setFlags(position);
						myIntent.putExtra("BOOKID",BOOKID);
						myIntent.putExtra("percent", percent+"");
						myIntent.putExtra("Select", "true");
						
					//System.out.println("BOOKID=="+BOOKID+"position=="+position+"percent=="+percent);
						startActivity(myIntent);
					}
				})
				.setNegativeButton("取消", null)
				.create()
				.show();
				break;

		}
		return true;
	}
	
	private void setmPageWidget(PageWidgetForNothing mPageForNothingWidget) {
		this.mPageForNothingWidget = mPageForNothingWidget;
	}
	PageWidgetForNothing getmPageForNothingWidget() {
		return mPageForNothingWidget;
	}
	
	private void DisToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
}
