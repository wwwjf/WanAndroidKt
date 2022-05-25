package com.xianghe.ivy.utils;

import android.text.TextUtils;
import android.util.Log;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <li>当天显示 xx分钟前 几小时前</li>
 * <li>昨天的显示 昨天 XX：XX</li>
 * <li>昨天以后的显示XX月XX日+XX：XX</li>
 * <li>去年以后的显示XX年XX月XX日+XX时:XX分</li>
 * <li>其余显示，2016-10-15</li>
 */
public class TimeFormatUtils {

    private static final int MIN = 60 * 1000;
    private static final int HOUR = 60 * 60 * 1000;

    public static String toToday(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return toToday(sdf.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatTimeMillis(final long millis){
        long now = System.currentTimeMillis()/1000;
        long span = now - millis;

        Log.e("123456789",span + "--" + now + "---" + millis );
        if(span < 0){
            return IvyApp.getInstance().getResources().getString(R.string.common_just_now);
        }else if(span >= 0 && span < (60)){
            return IvyApp.getInstance().getResources().getString(R.string.common_just_now);
        } else if(span >= (60) && span < (60 * 60)){
            //分钟
             return span/(60) + "分钟前";
        }else if(span >= (60 * 60 ) && span < (24 * 60 * 60  )){
            return span/(60 * 60 ) + "小时前";
        }else if(span >= (24 * 60 * 60 ) && span < (30 * 24 * 60 * 60  )){
            return span/(24 * 60 * 60 ) + "天前";
        }else if(span >= (30 * 24  * 60 * 60 ) && span < (356  * 24 * 60 * 60  )){
            return span/(30 *  24  * 60 * 60 ) + "月前";
        }else if(span >= (365  * 24  * 60 * 60 )){
            return span/(365  *  24  * 60 * 60 ) + "年前";
        }else {
            return "未知";
        }

    }


    public static String toToday(final long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0)
            return String.format("%tF", millis);
        if (span < TimeFormatUtils.MIN) {
            return IvyApp.getInstance().getResources().getString(R.string.common_just_now);
        } else if (span < TimeFormatUtils.HOUR) {
            return String.format(Locale.getDefault(), IvyApp.getInstance().getResources().getString(R.string.common_minute_ago), span / TimeFormatUtils.MIN);
        }
        // 当天0点时间戳
        long beginDayOfToday = getBeginDayOfToday();
        // 昨天0点时间戳
        long beginDayOfYesterday = getBeginDayOfYesterday();
        //今年1月1日0点时间戳
        long beginDayOfYear = getBeginDayOfYear();
        if (millis >= beginDayOfToday) {//大于今天0点
            return String.format(Locale.getDefault(), IvyApp.getInstance().getResources().getString(R.string.common_hours_ago), span / TimeFormatUtils.HOUR);
        } else if (millis >= beginDayOfYesterday) {//大于昨天0点
            return String.format(IvyApp.getInstance().getResources().getString(R.string.common_yesterday_ago), millis);
        } else if (millis >= beginDayOfYear) {
            SimpleDateFormat sdf = new SimpleDateFormat(IvyApp.getInstance().getResources().getString(R.string.common_this_year), Locale.getDefault());
            return sdf.format(new Date(millis));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(IvyApp.getInstance().getResources().getString(R.string.common_last_year), Locale.getDefault());
            return sdf.format(new Date(millis));
        }
    }

    /**
     * 当天开始时间
     *
     * @return long 时间戳
     */
    public static long getBeginDayOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获取昨天的开始时间
    private static long getBeginDayOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTimeInMillis();
    }

    //获取本年的开始时间
    private static long getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        // cal.set
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);

        return getDayStartTime(cal.getTime());
    }

    //获取今年是哪一年
    private static Integer getNowYear() {
        Date date = new Date();
        Calendar gc = Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.YEAR);
    }

    //获取某个日期的开始时间
    private static long getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean isSameDay(long t1, long t2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(t1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(t2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }

    public static boolean isSameMonth(long t1, long t2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(t1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(t2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        return isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    public static int getAge(String birthDayStr) {
        if (birthDayStr == null || TextUtils.isEmpty(birthDayStr)) {
            return -1;
        }
        Date birthDay = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            birthDay = formatter.parse(birthDayStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常
        if (cal.before(birthDay)) {
            return -1;
        }
        //取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        //将日期设置为出生日期
        cal.setTime(birthDay);
        //取出出生日期的年、月、日部分
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        //当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth;
        //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
        if (monthNow <= monthBirth) {
            //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss.sss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
