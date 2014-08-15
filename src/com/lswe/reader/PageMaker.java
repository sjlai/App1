package com.lswe.reader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.lswe.db.LocalTable;

public class PageMaker {
	private String bookcontent;
	private int begin;// 绘制最后页时的开始位置
	private int end;
	private int length;
	private int marginHeight = 50; // 上下与边缘的距离
	private int marginWidth = 50; // 左右与边缘的距离
	private int mLineCount; // 每页可以显示的行数
	private Paint mPaint;// 画笔;
	private float mVisibleHeight; // 绘制内容的宽
	private float mVisibleWidth; // 绘制内容的宽
	private int backColor = 0xffff9e85; // 背景颜色
	private int mHeight;
	private int mWidth;
	private int fontSize = 30;
	private int textColor = R.color.black;
	private Bitmap book_bg = null;
	private boolean isfirst, islast;
	private Vector<String> lines = new Vector<String>();
	private List<String> list = new ArrayList<String>();
	private float fPercent;
	private int Bookmark;

	PageMaker(int width, int height) {
		mWidth = width;
		mHeight = height;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
		mPaint.setTextAlign(Align.LEFT);// 做对其
		mPaint.setTextSize(fontSize);// 字体大小
		mPaint.setColor(textColor);// 字体颜色
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2;
		mLineCount = (int) (mVisibleHeight / fontSize) - 1; // 可显示的行数,-1是因为底部显示进度的位置容易被遮住
		System.out.println(mVisibleWidth + "    " + mWidth);
	}

	public void openBook(String BOOKID, int position) {
		Cursor cursor = LocalTable.GetChapList(BOOKID);
		cursor.moveToPosition(position);
		String chapid = cursor.getString(cursor.getColumnIndex("ChapterID"));
		//System.out.println("chapid:" + chapid);
		Cursor temp = LocalTable.GetChapContent(chapid);
		bookcontent = temp.getString(temp.getColumnIndex("ChapterContent"));
		length = bookcontent.length();
		begin = 0;
		//64个字符为一个string添加一次
		while (bookcontent.length() >= 64) {
			String tempstr = bookcontent.substring(0, 64);
			list.add(tempstr);
			bookcontent = bookcontent.substring(64);
		}
		//当不够64个字符的时候，判定空后全部添加
		if (!bookcontent.equals("")) {
			list.add(bookcontent);
		}
	}
	public void openBookMark(String BOOKID,int position,String percent){
		Cursor cursor = LocalTable.GetChapList(BOOKID);
		cursor.moveToPosition(position);
		String chapid = cursor.getString(cursor.getColumnIndex("ChapterID"));
		Cursor temp = LocalTable.GetChapContent(chapid);
		bookcontent = temp.getString(temp.getColumnIndex("ChapterContent"));
		System.out.println(bookcontent.length());
		length = bookcontent.length();
		Bookmark = (int)(length*Float.parseFloat(percent));
		begin = (int)(length*Float.parseFloat(percent));
		end = (int)(length*Float.parseFloat(percent));
		//找到书签字符位置
		bookcontent = bookcontent.substring(Bookmark);
		System.out.println(bookcontent.length());
		while (bookcontent.length() >= 64) {
			String tempstr = bookcontent.substring(0, 64);
			list.add(tempstr);
			bookcontent = bookcontent.substring(64);
		}
		//当不够64个字符的时候，判定空后全部添加
		if (!bookcontent.equals("")) {
			list.add(bookcontent);
		}
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas c) {
		//有一个借口方法可以手动更改字体大小
		mPaint.setTextSize(fontSize);
		mPaint.setColor(textColor);
		//第一次绘制，使用pagedown方法看来breaktest是绘制了
		if (lines.size() == 0)
			lines = pageDown();
		//存有应该是前一次绘制的页数
		if (lines.size() > 0) {
			if (book_bg == null)
				c.drawColor(backColor);
			else
				c.drawBitmap(book_bg, 0, 0, null);
			//y代表当前的行数绘制的坐标
			int y = marginHeight;
			for (String strLine : lines) {
				y += (fontSize + 1);
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		fPercent = (float) (begin * 1.0 / length);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
	}
	
	public void updateFor(){
		if(lines.size()>0){
			lines.clear();
			lines = pageDown();
		}
	}
	
	public float getPercent(){
		return fPercent;
	}

	/**
	 * 向后翻页
	 * 
	 * @throws IOException
	 */
	public void nextPage() throws IOException {
		if (end >= length) {
			islast = true;
			return;
		} else
			islast = false;
		lines.clear();
		begin = end;// 下一页页起始位置=当前页结束位置
		lines = pageDown();
	}

	/**
	 * 画指定页的下一页
	 * 
	 * @return 下一页的内容 Vector<String>
	 */
	protected Vector<String> pageDown() {
		//重新设置画笔??
		mPaint.setTextSize(fontSize);
		mPaint.setColor(textColor);
		String strParagraph = "";
		//返回值
		Vector<String> lines = new Vector<String>();
		//length代表更新的content的字符长度，mLineCount代表每一页可绘制的最大行数，end应该是当前绘制的字符数量
		while (lines.size() < mLineCount && end < length) {
			strParagraph = readParagraphForward(end);
			end += strParagraph.length();// 每次读取后，记录结束点位置，该位置是段落结束位置
			String strReturn = "";
			// 替换掉回车换行符
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}
			
			//不要这个，就应该没有空一行了
			/*if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}*/
			
			
			while (strParagraph.length() > 0) {
				//breakText,把一串字符串分行的方法，有没有绘制？？
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);// 得到剩余的文字
				// 超出最大行数则不再画
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			// 如果该页最后一段只显示了一部分，则从新定位结束点位置
			if (strParagraph.length() != 0) {
				end -= (strParagraph + strReturn).length();
			}
		}
		return lines;
	}

	/**
	 * 读取指定位置的下一个段落
	 * 
	 * @param nFromPos
	 * @return byte[]
	 */
	protected String readParagraphForward(int nFromPos) {
		StringBuffer sb = new StringBuffer();
		//传入上一次读取的位置
		int nStart = nFromPos;
		int i = nStart;
		char ch0;
		//的到换行之前的所有字符数量
		while (i < length) {
			//获得对应位置的字符
			ch0 = ListCharAt(i++);
			// ch0 = bookcontent.charAt(i++);
			//0x0确定是一段
			if (ch0 == 0x0a) {
				break;
			}
		}
		//得到段落的字符数
		int nParaSize = i - nStart;
		for (i = 0; i < nParaSize; i++) {
			// ch = bookcontent.charAt(nFromPos + i);
			char ch = ListCharAt(i + nFromPos);
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 向前翻页
	 * 
	 * @throws IOException
	 */
	protected void prePage() throws IOException {
		if (begin <= 0) {
			begin = 0;
			isfirst = true;
			return;
		} else
			isfirst = false;
		lines.clear();
		pageUp();
		lines = pageDown();
	}

	/**
	 * 得到上上页的结束位置
	 */
	protected void pageUp() {
		if (begin < 0)
			begin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && begin > 0) {
			Vector<String> paraLines = new Vector<String>();
			strParagraph = readParagraphBack(begin);
			begin -= strParagraph.length();// 每次读取一段后,记录开始点位置,是段首开始的位置
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");
			// 如果是空白行，直接添加
			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				// 画一行文字
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}

		while (lines.size() > mLineCount) {
			begin += lines.get(0).length();
			lines.remove(0);
		}
		end = begin;// 上上一页的结束点等于上一页的起始点
		return;
	}

	/**
	 * 读取指定位置的上一个段落
	 * 
	 * @param nFromPos
	 * @return byte[]
	 */
	protected String readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		char ch0;
		StringBuffer sb = new StringBuffer();
		i = nEnd - 1;
		while (i > 0) {
			// ch0 = bookcontent.charAt(i);
			
			ch0 = ListCharAt(i);
			if (ch0 == 0x0a && i != nEnd - 1) {// 0x0a表示换行符
				i++;
				break;
			}
			i--;
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		for (j = 0; j < nParaSize; j++) {

			char ch = ListCharAt(i + j);
			// char ch = bookcontent.charAt(i + j);
			sb.append(ch);
		}
		return sb.toString();
	}

	//因为list时候64一存string所以需要找到List的位置i然后得到具体位置j
	private char ListCharAt(int position) {
		char ch;
		// System.out.println(position);
		// position = position - 1;
		int i = position / 64;
		int j = position % 64;
		ch = list.get(i).charAt(j);
		// System.out.println(ch);
		return ch;
	}

	public String getFirstLineText() {
		return lines.size() > 0 ? lines.get(0) : "";
	}

	public int getbegin() {
		return begin;
	}

	public void setBgBitmap(Bitmap BG) {
		book_bg = BG;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		mLineCount = (int) (mVisibleHeight / fontSize) - 1;
	}

	public boolean isfirstPage() {
		return isfirst;
	}

	public boolean islastPage() {
		return islast;
	}
}
