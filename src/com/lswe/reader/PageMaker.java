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
	private int begin;// �������ҳʱ�Ŀ�ʼλ��
	private int end;
	private int length;
	private int marginHeight = 50; // �������Ե�ľ���
	private int marginWidth = 50; // �������Ե�ľ���
	private int mLineCount; // ÿҳ������ʾ������
	private Paint mPaint;// ����;
	private float mVisibleHeight; // �������ݵĿ�
	private float mVisibleWidth; // �������ݵĿ�
	private int backColor = 0xffff9e85; // ������ɫ
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
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// ����
		mPaint.setTextAlign(Align.LEFT);// ������
		mPaint.setTextSize(fontSize);// �����С
		mPaint.setColor(textColor);// ������ɫ
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2;
		mLineCount = (int) (mVisibleHeight / fontSize) - 1; // ����ʾ������,-1����Ϊ�ײ���ʾ���ȵ�λ�����ױ���ס
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
		//64���ַ�Ϊһ��string���һ��
		while (bookcontent.length() >= 64) {
			String tempstr = bookcontent.substring(0, 64);
			list.add(tempstr);
			bookcontent = bookcontent.substring(64);
		}
		//������64���ַ���ʱ���ж��պ�ȫ�����
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
		//�ҵ���ǩ�ַ�λ��
		bookcontent = bookcontent.substring(Bookmark);
		System.out.println(bookcontent.length());
		while (bookcontent.length() >= 64) {
			String tempstr = bookcontent.substring(0, 64);
			list.add(tempstr);
			bookcontent = bookcontent.substring(64);
		}
		//������64���ַ���ʱ���ж��պ�ȫ�����
		if (!bookcontent.equals("")) {
			list.add(bookcontent);
		}
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas c) {
		//��һ����ڷ��������ֶ����������С
		mPaint.setTextSize(fontSize);
		mPaint.setColor(textColor);
		//��һ�λ��ƣ�ʹ��pagedown��������breaktest�ǻ�����
		if (lines.size() == 0)
			lines = pageDown();
		//����Ӧ����ǰһ�λ��Ƶ�ҳ��
		if (lines.size() > 0) {
			if (book_bg == null)
				c.drawColor(backColor);
			else
				c.drawBitmap(book_bg, 0, 0, null);
			//y����ǰ���������Ƶ�����
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
	 * ���ҳ
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
		begin = end;// ��һҳҳ��ʼλ��=��ǰҳ����λ��
		lines = pageDown();
	}

	/**
	 * ��ָ��ҳ����һҳ
	 * 
	 * @return ��һҳ������ Vector<String>
	 */
	protected Vector<String> pageDown() {
		//�������û���??
		mPaint.setTextSize(fontSize);
		mPaint.setColor(textColor);
		String strParagraph = "";
		//����ֵ
		Vector<String> lines = new Vector<String>();
		//length������µ�content���ַ����ȣ�mLineCount����ÿһҳ�ɻ��Ƶ����������endӦ���ǵ�ǰ���Ƶ��ַ�����
		while (lines.size() < mLineCount && end < length) {
			strParagraph = readParagraphForward(end);
			end += strParagraph.length();// ÿ�ζ�ȡ�󣬼�¼������λ�ã���λ���Ƕ������λ��
			String strReturn = "";
			// �滻���س����з�
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}
			
			//��Ҫ�������Ӧ��û�п�һ����
			/*if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}*/
			
			
			while (strParagraph.length() > 0) {
				//breakText,��һ���ַ������еķ�������û�л��ƣ���
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);// �õ�ʣ�������
				// ��������������ٻ�
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			// �����ҳ���һ��ֻ��ʾ��һ���֣�����¶�λ������λ��
			if (strParagraph.length() != 0) {
				end -= (strParagraph + strReturn).length();
			}
		}
		return lines;
	}

	/**
	 * ��ȡָ��λ�õ���һ������
	 * 
	 * @param nFromPos
	 * @return byte[]
	 */
	protected String readParagraphForward(int nFromPos) {
		StringBuffer sb = new StringBuffer();
		//������һ�ζ�ȡ��λ��
		int nStart = nFromPos;
		int i = nStart;
		char ch0;
		//�ĵ�����֮ǰ�������ַ�����
		while (i < length) {
			//��ö�Ӧλ�õ��ַ�
			ch0 = ListCharAt(i++);
			// ch0 = bookcontent.charAt(i++);
			//0x0ȷ����һ��
			if (ch0 == 0x0a) {
				break;
			}
		}
		//�õ�������ַ���
		int nParaSize = i - nStart;
		for (i = 0; i < nParaSize; i++) {
			// ch = bookcontent.charAt(nFromPos + i);
			char ch = ListCharAt(i + nFromPos);
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * ��ǰ��ҳ
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
	 * �õ�����ҳ�Ľ���λ��
	 */
	protected void pageUp() {
		if (begin < 0)
			begin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && begin > 0) {
			Vector<String> paraLines = new Vector<String>();
			strParagraph = readParagraphBack(begin);
			begin -= strParagraph.length();// ÿ�ζ�ȡһ�κ�,��¼��ʼ��λ��,�Ƕ��׿�ʼ��λ��
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");
			// ����ǿհ��У�ֱ�����
			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				// ��һ������
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
		end = begin;// ����һҳ�Ľ����������һҳ����ʼ��
		return;
	}

	/**
	 * ��ȡָ��λ�õ���һ������
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
			if (ch0 == 0x0a && i != nEnd - 1) {// 0x0a��ʾ���з�
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

	//��Ϊlistʱ��64һ��string������Ҫ�ҵ�List��λ��iȻ��õ�����λ��j
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
