package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;
import org.stevi.server.http.enumeration.HttpMethod;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
}
