package com.lswe.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lswe.db.LocalTable;

public class BookMarkActivity extends Activity{

	private String BOOKID;
	private String[]bookmark = new String[6];
	private int position;// �鼮����Cursorλ��
	private String percent;
	private String BookName;
	private String BookChapName;
	private String Author="";
	private SimpleAdapter bookMarkAdapter;
	private List<Map<String, String>> list;
	ListView listView2;
	LocalTable localTable;
	private String Select;
	private String Blmark = "true";
	private List<Map<String, String>> listItem = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);
		Intent intent =getIntent();
		position = intent.getFlags();
		BOOKID = intent.getStringExtra("BOOKID");
		percent = intent.getStringExtra("percent");
		Select = intent.getStringExtra("Select");
		System.out.println(Select);
		BookName = query2();
		BookChapName = getBookChapName();
		//������������ͬһ��actiivty����������ж��ǲ�����Ҫ�����ǩ
		if(Select.equalsIgnoreCase("true")){
			Insert(BOOKID,position+"",BookName,BookChapName,"����ɵ",percent);
		}
		
		listView2 = (ListView)findViewById(R.id.listView2);	
		init();
		adapter();
		listView2.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {		
				Cursor cursor = LocalTable.getBookMarkCursor();
				cursor.moveToPosition(position);
				int Iposition = Integer.parseInt(cursor.getString(cursor.getColumnIndex("position")));
				Intent intent = new Intent();
				intent.setClass(BookMarkActivity.this,ReaderActivityFornopage.class);
				intent.setFlags(Iposition);
				intent.putExtra("BOOKID", cursor.getString(cursor.getColumnIndex("BOOKID")));
				//intent.putExtra("position", cursor.getString(cursor.getColumnIndex("position")));
				intent.putExtra("percent", cursor.getString(cursor.getColumnIndex("Percent")));
				intent.putExtra("Blmark", Blmark);
				System.out.println("--->mypercent"+cursor.getString(cursor.getColumnIndex("Percent")));
				System.out.println("--->position"+cursor.getString(cursor.getColumnIndex("position")));
				System.out.println("--->Bookid"+cursor.getString(cursor.getColumnIndex("BOOKID")));
				System.out.println(Iposition);
				startActivity(intent);
			}
		});
		listView2.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(BookMarkActivity.this);
				dialog.setTitle("ɾ����ǩ");
				dialog.setMessage("ȷ��ɾ����ǩ")
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Map<String,String> mapFordelete = listItem.get(position);
						Object[] bindArgs = {mapFordelete.get("ID")};
						System.out.println(mapFordelete.get("ID"));
							String sql = "delete  from  Bookmarks  where ID = ? ";
							LocalTable.updateBySQL(sql,bindArgs);
						listItem.remove(position);
						bookMarkAdapter.notifyDataSetChanged();
						//listView2.notify();
					}
				})
				.setNegativeButton("ȡ��", null)
				.create()
				.show();
				return true;
			}
			
		});
	}
	private void Insert(String BOOKID,String position,String BookName,String BookChapName,String Author,String percent){
		bookmark[0] = BOOKID;
		bookmark[1] = position;
		bookmark[2] = BookName;
		bookmark[3] = BookChapName;
		bookmark[4] = Author;
		bookmark[5] = percent;
		LocalTable.InsertBookMark(bookmark);
	} 
	private void adapter() {
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		bookMarkAdapter = new SimpleAdapter(BookMarkActivity.this, listItem,// ����Դ
				R.layout.item,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "bookname", "author", "bookChapname", "bf" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] {R.id.bookname,R.id.bookChapname,R.id.author,R.id.bf });
		// ��Ӳ�����ʾ
		listView2.setAdapter(bookMarkAdapter);
	}
	
	public void init(){
		listItem = new ArrayList<Map<String, String>>();
		list = query3();
		System.out.println(list.size());
		for(int i = 0;i<list.size();i++){
			Map<String, String> map = new HashMap<String,String>();
			map = list.get(i);
			Map<String,String>mapforItem = new HashMap<String,String>();
			mapforItem.put("bookname", map.get("BookName"));
			mapforItem.put("bookChapname", map.get("BookChapname"));
			mapforItem.put("author", "����ɵ");
			mapforItem.put("bf", map.get("Percent"));
			mapforItem.put("ID",map.get("ID"));
			listItem.add(mapforItem);
		}

		/*if (listItem == null)
			listItem = new ArrayList<Map<String, String>>();
		listItem.clear();
		listItem = map;*/
	}
	
	public String  query2() {
		String sql = "select *   from  Books where BOOKID = ?  ";
		String BookName;
		Map<String, String> map = LocalTable.queryBySQL(sql, new String[] { BOOKID });
		BookName = map.get("title");
		return BookName;
	}
	
	public List<Map<String, String>> query3(){
		String sql = "select * from Bookmarks";
		List<Map<String,String>> map = new ArrayList<Map<String,String>>();
		map = LocalTable.getBookMarkList(sql,null);
		return map;
	}
	
	public String getBookChapName(){
		String BookChapName;
		Cursor curforChap = LocalTable.getChapCursor();
		curforChap.moveToPosition(position);
		BookChapName = curforChap.getString(curforChap.getColumnIndex("title"));
		return BookChapName;
	}
	
}
