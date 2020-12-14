package com.example.myread;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private User user;
    private SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        prf = getSharedPreferences("user_details",MODE_PRIVATE);
        setSupportActionBar(toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        System.out.println(prf.getString("username", ""));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_search, R.id.nav_settings)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//
        List<String> subjects = new ArrayList<String>();

//        User user = new User("Petertje");
        user.addBookCollection(new BookCollection("Hoost_en"));
        user.addBookCollection(new BookCollection("WIllem"));

        user.getBookCollection(1).addBook(user.name, "OL26586969M", "Joost", "Soup", "cover", "description", subjects, "9-12-2020", "Willem", "9", "3");
        user.getBookCollection(1).addBook(user.name, "OL26586969M","Willom", "Soep", "cover", "description", subjects, "9-12-2020", "Willem", "9", "3");
        user.getBookCollection(1).addBook(user.name, "OL26586969M","Hank", "Bee", "cover", "description", subjects, "9-12-2020", "Willem", "9", "3");
    }

    private void getBook() {
        Book response = ServerConnect.getInstance().getBook("OL26586969M");
    }

    private void getUser() throws JSONException {
//        user = ServerConnect.getInstance().getUser("Petertje"); // hier bij naam moet de username uit de cookie/session gehaald worden
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(MenuItem item) {
        SharedPreferences.Editor editor = prf.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}