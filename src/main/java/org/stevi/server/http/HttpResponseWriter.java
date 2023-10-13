package org.stevi.server.http;

import org.stevi.server.http.model.HttpResponse;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpResponseWriter {

    public static void writeResponse(final BufferedWriter outputStream, final HttpResponse response) {
        try {
            final int statusCode = response.getStatusCode();
            final List<String> responseHeaders = buildHeaderStrings(response.getResponseHeaders());

            outputStream.write("HTTP/1.1 " + statusCode + " " + "OK" + "\r\n");

            for (String header : responseHeaders) {
                outputStream.write(header);
            }

            final Optional<String> entityString = response.getEntity().flatMap(HttpResponseWriter::getResponseString);
            if (entityString.isPresent()) {
                final String encodedString = new String(entityString.get().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                outputStream.write("Content-Length: " + encodedString.getBytes().length + "\r\n");
                outputStream.write("\r\n");
                outputStream.write(encodedString);
            } else {
                outputStream.write("\r\n");
            }
        } catch (Exception ignored) {

        }
    }

    private static List<String> buildHeaderStrings(final Map<String, List<String>> responseHeaders) {
        final List<String> responseHeadersList = new ArrayList<>();

        responseHeaders.forEach((name, values) -> {
            final StringBuilder valuesCombined = new StringBuilder();
            values.forEach(valuesCombined::append);
            valuesCombined.append(";");

            responseHeadersList.add(name + ": " + valuesCombined + "\r\n");
        });

        return responseHeadersList;
    }

    private static Optional<String> getResponseString(final Object entity) {
        // Currently only supporting Strings
        if (entity instanceof String) {
            try {
                return Optional.of(entity.toString());
            } catch (Exception ignored) {
            }
        }
        return Optional.empty();
    }
}
