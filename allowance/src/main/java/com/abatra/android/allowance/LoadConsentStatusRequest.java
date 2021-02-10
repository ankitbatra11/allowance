package com.abatra.android.allowance;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoadConsentStatusRequest {

    private final Activity activity;
    private final List<String> publisherIds = new ArrayList<>();
    private final List<String> testDevices = new ArrayList<>();
    @Nullable
    private DebugGeography debugGeography;

    public LoadConsentStatusRequest(Activity activity) {
        this.activity = activity;
    }

    public LoadConsentStatusRequest addPublisherId(String publisherId) {
        publisherIds.add(publisherId);
        return this;
    }

    public List<String> getPublisherIds() {
        return publisherIds;
    }

    public LoadConsentStatusRequest addTestDevice(String testDevice) {
        testDevices.add(testDevice);
        return this;
    }

    public List<String> getTestDevices() {
        return testDevices;
    }

    public LoadConsentStatusRequest setDebugGeography(DebugGeography debugGeography) {
        this.debugGeography = debugGeography;
        return this;
    }

    @Nullable
    public DebugGeography getDebugGeography() {
        return debugGeography;
    }

    public Activity getActivity() {
        return activity;
    }
}
