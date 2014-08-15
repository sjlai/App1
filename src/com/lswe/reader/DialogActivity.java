package com.lswe.reader;


import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DialogActivity extends Activity{
	private SeekBar lightSeekBar;
	ReaderActivityFornopage raf;
	private static int screenHeight;
	
	
	 public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
         this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏显示        
		 	setContentView(R.layout.setting);
	 		init();  
	 		updateToggles();
	 }
	 
	 public void init(){
  		 screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();//获取屏幕高度
  		 
         WindowManager.LayoutParams lp = getWindow().getAttributes();////lp包含了布局的很多信息，通过lp来设置对话框的布局
         lp.width = LayoutParams.FILL_PARENT;
         lp.gravity = Gravity.BOTTOM;
         lp.height=screenHeight/8;//lp高度设置为屏幕的1/8
         getWindow().setAttributes(lp);//将设置好属性的lp应用到对话框
         
         //button_cancle.setHeight(lp.height/6);//将button的高度设置为对话框的1/6
	 }
     private void updateToggles() {
         // TODO Auto-generated method stub
    	 lightSeekBar = (SeekBar) findViewById(R.id.lightSetting);
    	 lightSeekBar.setProgress((int) (android.provider.Settings.System.getInt(
                         getContentResolver(),
                         android.provider.Settings.System.SCREEN_BRIGHTNESS, 255) ));
    	 lightSeekBar.setOnSeekBarChangeListener(seekListener);
 }

 private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

         public void onProgressChanged(SeekBar seekBar, int progress,
                         boolean fromUser) {
                 if (fromUser) {
                         Integer tmpInt = seekBar.getProgress();
                         System.out.println(tmpInt);
                         // 51 (seek scale) * 5 = 255 (max brightness)
                         // Old way
                         android.provider.Settings.System.putInt(getContentResolver(),
                                         android.provider.Settings.System.SCREEN_BRIGHTNESS,
                                         tmpInt); // 0-255
                         tmpInt = Settings.System.getInt(getContentResolver(),
                                         Settings.System.SCREEN_BRIGHTNESS, -1);
                         WindowManager.LayoutParams lp = getWindow().getAttributes();
                         // lp.screenBrightness = 1.0f;
                         // Float tmpFloat = (float)tmpInt / 255;
                         if (0<= tmpInt && tmpInt <= 255) {
                                 lp.screenBrightness = tmpInt;
                         }                       
                         getWindow().setAttributes(lp);
                 }

         }

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
 	};
}
