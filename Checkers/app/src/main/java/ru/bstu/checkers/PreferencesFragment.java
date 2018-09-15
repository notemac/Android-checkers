package ru.bstu.checkers;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * В методе onCreate загружается разметка окна настроек R.xml.preferences.
 */
public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
