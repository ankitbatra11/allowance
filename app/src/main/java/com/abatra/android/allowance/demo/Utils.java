package com.abatra.android.allowance.demo;

import android.content.Context;

import com.abatra.android.allowance.DebugGeography;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormLoadRequest;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentLoadRequest;

public interface Utils {

    String PUBLISHER_ID = "pub-4285683658805312";
    String TEST_DEVICE = "98612BBF1433B7833F375AE392714233";
    String PRIVACY_POLICY = "http://www.app.com/privacyPolicy";

    ConsentLibConsentLoadRequest CONSENT_LOAD_REQUEST = new ConsentLibConsentLoadRequest()
            .addPublisherId(Utils.PUBLISHER_ID)
            .addTestDevice(TEST_DEVICE)
            .setDebugGeography(DebugGeography.EEA);

    static ConsentLibConsentFormLoadRequest obtainConsentFormLoadRequest(Context context) {
        return ConsentLibConsentFormLoadRequest.obtainConsent(CONSENT_LOAD_REQUEST, context, PRIVACY_POLICY);
    }

    static ConsentLibConsentFormLoadRequest updateConsentFormLoadRequest(Context context) {
        return ConsentLibConsentFormLoadRequest.updateConsent(CONSENT_LOAD_REQUEST, context, PRIVACY_POLICY);
    }
}
