package com.example.myread;

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
    private OkHttpClient client;

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

    public void initUser(String name) throws JSONException {
        User user = User.getInstance();
        Response response = sendGet("/user/" + name);
        JSONArray jsonArray = new JSONArray();
        if (response.successful) {
            user = User.getInstance();
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
                    JSONArray bookArray = jsonArray.getJSONObject(i).getJSONArray("books");
                    for (int b = 0; b < bookArray.length(); b++) {
                        bc.addBook(getBookByID(bookArray.get(b).toString()));
                        System.out.println(bookArray.get(b).toString());
                    }
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Book getBookByID(String id) {
        Response response = sendGet("book/" + id);
        List<String> subjects = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response.responseString);
            JSONArray subjectsArray = jsonObject.getJSONArray("subjects");
            for (int j = 0; j < subjectsArray.length(); j++) {
                subjects.add(subjectsArray.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return new Book(jsonObject.optString("id", null), jsonObject.optString("title", null), jsonObject.optString("author", null), "", jsonObject.optString("description", null), subjects, jsonObject.optString("publishDate", null), jsonObject.optString("authorWiki", null), jsonObject.optString("isbn", null), jsonObject.optString("rating", null));
    }

    //TODO: Expand regex
    public Book getBook(String id) {
        Response response = sendGet("/Petertje/book/" + id.replaceAll("[/.]", ""));
        JSONObject jsonObject;
        List<String> subjects = new ArrayList<>();
        Book book = null;
//        Book book = new Book("","","","",subjects,"","","","");
        try {
            jsonObject = new JSONObject(response.responseString);
//             book.title = jsonObject.optString("title", null);
//             book.author = jsonObject.optString("author", null);
//             book.cover = "";
//             book.description = jsonObject.optString("description", null);
//             book.subjects = subjects;
//             book.publishDate = jsonObject.optString("publishDate", null);
//             book.authorWiki = jsonObject.optString("authorWiki", null);
//             book.isbn = jsonObject.optString("isbn", null);
//             book.rating = jsonObject.optString("rating", null);
            book = new Book(jsonObject.optString("id", null), jsonObject.optString("title", null), jsonObject.optString("author", null), "", jsonObject.optString("description", null), subjects, jsonObject.optString("publishDate", null), jsonObject.optString("authorWiki", null), jsonObject.optString("isbn", null), jsonObject.optString("rating", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return book;
    }

    public List<Book> getBooks(String bookName) {
        Response response = sendGet("search_book/" + bookName.replaceAll("[/.]", ""));
        JSONArray jsonArray;
        List<Book> books = new ArrayList<>();
        if (response.successful) {
            try {
                System.out.println(response.responseString);
                jsonArray = new JSONArray(response.responseString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    List<String> subjects = new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        JSONArray subjectsArray = jsonObject.getJSONArray("subjects");
                        for (int j = 0; j < subjectsArray.length(); j++) {
                            subjects.add(subjectsArray.getString(j));
                        }
                    } catch (JSONException e) {
                        System.out.println("Json error (possible empty subjects): " + e.getMessage());
                    }

                    Book book = new Book(jsonObject.optString("id", null), jsonObject.optString("title", null), jsonObject.optString("author", null), "", jsonObject.optString("description", null), subjects, jsonObject.optString("publishDate", null), jsonObject.optString("authorWiki", null), jsonObject.optString("isbn", null), jsonObject.optString("rating", null));
                    books.add(book);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return books;
        }
        return null;
    }

    public Response getBookList(String id) {
        return sendGet("booklist/" + id);
    }

    public Response getBookCollections(String name) {
        return sendGet("/user/" + name + "/book_collections");
    }

    public Response addBookCollectionServer(String name, String collection_name) {
        return sendGet("/user/" + name + "/add_book_collection/" + collection_name);
    }

    public Response deleteBookCollectionServer(String name, String collection_name) {
        return sendGet("/user/" + name + "/del_book_collection/" + collection_name);
    }

    public Response addBookToCollectionServer(String name, String collection_name, String book_id) {
        return sendGet("/user/" + name + "/add_book_to_collection/" + collection_name + "/" + book_id);
    }

//    public static Response postBookCollection(String name, RequestBody body) {
//        return sendPost("/user/" + name + "/add_book_collection", body);
//    }


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
