/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yinsin.utils.FileUtils;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);
    
   /**
    * 读取zip文件内容.
    * @param path path
    */
    public static void readZip(String path) {
         
        /**
         * 需要读取zip文件项的内容时，需要ZipFile类的对象的getInputStream方法取得该项的内容，
         * 然后传递给InputStreamReader的构造方法创建InputStreamReader对象，
         * 最后使用此InputStreamReader对象创建BufferedReader实例
         * 至此已把zip文件项的内容读出到缓存中，可以遍历其内容
         */
        ZipFile zipfile = null;
        try {
        	zipfile = new ZipFile(path, Charset.GBK.value());
        } 
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
        	Enumeration entries = zipfile.getEntries();
        	while(entries.hasMoreElements()){
        		ZipEntry zipEn =  (ZipEntry)entries.nextElement();
                if (!zipEn.isDirectory()) { // 判断此zip项是否为目录
                    System.out.println("file - " + zipEn.getName() + " : "  
                            + zipEn.getSize() + " bytes");  
                    /**
                     * 把是文件的zip项读出缓存，
                     * zfil.getInputStream(zipEn)：返回输入流读取指定zip文件条目的内容 zfil：new
                     * ZipFile();供阅读的zip文件 zipEn：zip文件中的某一项
                     */
                   
                    if(zipEn.getSize() > 0) {
                    	InputStream ins = zipfile.getInputStream(zipEn);
                    	System.out.println(ins);
                    	/*BufferedReader buff = new BufferedReader(new InputStreamReader(ins, Charset.GBK.value()));
                    	String str;
                    	while ((str = buff.readLine()) != null) {
                    		System.out.println("\t" + str);
                    	}
                    	buff.close();*/
                    }
                }
            }
        } 
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally {
            try {
                if(zipfile!=null){
                	zipfile.close();
                }
            } 
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static void downloadFile(String path, HttpServletRequest request, HttpServletResponse response) {
    	  
    	File file = new File(path);// path是根据日志路径和文件名拼接出来的
  		if (file.exists()) {
  			String filename = file.getName();// 获取日志文件名称
  			InputStream fis;
  			try {
  				fis = new BufferedInputStream(new FileInputStream(file));
  				byte[] buffer = new byte[fis.available()];
  				fis.read(buffer);
  				fis.close();
  				response.reset();
  				// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
  				String agent = request.getHeader("User-Agent").toLowerCase();
  				String encode = "utf-8";
  				// IE浏览器必须设置为gbk编码，不然会出现乱码
  				if(agent.indexOf("trident") != -1 || agent.indexOf("msie") != -1){
  				    encode = "gbk";
  				}else if(agent.indexOf("edge") != -1){//wind10自带edge浏览器
  					encode = "gbk";
  				}
  				response.addHeader("Content-Disposition", "attachment;filename="
  						+ new String(filename.replaceAll(" ", "").getBytes(encode), "iso8859-1"));
  				response.addHeader("Content-Length", "" + file.length());
  				OutputStream os = new BufferedOutputStream(response.getOutputStream());
  				response.setContentType("application/octet-stream");
  				os.write(buffer);// 输出文件
  				os.flush();
  				os.close();
  				
  				// 启动删除文件线程
  				FileUtils.deleteFileThread(path);
  			} catch (Exception e) {
  				logger.error("下载文件时异常：" + e.getMessage(), e);
  			}
    	  }else{
    	    logger.error("查阅的文件不存在。");
    	  }
     }
    
   /* public static void main(String[] a) {
    	readZip("E:\\统一收银台\\支付渠道对账文件样例\\FTP_FILE\\alipay\\AliPayAccountCheckData20170718.zip");
    }*/
}
