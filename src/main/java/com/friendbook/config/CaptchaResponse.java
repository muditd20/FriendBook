package com.friendbook.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CaptchaResponse {
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getChallengeTs() {
        return challengeTs;
    }
    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
