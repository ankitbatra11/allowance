package com.abatra.android.allowance.consent.lib;

import com.abatra.android.allowance.AbstractConsentFormRepository;
import com.abatra.android.allowance.ConsentFormLoadRequest;
import com.abatra.android.allowance.ConsentRepository;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

import static com.abatra.android.allowance.consent.lib.ConsentUtils.*;

public class ConsentLibConsentFormRepository extends AbstractConsentFormRepository {

    public ConsentLibConsentFormRepository(ConsentRepository consentRepository) {
        super(consentRepository);
    }

    @Override
    protected void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest) {

        ConsentLibConsentFormLoadRequest loadRequest = (ConsentLibConsentFormLoadRequest) consentFormLoadRequest;

        URL privacyPolicyURL = getPrivacyPolicyUrlOrThrow(loadRequest.getPrivacyPolicyUrl());
        ConsentForm consentForm = new ConsentForm.Builder(loadRequest.getContext(), privacyPolicyURL)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        super.onConsentFormLoaded();
                        Timber.d("onConsentFormLoaded");
                        ConsentLibConsentFormRepository.this.consentForm.setResourceValue(true);
                    }

                    @Override
                    public void onConsentFormError(String reason) {
                        super.onConsentFormError(reason);
                        Timber.e("onConsentFormError reason=%s", reason);
                        ConsentLibConsentFormRepository.this.consentForm.setError(new RuntimeException(reason));
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        super.onConsentFormClosed(consentStatus, userPrefersAdFree);
                        Timber.i("onConsentFormClosed consentStatus=%s userPrefersAdFree=%b", consentStatus, userPrefersAdFree);
                        consentRepository.upsert(createConsent(consentStatus));
                        weakConsentForm = null;
                        if (loadRequest.isLoadFormOnClose()) {
                            loadConsentForm(consentFormLoadRequest);
                        }
                    }
                })
                .withNonPersonalizedAdsOption()
                .withPersonalizedAdsOption()
                .build();

        weakConsentForm = new WeakReference<>(new ConsentLibConsentForm(consentForm));
        consentForm.load();
    }

    private URL getPrivacyPolicyUrlOrThrow(String privacyPolicy) {
        try {
            return new URL(privacyPolicy);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid policy url=" + privacyPolicy, e);
        }
    }
}
