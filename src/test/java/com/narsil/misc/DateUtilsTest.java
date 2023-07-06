package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DateUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("DateUtilsTest");

    private int years;
    private int months;
    private int days;
    private int hours;

    private String pattern;

    @Before
    public void init() {

        years = 1;
        months = 2;
        days = 3;
        hours = 4;
        pattern = "yyyy-MM-dd HH:mm:ss.SSS";
    }

    @Test
    public void test00_calculateDate() {
        Date date = new Date();
        LOGGER.info("after " + days + " days = " + DateUtils.calculateDate(date, days));
    }

    @Test
    public void test01_calculateDate() {
        Date date = new Date();
        LOGGER.info("after " + years + " years = " + DateUtils.calculateDate(date, DateUtils.TimeUnit.YEAR, years));
        LOGGER.info("before " + months + " months = " + DateUtils.calculateDate(date, DateUtils.TimeUnit.MONTH, -months));
        LOGGER.info("after " + days + " days = " + DateUtils.calculateDate(date, DateUtils.TimeUnit.DAY, days));
        LOGGER.info("before " + hours + " hours = " + DateUtils.calculateDate(date, DateUtils.TimeUnit.HOUR, -hours));
    }

    @Test
    public void test02_getLocalDateNow() {
        LOGGER.info("local date now = " + DateUtils.getLocalDateNow());
    }

    @Test
    public void test03_dateToString() {
        Date date = new Date();
        LOGGER.info("now = " + DateUtils.dateToString(date));
    }

    @Test
    public void test04_dateToString() {
        Date date = new Date();
        LOGGER.info("now = " + DateUtils.dateToString(date, pattern));
    }

    @Test
    public void test05_stringToDate() {
        String dateString = "2000/01/01 12:34:56";
        LOGGER.info(dateString + " = " + DateUtils.stringToDate(dateString));
    }

    @Test
    public void test06_stringToDate() {
        String dateString = "2000-01-01 12:34:56.789";
        LOGGER.info(dateString + " = " + DateUtils.stringToDate(dateString, pattern));
    }
}