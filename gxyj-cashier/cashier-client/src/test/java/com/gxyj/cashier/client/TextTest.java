/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.gxyj.cashier.client.execute.HttpClientUtil;






public class TextTest {
	@Test
	public void readTextFileLineTest() { 
		String str = readTextFileLine();
		System.out.println(str);
	}
	private static String readTextFileLine() {
		String filePath = "C:\\Users\\chu\\Desktop\\json_n.txt";
		StringBuffer buff = new StringBuffer();
		HttpClientUtil httpClient = new HttpClientUtil();
		//String str = "http://10.1.102.166:8989/qstods/qstods";
		//http://10.1.102.39:8080/yqfInterfacePlatform/api/dispatcher
		//localhost
		String str = "http://10.1.102.167:8989/yqfInterfacePlatform/api/dispatcher";
		Map <String, String> map = new HashMap<String, String>();
		int n = 0;
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { //判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding); //考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				String strMsg = "";
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt.trim());
					strMsg += lineTxt.trim();
					n++;
				}
				System.out.println("strMsg：" + strMsg.trim());
				map.put("jsonValue", strMsg.trim());
				String rtn = httpClient.doPost(str, map);
				System.out.println("rtn：" +  rtn);
				read.close();
			}
			else {
				System.out.println("找不到指定的文件");
			}
		}
		catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return n + "";
	}
}
