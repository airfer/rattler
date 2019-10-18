package com.airfer.rattler.client;

import com.google.common.collect.ImmutableMap;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午11:39
 */
public class HttpClients {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClients.class);

    private static final String CLIENT_IP;

    static {
        CLIENT_IP = getIpAddress();
    }

    private static String getIpAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("InetAddress.getLocalHost UnknownHostException", e);
            return "127.0.0.1";
        }
    }

    public static String sendGetHttpRequest(Map<String, String> headerMap,
                                            String url,
                                            Integer connectTimeout,
                                            Integer writeTimeout,
                                            Integer readTimeout
    ) {
        OkHttpClient httpClient = OkHttpClientSingleton.INSTANCE.getInstance().newBuilder()
                .connectTimeout(getDefaultValueIfNull(connectTimeout, 10), TimeUnit.SECONDS)
                .writeTimeout(getDefaultValueIfNull(writeTimeout, 10), TimeUnit.SECONDS)
                .readTimeout(getDefaultValueIfNull(readTimeout, 10), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
        Request request = new Request.Builder()
                .headers(Headers.of(headerMap))
                .url(url)
                .get()
                .build();
        try (Response httpResponse = httpClient.newCall(request).execute()) {
            if (httpResponse != null) {
                if (httpResponse.isSuccessful() && httpResponse.code() == 200) {
                    if (httpResponse.body() != null) {
                        String responseStr = new String(httpResponse.body().bytes(), Charset.forName("UTF-8"));
                        LOGGER.debug("http response body: {}", responseStr);
                        return responseStr;
                    } else {
                        LOGGER.error("http response body null");
                    }
                } else {
                    LOGGER.error("http response status code error，statusCode: {}, statusMsg: {}", httpResponse.code(), httpResponse.message());
                }
            } else {
                LOGGER.error("http response null");
            }
        } catch (Exception e) {
            LOGGER.error("http call exception", e);
        }
        return "";
    }

    public static String sendJsonPostHttpRequest(Map<String, String> headerMap,
                                                 String url,
                                                 String jsonBodyStr,
                                                 Integer connectTimeout,
                                                 Integer writeTimeout,
                                                 Integer readTimeout
    ) {
        OkHttpClient httpClient = getOkHttpClient().newBuilder()
                .connectTimeout(getDefaultValueIfNull(connectTimeout, 10), TimeUnit.SECONDS)
                .writeTimeout(getDefaultValueIfNull(writeTimeout, 10), TimeUnit.SECONDS)
                .readTimeout(getDefaultValueIfNull(readTimeout, 10), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        Request request = new Request.Builder()
                .headers(Headers.of(headerMap))
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json"), getDefaultValueIfNull(jsonBodyStr, "")))
                .build();
        try (Response httpResponse = httpClient.newCall(request).execute()) {
            if (httpResponse != null) {
                if (httpResponse.isSuccessful()) {
                    if (httpResponse.body() != null) {
                        String responseStr = new String(httpResponse.body().bytes(), Charset.forName("UTF-8"));
                        LOGGER.debug("http response body: {}", responseStr);
                        return responseStr;
                    } else {
                        LOGGER.error("http response body null");
                    }
                } else {
                    LOGGER.error("http response status code error，statusCode: {}, statusMsg: {}", httpResponse.code(), httpResponse.message());
                }
            } else {
                LOGGER.error("http response null");
            }
        } catch (Exception e) {
            LOGGER.error("http call exception", e);
        }
        return null;
    }

    /**
     * 上传链路信息
     * @param url 上送地址
     * @param body 上送内容
     * @return 返回结果
     */
    public static String uploadCoreChainInfo(String url,String body){
        Map<String,String> headerMap= ImmutableMap.of("Content-Type", "application/json;charset=utf-8");
        return sendJsonPostHttpRequest(headerMap,url,body,null,null,null);
    }


    public static OkHttpClient getOkHttpClient(){
        return OkHttpClientSingleton.INSTANCE.getInstance();
    }
    public static OkHttpClient setOkHttpClient(OkHttpClient client){
        return OkHttpClientSingleton.INSTANCE.setInstance(client);
    }

    private enum OkHttpClientSingleton {
        INSTANCE;

        private OkHttpClient okHttpClient;

        OkHttpClientSingleton() {
            this.okHttpClient = new OkHttpClient().newBuilder()
                    // .addInterceptor(new HttpApplicationInterceptor())
                    // .addNetworkInterceptor(new HttpNetInterceptor())
                    .build();
        }

        private OkHttpClient getInstance() {
            return okHttpClient;
        }

        private OkHttpClient setInstance(OkHttpClient client) {
            return okHttpClient = client;
        }
    }

    public static String getClientIp() {
        return CLIENT_IP;
    }

    private static byte[] combineBytes(byte[] front, byte[] rear) {
        byte[] result = new byte[front.length + rear.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = i < front.length ? front[i] : rear[i - front.length];
        }
        return result;
    }

    private static String formatToYYYYMMDDHHMMSS(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(formatter);
    }

    private static <T> T getDefaultValueIfNull(T t, T defaultValue) {
        return Optional.ofNullable(t).orElse(defaultValue);
    }

}
