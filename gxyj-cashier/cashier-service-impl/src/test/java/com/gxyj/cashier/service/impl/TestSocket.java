/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

/**
 * 
 * API功能测试类
 * @author Danny
 */
public class TestSocket {

	@Test
	public void test() {
		Socket clieSocket;
		try {
			clieSocket = new Socket("123.126.102.188", 8888);
			InputStream inStream = clieSocket.getInputStream();
			OutputStream outputStream = clieSocket.getOutputStream();
//			byte[] b = new byte[1024];
			System.out.println("ssssssss");
			System.out.println(clieSocket.isConnected());
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			StringBuilder sb = new StringBuilder();
//			java.io.File file = new java.io.File("D:\\MyXml2.xml");
			java.io.File file = new java.io.File("D:\\MyXml.xml");
			InputStreamReader reader  = new InputStreamReader(new FileInputStream(file));
			BufferedReader r = new BufferedReader(reader);
			String line;
			while ((line = r.readLine())!=null) {
				sb.append(line.trim());
			}
			System.out.println(sb.toString());
			writer.write(sb.toString());
			writer.flush();
//			Thread.currentThread().sleep(30000);
			String ss;
			BufferedReader re  = new BufferedReader(new InputStreamReader(inStream,"gbk"));
			while ((ss=re.readLine())!=null) {
				System.out.println(ss);
			}
			writer.close();
			re.close();
			clieSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	@Test
	public void readRemoteFile() {
		URL url;
		HttpURLConnection connection = null;
		try {
			url = new URL("http://123.126.102.188:8889/SHOP.105100000005347.20170717.02.success.txt.gz");
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			 //防止屏蔽程序抓取而返回403错误
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream inputStream = connection.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"gbk"));
			//文件保存位置
			File file = new File("C:/Users/edianzu/Desktop/建行交易对账文件/SHOP.105100000005347.20170717.02.success.txt.gz");
			file.createNewFile();
			FileOutputStream fop = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fop,"gbk"));
			byte[] b = new byte[2048];
			String line;
			while (inputStream.read(b)!=-1) {
				fop.write(b);
			}
			fop.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
	}
	@Test
	public void testMerge(){
		File file = new File("C:/Users/edianzu/Desktop/a");
		File outfile = new File("C:/Users/edianzu/Desktop/a.txt");
		FileWriter writer;
		try {
			writer = new FileWriter("C:/Users/edianzu/Desktop/a.txt", true);
		FileChannel channel=null;
		channel = new FileOutputStream(outfile).getChannel();
		FileOutputStream outputStream = new FileOutputStream(outfile);
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
		Charset charset=Charset.forName("utf-8");   
		  
        CharsetDecoder chdecoder=charset.newDecoder();   

        CharsetEncoder chencoder=charset.newEncoder(); 
        
        CharBuffer charBuffer;
        ByteBuffer nbuBuffer;
        ByteBuffer nBuffer;
			charBuffer = chdecoder.decode(buffer);
			nbuBuffer=chencoder.encode(charBuffer);
			nBuffer=chencoder.encode(charBuffer);
			String[] files = file.list();
			String lineSeparator = System.getProperty("line.separator", "\n\r");  
			System.out.println(lineSeparator.getBytes());
			for (int i = 0; i < files.length; i++) {
				
				writer.write(lineSeparator);
				writer.flush();
				
				
				String filepath = file.getPath();
				System.out.println(filepath);
				System.out.println(files[i]);
				FileChannel inChannel= new FileInputStream(new File(filepath+File.separator+files[i])).getChannel();
				while (inChannel.read(nbuBuffer)!=-1) {
					buffer.flip();
					nbuBuffer.flip();
					channel.write(nbuBuffer);
					System.out.println("........");
					buffer.clear();
					nbuBuffer.clear();
				}
				
				inChannel.close();
			}
			writer.close();
			    channel.close();
			} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	@Test
	public void testMerge2(){
		File file = new File("C:/Users/edianzu/Desktop/a");
		File outfile = new File("C:/Users/edianzu/Desktop/a.txt");
		FileInputStream inputStream;
		String[] filelist = file.list();
		String line;
		for (int i = 0; i < filelist.length; i++) {
			try {
				inputStream = new FileInputStream(file.getPath()+File.separator+filelist[i]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile,true), "UTF-8"));
				while ((line = reader.readLine())!=null) {
					writer2.write(line+", 1");
					writer2.newLine();
					writer2.flush();
				}
				writer2.close();
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	@Test
	public void getLocalDatas() {
		File file = new File("C:/Users/edianzu/Desktop/建行交易对账文件");
		file.setWritable(true);
		BufferedReader reader = null;
		String[] fileNames = file.list();
		String lineStr;
		try {
			if (fileNames.length!=0) {
				for (String fileName:fileNames) {
					
					reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("C:/Users/edianzu/Desktop/建行交易对账文件"+File.separator+fileName)),"gbk"));
					List<String> list = new ArrayList<String>();
					
					while ((lineStr = reader.readLine())!=null) {
						list.add(lineStr);
					}
					if (list.size()>4) {
						for (int i = 4; i < list.size(); i++) {
							String[] strings = list.get(i).split("	");
								System.out.println(strings.length);
								System.out.println(strings);
						}
					}
				}
			}
       	 		reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    }
	@Test
	public void testFile(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("C:/Users/edianzu/Desktop/CCBAccountFile/CCBPayAccountCheckData20170720/SHOP.105100000005347.20170719.02.success.txt.gz")),"GBK"));
			while (reader.readLine() != null) {
				System.out.println(reader.readLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void streamtest(){
		/*try {
			File file = new File("C:/Users/edianzu/Desktop/a/asdw.txt");
			FileOutputStream foStream = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(foStream));
			writer.write("hahhah哈哈是的我打算打");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			Integer.parseInt("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String s = "20170609";
		SimpleDateFormat siFormat = new SimpleDateFormat("yyyymmdd");
		SimpleDateFormat siFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = siFormat.parse(s);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			date = calendar.getTime();
			s=siFormat2.format(date);
			System.out.println(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void filetest(){
		File file1 = new File("C:/Users/edianzu/Desktop/c/cc");
		file1.mkdir();
		System.out.println(file1.getPath());
	}
}
