/**
 * 
 */
package com.interop.webapp;

/**
 * @author johnwarde
 *
 */
public class EffectFetch {

    private final String status;
    private final String requestId;
    private final String url;
    private final long elapsedTime;

    public EffectFetch(String status, String requestId, String url, long elapsedTime) {
        this.status = status;
        this.requestId = requestId;
        this.url = url;
        this.elapsedTime = elapsedTime;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }
    
    public String getUrl() {
        return url;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

	
}
