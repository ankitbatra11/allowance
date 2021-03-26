package com.abatra.android.allowance.consent.lib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abatra.android.allowance.ConsentLoadRequest;
import com.abatra.android.allowance.DebugGeography;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsentLibConsentLoadRequest implements ConsentLoadRequest {

    private final List<String> publisherIds = new ArrayList<>();
    private final List<String> testDevices = new ArrayList<>();
    @Nullable
    private DebugGeography debugGeography;

    public ConsentLibConsentLoadRequest addPublisherId(String publisherId) {
        publisherIds.add(publisherId);
        return this;
    }

    public List<String> getPublisherIds() {
        return publisherIds;
    }

    public ConsentLibConsentLoadRequest addTestDevice(String testDevice) {
        testDevices.add(testDevice);
        return this;
    }

    public List<String> getTestDevices() {
        return testDevices;
    }

    public ConsentLibConsentLoadRequest setDebugGeography(DebugGeography debugGeography) {
        this.debugGeography = debugGeography;
        return this;
    }

    public Optional<DebugGeography> getDebugGeography() {
        return Optional.ofNullable(debugGeography);
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsentLibConsentLoadRequest{" +
                ", publisherIds=" + publisherIds +
                ", testDevices=" + testDevices +
                ", debugGeography=" + debugGeography +
                '}';
    }
}
