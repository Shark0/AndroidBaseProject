package com.shark.base.webservice;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class WebServiceTask<R> {

	protected long startTimeInMiles = 0;
	protected long endTimeInMiles = 0;

	private byte[] body;
	private Map<String, String> headers;

	protected boolean debug = false;

	public abstract HttpMethod getMethod();

	public abstract String generateServiceUrl();

	public byte[] generateBody() {
		return null;
	}

	public abstract Map<String, String> generateHttpHeaders();

	public abstract Type generateResultType();

	public abstract void onTaskSucceed(R entity);

	public abstract void onTaskFailed(WebServiceErrorType errorType);

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

	public void start() {
		startTimeInMiles = System.currentTimeMillis();
	}

	public void end() {
		endTimeInMiles = System.currentTimeMillis();
	}

	public long getConnectTime() {
		return endTimeInMiles - startTimeInMiles;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
}
