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

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.infinity.support.preferences.SystemSettingEditTextPreference;
import com.infinity.support.preferences.SystemSettingSwitchPreference;
import com.android.internal.util.infinity.InfinityUtils;

import java.util.List;
import java.util.ArrayList;

@SearchIndexable
public class QuickSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
        
    private static final String X_FOOTER_TEXT_STRING = "x_footer_text_string";
    private static final String KEY_QS_COMPACT_PLAYER  = "qs_compact_media_player_mode";

    private SystemSettingEditTextPreference mFooterString;
    private ListPreference mQuickPulldown;
    private SystemSettingSwitchPreference mOmniFooterTextShow;
    private SystemSettingSwitchPreference mQsFooterDataUsage;
    private Preference mQsCompactPlayer;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.infinity_suite_quicksettings);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        int qpmode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mQuickPulldown = (ListPreference) findPreference("qs_quick_pulldown");
        mQuickPulldown.setValue(String.valueOf(qpmode));
        mQuickPulldown.setSummary(mQuickPulldown.getEntry());
        mQuickPulldown.setOnPreferenceChangeListener(this);
        
        mFooterString = (SystemSettingEditTextPreference) findPreference(X_FOOTER_TEXT_STRING);
        String footerString = Settings.System.getString(getContentResolver(),
                X_FOOTER_TEXT_STRING);
        if (footerString != null && footerString != "")
            mFooterString.setText(footerString);
        else {
            mFooterString.setText("Infinity X");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.X_FOOTER_TEXT_STRING, "Infinity X");
        }
        mFooterString.setOnPreferenceChangeListener(this);
        
        mOmniFooterTextShow = (SystemSettingSwitchPreference) findPreference("omni_footer_text_show");
        mOmniFooterTextShow.setOnPreferenceChangeListener(this);

        mQsFooterDataUsage = (SystemSettingSwitchPreference) findPreference("qs_footer_data_usage");
        updateQsFooterDataUsageState(mOmniFooterTextShow.isChecked());

        mQsCompactPlayer = (Preference) findPreference(KEY_QS_COMPACT_PLAYER);
        mQsCompactPlayer.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, value,
                    UserHandle.USER_CURRENT);
            int index = mQuickPulldown.findIndexOfValue((String) newValue);
            mQuickPulldown.setSummary(
                    mQuickPulldown.getEntries()[index]);
            return true;
        } else if (preference == mFooterString) {
            String value = (String) newValue;
            if (value != "" && value != null)
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.X_FOOTER_TEXT_STRING, value);
            else {
                mFooterString.setText("Infinity X");
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.X_FOOTER_TEXT_STRING, "Infinity X");
            }
            return true;
        } else if (preference == mOmniFooterTextShow) {
            updateQsFooterDataUsageState((Boolean) newValue);
            return true;
        } else if (preference == mQsCompactPlayer) {
            InfinityUtils.showSystemUiRestartDialog(getActivity());
            return true;
        }
        return false;
    }
    
    private void updateQsFooterDataUsageState(boolean isOmniFooterTextShowEnabled) {
        if (isOmniFooterTextShowEnabled) {
            mQsFooterDataUsage.setEnabled(false);
        } else {
            mQsFooterDataUsage.setEnabled(true);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mQuickPulldown != null) {
            mQuickPulldown.setOnPreferenceChangeListener(null);
        }
        if (mOmniFooterTextShow != null) {
            mOmniFooterTextShow.setOnPreferenceChangeListener(null);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.infinity_suite_quicksettings);

}
