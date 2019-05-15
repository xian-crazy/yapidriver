package com.crazy.autotest.yapidriver.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guoli on 2019/2/13.
 */
public class DateUtils {
    /**
     * 获取
     * @param cur
     * @param n
     * @return
     */

    public Date getNewDate(Date cur ,int n){
        Calendar c = Calendar.getInstance();
        c.setTime(cur);   //设置时间
        c.add(Calendar.HOUR_OF_DAY, n); //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime(); //结果
        return date;
    }

    public static String nowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");//设置日期格式
        return (df.format(new Date()));// new Date()为获取当前系统时间
    }

    public static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date string2Date(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date dateAddDay(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    public static String time(int year,int month,int day,int h,int m,int s){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Calendar c = Calendar.getInstance();
        c.set(year,month,day,h,m,s);
        return (df.format(c.getTime()));
    }

    /**
     *
     * @param c  = Calendar.getInstance();
     * @param format "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String time(Calendar c,String format){//
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return (df.format(c.getTime()));
    }

    /**
     * 将指日期转为固定格式的字符串日期，日期类型为String
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDateToString(Date date, DateformatEnum dateFormat) {
        if (null == date) {
            return "";
        }
        String strDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getFormat());
        strDate = sdf.format(date);
        return strDate;
    }

    /**
     *将指日期转为固定格式的字符串日期，日期类型为String
     * @param date
     * @param dateFormat
     * @return
     */
    public static long formatDateTolong(Date date, DateformatEnum dateFormat) {
        if (null == date) {
            return 0;
        }
        String strDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getFormat());
        strDate = sdf.format(date);
        return Long.parseLong(strDate);
    }

    /**
     * 将字符串类型日期转为日期格式
     * @param strDate
     * @param dateFormat
     * @return
     */
    public static Date formatStringToDate(String strDate, DateformatEnum dateFormat) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getFormat());
        try {
            d = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

}
