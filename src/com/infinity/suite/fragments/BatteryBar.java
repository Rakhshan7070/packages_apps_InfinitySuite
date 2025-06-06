/*
 * Copyright (C) 2018 AospExtended ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.infinity.suite.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreferenceCompat;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.lang.ref.WeakReference;

@SearchIndexable
public class BatteryBar extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_BATT_BAR = "statusbar_battery_bar";

    private SwitchPreferenceCompat mBatteryBar;
    private boolean mIsBarSwitchingMode = false;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_bar);

        final ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
        int intColor;
        String hexColor;

        mBatteryBar = (SwitchPreferenceCompat) findPreference(PREF_BATT_BAR);
        mHandler = new Handler();

        boolean showing = Settings.System.getIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR, 0, UserHandle.USER_CURRENT) != 0;
        mBatteryBar.setChecked(showing);
        mBatteryBar.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryBar) {
            if (mIsBarSwitchingMode) {
                return false;
            }
            mIsBarSwitchingMode = true;
            boolean value = ((Boolean)newValue);
            Settings.System.putIntForUser(resolver, Settings.System.STATUSBAR_BATTERY_BAR,
                    value ? 1 : 0, UserHandle.USER_CURRENT);

            mRunnable = new Runnable() {
                @Override
                public void run() {
                    mIsBarSwitchingMode = false;
                    boolean showing = Settings.System.getIntForUser(resolver,
                            Settings.System.STATUSBAR_BATTERY_BAR, 0, UserHandle.USER_CURRENT) != 0;
                    mBatteryBar.setChecked(showing);
                }
            };
            mHandler.postDelayed(mRunnable, 1500);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_COLOR, 0xff76c124, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 2, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, 0xffffc90f, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BATTERY_LOW_COLOR, 0xfff90028, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ENABLE_CHARGING_COLOR, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BLEND_COLOR, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_BLEND_COLOR_REVERSE, 0, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.INFINITY;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.battery_bar);
}
