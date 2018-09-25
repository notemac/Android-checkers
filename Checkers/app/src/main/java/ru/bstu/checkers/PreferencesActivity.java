package ru.bstu.checkers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.widget.TextView;

/**
 В методе OnCreate загружается объект PreferencesFragment, который содержит разметку окна настроек
 */
public class PreferencesActivity extends Activity {
    public static boolean isChangedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment preferencesFragment = new PreferencesFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(android.R.id.content, preferencesFragment);
        ft.commit();
        isChangedSettings = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onNavigateUp();
    }
}
