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
import com.android.settingslib.search.SearchIndexable;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.infinity.support.preferences.SystemSettingSwitchPreference;
import com.infinity.support.preferences.SystemSettingMainSwitchPreference;


@SearchIndexable
public class NetworkTrafficSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";

    private SystemSettingSwitchPreference mThreshold;
    private SystemSettingMainSwitchPreference mNetMonitor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.network_traffic_settings);

        final ContentResolver resolver = getActivity().getContentResolver();

        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingMainSwitchPreference) findPreference(NETWORK_TRAFFIC_STATE);
        if (mNetMonitor != null) {
            mNetMonitor.setChecked(isNetMonitorEnabled);
            mNetMonitor.setOnPreferenceChangeListener(this);
        }

        boolean isThresholdEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 0, UserHandle.USER_CURRENT) == 1;
        mThreshold = (SystemSettingSwitchPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        if (mThreshold != null) {
            mThreshold.setChecked(isThresholdEnabled);
            mThreshold.setOnPreferenceChangeListener(this);
            mThreshold.setEnabled(isNetMonitorEnabled);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getContentResolver();
        boolean value = (Boolean) objValue;

        if (preference == mNetMonitor) {
            Settings.System.putIntForUser(resolver, Settings.System.NETWORK_TRAFFIC_STATE,
                    value ? 1 : 0, UserHandle.USER_CURRENT);

            if (mThreshold != null) {
                mThreshold.setEnabled(value);
            }
            return true;
        } else if (preference == mThreshold) {
            Settings.System.putIntForUser(resolver, Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.network_traffic_settings);
}
