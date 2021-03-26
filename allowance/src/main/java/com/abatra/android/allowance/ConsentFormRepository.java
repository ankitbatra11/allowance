package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.ILifecycleObserver;
import com.abatra.android.wheelie.lifecycle.Resource;

import java.util.Optional;

public interface ConsentFormRepository extends ILifecycleObserver {

    LiveData<Resource<Boolean>> loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest);

    Optional<IConsentForm> getLoadedConsentForm();
}
