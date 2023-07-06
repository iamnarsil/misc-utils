package com.narsil.misc;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * stop watch
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class StopWatch {

    public static final String PATTERN_3RD_DECIMAL_PLACE = "#.###";

    private String pattern = PATTERN_3RD_DECIMAL_PLACE;
    private long lastPausePoint = 0L;
    private double pauseTime = 0.0f;
    private double totalTime = 0.0f;
    private boolean running = false;

    public StopWatch() {
    }

    public StopWatch(String pattern) {
        this.pattern = pattern;
    }

    public void start() {
        lastPausePoint = System.nanoTime();
        running = true;
    }

    public void reset() {
        lastPausePoint = 0L;
        pauseTime = 0.0f;
        totalTime = 0.0f;
        running = false;
    }

    public Tuple.Pair<Double, Double> pause() {

        if (running) {

            long now = System.nanoTime();

            pauseTime = (double) (now - lastPausePoint) / (double) 1000000000;
            totalTime += pauseTime;

            // normalize
            pauseTime = normalize(pauseTime);
            totalTime = normalize(totalTime);

            lastPausePoint = now;
        }

        return Tuple.collect(pauseTime, totalTime);
    }

    private double normalize(double data) {

        // according pattern to normalize input data
        // ex: default pattern (#.###) will result to 3rd decimal place, ex: 3.14159 -> 3.142
        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Double.parseDouble(df.format(data));
    }

    private String getResult(String topic, double data) {

        String line = " ... ";
        if (topic.length() <= 50) {
            char[] chars = new char[50 - topic.length()];
            Arrays.fill(chars, '.');
            line = (" ").concat(new String(chars)).concat(" ");
        }

        // ex: sample topic 1 ................. 1.234 sec
        // ex: sample topic 2 ................. 5.678 sec
        return topic.concat(line).concat(String.valueOf(data)).concat(" sec ");
    }

    public double getPauseTime() {
        // pause();
        return pauseTime;
    }

    public double getTotalTime() {
        // pause();
        return totalTime;
    }

    public boolean isRunning() {
        return running;
    }

    public String showPauseResult(String topic) {
        // pause();
        return getResult(topic, pauseTime);
    }

    public String showTotalResult(String topic) {
        // pause();
        return getResult(topic, totalTime);
    }
}
