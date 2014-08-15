package com.lswe.reader;

import java.util.ArrayList;
import java.util.List;

import com.lswe.db.LocalTable;

public class SettingInfo {

	public String ftv;
	public String stv;
	/*
	 * 1���̶� ����0�����
	 */
	public int bjw;

	public SettingInfo(String str, int bjw) {
		ftv = str;
		this.bjw = bjw;
	}

	public static List<SettingInfo> getSettingInfo() {
		List<SettingInfo> SettingInfo = new ArrayList<SettingInfo>();
		SettingInfo.add(new SettingInfo("��ǰ�û�", 1));
		SettingInfo.add(new SettingInfo("�޸�����", 2));
		String[] localuesr = LocalTable.getLocUser();
		SettingInfo.add(new SettingInfo(localuesr[0], 0));
		SettingInfo.add(new SettingInfo("�Ķ�����", 1));
		SettingInfo.add(new SettingInfo("����Ӵ�", 0));
		SettingInfo.add(new SettingInfo("�����С", 0));
		SettingInfo.add(new SettingInfo("��Ļ����", 0));
		SettingInfo.add(new SettingInfo("��Ļ�䰵", 0));
		SettingInfo.add(new SettingInfo("����", 1));
		SettingInfo.add(new SettingInfo("�������Ķ��� �汾 1.0.0", 0));
		return SettingInfo;
	}

}