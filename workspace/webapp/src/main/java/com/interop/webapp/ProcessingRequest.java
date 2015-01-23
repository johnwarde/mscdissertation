/**
 * 
 */
package com.interop.webapp;


/**
 * @author johnwarde
 *
 */
public abstract class ProcessingRequest {
	protected String queueId;
	protected String queueMsg;
	protected String requestId;
	public String getQueueId() {
		return queueId;
	}
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	public String getQueueMsg() {
		return queueMsg;
	}
	public void setQueueMsg(String queueMsg) {
		this.queueMsg = queueMsg;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	abstract public Boolean send();
		
}
