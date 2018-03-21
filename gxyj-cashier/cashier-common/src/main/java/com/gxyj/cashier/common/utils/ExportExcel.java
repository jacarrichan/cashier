/*
 * Copyright (c) 2015-2016 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.yinsin.utils.CommonUtils;

/**
 * excel导出工具类
 * @param <T> 类
 * @author chensj
 */
public class ExportExcel<T> {
	 
	   private HSSFWorkbook workbook; // 声明一个工作薄
	   private HSSFCellStyle styleTitle; //标题样式
	   private HSSFCellStyle styleBody;  //数据样式
	   private HSSFFont fontTitle; //标题字体
	   private HSSFFont fontBody; // 数据字体
	   
	   /**
	    * 
	    */
	   public ExportExcel() {
		   // 声明一个工作薄
		   workbook = new HSSFWorkbook();
		   styleTitle = workbook.createCellStyle();
		   styleBody = workbook.createCellStyle();
		   fontTitle = workbook.createFont();
		   fontBody = workbook.createFont();
		   
		   initTitleStyle();
		   
		   initBodyStyle();
	   }
	  
	  
	   
	   private void initTitleStyle() {
		  styleTitle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		  styleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  styleTitle.setBorderBottom(BorderStyle.THIN);
		  styleTitle.setBorderLeft(BorderStyle.THIN);
		  styleTitle.setBorderRight(BorderStyle.THIN);
		  styleTitle.setBorderTop(BorderStyle.THIN);
		  styleTitle.setAlignment(HorizontalAlignment.CENTER);
		  styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
	      
	      fontTitle.setColor(HSSFColor.VIOLET.index);
	      fontTitle.setFontHeightInPoints((short) 12);
	      fontTitle.setBold(true);
	      // 把字体应用到当前的样式
	      styleTitle.setFont(fontTitle);
	   }
	   
	   private void initBodyStyle() {
		  styleBody.setFillForegroundColor(HSSFColor.WHITE.index);
		  styleBody.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  styleBody.setBorderBottom(BorderStyle.THIN);
		  styleBody.setBorderLeft(BorderStyle.THIN);
		  styleBody.setBorderRight(BorderStyle.THIN);
		  styleBody.setBorderTop(BorderStyle.THIN);
		  styleBody.setAlignment(HorizontalAlignment.CENTER);
		  styleBody.setVerticalAlignment(VerticalAlignment.CENTER);
	      
	      fontBody.setColor(HSSFColor.BLACK.index);
	      fontBody.setFontHeightInPoints((short) 11);
	      //fontBody.setBold(true);
	      // 把字体应用到当前的样式
	      styleBody.setFont(fontBody);
	   }
	   
	   /**
	    * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	    *
	    * @param title
	    *            表格标题名
	    * @param headers
	    *            表格属性列名数组
	    * @param columnNames
	    *            表格属性列名数组
	    * @param dataset
	    *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	    *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	    * @param out
	    *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	    * @param pattern
	    *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	    */
	   @SuppressWarnings({ "unchecked"})
	   public void exportExcel(String title, String[] headers, String[] columnNames, 
	         Collection<T> dataset, OutputStream out, String pattern) {
	     
	      // 生成一个表格
	      HSSFSheet sheet = workbook.createSheet(title);
	      // 设置表格默认列宽度为32个字节
	      sheet.setDefaultColumnWidth((short) 32);
	     
	      // 声明一个画图的顶级管理器
	      HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
	      // 定义注释的大小和位置,详见文档
	      HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
	      // 设置注释内容
	      comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
	      // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
	      comment.setAuthor("FSS");
	 
	      //产生表格标题行
	      HSSFRow row = sheet.createRow(0);
	      for (short i = 0; i < headers.length; i++) {
	         HSSFCell cell = row.createCell(i);
	         cell.setCellStyle(styleTitle);
	         HSSFRichTextString text = new HSSFRichTextString(headers[i]);
	         cell.setCellValue(text);
	      }
	      
	      //遍历集合数据，产生数据行
	      Iterator<T> it = dataset.iterator();
	      int index = 0;
	      while (it.hasNext()) {
	         index++;
	         row = sheet.createRow(index);
	         T t = (T) it.next();
	         //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
	         
	         for (short i = 0; i < columnNames.length; i++) {
	        	HSSFCell cell = null;
	        	 
	            String fieldName = columnNames[i];
	            String getMethodName = "get"
	                   + fieldName.substring(0, 1).toUpperCase()
	                   + fieldName.substring(1);
	            try {
		                 
	            	Class tCls = t.getClass();
	                Method getMethod = tCls.getMethod(getMethodName,
		                      new Class[] {});
		            Object value = getMethod.invoke(t, new Object[] {});
	                	 
	                cell = row.createCell(i);
	 		        cell.setCellStyle(styleBody);
	 		        
	                //判断值的类型后进行强制类型转换
		            String textValue = null;
		              
		            if(value != null) {
		            	textValue = value.toString(); 
		            }
	                //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
	                if(textValue!=null){
	                   Pattern p = Pattern.compile("^//d+(//.//d+)?$");  
	                   Matcher matcher = p.matcher(textValue);
	                   if(matcher.matches()){
	                      //是数字当作double处理
	                      cell.setCellValue(Double.parseDouble(textValue));
	                   }else{
	                      HSSFRichTextString richString = new HSSFRichTextString(textValue);
	                      HSSFFont font3 = workbook.createFont();
	                      font3.setColor(HSSFColor.BLUE.index);
	                      richString.applyFont(font3);
	                      cell.setCellValue(richString);
	                   }
	                }
	            } catch (SecurityException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (NoSuchMethodException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (IllegalArgumentException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (InvocationTargetException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } finally {
	                //清理资源
	            }
	         }
	 
	      }
	      try {
	         workbook.write(out);
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	 
	   }
	   
	   
	   /**
        * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
        *
        * @param title
        *            表格标题名
        * @param headers
        *            表格属性列名数组
        * @param datas
        *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
        *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
        * @param out
        *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
        * @param pattern
        *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
        * @return 返回true或者false
        */
       public boolean createExcel(String title, List<String> headers, List<List<Object>> datas, OutputStream out, String pattern) {
           boolean result = false;
           try {
              // 生成一个表格
              HSSFSheet sheet = workbook.createSheet(title);
              // 设置表格默认列宽度为32个字节
              sheet.setDefaultColumnWidth((short) 32);
             
              // 声明一个画图的顶级管理器
              HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
              // 定义注释的大小和位置,详见文档
              HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
              // 设置注释内容
              comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
              // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
              comment.setAuthor("FSS");
         
              //产生表格标题行
              HSSFRow row = sheet.createRow(0);
              for (int i = 0, k = headers.size(); i < k; i++) {
                 HSSFCell cell = row.createCell(i);
                 cell.setCellStyle(styleTitle);
                 HSSFRichTextString text = new HSSFRichTextString(headers.get(i));
                 cell.setCellValue(text);
              }
              
              //遍历集合数据，产生数据行
              List<Object> dataList = null;
              Object value = null;
              String textValue = null;
              HSSFCell cell = null;
              for (int i = 0, k = datas.size(); i < k; i++) {
                  dataList = datas.get(i);
                  if(dataList != null){
                      row = sheet.createRow(i + 1);
                      for (int j = 0, l = dataList.size(); j < l; j++) {
                          try {
                              value = dataList.get(j);
                                   
                              cell = row.createCell(j);
                              cell.setCellStyle(styleBody);
                              
                              //判断值的类型后进行强制类型转换
                              textValue = CommonUtils.objectToString(value);
                              
                              //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                              if(textValue != null){
                                 Pattern p = Pattern.compile("^//d+(//.//d+)?$");  
                                 Matcher matcher = p.matcher(textValue);
                                 if(matcher.matches()){
                                    //是数字当作double处理
                                    cell.setCellValue(Double.parseDouble(textValue));
                                 } else{
                                    HSSFRichTextString richString = new HSSFRichTextString(textValue);
                                    /*HSSFFont font3 = workbook.createFont();
                                    font3.setColor(HSSFColor.BLUE.index);
                                    richString.applyFont(font3);*/
                                    cell.setCellValue(richString);
                                 }
                              }
                          } catch (SecurityException e) {
                              e.printStackTrace();
                              break;
                          }
                      }
                  }
              }
              workbook.write(out);
              result = true;
           } catch (IOException e) {
               e.printStackTrace();
               result = false;
           }
           return result;
       }
	 
	   /*public static void main(String[] args) {
	      // 测试学生
	      ExportExcel<FssStockFundCount> ex = new ExportExcel<FssStockFundCount>();
	      String[] headers = { "地区代码", "地区名称"};
	      String[] cols = { "areaCode", "areaName"};
	      List<FssStockFundCount> dataset = new ArrayList();
	      FssStockFundCount entity = new FssStockFundCount();
	      entity.setAreaCode("1231");
	      entity.setAreaName("3232323");
	      dataset.add(entity);
	      
	      entity = new FssStockFundCount();
	      entity.setAreaCode("对对对");
	      entity.setAreaName("东方大道");
	      dataset.add(entity);
	      
	      entity = new FssStockFundCount();
	      entity.setAreaCode("对对对1212");
	      entity.setAreaName("东方大道12122");
	      dataset.add(entity);
	     
	      try {
	    	 File file = new File("F://a.xls");
	 
	    	 if(!file.exists()) {
	    		 file.createNewFile();
	    	 }
	         OutputStream out = new FileOutputStream(file);
	         ex.exportExcel("中国", headers, cols, dataset, out, "");
	         out.close();
	         JOptionPane.showMessageDialog(null, "导出成功!");
	         System.out.println("excel导出成功！");
	      } catch (FileNotFoundException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	   }*/
}
