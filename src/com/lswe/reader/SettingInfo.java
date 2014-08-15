package com.lswe.reader;

import java.util.ArrayList;
import java.util.List;

import com.lswe.db.LocalTable;

public class SettingInfo {

	public String ftv;
	public String stv;
	/*
	 * 1→固定 →→0→添加
	 */
	public int bjw;

	public SettingInfo(String str, int bjw) {
		ftv = str;
		this.bjw = bjw;
	}

	public static List<SettingInfo> getSettingInfo() {
		List<SettingInfo> SettingInfo = new ArrayList<SettingInfo>();
		SettingInfo.add(new SettingInfo("当前用户", 1));
		SettingInfo.add(new SettingInfo("修改密码", 2));
		String[] localuesr = LocalTable.getLocUser();
		SettingInfo.add(new SettingInfo(localuesr[0], 0));
		SettingInfo.add(new SettingInfo("阅读设置", 1));
		SettingInfo.add(new SettingInfo("字体加大", 0));
		SettingInfo.add(new SettingInfo("字体减小", 0));
		SettingInfo.add(new SettingInfo("屏幕变亮", 0));
		SettingInfo.add(new SettingInfo("屏幕变暗", 0));
		SettingInfo.add(new SettingInfo("关于", 1));
		SettingInfo.add(new SettingInfo("电子书阅读器 版本 1.0.0", 0));
		return SettingInfo;
	}

}