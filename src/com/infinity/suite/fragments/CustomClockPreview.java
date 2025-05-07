/*
 * Copyright (C) 2023-2024 The risingOS Android Project
 * Copyright (C) 2024-25 Project Infinity X 
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

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.infinity.ThemeUtils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class CustomClockPreview extends SettingsPreferenceFragment {

    private static final String TAG = "LockClockPreview";
    private static final String PREF_FIRST_TIME = "first_time_clock_face_access";

    private ViewPager viewPager;
    private ClockPagerAdapter pagerAdapter;
    private ExtendedFloatingActionButton applyFab;
    private View highlightGuide;
    private TextView clockNameTextView;

    private int mClockPosition = 0;

    private ThemeUtils mThemeUtils;
    private Handler mHandler = new Handler();

    private final static int[] mCenterClocks = {2, 3, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16};

    private static final int[] CLOCK_LAYOUTS = {
            R.layout.keyguard_clock_default,
            R.layout.keyguard_clock_oos,
            R.layout.keyguard_clock_center,
            R.layout.keyguard_clock_simple,
            R.layout.keyguard_clock_miui,
            R.layout.keyguard_clock_ide,
            R.layout.keyguard_clock_moto,
            R.layout.keyguard_clock_stylish,
            R.layout.keyguard_clock_stylish2,
            R.layout.keyguard_clock_stylish3,
            R.layout.keyguard_clock_stylish4,
            R.layout.keyguard_clock_stylish5,
            R.layout.keyguard_clock_stylish6,
            R.layout.keyguard_clock_stylish7,
            R.layout.keyguard_clock_stylish8,
            R.layout.keyguard_clock_stylish9,
            R.layout.keyguard_clock_stylish10
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getActivity().getString(R.string.custom_clock_style));
        mThemeUtils = new ThemeUtils(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lockscreen_clock_preview, container, false);
        clockNameTextView = rootView.findViewById(R.id.clock_name);

        viewPager = rootView.findViewById(R.id.view_pager);
        pagerAdapter = new ClockPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        mClockPosition = Settings.Secure.getIntForUser(getContext().getContentResolver(), "clock_style", 0, UserHandle.USER_CURRENT);
        if (mClockPosition < 0 || mClockPosition >= CLOCK_LAYOUTS.length) {
            mClockPosition = 0;
            Settings.Secure.putIntForUser(getContext().getContentResolver(), "clock_style", 0, UserHandle.USER_CURRENT);
        }
        viewPager.setCurrentItem(mClockPosition);

        applyFab = rootView.findViewById(R.id.apply_extended_fab);
        applyFab.setOnClickListener(new View.OnClickListener() {
            @Override
	    public void onClick(View view) {
		Settings.Secure.putIntForUser(getContext().getContentResolver(),
		    "clock_style", mClockPosition, UserHandle.USER_CURRENT);
		Settings.Secure.putIntForUser(getContext().getContentResolver(),
		    "lock_screen_custom_clock_face", 0, UserHandle.USER_CURRENT);
		updateClockOverlays(mClockPosition);
	    }
        });

        highlightGuide = rootView.findViewById(R.id.highlight_guide);
        if (isFirstTime()) {
            highlightGuide.setVisibility(View.VISIBLE);
            highlightGuide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    highlightGuide.setVisibility(View.GONE);
                    disableHighlight();
                }
            });
        } else {
            highlightGuide.setVisibility(View.GONE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mClockPosition = position;
                if (viewPager != null) {
                    viewPager.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK);
                }
                updateClockName(position);
            }
        });
        return rootView;
    }
    
    private void updateClockName(int position) {
	String[] clockNames = {
	    "Default Clock",
	    "OnePlus Clock",
	    "IOS Clock",
	    "Simple Clock",
	    "MIUI Clock",
	    "IDE Clock",
	    "Moto Clock",
	    "Stylish Clock",
	    "Stylish Clock 2",
	    "Stylish Clock 3",
	    "Stylish Clock 4",
	    "Stylish Clock 5",
	    "Stylish Clock 6",
	    "Stylish Clock 7",
	    "Stylish Clock 8",
	    "Stylish Clock 9",
	    "Stylish Clock 10"
	};
	if (clockNameTextView != null && position >= 0 && position < clockNames.length) {
	    clockNameTextView.setText(clockNames[position]);
	}
    }

    private void updateClockOverlays(int clockStyle) {
        mThemeUtils.setOverlayEnabled(
                "android.theme.customization.hideclock",
                clockStyle != 0 ? "com.android.systemui.clocks.hideclock" : "android",
                "android");
        mThemeUtils.setOverlayEnabled(
                "android.theme.customization.smartspace",
                clockStyle != 0 ? "com.android.systemui.hide.smartspace" : "com.android.systemui",
                "com.android.systemui");
        mThemeUtils.setOverlayEnabled(
                "android.theme.customization.smartspace_offset",
                clockStyle != 0 && isCenterClock(clockStyle)
                        ? "com.android.systemui.smartspace_offset.smartspace"
                        : "com.android.systemui",
                "com.android.systemui");
    }

    private boolean isCenterClock(int clockStyle) {
        for (int centerClock : mCenterClocks) {
            if (centerClock == clockStyle) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldScaleDown(int position) {
        int layoutId = CLOCK_LAYOUTS[position];
        return layoutId == R.layout.keyguard_clock_stylish
               || layoutId == R.layout.keyguard_clock_stylish2 || layoutId == R.layout.keyguard_clock_stylish3
               || layoutId == R.layout.keyguard_clock_stylish4 || layoutId == R.layout.keyguard_clock_stylish5
               || layoutId == R.layout.keyguard_clock_stylish6 || layoutId == R.layout.keyguard_clock_stylish7
               || layoutId == R.layout.keyguard_clock_stylish8 || layoutId == R.layout.keyguard_clock_stylish9
               || layoutId == R.layout.keyguard_clock_stylish10;
    }

    private boolean isFirstTime() {
        return Settings.System.getIntForUser(
            getContext().getContentResolver(), PREF_FIRST_TIME, 1, UserHandle.USER_CURRENT) != 0;
    }

    private void disableHighlight() {
        Settings.System.putIntForUser(getContext().getContentResolver(), PREF_FIRST_TIME, 0, UserHandle.USER_CURRENT);
    }

    private class ClockPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View layout = inflater.inflate(CLOCK_LAYOUTS[position], container, false);

            int bottomPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 
                150, 
                getResources().getDisplayMetrics()
            );
            layout.setPadding(
                layout.getPaddingLeft(), 
                layout.getPaddingTop(), 
                layout.getPaddingRight(), 
                bottomPadding
            );
            
            if (shouldScaleDown(position)) {
                layout.setScaleX(0.5f);
                layout.setScaleY(0.5f);
            }
            
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return CLOCK_LAYOUTS.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
    
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateClockName(mClockPosition);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateClockName(mClockPosition);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VIEW_UNKNOWN;
    }
}
