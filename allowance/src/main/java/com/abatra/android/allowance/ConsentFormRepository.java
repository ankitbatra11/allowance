package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.core.Lce;
import com.abatra.android.wheelie.lifecycle.observer.ILifecycleObserver;

public interface ConsentFormRepository extends ILifecycleObserver {

    LiveData<Lce<Boolean>> loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest);

    void showConsentForm(ConsentFormCallback consentFormCallback);
}
