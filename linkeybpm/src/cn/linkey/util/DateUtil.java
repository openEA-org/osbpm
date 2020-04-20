package cn.linkey.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import cn.linkey.factory.BeanCtx;

public class DateUtil {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 判断某一天的时间是否是节假日
     * 
     * @param day Date对像
     * @return 返回true表示是,false表示否
     */
    public static boolean isHoliday(Date day) {
        return ((DateHoliday) BeanCtx.getBean("DateHoliday")).isHoliday(day);
    }

    /**
     * 获得两个时间字符串的差值(分钟)，如果需要减去非工作时间则需要在系统设置中开启工作有效时间计算
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回分钟
     */
    public static String getDifTime(String startTime, String endTime) {
        return ((DateHoliday) BeanCtx.getBean("DateHoliday")).getDifTime(startTime, endTime);
    }

    /**
     * 获得两个时间的差值,不减工作时间
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回分钟
     */
    public static String getAllDifTime(String startTime, String endTime) {
        try {
            Date begin = str2DateTime(startTime);
            Date end = str2DateTime(endTime);
            Long between = (end.getTime() - begin.getTime());
            if (between < 0) {
                return "-1";
            }
            between = between / 1000 / 60;
            return Long.toString(between);
        }
        catch (Exception e) {
            return "0";
        }
    }

    /**
     * 比较开始时间是否小于结束时间
     * 
     * @param startTime 开始时间字符串
     * @param endTime 结束时间字符串
     * @return 返回true表示成立,开始时间小于结束时间，false表示开始时间大于结束时间
     */
    public static boolean lessTime(String startTime, String endTime) {
        try {
            Date begin = str2DateTime(startTime);
            Date end = str2DateTime(endTime);
            Long between = (end.getTime() - begin.getTime());
            if (between > 0) {
                return true;
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "时间比较出错(" + startTime + "->" + endTime + ")");
        }
        return false;
    }

    public static Date str2DateTime(String str) {
        if (StringUtils.isNotEmpty(str)) {
            if(str.split(":").length < 2) {
                str = str + " 00:00:00";
            }
            else if(str.split(":").length == 2) {
                str = str + ":00";
            }
            SimpleDateFormat dfs = new SimpleDateFormat(DATE_TIME_FORMAT);
            try {
                return dfs.parse(str);
            }
            catch (ParseException e) {
                BeanCtx.log(e, "E", "时间格式化出错(" + str + ")");
            }
        }
        return null;
    }
    
    public static Date str2Date(String str) {
        return string2Date(str, DATE_FORMAT);
    }

    /**
     * 获得现在的时间
     * 
     * @return 返回指定格式的时间字符串
     */
    public static String getNow(String dateFormat) {
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat(dateFormat);
        return (String) insDateFormat.format(new java.util.Date());
    }

    /**
     * 获得现在的时间
     * 
     * @return 返回标准的如：2013-01-03 12:09
     */
    public static String getNow() {
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat(DATE_TIME_FORMAT);
        return (String) insDateFormat.format(new java.util.Date());
    }

    /**
     * 得到当前时间对像
     * 
     * @return 返回Date对像
     */
    public static Date getDate() {
        Calendar canlendar = Calendar.getInstance();
        return canlendar.getTime();
    }

    /**
     * 字符串转为时间Date对像
     * 
     * @param timeStr 要转换的字符串
     * @param timeFormat 时间格式，传null默认为yyyy-MM-dd HH:mm:ss
     * @return 返回Date对像
     */
    public static Date getDate(String timeStr, String timeFormat) {
        try {
            if (timeFormat == null) {
                timeFormat = DATE_TIME_FORMAT;
            }
            SimpleDateFormat dfs = new SimpleDateFormat(timeFormat);
            return dfs.parse(timeStr);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 指定的millis得到时间
     * 
     * @param millis
     * @return
     */
    public static Date getDate(long millis) {
        Calendar canlendar = Calendar.getInstance();
        canlendar.clear();
        canlendar.setTimeInMillis(millis);
        return canlendar.getTime();
    }

    public static long getMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 格式化时间为指定格式的字符串
     * 
     * @param date 时间对像
     * @param formate 格式化字符串如：yyyy-MM-dd HH:mm:ss
     * @return 返回字符串
     */
    public static String formatDate(Date date, String formate) {
        try {
            SimpleDateFormat simpleDateFormate = new SimpleDateFormat(formate);
            return simpleDateFormate.format(date);
        }
        catch (Exception e) {
        }
        return "";
    }

    /**
     * 根据指定格式,把字符串转成日期
     * 
     * @param sDate
     * @param formate
     * @return
     */
    public static Date string2Date(String sDate, String formate) {
        SimpleDateFormat simpleDateFormate = new SimpleDateFormat(formate);
        try {
            return simpleDateFormate.parse(sDate);
        }
        catch (ParseException e) {
            BeanCtx.log(e, "E", "时间格式化出错(" + sDate + ")");
            return null;
        }
    }

    /**
     * 获得现在的日期转换为数字格式
     * 
     * @return
     */
    public static String getDateNum() {
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
        return (String) insDateFormat.format(new java.util.Date());
    }

    /**
     * 获得现在的日期和时间转换为数字格式
     * 
     * @return
     */
    public static String getDateTimeNum() {
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        return (String) insDateFormat.format(new java.util.Date());
    }

    /**
     * 计算两个时间对像的时间差(时间的毫秒数),可以得到指定的毫秒数,秒数,分钟数,天数
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param tdatestr[part可选值["D","H","M","S","MS"] @return[endDate-startDate]
     */
    public static double getDifTwoTime(Date endDate, Date startDate, String tdatestr) {
        if (endDate == null || startDate != null) {
            return DateUtil.getDifTwoTime(endDate.getTime(), startDate.getTime(), tdatestr);
        }
        return 0;
    }

    /**
     * 两个长整型的时间相差(时间的毫秒数),可以得到指定的毫秒数,秒数,分钟数,天数
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param tdatestr[part可选值["D","H","M","S","MS"] @return[endDate-startDate]
     */
    public static double getDifTwoTime(long endDate, long startDate, String tdatestr) {
        if (tdatestr == null || tdatestr.equals("")) {
            tdatestr = "MS";
        }
        double temp = 1;
        /** 毫秒数 */
        if ("MS".equalsIgnoreCase(tdatestr)) {
            temp = 1;
        }
        /** 得到秒 */
        if ("S".equalsIgnoreCase(tdatestr)) {
            temp = 1000;
        }
        /** 得到分 */
        if ("M".equalsIgnoreCase(tdatestr)) {
            temp = 1000 * 60;
        }
        /** 得到小时 */
        if ("H".equalsIgnoreCase(tdatestr)) {
            temp = 1000 * 60 * 60;
        }
        /** 得到天 */
        if ("D".equalsIgnoreCase(tdatestr)) {
            temp = 1000 * 60 * 60 * 24;
        }
        return (endDate - startDate) / temp;
    }

    /**
     * 从日期中得到指定部分(YYYY/MM/DD/HH/SS/SSS)数字
     * 
     * @param date
     * @param part[part可选值["Y","M","D","H","M","S","MS"]
     * @return
     */
    public static int getPartOfTime(Date date, String part) {
        Calendar canlendar = Calendar.getInstance();
        canlendar.clear();
        canlendar.setTime(date);
        /** 得到年 */
        if (part.equalsIgnoreCase("Y")) {
            return canlendar.get(Calendar.YEAR);
        }
        /** 得到月 */
        if (part.equalsIgnoreCase("M")) {
            return canlendar.get(Calendar.MONTH) + 1;
        }
        /** 得到日 */
        if (part.equalsIgnoreCase("D")) {
            return canlendar.get(Calendar.DAY_OF_MONTH);
        }
        /** 得到时 */
        if (part.equalsIgnoreCase("H")) {
            return canlendar.get(Calendar.HOUR_OF_DAY);
        }
        /** 得到分 */
        if (part.equalsIgnoreCase("M")) {
            return canlendar.get(Calendar.MINUTE);
        }
        /** 得到秒 */
        if (part.equalsIgnoreCase("S")) {
            return canlendar.get(Calendar.SECOND);
        }
        /** 得到毫秒 */
        if (part.equalsIgnoreCase("MS")) {
            return canlendar.get(Calendar.MILLISECOND);
        }
        return -1;
    }
}