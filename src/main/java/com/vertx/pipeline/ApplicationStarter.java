package com.vertx.pipeline;

import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;
import static com.vertx.pipeline.util.Constant.KEY_SYSTEM;

import java.awt.PageAttributes.OrientationRequestedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.LocalMessageCodec;
import com.vertx.pipeline.message.RequestRoutingMessage;
import com.vertx.pipeline.message.ResponseRoutingMessage;
import com.vertx.pipeline.message.example.InventoryCheckedEvent;
import com.vertx.pipeline.message.example.OrderRequest;
import com.vertx.pipeline.message.example.PaymentProcessedEvent;
import com.vertx.pipeline.util.ProjectConfig;
import com.vertx.pipeline.util.Util;
import com.vertx.pipeline.vertical.FirstVerticle;
import com.vertx.pipeline.vertical.core.GeneralVerticle;
import com.vertx.pipeline.vertical.core.ResponseVerticle;
import com.vertx.pipeline.vertical.core.RouterVerticle;
import com.vertx.pipeline.vertical.example.InventoryCheckVerticle;
import com.vertx.pipeline.vertical.example.OrderAcceptanceVerticle;
import com.vertx.pipeline.vertical.example.OrderConfirmationVerticle;
import com.vertx.pipeline.vertical.example.PaymentProcessingVerticle;

import io.vertx.core.Launcher;
import io.vertx.core.http.HttpServerOptions;

/**
 * This class is responsible for starting the Gateway http server for CTM
 *
 * @author aalrbee
 */
public class ApplicationStarter extends GeneralVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStarter.class);

	public static void main(String[] args) {
		Launcher.executeCommand("run", ApplicationStarter.class.getName());
	}

	@Override
	public void start() {
		vertx.exceptionHandler(handler -> {
			MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
			LOGGER.error("Uncaught exception={}", Util.getStackTrace(handler));
		});
		ProjectConfig.getConfigRetriever(vertx, ProjectConfig.getResourceFile("Config.json")).onSuccess(res -> {
			MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
			vertx.executeBlocking(x -> initModule());
		});
	}

	public void deployVerticles() {

		deployVerticle(RouterVerticle.class.getName());
		deployVerticle(ResponseVerticle.class.getName());
		deployVerticle(FirstVerticle.class.getName());
		
		//example 
		deployVerticle(OrderAcceptanceVerticle.class.getName());
		deployVerticle(InventoryCheckVerticle.class.getName());
		deployVerticle(PaymentProcessingVerticle.class.getName());
		deployVerticle(OrderConfirmationVerticle.class.getName());
	}

	public void initModule() {
		MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
		HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true).setTcpKeepAlive(false);
		RouterVerticle.router = ProjectConfig.routesInitialize(vertx);
		registerMessageCodec();
		deployVerticles();
		int port = ProjectConfig.getConfig().getInteger("http.port");
		vertx.createHttpServer(options).requestHandler(RouterVerticle.router)
				.exceptionHandler(ApplicationStarter::httpErrorHandler).listen(port);
		LOGGER.info("Application HTTP server started on port={}", port);
	}

	private static void httpErrorHandler(Throwable ex) {
		LOGGER.error("HTTP server error={}", Util.getStackTrace(ex));
	}

	@Override
	public String getVerticalConsumer() {
		return null;
	}

	@Override
	public void process(AbstractMessage message) {

	}

	public void registerMessageCodec() {
		vertx.eventBus().registerDefaultCodec(AbstractMessage.class, new LocalMessageCodec<>(AbstractMessage.class));
		vertx.eventBus().registerDefaultCodec(RequestRoutingMessage.class,
				new LocalMessageCodec<>(RequestRoutingMessage.class));
		// example
		vertx.eventBus().registerDefaultCodec(OrderRequest.class,
				new LocalMessageCodec<>(OrderRequest.class));
		vertx.eventBus().registerDefaultCodec(InventoryCheckedEvent.class,
				new LocalMessageCodec<>(InventoryCheckedEvent.class));
		vertx.eventBus().registerDefaultCodec(OrientationRequestedType.class,
				new LocalMessageCodec<>(OrientationRequestedType.class));
		vertx.eventBus().registerDefaultCodec(PaymentProcessedEvent.class,
				new LocalMessageCodec<>(PaymentProcessedEvent.class));
	}
}