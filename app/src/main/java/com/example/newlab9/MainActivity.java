package com.example.newlab9;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.newlab9.UI.articles.ArticlesFragment;
import com.example.newlab9.UI.crud.AdminFragment;
import com.example.newlab9.UI.games.GamesFragment;
import com.example.newlab9.UI.home.HomeFragment;
import com.example.newlab9.UI.music.MusicFragment;
import com.example.newlab9.UI.weather.WeatherFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private boolean isAdmin;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Activity created");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d(TAG, "onCreate: No current user, redirecting to OnePageActivity");
            startActivity(new Intent(MainActivity.this, OnePageActivity.class));
            finish();
        } else {
            Log.d(TAG, "onCreate: User is authenticated");
            checkUserRole(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void checkUserRole(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("role");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.getValue(String.class);
                    isAdmin = "admin".equals(role);
                    Log.d(TAG, "isAdmin: " + isAdmin); // Добавляем лог
                    invalidateOptionsMenu(); // Обновляем меню
                    // Убедимся, что пункт меню отображается в NavigationView
                    Menu navMenu = navigationView.getMenu();
                    MenuItem adminItem = navMenu.findItem(R.id.nav_admin_panel);
                    if (adminItem != null) {
                        adminItem.setVisible(isAdmin);
                        Log.d(TAG, "Admin menu item visibility set to: " + isAdmin);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Роль пользователя не найдена", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Ошибка при получении данных о роли пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        MenuItem adminItem = menu.findItem(R.id.nav_admin_panel);
        if (adminItem != null) {
            adminItem.setVisible(isAdmin);
            Log.d(TAG, "isAdmin in onCreateOptionsMenu: " + isAdmin); // Добавляем лог
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            loadFragment(new HomeFragment());
        } else if (itemId == R.id.nav_weather) {
            loadFragment(new WeatherFragment());
        } else if (itemId == R.id.nav_music) {
            loadFragment(new MusicFragment());
        } else if (itemId == R.id.nav_games) {
            loadFragment(new GamesFragment());
        } else if (itemId == R.id.nav_articles) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isAdmin", isAdmin); // Передаем информацию о роли
            ArticlesFragment articlesFragment = new ArticlesFragment();
            articlesFragment.setArguments(bundle);
            loadFragment(articlesFragment);
        } else if (itemId == R.id.nav_admin_panel) {
            loadFragment(new AdminFragment());
        } else {
            return false;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
