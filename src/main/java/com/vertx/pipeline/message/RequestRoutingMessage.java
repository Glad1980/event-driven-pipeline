/**
 *
 */
package com.vertx.pipeline.message;

import java.util.List;

import io.vertx.core.json.JsonObject;

/**
 * @author aalrbee
 */
public class RequestRoutingMessage extends AbstractMessage {

	private final JsonObject requestBody;


	public RequestRoutingMessage(String messageSubject, List<Long> timeHistory, String correlationId, JsonObject requestBody) {
		super(messageSubject, timeHistory, correlationId);
		this.requestBody = requestBody;
	}
	public RequestRoutingMessage(String messageSubject, List<Long> timeHistory, JsonObject requestBody,String correlationId) {
		super(messageSubject, timeHistory,correlationId);
		this.requestBody = requestBody;
	}
	public RequestRoutingMessage(AbstractMessage am, String messageSubject, JsonObject requestBody) {
		super(am, messageSubject);
		this.requestBody = requestBody;
	}

	public JsonObject getRequestBody() {
		return requestBody;
	}

	@Override public String toString() {
		return "RequestRoutingMessage [requestBody=" + requestBody + ", " + super.toString() + "]";
	}
}
