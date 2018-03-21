/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.msg;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Socket 客户端请求发送类.
 *   主要用于建行消息交互
 * 
 * @author Danny
 */
public class SocketClient {

	private static final String USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";

	private static final String KEY_USER_AGENT = "User-Agent";

	private static final String PATH_SEPARATOR = "/";

	private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

	private static int recvBufferLen = 120000;
	private static int sendBufferLen = 120000;

	private static int coreTimeout = 30000;

	public SocketClient() {
	}

	/**
	 * 主动发送报文服务器并接收应答.
	 * @param requestUrl 请求的URL
	 * @param requestMsg 报文内容
	 * @param encode 字符编码
	 * @return String 应答消息
	 */
	public String sendRequest(String requestUrl, String requestMsg, String encode) {

		Socket connectSocket = null;
		try {
			connectSocket = new Socket();
			connectSocket.setReuseAddress(true);
			connectSocket.setKeepAlive(true);
			connectSocket.setSoTimeout(coreTimeout);// 超时设置，单位：毫秒
			connectSocket.setReceiveBufferSize(recvBufferLen); // 接收缓冲区大小
			connectSocket.setSendBufferSize(sendBufferLen); // 发送缓冲区大小

			connect(requestUrl, connectSocket);

			BufferedReader br = new BufferedReader(new InputStreamReader(connectSocket.getInputStream(), encode));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connectSocket.getOutputStream(), encode));

			// char[] sendMsg = new char[requestMsg.getBytes(encode).length];
			// requestMsg.getChars(0, requestMsg.length(), sendMsg, 0);
			// bw.write(sendMsg); // 实时写入
			bw.write(requestMsg);
			bw.flush(); // 发送完成
			connectSocket.shutdownOutput();//关闭输出流

			logger.info("thread id = " + Thread.currentThread().getId() + ",发送到服务器的报文内容是[" + requestMsg + "]");

			StringBuilder responseMsg = new StringBuilder();
			int c = 0;
			while ((c = br.read()) != -1) { // 等待接收行内前置机响应
				responseMsg.append((char) c);
				// if (data.length() >= 1) {
				// break;
				// }
			}

			logger.info("thread id=" + Thread.currentThread().getId() + ",接收到服务器同步响应报文内容是[" + responseMsg + "]");
			bw.close();
			br.close();

			return responseMsg.toString();

		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			try {
				if (connectSocket != null && connectSocket.isConnected()) {
					logger.info("thread id=" + Thread.currentThread().getId() + ",关闭socket");
					connectSocket.close();
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return "";
	}

	/**
	 * 通过Http Socket下载远程文件
	 * @param requestUrl 目标服务的请求URL
	 * @param fileName 远程文件名称
	 * @param localPath 本地存放文件目录
	 * @param remotePath 远程服务器文件所在路径
	 * @param encode 字符编码
	 * @return 下载保存后的文件名称
	 * @throws IOException IO异常
	 *
	 */
	public String downloadRemoteFile(String requestUrl, String fileName, String remotePath, String localPath, String encode)
			throws IOException {
		URL url;
		HttpURLConnection connection = null;
		try {
			String fullUrlPath = requestUrl + PATH_SEPARATOR + remotePath;
			if (fullUrlPath.lastIndexOf(PATH_SEPARATOR) != fullUrlPath.length() - 1) {
				fullUrlPath += PATH_SEPARATOR + fileName;
			}
			else {
				fullUrlPath += fileName;
			}

			logger.info("远程文件名称：" + fullUrlPath);
			url = new URL(fullUrlPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(coreTimeout);
			// 防止屏蔽程序抓取而返回403错误
			connection.setRequestProperty(KEY_USER_AGENT, USER_AGENT_VALUE);
			InputStream inputStream = connection.getInputStream();

			// 文件保存位置
			String filePath = localPath + File.separatorChar;
			File path = new File(filePath);
			if (!path.exists()) {
				logger.debug("目录【" + filePath + "】不存在，创建目录");
				path.mkdirs();
			}
			filePath = filePath + fileName;
			logger.info("本地文件名称：" + filePath);
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fop = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fop, encode));
			byte[] bytes = new byte[1024];

			while (inputStream.read(bytes) != -1) {
				fop.write(bytes);
				bytes = null;
				bytes = new byte[1024];
			}
			fop.flush();
			fop.close();
			writer.close();

			return filePath;

		}
		catch (IOException ioe) {
			throw ioe;
		}
		finally {
			connection.disconnect();
		}
	}

	public String sendGet(String requestUrl, String requestStr, String encode) throws IOException {

		Socket socket = new Socket();

		String host = connect(requestUrl, socket);

		OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());
		BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);

		bufferedWriter.write("GET " + requestUrl + " HTTP/1.1\r\n");
		bufferedWriter.write("Host: " + host + "\r\n");
		bufferedWriter.write("\r\n");
		bufferedWriter.flush();

		BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, encode));
		String line = null;
		StringBuilder builder = new StringBuilder("");
		while ((line = bufferedReader.readLine()) != null) {
			builder.append(line);
		}
		bufferedReader.close();
		bufferedWriter.close();
		socket.close();

		return builder.toString();
	}

	public String sendPost(String requestUrl, String requestMsg, Map<String, String> map, String encode) throws IOException {

		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		StringBuilder requestData = new StringBuilder();
		int index = 0;
		while (iterator.hasNext()) {
			Entry<String, String> elem = (Entry<String, String>) iterator.next();
			if (index > 0) {
				requestData.append("&");
			}
			requestData.append(URLEncoder.encode(elem.getKey(), encode)).append("=")
					.append(URLEncoder.encode(elem.getValue(), encode));
			index++;
		}

		String data = requestData.toString();
		Socket socket = new Socket();

		String host = connect(requestUrl, socket);

		OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream(), encode);
		BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);

		bufferedWriter.write("POST " + data + " HTTP/1.1\r\n");
		bufferedWriter.write("Host: " + host + "\r\n");
		bufferedWriter.write("Content-Length: " + data.length() + "\r\n");
		bufferedWriter.write("Content-Type: application/x-www-form-urlencoded\r\n");
		bufferedWriter.write("\r\n");
		bufferedWriter.write(data);
		bufferedWriter.flush();
		bufferedWriter.write("\r\n");
		bufferedWriter.flush();

		BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, encode));
		String line = null;
		StringBuilder builder = new StringBuilder("");
		while ((line = bufferedReader.readLine()) != null) {
			builder.append(line);
		}
		bufferedReader.close();
		bufferedWriter.close();
		socket.close();

		return builder.toString();
	}

	private String connect(String requestUrl, Socket socket) throws MalformedURLException, IOException {
		URL url = new URL(requestUrl);
		String host = url.getHost();
		int port = url.getPort();
		SocketAddress dest = new InetSocketAddress(host, port);
		socket.connect(dest);
		return host;
	}

}
