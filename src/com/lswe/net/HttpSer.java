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

	// 将网络获取的InputString转为String(Json)
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

	// 获取服务器的版本，地址信息Json
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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
	//返回更新的作者名，章节名，和一个boolean值
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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
	
	// 登录方法,接收用户名密码返回登录状态
	

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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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
	// 发送SESSIONID给服务器返回作者列表
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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

	// 根据作者ID获取作者下所有图书的列表
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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

	// 根据书籍ID获取章节目录
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
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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

	// 根据章节ID获取该章节内容章节
	public void doPost_getChapterContent(int ChapterID) {
		String returnConnection = null;
		String url = "http://yunedu.net/read/index.php/Mobile/Index/updateContent";// 需修改地址
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("SESSIONID", ConstDefine.SESSIONID));
		pairs.add(new BasicNameValuePair("chapterID", ChapterID + ""));
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs,
					"utf-8");
			/*
			 * 将POST数据放入HTTP请求
			 */
			httpPost.setEntity(p_entity);
			/*
			 * 发出实际的HTTP POST请求
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
