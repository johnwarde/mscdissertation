package com.interop.webapp;


public class EffectRequest {

    private final String status;
    private final String requestId;
    private final long created;

    public EffectRequest(String status, String requestId, long created) {
        this.status = status;
        this.requestId = requestId;
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }

    public long getCreated() {
        return created;
    }
}
