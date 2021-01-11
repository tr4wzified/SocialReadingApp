package com.example.myread;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.JsonReader;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerConnect extends AppCompatActivity {

    private static ServerConnect s = null;
    private final OkHttpClient client;
    private final String ip = GlobalApplication.getAppContext().getString(R.string.altaltip);
    private final SharedPreferences prf = GlobalApplication.getEncryptedSharedPreferences();

    private ServerConnect() {
        client = getUnsafeOkHttpClient();
    }

    //static method to create an instance of the Singleton class
// we can also create a method with the same name as the class name
    public static ServerConnect getInstance() {
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

        public final boolean successful;
        public final String response;
        public final String responseString;
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
                System.out.println("Response: " + response.toString());
                if (response.isSuccessful())
                    return new Response(true, response.toString(), response.body().string());
                return new Response(false, response.toString(), response.body().string());

            } catch (IOException e) {
                e.printStackTrace();
                return new Response(false, "Unable to reach server", "");
            } catch (NullPointerException e) {
                return new Response(false, "No body in request", "");
            }
        }
    }

    public boolean checkSession() {
        Response response = sendGet("user/" + prf.getString("username", ""));
        if (response.successful) return true;
        System.out.println("Session expired");
        return false;
    }

    public Response sendRequest(String page, RequestBody body) {
        try {
            URL url = null;
            try {
                url = new URL(ip + page);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;

            final Request.Builder request = new Request.Builder().url(url);
            if (body != null) request.post(body);

            ServerCall c = new ServerCall(client, request.build());
            ExecutorService e = newFixedThreadPool(1);
            return (Response) e.submit(c).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(false, "", "");
    }

    public Response sendPost(String page, RequestBody body) {
        return sendRequest(page, body);
    }

    public Response sendGet(String page) {
        return sendRequest(page, null);
    }

    public JSONArray loadBookCollections(User user) {
        Response r = getBookCollections(user.name);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(r.responseString);

            if (user.getCollectionList().size() != 0) user.getCollectionList().clear();

            for (int i = 0; i < jsonArray.length(); i++)
                user.initBookCollection(new BookCollection(jsonArray.getJSONObject(i).getString("name")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public void loadBooks(User user, JSONArray jsonArray) {
        try {
            for (int i = 0; i < user.getCollectionList().size(); i++) {
                BookCollection bc = user.getCollectionList().get(i);
                if (bc.getBookList().size() != 0) bc.getBookList().clear();

                JSONArray bookArray = jsonArray.getJSONObject(i).getJSONArray("books");

                for (int b = 0; b < bookArray.length(); b++) {
                    String bookString = bookArray.get(b).toString();
                    Book book = getBookByID(bookString);
                    if (!(book == null))
                        bc.initBook(book);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void initUser(String name) {
        User user = User.getInstance();
        user.name = name;
        Response response = sendGet("user/" + name);
        JSONArray jsonArray = new JSONArray();
        if (response.successful) {

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    loadBooks(user, loadBookCollections((user)));
                }
            });

            thread.start();
        }
    }

    public Book getBookByID(String id) {
        JSONObject jsonObject;
        Response response = sendGet("book/" + id);
        if (response.successful) {
            try {
                jsonObject = new JSONObject(response.responseString);
                return new Book(jsonObject.optString("id", ""), jsonObject.optString("title", ""), jsonObject.optString("author", ""), jsonObject.optString("cover_img_large", ""), jsonObject.optString("cover_img_small", ""), jsonObject.optString("cover_img_medium", ""), jsonObject.optString("description", ""), getSubjects(jsonObject), jsonObject.optString("publish_date", ""), jsonObject.optString("book_wiki", ""), jsonObject.optString("isbn", ""), jsonObject.optString("rating", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Response not successful");
        return null;
    }

    private List<String> getSubjects(JSONObject jsonObject) {
        JSONArray subjectsArray;
        List<String> subjects = new ArrayList<>();
        try {
            if (jsonObject.toString().contains("subjects")) {
                if (!jsonObject.get("subjects").equals("")) {
                    subjectsArray = jsonObject.getJSONArray("subjects");
                    for (int j = 0; j < subjectsArray.length(); j++)
                        subjects.add(subjectsArray.getString(j));
                    return subjects;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Book> getBooks(String bookName) {
        Response response = sendGet("search_book/" + bookName.replaceAll("[/.]", ""));
        List<Book> books = new ArrayList<>();
        if (response.successful)
            try {
                JSONArray jsonArray = new JSONArray(response.responseString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Book book = new Book(jsonObject.optString("id", ""), jsonObject.optString("title", ""), jsonObject.optString("author", ""), jsonObject.optString("cover_img_large", ""), jsonObject.optString("cover_img_small", ""), jsonObject.optString("cover_img_medium", ""), jsonObject.optString("description", ""), getSubjects(jsonObject), jsonObject.optString("publish_date", ""), jsonObject.optString("book_wiki", ""), jsonObject.optString("isbn", ""), jsonObject.optString("rating", ""));
                    books.add(book);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return books;
    }

    public Response getBookCollections(String name) {
        return sendGet("user/" + name + "/book_collections");
    }

    public Response addBookCollectionServer(String name, String collection_name) {
        return sendGet("user/" + name + "/add_book_collection/" + collection_name);
    }

    public Response deleteBookCollectionServer(String name, String collection_name) {
        return sendGet("user/" + name + "/del_book_collection/" + collection_name);
    }

    public Response addBookToCollectionServer(String name, String collection_name, String book_id) {
        return sendGet("user/" + name + "/add_book_to_collection/" + collection_name + "/" + book_id);
    }

    public Response deleteBookFromCollectionServer(String name, String collection_name, String book_id) {
        return sendGet("user/" + name + "/del_book_from_collection/" + collection_name + "/" + book_id);
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
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
            builder.readTimeout(60, TimeUnit.SECONDS);

            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(GlobalApplication.getAppContext()));
            builder.cookieJar(cookieJar);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
