package com.abatra.android.allowance.demo;

import com.abatra.android.allowance.DebugGeography;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentLoadRequest;

public interface Constants {

    String PUBLISHER_ID = "pub-4285683658805312";
    String TEST_DEVICE = "98612BBF1433B7833F375AE392714233";

    ConsentLibConsentLoadRequest CONSENT_LOAD_REQUEST = new ConsentLibConsentLoadRequest()
            .addPublisherId(Constants.PUBLISHER_ID)
            .addTestDevice(TEST_DEVICE)
            .setDebugGeography(DebugGeography.EEA);
}
