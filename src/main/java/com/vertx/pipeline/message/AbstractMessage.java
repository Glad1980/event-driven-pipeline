/**
 *
 */
package com.vertx.pipeline.message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

// import java.util.UUID;

/**
 * @author aalrbee
 *         <p>
 *         Abstract message, should be used as super-class for every message,
 *         This class will auto set creation time for each message, and will
 *         carry information needed for each request,
 *         <p>
 *         note that all of the message fields should be immutable, and no
 *         reference to an message should be changed or set to null, for data
 *         modification or for sharing between verticals,the message should be
 *         cloned, Also, new or cloned message should be created for every new
 *         auth.vertical step.
 */
public abstract class AbstractMessage {

	// When this message created
	@JsonProperty("creationTime")
	private final long creationTime;
	/** This field contains creation time for processed messages */
	@JsonProperty("timeHistory")
	private final List<Long> timeHistory;

	/*
	 * this field can contains any string value to describe the message, for now we
	 * set it to the vertical name[single class name] that sent this message
	 */
	@JsonProperty("messageSubject")
	private final String messageSubject;

	/*
	 * generate an correlation ID for each request, It can be used to make sure all
	 * request were reserved correctly,
	 *
	 */
	@JsonProperty("correlationId")
	private final String correlationId;

	public AbstractMessage(long creationTime, List<Long> timeHistory, String messageSubject,
			String correlationId) {
		this.creationTime = creationTime;
		this.timeHistory = timeHistory;
		this.messageSubject = messageSubject;
		this.correlationId = correlationId;
	}

	public AbstractMessage(String messageSubject, List<Long> timeHistory, String correlationId) {
		this.creationTime = Instant.now().toEpochMilli();
		this.timeHistory = timeHistory;
		this.correlationId = correlationId;
		this.messageSubject = messageSubject;
	}

	public AbstractMessage(AbstractMessage am, String messageSubject) {
		this.creationTime = Instant.now().toEpochMilli();
		List<Long> timeHistory = new ArrayList<Long>(am.getTimeHistory());
		timeHistory.add(am.getCreationTime());
		this.timeHistory = timeHistory;
		this.correlationId = am.getCorrelationId();
		this.messageSubject = messageSubject;
	}

	public AbstractMessage(AbstractMessage am) {
		this.creationTime = Instant.now().toEpochMilli();
		List<Long> timeHistory = new ArrayList<Long>(am.getTimeHistory());
		timeHistory.add(am.getCreationTime());
		this.timeHistory = timeHistory;
		this.correlationId = am.getCorrelationId();
		this.messageSubject = "";
	}
	public JsonObject replyMessage() {
		return null;
	}

	public String getMessageId() {
		return correlationId + "";
	}

	public long getCreationTime() {
		return creationTime;
	}

	public List<Long> getTimeHistory() {
		return timeHistory;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getMessageSubject() {
		return messageSubject;
	}

	@Override
	public String toString() {
		return "AbstractMessage [ creationTime=" + creationTime + ", timeHistory=" + timeHistory + ", correlationId=" + correlationId + "]";
	}

}
