package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Lce;
import com.abatra.android.wheelie.lifecycle.observer.ILifecycleObserver;

public interface ConsentRepository extends ILifecycleObserver {

    LiveData<Lce<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest);

    void upsert(Consent consent);
}
