package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;
import org.stevi.server.http.enumeration.HttpMethod;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Getter
@Builder
public class HttpRequest {

    private final HttpMethod httpMethod;
    private final URI uri;
    private final Map<String, List<String>> requestHeaders;
    private final InputStream bodyInputStream;

    public Optional<String> getRequestHeader(String headerName) {
        return Optional.ofNullable(getRequestHeaders()
                        .get(headerName))
                .map(List::getFirst)
                .map(String::trim);
    }

    public String getRequestHeaderOrThrow(String headerName) {
        return getRequestHeader(headerName)
                .orElseThrow(() -> new RuntimeException("No %s header is present".formatted(headerName)));
    }

    public Map<String, String> getCookies() {
        return getRequestHeader("Cookie")
                .map(value -> Arrays.asList(value.split(";")))
                .orElse(List.of())
                .stream()
                .collect(toMap(key -> key.trim().split("=")[0], value -> value.trim().split("=")[1]));
    }

    public Optional<String> getCookie(String name) {
        return Optional.ofNullable(getCookies().get(name));
    }
}
