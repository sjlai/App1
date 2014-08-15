package com.lswe.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Environment;
import android.util.Log;

import com.lswe.reader.ConstDefine;

public class HttpSer {

	private JsonUtils ju = new JsonUtils();

	// �������ȡ��InputStringתΪString(Json)
	public String ConvenStream2String(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	// ��ȡ�������İ汾����ַ��ϢJson
	public String[] getVersion() {
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/checkVersion";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConJson2Version(returnConnection);
	}
	//���ظ��µ����������½�������һ��booleanֵ
	public String[] getUpdatebook(){
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/checkBookupdate";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConJson2UpdateBook(returnConnection);
	}
	
	// ��¼����,�����û������뷵�ص�¼״̬
	

	public HashMap<String, Object> doPost_Login(String name, String psw) {
		String url = "http://yunedu.net/read/index.php/Mobile/Login/login";
		String returnConnection = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("userName", name));
		pairs.add(new BasicNameValuePair("userPwd", psw));
		//pairs.add(new BasicNameValuePair("UUID",uuid));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConvenJson2HashMap(returnConnection);
	}
	
	
	
	public HashMap<String,Object> doPost_ChangePsw(String oldpsw,String newpsw,String surepsw){
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/changePwd";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		pairs.add(new BasicNameValuePair("oldpsw", oldpsw));
		pairs.add(new BasicNameValuePair("newpsw", newpsw));
		pairs.add(new BasicNameValuePair("surepsw", surepsw));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConvenJson2HashMapforchange(returnConnection);
	}
	// ����SESSIONID�����������������б�
	public List<Integer> doPost_AuthorList() {
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/updateAuthor";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		System.out.println("SESSIONID" + ConstDefine.SESSIONID);
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConvenJson2AuthorList(returnConnection);
	}

	// ��������ID��ȡ����������ͼ����б�
	public List<Integer> doPost_AuthorBookList(int authorID) {
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/updateBook";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		pairs.add(new BasicNameValuePair("authorID", authorID + ""));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConJson2BookList(returnConnection);
	}

	// �����鼮ID��ȡ�½�Ŀ¼
	public List<Integer> doPost_AuthorBookChapterList(int BookID) {
		System.out.println(BookID + "");
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/updateChapter";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		pairs.add(new BasicNameValuePair("bookID", BookID + ""));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ju.ConJson2ChapList(returnConnection);
	}

	// �����½�ID��ȡ���½������½�
	public void doPost_getChapterContent(int ChapterID) {
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/updateContent";// ���޸ĵ�ַ
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		pairs.add(new BasicNameValuePair("chapterID", ChapterID + ""));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * ��POST���ݷ���HTTP����
			 */
			httpPost.setEntity(p_entity);
			/*
			 * ����ʵ�ʵ�HTTP POST����
			 */
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			returnConnection = ConvenStream2String(content);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ju.ConJson2ChapContent(returnConnection);
	}
}
