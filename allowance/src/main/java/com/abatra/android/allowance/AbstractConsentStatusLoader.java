package com.abatra.android.allowance;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;
import com.abatra.android.wheelie.pattern.Observable;

abstract public class AbstractConsentStatusLoader implements ConsentStatusLoader {

    private final ConsentStatusStore consentStatusStore;
    protected final Observable<Listener> listeners = Observable.copyOnWriteArraySet();

    protected AbstractConsentStatusLoader(ConsentStatusStore consentStatusStore) {
        this.consentStatusStore = consentStatusStore;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        addObserver(consentStatusStore);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void addObserver(Listener observer) {
        listeners.addObserver(observer);
    }

    @Override
    public void removeObserver(Listener observer) {
        listeners.removeObserver(observer);
    }
}
