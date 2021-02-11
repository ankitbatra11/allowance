package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusStore;
import com.abatra.android.allowance.LoadConsentStatusRequest;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

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
        loadConsentStatusRequest.getDebugGeography().ifPresent(dg -> {
            DebugGeography debugGeography = ConsentUtils.mapDebugGeography(dg);
            consentInformation.setDebugGeography(debugGeography);
        });
        Timber.d("loading consent status");
        consentInformation.requestConsentInfoUpdate(loadConsentStatusRequest.getPublisherIds().toArray(new String[0]), new ConsentInfoUpdateListener() {

            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                response = ConsentUtils.createResponse(consentInformation, consentStatus);
                loadConsentStatusRequest.getStatusLoaderListener().ifPresent(l -> l.loadedSuccessfully(response));
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Timber.e("onFailedToUpdateConsentInfo errorDescription=%s", errorDescription);
                loadConsentStatusRequest.getStatusLoaderListener().ifPresent(l -> {
                    RuntimeException error = new RuntimeException(errorDescription);
                    l.onConsentStatusLoadFailure(error);
                });
            }
        });
    }
}
