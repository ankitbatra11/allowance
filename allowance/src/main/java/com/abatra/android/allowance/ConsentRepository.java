package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.ILifecycleObserver;
import com.abatra.android.wheelie.lifecycle.Resource;

public interface ConsentRepository extends ILifecycleObserver {

    LiveData<Resource<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest);

    void upsert(Consent consent);
}
