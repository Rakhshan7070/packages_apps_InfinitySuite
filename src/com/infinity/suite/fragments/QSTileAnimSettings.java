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
import com.android.settings.search.BaseSearchIndexProvider;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.infinity.support.preferences.SystemSettingSeekBarPreference;


@SearchIndexable
public class QSTileAnimSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String KEY_PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String KEY_PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";

    private ListPreference mTileAnimationStyle;
    private SystemSettingSeekBarPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.qs_tile_anim_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mTileAnimationStyle = (ListPreference) findPreference(KEY_PREF_TILE_ANIM_STYLE);
        mTileAnimationDuration = (SystemSettingSeekBarPreference) findPreference(KEY_PREF_TILE_ANIM_DURATION);
        mTileAnimationInterpolator = (ListPreference) findPreference(KEY_PREF_TILE_ANIM_INTERPOLATOR);

        mTileAnimationStyle.setOnPreferenceChangeListener(this);
        mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        int tileAnimationStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_ANIMATION_STYLE, 0, UserHandle.USER_CURRENT);
        updateAnimTileStyle(tileAnimationStyle);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTileAnimationStyle) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_ANIMATION_STYLE, value,
                    UserHandle.USER_CURRENT);
            updateAnimTileStyle(value);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_ANIMATION_INTERPOLATOR, value,
                    UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    private void updateAnimTileStyle(int tileAnimationStyle) {
        mTileAnimationDuration.setEnabled(tileAnimationStyle != 0);
        mTileAnimationInterpolator.setEnabled(tileAnimationStyle != 0);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_tile_anim_settings);

}
