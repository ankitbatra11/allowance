package com.abatra.android.allowance;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsentRequest {

    private final Activity activity;
    private final List<String> publisherIds = new ArrayList<>();
    private final List<String> testDevices = new ArrayList<>();
    @Nullable
    private DebugGeography debugGeography;
    @Nullable
    private String privacyPolicy;

    public ConsentRequest(Activity activity) {
        this.activity = activity;
    }

    public ConsentRequest addPublisherId(String publisherId) {
        publisherIds.add(publisherId);
        return this;
    }

    public List<String> getPublisherIds() {
        return publisherIds;
    }

    public ConsentRequest addTestDevice(String testDevice) {
        testDevices.add(testDevice);
        return this;
    }

    public List<String> getTestDevices() {
        return testDevices;
    }

    public ConsentRequest setDebugGeography(DebugGeography debugGeography) {
        this.debugGeography = debugGeography;
        return this;
    }

    public Optional<DebugGeography> getDebugGeography() {
        return Optional.ofNullable(debugGeography);
    }

    public Activity getActivity() {
        return activity;
    }

    public Optional<String> getPrivacyPolicy() {
        return Optional.ofNullable(privacyPolicy);
    }

    public void setPrivacyPolicy(@Nullable String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsentRequest{" +
                ", publisherIds=" + publisherIds +
                ", testDevices=" + testDevices +
                ", debugGeography=" + debugGeography +
                '}';
    }
}
