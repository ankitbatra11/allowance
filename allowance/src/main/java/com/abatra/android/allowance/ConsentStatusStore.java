package com.abatra.android.allowance;

import androidx.annotation.Nullable;

public interface ConsentStatusStore extends ConsentStatusLoader.Listener {
    @Nullable
    ConsentStatusLoaderResponse getConsent();
}
