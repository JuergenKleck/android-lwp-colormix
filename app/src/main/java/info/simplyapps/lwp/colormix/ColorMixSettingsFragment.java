package info.simplyapps.lwp.colormix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

public class ColorMixSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.colormix_settings, rootKey);

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(ColorConstants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        SeekBarPreference sbTLCR = findPreference("tlcred");
        SeekBarPreference sbTLCG = findPreference("tlcgreen");
        SeekBarPreference sbTLCB = findPreference("tlcblue");
        SeekBarPreference sbBLCR = findPreference("blcred");
        SeekBarPreference sbBLCG = findPreference("blcgreen");
        SeekBarPreference sbBLCB = findPreference("blcblue");
        SeekBarPreference sbTRCR = findPreference("trcred");
        SeekBarPreference sbTRCG = findPreference("trcgreen");
        SeekBarPreference sbTRCB = findPreference("trcblue");
        SeekBarPreference sbBRCR = findPreference("brcred");
        SeekBarPreference sbBRCG = findPreference("brcgreen");
        SeekBarPreference sbBRCB = findPreference("brcblue");

        sbTLCR.setValue(sharedPreferences.getInt(ColorConstants.TOP_LEFT_RED, 0));
        sbTLCG.setValue(sharedPreferences.getInt(ColorConstants.TOP_LEFT_GREEN, 0));
        sbTLCB.setValue(sharedPreferences.getInt(ColorConstants.TOP_LEFT_BLUE, 0));
        sbBLCR.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_LEFT_RED, 0));
        sbBLCG.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_LEFT_GREEN, 0));
        sbBLCB.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_LEFT_BLUE, 0));
        sbTRCR.setValue(sharedPreferences.getInt(ColorConstants.TOP_RIGHT_RED, 0));
        sbTRCG.setValue(sharedPreferences.getInt(ColorConstants.TOP_RIGHT_GREEN, 0));
        sbTRCB.setValue(sharedPreferences.getInt(ColorConstants.TOP_RIGHT_BLUE, 0));
        sbBRCR.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_RIGHT_RED, 0));
        sbBRCG.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_RIGHT_GREEN, 0));
        sbBRCB.setValue(sharedPreferences.getInt(ColorConstants.BOTTOM_RIGHT_BLUE, 0));

        sbTLCR.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_LEFT_RED, (Integer) newValue).apply();
            return true;
        });
        sbTLCG.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_LEFT_GREEN, (Integer) newValue).apply();
            return true;
        });
        sbTLCB.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_LEFT_BLUE, (Integer) newValue).apply();
            return true;
        });

        sbBLCR.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_LEFT_RED, (Integer) newValue).apply();
            return true;
        });
        sbBLCG.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_LEFT_GREEN, (Integer) newValue).apply();
            return true;
        });
        sbBLCB.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_LEFT_BLUE, (Integer) newValue).apply();
            return true;
        });

        sbTRCR.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_RIGHT_RED, (Integer) newValue).apply();
            return true;
        });
        sbTRCG.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_RIGHT_GREEN, (Integer) newValue).apply();
            return true;
        });
        sbTRCB.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.TOP_RIGHT_BLUE, (Integer) newValue).apply();
            return true;
        });

        sbBRCR.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_RIGHT_RED, (Integer) newValue).apply();
            return true;
        });
        sbBRCG.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_RIGHT_GREEN, (Integer) newValue).apply();
            return true;
        });
        sbBRCB.setOnPreferenceChangeListener((preference, newValue) -> {
            sharedPreferences.edit().putInt(ColorConstants.BOTTOM_RIGHT_BLUE, (Integer) newValue).apply();
            return true;
        });

    }

}
