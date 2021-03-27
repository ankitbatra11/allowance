package com.abatra.android.allowance.consent.lib;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormRepository;
import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentFormCallback;
import com.abatra.android.allowance.ConsentFormLoadRequest;
import com.abatra.android.allowance.ConsentRepository;
import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMediatorLiveData;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import timber.log.Timber;

import static com.abatra.android.allowance.consent.lib.ConsentLibUtils.createConsent;

public class ConsentLibConsentFormRepository extends AbstractConsentFormRepository {

    @Nullable
    private ConsentForm consentForm;

    @Nullable
    private LiveDataConsentFormListener consentFormListener;

    public ConsentLibConsentFormRepository(ConsentRepository consentRepository) {
        super(consentRepository);
    }

    @Override
    protected void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest,
                                         ResourceMediatorLiveData<Boolean> result) {

        ConsentLibConsentFormLoadRequest loadRequest = (ConsentLibConsentFormLoadRequest) consentFormLoadRequest;

        consentFormListener = new LiveDataConsentFormListener(result);

        URL privacyPolicyURL = getPrivacyPolicyUrlOrThrow(loadRequest.getPrivacyPolicyUrl());
        consentForm = new ConsentForm.Builder(loadRequest.getContext(), privacyPolicyURL)
                .withListener(consentFormListener)
                .withNonPersonalizedAdsOption()
                .withPersonalizedAdsOption()
                .build();

        consentForm.load();
    }

    private URL getPrivacyPolicyUrlOrThrow(String privacyPolicy) {
        try {
            return new URL(privacyPolicy);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid policy url=" + privacyPolicy, e);
        }
    }

    @Override
    public void showConsentForm(ConsentFormCallback consentFormCallback) {
        Optional.ofNullable(consentFormListener).ifPresent(l -> l.setConsentFormCallback(consentFormCallback));
        Optional.ofNullable(consentForm).ifPresent(ConsentForm::show);
    }

    @Override
    public void onDestroy() {
        consentFormListener = null;
        consentForm = null;
        super.onDestroy();
    }

    private class LiveDataConsentFormListener extends ConsentFormListener {

        private final ResourceMediatorLiveData<Boolean> loadStatusLiveData;
        private ConsentFormCallback consentFormCallback;

        private LiveDataConsentFormListener(ResourceMediatorLiveData<Boolean> loadStatusLiveData) {
            this.loadStatusLiveData = loadStatusLiveData;
        }

        public void setConsentFormCallback(ConsentFormCallback consentFormCallback) {
            this.consentFormCallback = consentFormCallback;
        }

        @Override
        public void onConsentFormLoaded() {
            super.onConsentFormLoaded();
            Timber.d("onConsentFormLoaded");
            loadStatusLiveData.postResourceValue(true);
        }

        @Override
        public void onConsentFormError(String reason) {
            super.onConsentFormError(reason);
            Timber.e("onConsentFormError reason=%s", reason);
            loadStatusLiveData.postError(new RuntimeException(reason));
        }

        @Override
        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
            super.onConsentFormClosed(consentStatus, userPrefersAdFree);

            Timber.i("consent=%s userPrefersAdFreeOption=%b", consentStatus, userPrefersAdFree);

            Consent consent = createConsent(consentStatus);

            if (isLoadFormOnClose()) {

                consentForm = null;
                consentFormListener = null;

                loadStatusLiveData.setLoading();
                checkConsentAndLoadForm(Resource.loaded(consent), loadStatusLiveData);
            }
            consentFormCallback.onConsentFormDismissed(consent, Boolean.TRUE.equals(userPrefersAdFree));
        }

        private boolean isLoadFormOnClose() {
            return getConsentFormLoadRequest()
                    .map(r -> (ConsentLibConsentFormLoadRequest) r)
                    .map(ConsentLibConsentFormLoadRequest::isLoadFormOnClose)
                    .orElse(false);
        }
    }
}
