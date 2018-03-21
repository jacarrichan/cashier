/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * DateUtil
 * @author Danny
 *
 */
public final class DateUtil {

	private static final String dateTimeFormat = Constants.DATE_TIME_FORMAT;
	private static final String dateFormat = Constants.DATE_FORMAT;

	private DateUtil() {
	}

	public static String getDfTime() {
		SimpleDateFormat df = new SimpleDateFormat(Constants.TXT_FULL_DATE_FORMAT);
		return df.format(new Date());
	}
	
	public static Date parseDate(String dateString, String dFormat) {
		try {
			return DateUtils.parseDate(dateString, dFormat);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentDateString(String dateFormat) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());

		return DateFormatUtils.format(cal, dateFormat);
	}

	public static String getCurrentDateString() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return DateFormatUtils.format(cal, Constants.TXT_FULL_DATE_FORMAT);

	}

	public static String getDateString(Date date, String dateFormat) {
		if (date != null) {
			return DateFormatUtils.format(date, dateFormat);
		}
		else {
			return null;
		}
	}

	public static Date getDate(Date date, String dateFormat) {
		if (date != null) {

			return getDate(DateFormatUtils.format(date, dateFormat), dateFormat);
		}
		else {
			return null;
		}
	}

	public static Date getDate(String sDate, String dateFormat) {
		try {
			if (sDate == null || sDate.trim().equals("")) {
				return null;
			}
			return DateUtils.parseDate(sDate, dateFormat);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date addDays(String startDate, String dateFormat, int days) {
		return addDays(getDate(startDate, dateFormat), days);
	}

	public static Date addDays(Date startDate, int days) {
		return DateUtils.addDays(startDate, days);
	}

	public static Date addWeeks(Date startDate, int weeks) {
		return DateUtils.addWeeks(startDate, weeks);
	}

	public static Date addMonths(Date startDate, int months) {

		return DateUtils.addMonths(startDate, months);
	}

	public static Date addYears(Date startDate, int years) {

		return DateUtils.addYears(startDate, years);
	}

	public static Date addHours(Date startDate, int hours) {

		return DateUtils.addHours(startDate, hours);
	}

	public static Date addHours(String startDate, String dateFormat, int hours) {
		return addHours(getDate(startDate, dateFormat), hours);
	}

	public static String addHours(String startDate, String dateFormat1, int hours, String dateFormat2) {
		return getDateString(addHours(getDate(startDate, dateFormat1), hours), dateFormat2);
	}

	public static boolean isDateBetween(Date d, Date d1, Date d2) {
		return (d1.before(d) || d1.equals(d)) && (d.before(d2) || d.equals(d2));
	}

	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return DateFormatUtils.format(cal, "HH:mm:ss");
	}

	public static String getCurrentDateStr(String format) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());

		return DateFormatUtils.format(cal, format);

	}

	public static String getCurrentDate() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return DateFormatUtils.format(cal, dateFormat);
	}

	public static String getDateFile() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return DateFormatUtils.format(cal, Constants.TXT_SIMPLE_DATE_FORMAT);
	}

	public static String getCurrentDateInfo() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return DateFormatUtils.format(cal, dateTimeFormat);
	}

	public static String formatDate(Date theDate, String format) {
		return DateFormatUtils.format(theDate, format);
	}

	public static String formatDate(Date theDate) {

		return DateFormatUtils.format(theDate, dateFormat);
	}

	public static int getDateDiff(Date date1, Date date2, int sign) {
		long base = 1L;
		switch (sign) {
		case 5: // '\005'
			base *= 0x5265c00L;
			break;

		case 10: // '\n'
			base *= 0x36ee80L;
			break;

		case 12: // '\f'
			base *= 60000L;
			break;

		case 13: // '\r'
			base *= 1000L;
			break;
		}
		return (int) ((date1.getTime() - date2.getTime()) / base);
	}

	public static double getDateDiffTypeOfDouble(Date date1, Date date2, int sign) {
		if (date1 == null) {
			date1 = new Date();
		}
		if (date2 == null) {
			date2 = new Date();
		}
		long base = 1L;
		switch (sign) {
		case 5: // '\005'
			base *= 0x5265c00L;
			break;

		case 10: // '\n'
			base *= 0x36ee80L;
			break;

		case 12: // '\f'
			base *= 60000L;
			break;

		case 13: // '\r'
			base *= 1000L;
			break;
		}
		double returnminus = (double) (date1.getTime() - date2.getTime()) / (double) base;
		return returnminus;
	}

	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(1);
	}

	public static int getCurrentMonth() {
		int month = Calendar.getInstance().get(2) + 1;
		return month;
	}

	/**
	 * 算出当前时间与出入参数时间的相隔秒数
	 * 
	 * @param datePara datePara
	 * @return seconds
	 */
	public static int getSecondsByCurrentDate(String datePara) {
		// 传入需要比较时间
		Date d1 = getDate(datePara, dateTimeFormat);
		// 当前时间
		Date d2 = getDate(getCurrentDateString(dateTimeFormat), dateTimeFormat);
		// 算出相隔秒数
		int seconds = getDateDiff(d2, d1, 13);
		return seconds;
	}

	/**
	 * 算出当前时间与出入参数时间的相隔毫秒数
	 * @param datePara datePara
	 * @return seconds
	 */
	public static int getMilliSecondsByCurrentDate(String datePara) {
		String datePatt = "yyyy-MM-dd HH:mm:ss.SSS";
		// 传入需要比较时间
		Date d1 = getDate(datePara, datePatt);
		// 当前时间
		Date d2 = getDate(getCurrentDateString(datePatt), datePatt);
		// 算出相隔秒数
		int seconds = getDateDiff(d2, d1, 0);
		return seconds;
	}

	public static String getCurrentDateStr() {
		Date date = new Date();
		return getDateString(date, "yyyy-MM-dd HH:mm");
	}

	public static String getCurrentDateNow() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());

		return DateFormatUtils.format(cal, "yyyy-MM-dd HH:mm");
	}

	/**
	 * 算出当前时间与出入参数时间的相隔毫秒数
	 * 
	 * @param datePara datePara
	 * @return re_time
	 */
	public static String getformat(String datePara) {
		// 传入需要比较时间
		Date date = getDate(datePara, dateTimeFormat);
		// 当前时间
		return getDateString(date, dateTimeFormat);
	}

	/**
	 * 计算时间差 天
	 * 
	 * @param begin begin
	 * @param end end
	 * @return time
	 */
	public static int countTime(String begin, String end) {
		int hour = 0;
		int minute = 0;
		int day = 0;
		long total_minute = 0;
		StringBuffer sb = new StringBuffer();

		try {
			Date begin_date = DateUtils.parseDate(begin, dateTimeFormat);
			Date end_date = DateUtils.parseDate(end, dateTimeFormat);

			total_minute = (end_date.getTime() - begin_date.getTime()) / (1000 * 60);

			day = (int) total_minute / 1440;
			hour = (int) total_minute / 60;
			minute = (int) total_minute % 60;

		}
		catch (ParseException e) {
			System.out.println("传入的时间格式不符合规定");
		}

		sb.append("工作时间为：").append(day).append("天").append(hour).append("小时").append(minute).append("分钟");
		return day;
	}

	/**
	 * 计算时间差 天
	 * 
	 * @param begin begin
	 * @param end end
	 * @return countTimeMinue
	 */
	public static int countTimeMinue(String begin, String end) {
		int hour = 0;
		int minute = 0;
		int day = 0;
		long total_minute = 0;
		StringBuffer sb = new StringBuffer();

		SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);
		try {
			Date begin_date = df.parse(begin);
			Date end_date = df.parse(end);

			total_minute = (end_date.getTime() - begin_date.getTime()) / (1000 * 60);

			day = (int) total_minute / 1440;
			hour = (int) total_minute / 60;
			minute = (int) total_minute;

		}
		catch (ParseException e) {
			System.out.println("传入的时间格式不符合规定");
		}
		sb.append("工作时间为：").append(day).append("天").append(hour).append("小时").append(minute).append("分钟");
		return minute;
	}

	// /**
	// * 计算时间差 天
	// *
	// * @param begin begin
	// * @return countTimeMinue
	// */
	// public static boolean timeC(String begin) {
	// long total_minute = 0;
	// SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);
	// try {
	// Date begin_date = df.parse(begin);
	// Date end_date = new Date();
	//
	// total_minute = end_date.getTime() - begin_date.getTime();
	// return total_minute > 0;
	// } catch (ParseException e) {
	// System.out.println("传入的时间格式不符合规定");
	// return false;
	// }
	// }

	public static String nowTimeAddDay(int addDay) {
		return getDateString(addDays(new Date(), addDay), dateFormat);

	}

	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay specifiedDay
	 * @return getSpecifiedDayAfter
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {
		return getDateString(addDays(specifiedDay, "yy-MM-dd", 1), "yy-MM-dd");

	}

	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay specifiedDay
	 * @return String
	 * @throws Exception Exception
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {// 可以用new
																		// Date().toLocalString()传递参数
		return getDateString(addDays(specifiedDay, "yy-MM-dd", -1), "yy-MM-dd");
	}

	/**
	 * 获取当前日期的前一天  yyyyMMdd
	 * @return String
	 * @throws Exception Exception
	 */
	public static String getSpecifiedDayBeforeString() {
		SimpleDateFormat df = new SimpleDateFormat(Constants.TXT_SIMPLE_DATE_FORMAT);
		return getDateString(addDays(df.format(new Date()), "yyyyMMdd", -1), "yyyyMMdd");
	}

	
	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay specifiedDay
	 * @return getSpecifiedDayAfter
	 */
	public static String getSpecifiedDayAfter(Date specifiedDay) {

		if (specifiedDay == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		Date date = null;
		date = specifiedDay;

		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = DateFormatUtils.format(c, dateFormat);
		return dayAfter;
	}

	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay specifiedDay
	 * @return getSpecifiedDayBefore
	 * @throws Exception Exception
	 */
	public static String getSpecifiedDayBefore(Date specifiedDay) {// 可以用new
																	// Date().toLocalString()传递参数
		if (specifiedDay == null) {
			return null;
		}
		return getDateString(addDays(specifiedDay, -1), dateFormat);

	}

	public static boolean sameDate(Date d1, Date d2) {
		if (null == d1 || null == d2) {
			return false;
		}

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(d1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(d2);
		return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
	}

	/**
	 * 只比较年月日，不比较时分秒 返回1，d1小于d2 返回0，d1等于d2
	 * @param d1 d1
	 * @param d2 d2
	 * @return compareDateByYMD
	 */
	public static int compareDateByYMD(Date d1, Date d2) {
		LocalDate ld1 = ZonedDateTime.ofInstant(d1.toInstant(), ZoneId.systemDefault()).toLocalDate();
		LocalDate ld2 = ZonedDateTime.ofInstant(d2.toInstant(), ZoneId.systemDefault()).toLocalDate();

		if (ld1.isBefore(ld2)) {
			return 1;
		}
		if (ld1.isEqual(ld2)) {
			return 0;
		}
		if (ld1.isAfter(ld2)) {
			return -1;
		}
		return 0;
	}

	/**
	 * 判断是否月末
	 * @param date 日期
	 * @return true 是，false 否
	 */
	public static boolean isLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return (cal.get(Calendar.DATE) == cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 获取本月第一天
	 * @param date 日期
	 * @return firstDay
	 */
	public static String getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		return new SimpleDateFormat(dateFormat).format(cal.getTime());
	}

	/**
	 * 获取本月最后一天
	 * @param date 日期
	 * @return lastDay
	 */
	public static String getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.getActualMaximum(cal.DATE));
		return new SimpleDateFormat(dateFormat).format(cal.getTime());
	}

	/**
	 * 获取当月会计期间编码
	 * @param date 日期
	 * @return lastDay
	 */
	public static String getAcctPeriodByDate(Date date) {
		String acctPeriodCode = null;
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
			String year = String.valueOf(cal.get(Calendar.YEAR));
			String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			if (month.length() == 1) {
				month = (new StringBuilder("0")).append(month).toString();
			}
			acctPeriodCode = year + month;
		}

		return acctPeriodCode;
	}

	/**
	 * 判断是否年末
	 * @param date 日期
	 * @return true 是，false 否
	 */
	public static boolean isLastDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int y1 = cal.get(Calendar.YEAR);
		cal.setTime(addDays(date, 1));
		int y2 = cal.get(Calendar.YEAR);
		return (y2 > y1);
	}

	/**
	 * 校验日期格式
	 * @param format 格式
	 * @param date 日期值
	 * @return boolean 返回值
	 */
	public static boolean checkDate(String format, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			sdf.setLenient(false);
			sdf.parse(date);
			return true;
		}
		catch (ParseException e) {
			return false;
		}
	}
}
