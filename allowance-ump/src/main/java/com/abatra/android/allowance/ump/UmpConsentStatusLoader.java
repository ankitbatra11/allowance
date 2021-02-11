package com.abatra.android.allowance.ump;

import android.content.Context;

import com.abatra.android.allowance.AbstractConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusStore;
import com.abatra.android.allowance.LoadConsentStatusRequest;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class UmpConsentStatusLoader extends AbstractConsentStatusLoader {

    private final Context context;

    public UmpConsentStatusLoader(ConsentStatusStore consentStatusStore, Context context) {
        super(consentStatusStore);
        this.context = context;
    }

    @Override
    protected void tryLoadingConsentStatus(LoadConsentStatusRequest loadConsentStatusRequest) {

        ConsentDebugSettings.Builder builder = new ConsentDebugSettings.Builder(context);
        loadConsentStatusRequest.getDebugGeography().ifPresent(dg -> {
            int debugGeography = UmpConsentUtils.mapDebugGeography(dg);
            builder.setDebugGeography(debugGeography);
        });
        for (String testDevice : loadConsentStatusRequest.getTestDevices()) {
            builder.addTestDeviceHashedId(testDevice);
        }
        ConsentDebugSettings consentDebugSettings = builder.build();

        ConsentRequestParameters parameters = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(consentDebugSettings)
                .build();

        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(context);
        consentInformation.requestConsentInfoUpdate(loadConsentStatusRequest.getActivity(), parameters,
                () -> {
                    response = UmpConsentUtils.createResponse(consentInformation);
                    loadConsentStatusRequest.getStatusLoaderListener().ifPresent(l -> l.loadedSuccessfully(response));
                },
                formError -> {
                    UmpConsentUtils.report(formError, () -> "Requesting consent info update failed!");
                    loadConsentStatusRequest.getStatusLoaderListener().ifPresent(l -> {
                        RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                        l.onConsentStatusLoadFailure(error);
                    });
                });
    }
}
