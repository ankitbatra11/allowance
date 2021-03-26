package com.abatra.android.allowance.demo;

import android.content.Context;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.DebugGeography;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormLoadRequest;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentLoadRequest;

import java.util.Collection;

public interface Utils {

    String PUBLISHER_ID = "";
    String TEST_DEVICE = "98612BBF1433B7833F375AE392714233";
    String PRIVACY_POLICY = "http://www.app.com/privacyPolicy";

    ConsentLibConsentLoadRequest CONSENT_LOAD_REQUEST = new ConsentLibConsentLoadRequest()
            .addPublisherId(Utils.PUBLISHER_ID)
            .addTestDevice(TEST_DEVICE)
            .setDebugGeography(DebugGeography.EEA);

    static ConsentLibConsentFormLoadRequest createFormLoadRequest(Context context, Collection<Consent.Status> statuses) {
        return new ConsentLibConsentFormLoadRequest(
                CONSENT_LOAD_REQUEST,
                statuses,
                context,
                PRIVACY_POLICY
        );
    }
}
