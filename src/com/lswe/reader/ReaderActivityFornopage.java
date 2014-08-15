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
	private int position;// �鼮����Cursorλ��
	private int readHeight; // ��Ļ�߶�
	private int screenWidth;// ��Ļ���
	private int screenHeight;
	private ActionBar actionBar;
	private static final String TAG = "Read2";
	private static int begin = 0;// ��¼���鼮��ʼλ��
	private static String word = "";// ��¼��ǰҳ�������
	View contentView = null;
	AlertDialog dialog;
	
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	//�����ݿ������������С
	private int Size = LocalTable.GetSize();
	
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	
	PageWidgetForNothing mPageForNothingWidget;
	
	private PageMaker pagemaker;//ҳ�������
	private Dialog installDialog;
	private String Blmark;
	private String percent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fornopageread);
		Intent intent = getIntent();
		//listview��λ���ܲ����ܹ���Ӧ�½�id��
		position = intent.getFlags();
		//BOOKID�������������һ����
		BOOKID = intent.getStringExtra("BOOKID");
		Blmark =intent.getStringExtra("Blmark");

		percent = intent.getStringExtra("percent");
		screenWidth = ConstDefine.ScreenWidth;//��Ҫȷ�������ĸ�λ�ø��µ�ConstDefine��ScreenWidth
		screenHeight = ConstDefine.ScreenHeight;

		
		setmPageWidget(new PageWidgetForNothing(this,screenWidth,screenHeight));
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.readfornopagelayout);
		rlayout.addView(getmPageForNothingWidget());
		
		//�Ķ����ֿɴﵽ�����߶�
		readHeight = screenHeight - (20 * screenWidth) / 320;
		//Bitmap.Config ARGB_8888 32 ÿ������ ռ��λ 
		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		
		actionBar = getActionBar();
		// �����Ƿ���ʾӦ�ó���ͼ��
		//actionBar.setDisplayShowHomeEnabled(true);
		// ��Ӧ�ó���ͼ������Ϊ�ɵ���İ�ť
		//actionBar.setHomeButtonEnabled(true);
		// ��Ӧ�ó���ͼ������Ϊ�ɵ���İ�ť������ͼ������������ͷ
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide();
		
		Button bnSet = new Button(this);
		bnSet.setLayoutParams(new ViewGroup.LayoutParams(150,480));
		bnSet.setX(screenWidth/2-50);
		bnSet.setY(screenHeight/2-50);
		bnSet.setAlpha(0);
		rlayout.addView(bnSet);
		
		bnSet.setOnClickListener(new MyListener());
		//��������bitmap���л���
		getmPageForNothingWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
		getmPageForNothingWidget().setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					if (e.getY() > readHeight) {// ������Χ�ˣ���ʾ�������������������ҳ
						return false;
					}
					
					if(e.getX()>=screenWidth/2){
						System.out.println("take right");
						try {
							pagemaker.nextPage();
							begin = pagemaker.getbegin();// ��ȡ��ǰ�Ķ�λ��
							word = pagemaker.getFirstLineText();// ��ȡ��ǰ�Ķ�λ�õ���������
						} catch (IOException e1) {
							Log.e(TAG, "onTouch->nextPage error", e1);
						}
						if (pagemaker.islastPage()) {
							DisToast("�Ѿ������һҳ��");
							return false;
						}
						
					}
					if(e.getX()<screenHeight/2-10){
						try {
							pagemaker.prePage();
							begin = pagemaker.getbegin();// ��ȡ��ǰ�Ķ�λ��
							word = pagemaker.getFirstLineText();// ��ȡ��ǰ�Ķ�λ�õ���������
						} catch (IOException e1) {
							Log.e(TAG, "onTouch->prePage error", e1);
						}
						if (pagemaker.isfirstPage()) {
							DisToast("��ǰ�ǵ�һҳ");
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
		
		//��ʼ������ҳ��Ĳ������������ã��������򣬻���ÿҳ��������
		pagemaker = new PageMaker(screenWidth, readHeight);
		//�����Ķ�����
		pagemaker.setBgBitmap(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bg));
		//���������С����ɫ
		pagemaker.setFontSize(Size);
		pagemaker.setTextColor(Color.rgb(28, 28, 28));
		
		try {
			//ͨ��booklist������鱾id���½�λ�ã�����PageMaker���е�list<String>list.
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
					.setTitle("Ȩ�޲���")
					.setMessage("��ȥ��������ɵ���Ի�Ա��")
					.setPositiveButton("ȥ����",
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
					.setNegativeButton("ȡ��",
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
		// ״̬R.menu.context��Ӧ�Ĳ˵�������ӵ�menu��
		inflator.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	

	
	public class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
				// �����Ļ���벿�� ��ʾpopwindowF
				/*if (popupwindow != null) {
					if (popupwindow.isShowing())
						popupwindow.dismiss();
					else
						PopUpWindowSow();

				} else {
					PopUpWindowSow();
				}*/
				
				//��ʾactionbar
				if(actionBar.isShowing()){
					actionBar.hide();
				}else{
					actionBar.show();
				}
			
		}
		
	}
	

	
	@Override
	// ѡ��˵��Ĳ˵��������Ļص�����
	public boolean onOptionsItemSelected(MenuItem mi)
	{
		if(mi.isCheckable())
		{
			mi.setChecked(true);
		}
		// �жϵ��������ĸ��˵��������Ե�������Ӧ��
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
				//��ӿ���activity�Ķ���
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
				builder.setTitle("ȷ��Ҫ������ǩ")
				.setMessage("�������½������ٷֱ�")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
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
				.setNegativeButton("ȡ��", null)
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
