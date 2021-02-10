package com.abatra.android.allowance.ump;

import android.content.Context;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.ConsentStatusStore;
import com.abatra.android.allowance.LoadConsentStatusRequest;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class UmpConsentStatusLoader extends AbstractConsentStatusLoader {

    private static final String ONE_PLUS_DEVICE_HASH = "98612BBF1433B7833F375AE392714233";
    private static final String PIXEL_4_PLAY_STORE_DEVICE_HASH = "D89E8AA819D4DD40B7CC83D165D3D994";

    private final Context context;

    @Nullable
    private ConsentInformation consentInformation;

    public UmpConsentStatusLoader(ConsentStatusStore consentStatusStore, Context context) {
        super(consentStatusStore);
        this.context = context;
    }

    @Override
    public void loadConsentStatus(LoadConsentStatusRequest request) {
        if (consentInformation != null) {
            notifyLoadedConsentStatus(consentInformation);
        } else {
            doLoadConsentStatus(request);
        }
    }

    private void notifyLoadedConsentStatus(ConsentInformation consentInformation) {
        ConsentStatusLoaderResponse response = UmpConsentUtils.createResponse(consentInformation);
        listeners.forEachObserver(type -> type.loadedSuccessfully(response));
    }

    private void doLoadConsentStatus(LoadConsentStatusRequest request) {

        ConsentDebugSettings consentDebugSettings = new ConsentDebugSettings.Builder(context)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(ONE_PLUS_DEVICE_HASH)
                .addTestDeviceHashedId(PIXEL_4_PLAY_STORE_DEVICE_HASH)
                .build();

        ConsentRequestParameters parameters = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(consentDebugSettings)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(context);
        consentInformation.requestConsentInfoUpdate(request.getActivity(), parameters,
                () -> {
                    if (consentInformation != null) {
                        notifyLoadedConsentStatus(consentInformation);
                    }
                },
                formError -> {
                    UmpConsentUtils.report(formError, () -> "Requesting consent info update failed!");
                    RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                    listeners.forEachObserver(type -> type.onConsentStatusLoadFailure(error));
                });
    }

    @Override
    public void onDestroy() {
        consentInformation = null;
        super.onDestroy();
    }
}
