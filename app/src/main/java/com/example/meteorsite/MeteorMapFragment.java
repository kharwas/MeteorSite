package com.example.meteorsite;

import android.os.Bundle;
import com.google.android.gms.maps.MapFragment;

public class MeteorMapFragment extends MapFragment {
    public final static String FRAGMENT_TAG = "map_fragment";

    public MeteorMapFragment() {
        // Required empty public constructor
    }

    public static MeteorMapFragment newInstance(String param1, String param2) {
        MeteorMapFragment fragment = new MeteorMapFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
