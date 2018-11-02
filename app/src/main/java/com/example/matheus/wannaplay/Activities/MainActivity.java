package com.example.matheus.wannaplay.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.matheus.wannaplay.Fragments.HomeFragment;
import com.example.matheus.wannaplay.Fragments.MessagesFragment;
import com.example.matheus.wannaplay.Fragments.PreferencesFragment;
import com.example.matheus.wannaplay.Fragments.ProfileFragment;
import com.example.matheus.wannaplay.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Chamada do método loadFragment passando o Fragment Home por padrão
        loadFragment(new HomeFragment());
    }

    // Método que carrega o Fragment selecionado no BottomNavigationView
    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch(item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_preferences:
                fragment = new PreferencesFragment();
                break;

            case R.id.navigation_messages:
                fragment = new MessagesFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
