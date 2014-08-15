package com.lswe.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lswe.db.LocalTable;

public class BookChapList extends Activity {
	private ListView lv;
	private int position;
	private String BOOKID;
	CursorAdp cursorAdp;
	String[] chapName;
	private String Blmark = "false";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookchaplist);
		Intent intent = getIntent();
		position = intent.getFlags();
		Cursor cur = LocalTable.getCursor();
		cur.moveToPosition(position);
		BOOKID = cur.getString(cur.getColumnIndex("BOOKID"));
		
		cursorAdp = new CursorAdp(this, LocalTable.GetChapList(BOOKID), true);
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(cursorAdp);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent tempintent = new Intent();
				tempintent
						.setClass(getApplicationContext(), ReaderActivityFornopage.class);
				tempintent.setFlags(position);
				tempintent.putExtra("BOOKID", BOOKID);
				tempintent.putExtra("Blmark", Blmark);
				startActivity(tempintent);
			}
		});
	}

	class CursorAdp extends CursorAdapter {

		public CursorAdp(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = new View(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.bookchaplistitem, null);
			ListViewHolder holder = new ListViewHolder();
			holder.chapname = (TextView) view
					.findViewById(R.id.chaplistcontent);
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ListViewHolder holder = (ListViewHolder) view.getTag();
			holder.chapname.setText(cursor.getString(cursor
					.getColumnIndex("title")));
		}

		final class ListViewHolder {
			public TextView chapname;
		}

	}
}