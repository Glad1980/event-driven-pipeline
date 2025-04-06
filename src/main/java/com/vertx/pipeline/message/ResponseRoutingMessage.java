/**
 *
 */
package com.vertx.pipeline.message;

import java.util.List;

import io.vertx.core.json.JsonObject;

/**
 * @author aalrbee
 */
public class ResponseRoutingMessage extends AbstractMessage {
	private final JsonObject responseBody;
	private final String status;

	public ResponseRoutingMessage(String messageSubject, List<Long> timeHistory, String correlationId,
								  JsonObject responseBody, String status) {
		super(messageSubject, timeHistory, correlationId);
		this.responseBody = responseBody;
		this.status = status;
	}

	public ResponseRoutingMessage(AbstractMessage am, JsonObject responseBody,
			String status) {
		super(am, ResponseRoutingMessage.class.getName());
		this.responseBody = responseBody;
		this.status = status;
	}

	@Override public JsonObject replyMessage() {
		return responseBody;
	}

	@Override public String toString() {
		return "ResponseRoutingMessage [responseBody=" + responseBody + ", " + super.toString() + "]";
	}

	public JsonObject getResponseBody() {
		return responseBody;
	}

	public String getStatus() {
		return status;
	}
}
