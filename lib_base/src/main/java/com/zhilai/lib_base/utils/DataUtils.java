package com.zhilai.lib_base.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 构造下位机返回假数据用于进行取物流程调试
 * */
public class DataUtils {

	/** 完整时间 yyyy-MM-dd HH:mm:ss */
	public static final String simp = "yyyy-MM-dd";
	public static final String simple = "yyyy-MM-dd HH:mm:ss";
	public static final String sim = "yyyyMMddHHmmss";
	public static final String dateFormat= "yyyy-MM-dd HH:mm";
	/** 完整时间 yyyy-MM-dd HH:mm:ss */
	public static final String secondFormat = "yyyy-MM-dd HH:mm:ss.SSS";
	
	@SuppressLint("SimpleDateFormat")
	public static String DateToString(String dformat, Date date) {
		if (null==date) {
			return null;
		}
		if (null==dformat||"".equals(dformat)) {
			dformat=simple;
		}
		SimpleDateFormat df= new SimpleDateFormat(dformat);
		return df.format(date);
	}
	/**
	 * yyyy-MM-dd HH:mm
	 * @param datevalue
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isDate(String datevalue) {
        if (null==datevalue||"".equals(datevalue.trim())) {
            return false;
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
            Date dd = fmt.parse(datevalue);
            if (datevalue.equals(fmt.format(dd))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

	public static String DateToString(String dformat) {
		return DateToString(dformat, new Date());
	}
	public static String DateStr() {
		return DateToString(sim, new Date());
	}
	public static String Date() {
		return DateToString(simp, new Date());
	}
	public static String DateToString() {
		return DateToString(simple, new Date());
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public static String Stime() {
		return DateToString(secondFormat, new Date());
	}

	/**
	 * 将当前的时间按照"HH:mm:ss:SSS"格式化（带毫秒）
	 *
	 * @return "HH:mm:ss:SSS"格式化后的时间
	 */
	public static String getSSSToString() {
		String res;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CEST")); //设置时区
		long lt = new Long(Long.parseLong(System.currentTimeMillis() + ""));
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

}
