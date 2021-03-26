package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;

import java.util.Optional;

public interface ConsentFormRepository {

    LiveData<Resource<Boolean>> getConsentFormResourceLiveData();

    void loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest);

    Optional<IConsentForm> getLoadedConsentForm();
}
