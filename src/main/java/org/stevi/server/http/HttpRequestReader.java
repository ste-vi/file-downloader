package org.stevi.server.http;

import org.stevi.server.http.enumeration.HttpMethod;
import org.stevi.server.http.model.HttpRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class HttpRequestReader {

    public static Optional<HttpRequest> decode(InputStream inputStream) {
        return readMessage(inputStream).flatMap(HttpRequestReader::buildRequest);
    }

    private static Optional<List<String>> readMessage(final InputStream inputStream) {
        try {
            if (!(inputStream.available() > 0)) {
                return Optional.empty();
            }

            final char[] inBuffer = new char[inputStream.available()];
            final InputStreamReader inReader = new InputStreamReader(inputStream);
            final int read = inReader.read(inBuffer);

            List<String> message = new ArrayList<>();

            try (Scanner sc = new Scanner(new String(inBuffer))) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (!line.isBlank()) {
                        message.add(line);
                    }
                }
            }

            return Optional.of(message);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private static Optional<HttpRequest> buildRequest(List<String> message) {
        if (message.isEmpty()) {
            return Optional.empty();
        }

        String firstLine = message.get(0);
        String[] httpInfo = firstLine.split(" ");

        if (httpInfo.length != 3) {
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];
        if (!protocolVersion.equals("HTTP/1.1")) {
            return Optional.empty();
        }

        try {
            HttpRequest httpRequest = HttpRequest.builder()
                    .httpMethod(HttpMethod.valueOf(httpInfo[0]))
                    .uri(new URI(httpInfo[1]))
                    .requestHeaders(resolveRequestHeaders(message))
                    .build();
            return Optional.of(httpRequest);
        } catch (URISyntaxException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Map<String, List<String>> resolveRequestHeaders(final List<String> message) {
        final Map<String, List<String>> requestHeaders = new HashMap<>();

        if (message.size() > 1) {
            for (int i = 1; i < message.size(); i++) {
                String header = message.get(i);
                int colonIndex = header.indexOf(':');

                if (!(colonIndex > 0 && header.length() > colonIndex + 1)) {
                    break;
                }

                String headerName = header.substring(0, colonIndex);
                String headerValue = header.substring(colonIndex + 1);

                requestHeaders.compute(headerName, (key, values) -> {
                    if (values != null) {
                        values.add(headerValue);
                    } else {
                        values = new ArrayList<>();
                    }
                    return values;
                });
            }
        }

        return requestHeaders;
    }
}
