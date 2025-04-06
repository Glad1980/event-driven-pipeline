package com.vertx.pipeline.vertical.core;

import static com.vertx.pipeline.util.Constant.ADDRESS_FIRST_VERTICLE;
import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;
import static com.vertx.pipeline.util.Constant.KEY_SYSTEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.uuid.Generators;
import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.RequestRoutingMessage;
import com.vertx.pipeline.message.ResponseRoutingMessage;
import com.vertx.pipeline.util.HTTPRespCaching;
import com.vertx.pipeline.util.ProjectConfig;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * A class responsible for define all the HTTP routes for AUTO pay/inq APIs.
 *
 * @author aalrbee
 */
public class RouterVerticle extends GeneralVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouterVerticle.class);
    public static Router router;

    /**
     * This method (override method) is the starting point of the current vertical
     */
    @Override
    public void start() {
        MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
        Map<String, String> routers = getRouters();
        prepareRouters(routers);
    }

    private Map<String, String> getRouters() {
        String urlPrefix = ProjectConfig.getConfig().getString("AutoUrl");
        Map<String, String> routers = new HashMap<>();
        routers.put(urlPrefix + "first", ADDRESS_FIRST_VERTICLE);
        routers.put(urlPrefix + "order", "order.accept");
        return routers;
    }

    /**
     * This method is used to prepare and initialize all Routers using Map
     */
    public void prepareRouters(Map<String, String> routers) {
        int index = 0;
        LOGGER.info("Start initialize routing...");
        for (Map.Entry<String, String> routedString : routers.entrySet()) {
            index++;
            logRouter(index, routedString);
            router.post(routedString.getKey()).handler(routingContext -> {
                MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
                routerHandler(routingContext, routers);
            });
        }
        LOGGER.info("End initialize routing.");
//		int port = ProjectConfig.getConfig().getInteger("http.port");
//		SwaggerDocsBuilder.getInstance().buildSwaggerDocs(port, router);
    }

    /**
     * This method is used to handle specific routes after defining all the routers using send through event bus
     * and also it add Message To Cache
     *
     * @param context RoutingContext of the request context
     */
    private void routerHandler(RoutingContext context, Map<String, String> routers) {
        JsonObject body = createMessage(context);
        String correlationId = body.getString(KEY_CORRELATION_ID);
        String address = routers.get(context.currentRoute().getPath());
        LOGGER.trace("Start router handler for address={}", address);
        RequestRoutingMessage mr = createMessage(body,correlationId);
        if (address != null && !address.isEmpty()) {
            logReqApiAndBody(context, body);
            HTTPRespCaching.add(correlationId, context.response());
            send(address, mr);
        } else {
            LOGGER.warn("The address is null or empty={}", HttpResponseStatus.NOT_FOUND);
            JsonObject mfepResponse = new JsonObject().put("status", "404");
            ResponseRoutingMessage rrm = new ResponseRoutingMessage(mr, mfepResponse, "Address not exist");
            endRequest(correlationId, rrm);
        }
    }

    private RequestRoutingMessage createMessage( JsonObject body, String correlationId) {
        MDC.put(KEY_CORRELATION_ID, correlationId);
        LOGGER.trace("Create RequestRoutingMessage");
        return new RequestRoutingMessage(this.getClass().getSimpleName(), new ArrayList<>(),body,correlationId);
    }
    
    /**
     * This method is used to createMessage [JsonObject] and generatecorrelationId
     *
     * @param context JsonObject of the request context
     * @return JsonObject
     */
    private JsonObject createMessage(RoutingContext context) {
        JsonObject body = new JsonObject(context.body().asString());
        String correlationId = generateCorrelationId();
        body.put(KEY_CORRELATION_ID, correlationId);
        MDC.put(KEY_CORRELATION_ID, correlationId);
        LOGGER.trace("Create Request Message");
        return body;
    }

    /**
     * This method is used to generate correlationId using time Based GUID.
     *
     * @return generated time based GUID (correlationId)
     */
    private String generateCorrelationId() {
        LOGGER.trace("Generate correlationId ( GUID(time Based))");
        return Generators.timeBasedGenerator().generate().toString();
    }

    /**
     * this method is used to log the define Router
     *
     * @param index        the index of the define route
     * @param routedString Map<URL,Handler>
     */
    private void logRouter(int index, Map.Entry<String, String> routedString) {
        int port = ProjectConfig.getConfig().getInteger("http.port");
        String handler = routedString.getValue() != null ? routedString.getValue() : "";
        LOGGER.info("Router #{}=[ {}{} ] With handler={}", index, port, routedString.getKey(), handler);
    }

    /**
     * This method is used to log Request Api and message Body of the route
     *
     * @param context RoutingContext
     * @param body    RequestRoutingMessage
     */
    private void logReqApiAndBody(RoutingContext context, JsonObject body) {
        LOGGER.info("[REQUEST ON API={}]  [WITH BODY={}] ", context.currentRoute().getPath(), body);
    }

    @Override
    public String getVerticalConsumer() {
        return null;
    }

    @Override
    public void process(AbstractMessage message) {

    }
}
