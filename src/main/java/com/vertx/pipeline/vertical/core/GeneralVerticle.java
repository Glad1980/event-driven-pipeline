/**
 *
 */
package com.vertx.pipeline.vertical.core;

import static com.vertx.pipeline.util.Constant.ADDRESS_RESPONSE_VERTICLE;
import static com.vertx.pipeline.util.Constant.KEY_NULL_MESSAGE_ARRIVED_TO;
import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;
import static com.vertx.pipeline.util.Constant.KEY_START_PROCESS_REQUEST;
import static com.vertx.pipeline.util.Constant.KEY_SYSTEM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.util.Util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;

/**
 * @author aalrbee
 */
public abstract class GeneralVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralVerticle.class);
	private static final int DEFAULT_INSTANCES = 1;

	public void deployVerticle(String vertical) {
		deployVerticle(vertical, new DeploymentOptions()
				.setInstances(DEFAULT_INSTANCES));
	}

	public void deployVerticle(String vertical, int instances) {
		deployVerticle(vertical, new DeploymentOptions()
				.setInstances(instances));
	}

	public void deployVerticle(String vertical, DeploymentOptions deploymentOptions) {
		vertx.deployVerticle(vertical, deploymentOptions,
				handlingDeployment(vertical, deploymentOptions));
	}

	public void deployVerticle(GeneralVerticle vertical) {
		deployVerticle(vertical, new DeploymentOptions()
				.setInstances(DEFAULT_INSTANCES));
	}

	public void deployVerticle(GeneralVerticle vertical, int instances) {
		deployVerticle(vertical, new DeploymentOptions()
				.setInstances(instances));
	}

	public void deployVerticle(GeneralVerticle vertical, 
			DeploymentOptions deploymentOptions) {
		vertx.deployVerticle(vertical, deploymentOptions,
				handlingDeployment(vertical.getClass().getName(), deploymentOptions));
	}

	private Handler<AsyncResult<String>> handlingDeployment(String vertical,
			DeploymentOptions deploymentOptions) {
		return deployment -> {
			MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
			if (deployment.succeeded()) {
				String result = deployment.result();
				LOGGER.trace("[ DEPLOYING VERTICAL={} ] ,"
						+ " [NO OF INSTANCES={} ], [DEPLOYMENT ID={} ]", vertical,
						deploymentOptions.getInstances(), result);
			} else {
				LOGGER.trace("ERROR DEPLOYING VERTICAL={}, ERROR={}",
						vertical, Util.getStackTrace(deployment.cause()));
			}
		};
	}

	public void send(String to, AbstractMessage am) {
		MDC.put(KEY_CORRELATION_ID, am.getCorrelationId());
		LOGGER.trace("<<SEND MESSAGE>> [FROM={}] [TO={}] [BODY={}]",
				this.getClass().getSimpleName(), to, am);
		vertx.eventBus().send(to, am);
	}

	public void publish(String to, AbstractMessage am) {
		MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
		LOGGER.trace("<<PUBLISH MESSAGE>> [FROM={}] [TO=ALL] [BODY={}]",
				this.getClass().getSimpleName(), am);
		vertx.eventBus().publish(to, am);
	}

	public void endRequest(String correlationId, AbstractMessage am) {
		MDC.put(KEY_CORRELATION_ID, correlationId);
		String address = ADDRESS_RESPONSE_VERTICLE;
		LOGGER.trace("<<END RESPONSE MESSAGE>> [FROM={}] [TO={}] [BODY={}]",
				this.getClass().getSimpleName(), address,
				am);
		vertx.eventBus().send(address, am);
	}

	@Override
	public void start() {
		vertx.eventBus().<AbstractMessage>consumer(getVerticalConsumer(), message -> {
			LOGGER.info(KEY_START_PROCESS_REQUEST, getVerticalConsumer());
			if (message != null) {
				String correlationId = message.body().getCorrelationId();
				MDC.put(KEY_CORRELATION_ID, correlationId);
				process(message.body());
			} else {
				MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
				LOGGER.warn(KEY_NULL_MESSAGE_ARRIVED_TO, this.getClass().getName());
			}
		});
	}

	public abstract String getVerticalConsumer();

	public abstract void process(AbstractMessage message);

}
