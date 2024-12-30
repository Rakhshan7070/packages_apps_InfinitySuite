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

package com.infinity.suite;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.view.Surface;
import android.widget.LinearLayout;
import android.widget.ImageView;

import com.android.internal.logging.nano.MetricsProto;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.infinity.suite.fragments.*;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;
import com.android.settings.WallpaperBlurView;

public class InfinitySuite extends SettingsPreferenceFragment implements View.OnClickListener {

    private LinearLayout[] settingCards;
    private WallpaperBlurView mLockScreenSettingsCard;
    private MaterialCardView mCustomizationPickerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.InfinitySuite, container, false);
        settingCards = new LinearLayout[] {
            view.findViewById(R.id.quicksettings_card),
            view.findViewById(R.id.monetsettings_card),
            view.findViewById(R.id.statusbarsettings_card),
            view.findViewById(R.id.powersettings_card),
            view.findViewById(R.id.teaminfo_card),
            view.findViewById(R.id.notificationsettings_card),
            view.findViewById(R.id.theme_card),
            view.findViewById(R.id.navigationsettings_card),
            view.findViewById(R.id.miscsettings_card),
            view.findViewById(R.id.buttonsettings_card),
            view.findViewById(R.id.aodsettings_card)
        };
        for (LinearLayout card : settingCards) {
            card.setOnClickListener(this);
        }
        mLockScreenSettingsCard = view.findViewById(R.id.lockscreensettings_card);
        mLockScreenSettingsCard.setOnClickListener(this);
        
        mCustomizationPickerButton = view.findViewById(R.id.customization_picker_button);
        mCustomizationPickerButton.setOnClickListener(this);
        
        return view;
    }

    @Override
	public void onClick(View view) {
	    int id = view.getId();
	    Fragment fragment = null;
	    String title = null;
	    if (id == R.id.quicksettings_card) {
	        fragment = new QuickSettings();
	        title = getString(R.string.quicksettings_title);
	    } else if (id == R.id.monetsettings_card) {
	        fragment = new MonetSettings();
	        title = getString(R.string.monet_title);
	    } else if (id == R.id.statusbarsettings_card) {
	        fragment = new StatusBarSettings();
	        title = getString(R.string.statusbar_title);
	    } else if (id == R.id.lockscreensettings_card) {
	        fragment = new LockScreenSettings();
	        title = getString(R.string.lockscreen_title);
	    } else if (id == R.id.powersettings_card) {
	        fragment = new PowerMenuSettings();
	        title = getString(R.string.powermenu_title);
	    } else if (id == R.id.teaminfo_card) {
	        fragment = new TeamInfo();
	        title = getString(R.string.gestures_title);
	    } else if (id == R.id.notificationsettings_card) {
	        fragment = new NotificationSettings();
	        title = getString(R.string.notifications_title);
	    } else if (id == R.id.theme_card) {
	        fragment = new ThemesSettings();
	        title = getString(R.string.themes_title);
	    } else if (id == R.id.navigationsettings_card) {
	        fragment = new NavbarSettings();
	        title = getString(R.string.navbar_title);
	    } else if (id == R.id.miscsettings_card) {
	        fragment = new MiscSettings();
	        title = getString(R.string.misc_title);
	    } else if (id == R.id.buttonsettings_card) {
	        fragment = new ButtonSettings();
	        title = getString(R.string.button_title);
	    } else if (id == R.id.customization_picker_button) {
	        openCustomizationPickerActivity();
	    } else if (id == R.id.aodsettings_card) {
	        fragment = new AmbientCustomizations();
	        title = getString(R.string.ambient_text_category_title);
	    }       
	    
	    if (fragment != null && title != null) {
	        replaceFragment(fragment, title);
	    }	
    }

    private void replaceFragment(Fragment fragment, String title) {
            FragmentManager fragmentManager = getFragmentManager();
	    if (fragmentManager != null) {
	        FragmentTransaction transaction = fragmentManager.beginTransaction();
	        transaction.setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out);
	        transaction.replace(this.getId(), fragment);
	        transaction.addToBackStack(null);
	        transaction.commit();
	        getActivity().setTitle(title != null ? title : "Infinity Suite");
	   }  
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.INFINITY;
    }
    
    @Override
    public void onResume() {
    super.onResume();
    	getActivity().setTitle("Infinity Suite");
    }
    
    public void openCustomizationPickerActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.android.wallpaper", "com.android.customization.picker.CustomizationPickerActivity");
        startActivity(intent);
    }

    public static void lockCurrentOrientation(Activity activity) {
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        int frozenRotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (currentRotation) {
            case Surface.ROTATION_0:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        activity.setRequestedOrientation(frozenRotation);
    }
}
