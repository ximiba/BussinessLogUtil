package com.tian.li.util;


import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Pattern;

public class DateUtils {

    /**
     * yyyy
     */
    public static final String YYYY = "yyyy";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYYMMDD_CROSS_HHMMSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYYMMDD_CROSS_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYYMMDDCROSSHHMMSSSSS_NOSYBOL = "yyyyMMddHHmmssSSS";

    /**
     * yyyyMMddhhmmss
     */
    public static final String YYYYMMDDHHMMSS_NOSYBOL = "yyyyMMddHHmmss";

    /**
     * yyyyMMddhhmmss
     */
    public static final String YYYYMMDDHH = "yyyyMMddHH";

    /**
     * yyyy-MM-dd
     */
    public static final String YYYYMMDD_CROSS = "yyyy-MM-dd";
    public static final String YYYYMMDDCROSS = "yyyyMMdd";
    public static final String YYYYMDD_CROSS = "yyyy-M-dd";
    public static final String YYYYMD_CROSS = "yyyy-M-d";
    public static final String YYYYMMD_CROSS = "yyyy-MM-d";

    public static final String YYYYMMCROSS = "yyyyMM";
    public static final String YYYYMM_CROSS = "yyyy-MM";

    /**
     * HH:mm:ss
     */
    public static final String HHMMSS_COLON = "HH:mm:ss";

    /**
     * 月
     */
    public static final String MONTH = "MONTH";

    /**
     * 周
     */
    public static final String WEEK = "WEEK";

    /**
     * 根据格式获取当前时间
     *
     * @param format
     * @return
     */
    public static String getNowDate(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }


    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getNowDateShort() {
        return getNowDate(YYYYMMDD_CROSS);
    }


    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return new Date();
        }
        if (strDate.contains("-")) {
            String format = getDateFormat(strDate);
            return strToDate(strDate, format);
        }

        int strLength = strDate.length();
        switch (strLength) {
            case 4:
                return strToDate(strDate, YYYY);
            case 6:
                return strToDate(strDate, YYYYMMCROSS);
            case 8:
                if (strDate.contains(":")) {
                    return strToDate(strDate, HHMMSS_COLON);
                }
                return strToDate(strDate, YYYYMMDDCROSS);
            case 14:
                return strToDate(strDate, YYYYMMDDHHMMSS_NOSYBOL);
            case 17:
                return strToDate(strDate, YYYYMMDDCROSSHHMMSSSSS_NOSYBOL);
        }
        return getDefaultDate(strDate);
    }

    private static Date getDefaultDate(String strDate) {
        String[] strArr = StringUtils.split(strDate, "-");
        if (strArr[2].length() == 1) {
            return strToDate(strDate, YYYYMD_CROSS);
        }
        if (strArr[1].length() == 1) {
            return strToDate(strDate, YYYYMDD_CROSS);
        }
        return strToDate(strDate, YYYYMMDD_CROSS);
    }

    /**
     * 根据数据长度将string转换成date类型
     *
     * @param strDate
     * @return
     */
    public static Date strToDateByLength(String strDate) {
        if (org.apache.commons.lang.StringUtils.isBlank(strDate)) {
            return null;
        }
        if (strDate.length() == 4) {
            return strToDate(strDate, YYYY);
        }
        if (strDate.length() == 19) {
            return strToDate(strDate, YYYYMMDD_CROSS_HHMMSS);
        }
        if (strDate.length() == 23) {
            return strToDate(strDate, YYYYMMDD_CROSS_HHMMSS_SSS);
        }
        if (strDate.length() == 10) {
            return strToDate(strDate, YYYYMMDD_CROSS);
        }
        if (strDate.length() == 7) {
            return strToDate(strDate, YYYYMM_CROSS);
        }
        if (strDate.length() == 8) {
            return strToDate(strDate, HHMMSS_COLON);
        }
        return strToDate(strDate);
    }

    /**
     * 将时间格式字符串转换为时间
     * 根据具体的时间格式字符串来装换
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate, String format) {
        return Date.from(strToLocalDateTime(strDate, format).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate strToLocalDate(String strDate, String format) {
        return strToLocalDateTime(strDate, format).toLocalDate();
    }

    public static LocalDateTime strToLocalDateTime(String strDate, String format) {
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder().parseCaseInsensitive()
                .append(DateTimeFormatter.ofPattern(format));

        switch (format) {
            case YYYY:
                formatterBuilder.parseDefaulting(ChronoField.MONTH_OF_YEAR, 1);
            case YYYYMMCROSS:
            case YYYYMM_CROSS:
                formatterBuilder.parseDefaulting(ChronoField.DAY_OF_MONTH, 1);
            case YYYYMMD_CROSS:
            case YYYYMDD_CROSS:
            case YYYYMD_CROSS:
            case YYYYMMDD_CROSS:
            case YYYYMMDDCROSS:
                formatterBuilder.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0);
                break;
            case YYYYMMDDCROSSHHMMSSSSS_NOSYBOL:
                // JDK8 DateTimeFormatter 无法解析 yyyyMMddHHmmssSSS
                // 这是jdk8的一个bug，jdk9中修复
                // 这里加一个.就可以识别
                strDate = strDate.substring(0, 14) + "." + strDate.substring(14, 17);
                formatterBuilder = new DateTimeFormatterBuilder().parseCaseInsensitive()
                        .append(DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS"));
                break;
            case HHMMSS_COLON:
                LocalDateTime localDateTime = LocalDateTime.now();
                formatterBuilder.parseDefaulting(ChronoField.YEAR, localDateTime.getYear())
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, localDateTime.getMonthValue())
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, localDateTime.getDayOfMonth())
                ;
                break;
        }
        return LocalDateTime.parse(strDate, formatterBuilder.toFormatter());
    }


    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        return dateToString(new Date(), HHMMSS_COLON);
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMddhhmmss。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        if (sformat == null || "".equals(sformat.trim())) {
            sformat = YYYYMMDDHHMMSS_NOSYBOL;
        }
        return dateToString(new Date(), sformat);
    }


    public static String dateToString(Date target) {
        if (target == null) {
            return "";
        }
        return dateToString(target, YYYYMMDD_CROSS);
    }


    /**
     * date
     *
     * @param target
     * @param format
     * @return
     */
    public static String dateToString(Date target, String format) {
        if (target == null) {
            return "";
        }
        if (StringUtils.isBlank(format)) {
            format = YYYYMMDD_CROSS;
        }
        return target.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        return strToDate(strDate, YYYYMMDD_CROSS_HHMMSS);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        return dateToString(dateDate, YYYYMMDD_CROSS_HHMMSS);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate) {
        return dateToString(dateDate, YYYYMMDD_CROSS);
    }

    public static String dateToStrNoSplit(Date dateDate) {
        return dateToString(dateDate, YYYYMMDDCROSS);
    }


    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:mm"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 两时间只差
     *
     * @param beforeDate 减数
     * @param afterDate  被减数
     * @return
     */
    public static Long diffTimes(Date beforeDate, Date afterDate) {
        //这样得到的差值是微秒级别
        Long diffTime = null;
        try {
            diffTime = beforeDate.getTime() - afterDate.getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return diffTime;
    }

    /**
     * 具体两时间只差
     *
     * @param beforeDate 减数
     * @param afterDate  被减数
     * @param type       (0 得到 days， 1 得到 hours， 2 得到 minutes)
     * @return
     */
    public static Long diffTimes(Date beforeDate, Date afterDate, Integer type) {
        Long diffTime = null;
        try {
            diffTime = DateUtils.diffTimes(beforeDate, afterDate);
            long days = diffTime / (1000 * 60 * 60 * 24);
            long hours = (diffTime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diffTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            switch (type) {
                case 0:
                    diffTime = days;
                    break;
                case 1:
                    diffTime = hours;
                    break;
                case 2:
                    diffTime = minutes;
                    break;
                default:
                    diffTime = minutes;
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return diffTime;
    }

    /**
     * 判断两时间相差天数
     *
     * @param beforeDay "2017-05-10 16:36:30"
     * @param afterDay  "2017-05-19 16:02:30"
     * @return 9
     */
    public static int differentDays(Date beforeDay, Date afterDay) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beforeDay);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(afterDay);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) { //不同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366;
                } else { //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else { //同年
            return day2 - day1;
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        long day;
        try {
            Date date = strToDate(sj1, YYYYMMDD_CROSS);
            Date mydate = strToDate(sj2, YYYYMMDD_CROSS);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 时间前推或后推分钟,其中minutes表示分钟.
     */
    public static String getPreTime(String dateStr, String minutes) {
        String mydate1;
        try {
            Date date1 = strToDate(dateStr, YYYYMMDD_CROSS_HHMMSS);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(minutes) * 60;
            date1.setTime(Time * 1000);
            mydate1 = dateToString(date1, YYYYMMDD_CROSS_HHMMSS);
        } catch (Exception e) {
            return "";
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, int delay) {
        try {
            String mdate;
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = dateToString(d, YYYYMMDD_CROSS);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     *
     */
    public static String getPreviousDayYYMMDD() {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern(YYYYMMDDCROSS));
    }

    /**
     *
     */
    public static String getCurrentDayYYMMDD() {
        LocalDate localDate = LocalDate.now();
        return localDate.format(DateTimeFormatter.ofPattern(YYYYMMDDCROSS));
    }

    /**
     * 得到一个时间延后或前移几天的时间
     *
     * @param nowdate
     * @param delay   前移或后延的天数
     * @return
     */
    public static Date getNextDay(Date nowdate, int delay) {
        try {
            String dateStr = DateUtils.dateToStrLong(nowdate);
            dateStr = DateUtils.getNextDay(dateStr, delay);
            nowdate = DateUtils.strToDate(dateStr);
        } catch (Exception e) {
            return null;
        }

        return nowdate;
    }

    /**
     * 得到一个时间延后或前移几个月的时间
     *
     * @param date
     * @param delay
     * @return
     */
    public static String getDelayMonth(Date date, int delay) {
        Date formNow3Month = getDelayMonthToDate(date, delay);
        return dateToString(formNow3Month, YYYYMMDD_CROSS);
    }

    /**
     * 得到一个时间延后或前移几个月的时间
     *
     * @param date
     * @param delay
     * @return
     */
    public static Date getDelayMonthToDate(Date date, int delay) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) calendar.setTime(date);
        calendar.add(Calendar.MONTH, delay); // 得到前3个月 -3
        return calendar.getTime();
    }

    /**
     * 得到一个时间延后或前移几个月的时间
     *
     * @param strDate
     * @param delay
     * @return
     */
    public static String getDelayMonth(String strDate, int delay) {
        Date date = null;
        if (strDate == null || "".equals(strDate.trim())) {
            date = new Date();
        } else {
            date = strToDateLong(strDate);
        }
        return getDelayMonth(date, delay);
    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {
        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat yyyy-MM-dd
     * @return
     */
    public static String getEndDateOfMonth(String dat) {
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

    /**
     * 获得一个日期所在的周的星期几的日期，如要找出2017年2月17日所在周的星期一是几号
     *
     * @param sdate
     * @param num   （0 周日 1 周一）
     * @return
     */
    public static String getWeek(String sdate, String num) {
        // 再转换为时间
        Date dd = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) // 返回星期一所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        else if (num.equals("2")) // 返回星期二所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        else if (num.equals("3")) // 返回星期三所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        else if (num.equals("4")) // 返回星期四所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        else if (num.equals("5")) // 返回星期五所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        else if (num.equals("6")) // 返回星期六所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        else if (num.equals("0")) // 返回星期日所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return dateToString(c.getTime(), YYYYMMDD_CROSS);
    }

    /**
     * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
     *
     * @param k 表示是取几位随机数，可以自己定
     */

    public static String getNo(int k) {
        return getUserDate(YYYYMMDDHHMMSS_NOSYBOL) + getRandom(k);
    }

    /**
     * 返回一个随机数
     *
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();
        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     * 根据ymd维度获取当天或当月或当年开始时间
     * 例:ymd==DAY，今天时间为2017/2/12 返回的时间为 2017/2/12 00:00:00
     * ymd==MONTH，今天时间为2017/2/28 返回的时间为 2017/2/1 00:00:00
     * ymd==YEAR，今天时间为2017/2/28 返回的时间为 2017/1/1 00:00:00
     *
     * @param ymd DAY:按天获取 MONTH:按月获取 YEAR:按年获取
     * @return Date
     * @author xiaosisi on 2017/2/28
     */
    public static Date getStartDateByYMD(Date date, String ymd) {
        Date newDate = null;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        //当天开始时间
        if ("DAY".equals(ymd)) {
            calendar.set(year, month, day, 0, 0, 0);
        }
        //本周第一天
        if ("WEEK".equals(ymd)) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 0, 0, 0);
        }
        //本月
        if ("MONTH".equals(ymd)) {
            calendar.set(year, month, 1, 0, 0, 0);
        }
        //当年的第一天
        if ("YEAR".equals(ymd)) {
            calendar.set(year, 0, 1, 0, 0, 0);
        }
        newDate = calendar.getTime();
        return newDate;
    }

    /**
     * 根据ymd维度获取当天或当月或当年结束时间
     * 例:ymd==DAY，今天时间为2017/2/12 返回的时间为 2017/2/12 23:59:59
     * ymd==MONTH，今天时间为2017/2/12 返回的时间为 2017/2/28 23:59:59
     * ymd==YEAR，今天时间为2017/2/12 返回的时间为 2017/12/31 23:59:59
     *
     * @param date 如果传值，则获取date对应的结束时间
     * @param ymd  DAY:按天获取 MONTH:按月获取 YEAR:按年获取
     * @return Date
     * @author xiaosisi on 2017/2/28
     */
    public static Date getLastDateByYmdType(Date date, String ymd) {
        Date newDate;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        if ("DAY".equals(ymd)) {
            calendar.set(year, month, day, 23, 59, 59);
        }
        //获取当前时间所在周的周日
        if ("WEEK".endsWith(ymd)) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 23, 59, 59);
        }
        if ("MONTH".equals(ymd)) {
            calendar.set(year, month, 1);
            int maxDate = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(year, month, maxDate, 23, 59, 59);
        }
        if ("YEAR".equals(ymd)) {
            calendar.set(year, 11, 1);
            int maxDate = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(year, 11, maxDate, 23, 59, 59);
        }
        newDate = calendar.getTime();
        return newDate;
    }



    public static Date strToSpecialDate(String str) {
        Date date = null;
        try {
            date = strToDate(str, "yyyy-MM-dd HH-mm-ss");
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }

    public static Date SpecialStrToDate(String str) {
        Date date;
        try {
            date = strToDate(str, "yyyy-MM-dd");
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }

    public static Date SpeStrToDate(String str) {

        Date date;
        try {
            date = strToDate(str, "yyyy-MM");
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }


    public static String dateToSpecialStr(Date d) {
        return dateToString(d, "yyyy-MM-dd HH-mm-ss");

    }


    /**
     * @param date1 日期1
     * @param date2 日期2
     * @return
     * @title: dateCompare
     * @description: 比较日期大小
     */
    public static int dateCompare(Date date1, Date date2) {
        return date1.compareTo(date2);
    }


    /**
     * 获取指定的年份
     *
     * @param type
     * @return
     */
    public static String getYear(int type) {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.YEAR) - type;
        return String.valueOf(i);
    }

    /**
     * 得到一个时间延后或前移几个月的时间
     *
     * @param date
     * @param delay
     * @return
     */
    public static String getMoveMonth(Date date, int delay) {
        Date formNow3Month = getDelayMonthToDate(date, delay);
        return dateToString(formNow3Month, YYYYMMCROSS);
    }


    public static String getFirstDayOfMonth(String str) {
        int year = Integer.parseInt(str.split("-")[0]);  //年
        int month = Integer.parseInt(str.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return dateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getFirstDayYear(String str) {
        int year = Integer.parseInt(str.split("-")[0]);  //年
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, 0);
        //设置日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return dateToString(cal.getTime(), "yyyy-MM-dd");
    }

    public static String getFirstDayYearYYMM(String str) {
        int year = Integer.parseInt(str.substring(0, 4));  //年
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, 0);
        //设置日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return dateToString(cal.getTime(), "yyyyMM");
    }

    /**
     * 根据一个年月，返回当月最后一天的日期
     *
     * @param yearMonth yyyy-MM
     * @return yyyy-MM-dd
     */
    public static String getLastDayOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        return dateToString(cal.getTime(), "yyyy-MM-dd");
    }


    /**
     * 常规自动日期格式识别
     *
     * @param str 时间字符串
     * @return Date
     * @author dc
     */
    private static String getDateFormat(String str) {
        boolean year = false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(str.substring(0, 4)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (!year) {
            if (str.contains("月") || str.contains("-") || str.contains("/")) {
                if (Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            } else {
                index = 3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if (Character.isDigit(chr)) {
                if (index == 0) {
                    sb.append("y");
                }
                if (index == 1) {
                    sb.append("M");
                }
                if (index == 2) {
                    sb.append("d");
                }
                if (index == 3) {
                    sb.append("H");
                }
                if (index == 4) {
                    sb.append("m");
                }
                if (index == 5) {
                    sb.append("s");
                }
                if (index == 6) {
                    sb.append("S");
                }
            } else {
                if (i > 0) {
                    char lastChar = str.charAt(i - 1);
                    if (Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }
}
