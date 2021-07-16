package com.reSipWebRTC.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	/**
	 * 对比两个时间
	 * @param date1
	 * @param date2
	 * @return 1 date1>date2; -1 date1<date2; 0 date1=date2
	 */
	public static int compareDate(String date1, String date2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");// 日期格式控制
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 对比两个时间
	 * @param date1
	 * @param date2
	 * @return 1 date1>date2; -1 date1<date2; 0 date1=date2
	 */
	public static String formateStrDate(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");// 日期格式控制
		try {
//			Date dt = df.parse(date);
//			String temp1 = df.format(dt);
			String temp2 = df.format(date);
//			return df.format(dt);
			return df.format(date);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
