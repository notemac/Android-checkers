package ru.bstu.checkers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 В методе OnCreate загружается объект PreferencesFragment, который содержит разметку окна настроек
 */
public class PreferencesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment preferencesFragment = new PreferencesFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(android.R.id.content, preferencesFragment);
        ft.commit();
    }
}
