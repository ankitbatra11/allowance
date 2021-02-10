package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.ConsentStatusStore;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import timber.log.Timber;

public class LegacyConsentStatusLoader extends AbstractConsentStatusLoader {

    private static final String[] PUBLISHERS = new String[]{"pub-4285683658805312"};
    private static final String HASH_ONE_PLUS = "98612BBF1433B7833F375AE392714233";

    private final Context context;
    @Nullable
    private ConsentInformation consentInformation;

    LegacyConsentStatusLoader(ConsentStatusStore consentStatusStore, Context context) {
        super(consentStatusStore);
        this.context = context;
    }

    @Override
    public void loadConsentStatus(Request request) {

        consentInformation = ConsentInformation.getInstance(context);
        consentInformation.addTestDevice(HASH_ONE_PLUS);
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        Timber.d("loading consent status");
        consentInformation.requestConsentInfoUpdate(PUBLISHERS, new ConsentInfoUpdateListener() {

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
