package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;
import org.stevi.server.http.enumeration.HttpMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HttpRequest {

    private final HttpMethod httpMethod;
    private final URI uri;
    private final Map<String, List<String>> requestHeaders;
}
