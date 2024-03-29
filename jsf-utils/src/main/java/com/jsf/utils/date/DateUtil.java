package com.jsf.utils.date;

import com.jsf.utils.string.StringUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * <p>JODA TIME实现</p>
 *
 * @author jacksonrick
 * @version 2
 */
public class DateUtil {

    public final static DateTimeFormatter FMT_YYYY_MM_DD_HH_MM_SS = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter FMT_YYYY_MM_DD = DateTimeFormat.forPattern("yyyy-MM-dd");
    public final static DateTimeFormatter FMT_YYYYMMDD = DateTimeFormat.forPattern("yyyyMMdd");
    public final static DateTimeFormatter FMT_YYYY_MM = DateTimeFormat.forPattern("yyyy-MM");
    public final static DateTimeFormatter FMT_YYYYMM = DateTimeFormat.forPattern("yyyyMM");

    public final static String YYYY_MM_DD_HH_MM_SS_SS = "yyyy-MM-dd HH:mm:ss.SS";
    public final static String YYYYMMDDHHMMSSSS = "yyyyMMddHHmmssSS";
    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public final static String YYYYMMDD = "yyyyMMdd";
    public final static String YYYY_MM = "yyyy-MM";
    public final static String YYYYMM = "yyyyMM";
    public final static String YYYY = "yyyy";
    public final static String HH_MM_SS = "HH:mm:ss";
    public final static String HHMMSS = "HHmmss";

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentTime(1);
    }

    /**
     * 获取当前时间
     *
     * @param type 1 yyyy-MM-dd HH:mm:ss
     *             2 yyyy-MM-dd [Default]
     *             3 yyyy-MM
     *             4 yyyy
     * @return
     */
    public static String getCurrentTime(int type) {
        DateTime dt = new DateTime(System.currentTimeMillis());
        if (type == 1)
            return dt.toString(YYYY_MM_DD_HH_MM_SS);
        else if (type == 2)
            return dt.toString(YYYY_MM_DD);
        else if (type == 3)
            return dt.toString(YYYY_MM);
        else if (type == 4)
            return dt.toString(YYYY);
        return dt.toString(YYYY_MM_DD);
    }

    /**
     * 获取当前时间
     *
     * @param format 自定义格式
     * @return
     */
    public static String getCurrentTime(String format) {
        if (format == null || "".equals(format)) {
            format = YYYY_MM_DD_HH_MM_SS;
        }
        DateTime dt = new DateTime(System.currentTimeMillis());
        return dt.toString(format);
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static String getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 获取当前分钟
     *
     * @return
     */
    public static String getCurrentMinute() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.MINUTE));
    }

    /**
     * date转string
     *
     * @param date
     * @return YYYY_MM_DD_HH_MM_SS
     */
    public static String dateToStr(Date date) {
        if (date == null) {
            return StringUtil.EMPTY_STRING;
        }
        DateTime dt = new DateTime(date);
        return dt.toString(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * date转string
     *
     * @param date
     * @return YYYY_MM_DD
     */
    public static String dateToStrDay(Date date) {
        if (date == null) {
            return StringUtil.EMPTY_STRING;
        }
        DateTime dt = new DateTime(date);
        return dt.toString(YYYY_MM_DD);
    }

    /**
     * date转string
     *
     * @param date
     * @param format 自定义格式
     * @return
     */
    public static String dateToStr(Date date, String format) {
        if (date == null) {
            return StringUtil.EMPTY_STRING;
        }
        DateTime dt = new DateTime(date);
        return dt.toString(format);
    }

    /**
     * string转date
     * YYYY_MM_DD_HH_MM_SS
     *
     * @param dateString
     * @return
     */
    public static Date strToDate(String dateString) {
        if (dateString == null || "".equals(dateString)) {
            return null;
        }
        return FMT_YYYY_MM_DD_HH_MM_SS.parseDateTime(dateString).toDate();
    }

    /**
     * string转date
     * YYYY_MM_DD
     *
     * @param dateString
     * @return
     */
    public static Date strToDateDay(String dateString) {
        if (dateString == null || "".equals(dateString)) {
            return null;
        }
        return FMT_YYYY_MM_DD.parseDateTime(dateString).toDate();
    }

    /**
     * string转date
     *
     * @param dateString
     * @param format     自定义格式
     * @return
     */
    public static Date strToDate(String dateString, String format) {
        if (dateString == null || "".equals(dateString)) {
            return null;
        }
        DateTimeFormatter FMT = DateTimeFormat.forPattern(format);
        return FMT.parseDateTime(dateString).toDate();
    }

    /**
     * string转date
     *
     * @param dateString
     * @param format     自定义格式-DateTimeFormatter类型
     * @return
     */
    public static Date strToDate(String dateString, DateTimeFormatter format) {
        if (dateString == null || "".equals(dateString)) {
            return null;
        }
        return format.parseDateTime(dateString).toDate();
    }

    /**
     * long转date
     *
     * @param time
     * @return
     */
    public static Date longToDate(Long time) {
        DateTime dt = new DateTime(new Date(time));
        String date = dt.toString(YYYY_MM_DD_HH_MM_SS);
        return strToDate(date);
    }

    /**
     * 从时分秒获取总秒数，常用于音频文件的时长
     *
     * @param hhmmss
     * @return
     */
    public static int getSeconds(String hhmmss) {
        if (hhmmss == null || "".equals(hhmmss)) {
            return 0;
        }
        String[] text = hhmmss.split(":");
        int hour = Integer.valueOf(text[0]);
        int minute = Integer.valueOf(text[1]);
        int second = Integer.valueOf(text[2]);
        return hour * 3600 + minute * 60 + second;
    }

    /**
     * 获取年月
     *
     * @param flag 如果为true则有分隔符 -
     * @return 201601
     */
    public static String getYearAndMonth(boolean flag) {
        if (flag) {
            return getCurrentTime(YYYY_MM);
        }
        return getCurrentTime(YYYYMM);
    }

    /**
     * 获取年
     *
     * @return 2016
     */
    public static String getYear() {
        return getCurrentTime(4);
    }

    /**
     * 检查某时间是否在时间段内
     *
     * @param d1 起始时间
     * @param d2 终止时间
     * @param d  获取的时间
     * @return
     */
    public static boolean between(Date d1, Date d2, Date d) {
        return d.after(d1) && d.before(d2);
    }

    /**
     * 检查某时间是否在时间段内 格式：yyyy-MM-dd
     *
     * @param d1   起始时间
     * @param d2   终止时间
     * @param date 获取的时间
     * @return
     */
    public static boolean between(String d1, String d2, String date) {
        Date date1 = FMT_YYYY_MM_DD.parseDateTime(d1).toDate();
        Date date2 = FMT_YYYY_MM_DD.parseDateTime(d2).toDate();
        Date d = FMT_YYYY_MM_DD.parseDateTime(date).toDate();
        if (d.after(date1) && d.before(date2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取当月第一天
     *
     * @return
     */
    public static String getMonthFirstDay() {
        Calendar cal = Calendar.getInstance();
        Calendar f = (Calendar) cal.clone();
        f.clear();
        f.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        f.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        String firstday = dateToStrDay(f.getTime());
        firstday = firstday + " 00:00:00";
        return firstday;

    }

    /**
     * 获取当月的最后一天
     *
     * @return
     */
    public static String getMonthLastDay() {
        Calendar cal = Calendar.getInstance();
        Calendar l = (Calendar) cal.clone();
        l.clear();
        l.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        l.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        l.set(Calendar.MILLISECOND, -1);
        String lastday = dateToStrDay(l.getTime());
        lastday = lastday + " 23:59:59";
        return lastday;
    }

    /**
     * 计算两个时间之间相差的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long diffDays(Date startDate, Date endDate) {
        // 一天的毫秒数1000 * 60 * 60 * 24=86400000
        long days = 0;
        long start = startDate.getTime();
        long end = endDate.getTime();
        days = (end - start) / 86400000;
        return days;
    }

    /**
     * 获取两个时间相差秒数
     *
     * @param startDate
     * @param endDate
     * @param msec      是否显示毫秒
     * @return
     */
    public static long diffSeconds(Date startDate, Date endDate, boolean msec) {
        long times = endDate.getTime() - startDate.getTime();
        if (times < -1) {
            return 0;
        }
        if (msec) {
            return times;
        } else {
            return times / 1000;
        }
    }

    /**
     * 日期加上月数的时间
     *
     * @param date
     * @param month
     * @return
     */
    public static Date dateAddMonth(Date date, int month) {
        if (date == null) {
            return null;
        }
        return add(date, Calendar.MONTH, month);
    }

    /**
     * 日期加上天数的时间
     *
     * @param date
     * @param day
     * @return
     */
    public static Date dateAddDay(Date date, int day) {
        if (date == null) {
            return null;
        }
        return add(date, Calendar.DAY_OF_YEAR, day);
    }

    /**
     * 日期加上年数的时间
     *
     * @param date
     * @param year
     * @return
     */
    public static Date dateAddYear(Date date, int year) {
        if (date == null) {
            return null;
        }
        return add(date, Calendar.YEAR, year);
    }

    /**
     * 获取传入日期是周几
     *
     * @param date
     */
    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 检查时间是否过期
     *
     * @param time 结束时间
     * @param day  天数
     * @return true 过期
     */
    public static boolean checkTime(Date time, int day) {
        Date now = new Date();
        long t = time.getTime();
        t += day * 86400000l;
        Date d = new Date(t);
        return !d.after(now);
    }

    /**
     * 计算剩余时间 (多少天多少时多少分多少秒)
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String remainDateToString(Date startDate, Date endDate) {
        StringBuilder result = new StringBuilder();
        if (endDate == null) {
            return "Expire";
        }
        long times = endDate.getTime() - startDate.getTime();
        if (times < -1) {
            result.append("Expire");
        } else {
            long temp = 1000 * 60 * 60 * 24;
            // 天数
            long d = times / temp;
            // 小时数
            times %= temp;
            temp /= 24;
            long h = times / temp;
            // 分钟数
            times %= temp;
            temp /= 60;
            long m = times / temp;
            // 秒数
            times %= temp;
            temp /= 60;
            long s = times / temp;

            result.append(d);
            result.append("天");
            result.append(h);
            result.append("小时");
            result.append(m);
            result.append("分");
            result.append(s);
            result.append("秒");
        }
        return result.toString();
    }

    /**
     * 日期加减
     *
     * @param date
     * @param type  Calendar.DAY_OF_MONTH
     * @param value
     * @return
     */
    private static Date add(Date date, int type, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, value);
        return calendar.getTime();
    }

    /**
     * 获取距离当前时间前几秒、几分钟、几小时、几天
     *
     * @param time
     * @return
     */
    public static String getMoment(String time) {
        if (time == null || "".equals(time)) {
            return null;
        }
        Date now = new Date();
        Date date = FMT_YYYY_MM_DD_HH_MM_SS.parseDateTime(time).toDate();
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        StringBuffer sb = new StringBuffer("");
        if (day > 0)
            sb.append(day + "天");
        if (hour > 0)
            sb.append(hour + "小时");
        if (min > 0)
            sb.append(min + "分钟");
        if (s > 0)
            sb.append(s + "秒");
        sb.append("前");
        return sb.toString();
    }

    /**
     * 获取time距当前的时间间隔
     *
     * @param time 时间戳
     * @return
     */
    public static String getMoment(Long time) {
        long l = System.currentTimeMillis() - time;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuffer sb = new StringBuffer("");
        if (day > 0)
            sb.append(day + "天");
        if (hour > 0)
            sb.append(hour + "小时");
        if (min > 0)
            sb.append(min + "分钟");
        if (s > 0)
            sb.append(s + "秒");
        return sb.toString();
    }

    /**
     * 秒转换为中文
     *
     * @param seconds
     * @return
     */
    public static String secondToStr(Integer seconds) {
        if (seconds < 0) {
            return "0";
        }
        long day = seconds / (24 * 60 * 60);
        long hour = (seconds / (60 * 60) - day * 24);
        long min = ((seconds / 60) - day * 24 * 60 - hour * 60);
        long s = (seconds - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuffer sb = new StringBuffer("");
        if (day > 0)
            sb.append(day + "天");
        if (hour > 0)
            sb.append(hour + "小时");
        if (min > 0)
            sb.append(min + "分钟");
        if (s > 0)
            sb.append(s + "秒");
        return sb.toString();
    }

    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;

    public static final String ONE_SECOND_AGO = "秒前";
    public static final String ONE_MINUTE_AGO = "分钟前";
    public static final String ONE_HOUR_AGO = "小时前";
    public static final String ONE_DAY_AGO = "天前";
    public static final String ONE_MONTH_AGO = "月前";
    public static final String ONE_YEAR_AGO = "年前";
    public static final String ONE_UNKNOWN = "未知";

    /**
     * 转换为中文相对时间
     *
     * @param date
     * @return
     */
    public static String formatRelative(Date date) {
        if (null == date) {
            return ONE_UNKNOWN;
        }
        long delta = System.currentTimeMillis() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }
}
