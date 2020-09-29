package com.vanced.manager.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.preference.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
import com.vanced.manager.BuildConfig.MANAGER_LANGUAGES
import com.vanced.manager.BuildConfig.MANAGER_LANGUAGE_NAMES
import com.vanced.manager.R
import com.vanced.manager.utils.LanguageHelper.getLanguageFormat
import java.io.File

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        activity?.title = getString(R.string.title_settings)
        setHasOptionsMenu(true)

        findPreference<Preference>("update_check")?.setOnPreferenceClickListener {
            UpdateCheckFragment().show(childFragmentManager.beginTransaction(), "Update Center")
            true
        }

        findPreference<SwitchPreference>("vanced_notifs")?.apply {
            title = getString(R.string.push_notifications, "Vanced")
            summary = getString(R.string.push_notifications_summary, "Vanced")
            setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    true -> FirebaseMessaging.getInstance().subscribeToTopic("Vanced-Update")
                    false -> FirebaseMessaging.getInstance().unsubscribeFromTopic("Vanced-Update")
                }
                true
            }
        }

        findPreference<SwitchPreference>("microg_notifs")?.apply {
            title = getString(R.string.push_notifications, "microG")
            summary = getString(R.string.push_notifications_summary, "microG")
            setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    true -> FirebaseMessaging.getInstance().subscribeToTopic("MicroG-Update")
                    false -> FirebaseMessaging.getInstance().unsubscribeFromTopic("MicroG-Update")
                }
                true
            }
        }

        findPreference<SwitchPreference>("firebase_analytics")?.setOnPreferenceChangeListener { _, newValue ->
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(newValue as Boolean)
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = newValue
            FirebaseAnalytics.getInstance(requireActivity()).setAnalyticsCollectionEnabled(newValue)
            true
        }

        val themePref = preferenceScreen.sharedPreferences.getString("theme_mode", "Follow System")
        findPreference<ListPreference>("theme_mode")?.apply {
            summary = when (themePref) {
                    "Light" -> getString(R.string.theme_light)
                    "Dark" -> getString(R.string.theme_dark)
                    else -> getString(R.string.theme_follow)
                }

            setOnPreferenceChangeListener { _, newValue ->
                if (themePref != newValue) {
                    requireActivity().recreate()
                    return@setOnPreferenceChangeListener true
                }
                false
            }
        }

        val accentPref = preferenceScreen.sharedPreferences.getString("accent_color", "Blue")
        findPreference<ListPreference>("accent_color")?.apply {
            summary = when (accentPref) {
                    "Blue" -> getString(R.string.accent_blue)
                    "Red" -> getString(R.string.accent_red)
                    "Green" -> getString(R.string.accent_green)
                    "Yellow" -> getString(R.string.accent_yellow)
                    else -> getString(R.string.accent_purple)
                }

            setOnPreferenceChangeListener { _, newValue ->
                if (accentPref != newValue) {
                    requireActivity().recreate()
                    return@setOnPreferenceChangeListener true
                }
                false
            }
        }

        val langPref = preferenceScreen.sharedPreferences.getString("manager_lang", "System Default")
        preferenceScreen.findPreference<ListPreference>("manager_lang")?.apply {
            summary = langPref?.let { getLanguageFormat(requireActivity(), it) }
            entries = arrayOf(getString(R.string.system_default)) + MANAGER_LANGUAGE_NAMES
            entryValues = arrayOf("System Default") + MANAGER_LANGUAGES

            setOnPreferenceChangeListener { _, newValue ->
                if (langPref != newValue) {
                    requireActivity().recreate()
                    return@setOnPreferenceChangeListener true
                }
                false
            }
        }

        findPreference<Preference>("vanced_chosen_modes")?.setOnPreferenceClickListener {
            ChosenPreferenceDialogFragment().show(childFragmentManager.beginTransaction(), "Chosen Preferences")
            true
        }

        findPreference<Preference>("clear_files")?.setOnPreferenceClickListener {
            with(requireActivity()) {
                listOf("apk", "apks").forEach { dir ->
                    File(getExternalFilesDir(dir)?.path as String).deleteRecursively()
                }
                Toast.makeText(this, getString(R.string.cleared_files), Toast.LENGTH_SHORT).show()
            }
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val devSettings = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("devSettings", false)
        if (devSettings) {
            inflater.inflate(R.menu.dev_settings_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

}