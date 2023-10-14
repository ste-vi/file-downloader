package org.stevi.server.http;

import org.stevi.server.http.enumeration.HttpMethod;
import org.stevi.server.http.model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HttpRequestReader {

    public static Optional<HttpRequest> decodeRequest(InputStream inputStream) {
        Stream<String> httpLinesStream = readHttpLines(inputStream);
        return buildRequest(httpLinesStream);
    }

    private static Stream<String> readHttpLines(InputStream inputStream) {
        var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return bufferedReader.lines();
    }

    private static Optional<HttpRequest> buildRequest(Stream<String> httpLinesStream) {
        List<String> httpLines = httpLinesStream.toList();
        if (httpLines.isEmpty()) {
            return Optional.empty();
        }

        String info = httpLines.get(0);
        String[] httpInfo = info.split(" ");

        if (httpInfo.length != 3) {
            return Optional.empty();
        }

        String method = httpInfo[0];
        String uri = httpInfo[1];
        String protocolVersion = httpInfo[2];

        if (!protocolVersion.equals("HTTP/1.1")) {
            return Optional.empty();
        }

        try {
            var httpRequest = HttpRequest
                    .builder()
                    .httpMethod(HttpMethod.valueOf(method))
                    .uri(new URI(uri))
                    .requestHeaders(resolveRequestHeaders(httpLines))
                    .build();
            return Optional.of(httpRequest);
        } catch (URISyntaxException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Map<String, List<String>> resolveRequestHeaders(List<String> httpLines) {
        Map<String, List<String>> requestHeaders = new HashMap<>();

        if (httpLines.size() > 1) {
            for (int i = 1; i < httpLines.size(); i++) {
                String header = httpLines.get(i);
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
