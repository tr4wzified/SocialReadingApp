package com.example.myread;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerConnect extends AppCompatActivity {

    private static ServerConnect s = null;
    private OkHttpClient client = null;

    private ServerConnect()
    {
        client = getUnsafeOkHttpClient();
    }
    //static method to create an instance of the Singleton class
// we can also create a method with the same name as the class name
    public static ServerConnect getInstance()
    {
        if (s == null)
            s = new ServerConnect();
        return s;
    }

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
            } catch (NullPointerException e) {
                return new Response(false, "No body in request", "");
            }
        }
    }

    public Response sendPost(String page, RequestBody body) {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            final Request request = new Request.Builder().url(url).post(body).build();
            ServerCall c = new ServerCall(client, request);
            ExecutorService e = newFixedThreadPool(1);
            return (Response) e.submit(c).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    public Response sendGet(String page) {
        try {
            URL url = null;
            try {
                url = new URL("https://10.0.2.2:2048/" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            final Request request = new Request.Builder().url(url).build();

            ServerCall c = new ServerCall(client, request);
            ExecutorService e = newFixedThreadPool(1);
            return (Response) e.submit(c).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    //TODO: Expand regex
    public Response getBook(String id) {
        return sendGet("book/" + id.replaceAll("[/.]", ""));
    }

    public Response getBookList(String id) {
        return sendGet("booklist/" + id);
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }};

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(3, TimeUnit.SECONDS);

            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(GlobalApplication.getAppContext()));
            builder.cookieJar(cookieJar);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
