package com.lswe.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lswe.db.LocalTable;

public class JsonUtils {
	// �ѵ�¼���ص�JSONת��Map����
	public HashMap<String, Object> ConvenJson2HashMap(String str) {
		int status = 403;
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			String sessionid = person.getString("SESSIONID");
			System.out.println("SESSIONID"+sessionid);
			status = person.getInt("status");
			
			String data = person.getString("data");
			hashmap.put("status", status);
			hashmap.put("SESSIONID", sessionid);
			hashmap.put("data", data);
		} catch (Exception ex) {
			hashmap.put("status", status);
			hashmap.put("SESSIONID", "");
			hashmap.put("data", "");
		}
		return hashmap;
	}
	// ���޸�����Ľ������
	public HashMap<String, Object> ConvenJson2HashMapforchange(String str) {
		int status = 403;
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			String result = person.getString("result");
			status = person.getInt("status");
			hashmap.put("status", status);
			hashmap.put("result", result);
		} catch (Exception ex) {
			hashmap.put("status", status);
			hashmap.put("result", "");
		}
		return hashmap;
	}

	// ���������б��������ݿ�
	public List<Integer> ConvenJson2AuthorList(String str) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			JSONArray data = person.getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
				JSONObject jobj = data.getJSONObject(i);
				String authorname = jobj.getString("authorName");
				String authorid = jobj.getString("ID");
				list.add(Integer.valueOf(authorid));
				LocalTable.InsertAuthor(new String[] { authorname, authorid });
			}
		} catch (Exception e) {
			// ������߱�
			System.out.println("�����б�Jsonת����������");
		}
		return list;
	}
	// Json��һλ���ߵ��鼮�б�
	public List<Integer> ConJson2BookList(String str) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			String temp = person.getString("data");
			JSONArray data = new JSONArray(temp);
			for (int i = 0; i < data.length(); i++) {
				JSONObject jobj = data.getJSONObject(i);
				String ID = jobj.getString("ID");
				String title = jobj.getString("title");
				String AuthorID = jobj.getString("authorID");
				String photoUrl = jobj.getString("photoUrl");
				String status = jobj.getString("status");
				list.add(Integer.valueOf(ID));
				LocalTable.InsertBooks(new String[] { ID, title, AuthorID,
						photoUrl, status });
			}
		} catch (Exception ex) {
			System.out.println("�鼮�б�Jsonת����������");
		}
		return list;
	}

	// Json��Ϊһ������½��б�
	public List<Integer> ConJson2ChapList(String str) {
		List<Integer> list = new ArrayList<Integer>();

		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			String temp = person.getString("data");
			JSONArray data = new JSONArray(temp);
			for (int i = 0; i < data.length(); i++) {
				JSONObject jobj = data.getJSONObject(i);
				String ID = jobj.getString("ID");
				String bookID = jobj.getString("bookID");
				String title = jobj.getString("title");
				String updatetime = jobj.getString("updateTime");
				list.add(Integer.valueOf(ID));
				LocalTable.InsertChapList(new String[] { ID, bookID, title,
						updatetime });
			}
		} catch (Exception e) {
			System.out.println("�¹��б�Jsonת����������");
		}
		return list;
	}

	// Jsonת��һ�µ����ݣ�
	public void ConJson2ChapContent(String str) {
		try {
			StringBuffer sb = new StringBuffer();
			String chapterID = null;
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			String temp = person.getString("data");
			JSONArray data = new JSONArray(temp);
			for (int i = 0; i < data.length(); i++) {
				JSONObject jobj = data.getJSONObject(i);
				String content = jobj.getString("content");
				content = content.replace("&gt;", "").replace("&lt;", "")
						.replace("&amp;", "").replace("&lt;", "")
						.replace("span", "").replace("/span", "")
						.replace("/p&gt", "").replace("nbsp;", "")
						.replace("Msomal", "").replace("/quot;", "")
						.replace("br /", "").replace("/p", "")
						.replace("/", "  ")
						.replace("p class=&quot;MsoNormal&quot;", "");
				// String ID = jobj.getString("ID");
				// String title = jobj.getString("title");
				// String updatetime = jobj.getString("updateTime");
				chapterID = jobj.getString("chapterID");
				sb.append(content);
			}
			LocalTable
					.IsertChapContent(new String[] { chapterID, sb.toString() });
		} catch (Exception e) {
			System.out.println("�¹�����Jsonת����������");
		}
	}

	// Jsonת�ɰ汾��Ϣ�͵�ַ
	public String[] ConJson2Version(String str) {
		String[] st = new String[2];
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			st[0] = person.getString("version");
			st[1] = person.getString("address");
			System.out.println("st:" + st[0]);
			System.out.println("st:" + st[1]);

		} catch (Exception e) {

		}
		return st;
	}
	public String[] ConJson2UpdateBook(String str) {
		String[] st = new String[5];
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject person = (JSONObject) jsonParser.nextValue();
			st[0] = person.getString("result");
			st[1] = person.getString("all");
			st[2] = person.getString("AuthorID");
			st[3] = person.getString("BookID");
			st[4] = person.getString("chapID");
			System.out.println("st:" + st[0]);
			System.out.println("st:" + st[1]);

		} catch (Exception e) {

		}
		return st;
	}
}