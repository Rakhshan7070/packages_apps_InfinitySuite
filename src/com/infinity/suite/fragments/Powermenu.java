/*
 * Copyright (C) 2024 Project Infinity X
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.infinity.suite.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreferenceCompat;
import android.provider.Settings;
import com.android.settings.R;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class Powermenu extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final int MAX_ENABLED_BUTTONS = 4;

    private int enabledButtonCount = 0;
    private List<SwitchPreferenceCompat> enabledPreferences = new ArrayList<>();
    private SwitchPreferenceCompat lastToggledPreference;
    private Handler handler;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.powermenu);
        handler = new Handler();

        final PreferenceScreen prefScreen = getPreferenceScreen();

        for (int i = 0; i < prefScreen.getPreferenceCount(); i++) {
            Preference preference = prefScreen.getPreference(i);
            if (preference instanceof SwitchPreferenceCompat) {
                SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;
                switchPreference.setOnPreferenceChangeListener(this);
            }
        }
        updateEnabledButtonCount();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof SwitchPreferenceCompat) {
            SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;
            boolean isEnabled = (boolean) newValue;

            if (isEnabled) {
                if (enabledButtonCount >= MAX_ENABLED_BUTTONS) {
                    Toast.makeText(getActivity(), "Maximum buttons reached. Disabling last button.", Toast.LENGTH_SHORT).show();
                    lastToggledPreference = switchPreference;
                    disableLastEnabledButton();
                    return false;
                } else {
                    enabledButtonCount++;
                    enabledPreferences.add(switchPreference);
                }
            } else {
                enabledButtonCount--;
                enabledPreferences.remove(switchPreference);
            }
        }
        return true;
    }

    private void disableLastEnabledButton() {
        if (!enabledPreferences.isEmpty()) {
            SwitchPreferenceCompat lastEnabledPreference = enabledPreferences.get(enabledPreferences.size() - 1);
            lastEnabledPreference.setChecked(false);
            enabledPreferences.remove(lastEnabledPreference);
            enabledButtonCount--;
        }
    }

    private void updateEnabledButtonCount() {
        enabledButtonCount = 0;
        enabledPreferences.clear();
        PreferenceScreen prefScreen = getPreferenceScreen();
        for (int i = 0; i < prefScreen.getPreferenceCount(); i++) {
            Preference preference = prefScreen.getPreference(i);
            if (preference instanceof SwitchPreferenceCompat) {
                SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) preference;
                if (switchPreference.isChecked()) {
                    enabledButtonCount++;
                    enabledPreferences.add(switchPreference);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lastToggledPreference != null) {
            handler.postDelayed(() -> lastToggledPreference.setChecked(true), 2000);
            lastToggledPreference = null;
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceScreen prefScreen = getPreferenceScreen();
        for (int i = 0; i < prefScreen.getPreferenceCount(); i++) {
            Preference preference = prefScreen.getPreference(i);
            if (preference instanceof SwitchPreferenceCompat) {
                ((SwitchPreferenceCompat) preference).setOnPreferenceChangeListener(null);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.monet_engine);
}
