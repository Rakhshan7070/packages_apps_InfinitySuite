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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.infinity.support.preferences.SystemSettingSwitchPreference;

@SearchIndexable
public class NotificationSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NOTIFICATION_SOUND_VIB_SCREEN_ON = "notification_sound_vib_screen_on";

    private SystemSettingSwitchPreference mNotificationSoundVib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.infinity_suite_notifications);

        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        final PreferenceScreen screen = getPreferenceScreen();

        mNotificationSoundVib = screen.findPreference(NOTIFICATION_SOUND_VIB_SCREEN_ON);

        if (mNotificationSoundVib != null) {
            boolean isEnabled = Settings.System.getInt(resolver,
                    Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, 1) == 1;
            mNotificationSoundVib.setChecked(isEnabled);
            mNotificationSoundVib.setOnPreferenceChangeListener(this);
        }            
    }
    
    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, 1, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationSoundVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.infinity_suite_notifications);
}
