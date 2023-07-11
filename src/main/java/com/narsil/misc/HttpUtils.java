package com.narsil.misc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * http / https connection toolkit
 * <p>
 * supports GET, POST method
 *
 * @author iamnarsil
 * @version 20230711
 * @since 20230328
 */
public class HttpUtils {

    private static final Logger LOGGER = Logger.getLogger("HttpUtils");

    public static final int STATUS_CODE_UNKNOWN = 0;
    public static final int STATUS_CODE_CONNECTION_REFUSED = -1;
    public static final int STATUS_CODE_CONNECTION_TIMEOUT = -999;

    private static final String SYMBOL_DIRECT = " -> ";
    private static final String SYMBOL_WITH_PROXY = " +> ";

    private int statusCode = STATUS_CODE_UNKNOWN;
    private final String symbol;

    private final String url;
    private final Map<String, String> header;

    // unit: ms
    private final int maxConnectTimeout;
    private final int maxRetryTimes;

    // proxy setting
    private final boolean useProxy;
    private final String proxyServerProtocol;
    private final String proxyServerAddress;
    private final int proxyServerPort;

    // print
    private final boolean enablePrinting;

    // common trust manager (reduce I/O operation)
    private static final TrustManager trustManager;

    static {
        trustManager = new _CustomizedTrustManager().getTrustManager();
    }

    public HttpUtils(Builder builder) {

        this.url = builder.url;
        this.header = builder.header;
        this.maxConnectTimeout = builder.maxConnectTimeout;
        this.maxRetryTimes = builder.maxRetryTimes;
        this.useProxy = builder.useProxy;
        this.proxyServerProtocol = builder.proxyServerProtocol;
        this.proxyServerAddress = builder.proxyServerAddress;
        this.proxyServerPort = builder.proxyServerPort;
        this.enablePrinting = builder.enablePrinting;

        symbol = this.useProxy ? SYMBOL_WITH_PROXY : SYMBOL_DIRECT;
    }

    /**
     * get data
     *
     * @return response data
     */
    public Tuple.Pair<Integer, String> get() {

        String content = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            HttpGet httpGet = new HttpGet(url);

            if (header != null) {
                if (!header.containsKey("Accept")) {
                    httpGet.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
                }

                // add header
                for (Map.Entry<String, String> entry : header.entrySet()) {

                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (!key.trim().isEmpty()) {
                        httpGet.addHeader(key, value);
                    }
                }
            }

            // use "connection: close" to avoid SocketException (caused by too many open files) @ performance test
            // httpGet.setHeader("Connection", "close");
            if (enablePrinting) {
                LOGGER.info("\nHTTP REQ " + symbol + httpGet.getRequestLine());
            }

            httpClient = configHttpClient();
            httpResponse = httpClient.execute(httpGet);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity());
            }

        } catch (HttpResponseException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = e.getStatusCode();

        } catch (HttpHostConnectException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = STATUS_CODE_CONNECTION_REFUSED;

        } catch (HttpConnectTimeoutException | SocketTimeoutException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = STATUS_CODE_CONNECTION_TIMEOUT;

        } catch (Exception e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());

        } finally {
            if (enablePrinting) {
                LOGGER.info("\nHTTP RESP" + symbol + "(" + statusCode + ")\nHTTP RESP" + symbol + content);
            }

            try {
                if (httpClient != null) {
                    httpClient.close();
                }

                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        return Tuple.collect(statusCode, content);
    }

    /**
     * post JSON data
     *
     * @param json json
     * @return response data
     */
    public Tuple.Pair<Integer, String> postJson(String json) {
        return post(new StringEntity(json, StandardCharsets.UTF_8),
                ContentType.APPLICATION_JSON.toString());
    }

    /**
     * post text data
     *
     * @param text text
     * @return response data
     */
    public Tuple.Pair<Integer, String> postText(String text) {
        return post(new StringEntity(text, StandardCharsets.UTF_8),
                ContentType.TEXT_PLAIN.toString());
    }

    /**
     * post form data
     *
     * @param form form
     * @return response data
     */
    public Tuple.Pair<Integer, String> postForm(List<NameValuePair> form) {
        return post(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8),
                ContentType.APPLICATION_FORM_URLENCODED.toString());
    }

    /**
     * post file
     *
     * @param file file
     * @return response data
     */
    public Tuple.Pair<Integer, String> postFile(File file) {

        FormBodyPart formBodyPart = FormBodyPartBuilder.create("file", new FileBody(file)).build();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart(formBodyPart);
        builder.setContentType(ContentType.MULTIPART_FORM_DATA.withCharset(StandardCharsets.UTF_8));
        HttpEntity httpEntity = builder.build();

        return post(httpEntity, null);
    }

    /**
     * post data
     *
     * @param entity http entity
     * @param contentType http content type
     * @return response data
     */
    private Tuple.Pair<Integer, String> post(HttpEntity entity, String contentType) {

        String content = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            if (entity instanceof StringEntity) {
                httpPost.setHeader("Content-Type", contentType);
            }

            if (header != null) {
                if (!header.containsKey("Accept")) {
                    httpPost.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
                }

                // add header
                for (Map.Entry<String, String> entry : header.entrySet()) {

                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (!key.trim().isEmpty()) {
                        httpPost.addHeader(key, value);
                    }
                }
            }

            // use "connection: close" to avoid SocketException (caused by too many open files) @ performance test
            // httpPost.setHeader("Connection", "close");
            if (enablePrinting) {
                LOGGER.info("\nHTTP REQ " + symbol + httpPost.getRequestLine() + "\nHTTP REQ " + symbol + EntityUtils.toString(entity));
            }

            httpClient = configHttpClient();
            httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            content = EntityUtils.toString(httpResponse.getEntity());

        } catch (HttpResponseException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = e.getStatusCode();

        } catch (HttpHostConnectException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = STATUS_CODE_CONNECTION_REFUSED;

        } catch (HttpConnectTimeoutException | SocketTimeoutException e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());
            statusCode = STATUS_CODE_CONNECTION_TIMEOUT;

        } catch (Exception e) {
            LOGGER.severe("[" + e.getClass().getName() + "]:: " + e.getMessage());

        } finally {
            if (enablePrinting) {
                LOGGER.info("\nHTTP RESP" + symbol + "(" + statusCode + ")\nHTTP RESP" + symbol + content);
            }

            try {
                if (httpClient != null) {
                    httpClient.close();
                }

                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        return Tuple.collect(statusCode, content);
    }

    private CloseableHttpClient configHttpClient() throws NoSuchAlgorithmException, KeyManagementException {

        // get route planner
        HttpRoutePlanner routePlanner = configHttpRoutePlanner();

        // get request configuration
        RequestConfig requestConfig = configRequestConfig();

        // get SSL connection socket factory
        SSLConnectionSocketFactory connectionFactory = configSSLConnectionSocketFactory();

//        // get interceptor
//        HttpResponseInterceptor interceptor = configHttpResponseInterceptor();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(connectionFactory)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(maxRetryTimes, true))
                .setRoutePlanner(routePlanner)
//                .addInterceptorLast(interceptor)
                .build();
    }

    private HttpRoutePlanner configHttpRoutePlanner() {

        // 20220902 added: For HTTPS over proxy
        // instead of DefaultProxyRoutePlanner, use HttpRoutePlanner with customized route logic
        return (target, request, context) -> {

            HttpHost proxy = useProxy ? new HttpHost(proxyServerAddress, proxyServerPort, proxyServerProtocol) : null;
            boolean isSecure = "https".equalsIgnoreCase(target.getSchemeName());
            RouteInfo.TunnelType tunnelType = useProxy ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN;
            RouteInfo.LayerType layerType = "https".equalsIgnoreCase(target.getSchemeName()) ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN;

            return new HttpRoute(target, null, proxy, isSecure, tunnelType, layerType);
        };
    }

    private RequestConfig configRequestConfig() {

        // add default request configuration (timeout + proxy setting)
        RequestConfig.Builder rcb = RequestConfig.custom()
                .setConnectTimeout(maxConnectTimeout)
                .setConnectionRequestTimeout(maxConnectTimeout)
                .setSocketTimeout(maxConnectTimeout);

        return rcb.build();
    }

    private SSLConnectionSocketFactory configSSLConnectionSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        sslContext.init(new KeyManager[0], new TrustManager[] { trustManager }, new SecureRandom());

        SSLContext.setDefault(sslContext);
        HostnameVerifier hostnameVerifier = (hostname, session) -> true;

        return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
    }

//    private HttpResponseInterceptor configHttpResponseInterceptor() {
//
//        return (response, context) -> {
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                throw new IOException("unexpected HTTP status code (" + statusCode + ")");
//            }
//        };
//    }

    public boolean isSuccessful() {
        return isStatusSuccessful(statusCode);
    }

    public static boolean isStatusSuccessful(int statusCode) {
        // http status 2XX series
        return statusCode >= 200 && statusCode <= 299;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public int getMaxConnectTimeout() {
        return maxConnectTimeout;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public String getProxyServerProtocol() {
        return proxyServerProtocol;
    }

    public String getProxyServerAddress() {
        return proxyServerAddress;
    }

    public int getProxyServerPort() {
        return proxyServerPort;
    }

    public boolean isEnablePrinting() {
        return enablePrinting;
    }

    public static class Builder {

        private String url;
        private Map<String, String> header;

        // unit: ms
        private int maxConnectTimeout = 30000;
        private int maxRetryTimes = 0;

        // proxy setting
        private boolean useProxy = false;
        private String proxyServerProtocol = "http";
        private String proxyServerAddress = "0.0.0.0";
        private int proxyServerPort = 8080;

        // print (default: true)
        private boolean enablePrinting = true;

        public Builder() {
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setHeader(Map<String, String> header) {
            this.header = header;
            return this;
        }

        public Builder setMaxConnectTimeout(int maxConnectTimeout) {
            this.maxConnectTimeout = maxConnectTimeout;
            return this;
        }

        public Builder setMaxRetryTimes(int maxRetryTimes) {
            this.maxRetryTimes = maxRetryTimes;
            return this;
        }

        public Builder setUseProxy(boolean useProxy) {
            this.useProxy = useProxy;
            return this;
        }

        public Builder setProxyServerProtocol(String proxyServerProtocol) {
            this.proxyServerProtocol = proxyServerProtocol;
            return this;
        }

        public Builder setProxyServerAddress(String proxyServerAddress) {
            this.proxyServerAddress = proxyServerAddress;
            return this;
        }

        public Builder setProxyServerPort(int proxyServerPort) {
            this.proxyServerPort = proxyServerPort;
            return this;
        }

        public Builder setEnablePrinting(boolean enablePrinting) {
            this.enablePrinting = enablePrinting;
            return this;
        }

        public HttpUtils build() {
            return new HttpUtils(this);
        }
    }

    public static class _CustomizedTrustManager {

        private static final String KEY_STORE_CUSTOMIZED = "customized_keystore.jks";
        private static final String KEY_STORE_NATIVE = System.getProperty("java.home")
                + File.separator + "lib"
                + File.separator + "security"
                + File.separator + "cacerts";

        private int option = 0;
        private X509TrustManager trustManager = null;

        public _CustomizedTrustManager() {
            init();
        }

        public _CustomizedTrustManager(int option) {
            this.option = option;
            init();
        }

        private void init() {

            File file = null;

            switch (option) {
                case 1 -> file = new File(KEY_STORE_NATIVE);
                case 2 -> {
                    URL url = _CustomizedTrustManager.class.getClassLoader().getResource(KEY_STORE_CUSTOMIZED);
                    if (url != null) {
                        try {
                            file = new File(url.toURI());
                        } catch (Exception e) {
                            LOGGER.severe(e.getMessage());
                        }
                    }
                }
                default -> {
                    trustManager = new EmptyTrustManager();
                    return;
                }
            }

            if (file != null) {

                try (FileInputStream fis = new FileInputStream(file)) {

                    String pa55w012d = "changeit";
                    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                    ks.load(fis, pa55w012d.toCharArray());

                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);
                    trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

                } catch (Exception e) {
                    LOGGER.severe(e.getMessage());
                }

            } else {
                trustManager = new EmptyTrustManager();
            }
        }

        public X509TrustManager getTrustManager() {
            return trustManager;
        }

        private static class EmptyTrustManager implements X509TrustManager {

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }
    }
}
