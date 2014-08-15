package com.lswe.reader;

import java.util.ArrayList;
import java.util.List;

public class GridViewItem {
	public String word;
	public int img;

	GridViewItem(String word, int img) {
		this.word = word;
		this.img = img;
	}

	public static List<GridViewItem> GetItem() {
		List<GridViewItem> list = new ArrayList<GridViewItem>();
		list.add(new GridViewItem("������", R.drawable.bianda));
		list.add(new GridViewItem("�����С", R.drawable.bianxiao));
		list.add(new GridViewItem("��Ļ����", R.drawable.bianliang));
		list.add(new GridViewItem("��Ļ�䰵", R.drawable.bianliang));
		return list;
	}
}