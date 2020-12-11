package com.example.myread;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerConnect {
    public static class Response {
        public Response(boolean successful, String response, String responseString){
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

        public ServerCall(OkHttpClient client, Request request)
        {
            this.client = client;
            this.request = request;
        }

        @Override
        public Response call() throws Exception {
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
                url = new URL("https://10.0.2.2:2048" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            OkHttpClient client = getUnsafeOkHttpClient();

            final Request request = new Request.Builder().url(url).post(body).build();

            ExecutorService e = newFixedThreadPool(1);
            Response r = (Response) e.submit(new ServerCall(client, request)).get();
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
                url = new URL("https://10.0.2.2:2048" + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert  url != null;

            OkHttpClient client = getUnsafeOkHttpClient();

            final Request request = new Request.Builder().url(url).build();
            ExecutorService e = newFixedThreadPool(1);
            Response r = (Response) e.submit(new ServerCall(client, request)).get();
            return r;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    public static User getUser(String name) throws JSONException {
        Response response = sendGet("/user/" + name);
        JSONArray jsonArray = new JSONArray();
        if (response.successful) {
            User user = new User(name);
            try {
                Response r = getBookCollections(user.name);
                jsonArray = new JSONArray(r.responseString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    user.addBookCollection(new BookCollection(jsonArray.getJSONObject(i).getString("name")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                int i = 0;

                for (BookCollection bc : user.getCollectionList()) {
                    System.out.println("i:" + i);

                    String book_id = "";
                    JSONArray bookArray = jsonArray.getJSONObject(i).getJSONArray("books");
                    for (int b = 0; b < bookArray.length(); b++) {
                        System.out.println("b:" + b);
                        String object = bookArray.get(b).toString();
                        System.out.println(object);
                    }
//                    String bookid = jsonArray["books"][0];
                    if (!book_id.isEmpty()) {
                        bc.addBook(getBook(book_id));

                    }
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Book getBook(String id) {
        Response response = sendGet("/book/" + id);
        JSONObject jsonObject = null;
        List<String> subjects = new ArrayList<>();
        Book book = null;
        try {
             jsonObject = new JSONObject(response.responseString);
             book = new Book(jsonObject.getString("title"), jsonObject.getString("author"), "" , jsonObject.getString("description"), subjects, jsonObject.getString("publishDate"), jsonObject.getString("authorWiki"), jsonObject.getString("isbn"), jsonObject.getString("rating"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return book;
    }

    public static Response getBookList(String id) {
        return sendGet("/booklist/" + id);
    }

    public static Response getBookCollections(String name) {
        return sendGet("/user/" + name + "/book_collections");
    }

    public static Response addBookCollectionServer(String name, String collection_name) {
        return sendGet("/user/" + name + "/add_book_collection/" + collection_name);
    }

    public static Response addBookToCollectionServer(String name, String collection_name, String book_id){
        return sendGet("/user/" + name + "/add_book_to_collection/" + collection_name + "/" + book_id);
    }

//    public static Response postBookCollection(String name, RequestBody body) {
//        return sendPost("/user/" + name + "/add_book_collection", body);
//    }


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

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
