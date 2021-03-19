package com.abatra.android.allowance;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class ConsentRequestTest {

    private ConsentRequest consentRequest;

    @Mock
    private Activity mockedActivity;

    @Before
    public void setup() {
        consentRequest = new ConsentRequest(mockedActivity);
    }

    @Test
    public void test_constructor_nullActivity() {
        assertThrows(NullPointerException.class, () -> new ConsentRequest(null));
    }

    @Test
    public void test_constructor_nonNullActivity() {
        assertThat(consentRequest.getActivity(), sameInstance(mockedActivity));
    }

    @Test
    public void test_addPublisherId() {

        consentRequest.addPublisherId("pub1");
        consentRequest.addPublisherId("pub2");

        assertThat(consentRequest.getPublisherIds(), hasSize(2));

        assertThat(consentRequest.getPublisherIds().get(0), equalTo("pub1"));
        assertThat(consentRequest.getPublisherIds().get(1), equalTo("pub2"));
    }

    @Test
    public void test_addTestDevice() {

        consentRequest.addTestDevice("testDevice1");
        consentRequest.addTestDevice("testDevice2");

        assertThat(consentRequest.getTestDevices(), hasSize(2));

        assertThat(consentRequest.getTestDevices().get(0), equalTo("testDevice1"));
        assertThat(consentRequest.getTestDevices().get(1), equalTo("testDevice2"));
    }

    @Test
    public void test_setDebugGeography_nullValue() {

        consentRequest.setDebugGeography(null);

        assertThat(consentRequest.getDebugGeography().isPresent(), equalTo(false));
    }

    @Test
    public void test_setDebugGeography_nonNullValue() {

        consentRequest.setDebugGeography(DebugGeography.EEA);

        assertThat(consentRequest.getDebugGeography().isPresent(), equalTo(true));
        assertThat(consentRequest.getDebugGeography().get(), equalTo(DebugGeography.EEA));
    }

    @Test
    public void test_setPrivacyPolicy_nullValue() {

        consentRequest.setPrivacyPolicy(null);

        assertThat(consentRequest.getPrivacyPolicy().isPresent(), equalTo(false));
    }

    @Test
    public void test_setPrivacyPolicy_nonNullValue() {

        String expectedPolicy = "privacyPolicy";

        consentRequest.setPrivacyPolicy(expectedPolicy);

        assertThat(consentRequest.getPrivacyPolicy().isPresent(), equalTo(true));
        assertThat(consentRequest.getPrivacyPolicy().get(), equalTo(expectedPolicy));
    }

}
