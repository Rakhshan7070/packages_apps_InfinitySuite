/*
 *  Copyright (C) 2024 Project Infinity X
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.infinity.suite.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.preference.SwitchPreference;
import android.os.UserHandle;
import androidx.preference.SwitchPreferenceCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import android.provider.MediaStore;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.internal.util.infinity.OmniJawsClient;

import com.infinity.support.preferences.SystemSettingSwitchPreference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.android.internal.util.infinity.udfps.CustomUdfpsUtils;

@SearchIndexable
public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
        
    private static final String FP_SUCCESS_VIBRATE = "fp_success_vibrate";
    private static final String FP_ERROR_VIBRATE = "fp_error_vibrate";     

    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mFingerprintSuccessVib;
    private SystemSettingSwitchPreference mFingerprintErrorVib;

    private static final String KEY_WEATHER = "lockscreen_weather_enabled";
    private static final String UDFPS_CATEGORY = "udfps_category";

    private Preference mWeather;
    private OmniJawsClient mWeatherClient;
    private PreferenceCategory mUdfpsCategory;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.infinity_suite_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final PackageManager mPm = getActivity().getPackageManager();
        Resources resources = getResources();
        
        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintSuccessVib = (SystemSettingSwitchPreference) findPreference(FP_SUCCESS_VIBRATE);
        mFingerprintErrorVib = (SystemSettingSwitchPreference) findPreference(FP_ERROR_VIBRATE);
        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()) {
                prefSet.removePreference(mFingerprintSuccessVib);
                prefSet.removePreference(mFingerprintErrorVib);
            } else {
                mFingerprintSuccessVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FP_SUCCESS_VIBRATE, 1) == 1));
                mFingerprintSuccessVib.setOnPreferenceChangeListener(this);
                mFingerprintErrorVib.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.FP_ERROR_VIBRATE, 1) == 1));
                mFingerprintErrorVib.setOnPreferenceChangeListener(this);
            }
        } else {
            prefSet.removePreference(mFingerprintSuccessVib);
            prefSet.removePreference(mFingerprintErrorVib);
        }

        mWeather = (Preference) findPreference(KEY_WEATHER);
        mWeatherClient = new OmniJawsClient(getContext());
        updateWeatherSettings();
        
        mUdfpsCategory = findPreference(UDFPS_CATEGORY);
        if (!CustomUdfpsUtils.hasUdfpsSupport(getContext())) {
            prefSet.removePreference(mUdfpsCategory);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mFingerprintSuccessVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_SUCCESS_VIBRATE, value ? 1 : 0);
            return true;
        } else if (preference == mFingerprintErrorVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_ERROR_VIBRATE, value ? 1 : 0);
            return true;
        }
        return false;
    }

    private void updateWeatherSettings() {
        if (mWeatherClient == null || mWeather == null) return;

        boolean weatherEnabled = mWeatherClient.isOmniJawsEnabled();
        mWeather.setEnabled(weatherEnabled);
        mWeather.setSummary(weatherEnabled ? R.string.lockscreen_weather_summary :
            R.string.lockscreen_weather_enabled_info);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWeatherSettings();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.infinity_suite_lockscreen);
}
