package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import com.abatra.android.allowance.AbstractConsentRepository;
import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentLoadRequest;
import com.abatra.android.wheelie.lifecycle.liveData.LceMutableLiveData;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import timber.log.Timber;

public class ConsentLibConsentRepository extends AbstractConsentRepository {

    private final Context context;
    private final ConsentFactory consentFactory;

    public ConsentLibConsentRepository(Context context, ConsentFactory consentFactory) {
        this.context = context;
        this.consentFactory = consentFactory;
    }

    @Override
    protected void tryLoadingConsentStatus(ConsentLoadRequest consentLoadRequest, LceMutableLiveData<Consent> result) {

        ConsentLibConsentLoadRequest loadRequest = (ConsentLibConsentLoadRequest) consentLoadRequest;

        ConsentInformation consentInformation = ConsentInformation.getInstance(context);

        for (String testDevice : loadRequest.getTestDevices()) {
            consentInformation.addTestDevice(testDevice);
        }

        loadRequest.getDebugGeography().ifPresent(dg -> {
            DebugGeography debugGeography = ConsentLibUtils.mapDebugGeography(dg);
            consentInformation.setDebugGeography(debugGeography);
        });

        String[] publisherIds = loadRequest.getPublisherIds().toArray(new String[0]);

        Timber.d("loading consent with req=%s", loadRequest);
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {

            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Timber.d("onConsentInfoUpdated consentStatus=%s", consentStatus);
                try {
                    result.postResourceValue(consentFactory.createConsent(consentStatus));
                } catch (Throwable error) {
                    Timber.e(error);
                    result.postError(error);
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Timber.e("onFailedToUpdateConsentInfo errorDescription=%s", errorDescription);
                result.postError(new RuntimeException(errorDescription));
            }
        });
    }

    @Override
    public void upsert(Consent consent) {
        Timber.w("Unsupported operation!");
    }
}
