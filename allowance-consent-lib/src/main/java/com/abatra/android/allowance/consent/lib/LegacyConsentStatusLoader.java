package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusStore;
import com.abatra.android.allowance.LoadConsentStatusRequest;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import timber.log.Timber;

public class LegacyConsentStatusLoader extends AbstractConsentStatusLoader {

    private final Context context;

    public LegacyConsentStatusLoader(ConsentStatusStore consentStatusStore, Context context) {
        super(consentStatusStore);
        this.context = context;
    }

    @Override
    protected void tryLoadingConsentStatus(LoadConsentStatusRequest loadConsentStatusRequest) {

        ConsentInformation consentInformation = ConsentInformation.getInstance(context);

        for (String testDevice : loadConsentStatusRequest.getTestDevices()) {
            consentInformation.addTestDevice(testDevice);
        }

        consentInformation.setDebugGeography(ConsentUtils.mapDebugGeography(loadConsentStatusRequest.getDebugGeography()));

        Timber.d("loading consent status");
        consentInformation.requestConsentInfoUpdate(loadConsentStatusRequest.getPublisherIds().toArray(new String[0]), new ConsentInfoUpdateListener() {

            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                response = ConsentUtils.createResponse(consentInformation, consentStatus);
                if (loadConsentStatusRequest.getStatusLoaderListener() != null) {
                    loadConsentStatusRequest.getStatusLoaderListener().loadedSuccessfully(response);
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                RuntimeException error = new RuntimeException(errorDescription);
                Timber.e(error, "onFailedToUpdateConsentInfo");
                if (loadConsentStatusRequest.getStatusLoaderListener() != null) {
                    loadConsentStatusRequest.getStatusLoaderListener().onConsentStatusLoadFailure(error);
                }
            }
        });
    }
}
