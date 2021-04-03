package com.abatra.android.allowance;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMediatorLiveData;
import com.abatra.android.wheelie.lifecycle.ResourceMutableLiveData;
import com.abatra.android.wheelie.thread.BoltsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Optional;
import java.util.concurrent.Executor;

import bolts.Task;
import timber.log.Timber;

import static com.abatra.android.wheelie.thread.SaferTask.callOn;

public class PreferenceConsentRepository implements ConsentRepository, OnSharedPreferenceChangeListener {

    static final String PREF_KEY = "prefConsentV2";
    static final String DEF_VALUE = "";

    private final ConsentRepository delegate;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    @Nullable
    private ResourceMutableLiveData<Consent> preferenceConsent;
    Executor backgroundExecutor = Task.BACKGROUND_EXECUTOR;

    private PreferenceConsentRepository(ConsentRepository delegate, SharedPreferences sharedPreferences, Gson gson) {
        this.delegate = delegate;
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
    }

    public static PreferenceConsentRepository newInstance(ConsentRepository delegate, SharedPreferences sharedPreferences) {
        return new PreferenceConsentRepository(delegate, sharedPreferences, new GsonBuilder().create());
    }

    @Override
    public void onCreate() {
        Timber.v("Registering shared preference listener");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        Timber.v("Unregistering shared preference listener");
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public LiveData<Resource<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
        ResourceMediatorLiveData<Consent> result = new ResourceMediatorLiveData<>();
        loadConsentStatus(consentLoadRequest, result);
        return result;
    }

    private void loadConsentStatus(ConsentLoadRequest consentLoadRequest, ResourceMediatorLiveData<Consent> result) {
        result.setLoading();
        preferenceConsent = new ResourceMutableLiveData<>();
        result.addSource(preferenceConsent, resource -> onPreferenceConsentLiveDataUpdated(consentLoadRequest, result, resource));
        updatePreferenceConsentLiveData(preferenceConsent);
    }

    private void updatePreferenceConsentLiveData(ResourceMutableLiveData<Consent> preferenceConsent) {
        callOn(backgroundExecutor, this::loadConsentFromPreference).continueOnUiThread(task -> {
            if (task.getError() != null) {
                preferenceConsent.setError(task.getError());
            } else {
                Optional<Consent> optionalConsent = BoltsUtils.getOptionalResultTaskResult(task);
                if (optionalConsent.isPresent()) {
                    preferenceConsent.setResourceValue(optionalConsent.get());
                } else {
                    preferenceConsent.setError(new RuntimeException("Consent not set yet!"));
                }
            }
            return null;
        });
    }

    private void onPreferenceConsentLiveDataUpdated(ConsentLoadRequest consentLoadRequest,
                                                    ResourceMediatorLiveData<Consent> result,
                                                    Resource<Consent> consentResource) {
        if (consentResource.getStatus() == Resource.Status.LOADED) {
            result.setValue(consentResource);
        } else {
            loadConsentFromNetwork(consentLoadRequest, result);
        }
    }

    private void loadConsentFromNetwork(ConsentLoadRequest request, ResourceMediatorLiveData<Consent> result) {
        result.addSource(delegate.loadConsentStatus(request), resource -> onNetworkConsentLiveDataUpdated(result, resource));
    }

    private void onNetworkConsentLiveDataUpdated(ResourceMediatorLiveData<Consent> result, Resource<Consent> resource) {
        if (resource.getStatus() == Resource.Status.LOADED) {
            upsert(resource.getData());
        } else {
            result.setValue(resource);
        }
    }

    private Optional<Consent> loadConsentFromPreference() {
        Consent consent = null;
        String consentString = sharedPreferences.getString(PREF_KEY, DEF_VALUE);
        if (!consentString.isEmpty()) {
            consent = gson.fromJson(consentString, Consent.class);
        }
        Timber.d("preference consent=%s", consent);
        return Optional.ofNullable(consent);
    }

    @Override
    public void upsert(Consent consent) {
        callOn(backgroundExecutor, () -> {
            Timber.d("Updating preference with consent=%s", consent);
            sharedPreferences.edit().putString(PREF_KEY, gson.toJson(consent)).apply();
            return null;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PREF_KEY.equals(key)) {
            Timber.i("Consent preference changed!");
            Optional.ofNullable(preferenceConsent).ifPresent(this::updatePreferenceConsentLiveData);
        }
    }
}
