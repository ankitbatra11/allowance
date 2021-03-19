package com.abatra.android.allowance;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;

import bolts.Task;
import timber.log.Timber;

import static com.abatra.android.wheelie.thread.SaferTask.callOn;

public class ConsentStatusPreference implements ConsentStatusStore {

    static final String PREF_KEY = "pref_consent_status_json";

    private final SharedPreferences preferences;
    private final Gson gson;
    Executor executor = Task.BACKGROUND_EXECUTOR;

    private ConsentStatusPreference(SharedPreferences preferences, Gson gson) {
        this.preferences = preferences;
        this.gson = gson;
    }

    public static ConsentStatusPreference create(SharedPreferences sharedPreferences) {
        return new ConsentStatusPreference(sharedPreferences, new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create());
    }

    @Override
    public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
        callOn(executor, () -> {
            String value = gson.toJson(response);
            preferences.edit().putString(PREF_KEY, value).apply();
            Timber.d("saved consent=%s json=%s", response, value);
            return null;
        });
    }

    @Override
    public ConsentStatusLoaderResponse getConsent() {
        ConsentStatusLoaderResponse response = null;
        if (preferences.contains(PREF_KEY)) {
            try {
                response = tryGettingResponse();
            } catch (Throwable error) {
                Timber.e(error, "Failed to get response from preferences.");
            }
        }
        Timber.d("loaded consent=%s", response);
        return response;
    }

    private ConsentStatusLoaderResponse tryGettingResponse() {
        return gson.fromJson(preferences.getString(PREF_KEY, ""), ConsentStatusLoaderResponse.class);
    }
}
