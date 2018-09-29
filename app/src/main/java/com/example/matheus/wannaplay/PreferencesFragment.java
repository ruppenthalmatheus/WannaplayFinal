package com.example.matheus.wannaplay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.innovattic.rangeseekbar.RangeSeekBar;

public class PreferencesFragment extends Fragment {

    private SeekBar distance_seekbar;
    private TextView distanceTextView;
    private TextView minAgeTextView;
    private TextView maxAgeTextView;
    private RangeSeekBar ageSeekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the fragment_preferences layout into a View
        View preferencesView = inflater.inflate(R.layout.fragment_preferences, container, false);

        /* --- Setting up the SeekBar --- */

        //Loading the component
        distance_seekbar = preferencesView.findViewById(R.id.distance_seekbar);

        //Loading the tracking distance TextView label component
        distanceTextView = preferencesView.findViewById(R.id.txt_distance);

        //Setting up the maximum distance that can be covered by the filter
        distance_seekbar.setMax(200);

        //Setting up an default filter for the seek bar and updating the TextView label
        distance_seekbar.setProgress(50);
        distanceTextView.setText(String.valueOf(distance_seekbar.getProgress()));

        /* --- Setting up the RangeBar --- */

        //Loading the component
        ageSeekBar = preferencesView.findViewById(R.id.age_seekbar);

        //Loading the tracking TextView labels for max and min age
        minAgeTextView = preferencesView.findViewById(R.id.txt_min_age);
        maxAgeTextView = preferencesView.findViewById(R.id.txt_max_age);

        //Setting up the max age TextView label
        maxAgeTextView.setText(String.valueOf(ageSeekBar.getMax()));

        //Distance SeekBar config
        distance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValue = progress;
                distanceTextView.setText(String.valueOf(progressChangedValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                distanceTextView.setText(String.valueOf(progressChangedValue));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceTextView.setText(String.valueOf(progressChangedValue));
            }
        });

        //Age RangeBar config
        ageSeekBar.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {

            int minChangedValue;
            int maxChangedValue;

            @Override
            public void onValueChanged(int i, int i1) {
                minChangedValue = i;
                maxChangedValue = i1;
                minAgeTextView.setText(String.valueOf(minChangedValue));
                maxAgeTextView.setText(String.valueOf(maxChangedValue));
            }

            @Override
            public void onStoppedSeeking() {
                minAgeTextView.setText(String.valueOf(minChangedValue));
                maxAgeTextView.setText(String.valueOf(maxChangedValue));
            }

            @Override
            public void onStartedSeeking() {
                minAgeTextView.setText(String.valueOf(minChangedValue));
                maxAgeTextView.setText(String.valueOf(maxChangedValue));
            }
        });

        return preferencesView;

    }



}
