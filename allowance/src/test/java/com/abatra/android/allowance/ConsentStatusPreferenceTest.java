package com.abatra.android.allowance;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import bolts.Task;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.abatra.android.allowance.ConsentStatusType.OBTAINED;
import static com.abatra.android.allowance.ConsentType.PA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class ConsentStatusPreferenceTest {

    private ConsentStatusPreference preference;
    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preference = ConsentStatusPreference.create(sharedPreferences);
        preference.executor = Task.UI_THREAD_EXECUTOR;
    }

    @Test
    public void test_getConsent() {

        ConsentStatusLoaderResponse response = new ConsentStatusLoaderResponse(OBTAINED, PA, true);

        preference.loadedSuccessfully(response);
        Robolectric.flushForegroundThreadScheduler();

        assertThat(preference.getConsent(), notNullValue());
        assertThat(preference.getConsent().getConsentType(), equalTo(response.getConsentType()));
        assertThat(preference.getConsent().getConsentStatusType(), equalTo(response.getConsentStatusType()));

    }

    @Test
    public void test_getConsent_noPreferenceSaved() {

        sharedPreferences.edit().remove(ConsentStatusPreference.PREF_KEY).apply();

        assertThat(preference.getConsent(), nullValue());
    }

    @Test
    public void test_getConsent_gsonParseFailure() {

        sharedPreferences.edit().putString(ConsentStatusPreference.PREF_KEY, "invalidJson").apply();

        assertThat(preference.getConsent(), nullValue());
    }
}
