package com.vertx.pipeline.util;


import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;
import static com.vertx.pipeline.util.Constant.KEY_SYSTEM;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.ext.web.validation.BodyProcessorException;
import io.vertx.ext.web.validation.ParameterProcessorException;
import io.vertx.ext.web.validation.RequestPredicateException;

/**
 * @author aelaiwat
 */
public class ProjectConfig {

//    public static final Pattern regexCompiledPattern = Pattern.compile("^[{]?[0-9a-fA-F]{8}" + "-([0-9a-fA-F]{4}-)" + "{3}[0-9a-fA-F]{12}[}]?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectConfig.class);
    private static final long CONFIG_RETRIEVER_SCAN_PERIOD = 5000L;
//    public static JWTAuth jwtAuth;
//    public static JWTAuthHandler jwtAuthHandler;
//    public static JWTOptions jwtOptions;
    private static JsonObject config;
//    private static ConfigRetriever configRetriever;

    private ProjectConfig() {
    }

//    public static void jwtInitialize(Vertx vertx) {
//        try {
//            MDC.put(KEY_REQUEST_ID, KEY_SYSTEM);
//            String path = ProjectConfig.config.getString("KeyStore_Path");
//            String password = ProjectConfig.config.getString("KeyStore_Password");
//            LOGGER.info("KeyStore Path={}", path);
//            JWTAuthOptions authConfig = new JWTAuthOptions().setKeyStore(new KeyStoreOptions().setType("jceks").setPath(path).setPassword(password));
//            jwtAuth = JWTAuth.create(vertx, authConfig);
//            jwtAuthHandler = JWTAuthHandler.create(jwtAuth);
//            jwtOptions = new JWTOptions().setIgnoreExpiration(true)
//                    // .setExpiresInMinutes(ProjectConfig.Config.getInteger("jwt_ExpiresInMinutes"))
//                    // //10080: 7 days
//                    .setIssuer("jwt main Initializer");
//        } catch (Exception ex) {
//            LOGGER.error("failed to jwt initialize due to exception={}", Util.getStackTrace(ex));
//            throw ex;
//        }
//    }

    public static Future<JsonObject> getConfigRetriever(Vertx vertx, String configFilePath) {
        MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
        Promise<JsonObject> promise = Promise.promise();
        try {
            LOGGER.info("Config Path={}", configFilePath);
            ConfigStoreOptions fileStore = new ConfigStoreOptions().setType("file").setOptional(true).setConfig(new JsonObject().put("path", configFilePath));
            ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore).setScanPeriod(CONFIG_RETRIEVER_SCAN_PERIOD);
            ConfigRetriever configRetriever = ConfigRetriever.create(vertx, options);
            Future<JsonObject> jsonObjectFuture = configRetriever.getConfig().onSuccess(c -> {
                MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
                config = c;
                promise.complete(c);
            }).onFailure(ex -> {
                MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
                LOGGER.error("failed to retrieve config with exception={}", Util.getStackTrace(ex));
                promise.fail(ex);
            });
            return jsonObjectFuture;
        } catch (Exception ex) {
            promise.fail(ex);
            LOGGER.error("failed to retrieve config due to exception={}", Util.getStackTrace(ex));
        }
        return promise.future();
    }

    public static DeliveryOptions getDeliveryOptions(MultiMap headers) {
        MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        try {
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entries()) {
                    String key = entry.getKey() != null ? entry.getKey() : "";
                    String value = entry.getValue() != null ? entry.getValue() : "";
                    if (!key.equalsIgnoreCase("")) {
                        deliveryOptions.addHeader(key, value);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error("Error in (getDeliveryOptions) message due to exception={}", Util.getStackTrace(ex));
        }
        return deliveryOptions;
    }


    public static Router routesInitialize(Vertx vertx) {
        Router baseRouter = Router.router(vertx);
        Set<String> allowedHeaders = getAllowedHeaders();
        Set<HttpMethod> allowedMethods = getHttpAllowedMethods();
        CorsHandler corsHandler = CorsHandler.create("*").allowedMethods(allowedMethods).allowCredentials(true).allowedHeaders(allowedHeaders);
        baseRouter.route("/").handler(ProjectConfig::indexHandler);
        baseRouter.route().handler(corsHandler);
        baseRouter.route().failureHandler(ProjectConfig::routeErrorHandler);
        baseRouter.errorHandler(400, ProjectConfig::routeErrorHandler);
        Router publicApiRouter = Router.router(vertx);
        publicApiRouter.route("/*").handler(BodyHandler.create());
        baseRouter.mountSubRouter("/", publicApiRouter);
        return baseRouter;
    }

    private static void routeErrorHandler(RoutingContext routingContext) {
        MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
        Throwable failure = routingContext.failure();
        if (failure instanceof BadRequestException || failure instanceof RequestPredicateException) {
            // Something went wrong during validation or A request predicate is unsatisfied
            String validationErrorMessage = failure.getMessage();
            routingContext.response().setStatusCode(300).end(validationErrorMessage);
        }
        if (failure instanceof BodyProcessorException || failure instanceof ParameterProcessorException) {
            // Something went wrong while parsing/validating the body or parameters
            String validationErrorMessage = failure.getMessage();
            routingContext.response().setStatusCode(500).end(validationErrorMessage);
        }
    }

    public static Set<HttpMethod> getHttpAllowedMethods() {
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        return allowedMethods;
    }

    public static Set<String> getAllowedHeaders() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Method");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Allow-Credentials");
        allowedHeaders.add("Access-Control-Allow-Headers");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Referer");
        allowedHeaders.add("Content-Length");
        allowedHeaders.add("User-Agent");
        allowedHeaders.add("Accept-Encoding");
        allowedHeaders.add("Connection");
        allowedHeaders.add("X-PINGARUNER");
        allowedHeaders.add("Authorization");
        return allowedHeaders;
    }

    private static void indexHandler(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "text/html").end("Hello there!, This is a Test APIs Gateway version " + ProjectConfig.config.getString("Version") + ProjectConfig.config.getString("INDEX_PAGE_MSG"));
    }

    public static String getResourceFile(String name) {
        return System.getenv(name) != null ? System.getenv(name) : "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + name;
    }


    public static JsonObject getConfig() {
        return config;
    }

}
















