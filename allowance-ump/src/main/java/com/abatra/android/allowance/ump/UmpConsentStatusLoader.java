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
        if (loadConsentStatusRequest.getDebugGeography() != null) {
            builder.setDebugGeography(UmpConsentUtils.mapDebugGeography(loadConsentStatusRequest.getDebugGeography()));
        }
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
                    if (loadConsentStatusRequest.getStatusLoaderListener() != null) {
                        loadConsentStatusRequest.getStatusLoaderListener().loadedSuccessfully(response);
                    }
                },
                formError -> {
                    UmpConsentUtils.report(formError, () -> "Requesting consent info update failed!");
                    RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                    if (loadConsentStatusRequest.getStatusLoaderListener() != null) {
                        loadConsentStatusRequest.getStatusLoaderListener().onConsentStatusLoadFailure(error);
                    }
                });
    }
}
