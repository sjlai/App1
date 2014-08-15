package com.lswe.reader;

import java.io.IOException;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lswe.db.LocalTable;

public class ReadActivity extends Activity implements OnLongClickListener {
	private int position;// �鼮����Cursorλ��
	private int readHeight; // ��Ļ�߶�
	private int screenWidth;// ��Ļ���
	private int screenHeight;
	private PageWidget mPageWidget;
	private PageMaker pagemaker;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	public int CurrentPage, PageaMount;
	private PopupWindow popupwindow;
	private static int begin = 0;// ��¼���鼮��ʼλ��
	private static String word = "";// ��¼��ǰҳ�������
	View contentView = null;
	private static final String TAG = "Read2";
	private boolean show = false;
	private int Size = LocalTable.GetSize();
	private String BOOKID;
	private Dialog installDialog;
	private Button bnSet;
	private boolean showChoose = false;
	private Handler screenHandler;
	private Boolean Blmark = false;
	private String percent;
	@SuppressLint("WrongCall")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read);
		//������Ļ����
		final WindowManager.LayoutParams lp = getWindow().getAttributes();

		//lp.screenBrightness = 1.0f;

		//getWindow().setAttributes(lp);
		screenHandler = new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				case 0:
					if(lp.screenBrightness<=1.0){
					lp.screenBrightness +=0.5f;
					System.out.println("down");
					}
					break;
				case 1:
					lp.screenBrightness -=0.5f;
					System.out.println("down");
					break;
				}
				getWindow().setAttributes(lp);
			}
		};

		Intent intent = getIntent();
		position = intent.getFlags();
		//BOOKID�������������һ����
		BOOKID = intent.getStringExtra("BOOKID");
		//�ж����Զ���򿪣�������ǩ��
		Blmark = Boolean.getBoolean(intent.getStringExtra("Blmark"));
		percent = intent.getStringExtra("percent");
		screenWidth = ConstDefine.ScreenWidth;
		screenHeight = ConstDefine.ScreenHeight;
		//��ʼ����ҳ������
		setmPageWidget(new PageWidget(this, screenWidth, readHeight));// ҳ��
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.readlayout);
		rlayout.addView(getmPageWidget());
		
		Button bnSet = new Button(this);
		bnSet.setLayoutParams(new ViewGroup.LayoutParams(150,150));
		bnSet.setX(screenWidth/2-50);
		bnSet.setY(screenHeight/2-50);
		bnSet.setAlpha(0);
		rlayout.addView(bnSet);
		
		//bnSet.setOnClickListener(new MyListener());

		// defaultSize = (screenWidth * 20) / 320;
		//�Ķ����ֿɴﵽ�����߶�
		readHeight = screenHeight - (20 * screenWidth) / 320;
		//Bitmap.Config ARGB_8888 32 ÿ������ ռ��λ 
		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		getmPageWidget().setBitmaps(mCurPageBitmap, mCurPageBitmap);
		getmPageWidget().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				boolean ret = false;
				if (v == getmPageWidget()) {
					if (!show) {
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							if (e.getY() > readHeight) {// ������Χ�ˣ���ʾ�������������������ҳ
								return false;
							}
							getmPageWidget().abortAnimation();
							
							getmPageWidget().calcCornerXY(e.getX(), e.getY());
							pagemaker.onDraw(mCurPageCanvas);
							if (getmPageWidget().DragToRight()) {// ��
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
								pagemaker.onDraw(mNextPageCanvas);
							} else if (!getmPageWidget().DragToRight()) {// �ҷ�
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
								pagemaker.onDraw(mNextPageCanvas);
							}
							getmPageWidget().setBitmaps(mCurPageBitmap,
									mNextPageBitmap);
						}
						// editor.putInt(bookPath + "begin", begin).commit();
						ret = getmPageWidget().doTouchEvent(e);
						return ret;
					}
				}
				return false;
			}

		});
		// �鹤��
		//��ʼ������ҳ��Ĳ���
		pagemaker = new PageMaker(screenWidth, readHeight);
		pagemaker.setBgBitmap(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.background));
		//���������С
		pagemaker.setFontSize(Size);
		pagemaker.setTextColor(Color.rgb(28, 28, 28));
		try {
			if(!Blmark){
			pagemaker.openBook(BOOKID, position);
			pagemaker.onDraw(mCurPageCanvas);
			}else{
			pagemaker.openBookMark(BOOKID,position,percent);
			pagemaker.onDraw(mCurPageCanvas);
			}
		} catch (Exception e) {
			installDialog = new AlertDialog.Builder(ReadActivity.this)
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
		// CurrentPage =
		// pagemaker.findInt(LocalTable.GeteBooK(position).YiYueDu);
		// PageaMount = pagemaker.getPageAmount();

		// pagemaker.Draw(mCurPageCanvas, 0);
	}

	/*public class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!showChoose){
				// �����Ļ���벿�� ��ʾpopwindowF
				if (popupwindow != null) {
					if (popupwindow.isShowing())
						popupwindow.dismiss();
					else
						PopUpWindowSow();

				} else {
					PopUpWindowSow();
				}
			}
		}
		
	}*/
	/*private void Click() {
		// �����Ļ���벿�� ��ʾpopwindowF
		if (popupwindow != null) {
			if (popupwindow.isShowing())
				popupwindow.dismiss();
			else
				PopUpWindowSow();

		} else {
			PopUpWindowSow();
		}
	}*/

	/*private void PopUpWindowSow() {
		if (popupwindow == null)
			contentView = getLayoutInflater().inflate(R.layout.read_pop, null);

		Button bnwordAdd = (Button)contentView.findViewById(R.id.wordAdd);
		bnwordAdd.setOnClickListener(new ButtonListener());
		Button bnwordMinus = (Button)contentView.findViewById(R.id.wordMinus);
		bnwordMinus.setOnClickListener(new ButtonListener());
		Button bnlightAdd = (Button)contentView.findViewById(R.id.lightAdd);
		bnlightAdd.setOnClickListener(new ButtonListener());
		Button bnlightMinus = (Button)contentView.findViewById(R.id.lightMinus);
		bnlightMinus.setOnClickListener(new ButtonListener());
		
		
		TextView tv = new TextView(getApplicationContext());
		tv = (TextView) contentView.findViewById(R.id.textView1);
		tv.setText("�Ķ����ȣ�");
		SeekBar sb = new SeekBar(getApplicationContext());
		sb = (SeekBar) contentView.findViewById(R.id.seekBar1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
		// ����������������
		popupwindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		// popupwindow.setFocusable(true);
		popupwindow.showAtLocation(findViewById(R.id.readcall), Gravity.BOTTOM,
				0, 0);
	}
	public class ButtonListener implements OnClickListener{
		@SuppressLint("WrongCall")
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.wordAdd:
				LocalTable.updateSetting(true);
				pagemaker.setFontSize(LocalTable.GetSize());
				pagemaker.onDraw(mCurPageCanvas);
				getmPageWidget().setBitmaps(mCurPageBitmap,
						mNextPageBitmap);
				mPageWidget.updateView();
				break;
			case R.id.wordMinus:
				LocalTable.updateSetting(false);
				pagemaker.setFontSize(LocalTable.GetSize());
				pagemaker.onDraw(mCurPageCanvas);
				getmPageWidget().setBitmaps(mCurPageBitmap,
						mNextPageBitmap);
				mPageWidget.updateView();
				break;
			case R.id.lightAdd:
				screenHandler.sendEmptyMessage(0);
				break;
			case R.id.lightMinus:
				screenHandler.sendEmptyMessage(1);
				break;
			}

				
			
		}
	}*/

	private void DisToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onLongClick(View v) {
		//Click();
		return true;
	}

	private PageWidget getmPageWidget() {
		return mPageWidget;
	}

	private void setmPageWidget(PageWidget mPageWidget) {
		this.mPageWidget = mPageWidget;
	}


}