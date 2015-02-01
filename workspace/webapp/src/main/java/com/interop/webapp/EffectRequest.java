package com.interop.webapp;


public class EffectRequest {

    private final String status;
    private final String requestId;

    public EffectRequest(String status, String requestId) {
        this.status = status;
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }
}
