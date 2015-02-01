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

    public EffectFetch(String status, String requestId, String url) {
        this.status = status;
        this.requestId = requestId;
        this.url = url;
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

	
}
