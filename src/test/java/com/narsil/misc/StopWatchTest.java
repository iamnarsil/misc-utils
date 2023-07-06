package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StopWatchTest {

    private static final Logger LOGGER = Logger.getLogger("StopWatchTest");

    private String pattern;

    @Before
    public void init() {
        pattern = "#.#####";
    }

    @Test
    public void test00_getPauseTime_A() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000);
            Tuple.Pair<Double, Double> result = stopWatch.pause();
            LOGGER.info("pause time (#.###) = " + result.getA());
        } catch (Exception ignored) {
        }
    }

    @Test
    public void test00_getPauseTime_B() {

        StopWatch stopWatch = new StopWatch(pattern);
        stopWatch.start();
        try {
            Thread.sleep(1000);
            Tuple.Pair<Double, Double> result = stopWatch.pause();
            LOGGER.info("pause time (" + pattern + ") = " + result.getA());
        } catch (Exception ignored) {
        }
    }

    @Test
    public void test01_getTotalTime() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i=0; i<3; i++) {
            try {
                Thread.sleep(1000);
                Tuple.Pair<Double, Double> result = stopWatch.pause();
                LOGGER.info("[" + i + "] (pause, total) = (" + result.getA() + ", " + result.getB() + ")");
            } catch (Exception ignored) {
            }
        }
        LOGGER.info("total time = " + stopWatch.getTotalTime());
    }

    @Test
    public void test02_showPauseResult() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000);
            Tuple.Pair<Double, Double> result = stopWatch.pause();
            LOGGER.info(stopWatch.showPauseResult("pause time"));
        } catch (Exception ignored) {
        }
    }

    @Test
    public void test03_showTotalResult() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i=0; i<3; i++) {
            try {
                Thread.sleep(1000);
                Tuple.Pair<Double, Double> result = stopWatch.pause();
                LOGGER.info(stopWatch.showPauseResult("pause time"));
            } catch (Exception ignored) {
            }
        }
        LOGGER.info(stopWatch.showTotalResult("total time"));
    }

    @Test
    public void test04_reset_A() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000);
            Tuple.Pair<Double, Double> result = stopWatch.pause();
            LOGGER.info("pause time = " + result.getA());
        } catch (Exception ignored) {
        }

        stopWatch.reset();
        LOGGER.info("\n***** after reset *****\n");

        stopWatch.start();
        try {
            Thread.sleep(2000);
            Tuple.Pair<Double, Double> result = stopWatch.pause();
            LOGGER.info("pause time = " + result.getA());
        } catch (Exception ignored) {
        }

        LOGGER.info(stopWatch.showTotalResult("total time"));
    }

    @Test
    public void test04_reset_B() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            for (int i=0; i<9; i++) {
                if (i == 3) {
                    stopWatch.reset();
                    LOGGER.info("\n***** reset *****\n");
                } else if (i == 6) {
                    stopWatch.start();
                    LOGGER.info("\n***** restart *****\n");
                }

                Thread.sleep(1000);

                Tuple.Pair<Double, Double> result = stopWatch.pause();
                LOGGER.info("[" + i + "] (pause, total) = (" + result.getA() + ", " + result.getB() + ")");
            }
        } catch (Exception ignored) {
        }
    }
}