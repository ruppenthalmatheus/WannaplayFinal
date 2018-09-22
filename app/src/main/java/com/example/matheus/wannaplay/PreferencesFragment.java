package com.example.matheus.wannaplay;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencesFragment extends Fragment {

    private SeekBar distance_seekbar;
    private TextView distanceTextView;
    Activity preferencesFragment = getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View preferencesView = inflater.inflate(R.layout.fragment_preferences, container, false);
        distance_seekbar = preferencesView.findViewById(R.id.distance_seekbar);
        distanceTextView = preferencesView.findViewById(R.id.lbl_distance);
        distance_seekbar.setMax(200);
        distance_seekbar.setProgress(50);
        distanceTextView.setText("TESTE");

        distance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceTextView.setText(progressChangedValue);
                Toast.makeText(preferencesFragment, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return inflater.inflate(R.layout.fragment_preferences, null);

    }



}
