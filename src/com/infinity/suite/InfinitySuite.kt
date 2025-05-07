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

package com.infinity.suite

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.internal.logging.nano.MetricsProto
import com.android.settings.R
import com.android.settings.SettingsPreferenceFragment
import com.google.android.material.card.MaterialCardView
import com.infinity.suite.WallpaperBlur

import com.infinity.suite.fragments.QuickSettings
import com.infinity.suite.fragments.MonetSettings
import com.infinity.suite.fragments.StatusBarSettings
import com.infinity.suite.fragments.LockScreenSettings
import com.infinity.suite.fragments.PowerMenuSettings
import com.infinity.suite.fragments.TeamInfo
import com.infinity.suite.fragments.NotificationSettings
import com.infinity.suite.fragments.ThemesSettings
import com.infinity.suite.fragments.NavbarSettings
import com.infinity.suite.fragments.MiscSettings
import com.infinity.suite.fragments.ButtonSettings
import com.infinity.suite.fragments.AmbientCustomizations

class InfinitySuite : SettingsPreferenceFragment(), View.OnClickListener {

    private lateinit var settingCards: List<View>
    private lateinit var lockScreenSettingsCard: WallpaperBlur
    private lateinit var customizationPickerButton: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.InfinitySuite, container, false)

        val cardIds = listOf(
            R.id.quicksettings_card,
            R.id.monetsettings_card,
            R.id.statusbarsettings_card,
            R.id.powersettings_card,
            R.id.teaminfo_card,
            R.id.notificationsettings_card,
            R.id.theme_card,
            R.id.navigationsettings_card,
            R.id.miscsettings_card,
            R.id.buttonsettings_card,
            R.id.aodsettings_card
        )

        settingCards = cardIds.map { id -> view.findViewById<View>(id).apply { setOnClickListener(this@InfinitySuite) } }

        lockScreenSettingsCard = view.findViewById(R.id.lockscreensettings_card)
        lockScreenSettingsCard.setOnClickListener(this)

        customizationPickerButton = view.findViewById(R.id.customization_picker_button)
        customizationPickerButton.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View) {
        val fragment = when (view.id) {
            R.id.quicksettings_card -> QuickSettings()
            R.id.monetsettings_card -> MonetSettings()
            R.id.statusbarsettings_card -> StatusBarSettings()
            R.id.lockscreensettings_card -> LockScreenSettings()
            R.id.powersettings_card -> PowerMenuSettings()
            R.id.teaminfo_card -> TeamInfo()
            R.id.notificationsettings_card -> NotificationSettings()
            R.id.theme_card -> ThemesSettings()
            R.id.navigationsettings_card -> NavbarSettings()
            R.id.miscsettings_card -> MiscSettings()
            R.id.buttonsettings_card -> ButtonSettings()
            R.id.aodsettings_card -> AmbientCustomizations()
            R.id.customization_picker_button -> {
                openCustomizationPickerActivity()
                return
            }
            else -> null
        }

        val title = when (view.id) {
            R.id.quicksettings_card -> getString(R.string.quicksettings_title)
            R.id.monetsettings_card -> getString(R.string.monet_title)
            R.id.statusbarsettings_card -> getString(R.string.statusbar_title)
            R.id.lockscreensettings_card -> getString(R.string.lockscreen_title)
            R.id.powersettings_card -> getString(R.string.powermenu_title)
            R.id.teaminfo_card -> getString(R.string.team_title)
            R.id.notificationsettings_card -> getString(R.string.notifications_title)
            R.id.theme_card -> getString(R.string.themes_title)
            R.id.navigationsettings_card -> getString(R.string.navbar_title)
            R.id.miscsettings_card -> getString(R.string.misc_title)
            R.id.buttonsettings_card -> getString(R.string.button_title)
            R.id.aodsettings_card -> getString(R.string.ambient_text_category_title)
            else -> null
        }

        if (fragment != null && title != null) {
            replaceFragmentWithSystemAnimations(fragment, title)
        }
    }

    private fun replaceFragmentWithSystemAnimations(fragment: Fragment, title: String) {
        val transaction = parentFragmentManager.beginTransaction()
        
        transaction.setCustomAnimations(
	    R.anim.fragment_slide_in,
	    R.anim.fragment_slide_out
        )
        transaction.replace(this.id, fragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()

        activity?.title = title
    }

    override fun getMetricsCategory(): Int {
        return MetricsProto.MetricsEvent.INFINITY
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.infinity_suite_title)
    }

    private fun openCustomizationPickerActivity() {
        val intent = Intent().apply {
            setClassName("com.android.wallpaper", "com.android.customization.picker.CustomizationPickerActivity")
        }
        startActivity(intent)
    }

    companion object {
        fun lockCurrentOrientation(activity: Activity) {
            val currentRotation = activity.windowManager.defaultDisplay.rotation
            val orientation = activity.resources.configuration.orientation
            val frozenRotation = when (currentRotation) {
                Surface.ROTATION_0 -> if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                Surface.ROTATION_90 -> if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                else
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Surface.ROTATION_180 -> if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                else
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                Surface.ROTATION_270 -> if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            activity.requestedOrientation = frozenRotation
        }
    }
}
