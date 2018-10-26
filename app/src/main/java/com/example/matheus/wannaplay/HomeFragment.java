package com.example.matheus.wannaplay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Locale;

public class HomeFragment extends Fragment {

    Button preferencesButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the fragment_preferences layout into a View
        View homeView = inflater.inflate(R.layout.fragment_empty_home, container, false);

        preferencesButton = homeView.findViewById(R.id.homePreferencesBtn);

        //Changes the Preferences Button background if the device language is Portuguese
        if (Locale.getDefault().getDisplayLanguage().equals("português")) {
            preferencesButton.setBackgroundResource(R.drawable.btn_preferences_br);
        }

        return homeView;
    }
}
