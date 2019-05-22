package com.hskj.meettingsys.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络访问的工具类
 */
public class HttpUtils {

	public static byte[] doGet(String spec) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(spec);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				// 字节数组输出流，内部封装了一个字节数据
				// 只适用于小文件的下载
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len= is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				return out.toByteArray();
			} else {
				throw new RuntimeException("网络访问失败：" + code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放资源
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return null;
	}

}
