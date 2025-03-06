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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.Preference;
import com.android.internal.util.infinity.InfinityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.android.settings.SettingsPreferenceFragment;
import com.infinity.support.preferences.SystemSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.infinity.suite.fragments.SmartPixels;

@SearchIndexable
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String SMART_PIXELS = "smart_pixels";
    private static final String SCROLL_FLING_HAPTIC_FEEDBACK = "scroll_fling_haptic_feedback";
    
    private SystemSettingSwitchPreference mScrollFlingHapticFeedback;    
    private Preference mSmartPixels;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.infinity_suite_misc);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        
        mSmartPixels = (Preference) prefScreen.findPreference(SMART_PIXELS);
        boolean mSmartPixelsSupported = getResources().getBoolean(
                com.android.internal.R.bool.config_supportSmartPixels);
        if (!mSmartPixelsSupported)
            prefScreen.removePreference(mSmartPixels);
            
        mScrollFlingHapticFeedback = (SystemSettingSwitchPreference) findPreference(SCROLL_FLING_HAPTIC_FEEDBACK);
        mScrollFlingHapticFeedback.setOnPreferenceChangeListener(this);    
    }
    
    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        SmartPixels.reset(mContext);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mScrollFlingHapticFeedback) {
            InfinityUtils.showSystemUiRestartDialog(getActivity());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.infinity_suite_misc) {
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);

                    boolean mSmartPixelsSupported = context.getResources().getBoolean(
                            com.android.internal.R.bool.config_supportSmartPixels);
                    if (!mSmartPixelsSupported)
                        keys.add(SMART_PIXELS);

                    return keys;
                }
            };
}
