package com.abatra.android.allowance;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoadConsentStatusRequest {

    private final Activity activity;
    private final List<String> publisherIds = new ArrayList<>();
    private final List<String> testDevices = new ArrayList<>();
    @Nullable
    private DebugGeography debugGeography;
    @Nullable
    private ConsentStatusLoader.Listener statusLoaderListener;

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

    public Optional<DebugGeography> getDebugGeography() {
        return Optional.ofNullable(debugGeography);
    }

    public Activity getActivity() {
        return activity;
    }

    public LoadConsentStatusRequest setStatusLoaderListener(@Nullable ConsentStatusLoader.Listener statusLoaderListener) {
        this.statusLoaderListener = statusLoaderListener;
        return this;
    }

    public Optional<ConsentStatusLoader.Listener> getStatusLoaderListener() {
        return Optional.ofNullable(statusLoaderListener);
    }

    @NonNull
    @Override
    public String toString() {
        return "LoadConsentStatusRequest{" +
                "publisherIds=" + publisherIds +
                ", testDevices=" + testDevices +
                ", debugGeography=" + debugGeography +
                '}';
    }
}
