package com.example.myread;

import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerConnect {
    public static class Response {
        public Response(boolean successful, String response, String responseString) {
            this.successful = successful;
            this.response = response;
            this.responseString = responseString;
        }

        public boolean successful;
        public String response;
        public String responseString;
    }

    public static class ServerCall implements Callable {
        private final OkHttpClient client;
        private final Request request;

        public ServerCall(OkHttpClient client, Request request) {
            this.client = client;
            this.request = request;
        }

        @Override
        public Response call() {
            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return new Response(true, response.toString(), response.body().string());
                } else {
                    return new Response(false, response.toString(), response.body().string());
                }
            } catch (IOException e) {
                return new Response(false, "Unable to reach server", "");
            }
        }
    }

    public static Response sendPost(String page, RequestBody body) {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            OkHttpClient client = getUnsafeOkHttpClient();
            final Request request = new Request.Builder().url(url).post(body).build();
            ServerCall c = new ServerCall(client, request);
            ExecutorService e = newFixedThreadPool(1);
            Response r = (Response) e.submit(c).get();
            return r;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    public static Response sendGet(String page) {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            OkHttpClient client = getUnsafeOkHttpClient();

            final Request request = new Request.Builder().url(url).build();

            ServerCall c = new ServerCall(client, request);
            ExecutorService e = newFixedThreadPool(1);
            Response r = (Response) e.submit(c).get();
            return r;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    public static Response getBook(String id) {
        return sendGet("book/" + id);
    }

    public static Response getBookList(String id) {
        return sendGet("booklist/" + id);
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) { }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) { }
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }}};

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(3, TimeUnit.SECONDS);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
