/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package ccb;

import java.io.IOException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * 建行测试用例.
 * @author ECOOP1
 */
public class CcbInterfaceTest {
	
	/**
	 * 建行个人支付结果通知接口.
	 * @throws IOException 异常
	 * @throws ClientProtocolException   异常
	 */
	@Test
	public void ccbiNotifyResult() throws ClientProtocolException, IOException{
		 String result="";
		 String urlNameString = "http://127.0.0.1:8090/gxyj/iPayNotify?"
			 		+ "POSID=002043583&BRANCHID=110000000&ORDERID=CSRO0010CSR2017zhp454000504&PAYMENT=0.01&CURCODE=01&REMARK1=&"
			 		+ "REMARK2=&ACC_TYPE=12&SUCCESS=Y&TYPE=1&REFERER=&"
			 		+ "CLIENTIP=";
		 // 根据地址获取请求
        HttpGet request = new HttpGet(urlNameString);//这里发送get请求
        // 获取当前客户端对象
        HttpClient httpClient = new DefaultHttpClient();
        // 通过请求对象获取响应对象
        HttpResponse response = httpClient.execute(request);
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result= EntityUtils.toString(response.getEntity(),"utf-8");
        } 
		
	}
	
	
	
	/**
	 * 建行企业支付结果通知接口.
	 * @throws IOException 异常
	 * @throws ClientProtocolException   异常
	 */
	@Test
	public void ccbeNotifyResult() throws ClientProtocolException, IOException{
		 String result="";
		 String urlNameString = "http://127.0.0.1:8090/gxyj/ePayNotify?"
			 		+ "POSID=002043589&ORDER_NUMBER=CSRO0010CSR2018zhp454000504&CUST_ID=105100000005347&ACC_NO=150723333983&ACC_NAME=供销E家&AMOUNT=0.01&STATUS=2&REMARK1=&"
			 		+ "REMARK2=&TRAN_FLAG=N&TRAN_TIME=&BRANCH_NAME=1&SIGNSTRING=&"
			 		+ "CHECKOK=Y";
        HttpGet request = new HttpGet(urlNameString);//这里发送get请求
        // 获取当前客户端对象
        HttpClient httpClient = new DefaultHttpClient();
        // 通过请求对象获取响应对象
        HttpResponse response = httpClient.execute(request);
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result= EntityUtils.toString(response.getEntity(),"utf-8");
        } 
		
	}
	
	public CcbInterfaceTest() {
	}

}
