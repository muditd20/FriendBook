package com.friendbook.config;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CaptchaValidator {

	private static final String SECRET_KEY = "6Lc0qtIrAAAAAOL5eJddn456ffJvsWJnIybn2gno";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean validateCaptcha(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isBlank()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", SECRET_KEY);
        params.add("response", captchaResponse);

        CaptchaResponse response =
                restTemplate.postForObject(VERIFY_URL, params, CaptchaResponse.class);

        return response != null && response.isSuccess();
    }
}
