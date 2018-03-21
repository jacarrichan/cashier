/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gxyj.cashier.common.utils.Charset;
import com.gxyj.cashier.exception.NotSupportDataMsgException;
import com.gxyj.cashier.utils.CashierErrorCode;

/**
 * 负责处理发送HTTP消息请求
 * 
 * @author Danny
 */
@Component
public final class HttpRequestClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestClient.class);

	private static final String PROTOCOL_HTTPS = "https";

	private static final String PROTOCOL_HTTP = "http";

	private static final String HEADER_USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";

	private static final String KEY_USER_AGENT = "User-Agent";

	private static final String KEY_CONTENT_TYPE = "Content-type";

	private static final String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

	private PoolingHttpClientConnectionManager httpClientConnectionManager = null;

	private String keyStore;

	private String password;


	// 请求重试机制
	HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 1) {
				// 超过三次则不再重试请求
				return false;
			}
			if (exception instanceof InterruptedIOException) {
				// Timeout
				return false;
			}
			if (exception instanceof UnknownHostException) {
				// Unknown host
				return false;
			}
			if (exception instanceof ConnectTimeoutException) {
				// Connection refused
				return false;
			}
			if (exception instanceof SSLException) {
				// SSL handshake exception
				return false;
			}
			HttpClientContext clientContext = HttpClientContext.adapt(context);
			HttpRequest request = clientContext.getRequest();
			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
			if (idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};

	public HttpRequestClient() {
	}

	public HttpRequestClient(String keyStorePath, String passwd) {
		this.keyStore = keyStorePath;
		this.password = passwd;
	}

	@PostConstruct
	private void init() {
		int maxTotal = 200;
		LOGGER.debug("max pool size =" + maxTotal);
		httpClientConnectionManager = new PoolingHttpClientConnectionManager();
		httpClientConnectionManager.setMaxTotal(maxTotal);
		httpClientConnectionManager.setDefaultMaxPerRoute(3);
	}

	public CloseableHttpClient getHttpClient() {
		// 创建全局的requestConfig
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000)
				.setSocketTimeout(60000).build();
		// 声明重定向策略对象
		LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager)
				.setDefaultRequestConfig(requestConfig).setRedirectStrategy(redirectStrategy).setRetryHandler(myRetryHandler)
				.build();

		return httpClient;
	}

	public String doPost(String url, String xml) {
		return doPost(url, xml, Charset.UTF8.value());
	}

	public String doPost(String url, Map<String, String> map) {
		return doPost(url, map, Charset.UTF8.value());
	}

	public String doPost(String url, Map<String, String> map, String charset) {
		String result = null;
		HttpEntity resEntity = getEntityByPost(url, map, charset);
		if (resEntity != null) {
			try {
				result = EntityUtils.toString(resEntity, charset);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return result;
	}

	public HttpEntity getEntityByPost(String url, Map<String, String> map, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;

		try {
			httpClient = getHttpClient();
			// HttpClients.createDefault();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			
			CloseableHttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				//response.close();
				return resEntity;
			}
			
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public String doPost(String url, String xml, String charset) {
		String result = null;
		try {

			HttpEntity resEntity = getEntityByPost(url, xml, charset);
			if (resEntity != null) {
				result = EntityUtils.toString(resEntity, charset);
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	public HttpEntity getEntityByPost(String url, String xml, String charset) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;

		try {
			// httpClient = HttpClients.createDefault();
			httpClient = getHttpClient();
			httpPost = new HttpPost(url);

			StringEntity se = new StringEntity(xml, charset);

			httpPost.setEntity(se);
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(httpPost);
				if (response != null) {
					HttpEntity resEntity = response.getEntity();
					return resEntity;
				}

			}
			finally {
				if (response != null) {
					response.close();
				}
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	/**
	 * 发送Https服务请求
	 * @param url 对方服务器URL
	 * @param object 预发送的数据 只允许Map集合数据和字符串，其它类型将异常
	 * @param charset 字符集
	 * @return 应答消息
	 */
	@SuppressWarnings("unchecked")
	public String httpsRequest(String url, Object object, String charset) {

		CloseableHttpResponse response = null;
		String responseMsg = null;

		try {
			// 创建post方式请求对象
			HttpPost httpPost = new HttpPost(url);

			// 创建https的HttpClient对象
			CloseableHttpClient httpclient = getHttpsClient(httpPost);
			if (object instanceof String) {

				StringEntity strEntity = new StringEntity(object.toString(), charset);
				httpPost.setEntity(strEntity);
			}
			else if (object instanceof Map) {
				Map<String, String> paramMap = (Map<String, String>) object;
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				Iterator<Entry<String, String>> iterator = paramMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> elem = (Entry<String, String>) iterator.next();
					nvps.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
				}
				// 设置参数到请求对象中
				if (nvps.size() > 0) {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, charset);
					httpPost.setEntity(entity);
				}
				LOGGER.debug("==========请求地址：" + url);
				LOGGER.debug("==========请求参数：" + nvps.toString());
			}
			else {
				throw new NotSupportDataMsgException(CashierErrorCode.REQUEST_DATA_FORMAT_INVALID,"发送的数据类型不支持，只支持string和Map<String,String>");
			}

			// 执行请求操作，并拿到结果（同步阻塞）
			response = httpclient.execute(httpPost);
			// 获取结果实体
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				// 按指定编码转换结果实体为String类型
				responseMsg = EntityUtils.toString(resEntity, charset);
				LOGGER.info("-------------https 发送-------------------------");
				LOGGER.info("收到应签消息内容: " + responseMsg);
				LOGGER.info("---------------------------------------------");

			}

			EntityUtils.consume(resEntity);

		}
		catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				ex.printStackTrace();
			}
			LOGGER.error("========发送消息数据异常======" + ex.getMessage(), ex);

		}
		finally {
			if (response != null) {
				try {
					// 释放链接
					response.close();
				}
				catch (IOException e) {
					if (LOGGER.isDebugEnabled()) {
						e.printStackTrace();
					}
					LOGGER.error("========发送消息处理结束，链接释放异常======" + e.getMessage(), e);
				}
			}
		}
		return responseMsg;
	}

	/**
	 * 根据HttpPost实例创建对应的CloseableHttpClient实例
	 * 
	 * @param httpPost HttpPost实例
	 * @return CloseableHttpClient instance
	 * @throws MalformedURLException Http格式异常
	 * @throws IOException IO异常
	 */
	private CloseableHttpClient getHttpsClient(HttpPost httpPost) throws MalformedURLException, IOException {

		String keyStore = this.keyStore;
		String password = this.password;
		SSLContext sslContext = custom(keyStore, password);

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader(KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
		httpPost.setHeader(KEY_USER_AGENT, HEADER_USER_AGENT_VALUE);

		PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(new URL(httpPost.getURI().toString()));

		HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
		LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

		// 创建全局的requestConfig
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(6000)
				.setSocketTimeout(6000).build();

		// 声明重定向策略对象
		LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();

		registryBuilder.register(PROTOCOL_HTTP, plainSF);
		registryBuilder.register(PROTOCOL_HTTPS, sslSF);
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);

		return HttpClientBuilder.create().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig)
				.setRedirectStrategy(redirectStrategy).setRetryHandler(myRetryHandler).build();
	}

	/**
	 * 设置信任自签名证书
	 * 
	 * @param keyStorePath 密钥库路径
	 * @param keyStorepass 密钥库密码
	 * @return 签名证书对象
	 */
	private SSLContext custom(String keyStorePath, String keyStorepass) {
		SSLContext sc = null;
		FileInputStream instream = null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File(keyStorePath));
			trustStore.load(instream, keyStorepass.toCharArray());
			// 相信自己的CA和所有自签名的证书
			sc = SSLContextBuilder.create().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();

		}
		catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException e) {

			if (LOGGER.isDebugEnabled()) {
				e.printStackTrace();
			}

			LOGGER.error("========设置信任自签名证书异常======" + e.getMessage(), e);

		}
		finally {
			try {
				if (instream != null) {
					instream.close();
				}
			}
			catch (IOException e) {
			}
		}

		return sc;
	}
	
}
