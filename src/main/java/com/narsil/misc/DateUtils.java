package com.narsil.misc;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * date operation toolkit
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class DateUtils {

    private static final Logger LOGGER = Logger.getLogger("DateUtils");

    public enum TimeUnit {
        YEAR, MONTH, DAY, HOUR
    }

    public static final String DATETIME_PATTERN_DEFAULT = "yyyy/MM/dd HH:mm:ss";

    /**
     * calculate date by days
     *
     * @param date base date
     * @param days days (can be negative)
     * @return result date
     */
    public static Date calculateDate(Date date, int days) {
        return calculateDate(date, TimeUnit.DAY, days);
    }

    /**
     * calculate date by specifying time unit
     *
     * @param date base date
     * @param timeUnit time unit can be YEAR, MONTH, DAY, HOUR
     * @param num number of unit (can be negative)
     * @return result date
     */
    public static Date calculateDate(Date date, TimeUnit timeUnit, int num) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (timeUnit) {
            case YEAR -> c.add(Calendar.YEAR, num);
            case MONTH -> c.add(Calendar.MONTH, num);
            case DAY -> c.add(Calendar.DATE, num);
            case HOUR -> c.add(Calendar.HOUR_OF_DAY, num);
        }

        return c.getTime();
    }

    /**
     * get current local date
     *
     * @return current local date
     */
    public static Date getLocalDateNow() {

        LocalDateTime ldt = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * convert date to string (default: yyyy/MM/dd HH:mm:ss)
     *
     * @param date date
     * @return date string
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATETIME_PATTERN_DEFAULT);
    }

    /**
     * convert date to string
     *
     * @param date date
     * @param pattern specified date format pattern, default is yyyy/MM/dd HH:mm:ss
     * @return date string
     */
    public static String dateToString(Date date, String pattern) {

        SimpleDateFormat simpleDateFormat;

        if (pattern != null && !pattern.isEmpty()) {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } else {
            simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN_DEFAULT);
        }

        return simpleDateFormat.format(date);
    }

    /**
     * convert string to date (only accept default format: yyyy/MM/dd HH:mm:ss)
     *
     * @param dateString date string
     * @return date
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, DATETIME_PATTERN_DEFAULT);
    }

    /**
     * convert string to date
     *
     * @param dateString date string
     * @param pattern specified date format pattern, default is yyyy/MM/dd HH:mm:ss
     * @return date
     */
    public static Date stringToDate(String dateString, String pattern) {

        SimpleDateFormat simpleDateFormat;

        if (pattern != null && !pattern.isEmpty()) {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } else {
            simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN_DEFAULT);
        }

        try {
            return simpleDateFormat.parse(dateString);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }
}
