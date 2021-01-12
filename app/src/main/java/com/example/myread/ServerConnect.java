package com.example.myread;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

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
    private final String ip = GlobalApplication.getAppContext().getString(R.string.ip);
    private final SharedPreferences prf = GlobalFunctions.getEncryptedSharedPreferences();

    private ServerConnect() {
        client = getUnsafeOkHttpClient();
    }

    /**
     * A function that creates a ServerConnect instance if none exists and will return the instance if one already exists.
     * This way only one instance can be made.
     * @return the ServerConnect instance.
     */
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

        /**
         * A function that sends a request to the server and checks the response.
         * @return a response object.
         */
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

    /**
     * A function that checks if the user still has a session.
     * @return true or false.
     */
    public boolean checkSession() {
        Response response = sendGet("user/" + prf.getString("username", ""));
        if (response.successful) return true;
        System.out.println("Session expired");
        return false;
    }

    /**
     * A function that sends a request to the server.
     * @param page the page to which the request will be sent.
     * @param body an optional body for post requests.
     * @return a reponse object.
     */
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

    /**
     * A function that will send a post request to the server.
     * @param page the page to which the request will be sent.
     * @param body the body for the request.
     * @return a response object.
     */
    public Response sendPost(String page, RequestBody body) {
        return sendRequest(page, body);
    }

    /**
     * A function that will send a get request to the server.
     * @param page the page to which the request will be sent.
     * @return a response object.
     */
    public Response sendGet(String page) {
        return sendRequest(page, null);
    }

    /**
     * A function that will initialize the user when logging in.
     * @param name the name of the user.
     */
    public void initUser(String name) {
        User user = User.getInstance();
        user.name = name;
        Response response = sendGet("user/" + name);
        JSONArray jsonArray = new JSONArray();
        if (response.successful) {
            try {
                Response r = getBookCollections(user.name);
                jsonArray = new JSONArray(r.responseString);
                if (user.getCollectionList().size() != 0) {
                    user.getCollectionList().clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    user.initBookCollection(new BookCollection(jsonArray.getJSONObject(i).getString("name")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int i = 0;
                for (BookCollection bc : user.getCollectionList()) {
                    if (bc.getBookList().size() != 0)
                        bc.getBookList().clear();
                    JSONArray bookArray = jsonArray.getJSONObject(i).getJSONArray("books");
                    for (int b = 0; b < bookArray.length(); b++) {
                        String bookString = bookArray.get(b).toString();
                        Book book = getBookByID(bookString);
                        if (!(book == null)) {
                            bc.initBook(book);
                        }
                    }
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A function that will return a book by ID.
     * @param id the openlibrary id of the book.
     * @return a book.
     */
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

    /**
     * A function that will return a list of subjects based on a jsonObject.
     * @param jsonObject the jsonObject filled with subjects.
     * @return a list of subjects.
     */
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

    /**
     * A function that will return a list of books based on user input.
     * @param bookName the userinput containing a book name.
     * @return a list of books.
     */
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

    /**
     * A function that will get the book collections of a user.
     * @param name the username.
     * @return a response object.
     */
    public Response getBookCollections(String name) {
        return sendGet("user/" + name + "/book_collections");
    }

    /**
     * A function that will add a book collection to the server.
     * @param name the username.
     * @param collection_name the collection name.
     * @return a response object.
     */
    public Response addBookCollectionServer(String name, String collection_name) {
        return sendGet("user/" + name + "/add_book_collection/" + collection_name);
    }

    /**
     * A function that will delete a book collection from the server.
     * @param name the username.
     * @param collection_name the collection name.
     * @return a response object.
     */
    public Response deleteBookCollectionServer(String name, String collection_name) {
        return sendGet("user/" + name + "/del_book_collection/" + collection_name);
    }

    /**
     * A function that will add a book to a collection on the server.
     * @param name the username.
     * @param collection_name the collection name.
     * @param book_id the ID of the book.
     * @return a response object.
     */
    public Response addBookToCollectionServer(String name, String collection_name, String book_id) {
        return sendGet("user/" + name + "/add_book_to_collection/" + collection_name + "/" + book_id);
    }

    /**
     * A function that will delete a book from a collection on the server.
     * @param name the username.
     * @param collection_name the collection name.
     * @param book_id the ID of the book.
     * @return a response object.
     */
    public Response deleteBookFromCollectionServer(String name, String collection_name, String book_id) {
        return sendGet("user/" + name + "/del_book_from_collection/" + collection_name + "/" + book_id);
    }

    /**
     * A function that will create a HTTP client without certificate verification.
     * @return the HTTP client.
     */
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
