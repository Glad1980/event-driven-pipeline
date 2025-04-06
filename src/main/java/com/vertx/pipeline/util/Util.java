/**
 *
 */
package com.vertx.pipeline.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aalrbee
 */
public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private Util() {
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static void logEx(Exception ex, String source) {
        LOGGER.error("Exception!! in {} StackTrace={}", source, getStackTrace(ex));
    }

}
