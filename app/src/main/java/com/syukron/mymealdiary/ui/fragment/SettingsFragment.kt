package com.syukron.mymealdiary.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.syukron.mymealdiary.MainActivity
import com.syukron.mymealdiary.R
import com.syukron.mymealdiary.util.ThemeProvider

class SettingsFragment : PreferenceFragmentCompat() {

    private val themeProvider by lazy { ThemeProvider(requireContext()) }
    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.theme_preferences_key))
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setThemePreference()
        setGoalPreference()
        setApiPreference()
    }

    private fun setThemePreference() {
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val theme = themeProvider.getTheme(newValue)
                    AppCompatDelegate.setDefaultNightMode(theme)
                }
                true
            }
    }

    private fun setGoalPreference() {
        val editTextPreference = preferenceManager
            .findPreference<EditTextPreference>(getString(R.string.goal_preferences_key))
        editTextPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    getString(R.string.goal_not_set)
                } else {
                    getString(R.string.goal_set, text)
                }
            }
        editTextPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    private fun setApiPreference() {
        val editTextPreference = preferenceManager.findPreference<EditTextPreference>(
            getString(R.string.api_preferences_key)
        )
        editTextPreference?.setOnPreferenceChangeListener { _, _ -> restartApp(); true }
        editTextPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                val defaultKey = getString(R.string.api_key)
                val defaultSummary = getString(R.string.api_key_summary)
                when {
                    TextUtils.isEmpty(text) -> {
                        setDefaultKey(defaultKey)
                        defaultSummary
                    }
                    text == defaultKey -> defaultSummary
                    else -> getString(R.string.api_key_summary_set, text)
                }
            }
    }

    private fun setDefaultKey(default: String) {
        val apiPreferenceKey = getString(R.string.api_preferences_key)
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .edit()
            .putString(apiPreferenceKey, default)
            .apply()
    }

    private fun restartApp() {
        val context = requireContext()
        val intent = Intent(context, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.finishAffinity()
    }
}