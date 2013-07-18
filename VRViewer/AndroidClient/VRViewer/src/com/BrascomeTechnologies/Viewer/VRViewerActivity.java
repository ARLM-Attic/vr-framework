package com.BrascomeTechnologies.Viewer;

import android.app.Activity;
import android.os.Bundle;

public class VRViewerActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.out.println("started main activity");
    }
}