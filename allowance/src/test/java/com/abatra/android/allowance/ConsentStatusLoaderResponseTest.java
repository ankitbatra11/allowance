package com.abatra.android.allowance;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ConsentStatusLoaderResponseTest {

    private ConsentStatusLoaderResponse response;

    @Before
    public void setup() {
        response = new ConsentStatusLoaderResponse(ConsentStatusType.REQUIRED, ConsentType.NPA, true);
    }

    @Test
    public void test_isConsentRequired() {

        assertThat(response.isConsentRequired(), equalTo(true));

        response = new ConsentStatusLoaderResponse(ConsentStatusType.NOT_REQUIRED, null, false);

        assertThat(response.isConsentRequired(), equalTo(false));
    }

    @Test
    public void test_isConsentAcquired() {

        assertThat(response.isConsentAcquired(), equalTo(false));

        response = new ConsentStatusLoaderResponse(ConsentStatusType.OBTAINED, null, false);

        assertThat(response.isConsentAcquired(), equalTo(true));
    }

    @Test
    public void test_isConsentFormAvailable() {

        assertThat(response.isConsentFormAvailable(), equalTo(true));

        response = new ConsentStatusLoaderResponse(null, null, false);

        assertThat(response.isConsentFormAvailable(), equalTo(false));
    }

    @Test
    public void test_loadAds() {

        assertThat(response.loadAds(), equalTo(false));

        response = new ConsentStatusLoaderResponse(ConsentStatusType.OBTAINED, null, false);

        assertThat(response.loadAds(), equalTo(true));

        response = new ConsentStatusLoaderResponse(ConsentStatusType.NOT_REQUIRED, null, false);

        assertThat(response.loadAds(), equalTo(true));

    }
}
