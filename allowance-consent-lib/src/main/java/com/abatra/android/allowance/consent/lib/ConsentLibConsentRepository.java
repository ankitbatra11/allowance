package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import com.abatra.android.allowance.AbstractConsentRepository;
import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentLoadRequest;
import com.abatra.android.allowance.ConsentType;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import timber.log.Timber;

import static com.abatra.android.allowance.consent.lib.ConsentUtils.createConsent;

public class ConsentLibConsentRepository extends AbstractConsentRepository {

    private final Context context;

    public ConsentLibConsentRepository(Context context) {
        this.context = context;
    }

    @Override
    protected void tryLoadingConsentStatus(ConsentLoadRequest consentLoadRequest) {

        ConsentLibConsentLoadRequest loadRequest = (ConsentLibConsentLoadRequest) consentLoadRequest;

        ConsentInformation consentInformation = ConsentInformation.getInstance(context);

        for (String testDevice : loadRequest.getTestDevices()) {
            consentInformation.addTestDevice(testDevice);
        }

        loadRequest.getDebugGeography().ifPresent(dg -> {
            DebugGeography debugGeography = ConsentUtils.mapDebugGeography(dg);
            consentInformation.setDebugGeography(debugGeography);
        });

        String[] publisherIds = loadRequest.getPublisherIds().toArray(new String[0]);

        Timber.d("loading consent with req=%s", loadRequest);
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {

            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Timber.d("onConsentInfoUpdated consentStatus=%s", consentStatus);
                try {
                    ConsentLibConsentRepository.this.consentStatus.setResourceValue(createConsent(consentStatus));
                } catch (Throwable error) {
                    Timber.e(error);
                    ConsentLibConsentRepository.this.consentStatus.setError(error);
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Timber.e("onFailedToUpdateConsentInfo errorDescription=%s", errorDescription);
                ConsentLibConsentRepository.this.consentStatus.setError(new RuntimeException(errorDescription));
            }
        });
    }


}
