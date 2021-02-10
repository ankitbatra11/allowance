package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.ConsentStatusStore;
import com.abatra.android.allowance.LoadConsentStatusRequest;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import timber.log.Timber;

public class LegacyConsentStatusLoader extends AbstractConsentStatusLoader {

    private final Context context;
    @Nullable
    private ConsentInformation consentInformation;

    public LegacyConsentStatusLoader(ConsentStatusStore consentStatusStore, Context context) {
        super(consentStatusStore);
        this.context = context;
    }

    @Override
    public void loadConsentStatus(LoadConsentStatusRequest request) {

        consentInformation = ConsentInformation.getInstance(context);

        for (String testDevice : request.getTestDevices()) {
            consentInformation.addTestDevice(testDevice);
        }

        consentInformation.setDebugGeography(ConsentUtils.mapDebugGeography(request.getDebugGeography()));

        Timber.d("loading consent status");
        consentInformation.requestConsentInfoUpdate(request.getPublisherIds().toArray(new String[0]), new ConsentInfoUpdateListener() {

            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                ConsentStatusLoaderResponse response = ConsentUtils.createResponse(consentInformation, consentStatus);
                listeners.forEachObserver(type -> type.loadedSuccessfully(response));
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                RuntimeException error = new RuntimeException(errorDescription);
                Timber.e(error, "onFailedToUpdateConsentInfo");
                listeners.forEachObserver(type -> type.onConsentStatusLoadFailure(error));
            }
        });
    }


    @Nullable
    public ConsentInformation getConsentInformation() {
        return consentInformation;
    }

    @Override
    public void onDestroy() {
        consentInformation = null;
        super.onDestroy();
    }
}
