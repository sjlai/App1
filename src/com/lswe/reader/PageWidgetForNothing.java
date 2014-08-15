package com.lswe.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
public class PageWidgetForNothing extends View{



	private int mScreenWidth = 480; // 屏幕宽
	private int mScreenHeight = 800; // 屏幕高
	
	Bitmap mCurPageBitmap = null; // 当前页
	Bitmap mNextPageBitmap = null;

	

	
	Paint mPaint;
	//Bitmap mCurPageBitmap = null; // 当前页
	
	public PageWidgetForNothing(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(mCurPageBitmap, 0, 0,null);			
	}

	public PageWidgetForNothing(Context context,int width,int height){
		super(context);
		mScreenWidth = width;
		mScreenHeight = height;
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
	}
	
	public void setBitmaps(Bitmap bm1, Bitmap bm2) {
		mCurPageBitmap = bm1;
		mNextPageBitmap = bm2;
	}
	
	public void UpdatePaint(){
		this.invalidate();
	}
	

}
