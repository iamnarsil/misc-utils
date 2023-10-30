package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("HttpUtilsTest");

    private String url;
    private String hostname;

    private String url2;
    private String hostname2;

    private String reqJson;
    private File file;
    private int maxConnectionTimeout;

    @Before
    public void init() {

        try {
            url = "https://localhost:8443/spring-boot-sample/hello/test";
            hostname = new URL(url).getHost();

            url2 = "http://localhost:8081/mock/be/collect";
            hostname2 = new URL(url2).getHost();

            reqJson = "{\"aaa\":1,\"bbb\":\"2\"}";
            file = new File("D:\\temp\\123\\456.txt");
            maxConnectionTimeout = 3000;

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    public void test00_get() {

        HttpUtils httpUtils = new HttpUtils.Builder()
                .setUrl(url)
                .setAllowedHostnames(List.of(hostname))
                .setMaxConnectTimeout(maxConnectionTimeout)
                // .setUseProxy(true)
                // .setEnablePrinting(false)
                .build();
        Tuple.Pair<Integer, String> response = httpUtils.get();

        LOGGER.info("\n(" + response.getA() + ") " + response.getB());
    }

    @Test
    public void test01_postJson() {

        HttpUtils httpUtils = new HttpUtils.Builder()
                .setUrl(url)
                .setAllowedHostnames(List.of(hostname))
                .setMaxConnectTimeout(maxConnectionTimeout)
                // .setUseProxy(true)
                // .setEnablePrinting(false)
                .build();
        Tuple.Pair<Integer, String> response = httpUtils.postJson(reqJson);

        LOGGER.info("\n(" + response.getA() + ") " + response.getB());
    }

    @Test
    public void test02_postText() {
    }

    @Test
    public void test03_postForm() {
    }

    @Test
    public void test04_postFile() {

        HttpUtils httpUtils = new HttpUtils.Builder()
                .setUrl(url2)
                .setAllowedHostnames(List.of(hostname2))
                .setMaxConnectTimeout(maxConnectionTimeout)
                // .setUseProxy(true)
                // .setEnablePrinting(false)
                .build();
        Tuple.Pair<Integer, String> response = httpUtils.postFile(file);

        LOGGER.info("\n(" + response.getA() + ") " + response.getB());
    }
}