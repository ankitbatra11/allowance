package com.abatra.android.allowance.consent.lib;

import com.abatra.android.allowance.IConsentForm;
import com.google.ads.consent.ConsentForm;

public class ConsentLibConsentForm implements IConsentForm {

    private final ConsentForm consentForm;

    public ConsentLibConsentForm(ConsentForm consentForm) {
        this.consentForm = consentForm;
    }

    @Override
    public void show() {
        consentForm.show();
    }
}
