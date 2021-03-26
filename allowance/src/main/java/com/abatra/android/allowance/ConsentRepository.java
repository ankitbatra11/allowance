package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;

public interface ConsentRepository {

    LiveData<Resource<Consent>> getConsentStatusResourceLiveData();

    void loadConsentStatus(ConsentLoadRequest consentLoadRequest);

    void upsert(Consent consent);
}
